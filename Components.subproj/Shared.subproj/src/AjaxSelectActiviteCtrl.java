
import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;


/**
 * Controleur du composant {@link AjaxSelectActivite}
 * @author ctarade
 *
 */
public class AjaxSelectActiviteCtrl 
	extends A_ComponentControler {

	// le service DT concerné
	private EOGroupesDt groupesDt;
	
	// toutes les activites
	private NSArray<EOVActivites> allActivites;
	
	// la chaine recherchée
	public String actLibelleRecherche;
	
	// item
	public EOVActivites activiteResultItem;
	
	/**
	 * @deprecated
	 * @param session
	 */
	public AjaxSelectActiviteCtrl(Session session) {
		super(session);
	}
	
	/**
	 * @param session
	 * @param AjaxSelectActiviteCtrl
	 */
	public AjaxSelectActiviteCtrl(Session session, EOGroupesDt aGroupesDt) {
		super(session);
		groupesDt = aGroupesDt;
		initCtrl();
	}
	
	/**
	 * Initialisation des variables
	 */
	private void initCtrl() {
		// liste de toutes les activités du service
		allActivites = EOVActivites.fetchAll(
				ec(), 
				CktlDataBus.newCondition(
						EOVActivites.C_STRUCTURE_KEY + "='"+groupesDt.cStructure()+"'"),
				CktlSort.newSort(EOVActivites.ACT_LIBELLE_KEY));
	}
	
  /**
   * La liste des activités répondant à la chaine de recherche actLibelleRecherche.
   * Cette méthode est appelée à chaque saisie dans la zone de texte
   */
  public NSArray<EOVActivites> activiteResultList() {
  	NSMutableArray<EOVActivites> result = new NSMutableArray<EOVActivites>();
  	
  	// on ne fait la recherche que si l'utilisateur a tapé quelque chose
  	if (!StringCtrl.isEmpty(actLibelleRecherche)) {

  		// nettoyage de la chaine recherchee
  		String actLibelleRechercheClean = actLibelleRecherche;
  		actLibelleRechercheClean = StringCtrl.chaineSansAccents(actLibelleRechercheClean);
  		actLibelleRechercheClean = StringCtrl.toBasicString(actLibelleRechercheClean);
  		// virer les '_' qui ne font pas aboutir les recherches dès qu'il y a des accents
  		actLibelleRechercheClean = StringCtrl.replace(actLibelleRechercheClean, "_", "*");
  		
    	for(int i=0; i<allActivites.count() && result.count() < 10; i++) {
    		EOVActivites activite = allActivites.objectAtIndex(i);
    		if(activite.actLibelleClean().toLowerCase().indexOf(actLibelleRechercheClean.toLowerCase()) >= 0) {
    			result.addObject(activite);
    		}
    	}

  	}
  	
  	return result;
  }
  
  /**
   * Affichage d'un des résultats dans le composant de recherche.
   * On affiche le libellé et son arborsence entre parenthèses
   * @return
   */
  public String activiteResultItemDisplay() {
  	String display = activiteResultItem.actLibelle();
  	
  	String path = dtSession().dataCenter().activiteBus().findActivitePathString(
  			activiteResultItem.actOrdre(), false, "/");
  	
  	if (!StringCtrl.isEmpty(path)) {
    	display += " [ /" + path + " ]";
  	}
  	
  	
  	return display;
  }
}
