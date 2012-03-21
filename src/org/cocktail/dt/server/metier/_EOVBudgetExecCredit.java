// _EOVBudgetExecCredit.java
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

// DO NOT EDIT.  Make changes to EOVBudgetExecCredit.java instead.
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
public abstract class _EOVBudgetExecCredit extends  CktlRecord {
	public static final String ENTITY_NAME = "VJefyBudgetExecCredit";
	public static final String ENTITY_TABLE_NAME = "GRHUM.V_BUDGET_EXEC_CREDIT";

	// Attributes

	public static final String ANNEE_KEY = "annee";
	public static final String DISPONIBLE_KEY = "disponible";
	public static final String ORG_ORDRE_KEY = "orgOrdre";
	public static final String TCD_CODE_KEY = "tcdCode";

	// Non visible attributes

	// Colkeys
	public static final String ANNEE_COLKEY = "ANNEE";
	public static final String DISPONIBLE_COLKEY = "DISPONIBLE";
	public static final String ORG_ORDRE_COLKEY = "ORG_ORDRE";
	public static final String TCD_CODE_COLKEY = "TCD_CODE";

	// Non visible colkeys

	// Relationships
	public static final String TO_V_JEFY_ORGAN_KEY = "toVJefyOrgan";
	public static final String TO_V_JEFY_TYPE_CREDIT_KEY = "toVJefyTypeCredit";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVBudgetExecCredit with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param annee
	 * @param disponible
	 * @param orgOrdre
	 * @param tcdCode
	 * @return EOVBudgetExecCredit
	 */
	public static EOVBudgetExecCredit create(EOEditingContext editingContext, Integer annee, Integer disponible, Integer orgOrdre, String tcdCode) {
		EOVBudgetExecCredit eo = (EOVBudgetExecCredit) createAndInsertInstance(editingContext);
		eo.setAnnee(annee);
		eo.setDisponible(disponible);
		eo.setOrgOrdre(orgOrdre);
		eo.setTcdCode(tcdCode);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVBudgetExecCredit.
	 *
	 * @param editingContext
	 * @return EOVBudgetExecCredit
	 */
	public static EOVBudgetExecCredit create(EOEditingContext editingContext) {
		EOVBudgetExecCredit eo = (EOVBudgetExecCredit) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVBudgetExecCredit localInstanceIn(EOEditingContext editingContext) {
		EOVBudgetExecCredit localInstance = (EOVBudgetExecCredit) localInstanceOfObject(editingContext, (EOVBudgetExecCredit) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVBudgetExecCredit localInstanceIn(EOEditingContext editingContext, EOVBudgetExecCredit eo) {
		EOVBudgetExecCredit localInstance = (eo == null) ? null : (EOVBudgetExecCredit) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer annee() {
		return (Integer) storedValueForKey("annee");
	}

	public void setAnnee(Integer value) {
		takeStoredValueForKey(value, "annee");
	}
	public Integer disponible() {
		return (Integer) storedValueForKey("disponible");
	}

	public void setDisponible(Integer value) {
		takeStoredValueForKey(value, "disponible");
	}
	public Integer orgOrdre() {
		return (Integer) storedValueForKey("orgOrdre");
	}

	public void setOrgOrdre(Integer value) {
		takeStoredValueForKey(value, "orgOrdre");
	}
	public String tcdCode() {
		return (String) storedValueForKey("tcdCode");
	}

	public void setTcdCode(String value) {
		takeStoredValueForKey(value, "tcdCode");
	}

	public org.cocktail.dt.server.metier.EOVOrgan toVJefyOrgan() {
		return (org.cocktail.dt.server.metier.EOVOrgan)storedValueForKey("toVJefyOrgan");
	}

	public void setToVJefyOrganRelationship(org.cocktail.dt.server.metier.EOVOrgan value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOVOrgan oldValue = toVJefyOrgan();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toVJefyOrgan");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toVJefyOrgan");
		}
	}
  
	public org.cocktail.dt.server.metier.EOVTypeCredit toVJefyTypeCredit() {
		return (org.cocktail.dt.server.metier.EOVTypeCredit)storedValueForKey("toVJefyTypeCredit");
	}

	public void setToVJefyTypeCreditRelationship(org.cocktail.dt.server.metier.EOVTypeCredit value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOVTypeCredit oldValue = toVJefyTypeCredit();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toVJefyTypeCredit");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toVJefyTypeCredit");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVBudgetExecCredit.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVBudgetExecCredit.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVBudgetExecCredit)
	
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
	public static EOVBudgetExecCredit fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVBudgetExecCredit fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVBudgetExecCredit eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVBudgetExecCredit)eoObjects.objectAtIndex(0);
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
	public static EOVBudgetExecCredit fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVBudgetExecCredit fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVBudgetExecCredit fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVBudgetExecCredit eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVBudgetExecCredit)eoObjects.objectAtIndex(0);
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
	public static EOVBudgetExecCredit fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVBudgetExecCredit fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVBudgetExecCredit eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVBudgetExecCredit ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVBudgetExecCredit createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVBudgetExecCredit.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVBudgetExecCredit.ENTITY_NAME + "' !");
		}
		else {
			EOVBudgetExecCredit object = (EOVBudgetExecCredit) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVBudgetExecCredit localInstanceOfObject(EOEditingContext ec, EOVBudgetExecCredit object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVBudgetExecCredit " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVBudgetExecCredit) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
