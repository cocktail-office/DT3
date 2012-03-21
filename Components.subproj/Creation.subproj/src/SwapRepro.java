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
import java.util.Hashtable;
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

import fr.univlr.cri.dt.app.DTReproDataInfo;
import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;

/**
 * Controlleur de la vue de creation des demandes pour la reprographie.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class SwapRepro
		extends A_CreationSwapView {
	// La variable utilisee pour afficher un message d'erreur une seule fois
	private static boolean ignoreReproDataClassError = false;

	// Definition du document
	// Document numerique (0) ou papier (1)
	public int rdDocSupport;
	// Le document attache
	// Nb des pages et des copies
	public String tfNbPages;
	public String tfNbCopies;

	// Definition de la finition
	// Couvertures
	public boolean chCouvDessus;
	public boolean chCouvDessous;
	public int rdCouverture;
	// Reliures
	public int rdReliure;
	// Plastification
	public boolean chPlastification;
	// Livret
	public boolean chLivret;
	//
	public String libelle;

	//
	public String remarques;

	public boolean errorAttache;
	public boolean errorLibelle;
	public boolean errorLibelleSize;
	public boolean errorPages;
	public boolean errorCopies;
	public boolean errorLigneBud;
	public boolean errorLignesDevis;
	public boolean errorCfc;
	public boolean errorCfcContent;
	//
	private DTReproDataInfo lastSelectionsInfo;
	private String currentCout;
	private String disponible;
	// Les fichiers attaches
	public NSArray<String> filePaths;
	public boolean clearFilePaths;
	// la declaration CFC
	public Boolean showCompCfc;
	public String shouldUseCfcSelected;
	public final String SHOULD_USE_CFC_OUI = "OUI";
	public final String SHOULD_USE_CFC_NON = "NON";

	// ancres
	public final String ANCHOR_CFC_CONTENT = "DTCfcContent";

	private DestinationDevisCtrl destinationDevisCtrl;

	public class DestinationDevisCtrl
			extends A_ListeDestinationLolfCtrl {

		/**
		 * @param aSession
		 */
		public DestinationDevisCtrl(Session aSession) {
			super(aSession);
		}

		/*
		 * @see A_ListeDestinationLolfCtrl#fillAllDestinOnReset()
		 */
		@Override
		public boolean fillAllDestinOnReset() {
			return false;
		}

		/*
		 * @see A_ListeDestinationLolfCtrl#shouldInitDestinListRepro()
		 */
		@Override
		public boolean shouldInitDestinListRepro() {
			return true;
		}

		/*
		 * @see A_DTWebComponentCtrl#setMainError(java.lang.String)
		 */
		@Override
		public void setMainError(String message) {
			SwapRepro.this.setMainError(message);
		}

	}

	/**
	 * La gestion du footer pour la swap DTRepro
	 * 
	 * @author ctarade
	 * 
	 */
	public class SwapReproFooterCtrl
			extends A_FooterSwapCtrl {

		/**
		 * @param aSession
		 */
		public SwapReproFooterCtrl(Session aSession) {
			super(aSession);
		}

		/*
		 * @see A_FooterSwapCtrl#setMainError(java.lang.String)
		 */
		@Override
		public void setMainError(String message) {
			SwapRepro.this.setMainError(message);
		}

	}

	public SwapRepro(WOContext context) {
		super(context);
		clearFilePaths = true;
		filePaths = new NSArray<String>();
	}

	public void initView() {
		resetView();
	}

	public void resetView() {
		super.resetView();
		// documentTypeItem = (String)documentTypeListe.objectAtIndex(0);
		rdDocSupport = DTReproDataInfo.SUPPORT_NUMERIC;
		// on efface les traces des fichiers attaches
		clearFilesInfo();
		//
		tfNbPages = StringCtrl.emptyString();
		tfNbCopies = StringCtrl.emptyString();
		rdCouverture = DTReproDataInfo.COUV_TYPE_CARTONNE;
		chCouvDessus = false;
		chCouvDessous = false;
		// reliureItem = (String)reliureListe.objectAtIndex(0);
		rdReliure = DTReproDataInfo.RELIURE_NON;
		libelle = StringCtrl.emptyString();
		remarques = StringCtrl.emptyString();
		// par defaut, on fait une declaration CFC
		setShouldUseCfcSelected(SHOULD_USE_CFC_OUI);
		// raz du listener cfc
		_cfcListener = null;
		// On ne touche pas a la selection des lignes budgetaires
		// reinstancier le controleur du footer
		setFooterCtrl(new SwapReproFooterCtrl(dtSession()));
		// pas de date de réalisation par défaut
		getFooterCtrl().setIntDateSouhaite(null);// DateCtrl.now().timestampByAddingGregorianUnits(0,
		// 0, 3, 0, 0, 0));
		// instancier la selection des destinations LOLF
		setDestinationDevisCtrl(new DestinationDevisCtrl(dtSession()));
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
		filePaths = new NSArray<String>();
		clearFilePaths = true;
	}

	public void clearViewErrors() {
		clearErrorMessage();
		errorAttache = errorLibelle = errorLibelleSize = errorPages = errorCopies = false;
		errorLigneBud = errorCfc = errorCfcContent = false;
		errorLignesDevis = false;
		getFooterCtrl().clearErrors();
		getDestinationDevisCtrl().clearErrors();
	}

	public boolean hasErrors() {
		return hasMainErrors() || errorAttache || errorLibelle || errorLibelleSize || errorPages ||
						errorCopies || errorLigneBud || errorLignesDevis || errorCfc ||
						errorCfcContent || getFooterCtrl().hasErrors() || getDestinationDevisCtrl().hasErrors();
	}

	/*
	 * Les fichiers attaches sont obligatoires avec les demandes "repro".
	 */
	public boolean ignoreFileSaveErrors() {
		return false;
	}

	/*
	 * @see A_CreationSwapView#getPostCreateWarning()
	 */
	public String getPostCreateWarning() {
		// On fait appel la la methode getDTCheckStateMessage qui implemente ce
		// qu'il faut. On garde cette derniere pour plus de compatibilite avec
		// la version Windows de la DT.
		return getDTCheckStateMessage();
	}

	/**
	 * Retourne une nouvelle version des informations sur les selections en cours
	 * dans l'interface de la vue DT Repro.
	 */
	private DTReproDataInfo getNewSelectionsInfo() {
		DTReproDataInfo dataInfo =
				DTReproDataInfo.getNewInstance(dtApp().config(), ignoreReproDataClassError);
		if (dataInfo.hasError()) {
			setMainError(dataInfo.errorMessage() +
					"La création d'une demande Reprographie ne pourra pas être gérée correctement." +
					"Ce message ne sera affiché qu'une fois.");
			ignoreReproDataClassError = true;
		}
		dataInfo.typeDocSupport = rdDocSupport;
		if (dataInfo.typeDocSupport == DTReproDataInfo.SUPPORT_NUMERIC) {
			// dataInfo.filePaths = toFileBox.allFilePathsVector();
			dataInfo.filePaths = new Vector();
			for (int i = 0; i < filePaths.count(); i++)
				dataInfo.filePaths.addElement(filePaths.objectAtIndex(i));
		}
		if (browserRecord() != null) {
			dataInfo.actOrdre = browserRecord().intForKey("actOrdre");
			// [LRAppTasks] : @CktlLog.trace(@"browserRecord() : "+browserRecord());
			if (browserRecord().valueForKey("cartOrdre") != null)
				dataInfo.cartOrdre = browserRecord().intForKey("cartOrdre");
			dataInfo.format = parentPage().motsCles();
		}
		dataInfo.nbCopies = StringCtrl.toInt(tfNbCopies, 0);
		dataInfo.nbPages = StringCtrl.toInt(tfNbPages, 0);
		dataInfo.plastification = chPlastification;
		dataInfo.livret = chLivret;
		// Pas de couverture ni reliure dans le cas de la plastification
		if (!dataInfo.plastification) {
			dataInfo.typeCouverture = rdCouverture;
			dataInfo.couvDessus = chCouvDessus;
			dataInfo.couvDessous = chCouvDessous;
			dataInfo.typeReliure = rdReliure;
		}
		// [LRAppTasks] : @CktlLog.trace(@"New Selections Info : "+dataInfo);
		return dataInfo;
	}

	private void takeSelections() {
		lastSelectionsInfo = getNewSelectionsInfo();
	}

	public String getCategorieGED() {
		return "DT_REPRO";
	}

	/*
	 * @see A_CreationSwapView#attachedFilePaths()
	 */
	public NSArray attachedFilePaths() {
		return filePaths;
	}

	public String getTitleForDocument(int cleDT) {
		return "Document Reprographie (" + cleDT + ")";
	}

	public String getCoutEstime() {
		if (StringCtrl.normalize(currentCout).length() == 0)
			return "&lt;inconnu&gt;";
		else
			return currentCout;
	}

	public String getDisponible() {
		if (StringCtrl.normalize(disponible).length() == 0)
			return "&lt;inconnu&gt;";
		else
			return disponible;
	}

	private String getMotifResume() {
		StringBuffer texte = new StringBuffer();
		texte.append("[Details de demande Reprographie]");
		texte.append("\n").append(getResume());
		// texte.append("\n").append(getCFC());
		return texte.toString();
	}

	/**
	 * Retourne le resume de la demande reprograpie. Le resume decrit la demande
	 * et cette description est enregistree avec le motif de la demande (visible
	 * via l'inspecteur generic).
	 */
	private String getResume() {
		StringBuffer texte = new StringBuffer("Document :\n");
		if (rdDocSupport == DTReproDataInfo.SUPPORT_PAPIER) {
			texte.append("  <document papier>");
		} else {
			Vector allPaths = lastSelectionsInfo.filePaths;
			if (allPaths.size() == 0) {
				texte.append("  <non indiqué>");
			} else {
				for (int i = 0; i < allPaths.size(); i++) {
					texte.append("  <");
					texte.append(FileCtrl.getFileName((String) allPaths.elementAt(i))).append(">");
				}
			}
		}
		texte.append("\nFormat du document : ").append(parentPage().motsCles());
		texte.append("\nNombre de copies : ");
		texte.append(lastSelectionsInfo.getNbCopiesLibelle());
		texte.append("\nNombre de pages : ");
		texte.append(lastSelectionsInfo.getNbPagesLibelle());
		texte.append("\nCouverture : ");
		texte.append(lastSelectionsInfo.getTypeCouvLibelle(true));
		texte.append("\nReliure : ");
		texte.append(lastSelectionsInfo.getReliureLibelle());
		texte.append("\nPlastification : ");
		texte.append(lastSelectionsInfo.getPlastificationLibelle());
		texte.append("\nLivret : ");
		texte.append(lastSelectionsInfo.getLivretLibelle());
		return texte.toString();
	}

	/**
	 * Retourne la descripton de la liste des definitions des droits de copie CFC.
	 * Cette descrioption est ajoutee au motif de la demande creee.
	 */
	// private String getCFC() {
	// // CFCListItem item;
	// StringBuffer texte = new StringBuffer();
	// // Informations CFC
	// int n = cfcDefinitions.count();
	// texte.append("Infos CFC : ");
	// if (n > 0) {
	// texte.append(n).append(" publication(s)");
	// for(int i = 0; i < n; i++) {
	// item = (CFCListItem)cfcDefinitions.objectAtIndex(i);
	// texte.append("\n\n[CFC - Publication ").append(i+1).append("]");
	// texte.append("\nEditeur : ").append(item.editeur());
	// texte.append("\nAuteur : ").append(item.auteur());
	// texte.append("\nTitre : ").append(item.titre());
	// texte.append("\nPages : ").append(item.nbPages());
	// texte.append("\nExemplaires : ").append(item.nbExemplaires());
	// }
	// } else {
	// texte.append("<aucune publication>");
	// }
	// return texte.toString();
	// }
	//
	/*
	 * @see A_CreationSwapView#fillDataDictionary()
	 */
	public boolean fillDataDictionary() {
		clearViewErrors();
		saveDataDico.removeAllObjects();
		// Les infos sur le demandeur
		parentPage().fillDataDictionary(saveDataDico);

		if (hasMainErrors()) {
			return false;
		}
		// on se positionne au niveau de la demande
		parentPage().setPositionInPage(parentPage().PosDTCreateContent);
		// Document attache
		if (lastSelectionsInfo.isSupportNumeric() && (filePaths.count() == 0)) {
			setMainError("Le document attaché est absent");
			errorAttache = true;
		}
		// Libelle du document
		if (StringCtrl.isEmpty(libelle)) {
			setMainError("Le titre est obligatoire");
			errorLibelle = true;
		}
		// verification que le titre ne depasse pas
		// la taille max autorisee par PIE (100 c.)
		if (DTReproDataInfo.getLibelleDevis(newDtCleService(), libelle).length() > DTReproDataInfo.LIBELLE_DEVIS_MAX_SIZE) {
			setMainError("Le titre dépasse la taille maximum autorisée");
			errorLibelleSize = true;
		}
		// Nb des pages
		if (StringCtrl.toInt(tfNbPages, 0) == 0) {
			setMainError("Veuillez indiquer le nombre de pages");
			errorPages = true;
		}
		// Nb des copies. Pas obligatoire si le document n'est pas numeric
		if (lastSelectionsInfo.isSupportNumeric() && (StringCtrl.toInt(tfNbCopies, 0) == 0)) {
			setMainError("Veuillez indiquer le nombre de copies");
			errorCopies = true;
		}
		// La ligne budgetaire doit etre selectionnee si la config l'oblige
		getDestinationDevisCtrl().validate();
		// validation du footer
		getFooterCtrl().validate();
		// declaration CFC
		if (shouldUseCfcSelected.equals(SHOULD_USE_CFC_OUI) && cfcListener().getCfcList().count() == 0) {
			// on se positionne au niveau du CFC
			parentPage().setPositionInPage(ANCHOR_CFC_CONTENT);
			errorCfc = true;
			setMainError("Vous devez faire au moins une déclaration CFC");
		}
		// contenu du formulaire CFC
		if (shouldUseCfcSelected.equals(SHOULD_USE_CFC_OUI)) {
			// on se positionne au niveau du CFC
			parentPage().setPositionInPage(ANCHOR_CFC_CONTENT);
			errorCfcContent = !cfcListener().verifieData();
			if (errorCfcContent) {
				setMainError("Problème dans la déclaration CFC");
			}
		}
		// Contenu du devis
		if (mustCreateDevis() && lastSelectionsInfo.getLignes().size() == 0) {
			errorLignesDevis = true;
			setMainError("Erreur lors de l'enregistrement d'un devis : <br>" +
					"Il n'y a aucune ligne d'article dans le devis.");
			return false;
		}
		if (hasErrors()) {
			return false;
		}
		String texte = getMotifResume();
		libelle = StringCtrl.normalize(libelle);
		remarques = StringCtrl.normalize(remarques);
		if (remarques.length() > 0)
			remarques += "\n\n";
		if (!hasErrors()) {
			// Teste si le taille des remarques ne depasse pas de la taille
			// maximale autorisee des commentaires
			if (!dtSession().dtDataBus().checkForMaxSize("Intervention", "intMotif",
																			remarques, "Remarques",
																			texte.length(), true, true)) {
				setMainError(dtSession().dtDataBus().getErrorMessage());
				return false;
			}
		}
		if (!hasErrors()) {
			// Il faut retravailler le motif de la DT
			saveDataDico.setObjectForKey(remarques + texte, "intMotif");
			saveDataDico.setObjectForKey(getFooterCtrl().getIntDateSouhaite(), "intDateSouhaite");
			// On memorise le code destination.
			if (getDestinationLolfCtrl() != null && getDestinationLolfCtrl().lolfId() != null) {
				saveDataDico.setObjectForKey(getDestinationDevisCtrl().lolfId(), "lolfId");
			} else {
				saveDataDico.setObjectForKey(DTDataBus.nullValue(), "lolfId");
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Envoie un message email de cration d'une demande. Le parametre
	 * <code>filesURL</code> donne la liste des URL des documents attaches
	 * enregistres sur le serveur GEDFS.
	 */
	public void sendMail(NSArray<String> filesURL) {
		formatAndSendMail(filesURL);
	}

	/**
	 * Enregistre les informations specifiques a la description Reprographie.
	 */
	private void saveReproData(Integer transId) {
		interventionBus().addInterventionRepro(transId,
				newDtIntOrdre(),
				lastSelectionsInfo.isSupportNumeric(),
				lastSelectionsInfo.getTypeCouvCode(),
				lastSelectionsInfo.couvDessus,
				lastSelectionsInfo.couvDessous,
				new Integer(lastSelectionsInfo.nbCopies),
				new Integer(lastSelectionsInfo.nbPages),
				lastSelectionsInfo.getReliureCode(),
				lastSelectionsInfo.plastification,
				lastSelectionsInfo.livret,
				libelle, remarques);
		if (interventionBus().hasError())
			setMainError("Erreur lors de sauvegarde des données (saveReproData)<br>" +
					interventionBus().getErrorMessage());
	}

	/**
	 * Enregistre les donnees de la descrioption CFC d'une copie d'un document.
	 */
	private boolean saveCFCData(Integer transId, Number intOrdre) {
		if (shouldUseCfcSelected.equals(SHOULD_USE_CFC_OUI) && cfcListener().getCfcList().count() > 0) {
			boolean ok = cfcListener().saveData(transId, intOrdre);
			if (interventionBus().hasError())
				setMainError("Erreur lors de sauvegarde des données (saveCFCData)<br>" +
						interventionBus().getErrorMessage());
			return ok;
		}
		return true;
	}

	public boolean createDevis() {
		// [LRAppTasks] : @CktlLog.trace(@null);
		clearViewErrors();
		setNewDtPrestId(null);
		setNewDtPrestNumero(null);
		// Verifier si le service est disponible
		if (canUsePIE()) {
			Vector lignes = lastSelectionsInfo.getLignes();
			// [LRAppTasks] : @CktlLog.trace(@"Devis lignes : "+lignes);
			Integer lolfId = (Integer) getDestinationDevisCtrl().lolfId();
			// attention, si c'est -1, il faut forcer à null sinon les WS de Pie
			// petent une erreur !
			if (lolfId != null &&
					lolfId.intValue() == -1) {
				lolfId = null;
			}
			Hashtable result = pieBus().createDevis(
					DTReproDataInfo.getLibelleDevis(newDtCleService(), libelle),
					(Integer) newDtOrgId(),
					newDtTcdCode(),
					lolfId,
					dtApp().defaultReproPcoNum(),
					lignes);
			if (pieBus().hasError()) {
				setMainError("Erreur lors de l'enregistrement d'un devis : <br>" +
						pieBus().errorMessage());
			}
			if (result != null) {
				Object prestIdObj = result.get(DTPrestaServicesConst.FormPrestIdKey);
				if (prestIdObj != null) {
					setNewDtPrestId(Integer.valueOf(prestIdObj.toString()));
				}
				Object prestNumeroObj = result.get(DTPrestaServicesConst.FormPrestNumeroKey);
				if (prestNumeroObj != null) {
					setNewDtPrestNumero(Integer.valueOf(prestNumeroObj.toString()));
				}
			}
		}
		// Si le devis est cree, on remet a jour la base de donnees
		if (newDtPrestId() != null) {
			// Integer transId = interventionBus().beginECTransaction();
			saveInfinData(null);
			return true;
			// interventionBus().commitECTransaction(transId);
		}
		return false;
	}

	public void saveData() {
		Integer transId;
		clearViewErrors();
		// Sauvegarde par defaut
		super.saveData();
		// Sauvegarde de donnees Repro
		if (newDtIntOrdre() != null) {
			transId = interventionBus().beginECTransaction();
			saveReproData(transId);
			if (!hasErrors())
				saveCFCData(transId, newDtIntOrdre());
			if (!hasErrors())
				saveInfinData(transId);
			if (hasErrors()) {
				interventionBus().rollbackECTrancsacition(transId);
				interventionBus().deleteIntervention(null, newDtIntOrdre());
			} else {
				if (!interventionBus().commitECTransaction(transId)) {
					setMainError(interventionBus().getErrorMessage());
					interventionBus().deleteIntervention(null, newDtIntOrdre());
				}
			}
		}
		// Ici on pourrait retablir les valeurs de intOrdre et intCleService
		// dans resultDico
	}

	/**
	 * Effectue le teste de l'etat de la base de donnees (les services
	 * d'utilisateur, les fournisseurs valides) et retourne la description
	 * correspondante.
	 * 
	 * <p>
	 * Cette methode peut etre utilisee pour fournir les informations
	 * supplementaires aux utilisateurs cocernant les erreurs (possibles) suite a
	 * la creation de leurs demandes.
	 * </p>
	 * 
	 * <p>
	 * La methode retourne <i>null</i> si l'etat de la base est OK et la demande
	 * peut etre creee sans problemes. Elle retourne <i>null</i> aussi aussi
	 * lorsque le parametre <code>APP_ERROR_DESCRIPTION</code> et <i>NO</i> dans
	 * la configuration de l'application.
	 * </p>
	 */
	private String getDTCheckStateMessage() {
		if (dtApp().config().booleanForKey("APP_ERROR_DESCRIPTION")) {
			// StringBuffer info = new StringBuffer();
			// // On verifie d'abord si le service du demandeur est bien celui indique
			// // dans l'annuaire. Le demandeur peut lui-meme donner le service dans
			// les
			// // contacts, mais ceci peut ne pas etre renseigne dans l'annuaire.
			// String serviceName =
			// serviceBus().libelleForServiceCode(codeServiceDemandeur(), false,
			// true);
			// // Le service demandeur doit etre indique comme un "Service" de
			// l'annuaire.
			// if (!serviceBus().isServiceAnnuaire(codeServiceDemandeur(),
			// persIdDemandeur())) {
			// info.append("Votre information de contact indique que votre service est :<br> \"");
			// info.append(serviceName).append("\" (code ").append(codeServiceDemandeur());
			// info.append(")<br>mais cette information n'est pas enregistr&eacute;e dans l'annuaire de votre &eacute;tablissement.");
			// }
			// // On teste si le fournisseur est valide pour le devis
			// if (!jefyBus().checkFournisStructure(codeServiceDemandeur())) {
			// if (info.length() > 0) info.append("\n\n");
			// info.append("Les informations de l'annuaire indiquent que votre service<br>\"");
			// info.append(serviceName).append("\" (code ").append(codeServiceDemandeur());
			// info.append(")<br>n'est pas d&eacute;clar&eacute; comme un service des fournisseurs valides de l'&eacute;tablissement.");
			// // On propose les services valides
			// NSArray allSrv = jefyBus().getFournisStructures(persIdDemandeur());
			// NSDictionary rec;
			// if (allSrv.count() > 0) {
			// info.append("<br>Dans l'annuaire, vous &ecirc;tes affect&eacute; aux services valides suivants :");
			// for(int i=0; i<allSrv.count(); i++) {
			// rec = (NSDictionary)allSrv.objectAtIndex(i);
			// info.append("<br>&nbsp;-&nbsp;\"").append(rec.valueForKey("llStructure"));
			// info.append("\" (").append(rec.valueForKey("cStructure")).append(")");
			// }
			// }
			// }
			// //
			// if (info.length() > 0) {
			// info.insert(0, "ATTENTION !<br>");
			// info.append("<br><br>Vous pouvez contacter l'administrateur de votre annuaire\n");
			// info.append("pour corriger ces erreurs et ne plus avoir ces messages dans le futur.");
			// }
			// if (info.length() > 0)
			// return info.toString();
			return getPieDetailledError();
		}
		return null;
	}

	/* === Les methodes implementant les actions de l'interface HTML === */

	public WOComponent refreshPrix() {
		takeSelections();
		if (canUsePIE()) {
			Vector lignes = lastSelectionsInfo.getLignes();
			// [LRAppTasks] : @CktlLog.trace(@"Devis lignes : "+lignes);
			if (lignes.size() > 0) {
				Double somme = pieBus().estimeTTCDevis(lignes);
				if (pieBus().hasError()) {
					currentCout = "&lt;erreur&gt;";
				} else {
					currentCout = DTStringCtrl.formatDouble(somme.doubleValue(), 2);
				}
			} else {
				currentCout = "0,00";
			}
		} else { // Sinon, on marque qu'il n'est pas dispo
			currentCout = "&lt;PIE indisponible&gt;";
		}
		return null;
	}

	public WOComponent testerDisponible() {
		takeSelections();
		if (canUsePIE()) {
			Vector lignes = lastSelectionsInfo.getLignes();
			// [LRAppTasks] : @CktlLog.trace(@"Devis lignes : "+lignes);
			if (lignes.size() > 0) {
				Double totalDisponible = pieBus().sommeLigneBud((Integer) newDtOrgId(), newDtTcdCode());
				if (pieBus().hasError()) {
					disponible = "&lt;erreur&gt;";
				} else {
					// calculer le cout du devis
					Double coutDevis = pieBus().estimeTTCDevis(lignes);
					if (totalDisponible.doubleValue() >= coutDevis.doubleValue()) {
						disponible = "OK";
					} else {
						disponible = "INSUFFISANT";
					}
					disponible += " (" + DTStringCtrl.formatDouble(totalDisponible.doubleValue(), 2) + ")";
				}
			} else {
				disponible = "OK";
			}
		} else { // Sinon, on marque qu'il n'est pas dispo
			disponible = "&lt;PIE indisponible&gt;";
		}
		return null;
	}

	/*
	 * @see A_CreationSwapView#selectLigneBud()
	 */
	public WOComponent selectLigneBud() {
		takeSelections();
		clearViewErrors();
		SelectLigneBud selectPage = (SelectLigneBud) super.selectLigneBud();
		selectPage.resetPage();
		if (canUsePIE()) {
			selectPage.setTypeCreditAutorises(dtSession().pieBus().allTypcredAutorises());
		} else {
			selectPage.setTypeCreditAutorises(null);
		}
		return selectPage;
	}

	/*
	 * @see A_CreationSwapView#clearLigneBud()
	 */
	public WOComponent clearLigneBud() {
		takeSelections();
		return super.clearLigneBud();
	}

	/*
	 * @see A_CreationSwapView#validerDemande()
	 */
	public WOComponent validerDemande() {
		takeSelections();
		WOComponent page = super.validerDemande();
		clearFilesInfo();
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

	/**
	 * @see A_CreationSwapView.mustCreateDevis()
	 */
	public boolean mustCreateDevis() {
		// XXX transition JEFYCO2007 : le devis n'est pas obligatoire si
		// Pie n'est pas disponible
		return canUsePIE() && lastSelectionsInfo.mustCreateDevis();
	}

	// gestion CFC

	private CompCFCListener _cfcListener;

	public CompCFCListener cfcListener() {
		if (_cfcListener == null)
			_cfcListener = new CompCFCListener(interventionBus(), null/*
																																 * this.parentPage
																																 * ()
																																 */, null);
		return _cfcListener;
	}

	/*
	 * public WOComponent ajouterCFC() { CompCFC page = (CompCFC)
	 * pageWithName(CompCFC.class.getName()); page.setListener(cfcListener());
	 * return page; }
	 */

	/**
   * 
   */
	public void setShouldUseCfcSelected(String value) {
		shouldUseCfcSelected = value;
		showCompCfc = new Boolean(shouldUseCfcSelected.equals(SHOULD_USE_CFC_OUI));
	}

	/**
	 * On traite les informations financières pour les DTs repro
	 */
	public boolean shouldProcessDataInfin() {
		return true;
	}

	/**
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_REPRO_ID;
	}

	public final DestinationDevisCtrl getDestinationDevisCtrl() {
		return destinationDevisCtrl;
	}

	public final void setDestinationDevisCtrl(DestinationDevisCtrl destinationDevisCtrl) {
		this.destinationDevisCtrl = destinationDevisCtrl;
	}

	/**
	 * @see A_CreationSwapView#getDestinationLolfCtrl()
	 */
	@Override
	public A_ListeDestinationLolfCtrl getDestinationLolfCtrl() {
		return getDestinationDevisCtrl();
	}

}
