/*
 * Copyright CRI - Universite de La Rochelle, 1995-2005 
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

import java.util.StringTokenizer;

import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * Cette class represente une configuration utilisee pour la recherche des
 * demandes.
 */
public class RechercheDTConfig
		extends DTComponent
		implements NSKeyValueCoding.ErrorHandling {

	// Types de selections
	public static final int SELECTION_ETAT = 0;
	public static final int SELECTION_ALL = 1;
	public static final int USER_DEMANDEUR = 1;
	public static final int USER_INTERVENANT = 0;
	public static final int USER_UNDEFINED = -1;
	public static final int DETAILS_OFF = 0;
	public static final int DETAILS_ON = 1;
	public static final int DT_ALL = 0;
	public static final int DT_TRAITEES = 1;
	public static final int DT_NON_TRAITEES = 2;

	// les periodes d'interrogation possibles
	public final static String PERIODE_MOINS_1_MOIS = "&lt; 1 mois";
	public final static String PERIODE_MOINS_2_MOIS = "&lt; 2 mois";
	public final static String PERIODE_MOINS_6_MOIS = "&lt; 6 mois";
	public final static String PERIODE_MOINS_1_AN = "&lt; 1 an";
	public final static String PERIODE_MOINS_2_ANS = "&lt; 2 ans";
	public final static String PERIODE_MOINS_5_ANS = "&lt; 5 ans";
	public final static String PERIODE_TOUTE = "toute";

	/**
	 * Le type de selection (etat DT uniquement ou tous les parametres).
	 */
	private int modeSelection;

	private int modeUser;

	private int modeDetails;

	/**
	 * Code d'etat actuelle des DT's ("N", "T", etc...)
	 */
	private String etatDtCode;
	private boolean useEtat;

	/**
	 * La periode d'interrogation choisie
	 */
	private String periode;

	/**
	 * Numerot d'individu concerne.
	 */
	private int noIndividu;

	/**
	 * Type d'individu recherche: appelant, concerne, intervenant
	 */
	private boolean useIndividu, useIndAppelant, useIndConcerne, useIndIntervenant;

	private String dateDe, dateA;
	private boolean useDateDe, useDateA;
	private int numDt, typeDT;
	private boolean useNumDt;
	private String phrase;
	private boolean phraseMotif, phraseMotsCles, phraseTraitement;
	private boolean usePhrase;
	private String batiment;
	private boolean useBatiment, useNonBatiment;
	private String service;
	private boolean useService, useNonService;
	private String groupeDT;
	private boolean useGroupeDT;
	private boolean useActivite;
	private boolean useDeepActivite;
	private int activiteOrdre;
	private String activiteLibelle;
	private boolean showInterventionMasquee;
	//
	private EOQualifier additionalQualifier;
	private EOQualifier defaultEtatQualifier;
	//
	private DTUserInfo userInfo;

	public RechercheDTConfig(DTUserInfo aUserInfo) {
		etatDtCode = EOEtatDt.ETAT_NON_AFFECTEES;
		periode = PERIODE_MOINS_1_AN;
		noIndividu = -1;
		useIndAppelant = true;
		useIndConcerne = true;
		useIndIntervenant = false;
		useIndividu = useIndAppelant || useIndConcerne || useIndIntervenant;
		dateDe = null;
		useDateDe = false;
		dateA = null;
		useDateA = false;
		numDt = -1;
		useNumDt = false;
		phrase = null;
		phraseMotif = true;
		usePhrase = phraseMotsCles = phraseTraitement = false;
		batiment = null;
		useBatiment = false;
		useNonBatiment = false;
		service = null;
		useService = false;
		useNonService = false;
		activiteOrdre = -1;
		activiteLibelle = null;
		useActivite = false;
		useDeepActivite = false;
		typeDT = DT_ALL;
		modeDetails = DETAILS_OFF;
		useEtat = true;
		userInfo = aUserInfo;
		showInterventionMasquee = false;
	}

	public void initFromConfig(RechercheDTConfig config) {
		etatDtCode = config.etatDtCode();
		periode = config.periode();
		noIndividu = config.noIndividu();
		useIndAppelant = config.useIndAppelant();
		useIndConcerne = config.useIndConcerne();
		useIndIntervenant = config.useIndIntervenant();
		useIndividu = useIndAppelant || useIndConcerne || useIndIntervenant;
		dateDe = config.dateDe();
		useDateDe = config.useDateDe();
		dateA = config.dateA();
		useDateA = config.useDateA();
		numDt = config.numDt();
		useNumDt = config.useNumDt();
		phrase = config.phrase();
		phraseMotif = config.phraseMotif();
		phraseMotsCles = config.phraseMotsCles();
		phraseTraitement = config.phraseTraitement();
		usePhrase = config.usePhrase();
		batiment = config.batiment();
		useBatiment = config.useBatiment();
		useNonBatiment = config.useNonBatiment();
		service = config.service();
		useService = config.useService();
		useNonService = config.useNonService();
		typeDT = config.typeDT();
		activiteOrdre = config.activiteOrdre();
		activiteLibelle = config.activiteLibelle();
		useActivite = config.useActivite();
		useDeepActivite = config.useDeepActivite();
		useEtat = config.useEtat();
		showInterventionMasquee = config.showInterventionMasquee();
	}

	// Parametres
	public void setUserMode(int newUserMode) {
		modeUser = newUserMode;
	}

	public int getUserMode() {
		return modeUser;
	}

	// === Gestion des qualifiers et des conditions ===
	public void setAdditionalQualifier(EOQualifier newQualifier) {
		additionalQualifier = newQualifier;
	}

	public EOQualifier getAdditionalQualifier() {
		return additionalQualifier;
	}

	/**
	 * La condition qui sera ajoute a la condition de selection par etat. Ceci
	 * peut correspondre a l'utilisateur en cours, par exemple.
	 */
	public void setAdditionalEtatQualifier(EOQualifier newEtatQualifier) {
		defaultEtatQualifier = newEtatQualifier;
	}

	public EOQualifier getAdditionalEtatQualifier() {
		return defaultEtatQualifier;
	}

	private EOQualifier getFixedQualifier() {
		DTUserInfo ui = userInfo;
		StringBuffer sb = new StringBuffer();
		// Cas de l'ecran de recherche, on fixe le service de destination choisi
		if (modeUser == USER_UNDEFINED) {
			if (useGroupeDT) {
				sb.append("(cStructure='").append(groupeDT()).append("')");
			}
		} else {
			if (modeUser == USER_INTERVENANT)
				sb.append("(cStructure='").append(ui.dtServiceCode()).append("')");
			// Le cas special - les intervenants limites ne peuvent consulter
			// que leurs demandes
			if (ui.droits(ui.dtServiceCode()).intValue() == DTUserInfo.DROIT_INTERVENANT_LIMITE) {
				if (sb.length() > 0)
					sb.append(" and (");
				// Pour les DT non affectees - uniquement celle qu'ils ont cree
				// et celle pour lesquels il a au moins le droit "TECHNIQUE" sur
				// l'activite
				if (DTEtatBus.isEtatInitial(etatDtCode)) {
					sb.append("((intNoIndConcerne=").append(ui.noIndividu());
					sb.append(" or intNoIndAppelant=").append(ui.noIndividu()).append(")");
					sb.append("or (toActivites.tosActResponsables.noIndividu=").append(ui.noIndividu());
					sb.append("and (toActivites.tosActResponsables.actTypeResponsable='").append(EOActivites.TYPE_RESP_TECHNIQUE).append("'");
					sb.append("or toActivites.tosActResponsables.actTypeResponsable='").append(EOActivites.TYPE_RESP_FONCTIONNEL).append("')))");

				} else { // Sinon, celle leur affectees
					sb.append("(tosIntervenant.noIndividu=").append(ui.noIndividu()).append(")");

				}
				sb.append(")");
			}
		}

		// la periode d'interrogation est modifiable
		// si l'etat est final
		NSArray<NSTimestamp> args = new NSArray<NSTimestamp>();
		if (DTEtatBus.isEtatFinal(etatDtCode)) {
			if (periode.equals(PERIODE_TOUTE)) {
				// pas de clause sur la date de creation
			} else {
				NSTimestamp now = DateCtrl.now();
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				sb.append("(intDateCreation >= %@)");
				if (periode.equals(PERIODE_MOINS_1_MOIS)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(0, -1, 0, 0, 0, 0));
				} else if (periode.equals(PERIODE_MOINS_2_MOIS)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(0, -2, 0, 0, 0, 0));
				} else if (periode.equals(PERIODE_MOINS_6_MOIS)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(0, -6, 0, 0, 0, 0));
				} else if (periode.equals(PERIODE_MOINS_1_AN)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(-1, 0, 0, 0, 0, 0));
				} else if (periode.equals(PERIODE_MOINS_2_ANS)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(-2, 0, 0, 0, 0, 0));
				} else if (periode.equals(PERIODE_MOINS_5_ANS)) {
					args = args.arrayByAddingObject(now.timestampByAddingGregorianUnits(-5, 0, 0, 0, 0, 0));
				}
			}
		}

		if (sb.length() > 0) {
			EOQualifier qual = DTDataBus.newCondition(sb.toString(), args);
			return qual;
		} else {
			return null;
		}
	}

	private EOQualifier getEtatQualifier() {
		StringBuffer sb = new StringBuffer();
		if (useEtat) {
			sb.append("(intEtat = '").append(etatDtCode).append("')");
		}
		if (typeDT != DT_ALL) {
			if (modeSelection == SELECTION_ALL)
				if (sb.length() > 0) {
					sb.append(" and ");
				}
			if (typeDT == DT_TRAITEES) {
				sb.append("(tosTraitement.traOrdre != nil)");
			} else if (typeDT == DT_NON_TRAITEES) {
				sb.append("(tosTraitement.traOrdre = nil)");
			}

		}
		return EOQualifier.qualifierWithQualifierFormat(sb.toString(), null);
	}

	/**
	 * La condition pour les individus appelant/concernes/intervenant
	 */
	private EOQualifier getIndividuQualifier() {
		if (useIndividu) {
			StringBuffer sb = new StringBuffer();
			if (useIndConcerne && canSearchConcerne()) { // Concerne
				sb.append("(intNoIndConcerne = ").append(noIndividu).append(")");
			}
			if (useIndAppelant && canSearchAppelant()) { // Appelant
				if (sb.length() > 0)
					sb.append(" OR ");
				sb.append("(intNoIndAppelant = ").append(noIndividu).append(")");
			}
			if (useIndIntervenant && canSearchIntervenant()) {
				if (sb.length() > 0)
					sb.append(" OR ");
				sb.append("(tosIntervenant.noIndividu = ").append(noIndividu).append(")");
			}
			if (sb.length() > 0)
				sb.insert(0, "(").append(")");
			if (sb.length() == 0)
				return null;
			else {
				EOQualifier qual = CktlDataBus.newCondition(sb.toString());

				return qual;
			}
		} else {
			return null;
		}
	}

	/**
	 * Condition sur la date.
	 */
	private EOQualifier getDateQualifier() {
		StringBuffer sb = new StringBuffer();
		NSMutableArray<NSTimestamp> args = new NSMutableArray<NSTimestamp>();

		if (useDateDe && DateCtrl.isValid(dateDe)) {
			sb.append(" (intDateCreation >= %@)");
			args.addObject(DateCtrl.stringToDate(dateDe));
		}
		if (useDateA && DateCtrl.isValid(dateA)) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append("(intDateCreation <= %@)");
			args.addObject(DateCtrl.stringToDate(dateA));
		}
		return EOQualifier.qualifierWithQualifierFormat(sb.toString(), args);
	}

	/**
	 * Condition des dt appartenants a une activite.
	 */
	private EOQualifier getActiviteQualifier() {
		// if (useActivite && (activiteOrdre > 0))
		// return
		// EOQualifier.qualifierWithQualifierFormat("actOrdre = "+activiteOrdre,
		// null);
		// else
		// return null;
		if (useActivite) {
			EOQualifier qual = null;
			if (!useDeepActivite) {
				qual = EOQualifier.qualifierWithQualifierFormat(
						"toActivites.actOrdre = %@", new NSArray<Integer>(new Integer(activiteOrdre())));
			} else {
				// on recherche parmi les activites filles grace a la vue V_ACTIIVTES
				// qui regroupe la hierarchie pour chaque activite
				qual = EOQualifier.qualifierWithQualifierFormat(
						"toActivites.actOrdre = %@ OR " +
								"toActivites.toVActivites.actOrdreHierarchie like %@ OR " +
								"toActivites.toVActivites.actOrdreHierarchie like %@ OR " +
								"toActivites.toVActivites.actOrdreHierarchie = %@",
						new NSArray<Object>(new Object[] {
								new Integer(activiteOrdre()),
								"*; " + activiteOrdre + "*",
								"*" + activiteOrdre + ";*",
								"" + activiteOrdre
						}));

			}
			// return
			// EOQualifier.qualifierWithQualifierFormat("intMotsClefs like '"+activiteLibelle+"*'",
			// null);

			// return
			// EOQualifier.qualifierWithQualifierFormat("intMotsClefs like '"+activiteLibelle+"*'",
			// null);
			return qual;
		} else
			return null;
	}

	/**
	 * Condition sur le numero service de la DT.
	 */
	private EOQualifier getNumeroQualifier() {
		if (useNumDt && (numDt > 0))
			return EOQualifier.qualifierWithQualifierFormat("intCleService = " + numDt, null);
		else
			return null;
	}

	/**
	 * Condition sur le motif.
	 */
	private EOQualifier getPhraseQualifier() {
		if (usePhrase && (phrase.length() > 0) &&
				(phraseMotif || phraseMotsCles || phraseTraitement)) {
			//
			StringBuffer phraseLine = new StringBuffer("'*");
			StringTokenizer st = new StringTokenizer(phrase);
			while (st.hasMoreTokens())
				phraseLine.append(StringCtrl.normalize(st.nextToken())).append("*");
			phraseLine.append("'");
			//
			StringBuffer pCondition = new StringBuffer(); // new
																										// StringBuffer("intMotif caseInsensitiveLike '*");
			if (phraseMotif) {
				pCondition.append("intMotif caseInsensitiveLike").append(phraseLine);
			}
			if (phraseMotsCles) {
				if (pCondition.length() > 0)
					pCondition.append(" or ");
				pCondition.append("intMotsClefs caseInsensitiveLike").append(phraseLine);
			}
			if (phraseTraitement) {
				if (pCondition.length() > 0)
					pCondition.append(" or ");
				pCondition.append("tosTraitement.traTraitement caseInsensitiveLike").append(phraseLine);
			}
			return EOQualifier.qualifierWithQualifierFormat(pCondition.toString(), null);
		} else
			return null;
	}

	/**
	 * Condition sur la selection du batimen de demandeur.
	 */
	private EOQualifier getBatimentQualifier() {
		StringBuffer bCondition = new StringBuffer();
		if (useBatiment && (batiment.length() > 0))
			bCondition.append("(cLocal = '").append(batiment).append("')");
		if (useNonBatiment) {
			if (bCondition.length() > 0)
				bCondition.append(" OR ");
			bCondition.append("cLocal = nil");
		}
		if (bCondition.length() > 0)
			return EOQualifier.qualifierWithQualifierFormat(bCondition.toString(), null);
		else
			return null;
	}

	/**
	 * Condition sur la selection du batimen de demandeur.
	 */
	private EOQualifier getServiceDemandeurQualifier() {
		StringBuffer sCondition = new StringBuffer();
		if (useService && (service.length() > 0))
			sCondition.append("(intServiceConcerne = '").append(service).append("')");
		if (useNonService) {
			if (sCondition.length() > 0)
				sCondition.append(" OR ");
			sCondition.append("intServiceConcerne = nil");
		}
		if (sCondition.length() > 0)
			return EOQualifier.qualifierWithQualifierFormat(sCondition.toString(), null);
		else
			return null;
	}

	public EOQualifier getQualifier() {
		EOQualifier qualifier;
		if (modeSelection == SELECTION_ETAT)
			qualifier = getConditionEtat();
		else
			qualifier = getConditionComplete();
		return qualifier;
	}

	/**
	 * Retourne la condition base uniquement sur la selection d'etat.
	 */
	private EOQualifier getConditionEtat() {
		NSMutableArray<EOQualifier> qualifs = new NSMutableArray<EOQualifier>();
		//
		addQualifierIfNotNull(getFixedQualifier(), qualifs);
		addQualifierIfNotNull(getEtatQualifier(), qualifs);
		addQualifierIfNotNull(getAdditionalQualifier(), qualifs);
		addQualifierIfNotNull(getConditionMasquee(), qualifs);
		// Une seule condition en plus
		addQualifierIfNotNull(getAdditionalEtatQualifier(), qualifs);
		return new EOAndQualifier(qualifs);
	}

	/**
	 * Retourne la condition
	 * 
	 * @return
	 */
	private EOQualifier getConditionMasquee() {
		if (!showInterventionMasquee) {
			/*
			 * return CktlDataBus.newCondition(
			 * "tosIntervenant.toInterventionMasquee.intOrdre = nil");
			 */
			return CktlDataBus.newCondition(
					EOIntervention.INT_MASQUEE_LISTE_KEY + "=nil or " +
							"not(" + EOIntervention.INT_MASQUEE_LISTE_KEY + " caseInsensitiveLike '*" + userInfo.noIndividu().intValue() + ",')");

		} else
			return null;
	}

	/**
	 * La condition pour la recherche complete
	 */

	private EOQualifier getConditionComplete() {
		NSMutableArray<EOQualifier> qualifs = new NSMutableArray<EOQualifier>();
		//
		addQualifierIfNotNull(getFixedQualifier(), qualifs);
		addQualifierIfNotNull(getEtatQualifier(), qualifs);
		addQualifierIfNotNull(getAdditionalQualifier(), qualifs);
		addQualifierIfNotNull(getConditionMasquee(), qualifs);
		// Les autres conditions supplementaires
		addQualifierIfNotNull(getIndividuQualifier(), qualifs);
		addQualifierIfNotNull(getDateQualifier(), qualifs);
		// Les conditions supplementaires detaillees
		if (modeDetails == DETAILS_ON) {
			addQualifierIfNotNull(getActiviteQualifier(), qualifs);
			addQualifierIfNotNull(getPhraseQualifier(), qualifs);
			addQualifierIfNotNull(getNumeroQualifier(), qualifs);
			addQualifierIfNotNull(getBatimentQualifier(), qualifs);
			addQualifierIfNotNull(getServiceDemandeurQualifier(), qualifs);
		}
		//
		return new EOAndQualifier(qualifs);
	}

	private void addQualifierIfNotNull(EOQualifier qualifier, NSMutableArray<EOQualifier> array) {
		if (qualifier != null)
			array.addObject(qualifier);
	}

	/**
	 * Condition de selection sous forme d'une chaine de caracteres.
	 */
	// public String getSelectionString() {
	// return this.getSelectionQualifier().toString();
	// }

	public boolean canSearchConcerne() {
		return true;
	}

	public boolean canSearchAppelant() {
		return true;
	}

	public boolean canSearchIntervenant() {
		return true;
		// return modeUser == USER_POWER;
	}

	/* === Les accesseurs === */

	public void setEtatDtCode(String string) {
		etatDtCode = string;
	}

	public void setPeriode(String string) {
		periode = string;
	}

	public void setModeSelection(int i) {
		modeSelection = i;
	}

	public void setModeUser(int i) {
		modeUser = i;
	}

	public String batiment() {
		return batiment;
	}

	public String dateA() {
		return dateA;
	}

	public String dateDe() {
		return dateDe;
	}

	public String etatDtCode() {
		return etatDtCode;
	}

	public String periode() {
		return periode;
	}

	public int modeSelection() {
		return modeSelection;
	}

	public int modeUser() {
		return modeUser;
	}

	public String phrase() {
		return phrase;
	}

	public int noIndividu() {
		return noIndividu;
	}

	public int numDt() {
		return numDt;
	}

	public int typeDT() {
		return typeDT;
	}

	public boolean useBatiment() {
		return useBatiment;
	}

	public boolean useEtat() {
		return useEtat;
	}

	public boolean useDateA() {
		return useDateA;
	}

	public boolean useDateDe() {
		return useDateDe;
	}

	public boolean useIndividu() {
		return useIndividu;
	}

	public boolean useIndAppelant() {
		return useIndAppelant;
	}

	public boolean useIndConcerne() {
		return useIndConcerne;
	}

	public boolean useIndIntervenant() {
		return useIndIntervenant;
	}

	public boolean usePhrase() {
		return usePhrase;
	}

	public boolean useNonBatiment() {
		return useNonBatiment;
	}

	public boolean useNumDt() {
		return useNumDt;
	}

	public void setBatiment(String string) {
		batiment = StringCtrl.normalize(string);
	}

	public void setDateA(String string) {
		dateA = string;
	}

	public void setDateDe(String string) {
		dateDe = string;
	}

	public void setPhrase(String string) {
		phrase = StringCtrl.normalize(string);
	}

	public void setNoIndividu(int i) {
		noIndividu = i;
	}

	public void setNumDt(int i) {
		numDt = i;
	}

	public void setTypeDT(int i) {
		typeDT = i;
	}

	public void setUseBatiment(boolean b) {
		useBatiment = b;
	}

	public void setUseDateA(boolean b) {
		useDateA = b;
	}

	public void setUseDateDe(boolean b) {
		useDateDe = b;
	}

	public void setUseIndividu(boolean b) {
		useIndividu = b;
	}

	public void setUseIndAppelant(boolean b) {
		useIndAppelant = b;
	}

	public void setUseIndConcerne(boolean b) {
		useIndConcerne = b;
	}

	public void setUseIndIntervenant(boolean b) {
		useIndIntervenant = b;
	}

	public void setUsePhrase(boolean use) {
		usePhrase = use;
	}

	public void setUseNonBatiment(boolean b) {
		useNonBatiment = b;
	}

	public void setUseEtat(boolean b) {
		useEtat = b;
	}

	public void setUseNumDt(boolean b) {
		useNumDt = b;
	}

	public void setModeDetails(int mode) {
		modeDetails = mode;
	}

	public String service() {
		return service;
	}

	public void setService(String s) {
		service = StringCtrl.normalize(s);
	}

	public boolean useService() {
		return useService;
	}

	public void setUseService(boolean b) {
		useService = b;
	}

	public boolean useNonService() {
		return useNonService;
	}

	public void setUseNonService(boolean b) {
		useNonService = b;
	}

	public String groupeDT() {
		return groupeDT;
	}

	public void setGroupeDT(String s) {
		groupeDT = StringCtrl.normalize(s);
	}

	public boolean useGroupeDT() {
		return useGroupeDT;
	}

	public void setUseGroupeDT(boolean b) {
		useGroupeDT = b;
	}

	public void setUseActivite(boolean b) {
		useActivite = b;
	}

	public boolean useActivite() {
		return useActivite;
	}

	public void setUseDeepActivite(boolean b) {
		useDeepActivite = b;
	}

	public boolean useDeepActivite() {
		return useDeepActivite;
	}

	public void setActiviteOrdre(int ordre) {
		activiteOrdre = ordre;
	}

	public int activiteOrdre() {
		return activiteOrdre;
	}

	public void setActiviteLibelle(String libelle) {
		activiteLibelle = libelle;
	}

	public String activiteLibelle() {
		return activiteLibelle;
	}

	public int modeDetails() {
		return modeDetails;
	}

	public boolean phraseMotif() {
		return phraseMotif;
	}

	public boolean phraseMotsCles() {
		return phraseMotsCles;
	}

	public boolean phraseTraitement() {
		return phraseTraitement;
	}

	public void setPhraseMotif(boolean b) {
		phraseMotif = b;
	}

	public void setPhraseMotsCles(boolean b) {
		phraseMotsCles = b;
	}

	public void setPhraseTraitement(boolean b) {
		phraseTraitement = b;
	}

	public boolean showInterventionMasquee() {
		return showInterventionMasquee;
	}

	public void setShowInterventionMasquee(boolean b) {
		showInterventionMasquee = b;
	}

	/*
	 * rajout des methodes pour empecher les erreur lors de l'attribut de nombre
	 * null sur des int
	 */

	public Object handleQueryWithUnboundKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void handleTakeValueForUnboundKey(Object arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void unableToSetNullForKey(String arg0) {
		// TODO Auto-generated method stub

	}
}
