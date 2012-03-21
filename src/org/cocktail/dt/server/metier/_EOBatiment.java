// _EOBatiment.java
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

// DO NOT EDIT.  Make changes to EOBatiment.java instead.
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
public abstract class _EOBatiment extends  CktlRecord {
	public static final String ENTITY_NAME = "Batiment";
	public static final String ENTITY_TABLE_NAME = "GRHUM.LOCAL";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cLocal";

	public static final String ADRESSE_INTERNE_KEY = "adresseInterne";
	public static final String APPELLATION_KEY = "appellation";
	public static final String C_LOCAL_KEY = "cLocal";
	public static final String ID_ADRESSE_ADMIN_KEY = "idAdresseAdmin";

	// Non visible attributes

	// Colkeys
	public static final String ADRESSE_INTERNE_COLKEY = "ADRESSE_INTERNE";
	public static final String APPELLATION_COLKEY = "APPELLATION";
	public static final String C_LOCAL_COLKEY = "C_LOCAL";
	public static final String ID_ADRESSE_ADMIN_COLKEY = "ID_ADRESSE_ADMIN";

	// Non visible colkeys

	// Relationships
	public static final String TOS_IMPLANTATION_GEO_KEY = "tosImplantationGeo";
	public static final String TOS_REPART_BAT_IMP_GEO_KEY = "tosRepartBatImpGeo";
	public static final String TOS_SALLES_KEY = "tosSalles";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOBatiment with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param adresseInterne
	 * @param cLocal
	 * @param idAdresseAdmin
	 * @return EOBatiment
	 */
	public static EOBatiment create(EOEditingContext editingContext, String adresseInterne, String cLocal, Integer idAdresseAdmin) {
		EOBatiment eo = (EOBatiment) createAndInsertInstance(editingContext);
		eo.setAdresseInterne(adresseInterne);
		eo.setCLocal(cLocal);
		eo.setIdAdresseAdmin(idAdresseAdmin);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOBatiment.
	 *
	 * @param editingContext
	 * @return EOBatiment
	 */
	public static EOBatiment create(EOEditingContext editingContext) {
		EOBatiment eo = (EOBatiment) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOBatiment localInstanceIn(EOEditingContext editingContext) {
		EOBatiment localInstance = (EOBatiment) localInstanceOfObject(editingContext, (EOBatiment) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOBatiment localInstanceIn(EOEditingContext editingContext, EOBatiment eo) {
		EOBatiment localInstance = (eo == null) ? null : (EOBatiment) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String adresseInterne() {
		return (String) storedValueForKey("adresseInterne");
	}

	public void setAdresseInterne(String value) {
		takeStoredValueForKey(value, "adresseInterne");
	}
	public String appellation() {
		return (String) storedValueForKey("appellation");
	}

	public void setAppellation(String value) {
		takeStoredValueForKey(value, "appellation");
	}
	public String cLocal() {
		return (String) storedValueForKey("cLocal");
	}

	public void setCLocal(String value) {
		takeStoredValueForKey(value, "cLocal");
	}
	public Integer idAdresseAdmin() {
		return (Integer) storedValueForKey("idAdresseAdmin");
	}

	public void setIdAdresseAdmin(Integer value) {
		takeStoredValueForKey(value, "idAdresseAdmin");
	}

	public NSArray tosImplantationGeo() {
		return (NSArray)storedValueForKey("tosImplantationGeo");
	}

	public NSArray tosImplantationGeo(EOQualifier qualifier) {
		return tosImplantationGeo(qualifier, null);
	}

	public NSArray tosImplantationGeo(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosImplantationGeo();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosImplantationGeoRelationship(org.cocktail.dt.server.metier.EOImplantationGeo object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosImplantationGeo");
	}

	public void removeFromTosImplantationGeoRelationship(org.cocktail.dt.server.metier.EOImplantationGeo object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosImplantationGeo");
	}

	public org.cocktail.dt.server.metier.EOImplantationGeo createTosImplantationGeoRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ImplantationGeo");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosImplantationGeo");
		return (org.cocktail.dt.server.metier.EOImplantationGeo) eo;
	}

	public void deleteTosImplantationGeoRelationship(org.cocktail.dt.server.metier.EOImplantationGeo object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosImplantationGeo");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosImplantationGeoRelationships() {
		Enumeration objects = tosImplantationGeo().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosImplantationGeoRelationship((org.cocktail.dt.server.metier.EOImplantationGeo)objects.nextElement());
		}
	}
	public NSArray tosRepartBatImpGeo() {
		return (NSArray)storedValueForKey("tosRepartBatImpGeo");
	}

	public NSArray tosRepartBatImpGeo(EOQualifier qualifier) {
		return tosRepartBatImpGeo(qualifier, null, false);
	}

	public NSArray tosRepartBatImpGeo(EOQualifier qualifier, boolean fetch) {
		return tosRepartBatImpGeo(qualifier, null, fetch);
	}

	public NSArray tosRepartBatImpGeo(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EORepartBatImpGeo.TO_BATIMENT_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EORepartBatImpGeo.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosRepartBatImpGeo();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosRepartBatImpGeoRelationship(org.cocktail.dt.server.metier.EORepartBatImpGeo object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartBatImpGeo");
	}

	public void removeFromTosRepartBatImpGeoRelationship(org.cocktail.dt.server.metier.EORepartBatImpGeo object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBatImpGeo");
	}

	public org.cocktail.dt.server.metier.EORepartBatImpGeo createTosRepartBatImpGeoRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("RepartBatImpGeo");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartBatImpGeo");
		return (org.cocktail.dt.server.metier.EORepartBatImpGeo) eo;
	}

	public void deleteTosRepartBatImpGeoRelationship(org.cocktail.dt.server.metier.EORepartBatImpGeo object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBatImpGeo");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartBatImpGeoRelationships() {
		Enumeration objects = tosRepartBatImpGeo().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartBatImpGeoRelationship((org.cocktail.dt.server.metier.EORepartBatImpGeo)objects.nextElement());
		}
	}
	public NSArray tosSalles() {
		return (NSArray)storedValueForKey("tosSalles");
	}

	public NSArray tosSalles(EOQualifier qualifier) {
		return tosSalles(qualifier, null, false);
	}

	public NSArray tosSalles(EOQualifier qualifier, boolean fetch) {
		return tosSalles(qualifier, null, fetch);
	}

	public NSArray tosSalles(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOSalles.TO_LOCAL_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOSalles.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosSalles();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosSallesRelationship(org.cocktail.dt.server.metier.EOSalles object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosSalles");
	}

	public void removeFromTosSallesRelationship(org.cocktail.dt.server.metier.EOSalles object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosSalles");
	}

	public org.cocktail.dt.server.metier.EOSalles createTosSallesRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Salles");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosSalles");
		return (org.cocktail.dt.server.metier.EOSalles) eo;
	}

	public void deleteTosSallesRelationship(org.cocktail.dt.server.metier.EOSalles object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosSalles");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosSallesRelationships() {
		Enumeration objects = tosSalles().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosSallesRelationship((org.cocktail.dt.server.metier.EOSalles)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOBatiment.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOBatiment.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOBatiment)
	
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
	public static EOBatiment fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOBatiment fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOBatiment eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOBatiment)eoObjects.objectAtIndex(0);
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
	public static EOBatiment fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOBatiment fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOBatiment fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOBatiment eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOBatiment)eoObjects.objectAtIndex(0);
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
	public static EOBatiment fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOBatiment fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOBatiment eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOBatiment ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOBatiment createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOBatiment.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOBatiment.ENTITY_NAME + "' !");
		}
		else {
			EOBatiment object = (EOBatiment) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOBatiment localInstanceOfObject(EOEditingContext ec, EOBatiment object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOBatiment " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOBatiment) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
