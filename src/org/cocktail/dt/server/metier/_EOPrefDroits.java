// _EOPrefDroits.java
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

// DO NOT EDIT.  Make changes to EOPrefDroits.java instead.
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
public abstract class _EOPrefDroits extends  CktlRecord {
	public static final String ENTITY_NAME = "PrefDroits";
	public static final String ENTITY_TABLE_NAME = "PREF_DROITS";

	// Attributes

	public static final String DRO_NIVEAU_KEY = "droNiveau";
	public static final String DRO_SERVICE_KEY = "droService";
	public static final String LC_STRUCTURE_KEY = "lcStructure";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NOM_USUEL_KEY = "nomUsuel";
	public static final String PRENOM_KEY = "prenom";
	public static final String PRF_NO_IND_INTERVENANT_KEY = "prfNoIndIntervenant";

	// Non visible attributes

	// Colkeys
	public static final String DRO_NIVEAU_COLKEY = "DRO_NIVEAU";
	public static final String DRO_SERVICE_COLKEY = "DRO_SERVICE";
	public static final String LC_STRUCTURE_COLKEY = "$attribute.columnName";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String NOM_USUEL_COLKEY = "$attribute.columnName";
	public static final String PRENOM_COLKEY = "$attribute.columnName";
	public static final String PRF_NO_IND_INTERVENANT_COLKEY = "PRF_NO_IND_INTERVENANT";

	// Non visible colkeys

	// Relationships
	public static final String TO_INDIVIDU_ULR_KEY = "toIndividuUlr";
	public static final String TO_STRUCTURE_ULR_KEY = "toStructureUlr";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOPrefDroits with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param droNiveau
	 * @param droService
	 * @param noIndividu
	 * @param prfNoIndIntervenant
	 * @return EOPrefDroits
	 */
	public static EOPrefDroits create(EOEditingContext editingContext, Integer droNiveau, String droService, Integer noIndividu, String prfNoIndIntervenant) {
		EOPrefDroits eo = (EOPrefDroits) createAndInsertInstance(editingContext);
		eo.setDroNiveau(droNiveau);
		eo.setDroService(droService);
		eo.setNoIndividu(noIndividu);
		eo.setPrfNoIndIntervenant(prfNoIndIntervenant);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOPrefDroits.
	 *
	 * @param editingContext
	 * @return EOPrefDroits
	 */
	public static EOPrefDroits create(EOEditingContext editingContext) {
		EOPrefDroits eo = (EOPrefDroits) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOPrefDroits localInstanceIn(EOEditingContext editingContext) {
		EOPrefDroits localInstance = (EOPrefDroits) localInstanceOfObject(editingContext, (EOPrefDroits) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOPrefDroits localInstanceIn(EOEditingContext editingContext, EOPrefDroits eo) {
		EOPrefDroits localInstance = (eo == null) ? null : (EOPrefDroits) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer droNiveau() {
		return (Integer) storedValueForKey("droNiveau");
	}

	public void setDroNiveau(Integer value) {
		takeStoredValueForKey(value, "droNiveau");
	}
	public String droService() {
		return (String) storedValueForKey("droService");
	}

	public void setDroService(String value) {
		takeStoredValueForKey(value, "droService");
	}
	public String lcStructure() {
		return (String) storedValueForKey("lcStructure");
	}

	public void setLcStructure(String value) {
		takeStoredValueForKey(value, "lcStructure");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String nomUsuel() {
		return (String) storedValueForKey("nomUsuel");
	}

	public void setNomUsuel(String value) {
		takeStoredValueForKey(value, "nomUsuel");
	}
	public String prenom() {
		return (String) storedValueForKey("prenom");
	}

	public void setPrenom(String value) {
		takeStoredValueForKey(value, "prenom");
	}
	public String prfNoIndIntervenant() {
		return (String) storedValueForKey("prfNoIndIntervenant");
	}

	public void setPrfNoIndIntervenant(String value) {
		takeStoredValueForKey(value, "prfNoIndIntervenant");
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
  
	public org.cocktail.dt.server.metier.EOStructure toStructureUlr() {
		return (org.cocktail.dt.server.metier.EOStructure)storedValueForKey("toStructureUlr");
	}

	public void setToStructureUlrRelationship(org.cocktail.dt.server.metier.EOStructure value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOStructure oldValue = toStructureUlr();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toStructureUlr");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toStructureUlr");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOPrefDroits.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOPrefDroits.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOPrefDroits)
	
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
	public static EOPrefDroits fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOPrefDroits fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOPrefDroits eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOPrefDroits)eoObjects.objectAtIndex(0);
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
	public static EOPrefDroits fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOPrefDroits fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOPrefDroits fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOPrefDroits eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOPrefDroits)eoObjects.objectAtIndex(0);
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
	public static EOPrefDroits fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOPrefDroits fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOPrefDroits eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOPrefDroits ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOPrefDroits createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOPrefDroits.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOPrefDroits.ENTITY_NAME + "' !");
		}
		else {
			EOPrefDroits object = (EOPrefDroits) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOPrefDroits localInstanceOfObject(EOEditingContext ec, EOPrefDroits object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOPrefDroits " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOPrefDroits) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
