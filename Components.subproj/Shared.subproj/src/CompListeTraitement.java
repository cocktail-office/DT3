import java.util.Hashtable;

import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;
import org.cocktail.fwkcktlwebapp.server.components.CktlMailPage;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;

/**
 * Composant de gestion des traitements pour une demande de travaux TODO
 * modification de fichiers joints TODO faire du cache des controleur de
 * fichiers joints TODO classement des traitements (via l'interface et par les
 * preferences)
 * 
 * @author ctarade
 */
public class CompListeTraitement
		extends DTWebComponent {

	// BINDINGS

	/** la DT pour laquelle on regarde les traitements */
	public EOIntervention eoIntervention;
	/** indique si le controleur doit etre en mode ajout force */
	public boolean shouldForceAddTraitement;

	//

	/** controleur du composant */
	public CompListeTraitementCtrl ctrl;

	/** le display groupe contenant tous les traitements */
	public WODisplayGroup traitementDg;

	/** 1 traitement dans la liste */
	public EOTraitement traitementItem;

	/** message d'erreur */
	private String errorMessage;

	/** message d'avertissement */
	public String warnMessage;

	/** faut-il afficher l'interface d'ajout de traitement */
	public boolean showPanelAdd;

	/** l'enregistrement en cours de modification */
	private EOTraitement recTraitementUpd;

	/** la classe controleur de l'interface d'ajout de traitement */
	public TraitemenListener traitementListener;

	/** l'ancre HTML a atteindre lors du prochain chargement de la page */
	public String nextAnchor;

	/** fichiers joints a un traitement en cours d'inspection */
	public boolean showDetailTraitement;
	public boolean hasTraitementItemAttachments;
	public DTFileUploadCtrl traitementItemFileUploadCtrl;

	/**
	 * temoin qui indique si le controleur du composant doit etre reinstancie :
	 * oui si la DT en cours d'inspection a change
	 */
	private boolean shouldResetCtrl;

	/** Indique si la phase d'initialisation du composant doit etre accomplie */
	private boolean shouldInitComponent;

	/** liste des intervenants */
	public CktlRecord intervenantItem;
	public CktlRecord intervenantSelected;

	public CompListeTraitement(WOContext context) {
		super(context);
		shouldInitComponent = true;
	}

	/**
	 * Re-instancier le controleur quand necessaire
	 */
	public void appendToResponse(WOResponse arg0, WOContext arg1) {
		if (shouldInitComponent) {
			initComponent();
			shouldInitComponent = false;
		}
		if (shouldResetCtrl) {
			shouldResetCtrl = false;
			resetCtrl();
		}
		// faut-il afficher l'ecran d'ajout de traitement
		if (shouldForceAddTraitement) {
			addTraitement();
			shouldForceAddTraitement = false;
		}
		super.appendToResponse(arg0, arg1);
	}

	/**
	 * Lors du changement de l'intervention inspectee, on va reinstancier le
	 * controleur
	 * 
	 * @param value
	 */
	public void setEoIntervention(EOIntervention value) {
		EOIntervention prevEoIntervention = eoIntervention;
		eoIntervention = value;
		if (prevEoIntervention == null ||
				prevEoIntervention.intOrdre().intValue() != eoIntervention.intOrdre().intValue()) {
			shouldResetCtrl = true;
		}
	}

	/**
	 * Reinitialiser le controleur associe
	 */
	private void resetCtrl() {
		ctrl = new CompListeTraitementCtrl(this, eoIntervention);
	}

	/**
	 * Initialisation du composant
	 */
	private void initComponent() {
		componentConsultTaskBar().registerCompListTraitement(this);
		nextAnchor = null;
		setErrorMessage(null);
	}

	// actions

	/**
	 * Inspecter un traitement : on affiche une seconde ligne pour le meme
	 * traitement, avec les fichiers joints, consultable, les discussions ...
	 */
	public WOComponent addDetailTraitement() {
		ctrl.addTraitementDetail(traitementItem);
		clearErrorMessage();
		nextAnchor = traitementItemAnchor();
		componentInspecteur().targetPosition = nextAnchor;
		return null;
	}

	/**
	 * Ne plus inspecter un traitement. Si le traitement etait en cours d'edition,
	 * cela revient a annuler
	 */
	public WOComponent hideDetailTraitement() {
		ctrl.removeTraitementDetail(traitementItem);
		if (showPanelUpd()) {
			recTraitementUpd = null;
			return traitementListener.cancelPage();
		}
		return null;
	}

	/**
	 * Suppression de traitement
	 */
	public WOComponent deleteTraitement() {
		StringBuffer sb = new StringBuffer();
		sb.append("<br>Voulez-vous vraiment supprimer ce traitement (traitement <b>ET</b> fichiers attach&eacute;s) ?<br><br>");
		sb.append("<table class=\"textBoxBorder\" cellspacing=0 cellpadding=1><tr><td><div class=\"textBox\">");
		sb.append(ctrl.traitementLongDisplay(traitementItem));
		sb.append("</div></td></tr></table>");
		return CktlAlertPage.newAlertPageWithResponder(componentInspecteur(),
				"Suppression de Traitement",
				sb.toString(),
				"Oui", "Non", null, CktlAlertPage.INFO,
				new DTConfirmDeleteResponder(traitementItem));
	}

	/**
	 * Classe de gestion de la page de confirmation O/N de suppression d'un
	 * traitement
	 */
	public class DTConfirmDeleteResponder implements CktlAlertResponder {
		private EOTraitement recToDelete;

		public DTConfirmDeleteResponder(EOTraitement aRecToDelete) {
			super();
			recToDelete = aRecToDelete;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 1: // SUPPRIMER : delegation du boulot au controleur
				// et retour a la page
				ctrl.delete(recToDelete);
			case 2: // ANNULER
				// aucun changement, on revient a la page precedente
			default:
				return componentInspecteur();
			}
		}
	}

	// ------------ DEB AJOUT / MODIF DE TRAITEMENT ------------

	/**
	 * Ajout d'un nouveau traitement
	 * 
	 * @return
	 */
	public WOComponent addTraitement() {
		showPanelAdd = true;
		recTraitementUpd = null;
		// traitementItem n'est pas vide si c'est une update
		traitementListener = new TraitemenListener(null);
		// on raz l'ancre
		nextAnchor = null;
		componentInspecteur().targetPosition = nextAnchor;
		// raz erreur
		clearErrorMessage();
		return null;
	}

	/**
	 * Modification de traitement
	 * 
	 * @return
	 */
	public WOComponent updateTraitement() {
		showPanelAdd = false;
		recTraitementUpd = traitementItem;
		// traitementItem n'est pas vide si c'est une update
		traitementListener = new TraitemenListener(recTraitementUpd);
		// l'ancre se positionne sur le traitement
		nextAnchor = traitementItemAnchor();
		componentInspecteur().targetPosition = nextAnchor;
		// raz erreur
		clearErrorMessage();
		return null;
	}

	/**
	 * Classe de controle du composant TraitementIntervention (DT generique)
	 */
	public class TraitemenListener
			implements I_TraitementListener {

		private boolean shouldReset;
		private boolean sendMailTraitement;
		private EOTraitement recTraitement;
		private EOIntervention recIntervention;

		/**
		 * Instancier le controleur. Si <code>aRecTraitement</code> est vide, alors
		 * c'est un ajout.
		 * 
		 * @param aRecTraitement
		 */
		public TraitemenListener(EOTraitement aRecTraitement) {
			super();
			shouldReset = true;
			sendMailTraitement = false;
			recTraitement = aRecTraitement;
			// on remonte a l'intervention si on a le traitement, sinon on fetch
			recIntervention = (recTraitement != null ?
					recTraitement.toIntervention() : eoIntervention);
		}

		public EOIntervention recDemande() {
			return recIntervention;
		}

		public EOTraitement recTraitement() {
			return recTraitement;
		}

		public boolean shouldReset() {
			return shouldReset;
		}

		public void setShouldReset(boolean doReset) {
			shouldReset = doReset;
		}

		public void setErrorMessage(String message) {
			CompListeTraitement.this.setErrorMessage(message);
		}

		public WOComponent errorPage() {
			return CktlAlertPage.newAlertPageWithCaller(
					componentInspecteur(), "Erreur de traitement", getErrorMessage(), "Retour", CktlAlertPage.ERROR);
		}

		public WOComponent cancelPage() {
			// raz du composant
			setShouldReset(true);
			return this.hidePanelAndGoBackInspecteur();
		}

		public WOComponent addPage() {
			WOComponent nextPage = null;
			if (sendMailTraitement) {
				// Obtenir un nouveau composant "page mail"
				CktlMailPage mPage = (CktlMailPage) dtSession().getSavedPageWithName("CktlMailPage");
				// collecter les parametres du mail
				Hashtable params = dtSession().mailCenter().collectInfoMailAjouterTraitementDT();
				// Parametrer le composant
				mPage.showPage(componentInspecteur(),
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
				// Retourner pour l'affichage
				nextPage = mPage;
			} else {
				// refresh de la liste des traitements
				ctrl.doReloadTraitementDg();
				nextPage = hidePanelAndGoBackInspecteur();
			}
			// raz du composant
			setShouldReset(true);
			return nextPage;
		}

		public WOComponent updatePage() {
			// refresh de la liste des traitements
			ctrl.doReloadTraitementDg();
			WOComponent nextPage = hidePanelAndGoBackInspecteur();
			// raz du composant
			setShouldReset(true);
			return nextPage;
		}

		public boolean isUpdating() {
			return recTraitement != null;
		}

		public void setSendMailTraitement(boolean doMailTraitement) {
			sendMailTraitement = doMailTraitement;
		}

		public WOComponent closePage() {
			// Rafraichir la liste des DTs dans l'ecran qui affiche la liste complete
			// des DT
			pageListDemande().setShouldRefresh(true);
			// Obtenir un nouveau composant "page mail"
			CktlMailPage mPage = (CktlMailPage) dtSession().getSavedPageWithName(
					CktlMailPage.class.getName());
			// collecter les parametres du mail
			Hashtable params = dtSession().mailCenter().collectInfoMailCloreDT();
			// Parametrer le composant
			mPage.showPage(pageListDemande(),
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
			setShouldReset(true);
			// Retourner pour l'affichage
			return mPage;
		}

		/**
		 * sortir du mode ajouter un traitement et se positionner sur l'ancre des
		 * traitements dans la page d'inspecteur
		 */
		private WOComponent hidePanelAndGoBackInspecteur() {
			// on met la balise sur les traitements
			showPanelAdd = false;
			shouldForceAddTraitement = false;
			recTraitementUpd = null;
			return componentInspecteur();
		}

		/**
		 * La reference vers la page qui a appeler la page
		 * <code>PageInspecteur</code> qui affiche ce composant
		 */
		private A_PageUsingListeDemande pageListDemande() {
			return (A_PageUsingListeDemande) componentInspecteur().caller();
		}

		public String warnMessage() {
			return warnMessage;
		}

		public void setWarnMessage(String message) {
			warnMessage = message;
		}
	}

	/**
	 * La reference vers la page <code>PageInspecteur</code> qui affiche le
	 * composant
	 */
	private InspecteurDT componentInspecteur() {
		return (InspecteurDT) parent().parent();
	}

	/**
	 * La reference vers le composant <code>ConsultTaskBar</code> qui affiche le
	 * composant
	 */
	private ConsultTaskBar componentConsultTaskBar() {
		return (ConsultTaskBar) parent();
	}

	// ------------ FIN AJOUT DE TRAITEMENT ------------

	// boolean

	/**
	 * On affiche le tableau des traitements que s'il y a deja des traitements
	 */
	public boolean showListTraitement() {
		return getTraitementDg().allObjects().count() > 0;
	}

	/**
	 * Selon s'il s'agit d'un traitement avec prestation, on va afficher soit un
	 * <code>TraitementPrestation</code> ou bien un
	 * <code>TraitementIntervention</code>
	 */
	public boolean isTraitementPrestation() {
		return componentInspecteur().isDTPrestation.booleanValue();
	}

	/**
	 * Si le traitement a des fichiers attaches, on affiche un icone avec le
	 * nombre total
	 */
	private boolean getHasTraitementItemAttachments() {
		return traitementItemTotalAttachments() > 0;
	}

	/**
	 * Indique si le traitement en cours doit etre detaille. On ne detail pas un
	 * traitement en cours d'edition
	 */
	private boolean getShowDetailTraitement() {
		return !showPanelUpd() && ctrl.isBeingDetailled(traitementItem);
	}

	/**
	 * Visibilit� de l'interface d'edition d'un traitement
	 */
	public boolean showPanelUpd() {
		return recTraitementUpd != null && (traitementItem.intForKey("traOrdre") == recTraitementUpd.intForKey("traOrdre"));
	}

	/**
	 * On autorise la modification de traitement que sur les DT dans devis (la
	 * colonne et les boutons de modif)
	 */
	public boolean showColumnUpd() {
		return !isTraitementPrestation();
	}

	/**
	 * La fleche de fermeture du detail ou de l'edition d'un traitement
	 */
	public boolean showLnkCloseDetail() {
		return showDetailTraitement || showPanelUpd();
	}

	/**
	 * La disponibilite du bouton de modification de traitement : - l'utilisateur
	 * est autorise a modifier - le traitement n'est pas deja en cours de
	 * modification
	 */
	public boolean showLnkUpdateTraitement() {
		return ctrl.canModifierTraitement(traitementItem) && !showPanelUpd();
	}

	/**
	 * La disponibilite du bouton de suppression de traitement : - l'utilisateur
	 * est autorise a modifier - le traitement n'est pas en cours de modification
	 */
	public boolean showLnkDeleteTraitement() {
		return ctrl.canDeleteTraitement(traitementItem) && !showPanelUpd();
	}

	/**
	 * Affichage du message d'erreur
	 * 
	 * @return
	 */
	public boolean showErrorMessage() {
		return !StringCtrl.isEmpty(errorMessage);
	}

	// getters

	/**
	 * Le DG des traitements
	 */
	public WODisplayGroup getTraitementDg() {
		return traitementDg;
	}

	/**
	 * L'ancre HTML associee a un traitement
	 * 
	 * @return
	 */
	public String traitementItemAnchor() {
		return Integer.toString(ctrl.getTraitementKey(traitementItem).intValue());
	}

	/**
	 * Le temps total cumule de tous les traitements
	 * 
	 * @return
	 */
	public String getTotalTimeTraitement() {
		return ctrl.getTotalTimeTraitement(getTraitementDg().displayedObjects());
	}

	// display

	/**
	 * Le nom de l'intervenant {@link #intervenantItem} dans la liste des
	 * intervenants
	 */
	public String intervenantItemDisplay() {
		return ctrl.intervenantDisplay(intervenantItem);
	}

	/**
	 * Date de debut et de fin de traitement
	 */
	public String traitementItemDateDisplay() {
		return ctrl.dateDisplay(traitementItem);
	}

	/**
	 * Le nom de l'intervenant pour le traitement en cours {@link #traitementItem}
	 */
	public String traitementItemIntervenantDisplay() {
		return ctrl.traitementIntervenantDisplay(traitementItem);
	}

	/**
	 * Affiche le contenu reduit de traitement, non formatte sans saut de ligne du
	 * traitement dans la liste. Si ce dernier n'est pas public/constable, on va
	 * le mettre en rouge.
	 */
	public String traitementItemTraitementDisplay() {
		String result = ctrl.traitementShortDisplay(traitementItem);
		return result;
	}

	/**
	 * L'infobulle de detail du traitement. En caracteres plus gros, avec le texte
	 * integral
	 */
	public String getToolTipHtmlTraitement() {
		return ctrl.toolTipHtmlTraitement(traitementItem);
	}

	/**
	 * Le nombre de fichiers attaches au traitement
	 * 
	 * @return
	 */
	public int traitementItemTotalAttachments() {
		return ctrl.countAttachmentsForTraitement(traitementItem);
	}

	/**
	 * Affiche le contenu complet de traitement (traitement detaille)
	 */
	public String traitementItemTraitementDetailDisplay() {
		return ctrl.traitementLongDisplay(traitementItem);
	}

	/**
	 * Le controleur de fichiers attaches au traitement en cours d'inspection Il
	 * est en lecture seule.
	 */
	private DTFileUploadCtrl getTraitementItemFileUploadCtrl() {
		DTFileUploadCtrl fpCtrl = new DTFileUploadCtrl();
		fpCtrl.setFilePaths(ctrl.traitementFilePaths(traitementItem));
		fpCtrl.setReadOnly(true);
		return fpCtrl;
	}

	// setters

	/**
	 * Le controleur de fichier joint n'est pas modifiable par l'interface,
	 * uniquement par le code => setter silencieux
	 */
	public void setTraitementItemFileUploadCtrl(DTFileUploadCtrl value) {

	}

	/**
	 * En passant d'un traitement a un autre, on va effectuer un traitement 1
	 * seule fois. L'interface peut appeler 2 fois un getter, et ainsi flooder
	 * (ex: GEDFS...) TODO : en fermeture d'inspection, ce setter est appelee
	 * avant ... d'ou double inspection et donc rappel GEDFS ......
	 */
	public void setTraitementItem(EOTraitement value) {
		traitementItem = value;
		if (traitementItem != null) {
			// on fait les traitements qui ne doivent pas etre appeles par l'interface
			// via getter
			showDetailTraitement = getShowDetailTraitement();
			hasTraitementItemAttachments = getHasTraitementItemAttachments();
			// on n'instancie le controleur que s'il y a des fichiers attaches et
			// que le traitement est inspecte
			if (hasTraitementItemAttachments && showDetailTraitement) {
				traitementItemFileUploadCtrl = getTraitementItemFileUploadCtrl();
			}
		}
	}

	// appel externe

	/**
	 * Notification depuis la barre <code>ConsultTaskBar</code> que l'on ne
	 * souhaite pas afficher l'ecran d'ajout s'il est affiche
	 */
	protected void notifyHideAddTraitement() {
		showPanelAdd = false;
	}

	// actions

	/**
	 * Filtrer la liste des traitements selon la selection de l'intervenant
	 * {@link #intervenantSelected}
	 */
	public WOComponent doFiltrerTraitementDgParIntervenant() {
		ctrl.doFiltrerTraitementDgParIntervenant(intervenantSelected);
		return null;
	}

	public final String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @see
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (!StringCtrl.isEmpty(errorMessage)) {
			dtSession().addSimpleErrorMessage("Erreur", errorMessage);
		}
	}

	private void clearErrorMessage() {
		setErrorMessage(null);
	}
}