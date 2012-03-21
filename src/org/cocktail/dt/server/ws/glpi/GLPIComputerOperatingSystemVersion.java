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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;

import com.webobjects.foundation.NSArray;

/**
 * @author ctarade
 * 
 */
public class GLPIComputerOperatingSystemVersion
		extends A_GLPIDropdownObject {

	public final static String GLPI_COMPUTER_OPERATING_SYSTEM_VERSION_ID_KEY = "id";

	private static NSArray<A_GLPIDropdownObject> allGLPIComputerOperatingSystemVersion;

	static {
		resetCache();
	}

	/**
	 * @param aMap
	 */
	public GLPIComputerOperatingSystemVersion(HashMap<String, Object> aMap) {
		super(aMap);
	}

	public static final NSArray<A_GLPIDropdownObject> getAll() {
		return allGLPIComputerOperatingSystemVersion;
	}

	public static void resetCache() {
		allGLPIComputerOperatingSystemVersion = null;
		feedCache();
	}

	private static void feedCache() {

		try {
			allGLPIComputerOperatingSystemVersion = getAll(GLPIComputerOperatingSystemVersion.class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}

}
