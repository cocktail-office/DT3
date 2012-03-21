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
import java.util.Hashtable;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlUserInfoDB;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;

import fr.univlr.cri.dt.services.common.DTServicesConst;


/**
 * Implementation des services pour les directe actions et Web Services.
 * 
 * <p>Cette classe propose les methodes utilisees dans l'implementation
 * d'acces aux differents services proposes ou utilises par la Demande
 * de Travaux.</p>
 * 
 * @see DTWebServices
 * @see DTDirectLocalServices
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTServicesImpBase {
  /** L'instance de la session creee pour effectuer les operations avancees */  
  private Session dtSession;
  /** Le context en cours */
  private WOContext context;
  /** Le resultat d'execution d'un service */
  protected Hashtable sResults;
  /** L'identifiant du service */
  private String serviceKey;
  /** Indique si les logs doivent etre affiches lors des appels aux methodes */
  private boolean showLogs;
 
  /**
   * Cree une nouvelle instance de gestionnaire des service de la DT.
   */
  public DTServicesImpBase() {
    sResults = new Hashtable();
    showLogs = true;
  }
  
  /**
   * Retourne la reference vers l'application en cours d'execution.
   */
  protected Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  /**
   * Retourne la reference vers la session. Une nouvelle instance de la session
   * est creee si cette methode est appellee pour la premiere fois. 
   */
  protected Session dtSession() {
    if (dtSession == null) {
      openSession(null);
    }
    return dtSession;
  }

  /**
   * Definit l'identifiant du service.
   */
  protected void setServiceKey(String key) {
    serviceKey = key;
  }
  
  /**
   * Retourne l'identifiant de service.
   */
  protected String serviceKey() {
    return serviceKey;
  }

  /**
   * Cree une nouvelle instance de la session qui peut etre utilisee par
   * les methodes des services Web. Si le parametre <code>noIndividu</code>
   * n'est pas <em>null</em>, la session est ouverte au nom de l'individu
   * avec le code donne (les informations de connexion a la session).
   */
  protected boolean openSession(Number noIndividu) {
    dtSession = new Session();
    dtSession._setContext(context);
//  dtSession._awakeInContext(WOWebServiceUtilities.currentWOContext());
//    dtSession = (Session)WOWebServiceUtilities.currentWOContext().session();
    if (noIndividu != null) {
      CktlUserInfoDB ui = new CktlUserInfoDB(dtApp().dataBus());
      ui.individuForNoIndividu(noIndividu, true);
      if (ui.hasError()) {
        setError(ui.errorMessage());
      } else {
        dtSession.setConnectedUserInfo(ui);
      }
    } else {
      setError("Utilisateur inconnu");
    }
    return !hasError();
  }
  
  /**
   * Termine et clore la session actuellement ouverte.
   */
  protected void closeSession() {
    if (dtSession != null) {
      dtSession.terminate();
      dtSession = null;
    }
  }

  /**
   * Retourne la reference vers le gestionnaire des "dataBus". Cette methode
   * retourne le gestionnaire de la session en cours.
   */
  protected DTDataCenter dtDataCenter() {
    return dtSession().dataCenter();
  }

  /**
   * Definit le context en cours pour le service.
   */
  public void setContext(WOContext context) {
    this.context = context;
  }

  /**
   * Enregistre le message d'erreur <code>message</code> dans le dictionnaire
   * <code>dico</code>. Toutes les autres informations sont supprimees du
   * dictionnaire si elles y etaient presentes. Le message est enregistre
   * avec la cle <em>error</em>.
   */
  protected Hashtable setError(String message) {
    sResults.put(DTServicesConst.ErrorKey, message);
    return sResults;
  }
  
//  /**
//   * Retourne le message d'erreur survenue suite a la derniere operation.
//   * Retourne <em>null</em> si l'operation est executee sans erreurs.
//   */
//  private String getError() {
//    return (String)sResults.get(DTServicesConst.ErrorKey);
//  }

  /**
   * Test si le resultat contient une erreur (une entree avec la clee
   * <em>error</em>).
   */
  protected boolean hasError() {
    return (sResults.get(DTServicesConst.ErrorKey) != null);
  }

  /**
   * Definit si les logs doivent etre affiches lors de l'appel aux methodes
   * d'acces aux services Web. Par defaut, les logs sont affiches.
   */
  public void setShowLogs(boolean flag) {
    showLogs = flag;
  }
  
