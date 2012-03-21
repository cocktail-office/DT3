import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/**
 * Ecran rassemble toutes les DT mises dans le panier
 * 
 * @author ctarade
 * 
 */
public class PagePanier
		extends A_PageUsingListeDemande {

	public PagePanier(WOContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_PageUsingListeDemande#initDefaultConfig()
	 */
	@Override
	protected void initDefaultConfig() {
		listener = new ListenerPanier();
	}

	/**
	 * La classe utilisee pour la gestion de l'affichage des resultats.
	 */
	private class ListenerPanier
			implements I_CompListeDemandeListener {

		public A_PageUsingListeDemande caller() {
			return pagePanier();
		}

		// pas precis�
		public int modeUtilisateur() {
			return -1;
		}

		// colonne des intervenants : oui
		public boolean showColumnIntervenants() {
			return true;
		}

		// colonne validation : non
		public boolean showColumnValider() {
			return false;
		}

		// colonne affectaion : depend du niveau global
		public boolean showColumnAffecter() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT);
		}

		// colonne traitements : oui
		public boolean showColumnTraiter() {
			return true;
		}

		// TODO
		public boolean showColumnDiscussion() {
			return true;
		}

		// colonne ajout traitement : depend du niveau global
		public boolean showColumnAjoutTraitement() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);
		}

		// colonne etat : oui
		public boolean showColumnEtat() {
			return true;
		}

		// colonne suppression : depend du niveau global
		public boolean showColumnSupprimer() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);
		}

		public WODisplayGroup interventionDisplayGroup() {
			// TODO faire plus propre
			NSArray<EOIntervention> eoInterventionArray = dtSession().getEoInterventionArrayPanier();
			// classement
			eoInterventionArray = EOSortOrdering.sortedArrayUsingKeyOrderArray(eoInterventionArray, getSortForParams());
			getInterventionDisplayGroup().setObjectArray(eoInterventionArray);

			return getInterventionDisplayGroup();
		}

		public WOComponent doFetchDisplayGroup() {
			interventionDisplayGroup().setNumberOfObjectsPerBatch(getNumberOfObjectsPerBatch().intValue());
			interventionDisplayGroup().setSortOrderings(getSortForParams());
			return null;
		}

		public String warnMessage() {
			return null;
		}

		/**
		 * Page de gestion de masquage dans le panier
		 */
		public boolean useInterventionMasquee() {
			return false;
		}

		/**
		 * On affiche tout, independemment des masques
		 */
		public boolean showInterventionMasquee() {
			return true;
		}

		/**
		 * Pas d'information complementaires sur le nombre de DTs du panier
		 */
		public String strResultCommentSuffix() {
			return StringCtrl.emptyString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see I_CompListeDemandeListener#showColumnPanier()
		 */
		public boolean showColumnAjouterPanier() {
			// pas d'ajout / suppression dans la page du panier
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see I_CompListeDemandeListener#showColumnSupprimerPanier()
		 */
		public boolean showColumnSupprimerPanier() {
			return true;
		}
	}

	/**
	 * Pointeur vers cette propore page (utilise par la classe
	 * {@link ListenerPanier}
	 */
	private PagePanier pagePanier() {
		return this;
	}

}