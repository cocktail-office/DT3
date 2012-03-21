// _EOActivites.java
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

// DO NOT EDIT.  Make changes to EOActivites.java instead.
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
public abstract class _EOActivites extends  CktlRecord {
	public static final String ENTITY_NAME = "Activites";
	public static final String ENTITY_TABLE_NAME = "ACTIVITES";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "actOrdre";

	public static final String ACT_AFFICHABLE_KEY = "actAffichable";
	public static final String ACT_CREER_MAIL_KEY = "actCreerMail";
	public static final String ACT_LIBELLE_KEY = "actLibelle";
	public static final String ACT_MAIL_SERVICE_KEY = "actMailService";
	public static final String ACT_ORDRE_KEY = "actOrdre";
	public static final String ACT_PERE_KEY = "actPere";
	public static final String ACT_PREF_KEY = "actPref";
	public static final String ACT_RESP_KEY = "actResp";
	public static final String ACT_SWAP_MESSAGE_KEY = "actSwapMessage";
	public static final String ACT_SWAP_VIEW_KEY = "actSwapView";
	public static final String CART_ORDRE_KEY = "cartOrdre";
	public static final String C_STRUCTURE_KEY = "cStructure";

	// Non visible attributes

	// Colkeys
	public static final String ACT_AFFICHABLE_COLKEY = "ACT_AFFICHABLE";
	public static final String ACT_CREER_MAIL_COLKEY = "ACT_CREER_MAIL";
	public static final String ACT_LIBELLE_COLKEY = "ACT_LIBELLE";
	public static final String ACT_MAIL_SERVICE_COLKEY = "ACT_MAIL_SERVICE";
	public static final String ACT_ORDRE_COLKEY = "ACT_ORDRE";
	public static final String ACT_PERE_COLKEY = "ACT_PERE";
	public static final String ACT_PREF_COLKEY = "ACT_PREF";
	public static final String ACT_RESP_COLKEY = "ACT_RESP";
	public static final String ACT_SWAP_MESSAGE_COLKEY = "ACT_SWAP_MESSAGE";
	public static final String ACT_SWAP_VIEW_COLKEY = "ACT_SWAP_VIEW";
	public static final String CART_ORDRE_COLKEY = "CART_ORDRE";
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";

	// Non visible colkeys

