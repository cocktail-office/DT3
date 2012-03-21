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

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Classe d'inventaire des sessions ouvertes sur l'application
 * 
 * @author ctarade
 */
public class DTSessionManager {

	private final static NSMutableArray<Session> sessionArray = new NSMutableArray<Session>();
	private final static NSMutableDictionary<String, String> ipDico = new NSMutableDictionary<String, String>();

	public final static void registerSession(Session session) {
		sessionArray.addObject(session);
	}

	public final static void unregisterSession(Session session) {
		int index = sessionArray.indexOfIdenticalObject(session);
		if (index != NSArray.NotFound) {
			sessionArray.removeObjectAtIndex(index);
			ipDico.removeObjectForKey(session.sessionID());
		}
	}

	public final static NSMutableArray<Session> getSessionArray() {
		return sessionArray;
	}

	public final static void registerContext(Session session, WOContext context) {
		ipDico.setObjectForKey(dtApp().getRequestIPAddress(context.request()), session.sessionID());
	}

	public final static String getIpForSession(Session session) {
		String ip = null;

		ip = ipDico.objectForKey(session.sessionID());

		return ip;
	}

	private final static Application dtApp() {
		return (Application) Application.application();
	}
}
