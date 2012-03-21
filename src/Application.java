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
import java.util.Timer;
import java.util.TimerTask;

import org.cocktail.dt.server.ws.glpi.A_GLPIObject;
import org.cocktail.fwkcktlajaxwebext.serveur.CocktailAjaxApplication;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.URLCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;
import org.cocktail.fwkcktlwebapp.server.CktlMailBus;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.version.A_CktlVersion;
import org.cocktail.ycrifwk.utils.UtilException;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOWebServiceRegistrar;
import com.webobjects.appserver._private.WOResourceRequestHandler;
import com.webobjects.foundation.NSTimeZone;

import er.extensions.appserver.ERXMessageEncoding;
import fr.univlr.cri.dt.app.I_ApplicationConsts;
import fr.univlr.cri.dt.app.VersionOracleDbDT;

/**
 * La classe principale de l'application DT Web. Elle contient les methodes du
 * demarrage de l'application. Elle fournit les operations partagees par tous
 * les composants et les sessions.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class Application extends CocktailAjaxApplication {

	public static void main(String argv[]) {
		// com.webobjects.appserver.WOApplication.main(argv, Application.class);
		er.extensions.appserver.ERXApplication.main(argv, Application.class);
	}

	public Application() {
		super();
		registerRequestHandler(new WOResourceRequestHandler(), resourceRequestHandlerKey());
		setDefaultRequestHandler(requestHandlerForKey(directActionRequestHandlerKey()));
	}

	/**
	 * Execute les operations au demarrage de l'application, juste apres
	 * l'initialisation standard de l'application.
	 */
	public void startRunning() {

		// encoding en UTF-8 partout
		WOMessage.setDefaultEncoding("UTF-8");
		WOMessage.setDefaultURLEncoding("UTF-8");
		ERXMessageEncoding.setDefaultEncoding("UTF8");
		ERXMessageEncoding.setDefaultEncodingForAllLanguages("UTF8");

		// settage du timezone
		String tzs = config().stringForKey("DEFAULT_TIME_ZONE");
		if (tzs == null) {
			tzs = "CEST";
		}
		java.util.TimeZone tz = java.util.TimeZone.getTimeZone(tzs);
		java.util.TimeZone.setDefault(tz);
		NSTimeZone.setDefault(tz);

		rawLogAppInfos();
		rawLogVersionInfos();
		rawLogModelInfos();
		checkModel();
		initLocalWebServices();
		if (config().booleanForKey("APP_USE_PIE")) {
			checkPrestaWebService();
		}
		// ws GLPI
		A_GLPIObject.initAppConfig(config());
		// init arret programme
		secondsUntilStop = -1;
		appWillStop = false;
		CktlLog.log("Demande de Travaux demarre !");

		// CktlLog.setLevel(CktlLog.LEVEL_DEBUG);

		// TestClientXMLRPC.test();

	}

	/**
	 * Teste les services Web de prestations.
	 */
	private void checkPrestaWebService() {
		StringBuffer swMsg = new StringBuffer();
		StringBuffer swErr = new StringBuffer();
		try {
			DTPrestaBusWeb pieBus = new DTPrestaBusWeb(
					new Integer(-1), new Integer(-1), false);
			Hashtable result = pieBus.checkWebService();
			Float verNum = null;
			if (!pieBus.hasError()) {
				// On verifie la version du service
				if (result.get("app.ver.num") != null)
					verNum = Float.valueOf((String) result.get("app.ver.num"));
				if ((verNum == null) || (verNum.floatValue() < wsPrestaVersion())) {
					swErr.append("La version du service Web Prestations inconnue ou incorrecte\n");
					swErr.append("Requise : ").append(wsPrestaVersion());
					swErr.append(", disponible : ").append(verNum);
				}
			} else {
				swErr.append(pieBus.errorMessage());
			}
			// On construit le message final
			swMsg.append("  Service Web Prestations : ");
			swMsg.append((swErr.length() == 0) ? "DISPONIBLE" : "INDISPONIBLE");
			swMsg.append(" [serveur=").append(getPrestaServerName());
			swMsg.append(", version=").append(verNum).append("]");
		} catch (Throwable ex) {
			swErr.append(CktlLog.getMessageForException(ex));
		}
		if (swMsg.length() > 0)
			CktlLog.rawLog(swMsg.toString());
		if (swErr.length() > 0)
			DTLogger.logError(swErr.toString());
	}

	/**
	 * Retourne le nom du serveur du service Web Prestations.
	 */
	private String getPrestaServerName() {
		String url = config().stringForKey("APP_PRESTA_WS_URL");
		if (url != null) {
			// On enleve le "http://" et "/cgi-bin/"
			int idxStart = url.indexOf("://");
			int idxEnd = url.indexOf("/cgi-bin/");
			if (idxStart >= 0) {
				idxStart = idxStart + 3;
				if (idxStart < idxEnd)
					url = url.substring(idxStart, idxEnd);
				else
					url = url.substring(idxStart);

			}
		}
		return url;
	}

	/**
	 * Initilise les services Web de la gestion des demades de travaux. Ce sont
	 * les services Web propres a l'application Demdende de Travaux.
	 */
	private void initLocalWebServices() {
		try {
			WOWebServiceRegistrar.registerWebService(
					DTWebServices.ServiceExternalName, DTWebServices.class,
					DTWebServices.ServiceMethods, true);
		} catch (IllegalArgumentException ex) {
			// ex.printStackTrace();
			CktlLog.rawLog(">>Erreur d'enregistremment des services Web DT :");
			CktlLog.rawLog(">>" + CktlLog.getMessageForException(ex));
		}
	}

	/**
	 * Retourne la version du service Web Prestation requis par l'application.
	 */
	public float wsPrestaVersion() {
		return 200f;
	}

	/**
	 * Retourne la version du service Web fournit par l'application Demande de
	 * Travaux.
	 */
	public float wsDTVersion() {
		return 200f;
	}

	/**
	 * Retourne le nom de <em>EOModel</em> de l'application. Le nom ne doit pas
	 * avoir d'extention <em>.emodeld</em>.
	 */
	public String mainModelName() {
		return "DTWeb";
	}

	/**
	 * Retourne le nom de la table/l'entite avec les parametres de configuration.
	 * L'entite doit etre present dans le model de l'application. Peut retourner
	 * <em>null</em> dans le cas ou aucun configuration dans la base de donnees ne
	 * sera utilisee.
	 */
	public String configTableName() {
		return "DTGrhumParametres";
	}

	/**
	 * Retourne le nom du fichier de configuration de l'application. Le nom
	 * retournee doit avoir l'extention.
	 */
	public String configFileName() {
		return "DT3.config";
	}

	public CktlMailBus getNewMailBus(CktlConfig config) {
		String value = config.stringForKey("APP_FORWARD_MAIL");
		// Gestionnaire par defaut, si aucune definition...
		if (value != null) {
			value = value.toLowerCase();
			// ...ou si le gestionnaire par defaut
			if (!value.equals(DTForwardMailBus.DEFAULT_FORWARD)) {
				CktlLog.trace("taking forward mail instance");
				// Sinon, un vrai forward
				DTForwardMailBus mBus = new DTForwardMailBus(config, null);
				mBus.setForwardMail(value);
				return mBus;
			}
		}
		return super.getNewMailBus(config);
	}

	/* === Les specificites de la DT ==== */

	/**
	 * Methode utilisee pour le developpement uniquement. Elle permet d'ouvrir le
	 * navigateur Web par defaut au lancement de l'application sous Windows XP
	 * (sinon, l'environnement de developpement est considere comme inconnu).
	 */
	public boolean _isSupportedDevelopmentPlatform() {
		return (super._isSupportedDevelopmentPlatform() || System.getProperty("os.name").startsWith("Windows"));
	}

	/* ===== Les methodes retournant differents liens relatifs a la DT ==== */
	/**
	 * Retourne l'URL d'acces a la version Web de l'application Demande de
	 * Travaux. Cette methode utilise le parametre de configuration "APP_URL".
	 * 
	 * @see #getApplicationURL()
	 */
	public String appURL() {
		if (config().stringForKey("APP_URL") != null)
			return config().stringForKey("APP_URL");
		else
			return getApplicationURL(null);
	}

	/**
	 * Retourne URL que l'on peut utiliser comme l'alias (raccourci) pour acceder
	 * a l'application DT Web. Le URL est recherche comme parametre de
	 * configuration "APP_URL_ALIAS". Si l'alias n'est pas donne, le resultat de
	 * la methode <code>appURL</code> est retourne.
	 * 
	 * @see #appURL()
	 */
	public String appURLAlias() {
		String value = config().stringForKey("APP_URL_ALIAS");
		if (value != null)
			return value;
		else
			return appURL();
	}

	/**
	 * Retourne le URL qui peut etre utilise pour acceder a la validation de
	 * demandes via l'application Demande de Travaux Web. Cette methode ajoute une
	 * extention "/wa/DADemandes/valider" au lien retourne par la methode appURL.
	 * 
	 * @see #appURL()
	 */
	public String appValidationURL() {
		if (appURL() == null)
			return null;
		else
			// remplacement de "wa/DADemandes/valider" par "wa/DAValidation" (Richard
			// 30/8/2006)
			return URLCtrl.normalizeURL(URLCtrl.getWOAppAccessURL(appURL()), true) +
					"wa/DAValidation";
	}

	/**
	 * Retourne le chemin d'acces au repertoire ou les fichiers de l'application
	 * peuvent temporairement etre enregistres. Cette methode retourne la valeur
	 * du parametre de configuration <code>APP_TEMP_DIR</code> ou <em>null</em> si
	 * celui-ci n'est pas defini.
	 */
	public String appTempDir() {
		return config().stringForKey("APP_TEMP_DIR");
	}

	// ** controle de version **

	// 1 seule instance
	private A_CktlVersion _appCktlVersion;

	// controle de versions
	public A_CktlVersion appCktlVersion() {
		if (_appCktlVersion == null) {
			_appCktlVersion = new fr.univlr.cri.dt.app.Version();
		}
		return _appCktlVersion;
	}

	// activation du service de collecte automatique
	public boolean appShouldSendCollecte() {
		return true;
	}

	// 1 seule instance
	private A_CktlVersion _appCktlVersionDb;

	// collecte : lecture de la version de la base installee
	public A_CktlVersion appCktlVersionDb() {
		if (_appCktlVersionDb == null) {
			_appCktlVersionDb = new VersionOracleDbDT();
		}
		return _appCktlVersionDb;
	}

	// ** parametres **

	/**
	 * Les parametres obligatoires
	 */
	public String[] configMandatoryKeys() {
		return new String[] { "APP_USE_CAS", "APP_ID", "DEFAULT_TIME_ZONE" };
	}

	/**
	 * Les parametres facultatifs
	 */
	public String[] configOptionalKeys() {
		return new String[] { "APP_ADMIN_MAIL", "MAIN_WEB_SITE_URL", "MAIN_LOGO_URL", "APP_URL_ALIAS", "APP_TEMP_DIR", "APP_ACT_ALIASES_MAIL",
				"APP_FORCE_CONTACTS_LEVEL", "APP_ERROR_DESCRIPTION", "APP_WARN_NO_MAIL_SERVICE", "APP_MAIL_SUBJECT_FORMAT", "GRHUM_VLAN_ADMIN",
				"GRHUM_VLAN_RECHERCHE", "GRHUM_VLAN_ETUD", "GRHUM_VLAN_LOCAL", "GRHUM_VLAN_EXTERNE", "APP_USE_GEDFS", "APP_USE_MAIL",
				"APP_USE_JEFYCO", "APP_FORCE_LBUD_LEVEL", "APP_FORCE_DESTIN_LEVEL", "APP_USE_PIE", "LISTE_IP_FORCE_SSO_LOGOUT",
				"APP_PRESTA_WS_URL", "APP_PRESTA_WS_PASS", "REPRO_CODE_IMPUTATION", "REPRO_NB_COUV_A4R_COD", "REPRO_NB_COUV_A4RV_COD",
				"REPRO_NB_COUV_A3R_COD", "REPRO_NB_COUV_A3RV_COD", "REPRO_NB_TRANSP_A4_COD", "REPRO_CL_COUV_A4R_COD",
				"REPRO_CL_COUV_A4RV_COD", "REPRO_CL_COUV_A3R_COD", "REPRO_CL_COUV_A3RV_COD", "REPRO_CL_TRANSP_A4_COD",
				"REPRO_PLASTIF_A4_COD", "REPRO_PLASTIF_A3_COD", "REPRO_RELIURE_COD", "REPRO_RELIURE_COD", "REPRO_CATALOGUE_COD",
				"APP_SAM_MESSAGES_MAIL", "APP_SHOW_CONTACT_POLE", I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY,
				I_ApplicationConsts.PHRASE_SURCOUT_POSTE_NON_RESTITUE_KEY, I_ApplicationConsts.PHRASE_SURCOUT_POSTE_NON_RESTITUE_CLOTURE_KEY,
				I_ApplicationConsts.GLPI_WS_URL_KEY, I_ApplicationConsts.GLPI_WS_USER_KEY, I_ApplicationConsts.GLPI_FRONT_COMPUTER_URL_KEY };
	}

	// extinction programmee par l'administrateur

	/** la duree en secondes entre demande d'arret et arret souhaitee */
	private int secondsUntilStop;

	/** le moment ou il a ete demande l'arret de l'application */
	private long appBeginStopTime;

	/** indique si la procedure d'arret est en route */
	private boolean appWillStop;

	/**
	 * Lancer l'arret programme de l'application dans
	 * <code>someSecondsUntilStop</code> secondes. - notifier les sessions en
	 * cours que l'arret est programme - lancer le thread de decompte
	 */
	public void scheduleAppStop(int someSecondsUntilStop) {
		appWillStop = true;
		secondsUntilStop = someSecondsUntilStop;
		appBeginStopTime = System.currentTimeMillis();
		TasksLauncher tasksLauncher = new TasksLauncher();
		tasksLauncher.start();
	}

	/**
	 * Indiquer que l'application est en procedure d'arret
	 */
	public boolean appWillStop() {
		return appWillStop;
	}

	/**
	 * le temps restant de vie de l'application. format mm:ss
	 */
	public String appTtl() {
		int appSecondesTtl = (int) ((appBeginStopTime + secondsUntilStop * 1000 - System.currentTimeMillis()) / (long) 1000);
		int minutes = (appSecondesTtl > 60 ? appSecondesTtl / 60 : 0);
		String strMinutes = Integer.toString(minutes);
		if (strMinutes.length() == 1) {
			strMinutes = "0" + strMinutes;
		}
		String strSeconds = Integer.toString(appSecondesTtl - minutes * 60);
		if (strSeconds.length() == 1) {
			strSeconds = "0" + strSeconds;
		}
		return strMinutes + "min" + strSeconds + "s";
	}

	/**
	 * Classe assurant la plannification de l'arret de DT
	 * 
	 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
	 */
	private class TasksLauncher extends Thread {

		public TasksLauncher() {
			super();
		}

		public void run() {
			Timer timer = new Timer(true);
			timer.schedule(new TaskStopDT(), 0, 1000 * secondsUntilStop);
		}

		/**
		 * Classe permettant d'arreter DT
		 * 
		 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
		 */
		private class TaskStopDT extends TimerTask {

			private int callCount;

			public TaskStopDT() {
				super();
				callCount = 0;
			}

			public void run() {
				callCount++;
				// 1er appel : juste un message
				if (callCount == 1) {
					CktlLog.log(">> L'application va s'arreter dans " + secondsUntilStop + " secondes.");
				} else if (callCount == 2) {
					// 2eme appel : message + arret de l'appli
					CktlLog.log(">> L'application s'arrete.");
					// notifier l'administrateur de l'arret
					String contactMail = contactMail();
					if (!StringCtrl.isEmpty(contactMail)) {
						mailBus().sendMail(contactMail, contactMail, null, "Arret de DT",
								"L'application vient d'etre arretee");
					}
					System.exit(-1);
				}
			}
		}
	}

	/**
	 * Retourne le code d'imputation a utiliser lors de la creation d'un devis
	 * repro. Cette methode retourne la valeur du parametre de configuration
	 * <code>REPRO_CODE_IMPUTATION</code>. Retourne <em>null</em> dans le cas ou
	 * ce code n'est pas defini.
	 */
	public String defaultReproPcoNum() {
		return config().stringForKey("REPRO_CODE_IMPUTATION");
	}

	/**
	 * TODO provient de YCRIFwk pour avoir CktlAlertPage -> pb depuis passage dans
	 * les package gestion des exceptions : redirection vers une page d'erreur
	 * puis envoi du mail a l'adresse {@link #appErrorMail()} si defini, sinon a
	 * adm
	 */
	@Override
	public WOResponse handleException(Exception anException, WOContext aContext) {
		CktlLog.log("- Exception");
		anException.printStackTrace();
		if (anException.getCause() != null) {
			CktlLog.log("- Exception.getCause()");
			anException.getCause().printStackTrace();
		}
		CktlLog.log("-----------");
		String contenuPage =
				"Une erreur est survenue dans l'application ...<br>" +
						"Un mail contenant le message d'erreur a &eacute;t&eacute; envoy&eacute; &agrave; l'administrateur de l'application.<br>" +
						"L'erreur sera corrig&eacute;e au plus vite.<br><br>" +
						"Contenu de l'erreur :<br><br>" +
						"<textarea cols='75' rows='15'>" +
						UtilException.stackTraceToString(anException, false) +
						(anException.getCause() != null ?
								"\nCaused by : " + UtilException.stackTraceToString(anException.getCause(), false) : "") +
						"</textarea><br><br>" +
						"<center><a href='" + getApplicationInstanceURL(aContext) + "'>Fermer la session</a></center>";

		CktlAlertPage page = (CktlAlertPage) pageWithName(CktlAlertPage.class.getName(), aContext);
		page.showMessage(null, "Une erreur est survenue ...", contenuPage, null, null, null, CktlAlertPage.ERROR, null);
		String contenuMail = "Une exception est survenue ...\n";
		contenuMail += "Elle provient de la machine qui a l'ip *" + getRequestIPAddress(aContext.request()) + "*\n";
		contenuMail += "La personne qui a provoqué l'erreur est : ";
		Session dtSession = ((Session) aContext.session());
		DTUserInfo uiConnected = dtSession.dtUserInfo();
		if (uiConnected != null) {
			contenuMail += "*" + uiConnected.nomEtPrenom() + "*\n\n";
			contenuMail += uiConnected.toString();
		} else {
			contenuMail += "????????]";
		}
		contenuMail += "\nsessionId = " + dtSession.sessionID();
		contenuMail += "\n\n";
		//
		String to = config().stringForKey(CktlConfig.CONFIG_APP_ADMIN_MAIL_KEY);
		String from = to;
		if (uiConnected != null && !StringCtrl.isEmpty(uiConnected.email())) {
			from = uiConnected.email();
		}
		UtilException.sendExceptionTrace(mailBus(), name(), from, to, contenuMail, anException);
		WOResponse response = page.generateResponse();
		return response;
	}

}