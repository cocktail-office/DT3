// _EOImpParc.java
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

// DO NOT EDIT.  Make changes to EOImpParc.java instead.
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
public abstract class _EOImpParc extends  CktlRecord {
	public static final String ENTITY_NAME = "ImpParc";
	public static final String ENTITY_TABLE_NAME = "PARC_INFO.IMP_PARC";

	// Attributes

	public static final String CNF_LIBELLE_KEY = "cnfLibelle";
	public static final String CNF_ORDRE_KEY = "cnfOrdre";
	public static final String CNF_SERVICE_KEY = "cnfService";
	public static final String CNF_TYPE_KEY = "cnfType";
	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String EML_SERIE_KEY = "emlSerie";
	public static final String INFOS_KEY = "infos";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String SAL_NUMERO_KEY = "salNumero";
	public static final String STY_FABRICANT_KEY = "styFabricant";
	public static final String STY_MODELE_KEY = "styModele";
	public static final String TYP_LIBELLE_KEY = "typLibelle";

	// Non visible attributes

	// Colkeys
	public static final String CNF_LIBELLE_COLKEY = "CNF_LIBELLE";
	public static final String CNF_ORDRE_COLKEY = "CNF_ORDRE";
	public static final String CNF_SERVICE_COLKEY = "CNF_SERVICE";
	public static final String CNF_TYPE_COLKEY = "CNF_TYPE";
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String EML_SERIE_COLKEY = "EML_SERIE";
	public static final String INFOS_COLKEY = "INFOS";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String SAL_NUMERO_COLKEY = "SAL_NUMERO";
	public static final String STY_FABRICANT_COLKEY = "$attribute.columnName";
	public static final String STY_MODELE_COLKEY = "STY_MODELE";
	public static final String TYP_LIBELLE_COLKEY = "TYP_LIBELLE";

	// Non visible colkeys

	// Relationships

	// Create / Init methods

	/**
	 * Creates and inserts a new EOImpParc with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cnfLibelle
	 * @param cnfOrdre
	 * @param cnfService
	 * @param cnfType
	 * @param cStructure
	 * @param emlSerie
	 * @param infos
	 * @param noIndividu
	 * @param salNumero
	 * @param styFabricant
	 * @param styModele
	 * @param typLibelle
	 * @return EOImpParc
	 */
	public static EOImpParc create(EOEditingContext editingContext, String cnfLibelle, Integer cnfOrdre, String cnfService, String cnfType, String cStructure, String emlSerie, String infos, Integer noIndividu, Integer salNumero, String styFabricant, String styModele, String typLibelle) {
		EOImpParc eo = (EOImpParc) createAndInsertInstance(editingContext);
		eo.setCnfLibelle(cnfLibelle);
		eo.setCnfOrdre(cnfOrdre);
		eo.setCnfService(cnfService);
		eo.setCnfType(cnfType);
		eo.setCStructure(cStructure);
		eo.setEmlSerie(emlSerie);
		eo.setInfos(infos);
		eo.setNoIndividu(noIndividu);
		eo.setSalNumero(salNumero);
		eo.setStyFabricant(styFabricant);
		eo.setStyModele(styModele);
		eo.setTypLibelle(typLibelle);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOImpParc.
	 *
	 * @param editingContext
	 * @return EOImpParc
	 */
	public static EOImpParc create(EOEditingContext editingContext) {
		EOImpParc eo = (EOImpParc) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOImpParc localInstanceIn(EOEditingContext editingContext) {
		EOImpParc localInstance = (EOImpParc) localInstanceOfObject(editingContext, (EOImpParc) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOImpParc localInstanceIn(EOEditingContext editingContext, EOImpParc eo) {
		EOImpParc localInstance = (eo == null) ? null : (EOImpParc) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cnfLibelle() {
		return (String) storedValueForKey("cnfLibelle");
	}

	public void setCnfLibelle(String value) {
		takeStoredValueForKey(value, "cnfLibelle");
	}
	public Integer cnfOrdre() {
		return (Integer) storedValueForKey("cnfOrdre");
	}

	public void setCnfOrdre(Integer value) {
		takeStoredValueForKey(value, "cnfOrdre");
	}
	public String cnfService() {
		return (String) storedValueForKey("cnfService");
	}

	public void setCnfService(String value) {
		takeStoredValueForKey(value, "cnfService");
	}
	public String cnfType() {
		return (String) storedValueForKey("cnfType");
	}

	public void setCnfType(String value) {
		takeStoredValueForKey(value, "cnfType");
	}
	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public String emlSerie() {
		return (String) storedValueForKey("emlSerie");
	}

	public void setEmlSerie(String value) {
		takeStoredValueForKey(value, "emlSerie");
	}
	public String infos() {
		return (String) storedValueForKey("infos");
	}

	public void setInfos(String value) {
		takeStoredValueForKey(value, "infos");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public Integer salNumero() {
		return (Integer) storedValueForKey("salNumero");
	}

	public void setSalNumero(Integer value) {
		takeStoredValueForKey(value, "salNumero");
	}
	public String styFabricant() {
		return (String) storedValueForKey("styFabricant");
	}

	public void setStyFabricant(String value) {
		takeStoredValueForKey(value, "styFabricant");
	}
	public String styModele() {
		return (String) storedValueForKey("styModele");
	}

	public void setStyModele(String value) {
		takeStoredValueForKey(value, "styModele");
	}
	public String typLibelle() {
		return (String) storedValueForKey("typLibelle");
	}

	public void setTypLibelle(String value) {
		takeStoredValueForKey(value, "typLibelle");
	}


	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOImpParc.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOImpParc.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOImpParc)
	
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
	public static EOImpParc fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOImpParc fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOImpParc eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOImpParc)eoObjects.objectAtIndex(0);
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
	public static EOImpParc fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOImpParc fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOImpParc fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOImpParc eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOImpParc)eoObjects.objectAtIndex(0);
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
	public static EOImpParc fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOImpParc fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOImpParc eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOImpParc ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOImpParc createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOImpParc.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOImpParc.ENTITY_NAME + "' !");
		}
		else {
			EOImpParc object = (EOImpParc) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOImpParc localInstanceOfObject(EOEditingContext ec, EOImpParc object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOImpParc " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOImpParc) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
