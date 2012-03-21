/*
 * Copyright CRI - Universite de La Rochelle, 1995-2004 
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
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

/**
 * Permet de generer et afficher les messages dans les "log" de l'application.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTLogger {

  /**
   * Affiche dans les logs de l'application un message suite a la creation d'une
   * demande.
   * 
   * @param intCleService
   *        L'identifiant de la demande creee parmis toutes les demandes du
   *        service.
   * @param intOrdre
   *        L'identifiant (interne) de la demande creee.
   * @param serviceStruct
   *        Le code de la structure representant le service destinataire dans
   *        l'annuaire de l'etablissement.
   * @param userLogin
   *        Le login de l'utilisateur qui a cree la demande.
   * @param userId
   *        L'identifiant de l'utilisateur qui a cree la demande.
   */
  public static void logCreation(Object intCleService, Object intOrdre, 
                                 Object serviceStruct,
                                 Object userLogin, Object userId)
  {
    StringBuffer log = new StringBuffer();
    log.append("creation demande : #").append(intCleService).append("/").append(intOrdre);
    log.append(", service dest. : #").append(serviceStruct);
    log.append(", user : ").append(userLogin).append("/").append(userId);
    log.append(" - OK");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche dans les logs de l'application un message suite a l'enregistrement
   * d'un message
   * 
   * @param intCleService
   *        L'identifiant de la demande creee parmis toutes les demandes du
   *        service.
   * @param intOrdre
   *        L'identifiant (interne) de la demande creee.
   * @param serviceStruct
   *        Le code de la structure representant le service destinataire dans
   *        l'annuaire de l'etablissement.
   * @param userLogin
   *        Le login de l'utilisateur qui a cree la demande.
   * @param userId
   *        L'identifiant de l'utilisateur qui a cree la demande.
   */
  public static void logReply(Object disOrdre, Object intCleService, Object intOrdre, Object traOrdre, Object disOrdrePere,
                                 Object serviceStruct, Object userLogin, Object userId)
  {
    StringBuffer log = new StringBuffer();
    log.append("envoi message : #").append(disOrdre);
    log.append(", DT #").append(intCleService).append("/").append(intOrdre);
    if (traOrdre != null) {
    	log.append("- traitement #").append(traOrdre);
    }
    if (disOrdrePere != null) {
    	log.append("- message #").append(disOrdrePere);
    }
    log.append(", service dest. : #").append(serviceStruct);
    log.append(", user : ").append(userLogin).append("/").append(userId);
    log.append(" - OK");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche dans les logs de l'application un message suite a l'affectation d'une
   * demande.
   * 
   * @param intCleService
   *        L'identifiant de la demande creee parmis toutes les demandes du
   *        service.
   * @param intOrdre
   *        L'identifiant (interne) de la demande creee.
   * @param serviceStruct
   *        Le code de la structure representant le service destinataire dans
   *        l'annuaire de l'etablissement.
   * @param userId
   *        L'identifiant de l'utilisateur qui a affecte la demande.
   * @param userLogin
   *        Le login de l'utilisateur qui a affecte la demande.
   * @param usersDestLogin
   *        Les login des personnes affectees
   */
  public static void logAffectation(Object intCleService, Object intOrdre, 
                                 Object serviceStruct,
                                 Object userId, Object userLogin,
                                 Object usersDestLogin)
  {
    StringBuffer log = new StringBuffer();
    log.append("aff. demande : #").append(intCleService).append("/").append(intOrdre);
    log.append(", from : ").append(userLogin).append("/").append(userId);
    log.append(", service dest. : #").append(serviceStruct);
    log.append(", to : ").append(usersDestLogin);
    log.append(" - OK");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche dans les logs de l'application un message suit a la transmission d'une
   * demande a un autre service
   * 
   * @param prevIntCleService
   * 				L'ancien numero de DT
   * @param prevCStructure
   * 	      L'ancien service
   * @param intCleService
   *        L'identifiant de la demande creee parmis toutes les demandes du
   *        service.
   * @param intOrdre
   *        L'identifiant (interne) de la demande creee.
   * @param serviceStruct
   *        Le code de la structure representant le service destinataire dans
   *        l'annuaire de l'etablissement.
   * @param userId
   *        L'identifiant de l'utilisateur qui a affecte la demande.
   * @param userLogin
   *        Le login de l'utilisateur qui a affecte la demande.
   */
  public static void logTransmission(
  		Object prevIntCleService, Object prevCStructure,
  		Object intCleService, Object intOrdre, 
  		Object serviceStruct,
  		Object userId, Object userLogin) {
    StringBuffer log = new StringBuffer();
    log.append("trans. demande : #").append(prevIntCleService).append("/").append(intOrdre).append(" (service #").append(prevCStructure).append(")");
    log.append(", from : ").append(userLogin).append("/").append(userId);
    log.append(", to :").append(intCleService).append("/").append(intOrdre).append(" (service #").append(serviceStruct).append(")");
    log.append(" - OK");
    CktlLog.log(log.toString());
  }
  
  
  /**
   * Affiche dans les logs de l'application un message suite a la suppression d'affectation 
   * d'une demande.
   * 
   * @param intCleService
   *        L'identifiant de la demande creee parmis toutes les demandes du
   *        service.
   * @param intOrdre
   *        L'identifiant (interne) de la demande creee.
   * @param serviceStruct
   *        Le code de la structure representant le service destinataire dans
   *        l'annuaire de l'etablissement.
   * @param userLogin
   *        Le login de l'utilisateur qui a affecte la demande.
   * @param userId
   *        L'identifiant de l'utilisateur qui a affecte la demande.
   * @param usersDestId
   *        Les identifiants des personnes supprimees
   * @param usersDestLogin
   *        Les login des personnes supprimees
   */
  public static void logDeleteAffectation(Object intCleService, Object intOrdre, 
                                 Object serviceStruct,
                                 Object userId, Object userLogin,
                                 Object usersDestId, Object usersDestLogin)
  {
    StringBuffer log = new StringBuffer();
    log.append("delete affectation demande : #").append(intCleService).append("/").append(intOrdre);
    log.append(", affectant : ").append(userLogin).append("/").append(userId);
    log.append(", service dest. : #").append(serviceStruct);
    log.append(", intervenants : ").append(usersDestLogin);
    log.append(" - OK");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche dans les logs de l'application le message de la suppression d'une
   * demande.
   * 
   * @param appId
   *        L'identifiant de l'application qui effectue l'operation.
   * @param intOrdre
   *        Le code de la demande.
   * @param userId
   *        L'identifiant de la personne effectuant l'operation de suppression.
   */
  public static void logDelete(Object appId,
                               int intOrdre, int userId)
  {
    StringBuffer log = new StringBuffer();
    log.append("suppression demande : #").append(intOrdre);
    log.append(", id appli : ").append(appId);
    log.append(", user : ").append(userId).append(" - OK");
    CktlLog.log(log.toString());
  }
  
  /**
   * Affiche le message d'erreur dans la sortie standard de l'application.
   * Le message est prefixe par "&gt;&gt; Error |".
   */
  public static void logError(String errorMessage) {
    if (errorMessage != null)
      CktlLog.rawLog(StringCtrl.quoteText(errorMessage, ">> Error | ").trim());
  }
}
