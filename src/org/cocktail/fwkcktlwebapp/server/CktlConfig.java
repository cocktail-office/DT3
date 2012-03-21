package org.cocktail.fwkcktlwebapp.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.foundation.ERXProperties;

/**
 * TODO 01/09/2011 surchage afin de forcer la lecture des fichiers en UTF8. A
 * mettre dans le framework dès que tous les tests sont OK
 * 
 * Cette classe permet de gerer les parametres d'une application. Les parametres
 * peuvent etre enregistres localement dans une instace de la classe, dans un
 * fichier, stockes dans une base de donnees ou disponibles dans l'environement
 * de l'application.
 * 
 * <p>
 * Tous les parametres sont representes sous forme de couple <i>cle-valeur</i>.
 * En general, une seule valeur correspond a une cle, mais il est possible
 * d'avoir plusieures valeurs associees a une cle.
 * </p>
 * 
 * <p>
 * Cette classe propose les methodes pour la lecture des parametres. Elle
 * implemente l'interface definie par {@link Dictionary}
 * </p>
 * 
 * 
 * <h3>Fichier de configuration</h3>
 * 
 * Cette classe accepte les fichiers de configuration en format XML ou Java
 * Properties.
 * 
 * <p>
 * Si le fichier est en format XML, la balise <code>&lt;config&gt;</code> doit
 * etre sa balise racine. Tous les parametres doivent avoir la definition
 * <code>&lt;cle&gt;valeur&lt;/cle&gt;</code>. Un seul niveau des balises est
 * accepte. Toutes les "sous-balises" sont ignorees.
 * </p>
 * 
 * <p>
 * Si le fichier est en format <i>properties</i>, alors chaque parametre doit
 * avoir la definition de type "<code>cle=valeur</code>". Chaque definition doit
 * etre donnee sur une seule ligne. Chaque nouvelle definition est commencee par
 * une nouvelle ligne. Les lignes vides et celles commencees par le symbole "#"
 * (commentaire) sont ignorees.
 * </p>
 * 
 * <h3>Acces aux parametres</h3>
 * 
 * Les methodes <code>get</code> de la classe {@link Hashtable} et de
 * l'interface {@link NSKeyValueCoding#valueForKey(String)} sont (re)definies
 * pour retourner les valeurs associes a la cle donnee. Ces methodes retourne
 * les valeurs de la classe {@link Object}. Toutes les valeurs de configurations
 * sont consideres comme chaines de caracteres. On peut dans ce cas utiliser la
 * methode {@link #stringForKey(String)} qui retourne les valeurs comme des
 * objets {@link String}. Si les types des valeurs sont connus, on peut
 * egalement faire appel aux methodes {@link #intForKey(String)} et
 * {@link #booleanForKey(String)}.
 * 
 * <p>
 * La classe {@link CktlConfig} implemente egalement les methodes
 * {@link #get(Object)} et {@link #takeValueForKey(Object, String)}. Elles
 * pemettent d'enregistrer localement les valeurs temporaires. Elles n'existent
 * que pendant le temps d'existance de l'instance de la classe.
 * </p>
 * 
 * 
 * <h3>Ordre des parametres</h3>
 * 
 * La valeur d'un parametre est recherchee dans l'ordre suivant&nbsp;:
 * <ol>
 * <li>la definition locale a l'instance de la classe. Cette valeur peut etre
 * definie en appelant la methode {@link #put(Object, Object)}. C'est donc une
 * definition temporaire et valable pedant le temps d'existance de cette
 * instance de la classe&nbsp;;</li>
 * <li>la definition dans le fichier de configuration&nbsp;;</li>
 * <li>la definition dans la base de donnees. Si plusieures definitions
 * correspondent au parametre, elles peuvent etre retournees dans un
 * tableau&nbsp;;</li>
 * <li>la definition dans l'environement de l'application. C'est la definition
 * disponible dans la classe Java {@link System}.</li>
 * </ol>
 * 
 * La premiere definition trouvee est retournee. De cette maniere, les
 * definitions peuvent etre surchargees. Par exemple, si la definition d'un
 * parametre est donnee dans le fichier de configuration et dans la base de
 * donnees, alors la definition de fichier est utilisee.
 * 
 * 
 * <h3>Rafraichissement des parametres</h3>
 * 
 * Cette classe gere le cache memoire des parametres de configuration afin de
 * minimiser l'acces au disque et la base de donnees. Le cache peut ensiute etre
 * rafraichi pendant les intervalles regulieres pour prendre en compte leur
 * modification eventuelle. De cette maniere, les modifications des parametres
 * peuvent etre prises en compte immedaitement sans avoir recourt au redemarrage
 * de l'application.
 * 
 * <p>
 * L'intervalle de rechargement des parametres peut etre definie par la methode
 * {@link #setRefreshInterval(long)}. Si l'intervalle entre deux appels d'une
 * des methodes <code>xxxForKey</code> ou {@link #get(Object)} depasse la valeur
 * donnee, alors les parametres seront re-lus a partir des leurs sources.
 * Plusieurs cas sont possibles en fonction de la valeur de l'intervalle
 * <i>x</i> donnee.
 * </p>
 * 
 * <p>
 * Si l'intervalle <i>x</i> est superieure, alors les parametres sont relus
 * toutes les <i>x</i> secondes.
 * </p>
 * 
 * <p>
 * Si <i>x</i> est egale 0, alors les parametres sont relus a chaque appel
 * d'acces aux valeurs de configuration (a eviter !).
 * </p>
 * 
 * <p>
 * Si <i>x</i> est negative, alors les parametres ne sont jamais relus une fois
 * qu'ils sont mis dans le chache. Le redemarrage de l'application est dans ce
 * cas necessaire pour relire la configuration.
 * </p>
 * 
 * <p>
 * L'intervalle de rafraichissement par defaut est de 60 secondes.
 * </p>
 */
