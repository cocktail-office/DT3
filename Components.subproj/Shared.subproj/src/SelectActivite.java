

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * Gestionnaire de selection d'une activite pour une DT
 * pour un service specifique
 *
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class SelectActivite extends DTWebPage {
  /** le pilote (listener */
  public SelectActiviteListener listener;
  
  public SelectActivite(WOContext context) {
    super(context);
  }
 
  /**
   * Le formulaire existe deja s'il est declaree dans le listener
   */ 
  public boolean formExists() {
    return !StringCtrl.isEmpty(listener.formName());
  }
  
  /**
   * Le nom du form, deduit de l'existence ou non de celui du listener.
   */
  public String formName() {
    return (formExists() ? listener.formName() : "FormActivites");
  }

  /**
   * Si pas de form, alors on affiche le composant en pleine page :
   * - inserer dans une DTDefaultPage
   * - creer le form conteneur
   */
  public boolean showFullPage() {
    return !formExists();
  }

  /**
   * Methode neutre, utilise par default dans le formulaire
   */
  public WOComponent doNothing() {
    return null;
  }
  
  // -- DISPLAY --

  /**
   * L'affichage d'une activite trouvee : le path
   * dans le l'arborescence
   */
  public String displayActiviteFoundItem() {
    return activiteBus().findActivitePathString(listener.activiteFoundItem.actOrdre(), true, ";");
  }

  // GETTER
  
  /**
   * Le nombre de lignes a afficher dans le CktlHXBrowser pour le service
   */
  public int getNbLigBrowserAct() {
  	return listener.getNbLigBrowserAct();
  }
  
  // SETTER
  
  protected void setListener(SelectActiviteListener value) {
    listener = value;
  }
  
  // boutons
  
  /**
   * L'action de selectionner l'activite. On passe les paramatres
   * aux pages appelante et on retourne si tout s'est bien passe
   */
  public WOComponent selectActivite() {
    if (!isDisabledBtnSelectActivite() && listener.activiteSelectedPath.count() > 0 && listener.browserRecord() != null) {
      listener.caller().takeValueForKeyPath(listener.browserRecord(), "activiteSelectedBySelectActivite");
      listener.caller().takeValueForKeyPath(activiteBus().findActivitePathString(
          listener.getActiviteSelectedItem().actOrdre(), true, ";"), "motsClesSelectedBySelectActivite");
      listener.caller().valueForKeyPath("saveUpdateActiviteBySelectActivite");
      return nextPage();
    } else
      return null;
  }
  
  /**
   * ne pas choisir d'activite
   * @return
   */
  public WOComponent cancel() {
    return nextPage();
  }
  
  /**
   * La page de retour, on utilise en priorite <code>alternateCaller()</code>
   * si celle ci est renseignee.
   * @return
   */
  private WOComponent nextPage() {
  	if (listener.alternateCaller() == null) {
  		return listener.caller();
  	} else { 
  		return listener.alternateCaller();
  	}
  }
  
  /**
   * La selection d'activite n'est autorisee que sur une feuille
   * sauf si c'est explicitement dit qu'on peut faire la selection
   * malgr� tout, dans ce cas, on selectionne n'importe quoi
   */
  public boolean isDisabledBtnSelectActivite() {
 		if (listener.getActiviteSelectedItem() == null) 
 			return true;
 		if (listener.shouldSelectedOnlyLeaf()) {
  		return !listener.getActiviteSelectedItem().isLeaf();
  	}
 		return false;
  }

  
  // racourcis vers les bus
  
  private DTActiviteBus activiteBus() {
  	return dtSession().dataCenter().activiteBus();
  }
}