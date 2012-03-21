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
import java.util.Hashtable;

import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EOHistoriqueMotif;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOInterventionInfin;
import org.cocktail.dt.server.metier.EOInterventionRepro;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.GEDDescription;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlMailPage;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;

import fr.univlr.cri.dt.app.DTReproDataInfo;
import fr.univlr.cri.dt.app.DTReproDataInfoULR;
import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;

/**
 * Gestionnaire de l'interface de l'inspecteur de la DT "generique".
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class InspecteurDT
		extends DTWebComponent
		implements I_ConsultTaskBarListener, I_SelectActiviteReferer {

	protected EOIntervention demande;
	protected WOComponent pageRetour;

	// photo du demandeur
	public NSData photo;

	// fichiers attaches via GEDFS
	// pour la demande
	protected boolean hasAttachements;
	protected NSMutableArray<String> attachementPaths;

	protected final static String ANCHOR_TRAITEMENT = "traitements";

	private final static String STR_ERROR_NO_INTERVENTION = "erreur";

	/** message d'erreur issu du composant de traitements **/
	public String errorMessage;

	/** message d'erreur issu de l'application PIE **/
	public String pieErrorMessage;

	/** message d'erreur du aux infos internes de facturation **/
	public String factErrorMessage;

	/** La reference vers la page principale dont cette vue fait partie. */
	private A_PageUsingListeDemande caller;

	/** La reference vers l'instance de la taskbar. */
	protected ConsultTaskBar taskBar;

	/** faut-il recharger la liste des fichiers attaches **/
	private boolean shouldInitAttachments;

	/** le numero du devis pour affichage */
	private Integer prestNumero;

	/** informations sur le contact pour se connecter au WebServices */
	private Integer indPersId;
	private Integer structPersId;
	private Integer structFouOrdre;
	private Boolean _isServicePayeurFournisValidInterne;

	/** Indique si c'est une DT Reprographie */
	protected Boolean isDTRepro;

	/** Indique si cette DT utilise le service PIE */
	protected Boolean isDTPrestation;

	/** */
	public Boolean canChangerActivite;

	/** */
	public Boolean canChangerMotif;

	/** */
	public Boolean canChangerDemandeur;

	/** */
	protected Boolean canChangerCfc;

	/** Autorisation de recreer le devis */
	protected Boolean canRecreerDevis;

	/** Autorisation de changer les codes analytiques */
	public Boolean canNotChangeCodeAna;

	/** l'ancre HTML a atteindre dans la page */
	public String targetPosition;

	/** le lien URL permanent vers la DT */
	public String strUrlPermanent;

	/** indique si les WS de Pie sont disponibles */
	public Boolean isPieOffline;
	public boolean isDisabledBtnRecreerDevis;

	/** */
	public boolean isMotifEnCoursDeModification;

	/** */
	public String motifAAfficher;
	public String motif;

	/** historique des revisions */
	public EOHistoriqueMotif eoHistoriqueMotifItem;
	public EOHistoriqueMotif eoHistoriqueMotifSelected;

	public InspecteurDT(WOContext context) {
		super(context);
		attachementPaths = new NSMutableArray<String>();
		setShouldInitAttachments(true);
	}

	public void appendToResponse(WOResponse arg0, WOContext arg1) {

		// gestion de la recherche des fichiers dans GEDFS
		if (demande != null && shouldInitAttachments) {
			initAttachements(demande.intForKey("intOrdre"));
			setShouldInitAttachments(false);
		}

		super.appendToResponse(arg0, arg1);

		// script pour l'ouverture de la popup de la conf info
		addLocalJScript(arg0, "js/popup.js");
	}

	/**
	 * Appel externe
	 * 
	 * @param session
	 * @param pageRetour
	 * @param recDemande
	 * @return
	 */
	public static InspecteurDT getNewInspecteur(Session session, A_PageUsingListeDemande pageRetour, CktlRecord recDemande) {
		InspecteurDT inspecteur = (InspecteurDT) session.getSavedPageWithName(InspecteurDT.class.getName());
		inspecteur.afficherInspecteur(pageRetour, recDemande);
		inspecteur.setCaller(pageRetour);
		return inspecteur;
	}

	public void afficherInspecteur(A_PageUsingListeDemande pageRetour, CktlRecord recDemande) {
		setCaller(pageRetour);
		demande = (EOIntervention) recDemande;
		hasDevis = false;
		isDisabledBtnRecreerDevis = true;
		_isServicePayeurFournisValidInterne = null;
		isMotifEnCoursDeModification = false;

		if (isRecInterventionExists()) {
			motif = demande.intMotif();
			motifAAfficher = StringCtrl.replace(WOMessage.stringByEscapingHTMLAttributeValue(
					motif), "&#10;", "<br/>");
		} else {
			motif = STR_ERROR_NO_INTERVENTION;
			motifAAfficher = STR_ERROR_NO_INTERVENTION;
		}

		// verification que la demande existe
		if (demande != null) {
			isDTRepro = (isDTRepro(demande.intOrdre().intValue()) ? Boolean.TRUE : Boolean.FALSE);
			isDTPrestation = new Boolean(serviceBus().isServiceDTPrestation(demande.cStructure()));
			strUrlPermanent = dtSession().appConsultationURL(demande.intOrdre().intValue());
			canChangerActivite = new Boolean(interventionBus().canChangeActivite(demande, dtUserInfo().noIndividu()));
			canChangerMotif = new Boolean(interventionBus().canChangeMotif(demande, dtUserInfo().noIndividu()));
			canChangerDemandeur = new Boolean(interventionBus().canChangeDemandeur(demande, dtUserInfo().noIndividu()));
			canNotChangeCodeAna = new Boolean(!interventionBus().canChangeCodeAna(demande, dtUserInfo().noIndividu()));
			getPhotoForDT(demande.intNoIndConcerne().intValue());
			this.pageRetour = pageRetour;
			// infos budgetaires
			if (hasInterventionInfin()) {
				prestNumero = getCurrentDevis();
				servicePayeur = serviceBus().libelleForServiceCode(
						interventionInfin().cStructure(), true, true);
				ligneBudgetaire = libelleLigneBudgetaire();
				destination = jefyBus().libelleDestinationInfin(interventionInfin().lolfId());
				// pour acces au webservices : persid et cStructure du contact
				indPersId = new Integer(demande.toIndividuConcerne().persId().intValue());
			}
			// recherche de la structure "cliente"
			if (demande.ctOrdre() != null) {
				// par le contact
				String cStructService = dtSession().dataCenter().contactsBus().findContact(
						demande.ctOrdre()).stringForKey("cStructService");
				CktlRecord recService = dtSession().dataCenter().serviceBus().structureForCode(cStructService);
				structPersId = (recService != null ? new Integer(recService.intForKey("persId")) : null);
			} else if (hasInterventionInfin()) {
				// par les infos de facturation
				String cStructService = interventionInfin().stringForKey("cStructure");
				CktlRecord recService = dtSession().dataCenter().serviceBus().structureForCode(cStructService);
				structPersId = (recService != null ? new Integer(recService.intForKey("persId")) : null);
				structFouOrdre = (Integer) interventionInfin().numberForKey("fouOrdre");
			}
			if (isDTRepro == Boolean.TRUE) {
				recRepro =
						dtDataCenter().interventionBus().findInterventionRepro(
								DTDataBus.DefaultTransactionId, recDemande.numberForKey("intOrdre"));
				// reconstruire un DTReproDataInfoULR a partir de la DB
				reproDataInfo = new DTReproDataInfoULR();
				Hashtable hashRepro = CktlRecord.recordToHashtable(recRepro, false);
				reproDataInfo.takeInfoFromRecord(hashRepro);
				// quelques complements qui manquent dans takeInfoFromRecord
				reproDataInfo.actOrdre = demande.actOrdre().intValue();
				CktlRecord recActivite = dtDataCenter().activiteBus().findActivite(
						demande.numberForKey("actOrdre"), demande.cStructure());
				reproDataInfo.cartOrdre = recActivite.intForKey("cartOrdre");
				// raz des donnees de CFC
				_cfcListener = null;
				// memes droits sur le CFC que sur les activites
				canChangerCfc = canChangerActivite;
				// mêmes droits sur la recréation de devis que sur les actvitiés
				canRecreerDevis = canChangerActivite;
			}
		} else {
			// message d'erreur
		}
	}

	/**
	 * initialise tous les fichiers attaches a la DT : - pour la demande seule -
	 * pour tous les traitements
	 * 
	 * @param intOrdre
	 */
	private void initAttachements(int intOrdre) {

		// la demande
		EOQualifier condition = DTDataBus.newCondition("intOrdre=" + intOrdre + " AND traOrdre=nil");
		NSArray objects = dtSession().dataBus().fetchArray("DocumentDt", condition, null);
		CktlRecord rec;
		GEDDescription gedDescription;

		hasAttachements = (objects.count() > 0);
		attachementPaths.removeAllObjects();
		if (hasAttachements) {
			for (int i = 0; i < objects.count(); i++) {
				rec = (CktlRecord) objects.objectAtIndex(i);
				gedDescription = dtSession().gedBus().inspectDocumentGED(rec.intForKey("docOrdre"));
				if (gedDescription != null)
					attachementPaths.addObject(gedDescription.reference);
			}
		}
	}

	/**
	 * Faut-il afficher la liste des intervenants affectes (non si personne
	 * d'affecte)
	 */
	public boolean showIntervenants() {
		return isRecInterventionExists() && demande.tosIntervenant().count() > 0;
	}

	public String stringIntervenants() {
		NSArray noms = NSArrayCtrl.flattenArray(
				demande.arrayForKeyPath("tosIntervenant.toIndividuUlr.nomEtPrenom"));
		String str = "";
		for (int i = 0; i < noms.count(); i++) {
			str += noms.objectAtIndex(i);
			if (i < noms.count() - 1)
				str += ", ";
		}
		return str;
	}

	public String mailDemandeur() {
		if (demande == null)
			return StringCtrl.emptyString();
		CktlUserInfoDB ui = new CktlUserInfoDB(cktlApp.dataBus());
		ui.individuForNoIndividu(demande.intNoIndConcerne().intValue(), true);
		return ui.email();
	}

	/**
	 * Les logins associé au compte du demandeur
	 * 
	 * @return
	 */
	public String comptesDemandeur() {
		if (demande == null)
			return StringCtrl.emptyString();
		String strComptes = new String();

		EOIndividu demandeur = (EOIndividu) demande.recForKey(EOIntervention.TO_INDIVIDU_CONCERNE_KEY);

		strComptes = demandeur.comptes();

		return strComptes;
	}

	/**
	 * 
	 * @return
	 */
	public String contactInfo() {
		return isRecInterventionExists() ? DTStringCtrl.formatLocalInfoForHTML(
				dtDataCenter().contactsBus().getInterventionContactDescription(
						demande.intOrdre(), demande.ctOrdre(), "\n")) : STR_ERROR_NO_INTERVENTION;
	}

	public WOComponent envoyerMailDemandeur() {
		dtSession().mailCenter().reset();
		dtSession().mailCenter().setIntervention(demande, true);
		Hashtable params = dtSession().mailCenter().collectInfoMailInformationsDT();
		CktlMailPage mPage = CktlMailPage.newMailPage(this,
				dtUserInfo().email(),
				(String) params.get("to"),
				(String) params.get("cc"),
				(String) params.get("sujet"),
				(String) params.get("motif"));
		// On ouvre le "to" si ce dernier est vide
		boolean canChangeTo = (StringCtrl.isEmpty((String) params.get("to")));
		mPage.setCanChangeTo(canChangeTo);
		mPage.setCanChangeSubject(false);
		mPage.setHasFixedMessage(false);
		// d'apres les prefs
		mPage.setUseCktlMailPage(!dtUserInfo().useMailInterne());
		return mPage;
	}

	/**
	 * Retourne le libelle du service destinataire de la demande.
	 */
	public String libelleService() {
		return isRecInterventionExists() ? dtDataCenter().serviceBus().libelleForServiceCode(
				demande.cStructure(), true, true) : STR_ERROR_NO_INTERVENTION;
	}

	/**
	 * Retourne le libelle de l'etat de la demande.
	 */
	public String libelleEtat() {
		return isRecInterventionExists() ? dtDataCenter().etatBus().libelleForEtat(
				demande.intEtat()) : STR_ERROR_NO_INTERVENTION;
	}

	/**
	 * TODO A FAIRE !!!! Retourne le libelle de la destination, si elle est
	 * indique.
	 */
	public String libelleDestination() {
		/*
		 * CktlRecord rec = null; if (demande.valueForKey("lolfId") != null) { rec =
		 * dtDataCenter().jefyBus().findDestin(demande.numberForKey("lolfId")); } if
		 * (rec == null) { return "&lt;&nbsp;ind&eacute;fini&nbsp;&gt;";/* } else {
		 * return rec.stringForKey("dstLib"); }
		 */
		return "&lt;&nbsp;momentan&eacute;ment indisponible&nbsp;&gt;";
	}

	/* === Les methodes de I_ConsultTaskBarListener === */

	public I_ConsultTaskBarListener taksBarListener() {
		return this;
	}

	public WOComponent goBack() {
		// refresh liste DT
		caller().takeValueForKey(Boolean.TRUE, "shouldRefresh");
		return pageRetour;
	}

	/**
	 * Cette methode ne sera appel�e quoi qu'il en soit que par la
	 * PageConsultation
	 */
	public WOComponent getAfterValidationPage() {
		PageConsultation page =
				(PageConsultation) dtSession().getSavedPageWithName(PageConsultation.class.getName());
		dtSession().setPageContenu(page);
		page.listener.doFetchDisplayGroup();
		return page;
	}

	public WOComponent getAfterRejetPage() {
		return getAfterValidationPage();
	}

	/**
	 * Retourne la reference vers la page de consultation des demandes
	 * (PageConsultation ou PageRecherche). La vue de creation des demandes ne
	 * fait qu'une partie de cette page.
	 */
	protected A_PageUsingListeDemande caller() {
		return caller;
	}

	private void getPhotoForDT(Number noIndividu) {
		// Test, si la gestion des photos est implemente
		if (dtDataCenter().preferencesBus().usePhoto())
			photo = dtDataCenter().individuBus().photoForNoIndividu(
					noIndividu, demande.stringForKey("cStructure"));
		else
			photo = null;
	}

	/**
   * 
   */
	public boolean isAfficherPhoto() {
		return (photo != null);
	}

	private boolean isDTRepro(int intOrdre) {
		EOQualifier condition = DTDataBus.newCondition("intOrdre=" + intOrdre);
		NSArray objects = dtSession().dataBus().fetchArray("InterventionRepro", condition, null);
		return (objects.count() > 0);
	}

	private EOInterventionInfin _interventionInfin;

	/**
	 * Information financieres liees a la DT
	 */
	private EOInterventionInfin interventionInfin() {
		if (_interventionInfin == null) {
			_interventionInfin = interventionBus().findInterventionInfin(DTDataBus.DefaultTransactionId,
					demande.intOrdre());
		}
		return _interventionInfin;
	}

	/**
	 * Retourne le code de devis correspondant a la demande selectionne. Retourne
	 * <i>null</i> si aucune demande n'est selectionne, si la demande n'as pas de
	 * devis associe ou s'il y a un probleme de communication avec les service Web
	 * prestations.
	 */
	private Integer getCurrentDevis() {
		hasDevis = false;
		// On teste le devis
		if (hasInterventionInfin()) {
			Integer prestNumero = (Integer) interventionInfin().valueForKey("prestNumero");
			if (prestNumero == null) {
				// si pas de devis, alors erreur !
				factErrorMessage = "Erreur : Il n'y a pas de devis attaché a la demande !";
			}
			// Le devis existe et le service est disponible
			if ((interventionInfin().valueForKey("prestNumero") != null) && pieBus().checkPIEService()) {
				hasDevis = true;
				return (Integer) interventionInfin().valueForKey("prestNumero");
			}
		}
		return null;
	}

	// partie gestion PIE

	/**
	 * Permet à l'administrateur de créer / recréer le devis dans PIE si ce
	 * dernier n'a pas été correctement généré à la création de la DT (Pie
	 * plantée, pb réseau ...).
	 * 
	 * @return
	 */
	public WOComponent recreerDevis() {
		// il faut absolument un enregistrement INTERVENTION_INFIN
		// s'assurer qu'il y a bien absence du devis
		if (hasInterventionInfin() &&
				interventionInfin().prestId() == null) {
			// faire la configuration de PIE pour cette DT
			paramPieBus();
			// creation du devis
			Hashtable result = pieBus().createDevis(
					DTReproDataInfo.getLibelleDevis(
							demande.intCleService(), recRepro.libelle()),
					(Integer) interventionInfin().orgId(),
					interventionInfin().tcdCode(),
					(Integer) interventionInfin().lolfId(),
					dtApp().defaultReproPcoNum(),
					reproDataInfo().getLignes());
			if (pieBus().hasError()) {
				errorMessage =
						"Erreur lors de l'enregistrement d'un devis : <br>" +
								pieBus().errorMessage();
			} else {
				// enregistrer les infos budgetaires
				prestNumero = Integer.valueOf(result.get(DTPrestaServicesConst.FormPrestNumeroKey).toString());
				Number prestId = Integer.valueOf(result.get(DTPrestaServicesConst.FormPrestIdKey).toString());
				interventionBus().interventionBus().setInterventionInfin(
						null, demande.intOrdre(), null, null, null, null, null, null, null, null, null, prestId, prestNumero);
				// indiquer que le devis existe dorénavant
				hasDevis = true;
			}
			// suppression du cache pour forcer l'application a refetcher le numéro
			// du devis
			_interventionInfin = null;
		}

		return null;
	}

	/**
	 * Test si la pie marche
	 * 
	 * @return
	 */
	public WOComponent testerDisponibilitePie() {
		isPieOffline = Boolean.TRUE;
		isDisabledBtnRecreerDevis = true;
		pieBus().resetPieServiceDispo();
		if (pieBus().checkPIEService()) {
			isPieOffline = Boolean.FALSE;
			isDisabledBtnRecreerDevis = false;
		}
		return null;
	}

	/**
	 * Indique si la disponibilité de PIE a été testée
	 * 
	 * @return
	 */
	public boolean isPieTested() {
		return isPieOffline != null;
	}

	/**
	 * Indique si le contact de facturation est bien un service autorisé a faire
	 * des prestations ... si c'est pas le cas, ca va poser des soucis dans PIE
	 * ...
	 * 
	 * @return
	 */
	public boolean isServicePayeurFournisValidInterne() {
		//
		if (_isServicePayeurFournisValidInterne == null) {
			// on ne traite pas les cas des services payeurs inconnus
			String cStructurePayeur = hasInterventionInfin() ? interventionInfin().stringForKey("cStructure") : null;
			if (!StringCtrl.isEmpty(cStructurePayeur)) {
				_isServicePayeurFournisValidInterne = new Boolean(jefyBus().getFouOrdreForStructure(cStructurePayeur) != null);
			} else {
				_isServicePayeurFournisValidInterne = Boolean.TRUE;
			}
		}
		return _isServicePayeurFournisValidInterne.booleanValue();
	}

	/**
	 * Retourne la reference vers le gestionnaire des interventions.
	 */
	protected DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	/**
	 * Retourne la reference vers le gestionnaire des interventions.
	 */
	protected DTServiceBus serviceBus() {
		return dtSession().dataCenter().serviceBus();
	}

	/**
	 * Retourne la reference vers le gestionnaire des services.
	 */
	private DTPrestaBusWeb pieBus() {
		return dtSession().pieBus();
	}

	// on reconfigure le tout par rapport au demandeur
	private void paramPieBus() {
		dtSession().pieBus().setUser(
				new Integer(demande.intForKeyPath("toIndividuConcerne.persId")),
				jefyBus().getFouOrdreForStructure(interventionInfin().stringForKey("cStructure")));
	}

	/**
	 * Bus de données des donnees budgetaires
	 */
	private DTJefyBus jefyBus() {
		return dtSession().dataCenter().jefyBus();
	}

	/**
	 * Bus de données des contacts
	 */
	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	/**
	 * forcer le rafraichissement de tous les fichiers attaches dans GEDFS
	 * 
	 * @param value
	 */
	public void setShouldInitAttachments(boolean value) {
		shouldInitAttachments = value;
	}

	/**
	 * Forcer l'instance du caller, dans le cas de la consultation directe
	 * 
	 * @param value
	 */
	public void setCaller(A_PageUsingListeDemande value) {
		caller = value;
	}

	/** informations de facturation **/
	public String servicePayeur;
	public String ligneBudgetaire;
	public String destination;
	public boolean hasDevis;

	/**
	 * Retourne les infos sur la ligne budgetaire & co.
	 */
	public String libelleLigneBudgetaire() {
		String libelle = null;
		if (interventionInfin().orgId() != null) {
			libelle = dtSession().dataCenter().jefyBus().getLigneBudDescription(null,
					interventionInfin().orgId(),
					interventionInfin().tcdCode(), false, null);
		}
		if (libelle == null)
			libelle = "[Aucune ligne budgétaire]";
		return libelle;
	}

	/**
	 * Le numero affichable du devis associe a la DT en cours.
	 */
	public Integer getPrestNumero() {
		return prestNumero;
	}

	public boolean hasInterventionInfin() {
		boolean hasInterventionInfin = false;

		if (interventionInfin() != null) {
			hasInterventionInfin = true;
		}

		return hasInterventionInfin;
	}

	/**
	 * Le numero du devis associe a la DT en cours.
	 * 
	 * @return
	 */
	public Integer getPrestId() {
		return interventionInfin().prestId();
	}

	public Integer getIndPersId() {
		return indPersId;
	}

	public Integer getStrucPersId() {
		return structPersId;
	}

	public Integer getStrucFouOrdre() {
		return structFouOrdre;
	}

	// ==== PARTIE DEDIEE A LA REPRO ====//

	// Ancienne parge InspecteurRepro

	protected EOInterventionRepro recRepro;
	protected CktlRecord cfcItem;

	private DTReproDataInfoULR reproDataInfo;

	public String couverture() {
		return reproDataInfo().getTypeCouvLibelle(true);
	}

	public String reliure() {
		return null;
	}

	public boolean isLibelle() {
		return (StringCtrl.normalize(recRepro.stringForKey("libelle")).length() > 0);
	}

	public boolean isRemarques() {
		return (StringCtrl.normalize(recRepro.stringForKey("remarques")).length() > 0);
	}

	/**
	 * Retourne la cl� <code>cStructure</code> du service Repro. Elle est deduite
	 * du service destinataire de la DT en cours.
	 */
	protected String cStructureRepro() {
		return demande.stringForKey("cStructure");
	}

	protected DTReproDataInfoULR reproDataInfo() {
		return reproDataInfo;
	}

	protected void setTaskBar(ConsultTaskBar value) {
		taskBar = value;
	}

	/**
	 * Effectuer le parametrage de la barre. Cette methode est appelle par la
	 * barre lors de son instanciation. Si des parametres specifiques ont ete
	 * saisis alors elle s'initialise avec ceux la.
	 */
	protected void doParamTaskBar() {
		if (forceMode == FORCE_AFFECTATION)
			taskBar.toAffecter();
		if (forceMode == FORCE_TRAITEMENT)
			taskBar.toVoirTraitement();
		if (forceMode == FORCE_TRAITEMENT_ADD)
			taskBar.toAjouterTraitement();
		if (forceMode == FORCE_VALIDATION)
			taskBar.toValider();
	}

	private int forceMode;
	public final static int FORCE_AFFECTATION = 1;
	public final static int FORCE_TRAITEMENT = 2;
	public final static int FORCE_TRAITEMENT_ADD = 3;
	public final static int FORCE_VALIDATION = 4;

	/**
	 * Forcer l'affichage de la page dans un mode particulier.
	 */
	protected void forceStatus(int value) {
		forceMode = value;
	}

	/**
	 * Afficher le commentaire interne ? Oui, si la personne connectee est un des
	 * intervenant de la DT, ou bien un affectant. On affiche rien s'il est vide
	 * ...
	 */
	protected boolean showCommentaireInterne() {
		if (isRecInterventionExists() && !StringCtrl.isEmpty(demande.intCommentaireInterne())) {
			NSArray nosAccepted = new NSArray();
			// les intervenants
			nosAccepted = nosAccepted.arrayByAddingObjectsFromArray(
					(NSArray) demande.valueForKeyPath("tosIntervenant.noIndividu"));
			// les affectants
			nosAccepted = nosAccepted.arrayByAddingObjectsFromArray(
					(NSArray) demande.valueForKeyPath("tosIntervenant.intvNoIndAffectant"));
			nosAccepted = NSArrayCtrl.removeDuplicate(nosAccepted);
			return nosAccepted.containsObject(dtUserInfo().noIndividu());
		}
		return false;
	}

	public String commentaireInterne() {
		return StringCtrl.replace(demande.intCommentaireInterne(), "\n", "<br>");
	}

	/**
	 * Indique si la demande de travaux d'entree existe
	 * 
	 * @return
	 */
	private boolean isRecInterventionExists() {
		return demande != null;
	}

	/**
	 * La conf. informatique du demandeur n'est disponible que si la demande est
	 * disponible et que ce n'est pas une DTRepro.
	 * 
	 * XXX pour l'instant, seul les intervenants du CRI y ont accès (en dur :( )
	 * 
	 * @return
	 */
	public boolean isShowLnkShowConfInfo() {
		boolean show = false;

		if (isRecInterventionExists() &&
				dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, "25") &&
				isDTRepro == Boolean.FALSE &&
				dtSession().isGlpiServiceAvailable()) {
			show = true;
		}

		return show;

	}

	/* -- GESTION DU BROWSER DES ACTIVITES -- */

	/**
	 * La classe listener de gestion de la page a afficher SelectActivite.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	public class InspecteurDTActiviteListener extends SelectActiviteListener {

		/**
		 * L'activite de la DT dans l'entite VActivites
		 */
		private CktlRecord recVActivite;

		public void doAfterActiviteSelectedItem() {
		}

		protected CktlRecord recVActivite() {
			if (recVActivite == null)
				recVActivite = session().dataCenter().activiteBus().findVActivite(demande.numberForKey("actOrdre"));
			return recVActivite;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return null;
		}

		public void doAfterSearchActivite() {
		}

		public WOComponent caller() {
			return InspecteurDT.this;
		}

		public boolean shouldSelectedOnlyLeaf() {
			return true;
		}

		public boolean showHiddenActivite() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, recVActivite().stringForKey("cStructure"));
		}

		/**
		 * c'est un intervenant qui accede a cet écran, on affiche les activités
		 * underscore
		 */
		public boolean showUnderscoredActivite() {
			return true;
		}

		@Override
		public boolean showActivitesFavoritesDemandeur() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean showActivitesFavoritesIntervenant() {
			// TODO Auto-generated method stub
			return true;
		}

	}

	public InspecteurDTActiviteListener activiteListener;

	/**
	 * Validation de l'affectation avec une transmission a un autre service.
	 */
	protected WOComponent changerActivite() {
		// On va selectionner l'activite de service destinataire
		SelectActivite pageActivite = (SelectActivite) pageWithName(SelectActivite.class.getName());
		activiteListener = new InspecteurDTActiviteListener();
		pageActivite.setListener(activiteListener);
		return pageActivite;
	}

	// -- methodes de l'interface SelectActiviteConsumer utilise par
	// SelectActivite --

	private Number newActOrdre;
	private String newMotsClef;

	/**
	 * Methode appel�e par le composant SelectActivite un fois avoir valid�. On
	 * modifie l'activite associee a la DT avec la valeur passee en parametre.
	 */
	public void setActiviteSelectedBySelectActivite(CktlRecord value) {
		if (value != null)
			newActOrdre = value.numberForKey("actOrdre");
	}

	public void setMotsClesSelectedBySelectActivite(String value) {
		newMotsClef = value;
	}

	public void saveUpdateActiviteBySelectActivite() {
		interventionBus().updateIntervention(dtUserInfo(), null,
				demande.numberForKey("intOrdre"), null, null, null, null, newMotsClef, newActOrdre, null, null, null);
	}

	// conf informatique

	/**
	 * Afficher la conf informatique du demandeur
	 */
	public PageConfInfo afficherConfInfo() {
		PageConfInfo pageConfInfo = (PageConfInfo) pageWithName(PageConfInfo.class.getName());
		pageConfInfo.setEoIndividu(demande.toIndividuConcerne());
		return pageConfInfo;
	}

	// Repro CFC

	private CompCFCListener _cfcListener;

	public CompCFCListener cfcListener() {
		if (_cfcListener == null)
			_cfcListener = new CompCFCListener(interventionBus(), this, demande);
		return _cfcListener;
	}

	public WOComponent modifierCFC() {
		CompCFC page = (CompCFC) pageWithName(CompCFC.class.getName());
		page.setListener(cfcListener());
		return page;
	}

	/**
	 * La liste des CFC directement depuis la base de donn�es
	 * 
	 * @return
	 */
	public NSArray cfcList() {
		return interventionBus().findInterventionsReproCfc(null, demande.numberForKey("intOrdre"));
	}

	// modification du motif

	/**
   * 
   */
	public WOActionResults toModification() {
		isMotifEnCoursDeModification = true;
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public WOActionResults annuler() {
		isMotifEnCoursDeModification = false;
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public WOActionResults enregistrerMotif() {
		Integer transId = interventionBus().beginECTransaction();

		errMessage = "";

		if (dtSession().dtDataBus().checkForMaxSize(
				EOIntervention.ENTITY_NAME, EOIntervention.INT_MOTIF_KEY, motif, "Motif", 0, true, false) == false) {
			errMessage = dtSession().dtDataBus().getErrorMessage();
			return null;
		}

		if (StringCtrl.isEmpty(errMessage)) {
			if (interventionBus().updateIntervention(
					dtUserInfo(), null, demande.intOrdre(),
					motif, null, null,
					null, null, null,
					null, null, null)) {
				if (interventionBus().commitECTransaction(transId)) {
					motifAAfficher = StringCtrl.replace(WOMessage.stringByEscapingHTMLAttributeValue(
							motif), "&#10;", "<br/>");
					isMotifEnCoursDeModification = false;
				}
			} else {
				interventionBus().rollbackECTrancsacition(transId);
				errMessage = dtSession().dtDataBus().getErrorMessage();
			}
		}

		return null;
	}

	// gestion des messages d'erreur

	private String errMessage = "";

	/**
	 * Script dynamique pour la gestion de l'affichage des messages d'erreur
	 * 
	 * @return
	 */
	public String errMessageScript() {
		String errMessageScript = "";

		if (!StringCtrl.isEmpty(errMessage)) {
			errMessageScript = "alert('" + errMessage + "');";
		}

		return errMessageScript;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAfficherErreur() {
		boolean isAfficherErreur = false;

		if (!StringCtrl.isEmpty(errMessage)) {
			isAfficherErreur = true;
		}

		return isAfficherErreur;
	}

	// changement du demandeur

	public WOComponent changeDemandeur() {
		// [LRAppTasks] : @CktlLog.trace(@"personneListener : "+personneListener());
		return SelectPersonne.getNewPage(personneListener(),
							"Indiquez la personne pour le compte de laquelle la demande est faite",
							demande.intNoIndConcerne(), true);
	}

	private SelectPersonneListener personneListener;

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection des personnes.
	 */
	private SelectPersonneListener personneListener() {
		if (personneListener == null)
			personneListener = new ChangeDemandeur();
		return personneListener;
	}

	/**
	 * Classe de gestion du changement du demandeur
	 */
	private class ChangeDemandeur implements SelectPersonneListener {
		/*
		 * @see PersonneSelectListener#select(java.lang.Number, java.lang.Number)
		 */
		public WOComponent select(Number persId) {
			CktlUserInfo demandeurInfo = new CktlUserInfoDB(dtSession().dataBus());
			demandeurInfo.compteForPersId(persId, true);

			// mettre à jour le demandeur et le contact en prenant celui par defaut
			EOContact eoContact = (EOContact) contactsBus().findDefaultContact(
					demandeurInfo.noIndividu(), true);

			Integer transId = interventionBus().beginECTransaction();

			if (interventionBus().updateIntervention(
					dtUserInfo(), null, demande.intOrdre(),
					null, null, null,
					null, null, null,
					eoContact.ctOrdre(), null, demandeurInfo.noIndividu())) {
				if (interventionBus().commitECTransaction(transId)) {
					// maj de la photo
					getPhotoForDT(demande.intNoIndConcerne().intValue());
				}
			} else {
				interventionBus().rollbackECTrancsacition(transId);
			}

			return InspecteurDT.this;
		}

		/*
		 * @see PersonneSelectListener#cancel()
		 */
		public WOComponent cancel() {
			return InspecteurDT.this;
		}

		/*
		 * @see PersonneSelectListener#context()
		 */
		public WOContext getContext() {
			return context();
		}
	}

	/**
	 * On autorise pas le changement des motifs de l'historique
	 * 
	 * @return
	 */
	public final Boolean getCanChangerMotif() {
		return canChangerMotif && eoHistoriqueMotifSelected == null;
	}

	/**
	 * Reformater l'affichage du motif pour un historique
	 * 
	 * @return
	 */
	public String getMotifHistorique() {
		String motifHistorique = null;

		motifHistorique = StringCtrl.replace(WOMessage.stringByEscapingHTMLAttributeValue(
				eoHistoriqueMotifSelected.intMotif()), "&#10;", "<br/>");

		return motifHistorique;
	}
}
