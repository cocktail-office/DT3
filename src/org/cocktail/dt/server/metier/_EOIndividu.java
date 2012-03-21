// _EOIndividu.java
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

// DO NOT EDIT.  Make changes to EOIndividu.java instead.
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
public abstract class _EOIndividu extends  CktlRecord {
	public static final String ENTITY_NAME = "DTIndividuUlr";
	public static final String ENTITY_TABLE_NAME = "GRHUM.INDIVIDU_ULR";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "noIndividu";

	public static final String D_NAISSANCE_KEY = "dNaissance";
	public static final String IND_PHOTO_KEY = "indPhoto";
	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String NOM_ET_PRENOM_KEY = "nomEtPrenom";
	public static final String NOM_PATRONYMIQUE_KEY = "nomPatronymique";
	public static final String NOM_USUEL_KEY = "nomUsuel";
	public static final String PERS_ID_KEY = "persId";
	public static final String PRENOM_KEY = "prenom";
	public static final String TEM_VALIDE_KEY = "temValide";

	// Non visible attributes
	public static final String C_PAYS_NATIONALITE_KEY = "cPaysNationalite";

	// Colkeys
	public static final String D_NAISSANCE_COLKEY = "D_NAISSANCE";
	public static final String IND_PHOTO_COLKEY = "IND_PHOTO";
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String NOM_ET_PRENOM_COLKEY = "$attribute.columnName";
	public static final String NOM_PATRONYMIQUE_COLKEY = "NOM_PATRONYMIQUE";
	public static final String NOM_USUEL_COLKEY = "NOM_USUEL";
	public static final String PERS_ID_COLKEY = "PERS_ID";
	public static final String PRENOM_COLKEY = "PRENOM";
	public static final String TEM_VALIDE_COLKEY = "TEM_VALIDE";

	// Non visible colkeys
	public static final String C_PAYS_NATIONALITE_COLKEY = "C_PAYS_NATIONALITE";

	// Relationships
	public static final String TO_FWKPERS__PAYS_KEY = "toFwkpers_Pays";
	public static final String TOS_ACTIVITES_RESPONSABLES_KEY = "tosActivitesResponsables";
	public static final String TOS_COMPTE_KEY = "tosCompte";
	public static final String TOS_FWKGRH__CONTRAT_KEY = "tosFwkgrh_Contrat";
	public static final String TOS_INTERVENANT_KEY = "tosIntervenant";
	public static final String TOS_PREF_DROITS_KEY = "tosPrefDroits";
	public static final String TOS_REPART_BUREAU_KEY = "tosRepartBureau";
	public static final String TOS_REPART_STRUCTURE_KEY = "tosRepartStructure";
	public static final String TOS_TELEPHONE_KEY = "tosTelephone";

	// Create / Init methods

