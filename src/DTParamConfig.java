import java.math.BigDecimal;
import java.util.GregorianCalendar;

import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;

/*
 * Copyright Universit� de La Rochelle 2007
 *
 * Ce logiciel est un programme informatique servant � g�rer les demandes
 * d'utilisateurs aupr�s d'un service.
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

/**
 * Classe de définition des paramètres de consultation et de recherche de DT.
 * S'inspire de la recherche de ThunderBird 2.0
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class DTParamConfig
		extends DTComponent
		implements NSKeyValueCoding.ErrorHandling {

	// la liste de toutes les natures possibles de filtre
	public NSArray<Integer> natureList;

	// la liste des parametres : contient des objets
	// de type <code>DTParamNode</code>
	public NSMutableArray<DTParamLeaf> nodeList;

	// et/ou entre les parametres de la liste
	public final Integer LIST_AND = new Integer(1);
	public final Integer LIST_OR = new Integer(2);
	public Integer listSelected = LIST_AND;
	private final static String SEP_AND = " and ";
	private final static String SEP_OR = " or ";

	// nature du filtre
	public final static Integer NATURE_ETAT = new Integer(1);
	public final static Integer NATURE_SERVICE_DEST = new Integer(2);
	public final static Integer NATURE_INDIVIDU_APPELANT = new Integer(3);
	public final static Integer NATURE_INDIVIDU_INTERVENANT = new Integer(4);
	public final static Integer NATURE_DATE_CREATION = new Integer(5);
	public final static Integer NATURE_JOURS_CREATION = new Integer(6);
	public final static Integer NATURE_NUMERO = new Integer(7);
	public final static Integer NATURE_MOTIF = new Integer(8);
	public final static Integer NATURE_TRAITEMENTS = new Integer(9);
	public final static Integer NATURE_CONTACT_INDIVIDU = new Integer(10);
	public final static Integer NATURE_CONTACT_BATIMENT = new Integer(11);
	public final static Integer NATURE_CONTACT_SERVICE = new Integer(12);
	public final static Integer NATURE_ACTIVITE = new Integer(13);
	public final static Integer NATURE_WITH_TRAITEMENTS = new Integer(14);
	public final static Integer NATURE_MOTS_CLEFS = new Integer(15);
	public final static Integer NATURE_DATE_TRAITEMENT = new Integer(16);

	// contraintes applicables selon la nature
	public final static Integer CONSTRAINT_EST = new Integer(1);
	public final static Integer CONSTRAINT_N_EST_PAS = new Integer(2);
	public final static Integer CONSTRAINT_CONTIENT = new Integer(3);
	public final static Integer CONSTRAINT_NE_CONTIENT_PAS = new Integer(4);
	public final static Integer CONSTRAINT_AVANT = new Integer(5);
	public final static Integer CONSTRAINT_APRES = new Integer(6);
	public final static Integer CONSTRAINT_PLUS_PETIT = new Integer(7);
	public final static Integer CONSTRAINT_PLUS_GRAND = new Integer(8);
	public final static Integer CONSTRAINT_EST_FILS = new Integer(9);

	// type d'element attendu
	public final static Integer ELEMENT_TYPE_NONE = new Integer(0);
	public final static Integer ELEMENT_TYPE_LIST = new Integer(1);
	public final static Integer ELEMENT_TYPE_NUMBER = new Integer(2);
	public final static Integer ELEMENT_TYPE_STRING = new Integer(3);
	public final static Integer ELEMENT_TYPE_DATE = new Integer(4);
	public final static Integer ELEMENT_TYPE_INDIVIDU = new Integer(5);
	public final static Integer ELEMENT_TYPE_ACTIVITE = new Integer(6);

	// constantes d'elements
	private final static String ELEMENT_VALUE_YES = "OUI";
	private final static String ELEMENT_VALUE_NO = "NON";

	private Session session;

	/**
	 * Instancier un objet
	 */
	public DTParamConfig(Session aSession) {
		super();
		session = aSession;
		nodeList = new NSMutableArray<DTParamLeaf>();
		// TODO selon userinfo
		natureList = new NSArray<Integer>(new Integer[] {
				NATURE_ETAT, NATURE_CONTACT_INDIVIDU, NATURE_SERVICE_DEST,
				NATURE_INDIVIDU_APPELANT, NATURE_INDIVIDU_INTERVENANT, NATURE_DATE_CREATION,
				NATURE_JOURS_CREATION, NATURE_NUMERO, NATURE_MOTIF,
				NATURE_TRAITEMENTS, NATURE_MOTS_CLEFS, NATURE_CONTACT_BATIMENT,
				NATURE_CONTACT_SERVICE, NATURE_ACTIVITE, NATURE_WITH_TRAITEMENTS,
				NATURE_DATE_TRAITEMENT });
	}

	/**
	 * Affichage d'une nature dans une liste deroulante
	 * 
	 * @return
	 */
	public String natureDisplay(Integer param) {
		if (param == NATURE_ETAT) {
			return "Etat";
		} else if (param == NATURE_SERVICE_DEST) {
			return "Service destinataire";
		} else if (param == NATURE_INDIVIDU_APPELANT) {
			return "Créateur de la DT";
		} else if (param == NATURE_INDIVIDU_INTERVENANT) {
			return "Individu intervenant";
		} else if (param == NATURE_DATE_CREATION) {
			return "Date de création";
		} else if (param == NATURE_JOURS_CREATION) {
			return "Création depuis (jours) ";
		} else if (param == NATURE_NUMERO) {
			return "Numéro";
		} else if (param == NATURE_MOTIF) {
			return "Motif (contenu)";
		} else if (param == NATURE_TRAITEMENTS) {
			return "Traitement (contenu)";
		} else if (param == NATURE_MOTS_CLEFS) {
			return "Mots clefs (contenu)";
		} else if (param == NATURE_CONTACT_INDIVIDU) {
			return "Demandeur";
		} else if (param == NATURE_CONTACT_BATIMENT) {
			return "Batiment du contact";
		} else if (param == NATURE_CONTACT_SERVICE) {
			return "Service du contact";
		} else if (param == NATURE_ACTIVITE) {
			return "Activité";
		} else if (param == NATURE_WITH_TRAITEMENTS) {
			return "Avec traitements";
		} else if (param == NATURE_DATE_TRAITEMENT) {
			return "Date de traitement";
		} else
			return "Erreur";
	}

	/**
	 * Affichage d'un etat dans une liste deroulante
	 * 
	 * @return
	 */
	public String constraintDisplay(Integer constraint) {
		if (constraint == CONSTRAINT_EST) {
			return "est";
		} else if (constraint == CONSTRAINT_N_EST_PAS) {
			return "n'est pas";
		} else if (constraint == CONSTRAINT_CONTIENT) {
			return "contient";
		} else if (constraint == CONSTRAINT_NE_CONTIENT_PAS) {
			return "ne contient pas";
		} else if (constraint == CONSTRAINT_AVANT) {
			return "est avant";
		} else if (constraint == CONSTRAINT_APRES) {
			return "est aprés";
		} else if (constraint == CONSTRAINT_PLUS_PETIT) {
			return "est plus petit";
		} else if (constraint == CONSTRAINT_PLUS_GRAND) {
			return "est plus grand";
		} else if (constraint == CONSTRAINT_EST_FILS) {
			return "est (sous-elements inclus)";
		} else
			return "Erreur";
	}

	/**
	 * Donner le type d'element attendu pour un parametre
	 * 
	 * @param param
	 * @return
	 */
	public Integer getElementTypeForNature(DTParamLeaf value) {
		Integer param = value.natureSelected;
		if (param == NATURE_ETAT) {
			return ELEMENT_TYPE_LIST;
		} else if (param == NATURE_SERVICE_DEST) {
			return ELEMENT_TYPE_LIST;
		} else if (param == NATURE_INDIVIDU_APPELANT) {
			return ELEMENT_TYPE_INDIVIDU;
		} else if (param == NATURE_INDIVIDU_INTERVENANT) {
			return ELEMENT_TYPE_INDIVIDU;
		} else if (param == NATURE_DATE_CREATION) {
			return ELEMENT_TYPE_DATE;
		} else if (param == NATURE_JOURS_CREATION) {
			return ELEMENT_TYPE_NUMBER;
		} else if (param == NATURE_NUMERO) {
			return ELEMENT_TYPE_NUMBER;
		} else if (param == NATURE_MOTIF) {
			return ELEMENT_TYPE_STRING;
		} else if (param == NATURE_TRAITEMENTS) {
			return ELEMENT_TYPE_STRING;
		} else if (param == NATURE_MOTS_CLEFS) {
			return ELEMENT_TYPE_STRING;
		} else if (param == NATURE_CONTACT_INDIVIDU) {
			return ELEMENT_TYPE_INDIVIDU;
		} else if (param == NATURE_CONTACT_BATIMENT) {
			return ELEMENT_TYPE_LIST;
		} else if (param == NATURE_CONTACT_SERVICE) {
			return ELEMENT_TYPE_LIST;
		} else if (param == NATURE_ACTIVITE) {
			return ELEMENT_TYPE_ACTIVITE;
		} else if (param == NATURE_WITH_TRAITEMENTS) {
			return ELEMENT_TYPE_LIST;
		} else if (param == NATURE_DATE_TRAITEMENT) {
			return ELEMENT_TYPE_DATE;
		} else
			return ELEMENT_TYPE_NONE;
	}

	/**
	 * Affichage d'un element dans la liste
	 * 
	 * @return
	 */
	public String elementDisplay(Object value) {
		if (value == null) {
			return "<font class=\"textError\">** ERREUR (cliquez sur supprimer) **</font>";
		}
		String disp = value.toString();
		if (value instanceof CktlRecord) {
			// CktlRecord
			CktlRecord valueRecord = (CktlRecord) value;
			// structure
			if ("DTStructureUlr".equals(valueRecord.entityName())) {
				disp = DTStringCtrl.compactString(valueRecord.stringForKey("llStructure"), 30,
						"<...> (" + DTStringCtrl.compactString(valueRecord.stringForKey("lcStructure"), 5, "...") + ")");
			}
			// individu
			else if ("DTIndividuUlr".equals(valueRecord.entityName())) {
				disp = DTStringCtrl.compactString(valueRecord.stringForKey("nomEtPrenom"), 30, "<...>");
			}
			// activite
			else if ("Activites".equals(valueRecord.entityName())) {
				disp = dtDataCenter().activiteBus().findActivitePathString(valueRecord, true, null);
			}
			// batiment
			else if ("Batiment".equals(valueRecord.entityName())) {
				disp = DTStringCtrl.compactString(valueRecord.stringForKey("appellation"), 30, "<...>");
			}
			// etat
			else if ("EtatDt".equals(valueRecord.entityName())) {
				disp = valueRecord.stringForKey("etatLibelle");
			}
		} else if (value instanceof NSTimestamp) {
			// NSTimestamp
			disp = DateCtrl.dateToString((NSTimestamp) value);
		} else if (value instanceof Integer) {
			// Integer
			disp = Integer.toString(((Integer) value).intValue());
		}
		return disp;
	}

	/**
	 * 
	 */
	private DTUserInfo dtUserInfo() {
		return session.dtUserInfo();
	}

	/**
	 * 
	 */
	private DTDataCenter dtDataCenter() {
		return session.dataCenter();
	}

	/**
	 * Ajouter un nouveau parametre a la recherche
	 */
	public void addNode() {
		nodeList.addObject(new DTParamLeaf());
	}

	/**
	 * Supprimer un parametre de recherche
	 * 
	 * @param value
	 */
	public void removeNode(DTParamLeaf value) {
		nodeList.removeIdenticalObject(value);
	}

	/**
	 * 
	 * @author ctarade
	 * 
	 */
	public class DTParamNode {

	}

	/**
	 * La sous-classe de gestion d'un parametre parmi la liste TODO faire du cache
	 * avec les listes
	 * 
	 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
	 */
	public class DTParamLeaf {
		// la nature selectionnee
		public Integer natureSelected;

		/** l'eventuelle chaine de recherche associee */
		public String searchString;
		/** la liste des elements selectionnables */
		public NSArray elementList;
		/** la liste des contraintes selectionnables */
		public NSArray<Integer> constraintList;
		/** la contrainte selectionnee */
		public Integer constraintSelected;

		/** l'element selectionne */
		public Object elementSelected;

		/**
		 * 
		 */
		public DTParamLeaf() {
			super();
			// selection par defaut la meme nature du denier node,
			// sinon la premiere nature
			if (nodeList.count() >= 1) {
				setNatureSelected(nodeList.lastObject().natureSelected);
			} else if (!NSArrayCtrl.isEmpty(natureList)) {
				setNatureSelected((Integer) natureList.objectAtIndex(0));
			}
		}

		/**
		 * 
		 * @param nature
		 */
		public DTParamLeaf(Integer nature) {
			super();
			setNatureSelected(nature);
		}

		/**
		 * Forcer le parametre
		 * 
		 * @param value
		 */
		public void setNatureSelected(Integer value) {
			boolean hasChanges = (natureSelected != value);
			natureSelected = value;
			if (hasChanges) {
				refetchLists();
			}
		}

		/**
		 * Reconstruire les liste des valeurs selectionnables : - liste des elements
		 * - liste des contraintes
		 */
		private void refetchLists() {
			elementList = new NSArray<Object>();
			constraintList = new NSArray<Integer>();

			//
			if (natureSelected == NATURE_ETAT) {
				// etats
				elementList = dtDataCenter().etatBus().findEtatsAll();
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_SERVICE_DEST) {
				// service destinataire
				elementList = (NSArray<Object>) dtDataCenter().serviceBus().allGroupesDt(false).valueForKey(EOGroupesDt.TO_STRUCTURE_ULR_KEY);
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_INDIVIDU_APPELANT) {
				// individu appelant
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_INDIVIDU_INTERVENANT) {
				// individu intervenant
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_DATE_CREATION) {
				// date de creation
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS, CONSTRAINT_AVANT, CONSTRAINT_APRES });
			} else if (natureSelected == NATURE_DATE_TRAITEMENT) {
				// date de traitement
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS, CONSTRAINT_AVANT, CONSTRAINT_APRES });
			} else if (natureSelected == NATURE_JOURS_CREATION) {
				// jours de creation
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_PLUS_PETIT, CONSTRAINT_PLUS_GRAND });
			} else if (natureSelected == NATURE_NUMERO) {
				// numero
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS, CONSTRAINT_PLUS_PETIT, CONSTRAINT_PLUS_GRAND });
			} else if (natureSelected == NATURE_MOTIF) {
				// motif
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_CONTIENT, CONSTRAINT_NE_CONTIENT_PAS });
			} else if (natureSelected == NATURE_TRAITEMENTS) {
				// traitement
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_CONTIENT, CONSTRAINT_NE_CONTIENT_PAS });
			} else if (natureSelected == NATURE_MOTS_CLEFS) {
				// mots clefs
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_CONTIENT, CONSTRAINT_NE_CONTIENT_PAS });
			} else if (natureSelected == NATURE_CONTACT_INDIVIDU) {
				// individu du contact
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_CONTACT_BATIMENT) {
				// batiment du contact
				elementList = dtDataCenter().contactsBus().findAllBatiments();
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_CONTACT_SERVICE) {
				// service du contact
				elementList = (NSArray) dtDataCenter().serviceBus().allServices().valueForKey("toStructureUlr");
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_ACTIVITE) {
				// activite associee
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_EST_FILS, CONSTRAINT_N_EST_PAS });
			} else if (natureSelected == NATURE_WITH_TRAITEMENTS) {
				// avec ou sans traitements
				elementList = new NSArray<Object>(new String[] { ELEMENT_VALUE_YES, ELEMENT_VALUE_NO });
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST });
			} else if (natureSelected == NATURE_CONTACT_INDIVIDU) {
				// individu du contact
				constraintList = new NSArray<Integer>(new Integer[] { CONSTRAINT_EST, CONSTRAINT_N_EST_PAS });
			}

			// initialiser le receveur de la selection
			Integer elementType = getElementTypeForNature(this);
			if (elementType == ELEMENT_TYPE_DATE) {
				elementSelected = DateCtrl.now();
			} else if (elementType == ELEMENT_TYPE_STRING) {
				elementSelected = StringCtrl.emptyString();
			} else if (elementType == ELEMENT_TYPE_NUMBER) {
				elementSelected = new Integer(0);
			} else if (elementType == ELEMENT_TYPE_INDIVIDU) {
				// par defaut, pour un individu, on prend celui connecte
				elementSelected = new Integer(dtUserInfo().noIndividu().intValue());
			} else if (elementType == ELEMENT_TYPE_ACTIVITE) {
				// par defaut, activite vide
				elementSelected = null;
			} else {
				// liste : selection du premier si possible
				if (elementList.count() > 0) {
					elementSelected = elementList.objectAtIndex(0);
				}
			}

			// selection de la premier contrainte
			if (constraintList.count() > 0) {
				constraintSelected = (Integer) constraintList.objectAtIndex(0);
			}
		}

	}

	/**
	 * Donner le <code>EOQualifier</code> associe a la liste des nodes, qui a pour
	 * object de filtrer l'entite <code>Intervention</code>
	 */
	protected EOQualifier getQualifier() {
		EOQualifier dateCreationQualifier = (new DateCreationQualifierBuilder()).getQualifier();
		EOQualifier dateTraitementQualifier = (new DateTraitementQualifierBuilder()).getQualifier();
		EOQualifier joursCreationDepuisQualifier = (new JourCreationDepuisQualifierBuilder()).getQualifier();
		EOQualifier individuAppelantQualifier = (new IndividuAppelantQualifierBuilder()).getQualifier();
		EOQualifier individuConcerneQualifier = (new IndividuConcerneQualifierBuilder()).getQualifier();
		EOQualifier individuIntervenantQualifier = (new IndividuIntervenantQualifierBuilder()).getQualifier();
		EOQualifier numeroInterventionQualifier = (new NumeroInterventionQualifierBuilder()).getQualifier();
		EOQualifier batimentContactQualifier = (new BatimentContactQualifierBuilder()).getQualifier();
		EOQualifier etatQualifier = (new EtatQualifierBuilder()).getQualifier();
		EOQualifier serviceDemandeurQualifier = (new ServiceDemandeurQualifierBuilder()).getQualifier();
		EOQualifier serviceDestinataireQualifier = (new ServiceDestinataireQualifierBuilder()).getQualifier();
		EOQualifier motifQualifier = (new TextMotifQualifierBuilder()).getQualifier();
		EOQualifier traitementsQualifier = (new TextTraitementsQualifierBuilder()).getQualifier();
		EOQualifier motsClefsQualifier = (new TextMotsClefsQualifierBuilder()).getQualifier();
		EOQualifier activiteQualifier = (new ActiviteQualifierBuilder()).getQualifier();
		EOQualifier withTraitementQualifier = (new WithTraitementQualifierBuilder()).getQualifier();

		/*
		 * CktlLog.log("dateCreationQualifier = " + dateCreationQualifier);
		 * CktlLog.log("joursCreationDepuisQualifier = " +
		 * joursCreationDepuisQualifier); CktlLog.log("individuAppelantQualifier = "
		 * + individuAppelantQualifier); CktlLog.log("individuConcerneQualifier = "
		 * + individuConcerneQualifier);
		 * CktlLog.log("individuIntervenantQualifier = " +
		 * individuIntervenantQualifier);
		 * CktlLog.log("numeroInterventionQualifier = " +
		 * numeroInterventionQualifier); CktlLog.log("batimentContactQualifier = " +
		 * batimentContactQualifier); CktlLog.log("etatQualifier = " +
		 * etatQualifier); CktlLog.log("serviceDemandeurQualifier = " +
		 * serviceDemandeurQualifier); CktlLog.log("serviceDestinataireQualifier = "
		 * + serviceDestinataireQualifier); CktlLog.log("motifQualifier = " +
		 * motifQualifier); CktlLog.log("traitementsQualifier = " +
		 * traitementsQualifier); CktlLog.log("motsClefsQualifier = " +
		 * motsClefsQualifier); CktlLog.log("activiteQualifier = " +
		 * activiteQualifier);
		 */

		NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();

		addQualifierIfNotNull(dateCreationQualifier, qualifiers);
		addQualifierIfNotNull(dateTraitementQualifier, qualifiers);
		addQualifierIfNotNull(joursCreationDepuisQualifier, qualifiers);
		addQualifierIfNotNull(individuAppelantQualifier, qualifiers);
		addQualifierIfNotNull(individuConcerneQualifier, qualifiers);
		addQualifierIfNotNull(individuIntervenantQualifier, qualifiers);
		addQualifierIfNotNull(numeroInterventionQualifier, qualifiers);
		addQualifierIfNotNull(batimentContactQualifier, qualifiers);
		addQualifierIfNotNull(etatQualifier, qualifiers);
		addQualifierIfNotNull(serviceDemandeurQualifier, qualifiers);
		addQualifierIfNotNull(serviceDestinataireQualifier, qualifiers);
		addQualifierIfNotNull(motifQualifier, qualifiers);
		addQualifierIfNotNull(traitementsQualifier, qualifiers);
		addQualifierIfNotNull(motsClefsQualifier, qualifiers);
		addQualifierIfNotNull(activiteQualifier, qualifiers);
		addQualifierIfNotNull(withTraitementQualifier, qualifiers);
		//
		EOQualifier finalQualifier = null;
		if (listSelected == LIST_AND) {
			finalQualifier = new EOAndQualifier(qualifiers);
		} else {
			finalQualifier = new EOOrQualifier(qualifiers);
		}

		// ajouter le qualifier de restriction pour les non super admins
		EOQualifier restrictedQual = getRestrictedQualifier(dtDataCenter().serviceBus(), dtUserInfo());
		if (restrictedQual != null) {
			finalQualifier = new EOAndQualifier(new NSArray<EOQualifier>(new EOQualifier[] {
					finalQualifier, restrictedQual }));
		}

		CktlLog.log("finalQualifier=" + finalQualifier);

		return finalQualifier;
	}

	/**
	 * TODO a renommer et deplacer dans {@link DTInterventionBus} Le qualifier qui
	 * permet de restreindre la recherche a ce que l'utilisateur est autorise a
	 * voir : - administrateur : tout - intervenant : son service - demandeur :
	 * les DT dont il est le demandeur ou contact ou dont il est secretaire ou
	 * responsable du service du contact
	 * 
	 * @return
	 */
	public static EOQualifier getRestrictedQualifier(DTServiceBus sBus, DTUserInfo ui) {
		EOQualifier qual = null;
		if (!ui.hasDroit(DTUserInfo.DROIT_ADMIN_SUPER)) {
			// la liste de toutes les structures qui lui sont ouvertes
			NSArray<EOGroupesDt> groupeList = sBus.allGroupesDt(false);
			// on regarde s'il est intervenant
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < groupeList.count(); i++) {
				String cStructure = groupeList.objectAtIndex(i).cStructure();
				if (ui.hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, cStructure)) {
					// pour ceux qui ne sont que intervenant limite, uniquement celle ou
					// il sont
					// sur les affectes
					if (!ui.hasDroit(DTUserInfo.DROIT_INTERVENANT, cStructure)) {
						sb.append("(cStructure = '" + cStructure + "'").append(SEP_AND);
						sb.append("tosIntervenant.noIndividu = ").append(ui.noIndividu().intValue()).append(")");
					} else {
						sb.append("(cStructure = '" + cStructure + "')");
					}
					sb.append(SEP_OR);
				}
			}
			// virer le dernier OR
			int sbSize = sb.length();
			if (sbSize > 0) {
				sb.replace(sbSize - SEP_OR.length(), sbSize, "");
				sb.insert(0, "(");
				sb.append(")");
			}

			// condition pour les responsables d'activites
			if (sb.length() > 0) {
				sb.append(SEP_OR);
			}
			sb.append("((toActivites.tosActResponsables.noIndividu=").append(ui.noIndividu());
			sb.append(" and (toActivites.tosActResponsables.actTypeResponsable='").append(EOActivites.TYPE_RESP_TECHNIQUE).append("'");
			sb.append(" or toActivites.tosActResponsables.actTypeResponsable='").append(EOActivites.TYPE_RESP_FONCTIONNEL).append("')))");

			// condition pour le demandeur :
			// il faut filtrer par rapport aux DTs qu'il a créé
			// ou pour lequel il est demandeur
			sb.append(SEP_OR);
			sb.append("(intNoIndConcerne = " + ui.noIndividu().intValue() + SEP_OR +
					"intNoIndAppelant = " + ui.noIndividu().intValue() + ")");

			/*
			 * // les DTs pour lesquels le service demandeur de la DT // est l'un dont
			 * il est responsable ou secretaire sb.append(SEP_OR); // responsable
			 * sb.append("("+EOIntervention.TO_STRUCTURE_CONCERNE_KEY+"."+EOStructure.
			 * GRP_RESPONSABLE_KEY+"="+ui.noIndividu()+SEP_AND+
			 * EOIntervention.C_STRUCTURE_KEY
			 * +"="+EOIntervention.TO_STRUCTURE_KEY+"."+EOStructure
			 * .C_STRUCTURE_KEY+")"); // secretaire
			 */
			qual = EOQualifier.qualifierWithQualifierFormat(sb.toString(), null);

		}

		CktlLog.log("getRestrictedQualifier() : " + qual);

		return qual;
	}

	/**
	 * Ajouter un qualifier a une liste de qualifier, si celui ci est non null
	 * 
	 * @param qualifier
	 * @param array
	 */
	private void addQualifierIfNotNull(EOQualifier qualifier, NSMutableArray<EOQualifier> array) {
		if (qualifier != null)
			array.addObject(qualifier);
	}

	/**
	 * Connaitre le separateur des qualifiers selon la valeur de
	 * <code>listSelected</code>
	 */
	private String getQualifierSeparator() {
		return (listSelected == LIST_AND ? SEP_AND : SEP_OR);
	}

	/**
	 * Conditions sur les dates de creation
	 */
	private class DateCreationQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_DATE_CREATION;
		}

		public void prepareLocalQualifier(Object element, Integer constraint, StringBuffer sb, NSMutableArray<Object> args) {
			NSTimestamp date = nullHours((NSTimestamp) element);
			args.addObject(date);
			if (constraint == CONSTRAINT_AVANT) {
				sb.append("intDateCreation<=%@");
			} else if (constraint == CONSTRAINT_APRES) {
				sb.append("intDateCreation>=%@");
			} else {
				sb.append("intDateCreation >= %@ and intDateCreation <= %@");
				args.addObject(date.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0));
			}
		}
	}

	/**
	 * Conditions sur les dates de traitement
	 */
	private class DateTraitementQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_DATE_TRAITEMENT;
		}

		public void prepareLocalQualifier(Object element, Integer constraint, StringBuffer sb, NSMutableArray<Object> args) {
			NSTimestamp date = nullHours((NSTimestamp) element);
			args.addObject(date);
			if (constraint == CONSTRAINT_AVANT) {
				sb.append("tosTraitement.traDateFin<=%@");
			} else if (constraint == CONSTRAINT_APRES) {
				sb.append("tosTraitement.traDateDeb>=%@");
			} else {
				sb.append("((tosTraitement.traDateDeb >= %@ and tosTraitement.traDateDeb <= %@) or (tosTraitement.traDateDeb >= %@ and tosTraitement.traDateDeb <= %@))");
				NSTimestamp dateDPlus1 = date.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0);
				args.addObject(dateDPlus1);
				args.addObject(date);
				args.addObject(dateDPlus1);
			}
		}
	}

	/**
	 * Conditions sur les dates de creation depuis
	 */
	private class JourCreationDepuisQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_JOURS_CREATION;
		}

		public void prepareLocalQualifier(Object element, Integer constraint, StringBuffer sb, NSMutableArray<Object> args) {
			// decale du nombre de jours par rapport a la date du jours
			NSTimestamp date = nowDay().timestampByAddingGregorianUnits(0, 0, -((BigDecimal) element).intValue(), 0, 0, 0);
			if (constraint != CONSTRAINT_EST && constraint != CONSTRAINT_N_EST_PAS) {
				sb.append("intDateCreation");
				if (constraint == CONSTRAINT_PLUS_PETIT) {
					sb.append(">");
				} else if (constraint == CONSTRAINT_PLUS_GRAND) {
					sb.append("<");
				}
				sb.append("%@");
				args.addObject(date);
			} else {
				sb.append("intDateCreation >= %@ and intDateCreation <= %@");
				args.addObject(date);
				args.addObject(date.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0));
			}
		}
	}

	/**
	 * Donner la date du jour avec les heures vides
	 * 
	 * @return
	 */
	private NSTimestamp nowDay() {
		return nullHours(DateCtrl.now());
	}

	/**
	 * Mettre les heures a 0
	 * 
	 * @param date
	 * @return
	 */
	private NSTimestamp nullHours(NSTimestamp date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		int year = gc.get(GregorianCalendar.YEAR);
		int month = gc.get(GregorianCalendar.MONTH);
		int dayOfMonth = gc.get(GregorianCalendar.DAY_OF_MONTH);
		NSTimestamp newDate = new NSTimestamp(year, month + 1, dayOfMonth, 0, 0, 0, NSTimeZone.defaultTimeZone());
		return newDate;
	}

	/**
	 * La condition pour les individus appelant/concernes/intervenant
	 */
	private abstract class A_IndividuQualifierBuilder extends A_QualifierBuilder {

		public void prepareLocalQualifier(Object element, Integer constraint, StringBuffer sb, NSMutableArray<Object> args) {
			StringBuffer localSb = new StringBuffer();
			if (nature() == NATURE_CONTACT_INDIVIDU) {
				if (canSearchConcerne()) {
					localSb.append("intNoIndConcerne");
				}
			} else if (nature() == NATURE_INDIVIDU_INTERVENANT) {
				if (canSearchIntervenant()) {
					localSb.append("tosIntervenant.noIndividu");
				}
			} else if (nature() == NATURE_INDIVIDU_APPELANT) {
				if (canSearchAppelant()) {
					localSb.append("intNoIndAppelant");
				}
			}
			if (localSb.length() > 0) {
				localSb.append("=%@");
				sb.append(localSb);
				args.addObject(element);
			}
		}
	}

	/**
	 * La condition pour les individus concernes
	 */
	private class IndividuConcerneQualifierBuilder extends A_IndividuQualifierBuilder {
		public Integer nature() {
			return NATURE_CONTACT_INDIVIDU;
		}
	}

	/**
	 * La condition pour les individus intervenant
	 */
	private class IndividuIntervenantQualifierBuilder extends A_IndividuQualifierBuilder {
		public Integer nature() {
			return NATURE_INDIVIDU_INTERVENANT;
		}
	}

	/**
	 * La condition pour les individus appelant
	 */
	private class IndividuAppelantQualifierBuilder extends A_IndividuQualifierBuilder {
		public Integer nature() {
			return NATURE_INDIVIDU_APPELANT;
		}
	}

	/**
	 * La condition pour le numero visible de la DT (pas la cle primaire)
	 */
	private class NumeroInterventionQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_NUMERO;
		}

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			sb.append("intCleService=%@");
			args.addObject(element);
		}
	}

	/**
	 * La condition pour le batiment indiqu� dans le contact
	 */
	private class BatimentContactQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_CONTACT_BATIMENT;
		}

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			sb.append("cLocal=%@");
			args.addObject(((CktlRecord) element).stringForKey("cLocal"));
		}
	}

	/**
	 * La condition pour le batiment indiqu� dans le contact
	 */
	private class EtatQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_ETAT;
		}

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			sb.append("intEtat=%@");
			args.addObject(((CktlRecord) element).stringForKey("etatCode"));
		}
	}

	/**
	 * La condition pour le service indiqu� dans le contact
	 */
	private class ServiceDemandeurQualifierBuilder extends A_ServiceQualifierBuilder {
		public Integer nature() {
			return NATURE_CONTACT_SERVICE;
		}
	}

	/**
	 * La condition pour le service destinataire de la DT
	 */
	private class ServiceDestinataireQualifierBuilder extends A_ServiceQualifierBuilder {
		public Integer nature() {
			return NATURE_SERVICE_DEST;
		}
	}

	/**
	 * La condition pour un service
	 */
	private abstract class A_ServiceQualifierBuilder extends A_QualifierBuilder {

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			sb.append((nature() == NATURE_SERVICE_DEST ? "cStructure" : "intServiceConcerne"));
			sb.append("=%@");
			args.addObject(((CktlRecord) element).stringForKey("cStructure"));
		}
	}

	/**
	 * La condition pour le motif de la DT
	 */
	private class TextMotifQualifierBuilder extends A_TextQualifier {
		public Integer nature() {
			return NATURE_MOTIF;
		}
	}

	/**
	 * La condition pour le motif de la DT
	 */
	private class TextTraitementsQualifierBuilder extends A_TextQualifier {
		public Integer nature() {
			return NATURE_TRAITEMENTS;
		}
	}

	/**
	 * La condition pour le motif de la DT
	 */
	private class TextMotsClefsQualifierBuilder extends A_TextQualifier {
		public Integer nature() {
			return NATURE_MOTS_CLEFS;
		}
	}

	/**
	 * La condition sur une chaine de caractere
	 */
	private abstract class A_TextQualifier extends A_QualifierBuilder {

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			String str = "*" + (String) element + "*";
			sb.append(nature() == NATURE_MOTIF ? "intMotif" : (nature() == NATURE_TRAITEMENTS ? "tosTraitement.traTraitement" : ("intMotsClefs")));
			sb.append(" caseInsensitiveLike %@");
			args.addObject(str);
		}
	}

	/**
	 * La condition pour sur l'activite
	 */
	private class ActiviteQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_ACTIVITE;
		}

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			Integer actOrdre = (Integer) element;
			String strActOrdre = Integer.toString(actOrdre.intValue());
			if (constraint != CONSTRAINT_EST_FILS) {
				sb.append("actOrdre=%@");
				args.addObject(actOrdre);
			} else {
				sb.append("actOrdre = %@ or ");
				sb.append("toActivites.toVActivites.actOrdreHierarchie like %@ or ");
				sb.append("toActivites.toVActivites.actOrdreHierarchie like %@ or ");
				sb.append("toActivites.toVActivites.actOrdreHierarchie = %@");
				args.addObject(actOrdre);
				args.addObject("*; " + strActOrdre + "*");
				args.addObject("*" + strActOrdre + "*");
				args.addObject(strActOrdre);
			}
		}
	}

	// TODO selon le profil
	public boolean canSearchConcerne() {
		return true;
	}

	// TODO selon le profil
	public boolean canSearchAppelant() {
		return true;
	}

	// TODO selon le profil
	public boolean canSearchIntervenant() {
		return true;
	}

	/**
	 * La condition pour savoir si une DT a des traitements ou pas
	 */
	private class WithTraitementQualifierBuilder extends A_QualifierBuilder {

		public Integer nature() {
			return NATURE_WITH_TRAITEMENTS;
		}

		public void prepareLocalQualifier(Object element, Integer constraint,
				StringBuffer sb, NSMutableArray<Object> args) {
			sb.append("tosTraitement.traOrdre");
			if (((String) element).equals(ELEMENT_VALUE_YES)) {
				sb.append("!=");
			} else {
				sb.append("=");
			}
			sb.append("nil");
		}
	}

	/**
	 * Interface permettant de contruire simplement un qualifier
	 */
	private abstract class A_QualifierBuilder {

		/**
		 * La nature associee, exemple : NATURE_JOURS_CREATION
		 */
		public abstract Integer nature();

		/**
		 * La methode de remplissage de la chaine du qualifier et des arguments.
		 * C'est l� qu'est fait tout le metier
		 * 
		 * @param element
		 * @param constraint
		 * @param sb
		 * @param args
		 */
		public abstract void prepareLocalQualifier(Object element, Integer constraint, StringBuffer sb, NSMutableArray<Object> args);

		/**
		 * Retourne le qualifier associe
		 */
		public EOQualifier getQualifier() {
			StringBuffer sb = new StringBuffer();
			NSMutableArray<Object> args = new NSMutableArray<Object>();
			String qualSep = getQualifierSeparator();
			for (int i = 0; i < nodeList.count(); i++) {
				DTParamLeaf node = (DTParamLeaf) nodeList.objectAtIndex(i);
				if (node.natureSelected == nature()) {
					if (node.elementSelected != null) {
						StringBuffer sbLocal = new StringBuffer();
						Integer constraint = node.constraintSelected;
						prepareLocalQualifier(node.elementSelected, constraint, sbLocal, args);
						if (sbLocal.length() > 0) {
							sbLocal.insert(0, "(");
							// on insere un not pour les contraintes particulieres
							if (constraint == CONSTRAINT_N_EST_PAS || constraint == CONSTRAINT_NE_CONTIENT_PAS) {
								sbLocal.insert(0, "not");
							}
							sbLocal.append(")");
							sb.append(sbLocal);
							// ajout du mot clef seperateur
							if (sb.length() > 0) {
								sb.append(qualSep);
							}
						}
					}
				}
			}
			// supprimer le dernier separateur
			int sbSize = sb.length();
			if (sbSize > 0) {
				sb.replace(sbSize - qualSep.length(), sbSize, "");
			}
			EOQualifier qual = EOQualifier.qualifierWithQualifierFormat(sb.toString(), args);
			return qual;
		}
	}

	// sauvegarder

	/** le separateur entre couples (cle/valeur) */
	private final static String STORE_SEP = ".-_-.";
	/** le separateur entre 1 cle et 1 valeur */
	private final static String STORE_SEP_VALUE_KEY = "[O_O]";
	/** le mot cle indiquant le type d'interrogation (AND/OR) */
	private final static String STORE_KEY_LIST_SELECTED = "listSelected";

	/**
	 * Construire la chaine qui permettra de sauvegarder cette configuration en
	 * favoris.
	 */
	protected String toStorableString() {
		// constuire une chaine descriptive de la conf
		StringBuffer sb = new StringBuffer();
		// l'operation booleene
		sb.append(STORE_KEY_LIST_SELECTED).append(STORE_SEP_VALUE_KEY).append(listSelected).append(STORE_SEP);
		for (int i = 0; i < nodeList.count(); i++) {
			DTParamLeaf node = (DTParamLeaf) nodeList.objectAtIndex(i);
			// on ne traite que les selections non vides
			Object element = node.elementSelected;
			if (node.elementSelected != null) {
				sb.append(node.natureSelected).append(STORE_SEP_VALUE_KEY);
				sb.append(node.constraintSelected).append(STORE_SEP_VALUE_KEY);
				Integer natureSelected = node.natureSelected;
				String value = "?";
				// traitement particulier selon la nature de la selection
				if (natureSelected == NATURE_ETAT) {
					// etats
					value = ((CktlRecord) element).stringForKey("etatCode");
				} else if (natureSelected == NATURE_SERVICE_DEST ||
						natureSelected == NATURE_CONTACT_SERVICE) {
					// service destinataire, service du contact
					value = ((CktlRecord) element).stringForKey("cStructure");
				} else if (natureSelected == NATURE_INDIVIDU_APPELANT ||
						natureSelected == NATURE_INDIVIDU_INTERVENANT ||
						natureSelected == NATURE_CONTACT_INDIVIDU ||
						natureSelected == NATURE_ACTIVITE) {
					// individu appelant, individu intervenant, individu du contact; jours
					// de creation, activite associee
					value = Integer.toString(((Integer) element).intValue());
				} else if (natureSelected == NATURE_NUMERO ||
						natureSelected == NATURE_JOURS_CREATION) {
					// numero
					value = Integer.toString(((BigDecimal) element).intValue());
				} else if (natureSelected == NATURE_DATE_CREATION) {
					// date de creation
					value = DateCtrl.dateToString((NSTimestamp) element);
				} else if (natureSelected == NATURE_MOTIF ||
						natureSelected == NATURE_TRAITEMENTS ||
						natureSelected == NATURE_MOTS_CLEFS ||
						natureSelected == NATURE_WITH_TRAITEMENTS) {
					// motif, traitement, mots clefs, avec ou sans traitements
					value = (String) element;
				} else if (natureSelected == NATURE_CONTACT_BATIMENT) {
					// batiment du contact
					value = ((CktlRecord) element).stringForKey("cLocal");
				}
				sb.append(value).append(STORE_SEP);
			}
		}
		// supprimer le dernier separateur
		int sbSize = sb.length();
		if (sbSize > 0) {
			sb.replace(sbSize - STORE_SEP.length(), sbSize, "");
		}
		return sb.toString();
	}

	/**
	 * Parametrer le composant en objet a partir d'une chaine stockee
	 * 
	 * @param value
	 * @return
	 */
	protected boolean restore(String value) {
		nodeList = new NSMutableArray<DTParamLeaf>();
		NSArray<String> arrAll = NSArray.componentsSeparatedByString(value, STORE_SEP);
		// premier est toujours l'operation booleene
		NSArray<String> arrLocal = NSArray.componentsSeparatedByString((String) arrAll.objectAtIndex(0), STORE_SEP_VALUE_KEY);
		listSelected = getIntegerSep(Integer.parseInt((String) arrLocal.objectAtIndex(1)));
		// la suite c'est tous les autres parametres
		for (int i = 1; i < arrAll.count(); i++) {
			String strLocal = arrAll.objectAtIndex(i);
			arrLocal = NSArray.componentsSeparatedByString(strLocal, STORE_SEP_VALUE_KEY);
			int nature = Integer.parseInt((String) arrLocal.objectAtIndex(0));
			int constraint = Integer.parseInt((String) arrLocal.objectAtIndex(1));
			String strElement = arrLocal.objectAtIndex(2);
			Object element = null;
			if (nature == NATURE_ETAT.intValue()) {
				// etats
				element = dtDataCenter().etatBus().recEtatForCode(strElement);
			} else if (nature == NATURE_SERVICE_DEST.intValue() ||
					nature == NATURE_CONTACT_SERVICE.intValue()) {
				// service destinataire, service du contact
				element = dtDataCenter().serviceBus().structureForCode(strElement);
			} else if (nature == NATURE_INDIVIDU_APPELANT.intValue() ||
					nature == NATURE_INDIVIDU_INTERVENANT.intValue() ||
					nature == NATURE_CONTACT_INDIVIDU.intValue() ||
					nature == NATURE_ACTIVITE.intValue()) {
				// individu appelant, individu intervenant, individu du contact; jours
				// de creation, activite associee
				element = new Integer(Integer.parseInt(strElement));
			} else if (nature == NATURE_NUMERO.intValue() ||
					nature == NATURE_JOURS_CREATION.intValue()) {
				// numero
				element = new Integer(Integer.parseInt(strElement));
			} else if (nature == NATURE_DATE_CREATION.intValue()) {
				// date de creation
				element = DateCtrl.stringToDate(strElement);
			} else if (nature == NATURE_MOTIF.intValue() ||
					nature == NATURE_TRAITEMENTS.intValue() ||
					nature == NATURE_MOTS_CLEFS.intValue() ||
					nature == NATURE_WITH_TRAITEMENTS.intValue()) {
				// motif, traitement, mots clefs, avec ou sans traitements
				element = strElement;
			} else if (nature == NATURE_CONTACT_BATIMENT.intValue()) {
				// batiment du contact
				element = dtDataCenter().contactsBus().findBatimentForCode(strElement);
			}
			DTParamLeaf node = new DTParamLeaf(getIntegerNature(nature));
			node.constraintSelected = getIntegerConstraint(constraint);
			node.elementSelected = element;
			nodeList.addObject(node);
		}
		return true;
	}

	/**
	 * Recuperer la condition <code>Integer</code> associe a une valeur int. Il
	 * faut recuperer le pointeur exact.
	 * 
	 * @param nature
	 * @return
	 */
	private Integer getIntegerSep(int sep) {
		return (sep == LIST_AND.intValue() ? LIST_AND : LIST_OR);
	}

	/**
	 * Recuperer la nature <code>Integer</code> associe a une valeur int. Il faut
	 * recuperer le pointeur exact.
	 * 
	 * @param nature
	 * @return
	 */
	private Integer getIntegerNature(int nature) {
		return (nature == NATURE_ETAT.intValue() ? NATURE_ETAT :
				nature == NATURE_SERVICE_DEST.intValue() ? NATURE_SERVICE_DEST :
						nature == NATURE_CONTACT_SERVICE.intValue() ? NATURE_CONTACT_SERVICE :
								nature == NATURE_INDIVIDU_APPELANT.intValue() ? NATURE_INDIVIDU_APPELANT :
										nature == NATURE_INDIVIDU_INTERVENANT.intValue() ? NATURE_INDIVIDU_INTERVENANT :
												nature == NATURE_CONTACT_INDIVIDU.intValue() ? NATURE_CONTACT_INDIVIDU :
														nature == NATURE_ACTIVITE.intValue() ? NATURE_ACTIVITE :
																nature == NATURE_NUMERO.intValue() ? NATURE_NUMERO :
																		nature == NATURE_JOURS_CREATION.intValue() ? NATURE_JOURS_CREATION :
																				nature == NATURE_DATE_CREATION.intValue() ? NATURE_DATE_CREATION :
																						nature == NATURE_MOTIF.intValue() ? NATURE_MOTIF :
																								nature == NATURE_TRAITEMENTS.intValue() ? NATURE_TRAITEMENTS :
																										nature == NATURE_MOTS_CLEFS.intValue() ? NATURE_MOTS_CLEFS :
																												nature == NATURE_WITH_TRAITEMENTS.intValue() ? NATURE_WITH_TRAITEMENTS :
																														nature == NATURE_DATE_TRAITEMENT.intValue() ? NATURE_DATE_TRAITEMENT :
																																nature == NATURE_CONTACT_BATIMENT.intValue() ? NATURE_CONTACT_BATIMENT : null);
	}

	/**
	 * Recuperer la contrainte <code>Integer</code> associe a une valeur int. Il
	 * faut recuperer le pointeur exact.
	 * 
	 * @param nature
	 * @return
	 */
	private Integer getIntegerConstraint(int constraint) {
		return (constraint == CONSTRAINT_EST.intValue() ? CONSTRAINT_EST :
				constraint == CONSTRAINT_N_EST_PAS.intValue() ? CONSTRAINT_N_EST_PAS :
						constraint == CONSTRAINT_CONTIENT.intValue() ? CONSTRAINT_CONTIENT :
								constraint == CONSTRAINT_NE_CONTIENT_PAS.intValue() ? CONSTRAINT_NE_CONTIENT_PAS :
										constraint == CONSTRAINT_AVANT.intValue() ? CONSTRAINT_AVANT :
												constraint == CONSTRAINT_APRES.intValue() ? CONSTRAINT_APRES :
														constraint == CONSTRAINT_PLUS_PETIT.intValue() ? CONSTRAINT_PLUS_PETIT :
																constraint == CONSTRAINT_PLUS_GRAND.intValue() ? CONSTRAINT_PLUS_GRAND :
																		constraint == CONSTRAINT_EST_FILS.intValue() ? CONSTRAINT_EST_FILS : null);
	}

	/*
	 * rajout des methodes pour empecher les erreurs lors de l'attribut de nombre
	 * null sur des int
	 */
	public Object handleQueryWithUnboundKey(String key) {
		return null;
	}

	public void handleTakeValueForUnboundKey(Object value, String key) {
	}

	public void unableToSetNullForKey(String key) {
	}

}
