import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Ecran de gestion des activites et de leur responsables
 * 
 * @author ctarade
 */
public class SwapActivites extends A_AdministrationSwapView {

	/** listener du composant des activites */
	public AdminActiviteListener activiteListener;
	/** memoriser la derniere selection de service */
	private CktlRecord prevServiceSelected;
	/** le pt d'entree pour le listerner */
	public EOVActivites recVActivite;
	/** l'activite en cours de selection */
	public EOVActivites currentActivite;
	// L'enregistrement selectionne pour le deplacement (cut)
	public CktlRecord recordToCut;
	// Tous les fils (sous-fils,...) du l'enregistrement pour le deplacement ou la
	// fusion
	private NSMutableArray recordsFils;
	//
	public CktlRecord recordToMerge;

	public SwapActivites(WOContext context) {
		super(context);
	}

	// override setter
	public void setServiceSelected(EOStructure value) {
		prevServiceSelected = serviceSelected;
		serviceSelected = value;
		if (serviceSelected != null && (
				prevServiceSelected == null || (prevServiceSelected != null &&
						!serviceSelected.stringForKey("cStructure").equals(prevServiceSelected.stringForKey("cStructure"))))) {
			// refetcher l'activite d'entree du browser
			EOQualifier condition =
					DTDataBus.newCondition("cStructure='" + serviceSelected.stringForKey("cStructure") + "'");
			NSArray<EOVActivites> activitesForService = dtSession().dataBus().fetchArray(EOVActivites.ENTITY_NAME, condition, null);
			// selection de la premiere activite
			if (activitesForService.count() > 0) {
				recVActivite = activitesForService.objectAtIndex(0);
			}
			// effacer la selection locale
			currentActivite = null;
			// reinstancier le listener
			activiteListener = new AdminActiviteListener();
		}
	}

	/**
	 * Forcer la selection du service par son cStructure. Utile si l'objet
	 * CktlRecord via setServiceSelected(CktlRecord) n'est pas le meme que celui
	 * de la liste de <code>serviceList</code>
	 * 
	 */
	public void setServiceSelectedCStructure(String value) {
		EOQualifier condition = DTDataBus.newCondition("cStructure='" + value + "'");
		NSArray activitesForService = EOQualifier.filteredArrayWithQualifier(serviceList, condition);
		if (activitesForService.count() > 0) {
			setServiceSelected((EOStructure) activitesForService.objectAtIndex(0));
		}
	}

	/**
	 * La classe listener de gestion du sous composant SelectActivite.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	public class AdminActiviteListener extends SelectActiviteListener {

		public void doAfterActiviteSelectedItem() {
			// on memorise l'activite
			if (getActiviteSelectedItem() != null) {
				currentActivite = getActiviteSelectedItem().nodeRecord;
			} else {
				currentActivite = null;
			}
		}

		protected CktlRecord recVActivite() {
			return recVActivite;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return "formAdministration";
		}

		public void doAfterSearchActivite() {
		}

		public WOComponent caller() {
			return null;
		}

		public boolean shouldSelectedOnlyLeaf() {
			return true;
		}

		public boolean showHiddenActivite() {
			return true;
		}

		/**
		 * c'est un administrateur qui accede a cet écran, on affiche les activités
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
			return false;
		}

	}

	protected void initComponent() {
		// selection du premier service de la liste
		if (serviceList.count() > 0) {
			setServiceSelected(serviceList.objectAtIndex(0));
		}
	}

	// les actions possibles

	/**
	 * Autoriser un nouveau service comme service DT
	 */
	public WOComponent creerService() {
		PageActiviteService nextPage = (PageActiviteService) pageWithName(PageActiviteService.class.getName());
		nextPage.initForAjout(this, null);
		return nextPage;
	}

	/**
	 * Creer un nouvelle activite dans l'arbre
	 */
	public WOComponent creerActivite() {
		PageActiviteActivites nextPage = (PageActiviteActivites) pageWithName(PageActiviteActivites.class.getName());
		nextPage.initForAjout(this, (currentActivite == null ? recVActivite : currentActivite));
		return nextPage;
	}

