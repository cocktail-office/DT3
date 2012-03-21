// _EOIndividuPersId.java
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

// DO NOT EDIT.  Make changes to EOIndividuPersId.java instead.
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
public abstract class _EOIndividuPersId extends  CktlRecord {
	public static final String ENTITY_NAME = "IndividuUlrPersId";
	public static final String ENTITY_TABLE_NAME = "GRHUM.INDIVIDU_ULR";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "persId";

	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NOM_ET_PRENOM_KEY = "nomEtPrenom";
	public static final String NOM_PATRONYMIQUE_KEY = "nomPatronymique";
	public static final String NOM_USUEL_KEY = "nomUsuel";
	public static final String PERS_ID_KEY = "persId";
	public static final String PRENOM_KEY = "prenom";
	public static final String TEM_VALIDE_KEY = "temValide";

	// Non visible attributes

	// Colkeys
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String NOM_ET_PRENOM_COLKEY = "$attribute.columnName";
	public static final String NOM_PATRONYMIQUE_COLKEY = "NOM_PATRONYMIQUE";
	public static final String NOM_USUEL_COLKEY = "NOM_USUEL";
	public static final String PERS_ID_COLKEY = "PERS_ID";
	public static final String PRENOM_COLKEY = "PRENOM";
	public static final String TEM_VALIDE_COLKEY = "TEM_VALIDE";

	// Non visible colkeys

	// Relationships
	public static final String TO_INDIVIDU_ULR_KEY = "toIndividuUlr";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOIndividuPersId with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param noIndividu
	 * @param persId
	 * @return EOIndividuPersId
	 */
	public static EOIndividuPersId create(EOEditingContext editingContext, Integer noIndividu, Integer persId) {
		EOIndividuPersId eo = (EOIndividuPersId) createAndInsertInstance(editingContext);
		eo.setNoIndividu(noIndividu);
		eo.setPersId(persId);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOIndividuPersId.
	 *
	 * @param editingContext
	 * @return EOIndividuPersId
	 */
	public static EOIndividuPersId create(EOEditingContext editingContext) {
		EOIndividuPersId eo = (EOIndividuPersId) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOIndividuPersId localInstanceIn(EOEditingContext editingContext) {
		EOIndividuPersId localInstance = (EOIndividuPersId) localInstanceOfObject(editingContext, (EOIndividuPersId) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOIndividuPersId localInstanceIn(EOEditingContext editingContext, EOIndividuPersId eo) {
		EOIndividuPersId localInstance = (eo == null) ? null : (EOIndividuPersId) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String nomEtPrenom() {
		return (String) storedValueForKey("nomEtPrenom");
	}

	public void setNomEtPrenom(String value) {
		takeStoredValueForKey(value, "nomEtPrenom");
	}
	public String nomPatronymique() {
		return (String) storedValueForKey("nomPatronymique");
	}

	public void setNomPatronymique(String value) {
		takeStoredValueForKey(value, "nomPatronymique");
	}
	public String nomUsuel() {
		return (String) storedValueForKey("nomUsuel");
	}

	public void setNomUsuel(String value) {
		takeStoredValueForKey(value, "nomUsuel");
	}
	public Integer persId() {
		return (Integer) storedValueForKey("persId");
	}

	public void setPersId(Integer value) {
		takeStoredValueForKey(value, "persId");
	}
	public String prenom() {
		return (String) storedValueForKey("prenom");
	}

	public void setPrenom(String value) {
		takeStoredValueForKey(value, "prenom");
	}
	public String temValide() {
		return (String) storedValueForKey("temValide");
	}

	public void setTemValide(String value) {
		takeStoredValueForKey(value, "temValide");
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
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOIndividuPersId.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOIndividuPersId.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOIndividuPersId)
	
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
	public static EOIndividuPersId fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOIndividuPersId fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOIndividuPersId eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOIndividuPersId)eoObjects.objectAtIndex(0);
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
	public static EOIndividuPersId fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOIndividuPersId fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOIndividuPersId fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOIndividuPersId eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOIndividuPersId)eoObjects.objectAtIndex(0);
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
	public static EOIndividuPersId fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOIndividuPersId fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOIndividuPersId eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOIndividuPersId ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOIndividuPersId createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOIndividuPersId.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOIndividuPersId.ENTITY_NAME + "' !");
		}
		else {
			EOIndividuPersId object = (EOIndividuPersId) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOIndividuPersId localInstanceOfObject(EOEditingContext ec, EOIndividuPersId object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOIndividuPersId " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOIndividuPersId) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
