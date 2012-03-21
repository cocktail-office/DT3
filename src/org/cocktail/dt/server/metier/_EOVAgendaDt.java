// _EOVAgendaDt.java
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

// DO NOT EDIT.  Make changes to EOVAgendaDt.java instead.
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
public abstract class _EOVAgendaDt extends  CktlRecord {
	public static final String ENTITY_NAME = "VAgendaDt";
	public static final String ENTITY_TABLE_NAME = "V_AGENDA_DT";

	// Attributes

	public static final String CODE_DONNEES_KEY = "codeDonnees";
	public static final String CODE_TYPE_KEY = "codeType";
	public static final String COMMENTAIRE_KEY = "commentaire";
	public static final String DATE_DEB_KEY = "dateDeb";
	public static final String DATE_FIN_KEY = "dateFin";
	public static final String LIBELLE_KEY = "libelle";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NO_REFERENCE_KEY = "noReference";
	public static final String NO_TYPE_KEY = "noType";

	// Non visible attributes

	// Colkeys
	public static final String CODE_DONNEES_COLKEY = "CODE_DONNEES";
	public static final String CODE_TYPE_COLKEY = "CODE_TYPE";
	public static final String COMMENTAIRE_COLKEY = "COMMENTAIRE";
	public static final String DATE_DEB_COLKEY = "DATE_DEB";
	public static final String DATE_FIN_COLKEY = "DATE_FIN";
	public static final String LIBELLE_COLKEY = "LIBELLE";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String NO_REFERENCE_COLKEY = "NO_REFERENCE";
	public static final String NO_TYPE_COLKEY = "NO_TYPE";

	// Non visible colkeys

	// Relationships
	public static final String TOS_PREF_DROITS_KEY = "tosPrefDroits";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVAgendaDt with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param codeType
	 * @param noType
	 * @return EOVAgendaDt
	 */
	public static EOVAgendaDt create(EOEditingContext editingContext, String codeType, Integer noType) {
		EOVAgendaDt eo = (EOVAgendaDt) createAndInsertInstance(editingContext);
		eo.setCodeType(codeType);
		eo.setNoType(noType);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVAgendaDt.
	 *
	 * @param editingContext
	 * @return EOVAgendaDt
	 */
	public static EOVAgendaDt create(EOEditingContext editingContext) {
		EOVAgendaDt eo = (EOVAgendaDt) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVAgendaDt localInstanceIn(EOEditingContext editingContext) {
		EOVAgendaDt localInstance = (EOVAgendaDt) localInstanceOfObject(editingContext, (EOVAgendaDt) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVAgendaDt localInstanceIn(EOEditingContext editingContext, EOVAgendaDt eo) {
		EOVAgendaDt localInstance = (eo == null) ? null : (EOVAgendaDt) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String codeDonnees() {
		return (String) storedValueForKey("codeDonnees");
	}

	public void setCodeDonnees(String value) {
		takeStoredValueForKey(value, "codeDonnees");
	}
	public String codeType() {
		return (String) storedValueForKey("codeType");
	}

	public void setCodeType(String value) {
		takeStoredValueForKey(value, "codeType");
	}
	public String commentaire() {
		return (String) storedValueForKey("commentaire");
	}

	public void setCommentaire(String value) {
		takeStoredValueForKey(value, "commentaire");
	}
	public NSTimestamp dateDeb() {
		return (NSTimestamp) storedValueForKey("dateDeb");
	}

	public void setDateDeb(NSTimestamp value) {
		takeStoredValueForKey(value, "dateDeb");
	}
	public NSTimestamp dateFin() {
		return (NSTimestamp) storedValueForKey("dateFin");
	}

	public void setDateFin(NSTimestamp value) {
		takeStoredValueForKey(value, "dateFin");
	}
	public String libelle() {
		return (String) storedValueForKey("libelle");
	}

	public void setLibelle(String value) {
		takeStoredValueForKey(value, "libelle");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public Integer noReference() {
		return (Integer) storedValueForKey("noReference");
	}

	public void setNoReference(Integer value) {
		takeStoredValueForKey(value, "noReference");
	}
	public Integer noType() {
		return (Integer) storedValueForKey("noType");
	}

	public void setNoType(Integer value) {
		takeStoredValueForKey(value, "noType");
	}

	public NSArray tosPrefDroits() {
		return (NSArray)storedValueForKey("tosPrefDroits");
	}

	public NSArray tosPrefDroits(EOQualifier qualifier) {
		return tosPrefDroits(qualifier, null);
	}

	public NSArray tosPrefDroits(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosPrefDroits();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
	}

	public void removeFromTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
	}

	public org.cocktail.dt.server.metier.EOPrefDroits createTosPrefDroitsRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("PrefDroits");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosPrefDroits");
		return (org.cocktail.dt.server.metier.EOPrefDroits) eo;
	}

	public void deleteTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosPrefDroitsRelationships() {
		Enumeration objects = tosPrefDroits().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosPrefDroitsRelationship((org.cocktail.dt.server.metier.EOPrefDroits)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVAgendaDt.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVAgendaDt.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVAgendaDt)
	
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
	public static EOVAgendaDt fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVAgendaDt fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVAgendaDt eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVAgendaDt)eoObjects.objectAtIndex(0);
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
	public static EOVAgendaDt fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVAgendaDt fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVAgendaDt fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVAgendaDt eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVAgendaDt)eoObjects.objectAtIndex(0);
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
	public static EOVAgendaDt fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVAgendaDt fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVAgendaDt eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVAgendaDt ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVAgendaDt createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVAgendaDt.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVAgendaDt.ENTITY_NAME + "' !");
		}
		else {
			EOVAgendaDt object = (EOVAgendaDt) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVAgendaDt localInstanceOfObject(EOEditingContext ec, EOVAgendaDt object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVAgendaDt " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVAgendaDt) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