	/**
	 * Creates and inserts a new EOIndividu with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param noIndividu
	 * @param persId
	 * @return EOIndividu
	 */
	public static EOIndividu create(EOEditingContext editingContext, Integer noIndividu, Integer persId) {
		EOIndividu eo = (EOIndividu) createAndInsertInstance(editingContext);
		eo.setNoIndividu(noIndividu);
		eo.setPersId(persId);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOIndividu.
	 *
	 * @param editingContext
	 * @return EOIndividu
	 */
	public static EOIndividu create(EOEditingContext editingContext) {
		EOIndividu eo = (EOIndividu) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOIndividu localInstanceIn(EOEditingContext editingContext) {
		EOIndividu localInstance = (EOIndividu) localInstanceOfObject(editingContext, (EOIndividu) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOIndividu localInstanceIn(EOEditingContext editingContext, EOIndividu eo) {
		EOIndividu localInstance = (eo == null) ? null : (EOIndividu) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public NSTimestamp dNaissance() {
		return (NSTimestamp) storedValueForKey("dNaissance");
	}

	public void setDNaissance(NSTimestamp value) {
		takeStoredValueForKey(value, "dNaissance");
	}
	public String indPhoto() {
		return (String) storedValueForKey("indPhoto");
	}

	public void setIndPhoto(String value) {
		takeStoredValueForKey(value, "indPhoto");
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
	public String nomPatronymique() {
		return (String) storedValueForKey("nomPatronymique");
	}

	public void setNomPatronymique(String value) {
		takeStoredValueForKey(value, "nomPatronymique");
	}
	public String nomUsuel() {
		return (String) storedValueForKey("nomUsuel");
	}

	public void setNomUsuel(String value) {
		takeStoredValueForKey(value, "nomUsuel");
	}
	public Integer persId() {
		return (Integer) storedValueForKey("persId");
	}

	public void setPersId(Integer value) {
		takeStoredValueForKey(value, "persId");
	}
	public String prenom() {
		return (String) storedValueForKey("prenom");
	}

	public void setPrenom(String value) {
		takeStoredValueForKey(value, "prenom");
	}
	public String temValide() {
		return (String) storedValueForKey("temValide");
	}

	public void setTemValide(String value) {
		takeStoredValueForKey(value, "temValide");
	}

	public org.cocktail.fwkcktlpersonne.common.metier.EOPays toFwkpers_Pays() {
		return (org.cocktail.fwkcktlpersonne.common.metier.EOPays)storedValueForKey("toFwkpers_Pays");
	}

	public void setToFwkpers_PaysRelationship(org.cocktail.fwkcktlpersonne.common.metier.EOPays value) {
		if (value == null) {
			org.cocktail.fwkcktlpersonne.common.metier.EOPays oldValue = toFwkpers_Pays();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "toFwkpers_Pays");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "toFwkpers_Pays");
		}
	}
  
	public NSArray tosActivitesResponsables() {
		return (NSArray)storedValueForKey("tosActivitesResponsables");
	}

	public NSArray tosActivitesResponsables(EOQualifier qualifier) {
		return tosActivitesResponsables(qualifier, null, false);
	}

	public NSArray tosActivitesResponsables(EOQualifier qualifier, boolean fetch) {
		return tosActivitesResponsables(qualifier, null, fetch);
	}

	public NSArray tosActivitesResponsables(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOActivitesResponsables.TO_INDIVIDU_ULR_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOActivitesResponsables.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosActivitesResponsables();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosActivitesResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosActivitesResponsables");
	}

	public void removeFromTosActivitesResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActivitesResponsables");
	}

	public org.cocktail.dt.server.metier.EOActivitesResponsables createTosActivitesResponsablesRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("ActivitesResponsables");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosActivitesResponsables");
		return (org.cocktail.dt.server.metier.EOActivitesResponsables) eo;
	}

	public void deleteTosActivitesResponsablesRelationship(org.cocktail.dt.server.metier.EOActivitesResponsables object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosActivitesResponsables");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosActivitesResponsablesRelationships() {
		Enumeration objects = tosActivitesResponsables().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosActivitesResponsablesRelationship((org.cocktail.dt.server.metier.EOActivitesResponsables)objects.nextElement());
		}
	}
	public NSArray tosCompte() {
		return (NSArray)storedValueForKey("tosCompte");
	}

	public NSArray tosCompte(EOQualifier qualifier) {
		return tosCompte(qualifier, null);
	}

	public NSArray tosCompte(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosCompte();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosCompteRelationship(org.cocktail.dt.server.metier.EOCompte object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosCompte");
	}

	public void removeFromTosCompteRelationship(org.cocktail.dt.server.metier.EOCompte object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosCompte");
	}

	public org.cocktail.dt.server.metier.EOCompte createTosCompteRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DTCompte");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosCompte");
		return (org.cocktail.dt.server.metier.EOCompte) eo;
	}

