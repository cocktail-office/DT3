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

import java.util.GregorianCalendar;

import org.cocktail.fwkcktlgrh.common.metier.EOContrat;
import org.cocktail.fwkcktljefyadmin.common.metier.EOUtilisateur;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSValidation;

public class EOIndividu extends _EOIndividu {

	public EOIndividu() {
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

	// ajouts

	/**
	 * Retourne l'utlOrdre d'un individu.
	 * 
	 * @return null si non trouvé
	 */
	public static Integer utlOrdreForPersId(EOEditingContext ec, Integer persId) {
		Integer utlOrdre = null;

		try {
			EOUtilisateur utilisateur = EOUtilisateur.fetchByKeyValue(ec, EOUtilisateur.PERS_ID_KEY, persId);
			utlOrdre = new Integer(((Number) EOUtilities.primaryKeyForObject(ec, utilisateur).objectForKey(EOUtilisateur.UTL_ORDRE_KEY)).intValue());
		} catch (Exception e) {
			// TODO: handle exception
		}

		return utlOrdre;
	}

	/**
	 * Les contrats non annulés de l'agent
	 * 
	 * @return
	 */
	public NSArray<EOContrat> tosFwkgrh_ContratValide() {
		return tosFwkgrh_Contrat(CktlDataBus.newCondition(
					EOContrat.TEM_ANNULATION_KEY + "='N'"));
	}

	/**
	 * Les contrats inclus dans une période
	 * 
	 * @param dDebut
	 * @param dFin
	 * @return
	 */
	public NSArray<EOContrat> tosFwkgrh_ContratValidePendantPeriodeUniv(NSTimestamp dDebut, NSTimestamp dFin) {
		NSArray<EOContrat> contrats = tosFwkgrh_ContratValide();

		// reculer le debut au début d'année universitaire
		NSTimestamp dDebAnneeUniv = dateToDebutAnneeUniv(dDebut);
		NSTimestamp dFinAnneeUniv = dateToFinAnneeUniv(dFin);

		EOQualifier qual = CktlDataBus.newCondition(
					"(" + EOContrat.D_DEB_CONTRAT_TRAV_KEY + ">=%@ and " + EOContrat.D_DEB_CONTRAT_TRAV_KEY + " <=%@) or " +
							"(" + EOContrat.D_FIN_CONTRAT_TRAV_KEY + ">=%@ and " + EOContrat.D_FIN_CONTRAT_TRAV_KEY + " <=%@) or " +
							"(" + EOContrat.D_DEB_CONTRAT_TRAV_KEY + "<=%@ and " + EOContrat.D_FIN_CONTRAT_TRAV_KEY + " >=%@)",
					new NSArray<NSTimestamp>(new NSTimestamp[] {
							dDebAnneeUniv, dFinAnneeUniv, dDebAnneeUniv, dFinAnneeUniv, dDebAnneeUniv, dFinAnneeUniv }));

		contrats = EOQualifier.filteredArrayWithQualifier(contrats, qual);

		return contrats;
	}

	private final static String JOUR_MOIS_DEBUT_ANNEE_UNIV = "01/09";

	/**
	 * XXX a mettre en FWK retourne le debut de l'annee universitaire d'une date
	 * ex : 02/02/2005 -> 01/09/2004
	 * 
	 * @param uneDate
	 * @return
	 */
	private static NSTimestamp dateToDebutAnneeUniv(NSTimestamp uneDate) {
		// on transltate la date sur l'annee civile : 01/09/2004 -> 01/01/2004
		int jourDebutAnnee = Integer.valueOf(JOUR_MOIS_DEBUT_ANNEE_UNIV.substring(0, 2)).intValue();
		int moisDebutAnnee = Integer.valueOf(JOUR_MOIS_DEBUT_ANNEE_UNIV.substring(3, 5)).intValue();

		NSTimestamp uneDateDecalee = uneDate.timestampByAddingGregorianUnits(
						jourDebutAnnee - 1, -(moisDebutAnnee - 1), 0, 0, 0, 0);
		GregorianCalendar nowGC = new GregorianCalendar();
		nowGC.setTime(uneDateDecalee);
		NSTimestamp debutAnneeDecalee = uneDateDecalee.timestampByAddingGregorianUnits(
						0,
						-nowGC.get(GregorianCalendar.MONTH),
						-nowGC.get(GregorianCalendar.DAY_OF_MONTH) + 1,
						-nowGC.get(GregorianCalendar.HOUR_OF_DAY),
						-nowGC.get(GregorianCalendar.MINUTE),
						-nowGC.get(GregorianCalendar.SECOND)
				);
		NSTimestamp debutAnnee = debutAnneeDecalee.timestampByAddingGregorianUnits(
						0, (moisDebutAnnee - 1), 0, 0, 0, 0);
		return debutAnnee;
	}

	/**
	 * XXX a mettre en FWK retourne la fin de l'annee universitaire d'une date ex
	 * : 02/02/2005 -> 31/08/2005
	 * 
	 * @param uneDate
	 * @return
	 */
	private static NSTimestamp dateToFinAnneeUniv(NSTimestamp uneDate) {
		return dateToDebutAnneeUniv(uneDate).timestampByAddingGregorianUnits(1, 0, -1, 0, 0, 0);
	}

	/**
	 * Les logins associé a l'individu
	 * 
	 * @return
	 */
	public String comptes() {
		String strComptes = new String();

		for (int i = 0; i < tosCompte().count(); i++) {
			EOCompte compte = (EOCompte) tosCompte().objectAtIndex(i);
			strComptes += compte.cptLogin() + "(" + compte.cptVlan() + ")";
			if (i < tosCompte().count() - 1) {
				strComptes += ", ";
			}
		}

		return strComptes;
	}

	/**
	 * Nom
	 * 
	 * @author ctarade
	 */
	public String nomCourt() {
		String nomCourt = null;

		nomCourt = StringCtrl.formatName(prenom(), nomUsuel());

		return nomCourt;
	}
}
