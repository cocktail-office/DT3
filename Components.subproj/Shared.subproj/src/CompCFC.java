
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * La page de saisie des informations CFC liees a une DTRepro.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class CompCFC extends DTWebPage {

  // le formulaire pour 1 CFC
  public String cfcEditeur;
  public String cfcAuteur;
  public String cfcTitre;
  public Number cfcPages;
  public Number cfcExemplaires;
  // mise a jour sur les libelles
  private String cfcEditeurPrev;
  private String cfcAuteurPrev;
  private String cfcTitrePrev;
 
  // Constantes indiquant le mode d'edition des informations
  private static final int EDIT_STATUS_NONE = 0;
  private static final int EDIT_STATUS_ADD = 1;
  private static final int EDIT_STATUS_UPDATE = 2;
  // Le mode d'utilisation en cours
  private int editStatus = EDIT_STATUS_NONE;

  // le controleur
  private CompCFCListener listener;
  
  // Les erreurs
  public boolean errorEditeur, errorAuteur, errorTitre, errorPages, errorExemplaires, errorDoublon;
  public String errorMessage;
  
  // la WORepetition de tous les CFC pour cette DT
  public NSKeyValueCoding cfcItem;
  public NSKeyValueCoding cfcItemSelected;
  

  
  public CompCFC(WOContext context) {
    super(context);
  }

  /**
   * Affichage de la page lorsqu'il n'y a aucun CFC, on se 
   * met en mode ajout immediatement.
   */
  public void appendToResponse(WOResponse response, WOContext context) {
  	if (isConsulting() && listener.getCfcList().count() == 0) {
  		doAdd();
  	}
  	super.appendToResponse(response, context);
  }
  
  
  public void setListener(CompCFCListener newListener) {
    listener = newListener;
    // on initialise la selection
    if (listener.getCfcList().count() > 0) {
      cfcItem = (NSKeyValueCoding) listener.getCfcList().objectAtIndex(0);
      selectCfc();
    } else {
      cfcItemSelected = new NSMutableDictionary();
    }
  }
  
  public CompCFCListener listener() {
    return listener;
  }
  
  private void clearErrors() {
    errorEditeur = errorAuteur = errorPages = errorExemplaires = errorTitre = errorDoublon = false;
    errorMessage = null;
  }
  
  public boolean hasErrors() {
    return (errorEditeur || errorAuteur || errorPages ||
        errorExemplaires || errorTitre || errorDoublon || hasGeneralError());
  }
  
  public boolean hasGeneralError() {
    return (errorMessage != null);
  }

  
  /**
   * Teste si actuellement le CFC est en cours de modification.
   */
  public boolean isEditing() {
    return (editStatus == EDIT_STATUS_UPDATE);
  }
  
  /**
   * Teste si actuellement le CFC est en cours d'ajout.
   */
  public boolean isAdding() {
    return (editStatus == EDIT_STATUS_ADD);
  }
  
  /**
   * 
   * @return
   */
  public boolean isConsulting() {
  	return editStatus == EDIT_STATUS_NONE;
  }
  
  /**
   * Teste si la liste des CFC est vide.
   */
  public boolean hasNoCfc() {
    return (listener.getCfcList().count() == 0);
  }
  
  public boolean hasSelectedCfc() {
    return (cfcItemSelected != null);
  }
  
  public boolean isListCfcSelected() {
    return cfcItem == cfcItemSelected;
  }
  
  public boolean canShowAddUpdateDelete() {
    return (!isEditing() && hasSelectedCfc());
  }
  
  /**
   * Ajouter une nouvelle declaration
   * @return
   */
  public WOComponent doAdd() {
  	if (listener().isFullPage()) {
  		// en page complete, on prerempli le formulaire
  		editStatus = EDIT_STATUS_ADD;
  		cfcEditeur = cfcAuteur = cfcTitre = StringCtrl.emptyString();
  		cfcExemplaires = new Integer(1);
  		cfcPages = new Integer(1);
  	} else {
  		// en mode composante, on ajoute simplement une ligne vierge
  		listener().addCfc("", "", "", new Integer(1), new Integer(1));
  	}
    return null;
  }
  
  public WOComponent doUpdate() {
    editStatus = EDIT_STATUS_UPDATE;
    return null;
  }
  
  public WOComponent doDelete() {
  	if (listener().isFullPage()) {
      listener().removeCfc(cfcEditeur, cfcAuteur, cfcTitre);
      // on essaye de selectionner le premier cfc
      if (listener().getCfcList().count() > 0) {
        cfcItemSelected = (NSKeyValueCoding) listener.getCfcList().objectAtIndex(0);
        refillInterface();
      }
  	} else {
  		listener().removeCfc(cfcItem);
  	}
    return null;
  }

  public WOComponent doEditOK() {
    clearErrors();
    verifieData();
    if (!hasErrors()) {
      saveData();
      fillNewCfcItemSelected();
      refillInterface();
      editStatus = EDIT_STATUS_NONE;
    }
    return null;
  }
  
  public WOComponent doEditCancel() {
    editStatus = EDIT_STATUS_NONE;
    clearErrors();
    refillInterface();
    return null;
  }
  
  public WOComponent selectCfc() {
    if (cfcItem != null) {
      cfcItemSelected = cfcItem;
      cfcEditeurPrev = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_EDITEUR);
      cfcAuteurPrev = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_AUTEUR);
      cfcTitrePrev = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_TITRE);
    } else {
      cfcItemSelected = null;
    }
    editStatus = EDIT_STATUS_NONE;
    refillInterface();
    return null;
  }
  
  public WOComponent goBack() {
    if (listener != null)
      return listener.goBack();
    else
      return null;
  }
  
  /**
   * Construire le dico cfcItemSelected apres
   * l'ajout d'un nouveau CFC
   *
   */
  private void fillNewCfcItemSelected() {  
  	cfcItem = (NSKeyValueCoding) listener.getCfcList().lastObject();
  	selectCfc();
  }
  

  /**
   * Test la validite des donnees saisies. Affiche les message d'erreurs si
   * necessaire. Retourne <i>true</i> si les donnees sont saisies correctement.
   */
  public boolean verifieData() {
  	
  	boolean result = true;
  	
  	if (listener().isFullPage()) {
  		// en page complete, 1 seule ligne a verifier
      if (result && StringCtrl.isEmpty(cfcEditeur)) {
        errorEditeur = true;
        errorMessage = "L'editeur est obligatoire";
        result = false;
      }/*
      if (StringCtrl.isEmpty(cfcAuteur)) {
        errorAuteur = true;
        errorMessage = "L'auteur est obligatoire";
        return;
      }*/
      if (result && StringCtrl.isEmpty(cfcTitre)) {
        errorTitre = true;
        errorMessage = "Le titre est obligatoire";
        result = false;
      }
      if (result && cfcExemplaires == null) {
        errorExemplaires = true;
        errorMessage = "Le nombre d'exemplaires est obligatoire";
        result = false;
      }
      if (result && cfcPages == null) {
        errorPages = true;
        errorMessage = "Le nombre de pages est obligatoire";
        result = false;
      }
      // verifier qu'il n'y a pas de doublon
      if (result && listener().cfcExists(cfcEditeur, cfcAuteur, cfcTitre)) {
        errorDoublon = true;
        errorMessage = "Cette definition CFC est deja declarée !";
        result = false;
      }
  	} else {
  		// sinon verification 1 par 1
  		for (int i=0; i<listener().getCfcList().count(); i++) {
  			NSKeyValueCoding cfc = (NSKeyValueCoding) listener().getCfcList().objectAtIndex(i);
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
        listener().replaceCfcAtIndex(cfc, i);
  		}
  	}
    
    return true;
  }
  
  
  /**
   * 
   * @see #verifieData()
   */
  private void saveData() {
    if (editStatus == EDIT_STATUS_ADD) {
      listener().addCfc(cfcEditeur, cfcAuteur, cfcTitre, cfcPages, cfcExemplaires);
    } else {
      listener().updateCfc(
          cfcEditeurPrev, cfcAuteurPrev, cfcTitrePrev,
          cfcEditeur, cfcAuteur, cfcTitre, 
          cfcPages, cfcExemplaires
      );
    }
  }
  
  private void refillInterface() {
    if (cfcItemSelected != null) {
      cfcAuteur = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_AUTEUR);
      cfcEditeur  = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_EDITEUR);
      cfcTitre  = (String) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_TITRE);
      cfcExemplaires    = (Number) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_EXEMPLAIRE);
      cfcPages          = (Number) cfcItemSelected.valueForKey(CompCFCListener.KEY_CFC_PAGES);
    }
  }
  
  
  // affichage
  
  /**
   * On met en gras la declaration selectionnee
   */
  public String getStyleTrCfc() {
  	if (isListCfcSelected()) {
  		return "font-weight: bold";
  	} else {
  		return "font-weight: normal";
  	}
  }
}