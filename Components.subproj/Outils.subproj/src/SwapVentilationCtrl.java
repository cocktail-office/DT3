import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

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
 * Le controleur du composant SwapVentilation
 * 
 * @author ctarade
 */
public class SwapVentilationCtrl 
	extends DTComponentCtrl {

	// liste des services DT
	public NSArray serviceList;
	public CktlRecord serviceSelected;
	public boolean showServiceList;
	
	// pour affichage
	private int sizeMaxIntCleService; 
	private int sizeBrwsrIntervention;
	private int sizeBrwsrInterventionPanier;

	// taille des browser en nombre de lignes
	protected final static int SIZE_BROWSER = 15;
	protected final static int SIZE_BROWSER_PANIER = 8;

	/** panier */
	public NSMutableArray individuPanierList;
	public NSMutableArray interventionPanierList;

	/** selections du panier */
	public NSArray individuPanierSelecteds;
	public NSArray interventionPanierSelecteds;

	// message d'erreur
	private String errorMessage;
	
	// avancement de la tache de ventilation
	private int countDesaffectation;
	
	/** detail d'une DT */
	protected CktlRecord singleDetailDt;
	
	public SwapVentilationCtrl(SwapVentilation component) {
		super(component);
		initCtrl();
	}

	/**
	 * Initialisation du controleur
	 */
	private void initCtrl() {
		// les services destinataires : la liste des services dispo pour cet utilisateur
		NSArray allserviceList = (NSArray) preferencesBus().findAllPrefDroits(dtUserInfo().noIndividu()).valueForKey("toStructureUlr");
		serviceList = new NSArray();
    // on ne conserve que les services surlesquels il a au moins le droit SUPER_UTILISATEUR
    for (int i = 0; i < allserviceList.count(); i++) {
    	CktlRecord recService = (CktlRecord) allserviceList.objectAtIndex(i);
    	if (dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER, recService.stringForKey("cStructure"))) {
    		serviceList = serviceList.arrayByAddingObject(recService);
    	}
    }
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
		// init des variables
		sizeMaxIntCleService = 0;
		sizeBrwsrIntervention = SIZE_BROWSER;
		sizeBrwsrInterventionPanier = SIZE_BROWSER_PANIER;
		// panier
		individuPanierList = new NSMutableArray();
		interventionPanierList = new NSMutableArray();
		// erreur
		setErrorMessage(StringCtrl.emptyString());
	}

	// access base de donnees
	
	/**
	 * Recharger le contenu du display group des individu
	 * lors du changement du service
	 */
	private void fetchIndividuDg() {
		if (serviceSelected != null) {
			getIndividuDg().queryBindings().setObjectForKey(serviceSelected.stringForKey("cStructure"), "cStructureDroit");
			getIndividuDg().qualifyDataSource();
		}
	}

	/**
	 * Le changement des individus selectionnes entraine la mise 
	 * a jour de toutes les DTs en cours. Attention, la limite
	 * est de 60 intervenants (cf fetchSpec du modele)
	 */
	private void fetchInterventionDg() {
		getInterventionDg().queryBindings().removeAllObjects();
		NSArray individuSelecteds = getIndividuDg().selectedObjects();
		if (!NSArrayCtrl.isEmpty(individuSelecteds) && serviceSelected != null) {
			getInterventionDg().queryBindings().setObjectForKey(serviceSelected.stringForKey("cStructure"), "cStructure");
			for (int i = 0; i < individuSelecteds.count(); i++) {
				CktlRecord recIndividu = (CktlRecord) individuSelecteds.objectAtIndex(i);
				String key = "noIndividu" + (i<10 ? "0" : "") + String.valueOf(i);
				getInterventionDg().queryBindings().setObjectForKey(recIndividu.numberForKey("noIndividu"), key);
			}
		} else {
			// vider la liste
			getInterventionDg().queryBindings().setObjectForKey(new Integer(-1), "noIndividu00");
		}
		// fetch dans la bdd
		getInterventionDg().qualifyDataSource();
		// calcul des tailles pour affichage
    Integer maxIntCleService = (Integer) getInterventionDg().allObjects().valueForKey("@max.intCleService");
    sizeMaxIntCleService = (maxIntCleService != null ?
        Integer.toString(maxIntCleService.intValue()).length() : 0);
    int n = getInterventionDg().allObjects().count();
    sizeBrwsrIntervention = ( n > SIZE_BROWSER ? n : SIZE_BROWSER);
    n = interventionPanierList.count();
    sizeBrwsrInterventionPanier = ( n > SIZE_BROWSER_PANIER ? n : SIZE_BROWSER_PANIER);
	}
	
	// setters
	
	/**
	 * La liste des contacts et des activites sont modifiees 
	 * quand le service change
	 */
	public void setServiceSelected(CktlRecord value) {
		serviceSelected = value;
    // recharger la liste des individus 
		fetchIndividuDg();
	}
		
	/**
	 * Selection du service selectionn� a partir de son C_STRUCTURE
	 */
	private void setCStructureServiceSelected(String value) {
    NSArray recs = EOQualifier.filteredArrayWithQualifier(
    		serviceList, DTDataBus.newCondition("cStructure='"+value+"'"));
    setServiceSelected((CktlRecord) recs.objectAtIndex(0));
	}
		
	// operations
	
	protected void filtrer() {
		fetchInterventionDg();
	}
	
	protected void removeIndividuPanier() {
		individuPanierList.removeObjectsInArray(individuPanierSelecteds);
	}

	protected void removeInterventionPanier() {
		interventionPanierList.removeObjectsInArray(interventionPanierSelecteds);
	}

	protected void addIndividuPanier() {
		// on ajoute pas 2 fois les memes individus
		for (int i = 0; i < getIndividuDg().selectedObjects().count(); i++) {
			CktlRecord recIndividu = (CktlRecord) getIndividuDg().selectedObjects().objectAtIndex(i);
			if (!individuPanierList.containsObject(recIndividu)) {
				individuPanierList.addObject(recIndividu);
			}
		}
	}

	protected void addInterventionPanier() {
		// on ajoute pas 2 fois les memes DT
		for (int i = 0; i < getInterventionDg().selectedObjects().count(); i++) {
			CktlRecord recIntervention = (CktlRecord) getInterventionDg().selectedObjects().objectAtIndex(i);
			if (!interventionPanierList.containsObject(recIntervention)) {
				interventionPanierList.addObject(recIntervention);
			}
		}
	}

	/**
	 * Effectue la ventilation :
	 * - desaffecter tous les agents (qui n'ont pas saisi de traitement) sur
	 * 	la liste des DT selectionnees du panier <code>interventionSelecteds</code>
	 * - affecter les agents selectionnes du panier <code>individuPanierList</code>
	 * 	a la liste des DT selectionnees du panier <code>interventionSelecteds</code>
	 * @return
	 */
	protected boolean doVentiler() {
		// RAZ du message d'erreur
		setErrorMessage(StringCtrl.emptyString());
		boolean hasErrors = false;
		// RAZ etats
		countDesaffectation = 0;
  	// liste des intervennts
		NSArray nosIndNewIntervenant = (NSArray) individuPanierSelecteds.valueForKey("noIndividu");
    // On commence la transaction interne
    Integer transId = interventionBus().beginECTransaction();
    // On supprime d'abord les affectation en cours
    for(int i=0; i<interventionPanierSelecteds.count() && (!hasErrors); i++) {
      CktlRecord rec = (CktlRecord) interventionPanierSelecteds.objectAtIndex(i);
      NSArray intervenants = rec.arrayForKey("tosIntervenant");
      for(int j=0; j<intervenants.count() && (!hasErrors); j++) {
      	Number noIndOldIntervenant = ((CktlRecord)intervenants.objectAtIndex(j)).numberForKey("noIndividu");
      	// on ne supprime pas les intervenants qui sont dans la liste finale
      	boolean isAlsoNewIntervenant = false;
      	for (int k = 0; !isAlsoNewIntervenant && k < nosIndNewIntervenant.count(); k++) {
      		Number noIndNewIntervenant = (Number) nosIndNewIntervenant.objectAtIndex(k);
      		if (noIndNewIntervenant.intValue() == noIndOldIntervenant.intValue()) {
      			isAlsoNewIntervenant = true; 	
      		}
				}
      	if (!isAlsoNewIntervenant) {
          hasErrors = !interventionBus().deleteIntervenant(
              transId, rec.numberForKey("intOrdre"), noIndOldIntervenant);
          if (hasErrors) {
          	setErrorMessage("Suppression des affectations précédentes a échoué !");
          }
      	}
      }
      countDesaffectation++;
    }
    if (!hasErrors) {
      // Si tout ce passe bien, on continue avec la nouvelle affectation
      hasErrors = !interventionBus().performAffectation(
      		dtUserInfo(), interventionPanierSelecteds, nosIndNewIntervenant, dtUserInfo().noIndividu(), null, null);
      if (hasErrors) {
      	setErrorMessage("Suppression des affectations précédentes a échoué !");
      }
    }
    if (!hasErrors) {
    	interventionBus().commitECTransaction(transId);
    }

		return hasErrors;
	}
	
	/**
	 * Selection d'une DT pour detail (ne marche que si une 
	 * seul demande est selectionnee )
	 */
	protected void selectSingleDetailDt() {
		singleDetailDt = null;
		if (getInterventionDg().selectedObjects().count() == 1) {
			singleDetailDt = (CktlRecord) getInterventionDg().selectedObjects().lastObject();
		}
	}
	
	// getters
	
	private WODisplayGroup getIndividuDg() {
		return ((SwapVentilation) getComponent()).getIndividuDg();
	}
	
	private WODisplayGroup getInterventionDg() {
		return ((SwapVentilation) getComponent()).getInterventionDg();
	}
	
	/**
	 * Pour affichage d'une intervention dans la liste
	 */
	protected int getSizeMaxIntCleService() {
		return sizeMaxIntCleService;
	}

	/**
	 * Pour affichage du browser des interventions
	 */
	protected int getSizeBrwsrIntervention() {
		return sizeBrwsrIntervention;
	}
	
	/**
	 * Pour affichage du browser des interventions dans le panier
	 */
	protected int getSizeBrwsrInterventionPanier() {
		return sizeBrwsrInterventionPanier;
	}
	
	/**
	 * L'avancement de la tache de ventilation
	 * @return
	 */
	protected String htmlAdvanceString() {
		int total = interventionPanierSelecteds.count();
		String str = "D&eacute;saffectation de DT : " + countDesaffectation + "/" + total + "<br/>";
		str += "R&eacute;affectation de DT = " + interventionBus().countReaffectation() + "/" + total;
		return str;
	}
	
	// message d'erreur
	
	private void setErrorMessage(String value) {
		errorMessage = value;
	}
	
	protected String getErrorMessage() {
		return errorMessage;
	}
	
	// bus de donnees
	
	private DTPreferencesBus preferencesBus() {
		return dtSession().dataCenter().preferencesBus();
	}
	
	private DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}
}
