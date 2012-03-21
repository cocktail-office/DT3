import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

public class PageRechercheNew
		extends A_PageUsingListeDemande
		implements I_SelectActiviteReferer, IDTLongPageCaller {

	/** la configuration de recherche en cours */
	public DTParamConfig config;

	/** item node parametre */
	public DTParamConfig.DTParamLeaf natureNodeItem;

	/** selection */
	public DTParamConfig.DTParamLeaf natureNodeSelected;

	/** item parametre */
	public Integer natureItem;

	/** item contrainte */
	public Integer constraintItem;

	/** item element */
	public Object elementItem;

	/** afficher le panneau de commande */
	public boolean showSearchPanel;

	/** afficher les resultats */
	public boolean showCompListeDemande;

	/** bookmark en cours de creation / edition : le libelle */
	public String prbLibelle;
	private final static String PRB_LIBELLE_DEFAULT = "nouveau favori";

	/** liste de bookmarks */
	public NSArray<CktlRecord> bookmarkList;
	public CktlRecord bookmarkItem;
	private CktlRecord bookmarkSelected;

	public PageRechercheNew(WOContext context) {
		super(context);
		initComponent();
	}

	protected void initComponent() {
		super.initComponent();
		// initialisation des parametres
		config = new DTParamConfig(dtSession());
		addNode();
		// instancier le listener d'affichage des resultats
		listener = new ListenerDTRecherche();
		//
		showSearchPanel = true;
		showCompListeDemande = false;
		// lister les bookmarks
		reloadBookmarkList();
		bookmarkSelected = null;
		prbLibelle = PRB_LIBELLE_DEFAULT;
	}

	// navigation

	public WOComponent addNode() {
		config.addNode();
		return null;
	}

	public WOComponent removeNode() {
		config.removeNode(natureNodeItem);
		return null;
	}

	/**
	 * Selectionner un autre individu
	 */
	public WOComponent changePersonne() {
		natureNodeSelected = natureNodeItem;
		return SelectPersonne.getNewPage(new RecherchePersonneListener(),
							"Indiquez la personne sur laquelle vous voulez effectuer la recherche",
							(Integer) natureNodeSelected.elementSelected, true);
	}

	/**
	 * Listener de selection de l'individu pour la recherche
	 */
	private class RecherchePersonneListener implements SelectPersonneListener {
		public WOComponent select(Number persId) {
			CktlRecord recIndividu = individuBus().individuForPersId(persId);
			if (recIndividu != null) {
				natureNodeSelected.elementSelected = new Integer(recIndividu.intForKey("noIndividu"));
			}
			return PageRechercheNew.this;
		}

		public WOComponent cancel() {
			return PageRechercheNew.this;
		}

		public WOContext getContext() {
			return context();
		}
	}

	/**
	 * Selectionner une autre activite
	 * 
	 * @return
	 */
	public WOComponent changeActivite() {
		natureNodeSelected = natureNodeItem;
		SelectActivite pageActivite = (SelectActivite) pageWithName(SelectActivite.class.getName());
		RechercheActiviteListener activiteListener = new RechercheActiviteListener();
		// on autorise le changement de service : faire l'initialisation associee
		activiteListener.initServiceList();
		pageActivite.setListener(activiteListener);
		return pageActivite;
	}

	/**
	 * Listener de selection de l'activite pour la recherche
	 */
	public class RechercheActiviteListener extends SelectActiviteListener {

		public void doAfterActiviteSelectedItem() {
			// on memorise l'activite dans le node selectionne
			natureNodeSelected.elementSelected = new Integer(getActiviteSelectedItem().nodeRecord.intForKey("actOrdre"));
		}

		/** on passe <em>null</em> pour pouvoir changer le service */
		protected CktlRecord recVActivite() {
			return null;
		}

		public Session session() {
			return dtSession();
		}

		public NSArray allNodes() {
			return session().activitesNodes();
		}

		public String formName() {
			return null;
		}

		public void doAfterSearchActivite() {

		}

		public WOComponent caller() {
			return PageRechercheNew.this;
		}

		public boolean shouldSelectedOnlyLeaf() {
			return false;
		}

		public boolean showHiddenActivite() {
			boolean show = false;

			if (serviceSelected != null) {
				show = dtUserInfo().hasDroit(
						DTUserInfo.DROIT_INTERVENANT_LIMITE,
						serviceSelected.stringForKey(EOStructure.C_STRUCTURE_KEY));
			}

			return show;
		}

		/** selon le profil */
		public boolean showUnderscoredActivite() {
			boolean show = false;

			if (serviceSelected != null) {
				show = interventionBus().canViewActiviteUnderscore(
						serviceSelected.stringForKey(EOStructure.C_STRUCTURE_KEY), dtUserInfo().noIndividu());
			}

			return show;
		}

		@Override
		public boolean showActivitesFavoritesDemandeur() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean showActivitesFavoritesIntervenant() {
			// TODO Auto-generated method stub
			return false;
		}

	}

	// display

	public String natureItemDisplay() {
		return config.natureDisplay(natureItem);
	}

	public String constraintItemDisplay() {
		return config.constraintDisplay(constraintItem);
	}

	public String elementItemDisplay() {
		return config.elementDisplay(elementItem);
	}

	/**
	 * Pour un enregistrement qui n'est pas propos� dans une liste, on va afficher
	 * son libelle (ex: individu)
	 */
	public String individuDisplay() {
		return config.elementDisplay(
				individuBus().individuForNoIndividu((Number) natureNodeItem.elementSelected));
	}

	/**
	 * Pour un enregistrement qui n'est pas propos� dans une liste, on va afficher
	 * son libelle (ex: individu)
	 */
	public String activiteDisplay() {
		Integer actOrdre = (Integer) natureNodeItem.elementSelected;
		if (actOrdre != null) {
			return config.elementDisplay(activiteBus().findActivite((Number) actOrdre, null));
		} else {
			return "<vide>";
		}
	}

	/**
	 * Le bookmark en cours est mis en gras
	 */
	public String bookmarkItemDisplay() {
		String itemPrbLibelle = bookmarkItem.stringForKey("prbLibelle");
		StringBuffer sb = new StringBuffer(itemPrbLibelle);
		if (bookmarkSelected != null && bookmarkSelected.stringForKey("prbLibelle").equals(itemPrbLibelle)) {
			sb.insert(0, "<b>");
			sb.append("</b>");
		}
		return sb.toString();
	}

	// boolean

	/**
	 * On autorise pas la suppression du dernier node
	 */
	public boolean isDisabledBtnRemoveNode() {
		return config.nodeList.count() <= 1;
	}

	/**
	 * Le parametre impose un choix parmi une liste
	 */
	public boolean isElementList() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_LIST;
	}

	/**
	 * Le parametre impose un choix d'activite
	 */
	public boolean isElementActivite() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_ACTIVITE;
	}

	/**
	 * Le parametre impose un choix de date
	 */
	public boolean isElementDate() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_DATE;
	}

	/**
	 * Le parametre impose une chaine de caractere
	 */
	public boolean isElementString() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_STRING;
	}

	/**
	 * Le parametre impose un nombre
	 */
	public boolean isElementNumber() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_NUMBER;
	}

	/**
	 * Le parametre impose un choix d'individu
	 */
	public boolean isElementIndividu() {
		return config.getElementTypeForNature(natureNodeItem) == DTParamConfig.ELEMENT_TYPE_INDIVIDU;
	}

	/**
	 * On ne duplique que s'il y a une selection
	 */
	public boolean isDisabledBtnCopyBookmark() {
		return bookmarkSelected == null;
	}

	/**
	 * On ne peut supprimer qu'un bookmark selectionn�
	 */
	public boolean isDisabledBtnDeleteBookmark() {
		return bookmarkSelected == null;
	}

	// AFFICHAGE DES RESULTATS

	/**
	 * La classe utilisee pour la gestion de l'affichage des resultats.
	 */
	private class ListenerDTRecherche implements I_CompListeDemandeListener {

		public A_PageUsingListeDemande caller() {
			return pageRecherche();
		}

		// pas precis�
		public int modeUtilisateur() {
			return -1;
		}

		// colonne des intervenants : oui
		public boolean showColumnIntervenants() {
			return true;
		}

		// colonne validation : non
		public boolean showColumnValider() {
			return false;
		}

		// colonne affectaion : depend du niveau global
		public boolean showColumnAffecter() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT);
		}

		// colonne traitements : oui
		public boolean showColumnTraiter() {
			return true;
		}

		// TODO
		public boolean showColumnDiscussion() {
			return true;
		}

		// colonne ajout traitement : depend du niveau global
		public boolean showColumnAjoutTraitement() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);
		}

		// colonne etat : oui
		public boolean showColumnEtat() {
			return true;
		}

		// colonne suppression : depend du niveau global
		public boolean showColumnSupprimer() {
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);
		}

		public WODisplayGroup interventionDisplayGroup() {
			return getInterventionDisplayGroup();
		}

		public WOComponent doFetchDisplayGroup() {
			interventionDisplayGroup().setNumberOfObjectsPerBatch(getNumberOfObjectsPerBatch().intValue());
			interventionDisplayGroup().setSortOrderings(getSortForParams());

			// on controle la fetch limit pour eviter les OutOfMemory
			DTEOEditingContextHandler handler = new DTEOEditingContextHandler();

			/*
			 * dtSession().dataBus().fetchTable( interventionDisplayGroup(),
			 * config.getQualifier(), null, false, MAX_FETCH_INTERVENTION);
			 */

			// mémorisation avant filtrage de surcout
			NSArray<EOIntervention> eoInterventionArray = EOIntervention.fetchAll(
					interventionDisplayGroup().dataSource().editingContext(),
					config.getQualifier(),
					null,
					true,
					EOIntervention.MAX_FETCH_LIMIT_RECHERCHE,
					handler);
			// classement
			eoInterventionArray = EOSortOrdering.sortedArrayUsingKeyOrderArray(eoInterventionArray, getSortForParams());
			interventionDisplayGroup().setObjectArray(eoInterventionArray);

			// on sauvegarde si le depassement est atteint
			limitFetchExceeded = handler.getFetchLimitWasExceeded();
			// si des resultats, on masque le formulaire de recherche
			if (interventionDisplayGroup().allObjects().count() > 0) {
				showSearchPanel = false;
			}
			// on affiche les resultats des la premiere recherche
			showCompListeDemande = true;
			return null;
		}

		public String warnMessage() {
			return (limitFetchExceeded ?
					"D'autres résultats ont été trouvés, veuillez affiner vos filtres pour les afficher" : null);
		}

		/**
		 * Page de gestion de masquage dans la recherche
		 */
		public boolean useInterventionMasquee() {
			return false;
		}

		/**
		 * On affiche tout, independemment des masques
		 */
		public boolean showInterventionMasquee() {
			return true;
		}

		/**
		 * Pas d'information complementaires sur le nombre de DTs trouvees
		 */
		public String strResultCommentSuffix() {
			return StringCtrl.emptyString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see I_CompListeDemandeListener#showColumnPanier()
		 */
		public boolean showColumnAjouterPanier() {
			// pour l'instant, que pour les intervenants
			return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see I_CompListeDemandeListener#showColumnSupprimerPanier()
		 */
		public boolean showColumnSupprimerPanier() {
			return false;
		}
	}

	/**
	 * Pointeur vers cette propore page (utilise par la classe
	 * ListenerDTRecherche)
	 */
	private PageRechercheNew pageRecherche() {
		return this;
	}

	/**
	 * Lancer la recherche
	 * 
	 * @return
	 */
	public WOComponent doSearch() {
		DTLongPage longPage = (DTLongPage) pageWithName(DTLongPage.class.getName());
		longPage.setComponent(this);
		return longPage;
	}

	/**
	 * Remettre a zero le composant
	 * 
	 * @return
	 */
	public WOComponent doClear() {
		initComponent();
		return null;
	}

	// BOOKMARKS

	/**
	 * Charger et remplir le formulaire avec le bookmark selectionn�
	 */
	public WOComponent doLoadBookmark() {
		doLoadBookmark(bookmarkItem);
		return null;
	}

	private void doLoadBookmark(CktlRecord value) {
		bookmarkSelected = value;
		if (bookmarkSelected != null) {
			config.restore(bookmarkSelected.stringForKey("prbBookmark"));
			prbLibelle = bookmarkSelected.stringForKey("prbLibelle");
		}
	}

	/**
	 * Enregistrer un nouveau bookmark ou modifier un existant.
	 */
	public WOComponent doSaveBookmark() {
		if (!StringCtrl.isEmpty(prbLibelle)) {
			boolean isUpdate = (bookmarkSelected != null);
			if (isUpdate) {
				preferencesBus().deletePrefRechBookmark(null, bookmarkSelected);
			}
			CktlRecord rec = preferencesBus().updatePrefRechBookmark(
					null, dtUserInfo().noIndividu(), prbLibelle, config.toStorableString());
			reloadBookmarkList();
			doLoadBookmark(rec);
		}
		return null;
	}

	/**
	 * Dupliquer le bookmark en cours
	 */
	public WOComponent doCopyBookmark() {
		if (!StringCtrl.isEmpty(prbLibelle)) {
			CktlRecord rec = preferencesBus().updatePrefRechBookmark(
					null, dtUserInfo().noIndividu(), prbLibelle + " - copie", config.toStorableString());
			reloadBookmarkList();
			doLoadBookmark(rec);
		}
		return null;
	}

	/**
	 * Supprimer le bookmark selectionn�
	 */
	public WOComponent doDeleteBookmark() {
		if (bookmarkSelected != null && preferencesBus().deletePrefRechBookmark(null, bookmarkSelected)) {
			reloadBookmarkList();
			bookmarkSelected = null;
			prbLibelle = PRB_LIBELLE_DEFAULT;
		}
		return null;
	}

	/**
	 * Recharger la liste des bookmarks
	 */
	private void reloadBookmarkList() {
		bookmarkList = preferencesBus().getAllPrefRechBookmark(dtUserInfo().noIndividu());
	}

	/**
	 * Preparer le formulaire pour un nouveau favori
	 */
	public WOComponent doNewDefaultBookmark() {
		bookmarkSelected = null;
		prbLibelle = PRB_LIBELLE_DEFAULT;
		return null;
	}

	// bus de donnees

	private DTIndividuBus individuBus() {
		return dtSession().dataCenter().individuBus();
	}

	private DTActiviteBus activiteBus() {
		return dtSession().dataCenter().activiteBus();
	}

	private DTPreferencesBus preferencesBus() {
		return dtSession().dataCenter().preferencesBus();
	}

	private DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	// *****
	// Obligations interface <code>I_SelectActiviteReferer</code>
	// Ne sert pas dans cet ecran car tout se fait dans la methode
	// <code>doAfterActiviteSelectedItem()</code>
	// *****
	public void saveUpdateActiviteBySelectActivite() {
	}

	public void setActiviteSelectedBySelectActivite(CktlRecord value) {
	}

	public void setMotsClesSelectedBySelectActivite(String value) {
	}

	// *****
	// Obligations classe heritee <code>A_PageUsingListeDemande</code>
	// Ne sert pas dans cet ecran car l'initialisation se fait toute seule dans
	// <code>initComponent()</code>
	// *****
	protected void initDefaultConfig() {
	}

	// ****
	// Obligations interface <code>IDTLongPageCaller</code>
	//

	public void doTheJob() {
		searchStart = System.currentTimeMillis();
		listener.doFetchDisplayGroup();
		// on masque le panneau de recherche s'il y a des resultats
		if (getInterventionDisplayGroup().displayedObjects().count() > 0) {
			showSearchPanel = false;
		}
	}

	public WOComponent finishPage() {
		return PageRechercheNew.this;
	}

	// le temps depuis le lancement de la recherche
	private long searchStart;

	public String htmlAdvanceString() {
		long searchDurationSeconds = (System.currentTimeMillis() - searchStart) / 1000;
		return "Temps &eacute;coul&eacute; : " + (int) searchDurationSeconds + " s.";
	}

}