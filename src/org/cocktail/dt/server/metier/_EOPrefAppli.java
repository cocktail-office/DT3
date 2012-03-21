// _EOPrefAppli.java
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

// DO NOT EDIT.  Make changes to EOPrefAppli.java instead.
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
public abstract class _EOPrefAppli extends  CktlRecord {
	public static final String ENTITY_NAME = "PrefAppli";
	public static final String ENTITY_TABLE_NAME = "PREF_APPLI";

	// Attributes
	public static final String ENTITY_PRIMARY_KEY = "noIndividu";

	public static final String NO_INDIVIDU_KEY = "noIndividu";
	public static final String PRF_AIDE_SYS_NAV_KEY = "prfAideSysNav";
	public static final String PRF_CONFIRMATION_CLOTURE_KEY = "prfConfirmationCloture";
	public static final String PRF_CONTROLE_CHEVAUCHEMENT_PLANNING_KEY = "prfControleChevauchementPlanning";
	public static final String PRF_DRO_SERVICE_KEY = "prfDroService";
	public static final String PRF_ETAT_CODE_KEY = "prfEtatCode";
	public static final String PRF_EXPORT_PLANNING_KEY = "prfExportPlanning";
	public static final String PRF_INSERT_DT_SIG_KEY = "prfInsertDtSig";
	public static final String PRF_MAIL_TRAITEMENT_KEY = "prfMailTraitement";
	public static final String PRF_NB_INT_PER_PAGE_KEY = "prfNbIntPerPage";
	public static final String PRF_ONGLET_KEY = "prfOnglet";
	public static final String PRF_ORDRE_COLUMNS_KEY = "prfOrdreColumns";
	public static final String PRF_PANIER_KEY = "prfPanier";
	public static final String PRF_PHOTO_KEY = "prfPhoto";
	public static final String PRF_SAUVEGARDER_PANIER_KEY = "prfSauvegarderPanier";
	public static final String PRF_TIMER_KEY = "prfTimer";
	public static final String PRF_TRI_KEY = "prfTri";
	public static final String PRF_TRI_INT_KEY = "prfTriInt";
	public static final String PRF_TRI_TRA_KEY = "prfTriTra";
	public static final String PRF_USE_COUL_BAT_KEY = "prfUseCoulBat";
	public static final String PRF_USE_MAIL_INTERNE_KEY = "prfUseMailInterne";

	// Non visible attributes

	// Colkeys
	public static final String NO_INDIVIDU_COLKEY = "NO_INDIVIDU";
	public static final String PRF_AIDE_SYS_NAV_COLKEY = "PRF_AIDE_SYS_NAV";
	public static final String PRF_CONFIRMATION_CLOTURE_COLKEY = "PRF_CONFIRMATION_CLOTURE";
	public static final String PRF_CONTROLE_CHEVAUCHEMENT_PLANNING_COLKEY = "PRF_CTRL_CHEV_PLANNING";
	public static final String PRF_DRO_SERVICE_COLKEY = "PRF_DRO_SERVICE";
	public static final String PRF_ETAT_CODE_COLKEY = "PRF_ETAT_CODE";
	public static final String PRF_EXPORT_PLANNING_COLKEY = "PRF_EXPORT_PLANNING";
	public static final String PRF_INSERT_DT_SIG_COLKEY = "PRF_INSERT_DT_SIG";
	public static final String PRF_MAIL_TRAITEMENT_COLKEY = "PRF_MAIL_TRAITEMENT";
	public static final String PRF_NB_INT_PER_PAGE_COLKEY = "PRF_NB_INT_PER_PAGE";
	public static final String PRF_ONGLET_COLKEY = "PRF_ONGLET";
	public static final String PRF_ORDRE_COLUMNS_COLKEY = "PRF_ORDRE_COLUMNS";
	public static final String PRF_PANIER_COLKEY = "PRF_PANIER";
	public static final String PRF_PHOTO_COLKEY = "PRF_PHOTO";
	public static final String PRF_SAUVEGARDER_PANIER_COLKEY = "PRF_SAUVEGARDER_PANIER";
	public static final String PRF_TIMER_COLKEY = "PRF_TIMER";
	public static final String PRF_TRI_COLKEY = "PRF_TRI";
	public static final String PRF_TRI_INT_COLKEY = "PRF_TRI_INT";
	public static final String PRF_TRI_TRA_COLKEY = "PRF_TRI_TRA";
	public static final String PRF_USE_COUL_BAT_COLKEY = "PRF_USE_COUL_BAT";
	public static final String PRF_USE_MAIL_INTERNE_COLKEY = "PRF_USE_MAIL_INTERNE";

