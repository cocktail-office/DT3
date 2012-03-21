
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
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Controlleur de la page de seletion d'une ligne budgetaire.
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class SelectLigneBud extends DTWebPage {
  private static String NonSelectionneTitle = "<non s&eactue;lectionn&eacute;";
//- Pour le CktlHXBrowser -
  /** Le chemin les lignes budgetaires actuellement selectionnees. */
  public NSMutableArray lbudSelectedPath;
  private LigneBudNode lbudSelectedItem;
  private LigneBudNode lbudZeroItem;
  private SelectLigneBudListener listener;
  //
  public NSMutableArray typeCreditList;
  public CktlRecord typeCreditItem;
  public CktlRecord typeCreditSelected;
  private NSArray typeCreditAutorises;
  
  /**
   * Cree une nouvelle instance du composant dans le context donne.
   */
  public SelectLigneBud(WOContext context) {
    super(context);
    lbudSelectedPath = new NSMutableArray();
    typeCreditList = new NSMutableArray();
  }

  /**
   * Annule toutes les selections et definitions.
   */
  public void resetPage() {
    lbudSelectedPath.removeAllObjects();
    typeCreditList.removeAllObjects();
    typeCreditAutorises = new NSArray();
    typeCreditSelected = null;
    lbudSelectedItem = null;
  }
  
  public void setListener(SelectLigneBudListener listener) {
    this.listener = listener;
  }

  public void setTypeCreditAutorises(NSArray typesAutorises) {
    CktlLog.trace("typesAutorises : "+typesAutorises);
    if (typesAutorises == null)
      typeCreditAutorises = new NSArray();
    else
      typeCreditAutorises = typesAutorises;
  }
  
  private void initTypeCreditList() {
    typeCreditList.removeAllObjects();
    // On ne peut selectionner le type de credit qu'a partir
    // du niveau 3
    if ((lbudSelectedPath.count() >= 3) && (lbudSelectedItem != null)) {
        CktlRecord unTcd = lbudSelectedItem.contentRecord();
        NSArray tcdList = unTcd.arrayForKeyPath(
        		"tosVJefyBudgetExecCredit.toVJefyTypeCredit"); 
        CktlLog.trace("selectedRec.entity : "+unTcd.classDescription().entityName());
        CktlLog.trace("selectedRec : "+unTcd);
        for (int i=0; i<tcdList.count(); i++) {
          unTcd = (CktlRecord)tcdList.objectAtIndex(i);
          // On verifie si le type de credit est autorise
          if (typeCreditAutorises.count() > 0) {
            if (!typeCreditAutorises.containsObject(unTcd.valueForKey("tcdCode")))
              unTcd = null;
          }
          if (unTcd != null)
            typeCreditList.addObject(unTcd);
        }
    }
    CktlLog.trace("typeCreditList.count : "+typeCreditList.count());
  }
  
  private String getTitleForItem(int level, String fieldName) {
    if (lbudSelectedPath.count() > level) {
      LigneBudNode node = (LigneBudNode)lbudSelectedPath.objectAtIndex(level);
      return node.contentRecord().stringForKey(fieldName)+"&nbsp;:&nbsp;"+
             node.contentRecord().stringForKey("orgLib");
    }
    return NonSelectionneTitle;
  }

  public String getTitleUnite() {
    return getTitleForItem(0, "orgUnit");
  }

  public String getTitleComposante() {
    return getTitleForItem(1, "orgComp");
  }
  
  public String getTitleLigneBud() {
    return getTitleForItem(2, "orgLbud");
  }
  
  public String getTitleUC() {
    return getTitleForItem(3, "orgUc");
  }

  public boolean hasNoTypeCredit() {
//    CktlLog.trace("noTypeCredit : "+
//        ((lbudSelectedPath.count() < 4) || (lbudSelectedItem == null)));
//    return ((lbudSelectedPath.count() < 4) || (lbudSelectedItem == null));
    return (typeCreditList.count() == 0);
  }

  public String typeCreditDisplayString() {
    if (typeCreditItem == null)
      return "&lt;aucun type de cr&eacute;dit&gt;";
    else
      return typeCreditItem.stringForKey("tcdLib");
  }
  
  /**
   * La methode executee suite a la selection d'une ligne budgetaire dans
   * le "browser".
   */
  public void selectLigne() {
  }

  public void setLbudSelectedItem(LigneBudNode item) {
    lbudSelectedItem = item;
    initTypeCreditList();
//    resetSwapVisibility();
  }

  public LigneBudNode getLbudSelectedItem() {
    return lbudSelectedItem;
  }

  public LigneBudNode lbudZeroItem() {
    if (lbudZeroItem == null)
      lbudZeroItem = LigneBudNode.getNewZeroNode(dtSession());
    CktlLog.trace("zeroItem : "+lbudZeroItem);
    return lbudZeroItem;
  }

  public WOComponent annuler() {
    return listener.cancel();
  }
  
  public WOComponent select() {
    return listener.select(
        (Integer)lbudSelectedItem.contentRecord().numberForKey("orgId"),
        typeCreditSelected.stringForKey("tcdCode"));
  }
}