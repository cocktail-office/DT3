

import org.cocktail.dt.server.metier.EOVActivites;

import com.webobjects.appserver.WOContext;

/**
 * Composant de recherche et de selection d'une activité
 * pour un service donné.
 * 
 * @author ctarade
 */
public class AjaxSelectActivite 
	extends A_ComponentControled {
	
	// item dans la liste des resultats du composant CktlAjaxAutoComplete
	public EOVActivites activiteResultItem;
	
	public AjaxSelectActivite(WOContext context) {
		super(context);
	}

	/**
	 * cast du {@link A_ComponentControler} en {@link AjaxSelectActiviteCtrl}
	 * @return
	 */
	public AjaxSelectActiviteCtrl ctrl() {
		return (AjaxSelectActiviteCtrl) ctrl;
	}
}