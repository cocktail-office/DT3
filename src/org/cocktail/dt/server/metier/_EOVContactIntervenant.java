// _EOVContactIntervenant.java
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

// DO NOT EDIT.  Make changes to EOVContactIntervenant.java instead.
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
public abstract class _EOVContactIntervenant extends  CktlRecord {
	public static final String ENTITY_NAME = "VContactIntervenant";
	public static final String ENTITY_TABLE_NAME = "V_CONTACT_INTERVENANT";

	// Attributes

	public static final String C_LOCAL_KEY = "cLocal";
	public static final String C_STRUCT_SERVICE_KEY = "cStructService";
	public static final String C_STRUCTURE_DEST_KEY = "cStructureDest";
	public static final String CT_EMAIL_KEY = "ctEmail";
	public static final String CT_ORDRE_KEY = "ctOrdre";
	public static final String INT_DATE_CREATION_KEY = "intDateCreation";
	public static final String INT_ETAT_KEY = "intEtat";
	public static final String LC_STRUCTURE_KEY = "lcStructure";
	public static final String NO_IND_INTERVENANT_KEY = "noIndIntervenant";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NOM_ET_PRENOM_KEY = "nomEtPrenom";

	// Non visible attributes

	// Colkeys
	public static final String C_LOCAL_COLKEY = "C_LOCAL";
	public static final String C_STRUCT_SERVICE_COLKEY = "C_STRUCTURE_CONTACT";
	public static final String C_STRUCTURE_DEST_COLKEY = "C_STRUCTURE_DEST";
	public static final String CT_EMAIL_COLKEY = "CT_EMAIL";
	public static final String CT_ORDRE_COLKEY = "CT_ORDRE";
	public static final String INT_DATE_CREATION_COLKEY = "INT_DATE_CREATION";
	public static final String INT_ETAT_COLKEY = "INT_ETAT";
	public static final String LC_STRUCTURE_COLKEY = "LC_STRUCTURE";
	public static final String NO_IND_INTERVENANT_COLKEY = "NO_IND_INTERVENANT";
	public static final String NO_INDIVIDU_COLKEY = "NO_IND_CONTACT";
	public static final String NOM_ET_PRENOM_COLKEY = "NOM_ET_PRENOM";

	// Non visible colkeys

	// Relationships

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVContactIntervenant with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cStructureDest
	 * @param ctOrdre
	 * @param intDateCreation
	 * @param intEtat
	 * @param noIndIntervenant
	 * @param noIndividu
	 * @return EOVContactIntervenant
	 */
	public static EOVContactIntervenant create(EOEditingContext editingContext, String cStructureDest, Integer ctOrdre, NSTimestamp intDateCreation, String intEtat, Integer noIndIntervenant, Integer noIndividu) {
		EOVContactIntervenant eo = (EOVContactIntervenant) createAndInsertInstance(editingContext);
		eo.setCStructureDest(cStructureDest);
		eo.setCtOrdre(ctOrdre);
		eo.setIntDateCreation(intDateCreation);
		eo.setIntEtat(intEtat);
		eo.setNoIndIntervenant(noIndIntervenant);
		eo.setNoIndividu(noIndividu);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVContactIntervenant.
	 *
	 * @param editingContext
	 * @return EOVContactIntervenant
	 */
	public static EOVContactIntervenant create(EOEditingContext editingContext) {
		EOVContactIntervenant eo = (EOVContactIntervenant) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVContactIntervenant localInstanceIn(EOEditingContext editingContext) {
		EOVContactIntervenant localInstance = (EOVContactIntervenant) localInstanceOfObject(editingContext, (EOVContactIntervenant) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVContactIntervenant localInstanceIn(EOEditingContext editingContext, EOVContactIntervenant eo) {
		EOVContactIntervenant localInstance = (eo == null) ? null : (EOVContactIntervenant) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cLocal() {
		return (String) storedValueForKey("cLocal");
	}

	public void setCLocal(String value) {
		takeStoredValueForKey(value, "cLocal");
	}
	public String cStructService() {
		return (String) storedValueForKey("cStructService");
	}

	public void setCStructService(String value) {
		takeStoredValueForKey(value, "cStructService");
	}
	public String cStructureDest() {
		return (String) storedValueForKey("cStructureDest");
	}

	public void setCStructureDest(String value) {
		takeStoredValueForKey(value, "cStructureDest");
	}
	public String ctEmail() {
		return (String) storedValueForKey("ctEmail");
	}

	public void setCtEmail(String value) {
		takeStoredValueForKey(value, "ctEmail");
	}
	public Integer ctOrdre() {
		return (Integer) storedValueForKey("ctOrdre");
	}

	public void setCtOrdre(Integer value) {
		takeStoredValueForKey(value, "ctOrdre");
	}
	public NSTimestamp intDateCreation() {
		return (NSTimestamp) storedValueForKey("intDateCreation");
	}

	public void setIntDateCreation(NSTimestamp value) {
		takeStoredValueForKey(value, "intDateCreation");
	}
	public String intEtat() {
		return (String) storedValueForKey("intEtat");
	}

	public void setIntEtat(String value) {
		takeStoredValueForKey(value, "intEtat");
	}
	public String lcStructure() {
		return (String) storedValueForKey("lcStructure");
	}

	public void setLcStructure(String value) {
		takeStoredValueForKey(value, "lcStructure");
	}
	public Integer noIndIntervenant() {
		return (Integer) storedValueForKey("noIndIntervenant");
	}

	public void setNoIndIntervenant(Integer value) {
		takeStoredValueForKey(value, "noIndIntervenant");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String nomEtPrenom() {
		return (String) storedValueForKey("nomEtPrenom");
	}

	public void setNomEtPrenom(String value) {
		takeStoredValueForKey(value, "nomEtPrenom");
	}


	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVContactIntervenant.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVContactIntervenant.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVContactIntervenant)
	
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
	public static EOVContactIntervenant fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVContactIntervenant fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVContactIntervenant eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVContactIntervenant)eoObjects.objectAtIndex(0);
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
	public static EOVContactIntervenant fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVContactIntervenant fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVContactIntervenant fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVContactIntervenant eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVContactIntervenant)eoObjects.objectAtIndex(0);
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
	public static EOVContactIntervenant fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVContactIntervenant fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVContactIntervenant eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVContactIntervenant ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVContactIntervenant createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVContactIntervenant.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVContactIntervenant.ENTITY_NAME + "' !");
		}
		else {
			EOVContactIntervenant object = (EOVContactIntervenant) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVContactIntervenant localInstanceOfObject(EOEditingContext ec, EOVContactIntervenant object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVContactIntervenant " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVContactIntervenant) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
