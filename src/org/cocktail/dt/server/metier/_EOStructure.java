// _EOStructure.java
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

// DO NOT EDIT.  Make changes to EOStructure.java instead.
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
public abstract class _EOStructure extends  CktlRecord {
	public static final String ENTITY_NAME = "DTStructureUlr";
	public static final String ENTITY_TABLE_NAME = "GRHUM.STRUCTURE_ULR";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cStructure";

	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String C_STRUCTURE_PERE_KEY = "cStructurePere";
	public static final String C_TYPE_STRUCTURE_KEY = "cTypeStructure";
	public static final String GRP_ACCES_KEY = "grpAcces";
	public static final String GRP_ALIAS_KEY = "grpAlias";
	public static final String GRP_OWNER_KEY = "grpOwner";
	public static final String GRP_RESPONSABLE_KEY = "grpResponsable";
	public static final String IS_GROUPES_DT_KEY = "isGroupesDT";
	public static final String LC_STRUCTURE_KEY = "lcStructure";
	public static final String LC_STRUCTURE_PERE_KEY = "lcStructurePere";
	public static final String LL_STRUCTURE_KEY = "llStructure";
	public static final String PERS_ID_KEY = "persId";

	// Non visible attributes

	// Colkeys
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String C_STRUCTURE_PERE_COLKEY = "C_STRUCTURE_PERE";
	public static final String C_TYPE_STRUCTURE_COLKEY = "C_TYPE_STRUCTURE";
	public static final String GRP_ACCES_COLKEY = "GRP_ACCES";
	public static final String GRP_ALIAS_COLKEY = "GRP_ALIAS";
	public static final String GRP_OWNER_COLKEY = "GRP_OWNER";
	public static final String GRP_RESPONSABLE_COLKEY = "GRP_RESPONSABLE";
	public static final String IS_GROUPES_DT_COLKEY = "$attribute.columnName";
	public static final String LC_STRUCTURE_COLKEY = "LC_STRUCTURE";
	public static final String LC_STRUCTURE_PERE_COLKEY = "$attribute.columnName";
	public static final String LL_STRUCTURE_COLKEY = "LL_STRUCTURE";
	public static final String PERS_ID_COLKEY = "PERS_ID";

	// Non visible colkeys

