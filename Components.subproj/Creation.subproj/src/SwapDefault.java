/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Gere l'interface HTML de la vue de creation des demandes "generiques".
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class SwapDefault
		extends A_CreationSwapView {

	public String intMotif;
	// Les erreurs
	public boolean errorMotif;
	// Les fichiers attaches
	public NSArray filePaths;
	public boolean clearFilePaths;

	/**
	 * La gestion du footer pour la swap par défaut
	 * 
	 * @author ctarade
	 * 
	 */
	public class SwapDefautFooterCtrl
			extends A_FooterSwapCtrl {

		/**
		 * @param aSession
		 */
		public SwapDefautFooterCtrl(Session aSession) {
			super(aSession);
		}

		/*
		 * @see A_FooterSwapCtrl#setMainError(java.lang.String)
		 */
		@Override
		public void setMainError(String message) {
			SwapDefault.this.setMainError(message);
		}

	}

	public SwapDefault(WOContext context) {
		super(context);
		clearFilePaths = true;
		filePaths = new NSArray();
	}

	public void initView() {
		resetView();
		clearViewErrors();
	}

	public void resetView() {
		super.resetView();
		intMotif = StringCtrl.emptyString();
		// [LRAppTasks] : @CktlLog.trace(@"Resetting file paths...");
		// On supprime les fichers et le repertoire unique genere
		clearFilesInfo();
		// reinstancier le controleur du footer
		setFooterCtrl(new SwapDefautFooterCtrl(dtSession()));
		getFooterCtrl().setIntDateSouhaite(DateCtrl.now().timestampByAddingGregorianUnits(0, 0, 10, 0, 0, 0));
	}

	/**
	 * Supprime les fichiers et le repertoire unique genere pour le depot de
	 * documents.
	 */
	private void clearFilesInfo() {
		if (!NSArrayCtrl.isEmpty(filePaths)) {
			// FileCtrl.deleteDir(
			// FileCtrl.getParentDirectory((String)filePaths.objectAtIndex(0)), true);
			FileCtrl.deletePath(
					FileCtrl.getParentDirectory((String) filePaths.objectAtIndex(0)));
		}
		filePaths = new NSArray();
		clearFilePaths = true;
	}

	public void clearViewErrors() {
		clearErrorMessage();
		getFooterCtrl().clearErrors();
		errorMotif = false;
	}

	public boolean hasErrors() {
		return hasMainErrors() || errorMotif || getFooterCtrl().hasErrors();
	}

	public String getCategorieGED() {
		return "DT_COMMUN";
	}

	/*
	 * Pour les DT generiques, les fichiers attaches peuvent etre "perdus", bien
	 * qu'il serait bien de l'eviter.
	 */
	public boolean ignoreFileSaveErrors() {
		return true;
	}

	/*
	 * @see A_CreationSwapView#attachedFilePaths()
	 */
	public NSArray attachedFilePaths() {
		return filePaths;
	}

	/*
	 * @see A_CreationSwapView#getPostCreateWarning()
	 */
	public String getPostCreateWarning() {
		return null;
	}

	public boolean fillDataDictionary() {
		clearViewErrors();
		saveDataDico.removeAllObjects();
		parentPage().fillDataDictionary(saveDataDico);
		if (hasMainErrors()) {
			return false;
		}
		if (StringCtrl.normalize(intMotif).length() == 0) {
			errorMotif = true;
			setMainError("Veuillez indiquer un motif");
		}
		// validation du footer
		getFooterCtrl().validate();
		if (hasErrors()) {
			return false;
		}
		if (!hasErrors()) {
			// Teste si le taille des remarques ne depasse pas de la taille
			// maximale autorisee des commentaires
			if (!dtSession().dtDataBus().checkForMaxSize(
					EOIntervention.ENTITY_NAME,
					EOIntervention.INT_MOTIF_KEY,
					intMotif, "Motif", 0, true, true)) {
				setMainError(dtSession().dtDataBus().getErrorMessage());
				return false;
			}
		}
		if (!hasErrors()) {
			saveDataDico.setObjectForKey(getFooterCtrl().getIntDateSouhaite(), "intDateSouhaite");
			saveDataDico.setObjectForKey(intMotif, "intMotif");
			// On memorise le code destination.
			if (getDestinationLolfCtrl() != null && getDestinationLolfCtrl().lolfId() != null) {
				saveDataDico.setObjectForKey(getDestinationLolfCtrl().lolfId(), "lolfId");
			} else {
				saveDataDico.setObjectForKey(DTDataBus.nullValue(), "lolfId");
			}
		}
		return true;
	}

	public void saveData() {
		clearViewErrors();
		// Sauvegarde par defaut
		super.saveData();
		// S'assurer qu'il faut traiter les information financieres
		if (shouldProcessDataInfin()) {
			// enregistrer les informations financieres
			saveInfinData(null);
		}
	}

	public boolean createDevis() {
		clearViewErrors();
		// Rien pour ce type des demandes
		return false;
	}

	public void sendMail(NSArray<String> filesURL) {
		formatAndSendMail(filesURL);
	}

	/**
	 * Valide la creation d'une nouvelle demande de travaux.
	 */
	public WOComponent validerDemande() {
		parentPage().setPositionInPage(parentPage().PosDTCreateContent);
		WOComponent page = super.validerDemande();
		if (page != null) {
			// la creation s'est bien faite, on peut effacer les fichiers joints
			clearFilesInfo();
		}
		return page;
	}

	public WOComponent nouvelleDemande() {
		parentPage().setPositionInPage(parentPage().PosDTCreateContent);
		WOComponent page = super.nouvelleDemande();
		clearFilesInfo();
		return page;
	}

	/*
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		clearFilesInfo();
		super.finalize();
	}

	public boolean mustCreateDevis() {
		return false;
	}

	/**
	 * On ne traite pas les informations financières par défaut
	 */
	@Override
	public boolean shouldProcessDataInfin() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return I_Swap.SWAP_VIEW_DEFAULT_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_CreationSwapView#getDestinationLolfCtrl()
	 */
	@Override
	public A_ListeDestinationLolfCtrl getDestinationLolfCtrl() {
		return null;
	}
}
