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
import java.io.UnsupportedEncodingException;

import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOPrefAppli;
import org.cocktail.dt.server.ws.glpi.A_GLPIObject;
import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxSession;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.URLEncoder;
import org.cocktail.fwkcktlwebapp.server.CktlMailBus;
import org.cocktail.fwkcktlwebapp.server.CktlWebAction;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuItemSet;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuListener;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORedirect;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Represente la session de travail d'un utilisateur avec l'application.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class Session extends CocktailAjaxSession {
	public static final int MODE_CREAT = 1;
	public static final int MODE_DA_CREAT = 11;
	public static final int MODE_CONSULT = 2;
	public static final int MODE_PREFS = 3;
	public static final int MODE_RECH = 4;
	public static final int MODE_PANIER = 8;
	public static final int MODE_ADM = 5;
	public static final int MODE_DA_VALIDER = 41;
	public static final int MODE_DA_FAST = 6;
	public static final int MODE_OUTILS = 7;
	private int mode;
	private int currentServiceDest;
	private Number currentActOrdre;
	private DTUserInfo dtUserInfo;
	private DTDataCenter dataCenter;
	private DTPrintCenter printCenter;
	private DTDocumentCenter documentCenter;
	private DTMailCenter mailCenter;
	private DTPrestaBusWeb pieBus;

	/**
	 * Cree une nouvelle instance de la session.
	 */
	public Session() {
		super();
		currentServiceDest = 0;
		mode = MODE_CREAT;
		userWasWarned = false;
		CktlLog.log("<open session : " + sessionID() + ">");
		DTSessionManager.registerSession(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * er.extensions.appserver.ERXSession#appendToResponse(com.webobjects.appserver
	 * .WOResponse, com.webobjects.appserver.WOContext)
	 */
	@Override
	public void appendToResponse(WOResponse aResponse, WOContext aContext) {
		DTSessionManager.registerContext(this, aContext);
		super.appendToResponse(aResponse, aContext);
	}

	/**
	 * Retourne la reference vers la session de travail d'utilisateur.
	 */
	public Application dtApp() {
		return (Application) WOApplication.application();
	}

	public String modeEnCoursNom() {
		switch (mode) {
		case MODE_CREAT:
		case MODE_DA_CREAT:
			return "Cr&eacute;ation";
		case MODE_DA_FAST:
			return "Fast";
		case MODE_OUTILS:
			return "Outils";
		case MODE_CONSULT:
			return "Consultation";
		case MODE_RECH:
			return "Recherche";
		case MODE_PREFS:
			return "Pr&eacute;f&eacute;rences";
		case MODE_DA_VALIDER:
			return "Validation";
		case MODE_ADM:
			return "Administration";
		case MODE_PANIER:
			return "Panier";
		default:
			return "S&eacute;lection inconnue";
		}
	}

	public void setMode(int newMode) {
		mode = newMode;
	}

	public int getMode() {
		return mode;
	}

	public WOComponent selectCreat() {
		return selectCreat(0);
	}

	public WOComponent selectCreat(int serviceDest) {
		CktlLog.trace("serviceDest : " + serviceDest);
		setPageContenu(getSavedPageWithName(PageCreation.class.getName()));
		currentServiceDest = serviceDest;
		mode = MODE_CREAT;
		return pageContenu();
	}

	/**
	 * Affiche la page de consultation
	 * 
	 * @param doRechercheDT
	 *          : indique si la liste des DT doit etre fetchee au préalable
	 * @param intOrdre
	 *          : la dt a inspecter (-1 s'il faut rester sur la page de
	 *          consultation)
	 * @return
	 */
	public WOComponent selectConsult(boolean doRechercheDT, int intOrdre) {
		PageConsultation pageConsultation = (PageConsultation) getSavedPageWithName(PageConsultation.class.getName());
		mode = MODE_CONSULT;
		if (shouldReloadPrefsConsultation) {
			pageConsultation.initComponent();
			shouldReloadPrefsConsultation = false;
		}
		pageConsultation.setQualifierExternal(null);
		if (doRechercheDT) {
			pageConsultation.listener.doFetchDisplayGroup();
		}
		if (intOrdre != -1) {
			// verifier que l'utilisateur a bien acces a la demande
			EOIntervention recIntervention = dataCenter().interventionBus().findIntervention(null, new Integer(intOrdre));
			if (recIntervention != null) {
				// verifier que l'utilisateur a bien acces a la demande
				// TODO a deplacer dans les bus ...
				EOQualifier restrictedQualifier = DTParamConfig.getRestrictedQualifier(dataCenter().serviceBus(), dtUserInfo());
				EOQualifier intOrdreQualifier = CktlDataBus.newCondition(EOIntervention.INT_ORDRE_KEY + "=" + intOrdre);
				EOQualifier checkAccessQualifier = (restrictedQualifier == null ?
						intOrdreQualifier : new EOAndQualifier(
								new NSArray<EOQualifier>(new EOQualifier[] {
										intOrdreQualifier, restrictedQualifier })));
				boolean hasAccess = (dataBus().fetchObject(EOIntervention.ENTITY_NAME, checkAccessQualifier) != null);
				if (hasAccess) {
					// acces autorise => inspection
					InspecteurDT inspecteur = InspecteurDT.getNewInspecteur(this, pageConsultation, recIntervention);
					pageConsultation.setInspecteur(inspecteur);
					setPageContenu(inspecteur);
				} else {
					// acces refusé => message d'erreur
					setPageContenu(CktlAlertPage.newAlertPageWithMessage(
							pageConsultation,
							"Erreur : acc&egrave;s non autoris&eacute;",
							"Vous n'avez pas le droit de consulter la demande de travaux ayant le code &lt;" + intOrdre + "&gt;  ...<br/><br/>" +
									"Vous devez y &ecirc;tre autoris&eacute; explicitement (droits dans l'application), ou &ecirc;tre le demandeur.",
							CktlAlertPage.ERROR));
				}
			} else {
				// appel avec un intOrdre inconnu ...
				setPageContenu(CktlAlertPage.newAlertPageWithMessage(
						pageConsultation,
						"Erreur : DT introuvable",
						"La demande de travaux avec le code &lt;" + intOrdre + "&gt; est introuvable ...<br/><br/>" +
								"Soit votre requ&ecirc;te est incorrecte, soit cette demande &agrave; &eacute;t&eacute; supprim&eacute;e.",
						CktlAlertPage.ERROR));
			}
		} else {
			// pas de dt a inspecter => page de consultation
			setPageContenu(pageConsultation);
		}
		return pageContenu();
	}

	public WOComponent selectRecherche() {
		PageRechercheNew page = (PageRechercheNew) getSavedPageWithName(PageRechercheNew.class.getName());
		setPageContenu(page);
		mode = MODE_RECH;
		return pageContenu();
	}

	public WOComponent selectPanier() {
		PagePanier page = (PagePanier) getSavedPageWithName(PagePanier.class.getName());
		setPageContenu(page);
		mode = MODE_PANIER;
		return pageContenu();
	}

	/**
	 * Selection du sous-item des preferences. Si la selection se fait par le menu
	 * general, on selectionne le dernier sous onglet choisi. Sinon on selectionne
	 * explicitement le premier.
	 */
	public WOComponent selectPreferences(int onglet) {
		setPageContenu(getSavedPageWithName(PagePreferences.class.getName()));
		PagePreferences pagePrefs = (PagePreferences) pageContenu();
		pagePrefs.setSelectedOnglet(onglet);
		mode = MODE_PREFS;
		return pageContenu();
	}

	/**
	 * Selection du sous-item d'outil. Si la selection se fait par le menu
	 * general, on selectionne le dernier sous onglet choisi. Sinon on selectionne
	 * explicitement le premier.
	 */
	public WOComponent selectOutil(int onglet) {
		mode = MODE_OUTILS;
		setPageContenu(getSavedPageWithName(PageOutils.class.getName()));
		PageOutils pageOutils = (PageOutils) pageContenu();
		pageOutils.setSelectedOnglet(onglet);
		return pageContenu();
	}

	/**
	 * Selection du sous-item d'administration. Si la selection se fait par le
	 * menu general, on selectionne le dernier sous onglet choisi. Sinon on
	 * selectionne explicitement le premier.
	 */
	public WOComponent selectAdministration(int onglet) {
		setPageContenu(getSavedPageWithName(PageAdministration.class.getName()));
		PageAdministration pageAdm = (PageAdministration) pageContenu();
		pageAdm.setSelectedOnglet(onglet);
		mode = MODE_ADM;
		return pageContenu();
	}

	// creation par direct action

	public int getCurrectServiceDest() {
		return currentServiceDest;
	}

	public void setCurrentServiceDest(int serviceCode) {
		currentServiceDest = serviceCode;
	}

	public Number getCurrentActOrdre() {
		return currentActOrdre;
	}

	public void setCurrentActOrdre(Number actOrdre) {
		currentActOrdre = actOrdre;
	}

	//

	private WOComponent _pageContenu;

	public void setPageContenu(WOComponent value) {
		_pageContenu = value;
	}

	/**
	 * Methode modifiee suite a la suppression du frameset. Ce n'est plus qu'un
	 * simple getter
	 * 
	 * @return
	 */
	public WOComponent pageContenu() {
		return _pageContenu;
	}

	public DTUserInfo dtUserInfo() {
		if (dtUserInfo == null) {
			dtUserInfo = dataCenter().preferencesBus().getUserInfoForLogin(connectedUserInfo().login());
		}
		return dtUserInfo;
	}

	private boolean shouldReloadPrefsConsultation = false;

	/**
	 * Forcer le rechargement des preferences. La page PageConsultation doit etre
	 * rafraichie a son prochain affichage
	 */
	public void doReloadPreferences() {
		dtUserInfo = null;
		dtUserInfo();
		shouldReloadPrefsConsultation = true;
	}

	public DTDataCenter dataCenter() {
		if (dataCenter == null)
			dataCenter = new DTDataCenter(this);
		return dataCenter;
	}

	public DTPrintCenter printCenter() {
		if (printCenter == null)
			printCenter = new DTPrintCenter(this);
		return printCenter;
	}

	protected CktlMailBus getNewMailBus() {
		CktlMailBus aMailBus = cktlApp.getNewMailBus(cktlApp.config());
		if (aMailBus instanceof DTForwardMailBus) {
			((DTForwardMailBus) aMailBus).setSession(this);
		}
		return aMailBus;
	}

	protected CktlDataBus getNewDataBus() {
		return new DTDataBus(defaultEditingContext());
	}

	public DTDataBus dtDataBus() {
		return (DTDataBus) dataBus();
	}

	public DTDocumentCenter dtDocumentCenter() {
		if (documentCenter == null)
			documentCenter = new DTDocumentCenter(this);
		return documentCenter;
	}

	public DTMailCenter mailCenter() {
		if (mailCenter == null)
			mailCenter = new DTMailCenter(this,
					cktlApp.config().stringForKey("APP_PRESTA_WS_URL"));
		return mailCenter;
	}

	public DTPrestaBusWeb pieBus() {
		if (pieBus == null)
			pieBus = new DTPrestaBusWeb(this);
		return pieBus;
	}

	/**
	 * Teste si l'acces a la base de donnees JEFYCO est disponible. Cette methode
	 * utilise le parametre de configuration en <code>APP_USE_JEFYCO</code>.
	 */
	public boolean useJefyco() {
		return dtApp().config().booleanForKey("APP_USE_JEFYCO");
	}

	/**
	 * Teste si le choix des lignes budgetaires est supporte. Retourne une valeur
	 * 0, 1 ou 2 si la selection des lignes budgetaires est indisponible,
	 * optionnelle ou obligatoire.
	 * 
	 * @see #useJefyco()
	 */
	public int checkJefyLBudSupport() {
		int level = 0;
		if (useJefyco()) {
			level = dtApp().config().intForKey("APP_FORCE_LBUD_LEVEL");
			// Si rien n'est indique, on considere que la seletion est optionnelle
			if (level == -1)
				level = 1;
		}
		return level;
	}

	/**
	 * Teste si le choix des destinations est supporte. Retourne une valeur 0, 1
	 * ou 2 si la selection des destinations est indisponible, optionnelle ou
	 * obligatoire.
	 * 
	 * @see #useJefyco()
	 */
	public int checkJefyDestinSupport() {
		int level = 0;
		if (useJefyco()) {
			level = dtApp().config().intForKey("APP_FORCE_DESTIN_LEVEL");
			// Si rien n'est indique, on considere que la seletion est optionnelle
			if (level == -1)
				level = 1;
		}
		return level;
	}

	public void terminate() {
		// Recupere le log de la fermeture de session
		String userLogin = null;
		if (connectedUserInfo() != null)
			userLogin = connectedUserInfo().login();
		StringBuffer log = new StringBuffer();
		log.append("<close session : ").append(sessionID());
		if (userLogin != null)
			log.append(", user : ").append(userLogin);
		log.append(">");
		CktlLog.log(log.toString());
		DTSessionManager.unregisterSession(this);
		//
		super.terminate();
	}

	// gestion du menu : 1 seule instance

	private CktlMenuListener _dtMenuLister;
	private CktlMenuItemSet _dtMenuItemSet;

	public CktlMenuListener getDtMenuLister() {
		return _dtMenuLister;
	}

	public void setDtMenuLister(CktlMenuListener value) {
		_dtMenuLister = value;
	}

	public CktlMenuItemSet getDtMenuItemSet() {
		return _dtMenuItemSet;
	}

	public void setDtMenuItemSet(CktlMenuItemSet value) {
		_dtMenuItemSet = value;
	}

	// deconnexion

	private Boolean forceSSOLogout;

	/**
	 * Cas des machines type kiosque DT, la fermeture de session deconnecte aussi
	 * de CAS. On oblige a fermer la session CAS si l'ip du navigateur est dans la
	 * liste "LISTE_IP_FORCE_SSO_LOGOUT" du fichier DT3.config
	 */
	public boolean forceSSOLogout() {
		if (forceSSOLogout == null) {
			try {
				String ip = dtApp().getRequestIPAddress(context().request());
				forceSSOLogout = new Boolean(
						!StringCtrl.isEmpty(dtApp().config().stringForKey("LISTE_IP_FORCE_SSO_LOGOUT")) &&
								NSArray.componentsSeparatedByString(
										dtApp().config().stringForKey("LISTE_IP_FORCE_SSO_LOGOUT"), ",").containsObject(ip));
			} catch (Exception e) {
				e.printStackTrace();
				forceSSOLogout = new Boolean(false);
			}
		}
		return forceSSOLogout.booleanValue();
	}

	private Integer currentAnnee;

	/**
	 * L'annee actuel. Est utilisee pour recuperer les lignes bugetaires de
	 * l'exercice en cours
	 */
	public Integer currentAnnee() {
		if (currentAnnee == null) {
			currentAnnee = new Integer(Integer.parseInt(
					"20" + DateCtrl.currentDateString().substring(6, 8)));
		}
		return currentAnnee;
	}

	/**
	 * Execute l'action de la deconnexion
	 */
	public WOComponent doExit() {
		String url = dtApp().getApplicationURL(context());
		if (forceSSOLogout() && CktlWebAction.useCasService()) {
			// ENcode URL
			if (url.length() > 0) {
				StringBuffer u = new StringBuffer(CktlWebAction.casLogoutURL());
				try {
					u.append("?service=").append(URLEncoder.encode(url, "UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
				url = u.toString();
			}
		}
		if (context().hasSession())
			context().session().terminate();
		CktlLog.trace("logout.url : " + url);
		return pageForURL(url);
	}

	/**
	 * Retourne le composant de redirection vers l'URL donne. Retourne <i>null</i>
	 * si <code>url</code> n'est pas defini.
	 */
	protected WOComponent pageForURL(String url) {
		if (url == null)
			return null;
		WORedirect page = (WORedirect) cktlApp.pageWithName("WORedirect", context());
		page.setUrl(url);
		return page;
	}

	/**
	 * Retourne le lien de la directe action pour la consultation des demandes.
	 */
	protected String appConsultationURL(int intOrdre) {
		return dtApp().getApplicationURL(context()) + "/wa/DAConsultation?intOrdre=" + intOrdre;
	}

	/**
	 * cache de tous les nodes <code>Activites</code> qui ont ete cree
	 */
	private NSArray<ActivitesNode> activitesNodes = new NSArray<ActivitesNode>();

	public NSArray<ActivitesNode> activitesNodes() {
		return activitesNodes;
	}

	// arret programme de l'application

	/** indique que l'utilisateur a bien recu l'avertissement */
	private boolean userWasWarned;

	public boolean userWasWarned() {
		return userWasWarned;
	}

	public void notifyWarnIsDone() {
		userWasWarned = true;
	}

	// gestion du panier

	private NSMutableArray<EOIntervention> eoInterventionArrayPanier;

	public final NSMutableArray<EOIntervention> getEoInterventionArrayPanier() {
		if (eoInterventionArrayPanier == null) {
			eoInterventionArrayPanier = new NSMutableArray<EOIntervention>();
			// restauration du panier
			if (dtUserInfo().sauvegarderPanier()) {
				eoInterventionArrayPanier = EOPrefAppli.restaurerPanier(defaultEditingContext(), dtUserInfo().panier());
			}
		}
		return eoInterventionArrayPanier;
	}

	private final NSArray<Integer> getIntOrdreArrayPanier() {
		return (NSArray<Integer>) getEoInterventionArrayPanier().valueForKey(EOIntervention.INT_ORDRE_KEY);
	}

	public final void ajouterPanier(EOIntervention eoIntervention) {
		if (!isDansLePanier(eoIntervention)) {
			getEoInterventionArrayPanier().addObject(eoIntervention);
			if (dtUserInfo().sauvegarderPanier()) {
				String newPanier = EOPrefAppli.addInterventionPanier(eoIntervention, dtUserInfo().panier());
				dataCenter().preferencesBus().updatePrefAppli(
						null, dtUserInfo().noIndividu(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, newPanier);
				doReloadPreferences();
			}
		}
	}

	public final void supprimerPanier(EOIntervention eoIntervention) {
		if (isDansLePanier(eoIntervention)) {
			getEoInterventionArrayPanier().removeIdenticalObject(eoIntervention);
			if (dtUserInfo().sauvegarderPanier()) {
				String newPanier = EOPrefAppli.delInterventionPanier(eoIntervention, dtUserInfo().panier());
				dataCenter().preferencesBus().updatePrefAppli(
						null, dtUserInfo().noIndividu(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, newPanier);
				doReloadPreferences();
			}
		}
	}

	public final boolean isDansLePanier(EOIntervention eoIntervention) {
		boolean isDansLePanier = false;

		if (getIntOrdreArrayPanier().containsObject(eoIntervention.intOrdre())) {
			isDansLePanier = true;
		}

		return isDansLePanier;
	}

	public final void viderPanier() {
		getEoInterventionArrayPanier().removeAllObjects();
		dataCenter().preferencesBus().updatePrefAppli(
				null, dtUserInfo().noIndividu(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "");
		doReloadPreferences();
	}

	// GLPI

	private Boolean isGlpiServiceAvailable = null;

	/**
	 * La disponibilité du service est mémorisée une fois par session pour ne pas
	 * spammer GLPI à chaque fois qu'on affiche l'inspecteur sur une DT
	 * 
	 * @return
	 */
	public boolean isGlpiServiceAvailable() {
		if (isGlpiServiceAvailable == null) {
			isGlpiServiceAvailable = A_GLPIObject.testService();
		}
		return isGlpiServiceAvailable;
	}

}
