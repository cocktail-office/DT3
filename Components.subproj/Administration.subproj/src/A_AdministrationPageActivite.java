import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOVActivites;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Methodes communes aux sous page de gestion des activites / services (appel
 * depuis l'ecran de gestion des activites)
 * 
 * @author ctarade
 */

public abstract class A_AdministrationPageActivite
		extends A_AdministrationSwapView {

	// la liste des SwapView
	public NSArray<String> swapList;
	public String swapItem;
	public String swapSelected;

	/** etat d'utilisation du composant */
	public int curMode;
	public final static int MODE_AJOUT = 0;
	public final static int MODE_MODIF = 1;
	public final static int MODE_VIEW = 2;

	/** reference vers la page appelante */
	protected SwapActivites swapActiviteCaller;

	public A_AdministrationPageActivite(WOContext context) {
		super(context);
	}

	protected void initComponent() {
		swapList = new NSArray<String>(I_Swap.SWAP_LIST);
	}

	/** Valider les modifications */
	public abstract WOComponent ok();

	/** Annuler les modifications */
	public abstract WOComponent annuler();

	/** methode de validation du formulaire */
	protected abstract boolean valider();

	/**
	 * Utiliser le composant pour une creartion
	 * 
	 * @param caller
	 *          : la page appelante
	 * @param parentRecord
	 *          : le record pere (si necessaire)
	 */
	public abstract void initForAjout(SwapActivites caller, EOVActivites parentRecord);

	/**
	 * Utiliser le composant pour une modification
	 * 
	 * @param record
	 *          : l'enregistrement a modifier
	 * @param parentRecord
	 *          : le record pere (si necessaire)
	 */
	public abstract void initForModif(EOVActivites record, EOVActivites parentRecord);

}
