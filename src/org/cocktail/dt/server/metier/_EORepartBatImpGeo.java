// _EORepartBatImpGeo.java
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

// DO NOT EDIT.  Make changes to EORepartBatImpGeo.java instead.
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
public abstract class _EORepartBatImpGeo extends  CktlRecord {
	public static final String ENTITY_NAME = "RepartBatImpGeo";
	public static final String ENTITY_TABLE_NAME = "GRHUM.REPART_BAT_IMP_GEO";

	// Attributes

	public static final String C_IMPLANTATION_KEY = "cImplantation";
	public static final String C_LOCAL_KEY = "cLocal";
	public static final String IMGEO_ORDRE_KEY = "imgeoOrdre";

	// Non visible attributes

	// Colkeys
	public static final String C_IMPLANTATION_COLKEY = "C_IMPLANTATION";
	public static final String C_LOCAL_COLKEY = "C_LOCAL";
	public static final String IMGEO_ORDRE_COLKEY = "IMGEO_ORDRE";

	// Non visible colkeys

	// Relationships
	public static final String TO_BATIMENT_KEY = "toBatiment";
	public static final String TO_IMPLANTATION_GEO_KEY = "toImplantationGeo";

	// Create / Init methods

	/**
	 * Creates and inserts a new EORepartBatImpGeo with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cImplantation
	 * @param cLocal
	 * @param imgeoOrdre
	 * @param toBatiment
	 * @param toImplantationGeo
	 * @return EORepartBatImpGeo
	 */
	public static EORepartBatImpGeo create(EOEditingContext editingContext, String cImplantation, String cLocal, Integer imgeoOrdre, org.cocktail.dt.server.metier.EOBatiment toBatiment, org.cocktail.dt.server.metier.EOImplantationGeo toImplantationGeo) {
		EORepartBatImpGeo eo = (EORepartBatImpGeo) createAndInsertInstance(editingContext);
		eo.setCImplantation(cImplantation);
		eo.setCLocal(cLocal);
		eo.setImgeoOrdre(imgeoOrdre);
		eo.setToBatimentRelationship(toBatiment);
		eo.setToImplantationGeoRelationship(toImplantationGeo);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EORepartBatImpGeo.
	 *
	 * @param editingContext
	 * @return EORepartBatImpGeo
	 */
	public static EORepartBatImpGeo create(EOEditingContext editingContext) {
		EORepartBatImpGeo eo = (EORepartBatImpGeo) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EORepartBatImpGeo localInstanceIn(EOEditingContext editingContext) {
		EORepartBatImpGeo localInstance = (EORepartBatImpGeo) localInstanceOfObject(editingContext, (EORepartBatImpGeo) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EORepartBatImpGeo localInstanceIn(EOEditingContext editingContext, EORepartBatImpGeo eo) {
		EORepartBatImpGeo localInstance = (eo == null) ? null : (EORepartBatImpGeo) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cImplantation() {
		return (String) storedValueForKey("cImplantation");
	}

	public void setCImplantation(String value) {
		takeStoredValueForKey(value, "cImplantation");
	}
	public String cLocal() {
		return (String) storedValueForKey("cLocal");
	}

	public void setCLocal(String value) {
		takeStoredValueForKey(value, "cLocal");
	}
	public Integer imgeoOrdre() {
		return (Integer) storedValueForKey("imgeoOrdre");
	}

	public void setImgeoOrdre(Integer value) {
		takeStoredValueForKey(value, "imgeoOrdre");
	}

	public org.cocktail.dt.server.metier.EOBatiment toBatiment() {
		return (org.cocktail.dt.server.metier.EOBatiment)storedValueForKey("toBatiment");
	}

	public void setToBatimentRelationship(org.cocktail.dt.server.metier.EOBatiment value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOBatiment oldValue = toBatiment();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toBatiment");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toBatiment");
		}
	}
  
	public org.cocktail.dt.server.metier.EOImplantationGeo toImplantationGeo() {
		return (org.cocktail.dt.server.metier.EOImplantationGeo)storedValueForKey("toImplantationGeo");
	}

	public void setToImplantationGeoRelationship(org.cocktail.dt.server.metier.EOImplantationGeo value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOImplantationGeo oldValue = toImplantationGeo();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toImplantationGeo");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toImplantationGeo");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EORepartBatImpGeo.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EORepartBatImpGeo.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EORepartBatImpGeo)
	
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
	public static EORepartBatImpGeo fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EORepartBatImpGeo fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EORepartBatImpGeo eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EORepartBatImpGeo)eoObjects.objectAtIndex(0);
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
	public static EORepartBatImpGeo fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EORepartBatImpGeo fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EORepartBatImpGeo fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EORepartBatImpGeo eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EORepartBatImpGeo)eoObjects.objectAtIndex(0);
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
	public static EORepartBatImpGeo fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EORepartBatImpGeo fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EORepartBatImpGeo eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EORepartBatImpGeo ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EORepartBatImpGeo createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EORepartBatImpGeo.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EORepartBatImpGeo.ENTITY_NAME + "' !");
		}
		else {
			EORepartBatImpGeo object = (EORepartBatImpGeo) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EORepartBatImpGeo localInstanceOfObject(EOEditingContext ec, EORepartBatImpGeo object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EORepartBatImpGeo " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EORepartBatImpGeo) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
