import org.cocktail.dt.server.metier.EOVJefyDestin;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/*
 * Copyright Consortium Coktail, 11 d�c. 06
 * 
 * cyril.tarade at univ-lr.fr
 * 
 * Ce logiciel est un programme informatique servant � [rappeler les
 * caract�ristiques techniques de votre logiciel]. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".
 * 
 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.
 * 
 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 
 * 
 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

/**
 * Gestion des donnees financiere en lien avec les donnees Jefyco a partir de
 * 2007
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */

public class DTJefyBus extends DTDataBus {

	private NSArray _destinSort;

	public DTJefyBus(EOEditingContext editingContext) {
		super(editingContext);
	}

	/**
	 * Indique si la base de donnees JEFYCO est utilises avec cette application.
	 */
	private boolean isUseJefyco() {
		return dtApp().config().booleanForKey("APP_USE_JEFYCO");
	}

	/**
	 * Retourne l'objet de l'entite <em>VOrgan</em> correspondant a la
	 * <code>condition</code>. Si plusieurs objets sont selectionnes, alors le
	 * premier de la liste est retourne.
	 */
	private NSArray fetchLignesBud(
			EOEditingContext ec, EOQualifier condition, NSArray sort) {
		return fetchArray(ec, "VJefyOrgan", condition, sort);
	}

	/**
	 * Retourne la liste des ligne budgetaires dont attributs ont les valeurs
	 * indiquees dans les parametres correspondants. Les attributs correspondant
	 * aux parametres avec les valeurs <i>null</i> sont ignores.
	 * 
	 * <p>
	 * Le parametre <code>sort</code> indique l'ordre de tri des objets du
	 * resultat.
	 * </p>
	 */
	public NSArray findLignesBud(String orgUnit, String orgComp, String orgLbud,
																Number orgNiv, Number orgRat, NSArray sort) {
		NSMutableArray champs = new NSMutableArray();
		NSMutableArray valeurs = new NSMutableArray();
		if (orgUnit != null) {
			champs.addObject("orgUnit");
			valeurs.addObject(orgUnit);
		}
		if (orgComp != null) {
			champs.addObject("orgComp");
			valeurs.addObject(orgComp);
		}
		if (orgLbud != null) {
			champs.addObject("orgLbud");
			valeurs.addObject(orgLbud);
		}
		if (orgNiv != null) {
			champs.addObject("orgNiv");
			valeurs.addObject(orgNiv);
		}
		if (orgRat != null) {
			champs.addObject("orgRat");
			valeurs.addObject(orgRat);
		}
		return findLignesBud(champs, valeurs, sort);
	}

	/**
	 * Retourne la liste des enregistrements de l'entite <i>VOrgan</i> dont les
	 * attributs donnes dans la liste <code>champs</code> ont les valeurs donnees
	 * dans la liste correspondante <code>valeurs</code>. Les deux listes
	 * <code>champs</code> et <code>valeurs</code> doivent avoir le meme nombre
	 * d'elements.
	 * 
	 * <p>
	 * Le parametre <code>sort</code> indique l'ordre de tri des elements du
	 * resultat.
	 * </p>
	 */
	public NSArray findLignesBud(NSArray champs, NSArray valeurs, NSArray sort) {
		StringBuffer cond = new StringBuffer();
		for (int i = 0; i < champs.count(); i++) {
			if (i != 0)
				cond.append(" and ");
			cond.append(champs.objectAtIndex(i).toString()).append("=%@");
		}
		// ajout de l'anne en cours
		if (cond.length() > 0)
			cond.append(" and ");
		cond.append("annee=%@");
		valeurs = valeurs.arrayByAddingObject(dtSession().currentAnnee());
		return fetchLignesBud(editingContext(), newCondition(cond.toString(), valeurs), sort);
	}

	/**
	 * @deprecated
	 * @see #findLigneBud(Number)
	 */
	public CktlRecord findLigneBudDeprecated(Number orgOrdre) {
		return (CktlRecord) fetchObject(
				editingContext(), "VJefyOrgan", newCondition("orgOrdre=" + orgOrdre));
	}

	/**
	 * Retourne l'enregistrement de la ligne budgetaire avec le code
	 * <code>orgOrdre</code>. Retourne <i>null</i> si la ligne n'existe pas.
	 */
	public CktlRecord findLigneBud(Number orgId) {
		return (CktlRecord) fetchObject(
				editingContext(), "VJefyOrgan", newCondition("orgId=" + orgId.intValue()));
	}

