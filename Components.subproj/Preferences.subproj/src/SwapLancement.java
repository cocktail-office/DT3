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
 * Positionements par defaut dans l'application
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */


import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

public class SwapLancement extends PreferencesSwapView {
  // onglet par defaut
  public final int NUMERO_ONGLET_CREATION       = DTPreferencesBus.NUMERO_ONGLET_CREATION;
  public final int NUMERO_ONGLET_CONSULTATION   = DTPreferencesBus.NUMERO_ONGLET_CONSULTATION;
  public int numeroOnglet;
 
  // liste de tous les etats possibles
  public NSArray etatsLibelles;
  public String etat;
  public String etatSelectedLibelle;
  
  public SwapLancement(WOContext context) {
    super(context);
  }
  
  protected void initComponent() {
    if (etatsLibelles == null)
      etatsLibelles = dtDataCenter().etatBus().findLibellesAll();
    
    // onglet de preference
    if (dtUserInfo().prefOnglet() != null) {
      if (dtUserInfo().prefOnglet().intValue() == DTPreferencesBus.NUMERO_ONGLET_CREATION)
        numeroOnglet = NUMERO_ONGLET_CREATION;
      else
        numeroOnglet = NUMERO_ONGLET_CONSULTATION;
    } else
      numeroOnglet = NUMERO_ONGLET_CREATION;
    
    // etat de preference
    if (etatsLibelles.count() > 1) {
      etatSelectedLibelle = dtDataCenter().etatBus().
        libelleForEtat(dtUserInfo().prefEtatCode());
      if (etatSelectedLibelle == null)       
        etatSelectedLibelle = (String)etatsLibelles.objectAtIndex(1);
    }
    else
      etatSelectedLibelle = (String)etatsLibelles.objectAtIndex(0);
  }

  // surcharge des setters pour sauvegarde directe dans la base
  
  public void setNumeroOnglet(int value) {
    numeroOnglet = value;
    dtSession().dataCenter().preferencesBus().updatePrefAppli(
        null, dtUserInfo().noIndividu(), null, null, null, 
        new Integer(numeroOnglet),
        null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    loadNewPreferences();
  }

  public void setEtatSelectedLibelle(String value) {
    if (value != null) {
      etatSelectedLibelle = value;
      dtSession().dataCenter().preferencesBus().updatePrefAppli(
          null, dtUserInfo().noIndividu(), null, 
          dtSession().dataCenter().etatBus().etatForLibelle(etatSelectedLibelle), 
          null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
      loadNewPreferences();
    }
  }
  

}