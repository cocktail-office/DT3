// _EOIntervention.java
/*
 * Copyright COCKTAIL (www.cocktail.org), 2001, 2010 
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

// DO NOT EDIT.  Make changes to EOIntervention.java instead.
package org.cocktail.dt.server.metier;

import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.NoSuchElementException;

@SuppressWarnings("all")
public abstract class _EOIntervention extends  CktlRecord {
	public static final String ENTITY_NAME = "Intervention";
	public static final String ENTITY_TABLE_NAME = "INTERVENTION";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "intOrdre";

	public static final String ACT_ORDRE_KEY = "actOrdre";
	public static final String C_LOCAL_KEY = "cLocal";
	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String CT_ORDRE_KEY = "ctOrdre";
	public static final String DST_CODE_KEY = "dstCode";
	public static final String INT_CLE_SERVICE_KEY = "intCleService";
	public static final String INT_COMMENTAIRE_INTERNE_KEY = "intCommentaireInterne";
	public static final String INT_DATE_BUTOIR_KEY = "intDateButoir";
	public static final String INT_DATE_CREATION_KEY = "intDateCreation";
	public static final String INT_DATE_SOUHAITE_KEY = "intDateSouhaite";
	public static final String INT_ETAT_KEY = "intEtat";
	public static final String INT_MASQUEE_LISTE_KEY = "intMasqueeListe";
	public static final String INT_MOTIF_KEY = "intMotif";
	public static final String INT_MOTS_CLEFS_KEY = "intMotsClefs";
	public static final String INT_NO_IND_APPELANT_KEY = "intNoIndAppelant";
	public static final String INT_NO_IND_CONCERNE_KEY = "intNoIndConcerne";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String INT_PRIORITE_KEY = "intPriorite";
	public static final String INT_SERVICE_CONCERNE_KEY = "intServiceConcerne";
	public static final String MOD_CODE_KEY = "modCode";
	public static final String NOM_PRENOM_CONCERNE_KEY = "nomPrenomConcerne";

	// Non visible attributes

	// Colkeys
	public static final String ACT_ORDRE_COLKEY = "ACT_ORDRE";
	public static final String C_LOCAL_COLKEY = "C_LOCAL";
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String CT_ORDRE_COLKEY = "CT_ORDRE";
	public static final String DST_CODE_COLKEY = "DST_CODE";
	public static final String INT_CLE_SERVICE_COLKEY = "INT_CLE_SERVICE";
	public static final String INT_COMMENTAIRE_INTERNE_COLKEY = "INT_COMMENTAIRE_INTERNE";
	public static final String INT_DATE_BUTOIR_COLKEY = "INT_DATE_BUTOIR";
	public static final String INT_DATE_CREATION_COLKEY = "INT_DATE_CREATION";
	public static final String INT_DATE_SOUHAITE_COLKEY = "INT_DATE_SOUHAITE";
	public static final String INT_ETAT_COLKEY = "INT_ETAT";
	public static final String INT_MASQUEE_LISTE_COLKEY = "intMasqueeListe";
	public static final String INT_MOTIF_COLKEY = "INT_MOTIF";
	public static final String INT_MOTS_CLEFS_COLKEY = "INT_MOTS_CLEFS";
	public static final String INT_NO_IND_APPELANT_COLKEY = "INT_NO_IND_APPELANT";
	public static final String INT_NO_IND_CONCERNE_COLKEY = "INT_NO_IND_CONCERNE";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String INT_PRIORITE_COLKEY = "INT_PRIORITE";
	public static final String INT_SERVICE_CONCERNE_COLKEY = "INT_SERVICE_CONCERNE";
	public static final String MOD_CODE_COLKEY = "MOD_CODE";
	public static final String NOM_PRENOM_CONCERNE_COLKEY = "$attribute.columnName";

	// Non visible colkeys

	// Relationships
	public static final String TO_ACTIVITES_KEY = "toActivites";
	public static final String TO_BATIMENT_KEY = "toBatiment";
	public static final String TO_ETAT_KEY = "toEtat";
	public static final String TO_INDIVIDU_APPELANT_KEY = "toIndividuAppelant";
	public static final String TO_INDIVIDU_CONCERNE_KEY = "toIndividuConcerne";
	public static final String TOS_DISCUSSION_KEY = "tosDiscussion";
	public static final String TOS_DOCUMENT_DT_KEY = "tosDocumentDt";
	public static final String TOS_HISTORIQUE_MOTIF_KEY = "tosHistoriqueMotif";
	public static final String TOS_INTERVENANT_KEY = "tosIntervenant";
	public static final String TOS_INTERVENTION_INFIN_KEY = "tosInterventionInfin";
	public static final String TOS_INTERVENTION_MASQUEE_KEY = "tosInterventionMasquee";
	public static final String TOS_INTERVENTION_REPRO_KEY = "tosInterventionRepro";
	public static final String TOS_TRAITEMENT_KEY = "tosTraitement";
	public static final String TO_STRUCTURE_CONCERNE_KEY = "toStructureConcerne";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOIntervention with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param actOrdre
	 * @param intCleService
	 * @param intDateCreation
	 * @param intDateSouhaite
	 * @param intEtat
	 * @param intMotif
	 * @param intNoIndAppelant
	 * @param intNoIndConcerne
	 * @param intOrdre
	 * @param intPriorite
	 * @param modCode
	 * @param toActivites
	 * @return EOIntervention
	 */
	public static EOIntervention create(EOEditingContext editingContext, Integer actOrdre, Integer intCleService, NSTimestamp intDateCreation, NSTimestamp intDateSouhaite, String intEtat, String intMotif, Integer intNoIndAppelant, Integer intNoIndConcerne, Integer intOrdre, Integer intPriorite, String modCode, org.cocktail.dt.server.metier.EOActivites toActivites) {
		EOIntervention eo = (EOIntervention) createAndInsertInstance(editingContext);
		eo.setActOrdre(actOrdre);
		eo.setIntCleService(intCleService);
		eo.setIntDateCreation(intDateCreation);
		eo.setIntDateSouhaite(intDateSouhaite);
		eo.setIntEtat(intEtat);
		eo.setIntMotif(intMotif);
		eo.setIntNoIndAppelant(intNoIndAppelant);
		eo.setIntNoIndConcerne(intNoIndConcerne);
		eo.setIntOrdre(intOrdre);
		eo.setIntPriorite(intPriorite);
		eo.setModCode(modCode);
		eo.setToActivitesRelationship(toActivites);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOIntervention.
	 *
	 * @param editingContext
	 * @return EOIntervention
	 */
	public static EOIntervention create(EOEditingContext editingContext) {
		EOIntervention eo = (EOIntervention) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOIntervention localInstanceIn(EOEditingContext editingContext) {
		EOIntervention localInstance = (EOIntervention) localInstanceOfObject(editingContext, (EOIntervention) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOIntervention localInstanceIn(EOEditingContext editingContext, EOIntervention eo) {
		EOIntervention localInstance = (eo == null) ? null : (EOIntervention) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer actOrdre() {
		return (Integer) storedValueForKey("actOrdre");
	}

	public void setActOrdre(Integer value) {
		takeStoredValueForKey(value, "actOrdre");
	}
	public String cLocal() {
		return (String) storedValueForKey("cLocal");
	}

	public void setCLocal(String value) {
		takeStoredValueForKey(value, "cLocal");
	}
	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public Integer ctOrdre() {
		return (Integer) storedValueForKey("ctOrdre");
	}

	public void setCtOrdre(Integer value) {
		takeStoredValueForKey(value, "ctOrdre");
	}
	public String dstCode() {
		return (String) storedValueForKey("dstCode");
	}

	public void setDstCode(String value) {
		takeStoredValueForKey(value, "dstCode");
	}
	public Integer intCleService() {
		return (Integer) storedValueForKey("intCleService");
	}

	public void setIntCleService(Integer value) {
		takeStoredValueForKey(value, "intCleService");
	}
	public String intCommentaireInterne() {
		return (String) storedValueForKey("intCommentaireInterne");
	}

	public void setIntCommentaireInterne(String value) {
		takeStoredValueForKey(value, "intCommentaireInterne");
	}
	public NSTimestamp intDateButoir() {
		return (NSTimestamp) storedValueForKey("intDateButoir");
	}

	public void setIntDateButoir(NSTimestamp value) {
		takeStoredValueForKey(value, "intDateButoir");
	}
	public NSTimestamp intDateCreation() {
		return (NSTimestamp) storedValueForKey("intDateCreation");
	}

	public void setIntDateCreation(NSTimestamp value) {
		takeStoredValueForKey(value, "intDateCreation");
	}
	public NSTimestamp intDateSouhaite() {
		return (NSTimestamp) storedValueForKey("intDateSouhaite");
	}

	public void setIntDateSouhaite(NSTimestamp value) {
		takeStoredValueForKey(value, "intDateSouhaite");
	}
	public String intEtat() {
		return (String) storedValueForKey("intEtat");
	}

	public void setIntEtat(String value) {
		takeStoredValueForKey(value, "intEtat");
	}
	public String intMasqueeListe() {
		return (String) storedValueForKey("intMasqueeListe");
	}

	public void setIntMasqueeListe(String value) {
		takeStoredValueForKey(value, "intMasqueeListe");
	}
	public String intMotif() {
		return (String) storedValueForKey("intMotif");
	}

	public void setIntMotif(String value) {
		takeStoredValueForKey(value, "intMotif");
	}
	public String intMotsClefs() {
		return (String) storedValueForKey("intMotsClefs");
	}

	public void setIntMotsClefs(String value) {
		takeStoredValueForKey(value, "intMotsClefs");
	}
	public Integer intNoIndAppelant() {
		return (Integer) storedValueForKey("intNoIndAppelant");
	}

	public void setIntNoIndAppelant(Integer value) {
		takeStoredValueForKey(value, "intNoIndAppelant");
	}
	public Integer intNoIndConcerne() {
		return (Integer) storedValueForKey("intNoIndConcerne");
	}

	public void setIntNoIndConcerne(Integer value) {
		takeStoredValueForKey(value, "intNoIndConcerne");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public Integer intPriorite() {
		return (Integer) storedValueForKey("intPriorite");
	}

	public void setIntPriorite(Integer value) {
		takeStoredValueForKey(value, "intPriorite");
	}
	public String intServiceConcerne() {
		return (String) storedValueForKey("intServiceConcerne");
	}

	public void setIntServiceConcerne(String value) {
		takeStoredValueForKey(value, "intServiceConcerne");
	}
	public String modCode() {
		return (String) storedValueForKey("modCode");
	}

	public void setModCode(String value) {
		takeStoredValueForKey(value, "modCode");
	}
	public String nomPrenomConcerne() {
		return (String) storedValueForKey("nomPrenomConcerne");
	}

	public void setNomPrenomConcerne(String value) {
		takeStoredValueForKey(value, "nomPrenomConcerne");
	}

	public org.cocktail.dt.server.metier.EOActivites toActivites() {
		return (org.cocktail.dt.server.metier.EOActivites)storedValueForKey("toActivites");
	}

	public void setToActivitesRelationship(org.cocktail.dt.server.metier.EOActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOActivites oldValue = toActivites();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toActivites");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toActivites");
		}
	}
  
	public org.cocktail.dt.server.metier.EOBatiment toBatiment() {
		return (org.cocktail.dt.server.metier.EOBatiment)storedValueForKey("toBatiment");
	}

	public void setToBatimentRelationship(org.cocktail.dt.server.metier.EOBatiment value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOBatiment oldValue = toBatiment();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toBatiment");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toBatiment");
		}
	}
  
	public org.cocktail.dt.server.metier.EOEtatDt toEtat() {
		return (org.cocktail.dt.server.metier.EOEtatDt)storedValueForKey("toEtat");
	}

	public void setToEtatRelationship(org.cocktail.dt.server.metier.EOEtatDt value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOEtatDt oldValue = toEtat();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toEtat");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toEtat");
		}
	}
  
	public org.cocktail.dt.server.metier.EOIndividu toIndividuAppelant() {
		return (org.cocktail.dt.server.metier.EOIndividu)storedValueForKey("toIndividuAppelant");
	}

	public void setToIndividuAppelantRelationship(org.cocktail.dt.server.metier.EOIndividu value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIndividu oldValue = toIndividuAppelant();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIndividuAppelant");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIndividuAppelant");
		}
	}
  
	public org.cocktail.dt.server.metier.EOIndividu toIndividuConcerne() {
		return (org.cocktail.dt.server.metier.EOIndividu)storedValueForKey("toIndividuConcerne");
	}

	public void setToIndividuConcerneRelationship(org.cocktail.dt.server.metier.EOIndividu value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIndividu oldValue = toIndividuConcerne();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIndividuConcerne");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIndividuConcerne");
		}
	}
  
	public org.cocktail.dt.server.metier.EOStructure toStructureConcerne() {
		return (org.cocktail.dt.server.metier.EOStructure)storedValueForKey("toStructureConcerne");
	}

	public void setToStructureConcerneRelationship(org.cocktail.dt.server.metier.EOStructure value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOStructure oldValue = toStructureConcerne();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toStructureConcerne");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toStructureConcerne");
		}
	}
  
	public NSArray tosDiscussion() {
		return (NSArray)storedValueForKey("tosDiscussion");
	}

	public NSArray tosDiscussion(EOQualifier qualifier) {
		return tosDiscussion(qualifier, null, false);
	}

	public NSArray tosDiscussion(EOQualifier qualifier, boolean fetch) {
		return tosDiscussion(qualifier, null, fetch);
	}

	public NSArray tosDiscussion(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EODiscussion.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EODiscussion.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosDiscussion();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosDiscussionRelationship(org.cocktail.dt.server.metier.EODiscussion object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosDiscussion");
	}

	public void removeFromTosDiscussionRelationship(org.cocktail.dt.server.metier.EODiscussion object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDiscussion");
	}

	public org.cocktail.dt.server.metier.EODiscussion createTosDiscussionRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Discussion");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosDiscussion");
		return (org.cocktail.dt.server.metier.EODiscussion) eo;
	}

	public void deleteTosDiscussionRelationship(org.cocktail.dt.server.metier.EODiscussion object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDiscussion");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosDiscussionRelationships() {
		Enumeration objects = tosDiscussion().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosDiscussionRelationship((org.cocktail.dt.server.metier.EODiscussion)objects.nextElement());
		}
	}
	public NSArray tosDocumentDt() {
		return (NSArray)storedValueForKey("tosDocumentDt");
	}

	public NSArray tosDocumentDt(EOQualifier qualifier) {
		return tosDocumentDt(qualifier, null, false);
	}

	public NSArray tosDocumentDt(EOQualifier qualifier, boolean fetch) {
		return tosDocumentDt(qualifier, null, fetch);
	}

	public NSArray tosDocumentDt(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EODocumentDt.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EODocumentDt.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosDocumentDt();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
	}

	public void removeFromTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
	}

	public org.cocktail.dt.server.metier.EODocumentDt createTosDocumentDtRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DocumentDt");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosDocumentDt");
		return (org.cocktail.dt.server.metier.EODocumentDt) eo;
	}

	public void deleteTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosDocumentDtRelationships() {
		Enumeration objects = tosDocumentDt().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosDocumentDtRelationship((org.cocktail.dt.server.metier.EODocumentDt)objects.nextElement());
		}
	}
	public NSArray tosHistoriqueMotif() {
		return (NSArray)storedValueForKey("tosHistoriqueMotif");
	}

	public NSArray tosHistoriqueMotif(EOQualifier qualifier) {
		return tosHistoriqueMotif(qualifier, null, false);
	}

	public NSArray tosHistoriqueMotif(EOQualifier qualifier, boolean fetch) {
		return tosHistoriqueMotif(qualifier, null, fetch);
	}

	public NSArray tosHistoriqueMotif(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOHistoriqueMotif.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOHistoriqueMotif.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosHistoriqueMotif();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosHistoriqueMotifRelationship(org.cocktail.dt.server.metier.EOHistoriqueMotif object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosHistoriqueMotif");
	}

	public void removeFromTosHistoriqueMotifRelationship(org.cocktail.dt.server.metier.EOHistoriqueMotif object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosHistoriqueMotif");
	}

	public org.cocktail.dt.server.metier.EOHistoriqueMotif createTosHistoriqueMotifRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("HistoriqueMotif");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosHistoriqueMotif");
		return (org.cocktail.dt.server.metier.EOHistoriqueMotif) eo;
	}

	public void deleteTosHistoriqueMotifRelationship(org.cocktail.dt.server.metier.EOHistoriqueMotif object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosHistoriqueMotif");
			}

	public void deleteAllTosHistoriqueMotifRelationships() {
		Enumeration objects = tosHistoriqueMotif().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosHistoriqueMotifRelationship((org.cocktail.dt.server.metier.EOHistoriqueMotif)objects.nextElement());
		}
	}
	public NSArray tosIntervenant() {
		return (NSArray)storedValueForKey("tosIntervenant");
	}

	public NSArray tosIntervenant(EOQualifier qualifier) {
		return tosIntervenant(qualifier, null, false);
	}

	public NSArray tosIntervenant(EOQualifier qualifier, boolean fetch) {
		return tosIntervenant(qualifier, null, fetch);
	}

	public NSArray tosIntervenant(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOIntervenant.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOIntervenant.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosIntervenant();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosIntervenant");
	}

	public void removeFromTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervenant");
	}

	public org.cocktail.dt.server.metier.EOIntervenant createTosIntervenantRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Intervenant");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosIntervenant");
		return (org.cocktail.dt.server.metier.EOIntervenant) eo;
	}

	public void deleteTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervenant");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosIntervenantRelationships() {
		Enumeration objects = tosIntervenant().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosIntervenantRelationship((org.cocktail.dt.server.metier.EOIntervenant)objects.nextElement());
		}
	}
	public NSArray tosInterventionInfin() {
		return (NSArray)storedValueForKey("tosInterventionInfin");
	}

	public NSArray tosInterventionInfin(EOQualifier qualifier) {
		return tosInterventionInfin(qualifier, null, false);
	}

	public NSArray tosInterventionInfin(EOQualifier qualifier, boolean fetch) {
		return tosInterventionInfin(qualifier, null, fetch);
	}

	public NSArray tosInterventionInfin(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOInterventionInfin.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOInterventionInfin.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosInterventionInfin();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosInterventionInfinRelationship(org.cocktail.dt.server.metier.EOInterventionInfin object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosInterventionInfin");
	}

	public void removeFromTosInterventionInfinRelationship(org.cocktail.dt.server.metier.EOInterventionInfin object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionInfin");
	}

	public org.cocktail.dt.server.metier.EOInterventionInfin createTosInterventionInfinRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("InterventionInfin");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosInterventionInfin");
		return (org.cocktail.dt.server.metier.EOInterventionInfin) eo;
	}

	public void deleteTosInterventionInfinRelationship(org.cocktail.dt.server.metier.EOInterventionInfin object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionInfin");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosInterventionInfinRelationships() {
		Enumeration objects = tosInterventionInfin().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosInterventionInfinRelationship((org.cocktail.dt.server.metier.EOInterventionInfin)objects.nextElement());
		}
	}
	public NSArray tosInterventionMasquee() {
		return (NSArray)storedValueForKey("tosInterventionMasquee");
	}

	public NSArray tosInterventionMasquee(EOQualifier qualifier) {
		return tosInterventionMasquee(qualifier, null, false);
	}

	public NSArray tosInterventionMasquee(EOQualifier qualifier, boolean fetch) {
		return tosInterventionMasquee(qualifier, null, fetch);
	}

	public NSArray tosInterventionMasquee(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOInterventionMasquee.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOInterventionMasquee.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosInterventionMasquee();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosInterventionMasqueeRelationship(org.cocktail.dt.server.metier.EOInterventionMasquee object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosInterventionMasquee");
	}

	public void removeFromTosInterventionMasqueeRelationship(org.cocktail.dt.server.metier.EOInterventionMasquee object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionMasquee");
	}

	public org.cocktail.dt.server.metier.EOInterventionMasquee createTosInterventionMasqueeRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("InterventionMasquee");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosInterventionMasquee");
		return (org.cocktail.dt.server.metier.EOInterventionMasquee) eo;
	}

	public void deleteTosInterventionMasqueeRelationship(org.cocktail.dt.server.metier.EOInterventionMasquee object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionMasquee");
			}

	public void deleteAllTosInterventionMasqueeRelationships() {
		Enumeration objects = tosInterventionMasquee().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosInterventionMasqueeRelationship((org.cocktail.dt.server.metier.EOInterventionMasquee)objects.nextElement());
		}
	}
	public NSArray tosInterventionRepro() {
		return (NSArray)storedValueForKey("tosInterventionRepro");
	}

	public NSArray tosInterventionRepro(EOQualifier qualifier) {
		return tosInterventionRepro(qualifier, null, false);
	}

	public NSArray tosInterventionRepro(EOQualifier qualifier, boolean fetch) {
		return tosInterventionRepro(qualifier, null, fetch);
	}

	public NSArray tosInterventionRepro(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOInterventionRepro.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOInterventionRepro.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosInterventionRepro();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosInterventionReproRelationship(org.cocktail.dt.server.metier.EOInterventionRepro object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosInterventionRepro");
	}

	public void removeFromTosInterventionReproRelationship(org.cocktail.dt.server.metier.EOInterventionRepro object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionRepro");
	}

	public org.cocktail.dt.server.metier.EOInterventionRepro createTosInterventionReproRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("InterventionRepro");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosInterventionRepro");
		return (org.cocktail.dt.server.metier.EOInterventionRepro) eo;
	}

	public void deleteTosInterventionReproRelationship(org.cocktail.dt.server.metier.EOInterventionRepro object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionRepro");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosInterventionReproRelationships() {
		Enumeration objects = tosInterventionRepro().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosInterventionReproRelationship((org.cocktail.dt.server.metier.EOInterventionRepro)objects.nextElement());
		}
	}
	public NSArray tosTraitement() {
		return (NSArray)storedValueForKey("tosTraitement");
	}

	public NSArray tosTraitement(EOQualifier qualifier) {
		return tosTraitement(qualifier, null, false);
	}

	public NSArray tosTraitement(EOQualifier qualifier, boolean fetch) {
		return tosTraitement(qualifier, null, fetch);
	}

	public NSArray tosTraitement(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOTraitement.TO_INTERVENTION_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOTraitement.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosTraitement();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosTraitementRelationship(org.cocktail.dt.server.metier.EOTraitement object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosTraitement");
	}

	public void removeFromTosTraitementRelationship(org.cocktail.dt.server.metier.EOTraitement object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosTraitement");
	}

	public org.cocktail.dt.server.metier.EOTraitement createTosTraitementRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Traitement");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosTraitement");
		return (org.cocktail.dt.server.metier.EOTraitement) eo;
	}

	public void deleteTosTraitementRelationship(org.cocktail.dt.server.metier.EOTraitement object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosTraitement");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosTraitementRelationships() {
		Enumeration objects = tosTraitement().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosTraitementRelationship((org.cocktail.dt.server.metier.EOTraitement)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOIntervention.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOIntervention.fetchAll(editingContext, null, sortOrderings);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier) {
		return fetchAll(editingContext, qualifier, null, false);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		return fetchAll(editingContext, qualifier, sortOrderings, false);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings, int fetchLimit) {
		return fetchAll(editingContext, qualifier, sortOrderings, false, fetchLimit);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, String keyName, Object value, NSArray sortOrderings) {
		return fetchAll(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value), sortOrderings, false);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings, boolean distinct) {
		return fetchAll(editingContext, qualifier, sortOrderings, distinct, 0);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings, boolean distinct, int fetchLimit) {
		return fetchAll(editingContext, qualifier, sortOrderings, distinct, fetchLimit, null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings, int fetchLimit, DTEOEditingContextHandler handler) {
		return fetchAll(editingContext, qualifier, sortOrderings, false, fetchLimit, handler);
	}
	
	public static NSArray fetchAll(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings, boolean distinct, int fetchLimit, DTEOEditingContextHandler handler) {
		EOFetchSpecification fetchSpec = new EOFetchSpecification(ENTITY_NAME, qualifier, sortOrderings);
		fetchSpec.setIsDeep(true);
		fetchSpec.setUsesDistinct(distinct);
		fetchSpec.setFetchLimit(fetchLimit);
		if (handler != null) {
			fetchSpec.setPromptsAfterFetchLimit(true);
			editingContext.setMessageHandler(handler);
		}
		return (NSArray) editingContext.objectsWithFetchSpecification(fetchSpec);
	}
	

	// Fetching one (returns EOIntervention)
	
	/**
	 * Renvoie un objet simple.
	 * Pour recuperer un tableau, utiliser fetchAll(EOEditingContext, String, Object, NSArray).
	 * Si plusieurs objets sont susceptibles d'etre trouves, utiliser fetchFirstByKeyValue(EOEditingContext, String, Object).
	 * Une exception est declenchee si plusieurs objets sont trouves.
	 * 
	 * @param editingContext
	 * @param keyName
	 * @param value
	 * @return Renvoie l'objet correspondant a la paire cle/valeur
	 * @throws IllegalStateException  
	 */
	public static EOIntervention fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
		return fetchByQualifier(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
	}

	/**
	 * Renvoie l'objet correspondant au qualifier.
	 * Si plusieurs objets sont susceptibles d'etre trouves, utiliser fetchFirstByQualifier(EOEditingContext, EOQualifier).
	 * Une exception est declenchee si plusieurs objets sont trouves.
	 * 
	 * @param editingContext
	 * @param qualifier
	 * @return L'objet qui correspond au qualifier passé en parametre. Si plusieurs objets sont trouves, une exception est declenchee.
	 * 		   Si aucun objet n'est trouve, null est renvoye.
	 * @throws IllegalStateException
	 */
	public static EOIntervention fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOIntervention eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOIntervention)eoObjects.objectAtIndex(0);
		}
		else {
			throw new IllegalStateException("Il y a plus d'un objet qui correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}

	/**
	 * Renvoie le premier objet simple trouve.
	 * Pour recuperer un tableau, utiliser fetchAll(EOEditingContext, String, Object, NSArray).
	 * 
	 * @param editingContext
	 * @param keyName
	 * @param value
	 * @return Renvoie le premier objet trouve correspondant a la paire cle/valeur, null si aucun trouve
	 */
	public static EOIntervention fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
		return fetchFirstByQualifier(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value), null);
	}

	/**
	 * Renvoie le premier objet simple trouve.
	 * Pour recuperer un tableau, utiliser fetchAll(EOEditingContext, EOQualifier).
	 * 
	 * @param editingContext
	 * @param qualifier
	 * @return Renvoie le premier objet trouve correspondant au qualifier, null si aucun trouve
	 */
	public static EOIntervention fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
		return fetchFirstByQualifier(editingContext, qualifier, null);
	}

	/**
	 * Renvoie le premier objet simple trouve dans la liste triee.
	 * Pour recuperer un tableau, utiliser fetchAll(EOEditingContext, EOQualifier, NSArray).
	 * 
	 * @param editingContext
	 * @param qualifier
	 * @param sortOrderings
	 * @return Renvoie le premier objet trouve correspondant au qualifier, null si aucun trouve
	 */
	public static EOIntervention fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOIntervention eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOIntervention)eoObjects.objectAtIndex(0);
		}
		return eoObject;
	}  

	/**
	 * Renvoie le premier objet simple obligatoirement trouve.
	 * 
	 * @param editingContext
	 * @param keyName
	 * @param value
	 * @return Renvoie le premier objet trouve correspondant a la paire cle/valeur, une exception si aucun trouve.
	 * Pour ne pas avoir d'exception, utiliser fetchFirstByKeyValue(EOEditingContext, String, Object)
	 * @throws NoSuchElementException Si aucun objet trouve
	 */
	public static EOIntervention fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
		return fetchFirstRequiredByQualifier(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
	}

	/**
	 * Renvoie le premier objet simple obligatoirement trouve.
	 * 
	 * @param editingContext
	 * @param qualifier
	 * @return Renvoie le premier objet trouve correspondant au qualifier, une exception si aucun trouve.
	 * Pour ne pas avoir d'exception, utiliser fetchFirstByQualifier(EOEditingContext, EOQualifier)
	 * @throws NoSuchElementException Si aucun objet trouve
	 */
	public static EOIntervention fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOIntervention eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOIntervention ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	
	public static NSArray fetchFetchEnCoursIntervenant(EOEditingContext editingContext, NSDictionary bindings) {
		EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("FetchEnCoursIntervenant", "Intervention");
		fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
		return (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
	}


	// Internal utilities methods for common use (server AND client)...

	private static EOIntervention createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOIntervention.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOIntervention.ENTITY_NAME + "' !");
		}
		else {
			EOIntervention object = (EOIntervention) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOIntervention localInstanceOfObject(EOEditingContext ec, EOIntervention object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOIntervention " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOIntervention) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
