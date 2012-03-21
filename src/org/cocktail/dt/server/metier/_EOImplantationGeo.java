// _EOImplantationGeo.java
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

// DO NOT EDIT.  Make changes to EOImplantationGeo.java instead.
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
public abstract class _EOImplantationGeo extends  CktlRecord {
	public static final String ENTITY_NAME = "ImplantationGeo";
	public static final String ENTITY_TABLE_NAME = "GRHUM.IMPLANTATION_GEO";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cImplantation";

	public static final String C_IMPLANTATION_KEY = "cImplantation";
	public static final String D_DEB_VAL_KEY = "dDebVal";
	public static final String D_FIN_VAL_KEY = "dFinVal";
	public static final String IMGEO_ORDRE_KEY = "imgeoOrdre";
	public static final String LC_IMPLANTATION_GEO_KEY = "lcImplantationGeo";
	public static final String LL_IMPLANTATION_GEO_KEY = "llImplantationGeo";

	// Non visible attributes

	// Colkeys
	public static final String C_IMPLANTATION_COLKEY = "C_IMPLANTATION";
	public static final String D_DEB_VAL_COLKEY = "D_DEB_VAL";
	public static final String D_FIN_VAL_COLKEY = "D_FIN_VAL";
	public static final String IMGEO_ORDRE_COLKEY = "IMGEO_ORDRE";
	public static final String LC_IMPLANTATION_GEO_COLKEY = "LC_IMPLANTATION_GEO";
	public static final String LL_IMPLANTATION_GEO_COLKEY = "LL_IMPLANTATION_GEO";

	// Non visible colkeys

	// Relationships
	public static final String TOS_BATIMENT_KEY = "tosBatiment";
	public static final String TOS_REPART_BAT_IMP_GEO_KEY = "tosRepartBatImpGeo";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOImplantationGeo with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cImplantation
	 * @param imgeoOrdre
	 * @param lcImplantationGeo
	 * @param llImplantationGeo
	 * @return EOImplantationGeo
	 */
	public static EOImplantationGeo create(EOEditingContext editingContext, String cImplantation, Integer imgeoOrdre, String lcImplantationGeo, String llImplantationGeo) {
		EOImplantationGeo eo = (EOImplantationGeo) createAndInsertInstance(editingContext);
		eo.setCImplantation(cImplantation);
		eo.setImgeoOrdre(imgeoOrdre);
		eo.setLcImplantationGeo(lcImplantationGeo);
		eo.setLlImplantationGeo(llImplantationGeo);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOImplantationGeo.
	 *
	 * @param editingContext
	 * @return EOImplantationGeo
	 */
	public static EOImplantationGeo create(EOEditingContext editingContext) {
		EOImplantationGeo eo = (EOImplantationGeo) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOImplantationGeo localInstanceIn(EOEditingContext editingContext) {
		EOImplantationGeo localInstance = (EOImplantationGeo) localInstanceOfObject(editingContext, (EOImplantationGeo) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOImplantationGeo localInstanceIn(EOEditingContext editingContext, EOImplantationGeo eo) {
		EOImplantationGeo localInstance = (eo == null) ? null : (EOImplantationGeo) localInstanceOfObject(editingContext, eo);
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
	public NSTimestamp dDebVal() {
		return (NSTimestamp) storedValueForKey("dDebVal");
	}

	public void setDDebVal(NSTimestamp value) {
		takeStoredValueForKey(value, "dDebVal");
	}
	public NSTimestamp dFinVal() {
		return (NSTimestamp) storedValueForKey("dFinVal");
	}

	public void setDFinVal(NSTimestamp value) {
		takeStoredValueForKey(value, "dFinVal");
	}
	public Integer imgeoOrdre() {
		return (Integer) storedValueForKey("imgeoOrdre");
	}

	public void setImgeoOrdre(Integer value) {
		takeStoredValueForKey(value, "imgeoOrdre");
	}
	public String lcImplantationGeo() {
		return (String) storedValueForKey("lcImplantationGeo");
	}

	public void setLcImplantationGeo(String value) {
		takeStoredValueForKey(value, "lcImplantationGeo");
	}
	public String llImplantationGeo() {
		return (String) storedValueForKey("llImplantationGeo");
	}

	public void setLlImplantationGeo(String value) {
		takeStoredValueForKey(value, "llImplantationGeo");
	}

	public NSArray tosBatiment() {
		return (NSArray)storedValueForKey("tosBatiment");
	}

	public NSArray tosBatiment(EOQualifier qualifier) {
		return tosBatiment(qualifier, null);
	}

	public NSArray tosBatiment(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosBatiment();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosBatimentRelationship(org.cocktail.dt.server.metier.EOBatiment object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosBatiment");
	}

	public void removeFromTosBatimentRelationship(org.cocktail.dt.server.metier.EOBatiment object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosBatiment");
	}

	public org.cocktail.dt.server.metier.EOBatiment createTosBatimentRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Batiment");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosBatiment");
		return (org.cocktail.dt.server.metier.EOBatiment) eo;
	}

	public void deleteTosBatimentRelationship(org.cocktail.dt.server.metier.EOBatiment object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosBatiment");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosBatimentRelationships() {
		Enumeration objects = tosBatiment().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosBatimentRelationship((org.cocktail.dt.server.metier.EOBatiment)objects.nextElement());
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
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EORepartBatImpGeo.TO_IMPLANTATION_GEO_KEY, EOQualifier.QualifierOperatorEqual, this);
			
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

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOImplantationGeo.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOImplantationGeo.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOImplantationGeo)
	
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
	public static EOImplantationGeo fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOImplantationGeo fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOImplantationGeo eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOImplantationGeo)eoObjects.objectAtIndex(0);
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
	public static EOImplantationGeo fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOImplantationGeo fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOImplantationGeo fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOImplantationGeo eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOImplantationGeo)eoObjects.objectAtIndex(0);
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
	public static EOImplantationGeo fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOImplantationGeo fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOImplantationGeo eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOImplantationGeo ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOImplantationGeo createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOImplantationGeo.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOImplantationGeo.ENTITY_NAME + "' !");
		}
		else {
			EOImplantationGeo object = (EOImplantationGeo) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOImplantationGeo localInstanceOfObject(EOEditingContext ec, EOImplantationGeo object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOImplantationGeo " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOImplantationGeo) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
