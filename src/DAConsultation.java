import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.server.components.CktlLogin;
import org.cocktail.fwkcktlwebapp.server.components.CktlLoginResponder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSDictionary;

/**
 * Implemente les actions permettant d'acceder directement au detail d'une
 * demande de travaux.
 * 
 * @author ctarade
 */
public class DAConsultation extends DirectAction {
	public final static String INT_ORDRE_KEY = "intOrdre";
	private NSDictionary actionParams;

	public DAConsultation(WORequest request) {
		super(request);
	}

	protected WOActionResults consultation(Integer intOrdre) {
		setLoginComment("Consultation : " + intOrdre);
		actionParams = new NSDictionary(new Integer(intOrdre), INT_ORDRE_KEY);
		if (useCasService())
			return pageForURL(getLoginActionURL(context(), true, null, false, actionParams));
		else
			return loginNoCasPage(actionParams);
	}

	public WOActionResults defaultAction() {
		String strIntOrdre = request().stringFormValueForKey(INT_ORDRE_KEY);
		int intOrdre = intOrdre = Integer.parseInt(strIntOrdre);
		return consultation(intOrdre);
	}

	/*
	 * @see DirectAction#casLoginLink()
	 */
	public String casLoginLink() {
		return getLoginActionURL(context(), true, null, false, actionParams);
	}

	public WOActionResults loginCasSuccessPage(String netid, NSDictionary actionParams) {
		CktlLog.trace(null);
		String errorMsg = dtSession().setConnectedUser(netid);
		if (errorMsg == null) {
			CktlLog.log("login : " + netid + ", type : consultation - OK");
			return getConsultationPage(dtSession(), actionParams);
		}
		return loginCasFailurePage(errorMsg, null);
	}

	private WOComponent getConsultationPage(Session session, NSDictionary actionParams) {
		String strIntOrdre = actionParams.valueForKey(INT_ORDRE_KEY).toString();
		int intOrdre = Integer.parseInt(strIntOrdre);
		session.selectConsult(true, intOrdre);
		return session.pageContenu();
	}

	public CktlLoginResponder getNewLoginResponder(NSDictionary actionParams) {
		return new DAConsultationLoginResponder(actionParams);
	}

	public class DAConsultationLoginResponder extends DirectAction.DefaultLoginResponder {

		public DAConsultationLoginResponder(NSDictionary actionParams) {
			super(actionParams);
		}

		public WOComponent loginAccepted(CktlLogin loginComponent) {
			Session session = (Session) loginComponent.session();
			session.setConnectedUserInfo(loginComponent.loggedUserInfo());
			return getConsultationPage(session, actionParams);
		}
	}
}