	// Non visible colkeys

	// Relationships

	// Create / Init methods

	/**
	 * Creates and inserts a new EOPrefAppli with non null attributes and mandatory relationships.
	 *
	 * @param editingContext
	 * @param noIndividu
	 * @param prfAideSysNav
	 * @param prfConfirmationCloture
	 * @param prfControleChevauchementPlanning
	 * @param prfDroService
	 * @param prfEtatCode
	 * @param prfExportPlanning
	 * @param prfInsertDtSig
	 * @param prfMailTraitement
	 * @param prfOnglet
	 * @param prfOrdreColumns
	 * @param prfPhoto
	 * @param prfSauvegarderPanier
	 * @param prfTri
	 * @param prfTriInt
	 * @param prfTriTra
	 * @param prfUseCoulBat
	 * @param prfUseMailInterne
	 * @return EOPrefAppli
	 */
	public static EOPrefAppli create(EOEditingContext editingContext, Integer noIndividu, String prfAideSysNav, Integer prfConfirmationCloture, Integer prfControleChevauchementPlanning, String prfDroService, String prfEtatCode, Integer prfExportPlanning, Integer prfInsertDtSig, Integer prfMailTraitement, Integer prfOnglet, String prfOrdreColumns, Integer prfPhoto, Integer prfSauvegarderPanier, String prfTri, String prfTriInt, String prfTriTra, Integer prfUseCoulBat, Integer prfUseMailInterne) {
		EOPrefAppli eo = (EOPrefAppli) createAndInsertInstance(editingContext);
		eo.setNoIndividu(noIndividu);
		eo.setPrfAideSysNav(prfAideSysNav);
		eo.setPrfConfirmationCloture(prfConfirmationCloture);
		eo.setPrfControleChevauchementPlanning(prfControleChevauchementPlanning);
		eo.setPrfDroService(prfDroService);
		eo.setPrfEtatCode(prfEtatCode);
		eo.setPrfExportPlanning(prfExportPlanning);
		eo.setPrfInsertDtSig(prfInsertDtSig);
		eo.setPrfMailTraitement(prfMailTraitement);
		eo.setPrfOnglet(prfOnglet);
		eo.setPrfOrdreColumns(prfOrdreColumns);
		eo.setPrfPhoto(prfPhoto);
		eo.setPrfSauvegarderPanier(prfSauvegarderPanier);
		eo.setPrfTri(prfTri);
		eo.setPrfTriInt(prfTriInt);
		eo.setPrfTriTra(prfTriTra);
		eo.setPrfUseCoulBat(prfUseCoulBat);
		eo.setPrfUseMailInterne(prfUseMailInterne);
		return eo;
	}

	/**
	 * Creates and inserts a new empty EOPrefAppli.
	 *
	 * @param editingContext
	 * @return EOPrefAppli
	 */
	public static EOPrefAppli create(EOEditingContext editingContext) {
		EOPrefAppli eo = (EOPrefAppli) createAndInsertInstance(editingContext);
		return eo;
	}

	// Utilities methods

