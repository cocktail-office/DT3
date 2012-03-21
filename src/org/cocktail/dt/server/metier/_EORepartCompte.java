// _EORepartCompte.java
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

// DO NOT EDIT.  Make changes to EORepartCompte.java instead.
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
public abstract class _EORepartCompte extends  CktlRecord {
	public static final String ENTITY_NAME = "DTRepartCompte";
	public static final String ENTITY_TABLE_NAME = "GRHUM.REPART_COMPTE";

	// Attributes

	public static final String CPT_ORDRE_KEY = "cptOrdre";
	public static final String PERS_ID_KEY = "persId";

	// Non visible attributes

	// Colkeys
	public static final String CPT_ORDRE_COLKEY = "CPT_ORDRE";
	public static final String PERS_ID_COLKEY = "PERS_ID";

	// Non visible colkeys

	// Relationships
	public static final String TO_COMPTE_KEY = "toCompte";

	// Create / Init methods

	/**
	 * Creates and inserts a new EORepartCompte with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cptOrdre
	 * @param persId
	 * @param toCompte
	 * @return EORepartCompte
	 */
	public static EORepartCompte create(EOEditingContext editingContext, Integer cptOrdre, Integer persId, org.cocktail.dt.server.metier.EOCompte toCompte) {
		EORepartCompte eo = (EORepartCompte) createAndInsertInstance(editingContext);
		eo.setCptOrdre(cptOrdre);
		eo.setPersId(persId);
		eo.setToCompteRelationship(toCompte);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EORepartCompte.
	 *
	 * @param editingContext
	 * @return EORepartCompte
	 */
	public static EORepartCompte create(EOEditingContext editingContext) {
		EORepartCompte eo = (EORepartCompte) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EORepartCompte localInstanceIn(EOEditingContext editingContext) {
		EORepartCompte localInstance = (EORepartCompte) localInstanceOfObject(editingContext, (EORepartCompte) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EORepartCompte localInstanceIn(EOEditingContext editingContext, EORepartCompte eo) {
		EORepartCompte localInstance = (eo == null) ? null : (EORepartCompte) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer cptOrdre() {
		return (Integer) storedValueForKey("cptOrdre");
	}

	public void setCptOrdre(Integer value) {
		takeStoredValueForKey(value, "cptOrdre");
	}
	public Integer persId() {
		return (Integer) storedValueForKey("persId");
	}

	public void setPersId(Integer value) {
		takeStoredValueForKey(value, "persId");
	}

	public org.cocktail.dt.server.metier.EOCompte toCompte() {
		return (org.cocktail.dt.server.metier.EOCompte)storedValueForKey("toCompte");
	}

	public void setToCompteRelationship(org.cocktail.dt.server.metier.EOCompte value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOCompte oldValue = toCompte();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toCompte");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toCompte");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EORepartCompte.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EORepartCompte.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EORepartCompte)
	
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
	public static EORepartCompte fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EORepartCompte fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EORepartCompte eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EORepartCompte)eoObjects.objectAtIndex(0);
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
	public static EORepartCompte fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EORepartCompte fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EORepartCompte fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EORepartCompte eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EORepartCompte)eoObjects.objectAtIndex(0);
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
	public static EORepartCompte fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EORepartCompte fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EORepartCompte eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EORepartCompte ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EORepartCompte createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EORepartCompte.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EORepartCompte.ENTITY_NAME + "' !");
		}
		else {
			EORepartCompte object = (EORepartCompte) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EORepartCompte localInstanceOfObject(EOEditingContext ec, EORepartCompte object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EORepartCompte " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EORepartCompte) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
