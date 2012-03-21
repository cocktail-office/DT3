import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

/**
 * Composant permettant de gerer l'affectation d'une DT. Il peut etre utilise de
 * 2 fa�ons : - page complete : setShowFullPage(true) - page partielle : par
 * defaut
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class CompAffectation extends DTWebComponent {

	// la classe de gestion du composant
	public CompAffectationListener listener;
	// la DT que l'on va affecter
	private EOIntervention recIntervention;
	// la page a afficher en retour
	private WOComponent pageRetour;

	// premier affichage du composant : on fait un traitement particulier
	private boolean isInitializing;

	public CompAffectation(WOContext context) {
		super(context);
	}

	private void resetComponent() {
		// tableau contenant la liste de priorites
		if (prioritesList == null) {
			prioritesList = new NSArray();
			for (int i = 9; i >= -9; i--)
				prioritesList = prioritesList.arrayByAddingObject(new Integer(i));
			prioriteSelection = (Number) prioritesList.objectAtIndex(0);
		}
		// -- selection des radios par defaut
		if (StringCtrl.isEmpty(typeAff)) {
			// affectation a un intervenant DT
			setTypeAff(TYPE_AFF_AFFECTATION);
		}
		if (StringCtrl.isEmpty(typeInt)) {
			// on va preselectionner les responsables de l'activite
			// dans la liste des intervenants du service
			setTypeInt(TYPE_INT_ACTIVITES);
			refreshIndividuDG();
			if (individuDisplayGroup.allObjects().count() > 0) {
				individuDisplayGroup.setSelectedObjects(individuDisplayGroup.allObjects());
			}
			setTypeInt(TYPE_INT_INTERVENANTS);
		}
		isInitializing = true;
	}

	public Boolean showFullPage = Boolean.FALSE;

	/**
	 * Forcer le type d'affichage du composant
	 */
	public void setShowFullPage(Boolean value) {
		showFullPage = value;
	}

	/**
	 * Passer le listener au composant. On ne reset la page que s'il y a eu un
	 * changement de listener
	 */
	public void setListener(CompAffectationListener value) {
		if (listener != value) {
			listener = value;
			recIntervention = listener.recIntervention();
			pageRetour = listener.pageRetour();
			resetComponent();
		}
	}

	/**
	 * Test si la personne en cours a le droit d'affecter la DT selectionnee.
	 */
	protected boolean hasDroitsForAction(String typeResponsable, int niveauDroits, String cStructure) {
		boolean ok = false;
		// Teste si la personne est le responsable technique
		if (recIntervention != null) {
			ok = dtSession().dataCenter().activiteBus().isActiviteResponsable(
					recIntervention.numberForKey("actOrdre"), typeResponsable,
					dtSession().connectedUserInfo().noIndividu());
			// Sinon, peut etre administrateur/chef de service
			if ((!ok) && (niveauDroits != -1))
				ok = dtSession().dtUserInfo().hasDroit(niveauDroits, cStructure);
		}
		return ok;
	}

	public void appendToResponse(WOResponse arg0, WOContext arg1) {
		// faut-il rafraichir le displaygroup des structures
		if (shouldRefreshDGStructure) {
			refreshStructureDG();
			shouldRefreshDGStructure = false;
		}
		// faut-il rafraichir le displaygroup des individus
		if (shouldRefreshDGIndividu) {
			if (isInitializing) {
				String prevTypeInt = typeInt;
				// traitement tout particulier au premier chargement :
				// on va preselectionner les responsables de l'activite
				// dans la liste proposee
				setTypeInt(TYPE_INT_ACTIVITES);
				refreshIndividuDG();
				if (individuDisplayGroup.allObjects().count() > 0) {
					individuDisplayGroup.setSelectedObjects(individuDisplayGroup.allObjects());
				}
				setTypeInt(prevTypeInt);
			}
			refreshIndividuDG();
			shouldRefreshDGIndividu = false;
		}
		super.appendToResponse(arg0, arg1);
	}

	/**
   */
	public boolean canAffecter() {
		if (recIntervention == null)
			return false;
		String etat = recIntervention.stringForKey("intEtat");
		if (!(etat.equals(EOEtatDt.ETAT_NON_AFFECTEES) ||
				etat.equals(EOEtatDt.ETAT_ATTENTE_AFFECTER) ||
				etat.equals(EOEtatDt.ETAT_EN_COURS) || etat.equals(EOEtatDt.ETAT_ATTENTE)))
			return false;
		// En plus, il faut etre au moins intervenant avec les droits de validation
		return hasDroitsForAction(
				EOActivites.TYPE_RESP_TECHNIQUE, DTUserInfo.DROIT_INTERVENANT, recIntervention.stringForKey("cStructure"));
	}

	public WOComponent toAffecter() {
		if (!canAffecter()) {
			return reportError("Cette demande ne doit pas &ecirc;tre affect&eacute;e " +
					"ou vous n'avez pas les droits n&eacute;cessaires pour effectuer cette op&eacute;ration");
		} else {
			// lors de la premiere instanciation de la partie affectation
			// on construit le tableau contenant la liste de priorites
			if (prioritesList == null) {
				prioritesList = new NSArray();
				for (int i = 9; i >= -9; i--)
					prioritesList = prioritesList.arrayByAddingObject(new Integer(i));
				prioriteSelection = (Number) prioritesList.objectAtIndex(0);
			}
			// selection des radios par defaut
			if (StringCtrl.isEmpty(typeAff))
				setTypeAff(TYPE_AFF_AFFECTATION);
			if (StringCtrl.isEmpty(typeInt))
				setTypeInt(TYPE_INT_ANNUAIRE);
		}
		return null;
	}

	// le radio groupe de type d'affectations
	public final String TYPE_AFF_AFFECTATION = "A";
	public final String TYPE_AFF_TRANSMISSION = "T";
	public final String TYPE_AFF_SUITEADONNER = "S";
	public String typeAff;

	// la liste des services pour l'affectation
	public WODisplayGroup structureDisplayGroup;
	public CktlRecord structureItem;
	private boolean shouldRefreshDGStructure = true;

	/**
	 * impossible d'overrider WODisplayGroup, on passe par une variable temporaire
	 * pour surcharger la methode WODisplayGroup.setSelectedObjects()
	 */
	public void setStructureSelecteds(NSArray value) {
		structureDisplayGroup.setSelectedObjects(value);
		shouldRefreshDGIndividu = true;
		if (isTransmissionService() && structureDisplayGroup.selectedObjects().count() > 0) {
			// transmission de DT, on instancie le listener des activites
			CktlRecord recStructure = (CktlRecord) structureDisplayGroup.selectedObjects().lastObject();
			activiteListener = new TransmissionActiviteListener(recStructure);
		}
	}

	/**
	 * La selection des structures : - pour l'affectation ou la transmission :
	 * structureDisplayGroup.selectedObjects() - pour la suite a donner :
	 * 
	 * @return
	 */
	public NSArray structureSelecteds() {
		if (isSuiteADonner()) {
			if (structureSelectedItem != null) {
				return new NSArray(structureSelectedItem.nodeRecord);
			} else {
				return new NSArray();
			}
		} else {
			return structureDisplayGroup.selectedObjects();
		}
	}

	/**
	 * L'item TYPE_AFF_SUITEADONNER est-il selectionne
	 */
	public boolean isSuiteADonner() {
		return typeAff.equals(TYPE_AFF_SUITEADONNER);
	}

	private void refreshStructureDG() {
		// raz de la fetchspec du displaygroup
		structureDisplayGroup.queryBindings().setDictionary(new NSDictionary());
		// on clear la selection de service
		structureDisplayGroup.clearSelection();
		// selon le type d'affectation, la liste des services change.
		// CktlRecord userService =
		// serviceBus().structureForCode(dtSession().dtUserInfo().dtServiceCode());
		// le service de destination de la DT en cours
		String cStructureInterventionService = recIntervention.stringForKey("cStructure");
		if (TYPE_AFF_AFFECTATION.equals(typeAff)) {
			structureDisplayGroup.queryBindings().setObjectForKey(cStructureInterventionService, "cStructure");
			// le service des preferences de la personne connectee
			// if (userService != null) {
			// structureDisplayGroup.queryBindings().setObjectForKey(userService.stringForKey("cStructure"),
			// "cStructure");
			// } else {
			// structureDisplayGroup.queryBindings().setObjectForKey("-1",
			// "cStructure");
			// }
		} else if (TYPE_AFF_TRANSMISSION.equals(typeAff)) {
			// tous les services de la DT sauf celui de destination
			structureDisplayGroup.queryBindings().setObjectForKey(new Integer(1), "isGroupesDT");
			// if (userService != null) {
			// structureDisplayGroup.queryBindings().setObjectForKey(userService.stringForKey("cStructure"),
			// "cStructureExcluded");
			// }
			structureDisplayGroup.queryBindings().setObjectForKey(cStructureInterventionService, "cStructureExcluded");
		} else if (TYPE_AFF_SUITEADONNER.equals(typeAff)) {

		}
		// fetcher la base de donnees
		structureDisplayGroup.qualifyDataSource();
		// si 1 seul service, on le selectionne
		if (structureDisplayGroup.allObjects().count() == 1) {
			setStructureSelecteds(structureDisplayGroup.allObjects());
		}
	}

	// le CktlHXBrowser pour la selection des groupes pour suite a donner

	public StructureNode structureSelectedItem;
	private StructureNode structuresZeroItem;
	public NSMutableArray structureSelectedPath;

	// obtenir la racine de l'etablissement (ce n'est plus celle qui
	// a C_STRUCTURE = "1"

	private String cStructureEtablissement;

	private String cStructureEtablissement() {
		if (cStructureEtablissement == null) {
			cStructureEtablissement = serviceBus().structureEtablissement().stringForKey("cStructure");
		}
		return cStructureEtablissement;
	}

	/* === Les methodes pour la gestion du contenu du browseur === */
	public StructureNode structuresZeroItem() {
		if (structuresZeroItem == null ||
				!(structuresZeroItem.getRootCStructure().equals(cStructureEtablissement()))) {
			structuresZeroItem = new StructureNode(null, dtSession());
			structuresZeroItem.setRootCStructure(cStructureEtablissement());
		}
		return structuresZeroItem;
	}

	public String structureSelectionPath() {
		if ((structureSelectedPath == null) || (structureSelectedPath.count() == 0)) {
			return "&lt;Aucune selection&gt;";
		} else {
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < structureSelectedPath.count(); i++) {
				sb.append("&gt;").append(((StructureNode) structureSelectedPath.objectAtIndex(i)).nodeRecord.valueForKey("actLibelle"));
			}
			return sb.toString();
		}
	}

	/**
	 * override du setter pour forcer le refresh de la liste des individus
	 * 
	 * @param value
	 */
	public void setStructureSelectedItem(StructureNode value) {
		structureSelectedItem = value;
		shouldRefreshDGIndividu = true;
	}

	// le radio groupe de type d'affectations
	public final String TYPE_INT_ANNUAIRE = "ANNU";
	public final String TYPE_INT_INTERVENANTS = "SERV";
	public final String TYPE_INT_ACTIVITES = "ACTI";
	public final String TYPE_INT_AFFECTES = "AFF";
	public String typeInt;

	// la liste des individus pour l'affectation
	public WODisplayGroup individuDisplayGroup;
	public CktlRecord individuItem;
	private boolean shouldRefreshDGIndividu;

	/**
	 * forcer les donnees du DisplayGroup a etre rechargees.
	 */
	private void refreshIndividuDG() {
		// on affiche des individus que si une structure est selectionnee
		if (structureSelecteds().count() > 0) {
			CktlRecord recStructure = (CktlRecord) structureSelecteds().lastObject();
			// on recherche des individus que pour l'affectation ou la suite a donner
			// (pour la transmission c'est des activites)
			if (!isTransmissionService()) {
				// on clean le dico de la fetchSpec
				individuDisplayGroup.queryBindings().setDictionary(new NSDictionary());
				if (recStructure != null) {
					if (TYPE_INT_ANNUAIRE.equals(typeInt)) {
						individuDisplayGroup.queryBindings().setObjectForKey(recStructure.stringForKey("cStructure"), "cStructureAnnuaire");
					} else if (TYPE_INT_INTERVENANTS.equals(typeInt)) {
						individuDisplayGroup.queryBindings().setObjectForKey(recStructure.stringForKey("cStructure"), "cStructureDroit");
					} else if (TYPE_INT_ACTIVITES.equals(typeInt)) {
						// Le numero de l'activite (pere) avec les definitions des
						// responsables
						Number actOrdre = recIntervention.numberForKeyPath("toActivites.actResp");
						individuDisplayGroup.queryBindings().setObjectForKey(actOrdre, "actOrdre");
						// on affiche les intervenants et les techniques
						individuDisplayGroup.queryBindings().setObjectForKey("I", "typeResponsable1");
						individuDisplayGroup.queryBindings().setObjectForKey("T", "typeResponsable2");
					} else if (TYPE_INT_AFFECTES.equals(typeInt)) {
						individuDisplayGroup.queryBindings().setObjectForKey(recIntervention.numberForKey("intOrdre"), "intOrdre");
					}
				} else {
					individuDisplayGroup.queryBindings().setObjectForKey("-1", "cStructureAnnuaire");
				}
				individuDisplayGroup.qualifyDataSource(); // fetch
			}
		} else {
			individuDisplayGroup.setObjectArray(new NSArray());
		}
	}

	public void setTypeInt(String value) {
		typeInt = value;
		shouldRefreshDGIndividu = true;
	}

	public void setTypeAff(String value) {
		typeAff = value;
		shouldRefreshDGIndividu = true;
		shouldRefreshDGStructure = true;
	}

	// les listes de priorites

	public NSArray prioritesList;
	public Number prioriteItem;
	public Number prioriteSelection;

	// boutons d'affectation

	/**
	 * Affecter la demande a l'intervenant en cours.
	 */
	public WOComponent doMeLaffecter() {
		errorMessage = null;
		boolean noError = iBus().performAffectation(
				dtUserInfo(),
				new NSArray(recIntervention),
				new NSArray(dtSession().dtUserInfo().noIndividu()),
				dtSession().dtUserInfo().noIndividu(),
				affectationComment(), prioriteSelection);
		if (!noError) {
			reportError("L'affectation à echoué ! ");
			return null;
		} else {
			// logger l'operation d'affectation
			DTLogger.logAffectation(
					recIntervention.valueForKey("intCleService"),
					recIntervention.valueForKey("intOrdre"),
					((CktlRecord) structureSelecteds().lastObject()).stringForKey("cStructure"),
					dtUserInfo().noIndividu(),
					dtUserInfo().login(),
					dtUserInfo().login());

			// ok go sur la liste des DT + refresh
			pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
			return pageRetour;
		}
	}

	/**
	 * Verifier que la suppression de l'intevenant en cours est possible. Si
	 * impossible, alors errorMessage est mis a jour. conditions favorables : - au
	 * moins super intervenant sur le service - responsable technique de
	 * l'activite - individu selectione - il doit rester apres l'operation au
	 * moins 1 individu
	 */
	private boolean canDeleteIntervenant() {
		errorMessage = null;
		// responsable technique de l'activite
		boolean isRespTechAct = dtSession().dataCenter().activiteBus().isActiviteResponsable(
				recIntervention.numberForKey("actOrdre"), EOActivites.TYPE_RESP_TECHNIQUE,
				dtSession().connectedUserInfo().noIndividu());
		if (!isRespTechAct) {
			// super intervenant sur le service
			boolean hasDroitSuperInt = dtSession().dtUserInfo().hasDroit(
					DTUserInfo.DROIT_INTERVENANT_SUPER, recIntervention.stringForKey("cStructure"));
			if (!hasDroitSuperInt) {
				reportError("Vous n'avez pas les droits suffisants pour effectuer cette opération !");
				return false;
			}
		}
		if (individuDisplayGroup.allObjects().count() == 1 ||
				individuDisplayGroup.allObjects().count() == individuDisplayGroup.selectedObjects().count()) {
			reportError("Vous ne pouvez pas enlever le dernier intervenant !");
			return false;
		}
		if (individuDisplayGroup.selectedObjects().count() == 0) {
			return false;
		}

		StringBuffer errTraitements = new StringBuffer();

		for (int i = 0; i < individuDisplayGroup.selectedObjects().count(); i++) {
			CktlRecord recIndividu = (CktlRecord) individuDisplayGroup.selectedObjects().objectAtIndex(i);
			NSArray traitements = iBus().findTraitements(null, null,
					recIntervention.numberForKey("intOrdre"),
					recIndividu.numberForKey("noIndividu"));
			if (traitements.count() > 0) {
				CktlUserInfoDB userInfo = new CktlUserInfoDB(dtSession().dataBus());
				userInfo.compteForPersId(recIndividu.numberForKey("persId"), true);
				errTraitements.append(userInfo.nomEtPrenom());
				errTraitements.append(" a déjà effectué ou prévu des traitements !\n");
			}
		}

		if (errTraitements.length() > 0) {
			reportError(errTraitements.toString());
			return false;
		}

		return true;
	}

	/**
	 * Le panel qui s'affiche si il n'est pas possible de supprimer l'intervenant
	 * en cours. La methode reportError est remise a zero, pour ne pas avoir le
	 * message en page de retour (variable errorMessage)
	 */
	private WOComponent showMessageCantDeleteIntervenant() {
		String prevErrorMessage = errorMessage();
		errorMessage = null;
		return CktlAlertPage.newAlertPageWithCaller(this.parent().parent(),
				"Impossible de supprimer l'intervenant",
				prevErrorMessage, "Revenir", CktlAlertPage.ERROR);
	}

	/**
	 * Se supprimer soi-meme des intervenants et raffecter la DT a la personne
	 * selectionnee
	 */
	public WOComponent doMInterchanger() {
		errorMessage = null;
		// recuperer le nom de la personne selectionnee pour memoire
		StringBuffer nomEtPrenom = null;
		if (individuDisplayGroup.selectedObjects().count() > 0) {
			nomEtPrenom = new StringBuffer();
			for (int i = 0; i < individuDisplayGroup.selectedObjects().count(); i++) {
				CktlRecord recIndividu = (CktlRecord) individuDisplayGroup.selectedObjects().objectAtIndex(i);
				DTUserInfo userInfoInt = new DTUserInfo();
				userInfoInt.compteForPersId(recIndividu.numberForKey("persId"), true);
				// on verifie qu'il ne s'est pas selectionne lui meme
				if (userInfoInt.noIndividu().intValue() == dtUserInfo().noIndividu().intValue()) {
					reportError("S'échanger avec soi-même, c'est pas mal ...<BR>mais ça ne marche pas ! (à moins d'avoir un clône)");
					return null;
				}
				nomEtPrenom.append(userInfoInt.nomEtPrenom());
				if (i < individuDisplayGroup.selectedObjects().count() - 1)
					nomEtPrenom.append(", ");
			}
		} else {
			reportError("Aucun individu selectionné ...");
			return null;
		}

		// verif des droits de suppression sur soi-meme : autoselection puis
		// retribution si erreur
		NSArray prevSelections = individuDisplayGroup.selectedObjects();
		// se selectionner soit meme
		individuDisplayGroup.setSelectedObjects(new NSArray(
				individuBus().individuForNoIndividu(dtUserInfo().noIndividu())));
		boolean errDelete = !canDeleteIntervenant();
		// on retribue la selection precedente
		individuDisplayGroup.setSelectedObjects(prevSelections);
		if (errDelete) {
			return showMessageCantDeleteIntervenant();
		}
		return CktlAlertPage.newAlertPageWithResponder(this, "Echange d'affectation",
				"Voulez-vous réellement supprimer votre affectation,<BR> et ré-affecter à cette DT " +
						(individuDisplayGroup.selectedObjects().count() == 1 ? "l'intervenant" : "les intervenants") +
						" : <BR>" + nomEtPrenom.toString() + " ?",
				"Confirmer", "Annuler", null, CktlAlertPage.QUESTION, new MInterchangerResponder());
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la echange
	 * d'affectation avec un intervenant.
	 */
	public class MInterchangerResponder implements CktlAlertResponder {

		public MInterchangerResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return pageRetour;
			case 1:
				// affecter la personne en cours
				doAffectation();
				if (hasError())
					return pageRetour;

				// suppression de lui meme
				Integer localTransId = iBus().beginECTransaction();
				boolean noError = iBus().deleteIntervenant(localTransId, recIntervention.numberForKey("intOrdre"), dtUserInfo().noIndividu());
				if (noError) {
					iBus().commitECTrancsacition(localTransId);
					shouldRefreshDGIndividu = true;
					// logger l'operation d'affectation
					NSArray prevSelections = individuDisplayGroup.selectedObjects();
					individuDisplayGroup.setSelectedObjects(new NSArray(
							individuBus().individuForNoIndividu(dtUserInfo().noIndividu())));
					traceDeleteMassAffectation();
					// on retribue la selection precedente
					individuDisplayGroup.setSelectedObjects(prevSelections);
					// si lui meme fait parti des supprimes, on met a jour la page de
					// consultation
					// ok go sur la liste des DT + refresh
					pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
					return pageRetour;
				}
			default:
				return null;
			}
		}
	}

	/**
	 * Supprimer l'intervenant selectionne de la liste des intervenants de la
	 * demande.
	 */
	public WOComponent doSupprimerIntervenant() {

		if (!canDeleteIntervenant())
			return showMessageCantDeleteIntervenant();

		// le nom des gens a supprimer
		StringBuffer noms = new StringBuffer();
		DTUserInfo userInfoInt = new DTUserInfo();
		for (int i = 0; i < individuDisplayGroup.selectedObjects().count(); i++) {
			CktlRecord individu = (CktlRecord) individuDisplayGroup.selectedObjects().objectAtIndex(i);
			userInfoInt.compteForPersId(individu.numberForKey("persId"), true);
			noms.append(userInfoInt.nomEtPrenom());
			if (i < individuDisplayGroup.selectedObjects().count() - 1)
				noms.append(", ");
		}

		return CktlAlertPage.newAlertPageWithResponder(this, "Suppression d'affectation",
				"Voulez-vous réellement supprimer l'affectation de cet(ces) intervenant(s) : " + noms.toString() + " ?",
				"Confirmer", "Annuler", null, CktlAlertPage.QUESTION, new DeleteIntervenantResponder());
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la suppression
	 * d'un intervenant.
	 */
	public class DeleteIntervenantResponder implements CktlAlertResponder {

		public DeleteIntervenantResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return pageRetour;
			case 1:
				NSArray nosIndividu = (NSArray) individuDisplayGroup.selectedObjects().valueForKey("noIndividu");
				Integer localTransId = iBus().beginECTransaction();
				boolean noError = true;
				for (int i = 0; i < nosIndividu.count(); i++) {
					Number noIndividu = (Number) nosIndividu.objectAtIndex(i);
					noError = noError && iBus().deleteIntervenant(localTransId, recIntervention.numberForKey("intOrdre"), noIndividu);
				}
				if (noError) {
					iBus().commitECTrancsacition(localTransId);
					shouldRefreshDGIndividu = true;
					// logger l'operation d'affectation
					traceDeleteMassAffectation();
					// si lui meme fait parti des supprimes, on met a jour la page de
					// consultation
					// ok go sur la liste des DT + refresh
					pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
					return pageRetour;
				}
			default:
				return null;
			}
		}
	}

	/**
	 * TODO : faire la transmission au service Validation de la demande. Cette
	 * methode redirige l'appel vers l'affectation, transmission au service ou
	 * pour la suite a donner, en fonction des options.
	 */
	public WOComponent doAffectation() {
		errorMessage = null;
		if (individuDisplayGroup.selectedObjects().count() == 0)
			return null;

		if (typeAff.equals(TYPE_AFF_AFFECTATION)) {
			return affecterAuService();
		} else if (typeAff.equals(TYPE_AFF_TRANSMISSION)) {
			return affecterAuService();
		} else if (typeAff.equals(TYPE_AFF_SUITEADONNER)) {
			return suiteADonner();
		}
		return null;
	}

	/**
	 * Validation de la demande avec l'affectation aux intervenants de service.
	 */
	private WOComponent affecterAuService() {
		boolean noErrors = iBus().performAffectation(dtUserInfo(),
				new NSArray(recIntervention),
				(NSArray) individuDisplayGroup.selectedObjects().valueForKey("noIndividu"),
				dtSession().dtUserInfo().noIndividu(),
				StringCtrl.normalize(affectationComment()), prioriteSelection);

		if (!noErrors) {
			reportError("L'affectation de certaines demandes n'a pas eu lieu !");
			return null;
		} else {
			// logger l'operation d'affectation
			traceMassAffectation();
			// ok go sur la liste des DT + refresh
			pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
			return pageRetour;
		}

	}

	/**
	 * Validation d'affectation avec une suite a donner.
	 */
	private WOComponent suiteADonner() {
		SuiteADonnerResponder responder = new SuiteADonnerResponder();
		return CktlAlertPage.newAlertPageWithResponder(this, "Suite a donner",
				"Le contenu de la demande sera envoyé par mail au destinataire, puis la DT sera cloturée.<br/>" +
						"Confirmez vous que la demande #" + recIntervention.intForKey("intCleService") + " doit être definitivement traitée ?",
				"Confirmer", "Annuler", null, CktlAlertPage.QUESTION, responder);
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour l'affectation
	 * 'suite a donner'
	 */
	public class SuiteADonnerResponder implements CktlAlertResponder {

		public SuiteADonnerResponder() {
			super();
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return pageRetour;
			case 1:
				Integer transactionId = iBus().beginECTransaction();
				// On supprime tous les traitements en cours
				if (iBus().deleteTraitements(transactionId, null,
						EOEtatDt.ETAT_EN_COURS, recIntervention) == -1) {
					iBus().rollbackECTrancsacition(transactionId);
					reportError("La demande n'a pas été traitée (traitements en cours) !");
					return pageRetour;
				}
				// TODO Ajouter la personne en cours comme etant l'intervenant de la DT
				if (!iBus().updateIntervention(dtUserInfo(),
							transactionId, recIntervention.numberForKey("intOrdre"), null, null, null, EOEtatDt.ETAT_TRAITEES, null, null, null, null, null)) {
					iBus().rollbackECTrancsacition(transactionId);
					reportError("La demande n'a pas été traitée (changement d'état) !");
					return pageRetour;
				}
				if (!iBus().commitECTransaction(transactionId)) {
					reportError("La demande n'a pas été traitée (commit) !");
					return pageRetour;
				}
				// envoi des mails
				NSArray destinataires = (NSArray) individuDisplayGroup.selectedObjects().valueForKey("noIndividu");
				if (destinataires.count() > 0) {
					dtSession().mailCenter().reset();
					dtSession().mailCenter().setIntervention(recIntervention, true);
					dtSession().mailCenter().mailSuiteADonner(destinataires);
				}
				traceMassAffectation();
				// ok go sur la liste des DT + refresh
				pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
				return pageRetour;
			default:
				return null;
			}
		}
	}

	/**
	 * Laisser la demande non affectee
	 */
	public WOComponent cancel() {
		pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
		return pageRetour;
	}

	// disponibilites des boutons d'affectation

	public boolean isDisabledBtnAffecter() {
		boolean isDisabled = TYPE_INT_AFFECTES.equals(typeInt);
		return isDisabled;
	}

	public boolean isDisabledBtnMeLAffecter() {
		boolean isDisabled = false;
		return isDisabled;
	}

	public boolean isDisabledBtnInterchanger() {
		boolean isDisabled = false;
		// l'utilisateur ne peut s'interchanger que s'il est affecte a la DT
		isDisabled = !recIntervention.isIntervenant(dtUserInfo().noIndividu());
		return isDisabled;
	}

	public boolean isDisabledBtnSupprimer() {
		boolean isDisabled = !TYPE_INT_AFFECTES.equals(typeInt);
		return isDisabled;
	}

	/**
	 * On autorise pas la transmission tant qu'une activite feuille n'a pas ete
	 * selectionnee
	 */
	public boolean isDisabledBtnTransmission() {
		return !activiteListener.activiteDispoLeafYN();
	}

	/**
	 * L'item TYPE_AFF_TRANSMISSION est-il selectionne
	 */
	public boolean isTransmissionService() {
		return typeAff.equals(TYPE_AFF_TRANSMISSION);
	}

	/**
	 * On affiche le selectionneur d'activites que si le listener a ete instanci�
	 */
	public boolean shouldShowTransmissionActiviteComponent() {
		return activiteListener != null;
	}

	private CktlRecord rootActivite;
	public TransmissionActiviteListener activiteListener;

	/**
	 * La classe listener de gestion du sous composant SelectActivite.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	public class TransmissionActiviteListener extends SelectActiviteListener {

		private CktlRecord recService;

		public TransmissionActiviteListener(CktlRecord aRecService) {
			super();
			recService = aRecService;
		}

		public void doAfterActiviteSelectedItem() {
		}

		protected CktlRecord recVActivite() {
			String cStructure = recService.stringForKey("cStructure");
			rootActivite = activiteBus().findVActivite(new Integer("-" + cStructure));
			return rootActivite;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return "formAffectation";
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
			return false;
		}

	}

	/**
	 * Bouton de transmission suite a donner sous le composant des activites.
	 * 
	 * @return
	 */
	public WOComponent doTransmettre() {
		if (recIntervention.arrayForKey("tosTraitement") != null && recIntervention.arrayForKey("tosTraitement").count() > 0) {
			reportError("Impossible de transmettre une DT qui a deja des traitements de saisis ...");
		} else if (transmettreDtForActivite(activiteBus().findActivite(
				activiteListener.browserRecord().numberForKey("actOrdre"), rootActivite.stringForKey("cStructure")))) {
			return pageRetour;
		}
		return null;
	}

	/**
	 * Appel depuis la page SelectActivite, apres la selection de la nouvelle
	 * activite de la DT a transmettre
	 */
	private boolean transmettreDtForActivite(CktlRecord recActivite) {
		CktlRecord recService = (CktlRecord) structureSelecteds().objectAtIndex(0);
		// Annuler l'opreation si aucune activite n'est selectionnee
		if (recActivite == null) {
			reportError("Aucune activite n'a ete selectionnée !");
			return false;
		}
		// TODO la gestion de commentaire
		dtSession().dataBus().beginTransaction();
		// memoriser l'ancien numero et l'ancien service
		Number prevIntCleService = recIntervention.numberForKey("intCleService");
		String prevCStructure = recIntervention.stringForKey("cStructure");
		Number intCleService = iBus().updateServiceIntervention(null,
				recIntervention.numberForKey("intOrdre"), recService.stringForKey("cStructure"));
		if (intCleService == null) {
			dtApp().dataBus().rollbackTransaction();
			reportError("La transmission n'a pas eu lieu !");
			return false;
		} else {
			Number actOrdre = recActivite.numberForKey("actOrdre");
			// Mettre a jour la reference vers la nouvelle activite
			iBus().updateIntervention(dtUserInfo(),
						null,
						recIntervention.numberForKey("intOrdre"), null, null, null, EOEtatDt.ETAT_NON_VALIDEES, activiteBus().findActivitePathString(actOrdre, true, ";"), actOrdre, null, null, null);
			// desaffecter les gens qui y seraient affectes
			iBus().deleteAllIntervenants(null, recIntervention.numberForKey("intOrdre"));
			// On diffuse le mail sur la nouvelle activite
			dtSession().mailCenter().reset();
			dtSession().mailCenter().setIntervention(recIntervention, true);
			dtSession().mailCenter().mailTransmettreDTAuService(recService, intCleService);
			// ok refresh de la liste des DT
			pageRetour.takeValueForKey(Boolean.TRUE, "shouldRefresh");
			// log
			traceTransmission(prevIntCleService, prevCStructure);
			return true;
		}
	}

	/* logs */

	private void traceDeleteMassAffectation() {
		// logger l'operation d'affectation
		NSArray logins = new NSArray();
		DTUserInfo userInfoInt = new DTUserInfo();
		for (int i = 0; i < individuDisplayGroup.selectedObjects().count(); i++) {
			CktlRecord individu = (CktlRecord) individuDisplayGroup.selectedObjects().objectAtIndex(i);
			userInfoInt.compteForPersId(individu.numberForKey("persId"), true);
			logins = logins.arrayByAddingObject(userInfoInt.login());
		}
		DTLogger.logDeleteAffectation(
				recIntervention.valueForKey("intCleService"),
				recIntervention.valueForKey("intOrdre"),
				((CktlRecord) structureSelecteds().lastObject()).stringForKey("cStructure"),
				dtUserInfo().noIndividu(),
				dtUserInfo().login(),
				individuDisplayGroup.selectedObjects().valueForKey("noIndividu"),
				logins);
	}

	private void traceMassAffectation() {
		NSArray logins = new NSArray();
		DTUserInfo userInfoInt = new DTUserInfo();
		for (int i = 0; i < individuDisplayGroup.selectedObjects().count(); i++) {
			CktlRecord individu = (CktlRecord) individuDisplayGroup.selectedObjects().objectAtIndex(i);
			userInfoInt.compteForPersId(individu.numberForKey("persId"), true);
			logins = logins.arrayByAddingObject(userInfoInt.login());
		}
		// logger l'operation d'affectation
		DTLogger.logAffectation(
				recIntervention.valueForKey("intCleService"),
				recIntervention.valueForKey("intOrdre"),
				((CktlRecord) structureSelecteds().lastObject()).stringForKey("cStructure"),
				dtUserInfo().noIndividu(),
				dtUserInfo().login(),
				logins);
	}

	private void traceTransmission(Number prevIntCleService, String prevCStructure) {
		// logger l'operation de transmission
		DTLogger.logTransmission(
				prevIntCleService,
				prevCStructure,
				recIntervention.valueForKey("intCleService"),
				recIntervention.valueForKey("intOrdre"),
				((CktlRecord) structureSelecteds().lastObject()).stringForKey("cStructure"),
				dtUserInfo().noIndividu(),
				dtUserInfo().login());
	}

	/* gestion d'erreur */

	private String errorMessage;

	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	public String errorMessage() {
		return errorMessage;
	}

	public boolean hasError() {
		return (errorMessage != null);
	}

	/**
	 * Enregistre le message d'erreur.
	 */
	private WOComponent reportError(String message) {
		errorMessage = message;
		return null;
	}

	// ==+ les bus de donnees +==//

	private DTInterventionBus iBus() {
		return dtSession().dataCenter().interventionBus();
	}

	private DTIndividuBus individuBus() {
		return dtSession().dataCenter().individuBus();
	}

	private DTActiviteBus activiteBus() {
		return dtSession().dataCenter().activiteBus();
	}

	private DTServiceBus serviceBus() {
		return dtSession().dataCenter().serviceBus();
	}

	// ==+ le commentaire interne +==//

	private String affectationComment;

	public void setAffectationComment(String comment) {
		affectationComment = comment;
	}

	public String affectationComment() {
		return affectationComment;
	}

} // memoriser l'ancien numero