	// Relationships
	public static final String TO_ACT_PERE_KEY = "toActPere";
	public static final String TO_ACT_PREF_KEY = "toActPref";
	public static final String TO_ACT_RESP_KEY = "toActResp";
	public static final String TO_GROUPES_DT_KEY = "toGroupesDt";
	public static final String TOS_ACT_FILS_KEY = "tosActFils";
	public static final String TOS_ACT_RESPONSABLES_KEY = "tosActResponsables";
	public static final String TOS_INTERVENTION_KEY = "tosIntervention";
	public static final String TO_V_ACTIVITES_KEY = "toVActivites";
	public static final String TO_V_ACT_PREF_KEY = "toVActPref";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOActivites with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param actAffichable
	 * @param actLibelle
	 * @param actOrdre
	 * @param actPere
	 * @param actPref
	 * @param actResp
	 * @param cStructure
	 * @return EOActivites
	 */
	public static EOActivites create(EOEditingContext editingContext, String actAffichable, String actLibelle, Integer actOrdre, Integer actPere, Integer actPref, Integer actResp, String cStructure) {
		EOActivites eo = (EOActivites) createAndInsertInstance(editingContext);
		eo.setActAffichable(actAffichable);
		eo.setActLibelle(actLibelle);
		eo.setActOrdre(actOrdre);
		eo.setActPere(actPere);
		eo.setActPref(actPref);
		eo.setActResp(actResp);
		eo.setCStructure(cStructure);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOActivites.
	 *
	 * @param editingContext
	 * @return EOActivites
	 */
	public static EOActivites create(EOEditingContext editingContext) {
		EOActivites eo = (EOActivites) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOActivites localInstanceIn(EOEditingContext editingContext) {
		EOActivites localInstance = (EOActivites) localInstanceOfObject(editingContext, (EOActivites) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOActivites localInstanceIn(EOEditingContext editingContext, EOActivites eo) {
		EOActivites localInstance = (eo == null) ? null : (EOActivites) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String actAffichable() {
		return (String) storedValueForKey("actAffichable");
	}

	public void setActAffichable(String value) {
		takeStoredValueForKey(value, "actAffichable");
	}
	public String actCreerMail() {
		return (String) storedValueForKey("actCreerMail");
	}

	public void setActCreerMail(String value) {
		takeStoredValueForKey(value, "actCreerMail");
	}
	public String actLibelle() {
		return (String) storedValueForKey("actLibelle");
	}

	public void setActLibelle(String value) {
		takeStoredValueForKey(value, "actLibelle");
	}
	public String actMailService() {
		return (String) storedValueForKey("actMailService");
	}

	public void setActMailService(String value) {
		takeStoredValueForKey(value, "actMailService");
	}
	public Integer actOrdre() {
		return (Integer) storedValueForKey("actOrdre");
	}

	public void setActOrdre(Integer value) {
		takeStoredValueForKey(value, "actOrdre");
	}
	public Integer actPere() {
		return (Integer) storedValueForKey("actPere");
	}

	public void setActPere(Integer value) {
		takeStoredValueForKey(value, "actPere");
	}
	public Integer actPref() {
		return (Integer) storedValueForKey("actPref");
	}

	public void setActPref(Integer value) {
		takeStoredValueForKey(value, "actPref");
	}
	public Integer actResp() {
		return (Integer) storedValueForKey("actResp");
	}

	public void setActResp(Integer value) {
		takeStoredValueForKey(value, "actResp");
	}
	public String actSwapMessage() {
		return (String) storedValueForKey("actSwapMessage");
	}

	public void setActSwapMessage(String value) {
		takeStoredValueForKey(value, "actSwapMessage");
	}
	public Integer actSwapView() {
		return (Integer) storedValueForKey("actSwapView");
	}

	public void setActSwapView(Integer value) {
		takeStoredValueForKey(value, "actSwapView");
	}
	public Integer cartOrdre() {
		return (Integer) storedValueForKey("cartOrdre");
	}

	public void setCartOrdre(Integer value) {
		takeStoredValueForKey(value, "cartOrdre");
	}
	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}

	public org.cocktail.dt.server.metier.EOActivites toActPere() {
		return (org.cocktail.dt.server.metier.EOActivites)storedValueForKey("toActPere");
	}

	public void setToActPereRelationship(org.cocktail.dt.server.metier.EOActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOActivites oldValue = toActPere();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toActPere");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toActPere");
		}
	}
  
	public org.cocktail.dt.server.metier.EOActivites toActPref() {
		return (org.cocktail.dt.server.metier.EOActivites)storedValueForKey("toActPref");
	}

	public void setToActPrefRelationship(org.cocktail.dt.server.metier.EOActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOActivites oldValue = toActPref();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toActPref");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toActPref");
		}
	}
  
	public org.cocktail.dt.server.metier.EOActivites toActResp() {
		return (org.cocktail.dt.server.metier.EOActivites)storedValueForKey("toActResp");
	}

