// _EOInterventionReproCfc.java
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

// DO NOT EDIT.  Make changes to EOInterventionReproCfc.java instead.
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
public abstract class _EOInterventionReproCfc extends  CktlRecord {
	public static final String ENTITY_NAME = "InterventionReproCfc";
	public static final String ENTITY_TABLE_NAME = "INTERVENTION_REPRO_CFC";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cfcOrdre";

	public static final String AUTEURS_KEY = "auteurs";
	public static final String CFC_ORDRE_KEY = "cfcOrdre";
	public static final String D_CREATION_KEY = "dCreation";
	public static final String D_MODIFICATION_KEY = "dModification";
	public static final String EDITEURS_KEY = "editeurs";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String NB_EXEMPLAIRES_KEY = "nbExemplaires";
	public static final String NB_PAGES_KEY = "nbPages";
	public static final String TITRE_KEY = "titre";

	// Non visible attributes

	// Colkeys
	public static final String AUTEURS_COLKEY = "AUTEURS";
	public static final String CFC_ORDRE_COLKEY = "CFC_ORDRE";
	public static final String D_CREATION_COLKEY = "D_CREATION";
	public static final String D_MODIFICATION_COLKEY = "D_MODIFICATION";
	public static final String EDITEURS_COLKEY = "EDITEURS";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String NB_EXEMPLAIRES_COLKEY = "NB_EXEMPLAIRES";
	public static final String NB_PAGES_COLKEY = "NB_PAGES";
	public static final String TITRE_COLKEY = "TITRE";

	// Non visible colkeys

	// Relationships
	public static final String TO_INTERVENTION_REPRO_KEY = "toInterventionRepro";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOInterventionReproCfc with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cfcOrdre
	 * @param dCreation
	 * @param dModification
	 * @param editeurs
	 * @param intOrdre
	 * @param nbExemplaires
	 * @param nbPages
	 * @param titre
	 * @return EOInterventionReproCfc
	 */
	public static EOInterventionReproCfc create(EOEditingContext editingContext, Integer cfcOrdre, NSTimestamp dCreation, NSTimestamp dModification, String editeurs, Integer intOrdre, Integer nbExemplaires, Integer nbPages, String titre) {
		EOInterventionReproCfc eo = (EOInterventionReproCfc) createAndInsertInstance(editingContext);
		eo.setCfcOrdre(cfcOrdre);
		eo.setDCreation(dCreation);
		eo.setDModification(dModification);
		eo.setEditeurs(editeurs);
		eo.setIntOrdre(intOrdre);
		eo.setNbExemplaires(nbExemplaires);
		eo.setNbPages(nbPages);
		eo.setTitre(titre);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOInterventionReproCfc.
	 *
	 * @param editingContext
	 * @return EOInterventionReproCfc
	 */
	public static EOInterventionReproCfc create(EOEditingContext editingContext) {
		EOInterventionReproCfc eo = (EOInterventionReproCfc) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOInterventionReproCfc localInstanceIn(EOEditingContext editingContext) {
		EOInterventionReproCfc localInstance = (EOInterventionReproCfc) localInstanceOfObject(editingContext, (EOInterventionReproCfc) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOInterventionReproCfc localInstanceIn(EOEditingContext editingContext, EOInterventionReproCfc eo) {
		EOInterventionReproCfc localInstance = (eo == null) ? null : (EOInterventionReproCfc) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String auteurs() {
		return (String) storedValueForKey("auteurs");
	}

	public void setAuteurs(String value) {
		takeStoredValueForKey(value, "auteurs");
	}
	public Integer cfcOrdre() {
		return (Integer) storedValueForKey("cfcOrdre");
	}

	public void setCfcOrdre(Integer value) {
		takeStoredValueForKey(value, "cfcOrdre");
	}
	public NSTimestamp dCreation() {
		return (NSTimestamp) storedValueForKey("dCreation");
	}

	public void setDCreation(NSTimestamp value) {
		takeStoredValueForKey(value, "dCreation");
	}
	public NSTimestamp dModification() {
		return (NSTimestamp) storedValueForKey("dModification");
	}

	public void setDModification(NSTimestamp value) {
		takeStoredValueForKey(value, "dModification");
	}
	public String editeurs() {
		return (String) storedValueForKey("editeurs");
	}

	public void setEditeurs(String value) {
		takeStoredValueForKey(value, "editeurs");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public Integer nbExemplaires() {
		return (Integer) storedValueForKey("nbExemplaires");
	}

	public void setNbExemplaires(Integer value) {
		takeStoredValueForKey(value, "nbExemplaires");
	}
	public Integer nbPages() {
		return (Integer) storedValueForKey("nbPages");
	}

	public void setNbPages(Integer value) {
		takeStoredValueForKey(value, "nbPages");
	}
	public String titre() {
		return (String) storedValueForKey("titre");
	}

	public void setTitre(String value) {
		takeStoredValueForKey(value, "titre");
	}

	public org.cocktail.dt.server.metier.EOInterventionRepro toInterventionRepro() {
		return (org.cocktail.dt.server.metier.EOInterventionRepro)storedValueForKey("toInterventionRepro");
	}

	public void setToInterventionReproRelationship(org.cocktail.dt.server.metier.EOInterventionRepro value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOInterventionRepro oldValue = toInterventionRepro();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toInterventionRepro");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toInterventionRepro");
		}
	}
  

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOInterventionReproCfc.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOInterventionReproCfc.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOInterventionReproCfc)
	
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
	public static EOInterventionReproCfc fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOInterventionReproCfc fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOInterventionReproCfc eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOInterventionReproCfc)eoObjects.objectAtIndex(0);
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
	public static EOInterventionReproCfc fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOInterventionReproCfc fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOInterventionReproCfc fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOInterventionReproCfc eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOInterventionReproCfc)eoObjects.objectAtIndex(0);
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
	public static EOInterventionReproCfc fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOInterventionReproCfc fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOInterventionReproCfc eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOInterventionReproCfc ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOInterventionReproCfc createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOInterventionReproCfc.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOInterventionReproCfc.ENTITY_NAME + "' !");
		}
		else {
			EOInterventionReproCfc object = (EOInterventionReproCfc) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOInterventionReproCfc localInstanceOfObject(EOEditingContext ec, EOInterventionReproCfc object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOInterventionReproCfc " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOInterventionReproCfc) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
