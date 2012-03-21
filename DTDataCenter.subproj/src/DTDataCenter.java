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
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.server.database._CktlBasicDataBus;

import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Centralise l'acces aux gestionnaires des differentes entites
 * de la base de donnees (les "dataBus").
 * 
 * <p>Cette classe donne egalement l'implementation des operations complexes
 * principales comme la creation, la supression, l'affectation des demandes...
 * Elles implique l'utilisations plusieurs "bus" d'acces a la base
 * de donnees ainsi que les gestionnaires d'autres types d'informations
 * comme envoi de mail et enregistrement de documents dans GEDFS.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDataCenter {
  /** La cache local des gestionnaire d'acces a la base de donnees. */
  private NSMutableDictionary dataBusCache = new NSMutableDictionary();
  
  /** La session en cours */
  protected Session dtSession;
  
  /** Le message d'erreur (s'il existe) */
  protected String errorMessage;
  
  /**
   * Cree une nouvelle instance de gestionnaire central pour la session donnee.
   */
  public DTDataCenter(Session session) {
    dtSession = session;
}

  /**
   * Retourne la reference vers l'application en cours d'execution.
   */
  private Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  /**
   * Indique si une erreur est survenue pendant l'appel de la derniere
   * operation. Retourne <em>null</em> si l'operation est execute sans erreurs.
   */
  public String errorMessage() {
    return errorMessage;
  }
  
  /**
   * Indique si une erreur est survenue pendant l'appel de la derniere
   * operation. Dans le cas d'une erreur, la methore <code>errorMessage</code>
   * retourne son message.
   * 
   * @see #errorMessage
   */
  public boolean hasError() {
    return (errorMessage != null);
  }

// ============= Gestion des BUS ==================
  
  /**
   * Cree et retourne une instance du gestionnaire avec le nom
   * <code>busName</code> (le nom de la classe). Si une nouvelle instance est
   * creee, elle est ajoutee dans le cache local des gestionnaires. Elle sera
   * ensuite reutilisee prochaine fois que l'acces au bus <code>busName</code>
   * sera demande.
   */
  private _CktlBasicDataBus getBusForName(String busName) {
    errorMessage = null;
    Object aBus = dataBusCache.objectForKey(busName);
    // Si le bus n'est pas dans le cache, on le cree
    if (aBus == null) {
      try {
        // On utilise la reflexion de Java
        Object arguments[] = {dtApp().dataBus().editingContext()};
        Class argumentTypes[] = {EOEditingContext.class};
        Class busClass = Class.forName(busName);
        aBus = busClass.getConstructor(argumentTypes).newInstance(arguments);
        // On memorise dans le cache
        dataBusCache.setObjectForKey(aBus, busName);
      } catch (Throwable e) {
        e.printStackTrace();
        errorMessage = CktlLog.getMessageForException(e);
      }
    }
    // On definit les objets cles pour le nouveau bus
    if (aBus instanceof DTDataBus) {
      ((DTDataBus)aBus).setDtSession(dtSession);
      ((DTDataBus)aBus).setDtDataCenter(this);
    }
    return (_CktlBasicDataBus)aBus;
  }
  
  /**
   * Retourne une instance du bus "generic" <code>DTDataBus</code>.
   */
  public DTDataBus dtDataBus() {
    return (DTDataBus)getBusForName("DTDataBus");
  }

  /**
   * Retourne une instance du bus de la gestion des etats des demandes
   * <code>DTEtatBus</code>.
   */
  public DTEtatBus etatBus() {
    return (DTEtatBus)getBusForName("DTEtatBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des informations sur les
   * individus <code>DTIndividuBus</code>.
   */
  public DTIndividuBus individuBus() {
    return (DTIndividuBus)getBusForName("DTIndividuBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des informations sur les
   * interventions <code>DTInterventionBus</code>.
   */
  public DTInterventionBus interventionBus() {
    return (DTInterventionBus)getBusForName("DTInterventionBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des informations sur les
   * services et les structures de l'annuaire <code>DTServiceBus</code>.
   */
  public DTServiceBus serviceBus() {
    return (DTServiceBus)getBusForName("DTServiceBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des activites des demandes
   * <code>DTActiviteBus</code>.
   */
  public DTActiviteBus activiteBus() {
    return (DTActiviteBus)getBusForName("DTActiviteBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des preferences de
   * l'utilisateur de l'application Demande de Travaux
   * <code>DTPreferencesBus</code>.
   */
  public DTPreferencesBus preferencesBus() {
    return (DTPreferencesBus)getBusForName("DTPreferencesBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des informations GEDI/Courrier
   * <code>DTCourrierBus</code>.
   * 
   * @deprecated utiliser le framework GediBus
   */
  public DTCourrierBus courrierBus() {
    return (DTCourrierBus)getBusForName("DTCourrierBus");
  }
  
  /**
   * Retourne une instance du bus de la gestion des informations sur les
   * contacts et la localisation des utilisateurs de l'application Demande de
   * Travaux <code>DTContactsBus</code>.
   */
  public DTContactsBus contactsBus() {
    return (DTContactsBus)getBusForName("DTContactsBus");
  }

  /**
   * Retourne une instance du bus de la gestion des informations
   * "financiere" <code>DTJefyBus</code>.
   *//*
  public DTJefyBusOld jefyBus() {
    return (DTJefyBusOld)getBusForName("DTJefyBus");
  }*/
  
  /**
   * Retourne une instance du bus de la gestion des informations
   * "financiere" <code>DTJefy2007Bus</code>.
   */
  public DTJefyBus jefyBus() {
    return (DTJefyBus)getBusForName("DTJefyBus");
  }

  /**
   * Retourne une instance du bus de la gestion des impressions
   * "financiere" <code>LRPrintBus</code>.
   */
  public LRPrintBus printBus() {
    return (LRPrintBus)getBusForName("LRPrintBus");
  }
  
  /**
   * Retourne le gestionnaire des informations generiques.
   */
  public DTGenericBus genericBus() {
    return (DTGenericBus)getBusForName("DTGenericBus");
  }

  /**
   * Retourne une instance du bus de la gestion des informations
   * partages - <code>DTSharedInfoBus</code>.
   */
  public DTSharedInfoBus sharedInfoBus() {
    return (DTSharedInfoBus)getBusForName("DTSharedInfoBus");
  }

//============= Operations complexes ==================

  /**
   * Supprimer la demande avec le code <code>intOrdre</code>. Verifie que
   * l'utilisateur <code>noIndividu</code> ait le droit d'effectuer la
   * suppression. Envoie un message email suite a la suppression.
   */
  public void performDelete(int intOrdre, int noIndividu) {
    Integer nIntOrdre = new Integer(intOrdre);
    // Tester si la DT exite
    CktlRecord rec = interventionBus().findIntervention(null, nIntOrdre);
    if (rec == null) {
      errorMessage = "Demande inconnue";
      return;
    }
    // Tester si la suppression est possible :
    //  - etat pas encore definitivement traite
    //  - droits suffisant pour la suppression
    //  - aucun traitement n'est encore enregistre
    CktlLog.trace("etat : "+rec.stringForKey("intEtat"));
    CktlLog.trace("has droits : "+dtSession.dtUserInfo().hasDroit(
        DTUserInfo.DROIT_INTERVENANT_SUPER, rec.stringForKey("cStructure")));
    CktlLog.trace("traitements count : "+interventionBus().findTraitements(null, null, nIntOrdre, null).count());
    if (DTEtatBus.isEtatFinal(rec.stringForKey("intEtat")) ||
        (!dtSession.dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER, 
            rec.stringForKey("cStructure"))) ||
        (interventionBus().findTraitements(null, null, nIntOrdre, null).count() > 0))
    {
      errorMessage = "Suppression non autorisee";
      return;
    }
    // Memoriser les infos pour le mail
    NSDictionary intInfo = rec.snapshot();
    NSArray noIntervenants = rec.arrayForKeyPath("tosIntervenant.noIndividu");
    // Supprimer les fichier attaches, s'ils existent
    if (!supprimerFichiersDT(rec.numberForKey("intOrdre"))) {
      errorMessage = "Les documents attaches n'ont pas pu etre supprimes";
      return;
    }
    // Suppression de la DT
    if (!interventionBus().deleteIntervention(null, nIntOrdre)) {
      errorMessage = "La demande n'a pas ete supprimee";
      return;
    }
    //   iBus().setUseOperationOrder(false);
    CktlLog.trace("transaction commited. Post notification...");
    CktlLog.trace("posted. Sending mail...");
    // Envoi du mail au service et au(x) demandeur(s)
    dtSession.mailCenter().reset();
    dtSession.mailCenter().setIntervention(intInfo, false);
//    DTMailCenter.sharedCenter().setInterventionFiles(intFiles);
    dtSession.mailCenter().setNoIntervenants(noIntervenants);
    // on informe systematiquement la personne concernée
    dtSession.mailCenter().mailSupprimerDT(true);
  }
  
  /**
   * Supprime les fichiers GEDFS associes a la demande <code>intOrdre</code>.
   */
  public boolean supprimerFichiersDT(Number intOrdre) {
    NSArray objects = interventionBus().findDocuments(intOrdre, null, true);
    for(int i = 0; i < objects.count(); i++) {
      if (!dtSession.gedBus().deleteDocumentGED(
          CktlRecord.recordNumberForKey(
              objects.objectAtIndex(i), "docOrdre").intValue()))
        return false;
    }
    return true;
  }
}