	/**
	 * Editer le service selectionne
	 */
	public WOComponent modifierService() {
		PageActiviteService nextPage = (PageActiviteService) pageWithName(PageActiviteService.class.getName());
		nextPage.initForModif(recVActivite, null);
		if (nextPage.hasInitError()) {
			return CktlAlertPage.newAlertPageWithCaller(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Erreur", nextPage.getErrorMessage(), "Revenir", CktlAlertPage.ERROR);
		}
		return nextPage;
	}

	/**
	 * Editer l'activite selectionne
	 */
	public WOComponent modifierActivite() {
		// demande de confirmation si modification et deja utilise
		if (activiteBus().isActiviteInUse(currentActivite.actOrdre())) {
			return CktlAlertPage.newAlertPageWithResponder(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Demande de confirmation",
					"Il existe des enregistrements utilisant l'activité \"" +
							currentActivite.actLibelle() +
							"\"\nVoulez-vous vraiment modifier ce libelle ?",
					"Modifier", "Annuler", null, CktlAlertPage.QUESTION, new ModifierActiviteInUseResponder());
		} else {
			return goPageActiviteActivites();
		}
	}

	/**
	 * Aller directement a l'ecran de modification de l'activite
	 */
	private WOComponent goPageActiviteActivites() {
		PageActiviteActivites nextPage = (PageActiviteActivites) pageWithName(PageActiviteActivites.class.getName());
		nextPage.initForModif(currentActivite, currentActivite.toActPere());
		return nextPage;
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la suppression
	 * d'un service.
	 */
	public class ModifierActiviteInUseResponder implements CktlAlertResponder {
		public ModifierActiviteInUseResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1:
				return goPageActiviteActivites();
			case 2:
			default:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			}
		}
	}

	/**
	 * Supprimer le service selectionne
	 */
	public WOComponent supprimerService() {
		if (activiteBus().isActiviteInUse(recVActivite.numberForKey("actOrdre"))) {
			return CktlAlertPage.newAlertPageWithCaller(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Suppression impossible",
					"Il existe des enregistrements utilisant le service DT \"" + recVActivite.stringForKey("actLibelle") +
							"\"\nCe service ne peut pas être supprimé !",
					"Revenir",
					CktlAlertPage.ERROR);
		}
		return CktlAlertPage.newAlertPageWithResponder(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Demande de confirmation",
					"Voulez-vous vraiment supprimer le service \"" + recVActivite.valueForKey("actLibelle") + "\" ?",
					"Supprimer", "Annuler", null, CktlAlertPage.QUESTION, new DeleteServiceResponder(recVActivite));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la suppression
	 * d'un service.
	 */
	public class DeleteServiceResponder implements CktlAlertResponder {
		private CktlRecord recVActiviteToDelete;

		public DeleteServiceResponder(CktlRecord aRecVActivite) {
			super();
			recVActiviteToDelete = aRecVActivite;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			case 1:
				if (activiteBus().deleteGroupe(null, Integer.toString(-recVActiviteToDelete.intForKey("actOrdre")))) {
					// recharger la liste des services
					initServiceList();
					// reinitialiser le composant
					initComponent();
				} else {
					errorMessage = "Le service n'a pas pu &ecirc;tre supprim&eacute; !";
				}
			default:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			}
		}
	}

