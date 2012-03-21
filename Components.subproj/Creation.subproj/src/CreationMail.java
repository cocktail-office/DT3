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
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlDefaultPage;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gestionnaire de la page permettant de creer les demandes par email. Cette
 * page affiche la liste des activites avec les adresses email associees.
 * L'utilisateur pourra envoyer un message email a une adresse choisie et
 * ainsi enregistrer une nouvelle demande. Le message est envoye en utilisant
 * le client email par defaut de l'utilisateur. Son identite est determinee
 * d'apres l'adresse email "from".
 *
 * <p>La fonctionnalite de creation des messages par email utilise les serveur
 * SAM et son plugin SAM-DT.</p>
 *
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class CreationMail extends CktlDefaultPage {
//  /* La definition de tri des services */
//  private static NSArray ServiceSort;
//  /* La definition de tri des activites */
//  private static NSArray ActiviteSort;

  /** La liste de tous les services. Ce sont les entites GroupesDt */
  public NSMutableArray services;
  /** @TypeInfo GroupesDt */
  public CktlRecord unService;
  /** @TypeInfo Activites */
  public CktlRecord uneActivite;
  /** Association entre les services et les acitivites
   * (cStrcuture, NSArray de Activites) */
  private NSMutableDictionary serviceActs;
  /** Le editing context utilise pour fecther tous les elements */
  private EOEditingContext ec = new EOEditingContext();
  /** Gestionnaire d'acces a la base de donnees des activites */
  private DTActiviteBus activitesBus;
  
  /**
   * Cree une nouvelle instance de la page.
   */
  public CreationMail(WOContext context) {
    super(context);
    services = new NSMutableArray();
    serviceActs = new NSMutableDictionary();
    initPage();
  }

  /**
   * Retourne la reference vers l'application en cours de l'execution.
   */
  private Application dtApp() {
    return (Application)cktlApp;
  }

//  /**
//   * Retourne la definition de tri pour les services.
//   */
//  private NSArray serviceSort() {
//    if (ServiceSort == null) ServiceSort = CktlSort.newSort("llStructure");
//    return ServiceSort;
//  }

//  /**
//   * Retourne la definition de tri pour les activites.
//   */
//  private NSArray activiteSort() {
//    if (ActiviteSort == null) ActiviteSort = CktlSort.newSort("actLibelle");
//    return ActiviteSort;
//  }

//  /**
//   * Effectue le "fetch" d'objets dans la base de donnees dans l'editing context
//   * de la page.
//   *
//   * @see Application#dataBus()
//   */
//  private NSArray fetchArray(String tableName, String conditionStr, NSArray sort) {
//    return dtApp().dataBus().fetchArray(
//        ec, tableName, CktlDataBus.newCondition(conditionStr), sort);
//  }

  /**
   * Retourne le gestionnaire d'acces a la base de donnees des activites.
   */
  private DTActiviteBus activitesBus() {
    if (activitesBus == null)
      activitesBus = new DTActiviteBus(ec);
    return activitesBus;
  }
  
  /**
   * Retourne le URL a utiliser dans le lien "<em>dans le cas de probleme,
   * contactez...</em>".
   */
  public String lienPbConnexion() {
    return cktlApp.contactMail();
  }

  /**
   * Initialise les listes des services, activites et adresses email.
   * Cette methode doit etre appellee avant de retourner cette page.
   */
  public void initPage() {
    NSArray objServices, objActivites;
    NSMutableArray activites;
    // On retrouve d'abord les services "visibles"
//    objServices = fetchArray("GroupesDt", "grpAffichable='O'", serviceSort());
    objServices = activitesBus().allValideGroupes();
    objServices = CktlSort.sortedArray(objServices, "llStructure");
    for(int i=0; i<objServices.count(); i++) {
      activites = new NSMutableArray();
      unService = (CktlRecord)objServices.objectAtIndex(i);
      // Pour chaque service, on recupere les activites avec les emails
//      objActivites = fetchArray(
//            "Activites",
//            "(actCreerMail<>nil) and (cStructure='"+
//              unService.valueForKey("cStructure")+"') and (actAffichable ='O')",
//            activiteSort());
      objActivites = activitesBus().findAllActivites(true, true, true, 
            unService.stringForKey("cStructure"));
      objActivites = CktlSort.sortedArray(objActivites, "actLibelle");
      // Pour chaque acrtivite, on verifie, si on peut l'utiliser
      for(int j=0; j<objActivites.count(); j++) {
//        uneActivite = (CktlRecord)objActivites.objectAtIndex(j);
//        // [LRAppTasks] : @CktlLog.trace(@"Une activite : ordre : "+uneActivite.valueForKey("actOrdre")+", email : "+uneActivite.valueForKey("actCreerMail"));
//        // [LRAppTasks] : @CktlLog.trace(@"Is Affichable : "+canUseActivite(uneActivite));
        // On memorise les activites qui sont utilisables
        // if (canUseActivite(uneActivite)) activites.addObject(uneActivite);
        activites.addObject(objActivites.objectAtIndex(j));
      }
      // Si au moins une activite est disponible, on utilisera
      // le service correspondant
      if (activites.count() > 0) {
        services.addObject(unService);
        serviceActs.takeValueForKey(
            activites, unService.stringForKey("cStructure"));
      }
    }
  }

//  private boolean canUseActivite(CktlRecord recActivite) {
//    // Tester si l'activite est bien une activite feuille...
//    if (!activitesBus().checkIsLeaf(recActivite)) return false;
//    // Tester si l'activite ou un de ces peres n'est pas cachee
//    return activitesBus().checkIsVisible(recActivite);
//  }

  /**
   * Retourne le chemin de l'activite <code>recActivite</code>. Il inclu les
   * activites peres, separes par le separateur <code>separator</code>.
   */
  private String getPathForActivite(CktlRecord recActivite, String separator) {
    // On s'arrete a la racine
    if ((recActivite == null) || (recActivite.intForKey("actOrdre") == 0))
      return StringCtrl.emptyString();
    String path = getPathForActivite(recActivite.recForKeyPath("toActPere"), separator);
    if (path.length() > 0) path = path + separator;
    path = path + recActivite.stringForKey("actLibelle");
    return path;
  }
  
  /**
   * Teste si la liste des activites n'est pas vide.
   */
  public boolean hasActivitesMail() {
    return ((services != null) && (services.count() > 0));
  }
  
  /**
   * Retourne la liste des activites pour le service en cours. Retourne
   * une liste vide si aucune activite n'est associ� au service.
   */
  public NSArray activitesForService() {
    if (unService != null) {
      NSArray activites =
        (NSArray)serviceActs.valueForKey(unService.stringForKey("cStructure"));
      if (activites != null)
        return activites;
    }
    return new NSArray();
  }
  
  /**
   * Retourne le libelle qui sera affiche pour le lien d'envoie de email
   * d'une activite.
   */
  public String activitesMailLibelle() {
    // On retourne le chemin d'acces a l'activite, en commencant par la racine
    String libelle = StringCtrl.emptyString();
    if (uneActivite != null) libelle = getPathForActivite(uneActivite, " &gt; ");
    if (libelle.length() == 0) libelle = "Activite inconnu";
    return libelle;
  }
  
  /**
   * Retourne l'adresse email correspondant a l'activite en cours.
   */
  public String activiteMailRef() {
    if (uneActivite == null)
      return "adresse-email-inconnue";
    else
      return uneActivite.stringForKey("actCreerMail")+"?Subject=Demande de travaux par email";
  }
  
  /**
   * Retourne le URL d'acces a la page d'accueil de l'application.
   */
  public String urlRetourAccueil() {
    return dtApp().getApplicationURL(context());
  }
}