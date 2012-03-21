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
package org.cocktail.dt.server.metier.workflow;

import org.cocktail.dt.server.metier.EOIntervention;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSMutableArray;

/**
 * Clase de gestion d'un workflow
 * 
 * @author ctarade
 */
public abstract class A_WorkFlow {

	private EOIntervention eoIntervention;

	public A_WorkFlow(EOIntervention eoIntervention) {
		super();
		this.eoIntervention = eoIntervention;
	}

	private NSMutableArray<I_WorkFlowItem> tachesFaites =
			new NSMutableArray<I_WorkFlowItem>();

	public final void ajouterTacheFaite(I_WorkFlowItem workFlowItem) {
		tachesFaites.addObject(workFlowItem);
	}

	/** liste des taches obligatoires */
	public abstract NSMutableArray<I_WorkFlowItem> tachesObligatoires();

	/**
	 * indique toutes les taches obligatoires sont terminées
	 */
	public final boolean isEtapesObligatoiresTerminees() {
		boolean isTachesObligatoiresTerminees = false;

		if (getTachesObligatoiresAbsentes().count() == 0) {
			isTachesObligatoiresTerminees = true;
		}

		return isTachesObligatoiresTerminees;
	}

	/**
	 * Liste des taches non acomplies
	 * 
	 * @return
	 */
	public NSMutableArray<I_WorkFlowItem> getTachesObligatoiresAbsentes() {
		NSMutableArray<I_WorkFlowItem> tachesObligatoiresAbsentes = new NSMutableArray<I_WorkFlowItem>();

		for (int i = 0; i < tachesObligatoires().count(); i++) {
			I_WorkFlowItem tache = tachesObligatoires().objectAtIndex(i);
			if (!tachesFaites.containsObject(tache)) {
				tachesObligatoiresAbsentes.addObject(tache);
			}
		}

		return tachesObligatoiresAbsentes;
	}

	/**
	 * Le message d'erreur survenu dans le workflow
	 * 
	 * @return
	 */
	public final String getMessageErreur() {
		String messageErreur = null;

		// il faut que toutes les taches obligatoires soient accomplies
		NSMutableArray<I_WorkFlowItem> tachesObligatoiresAbsentes = getTachesObligatoiresAbsentes();
		if (tachesObligatoiresAbsentes.count() > 0) {
			messageErreur = "Tâches non effectuées : ";
			messageErreur += "<ul>";
			for (int i = 0; i < tachesObligatoiresAbsentes.count(); i++) {
				I_WorkFlowItem tache = tachesObligatoiresAbsentes.objectAtIndex(i);
				messageErreur += "<li>";
				messageErreur += tache.libelle();
				messageErreur += "</li>";
			}
			messageErreur += "</ul>";
		}

		return messageErreur;
	}

	/**
	 * 
	 * @return
	 */
	public final EOEditingContext getEditingContext() {
		return getEoIntervention().editingContext();
	}

	/**
	 * 
	 * @return
	 */
	public final EOIntervention getEoIntervention() {
		return eoIntervention;
	}

}
