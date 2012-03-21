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
import org.cocktail.fwkcktlwebapp.server.components.CktlLogin;
import org.cocktail.fwkcktlwebapp.server.components.CktlLoginResponder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * La classe implementant une directe action qui permet d'acceder a la
 * page de la validation des demandes.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DAValidation extends DirectAction {

  public DAValidation(WORequest request) {
    super(request);
  }

  public WOActionResults defaultAction() {
    setLoginComment("Validation");
    if (useCasService())
      return pageForURL(getLoginActionURL(context(), true, null, false, null));
    else
      return loginNoCasPage(null);
  }

  public WOActionResults loginCasSuccessPage(String netid, NSDictionary actionParams) {
    CktlLog.trace(null);
    dtSession().setMode(Session.MODE_DA_VALIDER);
    String errorMsg = dtSession().setConnectedUser(netid);
    if (errorMsg == null) {
      CktlLog.log("login : "+netid+", type : validation - OK");
      return getConsultationPage(dtSession());
    }
    return loginCasFailurePage(errorMsg, null);
  }
  
  public static PageConsultation getConsultationPage(Session session) {
    PageConsultation page = (
        PageConsultation)session.getSavedPageWithName(PageConsultation.class.getName());
    session.setPageContenu(page);
    StringBuffer condition = new StringBuffer();
    // forcer la recherche des droits
    DTUserInfo ui = session.dtUserInfo();
    NSArray listeDtServiceCode = ui.listeDtServiceCode();
    if (listeDtServiceCode.count() > 0) {
      for (int i = 0; i < listeDtServiceCode.count(); i++) {
        if (condition.length() > 0) {
          condition.append(" or ");
        }
        String serviceCode = (String) listeDtServiceCode.objectAtIndex(i);
        // l'admin voit toute les non validees de son service
        if (session.dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN, serviceCode)) {
          condition.append("(intEtat='V'");
        } else {
          // sinon que celle ou il est responsable d'activites
          condition.append("(intEtat='V' and toActivites.tosActResponsables.noIndividu=");
          condition.append(session.dtUserInfo().noIndividu());
          condition.append(" and toActivites.tosActResponsables.actTypeResponsable='F'");
        }
        condition.append(" and cStructure='").append(serviceCode).append("')");
      }
    } else {
      // sinon que celle ou il est responsable d'activites
      condition.append("intEtat='V' and toActivites.tosActResponsables.noIndividu=");
      condition.append(session.dtUserInfo().noIndividu());
      condition.append(" and toActivites.tosActResponsables.actTypeResponsable='F'");
    }
    CktlLog.log("condition:"+condition);
    page.setQualifierExternal(EOQualifier.qualifierWithQualifierFormat(condition.toString(), null));
    page.listener.doFetchDisplayGroup(); 
    return page;
  }

  public CktlLoginResponder getNewLoginResponder(NSDictionary actionParams) {
    return new DAValidtionLoginResponder(actionParams);
  }
  
  public class DAValidtionLoginResponder extends DirectAction.DefaultLoginResponder {
    
    public DAValidtionLoginResponder(NSDictionary actionParams) {
      super(actionParams);
    }

    public WOComponent loginAccepted(CktlLogin loginComponent) {
      Session session = (Session)loginComponent.session();
      CktlLog.trace("session ID : "+session.sessionID());
      session.setMode(Session.MODE_DA_VALIDER);
      session.setConnectedUserInfo(loginComponent.loggedUserInfo());
      return getConsultationPage(session);
    }
  }
}
