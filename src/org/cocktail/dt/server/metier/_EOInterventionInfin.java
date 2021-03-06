// _EOInterventionInfin.java
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

// DO NOT EDIT.  Make changes to EOInterventionInfin.java instead.
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
public abstract class _EOInterventionInfin extends  CktlRecord {
	public static final String ENTITY_NAME = "InterventionInfin";
	public static final String ENTITY_TABLE_NAME = "INTERVENTION_INFIN";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "intOrdre";

	public static final String CAN_CODE_KEY = "canCode";
	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String DEV_ORDRE_KEY = "devOrdre";
	public static final String DST_CODE_KEY = "dstCode";
	public static final String FOU_ORDRE_KEY = "fouOrdre";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String LOLF_ID_KEY = "lolfId";
	public static final String ORG_ID_KEY = "orgId";
	public static final String ORG_ORDRE_KEY = "orgOrdre";
	public static final String PREST_ID_KEY = "prestId";
	public static final String PREST_NUMERO_KEY = "prestNumero";
	public static final String TCD_CODE_KEY = "tcdCode";

	// Non visible attributes

	// Colkeys
	public static final String CAN_CODE_COLKEY = "CAN_CODE";
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String DEV_ORDRE_COLKEY = "DEV_ORDRE";
	public static final String DST_CODE_COLKEY = "DST_CODE";
	public static final String FOU_ORDRE_COLKEY = "FOU_ORDRE";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String LOLF_ID_COLKEY = "LOLF_ID";
	public static final String ORG_ID_COLKEY = "ORG_ID";
	public static final String ORG_ORDRE_COLKEY = "ORG_ORDRE";
	public static final String PREST_ID_COLKEY = "PREST_ID";
	public static final String PREST_NUMERO_COLKEY = "PREST_NUMERO";
	public static final String TCD_CODE_COLKEY = "TCD_CODE";

	// Non visible colkeys

	// Relationships
	public static final String TO_INTERVENTION_KEY = "toIntervention";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOInterventionInfin with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param intOrdre
	 * @return EOInterventionInfin
	 */
	public static EOInterventionInfin create(EOEditingContext editingContext, Integer intOrdre) {
		EOInterventionInfin eo = (EOInterventionInfin) createAndInsertInstance(editingContext);
		eo.setIntOrdre(intOrdre);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOInterventionInfin.
	 *
	 * @param editingContext
	 * @return EOInterventionInfin
	 */
	public static EOInterventionInfin create(EOEditingContext editingContext) {
		EOInterventionInfin eo = (EOInterventionInfin) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOInterventionInfin localInstanceIn(EOEditingContext editingContext) {
		EOInterventionInfin localInstance = (EOInterventionInfin) localInstanceOfObject(editingContext, (EOInterventionInfin) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOInterventionInfin localInstanceIn(EOEditingContext editingContext, EOInterventionInfin eo) {
		EOInterventionInfin localInstance = (eo == null) ? null : (EOInterventionInfin) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String canCode() {
		return (String) storedValueForKey("canCode");
	}

	public void setCanCode(String value) {
		takeStoredValueForKey(value, "canCode");
	}
	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public Integer devOrdre() {
		return (Integer) storedValueForKey("devOrdre");
	}

	public void setDevOrdre(Integer value) {
		takeStoredValueForKey(value, "devOrdre");
	}
	public String dstCode() {
		return (String) storedValueForKey("dstCode");
	}

	public void setDstCode(String value) {
		takeStoredValueForKey(value, "dstCode");
	}
	public Integer fouOrdre() {
		return (Integer) storedValueForKey("fouOrdre");
	}

	public void setFouOrdre(Integer value) {
		takeStoredValueForKey(value, "fouOrdre");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public Integer lolfId() {
		return (Integer) storedValueForKey("lolfId");
	}

	public void setLolfId(Integer value) {
		takeStoredValueForKey(value, "lolfId");
	}
	public Integer orgId() {
		return (Integer) storedValueForKey("orgId");
	}

	public void setOrgId(Integer value) {
		takeStoredValueForKey(value, "orgId");
	}
	public Integer orgOrdre() {
		return (Integer) storedValueForKey("orgOrdre");
	}

	public void setOrgOrdre(Integer value) {
		takeStoredValueForKey(value, "orgOrdre");
	}
	public Integer prestId() {
		return (Integer) storedValueForKey("prestId");
	}

	public void setPrestId(Integer value) {
		takeStoredValueForKey(value, "prestId");
	}
	public Integer prestNumero() {
		return (Integer) storedValueForKey("prestNumero");
	}

	public void setPrestNumero(Integer value) {
		takeStoredValueForKey(value, "prestNumero");
	}
	public String tcdCode() {
		return (String) storedValueForKey("tcdCode");
	}

	public void setTcdCode(String value) {
		takeStoredValueForKey(value, "tcdCode");
	}

	public org.cocktail.dt.server.metier.EOIntervention toIntervention() {
		return (org.cocktail.dt.server.metier.EOIntervention)storedValueForKey("toIntervention");
	}

	public void setToInterventionRelationship(org.cocktail.dt.server.metier.EOIntervention value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIntervention oldValue = toIntervention();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIntervention");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIntervention");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOInterventionInfin.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOInterventionInfin.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOInterventionInfin)
	
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
	public static EOInterventionInfin fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOInterventionInfin fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOInterventionInfin eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOInterventionInfin)eoObjects.objectAtIndex(0);
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
	public static EOInterventionInfin fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOInterventionInfin fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOInterventionInfin fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOInterventionInfin eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOInterventionInfin)eoObjects.objectAtIndex(0);
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
	public static EOInterventionInfin fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOInterventionInfin fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOInterventionInfin eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOInterventionInfin ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOInterventionInfin createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOInterventionInfin.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOInterventionInfin.ENTITY_NAME + "' !");
		}
		else {
			EOInterventionInfin object = (EOInterventionInfin) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOInterventionInfin localInstanceOfObject(EOEditingContext ec, EOInterventionInfin object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOInterventionInfin " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOInterventionInfin) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
