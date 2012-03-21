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
import org.cocktail.fwkcktlwebapp.server.CktlWebAction;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlLogin;
import org.cocktail.fwkcktlwebapp.server.components.CktlLoginResponder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSDictionary;
/**
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DirectAction extends CktlWebAction {
  private String loginComment;
  
  public DirectAction(WORequest aRequest) {
    super(aRequest);
  }

  public Session dtSession() {
    return (Session)session();
  }
  
  public Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  /**
   * Excecute l'action par defaut de l'application DT. Elle affiche la page
   * de connexion � l'application.
   */
  public WOActionResults defaultAction() {
    if (useCasService())
      return loginCASPage();
    else
      return loginNoCasPage(null);
  }
  
  /**
   * Retourne la page avec la description des services et directActions
   * de l'application.
   */
  public WOActionResults servicesAction() {
    return pageWithName("Services");
  }
  
  /**
   * 
   */
  public WOActionResults loginCasSuccessPage(String netid, NSDictionary actionParams) {
    CktlLog.trace(null);
    String errorMsg = dtSession().setConnectedUser(netid);
    if (errorMsg == null) {
      CktlLog.log("login : "+netid+", type : accueil - OK");
      if (dtSession().dtUserInfo().prefOnglet() != null &&
          dtSession().dtUserInfo().prefOnglet().intValue() == DTPreferencesBus.NUMERO_ONGLET_CONSULTATION)
        return dtSession().selectConsult(true, -1);
      else 
        return dtSession().selectCreat();

    }
    return loginCasFailurePage(errorMsg, null);
  }

  public WOActionResults loginCasFailurePage(String errorMessage, String errorCode) {
    StringBuffer msg = new StringBuffer();
    msg.append("Une erreur s'est produite lors de l'authentification de l'utilisateur");
    if (errorMessage != null)
      msg.append("&nbsp;:<br><br>").append(errorMessage);
    CktlLog.log(" user login - Erreur : ", errorMessage);
    return getErrorPage(msg.toString());
  }
  
  public WOActionResults loginNoCasPage(NSDictionary actionParams) {
    LoginLocal page = (LoginLocal)pageWithName("LoginLocal");
    page.setTitleComment(loginComment());
    page.registerLoginResponder(getNewLoginResponder(actionParams));
    return page;
  }

  public WOActionResults loginCASPage() {
    LoginCAS page = (LoginCAS)pageWithName("LoginCAS");
    page.setTitleComment(loginComment());
    page.setCASLoginLink(casLoginLink());
    return page;
  }

  /**
  *
   */
  private WOComponent getErrorPage(String errorMessage) {
    CktlAlertPage page = (CktlAlertPage)cktlApp.pageWithName("CktlAlertPage", context());
    page.showMessage(null, cktlApp.name()+" : ERREUR", errorMessage,
                     null, null, null, CktlAlertPage.ERROR, null);
    return page;
  }

  public String loginComment() {
    return loginComment; 
  }

  public void setLoginComment(String comment) {
    loginComment = comment;
  }
  
  public String casLoginLink() {
    return null;
  }
  
  public CktlLoginResponder getNewLoginResponder(NSDictionary actionParams) {
    return new DefaultLoginResponder(actionParams);
  }
    
  public class DefaultLoginResponder implements CktlLoginResponder {
    private NSDictionary actionParams;
    
    public DefaultLoginResponder(NSDictionary actionParams) {
      this.actionParams = actionParams;
    }
    
    public NSDictionary actionParams() {
      return actionParams;
    }
    
    public WOComponent loginAccepted(CktlLogin loginComponent) {
      Session session = (Session) loginComponent.session();
      CktlLog.trace("session ID : "+session.sessionID());
      CktlLog.trace(""+loginComponent.loggedUserInfo());
      session.setConnectedUserInfo(loginComponent.loggedUserInfo());
//      session.setMode(Session.MODE_CONSULT);
      CktlLog.log("user login : "+loginComponent.loggedUserInfo().login()+" - OK");
      if (session.dtUserInfo().prefOnglet() != null &&
          session.dtUserInfo().prefOnglet().intValue() == DTPreferencesBus.NUMERO_ONGLET_CONSULTATION)
        return session.selectConsult(true, -1);
      else 
        return session.selectCreat();
    }

    public boolean acceptLoginName(String loginName) {
      return cktlApp.acceptLoginName(loginName);
    }

    public boolean acceptEmptyPassword() {
      return cktlApp.config().booleanForKey("ACCEPT_EMPTY_PASSWORD");
    }
    
    public String getRootPassword() {
      return cktlApp.getRootPassword();
    }
  }
}