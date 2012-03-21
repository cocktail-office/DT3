/*
 * Copyright Universit� de La Rochelle 2004
 *
 * ctarade@univ-lr.fr
 *
 * Ce logiciel est un programme informatique servant � g�rer les comptes
 * informatiques des utilisateurs. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

package org.cocktail.ycrifwk.utils;

import org.cocktail.fwkcktlwebapp.server.CktlMailBus;

/**
 * @author ctarade
 * 
 *         classe permettant de gerer les Exception Java
 */
public class UtilException {

	/**
	 * permet de recuperer la trace d'une exception au format string message +
	 * trace
	 * 
	 * @param e
	 * @return
	 */
	public static String stackTraceToString(Throwable e, boolean useHtml) {
		String tagCR = "\n";
		if (useHtml)
			tagCR = "<br>";
		String stackStr = e.getMessage() + tagCR + tagCR;
		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; i < stack.length; i++)
			stackStr += ((StackTraceElement) stack[i]).toString() + tagCR;
		return stackStr;
	}

	/**
	 * envoi de la trace d'une exception � une personne
	 * 
	 * @param mailBus
	 * @param appName
	 * @param e
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean sendExceptionTrace(CktlMailBus mailBus, String appName, String from, String to, String startText, Throwable e) {
		return mailBus.sendMail(
				from,
				to,
				null,
				"[" + appName + "]Exception",
				startText + "\n\nTrace de l'exception : \n\n" +
						stackTraceToString(e, false) +
						(e != null && e.getCause() != null ? "\nCause by :" + stackTraceToString(e.getCause(), false) : ""));
	}

}
