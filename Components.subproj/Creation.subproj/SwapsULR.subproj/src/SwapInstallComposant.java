import org.cocktail.dt.server.metier.EOIntervention;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class SwapInstallComposant
		extends SwapDefault {

	// commande oui / non
	private String shouldCommande;
	public final String SHOULD_COMMANDE_OUI = EOIntervention.OUI;
	public final String SHOULD_COMMANDE_NON = EOIntervention.NON;

	// erreurs
	public boolean errorLBud;
	/*
	 * // bindings du CktlAjaxOrganSelect public EOOrgan organSelected; public
	 * Integer exeOrdreCurrent; public Integer utlOrdreConnected; public String
	 * typeLbudDepense = FinderOrgan.ORGAN_DEPENSE;
	 */

	private DestinationCommandeCtrl destinationCommandeCtrl;

	public final static String ANCHOR_DETAILS = "\nDétails : ";

	public class DestinationCommandeCtrl
			extends A_ListeDestinationLolfCtrl {

		/**
		 * @param aSession
		 */
		public DestinationCommandeCtrl(Session aSession) {
			super(aSession);
		}

		/*
		 * @see A_ListeDestinationLolfCtrl#fillAllDestinOnReset()
		 */
		@Override
		public boolean fillAllDestinOnReset() {
			return true;
		}

		/*
		 * @see A_ListeDestinationLolfCtrl#shouldInitDestinListRepro()
		 */
		@Override
		public boolean shouldInitDestinListRepro() {
			return false;
		}

		/*
		 * @see A_DTWebComponentCtrl#setMainError(java.lang.String)
		 */
		@Override
		public void setMainError(String message) {
			SwapInstallComposant.this.setMainError(message);
		}
	}

	public SwapInstallComposant(WOContext context) {
		super(context);
	}

	public boolean hasErrors() {
		return super.hasErrors() || errorLBud;
	}

	public void initView() {
		super.initView();
		// par defaut, on fait une commande
		setShouldCommande(SHOULD_COMMANDE_OUI);
		// reinstancier le controleur du footer
		setDestinationCommandeCtrl(new DestinationCommandeCtrl(dtSession()));
		// effacer la selection de la ligne pour les forcer a la choisir
		clearLigneBud();
	}

	/*
	 * 
	 * private void initLbud() { exeOrdreCurrent = new
	 * Integer(GFCUtilities.getExerciceOuvert
	 * (dtSession().defaultEditingContext()).exeExercice().intValue());
	 * GFCApplicationUser au = new
	 * GFCApplicationUser(dtSession().defaultEditingContext(), new
	 * Integer(dtUserInfo().persId().intValue())); utlOrdreConnected =
	 * au.getUtilisateur().utlOrdre(); }
	 */
	public boolean isCommande() {
		return getShouldCommande().equals(SHOULD_COMMANDE_OUI);
	}

	public void clearViewErrors() {
		super.clearViewErrors();
		errorLBud = false;
	}

	public boolean fillDataDictionary() {

		if (super.fillDataDictionary() == false) {
			return false;
		}

		// on oblige la saisie d'une ligne bugdetaire s'il faut faire une commande
		errorLBud = (isCommande() && newDtOrgId() == null);

		if (errorLBud) {
			setMainError("La ligne budgétaire pour la commande n'est pas renseignée");
			return false;
		}

		// on precede le motif de la ligne budgetaire si besoin
		String dicoIntMotif = (String) saveDataDico.valueForKey("intMotif");
		StringBuffer prefix = new StringBuffer();
		prefix.append(EOIntervention.MOTIF_POSTE_COMPLET_V2_REALISER_LA_COMMANDE).append((isCommande() ? OUI : NON)).append("\n");
		if (isCommande()) {
			prefix.append("Ligne budgétaire à imputer pour la commande :     ").append(getLigneBudDescription()).append("\n");
			Number lolfId = getDestinationCommandeCtrl().lolfId();
			if (lolfId != null) {
				prefix.append("Action Lolf :     ").append(jefyBus().libelleDestinationInfin(lolfId)).append("\n");
			}

		}
		prefix.append(ANCHOR_DETAILS);

		// ajoutage
		dicoIntMotif = prefix.toString() + dicoIntMotif;

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

	/**
	 * Pour les commandes de matériel, on traite les informations financieres
	 */
	@Override
	public boolean shouldProcessDataInfin() {
		return true;
	}

	public final String getShouldCommande() {
		return shouldCommande;
	}

	public final void setShouldCommande(String value) {
		shouldCommande = value;
		// en cas de non commande, on RAZ la ligne budgetaire et la destination
		if (shouldCommande.equals(NON)) {
			clearLigneBud();
		}
	}

	/**
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_INSTALL_COMPOSANT_ID;
	}

	private final static String CONTAINER_COMMANDE_ID = "ContainerCommande";

	public String getContainerCommandeId() {
		return CONTAINER_COMMANDE_ID;
	}

	public final DestinationCommandeCtrl getDestinationCommandeCtrl() {
		return destinationCommandeCtrl;
	}

	public final void setDestinationCommandeCtrl(DestinationCommandeCtrl destinationCommandeCtrl) {
		this.destinationCommandeCtrl = destinationCommandeCtrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_CreationSwapView#getDestinationLolfCtrl()
	 */
	@Override
	public A_ListeDestinationLolfCtrl getDestinationLolfCtrl() {
		return getDestinationCommandeCtrl();
	}
}
