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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * @author ctarade
 * 
 */
public abstract class A_GLPIDropdownObject
		extends A_GLPIObject {

	/**
	 * @param aMap
	 */
	public A_GLPIDropdownObject(HashMap<String, Object> aMap) {
		super(aMap);
	}

	/**
	 * Construire la liste complete d'objets
	 * 
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws MalformedURLException
	 * @throws XmlRpcException
	 */
	protected static NSArray<A_GLPIDropdownObject> getAll(
			Class dropdownClass) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, MalformedURLException, XmlRpcException {
		NSMutableArray<A_GLPIDropdownObject> array = new NSMutableArray<A_GLPIDropdownObject>();

		XmlRpcClient client = getNewXMLRPCClient();
		String session = openSession(client);

		// la clé dropdown associée à la classe
		String dropdownValue = null;
		if (dropdownClass == GLPIComputerModel.class) {
			dropdownValue = GLPI_DROPDOWN_COMPUTER_MODEL_VALUE;
		} else if (dropdownClass == GLPIComputerOperatingSystem.class) {
			dropdownValue = GLPI_DROPDOWN_OPERATING_SYSTEM_VALUE;
		} else if (dropdownClass == GLPIComputerOperatingSystemVersion.class) {
			dropdownValue = GLPI_DROPDOWN_OPERATING_SYSTEM_VERSION_VALUE;
		} else if (dropdownClass == GLPIComputerOperatingSystemServicePack.class) {
			dropdownValue = GLPI_DROPDOWN_OPERATING_SYSTEM_SERVICE_PACK_VALUE;
		} else if (dropdownClass == GLPIComputerLocation.class) {
			dropdownValue = GLPI_DROPDOWN_LOCATION_VALUE;
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(A_GLPIObject.GLPI_PARAM_SESSION_KEY, session);
		map.put(A_GLPIObject.GLPI_PARAM_DROPDOWN_KEY, dropdownValue);
		Object[] tabResult = (Object[]) A_GLPIObject.callXMLRPC(
				client, A_GLPIObject.GLPI_METHOD_LIST_DROPDOWN_VALUES, new Object[] { map });

		for (int i = 0; i < tabResult.length; i++) {

			HashMap<String, Object> localMap = (HashMap<String, Object>) tabResult[i];

			Constructor constructor = dropdownClass.getConstructor(
					new Class[] { HashMap.class });
			A_GLPIDropdownObject glpiDropDownObject = (A_GLPIDropdownObject) constructor.newInstance(
					new Object[] { localMap });
			array.addObject(glpiDropDownObject);
		}

		closeSession(client, session);

		return array.immutableClone();
	}
}
