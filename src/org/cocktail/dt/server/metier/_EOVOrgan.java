// _EOVOrgan.java
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

// DO NOT EDIT.  Make changes to EOVOrgan.java instead.
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
public abstract class _EOVOrgan extends  CktlRecord {
	public static final String ENTITY_NAME = "VJefyOrgan";
	public static final String ENTITY_TABLE_NAME = "GRHUM.V_ORGAN";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "orgOrdre";

	public static final String ANNEE_KEY = "annee";
	public static final String DST_CODE_KEY = "dstCode";
	public static final String ORG_COMP_KEY = "orgComp";
	public static final String ORG_DATE_KEY = "orgDate";
	public static final String ORG_ID_KEY = "orgId";
	public static final String ORG_LBUD_KEY = "orgLbud";
	public static final String ORG_LIB_KEY = "orgLib";
	public static final String ORG_NIV_KEY = "orgNiv";
	public static final String ORG_ORDRE_KEY = "orgOrdre";
	public static final String ORG_RAT_KEY = "orgRat";
	public static final String ORG_STAT_KEY = "orgStat";
	public static final String ORG_TVA_KEY = "orgTva";
	public static final String ORG_UC_KEY = "orgUc";
	public static final String ORG_UNIT_KEY = "orgUnit";
	public static final String SCT_CODE_KEY = "sctCode";

	// Non visible attributes

	// Colkeys
	public static final String ANNEE_COLKEY = "ANNEE";
	public static final String DST_CODE_COLKEY = "DST_CODE";
	public static final String ORG_COMP_COLKEY = "ORG_COMP";
	public static final String ORG_DATE_COLKEY = "ORG_DATE";
	public static final String ORG_ID_COLKEY = "ORG_ORDRE";
	public static final String ORG_LBUD_COLKEY = "ORG_LBUD";
	public static final String ORG_LIB_COLKEY = "ORG_LIB";
	public static final String ORG_NIV_COLKEY = "ORG_NIV";
	public static final String ORG_ORDRE_COLKEY = "ORG_ORDRE";
	public static final String ORG_RAT_COLKEY = "ORG_RAT";
	public static final String ORG_STAT_COLKEY = "ORG_STAT";
	public static final String ORG_TVA_COLKEY = "ORG_TVA";
	public static final String ORG_UC_COLKEY = "ORG_UC";
	public static final String ORG_UNIT_COLKEY = "ORG_UNIT";
	public static final String SCT_CODE_COLKEY = "SCT_CODE";

	// Non visible colkeys

