// _EOTraitement.java
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

// DO NOT EDIT.  Make changes to EOTraitement.java instead.
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
public abstract class _EOTraitement extends  CktlRecord {
	public static final String ENTITY_NAME = "Traitement";
	public static final String ENTITY_TABLE_NAME = "TRAITEMENT";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "traOrdre";

	public static final String INT_ORDRE_KEY = "intOrdre";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NOM_INTERVENANT_KEY = "nomIntervenant";
	public static final String TRA_COMMENTAIRE_INTERNE_KEY = "traCommentaireInterne";
	public static final String TRA_CONSULTABLE_KEY = "traConsultable";
	public static final String TRA_DATE_DEB_KEY = "traDateDeb";
	public static final String TRA_DATE_FIN_KEY = "traDateFin";
	public static final String TRA_ETAT_KEY = "traEtat";
	public static final String TRA_ORDRE_KEY = "traOrdre";
	public static final String TRA_TRAITEMENT_KEY = "traTraitement";
	public static final String TRA_TRAITEMENT_ADDITIONNEL_KEY = "traTraitementAdditionnel";
	public static final String TTY_KEY_KEY = "ttyKey";

	// Non visible attributes

	// Colkeys
	public static final String INT_ORDRE_COLKEY = "INT_ORDRE";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String NOM_INTERVENANT_COLKEY = "$attribute.columnName";
	public static final String TRA_COMMENTAIRE_INTERNE_COLKEY = "TRA_COMMENTAIRE_INTERNE";
	public static final String TRA_CONSULTABLE_COLKEY = "TRA_CONSULTABLE";
	public static final String TRA_DATE_DEB_COLKEY = "TRA_DATE_DEB";
	public static final String TRA_DATE_FIN_COLKEY = "TRA_DATE_FIN";
	public static final String TRA_ETAT_COLKEY = "TRA_ETAT";
	public static final String TRA_ORDRE_COLKEY = "TRA_ORDRE";
	public static final String TRA_TRAITEMENT_COLKEY = "TRA_TRAITEMENT";
	public static final String TRA_TRAITEMENT_ADDITIONNEL_COLKEY = "TRA_TRAITEMENT_ADDITIONNEL";
	public static final String TTY_KEY_COLKEY = "TTY_KEY";

	// Non visible colkeys

	// Relationships
	public static final String TO_ETAT_DT_KEY = "toEtatDt";
	public static final String TO_INDIVIDU_ULR_KEY = "toIndividuUlr";
	public static final String TO_INTERVENTION_KEY = "toIntervention";
	public static final String TOS_DOCUMENT_DT_KEY = "tosDocumentDt";
	public static final String TO_TRAITEMENT_TYPE_KEY = "toTraitementType";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOTraitement with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param intOrdre
	 * @param noIndividu
	 * @param traConsultable
	 * @param traDateDeb
	 * @param traDateFin
	 * @param traEtat
	 * @param traOrdre
	 * @param traTraitement
	 * @param ttyKey
	 * @param toIntervention
	 * @param toTraitementType
	 * @return EOTraitement
	 */
	public static EOTraitement create(EOEditingContext editingContext, Integer intOrdre, Integer noIndividu, String traConsultable, NSTimestamp traDateDeb, NSTimestamp traDateFin, String traEtat, Integer traOrdre, String traTraitement, Integer ttyKey, org.cocktail.dt.server.metier.EOIntervention toIntervention, org.cocktail.dt.server.metier.EOTraitementType toTraitementType) {
		EOTraitement eo = (EOTraitement) createAndInsertInstance(editingContext);
		eo.setIntOrdre(intOrdre);
		eo.setNoIndividu(noIndividu);
		eo.setTraConsultable(traConsultable);
		eo.setTraDateDeb(traDateDeb);
		eo.setTraDateFin(traDateFin);
		eo.setTraEtat(traEtat);
		eo.setTraOrdre(traOrdre);
		eo.setTraTraitement(traTraitement);
		eo.setTtyKey(ttyKey);
		eo.setToInterventionRelationship(toIntervention);
		eo.setToTraitementTypeRelationship(toTraitementType);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOTraitement.
	 *
	 * @param editingContext
	 * @return EOTraitement
	 */
	public static EOTraitement create(EOEditingContext editingContext) {
		EOTraitement eo = (EOTraitement) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOTraitement localInstanceIn(EOEditingContext editingContext) {
		EOTraitement localInstance = (EOTraitement) localInstanceOfObject(editingContext, (EOTraitement) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOTraitement localInstanceIn(EOEditingContext editingContext, EOTraitement eo) {
		EOTraitement localInstance = (eo == null) ? null : (EOTraitement) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer intOrdre() {
		return (Integer) storedValueForKey("intOrdre");
	}

	public void setIntOrdre(Integer value) {
		takeStoredValueForKey(value, "intOrdre");
	}
	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String nomIntervenant() {
		return (String) storedValueForKey("nomIntervenant");
	}

	public void setNomIntervenant(String value) {
		takeStoredValueForKey(value, "nomIntervenant");
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
	public String traTraitementAdditionnel() {
		return (String) storedValueForKey("traTraitementAdditionnel");
	}

	public void setTraTraitementAdditionnel(String value) {
		takeStoredValueForKey(value, "traTraitementAdditionnel");
	}
	public Integer ttyKey() {
		return (Integer) storedValueForKey("ttyKey");
	}

	public void setTtyKey(Integer value) {
		takeStoredValueForKey(value, "ttyKey");
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
  
	public org.cocktail.dt.server.metier.EOTraitementType toTraitementType() {
		return (org.cocktail.dt.server.metier.EOTraitementType)storedValueForKey("toTraitementType");
	}

	public void setToTraitementTypeRelationship(org.cocktail.dt.server.metier.EOTraitementType value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOTraitementType oldValue = toTraitementType();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toTraitementType");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toTraitementType");
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
		return _EOTraitement.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOTraitement.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOTraitement)
	
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
	public static EOTraitement fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOTraitement fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOTraitement eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOTraitement)eoObjects.objectAtIndex(0);
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
	public static EOTraitement fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOTraitement fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOTraitement fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOTraitement eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOTraitement)eoObjects.objectAtIndex(0);
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
	public static EOTraitement fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOTraitement fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOTraitement eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOTraitement ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	
	public static NSArray fetchFetchTraitement(EOEditingContext editingContext, NSDictionary bindings) {
		EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("FetchTraitement", "Traitement");
		fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
		return (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
	}


	// Internal utilities methods for common use (server AND client)...

	private static EOTraitement createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOTraitement.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOTraitement.ENTITY_NAME + "' !");
		}
		else {
			EOTraitement object = (EOTraitement) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOTraitement localInstanceOfObject(EOEditingContext ec, EOTraitement object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOTraitement " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOTraitement) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
