// _EOInterventionRepro.java
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

// DO NOT EDIT.  Make changes to EOInterventionRepro.java instead.
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
public abstract class _EOInterventionRepro extends  CktlRecord {
	public static final String ENTITY_NAME = "InterventionRepro";
	public static final String ENTITY_TABLE_NAME = "INTERVENTION_REPRO";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "intOrdre";

	public static final String COUV_DESSOUS_KEY = "couvDessous";
	public static final String COUV_DESSUS_KEY = "couvDessus";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String LIBELLE_KEY = "libelle";
	public static final String LIVRET_KEY = "livret";
	public static final String NB_COPIES_KEY = "nbCopies";
	public static final String NB_PAGES_KEY = "nbPages";
	public static final String PLASTIFIC_KEY = "plastific";
	public static final String RELIURE_KEY = "reliure";
	public static final String REMARQUES_KEY = "remarques";
	public static final String TYPE_COUV_KEY = "typeCouv";
	public static final String TYPE_DOCUMENT_KEY = "typeDocument";

	// Non visible attributes

	// Colkeys
	public static final String COUV_DESSOUS_COLKEY = "COUV_DESSOUS";
	public static final String COUV_DESSUS_COLKEY = "COUV_DESSUS";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String LIBELLE_COLKEY = "LIBELLE";
	public static final String LIVRET_COLKEY = "LIVRET";
	public static final String NB_COPIES_COLKEY = "NB_COPIES";
	public static final String NB_PAGES_COLKEY = "NB_PAGES";
	public static final String PLASTIFIC_COLKEY = "PLASTIFIC";
	public static final String RELIURE_COLKEY = "RELIURE";
	public static final String REMARQUES_COLKEY = "REMARQUES";
	public static final String TYPE_COUV_COLKEY = "TYPE_COUV";
	public static final String TYPE_DOCUMENT_COLKEY = "TYPE_DOCUMENT";

	// Non visible colkeys

