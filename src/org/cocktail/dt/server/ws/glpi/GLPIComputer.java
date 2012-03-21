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
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.dt.app.I_ApplicationConsts;
import fr.univlr.cri.planning._imports.StringCtrl;

/**
 * Représentation d'un objet "computer" issus de GLPI (via WS)
 * 
 * @author ctarade
 */
public class GLPIComputer
		extends A_GLPIObject {

	public final static String GLPI_COMPUTER_STATES_NAME_KEY = "states_name";
	public final static String GLPI_COMPUTER_COMPUTERMODELS_ID_KEY = "computermodels_id";
	public final static String GLPI_COMPUTER_OPERATINGSYSTEMS_ID_KEY = "operatingsystems_id";
	public final static String GLPI_COMPUTER_OPERATINGSYSTEMVERSIONS_ID_KEY = "operatingsystemversions_id";
	public final static String GLPI_COMPUTER_OPERATINGSYSTEMSERVICEPACKS_ID_KEY = "operatingsystemservicepacks_id";
	public final static String GLPI_COMPUTER_LOCATIONS_ID_KEY = "locations_id";
	public final static String GLPI_COMPUTER_TO_GLPI_COMPUTER_MODEL_KEY = "toGLPIComputerModel";
	public final static String GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_KEY = "toGLPIComputerOperatingSystem";
	public final static String GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_VERSION_KEY = "toGLPIComputerOperatingSystemVersion";
	public final static String GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_SERVICE_PACK_KEY = "toGLPIComputerOperatingSystemServicePack";
	public final static String GLPI_COMPUTER_TO_GLPI_COMPUTER_LOCATION_KEY = "toGLPIComputerLocation";

	private final static EOQualifier QUAL_CURRENT = new EOOrQualifier(
			new NSArray<EOQualifier>(new EOQualifier[] {
					ERXQ.likeInsensitive(GLPI_COMPUTER_STATES_NAME_KEY, "En service"),
					ERXQ.likeInsensitive(GLPI_COMPUTER_STATES_NAME_KEY, "En stock (neuf)") }));

	public GLPIComputer(HashMap<String, Object> map) {
		super(map);
		// le modele
		String computermodels_id = (String) map.get(GLPI_COMPUTER_COMPUTERMODELS_ID_KEY);
		if (!StringCtrl.isEmpty(computermodels_id)) {
			// voir s'il existe parmi les drop down
			NSArray<A_GLPIDropdownObject> result = EOQualifier.filteredArrayWithQualifier(
					GLPIComputerModel.getAll(),
					ERXQ.equals(
							GLPIComputerModel.GLPI_COMPUTER_MODEL_ID_KEY, computermodels_id));
			if (result.count() > 0) {
				map.put(GLPI_COMPUTER_TO_GLPI_COMPUTER_MODEL_KEY, result.objectAtIndex(0));
			}
		}
		// l'os
		String operatingsystems_id = (String) map.get(GLPI_COMPUTER_OPERATINGSYSTEMS_ID_KEY);
		if (!StringCtrl.isEmpty(computermodels_id)) {
			// voir s'il existe parmi les drop down
			NSArray<A_GLPIDropdownObject> result = EOQualifier.filteredArrayWithQualifier(
					GLPIComputerOperatingSystem.getAll(),
					ERXQ.equals(
							GLPIComputerOperatingSystem.GLPI_COMPUTER_OPERATING_SYSTEM_ID_KEY, operatingsystems_id));
			if (result.count() > 0) {
				map.put(GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_KEY, result.objectAtIndex(0));
			}
		}
		// os version
		String operatingsystemversions_id = (String) map.get(GLPI_COMPUTER_OPERATINGSYSTEMVERSIONS_ID_KEY);
		if (!StringCtrl.isEmpty(operatingsystemversions_id)) {
			// voir s'il existe parmi les drop down
			NSArray<A_GLPIDropdownObject> result = EOQualifier.filteredArrayWithQualifier(
					GLPIComputerOperatingSystemVersion.getAll(),
					ERXQ.equals(
							GLPIComputerOperatingSystemVersion.GLPI_COMPUTER_OPERATING_SYSTEM_VERSION_ID_KEY, operatingsystemversions_id));
			if (result.count() > 0) {
				map.put(GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_VERSION_KEY, result.objectAtIndex(0));
			}
		}
		// os service pack
		String operatingsystemservicepacks_id = (String) map.get(GLPI_COMPUTER_OPERATINGSYSTEMSERVICEPACKS_ID_KEY);
		if (!StringCtrl.isEmpty(operatingsystemversions_id)) {
			// voir s'il existe parmi les drop down
			NSArray<A_GLPIDropdownObject> result = EOQualifier.filteredArrayWithQualifier(
					GLPIComputerOperatingSystemServicePack.getAll(),
					ERXQ.equals(
							GLPIComputerOperatingSystemServicePack.GLPI_COMPUTER_OPERATING_SYSTEM_SERVICE_PACK_ID_KEY, operatingsystemservicepacks_id));
			if (result.count() > 0) {
				map.put(GLPI_COMPUTER_TO_GLPI_COMPUTER_OPERATING_SYSTEM_SERVICE_PACK_KEY, result.objectAtIndex(0));
			}
		}
		// localisation géographique
		String locations_id = (String) map.get(GLPI_COMPUTER_LOCATIONS_ID_KEY);
		if (!StringCtrl.isEmpty(locations_id)) {
			// voir s'il existe parmi les drop down
			NSArray<A_GLPIDropdownObject> result = EOQualifier.filteredArrayWithQualifier(
					GLPIComputerLocation.getAll(),
					ERXQ.equals(
							GLPIComputerLocation.GLPI_COMPUTER_LOCATION_ID_KEY, locations_id));
			if (result.count() > 0) {
				map.put(GLPI_COMPUTER_TO_GLPI_COMPUTER_LOCATION_KEY, result.objectAtIndex(0));
			}
		}
	}

	//

	/**
	 * Liste des {@link GLPIComputer} associés à l'individu via GLPI
	 * 
	 * @param nom
	 * @param prenom
	 * @return
	 * @throws MalformedURLException
	 * @throws XmlRpcException
	 */
	public static NSMutableArray<GLPIComputer> getGLPIComputersForEOIndividu(
			EOIndividu eoIndividu) throws MalformedURLException, XmlRpcException {
		NSMutableArray<GLPIComputer> array = new NSMutableArray<GLPIComputer>();

		//
		XmlRpcClient client = A_GLPIObject.getNewXMLRPCClient();

		// ouverture de session
		String session = openSession(client);

		// computers avec nom et prenom
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
		map.put(A_GLPIObject.GLPI_PARAM_ITEM_TYPE_KEY, A_GLPIObject.GLPI_ITEM_TYPE_COMPUTER_VALUE);
		map.put(
				A_GLPIObject.GLPI_PARAM_CONTACT_NUM_KEY, eoIndividu.nomUsuel() + " " + eoIndividu.prenom());

		Object[] tabResult = (Object[]) A_GLPIObject.callXMLRPC(
				client, A_GLPIObject.GLPI_METHOD_LIST_INVENTORY_OBJECTS, new Object[] { map });

		for (int i = 0; i < tabResult.length; i++) {
			String id = (String) ((HashMap<String, Object>) tabResult[i]).get(A_GLPIObject.GLPI_ID_KEY);
			GLPIComputer glpiComputer = callXMLRPCAndGetGLPIComputer(client, session, id);
			array.addObject(glpiComputer);

		}

		// pareil en inversant nom et prenom
		map.put(
				A_GLPIObject.GLPI_PARAM_CONTACT_NUM_KEY, eoIndividu.prenom() + " " + eoIndividu.nomUsuel());

		tabResult = (Object[]) A_GLPIObject.callXMLRPC(
				client, A_GLPIObject.GLPI_METHOD_LIST_INVENTORY_OBJECTS, new Object[] { map });

		for (int i = 0; i < tabResult.length; i++) {
			String id = (String) ((HashMap<String, Object>) tabResult[i]).get(A_GLPIObject.GLPI_ID_KEY);
			// on réinterroge pas plusieurs fois le même id
			if (!isGLPIComputerArrayContainsId(array, id)) {
				GLPIComputer glpiComputer = callXMLRPCAndGetGLPIComputer(client, session, id);
				array.addObject(glpiComputer);
			}
		}

		// d'apres son login (sur contact)
		CktlUserInfoDB ui = new CktlUserInfoDB(new
				CktlDataBus(eoIndividu.editingContext()));
		ui.compteForPersId(eoIndividu.persId(), true);

		if (!StringCtrl.isEmpty(ui.login())) {

			map = new HashMap<String, Object>();
			map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
			map.put(A_GLPIObject.GLPI_PARAM_ITEM_TYPE_KEY, A_GLPIObject.GLPI_ITEM_TYPE_COMPUTER_VALUE);
			map.put(A_GLPIObject.GLPI_PARAM_CONTACT_KEY, ui.login());

			tabResult = (Object[]) A_GLPIObject.callXMLRPC(
					client, A_GLPIObject.GLPI_METHOD_LIST_INVENTORY_OBJECTS, new Object[] { map });

			for (int i = 0; i < tabResult.length; i++) {
				String id = (String) ((HashMap<String, Object>) tabResult[i]).get(A_GLPIObject.GLPI_ID_KEY);
				// on réinterroge pas plusieurs fois le même id
				if (!isGLPIComputerArrayContainsId(array, id)) {
					GLPIComputer glpiComputer = callXMLRPCAndGetGLPIComputer(client, session, id);
					// le login doit être égal, non pas "like"
					String contact = (String) glpiComputer.valueForKey(A_GLPIObject.GLPI_PARAM_CONTACT_KEY);
					if (!StringCtrl.isEmpty(contact) &&
							contact.equals(ui.login())) {
						array.addObject(glpiComputer);
					}
				}

			}

			// d'apres son login (sur contact_num)
			map = new HashMap<String, Object>();
			map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
			map.put(A_GLPIObject.GLPI_PARAM_ITEM_TYPE_KEY, A_GLPIObject.GLPI_ITEM_TYPE_COMPUTER_VALUE);
			map.put(A_GLPIObject.GLPI_PARAM_CONTACT_NUM_KEY, ui.login());

			tabResult = (Object[]) A_GLPIObject.callXMLRPC(
					client, A_GLPIObject.GLPI_METHOD_LIST_INVENTORY_OBJECTS, new Object[] { map });

			for (int i = 0; i < tabResult.length; i++) {
				String id = (String) ((HashMap<String, Object>) tabResult[i]).get(A_GLPIObject.GLPI_ID_KEY);
				// on réinterroge pas plusieurs fois le même id
				if (!isGLPIComputerArrayContainsId(array, id)) {
					GLPIComputer glpiComputer = callXMLRPCAndGetGLPIComputer(client, session, id);
					// le login doit être égal, non pas "like"
					String contactNum = (String) glpiComputer.valueForKey(A_GLPIObject.GLPI_PARAM_CONTACT_NUM_KEY);
					if (!StringCtrl.isEmpty(contactNum) &&
							contactNum.equals(ui.login())) {
						array.addObject(glpiComputer);
					}
				}

			}

		}

		// fermeture de session
		closeSession(client, session);

		return array;
	}

	/**
	 * L'objet {@link GLPIComputer} associé à un id GLPI, obtenu par l'appel aux
	 * webservices
	 * 
	 * @param client
	 * @param session
	 * @param computerId
	 * @return
	 * @throws XmlRpcException
	 */
	private static GLPIComputer callXMLRPCAndGetGLPIComputer(
			XmlRpcClient client, String session, String computerId) throws XmlRpcException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
		map.put(A_GLPIObject.GLPI_PARAM_COMPUTER_KEY, computerId);
		map.put(A_GLPIObject.GLPI_PARAM_ID_2_NAME_KEY, A_GLPIObject.GLPI_ID_2_NAME_1_VALUE);

		HashMap<String, Object> result = (HashMap<String, Object>) A_GLPIObject.callXMLRPC(
				client, A_GLPIObject.GLPI_METHOD_GET_COMPUTER, new Object[] { map });

		GLPIComputer glpiComputer = new GLPIComputer(result);

		return glpiComputer;
	}

	/**
	 * Liste des {@link GLPIComputer} courants associés à l'individu via GLPI
	 * 
	 * @param nom
	 * @param prenom
	 * @return
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public static NSArray<GLPIComputer> getCurrentGLPIComputersForEOIndividu(EOIndividu eoIndividu) throws MalformedURLException, XmlRpcException {
		NSArray<GLPIComputer> result = getGLPIComputersForEOIndividu(eoIndividu);

		result = EOQualifier.filteredArrayWithQualifier(result, QUAL_CURRENT);

		return result;
	}

	/**
	 * Indique s'il y a au moins 1 {@link GLPIComputer} ayant l'id dans le tableau
	 * array
	 * 
	 * @param array
	 * @param id
	 * @return
	 */
	public static boolean isGLPIComputerArrayContainsId(
			NSArray<GLPIComputer> array, String id) {
		boolean isFound = false;

		NSArray<GLPIComputer> result = EOQualifier.filteredArrayWithQualifier(
				array, ERXQ.equals(A_GLPIObject.GLPI_ID_KEY, id));

		if (result.count() > 0) {
			isFound = true;
		}

		return isFound;
	}

	/**
	 * On intercepte tout ce qui n'est pas {@link NSKeyValueCoding} et qui pointe
	 * sur des methodes locales
	 * 
	 * @see org.cocktail.dt.server.ws.glpi.A_GLPIObject#valueForKey(java.lang.String)
	 */
	@Override
	public Object valueForKey(String key) {
		if (key.equals(GLPI_URL_FRONT_COMPUTER_KEY)) {
			return getGlpiUrlFrontComputer();
		}
		return super.valueForKey(key);
	}

	private final static String GLPI_URL_FRONT_COMPUTER_KEY = "glpiUrlFrontComputer";

	/**
	 * Url d'accès direct à GLPI sur cette machine
	 * 
	 * @return
	 */
	private String getGlpiUrlFrontComputer() {
		String url = "";

		url = appConfig.stringForKey(I_ApplicationConsts.GLPI_FRONT_COMPUTER_URL_KEY) + (String) valueForKey(GLPI_ID_KEY);

		return url;
	}
}
