// ${entity.prefixClassNameWithoutPackage}.java
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

// DO NOT EDIT.  Make changes to ${entity.classNameWithOptionalPackage}.java instead.
#if ($entity.superclassPackageName)
package $entity.superclassPackageName;

#end
import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

import com.webobjects.foundation.*;
import com.webobjects.eocontrol.*;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.NoSuchElementException;

@SuppressWarnings("all")
public abstract class ${entity.prefixClassNameWithoutPackage} extends #if ($entity.partialEntitySet)er.extensions.partials.ERXPartial<${entity.partialEntity.className}>#elseif ($entity.parentSet)${entity.parent.classNameWithDefault}#elseif ($EOGenericRecord)${EOGenericRecord}#else CktlRecord#end {
#if ($entity.partialEntitySet)
	public static final String ENTITY_NAME = "$entity.partialEntity.name";
#else
	public static final String ENTITY_NAME = "$entity.name";
#end
#if ($entity.externalName)
	public static final String ENTITY_TABLE_NAME = "$entity.externalName";
#end

	// Attributes
#set( $pkCount = $entity.primaryKeyAttributes.size() )  
#if ($pkCount == 1)
	public static final String ENTITY_PRIMARY_KEY = "$entity.singlePrimaryKeyAttribute.name";
#end	

#foreach ($attribute in $entity.sortedClassAttributes)
	public static final String ${attribute.uppercaseUnderscoreName}_KEY = "$attribute.name";
#end

	// Non visible attributes
#foreach ($attribute in $entity.nonClassAttributes)
	public static final String ${attribute.uppercaseUnderscoreName}_KEY = "$attribute.name";
#end

	// Colkeys
#foreach ($attribute in $entity.sortedClassAttributes)
	public static final String ${attribute.uppercaseUnderscoreName}_COLKEY = "$attribute.columnName";
#end

	// Non visible colkeys
#foreach ($attribute in $entity.nonClassAttributes)
	public static final String ${attribute.uppercaseUnderscoreName}_COLKEY = "$attribute.columnName";
#end

	// Relationships
#foreach ($relationship in $entity.sortedClassRelationships)
	public static final String ${relationship.uppercaseUnderscoreName}_KEY = "$relationship.name";
#end

	// Create / Init methods

	/**
#if (!$entity.partialEntitySet)
	 * Creates and inserts a new ${entity.classNameWithOptionalPackage} with non null attributes and mandatory relationships.
#else
	 * Init method for this object.
#end
	 *
	 * @param editingContext
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)
#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false')
	 * @param ${attribute.name}
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))
	 * @param ${relationship.name}
#end
#end
	 * @return ${entity.classNameWithOptionalPackage}
	 */
	public #if (!$entity.partialEntitySet)static #end${entity.classNameWithOptionalPackage}#if (!$entity.partialEntitySet) create#else init#end(EOEditingContext editingContext#foreach ($attribute in $entity.sortedClassAttributes)#if (!$attribute.allowsNull)#set ($restrictingQualifierKey = 'false')#foreach ($qualifierKey in $entity.restrictingQualifierKeys)#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')#end#end#if ($restrictingQualifierKey == 'false')#if ($attribute.userInfo.ERXConstantClassName), ${attribute.userInfo.ERXConstantClassName}#else, ${attribute.javaClassName}#end ${attribute.name}#end#end#end#foreach ($relationship in $entity.sortedClassToOneRelationships)#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey)), ${relationship.actualDestination.classNameWithDefault} ${relationship.name}#end#end) {
		${entity.classNameWithOptionalPackage} eo = (${entity.classNameWithOptionalPackage})#if ($entity.partialEntitySet)this;#else createAndInsertInstance(editingContext);
#end
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)
#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false')
		eo.set${attribute.capitalizedName}(${attribute.name});
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))
		eo.set${relationship.capitalizedName}Relationship(${relationship.name});
#end
#end
		return eo;
	}

#if (!$entity.partialEntitySet)
#set ($hasNonNullAttributes = 'false')
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)
#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false')
#set ($hasNonNullAttributes = 'true')
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))
#set ($hasNonNullAttributes = 'true')
#end
#end
#if ($hasNonNullAttributes == 'true')
	/**
	 * Creates and inserts a new empty ${entity.classNameWithOptionalPackage}.
	 *
	 * @param editingContext
	 * @return ${entity.classNameWithOptionalPackage}
	 */
	public static ${entity.classNameWithOptionalPackage} create(EOEditingContext editingContext) {
		${entity.classNameWithOptionalPackage} eo = (${entity.classNameWithOptionalPackage}) createAndInsertInstance(editingContext);
		return eo;
	}
#end
#end

	// Utilities methods

