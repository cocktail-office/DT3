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
import org.cocktail.dt.server.metier.EOActivitesResponsables;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOIntervenant;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.eof.ERXQ;

/**
 * Gestionnaire des informations sur les services et des activites dans la base
 * de donnees de l'application Demande de Travaux.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTActiviteBus extends DTDataBus {
	private static final NSArray<Object> EmtyArray = new NSArray<Object>();
	private ActiviteBusListener busListener;

	/**
   * 
   */
	public DTActiviteBus(EOEditingContext eocontext) {
		super(eocontext);
	}

	/**
	 * -- Definit l'auditeur des messages du bus. Si <code>listener</code> est
	 * <i>null</i>, tous les messages seront ignores.
	 */
	public void setActiviteBusListener(ActiviteBusListener listener) {
		busListener = listener;
	}

	/**
	 * Informe l'auditeur des messages du bus d'un nouveau message. Si
	 * <code>progress</code> est <i>true</i>, fait avancer la bar de progression
	 * d'auditeur.
	 */
	private void busListenerMessage(String message, boolean progress) {
		if (busListener != null) {
			if (progress)
				busListener.advanceMessage(message);
			else
				busListener.showMessage(message);
		}
	}

	private void updateValueForColumn(EOEnterpriseObject rec,
																		Object value, String column,
																		boolean forceUpdate) {
		if (forceUpdate) {
			rec.takeStoredValueForKey(valueIfNull(value), column);
		} else {
			if (value != null)
				rec.takeStoredValueForKey(value, column);
		}
	}

	public void printActivitesHierarchie(String serviceCode) {
		CktlLog.rawLog("<html><body><font size='-1' face='Courier New'>");
		printActivitesHierarchie(newCondition("cStructure='" + serviceCode + "' and actPere=0 and actOrdre<>0"), StringCtrl.emptyString());
		CktlLog.rawLog("</font><body><html>");
	}

	private void printActivitesHierarchie(EOQualifier condition, String printPrefix) {
		CktlRecord rec;
		NSArray objects = fetchArray("Activites", condition, CktlSort.newSort("actLibelle"));
		for (int i = 0; i < objects.count(); i++) {
			rec = (CktlRecord) objects.objectAtIndex(i);
			CktlLog.rawLog(printPrefix + " &gt; <b>" + rec.stringForKey("actLibelle") + "</b><br>");
			printActivitesHierarchie(newCondition("actPere=" + rec.valueForKey("actOrdre")),
					printPrefix + " &gt; " + rec.stringForKey("actLibelle"));
		}
	}

	// **************************************************************
	// ----- La gestion des services (GROUPE_DT) ----

	/**
	 * Trouve les definitions des services ayant la position dans la liste
	 * indiquee. <code>ignoreGroupCode</code> indique celui qui doit etre ignore
	 * pedant la recherche.
	 */
	public NSArray findGroupesWithPosition(int position, String ignoreGroupCode) {
		StringBuffer sb = new StringBuffer("grpPosition=").append(position);
		sb.append(" and cStructure != '").append(ignoreGroupCode).append("'");
		return fetchArray("GroupesDt", newCondition(sb.toString()), null);
	}

	/**
	 * Retourne la premiere position libre dans la liste des services.
	 */
	public int findFreeGrpPosition() {
		NSArray groupes = fetchArray("GroupesDt", null, CktlSort.newSort("grpPosition"));
		if (groupes.count() > 0)
			return ((CktlRecord) groupes.lastObject()).intForKey("grpPosition") + 1;
		else
			return 1;
	}

	/**
	 * Trouve la definition du service ayant le code <code>cStructure</code>.
	 */
	public CktlRecord findGroupeWithCode(String cStructure) {
		return (CktlRecord) fetchObject("GroupesDt", "cStructure = '" + cStructure + "'", null);
	}

	/**
	 * Retourne les definitions des services affichables dans la liste.
	 */
	public NSArray allValideGroupes() {
		return fetchArray("GroupesDt", newCondition("grpAffichable='O'"), null);
	}

	/**
	 * Retrouve et retourne la definition du service ayant le nom court
	 * <code>title</code>.
	 */
	public CktlRecord groupeWithShortTitle(String title) {
		return (CktlRecord) fetchObject("GroupesDt", "lcStructure = '" + title + "'", null);
	}

	/**
	 * Ajoute la definition d'un nouveau service.
	 * 
	 * @param transId
	 *          Code d'une transaction ou <i>null</i> si aucune.
	 * @param cStructure
	 *          Code du service (Structure).
	 * @param grpAffichable
	 *          Faut-il afficher le service dans la liste ('O', 'N')?
	 * @param grpEmail
	 *          L'adresse email associe au service (les messages de creation ou de
	 *          la mise a jour des demandes y seront envoyes).
	 * @param grpPosition
	 *          La position du service dans la liste des activites.
	 * @param grpNbLigBrowserAct
	 *          Le nombre de lignes visibles en meme temps dans l'ecran de
	 *          selection des activites pour ce service.
	 * @param grpActDefault
	 *          Le code de l'activite auquel les demandes sont associes lors de
	 *          leur transfert entre services (Consultation > Affectation).
	 * @param grpSwapView
	 *          Le code de la vue par defaut pour le service.
	 * @param grpVisibilite
	 *          Les codes des reseaux pour lesquels les demandes de ce service
	 *          sont visibles. La chaine doit etre composee des codes <i>Vlans</i>
	 *          (<i>cVlan</i>) separes par le symbole "|". Par exemple, "
	 *          <code>|R|E|WEB|</code>". Le code <code>WEB</code> indique la
	 *          visibilite du service dans l'application DT Web.
	 * @param grpVisibiliteStructure
	 *          Les codes des structures pour lesquels seuls les membres de ces
	 *          dernieres sont autorises voir le groupe DT. La chaine doit etre
	 *          composee des codes <i>StructureUlr</i> (<i>cStructure</i>) separes
	 *          par le symbole "|". Par exemple, "<code>|25|43|14281|</code>".
	 * @param grpEmailSam
	 *          Point d'entree de SAM pour ce service (laisser vide pour
	 *          desactiver SAM)
	 * 
	 * @return
	 */
	public CktlRecord addGroupe(Integer transId,
														String cStructure, String grpAffichable,
														String grpEmail, Number grpPosition, Number grpNbLigBrowserAct,
														Number grpSwapView, String grpVisibilite, String grpVisibiliteStructure,
														String grpEmailSam) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			CktlRecord rec = (CktlRecord) EOUtilities.createAndInsertInstance(ec, "GroupesDt");
			rec.takeValueForKey(cStructure, "cStructure");
			rec.takeValueForKey(grpAffichable, "grpAffichable");
			rec.takeValueForKey(DTDataBus.valueIfNull(grpEmail), "grpEmail");
			rec.takeValueForKey(grpPosition, "grpPosition");
			rec.takeValueForKey(grpNbLigBrowserAct, "grpNbLigBrowserAct");
			rec.takeValueForKey(new Integer(1), "grpSeq");
			rec.takeValueForKey("I", "gtCode"); // Le code "I" pour une intervention
			rec.takeValueForKey(valueIfNull(grpSwapView), "grpSwapView");
			rec.takeValueForKey(valueIfNull(grpVisibilite), "grpVisibilite");
			rec.takeValueForKey(valueIfNull(grpVisibiliteStructure), "grpVisibiliteStructure");
			rec.takeValueForKey("O", "grpMailService");
			if (!StringCtrl.isEmpty(grpEmailSam)) {
				rec.takeValueForKey(grpEmailSam, EOGroupesDt.GRP_EMAIL_SAM_KEY);
			}
			if (transId == null)
				commitECTransaction(localTransId);
			return rec;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Supprime la definition du service avec le code <code>cStructure</code>.
	 */
	public boolean deleteGroupe(Integer transId, String cStructure) {
		return (deleteFromTable(transId, "GroupesDt",
							newCondition("cStructure='" + cStructure + "'")) != -1);
	}

	/**
	 * Modifie la definition d'un service.
	 * 
	 * @param transId
	 *          Code d'une transaction ou <i>null</i> si aucune.
	 * @param cStructure
	 *          Code du service (Structure).
	 * @param grpAffichable
	 *          Faut-il afficher le service dans la liste ('O', 'N')?
	 * @param grpEmail
	 *          L'adresse email associe au service (les messages de creation ou de
	 *          la mise a jour des demandes y seront envoyes).
	 * @param grpPosition
	 *          La position du service dans la liste des activites.
	 * @param grpNbLigBrowserAct
	 *          Le nombre de lignes visibles en meme temps dans l'ecran de
	 *          selection des activites pour ce service.
	 * @param grpActDefault
	 *          Le code de l'activite auquel les demandes sont associes lors de
	 *          leur transfert entre services (Consultation > Affectation). La
	 *          valeur doit etre de type <code>Integer</code> ou
	 *          <code>EONullValue</code>.
	 * @param grpSwapView
	 *          Le code de la vue par defaut pour le service.
	 * @param grpVisibilite
	 *          Les codes des reseaux pour lesquels les demandes de ce service
	 *          sont visibles. La chaine doit etre composee des codes <i>Vlans</i>
	 *          (<i>cVlan</i>) separes par le symbole "|". Par exemple, "
	 *          <code>|R|E|WEB|</code>". Le code <code>WEB</code> indique la
	 *          visibilite du service dans l'application DT Web.
	 * @param grpMailService
	 *          Indique si le mail au service doit etre envoye (voir le parametre
	 *          <code>grpMail</code>).
	 * @param grpVisibiliteStructure
	 *          Les codes des structures pour lesquels seuls les membres de ces
	 *          dernieres sont autorises voir le groupe DT. La chaine doit etre
	 *          composee des codes <i>StructureUlr</i> (<i>cStructure</i>) separes
	 *          par le symbole "|". Par exemple, "<code>|25|43|14281|</code>".
	 * @param grpIsImpersonnel
	 *          Gestion du service impersonnelle ('O', 'N')?
	 * @param grpEmailSam
	 *          Point d'entree de SAM pour ce service (laisser vide pour
	 *          desactiver SAM)
	 * 
	 * @return <i>true</i> si l'operation reussi, ou <i>false</i> dans le cas
	 *         contraire.
	 */
	public boolean updateGroupe(Integer transId,
															String cStructure, String grpAffichable,
															String grpEmail, Integer grpPosition, Number grpNbLigBrowserAct,
															Number grpSwapView, String grpVisibilite, String grpVisibiliteStructure,
															Boolean grpMailService, String grpEmailSam) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			EOEnterpriseObject rec = fetchObject(ec,
					"GroupesDt",
					newCondition("cStructure = '" + cStructure + "'"));
			if (rec == null)
				return false;
			updateValueForColumn(rec, grpAffichable, "grpAffichable", false);
			updateValueForColumn(rec, grpEmail, "grpEmail", false);
			updateValueForColumn(rec, grpPosition, "grpPosition", false);
			updateValueForColumn(rec, grpNbLigBrowserAct, "grpNbLigBrowserAct", false);
			updateValueForColumn(rec, grpSwapView, "grpSwapView", false);
			updateValueForColumn(rec, grpVisibilite, "grpVisibilite", false);
			updateValueForColumn(rec, grpVisibiliteStructure, "grpVisibiliteStructure", false);
			updateValueForColumn(rec, grpMailService.booleanValue() ? "O" : "N", "grpMailService", false);
			// on accepte de mettre a jour une valeur vide de grpEmailSam
			updateValueForColumn(rec, valueIfNull(grpEmailSam), EOGroupesDt.GRP_EMAIL_SAM_KEY, false);
			if (transId == null)
				commitECTransaction(localTransId);
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	// **************************************************************
	// ----- La gestion des activites (ACTIVITES) ----

	/**
	 * Cree une nouvelle activite.
	 * 
	 * @param transId
	 *          L'identifiant d'une transaction ou <i>null</i>.
	 * @param actPere
	 *          Le code de l'activite pere.
	 * @param cStructure
	 *          Le code du service auquel l'activite est associe.
	 * @param actLibelle
	 *          Le libelle de l'activite.
	 * @param actCreerMail
	 *          L'adresse email pour enregistrer les demandes de cette activite ou
	 *          <i>null</i> si cette fonctionnalite n'est pas utilisee. Attention,
	 *          l'activite doit la derniere (feuille) de l'arborescence des
	 *          activites.
	 * @param cartOrdre
	 *          Le code de l'article d'une prestation. Si ce parametre est
	 *          indique, une prestation sera automatiquement suite a la creation
	 *          de la demande.
	 */
	public EOActivites addActivite(Integer transId, Number actPere,
															String cStructure, String actLibelle,
															String actCreerMail, Number cartOrdre) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			CktlRecord rec2 = (CktlRecord) fetchObject("SeqActivites", null);
			if (rec2 == null)
				return null;
			Number actPref;
			Number actOrdre = rec2.numberForKey("nextval");
			// On recupere l'activite "pere". On fait le fetch local, car
			// l'objet doit etre recupere dans le meme editing context.
			setRefreshFetchedObjects(false);
			EOActivites recPere = (EOActivites) fetchObject(
					ec, EOActivites.ENTITY_NAME, newCondition(EOActivites.ACT_ORDRE_KEY + "=" + actPere));
			if (recPere == null)
				return null;
			// On cree une instance de l'objet
			EOActivites rec = (EOActivites) EOUtilities.createAndInsertInstance(ec, EOActivites.ENTITY_NAME);
			rec.setActOrdre(new Integer(actOrdre.intValue()));
			rec.setActLibelle(actLibelle);
			rec.setCStructure(cStructure);
			rec.setActMailService("O");
			if (StringCtrl.normalize(actCreerMail).length() > 0) {
				rec.setActCreerMail(actCreerMail);
			}
			if (cartOrdre != null) {
				rec.setCartOrdre(new Integer(cartOrdre.intValue()));
			}
			rec.setActPere(new Integer(actPere.intValue()));
			// L'heritage des resposables et des preferences
			if (actPere.intValue() <= 0) {
				// Activite pere "racine".
				// On prend les definitions associees aux des services.
				actPref = StringCtrl.toInteger("-" + cStructure, -1);
			} else {
				// Sinon, on les recupere a partir du pere
				actPref = recPere.actPref();
			}
			rec.setActResp(new Integer(actPref.intValue()));
			rec.setActPref(new Integer(actPref.intValue()));
			rec.setActAffichable("O");
			if (transId == null) {
				commitECTransaction(localTransId);
			}
			return rec;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Mets a jour la definition d'une activite avec le code <code>actOrdre</code>
	 * . Les valeurs correspondant aux parametres avec la valeur <i>null</i> ne
	 * seront pas modifiees.
	 * 
	 * @param transId
	 *          L'identifiant d'une transaction ou <i>null</i>.
	 * @param actOrdre
	 *          Le code de l'activite a modifier.
	 * @param actLibelle
	 *          Le titre de libelle.
	 * @param actCreerMail
	 *          L'adresse email pour enregistrer les demandes pour cette activite.
	 *          L'activite doit appartenir a une feuille de la definition des
	 *          activites.
	 * @param actSwapView
	 *          Le code de la vue avec le formulaire de creation d'une demande.
	 *          Cette valeur sera enregistre dans le noeud de la definition des
	 *          preferences (<code>actPref</code>).
	 * @param actMailService
	 *          Indique si un message email doit etre envoye au service ou aux
	 *          responsables uniquement. Cette valeur sera enregistree dans le
	 *          noeud de la definition des responsables. Aucune modification n'est
	 *          faite si la valeur est <i>null</i>.
	 * @param cartOrdre
	 *          Le code de l'article des prestations. La valeur peut etre
	 *          <i>null</i>.
	 */
	public boolean updateActivite(Integer transId, Number actOrdre,
																String actLibelle, String actCreerMail,
																Boolean actMailService, Number cartOrdre) {
		CktlRecord rec;
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			rec = (CktlRecord) fetchObject(ec, "Activites", newCondition("actOrdre=" + actOrdre));
			if (rec != null) {
				if (actLibelle != null) {
					// La verification si le libelle est bien modifie sera faite
					// dans cette methode
					updateLibelle(ec, actLibelle, rec.stringForKey("actLibelle"),
												findActivitePathString(rec, true, null));
					rec.takeStoredValueForKey(actLibelle, "actLibelle");
				}
				if (actCreerMail != null)
					rec.takeStoredValueForKey(actCreerMail, "actCreerMail");
				if (actMailService != null) {
					rec.recForKey("toActResp").takeStoredValueForKey(
							actMailService.booleanValue() ? "O" : "N", "actMailService");
				}
				if (cartOrdre != null)
					rec.takeStoredValueForKey(cartOrdre, "cartOrdre");
				else
					rec.takeStoredValueForKey(CktlRecord.nullValue(), "cartOrdre");
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
	 * Mets a jour la definition des preferences d'une activite
	 * <code>actRecord</code> ayant le code <code>actOrdre</code>. Au moin un des
	 * ces deux parametres doit etre defini.
	 * 
	 * @param transId
	 *          L'identifiant d'une transaction ou <i>null</i>.
	 * @param actRecord
	 *          L'enregistrement avec une activite.
	 * @param actOrdre
	 *          Le code de l'activite a modifier.
	 * @param actSwapView
	 *          Le code de la vue avec le formulaire de creation d'une demande.
	 *          Cette valeur sera enregistre dans le noeud de la definition des
	 *          preferences (<code>actPref</code>).
	 */
	public boolean updateActivitePref(Integer transId,
										CktlRecord actRecord, Number actOrdre,
										Number actSwapView, String actSwapMessage,
										boolean defineLocal) {
		CktlRecord rec;
		Number actPref = null;
		EOEditingContext ec;
		CktlLog.trace("begin");
		try {
			if (actRecord != null) {
				rec = actRecord;
				ec = rec.editingContext();
			} else {
				EOQualifier condition = newCondition("actOrdre=" + actOrdre);
				ec = econtextForTransaction(transId, true);
				setRefreshFetchedObjects(false);
				rec = (CktlRecord) fetchObject(ec, "Activites", condition);
			}
			if (rec != null) {
				CktlLog.trace("actLibelle : " + rec.valueForKey("actLibelle") + ", actOrdre : " + rec.valueForKey("actOrdre"));
				if (defineLocal) {
					actPref = rec.numberForKey("actOrdre");
					rec.takeStoredValueForKey(rec.valueForKey("actOrdre"), "actPref");
					if (actSwapView != null)
						rec.takeStoredValueForKey(actSwapView, "actSwapView");
					else
						rec.takeStoredValueForKey(new Integer(0), "actSwapView");
					rec.takeStoredValueForKey(valueIfNull(actSwapMessage), "actSwapMessage");
				} else {
					if (rec.intForKey("actPere") <= 0) {
						actPref = StringCtrl.toInteger("-" + rec.stringForKey("cStructure"), -1);
					} else {
						setRefreshFetchedObjects(false);
						CktlRecord recPere = findActivite(rec.numberForKey("actPere"), null);
						actPref = recPere.numberForKey("actPref");
					}
					rec.takeStoredValueForKey(nullValue(), "actSwapView");
					rec.takeStoredValueForKey(nullValue(), "actSwapMessage");
				}
				CktlLog.trace("actPref : " + actPref);
				rec.takeStoredValueForKey(actPref, "actPref");
				rebuildActivitePref((EOActivites) rec, new Integer(actPref.intValue()), true);
			}
			if (transId == null)
				ec.saveChanges();
			CktlLog.trace("end - OK");
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			CktlLog.trace("end - Error");
			return false;
		}
	}

	/**
	 * Mets a jour la definition de la visibilite d'une activite
	 * <code>actRecord</code> ou ayant le code <code>actOrdre</code>. Au moin de
	 * ces deux parametres doit etre defini.
	 * 
	 * <p>
	 * Si l'activite n'est pas affichable, alors ces activites fils sont modifiees
	 * pour ne pas en etre visibles non plus. Si une activite est visible, alors
	 * tous ces peres doivent etre visibles aussi.
	 * </p>
	 * 
	 * @param transId
	 *          L'identifiant d'une transaction ou <i>null</i>.
	 * @param actRecord
	 *          L'enregistrement representant une activite.
	 * @param actOrdre
	 *          Le code de l'activite a modifier.
	 * @param actSwapView
	 *          Le code de la vue avec le formulaire de creation d'une demande.
	 *          Cette valeur sera enregistre dans le noeud de la definition des
	 *          preferences (<code>actPref</code>).
	 */
	public boolean updateActiviteAffichable(Integer transId,
										CktlRecord actRecord, Number actOrdre,
										boolean isAffichable) {
		CktlRecord rec;
		EOEditingContext ec;
		CktlLog.trace("begin");
		try {
			if (actRecord != null) {
				rec = actRecord;
				ec = rec.editingContext();
			} else {
				EOQualifier condition = newCondition("actOrdre=" + actOrdre);
				ec = econtextForTransaction(transId, true);
				setRefreshFetchedObjects(false);
				rec = (CktlRecord) fetchObject(ec, "Activites", condition);
			}
			doUpdateActAffichable(rec, isAffichable);
			if (transId == null)
				ec.saveChanges();
			CktlLog.trace("end - OK");
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			CktlLog.trace("end - Error");
			return false;
		}
	}

	/**
	 * Effectue la mise a jour de la visibilite des activites. Cette methode peut
	 * etre appele recoursivement pour tous les fils ou peres de l'activite
	 * donnee.
	 */
	private void doUpdateActAffichable(CktlRecord rec, boolean isAffichable) {
		if ((rec != null) && (rec.intForKey("actOrdre") > 0)) {
			CktlLog.trace("Affichable : " + isAffichable + "; activite : " + rec.valueForKey("actOrdre") + "/" + rec.valueForKey("actLibelle"));
			rec.takeStoredValueForKey((isAffichable ? "O" : "N"), "actAffichable");
			if (isAffichable) {
				// Si affichable, les peres doivent aussi etre affichables
				doUpdateActAffichable(rec.recForKeyPath("toActPere"), isAffichable);
			} else {
				// Si NON affichable, alors les fils doivent aussi ne plus etre visibles
				NSArray fils = rec.arrayForKeyPath("tosActFils");
				for (int i = 0; i < fils.count(); i++)
					doUpdateActAffichable((CktlRecord) fils.objectAtIndex(i), isAffichable);
			}
		}
	}

	// /**
	// * Deplace l'activite <code>actOrdre</code> dans l'arborescence des
	// activites.
	// * Elle sera placee comme fils de l'activite <code>newActPere</code>.
	// *
	// * @param actOrdre Le code de l'activite.
	// * @param newActPere Le code de nouveau pere de l'activite.
	// */
	// public boolean moveActivite(Number actOrdre, Number newActPere)
	// {
	// boolean prefChanged = false;
	// boolean respChanged = false;
	// busListenerMessage("Changement de l'activit� p�re...", true);
	// Integer localTransId = getTransaction(null);
	// EOEditingContext ec = econtextForTransaction(localTransId);
	// try {
	// CktlRecord recActivite = (CktlRecord)fetchObject(ec, "Activites",
	// newCondition("actOrdre="+actOrdre));
	// CktlRecord recNewPere = findVActivite(newActPere);
	// CktlRecord recOldPere = findVActivite(recActivite.numberForKey("actPere"));
	// String oldPath = findActivitePath(recActivite);
	// Number oldPere = recActivite.numberForKey("actPere");
	// Number oldPref = recActivite.numberForKey("actPref");
	// Number oldResp = recActivite.numberForKey("actResp");
	// recActivite.takeValueForKey(newActPere, "actPere");
	// busListenerMessage("Correction des d�finitions des pr�f�rences...", true);
	// // Correction des definitions des preferences
	// // Si n'est pas local, et pas celui du pere
	// if ((recActivite.intForKey("actPref") != recActivite.intForKey("actOrdre"))
	// // n'est pas local
	// && (recActivite.intForKey("actPref") != recNewPere.intForKey("actPref")))
	// {
	// // ... on insere la definition locale
	// updateActivitePref(localTransId, recActivite, null,
	// recOldPere.numberForKey("actSwapView"),
	// recOldPere.stringForKey("actSwapMessage"), true);
	// prefChanged = true;
	// // Sinon, on laisse inchange
	// }
	// busListenerMessage("Correction des d�finitions des responsables...", true);
	// // Correction des definitions des responsables
	// // Si n'est pas local, et pas celui du pere
	// if ((recActivite.intForKey("actResp") != recActivite.intForKey("actOrdre"))
	// // n'est pas local
	// && (recActivite.intForKey("actResp") != recNewPere.intForKey("actResp")))
	// {
	// // ... on definit les responsables localement
	// insertActiviteResponsables(localTransId,
	// recActivite.numberForKey("actOrdre"),
	// recActivite.numberForKey("actResp"));
	// respChanged = true;
	// // Sinon, on laisse inchange
	// }
	// busListenerMessage("Correction des mots cl�s...", true);
	// updatePath(ec,
	// findActivitePath(newActPere)+recActivite.valueForKey("actLibelle"),
	// oldPath);
	// busListenerMessage("Sauvegarde des donn�es...", true);
	// if (commitECTransaction(localTransId)) {
	// // On informe, s'il y avait des modifications "implicites"
	// if ((prefChanged || respChanged) && (busListener != null)) {
	// StringBuffer sb = new StringBuffer();
	// sb.append("Une d�finition des pr�f�rences est ajout�e localement pour l'activit� \"");
	// sb.append(recActivite.valueForKey("actLibelle")).append("\",\n");
	// sb.append("car les d�finitions h�rit�es de son ancienne et nouvelle activit� p�re son incompatibles.\n");
	// sb.append("Les modification concernent :\n");
	// if (prefChanged)
	// sb.append("  - la d�finition des pr�f�rences (la vue de cr�ation des demandes)\n");
	// if (respChanged)
	// sb.append("  - la d�finition des responsables.\n");
	// sb.append("\n\nVous pouvez modifier ces d�finitions via l'interface d'administration des activit�s.");
	// busListenerMessage(sb.toString(), false);
	// }
	// }
	// return true;
	// } catch(Throwable ex) {
	// throwError(ex);
	// return false;
	// }
	// }

	/**
	 * Supprime la definition de l'activite avec le code <code>actOrdre</code>.
	 * Cette methode supprime egalement la definition des responsabilites de
	 * l'activite. On ne peut supprimer que les activites qui ne sont pas
	 * utilisees : pas de fils et pas de demandes associes.
	 */
	public boolean deleteActivite(Integer transId, Number actOrdre) {
		// Pas de suppression si l'activite est utilise
		if (isActiviteInUse(actOrdre))
			return false;
		Integer localTransId = getTransaction(transId);
		// On ne peut supprimer que les fils, alors la suppression
		// simple des donnees est suffisante (pas de recalcul de la hierarchie)
		EOQualifier condition = newCondition("actOrdre = " + actOrdre);
		int result = deleteFromTable(localTransId, "ActivitesResponsables", condition);
		if (result >= 0)
			result = deleteFromTable(localTransId, "Activites", condition);
		if (transId == null) {
			if (result >= 0) {
				commitECTransaction(localTransId);
			} else {
				terminateECTransaction(localTransId);
			}
		}
		return (result >= 0);
	}

	/**
	 * Mets a jours les fils de l'activite <code>recActivite</code> pour reprendre
	 * les definitions des preferences donnees dans l'activite avec le code
	 * <code>actPref</code>.
	 */
	private void rebuildActivitePref(EOActivites recActivite, Integer actPref,
																		boolean startFromFils) {
		CktlLog.trace("rebuild for : " + recActivite.actLibelle());
		// Si la definition n'etait pas locale, alors on la met a jour
		if (recActivite.actPref().intValue() != recActivite.actOrdre().intValue() || startFromFils) {
			if (!startFromFils) {
				recActivite.setActPref(actPref);
				// Les deux autres seront prises chez le pere, alors on les supprime
				recActivite.setActSwapView(null);
				recActivite.setActSwapMessage(null);
			}
			NSArray<EOActivites> fils = recActivite.tosActFils();
			for (int i = 0; i < fils.count(); i++) {
				rebuildActivitePref(fils.objectAtIndex(i), actPref, false);
			}
		}
		// Si la definition est locale, alors on ne change rien
	}

	/**
	 * Teste si l'activite avec le code <code>actOrdre</code> est utilisee. Elle
	 * l'est si au moins une demande est deja enregistree ou si l'activite n'est
	 * pas une feuille de l'arborescence (elle a des fils).
	 */
	public boolean isActiviteInUse(Number actOrdre) {
		if (isVActiviteLeaf(actOrdre)) {
			return (fetchArray("Intervention", newCondition("actOrdre = " + actOrdre), null).count() > 0);
		} else {
			return true;
		}
	}

	/**
	 * Teste si l'activite avec le code <code>actOrdre</code> est une feuille de
	 * l'arborescence des activites.
	 */
	public boolean isVActiviteLeaf(Number actOrdre) {
		return (EOVActivites.fetchAll(editingContext(), newCondition("actPere = " + actOrdre), null).count() == 0);
	}

	/**
	 * Retourne la liste de tous les fils de l'activite dans le tableau
	 * <code>allFils</code>. Cette methode recupere egalement les fils des fils,
	 * etc...
	 */
	public void fillActivitesAllFils(Number actOrdre, NSMutableArray allFils) {
		NSArray fils = findActivite(actOrdre, null).arrayForKey("tosActFils");
		for (int i = 0; i < fils.count(); i++) {
			actOrdre = ((CktlRecord) fils.objectAtIndex(i)).numberForKey("actOrdre");
			allFils.addObject(actOrdre);
			fillActivitesAllFils(actOrdre, allFils);
		}
	}

	/**
	 * Retourne la liste des activites "fils" de l'activite avec le code
	 * <code>actPereCode</code>. Les enregistrements sont selectionnes dans la vue
	 * <i>VActivites</i>.
	 */
	public NSArray findVActivitesWithPere(Number actPereCode) {
		return EOVActivites.fetchAll(editingContext(), newCondition("actOrdre!=0 and actOrdre=" + actPereCode));
	}

	/**
	 * Retourne l'enregistrement de l'activite avec le code <code>actOrdre</code>.
	 * L'enregistrement est selectionne dans la vue <i>VActivites</i>.
	 */
	public EOVActivites findVActivite(Number actOrdre) {
		/*
		 * return EOVActivites.fetchByQualifier( editingContext(), ERXQ.equals(
		 * EOVActivites.ACT_ORDRE_KEY, actOrdre));
		 */
		return (EOVActivites) fetchObject(EOVActivites.ENTITY_NAME, EOVActivites.ACT_ORDRE_KEY + "=" + actOrdre, null);

	}

	/**
	 * Retourne l'enregistrement de l'activite ayant le nom <code>actOrdre</code>
	 * et attachee au service pointé par <code>cStructure</code>. La recherche
	 * effectue une recherche "caseInsensitiveLike". L'enregistrement est
	 * selectionne dans la vue <i>VActivites</i>.
	 * 
	 * @param showHiddenActivite
	 *          : faut-il permettre de trouver les activités non affichables (i.e.
	 *          celle dont tem_affichable = 'N')
	 * @param showUnderscoredActivites
	 *          : faut-il permettre de trouver les activités masquées (i.e. celle
	 *          dont le libellé commence par '_')
	 */
	public NSArray<EOVActivites> findVActivitesLike(
			String actLibelle,
			String cStructure,
			boolean showHiddenActivite,
			boolean showUnderscoredActivites) {
		// enlever les accents et caracteres speciaux
		String actLibelleClean = StringCtrl.chaineSansAccents(actLibelle);
		actLibelleClean = StringCtrl.toBasicString(actLibelleClean);
		// virer les '_' qui ne font pas aboutir les recherches dès qu'il y a des
		// accents
		actLibelleClean = StringCtrl.replace(actLibelleClean, "_", "*");
		String strQual =
				EOVActivites.ACT_LIBELLE_CLEAN_KEY + " caseInsensitiveLike '*" + actLibelleClean + "*' and " +
						EOVActivites.C_STRUCTURE_KEY + "='" + cStructure + "'";
		if (!showHiddenActivite) {
			strQual += " and " + EOVActivites.GRP_AFFICHABLE_KEY + " = 'O')";
		}
		if (!showUnderscoredActivites) {
			strQual += " and not(" + EOVActivites.ACT_LIBELLE_KEY + " isCaseInsensitiveLike '_*')";
		}
		// ne pas retourner les services
		strQual += " and " + EOVActivites.ACT_ORDRE_HIERARCHIE_KEY + "<> nil";
		return EOVActivites.fetchAll(editingContext(), newCondition(strQual));
	}

	/**
	 * Retourne le chemin de l'activite ayant le code <code>actOrdre</code>. Le
	 * chemin est constitue des libelles des activites "peres" separes par les
	 * ";".
	 * 
	 * @param includeLeaf
	 *          TODO
	 * @param separator
	 *          TODO
	 */
	public String findActivitePathString(
			Number actOrdre, boolean includeLeaf, String separator) {
		EOActivites rec = (EOActivites) fetchObject(
				EOActivites.ENTITY_NAME, EOActivites.ACT_ORDRE_KEY + "=" + actOrdre, null);
		if (rec == null)
			return StringCtrl.emptyString();
		return findActivitePathString(rec, includeLeaf, separator);
	}

	/**
	 * Retourne le chemin de l'activite representee dans l'enregistrement
	 * <code>actRec</code>. Le chemin est constitue des libelles des activites
	 * "peres" separes par les ";".
	 * 
	 * @param includeLeaf
	 *          TODO
	 * @param separator
	 *          TODO
	 */
	public String findActivitePathString(
			CktlRecord actRec, boolean includeLeaf, String separator) {
		StringBuffer path = new StringBuffer();
		Number clePere;
		EOVActivites rec;

		String sep = separator;
		if (StringCtrl.isEmpty(sep)) {
			sep = ";";
		}

		if (actRec != null && actRec.intForKey("actOrdre") >= 0) {
			clePere = actRec.numberForKey("actPere");
			if (includeLeaf) {
				path.append(actRec.stringNormalizedForKey("actLibelle")).append(sep);
			}
		} else {
			clePere = new Integer(-1);
		}

		while (clePere.intValue() > 0) {
			rec = EOVActivites.fetchByQualifier(editingContext(), newCondition(
					EOVActivites.ACT_ORDRE_KEY + "!=0 and " + EOVActivites.ACT_ORDRE_KEY + "=" + clePere));
			if (rec == null)
				break;
			clePere = rec.actPere();
			path.insert(0, sep).insert(0, StringCtrl.normalize(rec.actLibelle()));
		}

		return path.toString();
	}

	/**
	 * Retourne le chemin de l'activite representee dans l'enregistrement
	 * <code>actRec</code>. Le chemin est constitue des <code>CktlRecord</code> de
	 * l'entite <code>VActivite</code>.
	 */
	public NSArray findActivitePath(CktlRecord actRec) {
		NSArray path = new NSArray();
		Number clePere;
		CktlRecord rec;

		if ((actRec != null) && (actRec.intForKey("actOrdre")) >= 0) {
			clePere = actRec.numberForKey("actPere");
			path = path.arrayByAddingObject(actRec);
		} else {
			clePere = new Integer(-1);
		}
		while (clePere.intValue() > 0) {
			rec = EOVActivites.fetchByQualifier(editingContext(), newCondition("actOrdre!=0 and actOrdre=" + clePere));
			if (rec == null)
				break;
			clePere = rec.numberForKey("actPere");
			path = path.arrayByAddingObject(rec);
		}
		// on inverse tout ca pour avoir le sens pere -> fils
		NSArray revertPath = new NSArray();
		for (int i = 0; i < path.count(); i++)
			revertPath = revertPath.arrayByAddingObject(path.objectAtIndex(path.count() - i - 1));
		return revertPath;
	}

	/**
	 * Mets a jour les mots cles de totes les interventions dont les mots cles
	 * contiennent <code>oldPath</code>. Le chemin doit se terminer par le libelle
	 * <code>oldLibelle</code>. Il sera remplace par <code>newLibelle</code>. La
	 * methode ne fait rient si le libelle n'est pas modifie.
	 * 
	 * @param ec
	 *          Editing context en cours. Ne doit pas etre <i>null</i>.
	 * @param newLibelle
	 *          Le nouveau libelle.
	 * @param oldLibelle
	 *          Le libelle a modifier.
	 * @param oldPath
	 *          Le chemin des mots cles, chaque mot cle est separe par un ";".
	 */
	private void updateLibelle(EOEditingContext ec, String newLibelle,
															String oldLibelle, String oldPath) {
		// Modifier uniquement s'il faut modifier
		if (!StringCtrl.normalize(oldLibelle).equals(StringCtrl.normalize(newLibelle))) {
			// On corrige les mots cles de toutes les interventions pour qu'il
			// correspondent aux nouveaux mots cles
			CktlRecord rec;
			String oriPath;
			StringBuffer sb;
			EOQualifier condition = newCondition("intMotsClefs like '" + oldPath + "*'");
			NSArray objects = fetchArray(ec, "Intervention", condition, null);
			for (int i = 0; i < objects.count(); i++) {
				rec = (CktlRecord) objects.objectAtIndex(i);
				oriPath = rec.stringForKey("intMotsClefs");
				sb = new StringBuffer(StringCtrl.getPrefix(oldPath, oldLibelle + ";"));
				sb.append(newLibelle).append(";").append(StringCtrl.getSuffix(oriPath, oldPath));
				rec.takeValueForKey(sb.toString(), "intMotsClefs");
			}
		}
	}

	// /**
	// * Mets a jour les mots cles de totes les interventions dont les mots cles
	// * contiennent <code>oldPath</code>. Le chemin doit se terminer par le
	// * libelle <code>oldLibelle</code>. Il sera remplace par
	// <code>newLibelle</code>.
	// * La methode ne fait rient si le libelle n'est pas modifie.
	// *
	// * @param ec Editing context en cours. Ne doit pas etre <i>null</i>.
	// * @param newPath Les nouveaux chemin des mots cl�s. Chaque mot est separe
	// * par un ";
	// * @param oldPath Le chemin des anciens mots cles.
	// */
	// private void updatePath(EOEditingContext ec, String newPath, String
	// oldPath)
	// {
	// // Modifier uniquement s'il faut modifier
	// if (!StringCtrl.normalize(oldPath).equals(StringCtrl.normalize(newPath))) {
	// // On corrige les mots cles de toutes les interventions pour qu'il
	// // correspondent aux nouveaux mots cles
	// CktlRecord rec;
	// String oriPath;
	// StringBuffer sb = new StringBuffer();
	// EOQualifier condition = newCondition("intMotsClefs like '"+oldPath+"*'");
	// NSArray objects = fetchArray(ec, "Intervention", condition, null);
	// for(int i=0; i<objects.count(); i++) {
	// rec = (CktlRecord)objects.objectAtIndex(i);
	// oriPath = rec.stringForKey("intMotsClefs");
	// sb.setLength(0);
	// sb.append(newPath);
	// if (!newPath.endsWith(";")) sb.append(";");
	// sb.append(StringCtrl.getSuffix(oriPath, oldPath));
	// rec.takeValueForKey(sb.toString(), "intMotsClefs");
	// }
	// }
	// }

	/**
	 * Retourne le code de la vue a utiliser pour creer une demande associee a
	 * l'activite <code>actOrdre</code>.
	 */
	public int getSwapviewForActivite(Number actOrdre) {
		CktlRecord rec = findActivite(actOrdre, null);
		if (rec == null)
			return -1;
		if (rec.intForKey("actPref") > 0) {
			return rec.intForKeyPath("toActPref.actSwapView");
		} else {
			return rec.intForKeyPath("toGroupesDt.grpSwapView");
		}
	}

	/**
	 * Retourne le tableau des <code>cStructure</code> liee a une swapView dans
	 * l'arborescence des activites.
	 */
	public NSArray getCStructuresForSwapView(int swapViewID) {
		return (NSArray) fetchArray("Activites", newCondition(
				"actSwapView=" + swapViewID), null).valueForKey("cStructure");
	}

	/**
	 * Returne l'adresse email du service associe a l'activite
	 * <code>actOrdre</code>. Retourne <i>null</i> si l'adresse email n'est pas
	 * defini pour le service.
	 */
	public String getServiceMailForActivite(Number actOrdre) {
		setRefreshFetchedObjects(false);
		CktlRecord rec = findVActivite(actOrdre);
		if (rec == null)
			return null;
		return rec.stringForKeyPath("toActResp.actMailService");
	}

	/**
	 * Teste si l'activite avec le code <code>actOrdre</code> a des activites
	 * fils.
	 */
	public boolean hasFils(Number actOrdre) {
		// On execute un raw SQL
		NSArray objects = DTCktlEOUtilities.rawRowsForSQL(
				new EOEditingContext(), dtApp().mainModelName(),
				"SELECT COUNT(*) FROM ACTIVITES WHERE ACT_PERE=" + actOrdre, null);
		// S'il y a des resultats, on les teste
		if (objects.count() > 0) {
			NSDictionary dico = (NSDictionary) objects.objectAtIndex(0);
			objects = dico.allValues();
			if (objects.count() > 0)
				return Integer.valueOf(dico.allValues().objectAtIndex(0).toString()).intValue() > 0;
		}
		// Sinon, c'est comme s'il n'y avait pas de fils
		return false;
	}

	// **************************************************************
	// ----- La gestion des responsables ----

	/**
	 * Ajoute un nouveau responsable pour une activite.
	 */
	public CktlRecord addResponsable(Integer transId,
																	Number noIndividu, Number actOrdre,
																	String actTypeResponsable) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			CktlRecord rec = (CktlRecord) EOUtilities.createAndInsertInstance(ec, "ActivitesResponsables");
			rec.takeValueForKey(noIndividu, "noIndividu");
			rec.takeValueForKey(actOrdre, "actOrdre");
			rec.takeValueForKey(actTypeResponsable, "actTypeResponsable");
			if (transId == null)
				commitECTransaction(localTransId);
			return rec;
		} catch (Throwable ex) {
			throwError(ex);
			return null;
		}
	}

	/**
	 * Supprime la definition d'un responsable d'une activite.
	 */
	public boolean deleteResponsable(Integer transId,
																		Number noIndividu,
																		Number actOrdre,
																		String typeResposable) {
		StringBuffer sb = new StringBuffer();
		sb.append("noIndividu=").append(noIndividu);
		sb.append(" and actOrdre=").append(actOrdre);
		sb.append(" and actTypeResponsable='").append(typeResposable).append("'");
		return (deleteFromTable(transId, "ActivitesResponsables", newCondition(sb.toString())) >= 0);
	}

	/**
	 * Teste si l'individu <code>noIndividu</code> a des responsabilite
	 * <code>typeResponsable</code> pour l'activite <code>actOrdre</code>.
	 */
	public boolean isActiviteResponsable(Number actOrdre,
																				String typeResponsable,
																				Number noIndividu) {
		if ((actOrdre == null) || (typeResponsable == null) || (noIndividu == null))
			return false;
		// Ben, on cherche...
		return (findActiviteResponsables(actOrdre, typeResponsable, noIndividu).count() > 0);
	}

	/**
	 * Teste si la personne en cours a le niveau des droits demande pour l'activie
	 * <code>actOrdre</code>. La methode verifie d'abord si l'utilisateur a les
	 * fonctions <code>typeResponsable</code> pour l'activite (ex, le responsable
	 * fonctionnel ou technique). Sinon, on verifie s'il a les droits requis au
	 * niveau de l'application et de service en general (intervenant,
	 * administrateur,...).
	 */
	public boolean hasDroitsForActivite(Number actOrdre, Number noIndividu,
																			String typeResponsable, int niveauDroits) {
		boolean ok = false;
		CktlRecord rec = findActivite(actOrdre, null);
		// Teste si la personne est le responsable <code>typeResponsable</code>
		if (rec != null) {
			ok = isActiviteResponsable(actOrdre, typeResponsable, noIndividu);
			// Sinon, peut etre administrateur/chef de service
			if (!ok && (niveauDroits != -1))
				ok = userInfo().hasDroit(niveauDroits, userInfo().dtServiceCode()) &&
							rec.valueForKey("cStructure").equals(userInfo().dtServiceCode());
		}
		return ok;
	}

	/**
	 * Retourne la liste de tous les responsables de l'activite
	 * <code>actOrdre</code> de type <code>typeResponsable</code>. Le type doit
	 * etre une des valeurs <code>TYPE_RESP_FONCTIONNEL</code>,
	 * <code>TYPE_RESP_TECHNIQUE</code> ou <code>TYPE_RESP_INTERVENANT</code>.
	 * 
	 * <p>
	 * Les parametres <code>typeResponsable</code> et <code>noIndividu</code>
	 * peuvent etre <i>null</i>. Dans ce cas, ils ne seront pas pris en compte
	 * lors de la recherche.
	 * </p>
	 */
	public NSArray<EOActivitesResponsables> findActiviteResponsables(Number actOrdre,
																					String typeResponsable,
																					Number noIndividu) {
		// On trouve d'abord le code de l'activite
		setRefreshFetchedObjects(false);
		CktlRecord rec = findVActivite(actOrdre);
		// Si aucune definition, on retourne le tableau vide
		if (rec == null) {
			return new NSArray<EOActivitesResponsables>();
		}
		// Sinon, on cherche la definition des responsables,
		// mais on prend en compte l'heritage
		StringBuffer condition = new StringBuffer("actOrdre=");
		condition.append(rec.valueForKey("actResp"));
		if (typeResponsable != null)
			condition.append(" and actTypeResponsable='").append(typeResponsable).append("'");
		if (noIndividu != null)
			condition.append(" and noIndividu=" + noIndividu);
		setRefreshFetchedObjects(false);
		return fetchArray(EOActivitesResponsables.ENTITY_NAME, newCondition(condition.toString()), null);
	}

	/**
	 * Retourne la liste des adresses mail pour le service et le type des
	 * responsables donnees.
	 */
	public NSArray<String> findActiviteMails(
			Number actOrdre, String typeResponsable) {
		NSMutableArray<String> mails = new NSMutableArray<String>();
		NSArray<EOActivitesResponsables> responsables = findActiviteResponsables(actOrdre, typeResponsable, null);
		// Les mails des personnes responsables
		for (int i = 0; i < responsables.count(); i++) {
			EOActivitesResponsables rec = responsables.objectAtIndex(i);
			mails.addObject(
					individuBus().mailForNoIndividu(rec.noIndividu()));
		}
		// Le mail du service
		setRefreshFetchedObjects(false);
		/*
		 * rec = findVActivite(actOrdre); if (rec != null) { // Se positionner sur
		 * l'activite avec la definition des responsables
		 * if(rec.intForKey("actOrdre") != rec.intForKey("actResp")) rec =
		 * rec.recForKey("toActResp"); // Verifier qu'il faut bien envoyer le mail
		 * au service if (rec.stringForKey("actMailService").equals("O")) { rec =
		 * rec.recForKey("toGroupesDt"); if ((rec != null) &&
		 * (rec.stringNormalizedForKey("grpEmail").length() > 0))
		 * mails.addObject(rec.valueForKey("grpEmail")); } }
		 */

		EOVActivites eoVActivite = findVActivite(actOrdre);
		String mailService = eoVActivite.getGrpMailSiExistantEtUtilisable();
		if (!StringCtrl.isEmpty(mailService)) {
			mails.addObject(mailService);
		}

		return mails;
	}

	/**
	 * Retourne la liste des activites correspondant aux parametres donnes.
	 * 
	 * @param useAffichable
	 *          Indique que le test doit etre fait si une activite et sont service
	 *          sont affichables.
	 * @param useWeb
	 *          Indique que le test doit etre fait si les activites des services
	 *          sont visibles sur le Web.
	 * @param checkIfLeaf
	 *          Indique si le test si l'activite est une activite "feuille" doit
	 *          etre effectue.
	 * @param cStructure
	 *          Indique le code de service dont les activites sont recherchees. Si
	 *          ce parametre est <i>null</i>, la liste de toutes les activites est
	 *          retournee.
	 */
	public NSArray findAllActivites(
			boolean useAffichable, boolean useWeb,
			boolean checkIfLeaf, String cStructure) {
		StringBuffer condition = new StringBuffer();
		condition.append("actCreerMail <> nil");
		if (useAffichable) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("(actAffichable='O' and toGroupesDt.grpAffichable='O')");
		}
		if (useWeb) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append(
					"(toGroupesDt.grpVisibilite=nil or toGroupesDt.grpVisibilite like '*|WEB|*')");
		}
		if (cStructure != null) {
			if (condition.length() > 0)
				condition.append(" and ");
			condition.append("(cStructure='" + cStructure + "')");
		}
		CktlLog.trace("condition : " + condition.toString());
		NSArray activites = fetchArray("Activites", newCondition(condition.toString()), null);
		if (checkIfLeaf) {
			NSMutableArray fActivites = new NSMutableArray();
			CktlRecord rec;
			CktlLog.trace("activitesMails.count : " + activites.count());
			for (int i = 0; i < activites.count(); i++) {
				rec = (CktlRecord) activites.objectAtIndex(i);
				if (rec.arrayForKeyPath("tosActFils").count() == 0)
					fActivites.addObject(rec);
			}
			return fActivites;
		} else {
			return activites;
		}
	}

	/**
	 * Retourne la liste des adresses email associes aux activites. Ce sont les
	 * adresse email utilisees pour la creation des demandes par email. Les
	 * activites selectionnees doivent correspondre aux parametres indiques.
	 * 
	 * @param useAffichable
	 *          Indique que le test doit etre fait si une activite et sont service
	 *          sont affichables.
	 * @param useWeb
	 *          Indique que le test doit etre fait si les activites des services
	 *          sont visibles sur le Web.
	 * @param checkIfLeaf
	 *          Indique si le test si l'activite est une activite "feuille" doit
	 *          etre effectue.
	 * @param cStructure
	 *          Indique le code de service dont les activites sont recherchees. Si
	 *          ce parametre est <i>null</i>, la liste de toutes les activites est
	 *          retournee.
	 * 
	 * @see #findAllActivites(boolean, boolean, String)
	 */
	public NSArray findAllActivitesMails(
			boolean useAffichable, boolean useWeb,
			boolean checkIfLeaf, String cStructure) {
		CktlRecord rec;
		NSMutableArray mails = new NSMutableArray();
		NSArray activites =
				findAllActivites(useAffichable, useWeb, checkIfLeaf, cStructure);
		for (int i = 0; i < activites.count(); i++) {
			rec = (CktlRecord) activites.objectAtIndex(i);
			if (rec.stringNormalizedForKey("actCreerMail").length() > 0)
				mails.addObject(rec.stringForKey("actCreerMail"));
		}
		return mails;
	}

	/**
	 * Renvoie l'activite avec le code <code>actOrdre</code>. Si
	 * <code>cStructure</code> n'est pas <i>null</i>, verifie qu'elle appartienne
	 * au service indique. Renvoie <i>null</i> si les conditions donnees ne sont
	 * pas satisfaites.
	 */
	public CktlRecord findActivite(Number actOrdre, String cStructure) {
		StringBuffer condition = new StringBuffer();
		condition.append("actOrdre = ").append(actOrdre);
		if (cStructure != null)
			condition.append(" and cStructure = '").append(cStructure).append("'");
		return (CktlRecord) fetchObject("Activites", newCondition(condition.toString()));
	}

	/**
	 * Retourne l'activite correspondant a l'adresse email
	 * <code>activiteMail</code>. Retourne <em>null</em> si aucune activite ne
	 * correspond a l'adresse donnee.
	 * 
	 * <p>
	 * Si <code>testIfLeaf</code> est <em>true</em>, alors teste si l'activite
	 * correspond bien a une feuille dans l'arborescence des activites.
	 * </p>
	 */
	public Number findActiviteForMail(String activiteMail, boolean testIfLeaf) {
		StringBuffer condition = new StringBuffer();
		condition.append("actCreerMail caseInsensitiveLike '").append(activiteMail).append("'");
		if (testIfLeaf)
			condition.append(" and tosActFils.actOrdre = nil");
		NSArray objects =
				fetchArray("Activites", newCondition(condition.toString()), null);
		if (objects.count() > 0) {
			return ((CktlRecord) objects.objectAtIndex(0)).numberForKey("actOrdre");
		}
		return null;
	}

	/**
	 * Teste si l'activite est une activite "feuille", i.e. elle n'a pas de
	 * sous-activites.
	 */
	public boolean checkIsLeaf(CktlRecord recActivite) {
		// L'activite n'a pas de fils
		return (recActivite.arrayForKeyPath("tosActFils").count() == 0);
	}

	/**
	 * Teste si l'activite est afficheable. L'activite n'est pas afficheable si au
	 * moins une activite pere ne l'est pas.
	 */
	public boolean checkIsVisible(CktlRecord recActivite) {
		// Si on a remonte jusqu'au pere, alors l'activite est bien visible
		if (recActivite.intForKey("actOrdre") == 0)
			return true;
		// Si une activite n'est pas affichable, alors ni elle ni aucun de ces fils
		// ne l'est pas
		if (!recActivite.stringForKey("actAffichable").equals("O"))
			return false;
		// Sinon, on verifie de maniere recoursive aupres de son pere
		return checkIsVisible(recActivite.recForKeyPath("toActPere"));
	}

	/**
	 * Retourne le nombre des resposables de type <code>typeResponsable</code>
	 * defini pour l'activite avec le code <code>actOrdre</code>. Si le
	 * <code>typeResponsable</code> est <i>null</i>, alors retourne le nombre
	 * total des responsables.
	 */
	public int countResponsables(Number actOrdre, String typeResponsable) {
		setRefreshFetchedObjects(false);
		return findActiviteResponsables(actOrdre, typeResponsable, null).count();
	}

	/**
	 * Indique de quelle maniere les responsables sont definis pour l'activite
	 * <code>actOrdre</code>. Retourne une des valeurs <code>RESP_INDEFINI</code>,
	 * <code>RESP_DEFINI</code> (definition locale) ou <code>RESP_HERITE</code>
	 * (definitin heritee d'une des activites "peres").
	 */
	public int findTypeRespDefinition(Number actOrdre) {
		int type = EOActivites.RESP_INDEFINI;

		EOVActivites rec = findVActivite(actOrdre);

		// Activite racine (actResp = 0) : indefini
		if (rec != null && rec.actResp().intValue() != 0) {
			if (rec.actResp().intValue() == rec.actOrdre().intValue()) {
				return EOActivites.RESP_DEFINI;
			} else {
				return EOActivites.RESP_HERITE;
			}
		}

		return type;
	}

	// /**
	// * Rempli la table des responsables d'une activite et d'un type donne.
	// */
	// public void fetchResponsables(EODisplayGroup table, Number actOrdre, String
	// type) {
	// StringBuffer sb = new StringBuffer("actOrdre=").append(actOrdre);
	// sb.append(" and actTypeResponsable='").append(type).append("'");
	// fetchTable(table, newCondition(sb.toString()), null, true);
	// }

	/**
	 * Force le re-calcul "automatique" de la hierarchie de la definition des
	 * responsables pour toutes les activites. Si le parametre removeEmpty est
	 * <i>true</i> alors les definition vides (sans aucun responsable) seront
	 * supprimees.
	 */
	public void rebuildAllResponsables(boolean removeEmpty) {
		Number actResponsable;
		EOQualifier condition;
		CktlRecord rec;
		EOEditingContext ec = new EOEditingContext();
		NSArray activites = fetchArray(ec, "Activites", null, null);
		NSArray responsables = fetchArray("ActivitesResponsables", null, null);
		ActivitesResponsablesBuilder builder = new ActivitesResponsablesBuilder(activites, responsables);
		// On recupere tous les groupes
		activites = fetchArray("GroupesDt", null, null);
		// et on recalcul pour chaque groupe
		for (int i = 0; i < activites.count(); i++) {
			rec = (CktlRecord) activites.objectAtIndex(i);
			responsables = builder.filterResponsables(newCondition("actOrdre=-" + rec.valueForKey("cStructure")));
			condition = newCondition("(actPere=0) and (actOrdre!=0) and cStructure='" + rec.valueForKey("cStructure") + "'");
			actResponsable = StringCtrl.toInteger("-" + rec.valueForKey("cStructure"), 0);
			builder.forceRebuild(condition, actResponsable, rec.stringForKey("cStructure"), removeEmpty);
		}
		ec.saveChanges();
	}

	/**
	 * Ajoute la definiton des responsables pour l'activite <code>actOrdre</code>.
	 * 
	 * @param transId
	 *          Le code d'une transaction ou <code>null</code>.
	 * @param actOrdre
	 *          Le code de l'activite pour laquelle on cree un nouvelle definition
	 *          des responsables.
	 * @param importActResp
	 *          Si n'est pas <i>null</i>, les definitions seront importees a
	 *          partir de cette activite.
	 */
	public void insertActiviteResponsables(Integer transId, Number actOrdre, Number importActResp) {
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		NSArray activites;
		ActivitesResponsablesBuilder builder;
		CktlRecord rec;
		NSArray objects;

		// Ca fonctionne pas pour les groupes
		if (actOrdre.intValue() < 0)
			return;
		// Les activites, donc
		try {
			activites = fetchArray(ec, "Activites", null, null);
			// On cherche l'activite "actOrdre"
			objects = EOQualifier.filteredArrayWithQualifier(activites, newCondition("actOrdre=" + actOrdre));
			if (objects.count() > 0) {
				rec = (CktlRecord) objects.objectAtIndex(0);
				if (rec.intForKey("actOrdre") != rec.intForKey("actResp")) {
					// On indique un nouveau noeud de definition des responsables
					rec.takeValueForKey(actOrdre, "actResp");
					if (importActResp != null) {
						// On prend la definition du l'activite pere
						CktlLog.trace("getSendMailForActivite : " + getServiceMailForActivite(importActResp));
						rec.takeValueForKey(valueIfNull(getServiceMailForActivite(importActResp)), "actMailService");
						// On trouve les responsables de pour cette activite - ceux de son
						// pere
						objects = findActiviteResponsables(importActResp, null, null);
						for (int i = 0; i < objects.count(); i++) {
							rec = (CktlRecord) objects.objectAtIndex(i);
							addResponsable(localTransId, rec.numberForKey("noIndividu"),
															actOrdre, rec.stringForKey("actTypeResponsable"));
						}
					} else {
						// Par defaut, on envoie le mail au service
						rec.takeValueForKey("O", "actMailService");
					}
					builder = new ActivitesResponsablesBuilder(activites, null);
					builder.updateChildren(newCondition("actOrdre=" + actOrdre), actOrdre);
				}
			}
			if (transId == null)
				commitECTransaction(localTransId);
		} catch (Throwable ex) {
			throwError(ex);
			// if (transId == null) ec.revert();
		}
	}

	/**
	 * Public supprime la definition locale des responsables pour l'activite avec
	 * le code <code>actOrdre</code>.
	 * 
	 * @param transId
	 * @param actOrdre
	 */
	public boolean deleteResponsablesNoeud(Integer transId, Number actOrdre) {
		// Ca fonctionne pas pour les groupes
		if (actOrdre.intValue() < 0) {
			return false;
		}
		// Les activites, donc
		// On recalcule les dependances des fils
		try {
			Integer localTransId = getTransaction(transId);
			EOEditingContext ec = econtextForTransaction(localTransId, true);
			NSArray<EOActivites> activites = fetchArray(ec, EOActivites.ENTITY_NAME, null, null);
			ActivitesResponsablesBuilder builder = new ActivitesResponsablesBuilder(activites, null);
			// On reprend les definitions des responsabilites a partir du pere de
			// l'activite
			setRefreshFetchedObjects(false);
			EOActivites rec = (EOActivites) fetchObject(EOActivites.ENTITY_NAME,
					ERXQ.equals(EOActivites.ACT_ORDRE_KEY, actOrdre));
			if (rec == null) {
				return false;
			}
			// attention, la racine de toute activité est l'activité racine
			// il faut rester au niveau service
			EOActivites recPere = null;
			if (rec.toActPere() != null) {
				if (rec.toActPere().actOrdre().intValue() != 0) {
					recPere = rec.toActPere();
				} else {
					recPere = rec;
					rec.setActResp(new Integer("-" + rec.cStructure()));
				}
			}
			if (recPere == null) {
				return false;
			}
			builder.updateChildren(
					ERXQ.equals(EOActivites.ACT_ORDRE_KEY, actOrdre),
					recPere.actResp());
			// Si tout se passe bien, on supprime les definitions des responsabilites
			boolean deleted = deleteAllResponsables(localTransId, actOrdre);
			if (transId == null) {
				if (deleted) {
					commitECTransaction(localTransId);
				} else {
					rollbackECTrancsacition(localTransId);
				}
			}
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Supprime toutes les definitions des responsables associes a l'activite avec
	 * le code <code>actOrdre</code>.
	 * 
	 * @param transId
	 *          L'identifiant d'une transaction ou <i>null</i>.
	 * @param actOrdre
	 *          Le code d'une activite.
	 */
	public boolean deleteAllResponsables(Integer transId, Number actOrdre) {
		return (deleteFromTable(transId, "ActivitesResponsables", newCondition("actOrdre=" + actOrdre)) >= 0);
	}

	/**
	 * Just pour simplifier la vie... Filtre le tableau d'objets en selectionnat
	 * ceux qui satisfont la condition donnee. Retourne un tableau vide, si aucun
	 * objet ne peux pas etre trouve.
	 */
	private static NSArray filterArray(NSArray array, EOQualifier condition) {
		if (array == null)
			return EmtyArray;
		else
			return EOQualifier.filteredArrayWithQualifier(array, condition);
	}

	/**
	 * Teste si le groupe avec le code <code>cStructure</code> peut etre visible
	 * pour les utilisateurs du reseaux ayant le code <code>codeLan</code>. Le
	 * parametre <code>cStructure</code> indique le services dont les demandes
	 * peuvent etre accedees. <code>codeLan</code> peut avoir les valeurs :
	 * <ul>
	 * <li>a un des codes <code>cVlan</code> de la table <i>Vlans</i>&nbsp;;</li>
	 * <li>, ou le code "<code>WEB</code>" pour verifier si le service est visible
	 * sur le Web.</li>
	 * </ul>
	 * 
	 * Si aucune defintion n'est trouvee pour le service cStructure, alors return
	 * <i>true</i> (visible par defaut).
	 */
	public boolean isGroupeVisibleInLan(String codeLan, String cStructure) {
		CktlRecord rec;
		setRefreshFetchedObjects(false);
		rec = findGroupeWithCode(cStructure);
		if ((rec != null) && (rec.valueForKey("grpVisibilite") != null)) {
			StringBuffer sb = new StringBuffer();
			sb.append("|").append(codeLan).append("|");
			return (rec.stringForKey("grpVisibilite").indexOf(sb.toString()) >= 0);
		}
		// Visible par defaut.
		return true;
	}

	/**
	 * Retourne le code d'etat pour une nouvelle demande creee dans l'activite
	 * <code>actOrdre</code>. En regle general, si une activite a des responsables
	 * fonctionnels designes, alors l'etat est "non-validee". Sinon, l'etat est
	 * "non-affectee".
	 */
	public String newEtatCodeForActivite(Number actOrdre) {
		if (countResponsables(actOrdre, EOActivites.TYPE_RESP_FONCTIONNEL) > 0)
			return EOEtatDt.ETAT_NON_VALIDEES;
		else
			return EOEtatDt.ETAT_NON_AFFECTEES;
	}

	/**
	 * Permet de recalculer la hierarchie des responsables d'une activite donne.
	 * Le principe du calcul : si la definition d'un noeud est changee pour une
	 * activite, alors elle doit etre redefinie pour tous ses fils. Le calcul
	 * s'arrete si la definition local est trouvee.
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 */
	private class ActivitesResponsablesBuilder {
		// Toutes les activites
		private NSArray activites;
		// Tous les responsables
		private NSArray responsables;

		/**
		 * Cree une nouvelle instance effectuera les calculs sur les activites
		 * <code>newActivites</code> (toutes les activites) et les responsables
		 * <code>newResponsables</code> (tous les responsables).
		 */
		public ActivitesResponsablesBuilder(NSArray newActivites, NSArray newResponsables) {
			activites = newActivites;
			responsables = newResponsables;
		}

		public void forceRebuild(EOQualifier condition, Number newActResponsable, String codeService, boolean removeEmpty) {
			Number actResponsable;
			CktlRecord rec;
			NSArray resps;
			NSArray acts = filterActivites(condition);

			for (int i = 0; i < acts.count(); i++) {
				// Mise a jour de l'activite
				rec = (CktlRecord) acts.objectAtIndex(i);
				resps = filterResponsables(newCondition("actOrdre=" + rec.valueForKey("actOrdre")));
				// Trouver le code de responsable
				// S'il existe les definitions des responsables
				if (resps.count() > 0) {
					actResponsable = rec.numberForKey("actOrdre");
				} else {
					// S'agit-il d'une definition d'un noeud vide qu'il faut garder ?
					if (rec.valueForKey("actResp").equals(rec.valueForKey("actOrdre")) && (!removeEmpty)) {
						actResponsable = rec.numberForKey("actResp");
						// Sinon, les responsables, sont-ils definis pour une activite pere
						// ?
					} else if (newActResponsable != null) {
						actResponsable = newActResponsable;
						// Sinon, on va introduire un noeud vide
					} else {
						actResponsable = rec.numberForKey("actOrdre");
					}
				}
				rec.takeValueForKey(actResponsable, "actResp");
				forceRebuild(newCondition("actPere=" + rec.valueForKey("actOrdre") + " and cStructure='" + codeService + "'"),
						actResponsable, codeService, removeEmpty);
			}
		}

		/**
		 * Mets a jours toutes les activites satisfaisant la condition ainsi que
		 * tous leurs fils en leur indiquant le nouveau noeud de la definition des
		 * responsables. La mise a jour s'arrete sur les fils qui ont les
		 * definitions locales.
		 * 
		 * @param condition
		 *          La condition que les noeuds doivent satisfaire.
		 * @param newActRespOrdre
		 *          Le code de l'activite avec la definition des responsables.
		 */
		public void updateChildren(EOQualifier condition, Number newActRespOrdre) {
			CktlRecord rec;
			NSArray acts = filterActivites(condition);

			for (int i = 0; i < acts.count(); i++) {
				rec = (CktlRecord) acts.objectAtIndex(i);
				rec.takeValueForKey(newActRespOrdre, "actResp");
				// Tous les fils sauf ceux qui ont les definitions locales
				condition = newCondition("actPere=" + rec.valueForKey("actOrdre") + " and actResp<>actOrdre");
				updateChildren(condition, newActRespOrdre);
			}
		}

		/**
		 * Trouve toutes les activites satisfaisant la condition donnee.
		 */
		public NSArray filterActivites(EOQualifier condition) {
			return filterArray(activites, condition);
		}

		/**
		 * Trouve toutes les responsables satisfaisant la condition donnee.
		 */
		public NSArray filterResponsables(EOQualifier condition) {
			return filterArray(responsables, condition);
		}
	}

	/**
	 * Retourne la liste des responsables d'une activite et d'un type donne.
	 */
	public NSArray<EOActivitesResponsables> findResponsables(Number actOrdre, String type) {
		StringBuffer sb = new StringBuffer("actOrdre=").append(actOrdre);
		sb.append(" and actTypeResponsable='").append(type).append("'");
		return fetchArray("ActivitesResponsables", newCondition(sb.toString()), null);
	}

	/**
	 * Permet d'afficher les messages utilisateur pendant l'execution d'une suite
	 * d'opearations par ce data bus.
	 * 
	 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
	 */
	public interface ActiviteBusListener {
		/**
		 * Affiche un message d'avancement.
		 */
		public void advanceMessage(String message);

		/**
		 * Affiche un message.
		 * 
		 * @param message
		 */
		public WOComponent showMessage(String message);
	}

	/**
	 * Retourne la chaine de caracteres avec la hierarchie des activites du
	 * service ayant le code <code>serviceCode</code> (code structure). Le contenu
	 * de la chaine de caracteres est un document HTML.
	 */
	public String printActivitesHTML(String serviceCode) {
		StringBuffer out = new StringBuffer();
		CktlRecord rec;
		out.append("<html><body><center><h3>Les activit&eacute;s du service<br>");
		rec = serviceBus().structureForCode(serviceCode);
		if (rec != null) {
			out.append(rec.stringForKey("llStructure"));
		} else {
			out.append("<INCONNU>");
		}
		out.append("</h3></center>");
		out.append("<hr noshade size='1'><font size='-1' face='Courier New'>");
		printActivitesHTML(
				newCondition("cStructure='" + serviceCode + "' and actPere=0 and actOrdre<>0"),
				StringCtrl.emptyString(), out);
		out.append("</font><hr noshade size='2'></body></html>");
		return out.toString();
	}

	/**
	 * Exportation recursive des activites au format HTML
	 * 
	 * @param condition
	 * @param printPrefix
	 * @param out
	 */
	private void printActivitesHTML(
			EOQualifier condition, String printPrefix, StringBuffer out) {
		CktlRecord rec;
		NSArray objects = fetchArray("Activites", condition, CktlSort.newSort("actLibelle"));
		for (int i = 0; i < objects.count(); i++) {
			rec = (CktlRecord) objects.objectAtIndex(i);
			// TODO utiliser l'attribut actAffichable de la vue VActivites
			boolean actAffichable = rec.boolForKey("actAffichable");
			// on barre les activites masquees (balise d'ouverture)
			if (!actAffichable) {
				out.append("<s>");
			}
			out.append(printPrefix).append(" &gt; <b>");
			out.append(DTStringCtrl.codeHTML(rec.stringForKey("actLibelle"))).append("</b>");
			out.append("&nbsp;");
			// email SAM
			String emailSam = rec.stringForKey("actCreerMail");
			if (!StringCtrl.isEmpty(emailSam)) {
				out.append("<a href=\"mailto:").append(emailSam).append("\">");
				out.append("&lt;").append(emailSam).append("&gt;</a>&nbsp;");
			}
			// si heritage, alors on met la police en italique (balise d'ouverture)
			out.append("(");
			// les responsables
			printResponsablesHTML(rec, out);
			// on barre les activites masquees (balise de fermeture)
			if (!actAffichable) {
				out.append("</s>");
			}
			//
			out.append(")<br/>");
			printActivitesHTML(newCondition("actPere=" + rec.valueForKey("actOrdre")),
					printPrefix + " &gt; " + DTStringCtrl.codeHTML(rec.stringForKey("actLibelle")), out);
		}
	}

	/**
	 * La ligne contenant la descriptions des responsables
	 * 
	 * @param actOrdre
	 * @param out
	 */
	private void printResponsablesHTML(
			CktlRecord recActivite, StringBuffer out) {
		Number actOrdre = recActivite.numberForKey("actOrdre");
		int typeHeritage = findTypeRespDefinition(actOrdre);
		if (typeHeritage == EOActivites.RESP_HERITE) {
			// responsables herites, on affiche de qui
			CktlRecord recVActivite = findVActivite(actOrdre);
			String strActLibelle = recVActivite.stringForKeyPath("toActResp.actCode");
			strActLibelle = DTStringCtrl.getHTMLString(strActLibelle);
			out.append("<em>Responsables h&eacute;rit&eacute;s de <b>");
			out.append(strActLibelle);
			out.append("</b></em>");
		} else if (typeHeritage == EOActivites.RESP_INDEFINI) {
			out.append("<em>Pas de responsables</em>");
		} else {
			// on affiche tous les responsables, par type
			String typeList[] = { EOActivites.TYPE_RESP_FONCTIONNEL, EOActivites.TYPE_RESP_TECHNIQUE, EOActivites.TYPE_RESP_INTERVENANT };
			for (int k = 0; k < typeList.length; k++) {
				String typeResp = (String) typeList[k];
				//
				if (k > 0) {
					out.append("; ");
				}
				out.append("<u>");
				if (EOActivites.TYPE_RESP_FONCTIONNEL.equals(typeResp)) {
					out.append("Fct:");
				} else if (EOActivites.TYPE_RESP_TECHNIQUE.equals(typeResp)) {
					out.append("Tec:");
				} else {
					out.append("Int:");
				}
				out.append("</u> ");
				//
				NSArray respFct = activiteBus().findResponsables(actOrdre, typeResp);
				if (respFct.count() > 0) {
					for (int j = 0; j < respFct.count(); j++) {
						CktlRecord recResp = (CktlRecord) respFct.objectAtIndex(j);
						out.append(DTStringCtrl.formatName(
								recResp.stringForKeyPath("toIndividuUlr.prenom"), recResp.stringForKeyPath("toIndividuUlr.nomUsuel")));
						if (j < respFct.count() - 1) {
							out.append(", ");
						}
					}
				} else {
					out.append("-");
				}
			}
		}
	}

	/**
	 * Deplace l'activite <code>actOrdre</code> dans l'arborescence des activites.
	 * Elle sera placee comme fils de l'activite <code>newActPere</code>.
	 * 
	 * @param actOrdre
	 *          Le code de l'activite.
	 * @param newActPere
	 *          Le code de nouveau pere de l'activite.
	 * @param commitAfterMove
	 *          faut-il commiter apres l'operation
	 */
	public boolean moveActivite(Integer transId, Number actOrdre, Number newActPere) {
		boolean prefChanged = false;
		boolean respChanged = false;
		busListenerMessage("Changement de l'activité père...", true);
		Integer localTransId = getTransaction(transId);
		EOEditingContext ec = econtextForTransaction(localTransId, true);
		try {
			CktlRecord recActivite = (CktlRecord) fetchObject(ec, "Activites", newCondition("actOrdre=" + actOrdre));
			CktlRecord recNewPere = findVActivite(newActPere);
			CktlRecord recOldPere = findVActivite(recActivite.numberForKey("actPere"));
			String oldPath = findActivitePathString(recActivite, true, null);
			// Si le code est negatif, alors il s'agit de groupe_dt,
			// on va prendre la valeur "0" dans ce cas
			if (newActPere.intValue() < 0)
				recActivite.takeValueForKey(new Integer(0), "actPere");
			else
				recActivite.takeValueForKey(newActPere, "actPere");
			busListenerMessage("Correction des définitions des préférences...", true);
			// Correction des definitions des preferences
			// Si n'est pas local, et pas celui du pere
			if ((recActivite.intForKey("actPref") != recActivite.intForKey("actOrdre")) // n'est
																																									// pas
																																									// local
					&& (recActivite.intForKey("actPref") != recNewPere.intForKey("actPref"))) {
				// ... on insere la definition locale
				updateActivitePref(localTransId, recActivite, null,
						recOldPere.numberForKey("actSwapView"),
						recOldPere.stringForKey("actSwapMessage"), true);
				prefChanged = true;
				// Sinon, on laisse inchange
			}
			busListenerMessage("Correction des définitions des responsables...", true);
			// Correction des definitions des responsables
			// Si n'est pas local, et pas celui du pere
			if ((recActivite.intForKey("actResp") != recActivite.intForKey("actOrdre")) // n'est
																																									// pas
																																									// local
					&& (recActivite.intForKey("actResp") != recNewPere.intForKey("actResp"))) {
				// ... on definit les responsables localement
				insertActiviteResponsables(localTransId,
																		recActivite.numberForKey("actOrdre"),
																		recActivite.numberForKey("actResp"));
				respChanged = true;
				// Sinon, on laisse inchange
			}
			busListenerMessage("Correction des mots clés...", true);
			updatePath(ec, findActivitePathString(newActPere, true, ";") + recActivite.valueForKey("actLibelle"), oldPath);
			busListenerMessage("Sauvegarde des données...", true);
			if (commitECTransaction(localTransId)) {
				// On informe, s'il y avait des modifications "implicites"
				if ((prefChanged || respChanged) && (busListener != null)) {
					StringBuffer sb = new StringBuffer();
					sb.append("Une définition des préférences est ajoutée localement pour l'activité \"");
					sb.append(recActivite.valueForKey("actLibelle")).append("\",\n");
					sb.append("car les définitions héritées de son ancienne et nouvelle activité père son incompatibles.\n");
					sb.append("Les modification concernent :\n");
					if (prefChanged)
						sb.append("  - la définition des préférences (la vue de création des demandes)\n");
					if (respChanged)
						sb.append("  - la définition des responsables.\n");
					sb.append("\n\nVous pouvez modifier ces définitions via l'interface d'administration des activités.");
					busListenerMessage(sb.toString(), false);
				} else {
					StringBuffer sb = new StringBuffer("L'activité et ses sous activités ont correctement été déplacées");
					busListenerMessage(sb.toString(), false);
				}
			}
			return true;
		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}
	}

	/**
	 * Mets a jour les mots cles de totes les interventions dont les mots cles
	 * contiennent <code>oldPath</code>. Le chemin doit se terminer par le libelle
	 * <code>oldLibelle</code>. Il sera remplace par <code>newLibelle</code>. La
	 * methode ne fait rient si le libelle n'est pas modifie.
	 * 
	 * @param ec
	 *          Editing context en cours. Ne doit pas etre <i>null</i>.
	 * @param newPath
	 *          Les nouveaux chemin des mots cl�s. Chaque mot est separe par un ";
	 * @param oldPath
	 *          Le chemin des anciens mots cles.
	 */
	private void updatePath(EOEditingContext ec, String newPath, String oldPath) {
		// Modifier uniquement s'il faut modifier
		if (!StringCtrl.normalize(oldPath).equals(StringCtrl.normalize(newPath))) {
			// On corrige les mots cles de toutes les interventions pour qu'il
			// correspondent aux nouveaux mots cles
			CktlRecord rec;
			String oriPath;
			StringBuffer sb = new StringBuffer();
			EOQualifier condition = newCondition("intMotsClefs like '" + oldPath + "*'");
			NSArray objects = fetchArray(ec, "Intervention", condition, null);
			for (int i = 0; i < objects.count(); i++) {
				rec = (CktlRecord) objects.objectAtIndex(i);
				oriPath = rec.stringForKey("intMotsClefs");
				sb.setLength(0);
				sb.append(newPath);
				if (!newPath.endsWith(";"))
					sb.append(";");
				sb.append(StringCtrl.getSuffix(oriPath, oldPath));
				rec.takeValueForKey(sb.toString(), "intMotsClefs");
			}
		}
	}

	// ajouts pour la DTFast

	/**
	 * Retourne la liste de toutes les activites correspondantes aux DTs affectees
	 * a l'utilisateur <code>noIndividu</code> dans le service
	 * <code>cStructure</code>, dont la date de creation est comprise dans la
	 * periode passee en parametre
	 */
	public NSArray findAllActivitesIntervenant(Number noIndividu, String cStructure,
			Object dateDebutPeriode, Object dateFinPeriode, String intEtat) {
		EOQualifier qual = newCondition("noIndIntervenant = %@ and cStructure = %@ " +
				"and intDateCreation >= %@ and intDateCreation <= %@ and intEtat = %@",
				new NSArray(new Object[] { noIndividu, cStructure, dateDebutPeriode, dateFinPeriode, intEtat }));
		NSArray results = fetchArray("VActivitesIntervenant", qual, CktlSort.newSort("actLibelle"));
		return NSArrayCtrl.removeDuplicate(results);
	}

	// fusion d'activites

	/**
	 * Effectuer la fusion de 2 activités.
	 * 
	 * @param ui
	 *          TODO
	 * 
	 * @return <em>true</em> si l'operation est réalisée avec succès.
	 */
	public boolean mergeActivites(DTUserInfo ui, Integer transId, Number actOrdreFinal, Number actOrdreToMerge) {
		busListenerMessage("Contrôle des paramètres ...", true);

		//
		Integer localTransId = transId;
		if (transId != null) {
			localTransId = getTransaction(transId);
		}

		try {
			CktlRecord recActiviteFinal = findActivite(actOrdreFinal, null);
			CktlRecord recActiviteToMerge = findActivite(actOrdreToMerge, null);

			// on ne fusionne pas 2 activites identiques
			if (recActiviteFinal.intForKey("actOrdre") == recActiviteToMerge.intForKey("actOrdre")) {
				setErrorMessage("Les deux activités à fusionner sont identiques !");
				return false;
			}

			// on ne fusionne que des activites d'un meme service
			if (!recActiviteFinal.stringForKey("cStructure").equals(recActiviteToMerge.stringForKey("cStructure"))) {
				setErrorMessage("Les deux activités à fusionner ne sont pas dans le même service !");
				return false;
			}

			// on ne fusionne pas un pere avec un fils
			if (isActiviteParent(actOrdreToMerge, actOrdreFinal)) {
				setErrorMessage("Impossible de fusionner une activité avec un des ses parents !");
				return false;
			}

			StringBuffer message = new StringBuffer();
			message.append("Déplacement des DTs ...\n");

			// on deplace toutes les DTs de l'activité a fusionner vers l'activité
			// finale
			// busListenerMessage("Déplacement des DTs ...", true);
			String motsCles = findActivitePathString(recActiviteFinal, true, null);
			NSArray recsIntervention = recActiviteToMerge.arrayForKey("tosIntervention");
			for (int i = 0; i < recsIntervention.count(); i++) {
				CktlRecord recIntervention = (CktlRecord) recsIntervention.objectAtIndex(i);
				interventionBus().updateIntervention(
						ui, localTransId, recIntervention.numberForKey("intOrdre"), null, null, null, null, motsCles, actOrdreFinal, null, null, null);
			}

			message.append(recsIntervention.count() + " DT déplacées ...\n");

			// on deplace toutes les activites filles (les activites dt associées sont
			// mises à jour automatiquement)
			// busListenerMessage("Déplacement des activités filles ...", true);
			message.append("Déplacement des activités filles ...\n");

			NSArray recsFilles = recActiviteToMerge.arrayForKey("tosActFils");
			for (int i = 0; i < recsFilles.count(); i++) {
				CktlRecord recFille = (CktlRecord) recsFilles.objectAtIndex(i);
				moveActivite(localTransId, recFille.numberForKey("actOrdre"), actOrdreFinal);
			}
			message.append(recsFilles.count() + " activités déplacées ...\n");

			// on supprime l'activite
			// busListenerMessage("Suppression de l'activité initiale...", true);
			message.append("Suppression de l'activité initiale ...\n");
			deleteActivite(localTransId, actOrdreToMerge);

			// sauvegarde
			// busListenerMessage("Sauvegarde des données...", true);
			// message.append("Sauvegarde des données...<br/>");
			if (localTransId == null || commitECTransaction(localTransId)) {
				// busListenerMessage("Fusion des activités terminée", false);
				message.append("Fusion des activités terminée");
				// TODO quand la progression sera visible par l'utilisateur (ajax ou
				// autre), decommenter
				// les appels à busListenerMessage() et virer la ligne ci dessous et la
				// variable message
				busListenerMessage(message.toString(), false);
				return true;
			}
		} catch (Throwable ex) {
			throwError(ex);
		}
		return false;

	}

	/**
	 * Indique si une activite est "parente", i.e. elle est dans les
	 * sous-activites directes ou indirects de cette derniere.
	 * 
	 * @param actPere
	 * @param actFille
	 * @return
	 */
	private boolean isActiviteParent(Number actPere, Number actFille) {
		try {
			CktlRecord recActiviteFille = findVActivite(actFille);

			CktlRecord rec = recActiviteFille;
			while (rec != null && rec.intForKey("actPere") != 0 && rec.intForKey("actOrdre") != rec.intForKeyPath("actPere")) {
				if (rec.intForKey("actOrdre") == actPere.intValue()) {
					return true;
				}
				rec = rec.recForKey("toActPere");
			}

			return false;

		} catch (Throwable ex) {
			throwError(ex);
			return false;
		}

	}

	// nombre max de favoris par categorie (demandeur / intervenant)
	private final static int ACTIVITES_FAVORITES_MAX_RESULT = 5;
	// pour ne pas avoir un acces base trop long
	private final static int ACTIVITES_FAVORITES_FETCH_LIMITE = 200;

	/**
	 * Donne la liste des activités favorites pour un utilisateur. On fera
	 * apparaitre parmi cette liste : - les 5 dernières activités utilisées en
	 * création de DT - les 5 activités les plus courantes des DTs surlesquelles
	 * l'agent est affecté parmi les 50 dernières DTs.
	 * 
	 * La selection se fait selon le temoin asDemandeur
	 * 
	 * @return
	 */
	public NSArray<EOActivites> getActivitesFavorites(
			Integer noIndividu,
			String cStructure,
			boolean asDemandeur,
			boolean asIntervenant,
			boolean showHiddenActivite,
			boolean showUnderscoredActivite) {

		NSArray<EOActivites> result = new NSArray<EOActivites>();

		if (asDemandeur) {

			// les 5 activités choisies en tant que demandeur
			String strQual = EOIntervention.INT_NO_IND_APPELANT_KEY + "=%@ and " + EOIntervention.C_STRUCTURE_KEY + "=%@";
			if (!showHiddenActivite) {
				strQual += EOIntervention.COND_HIDE_ACTIVITE;
			}
			if (!showUnderscoredActivite) {
				strQual += EOIntervention.COND_HIDE_UNDERSCORE;
			}
			EOQualifier qual = CktlDataBus.newCondition(
					strQual,
					new NSArray<Object>(new Object[] { noIndividu, cStructure }));

			// on utilise les fetchspec pour limiter le nombre d'élements recherchés
			EOFetchSpecification myFetch =
					new EOFetchSpecification(
							EOIntervention.ENTITY_NAME,
							qual,
							CktlSort.newSort(EOIntervention.INT_DATE_CREATION_KEY, CktlSort.Descending));
			myFetch.setFetchLimit(ACTIVITES_FAVORITES_FETCH_LIMITE);

			NSArray<EOIntervention> dts = editingContext().objectsWithFetchSpecification(myFetch);

			NSMutableArray<EOActivites> activites = getActivitesFavoritesForInterventionList(dts, ACTIVITES_FAVORITES_MAX_RESULT);

			result = result.arrayByAddingObjectsFromArray(activites);
		}

		if (asIntervenant) {

			// les 5 activites les plus courantes parmi les dernières dts affectées en
			// date
			String strQual = EOIntervenant.NO_INDIVIDU_KEY + "=%@ and " +
					EOIntervenant.TO_INTERVENTION_KEY + "." + EOIntervention.C_STRUCTURE_KEY + "=%@";
			if (!showHiddenActivite) {
				strQual += EOIntervenant.COND_HIDE_ACTIVITE;
			}
			if (!showUnderscoredActivite) {
				strQual += EOIntervenant.COND_HIDE_UNDERSCORE;
			}

			EOQualifier qual = CktlDataBus.newCondition(
					strQual,
					new NSArray<Object>(new Object[] { noIndividu, cStructure }));

			// on utilise les fetchspec pour limiter le nombre d'élements recherchés
			EOFetchSpecification myFetch =
					new EOFetchSpecification(
							EOIntervenant.ENTITY_NAME,
							qual,
							CktlSort.newSort(
									EOIntervenant.TO_INTERVENTION_KEY + "." + EOIntervention.INT_DATE_CREATION_KEY, CktlSort.Descending));
			myFetch.setFetchLimit(ACTIVITES_FAVORITES_FETCH_LIMITE);

			NSArray<EOIntervenant> intervenants = editingContext().objectsWithFetchSpecification(myFetch);
			NSArray<EOIntervention> dts = (NSArray<EOIntervention>) intervenants.valueForKey(EOIntervenant.TO_INTERVENTION_KEY);

			NSMutableArray<EOActivites> activites = getActivitesFavoritesForInterventionList(dts, ACTIVITES_FAVORITES_MAX_RESULT);

			result = result.arrayByAddingObjectsFromArray(activites);
		}

		return result;
	}

	/**
	 * Donne la liste des 5 {@link EOActivites} apparaissant le plus de fois parmi
	 * une liste d'enregistrements.
	 * 
	 * @param interventionList
	 * @param maxResult
	 * @return
	 */
	private NSMutableArray<EOActivites> getActivitesFavoritesForInterventionList(
			NSArray<EOIntervention> interventionList, int maxResult) {

		// ne prendre que le top 5
		NSMutableDictionary<String, Integer> dicoAll = new NSMutableDictionary<String, Integer>();
		for (EOIntervention dt : interventionList) {
			String strActOrdre = Integer.toString(dt.actOrdre().intValue());
			Integer count = dicoAll.objectForKey(strActOrdre);
			if (count != null) {
				count = new Integer(count.intValue() + 1);
			} else {
				count = new Integer(1);
			}
			dicoAll.setObjectForKey(count, strActOrdre);
		}

		// ne prendre que maxResult
		NSMutableDictionary<String, Integer> dicoMaxResult = new NSMutableDictionary<String, Integer>();

		NSArray<String> allKeys = dicoAll.allKeys();

		if (allKeys.count() > 0) {

			NSMutableArray<EOActivites> activites = new NSMutableArray<EOActivites>();
			int i = 0;
			while (activites.count() < maxResult && i < allKeys.count() - 1) {
				String key = allKeys.objectAtIndex(i);
				Integer count = dicoAll.objectForKey(key);
				int nbSup = 0;
				for (String otherKey : allKeys) {
					if (!otherKey.equals(key)) {
						Integer otherCount = dicoAll.objectForKey(otherKey);
						if (count.intValue() < otherCount.intValue()) {
							nbSup++;
						}
					}
				}
				if (nbSup < maxResult) {
					dicoMaxResult.setObjectForKey(count, key);
				}
				i++;
			}
		}

		NSMutableArray<EOActivites> activites = new NSMutableArray<EOActivites>();

		NSArray<String> maxResultKeys = dicoMaxResult.allKeys();

		for (int i = 0; i < maxResultKeys.count(); i++) {
			Integer actOrdre = new Integer(Integer.parseInt(maxResultKeys.objectAtIndex(i)));
			activites.addObject(
					(EOActivites) findActivite(actOrdre, null));
		}

		return activites;

	}

}
