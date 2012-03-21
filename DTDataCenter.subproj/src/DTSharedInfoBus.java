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
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.RandomKeyGenerator;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * Gere l'acces aux informations "partages" entre plusieurs versions et types
 * des applications DT.
 * 
 * <p>Les methodes de cette classe peuvent etre utilisees pour echanger les
 * informations entre les applications DT Windows et DT Web, par exemple.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTSharedInfoBus extends DTDataBus {
  /** Onjet utilise pour synchroniser l'acces a la base de donnees. */
  private static final Object sync = new Object();
  
  /**
   * Cree et initialise une instance de gestionnaire.
   */
  public DTSharedInfoBus(EOEditingContext context) {
    super(context);
  }
  
  /**
   * Enregistre les infos sur l'utilisateur actuellement connecte a
   * l'application dans la table des informations partagees. Cette information
   * possede un ticket unique qui peut ensuite etre rejouee par une autre
   * application pour acceder a ces informations.
   *
   * <p>Le ticket correspondant a ces informations est retourne. La methode
   * retourne <i>null</i> dans le cas d'une erreur.</p> 
   */
  public String storeConnectedUserInfo() {
    return storeUserInfo(userInfo().persId(), userInfo().codeService());
  }
  
  /**
   * Enregistre les infos sur l'utilisateur <code>persId</code> appartenant au
   * service avec le code <code>cStructure</code> dans la table des informations
   * partagees. Cette information possede un ticket unique qui peut ensuite etre
   * rejouee par une autre application pour acceder a ces informations.
   *
   * <p>Le ticket correspondant a ces informations est retourne. La methode
   * retourne <i>null</i> dans le cas d'une erreur.</p> 
   */
  public String storeUserInfo(Number persId, String cStructure) {
    CktlLog.trace("persId : "+persId+", cStructure : "+cStructure);
    int structPersId = -1;
    CktlRecord rec = serviceBus().structureForCode(cStructure);
    if (rec != null) structPersId = rec.intForKey("persId");
    StringBuffer info = new StringBuffer();
    info.append("persId=").append(persId);
    info.append("\nstructPersId=").append(structPersId).append("\n");
    return storeInfo(info.toString());
  }
  
  /**
   * Enregistre les infos <code>info</code> dans la table des informations
   * partagees de l'application DT. Retourne la cle unique asssocie a cette
   * information. Retourne <i>null</i> dans le cas ou le depot d'informations
   * echoue.
   */
  public String storeInfo(String info) {
    String key = null;
    synchronized (sync) {
      try {
        key = RandomKeyGenerator.getNewKey(20);
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO SHARED_INFO (KEY, INFO) ");
        sql.append("VALUES ('").append(key);
        sql.append("', '").append(info).append("')");
        executeSQLQuery(sql.toString(), null);
      } catch(Exception ex) {
        ex.printStackTrace();
        key = null;
      }
    }
    return key;
  }
  
  /**
   * Supprime de la base de donnees les "informations partagees" avec la cle
   * <code>key</code>. Retourne <code>false</code> dans le cas d'une erreur
   * de communication avec la base de donnees. 
   */
  public boolean removeInfo(String key) {
    boolean result = false;
    synchronized (sync) {
      try {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM SHARED_INFO WHERE KEY='").append(key).append("'");
        executeSQLQuery(sql.toString(), null);
        result = true;
      } catch(Throwable ex) {
        ex.printStackTrace();
      }
    }
    return result;
  }

  /**
   * Retourne les infos enregistrees dans une table partagee. Les informations
   * correspondent a la cle <code>key</code>. Si le parametre
   * <code>clean</code> est <em>true</em>, l'information sera supprimes et
   * ne pourra plus etre reutilisee.
   * 
   * <p>
   * Retourne <em>null</em> si aucune information n'est enregistree avec la
   * clee donnee.
   * </p>
   * 
   * <p>
   * Cette methode peute etre utilisee pour partager les informations entre
   * differentes versions et instances de l'application (Windows, HTML,
   * JavaClient), et notamment, les informations confidentielles.
   * </p>
   */
  public String getInfo(String key, boolean clean) {
    String info = null;
    synchronized (sync) {
      NSArray dbList =
        DTCktlEOUtilities.rawRowsForSQL(editingContext(), dtApp().mainModelName(),
            "SELECT INFO FROM SHARED_INFO WHERE KEY = '"+key+"'",
            new NSArray("info"));
      if (dbList.count() > 0)
        info = (String)((NSDictionary)dbList.objectAtIndex(0)).valueForKey("info");
      if (clean) removeInfo(key);
    }
    return info;
  }
}
