import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.ws.glpi.GLPIComputer;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.dt.app.I_ApplicationConsts;

/**
 * Controlleur de l'interface HTML d'installation de materiel. Composant mis à
 * jour pour intégrer le surcout lors de l'acquisition d'un second poste, sans
 * restitution de l'ancien
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class SwapInstallPosteComplet2
		extends SwapInstallMateriel {

	// demandeur

	// logiciels complementaires
	protected String logiciels;

	// remplacement poste existant oui / non
	private Integer commandeNature;
	public final Integer COMMANDE_NATURE_PREMIER_POSTE = new Integer(0);
	public final Integer COMMANDE_NATURE_POSTE_REMPLACEMENT = new Integer(1);
	public final Integer COMMANDE_NATURE_POSTE_SUPPLEMENTAIRE = new Integer(2);

	public boolean errorRemplacer;

	//
	public String surcoutPosteSupplementaire;

	//
	public NSArray<GLPIComputer> glpiComputerArray;

	//
	public boolean isDisabledRadioCommandeNaturePosteRemplacement;
	public boolean isDisabledRadioCommandeNaturePosteSupplementaire;

	// destination Lbud surcout
	private DestinationSurcoutCtrl destinationSurcoutCtrl;

	public class DestinationSurcoutCtrl
			extends A_ListeDestinationLolfCtrl {

		/**
		 * @param aSession
		 */
		public DestinationSurcoutCtrl(Session aSession) {
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
			SwapInstallPosteComplet2.this.setMainError(message);
		}
	}

	public SwapInstallPosteComplet2(WOContext context) {
		super(context);
	}

	public void initView() {
		super.initView();
		// par defaut, on se selectionne rien sur le remplacement
		setCommandeNature(null);
		// raz des remarques
		logiciels = StringCtrl.emptyString();
		//
		surcoutPosteSupplementaire = dtApp().config().stringForKey(
				I_ApplicationConsts.VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY);
		//
		setDestinationSurcoutCtrl(new DestinationSurcoutCtrl(dtSession()));
	}

	public boolean hasErrors() {
		return super.hasErrors() || errorRemplacer || errorLBudSurcout || hasMainErrors();
	}

	public boolean fillDataDictionary() {

		// on ne bloque pas pour avoir toutes les erreurs
		super.fillDataDictionary();

		// verifier que le choix de remplacement O/N est fait
		if (getCommandeNature() == null) {
			setMainError("Vous n'avez pas indiqué la nature de la remise du poste");
			errorRemplacer = true;
		}

		// la ligne budgetaire est obligatoire en cas de surcout
		if (isPosteSupplementaire() &&
				isLigneBudSurcoutSelected() == false) {
			setMainError("La ligne budgétaire pour le surcoût des " + surcoutPosteSupplementaire + " &euro; n'est pas renseignée");
			errorLBudSurcout = true;
		}

		if (hasErrors()) {
			return false;
		}

		// recup du dico fait juste avant
		String dicoIntMotif = (String) saveDataDico.valueForKey("intMotif");

		// on precede le motif par l'indication de remplacement O/N
		StringBuffer bufv2infos = new StringBuffer();
		if (getCommandeNature() == COMMANDE_NATURE_PREMIER_POSTE) {
			bufv2infos.append(EOIntervention.MOTIF_POSTE_COMPLET_V2_PREFIX_PREMIER_POSTE);
		} else if (getCommandeNature() == COMMANDE_NATURE_POSTE_REMPLACEMENT) {
			bufv2infos.append(EOIntervention.MOTIF_POSTE_COMPLET_V2_PREFIX_REMPLACEMENT_POSTE);
		} else {
			bufv2infos.append(EOIntervention.MOTIF_POSTE_COMPLET_V2_PREFIX_POSTE_SUPPLEMENTAIRE);
		}
		bufv2infos.append(".\n");

		// ligne budgetaire si surcout
		if (isPosteSupplementaire()) {
			bufv2infos.append("Ligne budgétaire pour la prise en charge du surcout des ");
			bufv2infos.append(surcoutPosteSupplementaire).append(" EUR :     ").append(getLigneBudSurcoutDescription()).append("\n");
			Number lolfId = getDestinationSurcoutCtrl().lolfId();
			if (lolfId != null) {
				bufv2infos.append("Action Lolf :     ").append(jefyBus().libelleDestinationInfin(lolfId)).append("\n");
			}
			bufv2infos.append("\n");
		}

		// postes existants
		if (!NSArrayCtrl.isEmpty(glpiComputerArray)) {
			bufv2infos.append("Machines détectées dans GLPI :\n");
			for (GLPIComputer glpiComputer : glpiComputerArray) {
				String name = (String) glpiComputer.valueForKey("name");
				String location = (String) glpiComputer.valueForKeyPath("toGLPIComputerLocation.completename");
				String fabricant = (String) glpiComputer.valueForKey("manufacturers_name");
				String modele = (String) glpiComputer.valueForKeyPath("toGLPIComputerModel.name");
				bufv2infos.append("  * ");
				if (!StringCtrl.isEmpty(name)) {
					bufv2infos.append(name);
				}
				if (!StringCtrl.isEmpty(location)) {
					bufv2infos.append(" (" + location + ") ");
				}
				if (!StringCtrl.isEmpty(fabricant)) {
					bufv2infos.append(" - " + fabricant);
				}
				if (!StringCtrl.isEmpty(modele)) {
					bufv2infos.append(" - " + modele);
				}
				bufv2infos.append("\n");
			}
			bufv2infos.append("\n\n");
		}

		if (isPremierPoste() && !NSArrayCtrl.isEmpty(glpiComputerArray)) {
			bufv2infos.append("ATTENTION : incohérence entre \"premier poste\" et la détection de machines pour le demandeur dans GLPI !\n\n");
		}

		// rajouter les eventuels logiciels a installer
		if (!StringCtrl.isEmpty(logiciels)) {
			StringBuffer bufLogiciels = new StringBuffer("");
			if (StringCtrl.normalize(logiciels).length() > 0) {
				bufLogiciels.append("\n\nLogiciels complémentaires à installer :\n");
				bufLogiciels.append(StringCtrl.quoteText(logiciels, "  "));
			}

			// faut mettre les logiciels avant les remarques
			if (StringCtrl.containsIgnoreCase(dicoIntMotif, ANCHOR_REMARQUES)) {
				String before = StringCtrl.cut(dicoIntMotif, dicoIntMotif.indexOf(ANCHOR_REMARQUES));
				String after = dicoIntMotif.substring(before.length());
				dicoIntMotif = before + bufLogiciels.toString() + after;
			} else if (StringCtrl.containsIgnoreCase(dicoIntMotif, ANCHOR_PROCEDURE)) {
				// si pas de remarques, avant la procedure
				String before = StringCtrl.cut(dicoIntMotif, dicoIntMotif.indexOf(ANCHOR_PROCEDURE));
				String after = dicoIntMotif.substring(before.length());
				dicoIntMotif = before + bufLogiciels.toString() + after;
			} else {
				// bon ben apres si ya rien ...
				dicoIntMotif = dicoIntMotif + bufLogiciels.toString();
			}
		}

		// les infos liés à la v2 juste avant le destinataire du poste
		if (StringCtrl.containsIgnoreCase(dicoIntMotif, EOIntervention.MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE)) {
			String before = StringCtrl.cut(dicoIntMotif, dicoIntMotif.indexOf(EOIntervention.MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE));
			String after = dicoIntMotif.substring(before.length());
			dicoIntMotif = before + bufv2infos.toString() + after;
		}

		// dicoIntMotif = dicoIntMotif + prefix.toString();

		// ca rentre ?
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOIntervention.ENTITY_NAME, EOIntervention.INT_MOTIF_KEY, dicoIntMotif,
				"Motif", 0, true,
				true)) {
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

	public void clearViewErrors() {
		super.clearViewErrors();
		errorRemplacer = false;
		errorLBudSurcout = false;
	}

	public final Integer getCommandeNature() {
		return commandeNature;
	}

	public final void setCommandeNature(Integer commandeNature) {
		this.commandeNature = commandeNature;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPosteSupplementaire() {
		boolean isPosteSupplementaire = false;

		if (getCommandeNature() == COMMANDE_NATURE_POSTE_SUPPLEMENTAIRE) {
			isPosteSupplementaire = true;
		}

		return isPosteSupplementaire;
	}

	private final static String CONTAINER_ID = "ContainerSwapInstallPosteComplet2";
	private final static String CONTAINER_POSTE_SUPPLEMENTAIRE_ID = "ContainerPosteSupplementaire";

	public String getContainerId() {
		return CONTAINER_ID;
	}

	public String getContainerPosteSupplementaire() {
		return CONTAINER_POSTE_SUPPLEMENTAIRE_ID;
	}

	// la ligne budgetaire pour le surcout

	private Number orgIdSurcout;
	private String tcdCodeSurcout;
	public boolean errorLBudSurcout;

	/**
	 * Teste si une ligne budgetaire a ete selectionnee. Pour ceci, et la ligne
	 * budgetaire et son type de credit doivent etre indiques.
	 */
	public boolean isLigneBudSurcoutSelected() {
		return isLigneBudSelected(getOrgIdSurcout(), getTcdCodeSurcout());
	}

	/**
	 * Retourne la description courte de la ligne budgetaire et de son type de
	 * credit selectionnes.
	 */
	public String getLigneBudSurcoutDescription() {
		return getLigneBudDescription(getOrgIdSurcout(), getTcdCodeSurcout());
	}

	/**
   * 
   */
	public WOComponent clearLigneBudSurcout() {
		setOrgIdSurcout(null);
		seTcdCodeSurcout(null);
		return null;
	}

	/*
	 * @see A_CreationSwapView#selectLigneBud()
	 */
	public WOComponent selectLigneBudSurcout() {
		clearViewErrors();
		SelectLigneBud selectPage = (SelectLigneBud) selectLigneBud(
				new LbudSurcoutSelector());
		selectPage.resetPage();
		// on affiche toutes les lignes budgetaires
		selectPage.setTypeCreditAutorises(null);
		return selectPage;
	}

	/**
	 * Definit le code de la ligne budgetaire pour la demande de travaux en cours
	 * de creation.
	 */
	public void setOrgIdSurcout(Number orgId) {
		orgIdSurcout = orgId;
	}

	/**
	 * Retourne le code de la ligne budgetaire. Retourne <em>null</em> si aucune
	 * ligne n'a ete selectionnee.
	 */
	public Number getOrgIdSurcout() {
		return orgIdSurcout;
	}

	/**
	 * Definit le type de credit selectionne avec une ligne budgetaire.
	 */
	public void seTcdCodeSurcout(String tcdCode) {
		tcdCodeSurcout = tcdCode;
	}

	/**
	 * Retourne le code du type de credit selectionne avec une ligne budgetaire.
	 * Retourne <em>nulls</em> si aucune ligne n'est selectionnee.
	 */
	public String getTcdCodeSurcout() {
		return tcdCodeSurcout;
	}

	/**
	 * Lors du changement du demandeur, on va mettre à jour la configuration
	 */
	@Override
	public void setEoIndividuDemandeurConnu(EOIndividu eoIndividu) {
		EOIndividu prevEoIndividuDemandeurConnu = getEoIndividuDemandeurConnu();
		super.setEoIndividuDemandeurConnu(eoIndividu);
		if (prevEoIndividuDemandeurConnu != getEoIndividuDemandeurConnu()) {
			if (getEoIndividuDemandeurConnu() != null) {
				try {
					glpiComputerArray = GLPIComputer.getCurrentGLPIComputersForEOIndividu(getEoIndividuDemandeurConnu());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (XmlRpcException e) {
					e.printStackTrace();
				}
			} else {
				glpiComputerArray = new NSArray<GLPIComputer>();
			}
		}
	}

	/**
	 * Gestionnaire des evenements de la selection des lignes budgetaires.
	 */
	private class LbudSurcoutSelector implements SelectLigneBudListener {

		/*
		 * @see LigneBudSelector#selectedLigneBud(java.lang.Integer,
		 * java.lang.Number)
		 */
		public WOComponent select(Integer orgId, String tcdCode) {
			setOrgId(orgId);
			setTcdCode(tcdCode);
			// resetDestin();
			return parentPage();
		}

		/*
		 * @see LigneBudSelector#ligneBudSelectReturnPage()
		 */
		public WOComponent cancel() {
			return parentPage();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see SelectLigneBudListener#setOrgId(java.lang.Integer)
		 */
		public void setOrgId(Integer orgId) {
			setOrgIdSurcout(orgId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see SelectLigneBudListener#setTcdCode(java.lang.String)
		 */
		public void setTcdCode(String tcdCode) {
			seTcdCodeSurcout(tcdCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_INSTALL_POSTE_COMPLET2_ID;
	}

	/**
	 * 
	 * @return
	 */
	public String getTipMachines() {
		String tip = "";

		if (!NSArrayCtrl.isEmpty(glpiComputerArray)) {

			tip += "<ul>";

			for (GLPIComputer glpiComputer : glpiComputerArray) {
				String name = (String) glpiComputer.valueForKey("name");
				String location = (String) glpiComputer.valueForKeyPath("toGLPIComputerLocation.completename");
				String fabricant = (String) glpiComputer.valueForKey("manufacturers_name");
				String modele = (String) glpiComputer.valueForKeyPath("toGLPIComputerModel.name");

				tip += "<li>";

				if (!StringCtrl.isEmpty(name)) {
					tip += "<b>" + name + "</b>";
				}
				if (!StringCtrl.isEmpty(location)) {
					tip += " (" + location + ") ";
				}
				if (!StringCtrl.isEmpty(fabricant)) {
					tip += " - " + fabricant;
				}
				if (!StringCtrl.isEmpty(modele)) {
					tip += " - " + modele;
				}

				tip += "</li>";
			}

			tip += "</ul>";

		}

		return tip;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPremierPoste() {
		boolean isPremierPoste = false;

		if (commandeNature != null &&
				commandeNature.intValue() == COMMANDE_NATURE_PREMIER_POSTE.intValue()) {
			isPremierPoste = true;
		}

		return isPremierPoste;
	}

	/**
	 * Activer / désactiver certaines options si le demandeur est connu
	 */
	@Override
	public void setIsDemandeurConnu(boolean value) {
		super.setIsDemandeurConnu(value);
		// desactiver les options remplacement et poste supplémentaire pour les
		// inconnus
		isDisabledRadioCommandeNaturePosteRemplacement = false;
		isDisabledRadioCommandeNaturePosteSupplementaire = false;
		if (getIsDemandeurConnu() == false) {
			isDisabledRadioCommandeNaturePosteRemplacement = true;
			isDisabledRadioCommandeNaturePosteSupplementaire = true;
			// repasser en premier poste si remplacement ou supplémentaire déjà
			// selectionné
			if (getCommandeNature() != null &&
					isPremierPoste() == false) {
				setCommandeNature(COMMANDE_NATURE_PREMIER_POSTE);
			}
		}
	}

	private final static String CONTAINER_SWAP_INSTALL_POSTE_COMPLET2_ID = "ContainerSwapInstallPosteComplet2Id";

	public String getContainerSwapInstallPosteComplet2Id() {
		return CONTAINER_SWAP_INSTALL_POSTE_COMPLET2_ID;
	}

	public final DestinationSurcoutCtrl getDestinationSurcoutCtrl() {
		return destinationSurcoutCtrl;
	}

	public final void setDestinationSurcoutCtrl(DestinationSurcoutCtrl destinationSurcoutCtrl) {
		this.destinationSurcoutCtrl = destinationSurcoutCtrl;
	}
}