//  /**
//   * Teste si les messages logs doivent etre affiches lors de l'acces au
//   * service Web.
//   */
//  public boolean showLogs() {
//    return showLogs;
//  }
  
  /**
   * Affiche le log du debut d'execution d'un service.
   */
  protected void logStart(
      String appId, int userId, String userIdType, String operation) {
    if (!showLogs) return;
    StringBuffer uid = new StringBuffer();
    uid.append("(").append(userIdType).append(")").append(userId);
    logStart(appId, uid.toString(), operation);
  }
    
  /**
   * @deprecated
   * 
   * Affiche le log du debut d'execution d'un service sous l'identit�
   * d'une autre personne en precisant la cible
   */
  protected void logStart(
      String appId, int userId, int fakeUserId, int targerId, 
      String userIdType, String fakeUserIdType, String targetIdType, String operation)
  {
    if (!showLogs) return;
    StringBuffer uid = new StringBuffer();
    // "(persId)3065"
    uid.append("(").append(userIdType).append(")").append(userId);
    // "(persId)2115"
    uid.append(" for [user : (").append(fakeUserIdType).append(")").append(fakeUserId);
    uid.append("] on (").append(targetIdType).append(")").append(targerId);
    logStart(appId, uid.toString(), operation);
  }
  
  /**
   * Affiche le log du debut d'execution d'un service sous l'identit�
   * d'une autre personne et sous son service en precisant la cible
   */
  protected void logStart(
      String appId, int userId, int fakeUserId, int fakeStructUserId, int targerId, 
      String userIdType, String fakeUserIdType, String structUserIdType, 
      String targetIdType, String operation)
  {
    if (!showLogs) return;
    StringBuffer uid = new StringBuffer();
    uid.append("(").append(userIdType).append(")").append(userId);
    uid.append(" for [user : (").append(fakeUserIdType).append(")").append(fakeUserId);
    uid.append(" , service : (").append(structUserIdType).append(")").append(fakeStructUserId);
    uid.append("] on (").append(targetIdType).append(")").append(targerId);
   logStart(appId, uid.toString(), operation);
  }
  
  /**
   * Affiche le log du debut d'execution d'un service sur 
   * une cible
   */
  protected void logStart(
      String appId, int userId, int targerId, 
      String userIdType, String targetIdType, String operation)
  {
    if (!showLogs) return;
    StringBuffer uid = new StringBuffer();
    uid.append("(").append(userIdType).append(")").append(userId);
    uid.append(" on (").append(targetIdType).append(")").append(targerId);
   logStart(appId, uid.toString(), operation);
  }

  /**
   * Affiche le log du debut d'execution d'un service.
   */
  protected void logStart(String appId, String userId, String operation) {
    if (!showLogs) return;
    StringBuffer log = new StringBuffer();
    log.append("<").append(serviceKey()).append(" / ");
    log.append(operation).append(" : #").append(this.hashCode());
    if (appId != null)
      log.append(", app : ").append(appId);
    log.append(", user : ").append(userId).append(" - Start>");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche le log de la fin d'execution d'un service.
   */
  protected void logEnd(String operation) {
    if (!showLogs) return;
    StringBuffer log = new StringBuffer();
    String errorMessage = (String)sResults.get(DTServicesConst.ErrorKey);
    log.append("<").append(serviceKey()).append(" / ");
    log.append(operation).append(" : #");
    log.append(this.hashCode()).append(" - ");
    if (errorMessage != null) {
      CktlLog.log(log.append("Error>").toString());
      CktlLog.rawLog(StringCtrl.quoteText(errorMessage, ">>  "));
    } else {
      CktlLog.log(log.append("OK>").toString());
    }
  }
}