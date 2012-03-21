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
package org.cocktail.dt.server.metier;

import org.cocktail.fwkcktlwebapp.common.CktlLog;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStore;

/**
 * Classe de gestion des notifications des fetch dépassant le nombre de
 * résultats maximum autorisés
 * 
 * @author ctarade
 */
public class DTEOEditingContextHandler {

	private boolean fetchLimitWasExceeded;

	/**
	 * 
	 */
	public DTEOEditingContextHandler() {
		super();
		// raz du témoin de dépassement de limite
		fetchLimitWasExceeded = false;
	}

	/**
	 * Permet de savoir si la limite maximum d'enregistrements retournes a ete
	 * atteinte. Cette valeur n'a de sens que si setPromptsAfterFetchLimit est a
	 * true et que l'utilisateur utilise un fetch avec le parametre
	 * <code>fetchLimit</code> > 0.
	 * 
	 * Cette variable est remise a <code>false</code> avant chaque fetch.
	 */
	public boolean getFetchLimitWasExceeded() {
		return fetchLimitWasExceeded;
	}

	/**
	 * Methode surchargee de EOEditingContext qui permet de receptionner les
	 * notifications en cas de depassement de fetchLimit. Cette version n'est
	 * utilisee que si setPromptsAfterFetchLimit(true) est definit. lors de son
	 * appel (fait automatiquement par WO), alors la methode
	 * getFetchLimitWasExceeded() retournera true.
	 */
	public boolean editingContextShouldContinueFetching(EOEditingContext context, int count,
			int originalLimit, EOObjectStore objectStore) {
		fetchLimitWasExceeded = true;
		CktlLog.log(">>>>>>>>>>>>>>>>>> The limit of " + originalLimit + " has been reached");

		return false;
	}
}