	// Relationships
	public static final String TO_INTERVENTION_KEY = "toIntervention";
	public static final String TOS_INTERVENTION_REPRO_CFC_KEY = "tosInterventionReproCfc";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOInterventionRepro with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param couvDessous
	 * @param couvDessus
	 * @param intOrdre
	 * @param livret
	 * @param nbCopies
	 * @param nbPages
	 * @param plastific
	 * @param reliure
	 * @param typeCouv
	 * @param typeDocument
	 * @return EOInterventionRepro
	 */
	public static EOInterventionRepro create(EOEditingContext editingContext, String couvDessous, String couvDessus, Integer intOrdre, String livret, Integer nbCopies, Integer nbPages, String plastific, String reliure, String typeCouv, String typeDocument) {
		EOInterventionRepro eo = (EOInterventionRepro) createAndInsertInstance(editingContext);
		eo.setCouvDessous(couvDessous);
		eo.setCouvDessus(couvDessus);
		eo.setIntOrdre(intOrdre);
		eo.setLivret(livret);
		eo.setNbCopies(nbCopies);
		eo.setNbPages(nbPages);
		eo.setPlastific(plastific);
		eo.setReliure(reliure);
		eo.setTypeCouv(typeCouv);
		eo.setTypeDocument(typeDocument);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOInterventionRepro.
	 *
	 * @param editingContext
	 * @return EOInterventionRepro
	 */
	public static EOInterventionRepro create(EOEditingContext editingContext) {
		EOInterventionRepro eo = (EOInterventionRepro) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOInterventionRepro localInstanceIn(EOEditingContext editingContext) {
		EOInterventionRepro localInstance = (EOInterventionRepro) localInstanceOfObject(editingContext, (EOInterventionRepro) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOInterventionRepro localInstanceIn(EOEditingContext editingContext, EOInterventionRepro eo) {
		EOInterventionRepro localInstance = (eo == null) ? null : (EOInterventionRepro) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String couvDessous() {
		return (String) storedValueForKey("couvDessous");
	}

	public void setCouvDessous(String value) {
		takeStoredValueForKey(value, "couvDessous");
	}
	public String couvDessus() {
		return (String) storedValueForKey("couvDessus");
	}

	public void setCouvDessus(String value) {
		takeStoredValueForKey(value, "couvDessus");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public String libelle() {
		return (String) storedValueForKey("libelle");
	}

	public void setLibelle(String value) {
		takeStoredValueForKey(value, "libelle");
	}
	public String livret() {
		return (String) storedValueForKey("livret");
	}

	public void setLivret(String value) {
		takeStoredValueForKey(value, "livret");
	}
	public Integer nbCopies() {
		return (Integer) storedValueForKey("nbCopies");
	}

	public void setNbCopies(Integer value) {
		takeStoredValueForKey(value, "nbCopies");
	}
	public Integer nbPages() {
		return (Integer) storedValueForKey("nbPages");
	}

	public void setNbPages(Integer value) {
		takeStoredValueForKey(value, "nbPages");
	}
	public String plastific() {
		return (String) storedValueForKey("plastific");
	}

	public void setPlastific(String value) {
		takeStoredValueForKey(value, "plastific");
	}
	public String reliure() {
		return (String) storedValueForKey("reliure");
	}

	public void setReliure(String value) {
		takeStoredValueForKey(value, "reliure");
	}
	public String remarques() {
		return (String) storedValueForKey("remarques");
	}

	public void setRemarques(String value) {
		takeStoredValueForKey(value, "remarques");
	}
	public String typeCouv() {
		return (String) storedValueForKey("typeCouv");
	}

	public void setTypeCouv(String value) {
		takeStoredValueForKey(value, "typeCouv");
	}
	public String typeDocument() {
		return (String) storedValueForKey("typeDocument");
	}

	public void setTypeDocument(String value) {
		takeStoredValueForKey(value, "typeDocument");
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
  
	public NSArray tosInterventionReproCfc() {
		return (NSArray)storedValueForKey("tosInterventionReproCfc");
	}

	public NSArray tosInterventionReproCfc(EOQualifier qualifier) {
		return tosInterventionReproCfc(qualifier, null, false);
	}

	public NSArray tosInterventionReproCfc(EOQualifier qualifier, boolean fetch) {
		return tosInterventionReproCfc(qualifier, null, fetch);
	}

	public NSArray tosInterventionReproCfc(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOInterventionReproCfc.TO_INTERVENTION_REPRO_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOInterventionReproCfc.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosInterventionReproCfc();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosInterventionReproCfcRelationship(org.cocktail.dt.server.metier.EOInterventionReproCfc object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosInterventionReproCfc");
	}

	public void removeFromTosInterventionReproCfcRelationship(org.cocktail.dt.server.metier.EOInterventionReproCfc object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionReproCfc");
	}

	public org.cocktail.dt.server.metier.EOInterventionReproCfc createTosInterventionReproCfcRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("InterventionReproCfc");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosInterventionReproCfc");
		return (org.cocktail.dt.server.metier.EOInterventionReproCfc) eo;
	}

	public void deleteTosInterventionReproCfcRelationship(org.cocktail.dt.server.metier.EOInterventionReproCfc object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosInterventionReproCfc");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosInterventionReproCfcRelationships() {
		Enumeration objects = tosInterventionReproCfc().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosInterventionReproCfcRelationship((org.cocktail.dt.server.metier.EOInterventionReproCfc)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOInterventionRepro.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOInterventionRepro.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOInterventionRepro)
	
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
	public static EOInterventionRepro fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOInterventionRepro fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOInterventionRepro eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOInterventionRepro)eoObjects.objectAtIndex(0);
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
	public static EOInterventionRepro fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOInterventionRepro fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOInterventionRepro fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOInterventionRepro eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOInterventionRepro)eoObjects.objectAtIndex(0);
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
	public static EOInterventionRepro fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOInterventionRepro fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOInterventionRepro eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOInterventionRepro ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOInterventionRepro createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOInterventionRepro.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOInterventionRepro.ENTITY_NAME + "' !");
		}
		else {
			EOInterventionRepro object = (EOInterventionRepro) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOInterventionRepro localInstanceOfObject(EOEditingContext ec, EOInterventionRepro object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOInterventionRepro " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOInterventionRepro) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