	/**
	 * Retourne l'enregistrement du type de credit avec le code
	 * <code>tcdCode</code>. Returne <i>null</i> si l'enregistrement n'existe pas.
	 */
	public CktlRecord findTypeCredit(String tcdCode) {
		if (tcdCode == null)
			return null;
		else
			return (CktlRecord) fetchObject("VJefyTypeCredit", newCondition("tcdCode='" + tcdCode + "'"));
	}

	/**
	 * Retourne le description d'une ligne budgetaire selectionnee pour la demande
	 * de travaux avec l'identifiant <code>intOrdre</code>.
	 * 
	 * @param intOrdre
	 *          L'identifiant d'une demande de travaux.
	 * @param isDetailed
	 *          Indique si le format de la description doit etre court ou
	 *          detaille.
	 * @param separator
	 *          Le separateur a utiliser pour distinguer differents elements de la
	 *          description. Par exemple, ",\n" ou ", ". Le separateur est ignore
	 *          si le format n'est pas "detailed".
	 * 
	 * @return La description ou <i>null</i> si aucune ligne n'est selectionnee
	 *         pour la demande donnee.
	 */
	public String getLigneBudDescription(Number intOrdre, boolean isDetailed, String separator) {
		CktlRecord rec = interventionBus().findInterventionInfin(null, intOrdre);
		if (rec != null) {
			return getLigneBudDescription(null, rec.numberForKey("orgId"),
					rec.stringForKey("tcdCode"), isDetailed, separator);
		}
		return null;
	}

