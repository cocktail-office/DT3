
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
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlHXBrowserNode;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Gere le contenu des "browser" des services. Cette classe
 * permet de recuperer les donnees et d'initialiser le contenu du chaque
 * cologne du browser.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class StructureNode extends CktlHXBrowserNode {
  private static String condStringRoot;
  private static String condStringChild;
  private static NSArray sortOrder;
  private String rootCStructure;

  public CktlRecord nodeRecord;
  private NSArray children;
  private Session dtSession;
  
  static {
    condStringRoot = "cStructure=cStructurePere and cTypeStructure='E'";
    condStringChild = "cStructurePere= %@ and grpAcces='+'";
    sortOrder = CktlSort.newSort("llStructure", CktlSort.Ascending);
//    sortOrder = new NSArray(
//        EOSortOrdering.sortOrderingWithKey("actLibelle", 
//            EOSortOrdering.CompareAscending));
  }
  
  public StructureNode(CktlRecord theRecord, Session session) {
    nodeRecord = theRecord;
    dtSession = session;
    rootCStructure = null;
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

  private NSArray objectsToNodes(NSArray objects) {
    NSMutableArray items = new NSMutableArray();
    for(int i=0; i<objects.count(); i++) {
      StructureNode node = new StructureNode((CktlRecord)objects.objectAtIndex(i), dtSession);
      node.setRootCStructure(rootCStructure);
      items.addObject(node);
    }
    return items;
  }
  
  private NSArray selectObjects(Number actPereOrdre, String codeStructure) {
    NSArray objects;
//    NSMutableArray args = new NSMutableArray();
    EOQualifier condition;
//    args.addObject(codeStructure);
//    args.addObject(actPereOrdre);
    condition = EOQualifier.qualifierWithQualifierFormat(
        condStringRoot, null);
    objects = dtSession.dataBus().fetchArray("DTStructureUlr", condition, sortOrder);
    return sortActivites(objects);
  }

  private NSArray sortActivites(NSArray activites) {
    return EOSortOrdering.sortedArrayUsingKeyOrderArray(activites, sortOrder);
  }

  public String toString() {
    return displayName();
  }
  
  public String displayName() {
    String display = StringCtrl.emptyString();
    if (nodeRecord != null) {
      String llStructure = (String)nodeRecord.valueForKey("llStructure");
      if (!StringCtrl.isEmpty(llStructure)) {
        display = llStructure;
        if (display.length() > 40)
          display = display.substring(0,37) + "...";
      }
    }
    return display;
  }

  public NSArray retrieveChildrenNodes() {
    if (children == null) {
      children = dtSession.dataBus().fetchArray(
          "DTStructureUlr", EOQualifier.qualifierWithQualifierFormat(
              condStringChild,
              new NSArray(nodeRecord.valueForKey("cStructure"))),
              sortOrder);
      children = objectsToNodes(sortActivites(children));
    }
    return children;
  }

  public boolean isLeaf() {
    return childrenNodes().count() == 0;
  }
}
