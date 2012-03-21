

import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableArray;


/**
 * Ecran de demande d'acces aux agendas des étudiants
 * pour la cellule handicap
 * 
 * @author ctarade
 *
 */
public class SwapSupportHandicap 
	extends SwapDefault {

	// identité du suiveur
	public String nom;
	public String prenom;
	
	// liste des etudiants suivis
	public NSMutableArray<EOIndividu> etudiantArray;
	public EOIndividu etudiantItem;
	
	// etudiant concerne
  public Number noIndividuEtudiant;
  public EOIndividu eoIndividuEtudiant;
  public SelectPersonneListener etudiantListener;
  
  // complement
  public String complement;
	
  // erreur
  public boolean errorNom;
  public boolean errorPrenom;
  public boolean errorEtudiantArrayEmpty;
  
	public SwapSupportHandicap(WOContext context) {
		super(context);
	}
	
	public void initView() {
		super.initView();
		
		// raz nom / prenom
		nom = null;
		prenom = null;

    // pas d'etudiant par defaut
		etudiantArray = new NSMutableArray<EOIndividu>();
    noIndividuEtudiant = null;
    eoIndividuEtudiant = null;
    etudiantListener = null;
	}    
  
  public void clearViewErrors() {
    super.clearViewErrors();
    errorNom = false;
    errorPrenom = false;
    errorEtudiantArrayEmpty = false;
  }
  
  /**
   * 
   */
  public boolean hasErrors() {
    return 
    	super.hasMainErrors() ||
    	errorNom || 
    	errorPrenom || 
    	errorEtudiantArrayEmpty;
  }
	
  /**
   * Supprimer un etudiant 
   */
  public WOComponent removeEtudiantItem() {
  	etudiantArray.removeIdenticalObject(etudiantItem);

  	return parentPage();
  }
  
  /**
   * Ajouter un autre étudiant
   */
  public WOComponent addEtudiant() {
  	if (eoIndividuEtudiant != null) {
  		etudiantArray.addObject(eoIndividuEtudiant);
  		// raz de l'étudiant en cours
  		noIndividuEtudiant = null;
      eoIndividuEtudiant = null;
      etudiantListener = null;
  	}
  	
  	return parentPage();
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
  
	/**
	 * Classe de controle du composant de selection d'etudiant
	 * @author ctarade
	 */
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
  
 public boolean fillDataDictionary() {
		
  	// pour que la validation fonctionne via super.fillDataDictionary(), il faut un motif "bidon"
		intMotif = "motif généré automatiquement";
		
    if (super.fillDataDictionary() == false) {
    	return false;
    }

    if (StringCtrl.isEmpty(nom)) {
    	errorNom = true;
    }

    if (StringCtrl.isEmpty(prenom)) {
    	errorPrenom = true;
    }
    
    // ajouter l'étudiant selectionné au tableau s'il n'est pas dedans
    if (eoIndividuEtudiant != null &&
    		!etudiantArray.containsObject(eoIndividuEtudiant)) {
    	etudiantArray.addObject(eoIndividuEtudiant);
    }

    if (NSArrayCtrl.isEmpty(etudiantArray)) {
    	errorEtudiantArrayEmpty = true;
    }
    
    if (hasErrors()) {
    	return false;

    }else {
    
    	String dicoIntMotif = "";
      
    	dicoIntMotif += "Personne responsable :\n" + 
    		"  Nom :    " + nom.toUpperCase() + "\n" + "  Prénom : " + prenom.toUpperCase();
    	
    	dicoIntMotif += "\n\nEtudiant(s) suivi(s) : \n";

    	for (EOIndividu etudiant : etudiantArray) {
        dicoIntMotif += "  - " + etudiant.nomEtPrenom() + " - compte(s) " + etudiant.comptes()+ "\n";
    	}
    	      
      dicoIntMotif += "\n";
      
      if (!StringCtrl.isEmpty(complement)) {
        dicoIntMotif += "Complément d'information :\n" + complement;
      }
      
      saveDataDico.setObjectForKey(dicoIntMotif, "intMotif");   
      return true;
    }
    
  }
  
  // -- LES BUS DE DONNEES -- 
  
  private DTIndividuBus individuBus() {
    return dtSession().dataCenter().individuBus();
  }

	/* (non-Javadoc)
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_SUPPORT_HANDICAP_ID;
	}
}