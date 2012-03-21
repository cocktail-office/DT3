// _EOVJefyDestin.java
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

// DO NOT EDIT.  Make changes to EOVJefyDestin.java instead.
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
public abstract class _EOVJefyDestin extends  CktlRecord {
	public static final String ENTITY_NAME = "VJefyDestin";
	public static final String ENTITY_TABLE_NAME = "GRHUM.V_DESTIN";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "dstCode";

	public static final String ANNEE_KEY = "annee";
	public static final String DST_ABREGE_KEY = "dstAbrege";
	public static final String DST_CODE_KEY = "dstCode";
	public static final String DST_DLIB_KEY = "dstDlib";
	public static final String DST_LIB_KEY = "dstLib";
	public static final String DST_NIV_KEY = "dstNiv";
	public static final String DST_TYPE_KEY = "dstType";
	public static final String LOLF_ID_KEY = "lolfId";

	// Non visible attributes

	// Colkeys
	public static final String ANNEE_COLKEY = "ANNEE";
	public static final String DST_ABREGE_COLKEY = "DST_ABREGE";
	public static final String DST_CODE_COLKEY = "DST_CODE";
	public static final String DST_DLIB_COLKEY = "DST_DLIB";
	public static final String DST_LIB_COLKEY = "DST_LIB";
	public static final String DST_NIV_COLKEY = "DST_NIV";
	public static final String DST_TYPE_COLKEY = "DST_TYPE";
	public static final String LOLF_ID_COLKEY = "LOLF_ID";

	// Non visible colkeys

	// Relationships

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVJefyDestin with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param annee
	 * @param dstAbrege
	 * @param dstCode
	 * @param dstDlib
	 * @param dstLib
	 * @param dstNiv
	 * @param dstType
	 * @param lolfId
	 * @return EOVJefyDestin
	 */
	public static EOVJefyDestin create(EOEditingContext editingContext, Integer annee, String dstAbrege, String dstCode, String dstDlib, String dstLib, Double dstNiv, String dstType, Integer lolfId) {
		EOVJefyDestin eo = (EOVJefyDestin) createAndInsertInstance(editingContext);
		eo.setAnnee(annee);
		eo.setDstAbrege(dstAbrege);
		eo.setDstCode(dstCode);
		eo.setDstDlib(dstDlib);
		eo.setDstLib(dstLib);
		eo.setDstNiv(dstNiv);
		eo.setDstType(dstType);
		eo.setLolfId(lolfId);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVJefyDestin.
	 *
	 * @param editingContext
	 * @return EOVJefyDestin
	 */
	public static EOVJefyDestin create(EOEditingContext editingContext) {
		EOVJefyDestin eo = (EOVJefyDestin) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVJefyDestin localInstanceIn(EOEditingContext editingContext) {
		EOVJefyDestin localInstance = (EOVJefyDestin) localInstanceOfObject(editingContext, (EOVJefyDestin) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVJefyDestin localInstanceIn(EOEditingContext editingContext, EOVJefyDestin eo) {
		EOVJefyDestin localInstance = (eo == null) ? null : (EOVJefyDestin) localInstanceOfObject(editingContext, eo);
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
	public String dstAbrege() {
		return (String) storedValueForKey("dstAbrege");
	}

	public void setDstAbrege(String value) {
		takeStoredValueForKey(value, "dstAbrege");
	}
	public String dstCode() {
		return (String) storedValueForKey("dstCode");
	}

	public void setDstCode(String value) {
		takeStoredValueForKey(value, "dstCode");
	}
	public String dstDlib() {
		return (String) storedValueForKey("dstDlib");
	}

	public void setDstDlib(String value) {
		takeStoredValueForKey(value, "dstDlib");
	}
	public String dstLib() {
		return (String) storedValueForKey("dstLib");
	}

	public void setDstLib(String value) {
		takeStoredValueForKey(value, "dstLib");
	}
	public Double dstNiv() {
		return (Double) storedValueForKey("dstNiv");
	}

	public void setDstNiv(Double value) {
		takeStoredValueForKey(value, "dstNiv");
	}
	public String dstType() {
		return (String) storedValueForKey("dstType");
	}

	public void setDstType(String value) {
		takeStoredValueForKey(value, "dstType");
	}
	public Integer lolfId() {
		return (Integer) storedValueForKey("lolfId");
	}

	public void setLolfId(Integer value) {
		takeStoredValueForKey(value, "lolfId");
	}


	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVJefyDestin.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVJefyDestin.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVJefyDestin)
	
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
	public static EOVJefyDestin fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVJefyDestin fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVJefyDestin eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVJefyDestin)eoObjects.objectAtIndex(0);
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
	public static EOVJefyDestin fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVJefyDestin fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVJefyDestin fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVJefyDestin eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVJefyDestin)eoObjects.objectAtIndex(0);
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
	public static EOVJefyDestin fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVJefyDestin fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVJefyDestin eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVJefyDestin ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVJefyDestin createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVJefyDestin.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVJefyDestin.ENTITY_NAME + "' !");
		}
		else {
			EOVJefyDestin object = (EOVJefyDestin) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVJefyDestin localInstanceOfObject(EOEditingContext ec, EOVJefyDestin object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVJefyDestin " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVJefyDestin) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
