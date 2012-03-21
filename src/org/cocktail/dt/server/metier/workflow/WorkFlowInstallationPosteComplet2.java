package org.cocktail.dt.server.metier.workflow;

import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitementType;

import com.webobjects.foundation.NSMutableArray;

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

/**
 * Le workflow associé aux demandes de travaux utilisant le formulaire
 * {@link SwapInstallPosteComplet2}
 * 
 * @author ctarade
 */
public class WorkFlowInstallationPosteComplet2
		extends A_WorkFlow {

	private EOTraitementType eoTraitementTypeEmissionBdcInternePourSignature;
	private EOTraitementType eoTraitementTypeReceptionBdcInterneSigne;
	private EOTraitementType eoTraitementTypeRemisePosteCommande;
	private EOTraitementType eoTraitementTypeRestitutionPosteSuiteRemplacement;
	private NSMutableArray<I_WorkFlowItem> tachesObligatoires;

	/**
	 * @param eoIntervention
	 */
	public WorkFlowInstallationPosteComplet2(EOIntervention eoIntervention) {
		super(eoIntervention);
	}

	private EOTraitementType getEoTraitementTypeEmissionBdcInternePourSignature() {
		if (eoTraitementTypeEmissionBdcInternePourSignature == null) {
			eoTraitementTypeEmissionBdcInternePourSignature = EOTraitementType.fetchByKeyValue(
					getEditingContext(), EOTraitementType.TTY_CODE_KEY, EOTraitementType.TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);
		}
		return eoTraitementTypeEmissionBdcInternePourSignature;
	}

	private EOTraitementType getEoTraitementTypeReceptionBdcInterneSigne() {
		if (eoTraitementTypeReceptionBdcInterneSigne == null) {
			eoTraitementTypeReceptionBdcInterneSigne = EOTraitementType.fetchByKeyValue(
					getEditingContext(), EOTraitementType.TTY_CODE_KEY, EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
		}
		return eoTraitementTypeReceptionBdcInterneSigne;
	}

	private EOTraitementType getEoTraitementTypeRemisePosteCommande() {
		if (eoTraitementTypeRemisePosteCommande == null) {
			eoTraitementTypeRemisePosteCommande = EOTraitementType.fetchByKeyValue(
					getEditingContext(), EOTraitementType.TTY_CODE_KEY, EOTraitementType.TYPE_CODE_REMISE_POSTE_COMMANDE);
		}
		return eoTraitementTypeRemisePosteCommande;
	}

	private EOTraitementType getEoTraitementTypeRestitutionPosteSuiteRemplacement() {
		if (eoTraitementTypeRestitutionPosteSuiteRemplacement == null) {
			eoTraitementTypeRestitutionPosteSuiteRemplacement = EOTraitementType.fetchByKeyValue(
					getEditingContext(), EOTraitementType.TTY_CODE_KEY, EOTraitementType.TYPE_CODE_RESTITUTION_POSTE_SUITE_REMPLACEMENT);
		}
		return eoTraitementTypeRestitutionPosteSuiteRemplacement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.metier.workflow.A_WorkFlow#tachesObligatoires()
	 */
	public NSMutableArray<I_WorkFlowItem> tachesObligatoires() {
		if (tachesObligatoires == null) {

			tachesObligatoires = new NSMutableArray<I_WorkFlowItem>();

			if (getEoIntervention().isCommandeARealiser() ||
					getEoIntervention().isPosteSupplementaire()) {
				tachesObligatoires.addObject(getEoTraitementTypeEmissionBdcInternePourSignature());
				tachesObligatoires.addObject(getEoTraitementTypeReceptionBdcInterneSigne());

				// tout remplacement de poste doit contenir au moins un traitement de
				// type restitution
				if (getEoIntervention().isPosteARemplacer()) {
					tachesObligatoires.add(getEoTraitementTypeRestitutionPosteSuiteRemplacement());
				}

				// le poste doit être remis à l'agent
				tachesObligatoires.add(getEoTraitementTypeRemisePosteCommande());
			}

		}
		return tachesObligatoires;
	}
}
