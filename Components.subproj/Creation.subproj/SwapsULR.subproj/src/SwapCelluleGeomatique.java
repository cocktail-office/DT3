import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

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
 * Swap associé aux demandes de la cellule Geomatique (ULR)
 * 
 * @author ctarade
 */
public class SwapCelluleGeomatique
		extends SwapDefault {

	private final static String MOTIF_PREFIX_EQUIPE = "Equipe : ";

	private final static String EQUIPE_01 = "AGILE";
	private final static String EQUIPE_02 = "AMARE";
	private final static String EQUIPE_03 = "AMES (MAB/BIEN)";
	private final static String EQUIPE_04 = "DPL";
	private final static String EQUIPE_05 = "DYPHEA";
	private final static String EQUIPE_06 = "ESTRAN";

	private final static NSArray<String> EQUIPE_ARRAY = new NSArray<String>(new String[] {
			EQUIPE_01, EQUIPE_02, EQUIPE_03, EQUIPE_04, EQUIPE_05, EQUIPE_06 });

	public NSArray<String> equipeArray = new NSArray<String>(EQUIPE_ARRAY);
	public String equipeItem;
	public String equipeSelected;

	private final static String QUESTION_GEOMATIQUE = "Seriez vous prêt à partager ces données au sein du LIENSs et " +
			"de créer les métadonnées associées avec l'appui du Plateau Géomatique ?";
	public String questionGeomatique;
	public String reponseSelected;

	public boolean errReponseVide;

	/**
	 * 
	 */
	public SwapCelluleGeomatique(WOContext context) {
		super(context);
	}

	/**
	 * 
	 */
	public void initView() {
		super.initView();
		equipeArray = new NSArray<String>(EQUIPE_ARRAY);
		questionGeomatique = QUESTION_GEOMATIQUE;
		reponseSelected = null;
	}

	/**
	 * 
	 */
	public void clearViewErrors() {
		super.clearViewErrors();
		errReponseVide = false;
	}

	/**
   * 
   */
	public boolean hasErrors() {
		return super.hasMainErrors() ||
				errReponseVide;
	}

	public boolean fillDataDictionary() {

		if (super.fillDataDictionary() == false) {
			return false;
		}

		// réponse à la question
		if (reponseSelected == null) {
			setMainError("Veuillez répondre à la question relative aux métadonnées, s'il vous plaît");
			errReponseVide = true;
			return false;
		}

		// on precede le motif de la ligne budgetaire si besoin
		String dicoIntMotif = (String) saveDataDico.valueForKey("intMotif");
		StringBuffer suffix = new StringBuffer();

		String equipe = equipeSelected;
		if (StringCtrl.isEmpty(equipe)) {
			equipe = "<non renseignée>";
		}
		suffix.append(MOTIF_PREFIX_EQUIPE).append(equipe).append("\n\n");

		suffix.append(dicoIntMotif);

		boolean isOui = false;
		if (reponseSelected.equals(OUI)) {
			isOui = true;
		}

		suffix.append("\n\n").append(QUESTION_GEOMATIQUE).append(" ").append(isOui ? OUI : NON).append("\n");

		// ajoutage
		dicoIntMotif = suffix.toString();

		// on test si ca rentre encore
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOIntervention.ENTITY_NAME, EOIntervention.INT_MOTIF_KEY, dicoIntMotif, "Motif", 0, true, true)) {
			setMainError(dtSession().dtDataBus().getErrorMessage());
			return false;
		}

		if (!hasErrors()) {
			// hop on le remet
			saveDataDico.setObjectForKey(dicoIntMotif, "intMotif");
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_SUPPORT_HANDICAP_ID;
	}
}
