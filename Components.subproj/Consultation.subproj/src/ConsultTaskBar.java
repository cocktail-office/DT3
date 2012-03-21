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

import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;
import org.cocktail.fwkcktlwebapp.server.components.CktlMailPage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * La barre des icones de gestion des demandes : validation, affectation,
 * suppression, etc...
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class ConsultTaskBar extends DTWebComponent {
	// bindings
	public I_ConsultTaskBarListener taskBarListener;

	// les modes d'utilisation de la barre des taches
	private static final int MODE_TASKBAR = 0;
	private static final int MODE_VALIDER = 1;
	private static final int MODE_AFFECTER = 2;
	private static final int MODE_TRAITEMENT = 3;

	private int mode;
	private String validationComment;
	private String errorMessage;
	private String warnMessage;
	private boolean showGoToBar;
	private InspecteurDT pageInspecteurDT;

	/**
	 * ajout d'un traitement : on preclique sur le lien "ajouter" de
	 * CompListTraitement
	 */
	public boolean shouldForceAddTraitement;

	/** le pointeur vers le composant d'ajout de traitement actuellement affiche */
	private CompListeTraitement componentListTraitement;

	/**
   * 
   */
	public ConsultTaskBar(WOContext context) {
		super(context);
		pageInspecteurDT = (InspecteurDT) context().page();
		pageInspecteurDT.setTaskBar(this);
		toTaskBar();
		pageInspecteurDT.doParamTaskBar();
	}

	public String errorMessage() {
		return errorMessage;
	}

	public boolean hasError() {
		return (errorMessage != null);
	}

	public String warnMessage() {
		return warnMessage;
	}

	public boolean hasWarning() {
		return (warnMessage != null);
	}

	public boolean showGoToBar() {
		return showGoToBar;
	}

	public boolean isTaskBarVisible() {
		return (mode == MODE_TASKBAR);
	}

	/**
	 * Annule les operations en cours et retourne vers le mode de taskbar.
	 */
	public WOComponent toTaskBar() {
		mode = MODE_TASKBAR;
		errorMessage = null;
		warnMessage = null;
		showGoToBar = false;
		shouldForceAddTraitement = false;
		return null;
	}

	/**
	 * Enregistre le message d'erreur a afficher dans la zone de task bar. Si le
	 * parametre <code>showLink</code> est <i>true</i>, un bouton.
	 */
	private WOComponent reportError(String message, boolean showLink) {
		errorMessage = message;
		showGoToBar = showLink;
		return null;
	}

	/* === Disponibilite des boutons de navigation === */

	/**
	 * La consultation des traitements n'est possible que s'il y en a
	 */
	public boolean isDisabledTraiter() {
		return iBus().findTraitements(null, null, getRecIntervention().numberForKey("intOrdre"), null).count() == 0;
	}

	/* === Retour dans la page precedente === */

	public WOComponent doGoBak() {
		return taskBarListener.goBack();
	}

	/* === Changement d'etat === */

	public boolean canChangerEtat() {
		return isRecInterventionExists() && iBus().canChangeEtat(getRecIntervention(), dtUserInfo().noIndividu());
	}

	/**
	 * Afficher un ecran avec tous les etats de destination possibles pour cette
	 * DT
	 */
	public WOComponent changerEtat() {
		StringBuffer sb = new StringBuffer();
		sb.append("<center>Vous souhaitez changer l'etat de la DT,<br>").
				append("veuillez choisir parmi les suivants<br>").
				append("ou annuler l'operation<br><br>").
				append("<i>Aucun mail ne sera envoyé suite à cette opération !</i></center>");
		// determiner tous les etats de destination possibles
		NSArray dstEtatsCode = new NSArray();
		for (int i = 0; i < pageInspecteurDT.caller().etatsLibelles.count(); i++) {
			String etatLibelle = (String) pageInspecteurDT.caller().etatsLibelles.objectAtIndex(i);
			String etatCode = etatBus().etatForLibelle(etatLibelle);
			if (DTEtatBus.isAllowedEtatChange(getRecIntervention().intEtat(), etatCode))
				dstEtatsCode = dstEtatsCode.arrayByAddingObject(etatCode);
		}
		// 3 possibilites :
		// - pas d'etats de destination
		// - 1 etat de destination
		// - 2 etats de destination
		String title = "Changement d'état de DT";
		if (dstEtatsCode.count() == 0) {
			// pas d'etat, message d'info
			return CktlAlertPage.newAlertPageWithCaller(pageInspecteurDT, title,
					"L'etat actuel de cette demande ne peut pas etre changé manuellement.",
					"Revenir", CktlAlertPage.INFO);
		} else if (dstEtatsCode.count() <= 2) {
			String etat1 = etatBus().libelleForEtat((String) dstEtatsCode.objectAtIndex(0));
			String etat2 = (dstEtatsCode.count() > 1 ?
					etatBus().libelleForEtat((String) dstEtatsCode.objectAtIndex(1)) : null);
			return CktlAlertPage.newAlertPageWithResponder(pageInspecteurDT, title,
					sb.toString(), etat1, etat2, "Annuler", CktlAlertPage.QUESTION,
					new AskChangeEtatResponder(pageInspecteurDT, dstEtatsCode));
		} else {
			// trop d'etats ... msg d'erreur
			return CktlAlertPage.newAlertPageWithCaller(pageInspecteurDT, title,
					"Les etats de destination sont trop nombreux ... merci de contacter le developpeur !",
					"Revenir", CktlAlertPage.ERROR);
		}

	}

	// les diveres pages qui s'enchaine pour la suppression*

	/**
	 * Le pattern des respondeur utilises sur cette page
	 */
	private class AskConfirmResponder implements CktlAlertResponder {
		protected WOComponent caller;

		public AskConfirmResponder(WOComponent aCaller) {
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			return null;
		}
	}

	/**
	 * Ecran de demande de changement d'etat de DT
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class AskChangeEtatResponder extends AskConfirmResponder {
		// les etats de destination
		private NSArray etatsCode;

		public AskChangeEtatResponder(WOComponent aCaller, NSArray someEtatsCode) {
			super(aCaller);
			etatsCode = someEtatsCode;
		}

		public WOComponent respondToButton(int buttonNo) {
			// etat 1 ou etat2
			if (buttonNo == 1 || buttonNo == 2) {
				return changeSelectionEtat((String) etatsCode.objectAtIndex(buttonNo - 1));
			}
			// annuler est sur le bouton 3
			return caller;
		}
	}

	/**
	 * Effectue l'action de changement d'etat d'une demande selectionnee. Cette
	 * methode effectue egalement les tests necessaires sur la validite de
	 * changement de l'etat.
	 */
	private WOComponent changeSelectionEtat(String newEtatCode) {
		// On collecte les informations
		if (getRecIntervention() == null)
			return pageInspecteurDT;
		Number intOrdre = getRecIntervention().numberForKey("intOrdre");
		String oldEtat = getRecIntervention().stringForKey("intEtat");
		boolean shouldChange = false;
		boolean hasNoErrors = true;
		String alertMessage = StringCtrl.emptyString();
		// [LRAppTasks] : @CktlLog.trace(@"to Etat : "+newEtatLibelle+", "+newEtat);
		// On verifie si l'utilisateur a le droit d'executer l'operation
		// du changement d'etat. On doit :
		// - etre en mode "intervenant"
		// - si l'intervenant affecte, avoir au moins les droits "intervenant"
		// - sinon, au moins les droits de super-intervenant
		if (!iBus().canChangeEtat(getRecIntervention(), null)) {
			alertMessage = "Vous n'avez pas le droit d'exécuter cette opération.";
		} else if (newEtatCode.equals(oldEtat)) {
			// On verifie, si la demande n'est pas deja dans l'etat souhaite
			alertMessage = "La demande est déjà dans l'état souhaité.";
		} else if (!DTEtatBus.isAllowedEtatChange(oldEtat, newEtatCode)) {
			// Le changement de l'etat n'est pas autorise
			// On fait rien et on affichera un message d'avertissement
			alertMessage = "Cet état de destination est interdit pour cette DT.";
		} else { // On peut changer l'etat de la demande
			shouldChange = true;
			// Le cas de "en cours" -> "non affecte" est special.
			if (DTEtatBus.isEtat(oldEtat, EOEtatDt.ETAT_EN_COURS) &&
					DTEtatBus.isEtat(newEtatCode, EOEtatDt.ETAT_NON_AFFECTEES)) {
				// Si on veut remettre dans "non affectee", il faut supprimer
				// les intervenants affectes et changer d'etat. Mais il faut que aucun
				// traitement ne soit pas encore realise.
				if (iBus().findTraitements(null, null, intOrdre, null).count() > 0) {
					alertMessage = "Cette demande a des traitements associés.";
					shouldChange = false;
				} else {
					// On supprime toutes les affectations et on change l'etat de la
					// demande
					Integer transId = iBus().beginECTransaction();
					if (iBus().deleteAllIntervenants(transId, intOrdre) &&
							iBus().updateIntervention(
									dtUserInfo(), null, intOrdre, null, null, null, newEtatCode, null, null, null, null, null)) {
						iBus().commitECTransaction(transId);
					} else {
						iBus().rollbackECTrancsacition(transId);
						hasNoErrors = true;
						alertMessage = "Ce changement d'état n'a pas pu être exécuté avec succès";
					}
				}
			} else {
				// Sinon, pour tous les autres etats, c'est un simple changement
				// de "old" vers "new"
				hasNoErrors = iBus().updateIntervention(
						dtUserInfo(), null, intOrdre, null, null, null, newEtatCode, null, null, null, null, null);
			}
		}
		// Finalement : faut-il afficher un message d'avertissement
		if (alertMessage.length() > 0)
			alertMessage += "\n";
		if (shouldChange) { // Il faut changer
			if (!hasNoErrors) // ...mais il y a des erreurs
				alertMessage += "Ce changement d'état n'a pas pu être effectué.";
		}

		if (alertMessage.length() > 0)
			return CktlAlertPage.newAlertPageWithCaller(pageInspecteurDT, "Erreur de changement d'etat",
					alertMessage, "Revenir", CktlAlertPage.ERROR);
		else {
			pageInspecteurDT.caller().setShouldRefresh(true);
			return pageInspecteurDT;
		}
	}

	/* === La validation des demandes === */

	public void setValidationComment(String comment) {
		validationComment = comment;
	}

	public String validationComment() {
		return validationComment;
	}

	public boolean isValiderVisible() {
		return (mode == MODE_VALIDER);
	}

	public boolean canValider() {
		return isRecInterventionExists() && iBus().canValider(getRecIntervention(), dtSession().dtUserInfo().noIndividu());
	}

	public WOComponent toValider() {
		if (!canValider()) {
			return reportError(
					"Cette demande ne doit pas &ecirc;tre valid&eacute;e " +
							"ou vous n'avez pas les droits n&eacute;cessaires pour effectuer cette op&eacute;ration",
					false);
		}
		mode = MODE_VALIDER;
		return null;
	}

	/**
	 * Action de valider la demande de travaux
	 * 
	 * @return
	 */
	public WOComponent doValider() {
		if (getRecIntervention() == null) {
			mode = MODE_TASKBAR;
			return null;
		}
		validationComment = StringCtrl.normalize(validationComment);
		// Teste si le taille des remarques ne depasse pas de la taille
		// maximale autorisee des commentaires
		if (!dtSession().dtDataBus().checkForMaxSize("Intervention", "intCommentaireInterne",
																		validationComment, "Commentaire interne de la demande",
																		0, true, true)) {
			return reportError(dtSession().dtDataBus().getErrorMessage(), false);
		}
		boolean isDoneValidation = dtDataCenter().interventionBus().performValidation(
				dtUserInfo(),
				getRecIntervention().intOrdre(), StringCtrl.normalize(validationComment), EOEtatDt.ETAT_NON_AFFECTEES);

		if (isDoneValidation) {
			// on propose d'affecter immediatement s'il en a le droit
			boolean canDoPreAffectation = iBus().canDoPreAffectation(
					EOEtatDt.ETAT_NON_AFFECTEES, getRecIntervention().actOrdre(),
					getRecIntervention().cStructure(), dtUserInfo().noIndividu());
			if (canDoPreAffectation) {
				return askForAffectationSuiteAValidation();
			} else {
				// sinon on retourne a la liste
				return taskBarListener.getAfterValidationPage(); // Informer que la
																													// liste a ete changee
			}
		} else {
			// Sinon, afficher un message d'erreur
			return reportError("La validation de la demande n'a pas eu lieu !", false);
		}
	}

	/**
	 * Suite a validation, on peut vouloir affecter immediatement apres
	 */
	private WOComponent askForAffectationSuiteAValidation() {
		// Construction d'un message d'avertissement
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Voulez-vous affecter cette demande maintenant ?");
		return CktlAlertPage.newAlertPageWithResponder(this, "Affectation imm&eacute;diate",
				sb.toString(),
				"Oui", "Non", null, CktlAlertPage.INFO,
				new DTAffectationSuiteAValidationResponder());
	}

	/**
	 * Affiche l'ecran d'affectation, si tel est le choix d'utilisateur.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class DTAffectationSuiteAValidationResponder implements CktlAlertResponder {
		public WOComponent respondToButton(int buttonNo) {
			if (buttonNo == 1) {
				// on revient sur la page ConsultTaskBar
				return toAffecter((ConsultTaskBar.this).parent());
			}
			return taskBarListener.getAfterValidationPage();
		}
	}

	public WOComponent doRejeter() {
		if (getRecIntervention() == null) {
			mode = MODE_TASKBAR;
			return null;
		}
		if (StringCtrl.normalize(validationComment).length() == 0) {
			return reportError(
					"Veuillez indiquer le motif du rejet de la demande<br>(commentaire interne de la demande) !",
					false);
		}
		if (dtDataCenter().interventionBus().performValidation(
				dtUserInfo(),
				getRecIntervention().intOrdre(), StringCtrl.normalize(validationComment), EOEtatDt.ETAT_REJETEES))
			return taskBarListener.getAfterValidationPage(); // Informer que la liste
																												// a ete changee
		else
			// Sinon, afficher un message d'erreur
			return reportError("Le rejet de la demande n'a pas eu lieu !", false);
	}

	/* === L'affectation des demandes === */

	public boolean isAffecterVisible() {
		return (mode == MODE_AFFECTER);
	}

	/**
   */
	public boolean canAffecter() {
		return isRecInterventionExists() && iBus().canAffecter(getRecIntervention().stringForKey("intEtat"),
				getRecIntervention().numberForKey("actOrdre"),
				getRecIntervention().stringForKey("cStructure"),
				dtSession().dtUserInfo().noIndividu());
	}

	public CompAffectationListener compAffectationListener;

	public WOComponent toAffecter() {
		return toAffecter(null);
	}

	/**
	 * La page d'affectation peut etre appelee depuis une CktlAlertPage. Ce qui
	 * fait qu'on ne voit rien si on ne repositionne pas sur la page
	 * ConsultTaskBar (le composant d'affectation y est affiche en page partielle)
	 * 
	 * @param nextPage
	 * @return
	 */
	private WOComponent toAffecter(WOComponent nextPage) {
		if (!canAffecter()) {
			return reportError(
					"Cette demande ne doit pas &ecirc;tre affect&eacute;e " +
							"ou vous n'avez pas les droits n&eacute;cessaires pour effectuer cette op&eacute;ration",
					false);
		} else {
			// instancier le listener
			if (compAffectationListener == null) {
				compAffectationListener = new CompAffectationListener(getRecIntervention(), pageInspecteurDT.caller());
			}
		}
		mode = MODE_AFFECTER;
		// ne pas forcer l'ajout
		shouldForceAddTraitement = false;
		return nextPage;
	}

	/* === Les traitements et clotures des demandes === */

	public boolean isTraiterVisible() {
		return (mode == MODE_TRAITEMENT);
	}

	/**
	 * A-t-il accès aux traitement des DT
	 */
	public boolean canVoirTraitement() {
		if (!isRecInterventionExists())
			return false;
		else
			return !DTEtatBus.isEtatInitial(getRecIntervention().stringForKey("intEtat"));
	}

	/**
	 * A-t-il accès a l'ajout d'un traitement pour cette DT. Cette fonctionnalite
	 * est disponible : - les traitements ne sont pas inspectes - les traitements
	 * sont inspectes (on verifie alors qu'il n'est pas en train d'ajouter)
	 * 
	 * @return
	 */
	public boolean canAjouterTraitement() {
		boolean ok = isRecInterventionExists() && iBus().canAjouterTraitement(getRecIntervention(), dtSession().dtUserInfo().noIndividu());
		if (ok && mode == MODE_TRAITEMENT) {
			// est-ce que le temoin d'ajout de traitement est a <code>true</code>
			if (componentListTraitement != null) {
				ok = !componentListTraitement.showPanelAdd;
			} else {
				// composant <code>compAffectationListener</code> pas instancie, on
				// regarde alors le temoin d'ajout force :
				// <code>shouldForceAddTraitement</code>
				ok = !shouldForceAddTraitement;
			}
		}
		return ok;
	}

	/**
	 * Afficher la vue des traitements / clotures
	 */
	public WOComponent toVoirTraitement() {
		if (!canVoirTraitement()) {
			return getErrorPageTraiter();
		}
		// ne pas forcer l'ajout
		shouldForceAddTraitement = false;
		// notifier le composant de traitement s'il est connu
		// qu'on ne veut pas voir l'ecran d'ajout
		if (componentListTraitement != null) {
			componentListTraitement.notifyHideAddTraitement();
		}
		mode = MODE_TRAITEMENT;
		return null;
	}

	/**
	 * Afficher la vue des traitements / clotures. Positionner la fenetre pour que
	 * la saisie d'un traitement soit directement possible.
	 */
	public WOComponent toAjouterTraitement() {
		if (!canVoirTraitement()) {
			return getErrorPageTraiter();
		}
		// forcer l'ajout
		shouldForceAddTraitement = true;
		mode = MODE_TRAITEMENT;
		return null;
	}

	/**
	 * La page d'erreur si l'acces aux traitements n'est pas possible
	 */
	private WOComponent getErrorPageTraiter() {
		return reportError(
				"Cette demande n'a pas de traitements " +
						"ou vous n'avez pas les droits n&eacute;cessaires pour effectuer cette op&eacute;ration",
				false);
	}

	/* === Renvoi du mail de cloture de la demande === */

	/**
	 * L'envoi du mail de cloture n'est autorisé que si la DT est traitée et que
	 * l'utilisateur est un intervenant affecté à la demande
	 */
	public boolean canRenvoyerMailCloture() {
		return isRecInterventionExists() &&
						DTEtatBus.isEtat(getRecIntervention().stringForKey(EOIntervention.INT_ETAT_KEY), EOEtatDt.ETAT_TRAITEES) &&
						getRecIntervention().isIntervenant(dtUserInfo().noIndividu());
	}

	/**
	 * Permet à l'intervenant de renvoyer le mail automatique de cloture de la DT.
	 * TODO pour l'instant, on ne le fait pas pour les DT REPRO
	 */
	public WOComponent renvoyerMailCloture() {
		dtSession().mailCenter().prepareMailAjoutTraitement(
				(EOIntervention) getRecIntervention(),
				null,
				true,
				false,
				false,
				false,
				false,
				false,
				dtUserInfo().sendMailTraitement());
		// on va réinstancier le lister
		return closePage();
	}

	/**
	 * 
	 * @return
	 */
	public WOComponent closePage() {
		// Rafraichir la liste des DTs dans l'ecran qui affiche la liste complete
		// des DT
		// pageListDemande().setShouldRefresh(true);
		// Obtenir un nouveau composant "page mail"
		CktlMailPage mPage = (CktlMailPage) dtSession().getSavedPageWithName(
				CktlMailPage.class.getName());
		// collecter les parametres du mail
		Hashtable params = dtSession().mailCenter().collectInfoMailCloreDT();
		// Parametrer le composant
		mPage.showPage((ConsultTaskBar.this).parent(),
				dtUserInfo().email(),
				(String) params.get("to"),
				(String) params.get("cc"),
				(String) params.get("sujet"),
				(String) params.get("motif"));
		mPage.setCanChangeSubject(true);
		// On ouvre le "to" si ce dernier est vide
		boolean canChangeTo = (StringCtrl.isEmpty((String) params.get("to")));
		mPage.setCanChangeTo(canChangeTo);
		mPage.setHasFixedMessage(false);
		// d'apres les prefs
		mPage.setUseCktlMailPage(!dtUserInfo().useMailInterne());
		// raz du composant
		// setShouldReset(true);
		// Retourner pour l'affichage
		return mPage;
	}

	// appels externes

	/**
	 * Le <code>CompListeTraitement</code> en cours d'affichage se declare a
	 * partir de cette methode.
	 */
	public void registerCompListTraitement(CompListeTraitement value) {
		componentListTraitement = value;
	}

	// boolean

	/**
   * 
   */
	public boolean showTaskTitle() {
		return mode != MODE_TASKBAR;
	}

	// getters

	/**
	 * Titre
	 */
	public String getTaskTitle() {
		String title = null;
		switch (mode) {
		case MODE_AFFECTER:
			title = "Affectation";
			break;
		case MODE_TRAITEMENT:
			title = "Traitements";
			break;
		case MODE_VALIDER:
			title = "Validation";
			break;
		default:
			title = "";
		}
		return title + "&nbsp;DT&nbsp;#" + getIntCleService();
	}

	/**
	 * Pointeur vers la DT en cours d'inspection
	 */
	public EOIntervention getRecIntervention() {
		return pageInspecteurDT.demande;
	}

	/**
	 * Indique si la demande de travaux d'entree existe
	 * 
	 * @return
	 */
	private boolean isRecInterventionExists() {
		return getRecIntervention() != null;
	}

	private int getIntCleService() {
		return getRecIntervention().intCleService().intValue();
	}

	// ==+ les bus de donnees +==//

	private DTInterventionBus iBus() {
		return dtSession().dataCenter().interventionBus();
	}

	private DTEtatBus etatBus() {
		return dtSession().dataCenter().etatBus();
	}

}