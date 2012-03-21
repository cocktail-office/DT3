
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
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebPage;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

/**
 * La page par defaut pour l'application DT Web. Ce composant herite et
 * redefinit le CktlDefaultPage en y ajoutant des options supplementaires.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDefaultPage extends CktlWebPage {  
 
	/**
   * Cree une nouvelle instance du composant.
   */
  public DTDefaultPage(WOContext context) {
    super(context);
  }
  
  public void appendToResponse(WOResponse arg0, WOContext arg1) {
    super.appendToResponse(arg0, arg1);
    addLocalCss(arg0, "css/CktlCommon.css", "FwkCktlThemes");
    //addLocalCss(arg0, "css/CktlCommonCouleur.css", "FwkCktlThemes");
    addLocalCss(arg0, "css/dtstyle.css");
  }

  /**
   * Retourne la reference vers la session en cours d'execution. C'est une
   * instance de la classe <code>Session</code>.
   * 
   * @see Session
   */
  public Session dtSession() {
    return (Session)session();
  }
  
  /**
   * Retourne la reference vers l'application en cours d'execution. C'est une
   * instance de la classe <code>Application</code>.
   * 
   * @see Application 
   */
  public Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  /**
   * Retourne <em>false</em> pour indiquer que la lecture/ecriture des valeurs
   * des balises ne doit pas etre faite automatiquement.
   */
  public boolean synchronizesVariablesWithBindings() {
    return false;
  }
  
  /**
   * Retourne le titre afficher dans l'entete de la page HTML generee.
   */
  public String dtPageTitle() {
    return "Demande de Travaux : "+dtSession().modeEnCoursNom();
  }
  
  /**
   * 
   */
  private final static String BND_SHOULD_SHOW_MENU = "shouldShowMenu";
  
  public Boolean shouldShowMenu() {
    if (hasBinding(BND_SHOULD_SHOW_MENU))
      return (Boolean) valueForBinding(BND_SHOULD_SHOW_MENU);
    return Boolean.FALSE;
  }
  
  /**
   * 
   */
  private final static String BND_SHOULD_SHOW_TITLE = "shouldShowTitle";
  
  public Boolean shouldShowTitle() {
    if (hasBinding(BND_SHOULD_SHOW_TITLE))
      return (Boolean) valueForBinding(BND_SHOULD_SHOW_TITLE);
    return Boolean.TRUE;
  }
  
  /**
   * On affiche le menu que pour les WOComponent nomm�s Page* (page completes)
   * et en mode non directAction.
   */
  public boolean showMenu() {
    return shouldShowMenu() == Boolean.TRUE &&
    	(dtSession().getMode() != Session.MODE_DA_CREAT && dtSession().getMode() != Session.MODE_DA_VALIDER && dtSession().getMode() != Session.MODE_DA_FAST);
  }
  
  private final static String APP_WILL_STOP_MESSAGE_TEMPLATE = 
  	"<b>Attention, DT va &ecirc;tre arr&ecirc;t&eacute;e dans %TTL%.</b><br/><br/>" +
  	"Merci de bien vouloir terminer votre saisie et fermer votre session sur l'application.";

  /**
   * Lors du lancement de la procedure d'arret de l'application, on affiche
   * un message a l'utilisateur.
   */
  public boolean shouldShowLayerMessageStop() {
  	boolean shouldShow = dtApp().appWillStop() && !dtSession().userWasWarned();
  	if (shouldShow) {
  		// notifier que le message est passe
  		dtSession().notifyWarnIsDone();
  	}
  	return shouldShow;
  }
  
  /**
   * Le message affiche a l'utilisateur pour l'informer de l'arret
   */
  public String strAppWillStopMessage() {
  	return StringCtrl.replace(APP_WILL_STOP_MESSAGE_TEMPLATE, "%TTL%", dtApp().appTtl());
  }
}