
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Interface de création d'une DT liée à l'activité
 * d'un référent fonctionnel
 * 
 * @author ctarade
 *
 */
public class SwapReferentFonctionnel 
	extends SwapDefault {
	
	// liste des groupes de travail
	public NSArray groupeTravailList;
	public NSArray groupeTravailSelectedList;
	public String groupeTravail;
	
	private final static String GROUPE_TRAVAIL_01 = "Gestion Financière et Comptable";
	private final static String GROUPE_TRAVAIL_02 = "Gestion des Ressources Humaines - Paie";
	private final static String GROUPE_TRAVAIL_03 = "Gestion des rôles et autorisations";
	private final static String GROUPE_TRAVAIL_04 = "Infocentre & pilotage";
	private final static String GROUPE_TRAVAIL_05 = "Patrimoine";
	private final static String GROUPE_TRAVAIL_06 = "Recherche";
	private final static String GROUPE_TRAVAIL_07 = "Référentiel";
	private final static String GROUPE_TRAVAIL_08 = "Scolarité";
	private final static String GROUPE_TRAVAIL_09 = "Technologie Cocktail";
	private final static String GROUPE_TRAVAIL_10 = "Autres ...";
	
	// liste des natures de proposition
	public NSArray naturePropositionList;
	public NSArray naturePropositionSelectedList;
	public String natureProposition;
	
	private final static String NATURE_PROPOSITION_01 = "Réunion de groupe de travail (rédaction du compte rendu, relevé de conclusions, cahier des charges, ...)";
	private final static String NATURE_PROPOSITION_02 = "Etude de dossier, de textes, de cahier des charges";
	private final static String NATURE_PROPOSITION_03 = "Réalisation de documentations";
	private final static String NATURE_PROPOSITION_04 = "Réalisation de cahiers des charges";
	private final static String NATURE_PROPOSITION_05 = "Représentation Cocktail";
	private final static String NATURE_PROPOSITION_06 = "Expertises";
	private final static String NATURE_PROPOSITION_07 = "Autres ...";

	// détail de la proposition
	public String detailProposition;
	
	// dates de la proposition
	public String datesProposition;
	
  // erreurs
	public boolean errorGroupeTravail;
	public boolean errorNatureProposition;
	public boolean errorDetailProposition;
  

	public SwapReferentFonctionnel(WOContext context) {
		super(context);
		initView();
	}
	
  /**
   * RAZ des témoins d'erreur
   */
  public void clearViewErrors() {
    super.clearViewErrors();
    errorGroupeTravail = errorNatureProposition = errorDetailProposition = false;
  }

  /**
   * 
   */
  public boolean hasErrors() {
    return super.hasErrors() || errorGroupeTravail || errorNatureProposition || errorDetailProposition;
  }
	
	/**
	 * Initialiser l'objet
	 */
	public void initView() {
		super.initView();
		//
		groupeTravailList = new NSArray(new String[] {
				GROUPE_TRAVAIL_01,
				GROUPE_TRAVAIL_02,
				GROUPE_TRAVAIL_03,
				GROUPE_TRAVAIL_04,
				GROUPE_TRAVAIL_05,
				GROUPE_TRAVAIL_06,
				GROUPE_TRAVAIL_07,
				GROUPE_TRAVAIL_08,
				GROUPE_TRAVAIL_09,
				GROUPE_TRAVAIL_10});
		//
		naturePropositionList = new NSArray(new String[] {
				NATURE_PROPOSITION_01,
				NATURE_PROPOSITION_02,
				NATURE_PROPOSITION_03,
				NATURE_PROPOSITION_04,
				NATURE_PROPOSITION_05,
				NATURE_PROPOSITION_06,
				NATURE_PROPOSITION_07});
	}
	
	/**
	 * 
	 */
	public boolean fillDataDictionary() {
		// pour que la validation fonctionne via super.fillDataDictionary(), il faut un motif "bidon"
		intMotif = "motif généré automatiquement";
		
		if (super.fillDataDictionary() == false) {
			return false;
		}

		// on s'assure qu'il y a au moins un groupe de travail
		if (NSArrayCtrl.isEmpty(groupeTravailSelectedList)) {
			errorGroupeTravail = true;
			return false;
		}
			
		// on s'assure qu'il y a au moins une nature de proposition
		if (NSArrayCtrl.isEmpty(naturePropositionSelectedList)) {
			errorNatureProposition = true;
			return false;
		}
			
		// on s'assure que le détail est saisi
		if (StringCtrl.isEmpty(detailProposition)) {
			errorDetailProposition = true;
			return false;
		}
			
		String intMotif = "";
		
		// 
		intMotif += "Groupe de travail:\n";
		for (int i = 0; i < groupeTravailSelectedList.count(); i++) {
			String groupeTravail = (String) groupeTravailSelectedList.objectAtIndex(i);
			intMotif += "- " + groupeTravail + "\n";
			if (i == groupeTravailSelectedList.count() -1) {
				intMotif += "\n";
			}
		}
	    
		intMotif += "Nature de la proposition:\n";
		for (int i = 0; i < naturePropositionSelectedList.count(); i++) {
			String natureProposition = (String) naturePropositionSelectedList.objectAtIndex(i);
			intMotif += "- " + natureProposition + "\n";
			if (i == naturePropositionSelectedList.count() -1) {
				intMotif += "\n";
			}
		}
	    
		intMotif += "Détail de la proposition:\n";
		intMotif += detailProposition;

		if (!StringCtrl.isEmpty(datesProposition)) {
			intMotif += "\n\nDates envisagées:\n" + datesProposition;
		}
		
		// on test si ca rentre 
		if (!dtSession().dtDataBus().checkForMaxSize(
				"Intervention", "intMotif", intMotif, "Motif", 0, true, true)) {
			setMainError(dtSession().dtDataBus().getErrorMessage());
			return false;
		}
	    
		if (!hasErrors()) {
			// hop on le remet
			saveDataDico.setObjectForKey(intMotif, "intMotif");   
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_REFERENT_FONCTIONNEL_ID;
	}
	  
}