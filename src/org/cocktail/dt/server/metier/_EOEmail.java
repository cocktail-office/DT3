// _EOEmail.java
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

// DO NOT EDIT.  Make changes to EOEmail.java instead.
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
public abstract class _EOEmail extends  CktlRecord {
	public static final String ENTITY_NAME = "Email";
	public static final String ENTITY_TABLE_NAME = "DT3.EMAIL";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "emailOrdre";

	public static final String CC_KEY = "cc";
	public static final String D_CREATION_KEY = "dCreation";
	public static final String DIS_ORDRE_KEY = "disOrdre";
	public static final String D_MODIFICATION_KEY = "dModification";
	public static final String FROM_KEY = "from";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String MESSAGE_KEY = "message";
	public static final String MESSAGE_ID_KEY = "messageId";
	public static final String TO_KEY = "to";
	public static final String TRA_ORDRE_KEY = "traOrdre";

	// Non visible attributes
	public static final String EMAIL_ORDRE_KEY = "emailOrdre";

	// Colkeys
	public static final String CC_COLKEY = "cc";
	public static final String D_CREATION_COLKEY = "D_CREATION";
	public static final String DIS_ORDRE_COLKEY = "DIS_ORDRE";
	public static final String D_MODIFICATION_COLKEY = "D_MODIFICATION";
	public static final String FROM_COLKEY = "FROM";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String MESSAGE_COLKEY = "MESSAGE";
	public static final String MESSAGE_ID_COLKEY = "MESSAGE_ID";
	public static final String TO_COLKEY = "TO";
	public static final String TRA_ORDRE_COLKEY = "TRA_ORDRE";

	// Non visible colkeys
	public static final String EMAIL_ORDRE_COLKEY = "EMAIL_ORDRE";

	// Relationships
	public static final String TO_DISCUSSION_KEY = "toDiscussion";
	public static final String TO_INTERVENTION_KEY = "toIntervention";
	public static final String TO_TRAITEMENT_KEY = "toTraitement";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOEmail with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param dCreation
	 * @param dModification
	 * @return EOEmail
	 */
	public static EOEmail create(EOEditingContext editingContext, NSTimestamp dCreation, NSTimestamp dModification) {
		EOEmail eo = (EOEmail) createAndInsertInstance(editingContext);
		eo.setDCreation(dCreation);
		eo.setDModification(dModification);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOEmail.
	 *
	 * @param editingContext
	 * @return EOEmail
	 */
	public static EOEmail create(EOEditingContext editingContext) {
		EOEmail eo = (EOEmail) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOEmail localInstanceIn(EOEditingContext editingContext) {
		EOEmail localInstance = (EOEmail) localInstanceOfObject(editingContext, (EOEmail) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOEmail localInstanceIn(EOEditingContext editingContext, EOEmail eo) {
		EOEmail localInstance = (eo == null) ? null : (EOEmail) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cc() {
		return (String) storedValueForKey("cc");
	}

	public void setCc(String value) {
		takeStoredValueForKey(value, "cc");
	}
	public NSTimestamp dCreation() {
		return (NSTimestamp) storedValueForKey("dCreation");
	}

	public void setDCreation(NSTimestamp value) {
		takeStoredValueForKey(value, "dCreation");
	}
	public Integer disOrdre() {
		return (Integer) storedValueForKey("disOrdre");
	}

	public void setDisOrdre(Integer value) {
		takeStoredValueForKey(value, "disOrdre");
	}
	public NSTimestamp dModification() {
		return (NSTimestamp) storedValueForKey("dModification");
	}

	public void setDModification(NSTimestamp value) {
		takeStoredValueForKey(value, "dModification");
	}
	public String from() {
		return (String) storedValueForKey("from");
	}

	public void setFrom(String value) {
		takeStoredValueForKey(value, "from");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public String message() {
		return (String) storedValueForKey("message");
	}

	public void setMessage(String value) {
		takeStoredValueForKey(value, "message");
	}
	public String messageId() {
		return (String) storedValueForKey("messageId");
	}

	public void setMessageId(String value) {
		takeStoredValueForKey(value, "messageId");
	}
	public String to() {
		return (String) storedValueForKey("to");
	}

	public void setTo(String value) {
		takeStoredValueForKey(value, "to");
	}
	public Integer traOrdre() {
		return (Integer) storedValueForKey("traOrdre");
	}

	public void setTraOrdre(Integer value) {
		takeStoredValueForKey(value, "traOrdre");
	}

	public org.cocktail.dt.server.metier.EODiscussion toDiscussion() {
		return (org.cocktail.dt.server.metier.EODiscussion)storedValueForKey("toDiscussion");
	}

	public void setToDiscussionRelationship(org.cocktail.dt.server.metier.EODiscussion value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EODiscussion oldValue = toDiscussion();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toDiscussion");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toDiscussion");
		}
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
  
	public org.cocktail.dt.server.metier.EOTraitement toTraitement() {
		return (org.cocktail.dt.server.metier.EOTraitement)storedValueForKey("toTraitement");
	}

	public void setToTraitementRelationship(org.cocktail.dt.server.metier.EOTraitement value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOTraitement oldValue = toTraitement();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toTraitement");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toTraitement");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOEmail.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOEmail.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOEmail)
	
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
	public static EOEmail fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOEmail fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOEmail eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOEmail)eoObjects.objectAtIndex(0);
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
	public static EOEmail fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOEmail fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOEmail fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOEmail eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOEmail)eoObjects.objectAtIndex(0);
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
	public static EOEmail fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOEmail fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOEmail eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOEmail ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOEmail createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOEmail.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOEmail.ENTITY_NAME + "' !");
		}
		else {
			EOEmail object = (EOEmail) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOEmail localInstanceOfObject(EOEditingContext ec, EOEmail object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOEmail " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOEmail) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
