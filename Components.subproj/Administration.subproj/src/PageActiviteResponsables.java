import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOActivitesResponsables;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Ecran de gestion des responsables des activites
 * 
 * @author ctarade
 */

public class PageActiviteResponsables
		extends DTWebComponent {

	// les 3 niveaux de responsabilites
	private final String RESP_FONCTIONNEL = EOActivites.TYPE_RESP_FONCTIONNEL;
	private final String RESP_TECHNIQUE = EOActivites.TYPE_RESP_TECHNIQUE;
	private final String RESP_INTERVENANT = EOActivites.TYPE_RESP_INTERVENANT;
	private String currentNiveau;

	// la liste des responsables
	public NSArray<EOActivitesResponsables> responsableList;
	public EOActivitesResponsables responsableItem;
	public NSArray<EOActivitesResponsables> responsableSelecteds;

	// le nombre de responsable par niveau
	public int totalFonctionnel;
	public int totalTechnique;
	public int totalIntervenant;

	/** etat d'utilisation du composant */
	public int curMode;
	public final static int MODE_AJOUT = 0;
	public final static int MODE_MODIF = 1;
	public final static int MODE_VIEW = 2;

	/** */
	public boolean isEnvoyerMail;
	/** l'activite en cours de traitement */
	public EOVActivites recActivite;
	/** indique si la definition est heritee */
	private boolean isHeritee;
	/** le nom de l'activite heritee */
	public String actPereLibelle;

	/** le style css pour la selection du type de responsable */
	private final static String STYLE_LNK_SELECTED = "font-weight: bold";

	/** le style css pour mettre en avant les defintions heritees */
	private final static String STYLE_TABLE_HERITE = "border: 1px #888888 solid; background-color: #DDDDDD";

	// les infos bulles de legende pour chacun des 3 types de responsables
	private final static String HTML_RESP_INTERVENANT =
			"<u>Responsable intervenant</u>" +
					"<ul>" +
					"<li>Est préselectionné dans la liste des agents dans l'écran d'affectation</li>" +
					"</ul>";
	private final static String HTML_RESP_TECHNIQUE =
			"<u>Responsable technique</u>" +
					"<ul>" +
					"<li>Droit d'affectation</li>" +
					"<li>Droit de suppression d'affectation</li>" +
					"<li>Droit de validation</li>" +
					"<li>Destinataire des mails : " +
					"<ul>" +
						"<li>de création de DTs (si leur état initial est non &quot;affectée&quot;)</li>" +
						"<li>de validation</li>" +
						"<li>d'ajout de traitement (si l'intervenant le souhaite)</li>" +
						"<li>de cloture</li>" +
					"</ul>" +
					"</li>" +
					"<li>S'il est aussi intervenant limité : visibilité en tant qu'intervenant sur les DTs" +
					"<ul>" +
						"<li>non affectées</li>" +
						"<li>non validées</li>" +
					"</ul>" +
					"</li>" +
					"<li>Est préselectionné dans la liste des agents dans l'écran d'affectation</li>" +
					"</ul>";
	private final static String HTML_RESP_FONCTIONNEL =
			"<u>Responsable fonctionnel</u>" +
					"<ul>" +
					"<li>Droit de validation directement après création</li>" +
					"<li>Destinataire des mails : " +
					"<ul>" +
						"<li>de création de DTs (si leur état initial est non &quot;validée&quot;)</li>" +
						"<li>de rejet</li>" +
					"</ul>" +
					"</li>" +
					"</ul>";

	public PageActiviteResponsables(WOContext context) {
		super(context);
	}

	/**
	 * On initialise les donnees et on passe en mode lecture.
	 */
	public void init(EOVActivites aRecord) {
		currentNiveau = RESP_FONCTIONNEL;
		recActivite = aRecord;
		fillComponent();
		// on definit le mode d'utilisation selon l'heritage
		if (isHeritee()) {
			curMode = MODE_VIEW;
		} else {
			curMode = MODE_MODIF;
		}
	}

	// actions

	public WOComponent ajouterResponsable() {
		return SelectPersonne.getNewPage(new NewResponsableListener(this),
				"Indiquez la personne responsable", null, true);
	}

	/**
	 * Implemente les methode necessaires pour communiquer avec la page de la
	 * selection des personnes. Permet de choisir la personne a qui affecter le
	 * droit.
	 * 
	 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
	 */
	private class NewResponsableListener implements SelectPersonneListener {

		private WOComponent caller;

		public NewResponsableListener(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		/*
		 * @see PersonneSelectListener#select(java.lang.Number, java.lang.Number)
		 */
		public WOComponent select(Number persId) {
			if (persId != null) {
				CktlUserInfo userInfo = new CktlUserInfoDB(dtSession().dataBus());
				userInfo.compteForPersId(persId, true);
				Number noIndividu = new Integer(userInfo.noIndividu().intValue());
				Number actOrdre = recActivite.actOrdre();
				if (activiteBus().addResponsable(null, noIndividu, actOrdre, currentNiveau) == null) {
					return getErrorPage(caller, "Erreur d'ajout",
							"Le nouveau responsable n'a pas pu etre ajouté &agrave; la liste !");
				} else {
					fillComponent();
				}
			}
			return caller;
		}

		/*
		 * @see PersonneSelectListener#cancel()
		 */
		public WOComponent cancel() {
			return caller;
		}

		/*
		 * @see PersonneSelectListener#context()
		 */
		public WOContext getContext() {
			return context();
		}
	}

	/**
	 * Suppression du responsable selectionne
	 * 
	 * @return
	 */
	public WOComponent supprimerResponsable() {
		if (responsableSelecteds == null || responsableSelecteds.count() == 0) {
			return getErrorPage(this, "Selection vide", "Aucune personne n'a été sélectionnée dans la liste !");
		}
		CktlRecord recResponsable = (CktlRecord) responsableSelecteds.lastObject();
		return CktlAlertPage.newAlertPageWithResponder(this, "Suppression de responsable",
				"Voulez-vous vraiment supprimer \"" + recResponsable.valueForKeyPath("toIndividuUlr.nomEtPrenom") + "\" de la liste",
				"Supprimer", "Annuler", null, CktlAlertPage.QUESTION,
				new ConfirmSuppResponsableResponder(this, recResponsable));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la confirmation
	 * de suppression d'un responsable
	 */
	public class ConfirmSuppResponsableResponder implements CktlAlertResponder {
		private WOComponent caller;
		private CktlRecord recResponsable;

		public ConfirmSuppResponsableResponder(WOComponent aCaller, CktlRecord aRecResponsable) {
			super();
			caller = aCaller;
			recResponsable = aRecResponsable;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				if (!activiteBus().deleteResponsable(null,
								recResponsable.numberForKey("noIndividu"),
								recActivite.actOrdre(), currentNiveau)) {
					return getErrorPage(caller, "Erreur de suppression",
										"Des erreurs ont survenu lors de la suppression");
				}
			default:
				fillComponent();
				return caller;
			}
		}
	}

	public WOComponent supprimerAllResponsable() {
		return CktlAlertPage.newAlertPageWithResponder(this, "Suppression de responsable",
				"Voulez-vous vraiment supprimer toutes les définitions des responsables ?",
				"Supprimer", "Annuler", null, CktlAlertPage.QUESTION,
				new ConfirmSuppAllResponsableResponder(this));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la confirmation
	 * de suppression de tous les responsables
	 */
	public class ConfirmSuppAllResponsableResponder implements CktlAlertResponder {
		private WOComponent caller;

		public ConfirmSuppAllResponsableResponder(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				if (!activiteBus().deleteAllResponsables(null, recActivite.actOrdre())) {
					return getErrorPage(caller, "Erreur de suppression",
											"Des erreurs ont survenu lors de la suppression des responsables !");
				}
			default:
				fillComponent();
				return caller;
			}
		}
	}

	/**
	 * Action par defaut du formulaire, executee lors du changement de la
	 * selection du radio type de responsable. Ne fait rien, a part rafraichir la
	 * liste des responsables
	 * 
	 * @return
	 */
	public WOComponent doNothing() {
		return null;
	}

	/**
	 * Creer une definition locale
	 */
	public WOComponent doChangeToLocalPref() {
		return CktlAlertPage.newAlertPageWithResponder(this,
				"Définition locale de responsables",
				"Voulez-vous vraiment redéfinir l'ensemble des responsables ?",
				"Redéfinir", "Annuler", null, CktlAlertPage.QUESTION, new ConfirmCreateLocalDefResponder(this));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la confirmation
	 * de suppression de defintion locale
	 */
	public class ConfirmCreateLocalDefResponder implements CktlAlertResponder {
		WOComponent caller;

		public ConfirmCreateLocalDefResponder(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				Integer transactionId = activiteBus().beginECTransaction();
				curMode = MODE_MODIF;
				activiteBus().insertActiviteResponsables(transactionId,
						recActivite.actOrdre(),
						recActivite.actPere());
				activiteBus().commitECTransaction(transactionId);
			default:
				fillComponent();
				return caller;
			}
		}
	}

	/**
	 * Supprimer la definition locale
	 */
	public WOComponent doChangeToPerePref() {
		// on supprime pas la definition locale d'une racine
		if (recActivite.actOrdre().intValue() < 0) {
			return getErrorPage(this, "Suppression impossible",
					"La définition des responsables ne peut pas être supprimée<br>dans la racine des activités !");
		}
		return CktlAlertPage.newAlertPageWithResponder(this,
				"Suppression de definition locale",
				"Voulez-vous vraiment supprimer ces définitions des responsables<br>" +
						" (les définitions seront héritées d'une des activités pères) ?",
				"Supprimer", "Annuler", null, CktlAlertPage.QUESTION, new ConfirmDeleteLocalDefResponder(this));
	}

	/**
	 * La classe interne - l'implementation de AlertResponder pour la confirmation
	 * de suppression de defintion locale
	 */
	public class ConfirmDeleteLocalDefResponder implements CktlAlertResponder {
		WOComponent caller;

		public ConfirmDeleteLocalDefResponder(WOComponent aCaller) {
			super();
			caller = aCaller;
		}

		public WOComponent respondToButton(int buttonNo) {
			switch (buttonNo) {
			case 2:
				return caller;
			case 1:
				if (!activiteBus().deleteResponsablesNoeud(null, recActivite.actOrdre())) {
					return getErrorPage(caller, "Erreur de suppression",
							"Des erreurs ont survenu lors de la suppression des responsables !");
				}
			default:
				curMode = MODE_VIEW;
				fillComponent();
				return caller;
			}
		}
	}

	/**
	 * Retourner a la page de gestion des activites.
	 * 
	 * @return
	 */
	public WOComponent revenir() {
		return dtSession().getSavedPageWithName(PageAdministration.class.getName());
	}

	// methodes internes

	/**
	 * Retrouver la liste des responsables selon le niveau
	 * <code>currentNiveau</code>.
	 */
	private void fillComponent() {

		Number actOrdre = recActivite.actOrdre();

		// determiner si heritage ou pas
		if (activiteBus().findTypeRespDefinition(actOrdre) == EOActivites.RESP_HERITE) {
			setIsHeritee(true);
		} else {
			setIsHeritee(false);
		}
		// definir depuis quelle activite lire les infos
		if (isHeritee()) {
			actOrdre = recActivite.toActResp().actOrdre();
			actPereLibelle = recActivite.toActResp().actLibelle();
		}
		// la liste des responsables pour le niveau en cours
		responsableList = activiteBus().findResponsables(actOrdre, currentNiveau);
		// le total de responsable par niveaux
		totalFonctionnel = activiteBus().countResponsables(actOrdre, RESP_FONCTIONNEL);
		totalTechnique = activiteBus().countResponsables(actOrdre, RESP_TECHNIQUE);
		totalIntervenant = activiteBus().countResponsables(actOrdre, RESP_INTERVENANT);
		// envoi mail
		isEnvoyerMail = activiteBus().getServiceMailForActivite(actOrdre).equals("O");
	}

	/**
	 * Affichage d'une page d'erreur avec bouton de retour
	 * 
	 * @param caller
	 * @param errTitle
	 * @param errMessage
	 * @return
	 */
	private WOComponent getErrorPage(WOComponent caller, String errTitle, String errMessage) {
		return CktlAlertPage.newAlertPageWithCaller(caller, errTitle, errMessage, "Revenir", CktlAlertPage.ERROR);
	}

	// setters

	/**
	 * Afficher les responsables fonctionnels
	 */
	public WOComponent goNiveauFonctionnel() {
		setCurrentNiveau(RESP_FONCTIONNEL);
		return null;
	}

	/**
	 * Afficher les responsables fonctionnels
	 */
	public WOComponent goNiveauTechnique() {
		setCurrentNiveau(RESP_TECHNIQUE);
		return null;
	}

	/**
	 * Afficher les responsables fonctionnels
	 */
	public WOComponent goNiveauIntervenant() {
		setCurrentNiveau(RESP_INTERVENANT);
		return null;
	}

	/**
	 * Changement du type de responsabilite : on rafraichit la liste des
	 * responsables.
	 */
	private void setCurrentNiveau(String value) {
		currentNiveau = value;
		fillComponent();
	}

	// interface

	public boolean isDisabledInterface() {
		return curMode != MODE_MODIF;
	}

	/**
	 * La table des responsables grisee dans le cas d'une definition heritee
	 */
	public String styleTableResponsable() {
		StringBuffer style = new StringBuffer("");
		if (isHeritee()) {
			style.append(STYLE_TABLE_HERITE);
		}
		return style.toString();
	}

	/**
	 * Affichage du lien niveau fonctionnel
	 */
	public String styleNiveauFonctionnel() {
		if (currentNiveau.equals(RESP_FONCTIONNEL)) {
			return STYLE_LNK_SELECTED;
		}
		return "";
	}

	/**
	 * Affichage du lien niveau technique
	 */
	public String styleNiveauTechnique() {
		if (currentNiveau.equals(RESP_TECHNIQUE)) {
			return STYLE_LNK_SELECTED;
		}
		return "";
	}

	/**
	 * Affichage du lien niveau intervenant
	 */
	public String styleNiveauIntervenant() {
		if (currentNiveau.equals(RESP_INTERVENANT)) {
			return STYLE_LNK_SELECTED;
		}
		return "";
	}

	public String getHtmlTextRespIntervenant() {
		return HTML_RESP_INTERVENANT;
	}

	public String getHtmlTextRespTechnique() {
		return HTML_RESP_TECHNIQUE;
	}

	public String getHtmlTextRespFonctionnel() {
		return HTML_RESP_FONCTIONNEL;
	}

	// bus de donnees

	private DTActiviteBus activiteBus() {
		return dtSession().dataCenter().activiteBus();
	}

	public final boolean isHeritee() {
		return isHeritee;
	}

	public final void setIsHeritee(boolean isHeritee) {
		this.isHeritee = isHeritee;
	}

}