// _EOCompte.java
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

// DO NOT EDIT.  Make changes to EOCompte.java instead.
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
public abstract class _EOCompte extends  CktlRecord {
	public static final String ENTITY_NAME = "DTCompte";
	public static final String ENTITY_TABLE_NAME = "GRHUM.COMPTE";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cptOrdre";

	public static final String CPT_DOMAINE_KEY = "cptDomaine";
	public static final String CPT_EMAIL_KEY = "cptEmail";
	public static final String CPT_LOGIN_KEY = "cptLogin";
	public static final String CPT_ORDRE_KEY = "cptOrdre";
	public static final String CPT_PASSWD_KEY = "cptPasswd";
	public static final String CPT_UID_GID_KEY = "cptUidGid";
	public static final String CPT_VLAN_KEY = "cptVlan";
	public static final String EMAIL_KEY = "email";
	public static final String PERS_ID_KEY = "persId";

	// Non visible attributes

	// Colkeys
	public static final String CPT_DOMAINE_COLKEY = "CPT_DOMAINE";
	public static final String CPT_EMAIL_COLKEY = "CPT_EMAIL";
	public static final String CPT_LOGIN_COLKEY = "CPT_LOGIN";
	public static final String CPT_ORDRE_COLKEY = "CPT_ORDRE";
	public static final String CPT_PASSWD_COLKEY = "CPT_PASSWD";
	public static final String CPT_UID_GID_COLKEY = "CPT_UID_GID";
	public static final String CPT_VLAN_COLKEY = "CPT_VLAN";
	public static final String EMAIL_COLKEY = "$attribute.columnName";
	public static final String PERS_ID_COLKEY = "PERS_ID";

	// Non visible colkeys

	// Relationships
	public static final String TOS_REPART_COMPTE_KEY = "tosRepartCompte";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOCompte with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cptDomaine
	 * @param cptEmail
	 * @param cptOrdre
	 * @param cptUidGid
	 * @param persId
	 * @return EOCompte
	 */
	public static EOCompte create(EOEditingContext editingContext, String cptDomaine, String cptEmail, Integer cptOrdre, Integer cptUidGid, Integer persId) {
		EOCompte eo = (EOCompte) createAndInsertInstance(editingContext);
		eo.setCptDomaine(cptDomaine);
		eo.setCptEmail(cptEmail);
		eo.setCptOrdre(cptOrdre);
		eo.setCptUidGid(cptUidGid);
		eo.setPersId(persId);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOCompte.
	 *
	 * @param editingContext
	 * @return EOCompte
	 */
	public static EOCompte create(EOEditingContext editingContext) {
		EOCompte eo = (EOCompte) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOCompte localInstanceIn(EOEditingContext editingContext) {
		EOCompte localInstance = (EOCompte) localInstanceOfObject(editingContext, (EOCompte) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOCompte localInstanceIn(EOEditingContext editingContext, EOCompte eo) {
		EOCompte localInstance = (eo == null) ? null : (EOCompte) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cptDomaine() {
		return (String) storedValueForKey("cptDomaine");
	}

	public void setCptDomaine(String value) {
		takeStoredValueForKey(value, "cptDomaine");
	}
	public String cptEmail() {
		return (String) storedValueForKey("cptEmail");
	}

	public void setCptEmail(String value) {
		takeStoredValueForKey(value, "cptEmail");
	}
	public String cptLogin() {
		return (String) storedValueForKey("cptLogin");
	}

	public void setCptLogin(String value) {
		takeStoredValueForKey(value, "cptLogin");
	}
	public Integer cptOrdre() {
		return (Integer) storedValueForKey("cptOrdre");
	}

	public void setCptOrdre(Integer value) {
		takeStoredValueForKey(value, "cptOrdre");
	}
	public String cptPasswd() {
		return (String) storedValueForKey("cptPasswd");
	}

	public void setCptPasswd(String value) {
		takeStoredValueForKey(value, "cptPasswd");
	}
	public Integer cptUidGid() {
		return (Integer) storedValueForKey("cptUidGid");
	}

	public void setCptUidGid(Integer value) {
		takeStoredValueForKey(value, "cptUidGid");
	}
	public String cptVlan() {
		return (String) storedValueForKey("cptVlan");
	}

	public void setCptVlan(String value) {
		takeStoredValueForKey(value, "cptVlan");
	}
	public String email() {
		return (String) storedValueForKey("email");
	}

	public void setEmail(String value) {
		takeStoredValueForKey(value, "email");
	}
	public Integer persId() {
		return (Integer) storedValueForKey("persId");
	}

	public void setPersId(Integer value) {
		takeStoredValueForKey(value, "persId");
	}

	public NSArray tosRepartCompte() {
		return (NSArray)storedValueForKey("tosRepartCompte");
	}

	public NSArray tosRepartCompte(EOQualifier qualifier) {
		return tosRepartCompte(qualifier, null, false);
	}

	public NSArray tosRepartCompte(EOQualifier qualifier, boolean fetch) {
		return tosRepartCompte(qualifier, null, fetch);
	}

	public NSArray tosRepartCompte(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EORepartCompte.TO_COMPTE_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EORepartCompte.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosRepartCompte();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosRepartCompteRelationship(org.cocktail.dt.server.metier.EORepartCompte object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartCompte");
	}

	public void removeFromTosRepartCompteRelationship(org.cocktail.dt.server.metier.EORepartCompte object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartCompte");
	}

	public org.cocktail.dt.server.metier.EORepartCompte createTosRepartCompteRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DTRepartCompte");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartCompte");
		return (org.cocktail.dt.server.metier.EORepartCompte) eo;
	}

	public void deleteTosRepartCompteRelationship(org.cocktail.dt.server.metier.EORepartCompte object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartCompte");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartCompteRelationships() {
		Enumeration objects = tosRepartCompte().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartCompteRelationship((org.cocktail.dt.server.metier.EORepartCompte)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOCompte.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOCompte.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOCompte)
	
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
	public static EOCompte fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOCompte fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOCompte eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOCompte)eoObjects.objectAtIndex(0);
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
	public static EOCompte fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOCompte fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOCompte fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOCompte eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOCompte)eoObjects.objectAtIndex(0);
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
	public static EOCompte fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOCompte fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOCompte eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOCompte ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOCompte createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOCompte.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOCompte.ENTITY_NAME + "' !");
		}
		else {
			EOCompte object = (EOCompte) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOCompte localInstanceOfObject(EOEditingContext ec, EOCompte object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOCompte " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOCompte) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
