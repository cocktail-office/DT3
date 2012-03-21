// _EOHoraire.java
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

// DO NOT EDIT.  Make changes to EOHoraire.java instead.
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
public abstract class _EOHoraire extends  CktlRecord {
	public static final String ENTITY_NAME = "Horaire";
	public static final String ENTITY_TABLE_NAME = "EDT.HORAIRE_GRHUM";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "horNumero";

	public static final String HOR_DATE_DEBUT_KEY = "horDateDebut";
	public static final String HOR_DATE_FIN_KEY = "horDateFin";
	public static final String HOR_HEURES_KEY = "horHeures";
	public static final String HOR_NUMERO_KEY = "horNumero";
	public static final String HOR_TOTAL_KEY = "horTotal";
	public static final String HOR_VALIDE_KEY = "horValide";
	public static final String NO_INDIVIDU_KEY = "noIndividu";

	// Non visible attributes

	// Colkeys
	public static final String HOR_DATE_DEBUT_COLKEY = "HOR_DATE_DEBUT";
	public static final String HOR_DATE_FIN_COLKEY = "HOR_DATE_FIN";
	public static final String HOR_HEURES_COLKEY = "HOR_HEURES";
	public static final String HOR_NUMERO_COLKEY = "HOR_NUMERO";
	public static final String HOR_TOTAL_COLKEY = "HOR_TOTAL";
	public static final String HOR_VALIDE_COLKEY = "HOR_VALIDE";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";

	// Non visible colkeys

	// Relationships

	// Create / Init methods

	/**
	 * Creates and inserts a new EOHoraire with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param horDateDebut
	 * @param horHeures
	 * @param horNumero
	 * @param horValide
	 * @param noIndividu
	 * @return EOHoraire
	 */
	public static EOHoraire create(EOEditingContext editingContext, NSTimestamp horDateDebut, String horHeures, Integer horNumero, String horValide, Integer noIndividu) {
		EOHoraire eo = (EOHoraire) createAndInsertInstance(editingContext);
		eo.setHorDateDebut(horDateDebut);
		eo.setHorHeures(horHeures);
		eo.setHorNumero(horNumero);
		eo.setHorValide(horValide);
		eo.setNoIndividu(noIndividu);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOHoraire.
	 *
	 * @param editingContext
	 * @return EOHoraire
	 */
	public static EOHoraire create(EOEditingContext editingContext) {
		EOHoraire eo = (EOHoraire) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOHoraire localInstanceIn(EOEditingContext editingContext) {
		EOHoraire localInstance = (EOHoraire) localInstanceOfObject(editingContext, (EOHoraire) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOHoraire localInstanceIn(EOEditingContext editingContext, EOHoraire eo) {
		EOHoraire localInstance = (eo == null) ? null : (EOHoraire) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public NSTimestamp horDateDebut() {
		return (NSTimestamp) storedValueForKey("horDateDebut");
	}

	public void setHorDateDebut(NSTimestamp value) {
		takeStoredValueForKey(value, "horDateDebut");
	}
	public NSTimestamp horDateFin() {
		return (NSTimestamp) storedValueForKey("horDateFin");
	}

	public void setHorDateFin(NSTimestamp value) {
		takeStoredValueForKey(value, "horDateFin");
	}
	public String horHeures() {
		return (String) storedValueForKey("horHeures");
	}

	public void setHorHeures(String value) {
		takeStoredValueForKey(value, "horHeures");
	}
	public Integer horNumero() {
		return (Integer) storedValueForKey("horNumero");
	}

	public void setHorNumero(Integer value) {
		takeStoredValueForKey(value, "horNumero");
	}
	public String horTotal() {
		return (String) storedValueForKey("horTotal");
	}

	public void setHorTotal(String value) {
		takeStoredValueForKey(value, "horTotal");
	}
	public String horValide() {
		return (String) storedValueForKey("horValide");
	}

	public void setHorValide(String value) {
		takeStoredValueForKey(value, "horValide");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}


	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOHoraire.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOHoraire.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOHoraire)
	
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
	public static EOHoraire fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOHoraire fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOHoraire eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOHoraire)eoObjects.objectAtIndex(0);
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
	public static EOHoraire fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOHoraire fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOHoraire fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOHoraire eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOHoraire)eoObjects.objectAtIndex(0);
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
	public static EOHoraire fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOHoraire fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOHoraire eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOHoraire ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOHoraire createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOHoraire.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOHoraire.ENTITY_NAME + "' !");
		}
		else {
			EOHoraire object = (EOHoraire) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOHoraire localInstanceOfObject(EOEditingContext ec, EOHoraire object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOHoraire " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOHoraire) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
