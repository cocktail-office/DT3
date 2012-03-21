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
 * Gestion des couleurs et ordonencements de toutes les listes
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

public class SwapListes extends PreferencesSwapView {

  // le delai de rafraichissement
	public NSArray secondeList;
  public Integer secondeItem;
  public Integer secondeSelected;
  
  // le nombre de DT par page
  public NSArray nombreIntList;
  public Integer nombreIntItem;
  public Integer nombreIntSelected;
  
  // l'ordre de tri des interventions / traiements
  public final String ASCENDING   = DTPreferencesBus.ASCENDING;
  public final String DESCENDING = DTPreferencesBus.DESCENDING;
  public String ordTriInt; // intervention
  public String ordTriTra; // traitement
  
  // les constantes pour le timer
  private final static int TIMER_MAX_TIME = 120;
  private final static int TIMER_NO_TIMER = 99999999;
  public final String TIMER_LABEL_NO_TIMER = "jamais";
  
  public SwapListes(WOContext context) {
    super(context);
  }

  protected void initComponent() {
    // liste de tous les temps
    secondeList = new NSArray();
    for (int i = 10; i <= TIMER_MAX_TIME; i+=10)
      secondeList = secondeList.arrayByAddingObject(new Integer(i));
    if (dtUserInfo().timer() != null)
      secondeSelected = new Integer(dtUserInfo().timer().intValue());      
    else
      secondeSelected = (Integer) secondeList.lastObject();
    // ajout de la valeur 'jamais'
    secondeList = secondeList.arrayByAddingObject(new Integer(TIMER_NO_TIMER));
    
    // liste de tous les nombres de DT
    nombreIntList = new NSArray();
    for (int i = 10; i <= 30; i+=10)
      nombreIntList = nombreIntList.arrayByAddingObject(new Integer(i));
    if (dtUserInfo().nbIntPerPage() != null)
      nombreIntSelected = new Integer(dtUserInfo().nbIntPerPage().intValue());      
    else
      nombreIntSelected = (Integer) nombreIntList.objectAtIndex(0);
    
    // recuperer les preferences de tri
    if (dtSession().dtUserInfo().isSortIntAscending())
      ordTriInt = ASCENDING;
    else
      ordTriInt = DESCENDING;
    if (dtSession().dtUserInfo().isSortTraAscending())
      ordTriTra = ASCENDING;
    else
      ordTriTra = DESCENDING;
  }


  public void setOrdTriInt(String value) {
    if (value != null) {
      ordTriInt = value;
      pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null,
          null, null, null, null, null, ordTriInt, null, null, null, null, null, null, null, null, null, null, null);
      dtSession().doReloadPreferences();
    }
  }
  
  public void setOrdTriTra(String value) {
    if (value != null) {
      ordTriTra = value;
      pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null,
          null, null, null, null, null, null, ordTriTra, null, null, null, null, null, null, null, null, null, null);
      dtSession().doReloadPreferences();
    }
  }
  
  public void setSecondeSelected(Integer value) {
    if (value != null) {
      secondeSelected = value;
      pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null,
          null, null, null, null, secondeSelected, null, null, null, null, null, null, null, null, null, null, null, null);
      dtSession().doReloadPreferences();
    }
  }
  
  public void setNombreIntSelected(Integer value) {
    if (value != null) {
      nombreIntSelected = value;
      pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null,
          null, null, null, null, null, null, null, null, nombreIntSelected, null, null, null, null, null, null, null, null);
      dtSession().doReloadPreferences();
    }
  }

  /*== affichage ==*/
  
  /**
   * Liste deroulante des durees de refresh : on affiche 
   * la valeur des secondes, excepte pour la valeur qui
   * represente la desactivation.
   */
  public String displaySecondeItem() {
    String display = Integer.toString(secondeItem.intValue());
    if (secondeItem.intValue() == TIMER_NO_TIMER)
      display = TIMER_LABEL_NO_TIMER;
    return display;
  }
  
}