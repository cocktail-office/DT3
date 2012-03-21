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

import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOPrefDroits;
import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;

/**
 * Gestionnaire de la page de consultation des demandes.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class PageConsultation
		extends A_PageUsingListeDemande {

	public RechercheDTConfig configIntervenant, configDemandeur;

	public final int USER_DEMANDEUR = RechercheDTConfig.USER_DEMANDEUR;
	public final int USER_INTERVENANT = RechercheDTConfig.USER_INTERVENANT;
	public int modeUtilisateur;

	private EOQualifier qualifierExternal;

	/** rafraichissement automatique de la page */
	public Integer secondesRefresh;
	public boolean isAutoRefresh;
	private static String codeRefreshTime =
			"var delai = setTimeout('refresh()', $1);\n" +
					"function refresh() \n " +
					"{\n" +
					"  formeRecherche.btnAutoRefresh.click();\n" +
					"};";

	/** la liste des services ou l'intervenant traite des demande */
	public NSArray<EOStructure> serviceList;
	public EOStructure serviceItem;
	public EOStructure serviceSelected;
	public boolean showServiceList;

	/** afficher le radio (intervenant/demandeur) */
	public boolean showRadioIntDemandeur;

	/** afficher le bouton "raz parametres" */
	public boolean showBtnNouvelleRecherche;

	/**
	 * Afficher la fonction de masquage des demandes de travaux. Elle n'est
	 * utilisee que si <code>listener.useInterventionMasquee()</code> est a
	 * <code>true</code>.
	 */
	public boolean showInterventionMasquee = false;

	// liste des periodes d'interrogation
	public NSArray<String> periodList;
	public String periodSelected;
	public String periodItem;
	public boolean showPeriodeList;

	/**
	 * Affichier les Dts de commande de matériel V2 dont le paiement du surcout a
	 * été refusé
	 */
	public boolean showDtSurcoutRefuse = false;

	/**
	 * Affichier les Dts de commande de matériel V2 dont la restitution d'un poste
	 * de remplacement n'a pas été faite
	 */
	public boolean showDtRemplacementNonRestitue = false;

	/**
   *
   */
	public PageConsultation(WOContext context) {
		super(context);
	}

	public void appendToResponse(WOResponse arg0, WOContext arg1) {
		// a chaque rechargement, on masque/montre certains elements
		resetInterfacePermissions();
		super.appendToResponse(arg0, arg1);
	}

	/**
	 * Initialisation des preferences.
	 */
	private void initPrefs() {
		if (dtUserInfo().timer() == null) {
			initPrefsDefault();
		} else {
			// Preferences Utilisateur
			initConfigFromUserInfo();
			// Initialisation de l'etat en fct des preferences
			String etatCode = dtUserInfo().startEtatCode();
			if (StringCtrl.normalize(etatCode).length() > 0) {
				if (dtDataCenter().etatBus().libelleForEtat(etatCode).length() > 0) {
					configIntervenant.setEtatDtCode(etatCode);
					configDemandeur.setEtatDtCode(etatCode);
				}
			}
		}
	}

	/**
	 * Initialisation a partir des preferences personnelles.
	 */
	private void initConfigFromUserInfo() {
		EOQualifier qualifier = null;
		// ** d'apres les preferences **
		// Timer
		secondesRefresh = null;
		if (dtUserInfo().timer() != null)
			secondesRefresh = new Integer(dtUserInfo().timer().intValue());
		isAutoRefresh = (secondesRefresh != null);

		// si plusieurs services, alors on selectionne celui des preferences
		String prfDroService = pBus().getDefaultServiceCode(dtUserInfo().noIndividu());
		if (showServiceList && !StringCtrl.isEmpty(prfDroService))
			setServiceSelected(sBus().structureForCode(prfDroService));
		// Les conditions de filtre par defaut pour la liste des DT
		// Dans le cas d'un intervenant
		Number noIndividu = dtUserInfo().noIndividu();
		StringBuffer condition = new StringBuffer();
		StringBuffer condInd = new StringBuffer();
		String noIntervenats = StringCtrl.normalize(dtUserInfo().noIntervenats());
		// Si les intervenants on ete explicitement precises...
		if (noIntervenats.length() > 0) {
			NSArray<String> arrayIntervenantsPrefs = NSArray.componentsSeparatedByString(noIntervenats, "|");
			for (int i = 0; i < arrayIntervenantsPrefs.count(); i++) {
				if (condInd.length() > 0)
					condInd.append(" or ");
				condInd.append("(tosIntervenant.noIndividu=").append(arrayIntervenantsPrefs.objectAtIndex(i)).append(")");
			}
			if (condInd.length() == 0)
				condInd.append("(tosIntervenant.noIndividu=").append(noIndividu).append(")");
		} else { // Sinon, on prend l'intervenant lui-meme
			condInd.append("(tosIntervenant.noIndividu=").append(noIndividu).append(")");
		}
		// La condition-filtre : dans l'etat "non affectees" et "non validees" on
		// vera les DT sans intervenants et dans les autres etats - uniquement
		// celles
		// avec les traitements.
		condition.append("((intEtat='").append(EOEtatDt.ETAT_NON_VALIDEES);
		condition.append("' or intEtat='").append(EOEtatDt.ETAT_NON_AFFECTEES);
		condition.append("' or intEtat='").append(EOEtatDt.ETAT_ATTENTE_AFFECTER);
		condition.append("' or intEtat='").append(EOEtatDt.ETAT_REJETEES);
		condition.append("') and tosIntervenant.noIndividu=nil) or ");
		condition.append("((intEtat!='").append(EOEtatDt.ETAT_NON_VALIDEES);
		condition.append("' and intEtat!='").append(EOEtatDt.ETAT_NON_AFFECTEES);
		condition.append("' and intEtat!='").append(EOEtatDt.ETAT_ATTENTE_AFFECTER);
		condition.append("' and intEtat!='").append(EOEtatDt.ETAT_REJETEES);
		condition.append("') and (").append(condInd.toString()).append("))");
		qualifier = DTDataBus.newCondition(condition.toString());
		configIntervenant.setAdditionalEtatQualifier(qualifier);
		//
		// Les condition de filtre dans le cas d'un demandeur
		condition = new StringBuffer();
		condition.append("(intNoIndConcerne = ").append(noIndividu);
		condition.append(") OR (intNoIndAppelant = ").append(noIndividu).append(")");
		qualifier = DTDataBus.newCondition(condition.toString());
		configDemandeur.setAdditionalEtatQualifier(qualifier);
	}

	/**
	 * Initialisation des preferences par defaut.
	 */
	private void initPrefsDefault() {
		// Timer
		secondesRefresh = new Integer(120);
		isAutoRefresh = (secondesRefresh != null);
		//
		// elemParPageSelectedItem = new Integer(10);
		// Condition "intervenant"
		Number noIndividu = dtUserInfo().noIndividu();
		EOQualifier qualifier = EOQualifier.qualifierWithQualifierFormat("tosIntervenant.intOrdre=nil or tosIntervenant.noIndividu=" + noIndividu, null);
		configIntervenant.setAdditionalEtatQualifier(qualifier);
		// Conditions "utilisateur"
		qualifier = EOQualifier.qualifierWithQualifierFormat(
				"(intNoIndConcerne = " + noIndividu + ") OR (intNoIndAppelant = " + noIndividu + ")", null);
		configDemandeur.setAdditionalEtatQualifier(qualifier);
	}

	/**
	 * Reinitialisation globale du composant
	 * 
	 */
	protected void initComponent() {
		// le listener de la liste de DT
		listener = new ListenerDTConsultation();
		// les init generiques
		super.initComponent();
		// la liste des services dispo pour cet utilisateur
		NSArray<EOPrefDroits> eoPrefDroitArray = pBus().findAllPrefDroits(dtUserInfo().noIndividu());
		serviceList = (NSArray<EOStructure>) eoPrefDroitArray.valueForKey(EOPrefDroits.TO_STRUCTURE_ULR_KEY);
		// la liste des periodes
		periodList = new NSArray<String>(new String[] {
				RechercheDTConfig.PERIODE_MOINS_1_MOIS,
				RechercheDTConfig.PERIODE_MOINS_2_MOIS,
				RechercheDTConfig.PERIODE_MOINS_6_MOIS,
				RechercheDTConfig.PERIODE_MOINS_1_AN,
				RechercheDTConfig.PERIODE_MOINS_2_ANS,
				RechercheDTConfig.PERIODE_MOINS_5_ANS,
				RechercheDTConfig.PERIODE_TOUTE });
		//
		resetInterfacePermissions();
		//
		initPrefs();
		//
		initFromConfig();
	}

	public boolean showColumnIntervenants, showColumnValider, showColumnAffecter, showColumnTraiter,
			showColumnAjoutTraitement, showColumnSupprimer;

	private void resetInterfacePermissions() {
		// liste des services : visible qu'en intervenant travaillant sur plusieurs
		// services
		showServiceList = modeUtilisateur != USER_DEMANDEUR && (serviceList.count() > 1);

		// popup de selection de la periode : uniquement pour les etat "final"
		showPeriodeList = DTEtatBus.isEtatFinal(etatBus().etatForLibelle(getEtatSelectedLibelle()));

		// radio (intervenant/demandeur) : pas visible pour ceux qui n'ont
		// pas un minimum de droit sur un service au moins
		showRadioIntDemandeur = dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE);

		// bouton RAZ : visible pour ceux qui ont les services ou le radio
		showBtnNouvelleRecherche = showServiceList || showRadioIntDemandeur;

		// bouton de details et autre internet aux non intervenants

		// bouton supprimer

		// bouton mail

		// bouton inspecteur

		// dispo des colonnes
		String etatEnCours = configEnCours().etatDtCode();
		showColumnIntervenants = !isDirectActionMode() && !DTEtatBus.isEtatInitial(etatEnCours);
		showColumnValider = isDirectActionMode() || (!StringCtrl.isEmpty(etatEnCours) && modeUtilisateur != USER_DEMANDEUR && etatEnCours.equals(EOEtatDt.ETAT_NON_VALIDEES));
		showColumnAffecter = !isDirectActionMode() && modeUtilisateur != USER_DEMANDEUR && DTEtatBus.isEtatAffectation(etatEnCours);
		showColumnTraiter = !isDirectActionMode() && !DTEtatBus.isEtatInitial(etatEnCours);
		showColumnAjoutTraitement = !isDirectActionMode() && modeUtilisateur != USER_DEMANDEUR && DTEtatBus.isEtatTraitement(etatEnCours);
		showColumnSupprimer = !isDirectActionMode() && modeUtilisateur != USER_DEMANDEUR;
	}

	/**
	 * Initialisation des parametres de recherches des DT par defaut.
	 */
	protected void initDefaultConfig() {
		NSTimestamp today = DateCtrl.now();
		NSTimestamp before = today.timestampByAddingGregorianUnits(-1, 0, 0, 0, 0, 0);
		// Creer et initialiser les parametres de configuration
		// Pour le mode "intervenant"
		configIntervenant = new RechercheDTConfig(dtUserInfo());
		configIntervenant.setEtatDtCode(EOEtatDt.ETAT_NON_AFFECTEES);
		configIntervenant.setNoIndividu(dtUserInfo().noIndividu().intValue());
		configIntervenant.setUseIndAppelant(false);
		configIntervenant.setUseIndConcerne(false);
		configIntervenant.setUseIndIntervenant(true);
		configIntervenant.setDateDe(DateCtrl.dateToString(before));
		configIntervenant.setUseDateDe(true);
		configIntervenant.setDateA(DateCtrl.dateToString(today));
		configIntervenant.setUseDateA(false);
		configIntervenant.setNumDt(-1);
		configIntervenant.setUseNumDt(false);
		configIntervenant.setPhrase(null);
		configIntervenant.setUsePhrase(false);
		configIntervenant.setBatiment(null);
		configIntervenant.setUseBatiment(false);
		configIntervenant.setUseNonBatiment(false);
		configIntervenant.setService(null);
		configIntervenant.setUseService(false);
		configIntervenant.setUseNonService(false);
		configIntervenant.setTypeDT(RechercheDTConfig.DT_ALL);
		configIntervenant.setModeSelection(RechercheDTConfig.SELECTION_ETAT);
		configIntervenant.setUserMode(RechercheDTConfig.USER_INTERVENANT);
		configIntervenant.setUseEtat(true);
		// Pour le mode "utilisateur"
		configDemandeur = new RechercheDTConfig(dtUserInfo());
		configDemandeur.initFromConfig(configIntervenant);
		configDemandeur.setUseIndAppelant(true);
		configDemandeur.setUseIndConcerne(true);
		configDemandeur.setUseIndIntervenant(false);
		configDemandeur.setUserMode(RechercheDTConfig.USER_DEMANDEUR);
		configDemandeur.setUseEtat(true);
		//
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, dtUserInfo().dtServiceCode()))
			modeUtilisateur = RechercheDTConfig.USER_INTERVENANT;
		else
			modeUtilisateur = RechercheDTConfig.USER_DEMANDEUR;
		//
		if (modeUtilisateur == RechercheDTConfig.USER_INTERVENANT)
			setConfigEnCours(configIntervenant);
		else
			setConfigEnCours(configDemandeur);
	}

	/**
    *
   */
	public WOComponent nouvelleRecherche() {
		initComponent();
		dtSession().dataBus().clearTable(listener.interventionDisplayGroup());
		listener.doFetchDisplayGroup();
		return null;
	}

	public WOComponent doNothing() {
		return null;
	}

	public void setQualifierExternal(EOQualifier qualifier) {
		qualifierExternal = qualifier;
	}

	public boolean isEnableSearch() {
		if (hasSession()) {
			return (dtSession().getMode() != Session.MODE_DA_VALIDER);
		}
		return false;
	}

	public boolean isDirectActionMode() {
		return (dtSession().getMode() == Session.MODE_DA_VALIDER);
	}

	/**
	 * Le code javascript pour la gestion du refresh automatique
	 */
	public String codeJavascriptAutoRefresh() {
		return StringCtrl.replace(codeRefreshTime, "$1", Long.toString(secondesRefresh.intValue() * 1000));
	}

	/**
	 * Cette methode est appelee lors du refresh automatique de la page, on va se
	 * replacer a la bonne page du CktlNavigationBar.
	 */
	public WOComponent doAutoRefresh() {
		int indexSelection = listener.interventionDisplayGroup().currentBatchIndex();
		WOComponent page = listener.doFetchDisplayGroup();
		listener.interventionDisplayGroup().setCurrentBatchIndex(indexSelection);
		return page;
	}

	// pointeur vers l'instance du composant InspecteurDT en cours
	private InspecteurDT inspecteurDT;

	public void setInspecteur(InspecteurDT value) {
		inspecteurDT = value;
	}

	public InspecteurDT inspecteurDT() {
		return inspecteurDT;
	}

	// getters / setter correspondance config

	public String getEtatSelectedLibelle() {
		return dtDataCenter().etatBus().libelleForEtat(configEnCours().etatDtCode());
	}

	/**
	 * Page de consultation : l'utilisateur peut choisir l'etat des DTs qu'il
	 * consulte (1 et 1 seul). S'il change, la liste doit etre rechargee.
	 */
	public void setEtatSelectedLibelle(String value) {
		if (value != null) {
			configEnCours().setEtatDtCode(dtDataCenter().etatBus().etatForLibelle(value));
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * Changer la periode d'interrogation
	 * 
	 * @param value
	 */
	public void setPeriodSelected(String value) {
		if (value != null) {
			periodSelected = value;
			configEnCours().setPeriode(value);
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * On recharge la liste si le mode a change
	 */
	public void setModeUtilisateur(int value) {
		int prevModeUtilisateur = modeUtilisateur;
		modeUtilisateur = value;
		if (prevModeUtilisateur != modeUtilisateur) {
			if (modeUtilisateur == RechercheDTConfig.USER_INTERVENANT)
				setConfigEnCours(configIntervenant);
			else
				setConfigEnCours(configDemandeur);
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * On recharge la liste si on choisit d'afficher ou de cacher les demandes
	 * masquees
	 */
	public void setShowInterventionMasquee(boolean value) {
		boolean prevShowInterventionMasquee = showInterventionMasquee;
		showInterventionMasquee = value;
		if (prevShowInterventionMasquee != showInterventionMasquee) {
			configEnCours().setShowInterventionMasquee(showInterventionMasquee);
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * On recharge la liste si on choisit d'afficher les DTs avec surcout refusé
	 */
	public void setShowDtSurcoutRefuse(boolean value) {
		boolean prevShowDtSurcoutRefuse = showDtSurcoutRefuse;
		showDtSurcoutRefuse = value;
		if (prevShowDtSurcoutRefuse != showDtSurcoutRefuse) {
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * On recharge la liste si on choisit d'afficher les DTs avec non restitution
	 * apres remplacement
	 */
	public void setShowDtRemplacementNonRestitue(boolean value) {
		boolean prevShowDtRemplacementNonRestitue = showDtRemplacementNonRestitue;
		showDtRemplacementNonRestitue = value;
		if (prevShowDtRemplacementNonRestitue != showDtRemplacementNonRestitue) {
			listener.doFetchDisplayGroup();
		}
	}

	/**
	 * Lors changement de config pour cette page, il faut selectionne l'etat
	 * correct d'apres son contenu
	 */
	public void setConfigEnCours(RechercheDTConfig value) {
		super.setConfigEnCours(value);
		setEtatSelectedLibelle(dtDataCenter().etatBus().libelleForEtat(configEnCours().etatDtCode()));
		setPeriodSelected(configEnCours().periode());
	}

	/**
	 * On change la config si le service change, en mettant egalement a jour le
	 * niveau pour le service
	 */
	public void setServiceSelected(EOStructure value) {
		serviceSelected = value;
		String cStructureSelected = (serviceSelected != null ? serviceSelected.cStructure() : null);
		String cStructureSearch = dtUserInfo().dtServiceCode();
		if (cStructureSelected != null && cStructureSearch != null && !cStructureSearch.equals(cStructureSelected)) {
			EOPrefDroits recDroit = pBus().findPrefDroits(dtUserInfo().noIndividu(), cStructureSelected);
			dtUserInfo().setDTServiceCode(recDroit.droService());
			dtUserInfo().setDroits(recDroit.droNiveau(), dtUserInfo().dtServiceCode());
			dtUserInfo().setDTServiceLibelle(recDroit.toStructureUlr().llStructure());
			// forcer le rafraichissement
			listener.doFetchDisplayGroup();
		}
	}

	// ** la classe de gestion de la liste des demandes **

	public class ListenerDTConsultation
			implements I_CompListeDemandeListener {

		public WODisplayGroup interventionDisplayGroup() {
			return getInterventionDisplayGroup();
		}

		public A_PageUsingListeDemande caller() {
			return pageConsultation();
		}

		public boolean showColumnIntervenants() {
			return showColumnIntervenants;
		}

		public boolean showColumnValider() {
			return showColumnValider;
		}

		public boolean showColumnAffecter() {
			return showColumnAffecter;
		}

		public boolean showColumnTraiter() {
			return showColumnTraiter;
		}

		// TODO
		public boolean showColumnDiscussion() {
			return true;
		}

		public boolean showColumnAjoutTraitement() {
			return showColumnAjoutTraitement;
		}

		public boolean showColumnEtat() {
			return false;
		}

		public boolean showColumnSupprimer() {
			return showColumnSupprimer;
		}

		public int modeUtilisateur() {
			return modeUtilisateur;
		}

		public WOComponent doFetchDisplayGroup() {
			CktlLog.trace(null);
			// l'appel a setNumberOfObjectsPerBatch() fait que le DG se remet a la
			// premiere page
			// premier appel : on prend la valeur des préférences
			if (interventionDisplayGroup().numberOfObjectsPerBatch() == 0) {
				interventionDisplayGroup().setNumberOfObjectsPerBatch(getNumberOfObjectsPerBatch().intValue());
			}

			// on controle la fetch limit pour eviter les OutOfMemory
			DTEOEditingContextHandler handler = new DTEOEditingContextHandler();

			if (qualifierExternal == null) {
				configEnCours().setModeDetails(RechercheDTConfig.DETAILS_OFF);
				CktlLog.trace("condition : " + configEnCours().getQualifier());

				// mémorisation avant filtrage de surcout
				eoInterventionArraySansFiltre = EOIntervention.fetchAll(
						interventionDisplayGroup().dataSource().editingContext(),
						configEnCours().getQualifier(),
						null,
						true,
						EOIntervention.MAX_FETCH_LIMIT_RECHERCHE,
						handler);
				NSArray<EOIntervention> eoInterventionArray = eoInterventionArraySansFiltre;

				NSMutableArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();

				if (showChkSurcout() && !showDtSurcoutRefuse) {
					quals.addObject(ERXQ.equals(
							EOIntervention.IS_INTERVENTION_INSTALLATION_POSTE_COMPLET2_SURCOUT_NON_SIGNE_KEY, new Boolean(Boolean.FALSE)));
				}

				if (showChkRemplacementNonRestitute() && !showDtRemplacementNonRestitue) {
					quals.addObject(ERXQ.equals(
							EOIntervention.IS_INTERVENTION_INSTALLATION_POSTE_COMPLET2_REMPLACEMENT_NON_RESTITUE_KEY, new Boolean(Boolean.FALSE)));
				}

				if (quals.count() > 0) {
					eoInterventionArray = EOQualifier.filteredArrayWithQualifier(
							eoInterventionArray, new EOAndQualifier(quals));
				}

				// classement
				eoInterventionArray = EOSortOrdering.sortedArrayUsingKeyOrderArray(eoInterventionArray, getSortForParams());

				interventionDisplayGroup().setObjectArray(eoInterventionArray);

			} else {
				CktlLog.trace("condition externe : " + qualifierExternal);
				// dtSession().dataBus().fetchTable(interventionDisplayGroup(),
				// qualifierExternal, null, false, MAX_FETCH_INTERVENTION);
				NSArray<EOIntervention> eoInterventionArray = EOIntervention.fetchAll(
						interventionDisplayGroup().dataSource().editingContext(),
						configEnCours().getQualifier(),
						null,
						EOIntervention.MAX_FETCH_LIMIT_RECHERCHE,
						handler);
				interventionDisplayGroup().setObjectArray(eoInterventionArray);

			}

			// on sauvegarde si le depassement est atteint
			limitFetchExceeded = handler.getFetchLimitWasExceeded();
			// CktlLog.trace("Objects fetched : " +
			// interventionDisplayGroup().displayedObjects().count());

			return caller();
		}

		public String warnMessage() {
			return (limitFetchExceeded ? "Trop de résultats ont été trouvés, veuillez passer par " +
						"l'écran de recherche pour affiner." : null);
		}

		/**
		 * La fonctionalité masquage n'est disponible que pour les intervenants et
		 * pour les DT en cours
		 */
		public boolean useInterventionMasquee() {
			return modeUtilisateur() == USER_INTERVENANT &&
					DTEtatBus.isEtat(etatBus().etatForLibelle(getEtatSelectedLibelle()), EOEtatDt.ETAT_EN_COURS);
		}

		/**
		 * On affiche les interventions masquees que si l'utilisateur l'a
		 * explicitement dit, et que biensur on gere les interventions masquees
		 */
		public boolean showInterventionMasquee() {
			return showInterventionMasquee && useInterventionMasquee();
		}

		/**
		 * Complement d'information pour les DT en cours et en attente. Ne marche
		 * que si on regarde 1 seul individu
		 */
		public String strResultCommentSuffix() {
			String comment = StringCtrl.emptyString();
			String noIntervenats = StringCtrl.normalize(dtUserInfo().noIntervenats());
			// La conf ne doit concerner qu'1 seul individu
			if (StringCtrl.isEmpty(noIntervenats) || (
					noIntervenats.length() > 0 && !StringCtrl.containsIgnoreCase(noIntervenats, "|"))) {
				Number noIndividu = (StringCtrl.isEmpty(noIntervenats) ?
						dtUserInfo().noIndividu() : new Integer(Integer.parseInt(noIntervenats)));
				if (DTEtatBus.isEtat(configEnCours().etatDtCode(), EOEtatDt.ETAT_EN_COURS)) {
					// en cours : on affiche celle en attente
					String cStructure = (serviceSelected != null ? serviceSelected.stringForKey("cStructure") : null);
					int count = iBus().countInterventionsForIndividu(noIndividu, EOEtatDt.ETAT_ATTENTE, cStructure);
					comment = " (" + count + " " + etatBus().libelleForEtat(EOEtatDt.ETAT_ATTENTE).toLowerCase() + ")";
				} else if (DTEtatBus.isEtat(configEnCours().etatDtCode(), EOEtatDt.ETAT_ATTENTE)) {
					// en attente : on affiche celle en cours
					String cStructure = (serviceSelected != null ? serviceSelected.stringForKey("cStructure") : null);
					int count = iBus().countInterventionsForIndividu(noIndividu, EOEtatDt.ETAT_EN_COURS, cStructure);
					comment = " (" + count + " " + etatBus().libelleForEtat(EOEtatDt.ETAT_EN_COURS).toLowerCase() + ")";
				}
			}
			return comment;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see I_CompListeDemandeListener#showColumnPanier()
		 */
		public boolean showColumnAjouterPanier() {
			// pour l'instant, uniquement pour les intervenants
			return showRadioIntDemandeur;
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

	private PageConsultation pageConsultation() {
		return this;
	}

	// le résultat de la recherche ne tenant pas compte du filtre sur le
	// le surcout non signé, afin de savoir s'il faut affichier un filtre
	// ou non
	private NSArray<EOIntervention> eoInterventionArraySansFiltre;

	/**
	 * Le filtre sur le surcout refusé n'est possible que si au moins une des
	 * demandes de la liste est dans ce cas. Seul les états EN COURS et EN ATTENTE
	 * sont autorisés
	 * 
	 * @return
	 */
	public boolean showChkSurcout() {
		boolean showChkSurcout = true;

		// en mode consultation uniquement
		if (modeUtilisateur != RechercheDTConfig.USER_INTERVENANT) {
			showChkSurcout = false;
		}

		// peut-on ajouter des traitements
		if (showChkSurcout) {
			if (DTEtatBus.isEtatTraitement(configEnCours().etatDtCode()) == false) {
				showChkSurcout = false;
			}
		}

		// au moins une DT de ce type
		if (showChkSurcout) {
			showChkSurcout = false;
			int i = 0;
			while (!showChkSurcout && i < eoInterventionArraySansFiltre.count()) {
				EOIntervention eoIntervention = eoInterventionArraySansFiltre.objectAtIndex(i);
				if (eoIntervention.isInterventionInstallationPosteComplet2SurcoutNonSigne()) {
					showChkSurcout = true;
				}
				i++;
			}
		}

		return showChkSurcout;
	}

	/**
	 * Le filtre sur le remplacement non restitué n'est possible que si au moins
	 * une des demandes de la liste est dans ce cas. Seul les états EN COURS et EN
	 * ATTENTE sont autorisés
	 * 
	 * @return
	 */
	public boolean showChkRemplacementNonRestitute() {
		boolean showChkRemplacementNonRestitute = true;

		// en mode consultation uniquement
		if (modeUtilisateur != RechercheDTConfig.USER_INTERVENANT) {
			showChkRemplacementNonRestitute = false;
		}

		// peut-on ajouter des traitements
		if (showChkRemplacementNonRestitute) {
			if (DTEtatBus.isEtatTraitement(configEnCours().etatDtCode()) == false) {
				showChkRemplacementNonRestitute = false;
			}
		}

		// au moins une DT de ce type
		if (showChkRemplacementNonRestitute) {
			showChkRemplacementNonRestitute = false;
			int i = 0;
			while (!showChkRemplacementNonRestitute && i < eoInterventionArraySansFiltre.count()) {
				EOIntervention eoIntervention = eoInterventionArraySansFiltre.objectAtIndex(i);
				if (eoIntervention.isInterventionInstallationPosteComplet2RemplacementNonRestitue()) {
					showChkRemplacementNonRestitute = true;
				}
				i++;
			}
		}

		return showChkRemplacementNonRestitute;
	}

	// les bus de donnees

	protected DTPreferencesBus pBus() {
		return dtSession().dataCenter().preferencesBus();
	}

	public DTServiceBus sBus() {
		return dtSession().dataCenter().serviceBus();
	}

	private DTInterventionBus iBus() {
		return dtSession().dataCenter().interventionBus();
	}

	private DTEtatBus etatBus() {
		return dtSession().dataCenter().etatBus();
	}
}