	// Relationships
	public static final String TO_GROUPES_DT_KEY = "toGroupesDt";
	public static final String TO_INDIVIDU_ULR_KEY = "toIndividuUlr";
	public static final String TO_INDIVIDU_ULR_RESPONSABLE_KEY = "toIndividuUlrResponsable";
	public static final String TOS_REPART_TYPE_GROUPE_KEY = "tosRepartTypeGroupe";
	public static final String TO_STRUCTURE_PERE_KEY = "toStructurePere";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOStructure with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cStructure
	 * @param cTypeStructure
	 * @param persId
	 * @return EOStructure
	 */
	public static EOStructure create(EOEditingContext editingContext, String cStructure, String cTypeStructure, Integer persId) {
		EOStructure eo = (EOStructure) createAndInsertInstance(editingContext);
		eo.setCStructure(cStructure);
		eo.setCTypeStructure(cTypeStructure);
		eo.setPersId(persId);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOStructure.
	 *
	 * @param editingContext
	 * @return EOStructure
	 */
	public static EOStructure create(EOEditingContext editingContext) {
		EOStructure eo = (EOStructure) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOStructure localInstanceIn(EOEditingContext editingContext) {
		EOStructure localInstance = (EOStructure) localInstanceOfObject(editingContext, (EOStructure) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOStructure localInstanceIn(EOEditingContext editingContext, EOStructure eo) {
		EOStructure localInstance = (eo == null) ? null : (EOStructure) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public String cStructurePere() {
		return (String) storedValueForKey("cStructurePere");
	}

	public void setCStructurePere(String value) {
		takeStoredValueForKey(value, "cStructurePere");
	}
	public String cTypeStructure() {
		return (String) storedValueForKey("cTypeStructure");
	}

	public void setCTypeStructure(String value) {
		takeStoredValueForKey(value, "cTypeStructure");
	}
	public String grpAcces() {
		return (String) storedValueForKey("grpAcces");
	}

	public void setGrpAcces(String value) {
		takeStoredValueForKey(value, "grpAcces");
	}
	public String grpAlias() {
		return (String) storedValueForKey("grpAlias");
	}

	public void setGrpAlias(String value) {
		takeStoredValueForKey(value, "grpAlias");
	}
	public Integer grpOwner() {
		return (Integer) storedValueForKey("grpOwner");
	}

	public void setGrpOwner(Integer value) {
		takeStoredValueForKey(value, "grpOwner");
	}
	public Integer grpResponsable() {
		return (Integer) storedValueForKey("grpResponsable");
	}

	public void setGrpResponsable(Integer value) {
		takeStoredValueForKey(value, "grpResponsable");
	}
	public Integer isGroupesDT() {
		return (Integer) storedValueForKey("isGroupesDT");
	}

	public void setIsGroupesDT(Integer value) {
		takeStoredValueForKey(value, "isGroupesDT");
	}
	public String lcStructure() {
		return (String) storedValueForKey("lcStructure");
	}

	public void setLcStructure(String value) {
		takeStoredValueForKey(value, "lcStructure");
	}
	public String lcStructurePere() {
		return (String) storedValueForKey("lcStructurePere");
	}

	public void setLcStructurePere(String value) {
		takeStoredValueForKey(value, "lcStructurePere");
	}
	public String llStructure() {
		return (String) storedValueForKey("llStructure");
	}

	public void setLlStructure(String value) {
		takeStoredValueForKey(value, "llStructure");
	}
	public Integer persId() {
		return (Integer) storedValueForKey("persId");
	}

	public void setPersId(Integer value) {
		takeStoredValueForKey(value, "persId");
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
  
	public org.cocktail.dt.server.metier.EOIndividu toIndividuUlr() {
		return (org.cocktail.dt.server.metier.EOIndividu)storedValueForKey("toIndividuUlr");
	}

	public void setToIndividuUlrRelationship(org.cocktail.dt.server.metier.EOIndividu value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIndividu oldValue = toIndividuUlr();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIndividuUlr");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIndividuUlr");
		}
	}
  
	public org.cocktail.dt.server.metier.EOIndividu toIndividuUlrResponsable() {
		return (org.cocktail.dt.server.metier.EOIndividu)storedValueForKey("toIndividuUlrResponsable");
	}

	public void setToIndividuUlrResponsableRelationship(org.cocktail.dt.server.metier.EOIndividu value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIndividu oldValue = toIndividuUlrResponsable();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIndividuUlrResponsable");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIndividuUlrResponsable");
		}
	}
  
	public org.cocktail.dt.server.metier.EOStructure toStructurePere() {
		return (org.cocktail.dt.server.metier.EOStructure)storedValueForKey("toStructurePere");
	}

	public void setToStructurePereRelationship(org.cocktail.dt.server.metier.EOStructure value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOStructure oldValue = toStructurePere();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toStructurePere");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toStructurePere");
		}
	}
  
	public NSArray tosRepartTypeGroupe() {
		return (NSArray)storedValueForKey("tosRepartTypeGroupe");
	}

	public NSArray tosRepartTypeGroupe(EOQualifier qualifier) {
		return tosRepartTypeGroupe(qualifier, null);
	}

	public NSArray tosRepartTypeGroupe(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosRepartTypeGroupe();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosRepartTypeGroupeRelationship(org.cocktail.dt.server.metier.EORepartTypeGroupe object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartTypeGroupe");
	}

	public void removeFromTosRepartTypeGroupeRelationship(org.cocktail.dt.server.metier.EORepartTypeGroupe object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartTypeGroupe");
	}

	public org.cocktail.dt.server.metier.EORepartTypeGroupe createTosRepartTypeGroupeRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("RepartTypeGroupe");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartTypeGroupe");
		return (org.cocktail.dt.server.metier.EORepartTypeGroupe) eo;
	}

	public void deleteTosRepartTypeGroupeRelationship(org.cocktail.dt.server.metier.EORepartTypeGroupe object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartTypeGroupe");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartTypeGroupeRelationships() {
		Enumeration objects = tosRepartTypeGroupe().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartTypeGroupeRelationship((org.cocktail.dt.server.metier.EORepartTypeGroupe)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOStructure.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOStructure.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOStructure)
	
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
	public static EOStructure fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOStructure fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOStructure eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOStructure)eoObjects.objectAtIndex(0);
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
	public static EOStructure fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOStructure fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOStructure fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOStructure eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOStructure)eoObjects.objectAtIndex(0);
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
	public static EOStructure fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOStructure fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOStructure eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOStructure ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	
	public static NSArray fetchFetchAffectation(EOEditingContext editingContext, NSDictionary bindings) {
		EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("fetchAffectation", "DTStructureUlr");
		fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
		return (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
	}


	// Internal utilities methods for common use (server AND client)...

	private static EOStructure createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOStructure.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOStructure.ENTITY_NAME + "' !");
		}
		else {
			EOStructure object = (EOStructure) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOStructure localInstanceOfObject(EOEditingContext ec, EOStructure object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOStructure " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOStructure) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