	/**
	 * Supprimer l'activite selectionnee
	 */
	public WOComponent supprimerActivite() {
		/*
		 * if
		 * (activiteBus().isActiviteInUse(currentActivite.numberForKey("actOrdre")))
		 * { return CktlAlertPage.newAlertPageWithCaller(
		 * dtSession().getSavedPageWithName(PageAdministration.class.getName()),
		 * "Suppression impossible",
		 * "Il existe des enregistrements utilisant l'activit&eacute; \"" +
		 * currentActivite.valueForKey("actLibelle") +
		 * "\"\nCette activit&eacute; ne peut pas etre supprim&eacute;e !",
		 * "Revenir", CktlAlertPage.ERROR); }
		 */
		return CktlAlertPage.newAlertPageWithResponder(
				dtSession().getSavedPageWithName(PageAdministration.class.getName()),
				"Demande de confirmation",
				"Voulez-vous vraiment supprimer l'activit&eacute; \"" + currentActivite.valueForKey("actLibelle") + "\" ?",
				"Supprimer", "Annuler", null, CktlAlertPage.QUESTION, new DeleteActiviteResponder(currentActivite));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la suppression
	 * d'une activite.
	 */
	public class DeleteActiviteResponder implements CktlAlertResponder {
		private CktlRecord recActiviteToDelete;

		public DeleteActiviteResponder(CktlRecord aRecActivite) {
			super();
			recActiviteToDelete = aRecActivite;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1:
				Number actOrdrePere = recActiviteToDelete.numberForKey("actPere");
				if (activiteBus().deleteActivite(null, recActiviteToDelete.numberForKey("actOrdre"))) {
					refreshAndClearSelection();
					// selectionner l'activite pere dans l'arbre
					if (actOrdrePere != null && actOrdrePere.intValue() > 0) {
						activiteListener.selectActiviteByActOrdre(actOrdrePere);
					}
				} else {
					errorMessage = "L'activité n'a pas pu être supprimée !";
				}
			case 2:
			default:
				return dtSession().getSavedPageWithName(PageAdministration.class.getName());
			}
		}
	}

	/**
	 * Definir les responsables pour le service ou l'activite selectionne
	 */
	public WOComponent definirResponsables() {
		PageActiviteResponsables nextPage = (PageActiviteResponsables) pageWithName(PageActiviteResponsables.class.getName());
		// on voit si c'est une modif de service ou d'activite
		if (currentActivite != null) {
			nextPage.init(currentActivite);
		} else {
			nextPage.init(recVActivite);
		}
		return nextPage;
	}

	/**
	 * Memorise l'activite selectionne pour la deplacer ulterieurement.
	 */
	public WOComponent couper() {
		// On deplace les activites, mais pas les services
		if (currentActivite != null) {
			recordToCut = currentActivite;
			recordsFils = new NSMutableArray();
			// On construile le array avec tous les fils, sous-fils, sou-sous-fils...
			recordsFils.addObject(recordToCut.valueForKey("actOrdre"));
			activiteBus().fillActivitesAllFils(recordToCut.numberForKey("actOrdre"), recordsFils);
		}
		return null;
	}

	/**
	 * Deplace l'activite selectionnee precedement comme une sous-activite de
	 * celle actuellement selectionne. Cette operation est possible comme suite de
	 * l'appel a la methode <code>couper</code>.
	 * 
	 * @see #couper()
	 */

