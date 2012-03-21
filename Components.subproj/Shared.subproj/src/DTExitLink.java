

import java.io.UnsupportedEncodingException;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.URLEncoder;
import org.cocktail.fwkcktlwebapp.server.CktlWebAction;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORedirect;

/**
 * Gere le composant avec le lien de deconnexion de l'application.
 * Ce composant propose un lien permettant de fermer la session de travail
 * avec l'application et de revenir a sa page d'acceuil.
 * 
 * <p>Le comportement de la balise peut etre configure via les connecteurs
 * de son API. Voir la definition des constantes privees pour les balises.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTExitLink extends DTWebComponent {
  // Les nom des connectuers dans la definition de l'API du composant
  private static String BND_TARGET = "target";
  private static String BND_LINK_URL ="linkURL";
  private static String BND_FORCE_SSO_LOGOUT = "forceSSOLogout";
  private static String BND_LINK_TEXT = "linkText";
  private static String BND_LINK_TOOL_TIP = "linkToolTip";
  private static String BND_LINK_STYLE_CLASS = "linkStyleClass";
  
  /**
   * Cree une nouvelle instance de composant.
   */
  public DTExitLink(WOContext context) {
    super(context);
  }

  /**
   * Retourne <em>false</em> (aucune synchronisation des valeurs, elles seront
   * recuperees "manuellement").  
   */
  public boolean synchronizesVariablesWithBindings() {
    return false;
  }
  
  /**
   * Retourne la destination de lien de la deconnexion de l'application.
   * Elle peut avoir les valeurs comme "_parent", "_top", etc... Retourne la
   * chaine vide si aucune valeur n'est donnee.
   * 
   * <p>C'est la valeur de la balise <code>target</code>.</p>
   */
  public String target() {
    if (hasBinding(BND_TARGET))
      return (String)valueForBinding(BND_TARGET);
    else
      return StringCtrl.emptyString();
  }
  
  /**
   * Retourne le lien ou le navigateur doit etre redirige apres la deconnexion.
   * Si valeur n'est pas donnee, on le redirige vers la page d'acceuil de
   * l'application.
   * 
   * <p>C'est la valeur de la balise <em>linkURL</em>.</p>
   */
  public String linkURL() {
    String link = null;
    if (hasBinding(BND_LINK_URL))
      link = (String)valueForBinding(BND_LINK_URL);
    if (StringCtrl.normalize(link).length() == 0)
      link = dtApp().getApplicationURL(context());
    return link;
  }
  
  /**
   * Indique si le deconnexion SSO/CAS doit etre effectuee. Si c'est le cas
   * l'utilisateur sera oblige de se re-authentifie lors de sont prochaine
   * connexion a l'application.
   * 
   * <p>C'est la valeur de la balise <em>forceSSOLogout</em>.</p>
   */
  public boolean forceSSOLogout() {
    if (hasBinding(BND_FORCE_SSO_LOGOUT))
      return ((Boolean)valueForBinding(BND_FORCE_SSO_LOGOUT)).booleanValue();
    else
      return false;
  }
  
  /**
   * Retourne le text a afficher dans le lien de la deconnexion. Si aucune
   * valeur n'est donnee, retourne "Quitter".
   * 
   * <p>C'est la valeur de la balise <em>linkText</em>.</p>
   */
  public String linkText() {
    if (hasBinding(BND_LINK_TEXT))
      return (String)valueForBinding(BND_LINK_TEXT);
    else
      return "Quitter";
  }
  
  /**
   * Retourne le texte qui sera affiche lorsque le courseur de la souris passe
   * au dessus de lien de la deconnexion. Aucune valeur peux ne pas etre
   * indiquee.
   * 
   * <p>C'est la valeur de la balise <em>linkToolTip</em>.</p>
   */
  public String linkToolTip() {
    return StringCtrl.normalize((String)valueForBinding(BND_LINK_TOOL_TIP)); 
  }
  
  /**
   * Retourne le nom de la definition de la classe CSS a utiliser pour definir
   * la mise en page de lien de la deconnexion. Si aucune valeur n'est donnee
   * alors retourne "linkPage".
   * 
   * <p>C'est la valeur de la balise <em>linkStyleClass</em>.</p>
   */
  public String linkStyleClass() {
    String style = null;
    if (hasBinding(BND_LINK_STYLE_CLASS))
      style = (String)valueForBinding(BND_LINK_STYLE_CLASS);
    if (StringCtrl.normalize(style).length() == 0)
      style = "linkPage";
    return style;
  }
  
  /**
   * Execute l'actionne de la deconnexion suite au click sur le lien de la
   * deconnexion.
   */
  public WOComponent doExit() {
    String url = linkURL();
    if (forceSSOLogout() && CktlWebAction.useCasService()) {
      // ENcode URL
      if (url.length() > 0) {
        StringBuffer u = new StringBuffer(CktlWebAction.casLogoutURL());
        try {
          u.append("?service=").append(URLEncoder.encode(url, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
          ex.printStackTrace();
        }
        url = u.toString();
      }
    }
    if (context().hasSession())
      context().session().terminate();
    CktlLog.trace("logout.url : "+url);
    return pageForURL(url);
  }
  
  /**
   * Retourne le composant de redirection vers l'URL donne. Retourne
   * <i>null</i> si <code>url</code> n'est pas defini.
   */
  protected WOComponent pageForURL(String url) {
    if (url == null) return null;
    WORedirect page = (WORedirect)cktlApp.pageWithName("WORedirect", context());
    page.setUrl(url);
    return page;
  }
}