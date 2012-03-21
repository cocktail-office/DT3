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
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOWebServiceUtilities;

import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;
import fr.univlr.cri.dt.services.common.DTServicesConst;
import fr.univlr.cri.dt.services.common.DTServicesRequestCoder;

/**
 * Definit une interface d'acces aux services Web de la gestion des
 * prestations. L'acces ce fait via les directes actions. Les donnees
 * sont transferes via le requets HTTP-POST et la reponse est renvoye en
 * format Properties (couples "cle=valeur"). Le client de la directe action
 * dispose des methodes permettant de convertir le resultat en un dictionnaire
 * (Hashtable). 
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDirectPrestaServices extends DTDirectServicesBase {
  DTPrestaServicesImp servicePerformer;
  
  /**
   * Cree une nouvelle instance de l'objet. 
   */
  public DTDirectPrestaServices(WORequest request) {
    super(request);
  }
  
  /**
   * Retourne l'instance de l'objet qui realise les actions du service. 
   */
  private DTPrestaServicesImp servicePerformer() {
    if (servicePerformer == null) {
      servicePerformer =
        new DTPrestaServicesImp(
            dtApp().config().stringForKey("APP_PRESTA_WS_URL"),
            dtApp().config().stringForKey("APP_PRESTA_WS_PASS"));
      servicePerformer.setContext(WOWebServiceUtilities.currentWOContext());
    }
    return servicePerformer;
  }
  
//  /*
//   * Affiche le contenu d'une requette HTTP.
//   */
//  private void _tmpShowRequest(WORequest request) {
//    NSArray keys = request.formValueKeys();
//    CktlLog.rawTrace("-- Request form values --");
//    for(int i=0; i<keys.count(); i++) {
//      CktlLog.rawTrace(keys.objectAtIndex(i)+"="+request.formValueForKey((String)keys.objectAtIndex(i)));
//    }
//    CktlLog.rawTrace("-- -- -- -- -- -- --");
//  }
 
  /**
   * Extrait les informations sur l'utilisateur des parametres d'une requete
   * HTTP et de la base de donnees et les retourne dans un dictionnaire. Dans le
   * cas d'une erreur, le dictionnaire contient le message d'erreur avec la cle
   * <code>DTPrestaServiceConst.FormUITicketKey</code>.
   * 
   * @param reqParams
   *        Les parametres passes dans une requete HTTP.
   */
  private Hashtable getUserInfo(DTServicesRequestParams params) {
    Hashtable repDico;
    // Recupere les parametres de l'action (valeurs de formulaire)
    String uiTicket = params.getString(DTPrestaServicesConst.FormUITicketKey);
    if (uiTicket == null) {
      repDico = new Hashtable();
      repDico.put(
          DTServicesConst.ErrorKey,
          "La cle d'acces aux informations d'utilisateur n'est pas disponible (\""+
          DTPrestaServicesConst.FormUITicketKey+"\")"
          );
    } else {
      repDico = getInfoForTicket(uiTicket);
      CktlLog.trace("ticket : "+uiTicket);
      CktlLog.trace("info : "+repDico);
      if ((repDico.get("persId") == null) ||
          (repDico.get("structPersId") == null))
        repDico.put(
            DTServicesConst.ErrorKey,
            "Informations d'utilisateurs incompletes (persId, structPersId)"
            );
    }
    return repDico;
  }
  
  /**
   * Traite la requete HTTP correspondant a la creation d'un devis.
   */
  public WOActionResults createDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