	/**
	 * @deprecated
	 * @see #getLigneBudDescription(CktlRecord, Number, String, boolean, String)
	 */
	public String getLigneBudDescriptionDeprecated(CktlRecord recOrgan, Number orgOrdre,
			String tcdCode, boolean isDetailed, String separator) {
		if (recOrgan == null)
			recOrgan = findLigneBud(orgOrdre);
		if (recOrgan != null) {
			CktlRecord recTypeCredit = findTypeCredit(tcdCode);
			StringBuffer sb = new StringBuffer();
			if (isDetailed) {
				sb.append("Unité : ").append(recOrgan.valueForKey("orgUnit")).append(separator);
				sb.append("Composante : ").append(recOrgan.valueForKey("orgComp")).append(separator);
				sb.append("Ligne budgétaire : ").append(recOrgan.valueForKey("orgLbud")).append(separator);
				sb.append("Utilisateur de crédit : ").append(recOrgan.valueForKey("orgUc"));
				if (recTypeCredit != null)
					sb.append(separator).append("Type de crédit : ").append(recTypeCredit.valueForKey("tcdLib"));
			} else {
				sb.append(recOrgan.valueForKey("orgUnit")).append(" / ");
				sb.append(recOrgan.valueForKey("orgComp")).append(" / ");
				sb.append(recOrgan.valueForKey("orgLbud")).append(" / ");
				sb.append(recOrgan.valueForKey("orgUc"));
				if (recTypeCredit != null)
					sb.append(" [TC : ").append(recTypeCredit.valueForKey("tcdLib")).append("]");
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * Retourne le description d'une ligne budgetaire selectionnee dans
	 * l'enregistrement recOrdre ou (si ce denier est <code>null</code>) ayant le
	 * code <code>orgOrdre</code>. Retourne <i>null</i> si la ligne budgetaire
	 * n'existe pas.
	 * 
	 * @param recOrgan
	 * @param orgId
	 * @param tcdCode
	 * @param isDetailed
	 * @param separator
	 * 
	 * @return La description ou <i>null</i> si la ligne n'existe pas.
	 */
	public String getLigneBudDescription(CktlRecord recOrgan, Number orgId,
			String tcdCode, boolean isDetailed, String separator) {
		if (recOrgan == null)
			recOrgan = findLigneBud(orgId);
		if (recOrgan != null) {
			CktlRecord recTypeCredit = findTypeCredit(tcdCode);
			StringBuffer sb = new StringBuffer();
			if (isDetailed) {
				sb.append("Unité : ").append(recOrgan.valueForKey("orgUnit")).append(separator);
				sb.append("Composante : ").append(recOrgan.valueForKey("orgComp")).append(separator);
				sb.append("Ligne budgétaire : ").append(recOrgan.valueForKey("orgLbud")).append(separator);
				sb.append("Utilisateur de crédit : ").append(
						recOrgan.valueForKey("orgUc") != null ? recOrgan.valueForKey("orgUc") : "<aucun>");
				if (recTypeCredit != null)
					sb.append(separator).append("Type de crédit : ").append(recTypeCredit.valueForKey("tcdLib"));
			} else {
				sb.append(recOrgan.valueForKey("orgUnit")).append(" / ");
				sb.append(recOrgan.valueForKey("orgComp")).append(" / ");
				sb.append(recOrgan.valueForKey("orgLbud"));
				if (recOrgan.valueForKey("orgUc") != null) {
					sb.append(" / ").append(recOrgan.valueForKey("orgUc"));
				}
				if (recTypeCredit != null)
					sb.append(" [TC : ").append(recTypeCredit.valueForKey("tcdLib")).append("]");
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * Retourne la definition de tri pour les destinations. On les trie par le
	 * libelle.
	 */
	private NSArray destinSort() {
		if (_destinSort == null)
			_destinSort = CktlSort.newSort("dstLib", CktlSort.Ascending);
		return _destinSort;
	}

	/**
	 * Retourne la liste de toutes les destinations. C'est une liste des objets de
	 * la vue <i>VJefyDestin</i>.
	 */
	public NSArray<EOVJefyDestin> findAllDestin() {
		CktlLog.trace(null);
		if (!isUseJefyco())
			return new NSArray<EOVJefyDestin>();
		//
		// int dstNiv = jefyParams().intForKey("DST_NIV_DEPENSE");
		return fetchArray(EOVJefyDestin.ENTITY_NAME,
				newCondition(EOVJefyDestin.ANNEE_KEY + "=" + dtSession().currentAnnee().intValue()), destinSort());
	}

	/**
	 * Retourne la description de la destination ayante le code
	 * <code>lolfId</code>. Cette methode retourne un enregistremment de la vue
	 * <code>VJefyDestin</code>.
	 */
	private EOVJefyDestin findDestin(Number lolfId) {
		return (EOVJefyDestin) fetchObject(
				EOVJefyDestin.ENTITY_NAME, newCondition(EOVJefyDestin.LOLF_ID_KEY + "=" + lolfId.intValue()));
	}

	/**
	 * Retourne le libelle de la destination LOLF
	 */
	public String libelleDestinationInfin(
			Number lolfId) {
		EOVJefyDestin eoVJefyDestin = null;
		if (lolfId != null) {
			eoVJefyDestin = findDestin(lolfId);
		}
		if (eoVJefyDestin == null) {
			// return "&lt;&nbsp;ind&eacute;fini&nbsp;&gt;";
			return "< indéfini >";
		} else {
			return eoVJefyDestin.dstLib();
		}
	}

	/**
	 * Converti la liste <code>destinRecs</code> des enregistremments de la vue
	 * <i>VJefyDestin</i> dans la liste des objets <code>DTDestinListItem</code>.
	 */
	public NSArray<DTDestinListItem> destinRecToListItems(NSArray<EOVJefyDestin> destinRecs) {
		CktlLog.trace("destinRecs : " + destinRecs);
		NSMutableArray<DTDestinListItem> liste = new NSMutableArray<DTDestinListItem>();
		for (int i = 0; i < destinRecs.count(); i++) {
			liste.addObject(new DTDestinListItem(destinRecs.objectAtIndex(i)));
		}
		return liste;
	}

	/**
	 * Verifie si la structure avec le code <code>cStructure</code> appartient a
	 * la liste des fournisseurs valides.
	 * 
	 * <p>
	 * Cette methode est utilisee pour la creation des prestations internes. La
	 * creation de devis peut echouer si le service n'est pas un fournisseur.
	 * </p>
	 */
	public boolean checkFournisStructure(String cStructure) {
		boolean bol = (getAllFournissValides("S1.C_STRUCTURE='" + cStructure + "'").count() > 0);
		CktlLog.trace("Response : " + bol);
		return bol;
	}

	/**
	 * Retourne la liste des structures de fournisseur valides pour la personne
	 * avec l'identifiant <code>persId</code>.
	 * 
	 * <p>
	 * Cette methode retourne une liste des dictionnaires contenant les valeurs
	 * <code>fouOrdre</code>, <code>persId</code>, <code>noIndividu</code>,
	 * <code>cStructure</code>, <code>lcStructure</code>, <code>llStructure</code>
	 * .
	 * </p>
	 */
	public NSArray getFournisStructures(Number persId) {
		NSArray arr = getAllFournissValides("IND.PERS_ID=" + persId);
		CktlLog.trace("All fournis : " + arr);
		return arr;
	}

	/**
	 * Selectionne la liste des fournisseurs valides suivant la condition donnee.
	 * 
	 * <p>
	 * Cette methode retourne une liste des dictionnaires contenant les valeurs
	 * <code>fouOrdre</code>, <code>persId</code>, <code>noIndividu</code>,
	 * <code>cStructure</code>, <code>lcStructure</code>, <code>llStructure</code>
	 * .
	 * </p>
	 */
	private NSArray getAllFournissValides(String condition) {
		// On trouve le code de la strucutre qui a les codes des fournisseurs
		// valides
		String fouValCode = dtApp().config().stringForKey("ANNUAIRE_FOU_VALIDE_INTERNE");
		if (fouValCode == null)
			return new NSArray();
		// On construit ensuite la requete
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT F.FOU_ORDRE, IND.PERS_ID, IND.NO_INDIVIDU, ");
		sql.append("S1.C_STRUCTURE, S1.LC_STRUCTURE, S1.LL_STRUCTURE ");
		sql.append("FROM GRHUM.INDIVIDU_ULR IND, GRHUM.REPART_STRUCTURE REP1, ");
		sql.append("GRHUM.STRUCTURE_ULR S1, GRHUM.FOURNIS_ULR F, GRHUM.REPART_STRUCTURE REP2 ");
		sql.append("WHERE IND.PERS_ID=REP1.PERS_ID AND REP1.C_STRUCTURE=S1.C_STRUCTURE ");
		sql.append("AND S1.PERS_ID=F.PERS_ID AND F.FOU_VALIDE='O' AND F.PERS_ID=REP2.PERS_ID ");
		sql.append("AND REP2.C_STRUCTURE='").append(fouValCode).append("'");
		if (condition != null)
			sql.append(" AND (").append(condition).append(")");
		// On execute la requete
		CktlLog.trace("--- Condition : ---\n" + sql);
		return EOUtilities.rawRowsForSQL(
				editingContext(), dtApp().mainModelName(), sql.toString(),
				new NSArray(new Object[] {
						"fouOrdre", "persId", "noIndividu", "cStructure", "lcStructure", "llStructure" }));
	}

	/**
	 * Selectionne la liste des structures fournisseurs valides internes.
	 * 
	 * <p>
	 * Cette methode retourne une liste des dictionnaires contenant les valeurs
	 * <code>fouOrdre</code>, <code>persId</code>, <code>cStructure</code>,
	 * <code>lcStructure</code>, <code>llStructure</code>.
	 * </p>
	 */
	protected NSArray getAllFournissServiceValides() {
		// On trouve le code de la strucutre qui a les codes des fournisseurs
		// valides
		String fouValCode = dtApp().config().stringForKey("ANNUAIRE_FOU_VALIDE_INTERNE");
		if (fouValCode == null)
			return new NSArray();
		// On construit ensuite la requete
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT F.FOU_ORDRE, S1.PERS_ID, ");
		sql.append("S1.C_STRUCTURE, S1.LC_STRUCTURE, S1.LL_STRUCTURE ");
		sql.append("FROM GRHUM.REPART_STRUCTURE REP1, ");
		sql.append("GRHUM.STRUCTURE_ULR S1, GRHUM.FOURNIS_ULR F, GRHUM.REPART_STRUCTURE REP2 ");
		// TODO specificite rochelaise : on ignore les services qui sont sous le
		// groupe
		// dont le LC_STRUCTURE est 'ARCH' : structures archivees
		sql.append(",GRHUM.STRUCTURE_ULR SPERE ");
		sql.append("WHERE REP1.C_STRUCTURE=S1.C_STRUCTURE ");
		sql.append("AND S1.PERS_ID=F.PERS_ID AND F.FOU_VALIDE='O' AND F.PERS_ID=REP2.PERS_ID ");
		sql.append("AND REP2.C_STRUCTURE='").append(fouValCode).append("'");
		// TODO specificite rochelaise : on ignore les services qui sont sous le
		// groupe
		// dont le LC_STRUCTURE est 'ARCH' : structures archivees
		sql.append(" AND SPERE.C_STRUCTURE = S1.C_STRUCTURE_PERE AND SPERE.LC_STRUCTURE <> 'ARCH'");
		return EOUtilities.rawRowsForSQL(
				editingContext(), dtApp().mainModelName(), sql.toString(),
				new NSArray(new Object[] { "fouOrdre", "persId", "cStructure", "lcStructure", "llStructure" }));
	}

	/**
	 * Obtenir la valeur <code>fouOrdre</code> associe a une structure designee
	 * par le code <code>cStructure</code>.
	 * 
	 * Cette valeur doit exister si le groupe appartient aux fournisseurs valides.
	 */
	public Integer getFouOrdreForStructure(String cStructure) {
		NSArray recsFouOrdre = (NSArray) getAllFournissValides(
				"S1.C_STRUCTURE='" + cStructure + "'").valueForKey("fouOrdre");
		if (recsFouOrdre.count() > 0) {
			return new Integer(((Number) recsFouOrdre.lastObject()).intValue());
		} else {
			return null;
		}
	}
}
