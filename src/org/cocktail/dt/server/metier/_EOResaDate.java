// _EOResaDate.java
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

// DO NOT EDIT.  Make changes to EOResaDate.java instead.
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
public abstract class _EOResaDate extends  CktlRecord {
	public static final String ENTITY_NAME = "ResaDate";
	public static final String ENTITY_TABLE_NAME = "RESERVATION.RESA_DATE";

	// Attributes

	public static final String OBJE_CLE_KEY = "objeCle";
	public static final String OBJE_ORDRE_KEY = "objeOrdre";
	public static final String RESA_DEBUT_KEY = "resaDebut";
	public static final String RESA_ETAT_KEY = "resaEtat";
	public static final String RESA_FIN_KEY = "resaFin";
	public static final String RESA_MOTIF_KEY = "resaMotif";
	public static final String RESA_ORDRE_KEY = "resaOrdre";
	public static final String RESA_POS_DEBUT_KEY = "resaPosDebut";
	public static final String RESA_POS_FIN_KEY = "resaPosFin";
	public static final String TYPE_CODE_KEY = "typeCode";

	// Non visible attributes

	// Colkeys
	public static final String OBJE_CLE_COLKEY = "$attribute.columnName";
	public static final String OBJE_ORDRE_COLKEY = "$attribute.columnName";
	public static final String RESA_DEBUT_COLKEY = "RESA_DEBUT";
	public static final String RESA_ETAT_COLKEY = "$attribute.columnName";
	public static final String RESA_FIN_COLKEY = "RESA_FIN";
	public static final String RESA_MOTIF_COLKEY = "$attribute.columnName";
	public static final String RESA_ORDRE_COLKEY = "RESA_ORDRE";
	public static final String RESA_POS_DEBUT_COLKEY = "RESA_POS_DEBUT";
	public static final String RESA_POS_FIN_COLKEY = "RESA_POS_FIN";
	public static final String TYPE_CODE_COLKEY = "$attribute.columnName";

	// Non visible colkeys

	// Relationships
	public static final String TO_RESA_PLANNING_KEY = "toResaPlanning";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOResaDate with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param objeCle
	 * @param objeOrdre
	 * @param resaDebut
	 * @param resaFin
	 * @param resaOrdre
	 * @param resaPosDebut
	 * @param resaPosFin
	 * @param typeCode
	 * @return EOResaDate
	 */
	public static EOResaDate create(EOEditingContext editingContext, Integer objeCle, Integer objeOrdre, NSTimestamp resaDebut, NSTimestamp resaFin, Integer resaOrdre, Integer resaPosDebut, Integer resaPosFin, String typeCode) {
		EOResaDate eo = (EOResaDate) createAndInsertInstance(editingContext);
		eo.setObjeCle(objeCle);
		eo.setObjeOrdre(objeOrdre);
		eo.setResaDebut(resaDebut);
		eo.setResaFin(resaFin);
		eo.setResaOrdre(resaOrdre);
		eo.setResaPosDebut(resaPosDebut);
		eo.setResaPosFin(resaPosFin);
		eo.setTypeCode(typeCode);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOResaDate.
	 *
	 * @param editingContext
	 * @return EOResaDate
	 */
	public static EOResaDate create(EOEditingContext editingContext) {
		EOResaDate eo = (EOResaDate) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOResaDate localInstanceIn(EOEditingContext editingContext) {
		EOResaDate localInstance = (EOResaDate) localInstanceOfObject(editingContext, (EOResaDate) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOResaDate localInstanceIn(EOEditingContext editingContext, EOResaDate eo) {
		EOResaDate localInstance = (eo == null) ? null : (EOResaDate) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer objeCle() {
		return (Integer) storedValueForKey("objeCle");
	}

	public void setObjeCle(Integer value) {
		takeStoredValueForKey(value, "objeCle");
	}
	public Integer objeOrdre() {
		return (Integer) storedValueForKey("objeOrdre");
	}

	public void setObjeOrdre(Integer value) {
		takeStoredValueForKey(value, "objeOrdre");
	}
	public NSTimestamp resaDebut() {
		return (NSTimestamp) storedValueForKey("resaDebut");
	}

	public void setResaDebut(NSTimestamp value) {
		takeStoredValueForKey(value, "resaDebut");
	}
	public String resaEtat() {
		return (String) storedValueForKey("resaEtat");
	}

	public void setResaEtat(String value) {
		takeStoredValueForKey(value, "resaEtat");
	}
	public NSTimestamp resaFin() {
		return (NSTimestamp) storedValueForKey("resaFin");
	}

	public void setResaFin(NSTimestamp value) {
		takeStoredValueForKey(value, "resaFin");
	}
	public String resaMotif() {
		return (String) storedValueForKey("resaMotif");
	}

	public void setResaMotif(String value) {
		takeStoredValueForKey(value, "resaMotif");
	}
	public Integer resaOrdre() {
		return (Integer) storedValueForKey("resaOrdre");
	}

	public void setResaOrdre(Integer value) {
		takeStoredValueForKey(value, "resaOrdre");
	}
	public Integer resaPosDebut() {
		return (Integer) storedValueForKey("resaPosDebut");
	}

	public void setResaPosDebut(Integer value) {
		takeStoredValueForKey(value, "resaPosDebut");
	}
	public Integer resaPosFin() {
		return (Integer) storedValueForKey("resaPosFin");
	}

	public void setResaPosFin(Integer value) {
		takeStoredValueForKey(value, "resaPosFin");
	}
	public String typeCode() {
		return (String) storedValueForKey("typeCode");
	}

	public void setTypeCode(String value) {
		takeStoredValueForKey(value, "typeCode");
	}

	public org.cocktail.dt.server.metier.EOResaPlanningHarp toResaPlanning() {
		return (org.cocktail.dt.server.metier.EOResaPlanningHarp)storedValueForKey("toResaPlanning");
	}

	public void setToResaPlanningRelationship(org.cocktail.dt.server.metier.EOResaPlanningHarp value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOResaPlanningHarp oldValue = toResaPlanning();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toResaPlanning");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toResaPlanning");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOResaDate.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOResaDate.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOResaDate)
	
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
	public static EOResaDate fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOResaDate fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOResaDate eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOResaDate)eoObjects.objectAtIndex(0);
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
	public static EOResaDate fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOResaDate fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOResaDate fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOResaDate eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOResaDate)eoObjects.objectAtIndex(0);
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
	public static EOResaDate fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOResaDate fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOResaDate eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOResaDate ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOResaDate createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOResaDate.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOResaDate.ENTITY_NAME + "' !");
		}
		else {
			EOResaDate object = (EOResaDate) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOResaDate localInstanceOfObject(EOEditingContext ec, EOResaDate object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOResaDate " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOResaDate) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