	public WOComponent coller() {
		StringBuffer sb = new StringBuffer();
		// On vérifie si le deplacement est autorise
		boolean ok = (recordToCut != null);
		CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
		if (!ok) { // On n'a rien selectionne pour deplacer
			sb.append("Aucune activit&eacute; n'a &eacute;t&eacute; s&eacute;lectionn&eacute;e pour d&eacute;placer");
		} else {
			// Tester si on n'essaye pas de deplacer dans une sous activite
			ok = (!isInRecordToMoveFils(recActivite));
			if (!ok) {
				sb.append("L'activit&eacute; \"").append(recordToCut.valueForKey("actLibelle"));
				sb.append("\" ne peut pas &ecir;tre d&eacute;plac&eacute;e\ndans une de ses sous-activit&eacute;s !");
			}
		}
		if (ok) {
			// Tester si on n'esaye pas de deplacer dans un autre service
			ok = recActivite.stringForKey("cStructure").equals(recordToCut.stringForKey("cStructure"));
			if (!ok) {
				sb.append("Une activit&eacute; ne peux pas &ecirc;tre\n");
				sb.append("d&eacute;plac&eacute;e entre diff&eacute;rents services !");
			}
		}
		if (sb.length() > 0) {
			return getMessagePage(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Deplacement impossible", sb.toString(), true);
		}
		sb.append("Voulez-vous d&eacute;placer l'activit&eacute; \"");
		sb.append(recordToCut.valueForKey("actLibelle"));
		sb.append("\"\ndans l'activit&eacute; \"");
		sb.append(recActivite.valueForKey("actLibelle")).append("\" ?");

		return CktlAlertPage.newAlertPageWithResponder(
				dtSession().getSavedPageWithName(PageAdministration.class.getName()),
				"Demande de confirmation", sb.toString(),
				"D&eacute;placer", "Annuler", null, CktlAlertPage.QUESTION, new MoveActiviteResponder());
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour le deplacement
	 * d'une activite.
	 */
	public class MoveActiviteResponder implements CktlAlertResponder {

		private WOComponent caller;

		public MoveActiviteResponder() {
			super();
			caller = dtSession().getSavedPageWithName(PageAdministration.class.getName());
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				ActiviteBusListenerImp busListener = new ActiviteBusListenerImp();
				activiteBus().setActiviteBusListener(busListener);
				// Effectuer les traitements
				CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
				boolean success = activiteBus().moveActivite(
							null,
							recordToCut.numberForKey("actOrdre"),
							recActivite.numberForKey("actOrdre"));
				if (!success) {
					return getMessagePage(caller, "Erreur de sauvegarde", activiteBus().getErrorMessage(), true);
				}
				// rafraichir
				refreshAndClearSelection();
				return busListener.showMessage();
			default:
				return caller;
			}
		}
	}

	/**
	 * Test si l'activite <code>rec</code> appartient a l'arborescence des fils
	 * d'une activite selectionnee pour le deplacement (couper/coller). Les codes
	 * de tous les fils sont disponibles dans le tableau
	 * <code>recortToCutFils</code>.
	 */
	private boolean isInRecordToMoveFils(CktlRecord rec) {
		for (int i = 0; i < recordsFils.count(); i++) {
			if (((Number) recordsFils.objectAtIndex(i)).intValue() == rec.intForKey("actOrdre"))
				return true;
		}
		return false;
	}

	/**
	 * Afficher l'arboresence des activites du service sous forme HTML.
	 */
	public WOResponse exporterHTML() {
		if (serviceSelected != null) {
			String content = activiteBus().printActivitesHTML(serviceSelected.stringForKey("cStructure"));
			CktlDataResponse resp = new CktlDataResponse();
			if (content != null) {
				resp.setContent(content);
				String fileName = "Activites_" + serviceSelected.stringForKey("lcStructure") + ".html";
				resp.setFileName(fileName);
			} else {
				resp.setContent("");
				resp.setHeader("0", "Content-Length");
			}
			return resp.generateResponse();
		}
		return null;
	}

	/**
	 * Reinitialise les aliases email utilisees pour creer les "demande par mail".
	 * Le serveur SAM avec le plugin "Demande de Travaux" doit etre utilise dans
	 * ce cas. Un message est envoye a l'adresse definit dans le parametre de
	 * configuration <code>APP_ACT_ALIASES_MAIL</code> pour informer SAM que les
	 * aliases doivent etre recrees.
	 */
	public WOComponent notifierSAM() {
		String addr = DTStringCtrl.normalize(dtApp().config().stringForKey("APP_ACT_ALIASES_MAIL"));
		WOComponent caller = dtSession().getSavedPageWithName(PageAdministration.class.getName());
		if (addr.length() > 0) {
			if (!dtApp().mailBus().sendMail(dtUserInfo().email(), addr, null,
					"UPDATE", "[mise a jour des activites]")) {
				return getMessagePage(caller, "Erreur",
						"Une erreur s'est produite lors de l'envoi de message\n" +
								"de la mise &agrave; jour des aliases email", true);
			}
			return getMessagePage(caller, "Information",
					"Un message de mise &agrave; jour est envoy&eacute; au serveur SAM.\n" +
							"Adresse email : \"" + dtApp().config().stringForKey("APP_ACT_ALIASES_MAIL") + "\"\n\n" +
							"Consultez les logs de SAM pour plus d'informations.", false);
		} else {
			return getMessagePage(caller, "Erreur",
					"La configuration de l'application indique que\n" +
							"la cr&eacute;ation de demandes par email n'est pas support&eacute;e\n" +
							"(param&eacute;tre APP_ACT_ALIASES_MAIL n'est pas d&eacute;fini) !", true);
		}
	}

	/**
	 * Deselectionner l'activite en cours et rafraichir la liste des activites.
	 * Utile pour l'ajout / modification d'activite, mais aussi pour creer une
	 * activite a la racine du service
	 */
	public WOComponent refreshAndClearSelection() {
		CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
		String cStructure = recActivite.stringForKey("cStructure");
		setServiceSelected(null);
		setServiceSelectedCStructure(cStructure);
		clearRecordToCutAndRecordToMerge();
		return null;
	}

	/**
	 * Deselectionner les selections des activités à deplacer ou fusionner
	 * 
	 * @return
	 */
	public WOComponent clearRecordToCutAndRecordToMerge() {
		recordToCut = null;
		recordToMerge = null;
		return null;
	}

	// methode interne

	/**
	 * Affichage d'une page de message avec bouton de retour / continuer
	 * 
	 * @param caller
	 * @param errTitle
	 * @param message
	 * @param isError
	 * @return
	 */
	private WOComponent getMessagePage(WOComponent caller, String errTitle, String message, boolean isError) {
		return CktlAlertPage.newAlertPageWithCaller(caller, errTitle, DTStringCtrl.codeHTML(message),
				isError ? "Revenir" : "Continuer", isError ? CktlAlertPage.ERROR : CktlAlertPage.INFO);
	}

	// disponibilite des boutons

	public boolean isDisabledBtnCreerService() {
		return !dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER);
	}