#if (!$entity.partialEntitySet)
	public $entity.classNameWithOptionalPackage localInstanceIn(EOEditingContext editingContext) {
		$entity.classNameWithOptionalPackage localInstance = ($entity.classNameWithOptionalPackage) localInstanceOfObject(editingContext, ($entity.classNameWithOptionalPackage) this);
		if (localInstance == null) {
			throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
		}
		return localInstance;
	}

	public static ${entity.classNameWithOptionalPackage} localInstanceIn(EOEditingContext editingContext, ${entity.classNameWithOptionalPackage} eo) {
		${entity.classNameWithOptionalPackage} localInstance = (eo == null) ? null : (${entity.classNameWithOptionalPackage}) localInstanceOfObject(editingContext, eo);
		if (localInstance == null && eo != null) {
			throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
		}
		return localInstance;
	}
#end

	// Accessors methods

#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.inherited)
#if ($attribute.userInfo.ERXConstantClassName)
	public $attribute.userInfo.ERXConstantClassName ${attribute.name}() {
		Number value = (Number)storedValueForKey("$attribute.name");
		return ($attribute.userInfo.ERXConstantClassName)value;
	}

	public void set${attribute.capitalizedName}($attribute.userInfo.ERXConstantClassName value) {
		takeStoredValueForKey(value, "$attribute.name");
	}
#else
	public $attribute.javaClassName ${attribute.name}() {
		return ($attribute.javaClassName) storedValueForKey("$attribute.name");
	}

	public void set${attribute.capitalizedName}($attribute.javaClassName value) {
		takeStoredValueForKey(value, "$attribute.name");
	}
#end
#end
#end

#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if (!$relationship.inherited) 
	public $relationship.actualDestination.classNameWithDefault ${relationship.name}() {
		return ($relationship.actualDestination.classNameWithDefault)storedValueForKey("$relationship.name");
	}

	public void set${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault value) {
		if (value == null) {
			$relationship.actualDestination.classNameWithDefault oldValue = ${relationship.name}();
			if (oldValue != null) {
				removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "$relationship.name");
			}
		} else {
			addObjectToBothSidesOfRelationshipWithKey(value, "$relationship.name");
		}
	}
  
#end
#end
#foreach ($relationship in $entity.sortedClassToManyRelationships)
#if (!$relationship.inherited) 
	public NSArray ${relationship.name}() {
		return (NSArray)storedValueForKey("${relationship.name}");
	}

#if (!$relationship.inverseRelationship || $relationship.flattened || !$relationship.inverseRelationship.classProperty)
	public NSArray ${relationship.name}(EOQualifier qualifier) {
		return ${relationship.name}(qualifier, null);
	}
#else
	public NSArray ${relationship.name}(EOQualifier qualifier) {
		return ${relationship.name}(qualifier, null, false);
	}

	public NSArray ${relationship.name}(EOQualifier qualifier, boolean fetch) {
		return ${relationship.name}(qualifier, null, fetch);
	}
#end

	public NSArray ${relationship.name}(EOQualifier qualifier, NSArray sortOrderings#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty), boolean fetch#end) {
		NSArray results;
		#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty)
		if (fetch) {
			EOQualifier fullQualifier;
			#if (${relationship.actualDestination.genericRecord})
			EOQualifier inverseQualifier = new EOKeyValueQualifier("${relationship.inverseRelationship.name}", EOQualifier.QualifierOperatorEqual, this);
			#else
			EOQualifier inverseQualifier = new EOKeyValueQualifier(${relationship.actualDestination.classNameWithDefault}.${relationship.inverseRelationship.uppercaseUnderscoreName}_KEY, EOQualifier.QualifierOperatorEqual, this);
			#end

			if (qualifier == null) {
				fullQualifier = inverseQualifier;
			}
			else {
				NSMutableArray qualifiers = new NSMutableArray();
				qualifiers.addObject(qualifier);
				qualifiers.addObject(inverseQualifier);
				fullQualifier = new EOAndQualifier(qualifiers);
			}

			#if (${relationship.actualDestination.genericRecord})
			EOFetchSpecification fetchSpec = new EOFetchSpecification("${relationship.actualDestination.name}", qualifier, sortOrderings);
			fetchSpec.setIsDeep(true);
			results = (NSArray)editingContext().objectsWithFetchSpecification(fetchSpec);
			#else
			results = ${relationship.actualDestination.classNameWithDefault}.fetchAll(editingContext(), fullQualifier, sortOrderings);
			#end
		}
		else {
		#end
		results = ${relationship.name}();
		if (qualifier != null) {
			results = (NSArray)EOQualifier.filteredArrayWithQualifier(results, qualifier);
		}
		if (sortOrderings != null) {
			results = (NSArray)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
		}
		#if ($relationship.inverseRelationship && !$relationship.flattened && $relationship.inverseRelationship.classProperty)
		}
		#end
		return results;
	}
  
	public void addTo${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
		addObjectToBothSidesOfRelationshipWithKey(object, "${relationship.name}");
	}

	public void removeFrom${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
	}

	public $relationship.actualDestination.classNameWithDefault create${relationship.capitalizedName}Relationship() {
		EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("${relationship.actualDestination.name}");
		EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
		editingContext().insertObject(eo);
		addObjectToBothSidesOfRelationshipWithKey(eo, "${relationship.name}");
		return ($relationship.actualDestination.classNameWithDefault) eo;
	}

	public void delete${relationship.capitalizedName}Relationship($relationship.actualDestination.classNameWithDefault object) {
		removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
		#if (!$relationship.ownsDestination)
		editingContext().deleteObject(object);
		#end
	}

	public void deleteAll${relationship.capitalizedName}Relationships() {
		Enumeration objects = ${relationship.name}().immutableClone().objectEnumerator();
		while (objects.hasMoreElements()) {
			delete${relationship.capitalizedName}Relationship(($relationship.actualDestination.classNameWithDefault)objects.nextElement());
		}
	}
