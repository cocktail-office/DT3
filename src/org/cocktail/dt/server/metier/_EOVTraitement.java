// _EOVTraitement.java
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

// DO NOT EDIT.  Make changes to EOVTraitement.java instead.
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
public abstract class _EOVTraitement extends  CktlRecord {
	public static final String ENTITY_NAME = "VTraitement";
	public static final String ENTITY_TABLE_NAME = "V_TRAITEMENT";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "traOrdre";

	public static final String ACT_LIBELLE_KEY = "actLibelle";
	public static final String ACT_ORDRE_KEY = "actOrdre";
	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String INT_CLE_SERVICE_KEY = "intCleService";
	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String LC_STRUCTURE_KEY = "lcStructure";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String TRA_COMMENTAIRE_INTERNE_KEY = "traCommentaireInterne";
	public static final String TRA_CONSULTABLE_KEY = "traConsultable";
	public static final String TRA_DATE_DEB_KEY = "traDateDeb";
	public static final String TRA_DATE_FIN_KEY = "traDateFin";
	public static final String TRA_ETAT_KEY = "traEtat";
	public static final String TRA_ORDRE_KEY = "traOrdre";
	public static final String TRA_TRAITEMENT_KEY = "traTraitement";

	// Non visible attributes

	// Colkeys
	public static final String ACT_LIBELLE_COLKEY = "ACT_LIBELLE";
	public static final String ACT_ORDRE_COLKEY = "ACT_ORDRE";
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String INT_CLE_SERVICE_COLKEY = "INT_CLE_SERVICE";
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String LC_STRUCTURE_COLKEY = "LC_STRUCTURE";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String TRA_COMMENTAIRE_INTERNE_COLKEY = "TRA_COMMENTAIRE_INTERNE";
	public static final String TRA_CONSULTABLE_COLKEY = "TRA_CONSULTABLE";
	public static final String TRA_DATE_DEB_COLKEY = "TRA_DATE_DEB";
	public static final String TRA_DATE_FIN_COLKEY = "TRA_DATE_FIN";
	public static final String TRA_ETAT_COLKEY = "TRA_ETAT";
	public static final String TRA_ORDRE_COLKEY = "TRA_ORDRE";
	public static final String TRA_TRAITEMENT_COLKEY = "TRA_TRAITEMENT";

	// Non visible colkeys