	public boolean isDisabledBtnModifierService() {
		return serviceSelected == null;
	}

	public boolean isDisabledBtnSupprimerService() {
		return serviceSelected == null || !dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER);
	}

	public boolean isDisabledBtnCouper() {
		return currentActivite == null;
	}

	/**
	 * -- Coller - -si une activite etait selectionne pour couper -si elle
	 * n'appartient pas a son propre arborescencens -si elle n'appartient a un
	 * autre service
	 * 
	 * @return
	 */
	public boolean isDisabledBtnColler() {
		CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
		return !(recordToCut != null &&
				recActivite != null && recActivite.stringForKey("cStructure").equals(recordToCut.stringForKey("cStructure")));
	}

	public boolean isDisabledBtnCreerActivite() {
		return serviceSelected == null;
	}

	public boolean isDisabledBtnModifierActivite() {
		return currentActivite == null;
	}

	public boolean isDisabledBtnSupprimerActivite() {
		return currentActivite == null;
	}

	public boolean isDisabledBtnDefinirResponsables() {
		return serviceSelected == null;
	}

	public boolean isDisabledBtnClearSelection() {
		return currentActivite == null;
	}

	public boolean isARecordToCutSelected() {
		return recordToCut != null;
	}

	public boolean isARecordToMergeSelected() {
		return recordToMerge != null;
	}

	private class ActiviteBusListenerImp implements DTActiviteBus.ActiviteBusListener {

		private String msg;

		public ActiviteBusListenerImp() {
			super();
			msg = "";
		}

		public WOComponent showMessage() {
			return showMessage(msg);
		}

		public WOComponent showMessage(String message) {
			msg = message;
			return getMessagePage(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Information", message, false);
		}

		public void advanceMessage(String message) {
			msg += message;
		}

	}

	// fusion

	/**
	 * Memorise l'activite selectionne pour la fusionner ulterieurement.
	 */
	public WOComponent selectToMerge() {
		// On deplace les activites, mais pas les services
		if (currentActivite != null) {
			recordToMerge = currentActivite;
			recordsFils = new NSMutableArray();
			// On construile le array avec tous les fils, sous-fils, sou-sous-fils...
			recordsFils.addObject(recordToMerge.valueForKey("actOrdre"));
			activiteBus().fillActivitesAllFils(recordToMerge.numberForKey("actOrdre"), recordsFils);
		}
		return null;
	}

	/**
	 * Fusionne l'activite selectionnee precedement avec celle qui est choisie.
	 * Cette operation est possible comme suite de l'appel a la methode
	 * <code>selectToMerge</code>.
	 * 
	 * @see #selectToMerge()
	 */

	public WOComponent merge() {
		StringBuffer sb = new StringBuffer();
		// On vérifie si le deplacement est autorise
		boolean ok = (recordToMerge != null);
		CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
		if (!ok) { // On n'a rien selectionne pour fusion
			sb.append("Aucune activit&eacute; n'a &eacute;t&eacute; s&eacute;lectionn&eacute;e pour fusionner");
		} else {
			// Tester si on n'essaye pas de fusionner dans une sous activite
			ok = (!isInRecordToMoveFils(recActivite));
			if (!ok) {
				sb.append("L'activit&eacute; \"").append(recordToMerge.valueForKey("actLibelle"));
				sb.append("\" ne peut pas &ecir;tre fusionn&eacute;e\ndans une de ses sous-activit&eacute;s !");
			}
		}
		if (ok) {
			// Tester si on n'essaye pas de fusionner dans un autre service
			ok = recActivite.stringForKey("cStructure").equals(recordToMerge.stringForKey("cStructure"));
			if (!ok) {
				sb.append("Une activit&eacute; ne peux pas &ecirc;tre\n");
				sb.append("fusionn&eacute;e entre diff&eacute;rents services !");
			}
		}
		if (sb.length() > 0) {
			return getMessagePage(
					dtSession().getSavedPageWithName(PageAdministration.class.getName()),
					"Fusion impossible", sb.toString(), true);
		}
		sb.append("Voulez-vous fusionner l'activit&eacute; \"");
		sb.append(recordToMerge.valueForKey("actLibelle"));
		sb.append("\"\navec l'activit&eacute; \"");
		sb.append(recActivite.valueForKey("actLibelle")).append("\" ?");
		sb.append("<br/><br/>L'activit&eacute; finale sera <b>").append(recActivite.valueForKey("actLibelle")).append("</b>");

		return CktlAlertPage.newAlertPageWithResponder(
				dtSession().getSavedPageWithName(PageAdministration.class.getName()),
				"Demande de confirmation", sb.toString(),
				"Fusionner", "Annuler", null, CktlAlertPage.QUESTION, new MergeActiviteResponder());
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la fusion
	 * d'activite.
	 */
	public class MergeActiviteResponder implements CktlAlertResponder {

		private WOComponent caller;

		public MergeActiviteResponder() {
			super();
			caller = dtSession().getSavedPageWithName(PageAdministration.class.getName());
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				ActiviteBusListenerImp busListener = new ActiviteBusListenerImp();
				activiteBus().setActiviteBusListener(busListener);
				// Effectuer les traitements
				CktlRecord recActivite = (currentActivite != null ? currentActivite : recVActivite);
				boolean success = activiteBus().mergeActivites(
							dtUserInfo(),
							null,
							recActivite.numberForKey("actOrdre"), recordToMerge.numberForKey("actOrdre"));
				if (!success) {
					return getMessagePage(caller, "Erreur de sauvegarde", activiteBus().getErrorMessage(), true);
				}
				// rafraichir
				refreshAndClearSelection();
				return busListener.showMessage();
			default:
				return caller;
			}
		}
	}

}