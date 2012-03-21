// _EOGroupesDt.java
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

// DO NOT EDIT.  Make changes to EOGroupesDt.java instead.
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
public abstract class _EOGroupesDt extends  CktlRecord {
	public static final String ENTITY_NAME = "GroupesDt";
	public static final String ENTITY_TABLE_NAME = "GROUPES_DT";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "cStructure";

	public static final String C_STRUCTURE_KEY = "cStructure";
	public static final String GRP_AFFICHABLE_KEY = "grpAffichable";
	public static final String GRP_EMAIL_KEY = "grpEmail";
	public static final String GRP_EMAIL_SAM_KEY = "grpEmailSam";
	public static final String GRP_MAIL_SERVICE_KEY = "grpMailService";
	public static final String GRP_NB_LIG_BROWSER_ACT_KEY = "grpNbLigBrowserAct";
	public static final String GRP_POSITION_KEY = "grpPosition";
	public static final String GRP_SEQ_KEY = "grpSeq";
	public static final String GRP_SWAP_VIEW_KEY = "grpSwapView";
	public static final String GRP_VISIBILITE_KEY = "grpVisibilite";
	public static final String GRP_VISIBILITE_STRUCTURE_KEY = "grpVisibiliteStructure";
	public static final String GT_CODE_KEY = "gtCode";
	public static final String LC_STRUCTURE_KEY = "lcStructure";
	public static final String LL_STRUCTURE_KEY = "llStructure";

	// Non visible attributes

	// Colkeys
	public static final String C_STRUCTURE_COLKEY = "C_STRUCTURE";
	public static final String GRP_AFFICHABLE_COLKEY = "GRP_AFFICHABLE";
	public static final String GRP_EMAIL_COLKEY = "GRP_EMAIL";
	public static final String GRP_EMAIL_SAM_COLKEY = "GRP_EMAIL_SAM";
	public static final String GRP_MAIL_SERVICE_COLKEY = "GRP_MAIL_SERVICE";
	public static final String GRP_NB_LIG_BROWSER_ACT_COLKEY = "GRP_NB_LIG_BROWSER_ACT";
	public static final String GRP_POSITION_COLKEY = "GRP_POSITION";
	public static final String GRP_SEQ_COLKEY = "GRP_SEQ";
	public static final String GRP_SWAP_VIEW_COLKEY = "GRP_SWAP_VIEW";
	public static final String GRP_VISIBILITE_COLKEY = "GRP_VISIBILITE";
	public static final String GRP_VISIBILITE_STRUCTURE_COLKEY = "GRP_VISIBILITE_STRUCTURE";
	public static final String GT_CODE_COLKEY = "GT_CODE";
	public static final String LC_STRUCTURE_COLKEY = "$attribute.columnName";
	public static final String LL_STRUCTURE_COLKEY = "$attribute.columnName";

	// Non visible colkeys

