import org.cocktail.dt.server.metier.EOPrefDroits;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

/**
 * Ecran de gestion des intervenants et de leur droits sur les services
 * 
 * @author ctarade
 */
public class SwapDroits extends A_AdministrationSwapView {

	/**
	 * le numero de l'individu selectionne via le listener
	 * <code>SelectPersonneListener</code>
	 */
	public Integer noIndividu;
	/** le nom prenom de cet individu */
	public String nomPrenom;
	/** */
	private SelectPersonneListener personneListener;

	// la liste des types de droits connus
	public NSArray<Integer> droitNiveauList;
	public Integer droitNiveauItem;
	public Integer droitNiveauSelected;

	// le display group contenant l'ensemble des droits
	public WODisplayGroup dgDroits;
	public CktlRecord droitItem;
	public NSArray droitSelecteds;
	private boolean shouldInitDg;

	private EOQualifier qualDgDroits;
	private static NSArray sortDgDroits = CktlSort.newSort("droService").add("nomUsuel").add("prenom");

	// code CSS d'affichage pour police proportionnels dans le combo
	private final static String CSS_PROPORTIONNAL = "option {font-family: monospace}";

	// les differents bulles d'aide sur chaque niveau
	private final static String HTML_NIVEAU_1 =
			"<b>Niveau 1 - Intervenant limité</b>" +
					"<ul>" +
					"<li>Ajout / édition de traitements et cloture de DT</li>" +
					"<li>Visibilité uniquement sur les DTs qui lui sont affectées personnellement</li>" +
					"</ul>";

	private final static String HTML_NIVEAU_2 =
			"<b>Niveau 2 - Intervenant</b>" +
					"<ul>" +
					"<li>Droits Niveau 1</li>" +
					"<li>Droits d'affectation</li>" +
					"<li>Changement d'état (uniquement s'il fait partie des intervenants)</li>" +
					"<li>Droit de valider les DTs directement après leur création</li>" +
					"</ul>";

	private final static String HTML_NIVEAU_3 =
			"<b>Niveau 3 - Super-intervenant</b>" +
					"<ul>" +
					"<li>Droits Niveau 2</li>" +
					"<li>Enlever des affectations.</li>" +
					"<li>Suppression de DT</li>" +
					"<li>Changement d'état</li>" +
					"<li>Droit d'affecter les DTs directement après leur création</li>" +
					"<li>Droit de modifier les motifs et les demandeurs de toutes les DTs</li>" +
					"</ul>";

	private final static String HTML_NIVEAU_4 =
			"<b>Niveau 4 - Administrateur</b>" +
					"<ul>" +
					"<li>Droits Niveau 3</li>" +
					"<li>Gestion des droits sur le service (droit max = 4)</li>" +
					"<li>Gestion des activités du service</li>" +
					"</ul>";

	private final static String HTML_NIVEAU_5 =
			"<b>Niveau 5 - Super-administrateur</b>" +
					"<ul>" +
					"<li>Niveau 4 sur tous les services</li>" +
					"<li>Droit de supprimer / créer des services DT</li>" +
					"</ul>";

	// le mode d'utilisation du composant
	public int currentMode;
	private final static int MODE_CONSULT = 0;
	private final static int MODE_AJOUT = 1;
	private final static int MODE_MODIF = 2;

	// eventuel message d'erreur
	public String errorMessage;

	public SwapDroits(WOContext context) {
		super(context);
	}

