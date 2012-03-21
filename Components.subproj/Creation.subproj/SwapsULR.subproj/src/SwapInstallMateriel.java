import org.cocktail.dt.server.metier.EOBatiment;
import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOSalles;
import org.cocktail.dt.server.metier.EOVService;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Controlleur de l'interface HTML d'installation de materiel "lourd". Il s'agit
 * d'un écran qui demande : - ligne budgetaire - demandeur - service /
 * localisation - intelocuteur - remarques
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class SwapInstallMateriel
		extends SwapInstallComposant {

	// demandeur
	private EOIndividu eoIndividuDemandeurConnu;
	private String nomPrenomDemandeurInconnu;
	public SelectPersonneListener demandeurListener;
	private boolean isDemandeurConnu;
	private EOIndividu prevEoIndividuDemandeurConnu;

	// services - batiments - bureaux
	public NSArray<EOVService> serviceList;
	public EOVService serviceItem;
	private EOVService serviceSelected;

	public EOBatiment batimentSelected;
	public EOSalles bureauSelected;

	// interlocuteur
	// private String nomInterlocuteur;
	// public Number noIndividuInterlocuteur;
	private EOIndividu eoIndividuInterlocuteur;
	public SelectPersonneListener interlocuteurListener;

	// coche de prise de connaissance de la procedure
	public boolean isPriseConnaissance;

	// erreurs
	public boolean errorDemandeur;
	public boolean errorService;
	public boolean errorBatiment;
	public boolean errorBureau;
	public boolean errorPriseConnaissance;

	public final static String ANCHOR_REMARQUES = "\n\nRemarques :\n";
	public final static String ANCHOR_PROCEDURE = "\n\nRéférence vers la procédure d'installation:\n";

	public SwapInstallMateriel(WOContext context) {
		super(context);
	}

	// url vers la procedure
	private String urlProcedureInstallation;

	public String urlProcedureInstallation() {
		if (urlProcedureInstallation == null) {
			urlProcedureInstallation = dtApp().config().stringForKey("URL_PROCEDURE_SWAP_INSTALL");
		}
		return urlProcedureInstallation;
	}

	public boolean showProcedureInstallation() {
		return !StringCtrl.isEmpty(urlProcedureInstallation());
	}

	public void initView() {
		super.initView();
		// par defaut, on dit que le demandeur est lui meme
		setEoIndividuDemandeurConnu(individuBus().individuForNoIndividu(dtUserInfo().noIndividu()));
		shouldIgnoreSetterIsDemandeurConnu = true;
		// services (on prend celui de son contact par defaut)
		serviceList = serviceBus().allServices();
		setServiceSelected(serviceBus().serviceForCode(codeServiceDemandeur()));
		// batiments (on prend celui de son contact par defaut)
		// batimentSelected =
		// contactsBus().findBatimentForCode(parentPage().batimentDemandeurCode());
		// bureaux (on prend celui de son contact par defaut)
		// bureauSelected =
		// contactsBus().findBureauForCode(parentPage().bureauDemandeurNumero());
		// par defaut, on dit que l'interlocteur est lui meme
		setEoIndividuInterlocuteur(individuBus().individuForNoIndividu(dtUserInfo().noIndividu()));
	}

	public boolean hasErrors() {
		return super.hasErrors() || errorDemandeur || errorService || errorBatiment ||
				errorBureau || errorPriseConnaissance || hasMainErrors();
	}

	public boolean fillDataDictionary() {

		// on va bricoler le motif pour pas que ca bloque
		// dans les verification precedentes
		String prevIntMotif = intMotif;
		intMotif = "-";

		if (super.fillDataDictionary() == false) {
			return false;
		}

		intMotif = prevIntMotif;

		String strNomPrenomDemandeur = getNomPrenomDemandeurInconnu();
		if (getIsDemandeurConnu()) {
			strNomPrenomDemandeur = getEoIndividuDemandeurConnu().nomEtPrenom();
		}
		if (StringCtrl.normalize(strNomPrenomDemandeur).length() == 0) {
			setMainError("Le demandeur n'est pas renseigné");
			errorDemandeur = true;
		}
		if (serviceList.count() > 0 && getServiceSelected() == null) {
			setMainError("Le service n'est pas renseigné");
			errorService = true;
		}
		if (batimentSelected == null) {
			setMainError("Le batiment n'est pas renseigné");
			errorBatiment = true;
		}
		if (bureauSelected == null) {
			setMainError("Le bureau n'est pas renseigné");
			errorBureau = true;
		}
		if (showProcedureInstallation() && !isPriseConnaissance) {
			setMainError("Vous n'avez pas pris connaissance de la procédure");
			errorPriseConnaissance = true;
		}

		if (hasErrors()) {
			return false;
		}

		// on ajoute au la localisation au motif global
		String dicoIntMotif = (String) saveDataDico.valueForKey("intMotif");
		// on degage avant tout le motif bidon precedent
		dicoIntMotif = StringCtrl.cut(dicoIntMotif, dicoIntMotif.indexOf(ANCHOR_DETAILS));

		StringBuffer suffix = new StringBuffer("\n");

		suffix.append(EOIntervention.MOTIF_COMMANDE_MATERIEL_PREFIX_DESTINATAIRE).append(strNomPrenomDemandeur);
		suffix.append(EOIntervention.MOTIF_COMMANDE_MATERIEL_SUFFIX_DESTINATAIRE);
		if (getIsDemandeurConnu() == false) {
			suffix.append(EOIntervention.MOTIF_COMMANDE_MATERIEL_DESTINATAIRE_INCONNU);
		}
		suffix.append("Service                            :   ").append(getServiceSelected().llStructure()).append("\n");
		suffix.append("Interlocuteur du service demandeur :  	").append(getEoIndividuInterlocuteur().nomEtPrenom()).append("\n\n");
		suffix.append("Localisation    : ");
		if (batimentSelected != null)
			suffix.append(batimentSelected.stringForKey("appellation"));
		else
			suffix.append("<batiment inconnu>");
		suffix.append(" - ");
		if (bureauSelected != null) {
			suffix.append(bureauSelected.salPorte());
		} else {
			suffix.append("<bureau inconnu>");
		}

		// ajoutage
		dicoIntMotif = dicoIntMotif + suffix.toString();

		// rajouter les eventuelles remarques
		if (!StringCtrl.isEmpty(intMotif)) {
			StringBuffer remarques = new StringBuffer("");
			if (StringCtrl.normalize(intMotif).length() > 0) {
				remarques.append(ANCHOR_REMARQUES);
				remarques.append(StringCtrl.quoteText(intMotif, "  "));

				// encore ajoutage
				dicoIntMotif = dicoIntMotif + remarques.toString();
			}
		}

		// rajouter le lien vers la procedure
		if (!StringCtrl.isEmpty(urlProcedureInstallation()))
			dicoIntMotif += ANCHOR_PROCEDURE + urlProcedureInstallation();

		// ca rentre ?
		if (!dtSession().dtDataBus().checkForMaxSize(EOIntervention.ENTITY_NAME, EOIntervention.INT_MOTIF_KEY,
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

	/* -- PERSONNE DEMANDEUR -- */

	public WOComponent changeDemandeur() {
		// [LRAppTasks] : @CktlLog.trace(@"personneListener : "+personneListener());
		return SelectPersonne.getNewPage(demandeurListener(),
				"Indiquez la personne destinataire du poste",
				getIsDemandeurConnu() ? getEoIndividuDemandeurConnu().noIndividu() : null, true);
	}

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection des personnes.
	 */
	protected SelectPersonneListener demandeurListener() {
		if (demandeurListener == null)
			demandeurListener = new ChangeDemandeurListener();
		return demandeurListener;
	}

	private class ChangeDemandeurListener implements SelectPersonneListener {
		public WOComponent select(Number persId) {
			EOIndividu recIndividu = individuBus().individuForPersId(persId);
			if (recIndividu != null) {
				setEoIndividuDemandeurConnu(recIndividu);
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

	/* -- SERVICE -- */

	public String serviceLibelle() {
		if (serviceItem == null) {
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			String libelle = serviceItem.stringNormalizedForKey("llStructure");
			if (libelle.length() > 40)
				sb.append(libelle.substring(0, 40)).append("...");
			else
				sb.append(libelle);
			sb.append(" (").append(serviceItem.valueForKey("lcStructurePere")).append(")");
			return sb.toString();
		}
	}

	/* -- PERSONNE INTERLOCUTEUR -- */

	public WOComponent changeInterlocuteur() {
		// [LRAppTasks] : @CktlLog.trace(@"personneListener : "+personneListener());
		return SelectPersonne.getNewPage(interlocuteurListener(),
				"Indiquez l'interlocuteur pour cette demande d'installation",
				getEoIndividuInterlocuteur().noIndividu(), true);
	}

	/**
	 * Retourne une instance de la classe qui permet de gerer les evenements de la
	 * selection des personnes.
	 */
	private SelectPersonneListener interlocuteurListener() {
		if (interlocuteurListener == null)
			interlocuteurListener = new ChangeInterlocuteurListener();
		return interlocuteurListener;
	}

	private class ChangeInterlocuteurListener implements SelectPersonneListener {
		public WOComponent select(Number persId) {
			EOIndividu recIndividu = individuBus().individuForPersId(persId);
			if (recIndividu != null) {
				setEoIndividuInterlocuteur(recIndividu);
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

	public void clearViewErrors() {
		super.clearViewErrors();
		errorDemandeur = errorService = errorBatiment = errorBureau = errorPriseConnaissance = false;
	}

	// -- LES BUS DE DONNEES --

	private DTIndividuBus individuBus() {
		return dtSession().dataCenter().individuBus();
	}

	protected DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	// getters setters

	public final EOVService getServiceSelected() {
		return serviceSelected;
	}

	/**
	 * Interdire la mise à vide du service de destination
	 * 
	 * @param nomInterlocuteur
	 */
	public final void setServiceSelected(EOVService serviceSelected) {
		if (serviceSelected != null) {
			this.serviceSelected = serviceSelected;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_INSTALL_MATERIEL_ID;
	}

	public final boolean getIsDemandeurConnu() {
		if (getEoIndividuDemandeurConnu() != null) {
			return true;
		} else {
			return isDemandeurConnu;
		}
	}

	// ignorer au premier appel
	private boolean shouldIgnoreSetterIsDemandeurConnu;

	public void setIsDemandeurConnu(boolean value) {
		if (shouldIgnoreSetterIsDemandeurConnu == false) {
			this.isDemandeurConnu = value;
			if (value == false) {
				setEoIndividuDemandeurConnu(null);
			} else {
				setEoIndividuDemandeurConnu(prevEoIndividuDemandeurConnu);
			}
		}
		shouldIgnoreSetterIsDemandeurConnu = false;
	}

	public final EOIndividu getEoIndividuDemandeurConnu() {
		return eoIndividuDemandeurConnu;
	}

	public void setEoIndividuDemandeurConnu(EOIndividu eoIndividuDemandeurConnu) {
		if (eoIndividuDemandeurConnu != null) {
			prevEoIndividuDemandeurConnu = eoIndividuDemandeurConnu;
		}
		this.eoIndividuDemandeurConnu = eoIndividuDemandeurConnu;
	}

	public final String getNomPrenomDemandeurInconnu() {
		return nomPrenomDemandeurInconnu;
	}

	public final void setNomPrenomDemandeurInconnu(String nomDemandeur) {
		this.nomPrenomDemandeurInconnu = nomDemandeur;
	}

	private final static String CONTAINER_DEMANDEUR_POSTE_ID = "ContainerDemandeurPoste";

	public final String getContainerDemandeurPosteId() {
		return CONTAINER_DEMANDEUR_POSTE_ID;
	}

	public final EOIndividu getEoIndividuInterlocuteur() {
		return eoIndividuInterlocuteur;
	}

	public final void setEoIndividuInterlocuteur(EOIndividu eoIndividuInterlocuteur) {
		this.eoIndividuInterlocuteur = eoIndividuInterlocuteur;
	}
}