	// Relationships
	public static final String TO_ETAT_DT_KEY = "toEtatDt";
	public static final String TO_INDIVIDU_ULR_KEY = "toIndividuUlr";
	public static final String TO_INTERVENTION_KEY = "toIntervention";
	public static final String TOS_DOCUMENT_DT_KEY = "tosDocumentDt";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVTraitement with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cStructure
	 * @param intCleService
	 * @param intOrdre
	 * @param noIndividu
	 * @param traConsultable
	 * @param traDateDeb
	 * @param traDateFin
	 * @param traEtat
	 * @param traOrdre
	 * @param traTraitement
	 * @param toIntervention
	 * @return EOVTraitement
	 */
	public static EOVTraitement create(EOEditingContext editingContext, String cStructure, Integer intCleService, Integer intOrdre, Integer noIndividu, String traConsultable, NSTimestamp traDateDeb, NSTimestamp traDateFin, String traEtat, Integer traOrdre, String traTraitement, org.cocktail.dt.server.metier.EOIntervention toIntervention) {
		EOVTraitement eo = (EOVTraitement) createAndInsertInstance(editingContext);
		eo.setCStructure(cStructure);
		eo.setIntCleService(intCleService);
		eo.setIntOrdre(intOrdre);
		eo.setNoIndividu(noIndividu);
		eo.setTraConsultable(traConsultable);
		eo.setTraDateDeb(traDateDeb);
		eo.setTraDateFin(traDateFin);
		eo.setTraEtat(traEtat);
		eo.setTraOrdre(traOrdre);
		eo.setTraTraitement(traTraitement);
		eo.setToInterventionRelationship(toIntervention);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVTraitement.
	 *
	 * @param editingContext
	 * @return EOVTraitement
	 */
	public static EOVTraitement create(EOEditingContext editingContext) {
		EOVTraitement eo = (EOVTraitement) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVTraitement localInstanceIn(EOEditingContext editingContext) {
		EOVTraitement localInstance = (EOVTraitement) localInstanceOfObject(editingContext, (EOVTraitement) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVTraitement localInstanceIn(EOEditingContext editingContext, EOVTraitement eo) {
		EOVTraitement localInstance = (eo == null) ? null : (EOVTraitement) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String actLibelle() {
		return (String) storedValueForKey("actLibelle");
	}

	public void setActLibelle(String value) {
		takeStoredValueForKey(value, "actLibelle");
	}
	public Integer actOrdre() {
		return (Integer) storedValueForKey("actOrdre");
	}

	public void setActOrdre(Integer value) {
		takeStoredValueForKey(value, "actOrdre");
	}
	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public Integer intCleService() {
		return (Integer) storedValueForKey("intCleService");
	}

	public void setIntCleService(Integer value) {
		takeStoredValueForKey(value, "intCleService");
	}
	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public String lcStructure() {
		return (String) storedValueForKey("lcStructure");
	}

	public void setLcStructure(String value) {
		takeStoredValueForKey(value, "lcStructure");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String traCommentaireInterne() {
		return (String) storedValueForKey("traCommentaireInterne");
	}

	public void setTraCommentaireInterne(String value) {
		takeStoredValueForKey(value, "traCommentaireInterne");
	}
	public String traConsultable() {
		return (String) storedValueForKey("traConsultable");
	}

	public void setTraConsultable(String value) {
		takeStoredValueForKey(value, "traConsultable");
	}
	public NSTimestamp traDateDeb() {
		return (NSTimestamp) storedValueForKey("traDateDeb");
	}

	public void setTraDateDeb(NSTimestamp value) {
		takeStoredValueForKey(value, "traDateDeb");
	}
	public NSTimestamp traDateFin() {
		return (NSTimestamp) storedValueForKey("traDateFin");
	}

	public void setTraDateFin(NSTimestamp value) {
		takeStoredValueForKey(value, "traDateFin");
	}
	public String traEtat() {
		return (String) storedValueForKey("traEtat");
	}

	public void setTraEtat(String value) {
		takeStoredValueForKey(value, "traEtat");
	}
	public Integer traOrdre() {
		return (Integer) storedValueForKey("traOrdre");
	}

	public void setTraOrdre(Integer value) {
		takeStoredValueForKey(value, "traOrdre");
	}
	public String traTraitement() {
		return (String) storedValueForKey("traTraitement");
	}

	public void setTraTraitement(String value) {
		takeStoredValueForKey(value, "traTraitement");
	}

	public org.cocktail.dt.server.metier.EOSharedInfo toEtatDt() {
		return (org.cocktail.dt.server.metier.EOSharedInfo)storedValueForKey("toEtatDt");
	}

	public void setToEtatDtRelationship(org.cocktail.dt.server.metier.EOSharedInfo value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOSharedInfo oldValue = toEtatDt();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toEtatDt");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toEtatDt");
		}
	}
  
	public org.cocktail.dt.server.metier.EOIndividu toIndividuUlr() {
		return (org.cocktail.dt.server.metier.EOIndividu)storedValueForKey("toIndividuUlr");
	}

	public void setToIndividuUlrRelationship(org.cocktail.dt.server.metier.EOIndividu value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOIndividu oldValue = toIndividuUlr();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toIndividuUlr");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toIndividuUlr");
		}
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
  
	public NSArray tosDocumentDt() {
		return (NSArray)storedValueForKey("tosDocumentDt");
	}

	public NSArray tosDocumentDt(EOQualifier qualifier) {
		return tosDocumentDt(qualifier, null);
	}

	public NSArray tosDocumentDt(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosDocumentDt();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
	}

	public void removeFromTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
	}

	public org.cocktail.dt.server.metier.EODocumentDt createTosDocumentDtRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DocumentDt");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosDocumentDt");
		return (org.cocktail.dt.server.metier.EODocumentDt) eo;
	}

	public void deleteTosDocumentDtRelationship(org.cocktail.dt.server.metier.EODocumentDt object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosDocumentDt");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosDocumentDtRelationships() {
		Enumeration objects = tosDocumentDt().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosDocumentDtRelationship((org.cocktail.dt.server.metier.EODocumentDt)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVTraitement.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVTraitement.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVTraitement)
	
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
	public static EOVTraitement fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVTraitement fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVTraitement eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVTraitement)eoObjects.objectAtIndex(0);
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
	public static EOVTraitement fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVTraitement fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVTraitement fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVTraitement eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVTraitement)eoObjects.objectAtIndex(0);
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
	public static EOVTraitement fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVTraitement fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVTraitement eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVTraitement ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVTraitement createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVTraitement.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVTraitement.ENTITY_NAME + "' !");
		}
		else {
			EOVTraitement object = (EOVTraitement) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVTraitement localInstanceOfObject(EOEditingContext ec, EOVTraitement object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVTraitement " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVTraitement) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
