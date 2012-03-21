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

import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOPrefDefaut;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Regroupe les methodes et les attributs partages par les controleurs des vues
 * de saisie des demandes. Le controeur de chaque vue doit heriter de cette
 * classe.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public abstract class A_CreationSwapView
		extends DTWebComponent
		implements I_Swap {

	// binding
	public SelectActiviteListener activiteListener;
	private A_FooterSwapCtrl footerCtrl;

	/** La reference vers la page principale dont cette vue fait partie. */
	private PageCreation parentPage;
	/** Les informations a enregistrer avec une nouvelle demande. */
	protected NSMutableDictionary<String, Object> saveDataDico;
	/** Le code interne de la demande nouvellement creee. */
	private Number newDtIntOrdre;
	/** Le code de service de la demande nouvellement creee. */
	private Number newDtCleService;
	/** Le code de la ligne budgetaire si elle est selectionnee */
	private Number newDtOrgId;
	/** Le code de type de credit selectionne avec une ligne budgetaire */
	private String newDtTcdCode;
	/** Le code de devis, s'il a ete cree pour la nouvelle demande */
	private Number newDtPrestId;
	private Number newDtPrestNumero;

	/**
	 * Cree une nouvelle instance d'une vue de creation des demandes.
	 */
	public A_CreationSwapView(WOContext context) {
		super(context);
		parentPage = (PageCreation) context().page();
		parentPage.addSwap(this);
		saveDataDico = new NSMutableDictionary<String, Object>();
		initView();
		if (shouldProcessDataInfin()) {
			loadInfinDefaults();
		}
	}

	public void awake() {
		clearViewErrors();
		super.awake();
	}

	/**
	 * Reinitialise la vue. Cette methode est appelee juste apres la creation de
	 * composant.
	 */
	public abstract void initView();

	/**
	 * Remplit le dictionnaire correspondant a la nouvelle demande en cours de
	 * creation. Cette methode doit replire le dictionnaire
	 * <code>saveDataDico</code> par les valeurs pour la nouvelle demande.
	 * 
	 * <p>
	 * Cette methode doit egallement effectuer la validation des donnees et
	 * afficher des messages d'erreurs en cas de besoin.
	 * </p>
	 * 
	 * @return <code>true</code> si pas d'erreur, <code>false</code> sinon
	 * 
	 * @see #saveDataDico
	 */
	protected abstract boolean fillDataDictionary();

	/**
	 * La vérification locale des erreurs.
	 * 
	 * @return
	 */
	public abstract boolean hasErrors();

	/**
	 * Declenche l'envoi des messages email suite a la creation d'une nouvelle
	 * demande. La parametre <code>filesURL</code> donne la liste des fichiers
	 * associes a la demande, s'ils etaient donnes. Ces fichiers sont
	 * prealeablement enregistres sur le serveur GEDFS.
	 */
	protected abstract void sendMail(NSArray<String> filesURL);

	/**
	 * Declenche la procedure de creation de devis, si necessaire. Cette methode
	 * est appelee lors de l'enregistrement d'une nouvelle demande (methode
	 * <code>validerDemande</code>).
	 * 
	 * @return <code>true</code> : si le devis genere si succes,
	 *         <code>false</code> pas de creation. Si une erreur est survenue,
	 *         <code>false</code> est retourne et <code>mainError()</code> est mis
	 *         a jour.
	 */
	protected abstract boolean createDevis();

	/**
	 * Retourne le code de la categorie du serveur GEDFS pour l'enregistrement des
	 * documents associes aux demandes creees via cette vue.
	 */
	protected abstract String getCategorieGED();

	/**
	 * Retourne la liste des chemins des fichiers attaches. Retourne une liste
	 * vide si aucun fichier attache n'est donnes.
	 */
	protected abstract NSArray<String> attachedFilePaths();

	/**
	 * Supprime toutes les erreurs enregistrees dans la vue de creation.
	 */
	protected abstract void clearViewErrors();

	/**
	 * Indique si les erreurs de sauvegarde des documents attaches peuvent etre
	 * ignorees. Si c'est le cas, une nouvelle demande peut etre creee meme sans
	 * les documents attaches. Sinon, la creation d'une nouvelle demande est
	 * interdite.
	 * 
	 * <p>
	 * La sauvegarde d'un nouveau document peut echouer a cause des erreurs de
	 * communication avec le serveur GEDFS, par exemple.
	 * </p>
	 */
	protected abstract boolean ignoreFileSaveErrors();

	/**
	 * Retourne un message d'avertissement qui peut etre affiche suite a la
	 * creation d'une nouvelle demande. Ce message sera ajoute aux autres
	 * informations affiches dans la page affichant les resultat de la creation
	 * d'une demande : numero, devis, etc...
	 * 
	 * <p>
	 * La methode doit retourner null, si aucun avertissement ne doit pas etre
	 * affiche. Le message doit etre en format HTML.
	 * </p>
	 */
	protected abstract String getPostCreateWarning();

	/**
	 * Indique s'il faut traiter les informations financieres. Si oui, alors ces
	 * dernieres seront indiquées dans le sujet du mail de création mais aussi
	 * sauvegardées dans la table INTERVENTION_INFIN
	 */
	public abstract boolean shouldProcessDataInfin();

	/**
	 * Retourne la reference vers la principale page de creation des demandes. La
	 * vue de creation des demandes ne fait qu'une partie de cette page.
	 */
	protected PageCreation parentPage() {
		return parentPage;
	}

	protected DTPrestaBusWeb pieBus() {
		// On reinitialise les infos sur le demandeur au cas
		// ou les informations du contact change
		Number persIdDemadeur = (parentPage().isOnlyFactService ?
				dtUserInfo().persId() :
				parentPage.recContactDemandeur().numberForKey("persId"));
		dtSession().pieBus().setUser(
				new Integer(persIdDemadeur.intValue()),
				parentPage.fouOrdreServiceDemandeur());
		return dtSession().pieBus();
	}

	protected DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	protected DTJefyBus jefyBus() {
		return dtSession().dataCenter().jefyBus();
	}

	protected DTServiceBus serviceBus() {
		return dtSession().dataCenter().serviceBus();
	}

	protected String getTitleForDocument(Number cleDT) {
		return "Document DT (" + cleDT + ")";
	}

	protected void resetView() {
		saveDataDico.removeAllObjects();
		newDtIntOrdre = newDtCleService = null;
		setNewDtPrestId(null);
		setNewDtPrestNumero(null);
		clearErrorMessage();
	}

	public final void clearErrorMessage() {
		setMainError(null);
	}

	public final void setMainError(String message) {
		parentPage.setErrorMessage(message);
	}

	public final String mainError() {
		return parentPage.getErrorMessage();
	}

	public final boolean hasMainErrors() {
		return parentPage.hasErrors();
	}

	/**
	 * Annule toutes les selections et initialise le composant pour la creation
	 * d'une nouvelle demande.
	 */
	public final void resetForNewDt() {
		// resultDataDico.removeAllObjects();
		resetView();
		parentPage.resetComponent();
	}

	/**
	 * Retourne l'enregistrement correspondant au dernier noeud selectionne dans
	 * le browser des activites.
	 */
	public CktlRecord browserRecord() {
		// return parentPage.activiteListener.browserRecord();
		return activiteListener.browserRecord();
	}

	/**
	 * Retourne l'etat pour la demande nouvellement cree. "V" (non-validee) s'il
	 * existent des responsables fonctionnels, "N" (non-affectee) dans le cas
	 * contraire.
	 * 
	 * <p>
	 * L'etat peut etre explicitement precice a l'aide de la methode
	 * <code>setNewEtatDT</code>.
	 */
	public String newDtEtat() {
		return parentPage.newDtEtat();
	}

	/**
	 * Definit explicitement l'etat de la nouvelle demande.
	 */
	public void setNewDtEtat(String etat) {
		parentPage.setNewDtEtat(etat);
	}

	/**
	 * Retourne le code interne de la demande nouvellement creee ou <i>null</i> si
	 * la demande n'est pas encore enregistree.
	 */
	public Number newDtIntOrdre() {
		return newDtIntOrdre;
	}

	/**
	 * Retourne le numero de la demande nouvellement creee pour le service
	 * concerne. Retourne <i>null</i> si la demande n'est pas encore enregistree.
	 */
	public Number newDtCleService() {
		return newDtCleService;
	}

	/**
	 * Definit le code de la ligne budgetaire pour la demande de travaux en cours
	 * de creation.
	 */
	public void setNewDtOrgId(Number orgId) {
		newDtOrgId = orgId;
	}

	/**
	 * Retourne le code de la ligne budgetaire. Retourne <em>null</em> si aucune
	 * ligne n'a ete selectionnee.
	 */
	public Number newDtOrgId() {
		return newDtOrgId;
	}

	/**
	 * Definit le type de credit selectionne avec une ligne budgetaire.
	 */
	public void setNewDtTcdCode(String tcdCode) {
		newDtTcdCode = tcdCode;
	}

	/**
	 * Retourne le code du type de credit selectionne avec une ligne budgetaire.
	 * Retourne <em>nulls</em> si aucune ligne n'est selectionnee.
	 */
	public String newDtTcdCode() {
		return newDtTcdCode;
	}

	/**
	 * Retourne le code de devis cree avec la nouvelle demande. Retourne null si
	 * aucun devis n'est cree (numero reel du devis)
	 */
	public Number newDtPrestNumero() {
		return newDtPrestNumero;
	}

	/**
	 * Retourne le code de devis cree avec la nouvelle demande. Retourne null si
	 * aucun devis n'est cree (cle primaire du devis)
	 */
	public Number newDtPrestId() {
		return newDtPrestId;
	}

	/**
	 * Definit le code de devis cree pour une nouvelle demande (numero reel du
	 * devis)
	 */
	public void setNewDtPrestNumero(Integer prestNumero) {
		newDtPrestNumero = prestNumero;
	}

	/**
	 * Definit le code de devis cree pour une nouvelle demande (cle primaire du
	 * devis)
	 */
	public void setNewDtPrestId(Integer prestId) {
		newDtPrestId = prestId;
	}

	/**
	 * Retourne le code du service payeur si on ne passe pas par le contact
	 */
	public String newCStructure() {
		return parentPage().codeServiceDemandeur();
	}

	public Number newFouOrdre() {
		return parentPage.fouOrdreServiceDemandeur();
	}

	/**
	 * Retourne le code du service de demandeur courant.
	 */
	public String codeServiceDemandeur() {
		return parentPage().codeServiceDemandeur();
	}

	/**
	 * Retourne l'identifiant du demandeur courant.
	 */
	public Number persIdDemandeur() {
		return parentPage().persIdDemandeur();
	}

	// gestion des lignes budgetaires

	/**
	 * Indique si un ligne budgetaire peut etre selectionnee.
	 */
	public boolean canSelectLigneBud() {
		// Le niveau doit etre au moins 1
		return (dtSession().checkJefyLBudSupport() > 0);
	}

	/**
	 * Teste si une ligne budgetaire a ete selectionnee. Pour ceci, et la ligne
	 * budgetaire et son type de credit doivent etre indiques.
	 */
	public boolean isLigneBudSelected() {
		return isLigneBudSelected(newDtOrgId(), newDtTcdCode());
	}

	/**
	 * Teste si une ligne budgetaire a ete selectionnee. Pour ceci, et la ligne
	 * budgetaire et son type de credit doivent etre indiques.
	 */
	public boolean isLigneBudSelected(Number orgId, String tcdCode) {
		return orgId != null && tcdCode != null;
	}

	/**
	 * Retourne la description courte de la ligne budgetaire et de son type de
	 * credit selectionnes.
	 */
	public String getLigneBudDescription() {
		return getLigneBudDescription(newDtOrgId(), newDtTcdCode());
	}

	/**
	 * 
	 * @param orgId
	 * @param tcdCode
	 * @return
	 */
	protected String getLigneBudDescription(
			Number orgId, String tcdCode) {
		int level = dtSession().checkJefyLBudSupport();
		if (level < 1) {
			// La selection interdite dans la configuration
			return "&lt;La s&eacute;lection des lignes budg&eacute;taires indisponible&gt;";
		} else {
			// La selection autorisee, mais rien n'a ete selectionne
			if (orgId == null)
				return "&lt;aucune ligne budg&eacute;taire s&eacute;lectionn&eacute;e&gt;";
			else
				return dtSession().dataCenter().jefyBus().getLigneBudDescription(
						null, orgId, tcdCode, false, null);
		}
	}

	//

	/**
	 * Indique si le service Web Prestations peut etre utilise.
	 */
	public boolean canUsePIE() {
		return pieBus().checkPIEService();
	}

	/**
	 * A redefinir dans les sous classes, si les donnees sont enregistrees
	 * autrement
	 */
	public void saveData() {
		clearErrorMessage();
		newDtCleService = newDtIntOrdre = null;
		// [LRAppTasks] : @CktlLog.trace(@"saveDataDico : "+saveDataDico);
		interventionBus().beginTransaction();
		NSDictionary result = interventionBus().addIntervention(saveDataDico);
		// [LRAppTasks] : @CktlLog.trace(@"result : "+result);
		if (result == null) {
			setMainError(interventionBus().getErrorMessage());
			interventionBus().rollbackTransaction();
		} else {
			newDtCleService = (Number) result.objectForKey("intCleService");
			newDtIntOrdre = (Number) result.objectForKey("intOrdre");
			interventionBus().commitTransaction();
		}
	}

	/**
	 * Enregistre les informations financieres relatives a la demande en cours de
	 * creation. La demande doit deja etre enregistree et la methode
	 * <code>newDtIntOrdre</code> doit retourner son code.
	 */
	public void saveInfinData(Integer transId) {
		// On met a jour seulement s'il le faut
		// [LRAppTasks] : @CktlLog.trace(@"newDtLBudOrdre : "+newDtLBudOrdre());
		// [LRAppTasks] : @CktlLog.trace(@"newDtTcdCode : "+newDtTcdCode());
		// [LRAppTasks] : @CktlLog.trace(@"newDtDstCode : "+newDtDstCode());
		// [LRAppTasks] : @CktlLog.trace(@"newDtDevOrdre : "+newDtDevOrdre());

		Number lolfId = null;
		if (getDestinationLolfCtrl().lolfId() != null) {
			lolfId = getDestinationLolfCtrl().lolfId();
		}

		if (newDtOrgId() != null ||
				newDtTcdCode() != null ||
				newDtPrestId() != null ||
				newDtPrestNumero() != null ||
				lolfId != null) {
			interventionBus().setInterventionInfin(
					transId,
					newDtIntOrdre(), null, newDtTcdCode(),
					null, null, null,
					newCStructure(), newFouOrdre(), newDtOrgId(),
					lolfId, newDtPrestId(), newDtPrestNumero());
			if (interventionBus().hasError())
				setMainError("Erreur lors de sauvegarde des données financières (saveInfinData)<br>" +
						interventionBus().getErrorMessage());
		}
	}

	/**
	 * Enregistre les definitions et les selections par defaut pour l'utilisateur
	 * actuellement connecte. Par exemple, cette methode peut enregistrer la
	 * selection des lignes budgetaires, des contacts.
	 */
	protected void saveDefaults() {
		// [LRAppTasks] : @CktlLog.trace(@null);
		// On ne mets a jour les selections que si le demandeur fait la demande
		// pour lui-meme
		if (parentPage().demandeurInfo().noIndividu().intValue() == dtSession().connectedUserInfo().noIndividu().intValue()) {
			Number lolfId = null;
			if (getDestinationLolfCtrl() != null) {
				lolfId = getDestinationLolfCtrl().lolfId();
			}
			dtSession().dataCenter().preferencesBus().updatePrefDefaut(
					null,
					parentPage().demandeurInfo().noIndividu(), parentPage().demandeurContactCode(),
					null,
					newDtTcdCode(),
					null,
					parentPage().codeServiceDemandeur(),
					newDtOrgId(),
					lolfId);
		}
	}

	/**
	 * Charger les preferences financieres par defaut de l'utilisateur connecté
	 */
	private void loadInfinDefaults() {
		EOPrefDefaut rec = (EOPrefDefaut)
				dtSession().dataCenter().preferencesBus().findPrefDefaut(
						parentPage().demandeurInfo().noIndividu());
		// [LRAppTasks] : @CktlLog.trace(@"defaults : "+rec);
		if (rec != null) {
			setNewDtOrgId(rec.prfOrgId());
			setNewDtTcdCode(rec.prfTcdCode());
			getDestinationLolfCtrl().setLolfId(rec.prfLolfId());
		}
	}

	private NSArray<String> saveFichierAttache(Number cleDT) {
		clearErrorMessage();
		// On ne fait rien si aucun fichier attache
		if (NSArrayCtrl.isEmpty(attachedFilePaths()))
			return null;
		// Tester si le GEDFS est dispo
		if (!cktlApp.useGed()) {
			setMainError("Le support du service GEDFS n'est pas activé dans l'application (APP_USE_GEDFS)!");
			return null;
		}
		NSMutableArray<String> documentsURL = new NSMutableArray<String>();
		int noDocument;

		// Enregistrer tous les fichiers
		for (int i = 0; i < attachedFilePaths().count(); i++) {
			noDocument = dtSession().gedBus().saveDocument(
					(String) attachedFilePaths().objectAtIndex(i),
					getTitleForDocument(cleDT),
					getCategorieGED());
			if (noDocument == -1) {
				setMainError("Le fichier attaché n'a pa pu être enregistré (GEDClient) !<br>" +
						"Erreur : " + dtSession().gedBus().gedMessage());
				return null;
			}
			// Sauvegarde donnees
			if (!interventionBus().addDocumentDt(null, new Integer(noDocument), cleDT, null, null)) {
				setMainError("Le fichier attaché n'a pa pu être enregistré (base de données) <br>" +
						"Erreur : " + interventionBus().getErrorMessage());
				return null;
			}
			// [LRAppTasks] :
			// @CktlLog.trace(@"Attached file URL : "+dtSession().gedBus().gedDescription().reference);
			documentsURL.addObject(dtSession().gedBus().gedDescription().reference);
		}
		return documentsURL;
	}

	protected void formatAndSendMail(NSArray<String> attachedFilesURL) {
		clearErrorMessage();
		if (newDtIntOrdre != null) {
			saveDataDico.takeValueForKey(newDtCleService(), "intCleService");
			saveDataDico.takeValueForKey(newDtIntOrdre(), "intOrdre");
			dtSession().mailCenter().reset();
			dtSession().mailCenter().setIntervention(saveDataDico, false);
			dtSession().mailCenter().setInterventionFiles(attachedFilesURL);
			if (!dtSession().mailCenter().mailCreerDT()) {
				setMainError("Le mail au service concerné n'a pas pu être envoyé !");
			}
		}
	}

	/**
	 * Valide la creation d'une nouvelle DT Repro.
	 * 
	 * <p>
	 * La creation d'une DT Repro est special, car un document attache y est
	 * obligatoire. Une nouvelle DT ne doit pas etre cree si le document n'a pa pu
	 * etre joint a la demande (par exemple, enregistre dans GEDFS). Ceci n'est
	 * pas le cas pour les demandes "ordinaires".
	 * </p>
	 */
	public WOComponent validerDemande() {
		NSArray<String> filesURL;
		StringBuffer addMessage = new StringBuffer();

		if (fillDataDictionary() == false ||
				hasMainErrors()) {
			return null;
		}

		// On enregistre la demande elle meme
		saveData();
		if (hasMainErrors()) {
			return CktlAlertPage.newAlertPageWithCaller(
					parentPage, "Enregistrement", mainError(),
					"Retour", CktlAlertPage.ERROR);
		}
		// Ensuite, on enregistre les documents attaches
		filesURL = saveFichierAttache(newDtIntOrdre());
		if (hasMainErrors()) {
			if (ignoreFileSaveErrors()) {
				// Si l'erreur d'enregistrement peut etre ignoree, alors on memorise
				// seulement le message d'erreur.
				addMessage.append("<BR><BR>").append(mainError());
			} else {
				// Sinon, on arrete la creation de la demande
				interventionBus().deleteIntervention(null, newDtIntOrdre());
				return CktlAlertPage.newAlertPageWithCaller(
						parentPage, "Enregistrement",
						"Une erreur s'est produite lors de l'enregistrement des documents joins.<br>" +
								"Une nouvelle demande ne sera pas créée, car les documents attachés y son obligatoires.<br>" +
								"<br>" +
								"Le message d'erreur :<br>" +
								mainError(),
						"Retour", CktlAlertPage.ERROR);
			}
		}
		// On continue la creation de la demande
		if (createDevis() == false) {
			// le devis est obligatoire
			if (mustCreateDevis()) {
				// oui : suppression de la DT et message d'erreur
				interventionBus().deleteIntervention(null, newDtIntOrdre());
				// le message
				String msgTxt = getPieDetailledError();
				// si pas de description, le message PIE
				if (StringCtrl.isEmpty(msgTxt)) {
					if (pieBus().hasError()) {
						msgTxt = pieBus().errorMessage();
					}
				}
				// pas de message du tout ...
				if (StringCtrl.isEmpty(msgTxt)) {
					msgTxt = "[Aucune description disponible]";
				}
				return CktlAlertPage.newAlertPageWithCaller(
						parentPage, "Enregistrement",
						"Une erreur s'est produite lors de la creation du devis DTRepro.<br>" +
								"La demande ne sera pas créée, car le devis est obligatoire.<br>" +
								"<br>" +
								"Le message d'erreur :<br>" +
								msgTxt,
						"Retour", CktlAlertPage.ERROR);

			}
		}

		if (hasMainErrors())
			addMessage.append("<BR><BR>").append(mainError());
		sendMail(filesURL);
		if (hasMainErrors())
			addMessage.append("<BR><BR>").append(mainError());
		DTLogger.logCreation(
				newDtCleService, newDtIntOrdre,
				saveDataDico.valueForKey("cStructure"),
				dtSession().connectedUserInfo().login(),
				dtSession().connectedUserInfo().noIndividu());
		saveDefaults();
		return doPreValidation(addMessage.toString());
	}

	// private String getMessageForEtat(String etat) {
	// if (DTEtatBus.ETAT_NON_VALIDEES.equals(etat)) {
	// return
	// "Cette demande devra &ecirc;tre VALID&Eacute;E avant<br>" +
	// "d'&ecirc;tre prise en compte par le service concern&eacute;&nbsp;!";
	// }
	// return null;
	// }

	/**
	 * Reinitialise la vue pour la creation d'une nouvelle demande.
	 */
	public WOComponent nouvelleDemande() {
		resetForNewDt();
		return null;
	}

	/**
	 * Teste et effectue si necessaire une pre-validation. Sinon, cette methode
	 * affiche le message avec le numero de la demande creee. Le parametre
	 * <code>addMessage</code> indique le message supplementaire a afficher (par
	 * exemple, un avertissement resu lors de l'enregistrement).
	 */
	public WOComponent doPreValidation(String addMessage) {
		// [LRAppTasks] : @CktlLog.trace(@"Cle service : "+newDtCleService());
		// [LRAppTasks] : @CktlLog.trace(@"Int Ordre : "+newDtIntOrdre());
		// [LRAppTasks] : @CktlLog.trace(@"Etat DT : "+newDtEtat());
		// Construction d'un message d'avertissement
		StringBuffer sb = new StringBuffer();
		// Le numero de la demande
		sb.append("Votre demande a le num&eacute;ro ").append(newDtCleService());
		// Le numero de devis, s'il est cree
		if (newDtPrestId() != null) {
			sb.append("<br>Le devis cr&eacute;&eacute; pour votre demande a le num&eacute;ro ").append(newDtPrestNumero());
		}
		// Un message transmise par la vue
		if (addMessage.length() > 0)
			sb.append("<hr class=\"subLine\">").append(addMessage);
		// Un message d'avertissement des tests post-creation
		// affiche si on est pas en mode choix de service de facturation
		if (!parentPage().isOnlyFactService) {
			String warn = getPostCreateWarning();
			if (!StringCtrl.isEmpty(warn))
				sb.append("<hr class=\"subLine\">").append(warn);
		}
		// Un message supplementaire, s'il faut valider la demande
		if (EOEtatDt.ETAT_NON_VALIDEES.equals(newDtEtat())) {
			sb.append("<hr class=\"subLine\">");
			sb.append("Cette demande devra &ecirc;tre VALID&Eacute;E avant<br>");
			sb.append("d'&ecirc;tre prise en compte par le service concern&eacute; !");
			// Demande de prevalidation s'il a les droits
			if (interventionBus().canDoPreValidation(browserRecord().numberForKey("actOrdre"))) {
				sb.append("<br><br>Voulez-vous valider cette demande ?");
				return CktlAlertPage.newAlertPageWithResponder(this, "Enregistrement",
						sb.toString(),
						"Valider", "Laisser non-valid&eacute;e", null, CktlAlertPage.INFO,
						new DTValidationResponder());
			}
		} else {
			// DT deja validee, verifier pour l'affectation immediate
			if (interventionBus().canDoPreAffectation(
					newDtEtat(), browserRecord().numberForKey("actOrdre"), browserRecord().stringForKey("cStructure"), dtUserInfo().noIndividu()))
				return askForPreAffectation();
		}
		// resetForNewDt();
		// return CktlAlertPage.newAlertPageWithCaller(
		// parentPage, "Enregistrement", sb.toString(), "Nouvelle demande",
		// CktlAlertPage.INFO);
		return CktlAlertPage.newAlertPageWithResponder(this, "Enregistrement",
				sb.toString(),
				"Nouvelle demande", "Fermer la session", null, CktlAlertPage.INFO,
				new DTEndCreateResponder());
	}

	/**
	 * Affiche l'ecran de fin de creation de DT. 2 Choix : recreer une nouvelle
	 * demande ou fermer la session.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class DTEndCreateResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			// nouvelle DT
			if (buttonNo == 1) {
				resetForNewDt();
				return parentPage;
			} else {
				// fermeture session
				return dtSession().doExit();
			}
		}
	}

	/**
	 * demande et effectue une affectation.
	 */
	private WOComponent askForPreAffectation() {
		// Construction d'un message d'avertissement
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Voulez-vous affecter cette demande maintenant ?");
		return CktlAlertPage.newAlertPageWithResponder(this, "Affectation imm&eacute;diate",
				sb.toString(),
				"Oui", "Non", null, CktlAlertPage.INFO,
				new DTAffectationResponder());
	}

	/* == Actions de l'interface HTML == */

	public WOComponent selectLigneBud() {
		return selectLigneBud(new LbudSelector());
	}

	public WOComponent selectLigneBud(
			SelectLigneBudListener listener) {
		SelectLigneBud selector = (SelectLigneBud) dtSession().getSavedPageWithName(
				SelectLigneBud.class.getName());
		selector.setListener(listener);
		return selector;
	}

	public WOComponent clearLigneBud() {
		setNewDtOrgId(null);
		setNewDtTcdCode(null);
		if (getDestinationLolfCtrl() != null) {
			getDestinationLolfCtrl().setLolfId(null);
		}
		return null;
	}

	/* == Gestionaires des evenements lors de l'appel aux autres composants == */

	/**
	 * Gestionnaire des evenements de la selection des lignes budgetaires.
	 */
	private class LbudSelector implements SelectLigneBudListener {

		/**
		 * @see LigneBudSelector#selectedLigneBud(java.lang.Integer,
		 *      java.lang.Number)
		 */
		public WOComponent select(Integer orgId, String tcdCode) {
			setOrgId(orgId);
			setTcdCode(tcdCode);
			if (getDestinationLolfCtrl() != null) {
				getDestinationLolfCtrl().setLolfId(null);
			}
			return parentPage();
		}

		/**
		 * @see LigneBudSelector#ligneBudSelectReturnPage()
		 */
		public WOComponent cancel() {
			return parentPage();
		}

		/**
		 * @see SelectLigneBudListener#setOrgId(java.lang.Integer)
		 */
		public void setOrgId(Integer orgId) {
			setNewDtOrgId(orgId);
		}

		/**
		 * @see SelectLigneBudListener#setTcdCode(java.lang.String)
		 */
		public void setTcdCode(String tcdCode) {
			setNewDtTcdCode(tcdCode);
		}
	}

	/**
	 * Effectue la validation d'une demande, si tel est le choix d'utilisateur.
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 */
	private class DTValidationResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			WOComponent nextPage = parentPage;
			boolean shouldResetForNewDT = true;
			if (buttonNo == 1) {
				dtDataCenter().interventionBus().performValidation(
						dtUserInfo(), newDtIntOrdre(), null, EOEtatDt.ETAT_NON_AFFECTEES);
				// mise a jour de l'etat pour le composant
				setNewDtEtat(EOEtatDt.ETAT_NON_AFFECTEES);
				if (interventionBus().canDoPreAffectation(newDtEtat(), browserRecord().numberForKey("actOrdre"), browserRecord().stringForKey("cStructure"), dtUserInfo().noIndividu())) {
					shouldResetForNewDT = false;
					nextPage = askForPreAffectation();
				}
			}

			if (shouldResetForNewDT)
				resetForNewDt();
			return nextPage;
		}
	}

	/**
	 * Affiche l'ecran d'affectation, si tel est le choix d'utilisateur.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class DTAffectationResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			if (buttonNo == 1) {
				// // afficher la page de consultation
				// dtSession().selectConsult();
				// // dans cette page, se mettre sur la vue d'affectation pour cette DT
				// InspecteurDT pageInspecteur = (InspecteurDT) dtSession().
				// getSavedPageWithName(InspecteurDT.class.getName());
				// // recuperer le record de la dt nouvellement creee
				// CktlRecord recNewDt = interventionBus().findIntervention(null,
				// newDtIntOrdre());
				// // a la fin de l'affectation, on revient ici
				// PageCreation pageCreation = (PageCreation) dtSession().
				// getSavedPageWithName(PageCreation.class.getName());
				// pageInspecteur.afficherInspecteur(pageCreation, recNewDt);
				// // on selectionne l'ecran affectation de la taskBar
				// pageInspecteur.taskBar.doAffectation();
				CompAffectation component = (CompAffectation) pageWithName(CompAffectation.class.getName());
				// recuperer le record de la dt nouvellement creee
				EOIntervention recNewDt = interventionBus().findIntervention(null, newDtIntOrdre);
				// passer tout �a en parametres a la page d'affectation
				component.setShowFullPage(Boolean.TRUE);
				component.setListener(new CompAffectationListener(recNewDt, parentPage));
				// raz du composant
				resetForNewDt();
				// allez hop on affiche
				return component;
			}
			resetForNewDt();
			return parentPage;
		}
	}

	/**
	 * Methode qui ne fait rien d'autre que de poster les champs du formulaire
	 * (refresh de page)
	 */
	public WOComponent doNothing() {
		return null;
	}

	/**
	 * Indique si la creation du devis pour la DT est obligatoire quoi qu'il en
	 * soit pour qu'elle soit valide.
	 */
	public abstract boolean mustCreateDevis();

	/**
	 * Retourne un message detaille de l'erreur survenue lors de la creation du
	 * devis.
	 * 
	 * @see SwapRepro.getDTCheckStateMessage()
	 */
	protected String getPieDetailledError() {
		StringBuffer info = new StringBuffer();

		if (dtApp().config().booleanForKey("APP_ERROR_DESCRIPTION")) {
			// On verifie d'abord si le service du demandeur est bien celui indique
			// dans l'annuaire. Le demandeur peut lui-meme donner le service dans les
			// contacts, mais ceci peut ne pas etre renseigne dans l'annuaire.
			String serviceName =
					serviceBus().libelleForServiceCode(codeServiceDemandeur(), false, true);
			// Le service demandeur doit etre indique comme un "Service" de
			// l'annuaire.
			if (!serviceBus().isServiceAnnuaire(codeServiceDemandeur(), persIdDemandeur())) {
				info.append("Votre information de contact indique que votre service est : \"");
				info.append(serviceName).append("\" (code ").append(codeServiceDemandeur());
				info.append(")mais cette information n'est pas enregistr&eacute;e dans l'annuaire de votre &eacute;tablissement.");
			}
			// On teste si le fournisseur est valide pour le devis
			if (!jefyBus().checkFournisStructure(codeServiceDemandeur())) {
				if (info.length() > 0)
					info.append("\n\n");
				info.append("Les informations de l'annuaire indiquent que votre service \"");
				info.append(serviceName).append("\" (code ").append(codeServiceDemandeur());
				info.append(") n'est pas d&eacute;clar&eacute; comme un service des fournisseurs valides de l'&eacute;tablissement.");
				// On propose les services valides
				NSArray allSrv = jefyBus().getFournisStructures(persIdDemandeur());
				NSDictionary rec;
				info.append("<br>Toutefois, dans l'annuaire, vous &ecirc;tes affect&eacute; aux services valides suivants :");
				if (allSrv.count() > 0) {
					for (int i = 0; i < allSrv.count(); i++) {
						rec = (NSDictionary) allSrv.objectAtIndex(i);
						info.append("<br>&nbsp;-&nbsp;\"").append(rec.valueForKey("llStructure"));
						info.append("\" (").append(rec.valueForKey("cStructure")).append(")");
					}
				}
				info.append("<br><u>Vous pouvez modifier votre contact en choisissant une de ces structures.</u>");
				// le message de pie en lui meme
				if (pieBus().hasError()) {
					if (info.length() > 0)
						info.append("\n\n");
					info.append("<br><br>Le message issus de PIE : <br><i>");
					info.append(pieBus().errorMessage()).append("i");
				}
			}
			//
			if (info.length() > 0) {
				info.insert(0, "ATTENTION !<br>");
				info.append("<br><br>Vous pouvez contacter la personne responsable de votre annuaire<br>");
				info.append("pour corriger ces erreurs et ne plus avoir ces messages dans le futur.");
			}
		}

		return info.toString();
	}

	/**
	 * 
	 * @return
	 */
	public WOComponent neFaitRien() {
		return null;
	}

	public final A_FooterSwapCtrl getFooterCtrl() {
		return footerCtrl;
	}

	public final void setFooterCtrl(A_FooterSwapCtrl footerCtrl) {
		this.footerCtrl = footerCtrl;
	}

	/**
	 * la liste des destinations LOLF. Est utilisé pour etre RAZ après selection
	 * de la ligne budgetaire via {@link LbudSelector#select(Integer, String))
	 */
	public abstract A_ListeDestinationLolfCtrl getDestinationLolfCtrl();
}
