/*
 * Copyright Cocktail, 2001-2008 
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

package org.cocktail.dt.server.metier;

import org.cocktail.dt.server.metier.workflow.I_WorkFlowItem;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.dt.app.I_ApplicationConsts;

public class EOTraitementType
		extends _EOTraitementType
		implements I_WorkFlowItem {

	public EOTraitementType() {
		super();
	}

	public void validateForInsert() throws NSValidation.ValidationException {
		this.validateObjectMetier();
		validateBeforeTransactionSave();
		super.validateForInsert();
	}

	public void validateForUpdate() throws NSValidation.ValidationException {
		this.validateObjectMetier();
		validateBeforeTransactionSave();
		super.validateForUpdate();
	}

	public void validateForDelete() throws NSValidation.ValidationException {
		super.validateForDelete();
	}

	/**
	 * Apparemment cette methode n'est pas appel̩e.
	 * 
	 * @see com.webobjects.eocontrol.EOValidation#validateForUpdate()
	 */
	public void validateForSave() throws NSValidation.ValidationException {
		validateObjectMetier();
		validateBeforeTransactionSave();
		super.validateForSave();

	}

	/**
	 * Peut etre appele �� partir des factories.
	 * 
	 * @throws NSValidation.ValidationException
	 */
	public void validateObjectMetier() throws NSValidation.ValidationException {

	}

	/**
	 * A appeler par les validateforsave, forinsert, forupdate.
	 * 
	 */
	private final void validateBeforeTransactionSave() throws NSValidation.ValidationException {

	}

	// les codes des types de traitement connus

	public final static String TYPE_CODE_TEXTUEL = "T";
	public final static String TYPE_CODE_PRESTATION = "P";
	public final static String TYPE_CODE_COMMANDE = "C";
	public final static String TYPE_CODE_LIVRAISON = "L";
	public final static String TYPE_CODE_NO_SERIE = "S";

	public final static String TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE = "E";
	public final static String TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE = "R";
	public final static String TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE = "N";

	public final static String TYPE_CODE_INCOHERENCE_DEMANDE_NB_POSTE = "I";

	public final static String TYPE_CODE_REMISE_POSTE_COMMANDE = "O";
	public final static String TYPE_CODE_RESTITUTION_POSTE_SUITE_REMPLACEMENT = "Q";

	public final static String TRAITEMENT_MOTIF_REMISE_POSTE_COMMANDE = "Poste remis à l'utilisateur";
	public final static String TRAITEMENT_MOTIF_RESTITUTION_POSTE_SUITE_REMPLACEMENT = "Poste restitué par l'utilisateur (suite au remplacement de l'ancien)";

	public boolean isTraitementTextuel() {
		return ttyCode().equals(TYPE_CODE_TEXTUEL);
	}

	public boolean isTraitementPrestation() {
		return ttyCode().equals(TYPE_CODE_PRESTATION);
	}

	public boolean isTraitementCommande() {
		return ttyCode().equals(TYPE_CODE_COMMANDE);
	}

	public boolean isTraitementLivraison() {
		return ttyCode().equals(TYPE_CODE_LIVRAISON);
	}

	public boolean isTraitementNoSerie() {
		return ttyCode().equals(TYPE_CODE_NO_SERIE);
	}

	public boolean isTraitementEmissionBdcInternePourSignature() {
		return ttyCode().equals(TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);
	}

	public boolean isTraitementReceptionBdcInterneSigne() {
		return ttyCode().equals(TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
	}

	public boolean isTraitementClotureSurcoutNonSigne() {
		return ttyCode().equals(TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);
	}

	public boolean isTraitementIncoherenceDemandeNbPoste() {
		return ttyCode().equals(TYPE_CODE_INCOHERENCE_DEMANDE_NB_POSTE);
	}

	public boolean isTraitementRemisePosteCommande() {
		return ttyCode().equals(TYPE_CODE_REMISE_POSTE_COMMANDE);
	}

	public boolean isTraitementRestitutionPosteSuiteRemplacement() {
		return ttyCode().equals(TYPE_CODE_RESTITUTION_POSTE_SUITE_REMPLACEMENT);
	}

	public boolean isTraitementTextuelLibreOuPreRempli() {
		boolean isTraitementTextuelLibreOuPreRempli = false;

		if (isTraitementTextuel() ||
				isTraitementClotureSurcoutNonSigne() ||
				isTraitementIncoherenceDemandeNbPoste()) {
			isTraitementTextuelLibreOuPreRempli = true;
		}

		return isTraitementTextuelLibreOuPreRempli;
	}

	public boolean isTraitementAvecDateEtSansMotif() {
		boolean isTraitementAvecDateEtSansMotif = false;

		if (isTraitementRemisePosteCommande() ||
				isTraitementRestitutionPosteSuiteRemplacement()) {
			isTraitementAvecDateEtSansMotif = true;
		}

		return isTraitementAvecDateEtSansMotif;
	}

	/**
	 * Pour certains type de traitement, il y a motif par défaut
	 * 
	 * TODO virer le paramètre quand la classe application sera dans un package
	 * 
	 * @return
	 */
	public String getTraitementMotifPourType(CktlConfig appConfig) {
		String result = null;

		if (isTraitementIncoherenceDemandeNbPoste()) {
			result = appConfig.stringForKey(I_ApplicationConsts.PHRASE_INCOHERENCE_DEMANDE_NB_POSTE_KEY);
		} else if (isTraitementAvecDateEtSansMotif()) {
			result = TRAITEMENT_MOTIF_REMISE_POSTE_COMMANDE;
		} else if (isTraitementRestitutionPosteSuiteRemplacement()) {
			result = TRAITEMENT_MOTIF_RESTITUTION_POSTE_SUITE_REMPLACEMENT;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cocktail.dt.server.metier.workflow.I_WorkFlowItem#libelleWorkFlowItem()
	 */
	public String libelle() {
		String libelleWorkFlowItem = null;

		libelleWorkFlowItem = "Traitement de type " + ttyLibelle();

		return libelleWorkFlowItem;
	}

	public static EOTraitementType getEoTraitementTypeForCodeInArray(
				NSArray<EOTraitementType> array, String ttyCode) {
		EOTraitementType eoTraitementType = null;

		NSArray<EOTraitementType> result = EOQualifier.filteredArrayWithQualifier(
					array,
					ERXQ.equals(EOTraitementType.TTY_CODE_KEY, ttyCode));

		if (result.count() > 0) {
			eoTraitementType = result.objectAtIndex(0);
		}

		return eoTraitementType;
	}

	/**
	 * Est-ce que ce type de traitement génère automatiquement le contenu
	 * 
	 * @return
	 */
	public boolean isContenuTraitementAutomatique() {
		boolean isContenuTraitementAutomatique = false;

		if (isTraitementEmissionBdcInternePourSignature() ||
				isTraitementReceptionBdcInterneSigne()) {
			isContenuTraitementAutomatique = true;
		}

		return isContenuTraitementAutomatique;
	}
}
