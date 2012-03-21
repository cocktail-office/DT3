import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOBatiment;
import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EODiscussion;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervenant;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOInterventionInfin;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXQ;

/*
 * Copyright CRI - Universite de La Rochelle, 1995-2006 
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

/**
 * Tableau liste de demandes de travaux.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class CompListeDemande
		extends DTWebComponent {

	/**
	 * Le controleur de ce composant
	 */
	private I_CompListeDemandeListener listener;

	/**
	 * dans le cas ou pas de couleur de preference pour l'item en cours alors la
	 * classe CSS a utiliser pour definir son style
	 */
	private final static String TR_INTERVENTION_DEFAULT_CLASS = "listboxLine";

	/**
	 * Dispos des colonnes. Le composant qui appele cette liste doit definir
	 * quelles sont les fonctionalites a afficher a l'utilisateur
	 */
	public boolean showColumnIntervenants, showColumnValider, showColumnAffecter,
			showColumnTraiter, showColumnAjoutTraitement;

	/** gestion des filtres sur le demandeur */
	public EOIndividu demandeurItem, demandeurSelected;

	/** gestion des filtres sur l'activite */
	public EOActivites activiteItem, activiteSelected;

	/** gestion des filtres sur les intervenants */
	public CktlRecord intervenantItem, intervenantSelected;

	public CompListeDemande(WOContext context) {
		super(context);
	}

	public NSArray<Integer> nbList = new NSArray<Integer>(
			new Integer[] { new Integer(10), new Integer(20), new Integer(30) });
	public Integer nbItem;

	/**
	 * Force le rafraichissement du contenu des données en se resynchronisant avec
	 * la base de données
	 * 
	 * @return
	 */
	public WOComponent doForceRefreshAll() {

		NSArray<EOIntervention> eoInterventionArray = getListener().interventionDisplayGroup().allObjects();
		for (EOIntervention eoIntervention : eoInterventionArray) {
			getListener().interventionDisplayGroup().dataSource().editingContext().invalidateObjectsWithGlobalIDs(
					new NSArray<EOGlobalID>(
							getListener().interventionDisplayGroup().dataSource().editingContext().globalIDForObject(eoIntervention)));
		}

		getListener().doFetchDisplayGroup();

		return null;
	}

	/**
	 * Afficher l'inspecteur dans son ecran de lecture de tous les traitements
	 */
	public InspecteurDT afficherInspecteurTraiter() {
		InspecteurDT inspecteurDT = afficherInspecteur();
		if (inspecteurDT != null)
			inspecteurDT.forceStatus(InspecteurDT.FORCE_TRAITEMENT);
		return inspecteurDT;
	}

	/**
	 * Afficher l'inspecteur dans son ecran d'ajout de traitement
	 */
	public InspecteurDT afficherInspecteurAjouterTraitement() {
		InspecteurDT inspecteurDT = afficherInspecteur();
		if (inspecteurDT != null)
			inspecteurDT.forceStatus(InspecteurDT.FORCE_TRAITEMENT_ADD);
		return inspecteurDT;
	}

	/**
	 * Afficher l'inspecteur dans son ecran d'affectation
	 */
	public InspecteurDT afficherInspecteurAffecter() {
		InspecteurDT inspecteurDT = afficherInspecteur();
		if (inspecteurDT != null)
			inspecteurDT.forceStatus(InspecteurDT.FORCE_AFFECTATION);
		return inspecteurDT;
	}

	/**
	 * Afficher l'inspecteur dans son ecran de validation
	 */
	public InspecteurDT afficherInspecteurValider() {
		InspecteurDT inspecteurDT = afficherInspecteur();
		if (inspecteurDT != null)
			inspecteurDT.forceStatus(InspecteurDT.FORCE_VALIDATION);
		return inspecteurDT;
	}

	/**
	 * L'infobulle de detail de la DT. En caracteres plus gros, avec le text
	 * integral de la DT
	 */
	public String toolTipHtmlMotif() {
		return DTStringCtrl.getHtmlToolTipBox("D&eacute;tails DT #" + currentIntervention.intCleService(),
				(String) currentIntervention.intMotif());
	}

	/**
	 * L'infobulle affichant le contact complet du demandeur
	 */
	public String toolTipHtmlContact() {
		return DTStringCtrl.getHtmlToolTipBox(nomDemandeurLong(),
				dtDataCenter().contactsBus().getInterventionContactDescription(
						currentIntervention.intOrdre(),
						currentIntervention.ctOrdre(), "\n"));
	}

	/**
	 * L'infobulle sur l'activite (chemin complet dans les mots clefs). On
	 * reformate en quelque chose de plus lisible
	 */
	public String toolTipHtmlActivite() {
		String pathActivite = activiteBus().findActivitePathString(currentIntervention.actOrdre(), true, ";");
		// oter le dernier ";"
		if (pathActivite.endsWith(";")) {
			pathActivite = pathActivite.substring(0, pathActivite.length() - 1);
		}
		// aerer les mots clefs
		pathActivite = StringCtrl.replace(pathActivite, ";", " > ");
		pathActivite = WOMessage.stringByEscapingHTMLAttributeValue(pathActivite);
		return DTStringCtrl.getHtmlToolTipBox(
				"Arborescence activit&eacute; DT #" + currentIntervention.intCleService().intValue(), pathActivite);
	}

	/**
	 * L'infobulle sur la date de creation de la DT
	 */
	public String toolTipHtmlDate() {
		return DTStringCtrl.getHtmlToolTipBox(
				"Date cr&eacute;ation DT #" + currentIntervention.intCleService(),
				DateCtrl.dateToString(currentIntervention.intDateCreation(), "%d/%m/%Y %H:%M"));
	}

	/**
   * 
   */
	public boolean showWarnMessage() {
		return !StringCtrl.isEmpty(getListener().warnMessage());
	}

	/**
	 * L'infobulle de detail des affectes sur la DT. Elle contient la liste des
	 * intervenants, avec pour chacun d'entre eux tous les traitements saisis.
	 */
	public String toolTipHtmlAffectes() {
		// on formate le motif en html pur
		StringBuffer htmlAffectes = new StringBuffer();
		NSArray<EOIntervenant> recsIntervenant = currentIntervention.tosIntervenant(null, true);
		for (int i = 0; i < recsIntervenant.count(); i++) {
			EOIndividu recIndividu = recsIntervenant.objectAtIndex(i).toIndividuUlr();
			htmlAffectes.append("<b>");
			htmlAffectes.append(StringCtrl.formatName(recIndividu.prenom(), recIndividu.nomUsuel()));
			htmlAffectes.append(" : </b> ");
			// retrouver ses traitements pour cette DT
			NSArray<EOTraitement> traitements = traitementsListe(currentIntervention, recIndividu);
			if (traitements.count() > 0) {
				for (int j = 0; j < traitements.count(); j++) {
					CktlRecord recTraitement = (CktlRecord) traitements.objectAtIndex(j);
					// on s'assure que l'individu a le droit de consulter ce trait
					String textAffectes = DTInterventionBus.HiddenCommentMessage;
					if (interventionBus().canReadTraitement(
							recTraitement, dtUserInfo().noIndividu(), currentIntervention.stringForKey("cStructure"))) {

						String strTraitement = interventionBus().traitementContentDisplay(recTraitement);
						// 150 caracteres par traitement, sans saut de ligne
						textAffectes = StringCtrl.compactString(
								StringCtrl.normalize(strTraitement), 150, "<...>");
						// on met en rouge pour lui indiquer que le traitement est masque
						if (recTraitement.stringForKey("traConsultable").equals("N")) {
							textAffectes = "<font color=\"red\">" + textAffectes + "</font>";
						}
					}
					htmlAffectes.append("\n\n&nbsp;&nbsp;&nbsp;<i>Traitement #");

					// la numérotation est directement liée au classement chronologique
					// des traitements
					if (dtUserInfo().isSortTraAscending()) {
						htmlAffectes.append(j + 1);
					} else {
						htmlAffectes.append(traitements.count() - j);
					}

					htmlAffectes.append(" (").append(interventionBus().traitementDateDisplay(recTraitement)).append(")</i> : \n");
					htmlAffectes.append(textAffectes);
				}
			} else
				htmlAffectes.append("--");
			htmlAffectes.append("\n\n");
		}

		return DTStringCtrl.getHtmlToolTipBox("Intervenants DT #" + currentIntervention.intForKey("intCleService"), htmlAffectes.toString());
	}

	/**
	 * liste des traitements d'une intervention par un individu
	 * 
	 * @param intervenant
	 * @return
	 */
	private NSArray<EOTraitement> traitementsListe(CktlRecord recIntervention, EOGenericRecord intervenant) {
		if (intervenant != null) {
			EOQualifier condition = DTDataBus.newCondition(
					"intOrdre=" + recIntervention.valueForKey("intOrdre") + " AND toIndividuUlr=%@",
					new NSArray(intervenant)
					);
			// classer selon les préférences utilisateur
			NSArray sort = interventionBus().getTraitementsSort(EOTraitement.TRA_DATE_DEB_KEY);
			return dtSession().dataBus().fetchArray("Traitement", condition, sort);
		} else
			return new NSArray();
	}

	/**
	 * L'infobulle de detail des messages de discussions sur la DT. Elle contient
	 * la liste des "discuteurs", avec pour chacun d'entre eux, tous les messages
	 * saisis.
	 */
	public String toolTipHtmlDiscussions() {
		// on formate le motif en html pur
		StringBuffer htmlDiscussions = new StringBuffer();
		NSArray<EODiscussion> recsDiscussion = NSArrayCtrl.flattenArray(currentIntervention.tosDiscussion());
		if (recsDiscussion.count() > 0) {
			for (int i = 0; i < recsDiscussion.count(); i++) {
				EODiscussion recDiscussion = recsDiscussion.objectAtIndex(i);
				htmlDiscussions.append("<b>");
				htmlDiscussions.append(DateCtrl.dateToString(recDiscussion.disDate(), "%d/%m/%Y %H:%M"));
				htmlDiscussions.append(" (");
				htmlDiscussions.append(StringCtrl.formatName(
						recDiscussion.toIndividuUlr().prenom(), recDiscussion.toIndividuUlr().nomUsuel()));
				htmlDiscussions.append(")</b><br/>");
				// on s'assure que l'individu a le droit de consulter ce message
				// TODO
				/*
				 * String textAffectes = DTInterventionBus.HiddenCommentMessage; if
				 * (interventionBus().canReadTraitement( recDiscussion,
				 * dtUserInfo().noIndividu(),
				 * currentIntervention.stringForKey("cStructure"))) { // 150 caracteres
				 * par traitement, sans saut de ligne textAffectes =
				 * StringCtrl.compactString(
				 * StringCtrl.normalize(recDiscussion.stringForKey("traTraitement")),
				 * 150, "<...>"); // on met en rouge pour lui indiquer que le traitement
				 * est masque if
				 * (recDiscussion.stringForKey("traConsultable").equals("N")) {
				 * textAffectes = "<font color=\"red\">" + textAffectes + "</font>"; } }
				 */
				String textDiscussion = StringCtrl.compactString(StringCtrl.normalize(recDiscussion.disMessage()), 150, "<...>");
				htmlDiscussions.append(textDiscussion);
				htmlDiscussions.append("<br/><br/>");
			}
		} else {
			htmlDiscussions.append("--");
		}

		return DTStringCtrl.getHtmlToolTipBox(
				"Messages DT #" + currentIntervention.intCleService(), htmlDiscussions.toString());
	}

	/**
	 * liste des messages de discussion autour d'une intervention par un individu
	 * 
	 * @param intervenant
	 * @return
	 */
	private NSArray discussionsListe(CktlRecord recIntervention, EOGenericRecord intervenant) {
		if (intervenant != null) {
			EOQualifier condition = DTDataBus.newCondition(
					EODiscussion.INT_ORDRE_KEY + "=" + recIntervention.valueForKey("intOrdre") + " AND " + EODiscussion.TO_INDIVIDU_ULR_KEY + "=%@",
					new NSArray(intervenant)
					);
			NSArray sort = CktlSort.newSort(EODiscussion.NOM_INTERVENANT_KEY).add(EODiscussion.DIS_DATE_KEY);
			return dtSession().dataBus().fetchArray(EODiscussion.ENTITY_NAME, condition, sort);
		} else
			return new NSArray();
	}

	public String nomService() {
		return dtSession().dataCenter().serviceBus().libelleForServiceCode(
				currentIntervention.stringForKey("cStructure"),
				false,
				false);
	}

	public String intervenantItemCourt() {
		return nomDemandeurCourt(intervenantItem);
	}

	/**
	 * Nom d'un demandeur d'une DT
	 * 
	 * @param recIndividu
	 * @return
	 */
	private String nomDemandeurCourt(CktlRecord recIndividu) {
		if (recIndividu != null) {
			return StringCtrl.formatName(recIndividu.stringForKey("prenom"), recIndividu.stringForKey("nomUsuel"));
		} else
			return StringCtrl.emptyString();
	}

	private String nomDemandeurLong() {
		if (currentIntervention != null) {
			return (String) currentIntervention.valueForKeyPath("toIndividuConcerne.nomEtPrenom");
		} else
			return StringCtrl.emptyString();
	}

	/**
	 * Le batiment ou realiser l'intervention Pour raison historique, cette info
	 * etait dans l'entite intervention ... or c'est dans le contact qu'il faut
	 * regarder ! On fait donc dans l'ordre : 1- le batiment dans la DT 2- celui
	 * dans le contact
	 */
	public String batimentLibelle() {
		String libelle = "&nbsp;";

		EOBatiment recBatiment = contactsBus().findBatimentForCode(currentIntervention.cLocal());

		if (recBatiment == null) {
			// on cherche dans le contact
			Number ctOrdre = currentIntervention.ctOrdre();
			if (ctOrdre != null) {
				EOContact recContact = contactsBus().findContact(ctOrdre);
				if (recContact != null) {
					recBatiment = contactsBus().findBatimentForCode(recContact.cLocal());
				}
			}
		}

		if (recBatiment != null) {
			libelle = recBatiment.stringNormalizedForKey("cLocal");
		}

		return libelle;
	}

	/**
	 * Le libelle long de l'etat de la demande visible par le passage de la souris
	 */
	public String toolTipHtmlEtat() {
		return "<center>" + DTStringCtrl.getHTMLString(
				"Cette demande fait partie des DT " +
						dtSession().dataCenter().etatBus().libelleForEtat(
								currentIntervention.stringForKey("intEtat"))) + "</center>";
	}

	/**
	 * Nombre de personnes affectees sur la DT ains que le nombre total de
	 * traitements (en gras s'il y en lui meme en a saisi au moins 1)
	 */
	public String nbAffectes() {
		String nbAffectes = "--";
		// nombre d'affectes
		if (currentIntervention != null) {
			NSArray recsIntervenant = currentIntervention.arrayForKey("tosIntervenant");
			if (recsIntervenant.count() > 0) {
				nbAffectes = String.valueOf(recsIntervenant.count());
				// nombre de traitements
				int nbTraitement = 0;
				for (int i = 0; i < recsIntervenant.count(); i++) {
					CktlRecord recIntervenant = (CktlRecord) recsIntervenant.objectAtIndex(i);
					NSArray recsTraitement = interventionBus().findTraitements(null, null,
							currentIntervention.numberForKey("intOrdre"), recIntervenant.numberForKey("noIndividu"));
					nbTraitement += recsTraitement.count();
				}
				// on met en gras le nombre de traitements saisis, si l'utilisateur
				// connecte en a saisi au moins 1
				boolean hasSaisiTraitement =
						(traitementsListe(currentIntervention,
								individuBus().individuForNoIndividu(dtUserInfo().noIndividu())).count() > 0);
				if (hasSaisiTraitement)
					nbAffectes += "<b>";
				nbAffectes += " (" + nbTraitement + ")";
				if (hasSaisiTraitement)
					nbAffectes += "</b>";
			}
		}
		return nbAffectes;
	}

	/**
	 * Nombre de messages de discussions sur la DT (et ses traitements) (en gras
	 * s'il y en lui meme en a saisi au moins 1)
	 */
	public String nbDiscussions() {
		String nbDiscussions = "--";
		// nombre de messages de discussion
		if (currentIntervention != null) {
			NSArray recsDiscussion = currentIntervention.tosDiscussion();
			if (recsDiscussion.count() > 0) {
				// nombre de traitements
				nbDiscussions = String.valueOf(recsDiscussion.count());
				// on met en gras le nombre de traitements saisis, si l'utilisateur
				// connecte en a saisi au moins 1
				NSArray discussionsSelf = discussionsListe(currentIntervention, individuBus().individuForNoIndividu(dtUserInfo().noIndividu()));
				boolean hasSaisiDiscussion = discussionsSelf.count() > 0;
				if (hasSaisiDiscussion) {
					nbDiscussions += "<b>";
				}
				nbDiscussions += " (" + discussionsSelf.count() + ")";
				if (hasSaisiDiscussion) {
					nbDiscussions += "</b>";
				}
			}
		}
		return nbDiscussions;
	}

	/**
	 * Afficher l'inspecteur
	 */
	public InspecteurDT afficherInspecteur() {
		if (currentIntervention != null) {
			selectedIntervention = currentIntervention;
			InspecteurDT inspecteur = (InspecteurDT) pageWithName("InspecteurDT");
			inspecteur.afficherInspecteur(getListener().caller(), currentIntervention);
			return inspecteur;
		} else {
			return null;
		}
	}

	// ** les variables pour la DT en cours **
	public EOIntervention currentIntervention;
	public EOIntervention selectedIntervention; // l'intervention pour laquelle on
	// affiche l'inspecteur
	public boolean canValider, canSupprimer, canAffecter, canTraiter, canAjouterTraitement, canChangerEtat;
	public String interventionItemBgColor, interventionItemClass, interventionItemStyle;
	public boolean hasTraitements;

	/**
	 * Indique si l'intervention en cours est celle dont on a affiche
	 * l'inspecteur. Si c'est le cas, on va mettre une ancre sur la ligne pour se
	 * positionner dessus au prochain affichage de la page.
	 */
	public boolean isTheSelectedIntervention() {
		return currentIntervention != null && selectedIntervention != null &&
				currentIntervention.intOrdre().intValue() == selectedIntervention.intOrdre().intValue();
	}

	private void resetVariables() {
		canValider = canSupprimer = canAffecter = canTraiter = canAjouterTraitement = canChangerEtat = false;
		hasTraitements = false;
	}

	/**
	 * Override du setter pour faire avancer le displayGroup et recuperer au
	 * prealable toutes les donnees necessaires.
	 */
	public void setCurrentIntervention(EOIntervention value) {
		currentIntervention = value;
		resetVariables();
		// selection de l'objet dans le display group
		if (value != null) {

			// interventionDisplayGroup().selectObject(value);

			// mise a jour des variable pour cette DT
			canValider = interventionBus().canValider(
					currentIntervention,
					dtSession().dtUserInfo().noIndividu());

			canSupprimer = interventionBus().canSupprimer(
					currentIntervention,
					dtSession().dtUserInfo().noIndividu(),
					getListener().modeUtilisateur());

			canAffecter = interventionBus().canAffecter(
					currentIntervention.intEtat(),
					currentIntervention.actOrdre(),
					currentIntervention.cStructure(),
					dtSession().dtUserInfo().noIndividu());

			canTraiter = interventionBus().canTraiter(
					currentIntervention,
					dtSession().dtUserInfo().noIndividu());

			canAjouterTraitement = interventionBus().canAjouterTraitement(
					currentIntervention,
					dtSession().dtUserInfo().noIndividu());

			canChangerEtat = interventionBus().canChangeEtat(
					currentIntervention,
					dtUserInfo().noIndividu());

			hasTraitements = false;
			if (currentIntervention.tosTraitement().count() > 0) {
				hasTraitements = true;
			}

			// couleur fond de la cellule HTML de l'intervention en cours
			// correspond aux preferences de l'utilisateur par rapport au batiment.
			if (dtUserInfo().useCoulBat()) {
				interventionItemBgColor = preferencesBus().getHtmlColorForCode(currentIntervention.cLocal());
			} else {
				interventionItemBgColor = preferencesBus().getHtmlColorForCode(currentIntervention.actOrdre());
			}

			// style CSS de la cellule HTML de l'intervention en cours
			// si pas de couleur, alors on passe la classe par defaut, sinon vide
			if (StringCtrl.isEmpty(interventionItemBgColor)) {
				interventionItemClass = TR_INTERVENTION_DEFAULT_CLASS;
			} else {
				interventionItemClass = "";
			}

			// si de plus la dt est masquee, alors la police d'ecriture est grisee.
			if (isInterventionMasquee()) {
				interventionItemStyle = "color: grey";
			} else {
				interventionItemStyle = "";
			}

		} else
			getListener().interventionDisplayGroup().clearSelection();
	}

	/**
	 * Le commentaire qui s'affiche en haut du tableau
	 */
	public String resultCountComment() {
		switch (getListener().interventionDisplayGroup().allObjects().count()) {
		case 0:
			return StringCtrl.emptyString();
		case 1:
			return " 1 demande trouv&eacute;e";
		default:
			return getListener().interventionDisplayGroup().allObjects().count() + " demandes trouv&eacute;es";
		}
	}

	// suppression de DT

	/**
	 * Supprimer une DT.
	 */
	public WOComponent supprimerDt() {
		if (currentIntervention.tosTraitement().count() > 0) {
			return CktlAlertPage.newAlertPageWithCaller(
					getListener().caller(), "Erreur de traitement",
					"Il existe des traitements réalisés ou prévus pour cette demande !<br>" +
							"La demande ne peut pas être supprimée.",
					"Retour", CktlAlertPage.ERROR);
		}
		return askConfirmSuppDT();
	}

	/**
	 * Devalide le devis associe avec la demande.
	 */
	private boolean supprimerDevis(Integer prestId) {
		if (prestId != null) {
			Boolean etat = pieBus().deleteDevisSansControle(prestId);
			return !pieBus().hasError() && etat.booleanValue();
		}
		return true;
	}

	/**
   *
   */
	private boolean supprimerFichiersDT(CktlRecord rec) {
		NSArray objects = interventionBus().findDocuments(rec.numberForKey("intOrdre"), null, true);
		boolean error = false;
		for (int i = 0; i < objects.count(); i++) {
			if (!dtApp().gedBus().deleteDocumentGED(
					CktlRecord.recordNumberForKey(objects.objectAtIndex(i), "docOrdre").intValue()))
				error = true;
		}
		return !error;
	}

	// les diveres pages qui s'enchaine pour la suppression*

	/**
	 * Le pattern des respondeur utilises sur cette page
	 */
	private class AskConfirmResponder implements CktlAlertResponder {
		protected WOComponent caller;

		public AskConfirmResponder(WOComponent aCaller) {
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			return null;
		}
	}

	/**
	 * Ecran de Demande de suppression de DT
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class AskConfirmSuppDTResponder extends AskConfirmResponder {
		public AskConfirmSuppDTResponder(WOComponent aCaller) {
			super(aCaller);
		}

		public WOComponent respondToButton(int buttonNo) {
			// supprimer la DT
			if (buttonNo == 1 || buttonNo == 2) {
				EOInterventionInfin recInfin = interventionBus().findInterventionInfin(null, currentIntervention.intOrdre());
				if (recInfin != null &&
						recInfin.prestNumero() != null) {
					return askConfirmSuppPresta();
				} else {
					// Si aucune prestation, on autorise la suppression
					return doSupprimeDT(caller, true, buttonNo == 1);
				}
			}
			return caller;
		}
	}

	private WOComponent askConfirmSuppDT() {
		// Construction d'un message d'avertissement
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Voulez-vous vraiment supprimer la demande ?");
		return CktlAlertPage.newAlertPageWithResponder(getListener().caller(), "Suppression de DT",
				sb.toString(),
				"Oui", "Oui (ne pas informer le demandeur)", "Non", CktlAlertPage.INFO,
				new AskConfirmSuppDTResponder(getListener().caller()));
	}

	/**
	 * Ecran de Demande de suppression de Prestation
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class AskConfirmSuppPrestaResponder extends AskConfirmResponder {
		public AskConfirmSuppPrestaResponder(WOComponent aCaller) {
			super(aCaller);
		}

		public WOComponent respondToButton(int buttonNo) {
			// supprimer que la DT
			if (buttonNo == 1) {
				return doSupprimeDT(caller, true, true);
			}
			// supprimer la DT et la prestation
			else if (buttonNo == 2) {
				CktlRecord recInfin = interventionBus().findInterventionInfin(null, currentIntervention.intOrdre());
				Integer prestId = (recInfin != null ? new Integer(recInfin.intForKey("prestId")) : null);
				if (prestId != null) {
					// La prestation existe mais elle n'est pas encore realisee
					if (pieBus().prestaRealisee(prestId) == Boolean.FALSE) {
						// on essaye de devalider
						if (supprimerDevis(prestId)) {
							// si ca marche, on vire la DT
							return doSupprimeDT(caller, true, true);
						} else {
							// erreur de suppression
							return CktlAlertPage.newAlertPageWithCaller(
									caller, "Erreur de traitement",
									"La devis associé n'a pas pu être dévalidé !<br>" +
											"Le numéro de devis " + prestId.intValue(),
									"Retour", CktlAlertPage.ERROR);
						}
					} else {
						// Il existe une prestation, mais elle EST realisee
						return CktlAlertPage.newAlertPageWithCaller(
								caller, "Erreur de traitement",
								"Cette demande a une prestation associée qui est déjà réalisée !" +
										"<br><br>La demande ne peux pas être supprimée.",
								"Retour", CktlAlertPage.ERROR);
					}
				}
			}
			return caller;
		}
	}

	private WOComponent askConfirmSuppPresta() {
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Cette demande a une prestation associée.<br><br>").
				append("Voulez-vous supprimer la demande uniquement,<br>").
				append("supprimer la demande et dévalider la prestation ou<br>").
				append("annuler la suppression?");
		return CktlAlertPage.newAlertPageWithResponder(getListener().caller(), "Suppression de DT",
				sb.toString(),
				"Supprimer uniquement la DT", "Supprimer la DT et le devis", "Annuler", CktlAlertPage.INFO,
				new AskConfirmSuppPrestaResponder(getListener().caller()));
	}

	/**
	 * Ecran de Demande de confirmation suppression de DT suite a une erreur
	 * survenue lors de la suppression des fichiers attaches
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class AskConfirmSuppDTErrorFileResponder extends AskConfirmResponder {
		private boolean informDemandeur;

		public AskConfirmSuppDTErrorFileResponder(WOComponent aCaller, boolean shouldInformDemandeur) {
			super(aCaller);
			informDemandeur = shouldInformDemandeur;
		}

		public WOComponent respondToButton(int buttonNo) {
			// supprimer la DT malgre l'erreur de suppresion
			if (buttonNo == 1) {
				return doSupprimeDT(caller, false, informDemandeur);
			}
			return caller;
		}
	}

	private WOComponent askConfirmSuppDTErrorFile(boolean informDemandeur) {
		// Construction d'un message d'avertissement
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Certains documents attachés ne peuvent pas être supprimés correctement<br>" +
						"Voulez vous continuer la suppression de la demande ?");
		return CktlAlertPage.newAlertPageWithResponder(getListener().caller(), "Suppression de DT",
				sb.toString(), "Supprimer", "Annuler", null, CktlAlertPage.INFO,
				new AskConfirmSuppDTErrorFileResponder(getListener().caller(), informDemandeur));
	}

	/**
	 * L'action effective de suppression de la DT
	 * 
	 * @param deleteFiles
	 *          : faut-il faire la suppresion des fichiers attaches ?
	 * @param informConcerne
	 *          : faut-il envoyer le mail de suppression de la DT ?
	 */
	private WOComponent doSupprimeDT(WOComponent caller, boolean deleteFiles, boolean informConcerne) {

		if (deleteFiles) {
			// Supprimer les fichier attaches, s'ils existent
			if (!supprimerFichiersDT(currentIntervention)) {
				return askConfirmSuppDTErrorFile(informConcerne);
			}
		}

		// Suppression du panier
		doSupprimerPanierCurrentIntervention();

		// Suppression de la DT
		boolean isSuppressionOk = false;
		String errMessageSuppression = null;
		try {
			isSuppressionOk = interventionBus().deleteIntervention(null, currentIntervention.intOrdre());
		} catch (Throwable e) {
			errMessageSuppression = e.getMessage();
		}

		if (isSuppressionOk == false) {
			String errMessage = "La demande n'a pas pu être supprimée !";
			if (!StringCtrl.isEmpty(errMessageSuppression)) {
				errMessage += ":\n\n" + errMessageSuppression;
			}
			return CktlAlertPage.newAlertPageWithCaller(
					caller, "Erreur de suppression", errMessage, "Retour", CktlAlertPage.ERROR);
		}

		NSArray intFiles = dtSession().dtDocumentCenter().getDocumentsURL(currentIntervention.tosDocumentDt());
		NSArray noIntervenants = currentIntervention.arrayForKeyPath("tosIntervenant.noIndividu");
		dtSession().mailCenter().reset();
		dtSession().mailCenter().setIntervention(currentIntervention, false);
		dtSession().mailCenter().setInterventionFiles(intFiles);
		dtSession().mailCenter().setNoIntervenants(noIntervenants);
		dtSession().mailCenter().mailSupprimerDT(informConcerne);

		getListener().caller().setShouldRefresh(true);

		return caller;
	}

	// masquer les DT

	/**
	 * Action de masquer la demande <code>currentIntervention</code> pour
	 * l'individu connecte
	 * 
	 * @throws Exception
	 */
	public WOComponent masquerIntervention() throws Exception {
		currentIntervention.masquer(dtUserInfo().noIndividu());
		sauvegarder(currentIntervention.editingContext());
		return null;
	}

	/**
	 * Action de "demasquer" la demande <code>currentIntervention</code> pour
	 * l'individu connecte
	 * 
	 * @throws Exception
	 */
	public WOComponent deMasquerIntervention() throws Exception {
		currentIntervention.demasquer(dtUserInfo().noIndividu());
		sauvegarder(currentIntervention.editingContext());
		return null;
	}

	/**
	 * Indique si la demande <code>currentIntervention</code> a ete masquee pour
	 * l'individu connecte.
	 */
	public boolean isInterventionMasquee() {
		boolean isInterventionMasquee = false;

		if (currentIntervention.isInterventionMasqueeForNoIndividu(
				dtUserInfo().noIndividu())) {
			isInterventionMasquee = true;
		}

		return isInterventionMasquee;
	}

	/**
	 * Indique s'il faut afficher la colonne contenant les boutons de masquage /
	 * demasquage
	 */
	public boolean showColumnInterventionMasquee() {
		return getListener().useInterventionMasquee() && getListener().showInterventionMasquee();
	}

	// classer les DT

	/**
	 * Classement par numero ascendant
	 */
	public WOComponent classeNoAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_NUMERO;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par numero descendant
	 */
	public WOComponent classeNoDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_NUMERO;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par date ascendant
	 */
	public WOComponent classeDateAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_DATE;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par date descendant
	 */
	public WOComponent classeDateDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_DATE;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par demandeur ascendant
	 */
	public WOComponent classeNomAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_DEMANDEUR;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par demandeur descendant
	 */
	public WOComponent classeNomDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_DEMANDEUR;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par batiment ascendant
	 */
	public WOComponent classeBatAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_BATIMENT;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par batiment descendant
	 */
	public WOComponent classeBatDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_BATIMENT;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par activite ascendant
	 */
	public WOComponent classeActAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_ACTIVITE;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par activite descendant
	 */
	public WOComponent classeActDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_ACTIVITE;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par service dest ascendant
	 */
	public WOComponent classeServiceAsc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_SERVICE;
		getListener().caller().setOrdreSelected(CktlSort.Ascending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	/**
	 * Classement par service dest descendant
	 */
	public WOComponent classeServiceDesc() {
		getListener().caller().triSelected = EOIntervention.TRI_ATTRIBUT_SERVICE;
		getListener().caller().setOrdreSelected(CktlSort.Descending);
		getListener().doFetchDisplayGroup();
		return null;
	}

	// filtres sur les colonnes

	/**
	 * La liste de tous les demandeurs associés aux DT du
	 * {@link I_CompListeDemandeListener#interventionDisplayGroup()}
	 */
	public NSArray demandeurList() {
		NSArray demandeurList = (NSArray) getListener().interventionDisplayGroup().allObjects().valueForKey("toIndividuConcerne");
		demandeurList = NSArrayCtrl.removeDuplicate(demandeurList);
		demandeurList = EOSortOrdering.sortedArrayUsingKeyOrderArray(demandeurList, CktlSort.newSort("nomEtPrenom"));
		return demandeurList;
	}

	/**
	 * La liste de tous les activites associées aux DT du
	 * {@link I_CompListeDemandeListener#interventionDisplayGroup()}
	 */
	public NSArray<EOActivites> activiteList() {
		NSArray<EOActivites> activiteList = (NSArray) getListener().interventionDisplayGroup().allObjects().valueForKey(
				EOIntervention.TO_ACTIVITES_KEY);
		activiteList = NSArrayCtrl.removeDuplicate(activiteList);
		activiteList = EOSortOrdering.sortedArrayUsingKeyOrderArray(activiteList, CktlSort.newSort("actLibelle"));
		return activiteList;
	}

	/**
	 * La liste de tous les intervenants affectés aux DT du
	 * {@link I_CompListeDemandeListener#interventionDisplayGroup()}
	 */
	public NSArray intervenantList() {
		NSArray intervenantList = (NSArray) getListener().interventionDisplayGroup().allObjects().valueForKeyPath("tosIntervenant.toIndividuUlr");
		intervenantList = NSArrayCtrl.flattenArray(intervenantList);
		intervenantList = NSArrayCtrl.removeDuplicate(intervenantList);
		intervenantList = EOSortOrdering.sortedArrayUsingKeyOrderArray(intervenantList, CktlSort.newSort("nomEtPrenom"));
		return intervenantList;
	}

	/**
	 * Appliquer l'un des filtre via les popups
	 * 
	 * @return
	 */
	public WOComponent doFilter() {
		NSArray<EOQualifier> quals = new NSArray<EOQualifier>();
		if (demandeurSelected != null) {
			EOQualifier qualDemandeur =
					ERXQ.equals(EOIntervention.TO_INDIVIDU_CONCERNE_KEY, demandeurSelected);
			quals = quals.arrayByAddingObject(qualDemandeur);
		}
		if (activiteSelected != null) {
			EOQualifier qualActivites =
					ERXQ.equals(EOIntervention.TO_ACTIVITES_KEY, activiteSelected);
			quals = quals.arrayByAddingObject(qualActivites);
		}
		// TODO
		if (intervenantSelected != null) {
			EOQualifier qualIntervenant = CktlDataBus.newCondition(
					"tosIntervenant.noIndividu=%@", new NSArray(intervenantSelected.numberForKey("noIndividu")));
			quals = quals.arrayByAddingObject(qualIntervenant);
		}
		//
		if (quals.count() > 0) {
			getListener().interventionDisplayGroup().setQualifier(new EOAndQualifier(quals));
		} else {
			getListener().interventionDisplayGroup().setQualifier(null);
		}
		getListener().doFetchDisplayGroup();
		// remettre le DG à la premiere page
		getListener().interventionDisplayGroup().setCurrentBatchIndex(1);
		return null;
	}

	/**
	 * Le nom du container ajax contenant toutes les demandes
	 * 
	 * @return
	 */
	public String getContainerListeID() {
		return "ContainerDemandes";
	}

	/**
	 * Le nom du container ajax pour les opération de masquage, par DT
	 * 
	 * @return
	 */
	public String getContainerMasqueID() {
		String id = "";

		id = "ContainerMasque_" + currentIntervention.intOrdre().intValue();

		return id;
	}

	/**
	 * Le nom du container ajax pour l'ajout au panier
	 * 
	 * @return
	 */
	public String getContainerAjoutPanierID() {
		String id = "";

		id = "ContainerPanier_" + currentIntervention.intOrdre().intValue();

		return id;
	}

	/**
	 * Ajout la DT au panier
	 * 
	 * @return
	 */
	public WOComponent doAjouterPanierCurrentIntervention() {
		dtSession().ajouterPanier(currentIntervention);
		return null;
	}

	/**
	 * Supprimer la DT du panier
	 * 
	 * @return
	 */
	public WOComponent doSupprimerPanierCurrentIntervention() {
		dtSession().supprimerPanier(currentIntervention);
		return null;
	}

	/**
	 * Ne pas autoriser l'ajout de DT au panier si celles ci y sont déjà
	 * 
	 * @return
	 */
	public boolean isAfficherAjouterPanier() {
		boolean isAfficherAjouterPanier = true;

		if (dtSession().isDansLePanier(currentIntervention)) {
			isAfficherAjouterPanier = false;
		}

		return isAfficherAjouterPanier;
	}

	// les bus de donnees

	private DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	private DTIndividuBus individuBus() {
		return dtSession().dataCenter().individuBus();
	}

	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	private DTPrestaBusWeb pieBus() {
		return dtSession().pieBus();
	}

	private DTActiviteBus activiteBus() {
		return dtSession().dataCenter().activiteBus();
	}

	private DTPreferencesBus preferencesBus() {
		return dtSession().dataCenter().preferencesBus();
	}

	public final I_CompListeDemandeListener getListener() {
		return listener;
	}

	public final void setListener(I_CompListeDemandeListener listener) {
		this.listener = listener;
	}

}