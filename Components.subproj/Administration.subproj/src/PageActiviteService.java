import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Ecran de gestion d'ajout et modification de service DT
 * 
 * @author ctarade
 */

public class PageActiviteService
		extends A_AdministrationPageActivite {

	// la liste des service de l'annuaire pour creation d'un groupe DT
	public NSArray serviceList;
	public CktlRecord serviceItem;
	public NSArray serviceSelecteds;

	// liste des VLAN
	public NSArray vlanList;
	public CktlRecord vlanItem;
	public NSArray vlanSelecteds;

	// la liste des structures pour visibilite
	public NSArray structureList;
	public CktlRecord structureItem;

	// code CSS d'affichage pour police proportionnels dans le combo
	private final static String CSS_PROPORTIONNAL = "option {font-family: monospace}";

	/** coche afficher dans la liste */
	public boolean isAfficherDansLaListe = true;
	/** nombre de lignes d'activites */
	public Integer nbLignesBrowserActivites;
	/** position dans la liste */
	public Integer positionDansLaListe;
	/** email du service */
	public String emailService;
	/** point d'entree SAM pour le dialogue avec les utilisateurs */
	public String emailServiceSam;

	/** grosse erreur qui ne permet pas d'afficher le composant */
	private boolean hasInitError;

	/** en modification, l'enregistrement sur lequel on travaille */
	private CktlRecord recGroupesDt;

	public PageActiviteService(WOContext context) {
		super(context);
	}

	/**
	 * Utiliser le composant pour l'ajout d'un nouveau service. Le parametre
	 * <code>parentRecord</code> est ignoré dans ce cas
	 */
	public void initForAjout(SwapActivites caller, EOVActivites parentRecord) {
		curMode = MODE_AJOUT;
		refrechListeServices(null);
		recGroupesDt = null;
		positionDansLaListe = new Integer(activiteBus().findFreeGrpPosition());
		// 8 lignes affichees par defaut
		nbLignesBrowserActivites = new Integer(8);
		emailService = StringCtrl.emptyString();
		emailServiceSam = StringCtrl.emptyString();
		swapSelected = I_Swap.NOUVELLE_ACTIVITE_SWAP_DEFAUT;
		vlanSelecteds = new NSArray(vlanList);
		swapActiviteCaller = caller;
	}

	/**
	 * Utiliser le composant pour la modification d'un service existant.
	 * 
	 * @param recVActivites
	 *          : l'enregistrement a modifier
	 * 
	 *          Retourne un message d'erreur si erreur
	 */
	public void initForModif(EOVActivites recVActivites, EOVActivites parentRecord) {
		curMode = MODE_MODIF;
		int found = refrechListeServices(recVActivites.stringForKey("cStructure"));
		// pas de groupe trouve
		if (found == 0) {
			errorMessage = "Le groupe \"" + recVActivites.valueForKeyPath("actLibelle") +
					"\" avec le code \"" + recVActivites.valueForKey("cStructure") +
					"\"\nn'a pas été trouvé dans l'Annuaire !";
			hasInitError = true;
		}
		// groupe mais pas service (juste un warning)
		if (found == 1) {
			errorMessage = "Le groupe \"" + recVActivites.valueForKeyPath("actLibelle") +
					"\" avec le code \"" + recVActivites.valueForKey("cStructure") +
					"\"\nn'est pas enregistré en tant que groupe DT dans l'Annuaire !";
		}
		recGroupesDt = activiteBus().findGroupeWithCode(recVActivites.stringForKey("cStructure"));
		isAfficherDansLaListe = recGroupesDt.valueForKey("grpAffichable").equals("O");
		positionDansLaListe = new Integer(recGroupesDt.intForKey("grpPosition"));
		nbLignesBrowserActivites = new Integer(recGroupesDt.intForKey("grpNbLigBrowserAct"));
		emailService = recGroupesDt.stringNormalizedForKey("grpEmail");
		emailServiceSam = recGroupesDt.stringNormalizedForKey(EOGroupesDt.GRP_EMAIL_SAM_KEY);
		swapSelected = (String) swapList.objectAtIndex(recGroupesDt.intForKey("grpSwapView"));
		doReseauVisibiliteSelection(recGroupesDt.stringForKey("grpVisibilite"));
		doStructureVisibiliteFillArray(recGroupesDt.stringForKey("grpVisibiliteStructure"));
	}

	/**
	 * 
	 */
	public void appendToResponse(WOResponse woresponse, WOContext wocontext) {
		super.appendToResponse(woresponse, wocontext);
		// css
		addTextCss(woresponse, CSS_PROPORTIONNAL);
	}

	public void initComponent() {
		super.initComponent();
		// TODO selection swap

		// les vlans
		vlanList = dtDataCenter().genericBus().findVlans(Boolean.TRUE, null);

		hasInitError = false;
	}

	/**
	 * Effectue l'enregistrement dans la base.
	 * 
	 * @return <code>true</code> si pas d'erreur
	 */
	private boolean confirmSave() {
		errorMessage = StringCtrl.emptyString();
		CktlRecord rec = (CktlRecord) serviceSelecteds.lastObject();
		String affichable = isAfficherDansLaListe ? "O" : "N";
		if (curMode == MODE_AJOUT) {
			recGroupesDt = activiteBus().addGroupe(null, rec.stringForKey("cStructure"), affichable,
					StringCtrl.normalize(emailService), positionDansLaListe, nbLignesBrowserActivites,
					new Integer(swapList.indexOfIdenticalObject(swapSelected)),
					getReseauVisibiliteCodes(), getStructureVisibiliteCodes(),
					emailServiceSam);
			// Sauvegarde de donnees
			if (recGroupesDt == null) {
				errorMessage = "l'ajout d'un nouveau service a échoué !";
			}
		} else if (curMode == MODE_MODIF) {
			if (activiteBus().updateGroupe(null,
						recGroupesDt.stringForKey("cStructure"), affichable,
						StringCtrl.normalize(emailService), positionDansLaListe, nbLignesBrowserActivites,
							new Integer(swapList.indexOfIdenticalObject(swapSelected)),
							getReseauVisibiliteCodes(), getStructureVisibiliteCodes(), Boolean.TRUE,
							emailServiceSam)) {
			} else {
				errorMessage = "Les modifications n'ont pas pu être enregistrées !";
			}
		}
		return StringCtrl.isEmpty(errorMessage);
	}

	// display

	public String serviceDisplay() {
		String display = DTStringCtrl.fitToSize(serviceItem.stringForKey("llStructure"), 40, ".") + " | " +
				DTStringCtrl.fitToSize(serviceItem.stringForKey("lcStructure"), 8, ".") + " | " + serviceItem.stringForKey("cStructure");
		return DTStringCtrl.replace(display, " ", "&nbsp;");
	}

	public String structureVisibiliteDisplay() {
		String display = structureItem.stringForKey("lcStructure") + " ( " + structureItem.stringForKey("cStructure") + ")";
		return DTStringCtrl.replace(display, " ", "&nbsp;");
	}

	// actions

	/**
	 * Valider le formuler et faire les actions correspondantes
	 */
	public WOComponent ok() {
		if (valider()) {
			// demande de confirmation si email vide (normalement c'est plus le cas)
			if (StringCtrl.isEmpty(emailService)) {
				return CktlAlertPage.newAlertPageWithResponder(
						dtSession().getSavedPageWithName(PageAdministration.class.getName()),
						"Demande de confirmation",
						"L'adresse mail n'est pas indiquée !\nVoulez-vous quand même enregistrer les données ?",
						"Oui", "Non", null, CktlAlertPage.QUESTION, new ConfirmSaveNoMailResponder(this));
			} else {
				// email saisi, on enregistre
				if (confirmSave()) {
					return goSwapActivite();
				} else {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la confirmation
	 * d'enregistrement si le mail est vide
	 */
	public class ConfirmSaveNoMailResponder implements CktlAlertResponder {
		WOComponent caller;

		public ConfirmSaveNoMailResponder(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				if (confirmSave()) {
					return goSwapActivite();
				} else {
					return caller;
				}
			default:
				return goSwapActivite();
			}
		}
	}

	/**
	 * Retourner a la page de gestion des activites. On effectue la selection du
	 * service ajoute si besoin.
	 */
	private WOComponent goSwapActivite() {
		if (curMode == MODE_AJOUT && recGroupesDt != null) {
			// forcer le refresh de la page de retour et la selection
			swapActiviteCaller.initServiceList();
			swapActiviteCaller.setServiceSelectedCStructure(
					recGroupesDt.stringForKey("cStructure"));
		}
		return dtSession().getSavedPageWithName(PageAdministration.class.getName());
	}

	/**
	 * Annuler la saisie
	 */
	public WOComponent annuler() {
		return goSwapActivite();
	}

	// disponibilite des elements du formulaire

	public boolean isDisabledBrwsServices() {
		return curMode == MODE_MODIF;
	}

	// methodes internes

	/**
	 * Rafraichir la liste des services, et retourne si le parametre
	 * <code>codeStructure</code> est indiqué l'etat du service a selectionner : 0
	 * - rien, 1 - structure generale, 2 - groupe DT
	 */
	private int refrechListeServices(String codeStructure) {
		int found = 0;
		// liste de toutes structures typees DT de l'annuaire
		serviceList = serviceBus().allStructuresDT();
		// selection du premier
		if (serviceList.count() > 0) {
			serviceSelecteds = new NSArray(serviceList.objectAtIndex(0));
		}
		if (codeStructure != null) {
			// On cherche d'abord dans les structures de type DT
			NSArray objects = serviceBus().allStructuresDT();
			serviceSelecteds = new NSArray();
			for (int i = 0; i < objects.count(); i++) {
				if (((CktlRecord) objects.objectAtIndex(i)).stringForKey("cStructure").equals(codeStructure)) {
					serviceSelecteds = serviceSelecteds.arrayByAddingObject(
							objects.objectAtIndex(i));
					found = 2;
					break;
				}
			}
			// Si le groupe n'est pas un service, alors on cherche parmis tous les
			// groupes de l'annuaire
			if (found == 0) {
				found = (serviceBus().structureForCode(codeStructure) != null ? 1 : 0);
			}
		}
		return found;
	}

	/**
	 * Valide la saisie des informations sur un service.
	 */
	protected boolean valider() {
		errorMessage = StringCtrl.emptyString();
		// On verifie la selection des valeurs
		// Service...
		CktlRecord serviceSelected = (serviceSelecteds != null && serviceSelecteds.count() > 0 ?
				(CktlRecord) serviceSelecteds.lastObject() : null);
		if (serviceSelected == null) {
			errorMessage = "Vous devez choisir un service !";
			return false;
		}

		// On oblige la saisie d'un email pour le service
		if (StringCtrl.isEmpty(emailService)) {
			errorMessage = "L'adresse du service est obligatoire !";
			return false;
		}

		// format de cette adresse
		if (!StringCtrl.isEmailValid(emailService)) {
			errorMessage = "Le format de l'adresse email du service est invalide !";
			return false;
		}

		// Position...
		int position = (positionDansLaListe != null ? positionDansLaListe.intValue() : -1);
		if (position != -1) {
			if (activiteBus().findGroupesWithPosition(position, serviceSelected.stringForKey("cStructure")).count() > 0) {
				position = -1;
			}
		}
		if (position == -1) {
			errorMessage = "La position dans la liste est incorrectement indiquée ou\nelle est déjà utilisée !";
			return false;
		}

		// Nombre de lignes...
		int nbLignes = (nbLignesBrowserActivites != null || nbLignesBrowserActivites.intValue() <= 0 ? nbLignesBrowserActivites.intValue() : -1);
		if (nbLignes == -1) {
			errorMessage = "Le nombre de lignes incorrectement indiqué !";
			return false;
		}

		// on exige la saisie correcte du mail SAM s'il est entré
		if (!StringCtrl.isEmpty(emailServiceSam) && !StringCtrl.isEmailValid(emailServiceSam)) {
			errorMessage = "Le format de l'adresse email du service SAM est invalide !";
			return false;
		}

		// Sinon, on peut sauvegarder les donnees
		return true;
	}

	/**
	 * Transformer la selection des VLANS de visibilte en une chaine prete a
	 * enregistrer
	 */
	private String getReseauVisibiliteCodes() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < vlanSelecteds.count(); i++) {
			CktlRecord recVlan = (CktlRecord) vlanSelecteds.objectAtIndex(i);
			if (sb.length() == 0)
				sb.append("|");
			sb.append(recVlan.stringForKey("cVlan")).append("|");
		}
		if (sb.length() > 0)
			sb.append("WEB|");
		return sb.toString();
	}

	/**
	 * Transformer une chaine enregistree dans la base en la selection de vlans
	 * dans la liste
	 * 
	 * @param strVisibilite
	 */
	private void doReseauVisibiliteSelection(String value) {
		String strVisibilite = value;
		if (strVisibilite == null) {
			strVisibilite = StringCtrl.emptyString();
		}
		NSArray codes = NSArray.componentsSeparatedByString(
				strVisibilite.trim(), "|");
		vlanSelecteds = new NSArray();
		for (int i = 0; i < codes.count(); i++) {
			String code = (String) codes.objectAtIndex(i);
			EOQualifier qualVlan = CktlDataBus.newCondition("cVlan = '" + code + "'");
			NSArray recsVlan = EOQualifier.filteredArrayWithQualifier(vlanList, qualVlan);
			if (recsVlan.count() > 0) {
				vlanSelecteds = vlanSelecteds.arrayByAddingObject(recsVlan.lastObject());
			}
		}
	}

	/**
	 * Transformer la selection des structure de visibilte en une chaine prete a
	 * enregistrer
	 */
	private String getStructureVisibiliteCodes() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; structureList != null && i < structureList.count(); i++) {
			CktlRecord revStructure = (CktlRecord) structureList.objectAtIndex(i);
			if (sb.length() == 0)
				sb.append("|");
			sb.append(revStructure.stringForKey("cStructure")).append("|");
		}
		return sb.toString();
	}

	/**
	 * Transformer une chaine enregistree dans la base en une liste de structure
	 * 
	 * @param strVisibiliteStructure
	 */
	private void doStructureVisibiliteFillArray(String value) {
		String strVisibilite = value;
		if (strVisibilite == null) {
			strVisibilite = StringCtrl.emptyString();
		}
		NSArray codes = NSArray.componentsSeparatedByString(
				strVisibilite.trim(), "|");
		structureList = new NSArray();
		for (int i = 0; i < codes.count(); i++) {
			String code = (String) codes.objectAtIndex(i);
			CktlRecord recStructure = serviceBus().structureForCode(code);
			if (recStructure != null) {
				structureList = structureList.arrayByAddingObject(recStructure);
			}
		}
	}

	// selection de structure visibilite

	// actions

	/**
	 * Effacer une structure autorisee a voir
	 */
	public WOContext supprimerStructureVisibilite() {
		NSMutableArray structureListMutable = new NSMutableArray(structureList);
		structureListMutable.removeIdenticalObject(structureItem);
		structureList = structureListMutable.immutableClone();
		return null;
	}

	/**
	 * Ajouter une structure autorisee a voir
	 */
	public WOComponent ajouterStructureVisibilite() {
		return SelectPersonne.getNewPage(new NewResponsableListener(this),
				"Indiquez la structure qui aura la visibilite sur le service", null, false);
	}

	/**
	 * Implemente les methode necessaires pour communiquer avec la page de la
	 * selection des personnes. Permet de choisir la personne a qui affecter le
	 * droit.
	 * 
	 * @author ctarade
	 */
	private class NewResponsableListener implements SelectPersonneListener {

		private WOComponent caller;

		public NewResponsableListener(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		/*
		 * @see PersonneSelectListener#select(java.lang.Number, java.lang.Number)
		 */
		public WOComponent select(Number persId) {
			if (persId != null) {
				structureList = structureList.arrayByAddingObject(serviceBus().structureForPersId(persId));
			}
			return caller;
		}

		/*
		 * @see PersonneSelectListener#cancel()
		 */
		public WOComponent cancel() {
			return caller;
		}

		/*
		 * @see PersonneSelectListener#context()
		 */
		public WOContext getContext() {
			return context();
		}
	}

	// getters

	public boolean hasInitError() {
		return hasInitError;
	}

}