#end
#end

#if (!$entity.partialEntitySet)
	// Finders

	// Fetching many (returns NSArray)
	
	public static NSArray fetchAll(EOEditingContext editingContext) {
		return ${entity.prefixClassNameWithoutPackage}.fetchAll(editingContext, (NSArray) null);
	}

	public static NSArray fetchAll(EOEditingContext editingContext, NSArray sortOrderings) {
		return ${entity.prefixClassNameWithoutPackage}.fetchAll(editingContext, null, sortOrderings);
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
	

	// Fetching one (returns ${entity.classNameWithOptionalPackage})
	
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
	public static ${entity.classNameWithOptionalPackage} fetchByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws IllegalStateException {
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
	public static ${entity.classNameWithOptionalPackage} fetchByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws IllegalStateException {
		NSArray eoObjects = fetchAll(editingContext, qualifier, null);
		${entity.classNameWithOptionalPackage} eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else if (count == 1) {
			eoObject = (${entity.classNameWithOptionalPackage})eoObjects.objectAtIndex(0);
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
	public static ${entity.classNameWithOptionalPackage} fetchFirstByKeyValue(EOEditingContext editingContext, String keyName, Object value) {
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
	public static ${entity.classNameWithOptionalPackage} fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier) {
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
	public static ${entity.classNameWithOptionalPackage} fetchFirstByQualifier(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) {
		NSArray eoObjects = fetchAll(editingContext, qualifier, sortOrderings);
		${entity.classNameWithOptionalPackage} eoObject;
		int count = eoObjects.count();
		if (count == 0) {
			eoObject = null;
		}
		else {
			eoObject = (${entity.classNameWithOptionalPackage})eoObjects.objectAtIndex(0);
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
	public static ${entity.classNameWithOptionalPackage} fetchFirstRequiredByKeyValue(EOEditingContext editingContext, String keyName, Object value) throws NoSuchElementException {
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
	public static ${entity.classNameWithOptionalPackage} fetchFirstRequiredByQualifier(EOEditingContext editingContext, EOQualifier qualifier) throws NoSuchElementException {
		${entity.classNameWithOptionalPackage} eoObject = fetchFirstByQualifier(editingContext, qualifier);
		if (eoObject == null) {
			throw new NoSuchElementException("Aucun objet ${entity.classNameWithOptionalPackage} ne correspond au qualifier '" + qualifier + "'.");
		}
		return eoObject;
	}	

	// FetchSpecs...
	
#foreach ($fetchSpecification in $entity.sortedFetchSpecs)
#if (true || $fetchSpecification.distinctBindings.size() > 0)
	public static NSArray fetch${fetchSpecification.capitalizedName}(EOEditingContext editingContext, NSDictionary bindings) {
		EOFetchSpecification fetchSpec = EOFetchSpecification.fetchSpecificationNamed("${fetchSpecification.name}", "${entity.name}");
		fetchSpec = fetchSpec.fetchSpecificationWithQualifierBindings(bindings);
		return (NSArray)editingContext.objectsWithFetchSpecification(fetchSpec);
	}
#end
#end

#end

	// Internal utilities methods for common use (server AND client)...

	private static $entity.classNameWithOptionalPackage createAndInsertInstance(EOEditingContext ec) {
		EOClassDescription classDescription = EOClassDescription.classDescriptionForEntityName(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);
		if(classDescription == null) {
			throw new IllegalArgumentException("Could not find EOClassDescription for entity name '" + ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME + "' !");
		}
		else {
			$entity.classNameWithOptionalPackage object = ($entity.classNameWithOptionalPackage) classDescription.createInstanceWithEditingContext(ec, null);
			ec.insertObject(object);
			return object;
		}
	}

	private static $entity.classNameWithOptionalPackage localInstanceOfObject(EOEditingContext ec, $entity.classNameWithOptionalPackage object) {
		if(object != null && ec != null) {
			EOEditingContext otherEditingContext = object.editingContext();
			if(otherEditingContext == null) {
				throw new IllegalArgumentException("The $entity.classNameWithOptionalPackage " + object + " is not in an EOEditingContext.");
			}
			else {
				com.webobjects.eocontrol.EOGlobalID globalID = otherEditingContext.globalIDForObject(object);
				return ($entity.classNameWithOptionalPackage) ec.faultForGlobalID(globalID, ec);
			}
		}
		else {
			return null;
		}
	}

}
