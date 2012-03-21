import com.webobjects.appserver.WOContext;

/**
 * Gestion des parametres generaux de l'application DT
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class PageAdministration extends DTWebComponent {

	// gestion de la selection des onglets
	private int selectedOnglet = ONGLET_ADM_NONE;
	public final static int ONGLET_ADM_NONE = -1;
	public final static int ONGLET_ADM_DROITS = 0;
	public final static int ONGLET_ADM_ACTIVITES = 1;
	public final static int ONGLET_ADM_SYSTEME = 2;
	public final static int ONGLET_ADM_STATS = 3;

	public final static String ONGLET_ADM_TITLE_DROITS = "Gestion des droits individuels";
	public final static String ONGLET_ADM_TITLE_ACTIVITES = "Arborescence des activit&eacute;s";
	public final static String ONGLET_ADM_TITLE_SYSTEME = "Op&eacute;rations syst&egrave;me";
	public final static String ONGLET_ADM_TITLE_STATS = "Statistiques";

	public PageAdministration(WOContext context) {
		super(context);
	}

	public void setSelectedOnglet(int id) {
		selectedOnglet = id;
	}

	public int getSelectedOnglet() {
		return selectedOnglet;
	}

	// quel swap afficher
	public boolean showSwapDroits() {
		return selectedOnglet == ONGLET_ADM_DROITS;
	}

	public boolean showSwapActivites() {
		return selectedOnglet == ONGLET_ADM_ACTIVITES;
	}

	public boolean showSwapSysteme() {
		return selectedOnglet == ONGLET_ADM_SYSTEME;
	}

	public boolean showSwapStats() {
		return selectedOnglet == ONGLET_ADM_STATS;
	}

	// titre du swap en cours
	public String swapTitle() {
		String title = "";
		if (showSwapDroits()) {
			title = ONGLET_ADM_TITLE_DROITS;
		} else if (showSwapActivites()) {
			title = ONGLET_ADM_TITLE_ACTIVITES;
		} else if (showSwapSysteme()) {
			title = ONGLET_ADM_TITLE_SYSTEME;
		} else if (showSwapStats()) {
			title = ONGLET_ADM_TITLE_STATS;
		}
		return title;
	}

}