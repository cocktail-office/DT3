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

import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EODiscussion;
import org.cocktail.dt.server.metier.EODocumentDt;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOHistoriqueMotif;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOInterventionInfin;
import org.cocktail.dt.server.metier.EOInterventionRepro;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.dt.server.metier.EOTraitementType;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.CRIpto;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;

/**
 * Gestionnaire des informations relatives aux intervenants : creation et
 * modification des interventions, affectation des intervenants, ajout des
 * traitements.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class DTInterventionBus extends DTDataBus {

	public static String HiddenCommentMessage = DTStringCtrl.getHTMLString("[Vous n'etes pas autorise a consulter le contenu de ce traitement]");

	/**
	 * Cree une nouvelle instance de gestionnaire.
	 */
	public DTInterventionBus(EOEditingContext eocontext) {
		super(eocontext);
	}

	/**
	 * Retourne la reference vers le gestionnaire d'envoi des messages email.
	 */
	private DTMailCenter mailCenter() {
		return dtSession().mailCenter();
	}

	// ------- Les methodes internes generiques -------//

	/**
	 * Retourne l'objet de l'entite <em>Intervention</em> correspondant a la
	 * <code>condition</code>. Si plusieurs objets sont selectionnes, alors le
	 * premier de la liste est retourne.
	 */
	private EOIntervention fetchIntervention(EOEditingContext ec, EOQualifier condition) {
		return (EOIntervention) fetchObject(ec, EOIntervention.ENTITY_NAME, condition);
	}

	/**
   * 
   */
	private CktlRecord fetchTraitement(EOEditingContext ec, Number traOrdre) {
		return (CktlRecord) fetchObject(ec, "Traitement", newCondition("traOrdre = " + traOrdre));
	}

	/**
   * 
   */
	private CktlRecord fetchDiscussion(EOEditingContext ec, Number disOrdre) {
		return (CktlRecord) fetchObject(ec, EODiscussion.ENTITY_NAME,
				newCondition(EODiscussion.DIS_ORDRE_KEY + " = " + disOrdre));
	}

	/*

  *//**
   * 
   */
	/*
	 * private CktlRecord fetchInterventionReproCfc(EOEditingContext ec, Number
	 * intOrdre, String editeurs, String auteurs, String titre) { return
	 * (CktlRecord)fetchObject(ec, "InterventionReproCfc",
	 * newCondition("intOrdre = "+intOrdre + " and editeurs = '" + editeurs +
	 * "' and auteurs = '"+auteurs+"' and titre = '" + titre + "'")); }
	 */

	/**
	 * Retourne l'affectation de la demande <code>intOrdre</code> et l'intervenant
	 * <code>noIndividu</code>.
	 */
	private CktlRecord fetchIntervenant(EOEditingContext ec, Number intOrdre, Number noIndividu) {
		String condition = "intOrdre=" + intOrdre;
		if (noIndividu != null)
			condition += " and noIndividu=" + noIndividu;
		return (CktlRecord) fetchObject(ec, "Intervenant", newCondition(condition));
	}

	/**
	 * Retourne le masquage de la demande <code>intOrdre</code> et l'intervenant
	 * <code>noIndividu</code>.
	 */
	private CktlRecord fetchInterventionMasquee(EOEditingContext ec, Number intOrdre, Number noIndividu) {
		String condition = "intOrdre=" + intOrdre + " and noIndividu=" + noIndividu;
		return (CktlRecord) fetchObject(ec, "InterventionMasquee", newCondition(condition));
	}

	/**
	 * Retourne l'affectation de code analytique <code>canId</code> a la demande
	 * <code>intOrdre</code>.
	 */
	private CktlRecord fetchInterventionCodeAna(EOEditingContext ec, Number intOrdre, Number canId) {
		String condition = "intOrdre=" + intOrdre + " and canId=" + canId;
		return (CktlRecord) fetchObject(ec, "InterventionCodeAna", newCondition(condition));
	}

	/**
	 * Retourne toutes les affectations de la demande <code>intOrdre</code>.
	 */
	private NSArray fetchIntervenants(EOEditingContext ec, Number intOrdre) {
		return fetchArray(ec, "Intervenant", newCondition("intOrdre=" + intOrdre), null);
	}

	/**
	 * Retourne tous les masques de la demande <code>intOrdre</code>.
	 */
	private NSArray fetchInterventionMasquees(EOEditingContext ec, Number intOrdre) {
		return fetchArray(ec, "InterventionMasquee", newCondition("intOrdre=" + intOrdre), null);
	}

	/**
	 * Selectionne les objets de l'entite <em>Traitement</em> correspondant a la
	 * <code>condition</code>.
	 */
	private NSArray<EOTraitement> fetchTraitements(EOEditingContext ec, EOQualifier condition) {
		return fetchArray(ec, EOTraitement.ENTITY_NAME, condition, null);
	}

	/**
	 * Selectionne les objets de l'entite <em>VTraitement</em> correspondant a la
	 * <code>condition</code>.
	 */
	private NSArray fetchVTraitements(EOEditingContext ec, EOQualifier condition) {
		return fetchArray(ec, "VTraitement", condition, null);
	}

	/**
	 * Selectionne les objets de l'entite <em>Discussion</em> correspondant a la
	 * <code>condition</code>.
	 */
	private NSArray fetchDiscussions(EOEditingContext ec, EOQualifier condition) {
		return fetchArray(ec, EODiscussion.ENTITY_NAME, condition, null);
	}

	/**
	 * Selectionne les objets de l'entite <em>Intervention</em> correspondant a la
	 * <code>condition</code>.
	 */
	private NSArray fetchInterventions(EOEditingContext ec, EOQualifier condition) {
		return fetchArray(ec, "Intervention", condition, null);
	}

	/**
	 * Execute la procedure <code>procedureName</code> avec les donnees
	 * <code>dataDico</code>. Retourne le resultat de son execution. Dans le cas
	 * d'une erreur, son message est memorise et accessible avec la methode
	 * getErrorMessage.
	 */
	private NSDictionary executeInterventionProcedure(
																				String procName, NSDictionary dataDico) {
		NSDictionary result = null;
		boolean ok;
		setErrorMessage(null);
		ok = executeProcedure(procName, dataDico);
		result = executedProcResult();
		if (!ok) {
			setErrorMessage((String) result.objectForKey(ERROR_KEY));
			return null;
		}
		return result;
	}

	// ------- La gestion des interventions -------//

	/**
	 * Enregistre une nouvelle demande dont la description est donnee dans
	 * <code>dataDico</code>. Retourne le dictionnaire avec l'identifiant unique
	 * de la demandes (<em>intOrdre</em>) et le numero de la demande dans la liste
	 * des demandes du service (<em>intCleService</em>).
	 * 
	 * <p>
	 * Returne <em>null</em> si une erreur se produit lors de sauvegarde de
	 * donnees. Dans ce cas la methode <code>getErrorMessage</code> retourne la
	 * description de l'erreur.
	 * </p>
	 */
	public NSDictionary addIntervention(NSDictionary dataDico) {
		return executeInterventionProcedure("insIntervention", dataDico);
	}

	/**
	 * Eregistre une extention "reprographie" d'une demande de travaux.
	 * 
	 * <p>
	 * Retourne <em>true</em> si l'operation est executee avec succes et
	 * <em>false</em> dans la cas contraire.
	 * </p>
	 * 
	 * <p>
	 * Cette metode n'enregistre que la partie specifique reprographie. La demande
	 * doit prealablement etre enregistre avec la methode
	 * <code>addIntervention</code>.
	 * </p>
	 * 
	 * @see #addIntervention(NSDictionary)
	 */
	public boolean addInterventionRepro(Integer transId,
			Number intOrdre, boolean isNumeric, String typeCouv, boolean isCouvDessus,
			boolean isCouvDessous, Integer nbCopies, Integer nbPages, String reliure,
			boolean isPlastific, boolean isLivret, String libelle, String remarques) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			// On cree une instance de l'objet
			CktlRecord rec =
					(CktlRecord) EOUtilities.createAndInsertInstance(ec, "InterventionRepro");
			rec.takeStoredValueForKey(intOrdre, "intOrdre");
			rec.takeStoredValueForKey(
					(isNumeric ? "N" : "P"), "typeDocument"); // Document numeric / papier
			rec.takeStoredValueForKey(typeCouv, "typeCouv");
			rec.takeStoredValueForKey(
					(isCouvDessus ? "O" : "N"), // Couverture oui:non
					"couvDessus");
			rec.takeStoredValueForKey(
					(isCouvDessous ? "O" : "N"), // Couverture oui/non
					"couvDessous");
			rec.takeStoredValueForKey(nbPages, "nbPages");
			rec.takeStoredValueForKey(nbCopies, "nbCopies");
			rec.takeStoredValueForKey(reliure, "reliure");
			rec.takeStoredValueForKey((isPlastific ? "O" : "N"), "plastific");
			rec.takeStoredValueForKey((isLivret ? "O" : "N"), "livret");
			if (StringCtrl.normalize(libelle).length() > 0)
				rec.takeStoredValueForKey(libelle, "libelle");
			if (StringCtrl.normalize(remarques).length() > 0)
				rec.takeStoredValueForKey(remarques, "remarques");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Enregistre une extention "reprographie-CFC" d'une demande de travaux.
	 * 
	 * <p>
	 * Retourne <em>true</em> si l'operation est executee avec succes et
	 * <em>false</em> dans la cas contraire.
	 * </p>
	 * 
	 * <p>
	 * Cette metode n'enregistre que la partie specifique reprographie. La demande
	 * doit prealablement etre enregistre avec les methodes
	 * <code>addIntervention</code> et <code>addInterventionRepro</code>.
	 * </p>
	 * 
	 * @see #addIntervention(NSDictionary)
	 * @see #addInterventionRepro(Integer, Number, boolean, boolean, boolean,
	 *      Integer, Integer, String, String)
	 */
	public boolean addInterventionReproCFC(Integer transId,
			Number intOrdre, String editeurs, String auteurs,
			String titre, Number nbPages, Number nbExemplaires) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			// On cree une instance de l'objet
			// On cree un nouvel objet
			Number cfcOrdre = (Number) fetchObject("SeqInterventionReproCfc", null).valueForKey("nextval");
			EOEnterpriseObject rec = newObjectInEntity("InterventionReproCfc", ec);
			rec.takeStoredValueForKey(cfcOrdre, "cfcOrdre");
			rec.takeStoredValueForKey(intOrdre, "intOrdre");
			if (editeurs != null)
				rec.takeValueForKey(StringCtrl.normalize(editeurs), "editeurs");
			if (auteurs != null)
				rec.takeValueForKey(StringCtrl.normalize(auteurs), "auteurs");
			if (titre != null)
				rec.takeValueForKey(StringCtrl.normalize(titre), "titre");
			if (nbPages != null)
				rec.takeValueForKey(nbPages, "nbPages");
			if (nbExemplaires != null)
				rec.takeValueForKey(nbExemplaires, "nbExemplaires");
			rec.takeStoredValueForKey(DateCtrl.now(), "dCreation");
			rec.takeStoredValueForKey(DateCtrl.now(), "dModification");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Effacer une declaration CFC
	 */
	protected int deleteInterventionReproCFC(Integer transId, CktlRecord recCfc) {
		CktlRecord recInterventionRepro = recCfc.recForKey("toInterventionRepro");
		recInterventionRepro.removeObjectFromBothSidesOfRelationshipWithKey(recCfc, "tosInterventionReproCfc");
		EOQualifier condition = newCondition(
				"intOrdre=%@ AND auteurs=%@ AND editeurs=%@ AND titre=%@",
				new NSArray(new Object[] {
						recCfc.numberForKey("intOrdre"), recCfc.stringForKey("auteurs"),
						recCfc.stringForKey("editeurs"), recCfc.stringForKey("titre") }));
		return deleteFromTable(transId, "InterventionReproCfc", condition);
	}

	/**
	 * Mest a jour une extention avec les informations "financieres" d'une demande
	 * de travaux.
	 * 
	 * <p>
	 * Retourne <em>true</em> si l'operation est executee avec succes et
	 * <em>false</em> dans la cas contraire.
	 * </p>
	 * 
	 * <p>
	 * La demande doit prealablement etre enregistre avec la methode
	 * <code>addIntervention</code>. Cette methode ajoute un nouveau
	 * enregistrement s'il n'existe pas encore ou mets a jour l'enregistrement
	 * deja existant.
	 * </p>
	 * 
	 * @see #addIntervention(NSDictionary)
	 */
	public boolean setInterventionInfin(Integer transId,
			Number intOrdre, Number orgOrdre, String tcdCode,
			Number devOrdre, String dstCode, String canCode,
			String cStructure, Number fouOrdre, Number orgId,
			Number lolfId, Number prestId, Number prestNumero) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			// On essaye de trouver une definition deja enregistre
			EOInterventionInfin rec =
					(EOInterventionInfin) fetchObject(ec, EOInterventionInfin.ENTITY_NAME, newCondition(EOInterventionInfin.INT_ORDRE_KEY + "=" + intOrdre));
			// Si elle n'existe pas encore, on en ajoute une
			if (rec == null)
				rec = (EOInterventionInfin) EOUtilities.createAndInsertInstance(ec, EOInterventionInfin.ENTITY_NAME);
			// rec.takeStoredValueForKey(intOrdre, "intOrdre");
			rec.setIntOrdre((Integer) intOrdre);
			if (orgOrdre != null) {
				// rec.takeStoredValueForKey(orgOrdre, "orgOrdre");
				rec.setOrgOrdre((Integer) orgOrdre);
			}
			if (tcdCode != null) {
				// rec.takeStoredValueForKey(tcdCode, "tcdCode");
				rec.setTcdCode(tcdCode);
			}
			if (devOrdre != null) {
				// rec.takeStoredValueForKey(devOrdre, "devOrdre");
				rec.setDevOrdre((Integer) devOrdre);
			}
			if (dstCode != null) {
				// rec.takeStoredValueForKey(dstCode, "dstCode");
				rec.setDstCode(dstCode);
			}
			if (canCode != null) {
				// rec.takeStoredValueForKey(canCode, "canCode");
				rec.setCanCode(canCode);
			}
			if (cStructure != null) {
				// rec.takeStoredValueForKey(cStructure, "cStructure");
				rec.setCStructure(cStructure);
			}
			if (fouOrdre != null) {
				// rec.takeStoredValueForKey(fouOrdre, "fouOrdre");
				rec.setFouOrdre((Integer) fouOrdre);
			}
			if (orgId != null) {
				// rec.takeStoredValueForKey(orgId, "orgId");
				rec.setOrgId((Integer) orgId);
			}
			if (lolfId != null) {
				// rec.takeStoredValueForKey(lolfId, "lolfId");
				rec.setLolfId((Integer) lolfId);
			}
			if (prestId != null) {
				// rec.takeStoredValueForKey(prestId, "prestId");
				rec.setPrestId((Integer) prestId);
			}
			if (prestNumero != null) {
				// rec.takeStoredValueForKey(prestNumero, "prestNumero");
				rec.setPrestNumero((Integer) prestNumero);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mets a jour la definition d'une intervention.
	 * 
	 * @param ui
	 *          TODO
	 * @param modCode
	 *          TODO
	 * @param intNoIndConcerne
	 *          TODO
	 */
	public boolean updateIntervention(DTUserInfo ui, Integer transId, Number intOrdre,
																		String motif, String commentaire, Number priorite,
																		String etat, String motsCles, Number actOrdre,
																		Number ctOrdre, String modCode, Number intNoIndConcerne) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		String prevMotif = null;
		try {
			EOIntervention eoIntervention = fetchIntervention(
					ec, ERXQ.equals(EOIntervention.INT_ORDRE_KEY, intOrdre));
			if (eoIntervention == null)
				return false;
			if (motif != null) {
				prevMotif = StringCtrl.normalize(eoIntervention.intMotif());
				eoIntervention.setIntMotif(StringCtrl.normalize(motif));
			}
			if (commentaire != null) {
				eoIntervention.setIntCommentaireInterne(StringCtrl.normalize(commentaire));
			}
			if (priorite != null) {
				eoIntervention.setIntPriorite(new Integer(priorite.intValue()));
			}
			if (etat != null) {
				eoIntervention.setIntEtat(etat);
			}
			// si le nouvel etat n'est pas en cours, on supprime les masques
			if (etat != null && !DTEtatBus.isEtat(etat, EOEtatDt.ETAT_EN_COURS)) {
				deleteAllInterventionMasquee(transId, intOrdre);
			}
			if (motsCles != null) {
				eoIntervention.setIntMotsClefs(motsCles);
			}
			if (actOrdre != null) {
				eoIntervention.setActOrdre(new Integer(actOrdre.intValue()));
			}
			if (ctOrdre != null) {
				eoIntervention.setCtOrdre(new Integer(ctOrdre.intValue()));
			}
			if (modCode != null) {
				eoIntervention.setModCode(modCode);
			}
			if (intNoIndConcerne != null) {
				eoIntervention.setIntNoIndConcerne(new Integer(intNoIndConcerne.intValue()));
			}

			// creation d'un enregistremetn d'historique s'il s'agit d'une
			// modification du motif
			if (!StringCtrl.isEmpty(motif) && !motif.equals(prevMotif)) {
				EOIndividu eoIndividu = EOIndividu.fetchFirstRequiredByKeyValue(ec, EOIndividu.NO_INDIVIDU_KEY, ui.noIndividu());
				EOHistoriqueMotif.create(
						ec, DateCtrl.now(), prevMotif, eoIndividu, eoIntervention);
			}

			if (transId == null)
				return commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mets a jour la definition d'une declaration CFC
	 */
	public boolean updateInterventionRefroCfc(Integer transId, Number intOrdre,
			String editeursPrev, String auteursPrev, String titrePrev,
			String editeurs, String auteurs, String titre,
			Number nbPages, Number nbExemplaires) {
		try {
			Integer localTransId = getTransaction(transId);
			EOEnterpriseObject rec = findInterventionReproCfc(localTransId, intOrdre, editeursPrev, auteursPrev, titrePrev);
			if (rec == null)
				return false;
			if (editeurs != null)
				rec.takeValueForKey(StringCtrl.normalize(editeurs), "editeurs");
			if (auteurs != null)
				rec.takeValueForKey(StringCtrl.normalize(auteurs), "auteurs");
			else
				rec.takeValueForKey(StringCtrl.emptyString(), "auteurs");
			if (titre != null)
				rec.takeValueForKey(StringCtrl.normalize(titre), "titre");
			if (nbPages != null)
				rec.takeValueForKey(nbPages, "nbPages");
			if (nbExemplaires != null)
				rec.takeValueForKey(nbExemplaires, "nbExemplaires");
			rec.takeValueForKey(DateCtrl.now(), "dModification");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Transmission d'une DT a un autre service
	 * 
	 * @param transId
	 * @param intOrdre
	 * @param cStructure
	 * @return le nouveau numero de DT ou -1 si erreur
	 */
	public Number updateServiceIntervention(Integer transId, Number intOrdre, String cStructure) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			// recherche l'intervention
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return null;
			// mise a jour de la sequence de la structure
			EOEnterpriseObject recGroupesDT = fetchObject(ec, "GroupesDt",
					newCondition("cStructure='" + cStructure + "' AND gtCode='I'"));
			if (recGroupesDT == null)
				return null;
			int grpSeq = ((Number) recGroupesDT.valueForKey("grpSeq")).intValue();
			recGroupesDT.takeValueForKey(new Integer(grpSeq + 1), "grpSeq");
			// mise a jour du numero de DT externe
			recIntervention.takeValueForKey(new Integer(grpSeq), "intCleService");
			// mise a jour du groupe DT associee
			recIntervention.takeStoredValueForKey(cStructure, "cStructure");
			if (transId == null)
				commitECTransaction(localTransId);
			return new Integer(grpSeq);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Supprime la demande avec le numero <code>intOrdre</code>. Retourne
	 * <i>true</i> dans le cas de succes de la suppression et <code>false</code>
	 * dans d'une echeque.
	 * 
	 * <p>
	 * Cette methode supprime toutes les informations relatives a une demande:
	 * documents attaches, affectations d'intervenants, demande de reprographie,
	 * les descriptions CFC associees, les informations financieres.
	 * </p>
	 */
	public boolean deleteIntervention(Integer transId, Number intOrdre) {
		// DELETE FROM document_dt WHERE int_ordre=a_int_ordre;
		// DELETE FROM intervention_repro_cfc WHERE int_ordre=a_int_ordre;
		// DELETE FROM intervention_repro WHERE int_ordre=a_int_ordre;
		// DELETE FROM intervention_infin WHERE int_ordre=a_int_ordre;
		// DELETE FROM intervention WHERE int_ordre=a_int_ordre;
		if (intOrdre == null)
			return false;
		Integer localTransId = getTransaction(transId);
		EOQualifier condition = newCondition("intOrdre=" + intOrdre);
		boolean succes = true;
		setRefreshFetchedObjects(false);
		succes = (deleteFromTable(localTransId, "DocumentDt", condition) >= 0);
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "Intervenant", condition) >= 0);
		}
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "InterventionMasquee", condition) >= 0);
		}
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "InterventionReproCfc", condition) >= 0);
		}
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "InterventionRepro", condition) >= 0);
		}
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "InterventionInfin", condition) >= 0);
		}
		if (succes) {
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(localTransId, "Intervention", condition) >= 0);
		}
		if (transId == null) {
			if (succes)
				succes = commitECTransaction(localTransId);
		}
		return succes;
	}

	/**
   * 
   */
	private int countInteventionsForIndividu(EOQualifier condition) {
		NSArray objects = fetchArray("VIntervenantDtCount", condition, null);
		int count = 0;
		for (int i = 0; i < objects.count(); i++) {
			count += CktlRecord.recordIntForKey(objects.objectAtIndex(i), "intCount");
		}
		return count;
	}

	/**
  * 
  */
	public int countInterventionsForIndividu(Number noIndividu, String etat, String service) {
		StringBuffer condition = new StringBuffer();
		if (noIndividu != null)
			condition.append("noIndividu=").append(noIndividu);
		if (etat != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("intEtat='").append(etat).append("'");
		}
		if (service != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("cStructure='").append(service).append("'");
		}
		if (condition.length() == 0)
			return -1;
		else
			return countInteventionsForIndividu(newCondition(condition.toString()));
	}

	/**
	 * Retourne la liste des interventions correspondant a la condition donnee. La
	 * liste contient les enregistrements de l'entite <i>Intervention</i>.
	 */
	public NSArray findInterventions(Integer transId, EOQualifier condition) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchInterventions(ec, condition);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne l'enregistrement avec les informations de l'intervention ayant le
	 * code <code>intOrdre</code>. L'objet retourne appartient a l'entite
	 * <i>Intervention</i>.
	 */
	public EOIntervention findIntervention(Integer transId, Number intOrdre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchIntervention(ec, newCondition(EOIntervention.INT_ORDRE_KEY + "=" + intOrdre));
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne l'enregistrement avec les informations de la demande de
	 * reprographie ayant le code <code>intOrdre</code>. L'enregistrement retourne
	 * appartient a l'entite <i>InterventionRepro</i>.
	 */
	public EOInterventionRepro findInterventionRepro(Integer transId, Number intOrdre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return (EOInterventionRepro) fetchObject(
					ec, EOInterventionRepro.ENTITY_NAME, newCondition(EOInterventionRepro.INT_ORDRE_KEY + "=" + intOrdre));
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne la liste des enregistrements avec les descriptions CFC de la
	 * demande reprographie ayant le code <code>intOrdre</code>. La liste contient
	 * les objects de l'entite <i>InterventionReproCFC</i>.
	 */
	public NSArray findInterventionsReproCfc(Integer transId, Number intOrdre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchArray(
					ec, "InterventionReproCfc", newCondition("intOrdre=" + intOrdre), null);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne un enregistrements avec les descriptions CFC de la demande
	 * reprographie ayant le code <code>intOrdre</code>
	 */
	public CktlRecord findInterventionReproCfc(
			Integer transId, Number intOrdre, String editeurs, String auteurs, String titre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			String strQual = "intOrdre=%@ and editeurs=%@ and titre=%@ and auteurs=%@";
			NSArray args = new NSArray(new Object[] { intOrdre, editeurs, titre });
			if (!StringCtrl.isEmpty(auteurs)) {
				args = args.arrayByAddingObject(auteurs);
			} else {
				args = args.arrayByAddingObject("");
			}
			return (CktlRecord) fetchObject(ec, "InterventionReproCfc", newCondition(strQual, args));
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne la liste des enregistrements d'affectation de code analytique a la
	 * demande de travaux <code>intOrdre</code>. La liste contient les objects de
	 * l'entite <i>InterventionCodeAna</i>.
	 */
	public NSArray findInterventionCodeAna(Integer transId, Number intOrdre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchArray(
					ec, "InterventionCodeAna", newCondition("intOrdre=" + intOrdre), null);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne la description des informations "financieres" relatives a
	 * l'intervention avec le code <code>intOrdre</code>. L'objet retourne
	 * appartient a l'entite <i>InterventionInfin</i>.
	 */
	public EOInterventionInfin findInterventionInfin(Integer transId, Number intOrdre) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(true);
			return (EOInterventionInfin) fetchObject(
					ec, EOInterventionInfin.ENTITY_NAME, newCondition("intOrdre=" + intOrdre));
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	// ------- La gestion des traitements -------//

	/**
	 * Ajouter un traitement pour une demande de travaux. Retourne le numero
	 * <code>traOrdre</code> du traitement dans la base de donnee nouvellement
	 * insere, <em>null</em> si une erreur est survenue.
	 */
	public Number addTraitement(Integer transId,
															Number intOrdre, Number noIndividu,
															String traEtat, String traTraitement,
															String traCommentaire, String traTraitementAdditionnel,
															NSTimestamp traDateDeb,
															NSTimestamp traDateFin, boolean consultable,
															String ttyCode) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			Number traOrdre = (Number) fetchObject("SeqTraitement", null).valueForKey("nextval");
			EOEnterpriseObject recTraitement = newObjectInEntity("Traitement", ec);
			recTraitement.takeValueForKey(traOrdre, "traOrdre");
			recTraitement.takeValueForKey(intOrdre, "intOrdre");
			recTraitement.takeValueForKey(noIndividu, "noIndividu");
			recTraitement.takeValueForKey(traEtat, "traEtat");
			recTraitement.takeValueForKey(traTraitement, "traTraitement");
			recTraitement.takeValueForKey(traCommentaire, "traCommentaireInterne");
			recTraitement.takeValueForKey(traTraitementAdditionnel, EOTraitement.TRA_TRAITEMENT_ADDITIONNEL_KEY);
			recTraitement.takeValueForKey(traDateDeb, "traDateDeb");
			recTraitement.takeValueForKey(traDateFin, "traDateFin");
			recTraitement.takeValueForKey((consultable ? "O" : "N"), "traConsultable");
			// On met a jour les references
			// Vers intervention
			setRefreshFetchedObjects(false);
			EOEnterpriseObject rec = fetchIntervention(ec, newCondition("intOrdre = " + intOrdre));
			recTraitement.addObjectToBothSidesOfRelationshipWithKey(rec, "toIntervention");
			// rec.addObjectToBothSidesOfRelationshipWithKey(recTraitement,
			// "tosTraitement");
			// Vers l'individu
			setRefreshFetchedObjects(false);
			rec = fetchObject(ec, "DTIndividuUlr", newCondition("noIndividu=" + noIndividu));
			recTraitement.addObjectToBothSidesOfRelationshipWithKey(rec, "toIndividuUlr");
			// Vers les etats
			// commentaire des 3 lignes suivantes sinon TRA_ETAT=NULL en sql
			// setRefreshFetchedObjects(false);
			// rec = fetchObject(ec, "EtatDt",
			// newCondition("etatCode='"+traEtat+"'"));
			// recTraitement.addObjectToBothSidesOfRelationshipWithKey(rec,
			// "toEtatDt");
			// Ajout de type de traitement
			EOTraitementType traitementType = EOTraitementType.fetchFirstRequiredByKeyValue(ec, EOTraitementType.TTY_CODE_KEY, ttyCode);
			recTraitement.takeValueForKey(traitementType.ttyKey(), "ttyKey");
			recTraitement.addObjectToBothSidesOfRelationshipWithKey(traitementType, "toTraitementType");

			CktlLog.trace("Updated objects : " + ec.updatedObjects());
			CktlLog.trace("Inserted objects : " + ec.insertedObjects());

			if (transId == null)
				commitECTransaction(localTransId);
			return traOrdre;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
   * 
   */
	public Number addTraitement(Integer transId, NSDictionary dico) {
		return addTraitement(null,
							(Number) dico.valueForKey("aIntOrdre"),
							(Number) dico.valueForKey("aNoIndividu"),
							(String) dico.valueForKey("aTraEtat"),
							(String) dico.valueForKey("aTraTraitement"),
							(String) dico.valueForKey("aTraCommentaireInterne"),
							(String) dico.valueForKey("aTraTraitementAdditionnel"),
							(NSTimestamp) dico.valueForKey("aTraDateDeb"),
							(NSTimestamp) dico.valueForKey("aTraDateFin"),
							dico.valueForKey("aTraConsultable").equals("O"),
							(String) dico.valueForKey("aTtyCode"));
	}

	/**
   * 
   */
	public boolean updateTraitement(Integer transId, Number traOrdre,
																	String traitement, String commentaire,
																	String traitementAdditionnel,
																	NSTimestamp dateDeb, NSTimestamp dateFin,
																	boolean consultable, String etat) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchTraitement(ec, traOrdre);
			if (rec == null)
				return false;
			if (traitement != null)
				rec.takeValueForKey(traitement, "traTraitement");
			if (commentaire != null)
				rec.takeValueForKey(commentaire, "traCommentaireInterne");
			if (traitementAdditionnel != null)
				rec.takeValueForKey(traitementAdditionnel, EOTraitement.TRA_TRAITEMENT_ADDITIONNEL_KEY);
			if (dateDeb != null)
				rec.takeValueForKey(dateDeb, "traDateDeb");
			if (dateFin != null)
				rec.takeValueForKey(dateFin, "traDateFin");
			if (etat != null)
				rec.takeValueForKey(etat, "traEtat");
			rec.takeValueForKey((consultable ? "O" : "N"), "traConsultable");
			// TODO Ajouter la mise a jour de la reference sur le nouvel etat.
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
   * 
   */
	public boolean updateTraitement(Integer transId, NSDictionary dico) {
		return updateTraitement(transId,
							(Number) dico.valueForKey("aTraOrdre"),
							(String) dico.valueForKey("aTraTraitement"),
							(String) dico.valueForKey("aTraCommentaireInterne"),
							(String) dico.valueForKey("aTraTraitementAdditionnel"),
							(NSTimestamp) dico.valueForKey("aTraDateDeb"),
							(NSTimestamp) dico.valueForKey("aTraDateFin"),
							dico.valueForKey("aTraConsultable").equals("O"),
							(String) dico.valueForKey("aTraEtat"));
	}

	/**
	 * Supprimer les traitements de la base de données.
	 * 
	 * @return Le nombre d'enregistrement effacés est retourné.
	 * @return <em>-1</em> si erreur
	 */
	public int deleteTraitements(
			Integer transId,
			EOTraitement eoTraitement,
			String etat,
			EOIntervention eoIntervention) {
		// condition pour la suppression de la table TRAITEMENT
		StringBuffer condition = new StringBuffer();
		// condition pour la suppression de la table DOCUMENT_DT
		StringBuffer conditionDoc = new StringBuffer();
		if (etat != null) {
			condition.append("traEtat='").append(etat).append("'");
		}
		if (eoTraitement != null) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("traOrdre=").append(eoTraitement.traOrdre());
			conditionDoc.append("traOrdre=").append(eoTraitement.traOrdre());
		}
		if (eoIntervention != null) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("intOrdre=").append(eoIntervention.intOrdre());
			if (conditionDoc.length() > 0) {
				conditionDoc.append(" and ");
			}
			conditionDoc.append("intOrdre=").append(eoIntervention.intOrdre());
		}
		// suppression de la table DOCUMENT_DT
		// TODO : faire plus propre, avec une modification dans le modele
		// on ne supprime pas les fichiers GEDFS si c'est une suppression
		// de traitement planifie !!
		boolean isTraitementPlanifie = (!StringCtrl.isEmpty(etat) && etat.equals(EOEtatDt.ETAT_EN_COURS));
		if (!isTraitementPlanifie && conditionDoc.length() > 0) {
			boolean succes = true;
			setRefreshFetchedObjects(false);
			succes = (deleteFromTable(transId, "DocumentDt", newCondition(conditionDoc.toString())) >= 0);
			if (!succes) {
				return -1;
			}
		}

		// Securite : on interdit la suppression de tous les traitements
		if (StringCtrl.normalize(condition.toString()).length() > 0) {
			if (eoTraitement != null) {
				eoIntervention.removeFromTosTraitementRelationship(eoTraitement);
			} else {
				NSArray<EOTraitement> eoTraitementArray = eoIntervention.tosTraitement();
				for (int i = 0; i < eoTraitementArray.count(); i++) {
					eoIntervention.removeFromTosTraitementRelationship(eoTraitement);
				}
			}
		}

		int result = deleteTraitements(transId, condition.toString());
		return result;
	}

	/**
   * 
   */
	private int deleteTraitements(Integer transId, String condition) {
		// Securite : on interdit la suppression de tous les traitements
		if (StringCtrl.normalize(condition).length() == 0)
			return -1;
		setRefreshFetchedObjects(false);
		return deleteFromTable(transId, "Traitement", newCondition(condition));
	}

	/**
	 * Donne une liste de traitements
	 */
	public NSArray findTraitements(Number traOrdre,
																	String traEtat,
																	Number intOrdre,
																	Number noIndividu) {
		StringBuffer condition = new StringBuffer();
		if (traOrdre != null)
			condition.append("traOrdre=").append(traOrdre);
		if (traEtat != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("traEtat='").append(traEtat).append("'");
		}
		if (intOrdre != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("intOrdre=").append(intOrdre);
		}
		if (noIndividu != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("noIndividu=").append(noIndividu);
		}
		return findTraitements(null, newCondition(condition.toString()));
	}

	/**
	 * Retourne la liste des traitements correspondant a la condition donnee. La
	 * liste contient les enregistrements de l'entite <i>Traitement</i>.
	 */
	private NSArray<EOTraitement> findTraitements(Integer transId, EOQualifier condition) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchTraitements(ec, condition);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Retourne la liste des traitements correspondant a la condition donnee. La
	 * liste contient les enregistrements de l'entite <i>VTraitement</i>.
	 */
	private NSArray findVTraitements(Integer transId, EOQualifier condition) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchVTraitements(ec, condition);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Donne la liste des traitements de l'entite <code>Traitement</code>
	 * effectues par un agent sur une periode donnee. Cette methode retourne des
	 * objets du type <code>CktlRecord</code> de l'entite Traitement.
	 * 
	 * @param noIndividu
	 *          : l'agent concerne
	 * @param debut
	 *          : le debut de la periode
	 * @param fin
	 *          : la fin de la periode
	 * @param traOrdre
	 *          : si renseigne, on ignore le traitement ayant cet identifiant
	 */

	public NSArray<EOTraitement> findTraitementsForPeriodeIgnoring(Number noIndividu, NSTimestamp debut,
			NSTimestamp fin, Number traOrdre) {
		return findTraitements(null, getDateTraitementQualifier(noIndividu, debut, fin, traOrdre));
	}

	/**
	 * Donne la liste des traitements de l'entite <code>VTraitement</code>
	 * effectues par un agent sur une periode donnee. Cette methode retourne des
	 * objets du type <code>CktlRecord</code> de l'entite Traitement.
	 * 
	 * @param noIndividu
	 *          : l'agent concerne
	 * @param debut
	 *          : le debut de la periode
	 * @param fin
	 *          : la fin de la periode
	 * @param traOrdre
	 *          : si renseigne, on ignore le traitement ayant cet identifiant
	 */

	public NSArray findVTraitementsForPeriodeIgnoring(Number noIndividu, NSTimestamp debut,
			NSTimestamp fin, Number traOrdre) {
		return findVTraitements(null, getDateTraitementQualifier(noIndividu, debut, fin, traOrdre));
	}

	/**
	 * Donne la liste des traitements de l'entite <code>VTraitement</code>
	 * effectues par un agent pour une intervention donnee.
	 * 
	 * @param noIndividu
	 *          : l'agent concerne
	 * @param intOrdre
	 *          : l'intervention associee
	 */

	public NSArray findVTraitementsForIndividuAndIntervention(Number noIndividu, Number intOrdre) {
		return findVTraitements(null, newCondition(
				"noIndividu=" + noIndividu.intValue() + " and intOrdre=" + intOrdre.intValue()));
	}

	/**
	 * Construire le qualifier qui permet d'obtenir une liste de traitements par
	 * individu. On peux definir un traitement a ignorer
	 * <code>traOrdreToIgnore</code>. Ce qualifier peut etre utilise sur les
	 * entites <code>Traitement</code> et <code>VTraitement</code>.
	 * 
	 * @param noIndividu
	 *          : l'agent concerne
	 * @param debut
	 *          : le debut de la periode
	 * @param fin
	 *          : la fin de la periode
	 * @param traOrdreToIgnore
	 *          : si renseigne, on ignore le traitement ayant cet identifiant
	 * @return
	 */
	private EOQualifier getDateTraitementQualifier(Number noIndividu, NSTimestamp debut,
			NSTimestamp fin, Number traOrdreToIgnore) {
		String strQual = "noIndividu = %@ AND (" +
				"(traDateDeb > %@ AND traDateDeb < %@) OR " + // debut dans le nouveau
																											// traitement
				"(traDateFin > %@ AND traDateFin < %@) OR " + // fin dans le nouveau
																											// traitement
				"(traDateDeb <= %@ AND traDateFin >= %@))"; // englobe le nouveau
																										// traitement
		if (traOrdreToIgnore != null)
			strQual += " AND traOrdre <> %@";
		NSArray args = new NSArray(new Object[] {
				noIndividu,
				debut, fin,
				debut, fin,
				debut, fin });
		if (traOrdreToIgnore != null) {
			args = args.arrayByAddingObject(traOrdreToIgnore);
		}
		return newCondition(strQual, args);
	}

	// ------- La gestion des discussions -------//

	/**
	 * Ajouter une message de discussion pour repondre a une demande de travaux,
	 * ou a un traitement ou a un autre message de discussion Retourne le numero
	 * <code>disOrdre</code> du message de discussion dans la base de donnee
	 * nouvellement insere, <em>null</em> si une erreur est survenue.
	 */
	public Integer addDiscussion(Integer transId,
															Number intOrdre, Number traOrdre, Number disOrdrePere,
															Number noIndividu, String disMessage, NSTimestamp disDate) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			Integer disOrdre = new Integer(((Number) fetchObject("SeqDiscussion", null).valueForKey("nextval")).intValue());
			EOEnterpriseObject recDiscussion = newObjectInEntity(EODiscussion.ENTITY_NAME, ec);
			recDiscussion.takeValueForKey(disOrdre, EODiscussion.DIS_ORDRE_KEY);
			recDiscussion.takeValueForKey(intOrdre, EODiscussion.INT_ORDRE_KEY);
			recDiscussion.takeValueForKey(noIndividu, EODiscussion.DIS_NO_INDIVIDU_KEY);
			if (traOrdre != null) {
				recDiscussion.takeValueForKey(traOrdre, EODiscussion.TRA_ORDRE_KEY);
			}
			if (disOrdrePere != null) {
				recDiscussion.takeValueForKey(disOrdrePere, EODiscussion.DIS_ORDRE_PERE_KEY);
			}
			recDiscussion.takeValueForKey(disMessage, EODiscussion.DIS_MESSAGE_KEY);
			recDiscussion.takeValueForKey(disDate, EODiscussion.DIS_DATE_KEY);
			// On met a jour les references
			// Vers intervention
			setRefreshFetchedObjects(false);
			EOEnterpriseObject rec = fetchIntervention(ec, newCondition("intOrdre = " + intOrdre));
			recDiscussion.addObjectToBothSidesOfRelationshipWithKey(rec, EODiscussion.TO_INTERVENTION_KEY);
			// Vers l'individu
			setRefreshFetchedObjects(false);
			rec = fetchObject(ec, "DTIndividuUlr", newCondition("noIndividu=" + noIndividu));
			recDiscussion.addObjectToBothSidesOfRelationshipWithKey(rec, EODiscussion.TO_INDIVIDU_ULR_KEY);
			// Vers traitement
			if (traOrdre != null) {
				setRefreshFetchedObjects(false);
				rec = fetchTraitement(ec, traOrdre);
				recDiscussion.addObjectToBothSidesOfRelationshipWithKey(rec, EODiscussion.TO_TRAITEMENT_KEY);
			}
			// Vers discussion
			if (disOrdrePere != null) {
				setRefreshFetchedObjects(false);
				rec = fetchDiscussion(ec, disOrdrePere);
				recDiscussion.addObjectToBothSidesOfRelationshipWithKey(rec, EODiscussion.TO_DISCUSSION_PERE_KEY);
			}
			// Dates
			recDiscussion.takeValueForKey(DateCtrl.now(), EODiscussion.D_CREATION_KEY);
			recDiscussion.takeValueForKey(DateCtrl.now(), EODiscussion.D_MODIFICATION_KEY);

			CktlLog.trace("Updated objects : " + ec.updatedObjects());
			CktlLog.trace("Inserted objects : " + ec.insertedObjects());

			if (transId == null)
				commitECTransaction(localTransId);
			return disOrdre;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
   * 
   */
	public boolean updateDiscussion(Integer transId, Number disOrdre,
																	String message) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchDiscussion(ec, disOrdre);
			if (rec == null)
				return false;
			if (message != null) {
				rec.takeValueForKey(message, EODiscussion.DIS_MESSAGE_KEY);
				rec.takeValueForKey(DateCtrl.now(), EODiscussion.D_MODIFICATION_KEY);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprimer les discussions de la base de donn�es.
	 * 
	 * @return Le nombre d'enregistrement effac�s est retourn�.
	 * @return <em>-1</em> si erreur
	 */
	public int deleteDiscussions(Integer transId, Number intOrdre,
																Number traOrdre, Number disOrdre) {
		StringBuffer condition = new StringBuffer();
		if (intOrdre != null) {
			condition.append(EODiscussion.INT_ORDRE_KEY).append("=").append(intOrdre);
		}
		if (traOrdre != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append(EODiscussion.TRA_ORDRE_KEY).append("=").append(traOrdre);
		}
		if (disOrdre != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append(EODiscussion.DIS_ORDRE_KEY).append("=").append(disOrdre);
		}
		// Le test si la condition est vide, ceci se fait dans l'autre methode
		return deleteDiscussions(transId, condition.toString());
	}

	/**
   * 
   */
	private int deleteDiscussions(Integer transId, String condition) {
		// Securite : on interdit la suppression de toutes les discussions
		if (StringCtrl.normalize(condition).length() == 0)
			return -1;
		setRefreshFetchedObjects(false);
		return deleteFromTable(transId, EODiscussion.ENTITY_NAME, newCondition(condition));
	}

	/**
	 * Donne une liste de message de discussion
	 */
	public NSArray findDiscussions(Number intOrdre,
																	Number traOrdre,
																	Number disOrdre,
																	Number noIndividu) {
		StringBuffer condition = new StringBuffer();
		if (intOrdre != null) {
			condition.append("intOrdre=").append(intOrdre);
		}
		if (traOrdre != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("traOrdre=").append(traOrdre);
		}
		if (disOrdre != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("disOrdre=").append(disOrdre);
		}
		if (noIndividu != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("noIndividu=").append(noIndividu);
		}
		return findDiscussions(null, newCondition(condition.toString()));
	}

	/**
	 * Retourne la liste de messages correspondant a la condition donnee. La liste
	 * contient les enregistrements de l'entite <i>Discussion</i>.
	 */
	private NSArray findDiscussions(Integer transId, EOQualifier condition) {
		EOEditingContext ec = econtextForTransaction(transId, true);
		try {
			setRefreshFetchedObjects(false);
			return fetchDiscussions(ec, condition);
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Verifier acc�s a l'ajout d'une discussion sur la DT par la personne. Les
	 * personnes autorisees sont : - intervenants - demandeur / appelant -
	 * intervenants du service demandeur - responsables fonctionnels de l'activite
	 * (les autres responsables techniques et intervenants sont normalement
	 * intervenants du service ...)
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canAjouterDiscussion(EOIntervention recIntervention, Number noIndividu) {
		// verifier que la DT est "discutable"
		boolean ok = DTEtatBus.isEtatTraitement(recIntervention.stringForKey("intEtat"));
		if (ok) {
			// verifier qu'il est intervenant
			ok = recIntervention.isIntervenant(
					dtSession().dtUserInfo().noIndividu());
			// verifier qu'il est le demandeur ou appelant
			if (!ok) {
				ok = (recIntervention.intForKey("intNoIndAppelant") == dtSession().dtUserInfo().noIndividu().intValue() ||
						recIntervention.intForKey("intNoIndConcerne") == dtSession().dtUserInfo().noIndividu().intValue());
			}
			// verifier qu'il est intervenant du service
			if (!ok) {
				ok = (preferencesBus().getDroitsForService(
						dtSession().dtUserInfo().noIndividu(), recIntervention.stringForKey("cStructure")) >= 0);
			}
			// verifier qu'il est responsable de l'activite associee
			if (!ok) {
				ok = activiteBus().isActiviteResponsable(
						recIntervention.numberForKey("actOrdre"),
						EOActivites.TYPE_RESP_FONCTIONNEL,
						dtSession().dtUserInfo().noIndividu());
			}
		}
		return ok;
	}

	// ------- La gestion des intervenants -------//

	/**
   * 
   */
	public boolean addIntervenant(Integer transId, Number intOrdre,
																Number noIndividu, Number noIndAffectant) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOIntervention recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			if (recIntervention.isIntervenant(noIndividu))
				return true;
			EOEnterpriseObject recIntervenant = newObjectInEntity("Intervenant", ec);
			if (recIntervenant == null)
				return false;
			recIntervenant.takeValueForKey(intOrdre, "intOrdre");
			recIntervenant.takeValueForKey(noIndividu, "noIndividu");
			recIntervenant.addObjectToBothSidesOfRelationshipWithKey(
					fetchObject(ec, "DTIndividuUlr", newCondition("noIndividu=" + noIndividu)), "toIndividuUlr");
			recIntervenant.takeValueForKey(noIndAffectant, "intvNoIndAffectant");
			recIntervenant.addObjectToBothSidesOfRelationshipWithKey(recIntervention, "toIntervention");
			recIntervention.addObjectToBothSidesOfRelationshipWithKey(recIntervenant, "tosIntervenant");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Affectation d'un code analytique pro-ratis� a une DT
	 * 
	 * @param transId
	 * @param intOrdre
	 * @param canId
	 * @param quotite
	 * @return
	 */
	public boolean addInterventionCodeAna(Integer transId, Number intOrdre,
			Number canId, Number quotite) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			EOEnterpriseObject recIntCodeAna = newObjectInEntity("InterventionCodeAna", ec);
			if (recIntCodeAna == null)
				return false;
			recIntCodeAna.takeValueForKey(intOrdre, "intOrdre");
			recIntCodeAna.takeValueForKey(canId, "canId");
			recIntCodeAna.takeValueForKey(quotite, "quotite");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mise a jour de la quotite d'une affectation de code analytique.
	 */
	public boolean updateInterventionCodeAna(Integer transId,
			Number intOrdre, Number canId, Number quotite) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchInterventionCodeAna(ec, intOrdre, canId);
			if (rec == null)
				return false;
			if (quotite != null)
				rec.takeValueForKey(quotite, "quotite");
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime l'affectation de la demande <code>intOrdre</code> a l'intervenant
	 * <code>noIndividu</code>. Retourne <i>true</i> si la suppression est
	 * effectue avec succes et <i>false</i> dans le cas contraire.
	 */
	public boolean deleteIntervenant(Integer transId, Number intOrdre, Number noIndividu) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			EOEnterpriseObject recIntervenant = fetchIntervenant(ec, intOrdre, noIndividu);
			if (recIntervenant == null)
				return false;
			recIntervenant.removeObjectFromBothSidesOfRelationshipWithKey(recIntervention, "toIntervention");
			recIntervention.removeObjectFromBothSidesOfRelationshipWithKey(recIntervenant, "tosIntervenant");
			ec.deleteObject(recIntervenant);
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime l'affectation d'un code analytique d'une DT. Retourne <i>true</i>
	 * si l' operation est effectue avec succes et <i>false</i> dans le cas
	 * contraire.
	 */
	public boolean removeInterventionCodeAna(Integer transId, Number intOrdre, Number canId) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			EOEnterpriseObject recIntCodeAna = fetchInterventionCodeAna(ec, intOrdre, canId);
			if (recIntCodeAna == null)
				return false;
			ec.deleteObject(recIntCodeAna);
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime toutes les affectations relatives a la demande
	 * <code>intOrdre</code>.
	 */
	public boolean deleteAllIntervenants(Integer transId, Number intOrdre) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			NSArray allAffectations = fetchIntervenants(ec, intOrdre);
			if (allAffectations.count() == 0)
				return true;
			EOEnterpriseObject recIntervenant;
			for (int i = 0; i < allAffectations.count(); i++) {
				recIntervenant = (CktlRecord) allAffectations.objectAtIndex(i);
				recIntervenant.removeObjectFromBothSidesOfRelationshipWithKey(recIntervention, "toIntervention");
				recIntervention.removeObjectFromBothSidesOfRelationshipWithKey(recIntervenant, "tosIntervenant");
				ec.deleteObject(recIntervenant);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime toutes les masques relatifs a la demande <code>intOrdre</code>.
	 * Est utilisee des que la demande n'est plus en cours.
	 */
	public boolean deleteAllInterventionMasquee(Integer transId, Number intOrdre) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			setRefreshFetchedObjects(false);
			EOEnterpriseObject recIntervention = fetchIntervention(ec, newCondition("intOrdre=" + intOrdre));
			if (recIntervention == null)
				return false;
			NSArray allMasques = fetchInterventionMasquees(ec, intOrdre);
			if (allMasques.count() == 0)
				return true;
			EOEnterpriseObject recMasque;
			for (int i = 0; i < allMasques.count(); i++) {
				recMasque = (CktlRecord) allMasques.objectAtIndex(i);
				ec.deleteObject(recMasque);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Teste si l'utilisateur <code>noIndividu</code> a le droit de changer l'etat
	 * de la demande <code>intOrdre</code>. Si <code>noIndividu</code> est
	 * <i>null</i>, alors on considere qu'il s'agit de l'individu actuellement
	 * connecte a l'application.
	 */
	public boolean canChangeEtat(EOIntervention recIntervention, Number noIndividu) {
		DTUserInfo ui = null;
		// Si c'est le meme individu, que celui connecte a l'application, on
		// reutilise les informations deja connues
		if ((noIndividu == null) ||
				(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
			ui = userInfo();
		} else {
			// Sinon, on recupere les information sur l'individu
			String login = individuBus().loginForNoIndividu(noIndividu);
			if (login != null)
				ui = preferencesBus().getUserInfoForLogin(login);
		}
		// Si les infos sont connues, on peut tester si l'utilisateur peut changer
		// l'etat de la demande
		String cStructure = recIntervention.stringForKey("cStructure");
		if (ui != null) {
			if (userInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER, cStructure)) {
				return true;
			} else if (recIntervention.isIntervenant(userInfo().noIndividu()) &&
									userInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT, cStructure)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Teste si l'utilisateur <code>noIndividu</code> a le droit de changer
	 * l'activite de la demande <code>intOrdre</code>. Si <code>noIndividu</code>
	 * est <i>null</i>, alors on considere qu'il s'agit de l'individu actuellement
	 * connecte a l'application.
	 */
	public boolean canChangeActivite(EOIntervention recIntervention, Number noIndividu) {
		if (recIntervention == null) {
			return false;
		}
		DTUserInfo ui = null;
		// Si c'est le meme individu, que celui connecte a l'application, on
		// reutilise les informations deja connues
		if ((noIndividu == null) ||
				(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
			ui = userInfo();
		} else {
			// Sinon, on recupere les information sur l'individu
			String login = individuBus().loginForNoIndividu(noIndividu);
			if (login != null)
				ui = preferencesBus().getUserInfoForLogin(login);
		}
		return ui.noIndividu().intValue() == recIntervention.intNoIndConcerne().intValue() ||
				ui.noIndividu().intValue() == recIntervention.intNoIndAppelant().intValue() ||
				userInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER, recIntervention.cStructure()); /*
																																																 * &&
																																																 * !
																																																 * DTEtatBus
																																																 * .
																																																 * isEtatFinal
																																																 * (
																																																 * recIntervention
																																																 * .
																																																 * stringForKey
																																																 * (
																																																 * "intEtat"
																																																 * )
																																																 * ;
																																																 */
	}

	/**
	 * Teste si l'utilisateur <code>noIndividu</code> a le droit d'intervenir sur
	 * l'association des codes analytiques de la demande <code>intOrdre</code>. Si
	 * <code>noIndividu</code> est <i>null</i>, alors on considere qu'il s'agit de
	 * l'individu actuellement connecte a l'application.
	 */
	public boolean canChangeCodeAna(CktlRecord recIntervention, Number noIndividu) {
		if (recIntervention == null)
			return false;
		DTUserInfo ui = null;
		// Si c'est le meme individu, que celui connecte a l'application, on
		// reutilise les informations deja connues
		if ((noIndividu == null) ||
				(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
			ui = userInfo();
		} else {
			// Sinon, on recupere les information sur l'individu
			String login = individuBus().loginForNoIndividu(noIndividu);
			if (login != null)
				ui = preferencesBus().getUserInfoForLogin(login);
		}
		return (((ui.noIndividu().intValue() == recIntervention.intForKey("intNoIndConcerne")) ||
							(ui.noIndividu().intValue() == recIntervention.intForKey("intNoIndAppelant")) || userInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER,
									recIntervention.stringForKey("cStructure"))) && !DTEtatBus.isEtatFinal(recIntervention.stringForKey("intEtat")));
	}

	/**
	 * L'utilisateur a le droit de modifier le motif d'une demande
	 * 
	 * @param eoIntervention
	 * @param noIndividu
	 * @return
	 */
	public boolean canChangeMotif(EOIntervention eoIntervention, Number noIndividu) {
		boolean canChangeMotif = false;

		if (eoIntervention != null) {
			DTUserInfo ui = null;
			// Si c'est le meme individu, que celui connecte a l'application, on
			// reutilise les informations deja connues
			if ((noIndividu == null) ||
					(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
				ui = userInfo();
			} else {
				// Sinon, on recupere les information sur l'individu
				String login = individuBus().loginForNoIndividu(noIndividu);
				if (login != null)
					ui = preferencesBus().getUserInfoForLogin(login);
			}

			boolean isEtatFinal = DTEtatBus.isEtatFinal(eoIntervention.intEtat());
			// etat non final
			if (isEtatFinal == false) {
				// soit il est demandeur
				if (ui.noIndividu().intValue() == eoIntervention.intNoIndAppelant().intValue() ||
						ui.noIndividu().intValue() == eoIntervention.intNoIndConcerne().intValue()) {
					canChangeMotif = true;
				}
				if (!canChangeMotif) {
					// soit il est intervenant avec pouvoir
					if (userInfo().hasDroit(
							DTUserInfo.DROIT_INTERVENANT_SUPER, eoIntervention.cStructure())) {
						canChangeMotif = true;
					}
				}
			} else {
				// etat final : il est intervenant avec pouvoir
				if (userInfo().hasDroit(
						DTUserInfo.DROIT_INTERVENANT_SUPER, eoIntervention.cStructure())) {
					canChangeMotif = true;
				}
			}
		}

		return canChangeMotif;
	}

	/**
	 * L'utilisateur a le droit de modifier le demandeur d'une demande
	 * 
	 * @param recIntervention
	 * @param noIndividu
	 * @return
	 */
	public boolean canChangeDemandeur(EOIntervention eoIntervention, Number noIndividu) {
		boolean canChangeDemandeur = false;

		if (eoIntervention != null) {
			DTUserInfo ui = null;
			// Si c'est le meme individu, que celui connecte a l'application, on
			// reutilise les informations deja connues
			if ((noIndividu == null) ||
					(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
				ui = userInfo();
			} else {
				// Sinon, on recupere les information sur l'individu
				String login = individuBus().loginForNoIndividu(noIndividu);
				if (login != null)
					ui = preferencesBus().getUserInfoForLogin(login);
			}
		}

		// seul l'intervenant avec pouvoir peut le faire

		if (userInfo().hasDroit(
				DTUserInfo.DROIT_INTERVENANT_SUPER, eoIntervention.cStructure())) {
			canChangeDemandeur = true;
		}

		return canChangeDemandeur;
	}

	/**
	 * Teste si l'utilisateur <code>noIndividu</code> a le droit de voir sur les
	 * activités commençant par '_', activités normalement internes au service? Si
	 * <code>noIndividu</code> est <i>null</i>, alors on considere qu'il s'agit de
	 * l'individu actuellement connecte a l'application.
	 */
	public boolean canViewActiviteUnderscore(String cStructure, Number noIndividu) {
		if (cStructure == null)
			return false;
		DTUserInfo ui = null;
		// Si c'est le meme individu, que celui connecte a l'application, on
		// reutilise les informations deja connues
		if ((noIndividu == null) ||
				(noIndividu.intValue() == userInfo().noIndividu().intValue())) {
			ui = userInfo();
		} else {
			// Sinon, on recupere les information sur l'individu
			String login = individuBus().loginForNoIndividu(noIndividu);
			if (login != null)
				ui = preferencesBus().getUserInfoForLogin(login);
		}
		// s'il a le droit au moins limité, c'est OK
		return ui.hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure);
	}

	// ------- Gestion des documents attaches -------//

	/**
	 * Ajoute un document relatif a une DT et/ou un traitement.
	 * 
	 * @param disOrdre
	 *          TODO
	 */
	public boolean addDocumentDt(Integer transId, Number docOrdre,
																Number intOrdre, Number traOrdre,
																Number disOrdre) {
		try {
			if ((docOrdre == null) || (intOrdre == null))
				return false;
			Integer localTransId = getTransaction(transId);
			EOEditingContext ec = econtextForTransaction(localTransId, true);
			EOIntervention recInt = (EOIntervention) fetchIntervention(ec,
					newCondition(EOIntervention.INT_ORDRE_KEY + "=" + intOrdre));
			EOTraitement recTra = null;
			if (traOrdre != null) {
				recTra = (EOTraitement) fetchTraitement(ec, traOrdre);
			}
			EODocumentDt newDtDocument = (EODocumentDt) newObjectInEntity(EODocumentDt.ENTITY_NAME, ec);
			if (newDtDocument == null)
				return false;
			newDtDocument.setDocOrdre(new Integer(docOrdre.intValue()));
			newDtDocument.setIntOrdre(new Integer(intOrdre.intValue()));
			if (traOrdre != null) {
				newDtDocument.setTraOrdre(new Integer(traOrdre.intValue()));
			}
			newDtDocument.setToInterventionRelationship(recInt);
			recInt.addToTosDocumentDtRelationship(newDtDocument);
			if (recTra != null) {
				recTra.addToTosDocumentDtRelationship(newDtDocument);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Retourne la liste des enregistrement <code>CktlRecord</code> de la table
	 * <code>DocumentDt<//code> attaches a une DT et/ou un traitement
	 * 
	 * @deprecated
	 * @see EOIntervention#hasFichierAttache()
	 * @see EOIntervention#hasFichierAttacheOuDansTraitement()
	 */
	public NSArray findDocuments(Number intOrdre, Number traOrdre,
																boolean interventionOnly) {
		if ((intOrdre == null) && (traOrdre == null)) {
			return new NSArray();
		} else {
			StringBuffer condition = new StringBuffer();
			if (intOrdre != null)
				condition.append("intOrdre=").append(intOrdre);
			if (traOrdre != null) {
				if (condition.length() > 0)
					condition.append(" and ");
				condition.append("traOrdre=").append(traOrdre);
			} else {
				if (interventionOnly) {
					if (condition.length() > 0)
						condition.append(" and ");
					condition.append("traOrdre=nil");
				}
			}
			return fetchArray("DocumentDt", newCondition(condition.toString()), null);
		}
	}

	/**
	 * Retourne la definition de tri par les cles <code>columns</code> pour une
	 * liste des interventions. Plusieurs noms des colonnes peuvent etre separes
	 * par ",". L'ordre de tri depend des preferences utilsateur.
	 */
	public NSArray getInterventionsSort(String columns) {
		return CktlSort.newSort(
					columns,
					userInfo().isSortIntAscending() ? CktlSort.Ascending : CktlSort.Descending);
	}

	/**
	 * Retourne la definition de tri par les cles <code>columns</code> pour une
	 * liste des traitements. Plusieurs noms des colonnes peuvent etre separes par
	 * ",". L'ordre de tri depend des preferences utilisateur.
	 */
	public CktlSort getTraitementsSort(String columns) {
		return CktlSort.newSort(
					columns,
					userInfo().isSortTraAscending() ? CktlSort.Ascending : CktlSort.Descending);
	}

	// ------- Des operations complexes -------//

	private int countReaffectation;

	/**
	 * Getter sur le nombre de reaffectation effectuees via la methode
	 * <code>performAffectation</code>. Permet de suivre l'evolution de cette
	 * tache qui peut etre longue (envoi de mail).
	 */
	public int countReaffectation() {
		return countReaffectation;
	}

	/**
	 * @param ui
	 *          TODO
	 * 
	 */
	public boolean performAffectation(DTUserInfo ui, NSArray allInterventions,
																		NSArray allNoIndividus, Number noIndAffectant, String commentaire, Number priorite) {
		CktlRecord rec;
		Integer transactionId;
		boolean noErrors = true;
		boolean noCurrentError;
		String newEtat;

		// RAZ total
		countReaffectation = 0;

		// On fait dans try-catch, car parfois il y a des exceptions non repertories
		try {
			// Affectation et mail pour chaque DT
			for (int i = 0; (i < allInterventions.count()); i++) {
				rec = (CktlRecord) allInterventions.objectAtIndex(i);
				// Affectation a tous les intervenants
				transactionId = beginECTransaction();
				noCurrentError = true;
				for (int j = 0; ((j < allNoIndividus.count()) && noCurrentError); j++) {
					noCurrentError = addIntervenant(transactionId,
														rec.numberForKey("intOrdre"),
														(Number) allNoIndividus.objectAtIndex(j),
														noIndAffectant);
				}
				if (noCurrentError) {
					if (DTEtatBus.isEtatInitial(rec.stringForKey("intEtat")))
						newEtat = EOEtatDt.ETAT_EN_COURS;
					else
						newEtat = null;
					noCurrentError = updateIntervention(ui, transactionId,
							rec.numberForKey("intOrdre"), null, commentaire, priorite, newEtat, null, null, null, null, null);
				}
				if (noCurrentError)
					noCurrentError = commitECTransaction(transactionId, false);
				else
					rollbackECTrancsacition(transactionId, false);
				// Envoyer le message mail si tout est OK
				if (noCurrentError) {
					mailCenter().reset();
					mailCenter().setIntervention(rec, true);
					mailCenter().mailAffecterDT(allNoIndividus);
				}
				noErrors = noErrors && noCurrentError;
				// Terminer la transaction
				terminateECTransaction(transactionId);
				// incrementer le total d'operations
				countReaffectation++;
			}
		} catch (Throwable ex) {
			throwError(ex);
			noErrors = false;
		}
		return noErrors;
	}

	/**
	 * Effectue la transmission de la demande au service.
	 */
	public String performTransmettreService(Number intOrdre, String cStructure) {
		// TODO Ajouter l'implementation de la methode et supprimer la procedure
		// stockee.
		return null;
	}

	/**
	 * Effectue la validation de la demande avec le code <code>intOrdre</code> et
	 * envoye un message email aux personnes concernees. Le parametre
	 * <code>commentaire</code> indique le commentaire interne de la demande. Dans
	 * le cas de rejet, <code>commenataire</code> contient le motif du rejet.
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 * @param ui
	 *          TODO
	 */
	public boolean performValidation(DTUserInfo ui, Number intOrdre, String commentaire, String etat) {
		Integer transId = beginECTransaction();
		CktlRecord rec = findIntervention(transId, intOrdre);
		boolean noErrors = interventionBus().updateIntervention(
				ui, null, intOrdre, null, StringCtrl.normalize(commentaire), null, etat, null, null, null, null, null);
		if (noErrors) {
			mailCenter().reset();
			mailCenter().setIntervention(rec, true);
			if (etat.equals(EOEtatDt.ETAT_REJETEES)) // On rejet la demande
				mailCenter().mailRejeterDT();
			else
				// Sinon, on la valide
				mailCenter().mailValiderDT();
		}
		rollbackECTrancsacition(transId);
		return noErrors;
	}

	/**
	 * Effectuer la verification que TOUTES les infos budgetaires sont saisies
	 * dans la DT. - ligne budgetaire - destination
	 * 
	 * @param intOrdre
	 *          Le numero de la demande
	 */
	public boolean isInfosBudgetairesCompletes(Number intOrdre) {
		Integer transId = beginECTransaction();
		EOInterventionInfin rec = findInterventionInfin(transId, intOrdre);

		// pour 2006
		// return rec == null || (rec.valueForKey("orgOrdre") != null &&
		// rec.valueForKey("dstCode") != null);
		// pour > 2007
		// return rec == null || (rec.orgId() != null && rec.lolfId() != null &&
		// rec.tcdCode() != null);
		//
		if (rec == null) {
			return true;
		}

		//
		if (rec.orgId() != null &&
				rec.lolfId() != null &&
				rec.lolfId().intValue() != -1 &&
				rec.tcdCode() != null) {
			return true;
		}

		return false;
	}

	/**
	 * Methode permettant de savoir si l'intervenant peut effacer la demande
	 * passee en parametre
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canSupprimer(CktlRecord recIntervention, Number noIndividu, int userMode) {
		if (recIntervention == null)
			return false;
		String etat = recIntervention.stringForKey("intEtat");
		String cStructure = recIntervention.stringForKey("cStructure");
		if (etat.equals(EOEtatDt.ETAT_TRAITEES) ||
				etat.equals(EOEtatDt.ETAT_REJETEES) ||
				(!dtSession().dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER, cStructure)) ||
				(userMode == RechercheDTConfig.USER_DEMANDEUR))
			return false;
		else
			return dtSession().dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure);
	}

	/**
	 * Methode permettant de savoir si l'intervenants peut affecter la demande
	 * passee en parametre
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canAffecter(String intEtat, Number actOrdre, String cStructure, Number noIndividu) {
		if (intEtat == null || actOrdre == null || cStructure == null) {
			return false;
		}
		// on ne peut pas affecter une DT non validee, traitee ou rejetee
		if (intEtat.equals(EOEtatDt.ETAT_NON_VALIDEES) ||
				intEtat.equals(EOEtatDt.ETAT_TRAITEES) ||
				intEtat.equals(EOEtatDt.ETAT_REJETEES)) {
			return false;
		}
		/*
		 * if (!(etat.equals(DTEtatBus.ETAT_NON_AFFECTEES) ||
		 * etat.equals(DTEtatBus.ETAT_ATTENTE_AFFECTER) ||
		 * etat.equals(DTEtatBus.ETAT_EN_COURS) ||
		 * etat.equals(DTEtatBus.ETAT_ATTENTE))) { return false; }
		 */
		// En plus, il faut etre au moins intervenant avec les droits de validation
		return hasDroitsForAction(actOrdre, cStructure, noIndividu,
				EOActivites.TYPE_RESP_TECHNIQUE, DTUserInfo.DROIT_INTERVENANT);
	}

	/**
	 * La personne a-t-elle le droit de proceder a la validation de la demande
	 * passee en parametre
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canValider(CktlRecord recIntervention, Number noIndividu) {
		if (recIntervention == null)
			return false;
		// on ne peux valider que les DT dont l'etat est ETAT_NON_VALIDEES
		if (!recIntervention.stringForKey("intEtat").equals(EOEtatDt.ETAT_NON_VALIDEES))
			return false;
		// En plus, il faut etre fonctionnel ou admin
		return hasDroitsForAction(recIntervention.numberForKey("actOrdre"),
				recIntervention.stringForKey("cStructure"), noIndividu,
				// DTActiviteBus.TYPE_RESP_TECHNIQUE, DTUserInfo.DROIT_ADMIN);
				EOActivites.TYPE_RESP_FONCTIONNEL, DTUserInfo.DROIT_ADMIN);
	}

	/**
	 * Verifier acc�s a l'ajout d'un traitement pour la DT par la personne
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canAjouterTraitement(EOIntervention recIntervention, Number noIndividu) {
		boolean ok = canTraiter(recIntervention, noIndividu);
		// l'acces au traitement general est-il possible
		if (ok) {
			// verifier que la DT est traitable
			ok = DTEtatBus.isEtatTraitement(recIntervention.stringForKey("intEtat"));
			// verifier qu'il est intervenant
			if (ok)
				ok = recIntervention.isIntervenant(dtSession().dtUserInfo().noIndividu());
		}
		return ok;
	}

	/**
	 * Verifier acc�s a la modification d'un traitement pour la DT par la personne
	 * 
	 * @param recTraitement
	 *          : le traitement
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 * @param cStructure
	 *          : le service destinataire de la DT
	 */
	public boolean canModifierTraitement(CktlRecord recTraitement, Number noIndividu, String cStructure) {
		// on autorise la suppression et modification de traitement meme si la DT
		// est close
		boolean isReadOnly = false;
		// DTEtatBus.isEtatFinal(recTraitement.stringForKeyPath("toIntervention.intEtat"));
		if (!isReadOnly)
			isReadOnly = (!dtSession().dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure));
		if (!isReadOnly)
			isReadOnly = (recTraitement.intForKey("noIndividu") != noIndividu.intValue());
		return !isReadOnly;
	}

	/**
	 * Verifier que la personne a acc�s aux traitement de la DT
	 * 
	 * @param recIntervention
	 *          : la DT
	 * @param noIndividu
	 *          : le numero de l'intervenant
	 */
	public boolean canTraiter(CktlRecord recIntervention, Number noIndividu) {
		if (recIntervention == null)
			return false;
		else
			return !DTEtatBus.isEtatInitial(recIntervention.stringForKey("intEtat"));
	}

	/**
	 * Test si la personne en cours a le droit de faire l'action sur une DT
	 * indirectement identifie par son activite et la structure associee. Oui si :
	 * - si elle est responsable niveau <code>typeResponsable</code> sur
	 * l'activite - si elle a le niveau <code>niveauDroits</code> sur le service
	 */
	private boolean hasDroitsForAction(Number actOrdre, String cStructure, Number noIndividu,
			String typeResponsable, int niveauDroits) {
		boolean ok = false;
		// Teste si la personne est le responsable niveau [typeResponsable] de
		// l'activite
		ok = activiteBus().isActiviteResponsable(actOrdre, typeResponsable, noIndividu);
		// Sinon, a-t-il au moins le niveau <code>niveauDroits</code> sur le service
		if (!ok && niveauDroits != -1) {
			ok = dtSession().dtUserInfo().hasDroit(niveauDroits, cStructure);
		}
		return ok;
	}

	/**
	 * Teste si l'utlisateur a le droit de consulter le commentaire du traitement.
	 */
	public boolean canReadTraitement(CktlRecord recTraitement, Number noIndividu, String cStructure) {
		// Si le traitement est publique, on l'affiche a tout le monde
		if (recTraitement.stringForKey("traConsultable").equals("O"))
			return true;
		// Sinon, il n'est visible qu'aux intervenants et au demandeur
		// -- c'est le demandeur ?
		if (recTraitement.intForKeyPath("toIntervention.intNoIndConcerne") == userInfo().noIndividu().intValue())
			return true;
		// -- c'est quelqu'un d'affecte, qui a toujours les droits de niveau 1 sur
		// le service ?
		if (userInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure)) {
			NSArray nosIndividuAffectes = recTraitement.arrayForKeyPath("toIntervention.tosIntervenant.noIndividu");
			for (int i = 0; i < nosIndividuAffectes.count(); i++) {
				Number curNoIndividu = (Number) nosIndividuAffectes.objectAtIndex(i);
				if (curNoIndividu.intValue() == noIndividu.intValue()) {
					return true;
				}
			}
		}
		return false;
	}

	// methodes de verification de droits lors de la creation de la DT

	/**
	 * Teste si l'utilisateur peut affecter une demande selon l'etat, l'activite
	 * et le service juste apres la creation (par lui meme)
	 */
	public boolean canDoPreAffectation(String newDtEtat, Number actOrdre, String cStructure, Number noIndividu) {
		// On pre-affecte uniquement les DT non affectees
		if (!EOEtatDt.ETAT_NON_AFFECTEES.equals(newDtEtat))
			return false;
		// on controle alors le droit d'affectation
		return canAffecter(newDtEtat, actOrdre, cStructure, noIndividu);
	}

	/**
	 * Teste si l'utilisateur peut valider une demande selon l'activite associee
	 * juste apres la creation (par lui meme).
	 */
	public boolean canDoPreValidation(Number actOrdre) {
		// Tester, si l'utilisateur peut valider la demande.
		// Il doit etre responsable fonctionnel ou avoir les droits
		// d'intervenant (si c'est lui meme qui cree la demande).
		// if (dtDataCenter().activiteBus().hasDroitsForActivite(
		// browserRecord().numberForKey("actOrdre"),
		// connectedUserInfo().noIndividu(),
		// DTActiviteBus.TYPE_RESP_FONCTIONNEL, DTUserInfo.DROIT_INTERVENANT))
		// {
		// }
		if (activiteBus().hasDroitsForActivite(actOrdre, userInfo().noIndividu(),
					EOActivites.TYPE_RESP_FONCTIONNEL, DTUserInfo.DROIT_INTERVENANT))
			return true;
		else
			return false;
	}

	/**
	 * Construire un dictionnaireutilise pour la creation d'une nouvelle demande
	 * de travaux. Ce dictionnaire est utilise par la suite par la methode
	 * {@link #addIntervention(NSDictionary)}.
	 * 
	 * Les valeurs initialisee par defaut sont : - intNoIndAppelant : personne
	 * connectee - mailIndAppelant : mail de la personne connectee -
	 * intCommentaireInterne : chaine vide - intDateButoir : aucune -
	 * intDateCreation : maintenant - intPriorite : 9 (la plus faible)
	 * 
	 * @return
	 */
	public NSMutableDictionary newDefaultDataDictionnaryNewIntervention(
			Number actOrdre, String cStructureDst, Number intNoIndConcerne,
			String mailIndConcerne, String intEtat, String motsClefs,
			String cStructureDemandeur, String cLocalDemandeur, Number ctOrdreDemandeur,
			String modCode) {
		NSMutableDictionary leDico = new NSMutableDictionary();
		leDico.setObjectForKey(actOrdre, "actOrdre");
		leDico.setObjectForKey(cStructureDst, "cStructure");
		leDico.setObjectForKey(intNoIndConcerne, "intNoIndConcerne");
		leDico.setObjectForKey(dtSession().connectedUserInfo().noIndividu(), "intNoIndAppelant");
		if (mailIndConcerne != null) {
			leDico.setObjectForKey(mailIndConcerne, "mailIndConcerne"); // Utilise
																																	// uniquement
																																	// pour le
																																	// mail
		}
		leDico.setObjectForKey(dtSession().connectedUserInfo().email(), "mailIndAppelant"); // idem
		leDico.setObjectForKey(StringCtrl.emptyString(), "intCommentaireInterne");
		leDico.setObjectForKey(CktlDataBus.nullValue(), "intDateButoir");
		leDico.setObjectForKey(DateCtrl.now(), "intDateCreation");
		leDico.setObjectForKey(intEtat, "intEtat");
		leDico.setObjectForKey(motsClefs, "intMotsClefs");
		leDico.setObjectForKey(new Integer(9), "intPriorite");

		leDico.setObjectForKey(cStructureDemandeur == null ? "-1" : cStructureDemandeur, "intServiceConcerne");
		leDico.setObjectForKey(cLocalDemandeur == null ? DTDataBus.nullValue() : cLocalDemandeur, "cLocal");
		// if (recContactDemandeur() != null && !isOnlyFactService)
		// leDico.setObjectForKey(recContactDemandeur().valueForKey("ctOrdre"),
		// "ctOrdre");
		// else
		// leDico.setObjectForKey(DTDataBus.nullValue(), "ctOrdre");
		leDico.setObjectForKey(ctOrdreDemandeur == null ? DTDataBus.nullValue() : ctOrdreDemandeur, "ctOrdre");

		leDico.setObjectForKey(modCode, "modCode");

		return leDico;
	}

	private NSArray modeAppelList;

	/**
	 * La liste de tous les modes d'appel connus pour creer une demande de
	 * travaux. Retourne des object <code>CktlRecord</code> de l'entite
	 * <code>ModeAppel</code>.
	 * 
	 * @return
	 */
	public NSArray allModeAppel() {
		if ((modeAppelList == null) || (modeAppelList.count() == 0)) {
			modeAppelList = fetchArray("ModeAppel", null, null, null);
		}
		return modeAppelList;
	}

	//

	/**
	 * Effectue l'extraction de donnees de la chaine de caracteres encodes. Les
	 * valeurs extraites sont : - intOrdre - traOrdre (facultatif) - disOrdre
	 * (facultatif)
	 * 
	 * @param crypted
	 *          la chaine codee
	 * 
	 * @return un {@link NSDictionary} avec comme cle les chaines "intOrdre",
	 *         "traOrdre" et "disOrdre"
	 */
	public NSDictionary<String, Integer> extractDiscussionKeys(String crypted) {
		String uncrypted = CRIpto.decrypt(crypted);
		// extraire les valeurs
		NSMutableDictionary<String, Integer> dico = new NSMutableDictionary<String, Integer>();
		NSArray<String> couples = NSArray.componentsSeparatedByString(uncrypted, "|");
		for (int i = 0; i < couples.count(); i++) {
			NSArray<String> objects = NSArray.componentsSeparatedByString(couples.objectAtIndex(i), "=");
			String key = objects.objectAtIndex(0);
			Integer value = new Integer(Integer.parseInt(objects.objectAtIndex(1)));
			dico.setObjectForKey(value, key);
		}
		return dico.immutableClone();
	}

	/**
	 * Creer la chaine cryptee comprenant le triplet intOrdre, traOrdre, disOrdre
	 * 
	 * @param intOrdre
	 * @param traOrdre
	 * @param disOrdre
	 * 
	 * @return la chaine cryptee. <em>null</em> si une erreur est survenue
	 */
	public String encodeDiscussionKeys(Number intOrdre, Number traOrdre, Number disOrdre) {
		StringBuffer uncrypted = new StringBuffer();
		if (intOrdre != null) {
			uncrypted.append("intOrdre=").append(intOrdre);
			if (traOrdre != null) {
				uncrypted.append("|traOrdre=").append(traOrdre);
			}
			if (traOrdre != null) {
				uncrypted.append("|disOrdre=").append(disOrdre);
			}
			return CRIpto.crypt(uncrypted.toString());
		}
		return null;
	}

	/*
	 * private NSArray traitementTypeList;
	 *//**
	 * La liste de tous les types de traitements. Retourne des object
	 * <code>CktlRecord</code> de l'entite <code>TraitementType</code>.
	 * 
	 * @return
	 */
	/*
	 * public NSArray allTraitementType() { if ((traitementTypeList == null) ||
	 * (traitementTypeList.count() == 0)) { traitementTypeList =
	 * fetchArray(EOTraitementType.ENTITY_NAME, null, null, null); } return
	 * traitementTypeList; }
	 */
	// affichage

	/**
	 * traitement + contenu additionnel si besoin
	 */
	public String traitementContentDisplay(CktlRecord recTraitement) {
		String traitement = "";

		traitement = recTraitement.stringForKey(EOTraitement.TRA_TRAITEMENT_KEY);
		// ajout du traitement additionnel s'il existe
		String traTraitementAdditionnel = recTraitement.stringForKey(EOTraitement.TRA_TRAITEMENT_ADDITIONNEL_KEY);
		if (!StringCtrl.isEmpty(traTraitementAdditionnel)) {
			traitement += "\n" + traTraitementAdditionnel;
		}

		// on prefixe par le type s'il s'agit d'une commande, d'une livraison ou
		// d'un numero de serie
		EOTraitementType recTraitementType = (EOTraitementType) recTraitement.recForKey(EOTraitement.TO_TRAITEMENT_TYPE_KEY);
		if (recTraitementType.isTraitementCommande() || recTraitementType.isTraitementLivraison() || recTraitementType.isTraitementNoSerie()) {
			traitement = "[" + recTraitementType.ttyLibelle() + "] \n" + traitement;
		}

		return traitement;
	}

	/**
	 * date(s) de traitement selon le type
	 */
	public String traitementDateDisplay(CktlRecord recTraitement) {
		String dates = "";

		dates = DateCtrl.dateToString(recTraitement.dateForKey(EOTraitement.TRA_DATE_DEB_KEY), "%d/%m/%Y %H:%M");
		// ajout de la date de fin pour le type textuel ou prestation
		EOTraitementType recTraitementType = (EOTraitementType) recTraitement.recForKey(EOTraitement.TO_TRAITEMENT_TYPE_KEY);
		if (recTraitementType.isTraitementTextuel() || recTraitementType.isTraitementPrestation()) {
			dates += "-" + DateCtrl.dateToString(recTraitement.dateForKey(EOTraitement.TRA_DATE_FIN_KEY), "%H:%M");
		}

		return dates;
	}

}
