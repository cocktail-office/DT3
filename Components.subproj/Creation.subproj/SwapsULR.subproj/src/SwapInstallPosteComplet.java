import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;

/**
 * Controlleur de l'interface HTML d'installation de materiel.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class SwapInstallPosteComplet
		extends SwapInstallMateriel {

	// logiciels complementaires
	protected String logiciels;

	// remplacement poste existant oui / non
	public String shouldRemplacer;
	public final String SHOULD_REMPLACER_OUI = "OUI";
	public final String SHOULD_REMPLACER_NON = "NON";

	public boolean errorRemplacer;

	public SwapInstallPosteComplet(WOContext context) {
		super(context);
	}

	public void initView() {
		super.initView();
		// par defaut, on se selectionne rien sur le remplacement
		shouldRemplacer = null;
		// raz des remarques
		logiciels = StringCtrl.emptyString();
	}

	public boolean hasErrors() {
		return super.hasErrors() || errorRemplacer || hasMainErrors();
	}

	public boolean fillDataDictionary() {

		if (super.fillDataDictionary() == false) {
			return false;
		}

		// verifier que le choix de remplacement O/N est fait
		if (shouldRemplacer == null) {
			setMainError("Veuillez indiquer s'il s'agit d'un poste de remplacement ou non");
			errorRemplacer = true;
		}

		if (hasErrors()) {
			return false;
		}

		// recup du dico fait juste avant
		String dicoIntMotif = (String) saveDataDico.valueForKey("intMotif");

		// on precede le motif par l'indication de remplacement O/N
		StringBuffer prefix = new StringBuffer();
		if (shouldRemplacer.equals(SHOULD_REMPLACER_OUI)) {
			prefix.append("Premier poste ou remplacement d'un poste existant.\n");
		} else {
			prefix.append("Poste supplémentaire.\n");
		}

		// ajoutage
		dicoIntMotif = prefix.toString() + dicoIntMotif;

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

		// ca rentre ?
		if (!dtSession().dtDataBus().checkForMaxSize("Intervention", "intMotif",
				dicoIntMotif, "Motif",
				0, true, true)) {
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_INSTALL_POSTE_COMPLET_ID;
	}
}