	// Relationships
	public static final String TOS_ACTIVITES_KEY = "tosActivites";
	public static final String TO_STRUCTURE_ULR_KEY = "toStructureUlr";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOGroupesDt with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param cStructure
	 * @param grpAffichable
	 * @param grpNbLigBrowserAct
	 * @param grpPosition
	 * @param grpSeq
	 * @param grpSwapView
	 * @return EOGroupesDt
	 */
	public static EOGroupesDt create(EOEditingContext editingContext, String cStructure, String grpAffichable, Integer grpNbLigBrowserAct, Integer grpPosition, Integer grpSeq, Integer grpSwapView) {
		EOGroupesDt eo = (EOGroupesDt) createAndInsertInstance(editingContext);
		eo.setCStructure(cStructure);
		eo.setGrpAffichable(grpAffichable);
		eo.setGrpNbLigBrowserAct(grpNbLigBrowserAct);
		eo.setGrpPosition(grpPosition);
		eo.setGrpSeq(grpSeq);
		eo.setGrpSwapView(grpSwapView);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOGroupesDt.
	 *
	 * @param editingContext
	 * @return EOGroupesDt
	 */
	public static EOGroupesDt create(EOEditingContext editingContext) {
		EOGroupesDt eo = (EOGroupesDt) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOGroupesDt localInstanceIn(EOEditingContext editingContext) {
		EOGroupesDt localInstance = (EOGroupesDt) localInstanceOfObject(editingContext, (EOGroupesDt) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOGroupesDt localInstanceIn(EOEditingContext editingContext, EOGroupesDt eo) {
		EOGroupesDt localInstance = (eo == null) ? null : (EOGroupesDt) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public String cStructure() {
		return (String) storedValueForKey("cStructure");
	}

	public void setCStructure(String value) {
		takeStoredValueForKey(value, "cStructure");
	}
	public String grpAffichable() {
		return (String) storedValueForKey("grpAffichable");
	}

	public void setGrpAffichable(String value) {
		takeStoredValueForKey(value, "grpAffichable");
	}
	public String grpEmail() {
		return (String) storedValueForKey("grpEmail");
	}

	public void setGrpEmail(String value) {
		takeStoredValueForKey(value, "grpEmail");
	}
	public String grpEmailSam() {
		return (String) storedValueForKey("grpEmailSam");
	}

	public void setGrpEmailSam(String value) {
		takeStoredValueForKey(value, "grpEmailSam");
	}
	public String grpMailService() {
		return (String) storedValueForKey("grpMailService");
	}

	public void setGrpMailService(String value) {
		takeStoredValueForKey(value, "grpMailService");
	}
	public Integer grpNbLigBrowserAct() {
		return (Integer) storedValueForKey("grpNbLigBrowserAct");
	}

	public void setGrpNbLigBrowserAct(Integer value) {
		takeStoredValueForKey(value, "grpNbLigBrowserAct");
	}
	public Integer grpPosition() {
		return (Integer) storedValueForKey("grpPosition");
	}

	public void setGrpPosition(Integer value) {
		takeStoredValueForKey(value, "grpPosition");
	}
	public Integer grpSeq() {
		return (Integer) storedValueForKey("grpSeq");
	}

	public void setGrpSeq(Integer value) {
		takeStoredValueForKey(value, "grpSeq");
	}
	public Integer grpSwapView() {
		return (Integer) storedValueForKey("grpSwapView");
	}

	public void setGrpSwapView(Integer value) {
		takeStoredValueForKey(value, "grpSwapView");
	}
	public String grpVisibilite() {
		return (String) storedValueForKey("grpVisibilite");
	}

	public void setGrpVisibilite(String value) {
		takeStoredValueForKey(value, "grpVisibilite");
	}
	public String grpVisibiliteStructure() {
		return (String) storedValueForKey("grpVisibiliteStructure");
	}

	public void setGrpVisibiliteStructure(String value) {
		takeStoredValueForKey(value, "grpVisibiliteStructure");
	}
	public String gtCode() {
		return (String) storedValueForKey("gtCode");
	}

	public void setGtCode(String value) {
		takeStoredValueForKey(value, "gtCode");
	}
	public String lcStructure() {
		return (String) storedValueForKey("lcStructure");
	}

	public void setLcStructure(String value) {
		takeStoredValueForKey(value, "lcStructure");
	}
	public String llStructure() {
		return (String) storedValueForKey("llStructure");
	}

	public void setLlStructure(String value) {
		takeStoredValueForKey(value, "llStructure");
	}

	public org.cocktail.dt.server.metier.EOStructure toStructureUlr() {
		return (org.cocktail.dt.server.metier.EOStructure)storedValueForKey("toStructureUlr");
	}

	public void setToStructureUlrRelationship(org.cocktail.dt.server.metier.EOStructure value) {
		if (value == null) {
			org.cocktail.dt.server.metier.EOStructure oldValue = toStructureUlr();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toStructureUlr");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toStructureUlr");
		}
	}
  
	public NSArray tosActivites() {
		return (NSArray)storedValueForKey("tosActivites");
	}

	public NSArray tosActivites(EOQualifier qualifier) {
		return tosActivites(qualifier, null, false);
	}

	public NSArray tosActivites(EOQualifier qualifier, boolean fetch) {
		return tosActivites(qualifier, null, fetch);
	}

	public NSArray tosActivites(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOActivites.TO_GROUPES_DT_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOActivites.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosActivites();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosActivitesRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosActivites");
	}

	public void removeFromTosActivitesRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActivites");
	}

	public org.cocktail.dt.server.metier.EOActivites createTosActivitesRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Activites");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosActivites");
		return (org.cocktail.dt.server.metier.EOActivites) eo;
	}

	public void deleteTosActivitesRelationship(org.cocktail.dt.server.metier.EOActivites object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActivites");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosActivitesRelationships() {
		Enumeration objects = tosActivites().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosActivitesRelationship((org.cocktail.dt.server.metier.EOActivites)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOGroupesDt.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOGroupesDt.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOGroupesDt)
	
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
	public static EOGroupesDt fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOGroupesDt fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOGroupesDt eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOGroupesDt)eoObjects.objectAtIndex(0);
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
	public static EOGroupesDt fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOGroupesDt fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOGroupesDt fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOGroupesDt eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOGroupesDt)eoObjects.objectAtIndex(0);
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
	public static EOGroupesDt fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOGroupesDt fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOGroupesDt eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOGroupesDt ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOGroupesDt createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOGroupesDt.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOGroupesDt.ENTITY_NAME + "' !");
		}
		else {
			EOGroupesDt object = (EOGroupesDt) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOGroupesDt localInstanceOfObject(EOEditingContext ec, EOGroupesDt object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOGroupesDt " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOGroupesDt) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
