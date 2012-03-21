/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

import java.util.Properties;

import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.dt.server.metier.EOTraitementType;
import org.cocktail.dt.server.metier.workflow.A_WorkFlow;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.dt.app.I_ApplicationConsts;
import fr.univlr.cri.planning.AvantModifPlanning;
import fr.univlr.cri.planning.DemandePlanning;
import fr.univlr.cri.planning.SPConstantes;

/**
 * Ajoute un nouveau traitement a la demande en cours de consultation.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class TraitementIntervention
		extends DTWebComponent {
	// la valeur du binding
	public I_TraitementListener traitementListener;

	public NSTimestamp traDateDeb, traDateFin;
	public String tfTraitement, tfCommentaireInterne;
	private String tfTraitementAdditionnel;
	public boolean chConsultable, chEmail;

	// le batiment et le bureau pour la vue "livraison"
	public CktlRecord recBatiment;
	public CktlRecord recBureau;

	// la gestion des traitements "complexes" pour les émissions et receptions
	// des bons de commande interne
	public String tfTraitementEmissionBdcNumeroCommande;
	public String tfTraitementEmissionBdcNumeroCommandeSurcout;
	public String receptionCommandeSigneON;
	private String receptionCommandeSurcoutSigneON;

	// indique s'il y a eu des modifications de valeurs
	private boolean hasTraDateDebChanges, hasTraDateFinChanges, hasTfTraitementChanges;

	/** le numero du nouveau traitement qui vient d'etre cree **/
	private Number traOrdre;

	/** le pointeur va la page parente (conteneur InspecteurDT) **/
	private InspecteurDT parentPage;

	// Les fichiers attaches
	public NSArray<String> filePaths;
	public boolean clearFilePaths;
	private NSArray<String> attachedFiles;

	// serveur de planning
	private boolean shouldIgnoreServPlanning;
	private boolean forceAddPlanningIndispo;

	//
	private boolean forceAddTraitementExists;

	//
	private boolean isClosing;

	protected boolean isTraitementPrestation;

	// types de traitement
	private NSArray<EOTraitementType> traitementTypeList;
	public EOTraitementType traitementTypeItem;
	public EOTraitementType traitementTypeSelected;
	public boolean isDisabledPopUpTraitementList;

	// ajaxeries
	public boolean isExpandedTraitementAdditionnel;

	// settereries
	private boolean shouldIgnoreSetterTfTraitementAdditionnel;

	public TraitementIntervention(WOContext context) {
		super(context);
		parentPage = (InspecteurDT) context().page();
		clearFilePaths = true;
		filePaths = new NSArray<String>();
		if (dtUserInfo().controleChevauchementPlanning()) {
			shouldIgnoreServPlanning = !DemandePlanning.serveurPlanningAccessible();
			forceAddPlanningIndispo = false;
			// forceAddTraitementExists = false;
		} else {
			shouldIgnoreServPlanning = true;
			forceAddPlanningIndispo = true;
			// forceAddTraitementExists = true;
		}
		forceAddTraitementExists = false;
		isTraitementPrestation = false;
		// ne pas ouvrir le volet traitement additionnel par defaut
		isExpandedTraitementAdditionnel = false;
		//
		shouldIgnoreSetterTfTraitementAdditionnel = false;
	}

	/**
	 * La liste des types de traitements potentiels. On fait le ménage selon la
	 * nature de la DT
	 * 
	 * @return
	 */
	public NSArray<EOTraitementType> getTraitementTypeList() {
		if (traitementTypeList == null) {
			traitementTypeList = eoIntervention().getTraitementTypeArray();
		}
		return traitementTypeList;
	}

	/**
	 * 
	 * @return
	 */
	public EOTraitementType getTraitementTypeSelected() {
		if (traitementTypeSelected == null) {
			// selection du type "generique" classique par defaut
			traitementTypeSelected = (EOTraitementType) EOQualifier.filteredArrayWithQualifier(
					getTraitementTypeList(),
					ERXQ.equals(
							EOTraitementType.TTY_CODE_KEY,
							EOTraitementType.TYPE_CODE_TEXTUEL)).lastObject();
		}
		return traitementTypeSelected;
	}

	/**
	 * 
	 * @return
	 */
	public EOIntervention eoIntervention() {
		return listener().recDemande();
	}

	/**
	 * 
	 * @return
	 */
	private EOTraitement eoTraitement() {
		return listener().recTraitement();
	}

	/**
	 * Execute quelques actions lors de l'affichage du composant.
	 */
	public void appendToResponse(WOResponse response, WOContext context) {
		if (listener().shouldReset()) {
			resetContent();
			listener().setShouldReset(false);
		}
		super.appendToResponse(response, context);
	}

	/**
	 * Remet a zero le contenu du composant.
	 */
	public void resetContent() {
		traitementTypeList = null;
		traitementTypeSelected = null;

		// le numero de traitement
		traOrdre = null;

		// La date et l'heure de traitement
		NSTimestamp dateFin = DateCtrl.now();
		NSTimestamp dateDebut = dateFin.timestampByAddingGregorianUnits(0, 0, 0, 0, -15, 0);

		// Le motif de la demande
		tfTraitement = StringCtrl.emptyString();
		tfCommentaireInterne = StringCtrl.emptyString();
		setTfTraitementAdditionnel(StringCtrl.emptyString());

		// Consultation : par defaut - consultable
		chConsultable = true;

		// ne pas ouvrir le volet traitement additionnel par defaut
		isExpandedTraitementAdditionnel = false;

		if (listener().isUpdating()) {
			dateDebut = eoTraitement().traDateDeb();
			dateFin = eoTraitement().traDateFin();
			tfTraitement = eoTraitement().traTraitement();
			tfCommentaireInterne = eoTraitement().traCommentaireInterne();
			setTfTraitementAdditionnel(eoTraitement().traTraitementAdditionnel());
			traOrdre = eoTraitement().traOrdre();
			chConsultable = eoTraitement().traConsultable().equals("O");
			traitementTypeSelected = eoTraitement().toTraitementType();/*
																																	 * EOTraitementType
																																	 * .
																																	 * getEoTraitementTypeForCodeInArray
																																	 * (
																																	 * getTraitementTypeList
																																	 * (),
																																	 * eoTraitement
																																	 * ().
																																	 * toTraitementType
																																	 * (
																																	 * ).ttyCode()
																																	 * );
																																	 */
			// s'il existe un traitement additionnel, alors on ouvre le volet associé
			if (!StringCtrl.isEmpty(getTfTraitementAdditionnel())) {
				isExpandedTraitementAdditionnel = true;
			}
			// DTs de type commande installation poste complet
			if (eoIntervention().isInterventionInstallationPosteComplet2()) {

				if (eoTraitement().toTraitementType().isTraitementEmissionBdcInternePourSignature()) {
					tfTraitementEmissionBdcNumeroCommande = eoTraitement().getNumeroCommande();
					tfTraitementEmissionBdcNumeroCommandeSurcout = eoTraitement().getNumeroCommandeSurcout();
				}

				if (eoTraitement().toTraitementType().isTraitementReceptionBdcInterneSigne()) {

					if (eoTraitement().isCommandeSigneeOui()) {
						receptionCommandeSigneON = OUI;
					} else if (eoTraitement().isCommandeSigneeNon()) {
						receptionCommandeSigneON = NON;
					} else if (eoTraitement().isCommandeSigneeSansObjet()) {
						receptionCommandeSigneON = SANS_OBJET;
					}

					if (eoTraitement().isCommandeSurcoutSigneeOui()) {
						setReceptionCommandeSurcoutSigneON(OUI);
					} else if (eoTraitement().isCommandeSurcoutSigneeNon()) {
						setReceptionCommandeSurcoutSigneON(NON);
					} else if (eoTraitement().isCommandeSurcoutSigneeSansObjet()) {
						setReceptionCommandeSurcoutSigneON(SANS_OBJET);
					}
				}

			}
		} else {

			// nouvelle DT

			// pre-selection des radios pour les DTs de type commande installation
			// poste
			// complet
			if (eoIntervention().isInterventionInstallationPosteComplet2()) {
				if (eoIntervention().isCommandeARealiser()) {
					receptionCommandeSigneON = OUI;
				} else {
					receptionCommandeSigneON = SANS_OBJET;
				}
				if (eoIntervention().isPosteSupplementaire()) {
					setReceptionCommandeSurcoutSigneON(OUI);
				} else {
					setReceptionCommandeSurcoutSigneON(SANS_OBJET);
				}
			}

		}

		traDateDeb = dateDebut;
		traDateFin = dateFin;

		// Envoi de mail apres le traitement
		chEmail = dtUserInfo().sendMailTraitement();
		resetErrors();

		// raz des fichiers attaches
		filePaths = new NSArray<String>();
		clearFilesInfo();
		attachedFiles = null;

		// raz des temoins de changement
		hasTraDateDebChanges = hasTraDateFinChanges = hasTfTraitementChanges = false;

		//
		shouldIgnoreSetterTfTraitementAdditionnel = false;

	}

	/**
	 * Annule toutes les erreurs.
	 */
	private void resetErrors() {
		listener().setErrorMessage(null);
	}

	/**
	 * Supprime les fichiers et le repertoire unique genere pour le depot de
	 * documents.
	 */
	private void clearFilesInfo() {
		if (!NSArrayCtrl.isEmpty(filePaths)) {
			FileCtrl.deletePath(
					FileCtrl.getParentDirectory(filePaths.objectAtIndex(0)));
		}
		filePaths = new NSArray<String>();
		clearFilePaths = true;
	}

	/**
   * 
   */
	private final NSMutableDictionary<String, Object> collecterInfosTraitement() {
		NSMutableDictionary<String, Object> leDico;

		resetErrors();

		// format
		if (traDateDeb == null || traDateFin == null) {
			// erreur de lecture des heures
			listener().setErrorMessage("Les heures/minutes sont vides ou leur format est incorrect!");
			return null;
		}

		// pour un traitement livraison, commande ou no serie, date debut = date fin
		if (getTraitementTypeSelected().isTraitementCommande() ||
				getTraitementTypeSelected().isTraitementLivraison() ||
				getTraitementTypeSelected().isTraitementNoSerie()) {
			traDateFin = traDateDeb;
		} else {
			// controle que la fin est apres le debut
			if (traDateFin.compareTo(traDateDeb) <= 0) {
				listener().setErrorMessage("La date de début doit être antérieure à la date de fin !");
				return null;
			}
		}

		// construire le traitement d'apres la selection du batiment en mode
		// livraison
		if (getTraitementTypeSelected().isTraitementLivraison()) {
			if (recBatiment != null) {
				tfTraitement = "Batiment : " + recBatiment.stringForKey("appellation");
				if (recBureau != null) {
					tfTraitement += "\nBureau : " + contactsBus().getLibelleForSalle(recBureau);
				}
			}
		}

		// remplissage automatiquement du traitement si le type le nécéssite
		String tfTraitementAuto = EOTraitement.getTraTraitementAutomatique(
				getTraitementTypeSelected(),
				tfTraitementEmissionBdcNumeroCommande,
				tfTraitementEmissionBdcNumeroCommandeSurcout,
				receptionCommandeSigneON,
				getReceptionCommandeSurcoutSigneON());
		if (!StringCtrl.isEmpty(tfTraitementAuto)) {
			tfTraitement = tfTraitementAuto;
		}

		if (getTraitementTypeSelected().isTraitementEmissionBdcInternePourSignature() &&
				eoIntervention().isCommandeARealiser() &&
				StringCtrl.isEmpty(tfTraitementEmissionBdcNumeroCommande)) {
			listener().setErrorMessage(
					"Le numéro de commande interne est obligatoire s'il faut réaliser une commande");
			return null;
		}

		if (getTraitementTypeSelected().isTraitementEmissionBdcInternePourSignature() &&
				eoIntervention().isPosteSupplementaire() &&
				StringCtrl.isEmpty(tfTraitementEmissionBdcNumeroCommandeSurcout)) {
			listener().setErrorMessage(
					"Le numéro de commande interne lié au surcoût est obligatoire en cas de poste supplémentaire");
			return null;
		}

		// le traitement de type cloture pour non paiement du surcout
		// doit forcément cloturer la DT
		if (getTraitementTypeSelected().isTraitementClotureSurcoutNonSigne() &&
				isClosing == false) {
			listener().setErrorMessage(
					"L'ajout d'un traitement de type " + getTraitementTypeSelected().ttyLibelle() + " doit forcément " +
							"être une clôture de la DT");
			return null;
		}

		if (StringCtrl.normalize(tfTraitement).length() == 0) {
			// message personalise selon le type de traitement
			String err = "Vous devez saisir un motif pour le traitement";
			if (getTraitementTypeSelected().isTraitementCommande()) {
				err = "Vous devez indiquer un numero de commande";
			} else if (getTraitementTypeSelected().isTraitementLivraison()) {
				err = "Vous devez indiquer un lieu de livraison (au moins le batiment)";
			} else if (getTraitementTypeSelected().isTraitementNoSerie()) {
				err = "Vous devez indiquer un numéro de serie";
			}
			listener().setErrorMessage(err);
			return null;
		}

		// Verifier si la longeur de commentaire est correcte.
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOTraitement.ENTITY_NAME, EOTraitement.TRA_TRAITEMENT_KEY,
				tfTraitement, "Traitement", 0, true, true)) {
			// errorTraLength = true;
			listener().setErrorMessage(dtSession().dtDataBus().getErrorMessage());
			dtSession().dtDataBus().setErrorMessage(null);
			return null;
		}

		// Verifier si la longeur de commentaire additionnel est correcte.
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOTraitement.ENTITY_NAME, EOTraitement.TRA_TRAITEMENT_ADDITIONNEL_KEY,
				getTfTraitementAdditionnel(), "Traitement additionnel", 0, true, true)) {
			// errorTraLength = true;
			listener().setErrorMessage(dtSession().dtDataBus().getErrorMessage());
			dtSession().dtDataBus().setErrorMessage(null);
			return null;
		}

		// Verifier si la longeur de commentaire interne est correcte.
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOTraitement.ENTITY_NAME, EOTraitement.TRA_COMMENTAIRE_INTERNE_KEY,
				tfCommentaireInterne, "Commentaire", 0, true, true)) {
			// errorTraLength = true;
			listener().setErrorMessage(dtSession().dtDataBus().getErrorMessage());
			dtSession().dtDataBus().setErrorMessage(null);
			return null;
		}

		// Collection
		leDico = new NSMutableDictionary<String, Object>();
		leDico.setObjectForKey(dtSession().dtUserInfo().noIndividu(), "aNoIndividu");
		leDico.setObjectForKey(eoIntervention().intOrdre(), "aIntOrdre");
		leDico.setObjectForKey((chConsultable ? "O" : "N"), "aTraConsultable");
		leDico.setObjectForKey(traDateDeb, "aTraDateDeb");
		leDico.setObjectForKey(traDateFin, "aTraDateFin");
		leDico.setObjectForKey(EOEtatDt.ETAT_TRAITEES, "aTraEtat");
		leDico.setObjectForKey(StringCtrl.normalize(tfTraitement), "aTraTraitement");
		leDico.setObjectForKey(StringCtrl.normalize(tfCommentaireInterne), "aTraCommentaireInterne");
		leDico.setObjectForKey(StringCtrl.normalize(getTfTraitementAdditionnel()), "aTraTraitementAdditionnel");

		// en mise a jour, on recup le traOrdre
		if (listener().isUpdating())
			leDico.setObjectForKey(traOrdre, "aTraOrdre");

		// le type de traitement
		if (isTraitementPrestation) {
			// forcer le type prestation en mode prestation
			leDico.setObjectForKey(EOTraitementType.TYPE_CODE_PRESTATION, "aTtyCode");
		} else {
			// sinon selection de l'utilisateur
			leDico.setObjectForKey(getTraitementTypeSelected().ttyCode(), "aTtyCode");
		}

		return leDico;
	}

	/**
	 * Execute l'action de l'annulation de saisie d'un traitement. Une
	 * confirmation est demandee si : - ajout : le motif n'est pas vide - edition
	 * : si quelque chose a change
	 */
	public WOComponent cancel() {
		if (listener().isUpdating()) {
			// on demande confirmation uniquement s'il y a des changements
			if (hasTraDateDebChanges || hasTraDateFinChanges || hasTfTraitementChanges) {
				DTConfirmCancelResponder responder = new DTConfirmCancelResponder();
				return CktlAlertPage.newAlertPageWithResponder(
						this, "Confirmation d'annulation de Traitement<br/>",
						"Voulez-vous vraiment quitter l'édition d'un traitement ?",
						"Quitter", "Annuler", null, CktlAlertPage.QUESTION, responder);
			}
		} else {
			// ajout : on regarde si le motif est saisi
			if (!StringCtrl.isEmpty(tfTraitement)) {
				DTConfirmCancelResponder responder = new DTConfirmCancelResponder();
				return CktlAlertPage.newAlertPageWithResponder(
						this, "Confirmation d'annulation de Traitement<br/>",
						"Voulez-vous vraiment quitter l'ajout d'un traitement ?",
						"Quitter", "Annuler", null, CktlAlertPage.QUESTION, responder);
			}
		}
		return listener().cancelPage();
	}

	/**
	 * Affichage du message si des traitements sont deja saisie aux dates donnees.
	 * L'utilisateur peut tout de meme saisir son traitement s'il le souhaite.
	 */
	private WOComponent pageWarnTraitementExists(
			NSArray<EOTraitement> records, NSTimestamp dateDebut, NSTimestamp dateFin) {
		WarnTraitementExistsResponder responder = new WarnTraitementExistsResponder();
		StringBuffer msg = new StringBuffer();
		msg.append("Attention, vous avez déjà des traitements enregistrés\n" +
							"durant la période :" +
							DateCtrl.dateToString(dateDebut, "%d/%m/%Y %H:%M") + " - " +
							DateCtrl.dateToString(dateFin, "%d/%m/%Y %H:%M") + " : " +
							"\n\n");
		for (int i = 0; i < records.count(); i++) {
			EOTraitement recTraitement = records.objectAtIndex(i);
			String intOrdre = Integer.toString(recTraitement.toIntervention().intOrdre().intValue());
			String periode = "du " + recTraitement.dateTimeStringForKey(EOTraitement.TRA_DATE_DEB_KEY) +
					" au " + recTraitement.dateTimeStringForKey(EOTraitement.TRA_DATE_FIN_KEY);
			msg.append("- DT #").append(intOrdre).append(" : ").append(periode).append("\n");
			msg.append(StringCtrl.compactString(recTraitement.traTraitement(), 50, "<...>")).append("\n\n");
		}
		msg.append("\nVoulez-vous malgré tout saisir le traitement ?");
		return CktlAlertPage.newAlertPageWithResponder(this, "Traitements existants",
				DTStringCtrl.getHTMLString(msg.toString()), "Confirmer", "Annuler", null, CktlAlertPage.ATTENTION, responder);
	}

	// La classe interne - l'implementation de AlertResponder
	public class WarnTraitementExistsResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1:
				forceAddTraitementExists = true;
				return (listener().isUpdating() ? update() : (isClosing ? close() : add()));
			case 2:
				forceAddTraitementExists = false;
				return parentPage;
			default:
				return null;
			}
		}
	}

	/**
	 * Affichage du message si le serveur de planning indique un conflit.
	 * L'utilisateur peut tout de meme saisir son traitement s'il le souhaite.
	 */
	private WOComponent pageWarnPlanningIndispo() {
		WarnPlanningIndispoResponder responder = new WarnPlanningIndispoResponder();
		StringBuffer msg = new StringBuffer();
		msg.append("Attention, le serveur de planning indique que vous\n" +
						"n'êtes pas disponible pendant la période de ce traitement :\n\n");
		NSArray<String> errs = new NSArray<String>();
		if (!StringCtrl.isEmpty(listener().warnMessage())) {
			errs = NSArray.componentsSeparatedByString(listener().warnMessage(), ";");
		}
		for (int i = 0; i < errs.count(); i++) {
			String err = (String) errs.objectAtIndex(i);
			if (!StringCtrl.isEmpty(err))
				msg.append("   -").append(err).append("\n");
		}
		msg.append("\nVoulez-vous malgré tout saisir le traitement ?");
		return CktlAlertPage.newAlertPageWithResponder(this, "Message du serveur de planning",
				DTStringCtrl.getHTMLString(msg.toString()), "Confirmer", "Annuler", null, CktlAlertPage.ATTENTION, responder);
	}

	// La classe interne - l'implementation de AlertResponder
	public class WarnPlanningIndispoResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1:
				forceAddPlanningIndispo = true;
				return (listener().isUpdating() ? update() : (isClosing ? close() : add()));
			case 2:
				forceAddPlanningIndispo = false;
				return parentPage;
			default:
				return null;
			}
		}
	}

	/**
   * 
   */
	public WOComponent neFaitRien() {
		return null;
	}

	/**
	 * Execute l'action de mise a jour d'un traitement.
	 */
	public WOComponent update() {
		isClosing = false;
		NSMutableDictionary<String, Object> dico = collecterInfosTraitement();

		// Verif
		if (dico == null) {
			return null;
		}

		// on fait les controles de chevauchement que si la periode a change
		if (hasTraDateDebChanges || hasTraDateFinChanges) {
			// Controle si des traitements sont la pdt cette periode
			if (!forceAddTraitementExists) {
				NSArray<EOTraitement> recsTraitement = iBus().findTraitementsForPeriodeIgnoring(
						dtUserInfo().noIndividu(),
						(NSTimestamp) dico.valueForKey("aTraDateDeb"),
						(NSTimestamp) dico.valueForKey("aTraDateFin"),
						traOrdre);
				// oter le traitement actuel
				NSMutableArray<EOTraitement> recsTraitementMutable = new NSMutableArray<EOTraitement>(recsTraitement);
				recsTraitementMutable.removeIdenticalObject(listener().recTraitement());
				recsTraitement = recsTraitementMutable.immutableClone();
				if (recsTraitement.count() > 0) {
					return pageWarnTraitementExists(
							recsTraitement,
							(NSTimestamp) dico.valueForKey("aTraDateDeb"),
							(NSTimestamp) dico.valueForKey("aTraDateFin"));
				}
			}

			// Verif de disponibilite au travers du serveur de planning
			if (!shouldIgnoreServPlanning && !forceAddPlanningIndispo) {
				Properties repServer = AvantModifPlanning.isDisponible(
						cktlApp.getApplicationInstanceURL(context()),
						SPConstantes.IDKEY_INDIVIDU,
						dtUserInfo().noIndividu(),
						(NSTimestamp) dico.valueForKey("aTraDateDeb"),
						(NSTimestamp) dico.valueForKey("aTraDateFin"));
				if (AvantModifPlanning.getBoolean(repServer) == false) {
					listener().setWarnMessage(AvantModifPlanning.getMessage(repServer));
					return pageWarnPlanningIndispo();
				}
			}
		}

		// Verification OK, on enregistre le traitement
		if (!iBus().updateTraitement(null, dico)) {
			return CktlAlertPage.newAlertPageWithCaller(listener().cancelPage(), "Message d'erreur",
					"La mise à jour du traitement &agrave; &eacute;chou&eacute; !",
					"Retourner", CktlAlertPage.ERROR);
		}

		// rafraichir la page de consultation pour mettre a jour le nombre de
		// traitements
		parentPage.caller().takeValueForKey(Boolean.TRUE, "shouldRefresh");
		return listener().updatePage();
	}

	/**
	 * Execute l'action d'ajout d'un nouveau traitement.
	 */
	public WOComponent add() {
		isClosing = false;
		NSMutableDictionary<String, Object> dico = collecterInfosTraitement();

		// Verif
		if (dico == null) {
			return null;
		}

		// Controle si des traitements sont la pdt cette periode
		if (!forceAddTraitementExists) {
			NSArray<EOTraitement> recsTraitement = iBus().findTraitementsForPeriodeIgnoring(
					dtUserInfo().noIndividu(),
					(NSTimestamp) dico.valueForKey("aTraDateDeb"),
					(NSTimestamp) dico.valueForKey("aTraDateFin"),
					null);
			if (recsTraitement.count() > 0) {
				return pageWarnTraitementExists(
						recsTraitement,
						(NSTimestamp) dico.valueForKey("aTraDateDeb"),
						(NSTimestamp) dico.valueForKey("aTraDateFin"));
			}
		}

		// Verif de disponibilite au travers du serveur de planning
		if (!shouldIgnoreServPlanning &&
				!forceAddPlanningIndispo) {
			long l1 = System.currentTimeMillis();
			Properties repServer = AvantModifPlanning.isDisponible(
					cktlApp.getApplicationInstanceURL(context()),
					SPConstantes.IDKEY_INDIVIDU,
					dtUserInfo().noIndividu(),
					(NSTimestamp) dico.valueForKey("aTraDateDeb"),
					(NSTimestamp) dico.valueForKey("aTraDateFin"));
			l1 = System.currentTimeMillis() - l1;
			CktlLog.log("time access : " + l1 + " ms.");
			if (AvantModifPlanning.getBoolean(repServer) == false) {
				listener().setWarnMessage(AvantModifPlanning.getMessage(repServer));
				return pageWarnPlanningIndispo();
			}
		}

		// Verification OK, on enregistre le traitement
		traOrdre = iBus().addTraitement(null, dico);
		if (traOrdre == null) {
			return CktlAlertPage.newAlertPageWithCaller(
					listener().cancelPage(),
					"Message d'erreur",
					"L'ajout de nouveau traitement a échoué !",
					"Retourner",
					CktlAlertPage.ERROR);
		}

		// Sauvegarde de fichier attache
		// Les chemins des fichiers attaches
		attachedFiles = new NSArray<String>(filePaths);
		if (attachedFiles.count() > 0) {
			attachedFiles = dtSession().dtDocumentCenter().saveAttachements(
					eoIntervention().intOrdre(),
					traOrdre,
					attachedFiles,
					DTDocumentCenter.CATEGORIE_DT_COMMUN);
			if (attachedFiles.count() == 0) {
				listener().setErrorMessage("Les fichiers attachés n'ont pas pu être enregistrés !");
			}
			// des fichiers ont ete ajoutes : refresh de cette liste dans la taskbar
			// parentPage.taskBar.setShouldInitAttachments(true);
		}

		// Envoi de mail
		if (chEmail) {
			mailCenter().reset();
			mailCenter().setIntervention(eoIntervention(), true);
			mailCenter().setTraitements(iBus().findTraitements(traOrdre, null, null, null), false, false);
			mailCenter().setTraitementsFiles(new NSDictionary<String, Object>(attachedFiles, traOrdre.toString()));
			mailCenter().mailAjouterTraitementDT(dtUserInfo().sendMailTraitement());
			listener().setSendMailTraitement(true);
		}

		// rafraichir la page de consultation pour mettre a jour le nombre de
		// traitements
		parentPage.caller().takeValueForKey(Boolean.TRUE, "shouldRefresh");
		return listener().addPage();
	}

	/**
	 * executer la premiere partie de l'action de cloture de la demande -
	 * verifications des donnees - ajout du traitement dans la DB - sauvegarde
	 * fichiers attaches - afficher message de confirmation
	 */
	public WOComponent close() {
		isClosing = true;
		NSMutableDictionary<String, Object> dico = collecterInfosTraitement();

		// Verif
		if (dico == null) {
			return null;
		}

		// Controle si des traitements sont la pdt cette periode
		if (!forceAddTraitementExists) {
			NSArray<EOTraitement> recsTraitement = iBus().findTraitementsForPeriodeIgnoring(
					dtUserInfo().noIndividu(),
					(NSTimestamp) dico.valueForKey("aTraDateDeb"),
					(NSTimestamp) dico.valueForKey("aTraDateFin"),
					null);
			if (recsTraitement.count() > 0) {
				return pageWarnTraitementExists(
						recsTraitement,
						(NSTimestamp) dico.valueForKey("aTraDateDeb"),
						(NSTimestamp) dico.valueForKey("aTraDateFin"));
			}
		}

		// Verif de disponibilite au travers du serveur de planning
		if (!shouldIgnoreServPlanning && !forceAddPlanningIndispo) {
			Properties repServer = AvantModifPlanning.isDisponible(
					cktlApp.getApplicationInstanceURL(context()),
					SPConstantes.IDKEY_INDIVIDU,
					dtUserInfo().noIndividu(),
					(NSTimestamp) dico.valueForKey("aTraDateDeb"),
					(NSTimestamp) dico.valueForKey("aTraDateFin"));
			if (AvantModifPlanning.getBoolean(repServer) == false) {
				listener().setWarnMessage(AvantModifPlanning.getMessage(repServer));
				return pageWarnPlanningIndispo();
			}
		}

		// Verification OK, on enregistre le traitement
		if ((traOrdre = iBus().addTraitement(null, dico)) == null) {
			return CktlAlertPage.newAlertPageWithCaller(listener().cancelPage(), "Message d'erreur",
					"L'ajout de nouveau traitement a échoué !",
					"Retourner", CktlAlertPage.ERROR);
		}

		// Sauvegarde de fichier attache
		// Les chemins des fichiers attaches
		attachedFiles = new NSArray<String>(filePaths);
		if (attachedFiles.count() > 0) {
			attachedFiles = dtSession().dtDocumentCenter().saveAttachements(
					eoIntervention().intOrdre(),
					traOrdre,
					attachedFiles,
					DTDocumentCenter.CATEGORIE_DT_COMMUN);
			if (attachedFiles.count() == 0) {
				listener().setErrorMessage("Les fichiers attaches n'ont pas pu être enregistrés !");
			}
			// des fichiers ont ete ajoutes : refresh de cette liste dans la task bar
			// parentPage.taskBar.setShouldInitAttachments(true);
		}

		if (dtUserInfo().confirmationCloture()) {
			// Test, si la DT peut etre definitivement validee en passant par une page
			// de confirmation
			DTConfirmCloseResponder responder = new DTConfirmCloseResponder();
			StringBuffer txtMsg = new StringBuffer();
			txtMsg.append("<center>Confirmation de l'operation:<br><br>");
			txtMsg.append("Est-ce que la demande est definitivement trait&eacute;e ?");
			txtMsg.append("<br><br>");
			txtMsg.append("<font class=\"textNote\">");
			txtMsg.append("Note : vous pouvez désactiver cette confirmation dans Préférences > Divers");
			txtMsg.append("</font>");

			return CktlAlertPage.newAlertPageWithResponder(this, "Confirmation de cl&ocirc;ture de DT<br>",
					txtMsg.toString(), "Confirmer", "Annuler", null, CktlAlertPage.QUESTION, responder);
		} else {
			// pas de confirmation
			return confirmClose(true, false);
		}

	}

	/**
	 * Execute la partie finale de l'action de la cloture de la demande.
	 * 
	 * @param traite
	 *          : faut-il clore definitivement la DT
	 * @param isDTPrestation
	 *          : indique cette DT est une prestation
	 */
	protected WOComponent confirmClose(boolean traite, boolean isDTPrestation) {
		try {
			// Determiner si le demandeur est le secretaire de la structure
			// pour laquelle il cree la DT
			Number noDemandeur = eoIntervention().intNoIndConcerne(); // numero du
																																// demandeur
			boolean isSecretaire = false;
			boolean isInfosBudgetairesCompletes = false;
			boolean isEnoughDisponible = true;
			if (isDTPrestation && !pieBus().checkPIEService()) {
				isDTPrestation = false;
			}

			// traitement specifique pour les DT Prestation
			Number intOrdre = eoIntervention().intOrdre();
			if (isDTPrestation) {
				NSArray<Integer> nosIndividuSec = (NSArray<Integer>) dtSession().dataCenter().serviceBus().
						responsablesPrestationForStructure(getStrucPersId()).valueForKey(EOIndividu.NO_INDIVIDU_KEY); // liste
																																																					// des
																																																					// noIndividu
																																																					// des
																																																					// secretaires
				isSecretaire = (nosIndividuSec.containsObject(noDemandeur));
				// verification si les infos obligatoire pour une facture sont la
				isInfosBudgetairesCompletes = iBus().isInfosBudgetairesCompletes(intOrdre);
			}

			// Operations de cloture definitive de la DT
			if (traite) {
				// Supperssion des traitements planifies
				if (iBus().deleteTraitements(null, null, EOEtatDt.ETAT_EN_COURS, eoIntervention()) == -1) {
					StringBuffer errMsg = new StringBuffer();
					errMsg.append("Il existe des traitements planifies qui ne peuvent pas être supprimés.<BR>");
					errMsg.append("La demande ne peut pas être clôse!<BR><BR>");
					errMsg.append("Vous pouvez utiliser Agenda pour supprimer/valider les traitements planifies<BR>");
					errMsg.append("( Agenda > Prevision/Realisation )");
					listener().setErrorMessage(errMsg.toString());
					return null;
				}
				// DT Prestation : fermeture de la prestation selon conditions
				// particulieres
				if (isDTPrestation && isSecretaire && isInfosBudgetairesCompletes) {
					// verification du disponible sur la ligne budgetaire
					isEnoughDisponible = dtPrestaGateway().isEnoughDisponible();
					if (isEnoughDisponible) {
						//

						// fermeture de la prestation
						try {
							dtPrestaGateway().validerToutDevis();
						} catch (Throwable e) {
							// recuperer l'exception de PIE et l'afficher
							listener().setErrorMessage(e.getMessage());
							return listener().errorPage();
						}
					}
				}
				// Verification que le workflow est bien respecté
				A_WorkFlow workFlow = eoIntervention().getWorkFlow();
				if (workFlow != null) {
					for (int i = 0; i < eoIntervention().tosTraitement().count(); i++) {
						EOTraitement traitement = (EOTraitement) eoIntervention().tosTraitement().objectAtIndex(i);
						workFlow.ajouterTacheFaite(traitement.toTraitementType());
					}
					// on oublie pas le traitement cours d'ajout
					workFlow.ajouterTacheFaite(getTraitementTypeSelected());
					if (workFlow.isEtapesObligatoiresTerminees() == false) {
						listener().setErrorMessage("La demande n'a pa pu être cloturée !\n" + workFlow.getMessageErreur());
						return listener().errorPage();
					}
				}

				// Maintenant on valide la DT
				if (!iBus().updateIntervention(
						dtUserInfo(), null, intOrdre, null, null, null, EOEtatDt.ETAT_TRAITEES, null, null, null, null, null)) {
					listener().setErrorMessage("La demande n'a pa pu être cloturée !");
					return listener().errorPage();
				}
			}

			mailCenter().prepareMailAjoutTraitement(
					eoIntervention(),
					traOrdre,
					traite,
					true,
					isDTPrestation,
					isSecretaire,
					isInfosBudgetairesCompletes,
					isEnoughDisponible,
					dtUserInfo().sendMailTraitement());

		} catch (Throwable ex) {
			StringBuffer errMsg = new StringBuffer();
			errMsg.append("Une erreur c'est produite lors de l'ajout du traitement :\n");
			errMsg.append(CktlLog.getMessageForException(ex));
			listener().setErrorMessage(errMsg.toString());
			// suppresion du traitement
			iBus().deleteTraitements(null, EOTraitement.fetchByKeyValue(iBus().editingContext(), EOTraitement.TRA_ORDRE_KEY, traOrdre), null, null);
			return listener().errorPage();
		}

		if (traite) {
			return listener().closePage();
		} else {
			return listener().addPage();
		}
	}

	/**
	 * Classe de gestion de la page de confirmation O/N pour la cloture definitive
	 * de la DT
	 */
	public class DTConfirmCloseResponder implements CktlAlertResponder {
		public DTConfirmCloseResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1: // CONFIRMER
				return confirmClose(true, false);
			case 2: // ANNULER
				return confirmClose(false, false);
			default:
				return null;
			}
		}
	}

	/**
	 * Classe de gestion de la page de confirmation O/N d'annuler la saisie d'un
	 * traitement
	 */
	public class DTConfirmCancelResponder implements CktlAlertResponder {
		public DTConfirmCancelResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1: // QUITTER : retour a la liste des traitements
				return listener().cancelPage();
			case 2: // ANNULER
				// aucun changement, on revient a la page precedente
				return parentPage;
			default:
				return null;
			}
		}
	}

	private DTPrestaGateway _dtPrestaGateway;

	/**
	 * Pointeur sur la passerelle de devis
	 */
	protected DTPrestaGateway initDtPrestaGateway(Integer catOrdre) {
		if (_dtPrestaGateway == null) {
			if (prestId() != null && pieBus().checkPIEService()) {
				// instancier la passerelle de devis
				_dtPrestaGateway = new DTPrestaGateway(dtSession().pieBus(),
						prestId(), getIndPersId(), getStrucFouOrdre(), catOrdre);
				try {
					dtPrestaGateway().getOriginalDevisFromPie();
				} catch (Exception e) {
					e.printStackTrace();
					listener().setErrorMessage(dtPrestaGateway().errorMessage());
				}
			}
		}
		return _dtPrestaGateway;
	}

	public DTPrestaGateway dtPrestaGateway() {
		return _dtPrestaGateway;
	}

	/**
	 * Retourne la numero <code>prestNumero</code> du devis de la DT en cours.
	 */
	public Integer prestNumero() {
		return parentPage.getPrestNumero();
	}

	/**
	 * Retourne la numero <code>prestNumero</code> du devis de la DT en cours.
	 */
	public Integer prestId() {
		return parentPage.getPrestId();
	}

	/**
	 * Retourne la clé <code>persid</code> de l'individu qui a cree la DT.
	 */
	private Integer getIndPersId() {
		return parentPage.getIndPersId();
	}

	/**
	 * Retourne la clé <code>persid</code> de la structure du contact de
	 * l'individu qui a cree la DT.
	 */
	private Integer getStrucPersId() {
		return parentPage.getStrucPersId();
	}

	/**
	 * Retourne la clé <code>fouOrdre</code> de la structure du contact de
	 * l'individu qui a cree la DT.
	 */
	private Integer getStrucFouOrdre() {
		return parentPage.getStrucFouOrdre();
	}

	// Setters pour controle des valeurs qui ont changé ou pas

	public void setTraDateDeb(NSTimestamp value) {
		hasTraDateDebChanges = true;
		traDateDeb = value;
	}

	public void setTraDateFin(NSTimestamp value) {
		hasTraDateFinChanges = true;
		traDateFin = value;
	}

	public void setTfTraitement(String value) {
		hasTfTraitementChanges = true;
		tfTraitement = value;
	}

	// getter

	/**
	 * Le titre de la boite change si on ajoute ou si on modifie
	 */
	public String getBoxTraitementTitle() {
		String boxTitle = null;

		if (listener().isUpdating()) {
			boxTitle = "Modification d'un traitement de type \"" + listener().recTraitement().toTraitementType().ttyLibelle() + "\"";
		} else {
			boxTitle = "Ajout d'un nouveau traitement";
		}

		return boxTitle;
	}

	/*
	 * @see A_CreationSwapView#attachedFilePaths()
	 */
	public NSArray<String> attachedFilePaths() {
		return filePaths;
	}

	/**
	 * Retourne la reference vers le listener des traitement. Cette objet
	 * correspond au connecteur "traitementListener" qui doit toujours etre
	 * defini.
	 */
	public I_TraitementListener listener() {
		return traitementListener;
	}

	/**
	 * Retourne la reference vers le gestionnaire des interventions.
	 * 
	 * @return
	 */
	private DTInterventionBus iBus() {
		return dtSession().dataCenter().interventionBus();
	}

	/**
	 * Retourne la reference vers le gestionnaire des contacts.
	 * 
	 * @return
	 */
	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	/**
	 * Pointeur vers le gestionnaire de Prestations PIE
	 */
	protected DTPrestaBusWeb pieBus() {
		return dtSession().pieBus();
	}

	/**
	 * Raccourci vers le gestionnaire de mail
	 * 
	 * @return
	 */
	private DTMailCenter mailCenter() {
		return dtSession().mailCenter();
	}

	public final String getReceptionCommandeSurcoutSigneON() {
		return receptionCommandeSurcoutSigneON;
	}

	/**
	 * Sous certaines condition, on va préremplir le traitement additionnel
	 * 
	 * @param receptionCommandeSurcoutSigneON
	 */
	public final void setReceptionCommandeSurcoutSigneON(String receptionCommandeSurcoutSigneON) {
		this.receptionCommandeSurcoutSigneON = receptionCommandeSurcoutSigneON;
		if (getTraitementTypeSelected().isTraitementReceptionBdcInterneSigne() &&
				eoIntervention().isPosteSupplementaire()) {
			if (receptionCommandeSurcoutSigneON.equals(NON)) {
				// le remplir que s'il est vide
				setTfTraitementAdditionnel(StringCtrl.replace(
						dtApp().config().stringForKey(I_ApplicationConsts.PHRASE_SURCOUT_POSTE_NON_RESTITUE_KEY),
						"${" + I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY + "}",
						dtApp().config().stringForKey(I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY)));
				// forcer le setter à ne pas marcher au prochain coup, car la valeur
				// va etre écrasée par celle qui y était avant
				shouldIgnoreSetterTfTraitementAdditionnel = true;
				// l'afficher
				isExpandedTraitementAdditionnel = true;
			} else {
				// sinon on le vide
				setTfTraitementAdditionnel("");
				// le masquer
				isExpandedTraitementAdditionnel = false;
			}
		}
	}

	public final String getTfTraitementAdditionnel() {
		return tfTraitementAdditionnel;
	}

	/**
	 * Corriger les pb liés à l'affectation automatique plus ou moins foireuse
	 * 
	 * @param tfTraitementAdditionnel
	 */
	public final void setTfTraitementAdditionnel(String tfTraitementAdditionnel) {
		if (shouldIgnoreSetterTfTraitementAdditionnel == false) {
			this.tfTraitementAdditionnel = tfTraitementAdditionnel;
		}
		if (shouldIgnoreSetterTfTraitementAdditionnel) {
			shouldIgnoreSetterTfTraitementAdditionnel = false;
		}
	}

	/**
	 * Interception du changement du type dans la popup
	 * 
	 * @param traitementTypeSelected
	 */
	public final void setTraitementTypeSelected(EOTraitementType traitementTypeSelected) {
		this.traitementTypeSelected = traitementTypeSelected;

		if (traitementTypeSelected.isTraitementClotureSurcoutNonSigne()) {
			tfTraitement = StringCtrl.replace(
					dtApp().config().stringForKey(I_ApplicationConsts.PHRASE_SURCOUT_POSTE_NON_RESTITUE_CLOTURE_KEY),
					"${" + I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY + "}",
					dtApp().config().stringForKey(I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY));
			tfTraitement = StringCtrl.replace(
					tfTraitement,
					"${" + EOIntervention.MOTIF_COMMANDE_MATERIEL_CLE_DESTINATAIRE + "}",
					eoIntervention().getDestinatairePoste());
			tfTraitementAdditionnel = null;
		} else {
			tfTraitement = traitementTypeSelected.getTraitementMotifPourType(dtApp().config());
			tfTraitementAdditionnel = null;
		}

	}
}