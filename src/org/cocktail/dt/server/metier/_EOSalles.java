// _EOSalles.java
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

// DO NOT EDIT.  Make changes to EOSalles.java instead.
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
public abstract class _EOSalles extends  CktlRecord {
	public static final String ENTITY_NAME = "Salles";
	public static final String ENTITY_TABLE_NAME = "GRHUM.SALLES";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "salNumero";

	public static final String C_LOCAL_KEY = "cLocal";
	public static final String SAL_ETAGE_KEY = "salEtage";
	public static final String SAL_NUMERO_KEY = "salNumero";
	public static final String SAL_PORTE_KEY = "salPorte";
	public static final String SAL_RESERVABLE_KEY = "salReservable";
	public static final String TSAL_NUMERO_KEY = "tsalNumero";

	// Non visible attributes

	// Colkeys
	public static final String C_LOCAL_COLKEY = "C_LOCAL";
	public static final String SAL_ETAGE_COLKEY = "SAL_ETAGE";
	public static final String SAL_NUMERO_COLKEY = "SAL_NUMERO";
	public static final String SAL_PORTE_COLKEY = "SAL_PORTE";
	public static final String SAL_RESERVABLE_COLKEY = "SAL_RESERVABLE";
	public static final String TSAL_NUMERO_COLKEY = "TSAL_NUMERO";

	// Non visible colkeys

	// Relationships
	public static final String TO_LOCAL_KEY = "toLocal";
	public static final String TOS_REPART_BUREAU_KEY = "tosRepartBureau";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOSalles with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param salEtage
	 * @param salNumero
	 * @param salPorte
	 * @return EOSalles
	 */
	public static EOSalles create(EOEditingContext editingContext, String salEtage, Integer salNumero, String salPorte) {
		EOSalles eo = (EOSalles) createAndInsertInstance(editingContext);
		eo.setSalEtage(salEtage);
		eo.setSalNumero(salNumero);
		eo.setSalPorte(salPorte);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOSalles.
	 *
	 * @param editingContext
	 * @return EOSalles
	 */
	public static EOSalles create(EOEditingContext editingContext) {
		EOSalles eo = (EOSalles) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOSalles localInstanceIn(EOEditingContext editingContext) {
		EOSalles localInstance = (EOSalles) localInstanceOfObject(editingContext, (EOSalles) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOSalles localInstanceIn(EOEditingContext editingContext, EOSalles eo) {
		EOSalles localInstance = (eo == null) ? null : (EOSalles) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cLocal() {
		return (String) storedValueForKey("cLocal");
	}

	public void setCLocal(String value) {
		takeStoredValueForKey(value, "cLocal");
	}
	public String salEtage() {
		return (String) storedValueForKey("salEtage");
	}

	public void setSalEtage(String value) {
		takeStoredValueForKey(value, "salEtage");
	}
	public Integer salNumero() {
		return (Integer) storedValueForKey("salNumero");
	}

	public void setSalNumero(Integer value) {
		takeStoredValueForKey(value, "salNumero");
	}
	public String salPorte() {
		return (String) storedValueForKey("salPorte");
	}

	public void setSalPorte(String value) {
		takeStoredValueForKey(value, "salPorte");
	}
	public String salReservable() {
		return (String) storedValueForKey("salReservable");
	}

	public void setSalReservable(String value) {
		takeStoredValueForKey(value, "salReservable");
	}
	public Integer tsalNumero() {
		return (Integer) storedValueForKey("tsalNumero");
	}

	public void setTsalNumero(Integer value) {
		takeStoredValueForKey(value, "tsalNumero");
	}

	public org.cocktail.dt.server.metier.EOBatiment toLocal() {
		return (org.cocktail.dt.server.metier.EOBatiment)storedValueForKey("toLocal");
	}

	public void setToLocalRelationship(org.cocktail.dt.server.metier.EOBatiment value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOBatiment oldValue = toLocal();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toLocal");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toLocal");
		}
	}
  
	public NSArray tosRepartBureau() {
		return (NSArray)storedValueForKey("tosRepartBureau");
	}

	public NSArray tosRepartBureau(EOQualifier qualifier) {
		return tosRepartBureau(qualifier, null, false);
	}

	public NSArray tosRepartBureau(EOQualifier qualifier, boolean fetch) {
		return tosRepartBureau(qualifier, null, fetch);
	}

	public NSArray tosRepartBureau(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EORepartBureau.TO_SALLES_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EORepartBureau.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosRepartBureau();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
	}

	public void removeFromTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
	}

	public org.cocktail.dt.server.metier.EORepartBureau createTosRepartBureauRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("RepartBureau");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartBureau");
		return (org.cocktail.dt.server.metier.EORepartBureau) eo;
	}

	public void deleteTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartBureauRelationships() {
		Enumeration objects = tosRepartBureau().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartBureauRelationship((org.cocktail.dt.server.metier.EORepartBureau)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOSalles.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOSalles.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOSalles)
	
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
	public static EOSalles fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOSalles fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOSalles eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOSalles)eoObjects.objectAtIndex(0);
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
	public static EOSalles fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOSalles fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOSalles fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOSalles eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOSalles)eoObjects.objectAtIndex(0);
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
	public static EOSalles fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOSalles fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOSalles eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOSalles ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOSalles createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOSalles.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOSalles.ENTITY_NAME + "' !");
		}
		else {
			EOSalles object = (EOSalles) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOSalles localInstanceOfObject(EOEditingContext ec, EOSalles object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOSalles " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOSalles) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