	protected void initComponent() {
		// inialisation de l'etat
		currentMode = MODE_CONSULT;

		// construire la liste des droits
		droitNiveauList = new NSArray(new Integer[] {
				new Integer(DTUserInfo.DROIT_DEMANDEUR),
				new Integer(DTUserInfo.DROIT_INTERVENANT_LIMITE),
				new Integer(DTUserInfo.DROIT_INTERVENANT),
				new Integer(DTUserInfo.DROIT_INTERVENANT_SUPER),
				new Integer(DTUserInfo.DROIT_ADMIN)
		});

		// seul l'administration peut attribuer le niveau 5
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER)) {
			droitNiveauList = droitNiveauList.arrayByAddingObject(new Integer(DTUserInfo.DROIT_ADMIN_SUPER));
		}

		// parametrer le display group selon le profil
		// Super-administrateur peut tout voir
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER)) {
			qualDgDroits = null;
		} else if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN)) {
			// Sinon, uniquement les intervenants des services ou il est
			// administrateur
			NSArray services = preferencesBus().getAllServiceCodes(
					dtUserInfo().noIndividu(), DTUserInfo.DROIT_ADMIN);
			if (services.count() > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < services.count(); i++) {
					if (sb.length() > 0)
						sb.append(" or ");
					sb.append("(").append("droService='").append(services.objectAtIndex(i)).append("')");
				}
				qualDgDroits = DTDataBus.newCondition(sb.toString());
			} else {
				// Sinon, rien dans la liste
				qualDgDroits = DTDataBus.newCondition("droService != droService");
			}
		} else {
			// Condition "false"
			qualDgDroits = DTDataBus.newCondition("droService != droService");
		}
		// indiquer que l'initialisation du DG doit se faire (1 fois)
		shouldInitDg = true;
	}

	/**
	 * Lors du premier chargement de la page, certains champs doivent etre
	 * pre-remplis.
	 */
	private void doFirstLoadSelections() {
		if (dgDroits.displayedObjects().count() > 0) {
			// selection du premier droit dans la liste
			droitSelecteds = new NSArray((CktlRecord) dgDroits.displayedObjects().objectAtIndex(0));
			doAfterChangeSelectionDG();
		}
	}

	/**
	 * Forcer le DG a relire ses donnees depuis la base de donnees.
	 */
	private void refreshDGFromDatabase() {
		dgDroits.setQualifier(qualDgDroits);
		dgDroits.setSortOrderings(sortDgDroits);
		dgDroits.fetch();
	}

	/**
	 * Lors du changement de la selection dans le display group, les 4 champs
	 * doivent etre remplis en regard de la selection
	 */
	private void doAfterChangeSelectionDG() {
		if (droitSelecteds.count() > 0) {
			CktlRecord recDroit = (CktlRecord) droitSelecteds.lastObject();
			// nom / prenom / noIndividu
			noIndividu = new Integer(recDroit.intForKey("noIndividu"));
			nomPrenom = recDroit.stringForKey("nomUsuel") + " " + recDroit.stringForKey("prenom");
			// selection des valeurs associees dans les listes
			serviceSelected = serviceBus().structureForCode(
					recDroit.stringForKeyPath("toStructureUlr.cStructure"));
			droitNiveauSelected = new Integer(recDroit.intForKey("droNiveau"));
		}
	}

	/**
	 * On attend que le display group soit instancié avant d'appliquer ses
	 * paramatres.
	 */
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
		if (shouldInitDg) {
			shouldInitDg = false;
			refreshDGFromDatabase();
			doFirstLoadSelections();
		}
		super.appendToResponse(woresponse, wocontext);
		// css
		addTextCss(woresponse, CSS_PROPORTIONNAL);
	}

	// methode d'affichage

	public String droitNiveauDisplay() {
		String display = "";
		if (droitNiveauItem.intValue() == DTUserInfo.DROIT_DEMANDEUR) {
			display += "Demandeur";
		} else if (droitNiveauItem.intValue() == DTUserInfo.DROIT_INTERVENANT_LIMITE) {
			display += "Intervenant limité";
		} else if (droitNiveauItem.intValue() == DTUserInfo.DROIT_INTERVENANT) {
			display += "Intervenant";
		} else if (droitNiveauItem.intValue() == DTUserInfo.DROIT_INTERVENANT_SUPER) {
			display += "Super-Intervenant";
		} else if (droitNiveauItem.intValue() == DTUserInfo.DROIT_ADMIN) {
			display += "Administrateur";
		} else if (droitNiveauItem.intValue() == DTUserInfo.DROIT_ADMIN_SUPER) {
			display += "Super-Administrateur";
		}
		display += " (" + droitNiveauItem.intValue() + ")";
		return display;
	}

	public String droitDisplay() {
		String display = DTStringCtrl.fitToSize(droitItem.stringForKey("lcStructure"), 13, ".") + " | " +
				DTStringCtrl.fitToSize(droitItem.stringForKey("nomUsuel") + " " + droitItem.stringForKey("prenom"), 30, ".") +
				" | " + droitItem.intForKey("droNiveau");
		return StringCtrl.replace(display, " ", "&nbsp;");
	}

	// boutons

	public WOComponent changePersonne() {
		return SelectPersonne.getNewPage(personneListener(),
				"Indiquez la personne a qui va etre affecte le nouveau droit",
				noIndividu, true);
	}

	/**
	 * Validation de l'operation en cours
	 */
	public WOComponent ok() {
		errorMessage = StringCtrl.emptyString();
		if (noIndividu == null || serviceSelected == null || droitNiveauSelected == null) {
			return null;
		}
		Integer transactionId = preferencesBus().beginECTransaction();
		if (!preferencesBus().updatePrefDroits(
				transactionId, noIndividu, serviceSelected.stringForKey("cStructure"), null, droitNiveauSelected)) {
			preferencesBus().rollbackECTrancsacition(transactionId);
			errorMessage = "La mise à jour des informations sur l'utilisateur a échoué !";
			return null;
		}
		preferencesBus().commitECTransaction(transactionId);

		// rafraichir la liste
		dgDroits.fetch();
		// selection de l'enregistrement nouvellement cree
		EOQualifier qualNewRec = DTDataBus.newCondition(
				"noIndividu=" + noIndividu + " and droService='" + serviceSelected.stringForKey("cStructure") + "'");
		droitSelecteds = EOQualifier.filteredArrayWithQualifier(
				dgDroits.displayedObjects(), qualNewRec);
		// forcer l'affichage des to-many
		CktlRecord recNewDroit = (CktlRecord) droitSelecteds.lastObject();
		recNewDroit.editingContext().invalidateObjectsWithGlobalIDs(new NSArray(
					recNewDroit.editingContext().globalIDForObject(recNewDroit)));
		refreshDGFromDatabase();

		currentMode = MODE_CONSULT;
		return null;
	}

	public WOComponent annuler() {
		currentMode = MODE_CONSULT;
		return null;
	}

	public WOComponent ajouter() {
		currentMode = MODE_AJOUT;
		return null;
	}

	public WOComponent modifier() {
		currentMode = MODE_MODIF;
		return null;
	}

	/**
	 * Effacer un droit existant
	 * 
	 * @return
	 */
	public WOComponent supprimer() {
		errorMessage = StringCtrl.emptyString();
		if (droitSelecteds == null || droitSelecteds.count() == 0) {
			return null;
		}
		EOPrefDroits recDroit = (EOPrefDroits) droitSelecteds.lastObject();
		int nbTraitementEnCours = interventionBus().countInterventionsForIndividu(
				recDroit.noIndividu(), "C", recDroit.droService());
		if (nbTraitementEnCours > 0) {
			errorMessage = "Cet utilisateur &agrave; des demandes en cours !\n " +
					"Il ne peut pas &ecirc;tre supprimé de la liste d'intervenants.";
			return null;
		}
		currentMode = MODE_CONSULT;
		return CktlAlertPage.newAlertPageWithResponder(this, "Suppression d'utilisateur",
				"Voulez-vous vraiment supprimer cet utilisateur ?",
				"Oui", "Non", null, CktlAlertPage.QUESTION, new DeleteUtilisateurResponder(recDroit));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la suppression
	 * d'un intervenant.
	 */
	public class DeleteUtilisateurResponder implements CktlAlertResponder {
		private EOPrefDroits recDroit;

		public DeleteUtilisateurResponder(EOPrefDroits aRecDroit) {
			super();
			recDroit = aRecDroit;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			case 1:
				if (!preferencesBus().deletePrefDroits(null,
						recDroit.noIndividu(),
						recDroit.droService())) {
					errorMessage = "L'utilisateur n'a pas pu &ecirc;tre supprimé !";
				}
				noIndividu = null;
				nomPrenom = null;
			default:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			}
		}
	}

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection des personnes.
	 */
	private SelectPersonneListener personneListener() {
		if (personneListener == null)
			personneListener = new DroitPersonneSelector();
		return personneListener;
	}

	/**
	 * Implemente les methode necessaires pour communiquer avec la page de la
	 * selection des personnes. Permet de choisir la personne a qui affecter le
	 * droit.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class DroitPersonneSelector implements SelectPersonneListener {
		/*
		 * @see PersonneSelectListener#select(java.lang.Number, java.lang.Number)
		 */
		public WOComponent select(Number persId) {
			CktlUserInfo userInfo = new CktlUserInfoDB(dtSession().dataBus());
			userInfo.compteForPersId(persId, true);
			noIndividu = new Integer(userInfo.noIndividu().intValue());
			nomPrenom = userInfo.nomEtPrenom();
			return dtSession().getSavedPageWithName(PageAdministration.class.getName());
		}

		/*
		 * @see PersonneSelectListener#cancel()
		 */
		public WOComponent cancel() {
			return dtSession().getSavedPageWithName(PageAdministration.class.getName());
		}

		/*
		 * @see PersonneSelectListener#context()
		 */
		public WOContext getContext() {
			return context();
		}
	}

	// setter

	/**
	 * intercepter le changement de la selection dans le DG pour effectuer des
	 * traitements
	 */
	public void setDroitSelecteds(NSArray value) {
		droitSelecteds = value;
		doAfterChangeSelectionDG();
	}

	// disponibilite des boutons

	public boolean isDisabledComboTypeDroits() {
		return !(currentMode == MODE_AJOUT || currentMode == MODE_MODIF);
	}

	public boolean isDisabledBtnChanger() {
		return !(currentMode == MODE_AJOUT);
	}

	public boolean isDisabledComboService() {
		return !(currentMode == MODE_AJOUT);
	}

	public boolean isDisabledBtnOK() {
		return !(currentMode == MODE_AJOUT || currentMode == MODE_MODIF);
	}

	public boolean isDisabledBtnAnnuler() {
		return !(currentMode == MODE_AJOUT || currentMode == MODE_MODIF);
	}

	public boolean isDisabledBtnAjouter() {
		return !(currentMode == MODE_CONSULT);
	}

	public boolean isDisabledBtnModifier() {
		return !(currentMode == MODE_CONSULT);
	}

	public boolean isDisabledBtnSupprimer() {
		return !(currentMode == MODE_CONSULT);
	}

	public boolean hasErrorMessage() {
		return !StringCtrl.isEmpty(errorMessage);
	}

	// getters

	public String getHtmlTextNiveau1() {
		return HTML_NIVEAU_1;
	}

	public String getHtmlTextNiveau2() {
		return HTML_NIVEAU_2;
	}

	public String getHtmlTextNiveau3() {
		return HTML_NIVEAU_3;
	}

	public String getHtmlTextNiveau4() {
		return HTML_NIVEAU_4;
	}

	public String getHtmlTextNiveau5() {
		return HTML_NIVEAU_5;
	}
}