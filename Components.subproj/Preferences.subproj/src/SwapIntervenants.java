/*
 * Copyright CRI - Universite de La Rochelle, 2001-2006
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
 * Choix de visibilite sr les DTs des intervenants et services
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class SwapIntervenants extends PreferencesSwapView {

  // la liste des services visibles
	public NSArray serviceList;
  public CktlRecord serviceItem;
  public CktlRecord serviceSelected;
  // la liste des intervenants
  public WODisplayGroup individuDG;
  public CktlRecord individuItem;
  private boolean shouldResfreshIndividuDG;
  // ignorer l'appel au setter
  //private boolean shouldIgnoreSetterIndividuSelecteds;
  
  public SwapIntervenants(WOContext context) {
    super(context);
  }

  public void appendToResponse(WOResponse arg0, WOContext arg1) {
    if (shouldResfreshIndividuDG) {
      refreshIndividuDG();
      shouldResfreshIndividuDG = false;
    }
    super.appendToResponse(arg0, arg1);
  }
  
  protected void initComponent() {
    // liste des services
    serviceList = (NSArray) pBus().findAllPrefDroits(dtUserInfo().noIndividu()).valueForKey("toStructureUlr");
    // selection de celui des prefs
    if (serviceList.count() > 0) {
      String prfDroService = pBus().getDefaultServiceCode(dtUserInfo().noIndividu());
      if (!StringCtrl.isEmpty(prfDroService))
        setServiceSelected(sBus().structureForCode(prfDroService));
      else
        setServiceSelected((CktlRecord) serviceList.objectAtIndex(0));
    }
    // selection des individus
    refreshIndividuDG();
    NSArray noIndividus = dtUserInfo().noIntervenatsArray();
    NSArray selections = new NSArray();
    for (int i = 0; i < individuDG.displayedObjects().count(); i++) {
      CktlRecord individu = (CktlRecord)individuDG.displayedObjects().objectAtIndex(i);
      if (noIndividus.containsObject(individu.numberForKey("noIndividu")))
        selections = selections.arrayByAddingObject(individu);
    }
    individuDG.setSelectedObjects(selections);
    //shouldIgnoreSetterIndividuSelecteds = true;
  }

  // getters
  public NSArray individuSelecteds() {
    return individuDG.selectedObjects();
  }
  
  public void setIndividuSelecteds(NSArray value) {
//    if (shouldIgnoreSetterIndividuSelecteds == false) {
      individuDG.setSelectedObjects(value);
      // construction de la chaine qui va bien
      String intervenants = "";
      if (individuDG.selectedObjects().count() > 0) {
        for (int i = 0; i < individuDG.selectedObjects().count(); i++) {
          CktlRecord individu = (CktlRecord) individuDG.selectedObjects().objectAtIndex(i);
          intervenants += individu.intForKey("noIndividu");
          if (i != individuDG.selectedObjects().count() -1)
            intervenants += "|";
        }
      }
      if (StringCtrl.isEmpty(intervenants))
        intervenants = Integer.toString(dtUserInfo().noIndividu().intValue());
      pBus().updatePrefDroits(null, dtUserInfo().noIndividu(), serviceSelected.stringForKey("cStructure"),
          intervenants, null);
      dtSession().doReloadPreferences();
    /*} else
      shouldIgnoreSetterIndividuSelecteds = false;*/
  }
  
  // override setter
  public void setServiceSelected(CktlRecord value) {
    if (value != null) {
      serviceSelected = value;
      shouldResfreshIndividuDG = true;
      pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), serviceSelected.stringForKey("cStructure"),
          null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
      // par defaut, selection de soi meme
      selectMoi();
      dtSession().doReloadPreferences();
    }
  }
  

  /**
   * forcer les donnes du DisplayGroup a etre rechargees.
   */
  private void refreshIndividuDG() {
   // on clean le dico de la fetchSpec
    individuDG.queryBindings().setDictionary(new NSDictionary());
    if (serviceSelected != null) {
      NSArray records = pBus().findAllIntervenantsService(serviceSelected.stringForKey("cStructure"));
      individuDG.setObjectArray(records);
      //pBus().fetchIntervenantsService(individuDG, serviceSelected.stringForKey("cStructure"));
    }
    else
     individuDG.setObjectArray(new NSArray());
  }
 
  /**
   * Selectionner tous les intervenants
   */
  public WOComponent selectTous() {
    setIndividuSelecteds(individuDG.displayedObjects());
    return null;
  }

  /**
   * Selection vide
   */
  public WOComponent selectAucun() {
    setIndividuSelecteds(new NSArray());
    return null;
  }

  /**
   * Selection individu connecte
   */
  public WOComponent selectMoi() {
    if (individuDG.displayedObjects().count() > 0) {
      // raz de la selection du DisplayGroup
      individuDG.selectObject(individuDG.displayedObjects().objectAtIndex(0));
      // selection de lui meme (ne marche pas avec intervenantsDisplayGroup.select(recIntervenant)
      for (int i = 0; i < individuDG.displayedObjects().count(); i++) {
        Number noIndividu = ((CktlRecord) individuDG.displayedObjects().objectAtIndex(i)).
          numberForKey("noIndividu");
        if (noIndividu.intValue() == dtUserInfo().noIndividu().intValue())
          break;
        individuDG.selectNext();
      }
      if(individuDG.selectedObjects().count() > 0)
        setIndividuSelecteds(individuDG.selectedObjects());   
    }
    return null;
  }
  
  /*== bus de donnees ==*/
  
  public DTIndividuBus iBus() {
    return dtSession().dataCenter().individuBus();
  }
  
  public DTServiceBus sBus() {
    return dtSession().dataCenter().serviceBus();
  }
}