	public void setToActRespRelationship(org.cocktail.dt.server.metier.EOActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOActivites oldValue = toActResp();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toActResp");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toActResp");
		}
	}
  
	public org.cocktail.dt.server.metier.EOGroupesDt toGroupesDt() {
		return (org.cocktail.dt.server.metier.EOGroupesDt)storedValueForKey("toGroupesDt");
	}

	public void setToGroupesDtRelationship(org.cocktail.dt.server.metier.EOGroupesDt value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOGroupesDt oldValue = toGroupesDt();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toGroupesDt");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toGroupesDt");
		}
	}
  
	public org.cocktail.dt.server.metier.EOVActivites toVActivites() {
		return (org.cocktail.dt.server.metier.EOVActivites)storedValueForKey("toVActivites");
	}

	public void setToVActivitesRelationship(org.cocktail.dt.server.metier.EOVActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOVActivites oldValue = toVActivites();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toVActivites");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toVActivites");
		}
	}
  
	public org.cocktail.dt.server.metier.EOVActivites toVActPref() {
		return (org.cocktail.dt.server.metier.EOVActivites)storedValueForKey("toVActPref");
	}

	public void setToVActPrefRelationship(org.cocktail.dt.server.metier.EOVActivites value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOVActivites oldValue = toVActPref();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toVActPref");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toVActPref");
		}
	}
  
	public NSArray tosActFils() {
		return (NSArray)storedValueForKey("tosActFils");
	}

	public NSArray tosActFils(EOQualifier qualifier) {
		return tosActFils(qualifier, null, false);
	}

	public NSArray tosActFils(EOQualifier qualifier, boolean fetch) {
		return tosActFils(qualifier, null, fetch);
	}

	public NSArray tosActFils(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOActivites.TO_ACT_PERE_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOActivites.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosActFils();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosActFilsRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosActFils");
	}

	public void removeFromTosActFilsRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActFils");
	}

	public org.cocktail.dt.server.metier.EOActivites createTosActFilsRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Activites");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosActFils");
		return (org.cocktail.dt.server.metier.EOActivites) eo;
	}

	public void deleteTosActFilsRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActFils");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosActFilsRelationships() {
		Enumeration objects = tosActFils().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosActFilsRelationship((org.cocktail.dt.server.metier.EOActivites)objects.nextElement());
		}
	}
	public NSArray tosActResponsables() {
		return (NSArray)storedValueForKey("tosActResponsables");
	}

	public NSArray tosActResponsables(EOQualifier qualifier) {
		return tosActResponsables(qualifier, null);
	}

	public NSArray tosActResponsables(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosActResponsables();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosActResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosActResponsables");
	}

	public void removeFromTosActResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActResponsables");
	}

	public org.cocktail.dt.server.metier.EOActivitesResponsables createTosActResponsablesRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ActivitesResponsables");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosActResponsables");
		return (org.cocktail.dt.server.metier.EOActivitesResponsables) eo;
	}

	public void deleteTosActResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActResponsables");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosActResponsablesRelationships() {
		Enumeration objects = tosActResponsables().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosActResponsablesRelationship((org.cocktail.dt.server.metier.EOActivitesResponsables)objects.nextElement());
		}
	}
	public NSArray tosIntervention() {
		return (NSArray)storedValueForKey("tosIntervention");
	}

	public NSArray tosIntervention(EOQualifier qualifier) {
		return tosIntervention(qualifier, null, false);
	}

	public NSArray tosIntervention(EOQualifier qualifier, boolean fetch) {
		return tosIntervention(qualifier, null, fetch);
	}

	public NSArray tosIntervention(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOIntervention.TO_ACTIVITES_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOIntervention.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosIntervention();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosInterventionRelationship(org.cocktail.dt.server.metier.EOIntervention object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosIntervention");
	}

	public void removeFromTosInterventionRelationship(org.cocktail.dt.server.metier.EOIntervention object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervention");
	}

	public org.cocktail.dt.server.metier.EOIntervention createTosInterventionRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Intervention");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosIntervention");
		return (org.cocktail.dt.server.metier.EOIntervention) eo;
	}

	public void deleteTosInterventionRelationship(org.cocktail.dt.server.metier.EOIntervention object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervention");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosInterventionRelationships() {
		Enumeration objects = tosIntervention().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosInterventionRelationship((org.cocktail.dt.server.metier.EOIntervention)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOActivites.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOActivites.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOActivites)
	
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
	public static EOActivites fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOActivites fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOActivites eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOActivites)eoObjects.objectAtIndex(0);
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
	public static EOActivites fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOActivites fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOActivites fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOActivites eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOActivites)eoObjects.objectAtIndex(0);
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
	public static EOActivites fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOActivites fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOActivites eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOActivites ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOActivites createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOActivites.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOActivites.ENTITY_NAME + "' !");
		}
		else {
			EOActivites object = (EOActivites) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOActivites localInstanceOfObject(EOEditingContext ec, EOActivites object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOActivites " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOActivites) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
