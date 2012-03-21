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
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.appserver.WOApplication;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.foundation.NSArray;

/**
 * Gestionnaire des connexion a la base de donnees pour l'application
 * Demande de Travaux.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDataBus extends CktlDataBus {
  /** La session en cours, si elle existe. */
  private Session dtSession;
  
  /** Le gestionnaire des "dataBus" gerant cet objet. */
  private DTDataCenter dtDataCenter;
  
  /** Le message de la derniere erreur */
  private String errorMessage;
  
  /**
   * Indique si le cache des objets EOF doit etre utilise lors d'appel aux
   * methodes du bus.
   */
  private boolean useCache;

  /**
   * Cree une nouvelle instance de data bus.
   */
  public DTDataBus(EOEditingContext editingContext) {
    super(editingContext);
    useCache = false; // Forcer le reload de tous les objets.
 }

  /**
   * Retourne la reference vers la session en cours.
   */
  protected Session dtSession() {
    return dtSession;
  }

  /**
   * Definit la reference vers la session en cours.
   */
  protected void setDtSession(Session session) {
    this.dtSession = session;
  }
  
  /**
   * Retourne la reference vers le gestionnaire des "dataBus" permettant
   * d'acceder � cette instance de l'objet.
   */
  protected DTDataCenter dtDataCenter() {
    return dtDataCenter;
  }
  
  /**
   * Definit la reference vers le gestionnaire des "dataBus" qui permettra
   * acceder a cette instance de l'objet.
   */
  protected void setDtDataCenter(DTDataCenter dataCenter) {
    dtDataCenter = dataCenter;
  }
  
  /**
   * Retourne la reference vers l'application en cours.
   */
  protected Application dtApp() {
    return (Application)WOApplication.application();  
  }

  /**
   * Retourne la reference version les informations utilisateur actuellement
   * connecte.
   */
  protected DTUserInfo userInfo() {
    return dtSession.dtUserInfo();
  }

  /**
   * Indique si le cache des objets doit etre utilise lors des appels
   * aux methodes du bus. La valeur par defaut est <i>false</i>.   
   */
  public void setUseCache(boolean flag) {
    useCache = flag;
  }

  /**
   * Teste si le cache des objets doit etre utilise lors des appels
   * aux methodes du bus.   
   */
  public boolean useCache() {
    return useCache;
  }

  /**
   * Retourne le dernier message d'erreur ou <i>null</i> si aucun message.
   */
  public String getErrorMessage() {
    return errorMessage;
  }
 
  /**
   * Definit un message d'erreur.
   */
  protected void setErrorMessage(String message) {
  	this.errorMessage = message;
  }
  
  /**
   * Teste si la derniere operation avec la base de donnees etait executee
   * avec les erreurs.
   */
  public boolean hasError() {
    return (errorMessage != null);
  }
  
  /**
   * Test si la longeur de la valeur saisie ne depasse pas la valeur
   * maximale autorisee.
   * 
   * @param tableName Le nom de la table, dont l'attribut sera modifie.
   * @param attributeName Le nom de l'attribut.
   * @param attributeValue La valeur de l'attribut a enregistrer dans la base.
   * @param libelle Le libelle de champs de l'interface dans lequel la valeur
   *   etait saisie.
   * @param makeMessage Indique s'il faut generer un message d'avertissement.
   * @param isHtml TODO
   */
  public boolean checkForMaxSize(String tableName,
                                 String attributeName,
                                 String attributeValue,
                                 String libelle,
                                 int reserverdLen,
                                 boolean makeMessage, boolean isHtml)
  {
    int maxLen = CktlRecord.maxLengthForAttribute(tableName, attributeName);
    int realLen = (attributeValue==null)?0:attributeValue.length();
    setErrorMessage(null);
    if (realLen > (maxLen-reserverdLen)) {
      if (makeMessage) {
      	StringBuffer sb = new StringBuffer();
      	String eaigu = "é";
      	String saut = "\\n";
      	if (isHtml) {
      		eaigu = "&eacute;";
      		saut = "<br>";
      	}
      	sb.append("La longueur de texte saisi dans le champ \"").append(libelle);
        sb.append("\""+saut+"d"+eaigu+"passe la longeur maximale autoris"+eaigu+"e.");
        sb.append(""+saut+saut+"Longueur maximale : ").append(maxLen-reserverdLen);
        sb.append(saut+"Longueur saisie : ").append(realLen);
        setErrorMessage(sb.toString());
      }
      return false;
    }
    return true;
  }

  /**
   * Rafraichie l'objet <code>rec</code> par rapport aux informations dans
   * la base de donnees.
   */
  public static void invalidateObject(EOEnterpriseObject rec) {
    if (rec != null) {
      EOEditingContext context = rec.editingContext();
      context.invalidateObjectsWithGlobalIDs(
          new NSArray(context.globalIDForObject(rec)));
    }
  }

  /**
   * Test si la valeur donnee represente une valeur vide. C'est le cas
   * si <code>value</code> est <i>null</i>, est une instance de
   * <code>EONullValue</code> et est une chaine de la longeur vide.
   */
  public static boolean isEmptyValue(Object value) {
    if (value == null) return true;
    if (value instanceof String)
      return (StringCtrl.normalize((String)value).length() == 0);
    return value.equals(CktlDataBus.nullValue());
  }

/* ======= Les methodes d'acces aux autres bus ======= */
  
  /**
   * Retourne la referencer vers une instance du gestionnaire d'acces aux
   * informations sur les individus - <code>DTIndividuBus</code>.
   */
  protected DTIndividuBus individuBus() {
    return dtDataCenter().individuBus();
  }
  
  /**
   * Retourne la referencer vers une instance de gestionnaire d'acces aux
   * informations sur les services - <code>DTServiceBus</code>.
   */
  protected DTServiceBus serviceBus() {
    return dtDataCenter().serviceBus();
  }
  
  /**
   * Retourne la referencer vers une instance de gestionnaire d'acces aux
   * informations sur les interventions - <code>DTInterventionBus</code>.
   */
  protected DTInterventionBus interventionBus() {
    return dtDataCenter().interventionBus();
  }
  
  /**
   * Retourne une reference vers le gestionnaire des preferences de
   * de l'utilisateur - <code>DTPreferencesBus</code>.
   */
  protected DTPreferencesBus preferencesBus() {
    return dtDataCenter().preferencesBus();
  }

  /**
   * Retourne une reference vers le gestionnaire des informations de
   * Jefyco - <code>DTJeyBus</code>.
   */
  protected DTJefyBus jefyBus() {
    return dtDataCenter().jefyBus();
  }

  /**
   * Retourne une reference vers le gestionnaire des informations partagees -
   * <code>DTSharedInfoBus</code>.
   */
  protected DTSharedInfoBus sharedInfoBus() {
    return dtDataCenter().sharedInfoBus();
  }
  

  /**
   * Retourne une reference vers le gestionnaire des activites -
   * <code>DTActiviteBus</code>.
   */
  protected DTActiviteBus activiteBus() {
    return dtDataCenter().activiteBus();
  }
}
