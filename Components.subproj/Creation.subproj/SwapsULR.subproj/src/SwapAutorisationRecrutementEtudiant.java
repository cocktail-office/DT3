

import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.fwkcktlgrh.common.metier.EOContrat;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning._imports.DateCtrl;

/**
 * Vue pour les DT de demande d'autorisation de recrutement
 * d'un étudiant
 * 
 * @author ctarade
 */
public class SwapAutorisationRecrutementEtudiant
	extends SwapDefault {

	// etudiant concerne
  public Number noIndividuEtudiant;
  public EOIndividu eoIndividuEtudiant;
  public SelectPersonneListener etudiantListener;
  
  // 
  public NSTimestamp dateDebutContrat;
  public NSTimestamp dateFinContrat;
  
  //
  public Integer nombreMaxiVacationEstimePourPeriode;
  
  //
  public String lieuxExecutionDuTravail;
  
  //
  private final static String RECRUTEMENT_ACTIVITE_01 = "Accueil des étudiants";
  private final static String RECRUTEMENT_ACTIVITE_02 = "Assistance et accompagnement des étudiants handicapés";
  private final static String RECRUTEMENT_ACTIVITE_03 = "Tutorat";
  private final static String RECRUTEMENT_ACTIVITE_04 = "Soutien informatique et aide à l'utilisation des nouvelles technologies";
  private final static String RECRUTEMENT_ACTIVITE_05 = "Service d'appui aux personnels des bibliothèques";
  private final static String RECRUTEMENT_ACTIVITE_06 = "Animations culturelles, scientifiques, sportives et sociales";
  private final static String RECRUTEMENT_ACTIVITE_07 = "Aide à l'insertion professionnelle";
  private final static String RECRUTEMENT_ACTIVITE_08 = "Promotion de l'offre de formation";
  public NSArray<String> recrutementActiviteList = new NSArray<String>(new String[] {
  		RECRUTEMENT_ACTIVITE_01, RECRUTEMENT_ACTIVITE_02, RECRUTEMENT_ACTIVITE_03, RECRUTEMENT_ACTIVITE_04, 
  		RECRUTEMENT_ACTIVITE_05, RECRUTEMENT_ACTIVITE_06, RECRUTEMENT_ACTIVITE_07, RECRUTEMENT_ACTIVITE_08});
  public NSArray<String> recrutementActiviteSelecteds = new NSArray<String>();

  // 
  public String complement;
  
  // responsable du recrutement
  public Number noIndividuResponsableRecrutement;
  public EOIndividu eoIndividuResponsableRecrutement;
  public SelectPersonneListener responsableRecrutementListener;
  
  // directeur composante ou service
  public NSArray<EOIndividu> directeurList;
  public EOIndividu directeurItem;
  public EOIndividu directeurSelected;
  
  // erreurs
  public boolean errorEtudiantVide;
  public boolean errorResponsableRecrutementVide;
  public boolean errorDateDebutContratVide;
  public boolean errorDateFinContratVide;
  public boolean errorDateDebutFinContrat;
  public boolean errorNombreMaxiVacationEstimePourPeriodeVide;
  public boolean errorLieuxExecutionDuTravailVide;
  public boolean errorRecrutementActiviteSelectedsVide;
  public boolean errorLBud;


  
  public SwapAutorisationRecrutementEtudiant(WOContext context) {
		super(context);
	}
  
  public boolean hasErrors() {
    return super.hasErrors() || 
    	errorEtudiantVide || 
    	errorResponsableRecrutementVide || 
    	errorDateDebutContratVide || 
    	errorDateFinContratVide || 
    	errorDateDebutFinContrat ||
    	errorNombreMaxiVacationEstimePourPeriodeVide || 
    	errorLieuxExecutionDuTravailVide || 
    	errorRecrutementActiviteSelectedsVide || 
    	errorLBud;
  }

  public void initView() {
    super.initView();
    
    // effacer la selection de la ligne pour les forcer a la choisir
    clearLigneBud();
    
    // pas d'etudiant par defaut
    noIndividuEtudiant = null;
    eoIndividuEtudiant = null;
    
    // 0 heures
    nombreMaxiVacationEstimePourPeriode = new Integer(0);
    
    // par defaut, le recruteur est la personne connectee
    noIndividuResponsableRecrutement = dtUserInfo().noIndividu();
    eoIndividuResponsableRecrutement = (EOIndividu) individuBus().individuForNoIndividu(noIndividuResponsableRecrutement);
    
    // dates par defaut
    dateDebutContrat = DateCtrl.now();
    dateFinContrat = dateDebutContrat.timestampByAddingGregorianUnits(0, 3, 0, 0, 0, 0);
    
    // remplir la liste des directeurs
    directeurList = new NSArray<EOIndividu>();
    // via le contact
    EOContact contact = (EOContact) parentPage().recContactDemandeur();
    if (contact != null && contact.toService() != null) {
    	directeurList = contact.toService().individuResponsableList();
    	// selection du premier par defaut
    	directeurSelected = directeurList.objectAtIndex(0);
    } else {
    	directeurList = new NSArray<EOIndividu>();
    	directeurSelected = null;
    }
    
  }
  
  public void clearViewErrors() {
    super.clearViewErrors();
    errorEtudiantVide = false;
    errorResponsableRecrutementVide = false;
    errorDateDebutContratVide = false;
    errorDateFinContratVide = false;
    errorDateDebutFinContrat = false;
    errorNombreMaxiVacationEstimePourPeriodeVide = false;
    errorLieuxExecutionDuTravailVide = false;
    errorRecrutementActiviteSelectedsVide = false;
    errorLBud = false;
  }
  
  public boolean fillDataDictionary() {
		
  	// pour que la validation fonctionne via super.fillDataDictionary(), il faut un motif "bidon"
		intMotif = "motif généré automatiquement";
		
    if (super.fillDataDictionary() == false) {
    	return false;
    }

    if (eoIndividuEtudiant == null) {
    	errorEtudiantVide = true;
    }

    if (eoIndividuResponsableRecrutement == null) {
    	errorResponsableRecrutementVide = true;
    }

    if (dateDebutContrat == null) {
    	errorDateDebutContratVide = true;
    }

    if (dateFinContrat == null) {
    	errorDateFinContratVide = true;
    }
    
    if (!errorDateDebutContratVide && !errorDateFinContratVide &&
    		DateCtrl.isAfter(dateDebutContrat, dateFinContrat)) {
    	errorDateDebutFinContrat = true;
    }

    if (nombreMaxiVacationEstimePourPeriode == null) {
    	errorNombreMaxiVacationEstimePourPeriodeVide = true;
    }

    if (StringCtrl.isEmpty(lieuxExecutionDuTravail)) {
    	errorLieuxExecutionDuTravailVide = true;
    }

    if (NSArrayCtrl.isEmpty(recrutementActiviteSelecteds)) {
    	errorRecrutementActiviteSelectedsVide = true;
    }

    if (newDtOrgId() == null) {
    	errorLBud = true;
    }
    
    if (hasErrors()) {
    	return false;
    } else {

    	String dicoIntMotif = "";
      
      dicoIntMotif += "Nom / prénom(s) de l'étudiant(e) : " + eoIndividuEtudiant.nomEtPrenom() + "\n";

      String strDNaissance = "non renseignée";
      if (eoIndividuEtudiant.dNaissance() != null) {
      	strDNaissance = DateCtrl.dateToString(eoIndividuEtudiant.dNaissance());
      }
      dicoIntMotif += "Date de naissance                : " + strDNaissance + "\n";
    
      String strNationalite = "non renseignée";
      if (eoIndividuEtudiant.toFwkpers_Pays() != null) {
      	strNationalite = eoIndividuEtudiant.toFwkpers_Pays().lNationalite();
      }
      dicoIntMotif += "Nationalité                      : " + strNationalite + "\n\n";
      
      dicoIntMotif += "Date de début du contrat : " + DateCtrl.dateToString(dateDebutContrat) + "\n";
      dicoIntMotif += "Date de fin du contrat   : " + DateCtrl.dateToString(dateFinContrat) + "\n\n";
      
      
      NSArray<EOContrat> contratsValides = eoIndividuEtudiant.tosFwkgrh_ContratValide();
      String strDejaSousContrat = "NON";
      if (!NSArrayCtrl.isEmpty(contratsValides)) {
      	strDejaSousContrat = "OUI";
      }
      dicoIntMotif += "L'étudiant a-t-il déjà été sous contrat de travail avec l'université de La Rochelle : " + strDejaSousContrat + "\n\n";
      
      NSArray<EOContrat> contratsValidesPeriode = eoIndividuEtudiant.tosFwkgrh_ContratValidePendantPeriodeUniv(dateDebutContrat, dateFinContrat);
      String strBeneficiaireContratPeriode = "NON";
      if (!NSArrayCtrl.isEmpty(contratsValidesPeriode)) {
      	strBeneficiaireContratPeriode = "OUI";
      }
      
      dicoIntMotif += "Est-il bénéficiaire d'un autre contrat de travail avec l'université de La Rochelle " +
      		"durant la ou les année(s) universitaire(s) de la période indiquée : " + strBeneficiaireContratPeriode + "\n\n";

      dicoIntMotif += "Nombre maxi de vacations estimé pour la période : " + Integer.toString(nombreMaxiVacationEstimePourPeriode.intValue()) + "h00\n\n";
      
      dicoIntMotif += "Imputation budgétaire : " + getLigneBudDescription() + "\n\n";
      
      dicoIntMotif += "Lieu(x) d'exécution du travail : " + lieuxExecutionDuTravail + "\n\n";

      String strRecrutementActivite = "";
      for (String s : recrutementActiviteSelecteds) {
      	strRecrutementActivite += "  - " + s + "\n";
      }
      dicoIntMotif += "Activité exercée par l'étudiant :\n" + strRecrutementActivite + "\n";
      
      dicoIntMotif += "Responsable du recrutement               : " + eoIndividuResponsableRecrutement.nomEtPrenom() + "\n";

      if (directeurSelected != null) {
        dicoIntMotif += "Directeur de la composante ou du service : " + directeurSelected.nomEtPrenom() + "\n";
      }
      
      dicoIntMotif += "\n";
      
      if (!StringCtrl.isEmpty(complement)) {
        dicoIntMotif += "Complément d'information :\n" + complement;
      }
      
      saveDataDico.setObjectForKey(dicoIntMotif, "intMotif");   
      
      return true;
    }
    
  }

  
  /* -- ETUDIANT CONCERNE -- */
  
  public WOComponent changeEtudiant() {
    // [LRAppTasks] : @CktlLog.trace(@"demandeurListener : "+demandeurListener());
    return SelectPersonne.getNewPage(etudiantListener(),
        "Indiquez l'&eacute;tudiant concern&eacute;",
        noIndividuEtudiant, true);
  }
  
  /**
   * Retourne une instance de la classe qui permet de gerer les evenements de
   * la selection des personnes.
   */
  protected SelectPersonneListener etudiantListener() {
    if (etudiantListener == null)
    	etudiantListener = new ChangeEtudiantListener();
    return etudiantListener;
  }
  
  private class ChangeEtudiantListener implements SelectPersonneListener {
    public WOComponent select(Number persId) {
    	eoIndividuEtudiant = (EOIndividu) individuBus().individuForPersId(persId);
      if (eoIndividuEtudiant != null) {
      	noIndividuEtudiant = eoIndividuEtudiant.noIndividu();
      }     
      return parentPage();
    }

    public WOComponent cancel() {
      return parentPage();
    }

    public WOContext getContext() {
      return context();
    }
  }
  
  /* -- RESPONSABLE RECRUTEMENT -- */
  
  public WOComponent changeResponsableRecrutement() {
    // [LRAppTasks] : @CktlLog.trace(@"demandeurListener : "+demandeurListener());
    return SelectPersonne.getNewPage(responsableRecrutementListener(),
        "Indiquez le responsable du recrutement",
        noIndividuResponsableRecrutement, true);
  }
  
  /**
   * Retourne une instance de la classe qui permet de gerer les evenements de
   * la selection des personnes.
   */
  protected SelectPersonneListener responsableRecrutementListener() {
    if (responsableRecrutementListener == null)
    	responsableRecrutementListener = new ChangeResponsableRecrutementListener();
    return responsableRecrutementListener;
  }
  
  private class ChangeResponsableRecrutementListener implements SelectPersonneListener {
    public WOComponent select(Number persId) {
    	eoIndividuResponsableRecrutement = (EOIndividu) individuBus().individuForPersId(persId);
      if (eoIndividuResponsableRecrutement != null) {
      	noIndividuResponsableRecrutement = eoIndividuResponsableRecrutement.noIndividu();
      }     
      return parentPage();
    }

    public WOComponent cancel() {
      return parentPage();
    }

    public WOContext getContext() {
      return context();
    }
  }
  
  
  /* -- LIGNE BUDGETAIRE -- */

  /*
   * @see A_CreationSwapView#selectLigneBud()
   */
  public WOComponent selectLigneBud() {
    clearViewErrors();
    SelectLigneBud selectPage = (SelectLigneBud) super.selectLigneBud();
    selectPage.resetPage();
    // on affiche toutes les lignes budgetaires
    selectPage.setTypeCreditAutorises(null);
    return selectPage;
  }
  
  
  // -- LES BUS DE DONNEES -- 
  
  private DTIndividuBus individuBus() {
    return dtSession().dataCenter().individuBus();
  }

	/* (non-Javadoc)
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_AUT_RECR_ETUDIANT_ID;
	}
}