/*
 * Copyright Université de La Rochelle 2011
 *
 * Ce logiciel est un programme informatique servant à gérer les demandes
 * d'utilisateurs auprès d'un service.
 * 
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.

 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant 
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */
package org.cocktail.dt.server.ws.glpi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;

import com.webobjects.foundation.NSKeyValueCoding;

import er.extensions.eof.ERXCustomObject;
import fr.univlr.cri.dt.app.I_ApplicationConsts;

/**
 * @author ctarade
 * 
 */
public abstract class A_GLPIObject
		extends ERXCustomObject
		implements NSKeyValueCoding {

	// methodes
	public final static String GLPI_METHOD_TEST = "glpi.test";
	public final static String GLPI_METHOD_STATUS = "glpi.status";
	public final static String GLPI_METHOD_DO_LOGIN = "glpi.doLogin";
	public final static String GLPI_METHOD_LIST_INVENTORY_OBJECTS = "glpi.listInventoryObjects";
	public final static String GLPI_METHOD_GET_COMPUTER = "glpi.getComputer";
	public final static String GLPI_METHOD_DO_LOGOUT = "glpi.doLogout";
	public final static String GLPI_METHOD_LIST_DROPDOWN_VALUES = "glpi.listDropdownValues";
	// cles
	public final static String GLPI_ID_KEY = "id";
	public final static String GLPI_SESSION_KEY = "session";
	// parametres
	public final static String GLPI_PARAM_LOGIN_USERNAME_KEY = "login_name";
	public final static String GLPI_PARAM_LOGIN_PASSWORD_KEY = "login_password";
	public final static String GLPI_PARAM_SESSION_KEY = "session";
	public final static String GLPI_PARAM_ITEM_TYPE_KEY = "itemtype";
	public final static String GLPI_PARAM_CONTACT_NUM_KEY = "contact_num";
	public final static String GLPI_PARAM_CONTACT_KEY = "contact";
	public final static String GLPI_PARAM_COMPUTER_KEY = "computer";
	public final static String GLPI_PARAM_ID_2_NAME_KEY = "id2name";
	public final static String GLPI_PARAM_DROPDOWN_KEY = "dropdown";
	// valeurs
	public final static String GLPI_ITEM_TYPE_COMPUTER_VALUE = "Computer";
	public final static String GLPI_ID_2_NAME_1_VALUE = "1";
	public final static String GLPI_DROPDOWN_COMPUTER_MODEL_VALUE = "ComputerModel";
	public final static String GLPI_DROPDOWN_OPERATING_SYSTEM_VALUE = "OperatingSystem";
	public final static String GLPI_DROPDOWN_OPERATING_SYSTEM_VERSION_VALUE = "OperatingSystemVersion";
	public final static String GLPI_DROPDOWN_OPERATING_SYSTEM_SERVICE_PACK_VALUE = "OperatingSystemServicePack";
	public final static String GLPI_DROPDOWN_LOCATION_VALUE = "Location";

	private HashMap<String, Object> map;

	/**
	 * 
	 */
	public A_GLPIObject(HashMap<String, Object> aMap) {
		super();
		map = aMap;
	}

	protected static CktlConfig appConfig;

	/**
	 * Affectation de la config à la classe
	 * 
	 * @param anAppConfig
	 */
	public static void initAppConfig(
			CktlConfig anAppConfig) {
		appConfig = anAppConfig;
	}

	/**
	 * 
	 */
	public static void resetAllCache() {
		GLPIComputerLocation.resetCache();
		GLPIComputerModel.resetCache();
		GLPIComputerOperatingSystem.resetCache();
		GLPIComputerOperatingSystemServicePack.resetCache();
		GLPIComputerOperatingSystemVersion.resetCache();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOCustomObject#valueForKey(java.lang.String)
	 */
	@Override
	public Object valueForKey(String key) {
		return map.get(key);
	}

	/**
	 * Ouverture d'une session authentifier vers GLPI
	 * 
	 * @param client
	 * @return l'identifiant de session
	 * @throws XmlRpcException
	 */
	protected static String openSession(XmlRpcClient client) throws XmlRpcException {

		// ouverture de session
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(A_GLPIObject.GLPI_PARAM_LOGIN_USERNAME_KEY, appConfig.stringForKey(I_ApplicationConsts.GLPI_WS_USER_KEY));
		map.put(A_GLPIObject.GLPI_PARAM_LOGIN_PASSWORD_KEY, appConfig.stringForKey(I_ApplicationConsts.GLPI_WS_PASSWORD_KEY));
		Object result = A_GLPIObject.callXMLRPC(client, A_GLPIObject.GLPI_METHOD_DO_LOGIN, new Object[] { map });
		String session = (String) ((HashMap<String, Object>) result).get(A_GLPIObject.GLPI_SESSION_KEY);

		return session;
	}

	/**
	 * Fermeture d'un session
	 * 
	 * @param client
	 * @param session
	 * @return
	 * @throws XmlRpcException
	 */
	protected static void closeSession(XmlRpcClient client, String session) throws XmlRpcException {

		// fermeture de session
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
		A_GLPIObject.callXMLRPC(client, A_GLPIObject.GLPI_METHOD_DO_LOGOUT, new Object[] { map });
	}

	/**
	 * 
	 * @param client
	 * @param method
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	protected static Object callXMLRPC(XmlRpcClient client, String method, Object[] params)
			throws XmlRpcException {

		// System.out.println("callXMLRPC()");
		// System.out.println(" --- method=" + method);
		// System.out.print(" --- params=");
		// for (int i = 0; i < params.length; i++) {
		// System.out.print(params[i] + "");
		// }
		// System.out.print("\n");

		Object result = client.execute(method, params);

		// if (result instanceof HashMap) {
		//
		// System.out.println("result = " + result);
		//
		// } else {
		// System.out.println("result.length = " + ((Object[]) result).length);
		//
		// for (int i = 0; i < ((Object[]) result).length; i++) {
		// System.out.println("(" + i + ") result = " + ((Object[]) result)[i]);
		// }
		//
		// }

		return result;

	}

	/**
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected static XmlRpcClient getNewXMLRPCClient()
			throws MalformedURLException {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		config.setServerURL(new URL(appConfig.stringForKey(I_ApplicationConsts.GLPI_WS_URL_KEY)));

		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		return client;
	}

	/**
	 * Teste la disponibilité du webservice
	 * 
	 * @return
	 */
	public static boolean testService() {
		boolean isTestOk = true;

		if (StringCtrl.isEmpty(appConfig.stringForKey(I_ApplicationConsts.GLPI_WS_URL_KEY))) {
			isTestOk = false;
		}

		// test disponibilité
		if (isTestOk) {

			XmlRpcClient client = null;

			try {
				//
				client = A_GLPIObject.getNewXMLRPCClient();

				// computers avec nom et prenom
				HashMap<String, Object> map = new HashMap<String, Object>();

				callXMLRPC(client, GLPI_METHOD_TEST, new Object[] { map });

			} catch (Exception e) {
				e.printStackTrace();
				isTestOk = false;
			}

			// test ouverture session
			if (isTestOk) {
				try {
					String session = openSession(client);
					closeSession(client, session);
				} catch (Exception e) {
					e.printStackTrace();
					isTestOk = false;
				}
			}

		}

		return isTestOk;
	}
}
