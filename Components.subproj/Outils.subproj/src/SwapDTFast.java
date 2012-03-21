


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
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;

/**
 * Interface de saisie et traitement rapide de DT
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class SwapDTFast 
	extends DTWebComponent
		implements I_SelectActiviteReferer {

	// le mode d'utilisation de cette page
	private final static int MODE_READ 	= SwapDTFastCtrl.MODE_READ;
	private final static int MODE_EDIT 	= SwapDTFastCtrl.MODE_EDIT;
	private final static int MODE_ADD 	= SwapDTFastCtrl.MODE_ADD;

	// etats
	public CktlRecord etatItem;
	
	// panneau de consultation services destinataires
	public CktlRecord serviceItem;
	
	// panneau de consultation : contacts
	public CktlRecord contactItem;
	
	// panneau de consultation : activites
	public CktlRecord activiteItem;
	
	// interventions repondant aux filtres
	public CktlRecord interventionItem;
	
	// modes d'appel
	public CktlRecord modeAppelItem;
	
	// nouvelle intervention : les traitements
	public SwapDTFastCtrl.Traitement newInterventionTraitementItem;
	
	// le controleur de cette page
	public SwapDTFastCtrl ctrl;
	
	// interventions repondant aux filtres (oblige de le mettre ici car 
	// le fichier .woo l'impose)
	public WODisplayGroup interventionDg;
	
	public SwapDTFast(WOContext context) {
		super(context);
		initComponent();
	}
	
	/**
	 * initialisation du composant
	 */
	private void initComponent() {
		ctrl = new SwapDTFastCtrl(this);
	}


	// actions
	
	/**
	 * Action par defaut du formulaire : ne rien faire
	 */
	public WOComponent doNothing() {
		return ctrl.doNothing();
	}
	
	/**
	 * Changer uniquement la periode 
	 */
	public WOComponent changePeriode() {
		return ctrl.changePeriode();
	}
		
	// boolean interface

	/**
	 * Le panneau de consultation n'est disponible qu'en mode consultation
	 */
	public boolean showConsultPanel() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * Le panneau de creation n'est disponible qu'en mode creation
	 */
	public boolean showCreatePanel() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * La liste de toutes les DT n'est visible que pour la consultation et
	 * la modification (elle sera disabled en modif)
	 */
	public boolean showComboIntervention() {
		return ctrl.getMode() != MODE_ADD;
	}

	/**
	 * On autorise pas le changement de la DT en cours si on l'edite
	 * @return
	 */
	public boolean isDisabledComboIntervention() {
		return ctrl.getMode() == MODE_EDIT;
	}
	
	/**
	 * On modifie le motif en modification, ou en creation.
	 * @return
	 */
	public boolean isDisabledTextMotif() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * On modifie le commentaire interne en modification, ou en creation.
	 * @return
	 */
	public boolean isDisabledTextCommentaireInterne() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * L'activite n'est modifiable en mode edition, ou en creation.
	 * @return
	 */
	public boolean isDisabledComboActiviteIntervention() {
		return ctrl.getMode() == MODE_READ;
	}

	/**
	 * Aller chercher une activite pas presente dans la liste n'est
	 * possible qu'en mode creation
	 * @return
	 */
	public boolean showBtnChangeActiviteAutre() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * Le contact n'est modifiable en mode edition, ou en creation.
	 * @return
	 */
	public boolean isDisabledComboContactIntervention() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * Aller chercher un contact pas present dans la liste n'est
	 * possible qu'en mode creation
	 * @return
	 */
	public boolean showBtnChangeContactAutre() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * Les types d'appel ne sont modifiables qu'en ajout et edition
	 * @return
	 */
	public boolean isDisabledComboModeAppelIntervention() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * On ajoute des traitement qu'en edition ou ajout
	 * @return
	 */
	public boolean showBtnAddTraitementAtEnd() {
		return ctrl.getMode() != MODE_READ;
	}
	
	/**
	 * On modifie le contenu du traitement en modification, ou en creation.
	 * @return
	 */
	public boolean isDisabledTextTraTraitement() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * On supprime des traitement qu'en edition ou ajout
	 * @return
	 */
	public boolean showBtnDelTraitement() {
		return ctrl.getMode() != MODE_READ;
	}	
	
	/**
	 * Le bouton "nouvelle DT" est disponible si on est mode lecture
	 */	
	public boolean showLnkNewIntervention() {
		return ctrl.getMode() == MODE_READ;
	}
	
	/**
	 * On ne peut annuler qu'en ajout ou en modif
	 */
	public boolean showLnkCancel() {
		return ctrl.getMode() != MODE_READ;
	}

	/**
	 * L'envoi des fichiers n'est autorise qu'en ajout
	 * @return
	 */
	public boolean showTraitementFileUpload() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * On ne peut modifier que si une DT est selectionnee et qu'on
	 * est en mode lecture
	 * TODO pour l'instant on edite pas
	 */
	public boolean showLnkEdit() {
		//return mode == MODE_READ && interventionSelected != null;
		return false;
	}

	/**
	 * L'envoi des fichiers n'est autorise qu'en ajout
	 * @return
	 */
	public boolean showInterventionFileUpload() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * La coche d'envoi de mail est visible en creation uniquement
	 * @return
	 */
	public boolean showCheckSendMails() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	/**
	 * Creation de DT non affectee / non validee
	 * @return
	 */
	public boolean showBtnCreate() {
		return ctrl.getMode() == MODE_ADD;
	}

	/**
	 * Creation de DT en cours affectee a soi meme
	 * @return
	 */
	public boolean showBtnCreateAndAffectSelf() {
		return ctrl.getMode() == MODE_ADD;
	}

	/**
	 * Creation de DT traitee affectee a soi meme
	 * @return
	 */
	public boolean showBtnCreateAndAffectSelfAndClose() {
		return ctrl.getMode() == MODE_ADD;
	}
	
	// getter
	
	
	// le controleur d'envoi de fichier pour la DT
	public DTFileUploadCtrl ctrlInterventionFileUpload() {
		return ctrl.ctrlInterventionFileUpload();
	}

	
	// navigation
	
	/**
	 * Passer en mode ajout d'une nouvelle DT, on initialise les variables
	 * locales a la DT en cours.
	 */
	public WOComponent newIntervention() {
		return ctrl.newIntervention();
	}
	
	/**
	 * Annuler l'operation en cours (repasser en mode lecture) :
	 * - ajout : reselection de l'intervention en cours
	 * - modification : rien
	 * @return
	 */
	public WOComponent cancel() {
		return ctrl.cancel();
	}
	
	/**
	 * Passer en mode modification de la DT en cours 
	 * @return
	 */
	public WOComponent edit() {
		return ctrl.edit();
	}
	
	/**
	 * Nouvelle DT : ne pas l'affecter.
	 * @return
	 */
	public WOComponent create() {
		return ctrl.create(parent());
	}
	
	/**
	 * Nouvelle DT : se l'affecter et la mettre en cours
	 * @return
	 */
	public WOComponent createAndAffectSelf() {
		return ctrl.createAndAffectSelf();
	}
	
	/**
	 * Nouvelle DT : se l'affecter et la traiter
	 * @return
	 */
	public WOComponent createAndAffectSelfAndClose() {
		return ctrl.createAndAffectSelfAndClose(parent());
	}
	
	
	// traitements
	
	/**
	 * Ajouter une nouvelle ligne de traitement
	 * @return
	 */
	public WOComponent addTraitementAtEnd() {
		return ctrl.addTraitementAtEnd();
	}
	
	/**
	 * Effacer la ligne de traitement
	 * @return
	 */
	public WOComponent delTraitement() {
		return ctrl.delTraitement(newInterventionTraitementItem);
	}
	
  // --------- DEBUT ECRAN CHANGEMENT ACTIVITE ----------
 
	/**
   * Choisir une activite qui n'est pas presente dans la liste
   * de celle proposees
   */
  public WOComponent changeActiviteAutre() {
    // On va selectionner l'activite de service destinataire
    SelectActivite pageActivite = (SelectActivite) pageWithName(SelectActivite.class.getName());
    activiteListener = new PageDTFastActiviteListener();
    pageActivite.setListener(activiteListener);
    return pageActivite;
  }
	
  /**
   * La classe listener de gestion de la page a afficher SelectActivite.
   * 
   * @author ctarade
   */
  public class PageDTFastActiviteListener extends SelectActiviteListener {
  	public void doAfterActiviteSelectedItem() {}
    protected CktlRecord recVActivite() {	
    	return ctrl.recVActiviteForActiviteListener(); 
    }
    public Session session() {      return dtSession();    }
    public NSArray allNodes() {	return session().activitesNodes(); }
    public String formName() {      return null;    }
    public void doAfterSearchActivite() {    }
    public WOComponent caller() {      return SwapDTFast.this; }
    public WOComponent alternateCaller() {      return parent(); }
    public boolean shouldSelectedOnlyLeaf() {return true;}
		public boolean showHiddenActivite() {	return dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE, recVActivite().stringForKey("cStructure"));}
		/** c'est un intervenant qui accede a cet écran, on affiche les activités underscore */
		public boolean showUnderscoredActivite() { return true;	}
		@Override
		public boolean showActivitesFavoritesDemandeur() {
			// TODO Auto-generated method stub
			return true;
		}
		@Override
		public boolean showActivitesFavoritesIntervenant() {
			// TODO Auto-generated method stub
			return true;
		}
  }
  
  public PageDTFastActiviteListener activiteListener;
    
  // -- methodes de l'interface SelectActiviteConsumer utilise par SelectActivite --
  public void setMotsClesSelectedBySelectActivite(String value) {}
  public void saveUpdateActiviteBySelectActivite() {}

  /**
   * Selection de l'activite, on va le rajouter a la liste
   * des activites selectionnables, et la selectionner.
   */
  public void setActiviteSelectedBySelectActivite(CktlRecord value) {
  	ctrl.setActiviteSelectedBySelectActiviteForActiviteListener(value);
  }
  
  // --------- FIN ECRAN CHANGEMENT ACTIVITE ----------
	
  
	
  // --------- DEBUT ECRAN CHANGEMENT DEMANDEUR ----------

  public WOComponent changeContactAutre() {
  	PageDTFastPersonneListener personneListener = new PageDTFastPersonneListener();
    return ctrl.callSelectPersonneForPersonneListener(personneListener);
  }
  
  /**
   * La classe listener du composant permettant de changer le demandeur
   * s'il n'est pas dans la liste des contacts proposes par defaut
   * 
   * @author ctarade
   */
  private class PageDTFastPersonneListener implements SelectPersonneListener {
   
  	/**
     * Selection de la personne, on va selectionner son contact
     * par defaut, puis l'ajouter a la liste des contacts selectionnables, 
     * et enfin le selectionner.
     */
    public WOComponent select(Number persId) {
      ctrl.doSelectForPersonneListener(persId);
      return parent();
    }
    
    public WOComponent cancel() {      return parent();    }
    public WOContext getContext() {      return context();    }
  }
  
  
  // --------- FIN ECRAN CHANGEMENT DEMANDEUR ----------
  
	// display
	
	/**
	 * Contact : attention, peut provenir de l'entite <code>VContactIntervenant</code>
	 * mais aussi de <code>Contact</code> (dans le cas de la selection d'un contact
	 * autre)
	 */
	public String contactItemDisplay() {
		String nomEtPrenom = "";
		String ctEmail = "";
		String lcStructure = "";
		try {
			// objects de <code>VContactIntervenant</code>
			nomEtPrenom = contactItem.stringForKey("nomEtPrenom");
			ctEmail = contactItem.stringForKey("ctEmail");
			lcStructure = contactItem.stringForKey("lcStructure");
		} catch (Exception e) {
			// objects de <code>Contact</code>
			CktlUserInfoDB ui = new CktlUserInfoDB(dtSession().dataBus());
			ui.individuForNoIndividu(contactItem.numberForKey("noIndividu"), true);
			nomEtPrenom = ui.nomEtPrenom();
			ctEmail = ui.email();
		}
		String display = nomEtPrenom;
		if (!StringCtrl.isEmpty(ctEmail)) {
			display += " - " + ctEmail;
		}
		if (!StringCtrl.isEmpty(lcStructure)) {
			display += " (" + lcStructure + ")";
		}
		return display; 

	}
	
	/**
	 * Activite : chemin complet
	 */
	public String activiteItemDisplay() {
		return ctrl.displayActivite(activiteItem); 
	}
	
	/**
	 * Intervention
	 */
	public String interventionItemDisplay() {
		return DTStringCtrl.compactString("#"+interventionItem.intForKey("intCleService") + " " + interventionItem.stringForKey("intMotif"), 100, "<...>"); 
	}
	
	/**
	 * Mode d'appel
	 */
	public String modeAppelItemDisplay() {
		return modeAppelItem.stringForKey("modLibelle"); 
	}
	
	/**
	 * Etat
	 */
	public String etatItemDisplay() {
		return etatItem.stringForKey("etatLibelle");
	}
	
	
	/**
	 * Groupe DT destinataire
	 */
	public String serviceItemDisplay() {
		return serviceItem.stringForKey("lcStructure");
	}
	
	/**
	 * Groupe DT destinataire (affichage long dans l'ecran de creation)
	 */
	public String serviceSelectedDisplay() {
		return ctrl.serviceSelected.stringForKey("llStructure");
	}
	
	// getters
	
	/**
	 * Getter pour le controleur
	 */
	public WODisplayGroup getInterventionDg() {
		return interventionDg;
	}
}
	