	// Relationships
	public static final String TOS_V_JEFY_BUDGET_EXEC_CREDIT_KEY = "tosVJefyBudgetExecCredit";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOVOrgan with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param annee
	 * @param orgDate
	 * @param orgId
	 * @param orgLib
	 * @param orgNiv
	 * @param orgOrdre
	 * @param orgStat
	 * @param orgTva
	 * @return EOVOrgan
	 */
	public static EOVOrgan create(EOEditingContext editingContext, Integer annee, NSTimestamp orgDate, Integer orgId, String orgLib, Integer orgNiv, Integer orgOrdre, Integer orgStat, Integer orgTva) {
		EOVOrgan eo = (EOVOrgan) createAndInsertInstance(editingContext);
		eo.setAnnee(annee);
		eo.setOrgDate(orgDate);
		eo.setOrgId(orgId);
		eo.setOrgLib(orgLib);
		eo.setOrgNiv(orgNiv);
		eo.setOrgOrdre(orgOrdre);
		eo.setOrgStat(orgStat);
		eo.setOrgTva(orgTva);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOVOrgan.
	 *
	 * @param editingContext
	 * @return EOVOrgan
	 */
	public static EOVOrgan create(EOEditingContext editingContext) {
		EOVOrgan eo = (EOVOrgan) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOVOrgan localInstanceIn(EOEditingContext editingContext) {
		EOVOrgan localInstance = (EOVOrgan) localInstanceOfObject(editingContext, (EOVOrgan) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOVOrgan localInstanceIn(EOEditingContext editingContext, EOVOrgan eo) {
		EOVOrgan localInstance = (eo == null) ? null : (EOVOrgan) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer annee() {
		return (Integer) storedValueForKey("annee");
	}

	public void setAnnee(Integer value) {
		takeStoredValueForKey(value, "annee");
	}
	public String dstCode() {
		return (String) storedValueForKey("dstCode");
	}

	public void setDstCode(String value) {
		takeStoredValueForKey(value, "dstCode");
	}
	public String orgComp() {
		return (String) storedValueForKey("orgComp");
	}

	public void setOrgComp(String value) {
		takeStoredValueForKey(value, "orgComp");
	}
	public NSTimestamp orgDate() {
		return (NSTimestamp) storedValueForKey("orgDate");
	}

	public void setOrgDate(NSTimestamp value) {
		takeStoredValueForKey(value, "orgDate");
	}
	public Integer orgId() {
		return (Integer) storedValueForKey("orgId");
	}

	public void setOrgId(Integer value) {
		takeStoredValueForKey(value, "orgId");
	}
	public String orgLbud() {
		return (String) storedValueForKey("orgLbud");
	}

	public void setOrgLbud(String value) {
		takeStoredValueForKey(value, "orgLbud");
	}
	public String orgLib() {
		return (String) storedValueForKey("orgLib");
	}

	public void setOrgLib(String value) {
		takeStoredValueForKey(value, "orgLib");
	}
	public Integer orgNiv() {
		return (Integer) storedValueForKey("orgNiv");
	}

	public void setOrgNiv(Integer value) {
		takeStoredValueForKey(value, "orgNiv");
	}
	public Integer orgOrdre() {
		return (Integer) storedValueForKey("orgOrdre");
	}

	public void setOrgOrdre(Integer value) {
		takeStoredValueForKey(value, "orgOrdre");
	}
	public Integer orgRat() {
		return (Integer) storedValueForKey("orgRat");
	}

	public void setOrgRat(Integer value) {
		takeStoredValueForKey(value, "orgRat");
	}
	public Integer orgStat() {
		return (Integer) storedValueForKey("orgStat");
	}

	public void setOrgStat(Integer value) {
		takeStoredValueForKey(value, "orgStat");
	}
	public Integer orgTva() {
		return (Integer) storedValueForKey("orgTva");
	}

	public void setOrgTva(Integer value) {
		takeStoredValueForKey(value, "orgTva");
	}
	public String orgUc() {
		return (String) storedValueForKey("orgUc");
	}

	public void setOrgUc(String value) {
		takeStoredValueForKey(value, "orgUc");
	}
	public String orgUnit() {
		return (String) storedValueForKey("orgUnit");
	}

	public void setOrgUnit(String value) {
		takeStoredValueForKey(value, "orgUnit");
	}
	public String sctCode() {
		return (String) storedValueForKey("sctCode");
	}

	public void setSctCode(String value) {
		takeStoredValueForKey(value, "sctCode");
	}

	public NSArray tosVJefyBudgetExecCredit() {
		return (NSArray)storedValueForKey("tosVJefyBudgetExecCredit");
	}

	public NSArray tosVJefyBudgetExecCredit(EOQualifier qualifier) {
		return tosVJefyBudgetExecCredit(qualifier, null, false);
	}

	public NSArray tosVJefyBudgetExecCredit(EOQualifier qualifier, boolean fetch) {
		return tosVJefyBudgetExecCredit(qualifier, null, fetch);
	}

	public NSArray tosVJefyBudgetExecCredit(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOVBudgetExecCredit.TO_V_JEFY_ORGAN_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOVBudgetExecCredit.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosVJefyBudgetExecCredit();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosVJefyBudgetExecCreditRelationship(org.cocktail.dt.server.metier.EOVBudgetExecCredit object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosVJefyBudgetExecCredit");
	}

	public void removeFromTosVJefyBudgetExecCreditRelationship(org.cocktail.dt.server.metier.EOVBudgetExecCredit object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosVJefyBudgetExecCredit");
	}

	public org.cocktail.dt.server.metier.EOVBudgetExecCredit createTosVJefyBudgetExecCreditRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("VJefyBudgetExecCredit");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosVJefyBudgetExecCredit");
		return (org.cocktail.dt.server.metier.EOVBudgetExecCredit) eo;
	}

	public void deleteTosVJefyBudgetExecCreditRelationship(org.cocktail.dt.server.metier.EOVBudgetExecCredit object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosVJefyBudgetExecCredit");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosVJefyBudgetExecCreditRelationships() {
		Enumeration objects = tosVJefyBudgetExecCredit().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosVJefyBudgetExecCreditRelationship((org.cocktail.dt.server.metier.EOVBudgetExecCredit)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOVOrgan.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOVOrgan.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOVOrgan)
	
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
	public static EOVOrgan fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOVOrgan fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOVOrgan eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOVOrgan)eoObjects.objectAtIndex(0);
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
	public static EOVOrgan fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOVOrgan fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOVOrgan fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOVOrgan eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOVOrgan)eoObjects.objectAtIndex(0);
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
	public static EOVOrgan fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOVOrgan fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOVOrgan eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOVOrgan ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOVOrgan createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOVOrgan.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOVOrgan.ENTITY_NAME + "' !");
		}
		else {
			EOVOrgan object = (EOVOrgan) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOVOrgan localInstanceOfObject(EOEditingContext ec, EOVOrgan object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOVOrgan " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOVOrgan) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
