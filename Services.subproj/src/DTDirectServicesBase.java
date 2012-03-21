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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;

import fr.univlr.cri.dt.services.common.DTServicesConst;
import fr.univlr.cri.dt.services.common.DTServicesRequestCoder;

/**
 * Propose des methodes pouvant etre reutilisees par differentes classes
 * implementant les actions directes pour les services Web. L'implementation
 * concrete des directes actions devrait heriter de cette classe.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDirectServicesBase extends WODirectAction {
  // Une instance de gestionnaire d'acces aux informations partagees
  private static DTSharedInfoBus sharedInfoBus;
  
  
  /**
   * Cree une nouvelle instance de l'objet. 
   */
  public DTDirectServicesBase(WORequest request) {
    super(request);
    // L'encodage a utiliser pour les valeurs des parametres HTTTP-POST
    // request.setDefaultFormValueEncoding("UTF-8");
  }

  /**
   * Retourne la reference vers l'application en cours d'execution.
   */
  public Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  /**
   * Retourne une reference vers le gestionnaire d'informations partagees.
   */
  private DTSharedInfoBus shaedInfoBus() {
    // On cree une instance en local. Sinon, il faudrait utiliser DTDataCenter,
    // et donc creer une session
    if (sharedInfoBus == null)
      sharedInfoBus = new DTSharedInfoBus(dtApp().dataBus().editingContext());
    return sharedInfoBus;
  }
  
//  /**
//   * Enregistre le contenu du dictionnaire dico dans une chaine de caracteres.
//   * Le contenu est enregistre sous format "properties". Chaque ligne est
//   * composee de paire "cle=valeur". Si une valeur est composee de plusieurs
//   * lignes alors le symbole "\" est ajoute a la fin de chaque ligne.
//   */
//  protected String resultToString(Hashtable dico) {
//    StringBuffer sb = new StringBuffer();
//    Enumeration keys = dico.keys();
//    String key;
//    while(keys.hasMoreElements()) {
//      key = (String)keys.nextElement();
//      sb.append(key).append("=");
//      // On encode le contenu de la chaine, si elle contient les symboles
//      // de passage a la ligne
//      sb.append(encodeResultValue(dico.get(key).toString())).append("\n");
//    }
//    return sb.toString();
//  }

//  /**
//   * Encode la valeur <code>value</code>. Remplace tous les "\n" par
//   * "&lt;&lt;nl&gt;&gt;".
//   */
//  private String encodeResultValue(String value) {
//    return URLCoder.encode(StringCtrl.replace(value, "\n", "<<nl>>"));
//  }
  
  /**
   * Retourne l'objet reponse avec la description de l'exception <code>ex</code>.
   */
  protected CktlDataResponse getResponseForException(Throwable ex) {
//    ex.printStackTrace();
    return getResponseForError(CktlLog.getMessageForException(ex));
  }
  
  /**
   * Retourne l'objet reponse avec le message d'erreur <code>errorMessage</code>.
   */
  protected CktlDataResponse getResponseForError(String errorMessage) {
    CktlDataResponse response = new CktlDataResponse();
    Hashtable resp = new Hashtable();
    resp.put(DTServicesConst.ErrorKey, errorMessage);
    response.setContent(DTServicesRequestCoder.encodeParams(resp));
    response.setHeader(CktlDataResponse.MIME_TXT , "Content-Type");
    CktlLog.log("Erreur : "+errorMessage);
    return response;
  }
  
  /**
   * Retourne les informations partagees correspondant au l'identifiant <code>uiTicket</code>.
   * Une fois retrouvees, les informations seront supprimees de la table partagee (pas de reutilisation multiple).
   * 
   * <p>Les informations pour la directe action sont retournees en format de dictionnaire.
   * Dans le cas d'une erreur, le dictionnaire contient le message d'erreur avec la cle
   * donnee dans la constante <code>DTServicesImpBase.ErrorKey</code>.</p>
   */
  protected Hashtable getInfoForTicket(String uiTicket) {
    String rawInfo = shaedInfoBus().getInfo(uiTicket, true);
    Properties dico = new Properties();
    if (StringCtrl.normalize(rawInfo).length() > 0) {
      rawInfo = StringCtrl.replace(rawInfo, "<<nl>>", "\n");
      try {
        dico.load(new ByteArrayInputStream(rawInfo.getBytes()));
      } catch (IOException ex) {
        ex.printStackTrace();
        dico.put(DTServicesConst.ErrorKey, CktlLog.getMessageForException(ex));
      }
    } else {
      dico.put(
          DTServicesConst.ErrorKey,
          "Les informations concernant l'utilisateur ne peuvent pas etre retrouvees (SharedInfo:"+uiTicket+")");
    }
    return dico;
  }
  
//  public Hashtable getRequestParams(String requestContent) {
//    Properties params = new Properties();
//    requestContent = StringCtrl.replace(requestContent, "&", "\n");
//    requestContent = URLCoder.decode(requestContent);
//    try {
//      params.load(new ByteArrayInputStream(requestContent.getBytes()));
//    } catch (IOException ex) {
//      // On s'en fout, il n'y aura pas d'erreurs la
//    }
//    return params;
//  }
}
