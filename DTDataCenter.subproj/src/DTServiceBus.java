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
import java.util.StringTokenizer;

import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.dt.server.metier.EOVService;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gestion des informations concernant les services est structures de
 * l'annuaire.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTServiceBus extends DTDataBus {
	/** La definition de tri des services */
	private NSArray sortServices;

	/** Le cache des tous les services */
	private NSArray services;

	/** Le cache des toutes les structures typees Dt */
	private NSArray structuresDt;

	/** Le cache des tous les groupes DT */
	private NSArray<EOGroupesDt> groupesDt;

	/** Le cache des fournisseurs valides internes */
	private NSArray servicesFouValideInterne;

	/**
	 * Cree une nouvelle instance de gestionnaire des services.
	 */
	public DTServiceBus(EOEditingContext eocontext) {
		super(eocontext);
		sortServices = CktlSort.newSort("llStructure");
	}

	/**
	 * Retourne la liste de tous les services enregistres dans l'annaire. La liste
	 * est constitue des objets <code>CktlRecord</code> de l'entite
	 * <i>VService</i>.
	 */
	public NSArray<EOVService> allServices() {
		if ((services == null) || (services.count() == 0))
			services = fetchArray(EOVService.ENTITY_NAME, null, sortServices);
		return services;
	}

	/**
	 * Retourne la liste de tous les groupes enregistres dans l'annaire types DT.
	 * La liste est constitue des objets <code>CktlRecord</code> de l'entite
	 * <i>StructureUlr</i>.
	 */
	public NSArray allStructuresDT() {
		if ((structuresDt == null) || (structuresDt.count() == 0))
			structuresDt = fetchArray("DTStructureUlr", newCondition("tosRepartTypeGroupe.tgrpCode = 'DT'"), sortServices);
		return structuresDt;
	}

	/**
	 * Retourne la liste de tous les services enregistres dans l'annaire en tant
	 * que fournisseur valide interne. La liste est constitue des objets
	 * <code>CktlRecord</code> de l'entite <i>StructureUlr</i>.
	 */
	public NSArray allServicesFouValideInterne() {
		if (servicesFouValideInterne == null || servicesFouValideInterne.count() == 0) {
			// liste de leurs c_structure
			NSArray cStructures = (NSArray) jefyBus().getAllFournissServiceValides().valueForKey("cStructure");
			servicesFouValideInterne = new NSArray();
			for (int i = 0; i < cStructures.count(); i++) {
				String cStructure = (String) cStructures.objectAtIndex(i);
				servicesFouValideInterne = servicesFouValideInterne.arrayByAddingObject(
						structureForCode(cStructure));
			}
			// classement alpha
			servicesFouValideInterne = EOSortOrdering.sortedArrayUsingKeyOrderArray(
					servicesFouValideInterne, sortServices);

		}
		return servicesFouValideInterne;
	}

	/**
	 * Retourne la liste de tous les services enregistres dans la DT. La liste est
	 * constitue des objets <code>CktlRecord</code> de l'entite <i>GroupesDt</i>.
	 * 
	 * @param clearCache
	 *          : faut-il
	 */
	public NSArray<EOGroupesDt> allGroupesDt(boolean clearCache) {
		if (clearCache) {
			groupesDt = null;
		}
		if (groupesDt == null || groupesDt.count() == 0) {
			groupesDt = (NSArray<EOGroupesDt>) fetchArray(EOGroupesDt.ENTITY_NAME, null, CktlSort.newSort(EOGroupesDt.GRP_POSITION_KEY));
			// groupesDt = EOSortOrdering.sortedArrayUsingKeyOrderArray(groupesDt,
			// sortServices);
		}
		return groupesDt;
	}

	/**
	 * Retourne la liste des noms de tous les services enregistres dans
	 * l'annuaire. La liste est constitue des chaines de caracteres.
	 */
	public NSArray allServiceTitles() {
		NSMutableArray titles = new NSMutableArray();
		CktlRecord rec;
		allServices();
		for (int i = 0; i < services.count(); i++) {
			rec = (CktlRecord) services.objectAtIndex(i);
			titles.insertObjectAtIndex(libelleForServiceRec(rec, true, true), i);
		}
		return titles;
	}

	/**
	 * Retourne la liste des services associes a la personne avec identifiant
	 * <code>persId</code>. La liste est composee des objets
	 * <code>CktlRecord</code> de l'entite <i>RepartStructure</i>.
	 */
	public NSArray repartServicesForPersId(Number persId) {
		return fetchArray("DTRepartStructure",
				newCondition("toStructureUlr.tosRepartTypeGroupe.tgrpCode='S' and persId=" + persId),
				null);
	}

	/**
	 * Retourne la liste des structures associes a la personne avec identifiant
	 * <code>persId</code>. La liste est composee des objets
	 * <code>CktlRecord</code> de l'entite <i>RepartStructure</i>.
	 */
	public NSArray repartStructuresForPersId(Number persId) {
		return fetchArray("DTRepartStructure",
				newCondition("persId=" + persId),
				null);
	}

	/**
	 * Test si le service avec le code <code>cStructure</code> est bien le service
	 * de l'annuaire et la personne <code>persId</code> y appartient. Si
	 * <code>persId</code> est <i>null</i>, alors la methode ne teste que si la
	 * structure est bien un service.
	 */
	public boolean isServiceAnnuaire(String cStructure, Number persId) {
		NSArray reponse;
		StringBuffer condition = new StringBuffer();
		// On test, s'il s'agit du service seulement
		condition.append("tosRepartTypeGroupe.tgrpCode='S' and cStructure='");
		condition.append(cStructure).append("'");
		reponse = fetchArray("DTStructureUlr", newCondition(condition.toString()), null);
		// S'il faut, on verifie aussi si la personne appartient au service
		if ((reponse.count() > 0) && (persId != null)) {
			condition.setLength(0);
			condition.append("cStructure='").append(cStructure).append("' and persId=");
			condition.append(persId);
			reponse = fetchArray("DTRepartStructure", newCondition(condition.toString()), null);
		}
		// On doit trouver quelque chose pour que ce soit OK
		return (reponse.count() > 0);
	}

	/**
	 * Retourne la liste des services associes a la personne avec identifiant
	 * <code>persId</code>. La liste est composee des objets
	 * <code>CktlRecord</code> de l'entite <i>StructureUlr</i>.
	 */
	public NSArray servicesForPersId(Number persId) {
		return (NSArray) repartServicesForPersId(persId).valueForKey("toStructureUlr");
	}

	/**
	 * Retourne un objet service ayant le code <code>cStructure</code>. L'objet
	 * retourne appartient a l'entite <i>VService</i>.
	 */
	public EOVService serviceForCode(String cStructure) {
		return (EOVService) fetchObject(EOVService.ENTITY_NAME, "cStructure='" + cStructure + "'", null);
	}

	/**
	 * Retourne le libelle long d'une structure represente par l'enregistremen
	 * <code>rec</code>. L'objet <code>rec</code> doit appartenir a l'entite
	 * <i>StructureUlr</i> ou <i>VService</i>.
	 * 
	 * <p>
	 * Si le parametre <code>useLongTitle</code> est <i>true</i>, alors le libelle
	 * long de la structure pere sera pris, sinon ce sera le libelle court.
	 * </p>
	 * 
	 * <p>
	 * Si le parametre <code>usePere</code> est <i>true</i>, alors le libelle
	 * court de la structure pere sera ajoute au libelle de la structure. Ceci
	 * permet de differencier deux services ayant le meme libelle long.
	 * </p>
	 */
	public String libelleForServiceRec(CktlRecord rec, boolean useLongTitle, boolean usePere) {
		StringBuffer sb = new StringBuffer();
		if (useLongTitle) {
			sb.append(rec.stringNormalizedForKey("llStructure"));
		} else {
			sb.append(rec.stringNormalizedForKey("lcStructure"));
		}
		if ((sb.length() > 0) && usePere)
			sb.append(" (").append(rec.stringForKey("lcStructurePere")).append(")");
		return sb.toString();
	}

	/**
	 * Retourne le libelle long d'une structure ayant le code
	 * <code>cStructure</code>. Si le parametre <code>useLongTitle</code> est
	 * <i>true</i>, alors le libelle long de la structure sera affiche. Si le
	 * parametre <code>usePere</code> est <i>true</i> alors le libelle court de la
	 * structure pere sera ajoute au libelle de la structure. Ceci permet de
	 * differencier deux services ayant le meme libelle long.</p>
	 */
	public String libelleForServiceCode(String cSturcute, boolean useLongTitle, boolean usePere) {
		CktlRecord rec = structureForCode(cSturcute);
		if (rec != null)
			return libelleForServiceRec(rec, useLongTitle, usePere);
		else
			return StringCtrl.emptyString();
	}

	/**
	 * Retourne le nom de l'etablissement au sein de lequel l'application est
	 * executee. Le nom de l'etablissement est etabli de la maniere
	 * suivante&nbsp;:
	 * <ul>
	 * <li>on cherche la structure avec le type 'E' (entite <i>StructureUlr</i>),</li>
	 * <li>sinon, on cherche un etablissement avec le code qui correspond a la
	 * valeur de configuration <code>GRHUM_DEFAULT_RNE</code> (entite <i>Rne</i>).
	 * </li>
	 * </ul>
	 * 
	 * La methode retourne <code>null</code> si le nom de l'etablissement ne peut
	 * pas etre detecte.
	 */
	public String nomEtablissement() {
		CktlRecord rec = structureEtablissement();
		if (rec != null)
			return rec.stringForKey("llStructure");
		else {
			String rne = dtApp().config().stringForKey("GRHUM_DEFAULT_RNE");
			if (rne != null) {
				rec = (CktlRecord) fetchObject("Rne", newCondition("cRne='" + rne + "'"));
				if (rec != null)
					return rec.stringForKey("llRne");
			}
			return null;
		}
	}

	/**
	 * Retourne la structure qui correspond a l'etablissement en cours. Cette
	 * structure doit avoir le type associe 'E'. L'objet retourne appartient a
	 * l'entite <i>StructureUlr</i>.
	 */
	public CktlRecord structureEtablissement() {
		return (CktlRecord) fetchObject("DTStructureUlr", newCondition("cTypeStructure='E' and cStructure=cStructurePere"));
	}

	/**
	 * Retourne la structure ayant le code <code>cStructure</code> ou <i>null</i>
	 * si elle n'existe pas. L'objet retourne appartient a l'entite
	 * <i>StructureUlr</i>.
	 */
	public EOStructure structureForCode(String cStructure) {
		return (EOStructure) fetchObject("DTStructureUlr", newCondition("cStructure='" + cStructure + "'"));
	}

	/**
	 * Retourne la structure ayant l'identifiant <code>persId</code> ou
	 * <i>null</i> si elle n'existe pas. L'objet retourne appartient a l'entite
	 * <i>StructureUlr</i>.
	 */
	public CktlRecord structureForPersId(Number persId) {
		return (CktlRecord) fetchObject("DTStructureUlr", newCondition("persId=" + persId.intValue()));
	}

	/**
	 * Retourne la liste de tous les destinataires des mails de validation des
	 * devis pour le service d�sign� par <code>cStructure</code> ou par son
	 * <code>persId</code>. Si une sous structure de type 'PD' existe, alors ses
	 * membre sont choisit comme secretaires en priorite. Les objets retournes
	 * appartiennent a l'entit� <i>IndividuUlr</i>.
	 */
	public NSArray<EOIndividu> responsablesPrestationForStructure(Object structId) {
		String cStructure = null;
		if (structId instanceof String)
			cStructure = (String) structId;
		else {
			// on retourve le cStructure par son persId
			CktlRecord recStructure = (CktlRecord) fetchObject("DTStructureUlr",
					newCondition("persId=" + ((Number) structId).intValue()));
			if (recStructure != null)
				cStructure = recStructure.stringForKey("cStructure");
		}

		NSArray recordsVp = fetchArray("DTStructureUlr",
				newCondition("cStructurePere='" + cStructure + "' " +
								"AND tosRepartTypeGroupe.tgrpCode='PD'"),
				null);
		if (recordsVp.count() > 0) {
			// le cStructure du groupe de destinataires
			String cStructureVp = ((CktlRecord) recordsVp.
					lastObject()).stringForKey("cStructure");
			NSArray recordsRepart = fetchArray("DTRepartStructure",
					newCondition("cStructure='" + cStructureVp + "'"), null);
			if (recordsRepart.count() > 0) {
				return (NSArray) recordsRepart.valueForKey("toIndividuUlr");
			}
		}
		// pas de secretaires "alternatives", on prend celles du groupe
		NSArray recordsSec = fetchArray("Secretariat",
				newCondition("cStructure='" + cStructure + "'"), null);
		return (NSArray) recordsSec.valueForKey("toIndividuUlr");
	}

	/**
	 * Retourne la liste des personnes qui valident les prestation du service
	 * identifie par <code>cStructure</code> sous forme d'une chaine de
	 * caracteres.
	 * 
	 * <p>
	 * La methode retourne la chaine vide si aucune description ne correspond au
	 * code donnes.
	 * </p>
	 */
	public String getResponsablesPrestationDescription(String cStructure) {
		CktlRecord rec;
		StringBuffer sbValidPrest = new StringBuffer();
		rec = structureForCode(cStructure);
		CktlLog.trace("cStructure : " + cStructure);
		if (rec != null) {
			NSArray responsables = responsablesPrestationForStructure(cStructure);
			for (int i = 0; i < responsables.count(); i++) {
				CktlRecord reponsable = (CktlRecord) responsables.objectAtIndex(i);
				sbValidPrest.append(reponsable.stringForKey("nomEtPrenom"));
				if (i < responsables.count() - 1)
					sbValidPrest.append(", ");
			}
			if (sbValidPrest.length() > 0)
				sbValidPrest.insert(0, "<font CLASS=textEntry>Prestations supervis&eacute;es par : &nbsp;</font>");
		}
		CktlLog.trace("Description :\n " + sbValidPrest.toString());
		return sbValidPrest.toString().trim();
	}

	/**
	 * Indique si le service passe en parametre gere des DT qui utilisent le
	 * service de prestations.
	 * 
	 * FIXME : enorme bidouille ... il faut rajouter cette information dans la
	 * base de donnees !!!!
	 * 
	 * @param cStructure
	 *          : le code de la structure
	 */
	public boolean isServiceDTPrestation(String cStructure) {
		boolean result = false;
		if (!StringCtrl.isEmpty(cStructure) && cStructure.equals("4236"))
			result = true;
		return result;
	}

	/**
	 * Retourne le nombre des groupes dont le libelle court / long est similaire a
	 * celui donnee dans <code>likeNomPrenom</code>.
	 */
	public int rawStructureCountForName(String likeLibelle) {
		return rawStructureCountForCondition(getNomLikeCondition(likeLibelle, "STR"));
	}

	/**
	 * Retourne la liste des descriptions des structure correspondant. Le nom est
	 * recherche en utilisant la condition de similitude ("like").
	 */
	public NSArray rawStructureForName(String likeLibelle) {
		return rawStructureForCondition(getNomLikeCondition(likeLibelle, "STR"));
	}

	/**
	 * Cree une condition de selection d'une structure ayant le libelle
	 * <code>libelle</code>. Pour cree une condition, le <code>libelle</code> est
	 * decompose en utilisant le symbole d'espace. Pour chaque composant, une
	 * condition "like" est creee.
	 */
	private String getNomLikeCondition(String libelle, String tableAlias) {
		// On construit la condition complementaire
		StringBuffer sb = new StringBuffer();
		StringTokenizer st;
		String s;

		libelle = StringCtrl.normalize(libelle).toUpperCase();
		tableAlias = StringCtrl.normalize(tableAlias).toUpperCase();
		st = new StringTokenizer(libelle);
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append("((UPPER(");
			if (tableAlias.length() > 0)
				sb.append(tableAlias).append(".");
			sb.append("LL_STRUCTURE) LIKE '%").append(s).append("%') OR (UPPER(");
			if (tableAlias.length() > 0)
				sb.append(tableAlias).append(".");
			sb.append("LC_STRUCTURE) LIKE '%").append(s).append("%'))");
		}
		return sb.toString();
	}

	/**
	 * Retourne la liste des descriptions des structure correspondant a la
	 * condition donnee.
	 */
	private NSArray rawStructureForCondition(String condition) {
		NSArray objects;
		NSMutableDictionary resultObjects;
		NSDictionary rec;
		EOEditingContext ec = new EOEditingContext();
		NSArray keys = new NSArray();
		StringBuffer sbSQL = new StringBuffer();
		// On construit la requete a executer
		sbSQL.append("SELECT UNIQUE STR.PERS_ID, STR.LL_STRUCTURE, STR.LC_STRUCTURE, STR.C_STRUCTURE ");
		sbSQL.append("FROM GRHUM.STRUCTURE_ULR STR ");
		if (condition != null)
			sbSQL.append(" WHERE ").append(condition);
		sbSQL.append(" ORDER BY STR.PERS_ID");
		// Les cles des dictionnaires avec le contenu d'objets
		keys = keys.arrayByAddingObject("persId").arrayByAddingObject("llStructure").arrayByAddingObject("lcStructure").arrayByAddingObject("cStructure");
		// On recupere les objets
		objects = DTCktlEOUtilities.rawRowsForSQL(ec, "DTWeb", sbSQL.toString(), keys);
		resultObjects = new NSMutableDictionary();
		for (int i = 0; i < objects.count(); i++) {
			rec = (NSDictionary) objects.objectAtIndex(i);
			resultObjects.setObjectForKey(rec, rec.valueForKey("persId").toString());
		}
		return resultObjects.allValues();
	}

	/**
	 * Retourne le nombre des structures correspondant a la condition donnee.
	 */
	private int rawStructureCountForCondition(String condition) {
		NSArray objects;
		EOEditingContext ec = new EOEditingContext();
		StringBuffer sbSQL = new StringBuffer();
		// On construit la requete a executer
		sbSQL.append("SELECT COUNT(*) FROM (");
		sbSQL.append("SELECT UNIQUE STR.PERS_ID ");
		sbSQL.append("FROM GRHUM.STRUCTURE_ULR STR");
		if (condition != null)
			sbSQL.append(" WHERE ").append(condition);
		sbSQL.append(")");
		// On recupere les objets
		objects =
				DTCktlEOUtilities.rawRowsForSQL(ec, "DTWeb", sbSQL.toString(), new NSArray("count"));
		CktlLog.trace("count : " + objects);
		Object value = ((NSDictionary) objects.objectAtIndex(0)).valueForKey("count");
		return Float.valueOf(value.toString()).intValue();
	}

	/**
	 * Obtenir l'enregistrement de l'entite GroupesDt
	 * 
	 * @param cStructure
	 * @return
	 */
	public CktlRecord getGroupesDt(String cStructure) {
		return (CktlRecord) fetchObject(EOGroupesDt.ENTITY_NAME,
				CktlDataBus.newCondition(EOGroupesDt.C_STRUCTURE_KEY + "='" + cStructure + "'"));
	}
}
