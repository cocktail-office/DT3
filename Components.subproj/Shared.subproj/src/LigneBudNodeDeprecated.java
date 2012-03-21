
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlHXBrowserNode;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/*
 * Copyright CRI - Universite de La Rochelle, 1995-2005 
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

/**
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class LigneBudNodeDeprecated extends CktlHXBrowserNode {
  // La liste des nom des champs d'enregistrement a afficher dans differents
  // collones
  private static NSMutableArray champs;
  private static NSMutableDictionary champsSorts;
  private Session dtSession;
  private int column;
  private CktlRecord recContent;
  
  private LigneBudNodeDeprecated(Session session, int column, CktlRecord content) {
    this.dtSession = session;
    this.column = column;
    this.recContent = content;
  }
  
  public static LigneBudNodeDeprecated getNewZeroNode(Session session) {
    return new LigneBudNodeDeprecated(session, -1, null);
  }
  
  public CktlRecord contentRecord() {
    return recContent;
  }
  
  private DTJefyBus jefyBus() {
    return dtSession.dataCenter().jefyBus();
  }
  
  private NSArray champs() {
    if (champs == null) {
      // les champs correspondant aux colones de browser
      champs = new NSMutableArray();
      champs.addObject("orgUnit");
      champs.addObject("orgComp");
      champs.addObject("orgLbud");
      champs.addObject("orgUc");
    }
    return champs;
  }

  private NSArray sortForColumn(int colNum) {
    if (champsSorts == null) champsSorts = new NSMutableDictionary();
    String colId = Integer.toString(colNum);
    NSArray sort = (NSArray)champsSorts.objectForKey(colId);
    if (sort == null) {
      sort = CktlSort.newSort((String)champs().objectAtIndex(colNum));
      champsSorts.setObjectForKey(sort, colId);
    }
    return sort;
  }
  

  /*
   * @see CktlHXBrowserNode#retrieveRootNodes()
   */
  public NSArray retrieveRootNodes() {
    return nodesForColumn(0);
  }

  /*
   * @see CktlHXBrowserNode#retrieveChildrenNodes()
   */
  public NSArray retrieveChildrenNodes() {
    return nodesForColumn(column+1);
  }

  /*
   * @see CktlHXBrowserNode#isLeaf()
   */
  public boolean isLeaf() {
    return (column == 3);
  }

  /*
   * @see CktlHXBrowserNode#displayName()
   */
  public String displayName() {
    if (recContent == null)
      return "<zero item>"; //StringCtrl.emptyString();
    return StringCtrl.normalize(recContent.stringForKey((String)champs.objectAtIndex(column)));
  }

  private NSArray nodesForColumn(int colNum) {
    NSMutableArray nodes = new NSMutableArray();
    NSArray sort = sortForColumn(colNum);
    CktlRecord rec;
    NSArray objects;
    LigneBudNodeDeprecated aNode;
    
    if (colNum == 0) { // Roote nodes, niveau 1
      objects = jefyBus().findLignesBud(
          null, null, null, new Integer(1), null, sort); 
    } else { // Colonne >= 1, les fils de l'objet en cours
      objects = jefyBus().findLignesBud(
          null, null, null, null, recContent.numberForKey("orgOrdre"), sort);
    }
    // On construit la suite du fetch
    for(int i=0; i<objects.count(); i++) {
      rec = (CktlRecord)objects.objectAtIndex(i);
      aNode = new LigneBudNodeDeprecated(dtSession, colNum, rec);
      nodes.addObject(aNode);
    }
    return nodes;
  }

  public String toString() {
    return displayName();
  }
}
