
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitementType;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

/*
 * Copyright Universit� de La Rochelle 2008
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
 * Le controleur du composant DTFast
 * 
 * @author ctarade
 */
public class SwapDTFastCtrl
	extends DTComponentCtrl {
	// le mode d'utilisation de cette page
	private int mode;
	protected final static int MODE_READ 	= 0;
	protected final static int MODE_EDIT 	= 1;
	protected final static int MODE_ADD 	= 2;
	
	// periode d'interrogation
	public NSTimestamp dateDebutPeriode;
	public NSTimestamp dateFinPeriode;
	private Object myDateDebutPeriode;
	private Object myDateFinPeriode;
	
	// etats
	public NSArray etatList;
	public CktlRecord etatSelected;
	
	// panneau de consultation services destinataires
	public NSArray serviceList;
	public CktlRecord serviceSelected;
	public boolean showServiceList;
	
	// panneau de consultation : contacts
	public NSArray contactList;
	public CktlRecord contactSelected;
	
	// panneau de consultation : activites
	public NSArray activiteList;
	public CktlRecord activiteSelected;
	
	// interventions repondant aux filtres
	public CktlRecord interventionSelected;
	
	// intervention en cours
	public String interventionSelectedMotif;
	public String interventionSelectedCommentaire;
	public String interventionSelectedCommentaireInterne;
	public CktlRecord interventionSelectedActiviteSelected;
	public CktlRecord interventionSelectedContactSelected;
	
	// modes d'appel
	public NSArray modeAppelList;
	public CktlRecord interventionSelectedModeAppelSelected;
	
	// temoins d'erreur
	public String errorMessage;
	
	// nouvelle intervention : les traitements
	public NSMutableArray newInterventionTraitementList;
	
	// nouvelle intervention : que faire apres la saisie
	private boolean isNewInterventionAffectSelf;
	private boolean isNewInterventionAffectSelfAndClose;
	
	// envoi de mail 
	public boolean shouldSendMails;
	
	// le controleur d'envoi de fichier pour la DT (pas les traitements)
	public DTFileUploadCtrl ctrlInterventionFileUpload;

	// les fichiers reellement attaches dans GEDFS
	private NSArray interventionAttachedFiles;
	
	public SwapDTFastCtrl(SwapDTFast aComponent) {
		super(aComponent);
		initCtrl();
	}
	
	/**
	 * initialisation du controleur
	 */
	private void initCtrl() {
		// en mode lecture
		mode = MODE_READ;
		// periode
		myDateFinPeriode = dateFinPeriode = DateCtrl.now().timestampByAddingGregorianUnits(0,0,1,0,0,0);
		myDateDebutPeriode = dateDebutPeriode = dateFinPeriode.timestampByAddingGregorianUnits(-1,0,-1,0,0,0);
		// tous les etats
		etatList = etatBus().findEtatsAffectation();
		if (!NSArrayCtrl.isEmpty(etatList)) {
			// Initialisation de l'etat en fct des preferences
      String etatCode = dtUserInfo().startEtatCode();
      if (StringCtrl.normalize(etatCode).length() > 0 && etatBus().libelleForEtat(etatCode).length() > 0) {
      	setEtatCodeSelected(etatCode);
      } 
      // pas de selection, on prend le premier
      if (etatSelected == null) {
      	etatSelected = (CktlRecord) etatList.objectAtIndex(0);
      }
		}
		// modes d'appel
		modeAppelList = interventionBus().allModeAppel();
		// les services destinataires : la liste des services dispo pour cet utilisateur
    serviceList = (NSArray) preferencesBus().findAllPrefDroits(dtUserInfo().noIndividu()).valueForKey("toStructureUlr");
    showServiceList = (serviceList.count() > 1);
		if (showServiceList) {
			// on selectionne celui des preferences
	    String prfDroService = preferencesBus().getDefaultServiceCode(dtUserInfo().noIndividu());
	    if (!StringCtrl.isEmpty(prfDroService)) {
	    	setCStructureServiceSelected(prfDroService);
	    } 
		}
		// si pas de selection de service faite, on prends le premier
		if (serviceSelected == null && serviceList.count()  > 0) {
			setServiceSelected((CktlRecord) serviceList.objectAtIndex(0));
		}
		// envoi de mail par defaut
		shouldSendMails = true;
	}

	
	// interrogation de la base de donnees
	
	/**
	 * Charger la liste des activites
	 */
	private void fetchActivites() {
		activiteList = activiteBus().findAllActivitesIntervenant(dtUserInfo().noIndividu(), 
				serviceSelected.stringForKey("cStructure"), myDateDebutPeriode, myDateFinPeriode, etatSelected.stringForKey("etatCode"));
		// virer tous les <em>NSKeyValueCoding.NullValue</em>
		NSMutableArray activiteListMutable = new NSMutableArray(activiteList);
		activiteListMutable.removeIdenticalObject(NSKeyValueCoding.NullValue);
		activiteList = activiteListMutable.immutableClone();
	}

	/**
	 * Charger la liste des contacts
	 */
	private void fetchContacts() {
		contactList = contactsBus().findAllContactsIntervenant(dtUserInfo().noIndividu(), 
				serviceSelected.stringForKey("cStructure"), myDateDebutPeriode, myDateFinPeriode, etatSelected.stringForKey("etatCode"));
		// virer tous les <em>NSKeyValueCoding.NullValue</em>
		NSMutableArray contactListMutable = new NSMutableArray(contactList);
		contactListMutable.removeIdenticalObject(NSKeyValueCoding.NullValue);
		contactList = contactListMutable.immutableClone();
	}

	/**
	 * Recharger le contenu du display group des DTs
	 * d'apres les filtres appliques par l'utilisateur
	 */
	private void fetchInterventionDg() {
		
  	// nettoyage des queryBindings
		getInterventionDg().queryBindings().removeAllObjects();

		// structure
		if (serviceSelected != null) {
			getInterventionDg().queryBindings().setObjectForKey(serviceSelected.stringForKey("cStructure"), "myCStructure");
		}

		// etat
		if (etatSelected != null) {
			String etatCode = etatSelected.stringForKey("etatCode");
			getInterventionDg().queryBindings().setObjectForKey(etatCode, "myIntEtat");
			// selon l'etat, on filtre sur les affectation de la personne connectee
			if (etatCode.equals(EOEtatDt.ETAT_EN_COURS) || etatCode.equals(EOEtatDt.ETAT_ATTENTE) || etatCode.equals(EOEtatDt.ETAT_TRAITEES)) {
				getInterventionDg().queryBindings().setObjectForKey(dtUserInfo().noIndividu(), "myNoIndIntervenant");
			}
		} else {
			// pas de selection d'etat
		}

		// contact
		if (contactSelected != null) {
			getInterventionDg().queryBindings().setObjectForKey(contactSelected.numberForKey("ctOrdre"), "myCtOrdre");
		}

		// activite
		if (activiteSelected != null) {
			getInterventionDg().queryBindings().setObjectForKey(activiteSelected.numberForKey("actOrdre"), "myActOrdre");
		}
		
		// periode
		//getInterventionDg().queryBindings().setObjectForKey(myDateDebutPeriode, "myDateDebutPeriode");
		//getInterventionDg().queryBindings().setObjectForKey(myDateFinPeriode, "myDateFinPeriode");
			
  	// fetcher la base de donnees des interventions
		getInterventionDg().qualifyDataSource();
  	
  	// selectionner la premiere si la liste n'est pas vide
  	if (getInterventionDg().allObjects().count() > 0) {
  		setInterventionSelected((CktlRecord) getInterventionDg().allObjects().objectAtIndex(0));
  	}
	}
	

	// actions
	
	/**
	 * Action par defaut du formulaire : ne rien faire
	 */
	protected WOComponent doNothing() {
		return null;
	}
	
	/**
	 * Changer uniquement la periode 
	 */
	protected WOComponent changePeriode() {
		fetchActivites();
		fetchContacts();
		fetchInterventionDg();
		return null;
	}
	
	
	// setters

	/**
	 * 
	 */
	public void setDateDebutPeriode(NSTimestamp value) {
		dateDebutPeriode = value;
		// l'objet associe est mis a jour selon si null ou pas 
		if (dateDebutPeriode != null) {
			myDateDebutPeriode = dateDebutPeriode;
		} else {
			myDateDebutPeriode = NSKeyValueCoding.NullValue;
		}
		changePeriode();
	}
	
	/**
	 * 
	 */
	public void setDateFinPeriode(NSTimestamp value) {
		dateFinPeriode = value;
		// l'objet associe est mis a jour selon si null ou pas 
		if (dateFinPeriode != null) {
			myDateFinPeriode = dateFinPeriode;
		} else {
			myDateFinPeriode = NSKeyValueCoding.NullValue;
		}
		changePeriode();
	}
	
	/**
	 * Selection du service selectionn� a partir de son C_STRUCTURE
	 */
	private void setCStructureServiceSelected(String value) {
    NSArray recs = EOQualifier.filteredArrayWithQualifier(
    		serviceList, DTDataBus.newCondition("cStructure='"+value+"'"));
    setServiceSelected((CktlRecord) recs.objectAtIndex(0));
	}
	
	/**
	 * La liste des contacts et des activites sont modifiees 
	 * quand le service change
	 */
	public void setServiceSelected(CktlRecord value) {
		serviceSelected = value;
		if (value != null) {
			dtUserInfo().setDTServiceCode(value.stringForKey("cStructure"));
		}
		// contacts
		fetchContacts();
		// activites
		fetchActivites();
    // recharger la liste des interventions 
    fetchInterventionDg();
	}
	
	/**
	 * Selection du service selectionn� a partir de son ETAT_CODE
	 */
	private void setEtatCodeSelected(String value) {
    NSArray recs = EOQualifier.filteredArrayWithQualifier(
    		etatList, DTDataBus.newCondition("etatCode='"+value+"'"));
    if (recs.count() > 0) {
      setEtatSelected((CktlRecord) recs.objectAtIndex(0));
    }
	}
	/**
	 * 
	 */
	public void setEtatSelected(CktlRecord value) {
		etatSelected = value;
    // recharger la liste des interventions 
    fetchInterventionDg();
	}

	/**
	 * 
	 */
	public void setContactSelected(CktlRecord value) {
		contactSelected = value;
    // recharger la liste des interventions 
    fetchInterventionDg();
	}

	/**
	 * 
	 */
	public void setActiviteSelected(CktlRecord value) {
		activiteSelected = value;
    // recharger la liste des interventions 
    fetchInterventionDg();
	}

	/**
	 * Mise a jour des variables locales de la DT en cours
	 * @param value
	 */
	public void setInterventionSelected(CktlRecord value) {
		interventionSelected = value;
		if (interventionSelected != null) {
			interventionSelectedMotif = interventionSelected.stringForKey("intMotif");
			interventionSelectedCommentaire = interventionSelected.stringForKey("intCommentaireInterne");
			interventionSelectedCommentaireInterne = interventionSelected.stringForKey("intCommentaireInterne");
			interventionSelectedContactSelected = selectFromContactListByCtOrdre(interventionSelected.numberForKey("ctOrdre"));
			interventionSelectedActiviteSelected = selectFromActiviteListByActOrdre(interventionSelected.numberForKey("actOrdre"));
			interventionSelectedModeAppelSelected = selectFromModeAppelListByModCode(interventionSelected.stringForKey("modCode"));
			// synchronisation des traitements avec la liste non EOF
			newInterventionTraitementList = new NSMutableArray();
			NSArray recsVTraitement = interventionBus().findVTraitementsForIndividuAndIntervention(
				dtUserInfo().noIndividu(), interventionSelected.numberForKey("intOrdre"));
			for (int i = 0; i < recsVTraitement.count(); i++) {
				CktlRecord recVTraitement = (CktlRecord) recsVTraitement.objectAtIndex(i);
				Traitement t = new Traitement();
				t.setConsultable(recVTraitement.boolForKey("traConsultable"));
				t.setDDeb(recVTraitement.dateForKey("traDateDeb"));
				t.setDFin(recVTraitement.dateForKey("traDateFin"));
				t.setText(recVTraitement.stringForKey("traTraitement"));
				newInterventionTraitementList.addObject(t);
			}
		}
	}
	
	// utilitaires de setter
	
	/**
	 * L'enregistrement parmi la liste <code>contactList</code> ayant pour
	 * identifiant <code>ctOrdre</code>
	 */
	private CktlRecord selectFromContactListByCtOrdre(Number ctOrdre) {
		CktlRecord result = null;
		EOQualifier qual = CktlDataBus.newCondition("ctOrdre="+ctOrdre, null);
		NSArray records = EOQualifier.filteredArrayWithQualifier(contactList, qual);
		if (records.count() > 0) {
			result = (CktlRecord) records.lastObject();
		}
		return result;
	}
	
	/**
	 * L'enregistrement parmi la liste <code>activiteList</code> ayant pour
	 * identifiant <code>actOrdre</code>
	 */
	private CktlRecord selectFromActiviteListByActOrdre(Number actOrdre) {
		CktlRecord result = null;
		EOQualifier qual = CktlDataBus.newCondition("actOrdre="+actOrdre, null);
		NSArray records = EOQualifier.filteredArrayWithQualifier(activiteList, qual);
		if (records.count() > 0) {
			result = (CktlRecord) records.lastObject();
		}
		return result;
	}
	
	/**
	 * L'enregistrement parmi la liste <code>modeAppelList</code> ayant pour
	 * identifiant <code>modCode</code>
	 */
	private CktlRecord selectFromModeAppelListByModCode(String modCode) {
		CktlRecord result = null;
		EOQualifier qual = CktlDataBus.newCondition("modCode='"+modCode+"'", null);
		NSArray records = EOQualifier.filteredArrayWithQualifier(modeAppelList, qual);
		if (records.count() > 0) {
			result = (CktlRecord) records.lastObject();
		}
		return result;
	}
	
	/**
	 * L'enregistrement parmi la liste <code>interventionDg</code> ayant pour
	 * identifiant <code>intOrdre</code>
	 */
	private CktlRecord selectFromInterventionDgByIntOrdre(Number intOrdre) {
		CktlRecord result = null;
		EOQualifier qual = CktlDataBus.newCondition("intOrdre="+intOrdre.intValue(), null);
		NSArray records = EOQualifier.filteredArrayWithQualifier(getInterventionDg().allObjects(), qual);
		if (records.count() > 0) {
			result = (CktlRecord) records.lastObject();
		}
		return result;
	}
		
	// methodes internes a la saisie d'une DT
	
	/**
	 * 1. verifie s'il utilisateur a le droit de faire la saisie de la DT (
	 * 	criteres sur l'etat final)
	 * 2. Indique si la demandes de travaux en cours de saisie / d'edition
	 * 	est correctement saisie.
	 */
	private boolean checkIntervention() {
		boolean valid = true;

		// contact
		if (interventionSelectedContactSelected == null) {
			setErrorMessage("Le contact est obligatoire");
			valid = false;
		}
		
		// activite
		if (valid && interventionSelectedActiviteSelected == null) {
			setErrorMessage("L'activit&eacute; est obligatoire");
			valid = false;
		}
		
		// motif non vide
		if (valid && StringCtrl.isEmpty(interventionSelectedMotif)) {
			setErrorMessage("Le motif est obligatoire");
			valid = false;
		}
		
		// taille max du motif
		if (valid) {
			boolean errSize = !dtSession().dtDataBus().checkForMaxSize(
					"Intervention", "intMotif", interventionSelectedMotif, "Motif", 0, true, true);
			if (errSize) {
				setErrorMessage(dtSession().dtDataBus().getErrorMessage());
				valid = false;
			}
		}
		
		// etat
		if (valid) {
			String etatFinal = etatNewIntervention();
			if (!isAllowedFinalEtat(etatFinal, 
				interventionSelectedActiviteSelected.numberForKey("actOrdre"), 
				interventionSelectedActiviteSelected.stringForKey("cStructure"))) {
				String labelEtatFinal = dtSession().dataCenter().etatBus().libelleForEtat(etatFinal);
				setErrorMessage("Vous n'&ecirc;tes pas autoris&eacute; &agrave; saisir la DT dans l'&eacute;tat " + labelEtatFinal + "<br/>" +
					"pour les demandes sur ce type d'activit&eacute;.");
				valid = false;
			}
		}
		
		// verification des traitements
		if (valid) {
			for (int i = 0; valid && i < newInterventionTraitementList.count(); i++) {
				Traitement t = (Traitement) newInterventionTraitementList.objectAtIndex(i);
		    // format date
		    if (t.getDDeb() == null || t.getDFin() == null) {
		    	// erreur de lecture des heures
		    	t.setErrorMessage("Les heures/minutes sont vides ou leur format est incorrect!");
		    	valid = false;
		    }
		    // coherence date
		    if (valid && t.getDFin().compareTo(t.getDDeb()) <= 0) {
		    	t.setErrorMessage("La date de d&eacute;but doit &ecirc;tre anteacute;rieure &agrave; la date de fin !");
		      valid = false;
		    }
		    // contenu non vide
		    if (valid && StringCtrl.normalize(t.getText()).length() == 0) {
		    	t.setErrorMessage("Vous devez saisir un motif pour le traitement !");
		      valid = false;
		    }
		    // Verifier si la longeur de commentaire est correcte.
		    if (valid && !dtSession().dtDataBus().checkForMaxSize("Traitement", "traTraitement",
		    		t.getText(), "Commentaire", 0, true, true)) {
		    	t.setErrorMessage(dtSession().dtDataBus().getErrorMessage());
		      dtSession().dtDataBus().setErrorMessage(null);
		      valid = false;
		    }
			}
			// une erreur est surevenu dans ce bloc : c'est un traitement
			if (!valid) {
				setErrorMessage("Une erreur est survenue lors de la validation des traitements");
			}
		}
		
		return valid;
	}
	
	/**
	 * Pour la creation de DT, on a plusieurs possibilite pour l'etat
	 * final : non affectee / non validee, en cours ou traitee.
	 * @return
	 */
	private String etatNewIntervention() {
		String result = null;
		if (isNewInterventionAffectSelf) {
			// se l'affecter 
			result = EOEtatDt.ETAT_EN_COURS;
		} else if (isNewInterventionAffectSelfAndClose) {
			// se l'affecter + cloture
			result = EOEtatDt.ETAT_TRAITEES;
		} else {
			// creation uniquement : determiner l'etat final
			// d'apres les responsables d'activites
			result = activiteBus().newEtatCodeForActivite(
	    		interventionSelectedActiviteSelected.numberForKey("actOrdre"));
		}
		return result;
	}
	
	/**
	 * Permet de savoir si la personne connectee est autorisee creer
	 * la DT avec l'etat final attendu.
	 * Cet etat est conditionne par le droits de la personne.
	 * - non validee : ouvert a tous
	 * - non affectee : ouvert a tous si pas de resp. fonct., sinon ayant
	 * 		droit de validation
	 * - en cours : si l'etat non affectee est atteint, alors ayant
	 * 		droit d'affectation
	 * - cloture : ouvert a tous
	 */
	private boolean isAllowedFinalEtat(String intEtat, Number actOrdre, String cStructure) {
		boolean result = false;
		if (intEtat == EOEtatDt.ETAT_NON_VALIDEES) {
			result = true;
		} else if (intEtat == EOEtatDt.ETAT_NON_AFFECTEES) {
			result = interventionBus().canDoPreValidation(actOrdre);
		} else if (intEtat == EOEtatDt.ETAT_EN_COURS || intEtat == EOEtatDt.ETAT_TRAITEES) {
			result = interventionBus().canDoPreValidation(actOrdre);
			if (result) {
				result = interventionBus().canDoPreAffectation(EOEtatDt.ETAT_NON_AFFECTEES, actOrdre, cStructure, dtUserInfo().noIndividu());
			}
		}
		return result;
	}
	
	private NSMutableDictionary saveDataDico;
	
	/**
	 * Remplir le dictionaire de creation de l'intervention dans
	 * la base de donnees
	 */
	private void fillDataDictionary() {
		// cas particulier pour ctEmail : ce truc sert a rien ... on prend
		// donc le mail issu de CktlUserInfo
		CktlUserInfoDB uiDemandeur = new CktlUserInfoDB(dtSession().dataBus());
		uiDemandeur.individuForNoIndividu(interventionSelectedContactSelected.numberForKey("noIndividu"), true);
    // Remplire le dictionnaire principal
		saveDataDico = interventionBus().newDefaultDataDictionnaryNewIntervention(
				interventionSelectedActiviteSelected.numberForKey("actOrdre"), 
				serviceSelected.stringForKey("cStructure"), 
  			interventionSelectedContactSelected.numberForKey("noIndividu"), 
  			/*interventionSelectedContactSelected.stringForKey("ctEmail"), */
  			uiDemandeur.email(),
  			etatNewIntervention(),
  			activiteBus().findActivitePathString(interventionSelectedActiviteSelected, true, null), 
  			interventionSelectedContactSelected.stringForKey("cStructService"), 
  			interventionSelectedContactSelected.stringForKey("cLocal"), 
  			interventionSelectedContactSelected.numberForKey("ctOrdre"), 
  			interventionSelectedModeAppelSelected.stringForKey("modCode"));
		//TODO mettre la date souhaitee
    saveDataDico.setObjectForKey(DateCtrl.now().timestampByAddingGregorianUnits(0, 0, 10, 0, 0, 0), "intDateSouhaite");
    // 
    saveDataDico.setObjectForKey(interventionSelectedMotif, "intMotif");
    //TODO pour l'instant pas de destination
    saveDataDico.setObjectForKey(DTDataBus.nullValue(), "lolfId");
	}
	
	/**
	 * Indique si l'etat final de la nouvelle DT autorise la saisie de 
	 * traitement et/ou d'affectation
	 * @return
	 */
	private boolean isEtatAffectationTraitement() {
		String etatFinal = etatNewIntervention();
		return DTEtatBus.isEtatTraitement(etatFinal) || etatFinal.equals(EOEtatDt.ETAT_TRAITEES);
	}
	
	/**
	 * Sauvegarde de l'intervention dans la base de donnees
	 * @return le numero <code>intOrdre</code> si l'operation s'est deroulee correctement,
	 * 	<em>null</em> sinon.
	 */
	private Number saveIntervention() {
    interventionBus().beginTransaction();
    boolean postSaveOpIsOk = true;
    Number intOrdre = null;
    // nouvelle demande de travaux
    if (mode == MODE_ADD) {
    	fillDataDictionary();
      NSDictionary dicoNewIntervention = interventionBus().addIntervention(saveDataDico);
      if (dicoNewIntervention == null) {
      	postSaveOpIsOk = false;
      } else {
      	// recuperer les numeros (intOrdre necessaire pour l'affectation)
      	intOrdre = (Number) dicoNewIntervention.objectForKey("intOrdre");
      	// ajouter les traitements si besoin (etat final en cours, en attente ou traitee)
      	if (isEtatAffectationTraitement()) {
          for (int i = 0; i < newInterventionTraitementList.count(); i++) {
    				Traitement t = (Traitement) newInterventionTraitementList.objectAtIndex(i);
    				t.setIntOrdre(intOrdre);
    				t.setTraOrdre(interventionBus().addTraitement(
    						null, (Number) dicoNewIntervention.valueForKey("intOrdre"), dtUserInfo().noIndividu(), 
    						EOEtatDt.ETAT_TRAITEES, t.getText(), null, null,
    						t.getDDeb(), t.getDFin(), t.getConsultable(), EOTraitementType.TYPE_CODE_TEXTUEL));
          }
      	}
      }
    }/* else if (mode == MODE_EDIT) {
    	// mise a jour 
    	postSaveOpIsOk = interventionBus().updateIntervention(
    			null, null, interventionSelectedMotif, 
    			interventionSelectedCommentaireInterne, null, null, 
    			interventionSelected.stringForKey("intMotsClef"), interventionSelectedActiviteSelected.numberForKey("actOrdre"), interventionSelectedContactSelected.numberForKey("ctOrdre"), 
    			interventionSelectedModeAppelSelected.stringForKey("modCode"));
    }*/
    // commit si tout est ok, erreur sinon
    if (postSaveOpIsOk) {
    	interventionBus().commitTransaction();
    	// affectation
    	if (isNewInterventionAffectSelf || isNewInterventionAffectSelfAndClose) {
    		interventionBus().addIntervenant(null, intOrdre, dtUserInfo().noIndividu(), dtUserInfo().noIndividu());
    	}
    	// fichiers attaches
    	doSaveAttachedFiles(intOrdre);
    	for (int i = 0; i < newInterventionTraitementList.count(); i++) {
				Traitement t = (Traitement) newInterventionTraitementList.objectAtIndex(i);
				t.doSaveAttachedFiles();
			}
    	// mails
    	if (shouldSendMails) {
      	doSendMails(intOrdre);
    	}
    } else {
    	setErrorMessage(interventionBus().getErrorMessage());
      interventionBus().rollbackTransaction();
    }
		return intOrdre;
	}

	/**
	 * Sauvegarde de fichier attache dans GEDFS.
	 * @return <em>false</em> si une erreur survient. <code>getErrorMessage()</code> est 
	 * alors mis a jour.
	 */
	private boolean doSaveAttachedFiles(Number intOrdre) {
		
		// les fichiers liés à la demande
		if (!NSArrayCtrl.isEmpty(ctrlInterventionFileUpload.getFilePaths())) {
			interventionAttachedFiles = dtSession().dtDocumentCenter().saveAttachements(
					intOrdre,
					null,
					ctrlInterventionFileUpload.getFilePaths(),
					DTDocumentCenter.CATEGORIE_DT_COMMUN);
      if (interventionAttachedFiles.count() == 0) {
        setErrorMessage("Les fichiers attachés n'ont pas pu être enregistres (demande) !");
        return false;
      }
		}

		// les fichiers liés aux traitements
		for (int i=0; i<newInterventionTraitementList.count(); i++) {
			Traitement traitement = (Traitement) newInterventionTraitementList.objectAtIndex(i);
			if (!NSArrayCtrl.isEmpty(traitement.ctrlTraitementFileUpload.getFilePaths())) {
				NSArray<String> traitementAttachedFiles = dtSession().dtDocumentCenter().saveAttachements(
						intOrdre, 
						traitement.getTraOrdre(), 
						traitement.ctrlTraitementFileUpload.getFilePaths(),
						DTDocumentCenter.CATEGORIE_DT_COMMUN); 
				if (traitementAttachedFiles.count() == 0){
	        setErrorMessage("Les fichiers attachés n'ont pas pu être enregistrés (traitements) !");
	        return false;
				}
			}
		}
			
		
		return true;
  }
	
	/**
	 * Envoi de tous les mails associes a l'operation pour la demande
	 * de travaux ayant pour numero <code>intOrdre</code>
	 * @return
	 */
	private boolean doSendMails(Number intOrdre) {
		boolean noError = true;
		// mail de creation
    dtSession().mailCenter().reset();
    dtSession().mailCenter().setIntervention(interventionBus().findIntervention(null, intOrdre), false);
    dtSession().mailCenter().setInterventionFiles(interventionAttachedFiles);
    noError = dtSession().mailCenter().mailCreerDT();
    if (!noError) {
    	setErrorMessage("Le mail de cr&eacute;ation au service concern&eacute; n'a pas pu &ecirc;tre envoy&eacute;&nbsp;!");
    }
    // mail de validation si besoin
    if ((isNewInterventionAffectSelf || isNewInterventionAffectSelfAndClose) && activiteBus().newEtatCodeForActivite(
    		interventionSelectedActiviteSelected.numberForKey("actOrdre")).equals(EOEtatDt.ETAT_NON_VALIDEES)) {
    	noError = dtSession().mailCenter().mailValiderDT();
      if (!noError) {
      	setErrorMessage("Le mail de validation n'a pas pu &ecirc;tre envoy&eacute;&nbsp;!");
      }
    }
    // mail d'affectation si besoin
    if (isNewInterventionAffectSelf || isNewInterventionAffectSelfAndClose) {
    	dtSession().mailCenter().mailAffecterDT(new NSArray(dtUserInfo().noIndividu()));
    	if (!noError) {
    		setErrorMessage("Le mail d'affectation n'a pas pu &ecirc;tre envoy&eacute;&nbsp;!");
      }
    }
    // mail de cloture
    if (isNewInterventionAffectSelfAndClose) {
    	// il faut l'objet CktlRecord pour que les traitements soient lisibles ...
    	EOIntervention recNewIntervention = interventionBus().findIntervention(null, intOrdre);
    	dtSession().mailCenter().setTraitements(recNewIntervention.tosTraitement(), true, true);
    	dtSession().mailCenter().mailCloreDT(DTMailCenter.DT_CLASSIC);
    	dtSession().mailCenter().mailCloreDTFacultatif();
    }
    
    return noError;
	}
		
	/**
	 * Effectuer les traitements qui suivent la creation d'une demande de travaux :
	 * - rafraichir la liste des DTs
	 * - selectionner la dt nouvelle
	 * - passer en mode lecture
	 */
	private void doPostCreateStuff(Number intOrdre) {
		fetchInterventionDg();
		// selectionner la DT nouvellement creee
		CktlRecord recNewIntervention = selectFromInterventionDgByIntOrdre(intOrdre);
		if (recNewIntervention != null) {
			setInterventionSelected(recNewIntervention);
		}
		// passer en mode lecture
		mode = MODE_READ;
	}
	
	// navigation
	
	/**
	 * Passer en mode ajout d'une nouvelle DT, on initialise les variables
	 * locales a la DT en cours.
	 */
	protected WOComponent newIntervention() {
		mode = MODE_ADD;
		interventionSelectedMotif = StringCtrl.emptyString();
		interventionSelectedCommentaire = StringCtrl.emptyString();
		interventionSelectedCommentaireInterne = StringCtrl.emptyString();
		// on selectionne les contacts, activites et mode d'appel de la derniere DT
		if (getInterventionDg().allObjects().count() > 0) {
			CktlRecord lastIntervention = (CktlRecord) getInterventionDg().allObjects().lastObject();
			interventionSelectedContactSelected = selectFromContactListByCtOrdre(lastIntervention.numberForKey("ctOrdre"));
			interventionSelectedActiviteSelected = selectFromActiviteListByActOrdre(lastIntervention.numberForKey("actOrdre"));
			interventionSelectedModeAppelSelected = selectFromModeAppelListByModCode(lastIntervention.stringForKey("modCode"));
			// traitements
			ctrlInterventionFileUpload = new DTFileUploadCtrl();
			clearTraitements();
		}
		return null;
	}
	
	/**
	 * Annuler l'operation en cours (repasser en mode lecture) :
	 * - ajout : reselection de l'intervention en cours
	 * - modification : rien
	 * @return
	 */
	protected WOComponent cancel() {
		if (mode == MODE_ADD) {
			setInterventionSelected(interventionSelected);
		}
		mode = MODE_READ;
		return null;
	}
	
	/**
	 * Passer en mode modification de la DT en cours 
	 * @return
	 */
	protected WOComponent edit() {
		mode = MODE_EDIT;
		return null;
	}
	
	/**
	 * Nouvelle DT : ne pas l'affecter. Affiche une page de confirmation
	 * si des traitements ont ete saisis.
	 * @return
	 */
	protected WOComponent create(WOComponent caller) {
		isNewInterventionAffectSelf = false;
		isNewInterventionAffectSelfAndClose = false;
		if (checkIntervention()) {
			if (newInterventionTraitementList.count() > 0) {
				return CktlAlertPage.newAlertPageWithResponder(caller, "Incoherence entre traitements et etat", 
						"Vous avez choisi de cr&eacute;er une DT dans un &eacute; n'autorisant pas de traitement !<br/>" +
						"Et vous avez saisi " + newInterventionTraitementList.count() + " traitements !<br/>" + 
						"Si vous continuez ces derniers seront ignor&eacute;s ...", 
						"Ignorer les traitements", "Revenir", null, CktlAlertPage.ATTENTION,
						new CreateNoTraitementConfirmResponder(caller));
			} else {
				createNonAffecteConfirm();
			}
		}
		return null;
	}
	
	/**
	 * Effectue l'enregistrement de la DT en non affectee / non validee
	 * @return
	 */
	private void createNonAffecteConfirm() {
		Number intOrdre = saveIntervention();
		doPostCreateStuff(intOrdre);
	}
	
	/**
	 * Gestion de la confirmation O/N de la saisie d'une DT n'acceptant 
	 * pas de traitement
	 * 
	 * @author ctarade
	 */
	private class CreateNoTraitementConfirmResponder implements CktlAlertResponder {

		private WOComponent caller;
		
		public CreateNoTraitementConfirmResponder(WOComponent aCaller) {
			super();
			caller = aCaller;
		}
		
		public WOComponent respondToButton(int arg0) {
			switch (arg0) {
	     	case 2: break;     
	      case 1: createNonAffecteConfirm();
	      default: break;
			}
			return caller;
		}
		
	}
	

	/**
	 * Nouvelle DT : se l'affecter et la mettre en cours
	 * @return
	 */
	protected WOComponent createAndAffectSelf() {
		isNewInterventionAffectSelf = true;
		isNewInterventionAffectSelfAndClose = false;
		if (checkIntervention()) {
			Number intOrdre = saveIntervention();
			doPostCreateStuff(intOrdre);	
		}
		return null;
	}
	
	/**
	 * Nouvelle DT : se l'affecter et la traiter
	 * @return
	 */
	protected WOComponent createAndAffectSelfAndClose(WOComponent caller) {
		isNewInterventionAffectSelf = false;
		isNewInterventionAffectSelfAndClose = true;
		if (checkIntervention()) {
			if (newInterventionTraitementList.count() == 0) {
				return CktlAlertPage.newAlertPageWithCaller(caller, "Pas de traitement saisis ...",
						"Vous ne pouvez pas cloturer une DT <u>sans</u> traitements !<br/>" +
						"Saisissez-en un, ou bien laissez la DT non affect&eacute;e ou en cours  ...", "Revenir", CktlAlertPage.ERROR);
			} else {
				Number intOrdre = saveIntervention();
				doPostCreateStuff(intOrdre);
			}
		}
		return null;
	}
	
	// traitements
	
	/**
	 * La classe de gestion de l'affichage des traitements 
	 *
	 * @author ctarade
	 */
	public class Traitement {
		// le numero de la demande de travaux
		private Number intOrdre;
		// la cle est saisie uniquement si le traitement est deja en base de donnees
		private Number traOrdre;
		private String text;
		private NSTimestamp dDeb;
		private NSTimestamp dFin;
		private boolean consultable; 
		// controleur du DTFileUpload associe
		private DTFileUploadCtrl ctrlTraitementFileUpload;
		// les fichiers reellement attaches dans GEDFS
		private NSArray traitementAttachedFiles;
		// message d'erreur local
		private String errorMessage; 
		
		public Traitement() {
			intOrdre = null;
			traOrdre = null;
			text = StringCtrl.emptyString();
			dDeb = DateCtrl.now();
			dFin = dDeb.timestampByAddingGregorianUnits(0, 0, 0, 0, 15, 0);
			consultable = true; //TODO voir avec les preferences utilisateur
			ctrlTraitementFileUpload = new DTFileUploadCtrl();
			clearFilesInfo();
			errorMessage = StringCtrl.emptyString();
		}
		
		/**
		 * Sauvegarde de fichier attache dans GEDFS.
		 * @return <em>false</em> si une erreur survient. <code>getErrorMessage()</code> est 
		 * alors mis a jour.
		 */
		private boolean doSaveAttachedFiles() {
			if (!NSArrayCtrl.isEmpty(ctrlTraitementFileUpload.getFilePaths())) {
				traitementAttachedFiles = dtSession().dtDocumentCenter().saveAttachements(
						getIntOrdre(),
						getTraOrdre(),
						ctrlTraitementFileUpload.getFilePaths(),
						DTDocumentCenter.CATEGORIE_DT_COMMUN);
	      if (traitementAttachedFiles.count() == 0) {
	        setErrorMessage("Les fichiers attachés n'ont pas pu être enregistrés !");
	        return false;
	      }
			}
			return true;
    }
		
	  /**
	   * Supprime les fichiers et le repertoire unique genere pour le depot de documents.
	   */
	  private void clearFilesInfo() {
	  	ctrlTraitementFileUpload.clearFilesInfo();
	  }
		
		public Number getIntOrdre() {			return intOrdre;		}
		public void setIntOrdre(Number intOrdre) {			this.intOrdre = intOrdre;		}
		public Number getTraOrdre() {			return traOrdre;		}
		public void setTraOrdre(Number traOrdre) {			this.traOrdre = traOrdre;		}
		public String getText() {			return text;		}
		public void setText(String text) {			this.text = text;		}
		public NSTimestamp getDDeb() {			return dDeb;		}
		public void setDDeb(NSTimestamp deb) {			dDeb = deb;		}		
		public NSTimestamp getDFin() {			return dFin;		}
		public void setDFin(NSTimestamp fin) {			dFin = fin;		}
		public boolean getConsultable() {			return consultable;		}
		public void setConsultable(boolean consultable) {			this.consultable = consultable;		}
		public String getErrorMessage() {			return errorMessage;		}
		public void setErrorMessage(String errorMessage) {			this.errorMessage = errorMessage;		}
		public boolean hasError() {		return !StringCtrl.isEmpty(getErrorMessage());  }
		public DTFileUploadCtrl getCtrlTraitementFileUpload() {			return ctrlTraitementFileUpload;		}
		public void setCtrlTraitementFileUpload(DTFileUploadCtrl ctrlTraitementFileUpload) {			this.ctrlTraitementFileUpload = ctrlTraitementFileUpload;		}
	}
	
	/**
	 * Ajouter une nouvelle ligne de traitement
	 * @return
	 */
	protected WOComponent addTraitementAtEnd() {
		Traitement t = new Traitement();
		// si d'autres existent, alors on reprend a la suite
		if (newInterventionTraitementList.count() > 0) {
			Traitement lastT = (Traitement) newInterventionTraitementList.lastObject();
			t.setText(lastT.getText());
			t.setDDeb(lastT.getDFin());
			t.setDFin(t.getDDeb().timestampByAddingGregorianUnits(0, 0, 0, 0, 15, 0));
			t.setConsultable(lastT.getConsultable());
		}
		newInterventionTraitementList.addObject(t);
		return null;
	}
	
	/**
	 * Effacer la ligne de traitement
	 * @return
	 */
	protected WOComponent delTraitement(Traitement t) {
		if (newInterventionTraitementList.count() > 0) {
			newInterventionTraitementList.removeIdenticalObject(t);
		}
		return null;
	}
	
	/**
	 * Effectuer le nettoyage de la structure des traitements :
	 * - suppression des fichiers temporaires utilises par le <code>CktlFileUpload</code>
	 * - RAZ du tableau <code>newInterventionTraitementList</code> 
	 */
	private void clearTraitements() {
		if (!NSArrayCtrl.isEmpty(newInterventionTraitementList)) {
			for (int i = 0; i < newInterventionTraitementList.count(); i++) {
				Traitement t = (Traitement) newInterventionTraitementList.objectAtIndex(i);
				t.clearFilesInfo();
			}
		}
		newInterventionTraitementList = new NSMutableArray();
	}
	
	// ---
	//  DEPLACEMENT DU METIER UTILISE PAR LES DIFFERENTS COMPOSANTS SELECTXX
	// ---
	
  // --------- DEBUT ECRAN CHANGEMENT ACTIVITE ----------
 
  /**
   * L'enregistrement d'activite necessaire au listener du SelectActivite
   */
  protected CktlRecord recVActiviteForActiviteListener() {	
  	return activiteBus().findActivite(interventionSelectedActiviteSelected.numberForKey("actOrdre"), null); 
  }
 
  /**
   * Selection de l'activite, on va le rajouter a la liste
   * des activites selectionnables, et la selectionner.
   */
  public void setActiviteSelectedBySelectActiviteForActiviteListener(CktlRecord value) {
  	activiteList = activiteList.arrayByAddingObject(value);
  	interventionSelectedActiviteSelected = value;
  }
  
  // --------- FIN ECRAN CHANGEMENT ACTIVITE ----------
	
  
  // --------- DEBUT ECRAN CHANGEMENT DEMANDEUR ----------
  
  protected WOComponent callSelectPersonneForPersonneListener(SelectPersonneListener listener) {
  	return SelectPersonne.getNewPage(listener,
             "Indiquez la personne pour le compte de laquelle la demande est faite",
             interventionSelectedContactSelected.numberForKey("noIndividu"), true);
  }

  // Changement du demandeur : traitement appele lors
  // du retour a l'ecran PageDTFast
  
  
	/**
   * Selection de la personne, on va selectionner son contact
   * par defaut, puis l'ajouter a la liste des contacts selectionnables, 
   * et enfin le selectionner.
   */
  protected void doSelectForPersonneListener(Number persId) {
    CktlUserInfo demandeurInfo = new CktlUserInfoDB(dtSession().dataBus());
    demandeurInfo.compteForPersId(persId, true);
    CktlRecord recContactDefault = contactsBus().findDefaultContact(
    		demandeurInfo.noIndividu(), true);
    // ajouter a la liste
    contactList = contactList.arrayByAddingObject(recContactDefault);
    // selection
    interventionSelectedContactSelected = recContactDefault;
  }
  
  
  // --------- FIN ECRAN CHANGEMENT DEMANDEUR ----------
  
	// ---
	//  FIN DEPLACEMENT DU METIER UTILISE PAR LES DIFFERENTS COMPOSANTS SELECTXX
	// ---

	// message d'erreur
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	private void setErrorMessage(String value) {
		errorMessage = value;
	}
	
	// display
	
	/**
	 * Affiche un enregistrement activite, sous forme de mots clefs
	 */
	protected String displayActivite(CktlRecord record) {
		return activiteBus().findActivitePathString(record, true, null);
	}
	
	// getters
	
	public boolean hasError() {
		return !StringCtrl.isEmpty(getErrorMessage());
	}
	
	protected int getMode() {
		return mode;
	}
	
	private WODisplayGroup getInterventionDg() {
		return ((SwapDTFast) getComponent()).getInterventionDg();
	}
	
	protected DTFileUploadCtrl ctrlInterventionFileUpload() {
		return ctrlInterventionFileUpload;
	}
	
	// raccourcis vers les bus
	
	private DTEtatBus etatBus() {
		return dtSession().dataCenter().etatBus();
	}
	
	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
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
}
