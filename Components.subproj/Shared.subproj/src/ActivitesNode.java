
/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
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
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlHXBrowserNode;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;

/**
 * Gere le contenu des "browser" des activites des services. Cette classe
 * permet de recuperer les donnees et d'initialiser le contenu du chaque
 * cologne du browser.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class ActivitesNode 
	extends CktlHXBrowserNode 
		implements NSKeyValueCoding {
	
  private static String condStringRoot;
  private static String condStringRootAll;
  private static String condStringChild;
  private static NSArray<CktlSort> sortOrder;
  private static NSArray<EOSortOrdering> sortOrder1;
  private String rootCStructure;

  public EOVActivites nodeRecord;
  private NSArray<ActivitesNode> children;
  
  private static int compteur = 1;
  private String label;
  
  // le bus pour faire les fetch
  private CktlDataBus dataBus;
  
  // le pointeur vers le tableau contenant tous les nodes
  private NSArray<ActivitesNode> allNodes;
  
  // affiche-t-on les activites cachees
  private boolean showHidden;
  private boolean showUnderscore;
  
  static {
    condStringRoot 		= "cStructure=%@ AND actPere = %@ AND actOrdre <> 0";
    condStringRootAll = "actPere = %@ AND actOrdre <> 0";
    condStringChild 	= "actPere = %@";
    sortOrder 				= CktlSort.newSort(EOVActivites.ACT_LIBELLE_KEY, CktlSort.Ascending);
    sortOrder1 = 		new NSArray(EOSortOrdering.sortOrderingWithKey(EOVActivites.ACT_LIBELLE_KEY, EOSortOrdering.CompareAscending)); 
  }
  
  /**
   * 
   * @param theRecord
   * @param aDataBus
   * @param someAllNodes
   * @param aShowHidden
   * @param aShowUnderscore : faut-il montrer les activités dont le libellé commence par "_"
   */
  public ActivitesNode(
  		EOVActivites theRecord,
  		CktlDataBus aDataBus, 
  		NSArray someAllNodes, 
  		boolean aShowHidden,
  		boolean aShowUnderscore) {
    nodeRecord = theRecord;
    dataBus = aDataBus;
    allNodes = someAllNodes;
    showHidden = aShowHidden;
    showUnderscore = aShowUnderscore;
    rootCStructure = null;    
  }
  
  public String label() {
    if (label == null) {
      label = ""+compteur++;
      if (nodeRecord != null)
        label +=" "+displayName();
    }
    return label;
  }

  public void setRootCStructure(String newRootCStructure) {
    rootCStructure = newRootCStructure;
  }

  public String getRootCStructure() {
    return rootCStructure;
  }
  
  public NSArray retrieveRootNodes() {
     return objectsToNodes(
         selectObjects(Integer.valueOf("-"+rootCStructure), rootCStructure));
  }

  private NSArray<ActivitesNode> objectsToNodes(NSArray<EOVActivites> objects) {
    NSMutableArray<ActivitesNode> items = new NSMutableArray<ActivitesNode>();
    for(int i=0; i<objects.count(); i++) {
      ActivitesNode node = newInstance(objects.objectAtIndex(i), dataBus);
      node.setRootCStructure(rootCStructure);
      items.addObject(node);
    }
    return items;
  }
  
  private NSArray<EOVActivites> selectObjects(Number actPereOrdre, String codeStructure) {
  	// cas particulier, si actPereOrdre=0, c'est qu'on veut tout l'arbre
  	// d'ou pas de filtrage sur la structure
  	EOQualifier condition = null;
  	if (actPereOrdre.intValue() != 0) {
    	String strQual = condStringRoot + (!showHidden ? EOVActivites.COND_HIDE_ACTIVITE : "") + (!showUnderscore ? EOVActivites.COND_HIDE_UNDERSCORE : "");
  		condition = EOQualifier.qualifierWithQualifierFormat(strQual, new NSArray(new Object[]{codeStructure, actPereOrdre}));
  	}	else {
  		String strQual = condStringRootAll + (!showHidden ? EOVActivites.COND_HIDE_ACTIVITE : "") + (!showUnderscore ? EOVActivites.COND_HIDE_UNDERSCORE : "");
  		condition = EOQualifier.qualifierWithQualifierFormat(strQual, new NSArray(new Object[]{actPereOrdre}));
  	}
    NSArray objects = dataBus.fetchArray(
    		EOVActivites.ENTITY_NAME, condition, sortOrder);
    return sortActivites(objects);
  }

  private NSArray sortActivites(NSArray activites) {
	    return EOSortOrdering.sortedArrayUsingKeyOrderArray(activites, sortOrder1);
  }
  
  public String displayName() {
    if (nodeRecord == null) return StringCtrl.emptyString();
    else return (String)nodeRecord.valueForKey("actLibelle");
  }
  
  public Number actOrdre() {
    if (nodeRecord == null) return null;
    else return nodeRecord.numberForKey("actOrdre");
  }

  public NSArray retrieveChildrenNodes() {
    if (children == null) {
    	String strQual = condStringChild + (!showHidden ? EOVActivites.COND_HIDE_ACTIVITE : "") + (!showUnderscore ? EOVActivites.COND_HIDE_UNDERSCORE : "");
      children = dataBus.fetchArray(
          EOVActivites.ENTITY_NAME, EOQualifier.qualifierWithQualifierFormat(strQual, new NSArray(actOrdre())), sortOrder);
      children = objectsToNodes(sortActivites(children));
    }
    return children;
  }
  
  public boolean isLeaf() {
    return childrenNodes().count() == 0;
  }

  // completment pour faire des fetch sur l'actOdre
  // NE PAS UTILISER POUR AUTRE CHOSE QUE actOrdre !!!!
  public Object valueForKey(String arg0) {
    if (arg0 != null && arg0.equals("actOrdre"))
      return actOrdre();
    if (arg0 != null && arg0.equals("label"))
      return label();
    if (arg0 != null && arg0.equals("actSwapMessage"))
      return nodeRecord.valueForKeyPath("toActPref.actSwapMessage");
    return null;
  }

  public void takeValueForKey(Object arg0, String arg1) {
    
  }
  
  
  /**
   * Avant d'instancier, on regarde dans la session si
   * l'objet n'existe pas deja
   */
  private ActivitesNode newInstance(EOVActivites theRecord, CktlDataBus aDataBus) {
    ActivitesNode theInstance = null;
    if (theRecord == null) {
    	theInstance = new ActivitesNode(theRecord, aDataBus, allNodes, showHidden, showUnderscore);
    } else {
      theInstance = findNode(theRecord);
      if (theInstance == null) {
        theInstance = new ActivitesNode(theRecord, aDataBus, allNodes, showHidden, showUnderscore);
        allNodes = allNodes.arrayByAddingObject(theInstance);
      }
    }
    return theInstance;
  }
  
  /**
   * Methode interne permettant de retrouver le node associe a une activite
   * @param someNodes :  les nodes d'un niveau de l'arbo parmi lesquesl on cherche
   * @param recVActivite : l'activite en question
   */
  public ActivitesNode findNode(EOVActivites recVActivite) {
    NSArray<ActivitesNode> nodes = EOQualifier.filteredArrayWithQualifier(allNodes, 
        CktlDataBus.newCondition("actOrdre=%@", new NSArray(recVActivite.actOrdre())));
    if (nodes.count() > 0) 
      return nodes.lastObject();
    return null;
  }
  
}