	public EOPrefAppli localInstanceIn(EOEditingContext editingContext) {
		EOPrefAppli localInstance = (EOPrefAppli) localInstanceOfObject(editingContext, (EOPrefAppli) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static EOPrefAppli localInstanceIn(EOEditingContext editingContext, EOPrefAppli eo) {
		EOPrefAppli localInstance = (eo == null) ? null : (EOPrefAppli) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}

	// Accessors methods

	public Integer noIndividu() {
		return (Integer) storedValueForKey("noIndividu");
	}

	public void setNoIndividu(Integer value) {
		takeStoredValueForKey(value, "noIndividu");
	}
	public String prfAideSysNav() {
		return (String) storedValueForKey("prfAideSysNav");
	}

	public void setPrfAideSysNav(String value) {
		takeStoredValueForKey(value, "prfAideSysNav");
	}
	public Integer prfConfirmationCloture() {
		return (Integer) storedValueForKey("prfConfirmationCloture");
	}

	public void setPrfConfirmationCloture(Integer value) {
		takeStoredValueForKey(value, "prfConfirmationCloture");
	}
	public Integer prfControleChevauchementPlanning() {
		return (Integer) storedValueForKey("prfControleChevauchementPlanning");
	}

	public void setPrfControleChevauchementPlanning(Integer value) {
		takeStoredValueForKey(value, "prfControleChevauchementPlanning");
	}
	public String prfDroService() {
		return (String) storedValueForKey("prfDroService");
	}

	public void setPrfDroService(String value) {
		takeStoredValueForKey(value, "prfDroService");
	}
	public String prfEtatCode() {
		return (String) storedValueForKey("prfEtatCode");
	}

	public void setPrfEtatCode(String value) {
		takeStoredValueForKey(value, "prfEtatCode");
	}
	public Integer prfExportPlanning() {
		return (Integer) storedValueForKey("prfExportPlanning");
	}

	public void setPrfExportPlanning(Integer value) {
		takeStoredValueForKey(value, "prfExportPlanning");
	}
	public Integer prfInsertDtSig() {
		return (Integer) storedValueForKey("prfInsertDtSig");
	}

	public void setPrfInsertDtSig(Integer value) {
		takeStoredValueForKey(value, "prfInsertDtSig");
	}
	public Integer prfMailTraitement() {
		return (Integer) storedValueForKey("prfMailTraitement");
	}

	public void setPrfMailTraitement(Integer value) {
		takeStoredValueForKey(value, "prfMailTraitement");
	}
	public Integer prfNbIntPerPage() {
		return (Integer) storedValueForKey("prfNbIntPerPage");
	}

	public void setPrfNbIntPerPage(Integer value) {
		takeStoredValueForKey(value, "prfNbIntPerPage");
	}
	public Integer prfOnglet() {
		return (Integer) storedValueForKey("prfOnglet");
	}

	public void setPrfOnglet(Integer value) {
		takeStoredValueForKey(value, "prfOnglet");
	}
	public String prfOrdreColumns() {
		return (String) storedValueForKey("prfOrdreColumns");
	}

	public void setPrfOrdreColumns(String value) {
		takeStoredValueForKey(value, "prfOrdreColumns");
	}
	public String prfPanier() {
		return (String) storedValueForKey("prfPanier");
	}

	public void setPrfPanier(String value) {
		takeStoredValueForKey(value, "prfPanier");
	}
	public Integer prfPhoto() {
		return (Integer) storedValueForKey("prfPhoto");
	}

	public void setPrfPhoto(Integer value) {
		takeStoredValueForKey(value, "prfPhoto");
	}
	public Integer prfSauvegarderPanier() {
		return (Integer) storedValueForKey("prfSauvegarderPanier");
	}

	public void setPrfSauvegarderPanier(Integer value) {
		takeStoredValueForKey(value, "prfSauvegarderPanier");
	}
	public Integer prfTimer() {
		return (Integer) storedValueForKey("prfTimer");
	}

	public void setPrfTimer(Integer value) {
		takeStoredValueForKey(value, "prfTimer");
	}
	public String prfTri() {
		return (String) storedValueForKey("prfTri");
	}

	public void setPrfTri(String value) {
		takeStoredValueForKey(value, "prfTri");
	}
	public String prfTriInt() {
		return (String) storedValueForKey("prfTriInt");
	}

	public void setPrfTriInt(String value) {
		takeStoredValueForKey(value, "prfTriInt");
	}
	public String prfTriTra() {
		return (String) storedValueForKey("prfTriTra");
	}

	public void setPrfTriTra(String value) {
		takeStoredValueForKey(value, "prfTriTra");
	}
	public Integer prfUseCoulBat() {
		return (Integer) storedValueForKey("prfUseCoulBat");
	}

	public void setPrfUseCoulBat(Integer value) {
		takeStoredValueForKey(value, "prfUseCoulBat");
	}
	public Integer prfUseMailInterne() {
		return (Integer) storedValueForKey("prfUseMailInterne");
	}

	public void setPrfUseMailInterne(Integer value) {
		takeStoredValueForKey(value, "prfUseMailInterne");
	}


	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return _EOPrefAppli.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return _EOPrefAppli.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns EOPrefAppli)
	
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
	public static EOPrefAppli fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static EOPrefAppli fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		EOPrefAppli eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (EOPrefAppli)eoObjects.objectAtIndex(0);
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
	public static EOPrefAppli fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static EOPrefAppli fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static EOPrefAppli fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		EOPrefAppli eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (EOPrefAppli)eoObjects.objectAtIndex(0);
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
	public static EOPrefAppli fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static EOPrefAppli fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		EOPrefAppli eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet EOPrefAppli ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	


	// Internal utilities methods for common use (server AND client)...

	private static EOPrefAppli createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(_EOPrefAppli.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + _EOPrefAppli.ENTITY_NAME + "' !");
		}
		else {
			EOPrefAppli object = (EOPrefAppli) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static EOPrefAppli localInstanceOfObject(EOEditingContext ec, EOPrefAppli object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The EOPrefAppli " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return (EOPrefAppli) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
