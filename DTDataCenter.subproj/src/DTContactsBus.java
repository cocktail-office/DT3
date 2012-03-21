/*
 * Copyright CRI - Universite de La Rochelle, 2003-2005 
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
import org.cocktail.dt.server.metier.EOBatiment;
import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EOSalles;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;

/**
 * Le gestionnaire des infos sur les contacts des utilisateurs. Il permet
 * d'acceder aux donnees permettant de retrouver les informations pour contacter
 * les personnes : service, mail, telephone, etc...
 * 
 * <p>
 * Cette classe se base sur l'utilisation de la table <i>Contacts</i>.
 * </p>
 * 
 * @see DTIndividuBus
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTContactsBus extends DTDataBus {

	/**
	 * Cree et initialise un nouvel objet <code>DTContactBus</code>.
	 */
	public DTContactsBus(EOEditingContext eocontext) {
		super(eocontext);
		setUseCache(true);
	}

	/**
	 * Retourne l'objet de la table <i>Contact</i> satisfaisant la condition
	 * donnee. Si plusieurs objets correspondent a la condition, le premier de la
	 * liste est retourne.
	 */
	private CktlRecord fetchContact(EOEditingContext ec, EOQualifier condition) {
		NSArray contacts = fetchContacts(ec, condition);
		if (contacts.count() > 0)
			return (CktlRecord) contacts.objectAtIndex(0);
		else
			return null;
	}

	/**
	 * Fetche les objects a partir de la table <i>Contact</i> correspondant a la
	 * condition donnee.
	 */
	private NSArray fetchContacts(EOEditingContext ec, EOQualifier condition) {
		setRefreshFetchedObjects(!useCache());
		return fetchArray(ec, "Contact", condition, null);
	}

	/**
	 * Fetche les objets a partir de la table <i>ImplantationGeo</i> correspondant
	 * a la condition donnee.
	 */
	private NSArray fetchImplantationGeos(EOEditingContext ec, EOQualifier condition) {
		setRefreshFetchedObjects(!useCache());
		return fetchArray(ec,
				"ImplantationGeo", condition, CktlSort.newSort("llImplantationGeo"));
	}

	/**
	 * Fetche les objets a partir de la table <i>Batiment</i> correspondant a la
	 * condition donnee.
	 */
	private NSArray<EOBatiment> fetchBatiments(EOEditingContext ec, EOQualifier condition) {
		setRefreshFetchedObjects(!useCache());
		return fetchArray(ec,
				EOBatiment.ENTITY_NAME, condition, CktlSort.newSort(EOBatiment.APPELLATION_KEY));
	}

	/**
	 * Fetche les objets a partir de la table <i>Salles</i> correspondant a la
	 * condition donnee.
	 */
	private NSArray<EOSalles> fetchBureau(EOEditingContext ec, EOQualifier condition) {
		setRefreshFetchedObjects(!useCache());
		return fetchArray(ec, EOSalles.ENTITY_NAME, condition, null);
	}

	/**
	 * Retourne la liste des enregistrements <i>PersonneTelephone</i> de
	 * l'individu avec le code <code>persId</code>. La recherche est faite parmis
	 * les telephones professionels ou internes a l'etablissement.
	 */
	private NSArray findAnnuaireTelephones(Number persId) {
		setRefreshFetchedObjects(!useCache());
		return fetchArray("DTPersonneTelephone",
				newCondition("(typeTel='PRF' or typeTel='INT') and typeNo='TEL' and persId=" + persId), null);
	}

	/**
	 * Retourne les informations sur le contact de la personne
	 * <code>noIndividu</code> remplit a partir des informations disponibles dans
	 * l'annuaire. Le dictionnaire retourne comporte les memes informations qu'un
	 * objet de la table <i>Contact</i>.
	 * 
	 * <p>
	 * Le dictionaire retourne par cette methode peut etre utilise pour ajouter un
	 * nouvel enregistrement dans la table <code>Contact</code> a l'aide de la
	 * methode <code>addContact</code>. Voir la documentation de celle-ci pour la
	 * description des informations que peut contenir le dictionnaire.
	 * </p>
	 * 
	 * @see #addContact(Integer, NSDictionary)
	 */
	public NSDictionary getAnnuaireContactInfo(Number noIndividu) {
		Number persId;
		NSArray objects;
		NSMutableDictionary dico = new NSMutableDictionary();
		DTUserInfo userInfo;
		dico.takeValueForKey(noIndividu, "noIndividu");
		persId = individuBus().persIdForNoIndividu(noIndividu);
		dico.takeValueForKey(persId, "persId");
		// On retrouve le compte, et donc le email
		userInfo = new DTUserInfo(this);
		userInfo.compteForPersId(persId, true);
		// S'il y a des problemes, on ne cherche plus rient
		if (userInfo.hasError())
			return null;
		// Email
		dico.takeValueForKey(userInfo.email(), "ctEmail"); // email
		// Utilisateur de reseau local ?
		dico.takeValueForKey(new Boolean(userInfo.isNetLocal()), "ctUserLocal");
		// Les infos suivants sont disponibles uniquement aux utilisateurs "locaux"
		if (userInfo.isNetLocal()) {
			serviceBus().setRefreshFetchedObjects(!useCache());
			CktlRecord rec = serviceBus().structureEtablissement();
			// Etablissement de l'utilisateur
			if (rec != null) {
				dico.takeValueForKey(rec.valueForKey("llStructure"), "ctLibelleEtab");
				dico.takeValueForKey(rec.valueForKey("cStructure"), "cStructEtab");
			}
			// Les services de l'utilisateur : on prend le premier
			objects = serviceBus().servicesForPersId(persId);
			if (objects.count() > 0)
				dico.takeValueForKey(
						((CktlRecord) objects.objectAtIndex(0)).valueForKey("cStructure"), "cStructService");
			// Batiment et bureau... s'il en a plusieurs, on prend le premiere
			objects = fetchBureau(editingContext(), newCondition("tosRepartBureau.noIndividu=" + noIndividu));
			if (objects.count() > 0) {
				rec = (CktlRecord) objects.objectAtIndex(0);
				dico.takeValueForKey(rec.valueForKey("cLocal"), "cLocal");
				dico.takeValueForKey(rec.valueForKey("salNumero"), "salNumero");
			}
		}
		// Le numero de telephone... on prend le dernier modifie
		objects = CktlSort.sortedArray(
				findAnnuaireTelephones(persId), "dModification", CktlSort.Descending);
		if (objects.count() > 0)
			dico.takeValueForKey(((CktlRecord) objects.objectAtIndex(0)).valueForKey("noTelephone"), "ctNoTelephone");
		//
		return dico;
	}

	/**
	 * Retourne le contact par defaut pour l'utilisateur <code>noIndividu</code>.
	 * Si aucun contact par defaut n'est encore defini et <code>insertNew</code>
	 * est <i>true</i>, alors un nouveau contact par defaut sera ajoute dans la
	 * base, si possible.
	 * 
	 * <p>
	 * Retourne <i>null</i> si aucun contact par defaut n'est encore defini et/ou
	 * ne peut pas etre cree.
	 * </p>
	 * 
	 * <p>
	 * Le dernier contact utilise est considere comme etant celui par defaut.
	 * </p>
	 */
	public EOContact findDefaultContact(Number noIndividu, boolean insertNew) {
		// On cherche celui qui est deja defini : le contact par defaut
		CktlRecord rec = preferencesBus().findPrefDefaut(noIndividu);
		// On essaie de le retrouver
		if (rec != null) {
			rec = findContact(rec.numberForKey("prfCtOrdre"));
		}
		// Celui par defaut n'existe pas et on veut inserer un nouvau
		if ((rec == null) && insertNew) {
			// On retrouve les contacts existants
			NSArray objects =
					fetchContacts(editingContext(), newCondition("noIndividu=" + noIndividu));
			// On va prendre le dernier modifie
			rec = null;
			objects = CktlSort.sortedArray(objects, "dModification", CktlSort.Descending);
			if (objects.count() > 0) {
				rec = (CktlRecord) objects.objectAtIndex(0);
			} else {
				// En fin, on va essayer de generer et inserer un nouveau contact
				NSDictionary dico = getAnnuaireContactInfo(noIndividu);
				if (dico != null) {
					Number ctOrdre = addContact(null, dico);
					if (ctOrdre != null)
						rec = findContact(ctOrdre);
				}
			}
			// Il s'agit d'un nouveau contact - il faut le memoriser
			if (rec != null)
				updateDefaultContact(null, noIndividu, rec.numberForKey("ctOrdre"));
		}
		return (EOContact) rec;
	}

	/**
	 * Retourne le code du contact par defaut pour l'utilisateur avec le code
	 * <code>noIndividu</code>. Retourne -1 si le contact par defaut ne peut pas
	 * etre trouve.
	 */
	public int getDefaulContactOrdre(Number noIndividu) {
		CktlRecord rec = preferencesBus().findPrefDefaut(noIndividu);
		if ((rec != null) && (rec.valueForKey("prfCtOrdre") != null)) {
			// On retrouve le contact avec ce numero, pour etre sur
			rec = findContact(rec.numberForKey("prfCtOrdre"));
			if (rec != null)
				return rec.intForKey("ctOrdre");
		}
		// Sinon, le contact par defaut n'est pas trouve
		return -1;
	}

	/**
	 * Retourne le contact avec le code <code>ctOrdre</code> ou <i>null</i> s'il
	 * ne peux pas etre trouve.
	 */
	public EOContact findContact(Number ctOrdre) {
		if (ctOrdre == null)
			return null;
		else
			return (EOContact) fetchContact(editingContext(),
					ERXQ.equals(EOContact.CT_ORDRE_KEY, ctOrdre));
	}

	/**
	 * Retourne la liste de tous les contacts correspondant a l'utilisateur
	 * <code>noIndividu</code>.
	 */
	public NSArray findAllContacts(Number noIndividu) {
		return fetchContacts(editingContext(), newCondition("noIndividu=" + noIndividu));
	}

	/**
	 * Retourne la liste des objets <i>StructureUlr</i> correspondant aux services
	 * de la personne avec le numero d'individu. Les services sont recherches
	 * parmis ceux deja saisis dans les contactes, ainsi que ceux de l'annuaire.
	 */
	public NSArray findServicesForIndividu(Number noIndividu) {
		NSMutableArray services = new NSMutableArray();
		NSMutableArray serviceCodes = new NSMutableArray();
		CktlRecord rec;
		NSArray list;
		list = findAllContacts(noIndividu);
		// On ajoute d'abord les structures a partir des contactes
		for (int i = 0; i < list.count(); i++) {
			rec = (CktlRecord) list.objectAtIndex(i);
			if (!isNullValue(rec.stringForKey("cStructService")) &&
					!serviceCodes.containsObject(rec.stringForKey("cStructService"))) {
				serviceCodes.addObject(rec.stringForKey("cStructService"));
				services.addObject(
						serviceBus().structureForCode(rec.stringForKey("cStructService")));
			}
		}
		// Ensuite, on ajoute les services annuaire de l'individu
		serviceBus().setRefreshFetchedObjects(!useCache());
		list = serviceBus().servicesForPersId(
				individuBus().persIdForNoIndividu(noIndividu));
		for (int i = 0; i < list.count(); i++) {
			rec = (CktlRecord) list.objectAtIndex(i);
			if (!serviceCodes.containsObject(rec.stringForKey("cStructure"))) {
				serviceCodes.addObject(rec.stringForKey("cStructure"));
				services.addObject(rec);
			}
		}
		// Et maintenant on retrie les objets (s'il le faut)
		if (services.count() > 1)
			CktlSort.sortArray(services, "llStructure");
		return services;
	}

	/**
	 * Retourne la liste des noms des etablissements de la personne avec le numero
	 * d'individu donne. La liste est initialisee a partir des valeurs
	 * enregistrees dans la liste des contacts. Retourne un tableau vide si la
	 * personne appartient a l'etablissement en cours (i.e., ce n'est pas une
	 * personne externe).
	 * 
	 * Le tableau retourne contient les chaines de caracteres.
	 */
	public NSArray findEtabsNomForIndividu(Number noIndividu) {
		NSMutableArray etabs = new NSMutableArray();
		String value;
		NSArray allConctats;
		allConctats = findAllContacts(noIndividu);
		for (int i = 0; i < allConctats.count(); i++) {
			value = ((CktlRecord) allConctats.objectAtIndex(i)).stringForKey("ctLibelleEtab");
			if ((value != null) && (!etabs.containsObject(value)))
				etabs.addObject(value);
		}
		return etabs;
	}

	/**
	 * Retourne la liste des object <i>Batiment</i> associes a l'utilisateur avec
	 * le numero <code>noIndividu</code>. Association est faite via les entites
	 * <i>Salles</i> &gt; <i>RepartBurea</i>. La liste inclue egalement les
	 * betiments deja selectionnes dans les anciens contacts.
	 */
	public NSArray findBatimentsForIndividu(Number noIndividu) {
		CktlRecord rec;
		NSArray ctBats;
		NSMutableArray bats;
		StringBuffer condition = new StringBuffer();

		// D'abord, tous les batiments officiellement associes a l'individu
		bats = new NSMutableArray(fetchBatiments(editingContext(),
				newCondition("tosSalles.tosRepartBureau.noIndividu=" + noIndividu)));
		// Ensuite, les batiments de contacts qui ne sont pas encore utilises
		condition.append("noIndividu=").append(noIndividu);
		for (int i = 0; i < bats.count(); i++) {
			condition.append(" and cLocal <> '");
			condition.append(((CktlRecord) bats.objectAtIndex(i)).valueForKey("cLocal"));
			condition.append("'");
		}
		ctBats = fetchContacts(editingContext(), newCondition(condition.toString()));
		for (int i = 0; i < ctBats.count(); i++) {
			rec = (CktlRecord) ctBats.objectAtIndex(i);
			if (rec.valueForKey("cLocal") != null) {
				rec = rec.recForKey("toBatiment");
				if (!bats.containsObject(rec))
					bats.addObject(rec);
			}
		}
		// Un peti sort et basta...
		CktlSort.sortArray(bats, "cLocal");
		return bats;
	}

	/**
	 * Retourne la liste des objets <i>ImplantationGeo</i> correspondant a tous
	 * les poles geographiques en cours de validite.
	 */
	public NSArray findAllImplantationGeos() {
		return fetchImplantationGeos(editingContext(),
				newCondition("(dDebVal = nil or dDebVal <= %@) and (dFinVal = nil or dFinVal >= %@)",
						new NSArray(new NSTimestamp[] { DateCtrl.now(), DateCtrl.now() })));
	}

	/**
	 * Retourne le batiment ayant le code <code>cLocal</code> ou <i>null</i> si le
	 * batiment n'existe pas.
	 * 
	 * <p>
	 * L'objet retourne appartien � l'entite <i>Batiment</i>.
	 * </p>
	 */
	public EOBatiment findBatimentForCode(String cLocal) {
		NSArray<EOBatiment> bats = fetchBatiments(editingContext(), newCondition("cLocal='" + cLocal + "'"));
		if (bats.count() > 0)
			return bats.objectAtIndex(0);
		else
			return null;
	}

	/**
	 * Retourne la liste des objets <i>Batiment</i> correspondant a tous les
	 * batiments connus.
	 */
	public NSArray<EOBatiment> findAllBatiments() {
		return fetchBatiments(editingContext(), null);
	}

	/**
	 * Retourne la liste de tous les bureaux du batiment avec le code
	 * <code>cLocal</code>.
	 * 
	 * <p>
	 * La liste contient les objets de l'entite <i>Salles</i>.
	 * </p>
	 */
	public NSArray findBureauxForBatiment(String cLocal) {
		return fetchBureau(editingContext(), newCondition("cLocal='" + cLocal + "'"));
	}

	/**
	 * Retourne le bureau avec l'identifiant <code>salNumero</code> ou <i>null</i>
	 * si tel bureau n'existe pas.
	 * 
	 * <p>
	 * L'objet retourne appartient a l'entite <i>Salles</i>.
	 * </p>
	 */
	public EOSalles findBureauForCode(Number salNumero) {
		NSArray<EOSalles> burs = fetchBureau(editingContext(), newCondition("salNumero=" + salNumero));
		if (burs.count() > 0)
			return burs.objectAtIndex(0);
		else
			return null;
	}

	/**
	 * Retourne le code de batiment associe a l'utilisateur avec le numero
	 * <code>noIndividu</code>. Le code du batiment est d'abord recherche parmis
	 * les contacts deja definis (la methode <code>findDefaultContact</code>).
	 * S'ils ne sont pas disponibles, alors le batiment est recherche parmi ceux
	 * associes a l'utilisatuer via la table <i>RepartBureau</i>.
	 * 
	 * <p>
	 * La methode retourne la valeur <i>null</i> si aucun batiment ne peut pas
	 * etre trouve.
	 * </p>
	 * 
	 * @see #findDefaultContact(Number)
	 * @see #findBatimentsForIndividu(Number)
	 */
	public String getDefaultLocalForIndividu(Number noIndividu) {
		// On essaie d'abord parmi les contacts
		CktlRecord rec = findDefaultContact(noIndividu, false);
		if ((rec != null) && (rec.valueForKey("cLocal") != null))
			return rec.stringForKey("cLocal");
		NSArray batList = findBatimentsForIndividu(noIndividu);
		if (batList.count() > 0)
			return ((CktlRecord) batList.objectAtIndex(0)).stringForKey("cLocal");
		return null;
	}

	/**
	 * Retourne le numero de bureau de l'individu <code>noIndividu</code> dans le
	 * batiment avec le code <code>cLocal</code>. Retourne <i>null</i> si aucun
	 * bureau n'est associe a l'individu dans le batiment donnee.
	 */
	public Number getDefaultBureauForIndividu(Number noIndividu, String cLocal) {
		CktlRecord rec;
		NSArray list;
		StringBuffer sb;

		// On cherche d'abord dans le contact et la salle par defaut
		rec = findDefaultContact(noIndividu, false);
		// ... si la salle est bien definie pour le batiment indique
		if ((rec != null) &&
				(rec.numberForKey("salNumero") != null) &&
				cLocal.equals(rec.stringForKey("cLocal")))
			return rec.numberForKey("salNumero");
		// Sinon, dans l'annuaire en general
		sb = new StringBuffer("cLocal='").append(cLocal);
		sb.append("' and tosRepartBureau.noIndividu=").append(noIndividu);
		NSArray bureaux = fetchBureau(editingContext(), newCondition(sb.toString()));
		if (bureaux.count() > 0)
			return ((CktlRecord) bureaux.objectAtIndex(0)).numberForKey("salNumero");
		// Sinon, toutes les salles du batiment dans les contacts
		sb = new StringBuffer("cLocal='");
		sb.append(cLocal).append("' and noIndividu=").append(noIndividu);
		list = fetchContacts(editingContext(), newCondition(sb.toString()));
		if (list.count() > 0) {
			// Le premier contact avec le numero de la salle
			for (int i = 0; i < list.count(); i++) {
				rec = (CktlRecord) list.objectAtIndex(i);
				if (rec.numberForKey("salNumero") != null)
					return rec.numberForKey("salNumero");
			}
		}
		// Mais s'il n'y a rien, il n'y a rien...
		return null;
	}

	/**
	 * Retourne le numero du bureau de l'individu <code>noIndividu</code> dans le
	 * batiment avec le code <code>cLocal</code>. Si <code>salNumero</code> n'est
	 * pas <i>null</i>, verifie si le bureau avec se numero existe bien dans le
	 * batiment. Si c'est le cas, le meme valeur <code>salNumero</code> est
	 * retournee. Si <code>salNumero</code> est <i>null</i>, alors la methode
	 * cherchera la salle par defaut associe a l'individu dans le batiment (appel
	 * a la methode <code>getDefaultBureauForIndividu</code>).
	 * 
	 * @see #getDefaultBureauForIndividu(Number, String)
	 */
	public Number getValideBureauForIndividu(Number noIndividu,
																					String cLocal, Number salNumero) {
		// On verifi d'abord si la salle propose se trouve bien dans le batiment
		if (salNumero != null) {
			StringBuffer sb = new StringBuffer("cLocal='");
			sb.append(cLocal).append("' and salNumero=").append(salNumero);
			if (fetchBureau(editingContext(), newCondition(sb.toString())).count() > 0)
				return salNumero;
		}
		// Sinon, on retourne la salle par defaut
		return getDefaultBureauForIndividu(noIndividu, cLocal);
	}

	/**
	 * Retourne la liste des telephones de la personne <code>persId</code>. La
	 * liste contient les telephones enregistres dans l'annuaire plus ceux qui
	 * sont enregistres en local dans la table des contacts.
	 * 
	 * <p>
	 * La liste retournee est composee des chaines de caracteres.
	 * </p>
	 */
	public NSArray findTelephonesForIndividu(Number persId) {
		NSMutableArray phones = new NSMutableArray();
		NSArray objects;
		Object value;

		// D'abord, les telephones de l'annuaire
		setRefreshFetchedObjects(!useCache());
		objects = findAnnuaireTelephones(persId);
		for (int i = 0; i < objects.count(); i++) {
			value = ((CktlRecord) objects.objectAtIndex(i)).valueForKey("noTelephone");
			if (value != null)
				phones.addObject(value);
		}
		// Ensuite, ceux enregistres en local, s'il en a
		objects = fetchContacts(editingContext(), newCondition("persId=" + persId));
		for (int i = 0; i < objects.count(); i++) {
			value = ((CktlRecord) objects.objectAtIndex(i)).valueForKey("ctNoTelephone");
			if ((value != null) && (!phones.containsObject(value)))
				phones.addObject(value);
		}
		return phones;
	}

	/**
	 * Retourne le numero de telephone par defaut pour l'individu
	 * <code>noIndividu</code> ou <i>null</i> s'il ne peut pas etre retrouve.
	 */
	public String getDefaultTelephoneForIndividu(Number noIndividu) {
		CktlRecord rec = findDefaultContact(noIndividu, false);
		if ((rec != null) && (rec.stringForKey("ctNoTelephone") != null))
			return rec.stringForKey("ctNoTelephone");
		return null;
	}

	/**
	 * Retourne le libelle correspondant a l'enregistrement <code>rec</code> de la
	 * table <i>Salles</i>.
	 */
	public String getLibelleForSalle(CktlRecord rec) {
		if (rec == null)
			return StringCtrl.emptyString();
		//
		StringBuffer sb = new StringBuffer("[");
		int etg = StringCtrl.toInt(rec.stringForKey("salEtage"), -1);
		// pour l'etage, on va afficher les noms beaux
		if (etg < 0)
			sb.append("-?-");
		else if (etg == 0)
			sb.append("rdc.");
		else if (etg == 1)
			sb.append(rec.valueForKey("salEtage")).append("ère");
		else
			sb.append(rec.valueForKey("salEtage")).append("ème");
		sb.append("] ").append(rec.valueForKey("salPorte"));
		return sb.toString();
	}

	/**
	 * Retourne la description de contact indentifie par <code>ctOrdre</code> sous
	 * forme d'une chaine de caracteres.
	 * 
	 * <p>
	 * Le parametre <code>serviceSeparator</code> est une chaine de caracteres a
	 * utiliser pour separer le nom du service/etablissement des autres
	 * informations. Par exemple, "<code>&lt;br&gt;</code>" ou "<code>\n</code>".
	 * S'il est <code>null</code>, alors toutes les informations sont affichees
	 * sur la meme ligne.
	 * </p>
	 * 
	 * <p>
	 * La methode retourne la chaine vide si aucune description ne correspond au
	 * code donnes.
	 * </p>
	 */
	public String getContactDescription(Number ctOrdre, String serviceSeparator) {
		CktlRecord rec;
		StringBuffer sbServ = new StringBuffer();
		StringBuffer sbLoc = new StringBuffer();
		rec = findContact(ctOrdre);
		if (rec != null) {
			if (rec.stringForKey("ctUserLocal").equals("O")) {
				if (rec.valueForKey("cStructService") != null) {
					sbServ.append("Service : ");
					sbServ.append(rec.valueForKeyPath("toService.llStructure")).append(" (");
					sbServ.append(rec.valueForKeyPath("toService.lcStructurePere")).append("). ");
				}
				if (rec.valueForKey("cLocal") != null) {
					sbLoc.append("Batiment : ");
					sbLoc.append(rec.valueForKeyPath("toBatiment.appellation")).append(". ");
				}
				if (rec.valueForKey("salNumero") != null) {
					sbLoc.append("Bureau : ");
					sbLoc.append(getLibelleForSalle(rec.recForKey("toSalles"))).append(". ");
				}
			} else {
				if (rec.stringNormalizedForKey("ctLibelleEtab").length() > 0) {
					sbLoc.append("Etablissement : ");
					sbLoc.append(rec.valueForKey("ctLibelleEtab")).append(". ");
				}
			}
			if (rec.stringNormalizedForKey("ctNoTelephone").length() > 0) {
				sbLoc.append("Téléphone : ");
				sbLoc.append(rec.valueForKey("ctNoTelephone")).append(". ");
			}
			if ((sbServ.length() > 0) && (sbLoc.length() > 0)) {
				if (serviceSeparator != null)
					sbServ.append(serviceSeparator);
				// else
				// sbServ.append(" ");
			}
			sbServ.append(sbLoc.toString());
		}
		return sbServ.toString().trim();
	}

	/**
	 * Retourne la description de contact associe a l'intervention avec le numero
	 * <code>intOrdre</code>. Si le contact est defini (nouvelles demandes), alors
	 * les infos de celui-ci sont utilises. Sinon, les informations des champs
	 * <code>cLocal</code> (batiment) et <code>intServiceConcerne</code> (service
	 * de demandeur) sont utilises (les anciennes demandes).
	 */
	public String getInterventionContactDescription(Number intOrdre,
																									Number ctOrdre,
																									String serviceSeparator) {
		String description = null;
		// On essaye d'abord dans les contactes (new style)
		if (ctOrdre != null)
			description = getContactDescription(ctOrdre, serviceSeparator);
		// Sinon, on cherche dans la definition de l'intervention (old style)
		if (StringCtrl.normalize(description).length() == 0) {
			StringBuffer sbServ = new StringBuffer();
			StringBuffer sbLoc = new StringBuffer();
			NSArray objects;
			CktlRecord recInt, rec;
			// Recupere l'objet intervention
			interventionBus().setRefreshFetchedObjects(!useCache());
			// TODO Remplacer par une reference vers une defaultTransaction()
			Integer transId = interventionBus().beginECTransaction();
			recInt = interventionBus().findIntervention(transId, intOrdre);
			// Recupere le service concerne
			serviceBus().setRefreshFetchedObjects(!useCache());
			rec = serviceBus().structureForCode(
					recInt.stringForKey("intServiceConcerne"));
			// Pour les anciennes DT, le service peut ne pas etre precise
			if (rec != null) {
				sbServ.append("Service : ").append(rec.valueForKey("llStructure"));
				sbServ.append(" (").append(rec.valueForKey("lcStructurePere")).append("). ");
			}
			// Recupere le batiment de l'individu
			objects =
					fetchBatiments(editingContext(), newCondition("cLocal='" + recInt.stringForKey("cLocal") + "'"));
			if (objects.count() > 0) {
				rec = (CktlRecord) objects.objectAtIndex(0);
				sbLoc.append("Batiment : ").append(rec.valueForKey("appellation")).append(". ");
			}
			// Recupere le numero de telephone
			objects = findTelephonesForIndividu(
					individuBus().persIdForNoIndividu(recInt.numberForKey("intNoIndConcerne")));
			if (objects.count() > 0) {
				sbLoc.append("Téléphone : ").append(objects.objectAtIndex(0));
			}
			if ((sbServ.length() > 0) && (sbLoc.length() > 0)) {
				if (serviceSeparator != null)
					sbServ.append(serviceSeparator);
			}
			description = sbServ.append(sbLoc.toString()).toString();
			interventionBus().terminateECTransaction(transId);
		}
		return description;
	}

	/**
	 * Ajoute une nouvelle definition de contact pour la personne
	 * <code>noIndividu</code>. Retourne l'identifiant du nouveau contact ou
	 * <i>null</i> dans le cas de probleme.
	 * 
	 * <p>
	 * La definition des parametres de cette methode suit le meme principe que
	 * ceux de la methode <code>addContact</code> prenant comme parametre un
	 * dictionnaire de donnees.
	 * </p>
	 * 
	 * @see #addContact(Integer, NSDictionary)
	 */
	public Number addContact(Integer transId, Number noIndividu,
														String ctEmail, String ctLibelleEtab, String cLocal,
														String cStructEtab, String cStructService,
														String ctIndQualite, String ctNoTelephone, Number persId,
														Number salNumero, Number adrOrdre, Object ctUserLocal) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			// La definition est locale, si le service et le batiment existe
			if (ctUserLocal == null)
				ctUserLocal = new Boolean((cStructService != null) && (cLocal == null));
			// On cree un nouvel objet
			Number ctOrdre = (Number) fetchObject("SeqContact", null).valueForKey("nextval");
			EOEnterpriseObject recContact = newObjectInEntity("Contact", ec);
			// et on y met les informations
			recContact.takeStoredValueForKey(ctOrdre, "ctOrdre");
			recContact.takeStoredValueForKey(noIndividu, "noIndividu");
			recContact.takeStoredValueForKey(persId, "persId");
			recContact.takeStoredValueForKey(valueIfNull(ctEmail), "ctEmail");
			recContact.takeStoredValueForKey(valueIfNull(ctLibelleEtab), "ctLibelleEtab");
			recContact.takeStoredValueForKey(valueIfNull(cLocal), "cLocal");
			recContact.takeStoredValueForKey(valueIfNull(cStructEtab), "cStructEtab");
			recContact.takeStoredValueForKey(valueIfNull(cStructService), "cStructService");
			recContact.takeStoredValueForKey(valueIfNull(ctIndQualite), "ctIndQualite");
			recContact.takeStoredValueForKey(ctNoTelephone, "ctNoTelephone");
			recContact.takeStoredValueForKey(valueIfNull(salNumero), "salNumero");
			recContact.takeStoredValueForKey(valueIfNull(adrOrdre), "adrOrdre");

			if (ctUserLocal instanceof Boolean) {
				recContact.takeStoredValueForKey(((Boolean) ctUserLocal).booleanValue() ? "O" : "N", "ctUserLocal");
			} else {
				// pas passe en parametre ... on regarde si le VLAN est local ou pas
				recContact.takeStoredValueForKey(userInfo().isNetLocal() ? "O" : "N", "ctUserLocal");
			}
			// Un nouveau contact est toujours valide
			recContact.takeStoredValueForKey("O", "ctValide");
			// // Un nouveau contact n'est pas celui par defaut
			// recContact.takeStoredValueForKey("N", "ctDefaut");
			//
			recContact.takeStoredValueForKey(DateCtrl.now(), "dModification");
			if (transId == null)
				commitECTransaction(localTransId);
			return ctOrdre;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Ajoute une nouvelle definition de contact pour la personne
	 * <code>noIndividu</code>. Les informations sont stockees dans le
	 * dictionnaire ou l'enregistrement <code>values</code>. Les valeurs suivantes
	 * y sont recherchees&nbsp;:
	 * <ul>
	 * <li><code>noIndividu</code> - le numero d'individu&nbsp;;</li>
	 * <li><code>ctEmail</code> - l'adresse email de l'individu (si different de
	 * compte ou si le compte n'existe pas)&nbsp;;</li>
	 * <li><code>ctLibelleEtab</code> - le libelle de l'etablissement (si pas
	 * l'etablissement en cours)&nbsp;;</li>
	 * <li><code>cLocal</code> - le code de batiment&nbsp;;</li>
	 * <li><code>cStructEtab</code> - le code la structure representant
	 * l'etablissement de l'individu&nbsp;;</li>
	 * <li><code>cStructService</code> - le code la structure representant le
	 * service de l'individu&nbsp;;</li>
	 * <li><code>ctIndQualite</code> - le nom de la qualite/activite de
	 * l'individu&nbsp;;</li>
	 * <li><code>ctNoTelephone</code> - le numero de telephone&nbsp;;</li>
	 * <li><code>persId</code> - l'identifiant personnel de l'individu&nbsp;;</li>
	 * <li><code>salNumero</code> - le numero de bureau de l'individu&nbsp;;</li>
	 * <li><code>adrOrdre</code> - le code d'enregistrement dans la table
	 * d'adresses&nbsp;;</li>
	 * <li><code>ctUserLocal</code> - indique si la definition correspond a
	 * l'utilisateur local ou externe.</li>
	 * </ul>
	 * 
	 * Tous les parametres peuvent etre indefinis (pas d'entrees dans le
	 * dictionnaire) sauf <code>noIndivud</code>, <code>persId</code> et
	 * <code>ctNoTelephone</code> qui sont obligatoires. Si le parametre ctDefault
	 * n'est pas defini, sa valeur est considere comme 'O' (<i>true</i>).
	 * 
	 * <p>
	 * La methode retourne le code de contact enregistre ou <i>null</i> dans le
	 * cas de probleme.
	 * </p>
	 */
	public Number addContact(Integer transId, NSKeyValueCoding values) {
		return addContact(transId,
											(Number) values.valueForKey("noIndividu"),
											(String) values.valueForKey("ctEmail"),
											(String) values.valueForKey("ctLibelleEtab"),
											(String) values.valueForKey("cLocal"),
											(String) values.valueForKey("cStructEtab"),
											(String) values.valueForKey("cStructService"),
											(String) values.valueForKey("ctIndQualite"),
											(String) values.valueForKey("ctNoTelephone"),
											(Number) values.valueForKey("persId"),
											(Number) values.valueForKey("salNumero"),
											(Number) values.valueForKey("adrOrdre"),
											values.valueForKey("ctUserLocal"));
	}

	/**
	 * Met a jour la definition de contact par defaut pour l'individu
	 * <code>noIndividu</code>. Supprime toutes les definitions des contacts par
	 * defaut donnees precedement (attribut "cDefault").
	 * 
	 * <p>
	 * Le parametre <code>ctDefautOrdre</code> indique le code de contact qui doit
	 * etre defini par defaut. S'il est <i>null</i> aucun contact ne sera defini
	 * comme etant par defaut suite a l'appel a la methode.
	 * </p>
	 * 
	 * <p>
	 * La methode retourne le nombre d'enregistremments pour lesquels la
	 * definition par defaut a ete supprimee. Elle retourne -1 dans le cas d'une
	 * erreur.
	 * </p>
	 */
	public boolean updateDefaultContact(Integer transId,
																	Number noIndividu,
																	Number ctOrdre) {
		return preferencesBus().updatePrefDefaut(
				transId, noIndividu, ctOrdre, null, null, null, null, null, null);
	}

	/**
	 * Mets a jour le contact avec le numero <code>ctOrdre</code> en modifiant les
	 * valeurs donnees. Uniquement les attributs corrspondant aux parametres avec
	 * les valeurs non <i>null</i> sont modifiees.
	 */
	public boolean updateContact(Integer transId, Number ctOrdre,
																Object ctEmail, String ctLibelleEtab,
																String cLocal, String cStructEtab,
																String cStructService, String ctIndQualite,
																Object ctNoTelephone, Number salNumero,
																Number adrOrdre, Boolean ctUserLocal,
																Boolean ctValide) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			NSArray contacts = fetchContacts(ec, newCondition("ctOrdre=" + ctOrdre));
			// S'il y a plusieurs ou aucun -> erreur
			if (contacts.count() != 1)
				return false;
			EOEnterpriseObject recContact = (EOEnterpriseObject) contacts.objectAtIndex(0);
			// et on y met les informations
			if (ctEmail != null)
				recContact.takeStoredValueForKey(valueIfNull(ctEmail), "ctEmail");
			if (ctLibelleEtab != null)
				recContact.takeStoredValueForKey(valueIfNull(ctLibelleEtab), "ctLibelleEtab");
			if (cLocal != null)
				recContact.takeStoredValueForKey(valueIfNull(cLocal), "cLocal");
			if (cStructEtab != null)
				recContact.takeStoredValueForKey(valueIfNull(cStructEtab), "cStructEtab");
			if (cStructService != null)
				recContact.takeStoredValueForKey(valueIfNull(cStructService), "cStructService");
			if (ctIndQualite != null)
				recContact.takeStoredValueForKey(valueIfNull(ctIndQualite), "ctIndQualite");
			if (ctNoTelephone != null)
				recContact.takeStoredValueForKey(ctNoTelephone, "ctNoTelephone");
			if (salNumero != null)
				recContact.takeStoredValueForKey(valueIfNull(salNumero), "salNumero");
			if (adrOrdre != null)
				recContact.takeStoredValueForKey(valueIfNull(adrOrdre), "adrOrdre");
			if (ctUserLocal != null)
				recContact.takeStoredValueForKey(ctUserLocal.booleanValue() ? "O" : "N", "ctUserLocal");
			if (ctValide != null)
				recContact.takeStoredValueForKey(ctValide.booleanValue() ? "O" : "N", "ctValide");
			// Mettre a jour la date de modification
			recContact.takeStoredValueForKey(DateCtrl.now(), "dModification");
			// Commit immediat s'il n'y avait pas de transactions
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mets a jour le contact avec le numero <code>ctOrdre</code>. Le dictionnaire
	 * <code>dico</code> contient les valeurs a modifies. Les valuers des
	 * attributs qui ne sont pas presents dans le dictionnaire en modifiant les
	 * valeurs donnees. Uniquement les attributs correspondant aux valeurs non
	 * <i>null</i> sont modifies.
	 */
	public boolean updateContact(Integer transId, Number ctOrdre,
																NSDictionary values) {
		CktlLog.log("updateContact() values=" + values);
		return updateContact(transId,
													ctOrdre,
													values.valueForKey("ctEmail"),
												(String) values.valueForKey("ctLibelleEtab"),
												(String) values.valueForKey("cLocal"),
												(String) values.valueForKey("cStructEtab"),
												(String) values.valueForKey("cStructService"),
												(String) values.valueForKey("ctIndQualite"),
												values.valueForKey("ctNoTelephone"),
												(Number) values.valueForKey("salNumero"),
												(Number) values.valueForKey("adrOrdre"),
												(Boolean) values.valueForKey("ctUserLocal"),
												(Boolean) values.valueForKey("ctValide"));
	}

	/**
	 * Supprime la definition de contact avec le code <code>ctOrdre</code>.
	 * Retourne <i>true</i> dans le cas de succes ou <i>false</i> dans le cas
	 * contraire.
	 * 
	 * <p>
	 * La suppression est interdite si le contact est deja utilise dans une
	 * demande (resultat <i>false</i>).
	 * </p>
	 */
	public boolean deleteContact(Integer transId, Number ctOrdre) {
		EOQualifier condition = newCondition("ctOrdre=" + ctOrdre);
		// D'abord, on test si le contact n'est pas utilise.
		NSArray objects = interventionBus().findInterventions(transId, condition);
		if (objects.count() > 0)
			return false;
		Integer localTransId = getTransaction(transId);
		setRefreshFetchedObjects(false);
		boolean succes = (deleteFromTable(localTransId, "Contact", condition) >= 0);
		if (transId == null) {
			if (succes)
				succes = commitECTransaction(localTransId);
		}
		return succes;
	}

	/**
	 * Test si les informations enregistrees dans le contact avec le code
	 * <code>ctOrdre</code> sont completes pour l'individu de ce contact. Les
	 * informations suivant sont obligatoires pour les personnes appartenant aux
	 * reseaux&nbsp;:
	 * <ul>
	 * <li><i>administration et enseignants</i> - le service, le batiment, le
	 * bureau et le numero de telephone&nbsp;;</li>
	 * <li><i>etudiants</i> - le batiment&nbsp;;</li>
	 * <li><i>externes</i> - l'etablissement et le numero de telephone&nbsp;;</li>
	 * </ul>
	 * 
	 * <p>
	 * Toute personne doit posseder un compte, alors la presence de email est
	 * implicitement verifie.
	 * </p>
	 * 
	 * <p>
	 * Le parametre <code>userInfo</code> contient les informations sur
	 * l'individu. Si ce parametre est <i>null</i>, alors les informations sont
	 * initialisees a partir de la base de donnees.
	 * </p>
	 * 
	 * <p>
	 * Si une erreur survient, alors le message d'erreur est recuperable via la
	 * methode getErrorMessage()
	 * </p>
	 */
	public boolean isContactComplet(Number ctOrdre, DTUserInfo userInfo) {
		// Trouver le contact
		CktlRecord recContact = findContact(ctOrdre);
		if (recContact == null)
			return false;
		// S'il faut, on retrouver les infos sur la personne
		if (userInfo == null) {
			userInfo = new DTUserInfo(this);
			userInfo.compteForPersId(recContact.numberForKey("persId"), true);
		}

		// Si la personne est externe : etablissement obligatoire
		if (userInfo.isNetExterne()) {
			if (isNullValue(recContact.valueForKey("ctLibelleEtab")) ||
					(recContact.stringNormalizedForKey("ctLibelleEtab").length() == 0)) {
				setErrorMessage("L'etablissement de la personne doit être indiqué !");
				return false;
			}
		}
		// Le service doit etre indique pour les profs et l'administration
		if (userInfo.isNetRecherche() || userInfo.isNetAdmin()) {
			if (isNullValue(recContact.valueForKey("cStructService"))) {
				setErrorMessage("Le service de la personne doit être indiqué !");
				return false;
			}
		}
		// Le batiment est necessaire pour tous les membres de l'etablissement
		// excepte pour les etudiants
		if (userInfo.isNetLocal() && !userInfo.isNetEdutiant()) {
			if (isNullValue(recContact.valueForKey("cLocal"))) {
				setErrorMessage("Le batiment de la personne doit être indiqué !");
				return false;
			}
		}
		// Les bureau - uniqiement pour les profs et le personnel
		if (userInfo.isNetRecherche() || userInfo.isNetAdmin()) {
			if (isNullValue(recContact.valueForKey("salNumero"))) {
				setErrorMessage("Le bureau de la personne doit être indiqué !");
				return false;
			}
		}
		// Le telephone est obligatoire pour prof, administration et les externes
		if (userInfo.isNetAdmin() || userInfo.isNetRecherche() || userInfo.isNetExterne()) {
			if (recContact.stringNormalizedForKey("ctNoTelephone").length() == 0) {
				setErrorMessage("Le numéro de téléphone de la personne doit être indiqué !");
				return false;
			}
		}
		return true;
	}

	// ajouts pour la DTFast

	/**
	 * Retourne la liste de tous les contacts correspondant aux DTs affectees a
	 * l'utilisateur <code>noIndividu</code> dans le service
	 * <code>cStructure</code>, dont la date de creation est comprise dans la
	 * periode passee en parametre
	 */
	public NSArray findAllContactsIntervenant(Number noIndividu, String cStructure,
			Object dateDebutPeriode, Object dateFinPeriode, String intEtat) {
		EOQualifier qual = newCondition("noIndIntervenant = %@ and cStructureDest = %@ " +
				"and intDateCreation >= %@ and intDateCreation <= %@ and intEtat = %@",
				new NSArray(new Object[] { noIndividu, cStructure, dateDebutPeriode, dateFinPeriode, intEtat }));
		NSArray objects = fetchArray("VContactIntervenant", qual, CktlSort.newSort("nomEtPrenom"));
		return NSArrayCtrl.removeDuplicate(objects);
	}

	// synchronisation des contacts DT et infos annuaire

	private final static String KEY_CONTACT_CT_EMAIL = "ctEmail";

	private final static String[] KEY_ANNU = new String[] {
			"cStructEtab", "cStructService", "cLocal",
			"salNumero", "ctNoTelephone", KEY_CONTACT_CT_EMAIL,
			"ctLibelleEtab" };

	private final static String[] KEY_ANNU_LABEL = new String[] {
			"&eacute;tablissement", "service", "b&agrave;timent",
			"bureau", "num&eacute;ro de t&eacute;l&eacute;phone", "adresse email",
			"libelle de l'&eacute;tablissement" };

	private final static NSDictionary DICO_MATCH_KEY_ANNU = new NSMutableDictionary(KEY_ANNU, KEY_ANNU_LABEL);

	private final static String SUFFIX_KEY_CONTACT = "oO-OoContact";

	/**
	 * Verifie que les donn�es de l'annuaire pour l'individu
	 * <code>noIndividu</code> sont strictement identiques a celle d'un de ses
	 * contacts <code>ctOrdre</code>. La comparaison va s'effectuer sur les cles
	 * suivantes : - cStrucEtab - cStrucService - cLocal - salNumero -
	 * ctNoTelephone - ctEmail
	 * 
	 * @return un dictionnaire contenant les paires cle / valeurs des informations
	 *         qui different (c'est l'annuaire qui est la reference). Les cles de
	 *         l'annuaire porte un libelle provenant du dictionnaire
	 *         <code>KEY_ANNU</code>, celle du contact sont suivies du suffixe
	 *         <code>SUFFIX_KEY_CONTACT</code>
	 * 
	 * @return <em>null</em> si aucun difference n'est constatee.
	 */
	public NSDictionary getDicoDiffContactAnnuaire(Number noIndividu, Number ctOrdre) {
		NSDictionary dicoAnnuaire = getAnnuaireContactInfo(noIndividu);
		CktlRecord recContact = findContact(ctOrdre);
		NSMutableDictionary dicoDiff = new NSMutableDictionary();
		for (int i = 0; i < KEY_ANNU.length; i++) {
			String key = (String) KEY_ANNU[i];
			Object valueAnnu = dicoAnnuaire.objectForKey(key);
			Object valueContact = recContact.valueForKey(key);
			// on test pas si les deux valeurs ne sont pas saisies
			if (valueAnnu == null && valueContact == null) {
				continue;
			}
			boolean isDifferent = true;
			// les 2 sont saisies, on fait la comparaison selon le type de donnee
			if (valueAnnu != null && valueContact != null) {
				if (valueAnnu instanceof Integer) {
					isDifferent = (((Integer) valueAnnu).intValue() != ((Integer) valueContact).intValue());
				} else if (valueAnnu instanceof String) {
					isDifferent = ((((String) valueAnnu).compareTo((String) valueContact)) != 0);
				}
			}
			if (isDifferent) {
				if (valueAnnu != null) {
					dicoDiff.setObjectForKey(valueAnnu, key);
					if (valueContact != null) {
						dicoDiff.setObjectForKey(valueContact, key + SUFFIX_KEY_CONTACT);
					} else {
						dicoDiff.setObjectForKey(NSKeyValueCoding.NullValue, key + SUFFIX_KEY_CONTACT);
					}
				}
			}
		}
		if (dicoDiff.allKeys().count() > 0) {
			return dicoDiff.immutableClone();
		} else {
			return null;
		}
	}

	/**
	 * Retourne un message HTML decrivant de facon lisible le dictionnaire
	 * differenciel entre Annuaire et Contact
	 * 
	 * @param dico
	 * @return
	 */
	public String getHTMLDicoDiff(NSDictionary dico) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dico.allKeys().count(); i++) {
			String key = (String) dico.allKeys().objectAtIndex(i);
			// on ignore s'il s'agit de la cle de contact (deja traitee)
			if (key.endsWith(SUFFIX_KEY_CONTACT)) {
				continue;
			}
			String valueAnnu = (String) dico.valueForKey(key);
			Object valueContact = dico.valueForKey(key + SUFFIX_KEY_CONTACT);
			String label = (String) DICO_MATCH_KEY_ANNU.objectForKey(key);
			sb.append("<li>\n").append(label).append(" : ");
			if (valueAnnu != null) {
				sb.append("Annuaire=\"" + valueAnnu + "\"");
				sb.append(" Contact=\"");
				if (valueContact == NSKeyValueCoding.NullValue) {
					sb.append("&lt;absent&gt;");
				} else {
					sb.append(valueAnnu);
				}
				sb.append("\"");
			}
			sb.append("\n</li>\n");
		}
		if (sb.length() > 0) {
			sb.insert(0, "<ul>\n");
			sb.append("</ul>");
		}
		return sb.toString();
	}

	/**
	 * Dans le cas d'une difference entre donnees du contact et de l'annuaire, si
	 * seulement l'adresse email est impactee, alors on agit differement : - si le
	 * mail du contact est vide : on fait une mise a jour - si le mail du contact
	 * n'est pas vide : on ne fait rien
	 * 
	 * @param dico
	 * @return
	 */
	public boolean shouldUpdate(NSDictionary dicoDiff) {
		boolean shouldUpdate = false;
		if (dicoDiff.allKeys().count() == 2 && (((String) dicoDiff.allKeys().lastObject()).startsWith(KEY_CONTACT_CT_EMAIL))) {
			Object ctMailContact = dicoDiff.objectForKey(KEY_CONTACT_CT_EMAIL + SUFFIX_KEY_CONTACT);
			if (ctMailContact == null || ctMailContact == NSKeyValueCoding.NullValue) {
				shouldUpdate = true;
			}
		}
		return shouldUpdate;
	}

	/**
	 * {@link #shouldUpdate(NSDictionary)}
	 * 
	 * @param dicoDiff
	 * @return
	 */
	public boolean shouldDuplicate(NSDictionary dicoDiff) {
		boolean shouldDuplicate = true;
		if (dicoDiff.allKeys().count() == 2 && (((String) dicoDiff.allKeys().lastObject()).startsWith(KEY_CONTACT_CT_EMAIL))) {
			shouldDuplicate = false;
		}
		return shouldDuplicate;
	}
}