//    _tmpShowRequest(req);
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Object value;
      Hashtable artDef;
      //
      Integer typeDevis =
        Integer.valueOf(params.getString(DTPrestaServicesConst.FormTypeDevisKey));
      //
      String devisLibelle =
        params.getString(DTPrestaServicesConst.FormDevisLibelleKey);
      //
      String prestaComment =
        params.getString(DTPrestaServicesConst.FormPrestaCommentKey);
      //
      value = params.getObject(DTPrestaServicesConst.FormOrgIdKey);
      Integer orgId = (value == null)?null:Integer.valueOf(value.toString());
      //
      String tcdCode = params.getString(DTPrestaServicesConst.FormTdcCodeKey);
      //
      value = params.getObject(DTPrestaServicesConst.FormLolfIdKey);
      Integer lolfId = (value == null)?null:Integer.valueOf(value.toString());
      //
      value = params.getObject(DTPrestaServicesConst.FormPcoNumKey);
      String pcoNum = (value == null)?null:value.toString();
      //
      int nbLignes = Integer.valueOf(
          params.getString(DTPrestaServicesConst.FormNbLignesKey)).intValue();
      Vector lignes = new Vector(nbLignes);
      for(int i=0; i<nbLignes; i++) {
        artDef = new Hashtable();
        artDef.put(DTPrestaServicesConst.FormCaarIdKey,
            params.getInteger(DTPrestaServicesConst.FormCaarIdKey+i));
        artDef.put(DTPrestaServicesConst.FormNbArticlesKey,
            params.getInteger(DTPrestaServicesConst.FormNbArticlesKey+i));
        value =
          params.getObject(DTPrestaServicesConst.FormTypeArticleKey+i);
        if (value != null)
          artDef.put(DTPrestaServicesConst.FormTypeArticleKey, value);
        //
        lignes.add(artDef);
      }
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().createDevis(
          typeDevis, devisLibelle, prestaComment, orgId, tcdCode,
          lolfId, pcoNum, lignes);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande des informations
   * sur un devis.
   */
  public WOActionResults inspectDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer prestId =
        params.getInteger(DTPrestaServicesConst.FormPrestIdKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().inspectDevis(prestId,
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structPersId")));
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la somme TTC
   * d'un devis.
   */
  public WOActionResults prestaRealiseeAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer prestId =
        params.getInteger(DTPrestaServicesConst.FormPrestIdKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().prestaRealisee(prestId);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de calculer le
   * montant TTC possible pour le devis.
   */
  public WOActionResults estimeTTCDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    CktlLog.trace("\n---Request.content() ----:\n"+context().request().contentString()+"\n--------");
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      //
      int nbLignes = params.getInt(DTPrestaServicesConst.FormNbLignesKey);
      Vector articles = new Vector(nbLignes);
      Vector nbArticles = new Vector(nbLignes);
      for(int i=0; i<nbLignes; i++) {
        articles.add(
            params.getInteger(DTPrestaServicesConst.FormCaarIdKey+i));
        nbArticles.add(
            params.getInteger(DTPrestaServicesConst.FormNbArticlesKey+i));
      }
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result =
        servicePerformer().estimeTTCDevis(articles, nbArticles);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la somme TTC
   * d'un devis.
   */
  public WOActionResults sommeTTCDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer prestId =
        params.getInteger(DTPrestaServicesConst.FormPrestIdKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().sommeTTCDevis(prestId);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la somme sur une
   * ligne budgetaire.
   */
  public WOActionResults sommeLigneBudAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer orgId = 
        params.getInteger(DTPrestaServicesConst.FormOrgIdKey);
      String tcdCode =
        params.getString(DTPrestaServicesConst.FormTdcCodeKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().sommeLigneBud(orgId, tcdCode);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande d'archivage d'un devis.
   */
  public WOActionResults deleteDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer prestId =
        params.getInteger(DTPrestaServicesConst.FormPrestIdKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().deleteDevis(prestId);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la somme sur une
   * ligne budgetaire.
   */
  public WOActionResults checkWebServiceAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().checkWebService();
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
      // On va definir le type de la reponse "text" pour pouvoir la consulter
      // via Web classique (par exemple, avec un navigateur Web).
      response.setHeader(CktlDataResponse.MIME_TXT , "Content-Type");
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la liste des etats
   * possibles des devis.
   */
  public WOActionResults allDevisEtatsAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().allDevisEtats();
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }

  /**
   * Traite la requete HTTP correspondant a la demande de la somme TTC
   * d'un devis.
   */
  public WOActionResults etatForDevisAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      //
      Integer prestId =
        params.getInteger(DTPrestaServicesConst.FormPrestIdKey);
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().etatForDevis(prestId);
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }
  
  /**
   * Traite la requete HTTP correspondant a la demande de la somme TTC
   * d'un devis.
   */
  public WOActionResults typcredAutorisesAction() {
    CktlDataResponse response;
    DTServicesRequestParams params =
      new DTServicesRequestParams(context().request());
    try {
      // Recupere les infos sur l'utilisateur
      Hashtable uInfo = getUserInfo(params);
      if (uInfo.get(DTServicesConst.ErrorKey) != null)
        return getResponseForError((String)uInfo.get(DTServicesConst.ErrorKey));
      // Appeler le service
      servicePerformer().setUser(
          Integer.valueOf((String)uInfo.get("persId")),
          Integer.valueOf((String)uInfo.get("structFouOrdre")));
      Hashtable result = servicePerformer().typcredAutorises();
      // Generer la reponse en fonction du resultat
      response = new CktlDataResponse();
      response.setContent(DTServicesRequestCoder.encodeParams(result));
    } catch(Throwable ex) {
      response = getResponseForException(ex);
    }
    return response;
  }
}
