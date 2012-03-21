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
import java.net.InetAddress;

import org.cocktail.dt.server.metier.EOAffectation;
import org.cocktail.dt.server.metier.EOCompte;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOPrefAppli;
import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.fwkcktldroitsutils.common.metier.EOVlans;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlWebApplication;
import org.cocktail.fwkcktlwebapp.server.database._CktlBasicDataBus;

import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gere les infos de l'utilisateur specifiques a l'application : gestion des
 * droits, infos personnelles, etc.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTUserInfo extends CktlUserInfoDB {
	/** Niveau des droits "demandeur simple". */
	public static final int DROIT_DEMANDEUR = 0;

	/** Niveau des droits "intervenant limite". */
	public static final int DROIT_INTERVENANT_LIMITE = 1;

	/** Niveau des droits "intervenant". */
	public static final int DROIT_INTERVENANT = 2;

	/** Niveau des droits "intervenant avec les pouvoirs". */
	public static final int DROIT_INTERVENANT_SUPER = 3;

	/** Niveau des droits "administrateur de service". */
	public static final int DROIT_ADMIN = 4;

	/** Niveau des droits "administrateur de l'application". */
	public static final int DROIT_ADMIN_SUPER = 5;

	/** Une collection des informations sur l'utilisateur. */
	private NSMutableDictionary infos;

	public final static String CONTROLE_CHEVAUCHEMENT_PLANNING_KEY = "prefControleChevauchementPlanning";
	public final static String CONFIRMATION_CLOTURE_KEY = "prefConfimationCloture";
	public final static String SAUVEGARDER_PANIER_KEY = "prefSauvegarderPanier";
	public final static String PANIER_KEY = "prefPanier";

	/**
	 * Cree une instance de l'objet avec la configuration par defaut.
	 * 
	 * @see #DTUserInfo(_CktlBasicDataBus)
	 */
	public DTUserInfo() {
		this(((CktlWebApplication) WOApplication.application()).dataBus());
	}

	/**
	 * Cree une instance de l'objet avec la configuration adapte au model de
	 * donnes de l'application. Elle utilise les tables du model pour gerer les
	 * informations sur un personne : nom, compte, etc...
	 */
	public DTUserInfo(_CktlBasicDataBus dataBus) {
		super(dataBus);
		setDataBaseModel(
				EOIndividu.ENTITY_NAME,
				EOStructure.ENTITY_NAME,
				EOCompte.ENTITY_NAME,
				EOAffectation.ENTITY_NAME,
				EOVlans.ENTITY_NAME);
		infos = new NSMutableDictionary();
	}

	/**
	 * Retourne la reference vers l'instance de l'application en cours de
	 * l'execution.
	 */
	public Application dtApp() {
		return (Application) WOApplication.application();
	}

	/**
	 * Efface les informations utilisateur collectees dans le dictionnaire
	 * interne.
	 */
	public void resetUserInfo() {
		infos.removeAllObjects();
	}

	/**
	 * Les droits attach�s aux structure. On creer un dico avec autant d'entr�es
	 * que de services, avec association : niveau de droits <-> c_structure.
	 * 
	 * Si le dico des droits n'existe pas, il est instancie avec les valeurs
	 * passees en parametre
	 * 
	 * @param droits
	 *          : le niveau de droit
	 * @param cStructure
	 *          : le service DT
	 */
	public void setDroits(Number droits, String cStructure) {
		Number niveau = droits == null ? new Integer(0) : droits;
		NSMutableDictionary dicoDroits = (NSMutableDictionary) infos.objectForKey("dtDroits");
		if (dicoDroits == null)
			dicoDroits = new NSMutableDictionary(niveau, cStructure);
		else
			dicoDroits.setObjectForKey(niveau, cStructure);
		infos.setObjectForKey(dicoDroits, "dtDroits");
	}

	/**
	 * On recupere le droit associe a un service
	 * 
	 * @param cStructure
	 *          : le service DT
	 */
	public Number droits(String cStructure) {
		Number droit = new Integer(0);
		NSMutableDictionary dicoDroits = (NSMutableDictionary) infos.objectForKey("dtDroits");
		if (dicoDroits != null) {
			Number valDroit = (Number) dicoDroits.objectForKey(cStructure);
			if (valDroit != null)
				droit = valDroit;
		}
		return droit;
	}

	public void setDTServiceCode(String code) {
		infos.setObjectForKey(code, "dtServiceCode");
	}

	public String dtServiceCode() {
		return (String) infos.objectForKey("dtServiceCode");
	}

	/**
	 * La liste des services sur lesquels il a des droits
	 */
	public NSArray listeDtServiceCode() {
		NSMutableDictionary dicoDroits = (NSMutableDictionary) infos.objectForKey("dtDroits");
		return dicoDroits.allKeys();
	}

	public void setDTServiceLibelle(String libelle) {
		infos.setObjectForKey(libelle, "dtServiceLibelle");
	}

	public String dtServiceLibelle() {
		return (String) infos.objectForKey("dtServiceLibelle");
	}

	public void setPreferences(NSDictionary prefs) {
		if ((prefs == null) || (prefs.count() == 0))
			return;
		infos.setObjectForKey(prefs.objectForKey("prfEtatCode"), "prefEtatCode");
		infos.setObjectForKey(prefs.objectForKey("prfOnglet"), "prefOnglet");
		// TODO champ utilise uniquement par DTWindows
		infos.setObjectForKey(prefs.objectForKey("prfOrdreColumns"), "prefOrdreColumns");
		infos.setObjectForKey(prefs.objectForKey("prfPhoto"), "prefPhoto");
		infos.setObjectForKey(prefs.objectForKey("prfTimer"), "prefTimer");
		infos.setObjectForKey(prefs.objectForKey("prfTri"), "prefTri");
		infos.setObjectForKey(prefs.objectForKey("prfMailTraitement"), "prefMailTraitement");
		infos.setObjectForKey(prefs.objectForKey("prfTriInt"), "prefTriInt");
		infos.setObjectForKey(prefs.objectForKey("prfTriTra"), "prefTriTra");
		infos.setObjectForKey(prefs.objectForKey("prfAideSysNav"), "prefAideSysNav");
		if (prefs.objectForKey("prfNoIndIntervenant") != null)
			infos.setObjectForKey(prefs.objectForKey("prfNoIndIntervenant"), "prefNoIndIntervenant");
		infos.setObjectForKey(prefs.objectForKey("prfNbIntPerPage"), "prefNbIntPerPage");
		infos.setObjectForKey(prefs.objectForKey("prfUseMailInterne"), "prefUseMailInterne");
		infos.setObjectForKey(prefs.objectForKey("prfUseCoulBat"), "prefUseCoulBat");
		infos.setObjectForKey(prefs.objectForKey("prfInsertDtSig"), "prefInsertDtSig");
		infos.setObjectForKey(prefs.objectForKey("prfExportPlanning"), "prefExportPlanning");
		infos.setObjectForKey(prefs.objectForKey(EOPrefAppli.PRF_CONTROLE_CHEVAUCHEMENT_PLANNING_KEY), CONTROLE_CHEVAUCHEMENT_PLANNING_KEY);
		infos.setObjectForKey(prefs.objectForKey(EOPrefAppli.PRF_CONFIRMATION_CLOTURE_KEY), CONFIRMATION_CLOTURE_KEY);
		infos.setObjectForKey(prefs.objectForKey(EOPrefAppli.PRF_SAUVEGARDER_PANIER_KEY), SAUVEGARDER_PANIER_KEY);
		String panier = (String) prefs.objectForKey(EOPrefAppli.PRF_PANIER_KEY);
		if (!StringCtrl.isEmpty(panier)) {
			infos.setObjectForKey(panier, PANIER_KEY);
		}
	}

	public String startEtatCode() {
		return (String) infos.objectForKey("prefEtatCode");
	}

	/**
	 * @deprecated ??
	 * @return
	 */
	public String ordreColumns() {
		return (String) infos.objectForKey("prefOrdreColumns");
	}

	public Number startOnglet() {
		return (Number) infos.objectForKey("prefOnglet");
	}

	public boolean usePhoto() {
		Number value = (Number) infos.objectForKey("prefPhoto");
		return ((value == null) || (value.intValue() == 1));
	}

	public Number timer() {
		return (Number) infos.objectForKey("prefTimer");
	}

	public boolean sendMailTraitement() {
		Number value = (Number) infos.objectForKey("prefMailTraitement");
		return ((value != null) && (value.intValue() == 1));
	}

	/**
	 * Retourne <i>true</i> si une liste des interventions doit etre triee dans
	 * l'ordre "croissant".
	 */
	public boolean isSortIntAscending() {
		String value = (String) infos.objectForKey("prefTriInt");
		CktlLog.trace("Intervention sort order : " + value);
		return ((value == null) || value.equals("A"));
	}

	/**
	 * Retourne <i>true</i> si une liste des traitements doit etre triee dans
	 * l'ordre "croissant".
	 */
	public boolean isSortTraAscending() {
		String value = (String) infos.objectForKey("prefTriTra");
		return ((value == null) || value.equals("A"));
	}

	/**
	 * Retourne true si l'utilisateur souhaite utiliser par defaut son mailer
	 * interne pour l'envoi de mail
	 */
	public boolean useMailInterne() {
		Number value = (Number) infos.objectForKey("prefUseMailInterne");
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur choisi de colorer selon le batiment pluto
	 * que selon l'activite associee a la DT
	 */
	public boolean useCoulBat() {
		Number value = (Number) infos.objectForKey("prefUseCoulBat");
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur choisi que DT genere automatiquement une
	 * signature en fin de mail
	 */
	public boolean insertDtSig() {
		Number value = (Number) infos.objectForKey("prefInsertDtSig");
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur accepte que le detail de ses traitement
	 * soient exportes vers le serveur de planning.
	 */
	public boolean exportPlanning() {
		Number value = (Number) infos.objectForKey("prefExportPlanning");
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur souhaite que DT controle s'il est disponible
	 * via ses traitements et ses occupations issues de serveur de planning
	 */
	public boolean controleChevauchementPlanning() {
		Number value = (Number) infos.objectForKey(CONTROLE_CHEVAUCHEMENT_PLANNING_KEY);
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur souhaite que DT lui demande confirmation
	 * lors de la cloture d'une demande
	 */
	public boolean confirmationCloture() {
		Number value = (Number) infos.objectForKey(CONFIRMATION_CLOTURE_KEY);
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Retourne true si l'utilisateur souhaite que DT enregistrer son panier dans
	 * la base de données
	 */
	public boolean sauvegarderPanier() {
		Number value = (Number) infos.objectForKey(SAUVEGARDER_PANIER_KEY);
		return ((value != null) && value.intValue() == 1);
	}

	/**
	 * Le panier de l'utilisateur
	 */
	public String panier() {
		String value = (String) infos.objectForKey(PANIER_KEY);
		return value;
	}

	/**
	 * Retourne true si l'utilisateur choisi d'utiliser le navigateur Web systeme
	 * pour consulter l'aide de l'application.
	 */
	public boolean isUseSystemNavForHelp() {
		String value = (String) infos.objectForKey("prefAideSysNav");
		return ((value != null) && value.equals("O"));
	}

	/**
	 * Retourne la liste d'identifiants des intervenants dont les demandes sont
	 * consultees. Les indetifiants sont separes par "|" dans la chaine retournee.
	 * 
	 * @see #noIntervenatsArray()
	 */
	public String noIntervenats() {
		return StringCtrl.normalize((String) infos.objectForKey("prefNoIndIntervenant"));
	}

	public Number nbIntPerPage() {
		return (Number) infos.objectForKey("prefNbIntPerPage");
	}

	/**
	 * Retourne la liste d'identifiants des intervenants dont les demandes seront
	 * consultees.
	 * 
	 * @see #noIntervenats()
	 */
	public NSArray noIntervenatsArray() {
		NSMutableArray args = new NSMutableArray();
		NSArray nos = NSArray.componentsSeparatedByString(noIntervenats(), "|");
		for (int i = 0; i < nos.count(); i++) {
			try {
				args.addObject(Integer.valueOf((String) nos.objectAtIndex(i)));
			} catch (Throwable e) {
			}
		}
		return args;
	}

	/**
	 * Retourne la description des infos sous forme d'une chaine de caracteres.
	 */
	public String toString() {
		return "DTUserInfo : " + infos.toString();
	}

	/**
	 * Verifie si l'utilisateur actuellement connecte a le droit de modifier les
	 * droits des intervenants de son service.
	 */
	public boolean canChangeDroits(String cStructure) {
		return hasDroit(DROIT_ADMIN, cStructure);
	}

	/**
	 * Retourne le nom du poste sur lequel l'application est execute. Retourne une
	 * chaine vide si le nom ne peux pas etre detecte.
	 */
	public String hostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Throwable e) {
		}
		return StringCtrl.emptyString();
	}

	/**
	 * Indique si l'utilisateur a les droits correspondant au niveau
	 * <code>droitsNiveau</code> sur le groupe de DT dont le
	 * <code>cStructure</code> est passe en parametre. Le niveau des droits de
	 * l'utilisateur doit etre superieur ou egal a <code>droitsNiveau</code>.
	 */
	public boolean hasDroit(int droitsNiveau, String cStructure) {
		return (droits(cStructure).intValue() >= droitsNiveau);
	}

	/**
	 * Indique si l'utilisateur a les droits correspondant au niveau
	 * <code>droitsNiveau</code> pour au moins un groupe de DT. Le niveau des
	 * droits de l'utilisateur doit etre superieur ou egal a
	 * <code>droitsNiveau</code>.
	 */
	public boolean hasDroit(int droitsNiveau) {
		NSMutableDictionary dicoDroits = (NSMutableDictionary) infos.objectForKey("dtDroits");
		for (int i = 0; i < dicoDroits.allKeys().count(); i++) {
			Number niveauDroit = (Number) dicoDroits.objectForKey(dicoDroits.allKeys().objectAtIndex(i));
			if (niveauDroit.intValue() >= droitsNiveau)
				return true;
		}
		return false;
	}

	/**
	 * Indique si le compte de l'utilisateur est local par rapport au reseau de
	 * l'etablissement. Cette methode utilise les codes des reseaux donnes dans le
	 * parametre de configuration <code>GRHUM_VLAN_LOCAL</code>.
	 */
	public boolean isNetLocal() {
		// if (vLan == null)
		// return false;
		// else
		// return vLan.equals("P") || vLan.equals("R") || vLan.equals("E");
		return isNetDefinedAs("GRHUM_VLAN_LOCAL");
	}

	/**
	 * Indique si le compte de l'utilisateur appartient au reseau "etudiant".
	 * Cette methode utilise les codes des reseaux donnes dans le parametre de
	 * configuration <code>GRHUM_VLAN_ETUD</code>.
	 */
	public boolean isNetEdutiant() {
		// return vLan().equals("E");
		return isNetDefinedAs("GRHUM_VLAN_ETUD");
	}

	/**
	 * Indique si le compte de l'utilisateur appartient au reseau "recherche".
	 * Cette methode utilise les codes des reseaux donnes dans le parametre de
	 * configuration <code>GRHUM_VLAN_RECHERCHE</code>.
	 */
	public boolean isNetRecherche() {
		// return vLan().equals("R");
		return isNetDefinedAs("GRHUM_VLAN_RECHERCHE");
	}

	/**
	 * Indique si le compte de l'utilisateur appartient au reseau
	 * "administration". Cette methode utilise les codes des reseaux donnes dans
	 * le parametre de configuration <code>GRHUM_VLAN_ADMIN</code>.
	 */
	public boolean isNetAdmin() {
		// return vLan().equals("P");
		return isNetDefinedAs("GRHUM_VLAN_ADMIN");
	}

	/**
	 * Indique si le compte de l'utilisateur appartient au reseau "externe". Cette
	 * methode utilise les codes des reseaux donnes dans le parametre de
	 * configuration <code>GRHUM_VLAN_EXTERNE</code>.
	 */
	public boolean isNetExterne() {
		// return vLan().equals("X");
		return isNetDefinedAs("GRHUM_VLAN_EXTERNE");
	}

	/**
	 * Teste si le type reseau en cours correspond a la definition du reseau
	 * donnee dans le parametre de configuration <code>netTypeParam</code>.
	 */
	private boolean isNetDefinedAs(String netTypeParamName) {
		if (vLan() == null)
			return false;
		String value =
				StringCtrl.normalize(dtApp().config().stringForKey(netTypeParamName));
		if (value.length() == 0)
			return false;
		if (!value.startsWith(","))
			value = "," + value;
		if (!value.endsWith(","))
			value += ",";
		return value.indexOf("," + vLan() + ",") >= 0;
	}

	/*
	 * On redefinit cette methode, car elle ne fonctionne pas correctement dans
	 * certains etablissements. On va s'appuyer sur le parametrage des reseaux
	 * dans l'application.
	 * 
	 * @see org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB#userStatus()
	 */
	public int userStatus() {
		if (isNetAdmin() || isNetRecherche())
			return STATUS_PERSONNEL;
		else if (isNetEdutiant())
			return STATUS_ETUDIANT;
		else if (isNetExterne())
			return STATUS_EXTERIEUR;
		else
			return STATUS_INCONNU;
	}

	/**
	 * @deprecated Changer la visibilite de la methode dans CktlUserInfoDB et
	 *             supprimer cette methode.
	 */
	public EOEnterpriseObject findCompteForPersId(Number persId) {
		return super.findCompteForPersId(persId);
	}

	// Rajouts pour DTWeb uniquement

	/**
	 * Le code etat des DT a afficher (preferences)
	 */
	public String prefEtatCode() {
		return (String) infos.objectForKey("prefEtatCode");
	}

	/**
	 * Sur quel attribut trier (preferences)
	 */
	public String prefTri() {
		return (String) infos.objectForKey("prefTri");
	}

	/**
	 * Sur quel attribut trier (preferences)
	 */
	public Number prefOnglet() {
		return (Number) infos.objectForKey("prefOnglet");
	}

}
