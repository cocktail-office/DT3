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

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSValidation;

public class EOTraitement
		extends _EOTraitement {

	public EOTraitement() {
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

	// ajout

	public final static String OUI = "OUI";
	public final static String NON = "NON";
	public final static String SANS_OBJET = "SANS OBJET";

	private final static String NUMERO_COMMANDE = "Numéro de commande pour le matériel : ";
	private final static String SEPARATEUR_NUMERO_COMMANDE = "\n";
	private final static String NUMERO_COMMANDE_SURCOUT = "Numéro de commande pour le surcoût : ";

	private final static String IS_COMMANDE_SIGNEE = "Commande(s) de matériel signée(s) : ";
	private final static String IS_COMMANDE_SURCOUT_SIGNEE = "Commande(s) liée(s) au(x) surcoût(s) signée(s) : ";

	public boolean isPublic() {
		return traConsultable().equals("O");
	}

	public boolean isCommandeSigneeOui() {
		boolean isCommandeSigneeOui = false;

		if (traTraitement().contains(IS_COMMANDE_SIGNEE + OUI)) {
			isCommandeSigneeOui = true;
		}

		return isCommandeSigneeOui;
	}

	public boolean isCommandeSigneeNon() {
		boolean isCommandeSigneeNon = false;

		if (traTraitement().contains(IS_COMMANDE_SIGNEE + NON)) {
			isCommandeSigneeNon = true;
		}

		return isCommandeSigneeNon;
	}

	public boolean isCommandeSigneeSansObjet() {
		boolean isCommandeSigneeSansObjet = false;

		if (traTraitement().contains(IS_COMMANDE_SIGNEE + SANS_OBJET) ||
					isCommandeSigneeOui() == false ||
					isCommandeSigneeNon() == false) {
			isCommandeSigneeSansObjet = true;
		}

		return isCommandeSigneeSansObjet;
	}

	public boolean isCommandeSurcoutSigneeOui() {
		boolean isCommandeSigneeSurcoutOui = false;

		if (traTraitement().contains(IS_COMMANDE_SURCOUT_SIGNEE + OUI)) {
			isCommandeSigneeSurcoutOui = true;
		}

		return isCommandeSigneeSurcoutOui;
	}

	public boolean isCommandeSurcoutSigneeNon() {
		boolean isCommandeSigneeSurcoutNon = false;

		if (traTraitement().contains(IS_COMMANDE_SURCOUT_SIGNEE + NON)) {
			isCommandeSigneeSurcoutNon = true;
		}

		return isCommandeSigneeSurcoutNon;
	}

	public boolean isCommandeSurcoutSigneeSansObjet() {
		boolean isCommandeSigneeSurcoutSansObjet = false;

		if (traTraitement().contains(IS_COMMANDE_SURCOUT_SIGNEE + SANS_OBJET) ||
					isCommandeSurcoutSigneeOui() == false ||
					isCommandeSurcoutSigneeNon() == false) {
			isCommandeSigneeSurcoutSansObjet = true;
		}

		return isCommandeSigneeSurcoutSansObjet;
	}

	/**
	 * Certains type de traitement génère automatiquement le contenu du
	 * traitement.
	 * 
	 * @return
	 */
	public static String getTraTraitementAutomatique(
			EOTraitementType eoTraitementType,
			String numCommande,
			String numCommandeSurcout,
			String commandeSignee,
			String commandeSurcoutSignee) {
		String traTraitement = null;

		if (eoTraitementType.isContenuTraitementAutomatique()) {

			if (eoTraitementType.isTraitementEmissionBdcInternePourSignature()) {

				traTraitement = NUMERO_COMMANDE;
				if (!StringCtrl.isEmpty(numCommande)) {
					traTraitement += numCommande;
				} else {
					traTraitement += NON;
				}

				traTraitement += "\n";
				traTraitement += NUMERO_COMMANDE_SURCOUT;

				if (!StringCtrl.isEmpty(numCommandeSurcout)) {
					traTraitement += numCommandeSurcout;
				} else {
					traTraitement += NON;
				}

			}

			if (eoTraitementType.isTraitementReceptionBdcInterneSigne()) {

				traTraitement = IS_COMMANDE_SIGNEE + commandeSignee;
				traTraitement += SEPARATEUR_NUMERO_COMMANDE;
				traTraitement += IS_COMMANDE_SURCOUT_SIGNEE + commandeSurcoutSignee;
			}

		}

		return traTraitement;
	}

	/**
	 * 
	 * @return
	 */
	public String getNumeroCommande() {
		String numeroCommande = null;

		numeroCommande = traTraitement().substring(
				NUMERO_COMMANDE.length(), traTraitement().indexOf(SEPARATEUR_NUMERO_COMMANDE));

		return numeroCommande;
	}

	/**
	 * 
	 * @return
	 */
	public String getNumeroCommandeSurcout() {
		String numeroCommandeSurcout = null;

		numeroCommandeSurcout = traTraitement().substring(
				traTraitement().indexOf(NUMERO_COMMANDE_SURCOUT) +
						NUMERO_COMMANDE_SURCOUT.length());

		return numeroCommandeSurcout;
	}
}