public class CktlConfig
		extends Hashtable
		implements NSKeyValueCoding {
	/**
	 * L'intervale en secondes par defaut de rafraichissement des valeurs de
	 * configuration.
	 */
	private static final int DEFAULT_REFRESH_INTERVAL = 60;

	/**
	 * Une instance d'un tableau vide. Elle represente la valeur retournee si
	 * aucune definition n'est trouvee.
	 */
	private static NSArray emptyArray = new NSArray();

	/**
	 * Une instance d'une chaine de caracteres vide. Elle est utilisee pour
	 * representer les valeurs vides.
	 */
	private static String emptyString = "";

	/**
	 * L'objet de communication avec la base de donnees.
	 */
	private CktlDataBus dataBus;

	/**
	 * Le nom de fichier de configuration.
	 */
	private String fileName;

	/**
	 * Le nom de la table des parametres dans la base de donnees.
	 */
	private String tableName;

	/**
	 * Le nom de la colonne de la table des parametres correspondant a la
	 * <i>cle</i> de parametre.
	 */
	private String keyColumnName;

	/**
	 * Le nom de la colonne de la table des parametres correspondant a la
	 * <i>valeur</i> de parametre.
	 */
	private String valueColumnName;

	/**
	 * La representation memoire des parametres du fichier de configuration.
	 */
	private Hashtable fileConfigData;

	/**
	 * La liste des cles de la base de donnees.
	 */
	private Vector dbConfigKeys;

	/**
	 * La liste des valeurs de la base de donnees. Chaque entree dans cette liste
	 * corresponde a une etree dans la liste <code>dbConfigKeys</code>.
	 */
	private Vector dbConfigValues;

	/**
	 * L'intervale en secondes de rafraichisement des valeurs de configuration
	 * gardees dans la memoire par rapport a leur source.
	 */
	private long refreshInterval;

	/**
	 * Le moment quand les valeurs d'un fichier de configuration ont ete
	 * rafraichies.
	 */
	private long lastFileRefreshTime;

	/**
	 * Le moment quand les valeurs de la base de donnees ont ete rafraichies.
	 */
	private long lastBaseRefreshTime;

	// /**
	// * Le nom de la balise racine si le fichier de configuration
	// * est en format XML.
	// */
	// private String xmlRootElement;

	/**
	 * Indique si le fichier de configuration est en format XML ou Properties de
	 * Java.
	 */
	private boolean xmlFormat;

	/**
	 * Indique si la configuration a partir d'un fichier de configuration a deja
	 * ete chargee.
	 */
	private boolean fileConfigLoaded;

	/**
	 * Indique si les valeurs nulles doivent etre ignorees dans la configuration
	 * de l'application.
	 */
	private boolean ignoreNullValues;

	// GEDFS
	public final static String CONFIG_APP_USE_GEDFS_KEY = "APP_USE_GEDFS";
	public final static String CONFIG_GEDFS_SERVICE_PORT_KEY = "GEDFS_SERVICE_PORT";
	public final static String CONFIG_GEDFS_SERVICE_HOST_KEY = "GEDFS_SERVICE_HOST";

	// mail
	public final static String CONFIG_APP_USE_MAIL_KEY = "APP_USE_MAIL";
	public final static String CONFIG_GRHUM_HOST_MAIL_KEY = "GRHUM_HOST_MAIL";

	// CAS
	public final static String CONFIG_CAS_USE_CAS_KEY = "CAS_USE_CAS";
	public final static String CONFIG_APP_USE_CAS_KEY = "APP_USE_CAS";
	public final static String CONFIG_CAS_SERVICE_URL_KEY = "CAS_SERVICE_URL";
	public final static String CONFIG_CAS_LOGIN_URL = "CAS_LOGIN_URL";
	public final static String CONFIG_CAS_LOGOUT_URL = "CAS_LOGOUT_URL";
	public final static String CONFIG_CAS_VALIDATE_URL = "CAS_VALIDATE_URL";

	// vlans
	public final static String CONFIG_GRHUM_VLAN_ADMIN_KEY = "GRHUM_VLAN_ADMIN";
	public final static String CONFIG_GRHUM_VLAN_RECHERCHE_KEY = "GRHUM_VLAN_RECHERCHE";
	public final static String CONFIG_GRHUM_VLAN_ETUD_KEY = "GRHUM_VLAN_ETUD";
	public final static String CONFIG_GRHUM_VLAN_LOCAL_KEY = "GRHUM_VLAN_LOCAL";
	public final static String CONFIG_GRHUM_VLAN_EXTERNE_KEY = "GRHUM_VLAN_EXTERNE";

	// noms des parametres de la configuration pour DateCtrl
	public final static String CONFIG_APP_DATECTRL_IGNORING_HOLIDAY_KEY = "APP_DATECTRL_IGNORING_HOLIDAY";
	public final static String CONFIG_APP_DATECTRL_ADDITIONAL_HOLIDAY_KEY = "APP_DATECTRL_ADDITIONAL_HOLIDAY";

	// proxy
	public final static String CONFIG_NET_PROXY_HOST_KEY = "NET_PROXY_HOST";
	public final static String CONFIG_NET_PROXY_PORT_KEY = "NET_PROXY_PORT";
	public final static String CONFIG_NET_NO_PROXY_HOSTS_KEY = "NET_NO_PROXY_HOSTS";

	// paybox
	public final static String CONFIF_ENCAISSEMENT_WEB = "ENCAISSEMENT_WEB";

	public final static String CONFIG_HTML_IMAGES_ROOT_KEY = "HTML_IMAGES_ROOT";
	public final static String CONFIG_APP_ADMIN_PASSWORD_KEY = "APP_ADMIN_PASSWORD";
	public final static String CONFIG_ACCEPT_EMPTY_PASSWORD_KEY = "ACCEPT_EMPTY_PASSWORD";
	public final static String CONFIG_ACCEPT_LOGINS_KEY = "ACCEPT_LOGINS";
	public final static String CONFIG_APP_ACCEPT_LOGINS_KEY = "APP_ACCEPT_LOGINS";
	public final static String CONFIG_APP_ID_KEY = "APP_ID";
	public final static String CONFIG_SAUT_ID_TRANSLATION_KEY = "SAUT_ID_TRANSLATION";
	public final static String CONFIG_APP_ALIAS_KEY = "APP_ALIAS";
	public final static String CONFIG_SAUT_URL_KEY = "SAUT_URL";
	public final static String CONFIG_MAIN_LOGO_URL_KEY = "MAIN_LOGO_URL";
	public final static String CONFIG_MAIN_WEB_SITE_URL_KEY = "MAIN_WEB_SITE_URL";
	public final static String CONFIG_ADMIN_MAIL_KEY = "ADMIN_MAIL";
	public final static String CONFIG_APP_ADMIN_MAIL_KEY = "APP_ADMIN_MAIL";
	public final static String CONFIG_DEFAULT_TIME_ZONE_KEY = "DEFAULT_TIME_ZONE";

	public final static String DEFAULT_TIME_ZONE_VALUE = "CEST";

	private static ERXProperties erxProperties;

	/**
	 * Creer une instance de la classe qui va utiliser les parametres enregistres
	 * dans le fichier de configuration <code>fileName</code>.
	 * 
	 * <p>
	 * Le fichier doit respecter la syntaxe des <i>properties</i> de Java. Il est
	 * constitue des expressions "cle=valeur". Chaque definition doit commencer
	 * par un nouvelle ligne. Le symbole "#" au debut de la ligne definit le
	 * commentaire.
	 * </p>
	 * 
	 * @see #CktlConfig(String, boolean)
	 */
	public CktlConfig(String fileName) {
		this(fileName, false);
	}

	/**
	 * Creer une instance de la classe qui va utiliser les parametres enregistres
	 * dans le fichier de configuration <code>fileName</code>. Le parametre
	 * <code>xmlFormat</code> indique si le fichier est en format <i>XML</i> ou
	 * <i>Java Properties</i>.
	 * 
	 * <p>
	 * Si le fichier est en format <i>properties</i> de Java, il doit etre
	 * constitue des expressions "cle=valeur". Chaque definition doit commencer
	 * par un nouvelle ligne. Le symbole "#" au debut de la ligne definit le
	 * commentaire.
	 * </p>
	 * 
	 * <p>
	 * Si le format est XML, alors tous les parametres doivent etre enregistrees
	 * sous forme de <code>&lt;cle&gt;valeur&lt;cle&gt;</code>. L'element racine
	 * de fichier XML doit etre la balise <code>&lt;config&gt;</code>.
	 * </p>
	 */
	public CktlConfig(String fileName, boolean xmlFormat) {
		this.dataBus = null;
		this.fileName = fileName;
		this.tableName = null;
		this.lastFileRefreshTime = 0;
		this.lastBaseRefreshTime = 0;
		this.keyColumnName = "paramKey";
		this.valueColumnName = "paramValue";
		this.xmlFormat = xmlFormat;
		this.fileConfigData = new Hashtable();
		this.dbConfigKeys = new Vector();
		this.dbConfigValues = new Vector();
		this.fileConfigLoaded = false;
		setRefreshInterval(DEFAULT_REFRESH_INTERVAL);
	}

	/**
	 * Definit les objects pour la recherche des parametres dans la base de
	 * donnees. Les deux <code>db</code> et <code>tableName</code> ne doit pas
	 * etre <i>null</i>. La table de configuration <code>tableName</code> doit
	 * comporter au moins deux colonnes avec les cles et les valeurs des
	 * parametres.
	 * 
	 * @param db
	 *          Le gestionnaire d'acces a la base de donnees.
	 * @param tableName
	 *          Le nom de la table avec les definitions des parametres. Ceci doit
	 *          etre le nom d'une entite dans le EOModel de l'application.
	 * 
	 * @see #setKeyColumnName(String)
	 * @see #setValueColumnName(String)
	 */
	public void setDatabase(CktlDataBus db, String tableName) {
		this.dataBus = db;
		this.tableName = tableName;
	}

	/**
	 * Definit le nom de la colonne de la table de configuration qui correspond
	 * aux noms des parametres (cles). Par defaut, la colonne des cles a le nom
	 * "paramKey".
	 * 
	 * @param keyColumnName
	 *          Le nom de la colonne des cles.
	 * 
	 * @see #setValueColumnName(String)
	 */
	public void setKeyColumnName(String keyColumnName) {
		this.keyColumnName = keyColumnName;
	}

	/**
	 * Definit le nom de la colonne de la table de configuration qui correspond
	 * aux valeurs. Par defaut, la colonne des valeurs a le nom "paramValue".
	 * 
	 * @param valueColumnName
	 *          Le nom de la colonne des valeus.
	 * 
	 * @see #setKeyColumnName(String)
	 */
	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

	/**
	 * Definit l'intervalle en secondes de rafraichisement des parametres de
	 * configuration par rapport a leur source.
	 * 
	 * <p>
	 * Si l'intervalle entre deux appels des methodes <code>xxxForKey</code>
	 * depasse <code>refreshInterval</code>, alors les parametres seront re-lus a
	 * partir des leurs sources.
	 * 
	 * <p>
	 * Si <code>refreshInterval</code> est egale 0, alors les parametres sont
	 * relus a chaque appel des methodes <code>xxxForKey</code> (a eviter !).
	 * </p>
	 * 
	 * <p>
	 * Si <code>refreshInterval</code> est negatif, alors les parametres ne sont
	 * jamais relus une fois au'ils sont mis dans le chache. Le redemarrage de
	 * l'application est dans ce cas necessaire pour relire la configuration.
	 * </p>
	 * 
	 * <p>
	 * L'intervalle de rafraichissement par defaut est de 60 secondes.
	 * </p>
	 * 
	 * @param refreshInterval
	 *          L'intervalle en secondes.
	 */
	public void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval * 1000;
	}

	/**
	 * Indique si les parametres dont les valeurs sont nulles, doivent etre
	 * ignorees. Si le parametre <code>ignore</code> est <code>true</code>, alors
	 * toutes les valeurs nulles seront transformees en chaines vides avant d'etre
	 * retourne.
	 * 
	 * <p>
	 * Par defaut, les valeurs nulles sont ignorees.
	 * </p>
	 */
	public void setIgnoreNullValues(boolean ignore) {
		this.ignoreNullValues = ignore;
	}

	/**
	 * Force la relecture de la configuration a partir d'un fichier de
	 * configuration.
	 */
	public void refresh() {
		lastFileRefreshTime = 0;
		lastBaseRefreshTime = 0;
		reloadFileConfig();
	}

	/**
	 * Reinitialise les parametres de configuration en les relisant a partir d'un
	 * fichier.
	 */
	private void reloadFileConfig() {
		fileConfigLoaded = true;
		synchronized (this) {
			// Ancienne configuration
			Hashtable oldConfig = fileConfigData;
			lastFileRefreshTime = nowTime();
			FileInputStream in = null;
			if (fileName != null) {
				try {

					in = new FileInputStream(fileName);

					if (xmlFormat)
						reloadXMLFileConfig(in);
					else {
						File f = new File(fileName);
						int size = ((int) f.length()) * 8;
						reloadPropertiesFileConfig(in, size);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					fileConfigData = oldConfig;
				} finally {
					try {
						in.close();
					} catch (Throwable e) {
					}
				}
			}
		}
	}

	/**
	 * Relit les parametres de configuration enregistres dans un fichier de type
	 * <i>Java Properties</i>.
	 */
	private void reloadPropertiesFileConfig(InputStream in, int size)
			throws IOException {
		fileConfigData = new Properties();

		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

		byte[] ba = new byte[size];

		int i = 0;
		int b = br.read();
		while (b != -1) {
			ba[i] = (byte) b;
			b = br.read();
			i++;
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(ba);

		((Properties) fileConfigData).load(bais);
	}

	/**
	 * Relit les parametres de configuration enregistres dans un fichier en format
	 * XML.
	 */
	private void reloadXMLFileConfig(InputStream in)
			throws ParserConfigurationException, IOException, SAXException {
		InputSource fis = new InputSource(new FileReader(fileName));
		SAXParserFactory spf = SAXParserFactory.newInstance();
		XMLReader parser = spf.newSAXParser().getXMLReader();
		parser.setContentHandler(new XMLConfigLoader());
		parser.setFeature("http://xml.org/sax/features/namespaces", true);
		parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		// parser.setFeature("http://apache.org/xml/features/validation/schema",
		// false);
		parser.parse(fis);
	}

	/**
	 * Recharge les parametres de configuration disponibles dans la base de
	 * donnees. Pour des raisons d'efficasite, cette methode charge toutes les
	 * parametres dans la memoire. Ils sont ensuite "filtres" et reutilises par la
	 * methode {@link #valuesForKeyFromBase(String)}.
	 * 
	 * @see #valuesForKeyFromBase(String)
	 */
	private void reloadBaseConfig() {
		dbConfigKeys.removeAllElements();
		dbConfigValues.removeAllElements();
		if ((dataBus != null) && CktlDataBus.isDatabaseConnected() &&
				(tableName != null)) {
			EOEnterpriseObject rec;
			EOEditingContext ec = new EOEditingContext();
			Enumeration records =
					dataBus.fetchArray(ec, tableName, null, null).objectEnumerator();
			lastBaseRefreshTime = nowTime();
			while (records.hasMoreElements()) {
				rec = (EOEnterpriseObject) records.nextElement();
				// Ignorer si le nom de parametre est inconnu
				if (rec.valueForKey(keyColumnName) != null) {
					// Ignorer, s'il faut ignorer les valeurs nulles
					if ((rec.valueForKey(valueColumnName) != null) ||
							(!ignoreNullValues)) {
						dbConfigKeys.addElement(rec.valueForKey(keyColumnName));
						if (rec.valueForKey(valueColumnName) == null)
							dbConfigValues.addElement(emptyString);
						else
							dbConfigValues.addElement(rec.valueForKey(valueColumnName));
					}
				}
			}
		}
	}

	/**
	 * Retourne la liste des valeurs correspondant a la cle <code>key</code>. Si
	 * aucune valeur n'est trouvee, alors la liste est vide est retournee. Les
	 * valeurs correspondant a la cle sont recherchees en local, dans le fichier
	 * de configuration, dans la base de donnees ou dans l'environement Java de
	 * l'application.
	 * 
	 * <p>
	 * Voir la documentation de la classe pour plus d'informations.
	 * </p>
	 * 
	 * @see CktlConfig
	 */
	public NSArray valuesForKey(String key) {
		NSMutableArray params = new NSMutableArray();
		String value;

		synchronized (this) {
			// D'abord, les parametres definis localement
			value = (String) super.get(key);
			// ajout suite seminaire fevrier 2011 : gestion des ERXProperties
			// On regarde dans le fichier properties de wonder
			if (value == null)
				value = ERXProperties.stringForKey(key);
			// Sinon, les parametres dans le fichier de config
			if (value == null)
				value = valueForKeyFromFile(key);
			if (value != null)
				params.addObject(value);
			// Sinon, on va chercher dans la base de donnees
			if (params.count() == 0) {
				params.addObjectsFromArray(valuesForKeyFromBase(key));
			}
			// En fin, parmi les proprietes du systeme
			// cela va aussi chercher dans les properties de wonder !
			if (params.count() == 0) {
				value = valueForKeyFromSystem(key);
				if (value != null)
					params.addObject(value);
			}
		}
		return params;
	}

	/**
	 * Retourne la valeur qui correspond a la cle <code>key</code>. Retourne
	 * <code>null</code> si aucune valeur n'est trouvee. Si plusieures valeurs
	 * correspondent a la cle <code>key</code>, alors la premiere valeur trouvee
	 * est retournee.
	 * 
	 * <p>
	 * Toutes les valeurs de configuration sont geres en tant que chaines de
	 * caracteres. Cette methode retourne une instance {@link Object}, car ceci
	 * est impose par l'interface {@link NSKeyValueCoding}. Utilisez la methode
	 * {@link #stringForKey(String)} pour recupere une valeur {@link String}.
	 * </p>
	 * 
	 * @see #stringForKey(String)
	 * @see #takeValueForKey(Object, String)
	 */
	public Object valueForKey(String key) {
		NSArray params = valuesForKey(key);
		if (params.count() > 0)
			return (String) params.objectAtIndex(0);
		return null;
	}

	/**
	 * Retourne la valeur qui correspond a la cle <code>key</code>. Retourne
	 * <code>null</code> si aucune valeur n'est trouvee. Si plusieures valeurs
	 * correspondent a la cle <code>key</code>, alors la premiere valeur trouvee
	 * est retournee.
	 * 
	 * <p>
	 * Toutes les valeurs de configuration sont geres en tant que chaines de
	 * caracteres.
	 * </p>
	 * 
	 * @see #intForKey(String)
	 * @see #booleanForKey(String)
	 */
	public String stringForKey(String key) {
		return (String) valueForKey(key);
	}

	/**
	 * Retourne une valeur entiere correspondant a la cle <code>key</code>.
	 * Retourne -1 si la valeur de <code>key</code> n'existe pas ou elle ne
	 * represente pas une expression entiere.
	 * 
	 * @see #stringForKey(String)
	 */
	public int intForKey(String key) {
		String value = stringForKey(key);
		if (value == null)
			return -1;
		else
			return StringCtrl.toInt(value, -1);
	}

	/**
	 * Retourne une valeur booleenne correspondant a la cle <code>key</code>.
	 * Retroune <code>false</code> si la valeur de <code>key</code> n'existe pas
	 * ou elle ne represente pas une expression booleenne.
	 * 
	 * <p>
	 * La valeur <code>true</code> est retournee uniquement si la valeur de
	 * <code>key</code> est egale a une des valeurs : "OUI", "VRAI, "YES", "TRUE"
	 * ou "1".
	 * </p>
	 */
	public boolean booleanForKey(String key) {
		String value = stringForKey(key);
		if (value == null)
			return false;
		else
			return StringCtrl.toBool(value);
	}

	/**
	 * Retourne la valeur correspondant a la cle <code>key</code> enregistree dans
	 * un fichier de configuration. Retourne la valeur <code>null</code> si la cle
	 * n'existe pas ou le fichier de configuration n'est pas defini.
	 */
	private String valueForKeyFromFile(String key) {
		if ((!fileConfigLoaded) ||
				((refreshInterval >= 0) &&
					((nowTime() - lastFileRefreshTime) > refreshInterval)))
			reloadFileConfig();
		return (String) fileConfigData.get(key);
	}

	/**
	 * Retourne la valeur correspondant a la cle <i>key</i> disponible dans
	 * l'environement Java de l'application. Retourne la valeur <code>null</code>
	 * si aucune valeur ne correspond a <code>key</code>.
	 * 
	 * <p>
	 * Au demarrage de l'application, WebObjects ajoute les parametres enregistres
	 * dans le fichier <i>Properties</i> donnes dans les ressources de
	 * l'application. Il y inclut egalement les parametres passes en ligne de
	 * commande.
	 * </p>
	 */
	private String valueForKeyFromSystem(String key) {
		try {
			return System.getProperty(key);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retourne les valeurs correspondant a la cle <code>key</code> disponibles
	 * dans la base de donnees. Retourne le tableau vide si aucune valeur n'est
	 * trouvee.
	 * 
	 * <p>
	 * Attention, la valeur de <code>key</code> est "case sensitive".
	 * </p>
	 */
	private NSArray valuesForKeyFromBase(String key) {
		NSMutableArray values = null;
		if ((dataBus != null) && CktlDataBus.isDatabaseConnected() &&
				(tableName != null)) {
			if ((refreshInterval >= 0) &&
					((nowTime() - lastBaseRefreshTime) > refreshInterval))
				reloadBaseConfig();
			for (int i = 0; i < dbConfigKeys.size(); i++) {
				if (dbConfigKeys.elementAt(i).equals(key)) {
					// On cree valuers seulement si on trouve qqchose,
					// sinon, on reutilisera emptyArray - c plus economique
					if (values == null)
						values = new NSMutableArray();
					values.addObject(dbConfigValues.elementAt(i));
				}
			}
		}
		if (values == null)
			return emptyArray;
		else
			return values;
	}

	/**
	 * Return le moment en cours en milisecondes.
	 */
	private long nowTime() {
		return (new Date()).getTime();
	}

	/**
	 * Retourne la valeur associee a la cle <code>key</code>. Retourne
	 * <code>null</code> si aucune valeur n'est definie. La cle <code>key</code>
	 * doit etre un objet {@link String}.
	 * 
	 * <p>
	 * Cette methode est synonyme de la methode {@link #valueForKey(String)}.
	 * </p>
	 * 
	 * @see #valueForKey(String)
	 */
	public Object get(Object key) {
		return valueForKey(key.toString());
	}

	/**
	 * Definie une valeur <code>value</code> associee a la cle <code>key</code>.
	 * 
	 * <p>
	 * Cette methode permet d'enregistrer localement les valeurs, qui seront
	 * valables pendant le temps d'execution de l'application. Dans ce cas, la
	 * definition des valeurs est surchargee par rapport a autres sources (par
	 * exemple, par rapport au fichier de configuration).
	 * </p>
	 * 
	 * <p>
	 * Attention, cet appel ne modifie pas des les valeurs de fichier de
	 * configuration ou de la base de donnees.
	 * </p>
	 * 
	 * <p>
	 * Cette methode est synonime de la methode {@link #put(Object, Object)} de
	 * {@link Hashtable}
	 * </p>
	 * 
	 * @see Hashtable#put(Object, Object)
	 */
	public void takeValueForKey(Object value, String key) {
		this.put(key, value);
	}

	/**
	 * L'analyseur de fichier de configuration en format XML.
	 * 
	 * <p>
	 * Chaque fichier de configuration doit avoir la balise-racine
	 * <code>&lt;config&gt;</code>. La case de la balise est ignoree. Chaque
	 * definition de parametre doit avoir le format
	 * <code>&lt;cle&gt;valeur&lt;/cle&gt;</code>
	 * </p>
	 */
	private class XMLConfigLoader extends DefaultHandler {
		/**
		 * La balise racine de document XML.
		 */
		private final String rootTag = "config";

		/**
		 * La balise en cours.
		 */
		private String currentTag = null;

		/**
		 * La valeur en cours correspondant a la balise en cours.
		 */
		private String currentValue = null;

		/**
		 * Indique si l'analyse de document a commence et la balise racine a ete
		 * detectee.
		 */
		private boolean started = false;

		/**
		 * Indique si on se trouve dans la balise en cours.
		 */
		private boolean inTag = false;

		/**
		 * Informe que le debut d'une nouvelle balise a ete detecte.
		 */
		public void startElement(String uri, String localName,
															String qName, Attributes atts)
				throws SAXException {
			if (started) {
				inTag = (currentTag == null);
				if (inTag)
					currentTag = localName;
			} else {
				if (localName.toUpperCase().equals(rootTag.toUpperCase()))
					started = true;
				else
					throw new SAXException("Fichier de configuration XML doit avoir la racine \"" + rootTag + "\"");
			}
		}

		/**
		 * Indique que la fin d'une balise <code>localName</code> a ete detectee.
		 */
		public void endElement(String uri, String localName, String qName) {
			if (started && inTag) {
				inTag = false;
				if ((currentTag != null) && (currentValue != null))
					fileConfigData.put(currentTag, currentValue);
				currentTag = null;
				currentValue = null;
			}
		}

		/**
		 * Indique que les nouvelles donnees ont ete lues pour la balise en cours.
		 */
		public void characters(char[] ch, int start, int len) {
			String value = new String(ch, start, len);
			if (started && inTag) {
				currentValue = concatValue(currentValue, value);
			}
		}

		/**
		 * Ajoute la chaine de caracteres <code>newValue</code> a la chaine
		 * <code>prevValue</code> et retourne la nouvelle valeur obtenue.
		 */
		private String concatValue(String prevValue, String newValue) {
			if (prevValue == null)
				return newValue;
			else if (newValue.trim().length() > 0)
				return prevValue + newValue;
			else
				return prevValue;
		}
	}
}
