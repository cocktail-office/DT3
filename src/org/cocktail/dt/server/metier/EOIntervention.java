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

import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.workflow.A_WorkFlow;
import org.cocktail.dt.server.metier.workflow.WorkFlowInstallationPosteComplet2;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSValidation;

import er.extensions.eof.ERXQ;

public class EOIntervention
		extends _EOIntervention {

	public EOIntervention() {
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

	/** les conditions à rajouter aux qualifier pour masquer les activites cachees */
	public final static String COND_HIDE_ACTIVITE = " and " + TO_ACTIVITES_KEY + "." + EOActivites.ACT_AFFICHABLE_KEY + "='O'";
	public final static String COND_HIDE_UNDERSCORE = " and not(" + TO_ACTIVITES_KEY + "." + EOActivites.ACT_LIBELLE_KEY + " isCaseInsensitiveLike '_*')";

	// commmande de poste complet v2
	public final static String MOTIF_POSTE_COMPLET_V2_PREFIX_PREMIER_POSTE = "Premier poste";
	public final static String MOTIF_POSTE_COMPLET_V2_PREFIX_REMPLACEMENT_POSTE = "Remplacement d'un poste existant";
	public final static String MOTIF_POSTE_COMPLET_V2_PREFIX_POSTE_SUPPLEMENTAIRE = "Poste supplémentaire";

	public final static String OUI = "OUI";
	public final static String NON = "NON";
	public final static String MOTIF_POSTE_COMPLET_V2_REALISER_LA_COMMANDE = "Réaliser la commande : ";

	// commande de matériel
	public final static String MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE = "Destinataire du matériel              :  	";
	public final static String MOTIF_COMMANDE_MATERIEL_SUFFIX_DESTINATAIRE = "\n";
	public final static String MOTIF_COMMANDE_MATERIEL_CLE_DESTINATAIRE = "DESTINATAIRE";
	public final static String MOTIF_COMMANDE_MATERIEL_DESTINATAIRE_INCONNU = "                                          (destinataire non enregistré dans le système d'information)\n";

	// tris
	public final static String TRI_ATTRIBUT_NUMERO = INT_CLE_SERVICE_KEY;
	public final static String TRI_ATTRIBUT_DATE = INT_DATE_CREATION_KEY;
	public final static String TRI_ATTRIBUT_DEMANDEUR = NOM_PRENOM_CONCERNE_KEY;
	public final static String TRI_ATTRIBUT_BATIMENT = TO_BATIMENT_KEY + "." + EOBatiment.APPELLATION_KEY;
	public final static String TRI_ATTRIBUT_ACTIVITE = TO_ACTIVITES_KEY + "." + EOActivites.ACT_LIBELLE_KEY;
	public final static String TRI_ATTRIBUT_SERVICE = EOIntervention.C_STRUCTURE_KEY;

	public final static String IS_COMMANDE_SURCOUT_SIGNEE_NON_KEY = "isCommandeSurcoutSigneeNon";

	public final static int MAX_FETCH_LIMIT_RECHERCHE = 2000;
	public final static int MAX_FETCH_LIMIT_STATS = 20000;

	/**
	 * Obtenir une instance de gestion du workflow pour un type de DT
	 * 
	 * @return
	 */
	public A_WorkFlow getWorkFlow() {
		A_WorkFlow workFlow = null;

		if (isInterventionInstallationPosteComplet2()) {
			workFlow = new WorkFlowInstallationPosteComplet2(this);
		}

		return workFlow;
	}

	//
	// ** gestion de la commande de matériel v2 ULR **
	//

	/**
	 * Indique si la DT est une installation de materiel (v1)
	 * 
	 * @return
	 */
	public boolean isInterventionInstallationMaterielv1() {
		boolean isIntervention = false;

		int actPref = toActivites().actPref();
		EOVActivites eoVActivites = EOVActivites.fetchFirstByKeyValue(
					editingContext(), EOVActivites.ACT_ORDRE_KEY, new Integer(actPref));
		int swapViewId = eoVActivites.actSwapView().intValue();
		if (swapViewId == I_Swap.SWAP_VIEW_INSTALL_COMPOSANT_ID ||
				swapViewId == I_Swap.SWAP_VIEW_INSTALL_MATERIEL_ID ||
				swapViewId == I_Swap.SWAP_VIEW_INSTALL_POSTE_COMPLET_ID) {
			isIntervention = true;
		}

		return isIntervention;
	}

	/**
	 * Indique si la DT est une installation de poste de complet (v2)
	 * 
	 * @return
	 */
	public boolean isInterventionInstallationPosteComplet2() {
		boolean isIntervention = false;

		/*
		 * int actPref = toActivites().actPref(); EOVActivites eoVActivites =
		 * EOVActivites.fetchFirstByKeyValue( editingContext(),
		 * EOVActivites.ACT_ORDRE_KEY, new Integer(actPref));
		 */

		EOVActivites eoVActivites = toActivites().toVActPref();

		if (eoVActivites != null) {
			if (eoVActivites.actSwapView() != null) {
				int swapViewId = eoVActivites.actSwapView().intValue();
				if (swapViewId == I_Swap.SWAP_VIEW_INSTALL_POSTE_COMPLET2_ID) {
					isIntervention = true;
				}
			}
		}

		return isIntervention;
	}

	/**
	 * Indique si la DT doit déclencher une commande
	 * 
	 * @return
	 */
	public boolean isCommandeARealiser() {
		boolean isCommandeARealiser = false;

		if (intMotif().contains(MOTIF_POSTE_COMPLET_V2_REALISER_LA_COMMANDE + OUI)) {
			isCommandeARealiser = true;
		}

		return isCommandeARealiser;
	}

	/**
	 * Indique si le demandeur a demandé un poste de remplacement
	 * 
	 * @return
	 */
	public boolean isPosteARemplacer() {
		boolean isPosteARemplacer = false;

		if (intMotif().contains(MOTIF_POSTE_COMPLET_V2_PREFIX_REMPLACEMENT_POSTE)) {
			isPosteARemplacer = true;
		}

		return isPosteARemplacer;
	}

	/**
	 * Indique si la DT doit déclencher une commande
	 * 
	 * @return
	 */
	public boolean isPosteSupplementaire() {
		boolean isPosteSupplementaire = false;

		if (intMotif().startsWith(MOTIF_POSTE_COMPLET_V2_PREFIX_POSTE_SUPPLEMENTAIRE)) {
			isPosteSupplementaire = true;
		}

		return isPosteSupplementaire;
	}

	/**
	 * Le nom du destinataire du poste dans une DT de commande de matériel (texte
	 * libre dans le motif)
	 * 
	 * @return
	 */
	public String getDestinatairePoste() {
		String destinatairePoste = "";

		if (StringCtrl.containsIgnoreCase(intMotif(), MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE)) {
			String debut = intMotif().substring(0, intMotif().indexOf(MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE)) + MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE;
			String fin = intMotif().substring(debut.length(), intMotif().length());
			String dest = fin.substring(0, fin.indexOf(MOTIF_COMMANDE_MATERIEL_SUFFIX_DESTINATAIRE));
			destinatairePoste = dest;
		} else {
			destinatairePoste = "..............";
		}

		return destinatairePoste;
	}

	/**
	 * 
	 * @param eoTraitementTypeArray
	 * @param ttyCode
	 * @return
	 */
	private NSArray<EOTraitementType> cacherType(NSArray<EOTraitementType> eoTraitementTypeArray, String ttyCode) {
		NSArray<EOTraitementType> array = null;

		array = EOQualifier.filteredArrayWithQualifier(eoTraitementTypeArray,
				ERXQ.notEquals(EOTraitementType.TTY_CODE_KEY, ttyCode));

		return array;
	}

	/**
	 * Obtenir tous les traitements du type passé en paramètre associé à la DT
	 * 
	 * @return
	 */
	public NSArray<EOTraitement> getEoTraitementArrayPourType(String type) {
		NSArray<EOTraitement> eoTraitementArray = null;

		eoTraitementArray = tosTraitement(
				ERXQ.equals(
						EOTraitement.TO_TRAITEMENT_TYPE_KEY + "." + EOTraitementType.TTY_CODE_KEY,
						type));

		return eoTraitementArray;
	}

	/**
	 * La liste des types de traitements potentiels. On fait le ménage selon la
	 * nature de la DT
	 * 
	 * @return
	 */
	public NSArray<EOTraitementType> getTraitementTypeArray() {
		NSArray<EOTraitementType> traitementTypeList = new NSMutableArray<EOTraitementType>(
				EOTraitementType.fetchAll(
						editingContext(), CktlSort.newSort(EOTraitementType.TTY_POSITION_KEY)));

		// on enleve le type prestation qui n'est pas accessible directement par
		// l'utilisateur
		traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_PRESTATION);

		// enlever les types commande / livraison / numéro de serie pour tout
		// ce qui n'est pas installation de matériel
		if (isInterventionInstallationMaterielv1() == false &&
				isInterventionInstallationPosteComplet2() == false) {
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_COMMANDE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_LIVRAISON);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_NO_SERIE);

		}

		// enlever les types commande de poste complet v2 s'il ne s'agit
		// pas d'une commande de poste complet v2
		if (isInterventionInstallationPosteComplet2() == false) {
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_INCOHERENCE_DEMANDE_NB_POSTE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_REMISE_POSTE_COMMANDE);
			traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_RESTITUTION_POSTE_SUITE_REMPLACEMENT);
		}

		// poste complet v2
		if (isInterventionInstallationMaterielv1()) {

			// on masque les types liés au surcout s'il n'y
			// a pas de commande a faire et qu'il ne s'agit pas d'un poste
			// supplémentaire
			if (isCommandeARealiser() == false &&
					isPosteSupplementaire() == false) {

				traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);
				traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
				traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);

			}
		}

		if (isInterventionInstallationPosteComplet2()) {

			// on peut noter une incohérence du contenu de la demande et le nombre de
			// poste réels tant le bon de commande interne n'a pas été envoyé
			boolean isBdcEmissionFaite = isTraitementPourTypeExiste(EOTraitementType.TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);

			if (isBdcEmissionFaite) {
				traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_INCOHERENCE_DEMANDE_NB_POSTE);
			}

			// pour un poste complet v2, on autorise 1 seul traitement de type
			// emission reception bdc
			if (isCommandeARealiser() ||
					isPosteSupplementaire()) {

				if (isBdcEmissionFaite) {
					// pas de doublon
					traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_EMISSION_BDC_INTERNE_POUR_SIGNATURE);
				} else {
					// on masque la reception si l'emission n'est pas encore faite
					traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
				}

				boolean isBdcReceptionFaite = isTraitementPourTypeExiste(EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);

				if (isBdcReceptionFaite) {
					// pas de doublon
					traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);
				}

			}

			// ne garder la cloture pour surcout non signé que pour le cas particulier
			if (isPosteSupplementaire()) {

				boolean isBdcReceptionFaite = isTraitementPourTypeExiste(EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);

				if (isBdcReceptionFaite == false) {
					traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);
				} else {
					// ne garder le surcout que si le bdc en retour n'a pas été signé
					EOTraitement eoTraitementReceptionBdc = getEoTraitementArrayPourType(
							EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE).objectAtIndex(0);

					if (eoTraitementReceptionBdc.isCommandeSurcoutSigneeOui()) {
						traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);
					}
				}

			} else {

				traitementTypeList = cacherType(traitementTypeList, EOTraitementType.TYPE_CODE_CLOTURE_SURCOUT_NON_SIGNE);

			}

		}

		return traitementTypeList;
	}

	/**
	 * Indique s'il existe un traitement du type passé en parametre
	 * 
	 * @return
	 */
	private boolean isTraitementPourTypeExiste(String ttyCode) {
		boolean isTraitementPourTypeExiste = false;

		NSArray<EOTraitement> eoTraitementArray = getEoTraitementArrayPourType(ttyCode);

		if (eoTraitementArray.count() > 0) {
			isTraitementPourTypeExiste = true;
		}

		return isTraitementPourTypeExiste;
	}

	//
	// ** gestion historique motif **
	//

	/**
	 * 
	 * @return
	 */
	public NSArray<EOHistoriqueMotif> tosHistoriqueMotifSorted() {
		NSArray<EOHistoriqueMotif> tosHistoriqueMotifSorted = tosHistoriqueMotif();

		tosHistoriqueMotifSorted = CktlSort.sortedArray(tosHistoriqueMotifSorted, EOHistoriqueMotif.D_CREATION_KEY, CktlSort.Descending);

		return tosHistoriqueMotifSorted;
	}

	//
	// ** affichage HTML **
	//

	private final static int MOTIF_MAX_SIZE = 150;

	/**
	 * Affiche le motif reduit de la DT, non formatte sans saut de ligne utilisé
	 * dans la liste des demandes
	 * 
	 * @return
	 */
	public String motifCourt() {
		String motif = null;

		motif = intMotif();
		// reduction a 150 car
		motif = StringCtrl.compactString(motif, MOTIF_MAX_SIZE, " <...>");

		return motif;
	}

	public final static String IS_INTERVENTION_INSTALLATION_POSTE_COMPLET2_SURCOUT_NON_SIGNE_KEY = "isInterventionInstallationPosteComplet2SurcoutNonSigne";

	/**
	 * La DT est une commande v2 ULR, mais le traitement de type reception bdc
	 * signé indique qu'il y a refus de payer le surcout.
	 * 
	 * @return
	 */
	public boolean isInterventionInstallationPosteComplet2SurcoutNonSigne() {
		boolean isInterventionInstallationPosteComplet2SurcoutNonSigne = false;

		if (isInterventionInstallationPosteComplet2()) {

			// trouver le traitement de type reception du bdc signé
			NSArray<EOTraitement> eoTraitementReceptionArray = getEoTraitementArrayPourType(
					EOTraitementType.TYPE_CODE_RECEPTION_BDC_INTERNE_SIGNE);

			if (eoTraitementReceptionArray.count() > 0) {
				EOTraitement eoTraitementReception = eoTraitementReceptionArray.objectAtIndex(0);
				if (eoTraitementReception.isCommandeSurcoutSigneeNon()) {
					isInterventionInstallationPosteComplet2SurcoutNonSigne = true;
				}
			}

		}

		return isInterventionInstallationPosteComplet2SurcoutNonSigne;
	}

	public final static String IS_INTERVENTION_INSTALLATION_POSTE_COMPLET2_REMPLACEMENT_NON_RESTITUE_KEY =
			"isInterventionInstallationPosteComplet2RemplacementNonRestitue";

	/**
	 * La DT est une commande v2 ULR pour un remplacement de poste, le poste a été
	 * livré mais l'ancien pas encore restitué
	 * 
	 * @return
	 */
	public boolean isInterventionInstallationPosteComplet2RemplacementNonRestitue() {
		boolean isInterventionInstallationPosteComplet2RemplacementNonRestitue = false;

		if (isInterventionInstallationPosteComplet2() &&
				isPosteARemplacer()) {

			// trouver le traitement de type remise
			NSArray<EOTraitement> eoTraitementRemiseArray = getEoTraitementArrayPourType(
					EOTraitementType.TYPE_CODE_REMISE_POSTE_COMMANDE);

			if (eoTraitementRemiseArray.count() > 0) {

				// trouver le traitement de type restitution
				NSArray<EOTraitement> eoTraitementRestitutionArray = getEoTraitementArrayPourType(
						EOTraitementType.TYPE_CODE_RESTITUTION_POSTE_SUITE_REMPLACEMENT);

				if (eoTraitementRestitutionArray.count() == 0) {
					isInterventionInstallationPosteComplet2RemplacementNonRestitue = true;
				}
			}

		}

		return isInterventionInstallationPosteComplet2RemplacementNonRestitue;
	}

	//
	// ** fichiers attachés **
	//

	/**
	 * Indique si la DT possède au moins 1 fichiers dans son motif traitements
	 * 
	 * @return
	 */
	public boolean hasFichierAttache() {
		boolean hasFichierAttache = false;

		if (tosDocumentDt().count() > 0) {
			hasFichierAttache = true;
		}

		return hasFichierAttache;
	}

	/**
	 * TODO Indique si la DT possède au moins 1 fichiers dans son motif et / ou
	 * ses traitements
	 * 
	 * @return
	 */
	public boolean hasFichierAttacheOuDansTraitement() {
		boolean hasFichierAttache = false;

		hasFichierAttache = hasFichierAttache();

		if (!hasFichierAttache) {
			// TODO
		}

		return hasFichierAttache;
	}

	//
	// ** masquer afficher les DTs pour son profil **
	//

	/**
	 * Indique si l'individu <code>noIndividu</code> a masque la demande
	 */
	public boolean isInterventionMasqueeForNoIndividu(Number noIndividu) {
		boolean isInterventionMasquee = false;

		NSArray<EOInterventionMasquee> eoInterventionMasqueeArrayForIndividu =
				tosInterventionMasquee(
					ERXQ.equals(EOInterventionMasquee.NO_INDIVIDU_KEY, noIndividu));

		if (eoInterventionMasqueeArrayForIndividu.count() > 0) {
			isInterventionMasquee = true;
		}

		return isInterventionMasquee;
	}

	/**
	 * Masquer une demande pour un individu
	 * 
	 * @param noIndividu
	 * @return
	 */
	public EOInterventionMasquee masquer(Number noIndividu) {
		EOInterventionMasquee eoInterventionMasquee = null;

		eoInterventionMasquee = EOInterventionMasquee.create(
				editingContext(), intOrdre(), new Integer(noIndividu.intValue()), this);

		return eoInterventionMasquee;
	}

	/**
	 * Supprime le masquage de l'intervention <code>intOrdre</code> a
	 * l'intervenant <code>noIndividu</code>. Retourne <i>true</i> si l' operation
	 * est effectue avec succes et <i>false</i> dans le cas contraire.
	 */
	public boolean demasquer(Number noIndividu) {
		boolean isSuppressionOk = false;

		NSArray<EOInterventionMasquee> aSupprimerArray = tosInterventionMasquee(
				ERXQ.equals(EOInterventionMasquee.NO_INDIVIDU_KEY, noIndividu));

		if (aSupprimerArray.count() > 0) {
			EOInterventionMasquee aSupprimer = aSupprimerArray.objectAtIndex(0);
			removeFromTosInterventionMasqueeRelationship(aSupprimer);
			editingContext().deleteObject(aSupprimer);
			isSuppressionOk = true;
		}

		return isSuppressionOk;
	}

	// ------- La gestion des intervenants -------//

	/**
	 * Savoir si la DT est affectee a l'individu <code>noIndividu</code> passe en
	 * parametre.
	 */
	public boolean isIntervenant(Number noIndividu) {
		boolean isIntervenant = false;

		if (tosIntervenant(
				ERXQ.equals(EOIntervenant.NO_INDIVIDU_KEY, noIndividu)).count() > 0) {
			isIntervenant = true;
		}

		return isIntervenant;
	}
}
