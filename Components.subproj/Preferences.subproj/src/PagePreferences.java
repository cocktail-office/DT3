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

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * Gestion des preferences utilisateurs de l'application DT
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class PagePreferences extends WOComponent {

  // gestion de la selection des onglets
  private int selectedOnglet = ONGLET_PREF_NONE;
  public final static int ONGLET_PREF_NONE            = -1;
  public final static int ONGLET_PREF_LANCEMENT       = 0;
  public final static int ONGLET_PREF_LISTES          = 1;
  public final static int ONGLET_PREF_INTERVENANTS    = 2;
  public final static int ONGLET_PREF_COULEURS        = 3;
  public final static int ONGLET_PREF_DIVERS          = 4;

  public final static String ONGLET_PREF_TITLE_LANCEMENT        = "Lancement de l'application";
  public final static String ONGLET_PREF_TITLE_LISTES           = "Liste des demandes et traitements";
  public final static String ONGLET_PREF_TITLE_INTERVENANTS     = "Services et intervenants";
  public final static String ONGLET_PREF_TITLE_COULEURS         = "Couleurs";
  public final static String ONGLET_PREF_TITLE_DIVERS           = "Autres param&egrave;tres";
  
  public PagePreferences(WOContext context) {
    super(context);
  }
  
  public void setSelectedOnglet(int id) {
    selectedOnglet = id;
  }
  
  public int getSelectedOnglet() {
    return selectedOnglet;
  }
      
  // quel swap afficher
  public boolean showSwapLancement()        {   return selectedOnglet == ONGLET_PREF_LANCEMENT;}
  public boolean showSwapListes()           {   return selectedOnglet == ONGLET_PREF_LISTES;}
  public boolean showSwapIntervenants()     {   return selectedOnglet == ONGLET_PREF_INTERVENANTS;}
  public boolean showSwapCouleurs()         {   return selectedOnglet == ONGLET_PREF_COULEURS;}
  public boolean showSwapDivers()           {   return selectedOnglet == ONGLET_PREF_DIVERS;}
  
  // titre du swap en cours
  public String swapTitle() {
    String title = "";
    if (showSwapLancement()) {
      title = ONGLET_PREF_TITLE_LANCEMENT;
    } else if (showSwapListes()) {
      title = ONGLET_PREF_TITLE_LISTES;
    } else if (showSwapIntervenants()) {
      title = ONGLET_PREF_TITLE_INTERVENANTS;
    }else if (showSwapCouleurs()) {
      title = ONGLET_PREF_TITLE_COULEURS;
    } else if (showSwapDivers()) {
      title = ONGLET_PREF_TITLE_DIVERS;
    }
    return title;
  }
  
}