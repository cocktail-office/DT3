
import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/*
 * Copyright Universit� de La Rochelle 2006
 *
 * ctarade@univ-lr.fr
 *
 * Ce logiciel est un programme informatique servant � g�rer les comptes
 * informatiques des utilisateurs. 
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
 * Interface a utiliser dans les pages qui souhaite afficher
 * le composant SelectActivite a l'interieur d'elle meme.
 * Il faudra creer une classe qui herite de SelectActiviteListener
 * pour pouvoir piloter le composant SelectActivite
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public abstract class SelectActiviteListener {

  /** Le chemin des activites actuellement selectionnees. */
  public NSMutableArray<ActivitesNode> activiteSelectedPath;
  private ActivitesNode activiteSelectedItem;
  private ActivitesNode activitesZeroItem;
  
  /**
   * Remettre � zero la selection (appel� au changement de service 
   * de <code>PageCreation</code>)
   */
  public void clearActiviteSelection() {
    activiteSelectedItem = null;
    activitesZeroItem = null;
    if (activiteSelectedPath != null) {
    	activiteSelectedPath.removeAllObjects();
    } else {
    	activiteSelectedPath = new NSMutableArray<ActivitesNode>();
    }
  }
  
  public boolean activiteDispoLeafYN() {
    if (activiteSelectedItem == null) {
    	return false;
    } else {
    	return activiteSelectedItem.isLeaf();
    }
  }
  
  /**
   * Le traitement a effectuer lors de la selection
   * de l'activite. On appelle la methode 
   */
  public void setActiviteSelectedItem(ActivitesNode selectedItem) {
    activiteSelectedItem = selectedItem;
    doAfterActiviteSelectedItem();
    resetSearchErrors();
  }
  
  /**
   * Le traitement a faire lors de la selection de l'activite
   */
  public abstract void doAfterActiviteSelectedItem();
  
  /** 
   * L'activite point d'entree (c'est l'activite "service").
   * Retourner <em>null</em> si non fixe.
   */
  protected abstract CktlRecord recVActivite();

  /** 
   * Le formulaire dans lequel est affiche le composant.
   * Si null est sa valeur, alors le composant en creera 
   * 1 automatiquement et il sera consid�r� comme 
   * affich� en page pleine.
   */
  public abstract String formName();
  
  /** le databus a utiliser pour effectuer le parcours de la base d'activites */
  //public abstract DTActiviteBus activiteBus();
  
  public abstract Session session();
  
  /** le tableau contenant tous les nodes instancies pour cette session */
  public abstract NSArray<ActivitesNode> allNodes();
  
  /** indique si les activites cachees doivent etre visibles */
  public abstract boolean showHiddenActivite();
  
  /** indique si les activités dont le libellé commence par un "_" doivent être affichées */
  public abstract boolean showUnderscoredActivite();

  /** faut-il afficher la suggestion d'activites en tant que demandeur */
  public abstract boolean showActivitesFavoritesDemandeur();

  /** faut-il afficher la suggestion d'activites en tant qu'intervenant */
  public abstract boolean showActivitesFavoritesIntervenant();

  /**
   * le titre de l'ajax expansion pour les activités favorites
   */
  public String getTitreExpansionActivitesFavorites() {
  	String titre = "Mots cles les plus utilises ";
  	
  	if (showActivitesFavoritesDemandeur()) {
  		titre += "dans vos dernieres demandes ";
  	}
  	
  	if (showActivitesFavoritesIntervenant()) {
  		
  		if (showActivitesFavoritesDemandeur()) {
  			titre += "et ";
  		}
  		
  		titre += "dans les DTs auxquelles vous etes affecte";
  	}
  	
  	titre += " ...";
  	
  	return titre;
  }
  
  /**
   * Binding <code>selectedObject</code> du <code>CktlHXBrowser</code>
   */
  public ActivitesNode getActiviteSelectedItem() {
    return activiteSelectedItem;
  }
  
  /**
   * Binding <code>zeroItem</code> du <code>CktlHXBrowser</code>
   * Le composant est reinstanci� lorsque le service de l'activite du 
   * listener est diff�rent.
   */
  public ActivitesNode activitesZeroItem() {
  	if (activitesZeroItem == null || !activitesZeroItem.getRootCStructure().equals(getRootCStructure())) {
      activitesZeroItem = new ActivitesNode(
      		null, activiteBus(), allNodes(), showHiddenActivite(), showUnderscoredActivite());
      activitesZeroItem.setRootCStructure(getRootCStructure());
    }
    return activitesZeroItem;
  }

  /**
   * Binding <code>activiteSelectedPath</code> du <code>CktlHXBrowser</code>
   */
  public String activiteSelectionPath() {
    return pathDisplay(activiteSelectedPath);
  }
  
  /**
   * Afficher le libelle de l'activit� arborescent.
   * @param records : l'arborescence de <code>ActivitesNode</code>
   * @return
   */
  private String pathDisplay(NSArray<ActivitesNode> records) {
    if ((records == null) || (records.count() == 0)) {
      return "&lt;Aucune selection&gt;";
    } else {
      StringBuffer sb = new StringBuffer("");
      for(int i=0; i<records.count(); i++) {
        sb.append("&raquo;&nbsp;").append(((ActivitesNode)records.objectAtIndex(i)).nodeRecord.valueForKey("actLibelle")).append("&nbsp;");
      }
      return sb.toString();
    }
  }
  
  /**
   * Retourne l'enregistrement correspondant au dernier noeud selectionne
   * dans le browser des activites.
   * Est utilis� par les pages externes pour connaitre la selection
   */
  public CktlRecord browserRecord() {
    if (activiteSelectedPath != null && activiteSelectedPath.count() > 0)
      return ((ActivitesNode)activiteSelectedPath.lastObject()).nodeRecord;
    else
      return null;
  }

  
  // LES ELEMENTS AUTRES QUE POUR LE BROWSER
  

  
  /** le textfield de recherche */
  public String strSearch;
  
  /** la taille minimum de la chaine a rechercher */
  public boolean errSizeStrSearch;
  public final int MIN_SIZE_STRING_SEARCH = 2;

  /** les resultats de recherche */
  public NSArray<EOVActivites> activitesFoundList;
  public EOVActivites activiteFoundItem;
  public boolean errNoResults; 
  
  // recherche
  
  /**
   * Lancer la recherche
   */
  public WOComponent searchActivite() {
    // retraitement 
    resetSearchErrors();
    if (StringCtrl.isEmpty(strSearch)) {
    	strSearch = StringCtrl.emptyString();
    }
    strSearch = StringCtrl.replace(strSearch, "*", "");
    strSearch = StringCtrl.replace(strSearch, "\\", "");    
    strSearch = StringCtrl.replace(strSearch, "''", "");    
    if (StringCtrl.normalize(strSearch).length() >= MIN_SIZE_STRING_SEARCH) {
    	activitesFoundList = activiteBus().findVActivitesLike(
    			strSearch, 
    			getRootCStructure(), 
    			showHiddenActivite(),
    			showUnderscoredActivite());
    	// classement
    	activitesFoundList = EOVActivites.sort(activitesFoundList);
    	if (activitesFoundList.count()==0) {
    		errNoResults = true;
    	}
    } else {
      errSizeStrSearch = true;
      activitesFoundList = null;
    }
    doAfterSearchActivite();
    return null;
  }
  
  /**
   * Le traitement a faire suite a une recherche
   */
  public abstract void doAfterSearchActivite();
  
  /**
   * Cliquer sur le libelle d'une activite suite a la recherche : 
   * cela va selectionner la dans l'arborescence
   */
  public WOComponent selectActiviteFoundItem() {
    // trouver l'arbo des activites associee
    NSArray<EOVActivites> recsVActivites = activiteBus().findActivitePath(activiteFoundItem);

    // le dernier node selection
    activiteSelectedItem = findNode(activitesZeroItem().retrieveRootNodes(), (CktlRecord) recsVActivites.objectAtIndex(0));

    // le resultat peut être nul, par exemple s'il s'agit d'une activité underscore
    // qui n'apparait pas dans le HXBrowser
    if (activiteSelectedItem != null) {
     
    	// selection du premier node
      activiteSelectedPath = new NSMutableArray<ActivitesNode>();
      activiteSelectedPath.addObject(activiteSelectedItem);

      int currentIndex = 1;
      
      // selection des autres
      while (currentIndex < recsVActivites.count()) {
        CktlRecord recVActivites = (CktlRecord) recsVActivites.objectAtIndex(currentIndex);
        activiteSelectedItem = findNode(activiteSelectedItem.retrieveChildrenNodes(), recVActivites);
        //activiteSelectedItem = ActivitesNode.findNode(recVActivites);
        activiteSelectedPath.addObject(activiteSelectedItem);
        currentIndex++;
      }
      
      // reselection de la fin pour que l'interface soit mise a jour
      setActiviteSelectedItem(activiteSelectedItem);
      
      // raz de la resultats
      activitesFoundList = null;
      
    }
    
    return null;
  }
  
  /**
   * Selectionner une activite dans l'arbre, a partir de son numero <code>actOrdre</code>.
   * 
   * @param actOrdre
   */
  public void selectActiviteByActOrdre(Number actOrdre) {
  	activiteFoundItem = activiteBus().findVActivite(actOrdre);
  	selectActiviteFoundItem();
  }
  
  /**
   * Indique si la selection doit forcement etre une feuille
   */
  public abstract boolean shouldSelectedOnlyLeaf();
  
  /**
   * TODO voir pk ca ne marche pas avec ActiviteNode.findNode()
   * 
   * Methode interne permettant de retrouver le node associe a une activite
   * @param someNodes :  les nodes d'un niveau de l'arbo parmi lesquesl on cherche
   * @param recVActivite : l'activite en question
   */
  private ActivitesNode findNode(NSArray<ActivitesNode> someNodes, CktlRecord recVActivite) {
    NSArray<ActivitesNode> nodes = EOQualifier.filteredArrayWithQualifier(someNodes, 
        CktlDataBus.newCondition("actOrdre=%@", new NSArray(recVActivite.numberForKey("actOrdre"))));
    if (nodes.count() > 0) {
      return (ActivitesNode) nodes.lastObject();
    } else {
      return null;
    }
  }
  
  private void resetSearchErrors() {
    errSizeStrSearch = errNoResults = false;
  }

  // POUR L'UTILISATION DU COMPOSANT EN PAGE COMPLETE
  
  /**
   * La page de retour suite a la selection de l'activite. N'est
   * utilisee que si le composant est en page complete 
   * formName() = null
   */
  public abstract WOComponent caller();
  
  /**
   * Pour le cas particulier de l'appel du composant depuis une page
   * qui n'est pas une page complete mais un <code>WOComponentContent</code>,
   * alors on utilise cette methode pour definir la page complete
   * (ex: parent()).
   * Pour ne pas prendre en compte cette methode, il suffit de ne pas la 
   * surcharger, ou bien la faire retourner <em>null</em>.
   * @return
   */
  public WOComponent alternateCaller() {
  	return null;
  }
  
  // ajout de la possibilite de changer le service
  
  /** l'activite racine-service en cours */
  private CktlRecord changingRootVActivite;
  
  /**
   * La vraie activite racine. Tiens compte si cette derniere
   * est fixe (passee via {@link #recVActivite()} ou pas)
   */
  private CktlRecord getRecRootVActivite() {
  	if (isAllowedChangeService()) {
  		return changingRootVActivite;
  	} else {
  		return recVActivite();
  	}
  }

  /**
   * Le C_STRUCTURE associe a l'activite point d'entr�e
   */
  private String getRootCStructure() {
  	return getRecRootVActivite().stringForKey("cStructure");
  }
  
  /**
   * L'utilisateur est-il autoris� a changer le service
   * point d'entree des activites. 
   * OUI si la methode {@link #recVActivite()} retourne <em>null</em>.
   */
  public boolean isAllowedChangeService() {
  	return recVActivite() == null;
  }

  /**
   * L'affichage du libelle du service si celui ci est fixe ou modifiable
   */
  public String displayLibelleService() {
  	if (isAllowedChangeService()) {
  		// on se base sur l'item de la liste
  		return serviceItem.stringForKey("llStructure");
  	} else {
  		// on le deduit de l'activite point d'entree fixe
    	return recVActivite().stringForKeyPath("toGroupesDt.llStructure");
  	}
  }

  /**
   * Le nombre de lignes affichable en meme temps dans le CktlHXBrowser 
   * des activites
   */
  public int getNbLigBrowserAct() {
  	if (isAllowedChangeService()) {
  		// on se base sur l'item de la liste
  		return serviceItem.intForKeyPath("toGroupesDt.grpNbLigBrowserAct");
  	} else {
  		// on le deduit de l'activite point d'entree fixe
    	return recVActivite().intForKeyPath("toGroupesDt.grpNbLigBrowserAct");
  	}
  }

  
  /** le service point d'entree modifié */
  public NSArray serviceList;
  public CktlRecord serviceItem;
  public CktlRecord serviceSelected;
  
  /**
   * Surchage du setter afin de mettre a jour l'activite
   * service point d'entree du <code>CktlHXBrowser</code>
   */
  public void setServiceSelected(CktlRecord value) {
  	serviceSelected = value;
    NSArray activitesForService = activiteBus().fetchArray(EOVActivites.ENTITY_NAME, 
    		DTDataBus.newCondition("cStructure='"+serviceSelected.stringForKey("cStructure")+"'"), null);
    // selection de la premiere activite
    if (activitesForService.count() > 0) {
      changingRootVActivite = (CktlRecord) activitesForService.objectAtIndex(0);
    }
  }
  
  public CktlRecord getServiceSelected() {
  	return serviceSelected;
  }
  
  /**
   * Dans le cas ou le service est modifiable, on initialise 
   * la liste des service et on selectionne le premier.
   * A appele imperativement apres instanciation si on veut pouvoir
   * changer de service
   */
  protected void initServiceList() {
  	serviceList = (NSArray) serviceBus().allGroupesDt(true).valueForKey("toStructureUlr");
  	// selection du premier
  	if (!NSArrayCtrl.isEmpty(serviceList)) {
  		setServiceSelected((CktlRecord) serviceList.objectAtIndex(0));
  	}
  }
  /**
   * La liste des activités favorites de l'individu pour le service choisi
   * @return
   */
  public NSArray<EOVActivites> getActivitesFavoritesList() {
  	NSArray<EOVActivites> result = new NSArray<EOVActivites>();
  	
  	// on ne fait de proposition que si un service est renseigné
  	if (getRootCStructure() != null) {
  		NSArray<EOActivites> resultActivites = activiteBus().getActivitesFavorites(
  				new Integer(session().dtUserInfo().noIndividu().intValue()), 
  				getRootCStructure(),
  				showActivitesFavoritesDemandeur(),
  				showActivitesFavoritesIntervenant(),
  				showHiddenActivite(),
  				showUnderscoredActivite());
  		// transformer en objet EOVActivite
  		for (EOActivites activites : resultActivites) {
  			result = result.arrayByAddingObject(
  					activiteBus().findVActivite(activites.actOrdre()));
  		}
  		// classement
  		result = EOVActivites.sort(result);
  	}

  	
  	return result;
  }
  
  
  // raccourcis vers les bus de donnees
  
  private DTActiviteBus activiteBus() {
  	return session().dataCenter().activiteBus();
  }

  private DTServiceBus serviceBus() {
  	return session().dataCenter().serviceBus();
  }


  
  
  // ajax auto complete
  
  /** la chaine de recherche */
  public String aacActiviteValue;

  /** l'activite selectionee */
  public CktlRecord aacActiviteSelected;

  /** l'activite item */
  public CktlRecord aacActiviteItem;

  /**
   * Filtre au fur et a mesure les resultats avec ce qui est 
   * tapé dans {@link #aacActiviteValue}
   * @return
   */
  public NSArray getAacActiviteList() {
  	NSArray result = new NSArray();
  	
  	if (!StringCtrl.isEmpty(aacActiviteValue)) {

  		aacActiviteValue = StringCtrl.replace(aacActiviteValue, "*", "");
  		aacActiviteValue = StringCtrl.replace(aacActiviteValue, "\\", "");    
  		aacActiviteValue = StringCtrl.replace(aacActiviteValue, "''", "");    
      if (StringCtrl.normalize(aacActiviteValue).length() >= MIN_SIZE_STRING_SEARCH) {
      	result = activiteBus().findVActivitesLike(
      			aacActiviteValue, 
      			getRootCStructure(), 
      			showHiddenActivite(),
      			showUnderscoredActivite());
      }
      
  	}
  	
  	return result;
  }
  
  /**
   * Interception de la modification de l'activité selectionnée :
   * on autoselectionne dans l'arbre
   * @param value
   */
  public void setAacActiviteSelected(CktlRecord value) {
  	
  }
  
  
}
