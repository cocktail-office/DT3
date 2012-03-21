
/*
 * Copyright CRI - Universite de La Rochelle, 1995-2006 
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
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;


/**
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class CompCFCListener {

  // la liste de tous les CFC a rajouter
  private NSArray cfcList;
  
  //
  private DTInterventionBus interventionBus;
  
  /**
   * La page de retour en cas d'utilisation en mode "fullPage".
   * Mettre <em>null</em> si la composante s'affiche au sein d'une autre page
   */
  private WOComponent pageRetour;
  
  private CktlRecord recIntervention;
  

  // TODO a mettre dans une classe metier
  public final static String KEY_CFC_AUTEUR = "auteurs";
  public final static String KEY_CFC_AUTEUR_ERR = "auteursErr";
  public final static String KEY_CFC_EDITEUR = "editeurs";
  public final static String KEY_CFC_EDITEUR_ERR = "editeursErr";
  public final static String KEY_CFC_TITRE = "titre";
  public final static String KEY_CFC_TITRE_ERR = "titreErr";
  public final static String KEY_CFC_EXEMPLAIRE = "nbExemplaires";
  public final static String KEY_CFC_EXEMPLAIRE_ERR = "nbExemplairesErr";
  public final static String KEY_CFC_PAGES = "nbPages";
  public final static String KEY_CFC_PAGES_ERR = "nbPagesErr";
  
  /**
   * 
   * @param anInterventionBus
   * @param aPageRetour
   * @param anIntervention : l'enregistrement existant s'il s'agit d'une mise a jour. Mettre <em>null</em> si nouvelle DT.
   */
  public CompCFCListener(
  		DTInterventionBus anInterventionBus, WOComponent aPageRetour, CktlRecord aRecIntervention) {
    interventionBus = anInterventionBus;
    pageRetour = aPageRetour;
    recIntervention = aRecIntervention;
    if (isDatabase()) {
    	refreshCfcListFromDb();
    } else {
    	// on met 2 enregistrements vides
      cfcList = new NSArray();
      addCfc("", "", "", new Integer(1), new Integer(1));
      addCfc("", "", "", new Integer(1), new Integer(1));
    }
  }
  
  /**
   * Recharger la liste depuis la base de donnees.
   */
  private void refreshCfcListFromDb() {
  	cfcList = interventionBus.findInterventionsReproCfc(null, intOrdre());
  }
  
  private Number intOrdre() {
  	return recIntervention.numberForKey("intOrdre");
  }
  
  /**
   * Indique si l'on travaille sur des donn�es de la base de donn�es ou non
   * @return
   */
  private boolean isDatabase() {
  	return (recIntervention != null) ;
  }
  
  /**
   * Indique le mode d'utilisation du composant
   * @return
   */
  public boolean isFullPage() {
  	return pageRetour != null;
  }
  
  /**
   * Retourne la liste des enregistrements CFC associes.
   * Cette liste est : 
   * - soit un simple NSArray pour le cas d'une nouvelle DT
   * - soit une liste d'enregistrements dans le cas d'une modification
   * @return
   */
  public NSArray getCfcList() {
    return cfcList;
  }
  
  /**
   * Nouveau CFC
   */
  public void addCfc(String editeurs, String auteurs, 
      String titre, Number nbPages, Number nbExemplaires) {
    NSMutableDictionary dicoCfc = new NSMutableDictionary();
    
    dicoCfc.setObjectForKey(editeurs, KEY_CFC_EDITEUR);
    if (!StringCtrl.isEmpty(auteurs)) {
      dicoCfc.setObjectForKey(auteurs, KEY_CFC_AUTEUR);
    }
    dicoCfc.setObjectForKey(titre, KEY_CFC_TITRE);
    dicoCfc.setObjectForKey(nbPages, KEY_CFC_PAGES);
    dicoCfc.setObjectForKey(nbExemplaires, KEY_CFC_EXEMPLAIRE);
    
    cfcList = cfcList.arrayByAddingObject(dicoCfc);
    
    if (isDatabase()) {
    	interventionBus.addInterventionReproCFC(null, recIntervention.numberForKey("intOrdre"), editeurs, auteurs, titre, nbPages, nbExemplaires);
    	refreshCfcListFromDb();
    }
  }
  
  /**
   * Trouver l'index d'un CFC parmi la liste
   */
  private int findCfcIndex(String editeurs, String auteurs, String titre) {
  	for (int i = 0; i < cfcList.count(); i++) {
  		NSKeyValueCoding dico = (NSKeyValueCoding) cfcList.objectAtIndex(i);
   		String localEditeurs = (String)dico.valueForKey(KEY_CFC_EDITEUR);
   		String localAuteurs = (String)dico.valueForKey(KEY_CFC_AUTEUR);
   		String localTitre = (String)dico.valueForKey(KEY_CFC_TITRE);
   		if (localEditeurs.equals(editeurs) &&
   				localTitre.equals(titre)) {
   			if ((StringCtrl.isEmpty(localAuteurs) && StringCtrl.isEmpty(auteurs)) ||
   					(!StringCtrl.isEmpty(localAuteurs) && !StringCtrl.isEmpty(auteurs) && localAuteurs.equals(auteurs))) {
    			return i;
   			}
  		}
  	}
    return -1;
  }

  /**
   * Remplacer un cfc pour la remontee d'erreur
   */
  public void replaceCfcAtIndex(NSKeyValueCoding cfc, int index) {
		NSMutableArray newCfcList = new NSMutableArray(cfcList);
		newCfcList.replaceObjectAtIndex(cfc, index);
		cfcList = newCfcList.immutableClone();
  }
  
  /**
   * Suppression de CFC en mode page partielle
   * @param dico
   * @return
   */
  public boolean removeCfc(NSKeyValueCoding dico) {
		NSMutableArray newCfcList = new NSMutableArray(cfcList);
		boolean result = newCfcList.removeObject(dico);
		cfcList = newCfcList.immutableClone();
		return result;
  }
  
  /**
   * Suppression de CFC en mode full page
   */
  public boolean removeCfc(String editeurs, String auteurs, String titre) {
  	if (isDatabase()) {
  		CktlRecord recCfc = interventionBus.findInterventionReproCfc(null, intOrdre(), editeurs, auteurs, titre);
  		boolean result = interventionBus.deleteInterventionReproCFC(null, recCfc) == 1;
  		if (result) {
  			refreshCfcListFromDb();
  		}
  		return result;
  	} else {
  		int index = findCfcIndex(editeurs, auteurs, titre);
  		if (index != -1) {
  			NSMutableArray newCfcList = new NSMutableArray(cfcList);
  			newCfcList.removeObjectAtIndex(index);
  			cfcList = newCfcList.immutableClone();
  			return true;
  		}
      return false;
  	}
  }
  
  /**
   * Mise a jour de CFC
   */
  public boolean updateCfc(String editeursPrev, String auteursPrev, String titrePrev,
  		String editeurs, String auteurs, String titre, 
      Number nbPages, Number nbExemplaires) {
  	if (isDatabase()) {
  		boolean result = interventionBus.updateInterventionRefroCfc(
  				null, intOrdre(), editeursPrev, auteursPrev, titrePrev, editeurs, auteurs, titre, nbPages, nbExemplaires);
  		if (result) {
  			refreshCfcListFromDb();
  		}
  		return result;
  	} else {
  		int index = findCfcIndex(editeursPrev, auteursPrev, titrePrev);
  		if (index != -1) {
  			NSMutableDictionary dicoCfc = new NSMutableDictionary();
  			
  			dicoCfc.setObjectForKey(editeurs, KEY_CFC_EDITEUR);
  	    if (!StringCtrl.isEmpty(auteurs)) {
  	      dicoCfc.setObjectForKey(auteurs, KEY_CFC_AUTEUR);
  	    }
	      dicoCfc.setObjectForKey(titre, KEY_CFC_TITRE);
	      dicoCfc.setObjectForKey(nbPages, KEY_CFC_PAGES);
	      dicoCfc.setObjectForKey(nbExemplaires, KEY_CFC_EXEMPLAIRE);
	      
	      NSMutableArray newCfcList = new NSMutableArray(cfcList);
	      newCfcList.replaceObjectAtIndex(dicoCfc, index);
	      cfcList = newCfcList.immutableClone();
	      return true;
	    }
      return false;
  	}
  }
  
  /**
   * Determiner si une definition CFC est deja presente parmi
   * la liste des CFC deja enregistrees.
   */
  public boolean cfcExists(String editeurs, String auteurs, String titre) {
    return findCfcIndex(editeurs, auteurs, titre) != -1;
  }
  
  /**
   * Verification des donnes en mode page partielle
   * @return <em>true</em> si aucune erreur n'est survenue
   */
  public boolean verifieData() {
  	boolean result = true;
		for (int i=0; i<getCfcList().count(); i++) {
			NSKeyValueCoding cfc = (NSKeyValueCoding) getCfcList().objectAtIndex(i);
			String localCfcEditeur  = (String) cfc.valueForKey(CompCFCListener.KEY_CFC_EDITEUR);
			String localCfcTitre  = (String) cfc.valueForKey(CompCFCListener.KEY_CFC_TITRE);
			Number localCfcExemplaires    = (Number) cfc.valueForKey(CompCFCListener.KEY_CFC_EXEMPLAIRE);
			Number localCfcPages          = (Number) cfc.valueForKey(CompCFCListener.KEY_CFC_PAGES);
			
			cfc.takeValueForKey(Boolean.FALSE, CompCFCListener.KEY_CFC_EDITEUR_ERR);
	    if (StringCtrl.isEmpty(localCfcEditeur)) {
				cfc.takeValueForKey(Boolean.TRUE, CompCFCListener.KEY_CFC_EDITEUR_ERR);
	      result = false;
	    }
	  	cfc.takeValueForKey(Boolean.FALSE, CompCFCListener.KEY_CFC_TITRE_ERR);
	    	if (StringCtrl.isEmpty(localCfcTitre)) {
				cfc.takeValueForKey(Boolean.TRUE, CompCFCListener.KEY_CFC_TITRE_ERR);
	      result = false;
	    }
	    cfc.takeValueForKey(Boolean.FALSE, CompCFCListener.KEY_CFC_EXEMPLAIRE_ERR);
	    if (localCfcExemplaires == null) {
				cfc.takeValueForKey(Boolean.TRUE, CompCFCListener.KEY_CFC_EXEMPLAIRE_ERR);
	      result = false;
	    }
	    cfc.takeValueForKey(Boolean.FALSE, CompCFCListener.KEY_CFC_PAGES_ERR);
	    if (localCfcPages == null) {
				cfc.takeValueForKey(Boolean.TRUE, CompCFCListener.KEY_CFC_PAGES_ERR);
	      result = false;
	    }
			// on remet dans la liste pour transmettre les messages d'erreur
	    replaceCfcAtIndex(cfc, i);
		}
		return result;
  }
  
  /**
   * Enregistrer les informations CFC dans la base de donnees.
   * Cette operation n'est possible que lorsque le record
   * Intervention est deja present.
   */
  public boolean saveData(Integer transId, Number intOrdre) {
    boolean ok = true;
    if (!isDatabase()) {
      for (int i = 0; i < cfcList.count(); i++) {
        NSMutableDictionary dico = (NSMutableDictionary) cfcList.objectAtIndex(i);
        ok = ok && interventionBus.addInterventionReproCFC(transId, 
        		intOrdre, 
        		(String)dico.objectForKey(KEY_CFC_EDITEUR),
        		(String)dico.objectForKey(KEY_CFC_AUTEUR),
        		(String)dico.objectForKey(KEY_CFC_TITRE),
        		(Number)dico.objectForKey(KEY_CFC_PAGES),
        		(Number)dico.objectForKey(KEY_CFC_EXEMPLAIRE));
      }
    }
    return ok;
  }
  
  public WOComponent goBack() {
    return pageRetour;
  }


}
