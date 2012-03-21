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

import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;

/**
 * Interface de ventilation de demande de travaux
 * 
 * @author ctarade
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Thierry SAIVRE <thierry.saivre at univ-lr.fr>
 */
public class SwapVentilation 
	extends DTWebComponent
		implements IDTLongPageCaller {

	// item service
	public CktlRecord serviceItem;
	
	/** liste des intervenants du service */
	public WODisplayGroup individuDg;
	public CktlRecord individuItem;
	
	/** liste des interventions des individus selectionnes */
	public WODisplayGroup interventionDg;
	public CktlRecord interventionItem;
	
	/** le controleur du composant */
	public SwapVentilationCtrl ctrl;
	
	/** dt detaillee */
	public String txtSingleDetailDt;
	public NSArray detailSingleDtIntervenant;
	
	private final static String DEFAULT_TXT_SINGLE_DETAIL_DT = "<Pas de selection ou selection multiple>";
	
	/** le contenu du tooltip d'aide a l'utilisation */
	private final static String HTML_TOOLTIP_HELP =
		"<b>Utilisation du module Ventilation</b><br/><br/>" +
		"<em><u>Description</u></em>" +
		"<ul>" +
		"<li>ce module permet de r&eacute;affecter &quot;en masse&quot; les DTs d'un service</li>" +
		"<li>il peut aussi servir pour voir rapidement les DTs affect&eacute;es &agrave; un &quot;panier d'agents&quot;</li>" +
		"</ul>" +
		"<br/>" +
		"<em><u>Utilisation</u></em>" +
		"<ol>" +
		"<li>Selectionnez une liste d'intervenants &quot;&agrave; d&eacute;charger&quot; (zone <b>[A]</b>)</li>" +
		"<li>Afficher la liste de leur DT en cours et en attente (bouton <b>[B]</b>)</li>" +
		"<li>Ces DT apparaissent dans la liste (liste <b>[C1]</b>)</li>" +
		"<li>Vous pouvez &eacute;ventuellement obtenir le d&eacute;tail d'une demande (bouton <b>[C2]</b>). Attention, il faut 1 seule DT selectionn&eacute;e.</li>" +
		"<li>Selectionnez la nouvelle liste d'agents qui r&eacute;cup&egrave;reront les DTs (zone <b>[A]</b>).</li>" +
		"<li>Faites les basculer dans le panier d&eacute;di&eacute; (bouton <b>[D1]</b>).</li>" +
		"<li>Selectionnez la liste des DTs &grave; redistribuer (liste <b>[C1]</b>).</li>" +
		"<li>Faites les basculer dans le panier d&eacute;di&eacute; (bouton <b>[D2]</b>).</li>" +
		"<li>Ajustez vos paniers de redistribution (listes <b>[E1]</b> et <b>[E2]</b>).</li>" +
		"<li>Effectuez la ventilation (bouton <b>[F]</b>)." +
		"<ul>" +
		"<li>Les DTs selectionn&eacute;es dans la liste <b>[E2]</b> seront affect&eacute;es aux agents selectionn&eacute;s dans la liste <b>[E1]</b> (les mails d'affectations sont envoy&eacute;s)</li>"+
		"<li>Les anciens agents affect&eacute;s ne feront plus partie de la liste des nouveaux intervenants (sauf s'ils sont toujours dans la liste finale ...)</li>"+
		"<li>Tous les traitements en cours seront supprim&eacute;s ! (sauf si leur auteur font toujours partie de la liste finale d'intervenants...) </li>"+
		"</ul>" +
		"</li>" +
		"</ol>";
		
	
	public SwapVentilation(WOContext context) {
		super(context);
		initComponent();
	}
	
	/**
	 * initialisation du composant
	 */
	private void initComponent() {
		ctrl = new SwapVentilationCtrl(this);
		// init variables
		txtSingleDetailDt = DEFAULT_TXT_SINGLE_DETAIL_DT;
		detailSingleDtIntervenant = new NSArray();
	}
	
	// getters
	
	/**
	 * Getter pour le controleur
	 */
	public WODisplayGroup getIndividuDg() {
		return individuDg;
	}

	/**
	 * Getter pour le controleur
	 */
	public WODisplayGroup getInterventionDg() {
		return interventionDg;
	}
	
	/**
	 * La documentation HTML
	 */
	public String getHtmlToolTipHelp() {
		return HTML_TOOLTIP_HELP;
	}
	
	// navigation
	
	/**
	 * Action par defaut du formulaire
	 */
	public WOComponent doNothing() {
		return null;
	}
	
	public WOComponent filtrer() {
		ctrl.filtrer();
		return null;
	}
	
	public WOComponent removeIndividuPanier() {
		ctrl.removeIndividuPanier();
		return null;
	}
	
	public WOComponent removeInterventionPanier() {
		ctrl.removeInterventionPanier();
		return null;
	}
	
	public WOComponent addIndividuPanier() {
		ctrl.addIndividuPanier();
		return null;
	}
	
	public WOComponent addInterventionPanier() {
		ctrl.addInterventionPanier();
		return null;
	}
	
	/**
	 * Selection de tous les individus du panier
	 */
	public WOComponent selectAllIndividuPanier() {
		ctrl.individuPanierSelecteds = ctrl.individuPanierList;
		return null;
	}
	
	/**
	 * Selection de toutes interventions du panier
	 */
	public WOComponent selectAllInterventionPanier() {
		ctrl.interventionPanierSelecteds = ctrl.interventionPanierList;
		return null;
	}
	
	/**
	 * Selection d'une DT pour detail
	 * @return
	 */
	public WOComponent selectSingleDetailDt() {
		ctrl.selectSingleDetailDt();
		// initialisation des variables associees
		if (ctrl.singleDetailDt != null) {
			txtSingleDetailDt = ctrl.singleDetailDt.stringForKey("intMotif");
			detailSingleDtIntervenant = ctrl.singleDetailDt.arrayForKeyPath("tosIntervenant.toIndividuUlr");
		} else {
			txtSingleDetailDt = DEFAULT_TXT_SINGLE_DETAIL_DT;
			detailSingleDtIntervenant = new NSArray();
		}
		return null;
	}
	
	
	// display
	
	/**
	 * Service
	 */
	public String serviceItemDisplay() {
		return serviceItem.stringForKey("lcStructure");
	}
	
	/**
	 * Individu
	 */
	public String individuItemDisplay() {
		return individuItem.stringForKey("nomEtPrenom");
	}
	
	/**
	 * Le nom prenom formate d'une personne
	 * @return
	 */
	private String displayIndividu(CktlRecord recIndividu) {
    String nomDemandeur = null;
    if (recIndividu != null) {
      nomDemandeur = StringCtrl.formatName(recIndividu.stringForKey("prenom"), recIndividu.stringForKey("nomUsuel"));
    } else
      nomDemandeur = StringCtrl.emptyString();
    return nomDemandeur;
  }
	
	private final static String SEP = "&nbsp;&#124;&nbsp;";
	
	/**
	 * Intervention : numero / demandeur / date / motif
	 */
	public String interventionItemDisplay() {
		String display = 
			DTStringCtrl.fitToSize(Integer.toString(interventionItem.intForKey("intCleService")), ctrl.getSizeMaxIntCleService(), null) + SEP +
			DTStringCtrl.fitToSize(displayIndividu(interventionItem.recForKey("toIndividuConcerne")), 12, ".") + SEP + 
			DateCtrl.dateToString(interventionItem.dateForKey("intDateCreation"), "%d/%m/%y") + SEP + 
			interventionItem.stringForKey("intMotif");
		display = StringCtrl.replace(display, " ", "&nbsp;");
		display = StringCtrl.replace(display, "\n", "&nbsp;");
		display = StringCtrl.replace(display, "\r", "&nbsp;");
		return display; 
	}

	
	// interface
	
	/**
	 * La hauteur du browser intervention en nombre de lignes.
	 * On adapte selon le nombre d'enregistrement trouves.
	 */
	public int sizeBrwsrIntervention() {
		return ctrl.getSizeBrwsrIntervention();
	}
	
	public int sizeBrwsrIndividu() {
		return SwapVentilationCtrl.SIZE_BROWSER;
	}
	
	public int sizeBrwsrInterventionPanier() {
		return ctrl.getSizeBrwsrInterventionPanier();
	}
	
	public int sizeBrwsrIndividuPanier() {
		return SwapVentilationCtrl.SIZE_BROWSER_PANIER;
	}
	
	public String getErrorMessage() {
		return ctrl.getErrorMessage();
	}
	
	public boolean hasError() {
		return !StringCtrl.isEmpty(getErrorMessage());
	}
	
	// long page
	
	public WOComponent doVentiler() {
		DTLongPage longPage = (DTLongPage) pageWithName(DTLongPage.class.getName());
		longPage.setComponent(this);
		return longPage;
	}
	
	/**
	 * La tache "longue" traitee par la <code>DTLongPage</code>
	 */
	public void doTheJob() {
		ctrl.doVentiler();
	}

	/**
	 * Pour affichage correct du resultat en page pleine
	 */
	public WOComponent finishPage() {
		return parent();
	}
	
	public String htmlAdvanceString() {
		return ctrl.htmlAdvanceString();
	}
}