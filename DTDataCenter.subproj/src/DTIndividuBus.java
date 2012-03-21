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
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gestionnaire d'acces aux informations relatives aux individus : noms, emails,
 * comptes, etc...
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTIndividuBus extends DTDataBus {
	public static final Integer RAW_SQL_TOO_MANY_OBJECTS = new Integer(50);

	//
	// /**
	// * Indique si le support des photos est disponible dans la base de donnees.
	// * Par defaut la valeur est <i>true</i>, mais elle est remise a <i>false</i>
	// * apres une erreur de fetch des photos. */
	// private boolean photoTableAvailable = true;

	/**
	 * Contient l'image d'une photo "vide". Valeur utilise un image lorsque les
	 * photos ne sont pas disponibles.
	 */
	private NSData defaultEmptyPhoto = null;

	/** Le cache de l'objet UserInfo pour une multiple reutilisation. */
	private DTUserInfo userInfoObject;

	/** La definition de tri des individus */
	// private NSArray sortIndividus;

	/**
	 * Cree un nouvel objet bus qui par defaut utilisera le editing context
	 * indique.
	 */
	public DTIndividuBus(EOEditingContext eocontext) {
		super(eocontext);
		// sortIndividus = CktlSort.newSort("nomEtPrenom");
	}

	/**
	 * Retourne un objet <code>Individu</code> correspondant au numero d'individu
	 * donne. Retourne <i>null</i> si l'individu n'existe pas.
	 */
	public EOIndividu individuForNoIndividu(Number noIndividu) {
		setRefreshFetchedObjects(false);
		return (EOIndividu) fetchObject("DTIndividuUlr", "noIndividu=" + noIndividu, null);
	}

	/**
	 * Retourne l'objet <code>Individu</code> correspondant a l'identifian de la
	 * personne donne. Retourne <i>null</i> si l'individu n'existe pas.
	 */
	public EOIndividu individuForPersId(Number persId) {
		setRefreshFetchedObjects(false);
		return (EOIndividu) fetchObject(
				EOIndividu.ENTITY_NAME, EOIndividu.PERS_ID_KEY + "=" + persId, null);
	}

	/**
	 * Retourne l'identifiant de la personne correspondant au numero d'individu.
	 * Retourne un objet avec la valeur -1 si l'individu n'existe pas.
	 * 
	 * <p>
	 * <i>Les deux valeurs <code>persId</code> et <code>noIndividu</code> sont les
	 * identifiants uniques de la personne.</i>
	 * </p>
	 */
	public Number persIdForNoIndividu(Number noIndividu) {
		CktlRecord rec = individuForNoIndividu(noIndividu);
		if (rec != null)
			return rec.numberForKey("persId");
		else
			return (new Integer(-1));
	}

	/**
	 * Retourne le numero d'individu correspondant a l'identifiant de la personne.
	 * Retourne un objet avec la valeur -1 si l'individu n'existe pas.
	 * 
	 * <p>
	 * <i>Les deux valeurs <code>persId</code> et <code>noIndividu</code> sont les
	 * identifiants uniques de la personne.</i>
	 * </p>
	 */
	public Number noIndividuForPersId(Number persId) {
		CktlRecord rec = individuForPersId(persId);
		if (rec != null)
			return rec.numberForKey("noIndividu");
		// return (new Integer(-1));
		return null;
	}

	/**
	 * Retourn le login correspondant au numero d'individu <code>noIndividu</code>
	 * . Retourne une chaine vide si l'individu un son compte n'existe pas.
	 */
	public String loginForNoIndividu(Number noIndividu) {
		Number persId = persIdForNoIndividu(noIndividu);
		if (persId.intValue() != -1)
			return loginForPersId(persId);
		return StringCtrl.emptyString();
	}

	/**
	 * Retourne le login correspondant a l'identifiant de la personne. Retourne
	 * une chaine vide si l'individu ou son compte n'existe pas.
	 */
	public String loginForPersId(Number persId) {
		DTUserInfo info = new DTUserInfo();
		info.compteForPersId(persId, false);
		if (info.hasError())
			return StringCtrl.emptyString();
		return info.login();
	}

	/**
	 * Retourne le numero d'individu correspondant au login donne. Le comte de
	 * l'individu est recherche en respectant les priorites definies dans la table
	 * <code>V_LAN</code>. Retourne <i>null</i> si l'individu ne peux pas etre
	 * trouve.
	 */
	public Number noIndividuForLogin(String login) {
		DTUserInfo info = new DTUserInfo();
		info.compteForLogin(login, null, true);
		if (info.hasError())
			return null;
		return info.noIndividu();
	}

	/**
	 * Retourne l'adresse email de la personne avec le numero d'individu donne.
	 * Retourne une chaine vide si l'individu ou le compte n'existe pas.
	 */
	public String mailForNoIndividu(Number noIndividu) {
		CktlRecord rec = individuForNoIndividu(noIndividu);
		if (rec == null)
			return StringCtrl.emptyString();
		return mailForPersId(rec.numberForKey("persId"));
	}

	/**
	 * Retourne l'adresse email de la personne avec l'identifiant donne. Retourne
	 * une chaine vide si l'individu ou le compte n'existe pas.
	 */
	public String mailForPersId(Number persId) {
		DTUserInfo info = new DTUserInfo();
		info.compteForPersId(persId, false);
		return StringCtrl.normalize(info.email());
	}

	/**
	 * Retourne le nom de la personne avec l'identifiant donne. Retourne une
	 * chaine vide si la personne n'existe pas.
	 */
	public String nomForNoIndividu(Number noIndividu) {
		CktlRecord rec = individuForNoIndividu(noIndividu);
		if (rec != null)
			return rec.stringForKey("nomUsuel");
		return StringCtrl.emptyString();
	}

	/**
	 * Retourne le nom et le prenom (une chaine concatenee) de la personne avec
	 * l'identifiant donne. Retourne une chaine vide si la personne n'existe pas.
	 */
	public String nomEtPrenomForNoIndividu(Number noIndividu) {
		CktlRecord rec = individuForNoIndividu(noIndividu);
		if (rec != null)
			return rec.stringForKey("nomEtPrenom");
		return StringCtrl.emptyString();
	}

	/**
	 * Retourne le nom, le prenom et l'adresse email (une chaine concatenee) de la
	 * personne avec l'identifiant donne. Retourne une chaine vide si la personne
	 * n'existe pas.
	 * 
	 * <p>
	 * Le resultat a le format : "<code>NOM PRENOM (adresse_email)</code>"
	 * </p>
	 */
	public String nomPrenomEmailForNoIndividu(Number noIndividu) {
		StringBuffer info = new StringBuffer();
		CktlRecord rec = individuForNoIndividu(noIndividu);
		if (rec != null) {
			info.append(rec.stringForKey("nomEtPrenom"));
			String mail = mailForPersId(rec.numberForKey("persId"));
			if (mail != null)
				info.append(" <").append(mail).append(">");
		}
		return info.toString();
	}

	/**
	 * Remplit la display group <code>table</code> avec les individu dont les noms
	 * et prenoms sont similaires au ceux donnees dans <code>likeNomPrenom</code>.
	 * Les donnes sont fetchees en utilisant la condition de la similitude des
	 * noms et des prenoms (<code>'like&nbsp;*nom*'</code> et
	 * 'like&nbsp;*prenom*').
	 * 
	 * Aucun fetch n'est effectue si <code>fetchIfNull</code> est false et la
	 * condition est vide (<code>likeNomPrenom</code>). Ce parametre permet
	 * d'eviter le fetch de tous les individus.
	 */
	public void fetchIndividuUlr(WODisplayGroup table,
																String likeNomPrenom,
																boolean fetchIfNull) {
		EOQualifier condition = null;
		StringBuffer sb = new StringBuffer();
		StringTokenizer st;
		String s;
		boolean noCondition = true;

		likeNomPrenom = StringCtrl.normalize(likeNomPrenom);
		st = new StringTokenizer(likeNomPrenom);
		sb.append("(temValide='O')"); // On cherchera parmi les valides
		noCondition = !(st.hasMoreTokens()); // Verifier si la condition n'est pas
																					// vide
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			sb.append(" and ");
			sb.append("((nomUsuel caseInsensitiveLike '*").append(s);
			sb.append("*') or (prenom caseInsensitiveLike '*").append(s).append("*'))");
		}
		if (sb.length() > 0)
			condition = newCondition(sb.toString());
		if (noCondition && (!fetchIfNull))
			return;
		fetchTable(table, condition, CktlSort.newSort("nomUsuel, prenom"), true);
	}

	/**
	 * Retourne l'objet <code>NSData</code> avec la photo de l'individu. Retourne
	 * la photo par defaut si le support des photos n'est pas disponible.
	 */
	public NSData photoForNoIndividu(Number noIndividu, String cStructure) {
		NSData photoImage = null;
		boolean canShowPhoto = preferencesBus().usePhoto();
		// Si l'affichage des photos en general est supporte
		if (canShowPhoto) {
			// On determine, si la photo peut etre affichee
			// Si c'est le membre du service, alors c'est OK
			canShowPhoto = userInfo().hasDroit(
					DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure);
			// Sinon, il peut voir si c'est lui meme
			if (!canShowPhoto)
				canShowPhoto =
						(userInfo().noIndividu().intValue() == noIndividu.intValue());
			// Sinon, on l'affiche si l'individu la indique explicitement
			if (!canShowPhoto) {
				CktlRecord rec = individuForNoIndividu(noIndividu);
				if (rec != null)
					canShowPhoto = rec.stringForKey("indPhoto").equals("O");
			}
		}
		CktlLog.trace("CanShowPhoto : " + canShowPhoto);
		// En fin, si on peut voir la photo, alors on la fetch
		if (canShowPhoto) {
			try {
				// On n'utilise pas de FetchArray a cause des objets blobs.
				EOFetchSpecification myFetch =
						new EOFetchSpecification("PhotoEmployes", newCondition("noIndividu=" + noIndividu), null);
				myFetch.setRefreshesRefetchedObjects(false);
				NSArray objects = editingContext().objectsWithFetchSpecification(myFetch);
				if (objects.count() > 0) {
					if (((EOEnterpriseObject) objects.objectAtIndex(0)).valueForKey("image") != null)
						photoImage = (NSData) ((EOEnterpriseObject) objects.objectAtIndex(0)).valueForKey("image");
				}
			} catch (Throwable ex) {
				preferencesBus().setUsePhoto(false);
				CktlLog.log("Erreur lors de l'acces a l'entite PhotoEmployes",
									CktlLog.getMessageForException(ex));
			}
		}
		if (photoImage == null)
			return getDefaultEmptyPhoto();
		else
			return photoImage;
	}

	/**
	 * Retourne l'image qui sera affiche lorsque la photo de l'uilisateur n'est
	 * pas disponible. Retourne <i>null</i> si aucune image ne doit pas etre
	 * affichee.
	 */
	private NSData getDefaultEmptyPhoto() {
		if (defaultEmptyPhoto == null) {
			String photoFile = dtApp().appResources().pathForResource("NoPhoto.jpg");
			CktlLog.trace("No photo file : " + photoFile);
			if ((photoFile != null) && FileCtrl.existsFile(photoFile)) {
				try {
					defaultEmptyPhoto = new NSData(new File(photoFile).toURL());
				} catch (IOException ex) {
					// ex.printStackTrace();
				}
			}
		}
		return defaultEmptyPhoto;
	}

	/**
	 * Returne la liste des telephones de la personne <code>persId</code>.
	 * <code>typeTel</code> indique le type de telephone (PRF, PRV, ETUD, INT) et
	 * <code>typeNo</code> le type de numero (TEL, FAX).
	 */
	public NSArray telephonesForPersId(Number persId, String typeTel, String typeNo) {
		StringBuffer sb = new StringBuffer();
		sb.append("persId=").append(persId);
		if (typeTel != null)
			sb.append(" and typeTel='").append(typeTel).append("'");
		if (typeNo != null)
			sb.append(" and typeNo='").append(typeNo).append("'");
		setRefreshFetchedObjects(false);
		return fetchArray("DTPersonneTelephone", newCondition(sb.toString()), null);
	}

	/**
	 * Retourne le comte correspondant a la personne avec l'identifiant
	 * <code>persId</code>. Cette methode fait appele a la methode
	 * <code>compteForPersId</code> de <code>CktlUserInfo</code> et respecte donc
	 * l'ordre de recherche des comptes definit dans la table <code>V_LANS</code>.
	 */
	public CktlRecord compteForPersId(Number persId) {
		if (userInfoObject == null)
			userInfoObject = new DTUserInfo(this);
		return (CktlRecord) userInfoObject.findCompteForPersId(persId);
	}

	/**
	 * Retourne une instance de l'entite <code>RepartCompte</code> avec les codes
	 * d'individu <code>noIndividu</code> et de compte <code>noCompte</code>.
	 * 
	 * @deprecated Utiliser la methode compteForPersId.
	 */
	public CktlRecord repartCompteForID(Number noIndividu, Number noCompte) {
		NSArray objects =
				fetchArray(
						"DTRepartCompte",
						EOQualifier.qualifierWithQualifierFormat(
								"persId=" + noIndividu + " and cptOrdre=" + noCompte, null),
						null);
		if (objects.count() > 0)
			return (CktlRecord) objects.objectAtIndex(0);
		else
			return null;
	}

	/**
	 * Retourne le numero d'individu correspondant a l'adresse email
	 * <code>userMail</code>. La recherche est faite dans la table des comptes
	 * ainsi que dans la table des alias des mails. Retourne <em>null</em> si
	 * l'individu ne peut pas etre retrouve a partir de l'adresse email.
	 */
	public Number noIndividuForEmail(String userMail) {
		return noIndividuForPersId(persIdForEmail(userMail));
	}

	/**
	 * Retourne le numero d'individu correspondant a l'adresse email
	 * <code>userMail</code>. La recherche est faite dans la table des comptes
	 * ainsi que dans la table des alias des mails. Retourne <em>null</em> si
	 * l'individu ne peut pas etre retrouve a partir de l'adresse email.
	 */
	public Number persIdForEmail(String emailAdresse) {
		return persIdForEmail(emailAdresse, null);
	}

	/**
	 * Renvoie l'objet representant le compte d'utilisateur ayant l'adresse mail
	 * <code>emailAdresse</code> et appartenant au reseau avec le code
	 * <code>vLanCode</code>.
	 */
	private Number persIdForEmail(String emailAdresse, String vLanCode) {
		StringBuffer condition = new StringBuffer();
		NSArray objects;
		String email, domaine;
		int i = emailAdresse.indexOf('@');

		if (i >= 0) {
			email = emailAdresse.substring(0, i);
			domaine = emailAdresse.substring(i + 1);
		} else {
			email = emailAdresse;
			domaine = dtApp().config().stringForKey("GRHUM_DOMAINE_PRINCIPAL");
		}
		// On cherche dans les mails
		// 29/08/2006 Modification Richard : remplacement du = par
		// caseInsensitiveLike (idem pour les alias)
		condition.append("cptEmail caseInsensitiveLike '").append(StringCtrl.cut(email, 40)).append("'");
		condition.append(" and cptDomaine='").append(StringCtrl.cut(domaine, 40)).append("'");
		if (vLanCode != null)
			condition.append(" and cptVlan = '").append(vLanCode).append("'");
		CktlLog.trace("condition.email : " + condition);
		objects = fetchArray("DTCompte", newCondition(condition.toString()), null);
		// Sinon, on cherche dans les logins
		if (objects.count() == 0) {
			condition.setLength(0);
			condition.append("cptLogin='").append(StringCtrl.cut(email, 20)).append("'");
			condition.append(" and cptDomaine='").append(StringCtrl.cut(domaine, 40)).append("'");
			if (vLanCode != null)
				condition.append(" and cptVlan='").append(vLanCode).append("'");
			CktlLog.trace("condition.login : " + condition);
			objects = fetchArray("DTCompte", newCondition(condition.toString()), null);
		}
		if (objects.count() > 0)
			objects = ((CktlRecord) objects.objectAtIndex(0)).arrayForKey("tosRepartCompte");
		// Enfin, dans les aliases des personnes
		if ((objects.count() == 0) && (vLanCode == null)) {
			condition.setLength(0);
			// 29/08/2006 Modification Richard : remplacement du = par
			// caseInsensitiveLike
			condition.append("alias caseInsensitiveLike '").append(StringCtrl.cut(email, 40)).append("'");
			CktlLog.trace("condition.alias : " + condition);
			objects = fetchArray("PersonneAlias", newCondition(condition.toString()), null);
		}
		if (objects.count() == 0)
			return null;
		else
			return ((CktlRecord) objects.objectAtIndex(0)).numberForKey("persId");
	}

	/**
	 * Retourne le nombre des individus dont le nom et/ou prenom est similaire a
	 * celui donnee dans <code>likeNomPrenom</code>.
	 */
	public int rawIndividuCountForName(String likeNomPrenom) {
		return rawIndividuCountForCondition(getNomLikeCondition(likeNomPrenom, "IND"));
	}

	/**
	 * Retourne la liste des descriptions des individus correspondant. Le nom est
	 * recherche en utilisant la condition de similitude ("like").
	 */
	public NSArray rawIndividuForName(String likeNomPrenom) {
		return rawIndividuForCondition(getNomLikeCondition(likeNomPrenom, "IND"));
	}

	/**
	 * Cree une condition de selection d'une personne ayant le nom et/ou prenom
	 * <code>nomPrenom</code>. Pour cree une condition, le <code>nomPrenom</code>
	 * est decompose en utilisant le symbole d'espace. Pour chaque composant, une
	 * condition "like" est creee pour le nom et le prenom.
	 */
	private String getNomLikeCondition(String nomPrenom, String tableAlias) {
		// On construit la condition complementaire
		StringBuffer sb = new StringBuffer();
		StringTokenizer st;
		String s;

		nomPrenom = StringCtrl.normalize(nomPrenom).toUpperCase();
		tableAlias = StringCtrl.normalize(tableAlias).toUpperCase();
		st = new StringTokenizer(nomPrenom);
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append("((UPPER(");
			if (tableAlias.length() > 0)
				sb.append(tableAlias).append(".");
			sb.append("NOM_USUEL) LIKE '%").append(s).append("%') OR (UPPER(");
			if (tableAlias.length() > 0)
				sb.append(tableAlias).append(".");
			sb.append("PRENOM) LIKE '%").append(s).append("%'))");
		}
		return sb.toString();
	}

	/**
	 * Retourne la liste des descriptions des individus correspondant a la
	 * condition donnee. La selection est faite via la jointure des tables
	 * individu_ulr, repart_structure, repart_compte, compte et vlans.
	 */
	private NSArray rawIndividuForCondition(String condition) {
		NSArray objects;
		NSMutableDictionary resultObjects;
		NSDictionary rec;
		EOEditingContext ec = new EOEditingContext();
		NSArray keys = new NSArray();
		StringBuffer sbSQL = new StringBuffer();
		// On construit la requete a executer
		sbSQL.append("SELECT UNIQUE IND.PERS_ID, IND.NOM_USUEL, IND.PRENOM, CPT.CPT_EMAIL, ");
		sbSQL.append("CPT.CPT_DOMAINE, CPT.CPT_LOGIN, CPT.CPT_ORDRE, VLN.PRIORITE ");
		sbSQL.append("FROM GRHUM.REPART_COMPTE RCP, ");
		sbSQL.append("GRHUM.INDIVIDU_ULR IND, GRHUM.COMPTE CPT, GRHUM.VLANS VLN ");
		sbSQL.append("WHERE IND.PERS_ID = RCP.PERS_ID AND ");
		sbSQL.append("RCP.CPT_ORDRE = CPT.CPT_ORDRE AND CPT.CPT_VLAN = VLN.C_VLAN ");
		sbSQL.append("AND VLN.PRISE_COMPTE = 'O'");
		if (condition != null)
			sbSQL.append(" AND ").append(condition);
		sbSQL.append(" ORDER BY IND.PERS_ID, VLN.PRIORITE DESC");
		// Les cles des dictionnaires avec le contenu d'objets
		keys = keys.arrayByAddingObject("persId").arrayByAddingObject("nomUsuel").arrayByAddingObject("prenom").arrayByAddingObject("cptEmail");
		keys = keys.arrayByAddingObject("cptDomaine").arrayByAddingObject("cptLogin").arrayByAddingObject("cptOrdre").arrayByAddingObject("priorite");
		// On recupere les objets
		objects =
				DTCktlEOUtilities.rawRowsForSQL(ec, "DTWeb", sbSQL.toString(), keys);
		// On refait un filtre au cas ou il y a plusieurs
		// comptes pour la meme personne
		resultObjects = new NSMutableDictionary();
		for (int i = 0; i < objects.count(); i++) {
			rec = (NSDictionary) objects.objectAtIndex(i);
			// On peut verifier cette condition directement dans le SQL...
			if (!isEmptyValue(rec.valueForKey("cptEmail")) &&
					!isEmptyValue(rec.valueForKey("cptDomaine"))) {
				// On memorise pour le meme persId => valeurs precedentes seront
				// oubliees
				// d'ou l'importance de la condition "order by"
				resultObjects.setObjectForKey(rec, rec.valueForKey("persId").toString());
			}
		}
		// On recupere les objets filtres
		return resultObjects.allValues();
	}

	/**
	 * Retourne le nombre des individus correspondant a la condition donnee. La
	 * selection est faite via la jointure des tables individu_ulr,
	 * repart_structure, repart_compte, compte et vlans.
	 */
	private int rawIndividuCountForCondition(String condition) {
		NSArray objects;
		EOEditingContext ec = new EOEditingContext();
		StringBuffer sbSQL = new StringBuffer();
		// On construit la requete a executer
		sbSQL.append("SELECT COUNT(*) FROM (");
		sbSQL.append("SELECT UNIQUE IND.PERS_ID, IND.NOM_USUEL, IND.PRENOM ");
		sbSQL.append("FROM GRHUM.REPART_COMPTE RCP, ");
		sbSQL.append("GRHUM.INDIVIDU_ULR IND, GRHUM.COMPTE CPT, GRHUM.VLANS VLN ");
		sbSQL.append("WHERE IND.PERS_ID = RCP.PERS_ID AND ");
		sbSQL.append("RCP.CPT_ORDRE = CPT.CPT_ORDRE AND CPT.CPT_VLAN = VLN.C_VLAN ");
		sbSQL.append("AND VLN.PRISE_COMPTE = 'O'");
		if (condition != null)
			sbSQL.append(" AND ").append(condition);
		sbSQL.append(")");
		// On recupere les objets
		objects =
				DTCktlEOUtilities.rawRowsForSQL(ec, "DTWeb", sbSQL.toString(), new NSArray("count"));
		CktlLog.trace("count : " + objects);
		Object value = ((NSDictionary) objects.objectAtIndex(0)).valueForKey("count");
		return Float.valueOf(value.toString()).intValue();
	}

}
