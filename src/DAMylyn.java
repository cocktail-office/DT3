import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.CktlLog;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.planning._imports.StringCtrl;

/**
 * Point d'entrée pour les accès depuis le plugin eclipse mylyn
 * 
 * @author ctarade
 */
public class DAMylyn extends DAConsultation {

	private final static String MYLYN_PASSWORD_CONFIG_KEY = "MYLYN_PASSWORD";

	private final static String LOGIN_REQUEST_KEY = "login";
	private final static String PASSWORD_MYLYN_REQUEST_KEY = "passwordMylyn";
	private final static String ACT_ORDRE_REQUEST_KEY = "actOrdre";

	/**
	 * @param aRequest
	 */
	public DAMylyn(WORequest aRequest) {
		super(aRequest);
	}

	public WOComponent mylynAction() {
		String passwordMylyn = request().stringFormValueForKey(PASSWORD_MYLYN_REQUEST_KEY);
		String passwordMylynConfig = dtApp().config().stringForKey(MYLYN_PASSWORD_CONFIG_KEY);
		String login = request().stringFormValueForKey(LOGIN_REQUEST_KEY);
		String strActOrdre = request().stringFormValueForKey(ACT_ORDRE_REQUEST_KEY);

		CktlLog.log("mylynAction() passwordMylyn=" + passwordMylyn + " login=" + login + " strActOrdre=" + strActOrdre);

		if (StringCtrl.isEmpty(passwordMylynConfig) ||
				(!StringCtrl.isEmpty(passwordMylynConfig) &&
						!StringCtrl.isEmpty(passwordMylyn) &&
					passwordMylyn.equals(passwordMylynConfig))) {

			Integer actOrdre = null;
			if (!StringCtrl.isEmpty(strActOrdre)) {
				actOrdre = new Integer(Integer.parseInt(strActOrdre));
			}
			PageMylyn nextPage = (PageMylyn) dtSession().getSavedPageWithName(PageMylyn.class.getName());
			nextPage.setLogin(login);
			nextPage.setActOrdre(actOrdre);
			return nextPage;

		}

		return null;
	}

	/*
	 * TODO a virer quand les liens de clic dans le plugin mylyn eclipse seront
	 * maitrisés ... ==> ne prend que la premiere DT en date pour 2 meme
	 * intCleService ! (non-Javadoc)
	 * 
	 * @see
	 * er.extensions.appserver.ERXDirectAction#performActionNamed(java.lang.String
	 * )
	 */
	@Override
	public WOActionResults performActionNamed(String actionName) {
		try {
			return super.performActionNamed(actionName);
		} catch (Exception e) {
			String strIntCleService = actionName.substring("mylyn".length(), actionName.length());
			Integer intCleService = new Integer(Integer.parseInt(strIntCleService));
			EOIntervention eoIntervention = (EOIntervention) EOIntervention.fetchAll(
					dtSession().defaultEditingContext(),
					ERXQ.equals(EOIntervention.INT_CLE_SERVICE_KEY, intCleService), null).objectAtIndex(0);
			return super.consultation(eoIntervention.intOrdre());
		}
	}
}
