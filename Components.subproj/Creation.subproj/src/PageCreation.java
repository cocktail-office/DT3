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
import java.util.Vector;

import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gere la page de creation des demandes de travaux.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class PageCreation
		extends DTWebComponent {
	// Les balise indiquant la position dans la page de creation. Elles sont
	// utilisee pour se repositionner dans une page.
	public final String PosDTCreateContent = "DTCreateContent";
	// public final String PosDTCreateError = "DTCreateError";

	/* -- Infos generales sur le demandeur -- */
	private CktlUserInfo demandeurInfo;
	public boolean canChangerDemandeur;
	/** L'enregistrement representant les informations de contact de demandeur. */
	private CktlRecord recContactDemandeur;
	/** Le code de service d'appartenance du demandeur. */
	private String codeServiceDemandeu;
	/** L'identifiant du service du demandeur. */
	private Integer persIdServiceDemandeur;
	private Integer fouOrdreServiceDemandeur;
	public String contactLibelle;

	/* -- La visibilite (les etapes) de la creation -- */
	public boolean enterAllDataYN;
	public boolean swapDefaultYN;
	public boolean swapCarteYN;
	public boolean swapReproYN;
	public boolean swapMessageYN;
	public boolean swapInstallComposantYN;
	public boolean swapInstallMaterielYN;
	public boolean swapInstallPosteCompletYN;
	public boolean swapInstallPosteComplet2YN;
	public boolean swapIndicateurYN;
	public boolean swapReferentFonctionnelYN;
	public boolean swapAutorisationRecrutementEtudiantYN;
	public boolean swapSupportHandicapYN;
	public boolean swapCelluleGeomatiqueYN;
	public boolean swapLogicielYN;

	/* ====== Les services ====== */
	/** @TypeInfo VActivites */
	public CktlRecord recVActivitesSelectedItem;
	/* ====== Les activites ====== */
	// /** L'ordre de tri des activites */
	// private NSArray activiteSort;

	private Vector<A_CreationSwapView> swapComponents;

	// /** Numero individu du demandeur. */
	// private Number noIndividuDemandeur;
	// /** Code personnel du demandeur. */
	// private Number persIdDemandeur;
	// /** L'etat de la demande nouvellement creee. */
	// private String newDtEtat;
	/** L'identifiant de l'emplacement dans la page (anchor) */
	private String positionInPage;
	private String serviceDestCode;
	private SelectPersonneListener personneListener;
	private CreationContactSelector contactListener;
	private String errorMessage;
	public boolean errorContact;
	public boolean errorSyncContact;
	public boolean errorFactService;

	// popup pour la selection du service de facturation
	// plutot que le contact
	public NSArray<CktlRecord> factServiceList;
	public CktlRecord factServiceItem;
	public CktlRecord factServiceSelected;
	public boolean isOnlyFactService;
	public String factServiceResponsablesLibelle;

	// gestion des activites
	public CreationActiviteListener activiteListener;

	// affiche le navigateur d'activité
	public boolean showSelectActivite;

	/** Le nom en toute lettre du service destinataire */
	public String serviceLibelle;
	/** Le nom en toute lettre de l'activité choisie */
	public String activiteLibelle;

	// test ajaxautocomplete
	public AjaxSelectActiviteCtrl ajaxSelectActiviteCtrl;

	public PageCreation(WOContext context) {
		super(context);
		// instancier le gestionaire d'activites
		activiteListener = new CreationActiviteListener();
		// [LRAppTasks] : @CktlLog.trace(@"session ID : "+dtSession().sessionID());
		swapComponents = new Vector<A_CreationSwapView>(0, 1);
		errorContact = false;
		errorSyncContact = false;
		errorFactService = false;
		resetComponent();
		positionInPage = StringCtrl.emptyString();
	}

	/**
	 * Effectuer les traitements survenus si le service de destination est change.
	 */
	private void resetService() {
		EOQualifier condition =
				DTDataBus.newCondition(EOVActivites.ACT_ORDRE_KEY + "=" + dtSession().getCurrectServiceDest());
		NSArray activitesForService = cktlApp.dataBus().fetchArray(EOVActivites.ENTITY_NAME, condition, null);
		// selection de la premiere activite
		if (activitesForService.count() > 0) {
			recVActivitesSelectedItem = (CktlRecord) activitesForService.objectAtIndex(0);
			ajaxSelectActiviteCtrl = new AjaxSelectActiviteCtrl(
					dtSession(),
					(EOGroupesDt) dtDataCenter().serviceBus().getGroupesDt(((EOVActivites) recVActivitesSelectedItem).cStructure()));
		} else {
			recVActivitesSelectedItem = null;
		}
		// on affiche la vue de selection rapide de contact pour
		// la facture que pour les service utilisant les prestations
		serviceDestCode = Integer.toString(-dtSession().getCurrectServiceDest());
		isOnlyFactService = serviceBus().isServiceDTPrestation(serviceDestCode);
		// raz de la structure de facturation
		if (isOnlyFactService && (factServiceList == null || factServiceList.count() == 0))
			factServiceList = serviceBus().allServicesFouValideInterne();
		if (factServiceSelected == null)
			loadDefaultFactServiceSelected();
		// liberer l'activite selectionnee
		activiteListener.clearActiviteSelection();
		// affiche le bon swap pour le service
		resetSwapVisibility();
	}

	/**
	 * Annule toutes les selections et initialise le composant pour la creation
	 * d'une nouvelle demande.
	 */
	public void resetComponent() {
		// Infos sur l'utilisateur
		// [LRAppTasks] : @CktlLog.trace(@"session ID : "+dtSession().sessionID());
		takeInfoDemandeur(dtSession().connectedUserInfo());
		canChangerDemandeur =
				(dtSession().connectedUserInfo().userStatus() == CktlUserInfo.STATUS_PERSONNEL);
		newDtEtat = StringCtrl.emptyString();
		factServiceSelected = null;
		//
		resetSwapVisibility();
		setErrorMessage(null);
		// par defaut, on affiche le navigateur d'activités
		showSelectActivite = true;
	}

	/**
   * 
   */
	public void awake() {
		serviceDestCode = Integer.toString(-dtSession().getCurrectServiceDest());
	}

	public boolean hasErrors() {
		return (errorContact || errorSyncContact || errorFactService || hasMainError());
	}

	private boolean hasMainError() {
		return (StringCtrl.normalize(getErrorMessage()).length() > 0);
	}

	public final String getErrorMessage() {
		return errorMessage;
	}

	public final void setErrorMessage(String message) {
		errorMessage = message;
		if (!StringCtrl.isEmpty(errorMessage)) {
			/*
			 * CktlLog.setLevel(CktlLog.LEVEL_DEBUG);
			 * CktlLog.trace("setErrorMessage()", true);
			 * CktlLog.setLevel(CktlLog.LEVEL_BASIC);
			 */
			dtSession().addSimpleErrorMessage("Erreur", errorMessage);
		}
	}

	public void setPositionInPage(String anchor) {
		positionInPage = anchor;
	}

	public String positionInPage() {
		return StringCtrl.normalize(positionInPage);
	}

	public boolean hasExitLink() {
		return (dtSession().getMode() == Session.MODE_DA_CREAT);
	}

	public String exitLinkURL() {
		StringBuffer u = new StringBuffer(dtApp().getApplicationURL(context()));
		if (dtSession().getMode() == Session.MODE_DA_CREAT) {
			if (u.charAt(u.length() - 1) != '/')
				u.append("/");
			u.append("wa/DACreation?").append(DACreation.SERVICE_CODE_KEY);
			u.append("=").append(serviceDestCode);
		}
		// [LRAppTasks] : @CktlLog.trace(@"Exit link : "+u.toString());
		return u.toString();
	}

	/**
	 * Teste si le contact du demandeur actuellement selectionne est complet et
	 * correct.
	 */
	private void checkContactDemandeur() {
		// Verifier si le contact est bien correct
		int level = dtApp().config().intForKey("APP_FORCE_CONTACTS_LEVEL");
		// [LRAppTasks] : @CktlLog.trace(@"Level : "+level);
		// [LRAppTasks] :
		// @CktlLog.trace(@"demandeurInfo().noIndividu() : "+demandeurInfo().noIndividu());
		// [LRAppTasks] :
		// @CktlLog.trace(@"connectedUserInfo().noIndividu() : "+connectedUserInfo().noIndividu());
		// [LRAppTasks] :
		// @CktlLog.trace(@"isContactComplet : "+dtDataCenter().contactsBus().isContactComplet(recContactDemandeur.numberForKey("ctOrdre"),
		// null));
		if ((level == 2) ||
				((level == 1) &&
					(demandeurInfo().noIndividu().intValue() == connectedUserInfo().noIndividu().intValue()))) {
			// Le contact doit etre complet pour toute DT
			// si c'est juste pour le service de facturation, on teste pas
			if (!isOnlyFactService) {
				errorContact = !contactsBus().isContactComplet(
						recContactDemandeur.numberForKey("ctOrdre"), null);
				setErrorMessage(contactsBus().getErrorMessage());
				// raz du message d'erreur
				contactsBus().setErrorMessage(null);
			} else
				errorFactService = (factServiceSelected == null);
		}
	}

	/**
	 * Teste si le contact du demandeur est synchronise avec les informations de
	 * l'annuaire
	 */
	private void checkSynchroContactAnnuaireDemandeur() {
		/*
		 * int level = dtApp().config().intForKey("APP_FORCE_CONTACTS_LEVEL"); if
		 * ((level == 2) || ((level == 1) &&
		 * (demandeurInfo().noIndividu().intValue() ==
		 * connectedUserInfo().noIndividu().intValue()))) { // si c'est juste pour
		 * le service de facturation, on teste pas if (!isOnlyFactService) {
		 * NSDictionary dicoDiff = contactsBus().getDicoDiffContactAnnuaire(
		 * recContactDemandeur.numberForKey("noIndividu"),
		 * recContactDemandeur.numberForKey("ctOrdre")); errorSyncContact =
		 * (dicoDiff != null); } }
		 */
	}

	/**
	 * Recuperer la valeur par defaut du service de facturation depuis les
	 * preferences
	 */
	private void loadDefaultFactServiceSelected() {
		CktlRecord rec = dtSession().dataCenter().preferencesBus().findPrefDefaut(demandeurInfo().noIndividu());
		// [LRAppTasks] : @CktlLog.trace(@"defaults : "+rec);
		if (rec != null) {
			codeServiceDemandeu = rec.stringForKey("prfCStructure");
			if (codeServiceDemandeu != null) {
				setFactServiceSelected(serviceBus().structureForCode(codeServiceDemandeu));
			}
		}
	}

	/**
	 * Definit le contact en version simplifiee : la selection se fait par le
	 * service, on n'enregistrera pas de contact, mais seulement le code de la
	 * structure dans les infos de facturation
	 */
	public void setFactServiceSelected(CktlRecord recService) {
		factServiceSelected = recService;
		factServiceResponsablesLibelle = null;
		if (recService != null) {
			codeServiceDemandeu = recService.stringForKey("cStructure");
			persIdServiceDemandeur = new Integer(recService.numberForKey("persId").intValue());
			fouOrdreServiceDemandeur = jefyBus().getFouOrdreForStructure(codeServiceDemandeu);
			factServiceResponsablesLibelle = serviceBus().getResponsablesPrestationDescription(codeServiceDemandeu);
		}
	}

	/**
	 * Definit le nouvel contact pour le demandeur actuellement selectionne.
	 */
	public void setContactDemandeur(EOContact recContact) {
		recContactDemandeur = recContact;
		codeServiceDemandeu = null;
		persIdServiceDemandeur = null;
		contactLibelle = null;
		errorContact = false;
		errorSyncContact = false;
		// On essaie de retrouver dans les contacts
		if (recContactDemandeur != null) {
			// Memoriser les nouvelles informations sur le contact
			codeServiceDemandeu = recContactDemandeur.stringForKey("cStructService");
			// La descrioption du contact, pour ne pas refetcher chaque fois
			contactLibelle = DTStringCtrl.formatLocalInfoForHTML(
					contactsBus().getContactDescription(
							recContactDemandeur.numberForKey("ctOrdre"), "\n"));
			// Verifier si le contact est bien correct
			checkContactDemandeur();
			// TODO ne verifier la synchro pas a chaque fois
			checkSynchroContactAnnuaireDemandeur();
		}
		// Sinon, on cherche dans le service par defaut
		if ((codeServiceDemandeu == null) &&
				(dtUserInfo().persId().intValue() == demandeurInfo.persId().intValue())) {
			codeServiceDemandeu = dtUserInfo().codeService();
		}
		// On reprend l'identifiant du service du demandeur
		if (codeServiceDemandeu != null) {
			CktlRecord rec = dtDataCenter().serviceBus().structureForCode(codeServiceDemandeu);
			if (rec != null)
				persIdServiceDemandeur = (Integer) rec.valueForKey("persId");

		}
		// On initialise la description de la localisation
		// [LRAppTasks] : @CktlLog.trace(@"errorContact : "+errorContact);
	}

	/**
   * 
   */
	private void takeInfoDemandeur(CktlUserInfo userInfo) {
		demandeurInfo = userInfo;
		setContactDemandeur(contactsBus().findDefaultContact(
						userInfo.noIndividu(), true));
	}

	public void addSwap(A_CreationSwapView newSwap) {
		if (!swapComponents.contains(newSwap)) {
			swapComponents.addElement(newSwap);
		}
	}

	public boolean serviceConcerneChoisisYN() {
		return (recVActivitesSelectedItem != null);
	}

	private int resetSwapVisibility() {
		int swapViewID = -1;
		if (activiteListener.activiteDispoLeafYN()) {
			enterAllDataYN = false;
			// [LRAppTasks] :
			// @CktlLog.trace(@"nodeRecord entity : "+activiteSelectedItem.nodeRecord.entityName());
			swapViewID = activiteListener.getActiviteSelectedItem().nodeRecord.toActPref().actSwapView().intValue();
			swapDefaultYN = (swapViewID == I_Swap.SWAP_VIEW_DEFAULT_ID);
			swapCarteYN = (swapViewID == I_Swap.SWAP_VIEW_COMPTE_ID);
			swapReproYN = (swapViewID == I_Swap.SWAP_VIEW_REPRO_ID);
			swapMessageYN = (swapViewID == I_Swap.SWAP_VIEW_MESSAGE_ID);
			swapInstallComposantYN = (swapViewID == I_Swap.SWAP_VIEW_INSTALL_COMPOSANT_ID);
			swapInstallMaterielYN = (swapViewID == I_Swap.SWAP_VIEW_INSTALL_MATERIEL_ID);
			swapInstallPosteCompletYN = (swapViewID == I_Swap.SWAP_VIEW_INSTALL_POSTE_COMPLET_ID);
			swapInstallPosteComplet2YN = (swapViewID == I_Swap.SWAP_VIEW_INSTALL_POSTE_COMPLET2_ID);
			swapIndicateurYN = (swapViewID == I_Swap.SWAP_VIEW_INDICATEUR_ID);
			swapReferentFonctionnelYN = (swapViewID == I_Swap.SWAP_VIEW_REFERENT_FONCTIONNEL_ID);
			swapAutorisationRecrutementEtudiantYN = (swapViewID == I_Swap.SWAP_VIEW_AUT_RECR_ETUDIANT_ID);
			swapSupportHandicapYN = (swapViewID == I_Swap.SWAP_VIEW_SUPPORT_HANDICAP_ID);
			swapCelluleGeomatiqueYN = (swapViewID == I_Swap.SWAP_VIEW_CELLULE_GEOMATIQUE_ID);
			swapLogicielYN = (swapViewID == I_Swap.SWAP_VIEW_LOGICIEL_ID);
			setPositionInPage(PosDTCreateContent);
		} else {
			enterAllDataYN = true;
			swapDefaultYN = false;
			swapCarteYN = false;
			swapReproYN = false;
			swapMessageYN = false;
			swapInstallComposantYN = false;
			swapInstallMaterielYN = false;
			swapInstallPosteCompletYN = false;
			swapInstallPosteComplet2YN = false;
			swapIndicateurYN = false;
			swapReferentFonctionnelYN = false;
			swapAutorisationRecrutementEtudiantYN = false;
			swapSupportHandicapYN = false;
			swapCelluleGeomatiqueYN = false;
			swapLogicielYN = false;
			setPositionInPage(null);
		}
		return swapViewID;
	}

	public WOComponent changeDemandeur() {
		// [LRAppTasks] : @CktlLog.trace(@"personneListener : "+personneListener());
		return SelectPersonne.getNewPage(personneListener(),
							"Indiquez la personne pour le compte de laquelle la demande est faite",
							demandeurInfo.noIndividu(), true);
	}

	public WOComponent choisirContact() {
		SelectContact page = (SelectContact) dtSession().getSavedPageWithName("SelectContact");
		page.setData(demandeurInfo.noIndividu(), null);
		page.setListener(contactListener());
		return page;
	}

	public CktlUserInfo demandeurInfo() {
		return demandeurInfo;
	}

	public EOContact recContactDemandeur() {
		return (EOContact) recContactDemandeur;
	}

	/**
	 * Retourne le code de batiment du demandeur ou <i>null</i> si le batiment ne
	 * peut pas etre determine.
	 */
	public String batimentDemandeurCode() {
		if (DTDataBus.isNullValue(recContactDemandeur.valueForKey("cLocal")))
			return null;
		else
			return recContactDemandeur.stringForKey("cLocal");
	}

	/**
	 * Retourne le numero de bureau du demandeur ou <i>null</i> si le bureau ne
	 * peut pas etre determine.
	 */
	public Number bureauDemandeurNumero() {
		if (DTDataBus.isNullValue(recContactDemandeur.valueForKey("salNumero")))
			return null;
		else
			return recContactDemandeur.numberForKey("salNumero");
	}

	public String demandeurNomMail() {
		StringBuffer sb = new StringBuffer(demandeurInfo.nomEtPrenom());
		if (StringCtrl.isEmpty(demandeurInfo.email()))
			sb.append(" &lt;email inconnu&gt;");
		else
			sb.append(" &lt;").append(demandeurInfo.email()).append("&gt;");
		return sb.toString();
	}

	public String motsCles() {
		if ((activiteListener.activiteSelectedPath == null) || (activiteListener.activiteSelectedPath.count() == 0)) {
			return StringCtrl.emptyString();
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < activiteListener.activiteSelectedPath.count(); i++) {
				sb.append(((ActivitesNode) activiteListener.activiteSelectedPath.objectAtIndex(i)).nodeRecord.valueForKey("actLibelle")).append(";");
			}
			return sb.toString();
		}
	}

	/**
	 * Retourne l'etat pour la demande nouvellement cree.
	 * 
	 * Si aucun etat n'a ete force, alors on retourne "V" (non-validee) s'il
	 * existent des responsables fonctionnels, "N" (non-affectee) dans le cas
	 * contraire.
	 * 
	 * <p>
	 * L'etat peut etre explicitement precice a l'aide de la methode
	 * <code>setNewEtatDT</code>.
	 */
	public String newDtEtat() {
		if (!StringCtrl.isEmpty(newDtEtat))
			return newDtEtat;
		if (activiteListener.browserRecord() != null)
			return dtDataCenter().activiteBus().newEtatCodeForActivite(
					activiteListener.browserRecord().numberForKey("actOrdre"));
		else
			return EOEtatDt.ETAT_NON_AFFECTEES;
	}

	private String newDtEtat;

	/**
	 * Definit explicitement l'etat de la nouvelle demande.
	 */
	public void setNewDtEtat(String etat) {
		newDtEtat = etat;
	}

	/**
	 * Retourne le code (<code>cStructure</code>) du service de demandeur.
	 */
	public String codeServiceDemandeur() {
		return codeServiceDemandeu;
	}

	/**
	 * Retourne l'identifiant (<code>persId</code>) du service de demandeur.
	 */
	public Integer persIdServiceDemandeur() {
		return persIdServiceDemandeur;
	}

	/**
	 * Retourne l'identifiant (<code>fouOrdre</code>) du service de demandeur.
	 */
	public Integer fouOrdreServiceDemandeur() {
		return fouOrdreServiceDemandeur;
	}

	/**
	 * Retourne l'identifiant de demandeur.
	 */
	public Number persIdDemandeur() {
		return demandeurInfo.persId();
	}

	public Integer demandeurContactCode() {
		if (recContactDemandeur != null)
			return (Integer) recContactDemandeur.valueForKey("ctOrdre");
		else
			return null;
	}

	// private String getNewDTEtat(Number actOrdre) {
	// if (actOrdre != null) {
	// DTActiviteBus aBus = dtSession().dataCenter().activiteBus();
	// if (aBus.countResponsables(actOrdre,
	// DTActiviteBus.TYPE_RESP_FONCTIONNEL) > 0)
	// return DTEtatBus.ETAT_NON_VALIDEES;
	// }
	// return DTEtatBus.ETAT_NON_AFFECTEES;
	// }

	public void fillDataDictionary(NSMutableDictionary leDico) {
		// Verifier si le contact du demandeur est bien renseigne
		errorContact = false;
		errorSyncContact = false;
		errorFactService = false;
		checkContactDemandeur();
		// [LRAppTasks] : @CktlLog.trace(@"ErrorMessage : "+errorMessage());
		if (!hasErrors()) {
			// Remplire le dictionnaire principal
			/*
			 * leDico = interventionBus().newDefaultDataDictionnaryNewIntervention(
			 * activiteListener.browserRecord().valueForKey("actOrdre"),
			 * activiteListener.browserRecord().valueForKey("cStructure"),
			 * demandeurInfo.noIndividu(), demandeurInfo.email(), newDtEtat(),
			 * motsCles(), codeServiceDemandeur(), batimentDemandeurCode(),
			 * ctOrdreDemandeur, "W");
			 */
			leDico.setObjectForKey(activiteListener.browserRecord().valueForKey("actOrdre"), "actOrdre");
			leDico.setObjectForKey(activiteListener.browserRecord().valueForKey("cStructure"), "cStructure");
			leDico.setObjectForKey(demandeurInfo.noIndividu(), "intNoIndConcerne");
			leDico.setObjectForKey(dtSession().connectedUserInfo().noIndividu(), "intNoIndAppelant");
			leDico.setObjectForKey(demandeurInfo.email(), "mailIndConcerne"); // Utilise
																																				// uniquement
																																				// pour
																																				// le
																																				// mail
			leDico.setObjectForKey(dtSession().connectedUserInfo().email(), "mailIndAppelant"); // idem
			leDico.setObjectForKey(StringCtrl.emptyString(), "intCommentaireInterne");
			leDico.setObjectForKey(CktlDataBus.nullValue(), "intDateButoir");
			leDico.setObjectForKey(DateCtrl.now(), "intDateCreation");
			leDico.setObjectForKey(newDtEtat(), "intEtat");
			leDico.setObjectForKey(motsCles(), "intMotsClefs");
			leDico.setObjectForKey(new Integer(9), "intPriorite");

			Object value = codeServiceDemandeur();
			leDico.setObjectForKey(value == null ? "-1" : value, "intServiceConcerne");
			value = batimentDemandeurCode();
			leDico.setObjectForKey(value == null ? DTDataBus.nullValue() : value, "cLocal");
			if (recContactDemandeur() != null && !isOnlyFactService)
				leDico.setObjectForKey(recContactDemandeur().valueForKey("ctOrdre"), "ctOrdre");
			else
				leDico.setObjectForKey(DTDataBus.nullValue(), "ctOrdre");

			leDico.setObjectForKey("W", "modCode");
		} else {
			if (errorContact)
				setErrorMessage("Le contact correct du demandeur doit être saisi.");
			else if (errorFactService)
				setErrorMessage("Le service de facturation doit être saisi.");
			else if (errorSyncContact)
				setErrorMessage("Le contact n'est pas synchronisé avec l'annuaire.");
			setPositionInPage(null);
		}
	}

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection des personnes.
	 */
	private SelectPersonneListener personneListener() {
		if (personneListener == null)
			personneListener = new CreationPersonneSelector();
		return personneListener;
	}

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection du contact de demandeur.
	 */
	private CreationContactSelector contactListener() {
		if (contactListener == null)
			contactListener = new CreationContactSelector();
		return contactListener;
	}

	/**
	 * Implemente les methode necessaires pour communiquer avec la page de la
	 * selection des personnes. Permet de choisir la personne pour le compte de
	 * laquelle la demande est faite.
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 */
	private class CreationPersonneSelector implements SelectPersonneListener {
		/*
		 * @see PersonneSelectListener#select(java.lang.Number, java.lang.Number)
		 */
		public WOComponent select(Number persId) {
			CktlUserInfo demandeurInfo = new CktlUserInfoDB(dtSession().dataBus());
			demandeurInfo.compteForPersId(persId, true);
			takeInfoDemandeur(demandeurInfo);
			setPositionInPage(null);
			return PageCreation.this;
		}

		/*
		 * @see PersonneSelectListener#cancel()
		 */
		public WOComponent cancel() {
			return PageCreation.this;
		}

		/*
		 * @see PersonneSelectListener#context()
		 */
		public WOContext getContext() {
			return context();
		}
	}

	/**
	 * 
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 */
	private class CreationContactSelector implements SelectContactListener {

		/*
		 * @see SelectContactListener#select()
		 */
		public WOComponent select(Number ctOrdre) {
			setContactDemandeur(contactsBus().findContact(ctOrdre));
			setPositionInPage(null);
			return PageCreation.this;
		}

		/*
		 * @see SelectContactListener#cancel()
		 */
		public WOComponent cancel() {
			return PageCreation.this;
		}

	}

	/* == surcharge de setter == */

	/**
	 * Detecter le changement du service destinataire.
	 */
	public void setCodeServiceSelectedItem(String cStructure) {
		recVActivitesSelectedItem = serviceBus().serviceForCode(cStructure);
		if (recVActivitesSelectedItem == null ||
				((Number) recVActivitesSelectedItem.valueForKey("actOrdre")).intValue() != dtSession().getCurrectServiceDest()) {
			resetService();
			activiteListener = new CreationActiviteListener();
		} else {
			serviceLibelle = recVActivitesSelectedItem.stringForKeyPath("toStructureUlr.llStructure");
		}
	}

	/**
	 * Permettre d'afficher ou masquer le browser des activités à partir d'une
	 * autre page
	 * 
	 * @param show
	 */
	public void setShowSelectActivite(boolean show) {
		showSelectActivite = show;
	}

	/* -- GESTION DU BROWSER DES ACTIVITES -- */

	/**
	 * La classe listener de gestion du sous composant SelectActivite.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	public class CreationActiviteListener extends SelectActiviteListener {

		/**
		 * Apres la selection d'une activite, on met a jour la swap view si besoin,
		 * et on efface les eventuels msg d'erreur.
		 */
		public void doAfterActiviteSelectedItem() {
			resetSwapVisibility();
			setErrorMessage(null);
		}

		protected CktlRecord recVActivite() {
			return recVActivitesSelectedItem;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return "FormCreationDT";
		}

		/**
		 * On remet l'ancre en haut apres une recherche
		 */
		public void doAfterSearchActivite() {
			setPositionInPage(null);
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

		/** selon le profil */
		public boolean showUnderscoredActivite() {
			return interventionBus().canViewActiviteUnderscore(recVActivite().stringForKey("cStructure"), dtUserInfo().noIndividu());
		}

		@Override
		public boolean showActivitesFavoritesDemandeur() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean showActivitesFavoritesIntervenant() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	/* == rajouts de methodes utilisee par le composant CompAffectation == */

	/**
	 * Methode appelee lorsque l'affectation s'est bien passee
	 */
	public void setShouldRefresh(boolean value) {
		resetComponent();
	}

	/**
	 * L'affichage du libelle du service dans le popup contact rapide
	 */
	public String factServiceLibelle() {
		if (factServiceItem == null) {
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			String libelle = factServiceItem.stringNormalizedForKey("llStructure");
			if (libelle.length() > 50)
				sb.append(libelle.substring(0, 50)).append("...");
			else
				sb.append(libelle);
			sb.append(" (").append(factServiceItem.valueForKey("lcStructurePere")).append(")");
			return sb.toString();
		}
	}

	/**
	 * Bus de donn�es des services
	 */
	private DTServiceBus serviceBus() {
		return dtSession().dataCenter().serviceBus();
	}

	/**
	 * Bus de donn�es des donnees budgetaires
	 */
	private DTJefyBus jefyBus() {
		return dtSession().dataCenter().jefyBus();
	}

	/**
	 * Bus de donn�es des contacts
	 */
	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	/**
	 * Bus de données des interventions
	 * 
	 * @return
	 */
	private DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	public final String getServiceDestCode() {
		return serviceDestCode;
	}

}
