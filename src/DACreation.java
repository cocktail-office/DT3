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
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlLogin;
import org.cocktail.fwkcktlwebapp.server.components.CktlLoginResponder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNumberFormatter;

/**
 * Implemente les actions permettant d'acceder directement a la page de la
 * creation des demandes.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DACreation extends DirectAction {
	public final static String SERVICE_CODE_KEY = "cs";
	public final static String ACTIVITE_CODE_KEY = "actOrdre";
	private NSDictionary actionParams;
	private String serviceName;
	private String activiteName;

	public DACreation(WORequest request) {
		super(request);
	}

	public WOActionResults defaultAction() {
		//
		String serviceCode = (String) request().formValueForKey(SERVICE_CODE_KEY);
		String checkError = checkService(serviceCode);
		if (checkError.length() > 0) {
			return loginCasFailurePage(checkError, null);
			// Et en fin, on affiche la page
		} else {
			// on a un service, on regarde s'il y a une activite
			NSNumberFormatter numberFormatter = new NSNumberFormatter();
			numberFormatter.setPattern("0");
			Number actOrdre = request().numericFormValueForKey(ACTIVITE_CODE_KEY, numberFormatter);
			if (actOrdre != null) {
				checkError = checkActiviteForService(actOrdre, serviceCode);
				if (checkError.length() > 0) {
					return loginCasFailurePage(checkError, null);
					// Et en fin, on affiche la page
				} else {
					setLoginComment("Cr&eacute;ation : " + serviceName + " => " + activiteName);
					actionParams = new NSDictionary(
							new Object[] { serviceCode, actOrdre },
							new String[] { SERVICE_CODE_KEY, ACTIVITE_CODE_KEY });
				}
			} else {
				setLoginComment("Cr&eacute;ation : " + serviceName);
				actionParams = new NSDictionary(serviceCode, SERVICE_CODE_KEY);
			}
			if (useCasService())
				// return loginCASPage();
				return pageForURL(getLoginActionURL(context(), true, null, false, actionParams));
			else
				return loginNoCasPage(actionParams);
		}
	}

	/**
	 * Verifie si l'activite avec le numero <code>anActOdre</code> est enregistre
	 * dans l'application DT et qu'une demande peut lui etre creee via Web.
	 * 
	 * <p>
	 * Retourne <em>null</em> dans le cas de succes. Retourne le message d'erreur
	 * si la demande ne peut pas etre creee pour l'activite donne.
	 * </p>
	 */
	private String checkActiviteForService(Number anActOrdre, String aServiceCode) {
		StringBuffer err = new StringBuffer();
		// Test si l'activite existe
		DTActiviteBus aBus = new DTActiviteBus(dtApp().dataBus().editingContext());
		CktlRecord rec = aBus.findActivite(anActOrdre, aServiceCode);
		if (rec == null) {
			err.append("L'activit&eacute; avec le num&eacute;ro et le code indiqu&eacute; n'est pas enregistr&eacute; dans l'application");
		} else {
			// test si l'activité une feuille
			if (!aBus.isVActiviteLeaf(anActOrdre)) {
				err.append("L'activit&eacute; avec le num&eacute;ro et le code indiqu&eacute; n'est pas une application &quot;feuille&quot;");
			} else {
				activiteName = rec.stringForKey("actLibelle");
			}
		}
		if (err.length() > 0) {
			err.append(" (").append(SERVICE_CODE_KEY).append("=").append(aServiceCode).append(", ");
			err.append(ACTIVITE_CODE_KEY).append("=").append(anActOrdre).append(")");
			err.append("<br>La page de cr&eacute;ation des demandes ne sera pas accessible.");
		}

		return err.toString();
	}

	/**
	 * Verifie si le service avec le code <code>serviceCode</code> est enregistre
	 * dans l'application DT et qu'une demande peut lui etre creee via Web.
	 * 
	 * <p>
	 * Retourne <em>null</em> dans le cas de succes. Retourne le message d'erreur
	 * si la demande ne peut pas etre creee pour le service donne.
	 * </p>
	 */
	private String checkService(String serviceCode) {
		StringBuffer err = new StringBuffer();
		// Le code du service est bien donne
		if (serviceCode == null) {
			err.append("Le code du service destinataire n'est pas pr&eacute;cis&eacute;");
		} else {
			// Le service existe
			DTActiviteBus aBus = new DTActiviteBus(dtApp().dataBus().editingContext());
			CktlRecord rec = aBus.findGroupeWithCode(serviceCode);
			if (rec == null) {
				err.append("Le service avec le code indiqu&eacute; n'est pas enregistr&eacute; dans l'application");
			} else {
				DTServiceBus sBus = new DTServiceBus(dtApp().dataBus().editingContext());
				rec = sBus.structureForCode(rec.stringForKey("cStructure"));
				if (rec == null) {
					err.append("Le service avec le code indiqu&eacute; n'existe pas dans la base de donn&eacute;es");
				} else {
					serviceName = rec.stringForKey("llStructure");
				}
			}
		}
		if (err.length() > 0) {
			err.append(" (").append(SERVICE_CODE_KEY).append(")");
			err.append("<br>La page de cr&eacute;ation des demandes ne sera pas accessible.");
		}

		return err.toString();
	}

	/**
	 * @see DirectAction#casLoginLink()
	 */
	public String casLoginLink() {
		return getLoginActionURL(context(), true, null, false, actionParams);
	}

	public WOActionResults loginCasSuccessPage(String netid, NSDictionary actionParams) {
		CktlLog.trace(null);
		dtSession().setMode(Session.MODE_DA_CREAT);
		dtSession().setCurrentServiceDest(
				-StringCtrl.toInt((String) actionParams.valueForKey(SERVICE_CODE_KEY), 0));
		String strActOrdre = (String) actionParams.valueForKey(ACTIVITE_CODE_KEY);
		if (!StringCtrl.isEmpty(strActOrdre)) {
			dtSession().setCurrentActOrdre(
					new Integer(Integer.parseInt(strActOrdre)));
		}
		String errorMsg = dtSession().setConnectedUser(netid);
		if (errorMsg == null) {
			CktlLog.log("login : " + netid + ", type : creation - OK");
			return getCreationPage(dtSession());
		}
		return loginCasFailurePage(errorMsg, null);
	}

	private PageCreation getCreationPage(Session session) {
		PageCreation pageCreation = (PageCreation) session.getSavedPageWithName(PageCreation.class.getName());
		pageCreation.setCodeServiceSelectedItem(Integer.toString(dtSession().getCurrectServiceDest()));
		// traitement particulier si l'activité est selectionnée
		if (session.getCurrentActOrdre() != null) {
			// preremplir le browser d'activités
			pageCreation.activiteListener.selectActiviteByActOrdre(session.getCurrentActOrdre());
			// on indique explicitement que l'on ne veut pas afficher le browser de
			// selection d'activité
			pageCreation.setShowSelectActivite(false);
		}
		session.setPageContenu(pageCreation);
		return pageCreation;
	}

	public CktlLoginResponder getNewLoginResponder(NSDictionary someActionParams) {
		return new DACreationLoginResponder(someActionParams);
	}

	public class DACreationLoginResponder extends DirectAction.DefaultLoginResponder {

		public DACreationLoginResponder(NSDictionary actionParams) {
			super(actionParams);
		}

		public WOComponent loginAccepted(CktlLogin loginComponent) {
			Session session = (Session) loginComponent.session();
			session.setMode(Session.MODE_DA_CREAT);
			session.setConnectedUserInfo(loginComponent.loggedUserInfo());
			session.setCurrentServiceDest(
					-StringCtrl.toInt((String) actionParams.valueForKey(SERVICE_CODE_KEY), 0));
			session.setCurrentActOrdre((Number) actionParams.valueForKey(ACTIVITE_CODE_KEY));
			return getCreationPage(session);
		}
	}
}
