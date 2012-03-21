import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

public class SwapStatistiques extends A_AdministrationSwapView {

	public EOVActivites eoVActivites;
	public NSTimestamp dDebut;
	public NSTimestamp dFin;

	private final static int DEFAULT_NB_JOUR = 365;

	public StatActiviteListener activiteListener = new StatActiviteListener();

	public SwapStatistiques(WOContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_AdministrationSwapView#initComponent()
	 */
	@Override
	protected void initComponent() {
		// TODO Auto-generated method stub
		initServiceList();
		setServiceSelected(serviceList.objectAtIndex(0));
		dDebut = DateCtrl.now().timestampByAddingGregorianUnits(0, 0, -DEFAULT_NB_JOUR, 0, 0, 0);
		dFin = DateCtrl.now();
	}

	// override setter
	public void setServiceSelected(EOStructure value) {
		EOStructure prevServiceSelected = serviceSelected;
		serviceSelected = value;
		if (serviceSelected != null && (
				prevServiceSelected == null || (prevServiceSelected != null &&
						!serviceSelected.cStructure().equals(prevServiceSelected.cStructure())))) {
			eoVActivites = EOVActivites.fetchByKeyValue(
					dtSession().defaultEditingContext(), EOVActivites.ACT_ORDRE_KEY, new Integer("-" + serviceSelected.cStructure()));
			// reinstancier le listener
			activiteListener = new StatActiviteListener();
		}
	}

	/**
	 * La classe listener de gestion du sous composant SelectActivite.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	public class StatActiviteListener extends SelectActiviteListener {

		/**
		 * Apres la selection d'une activite, on met a jour la swap view si besoin,
		 * et on efface les eventuels msg d'erreur.
		 */
		public void doAfterActiviteSelectedItem() {
			if (activiteListener.getActiviteSelectedItem() != null) {
				eoVActivites = activiteListener.getActiviteSelectedItem().nodeRecord;
			} else {
				// cas de la selection vide : on reprend la structure racine
				EOStructure bckServiceSelected = SwapStatistiques.this.serviceSelected;
				SwapStatistiques.this.setServiceSelected(null);
				SwapStatistiques.this.setServiceSelected(bckServiceSelected);
			}
		}

		protected CktlRecord recVActivite() {
			return eoVActivites;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return "formAdministration";
		}

		/**
		 * On remet l'ancre en haut apres une recherche
		 */
		public void doAfterSearchActivite() {

		}

		public WOComponent caller() {
			return null;
		}

		public boolean shouldSelectedOnlyLeaf() {
			return true;
		}

		public boolean showHiddenActivite() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, recVActivite().stringForKey("cStructure"));
		}

		/** selon le profil */
		public boolean showUnderscoredActivite() {
			return interventionBus().canViewActiviteUnderscore(recVActivite().stringForKey("cStructure"), dtUserInfo().noIndividu());
		}

		@Override
		public boolean showActivitesFavoritesDemandeur() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean showActivitesFavoritesIntervenant() {
			// TODO Auto-generated method stub
			return false;
		}

	}
}