	public void deleteTosCompteRelationship(org.cocktail.dt.server.metier.EOCompte object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosCompte");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosCompteRelationships() {
		Enumeration objects = tosCompte().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosCompteRelationship((org.cocktail.dt.server.metier.EOCompte)objects.nextElement());
		}
	}
	public NSArray tosFwkgrh_Contrat() {
		return (NSArray)storedValueForKey("tosFwkgrh_Contrat");
	}

	public NSArray tosFwkgrh_Contrat(EOQualifier qualifier) {
		return tosFwkgrh_Contrat(qualifier, null);
	}

	public NSArray tosFwkgrh_Contrat(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosFwkgrh_Contrat();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosFwkgrh_ContratRelationship(org.cocktail.fwkcktlgrh.common.metier.EOContrat object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosFwkgrh_Contrat");
	}

	public void removeFromTosFwkgrh_ContratRelationship(org.cocktail.fwkcktlgrh.common.metier.EOContrat object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosFwkgrh_Contrat");
	}

	public org.cocktail.fwkcktlgrh.common.metier.EOContrat createTosFwkgrh_ContratRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Fwkgrh_Contrat");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosFwkgrh_Contrat");
		return (org.cocktail.fwkcktlgrh.common.metier.EOContrat) eo;
	}

	public void deleteTosFwkgrh_ContratRelationship(org.cocktail.fwkcktlgrh.common.metier.EOContrat object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosFwkgrh_Contrat");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosFwkgrh_ContratRelationships() {
		Enumeration objects = tosFwkgrh_Contrat().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosFwkgrh_ContratRelationship((org.cocktail.fwkcktlgrh.common.metier.EOContrat)objects.nextElement());
		}
	}
	public NSArray tosIntervenant() {
		return (NSArray)storedValueForKey("tosIntervenant");
	}

	public NSArray tosIntervenant(EOQualifier qualifier) {
		return tosIntervenant(qualifier, null, false);
	}

	public NSArray tosIntervenant(EOQualifier qualifier, boolean fetch) {
		return tosIntervenant(qualifier, null, fetch);
	}

	public NSArray tosIntervenant(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOIntervenant.TO_INDIVIDU_ULR_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOIntervenant.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosIntervenant();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosIntervenant");
	}

	public void removeFromTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervenant");
	}

	public org.cocktail.dt.server.metier.EOIntervenant createTosIntervenantRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Intervenant");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosIntervenant");
		return (org.cocktail.dt.server.metier.EOIntervenant) eo;
	}

	public void deleteTosIntervenantRelationship(org.cocktail.dt.server.metier.EOIntervenant object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosIntervenant");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosIntervenantRelationships() {
		Enumeration objects = tosIntervenant().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosIntervenantRelationship((org.cocktail.dt.server.metier.EOIntervenant)objects.nextElement());
		}
	}
	public NSArray tosPrefDroits() {
		return (NSArray)storedValueForKey("tosPrefDroits");
	}

	public NSArray tosPrefDroits(EOQualifier qualifier) {
		return tosPrefDroits(qualifier, null, false);
	}

	public NSArray tosPrefDroits(EOQualifier qualifier, boolean fetch) {
		return tosPrefDroits(qualifier, null, fetch);
	}

	public NSArray tosPrefDroits(EOQualifier qualifier, NSArray sortOrderings, boolean fetch) {
		NSArray results;
				if (fetch) {
			EOQualifier fullQualifier;
						EOQualifier inverseQualifier = new EOKeyValueQualifier(org.cocktail.dt.server.metier.EOPrefDroits.TO_INDIVIDU_ULR_KEY, EOQualifier.QualifierOperatorEqual, this);
			
			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

						results = org.cocktail.dt.server.metier.EOPrefDroits.fetchAll(editingContext(), fullQualifier, sortOrderings);
					}
		else {
				results = tosPrefDroits();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				}
				return results;
	}
  
	public void addToTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
	}

	public void removeFromTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
	}

	public org.cocktail.dt.server.metier.EOPrefDroits createTosPrefDroitsRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("PrefDroits");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosPrefDroits");
		return (org.cocktail.dt.server.metier.EOPrefDroits) eo;
	}

	public void deleteTosPrefDroitsRelationship(org.cocktail.dt.server.metier.EOPrefDroits object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosPrefDroits");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosPrefDroitsRelationships() {
		Enumeration objects = tosPrefDroits().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosPrefDroitsRelationship((org.cocktail.dt.server.metier.EOPrefDroits)objects.nextElement());
		}
	}
	public NSArray tosRepartBureau() {
		return (NSArray)storedValueForKey("tosRepartBureau");
	}

	public NSArray tosRepartBureau(EOQualifier qualifier) {
		return tosRepartBureau(qualifier, null);
	}

	public NSArray tosRepartBureau(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosRepartBureau();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
	}

	public void removeFromTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
	}

	public org.cocktail.dt.server.metier.EORepartBureau createTosRepartBureauRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("RepartBureau");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartBureau");
		return (org.cocktail.dt.server.metier.EORepartBureau) eo;
	}

	public void deleteTosRepartBureauRelationship(org.cocktail.dt.server.metier.EORepartBureau object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartBureau");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartBureauRelationships() {
		Enumeration objects = tosRepartBureau().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartBureauRelationship((org.cocktail.dt.server.metier.EORepartBureau)objects.nextElement());
		}
	}
	public NSArray tosRepartStructure() {
		return (NSArray)storedValueForKey("tosRepartStructure");
	}

	public NSArray tosRepartStructure(EOQualifier qualifier) {
		return tosRepartStructure(qualifier, null);
	}

	public NSArray tosRepartStructure(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosRepartStructure();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosRepartStructureRelationship(org.cocktail.dt.server.metier.EORepartStructure object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosRepartStructure");
	}

	public void removeFromTosRepartStructureRelationship(org.cocktail.dt.server.metier.EORepartStructure object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartStructure");
	}

	public org.cocktail.dt.server.metier.EORepartStructure createTosRepartStructureRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DTRepartStructure");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosRepartStructure");
		return (org.cocktail.dt.server.metier.EORepartStructure) eo;
	}

	public void deleteTosRepartStructureRelationship(org.cocktail.dt.server.metier.EORepartStructure object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosRepartStructure");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosRepartStructureRelationships() {
		Enumeration objects = tosRepartStructure().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosRepartStructureRelationship((org.cocktail.dt.server.metier.EORepartStructure)objects.nextElement());
		}
	}
	public NSArray tosTelephone() {
		return (NSArray)storedValueForKey("tosTelephone");
	}

	public NSArray tosTelephone(EOQualifier qualifier) {
		return tosTelephone(qualifier, null);
	}

	public NSArray tosTelephone(EOQualifier qualifier, NSArray sortOrderings) {
		NSArray results;
				results = tosTelephone();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
				return results;
	}
  
	public void addToTosTelephoneRelationship(org.cocktail.dt.server.metier.EOPersonneTelephone object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "tosTelephone");
	}

	public void removeFromTosTelephoneRelationship(org.cocktail.dt.server.metier.EOPersonneTelephone object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosTelephone");
	}

	public org.cocktail.dt.server.metier.EOPersonneTelephone createTosTelephoneRelationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("DTPersonneTelephone");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "tosTelephone");
		return (org.cocktail.dt.server.metier.EOPersonneTelephone) eo;
	}

	public void deleteTosTelephoneRelationship(org.cocktail.dt.server.metier.EOPersonneTelephone object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "tosTelephone");
				editingContext().deleteObject(object);
			}

	public void deleteAllTosTelephoneRelationships() {
		Enumeration objects = tosTelephone().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			deleteTosTelephoneRelationship((org.cocktail.dt.server.metier.EOPersonneTelephone)objects.nextElement());
		}
	}

	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOIndividu.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOIndividu.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOIndividu)
	
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
	public static EOIndividu fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOIndividu fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOIndividu eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOIndividu)eoObjects.objectAtIndex(0);
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
	public static EOIndividu fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOIndividu fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOIndividu fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOIndividu eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOIndividu)eoObjects.objectAtIndex(0);
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
	public static EOIndividu fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOIndividu fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOIndividu eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOIndividu ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	
	public static NSArray fetchFetchAffectation(EOEditingContext editingContext, NSDictionary bindings) {
		EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("fetchAffectation", "DTIndividuUlr");
		fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
		return (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
	}


	// Internal utilities methods for common use (server AND client)...

	private static EOIndividu createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOIndividu.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOIndividu.ENTITY_NAME + "' !");
		}
		else {
			EOIndividu object = (EOIndividu) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOIndividu localInstanceOfObject(EOEditingContext ec, EOIndividu object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOIndividu " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOIndividu) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
