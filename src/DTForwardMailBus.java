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
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.CktlConfig;
import org.cocktail.fwkcktlwebapp.server.CktlMailBus;
import org.cocktail.fwkcktlwebapp.server.CktlWebSession;

import com.webobjects.foundation.NSData;

/**
 * Une extension de <code>CktlMailBus</code> qui redirige tous les messages
 * mail envoyes par l'application a l'adresse de l'utilisateur actuellement
 * connecte. 
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTForwardMailBus extends CktlMailBus {
  /** Constante : envoyer les emails aux vrais destinataires */
  public static final String DEFAULT_FORWARD = "default";
  
  /** Constante : ignorer tous les messages email */
  public static final String NULL_FORWARD = "null";
  
  /** Constante : rediriger les messages a l'utilisateur connecte a l'application */
  public static final String CONNECTED_FORWARD = "connected";
  
  /** L'adresse email definit explicitement. */
  private String forwardMail;
  
  /** La reference vers la session en cours */
  private CktlWebSession session;
  
  /**
   * Cree un nouvel objet qui utilise la configuration donnee. La configuration
   * contient les parametres de serveur mail.
   */  
  public DTForwardMailBus(CktlConfig config, CktlWebSession session) {
    super(config);
    this.session = session;
  }
  
  /**
   * Indique la session dans le cadre de laquelle les messages email sont
   * envoyes. 
   */
  public void setSession(CktlWebSession session) {
    this.session = session;
  }

  /**
   * Indique l'adresse email ou les messages doivent etre rediriges.
   */
  public void setForwardMail(String email) {
    CktlLog.trace("forward mail : "+email);
    forwardMail = email;
  }
  
  /**
   * Retourne l'adresse e-mail ou tous les messages doivent etre rediriges.
   */
  public String getForwardMail() {
    String realMail = null;
    if (forwardMail != null) {
      // L'adresse email de l'utilisateur connecte a l'application
      if (forwardMail.equals(CONNECTED_FORWARD)) {
        if ((session != null) && (session.connectedUserInfo() != null))
          realMail = session.connectedUserInfo().email();
        CktlLog.trace("connected mail : "+realMail);
        CktlLog.trace("connected userInfo : "+session.connectedUserInfo());
      } else if (!forwardMail.equals(NULL_FORWARD)) {
        // Sinon, l'adresse email est donnee explicitement
        realMail = forwardMail;
      }
      CktlLog.log("Forward email set to : "+realMail);
    }
    return realMail;
  }

  /**
   * Envoie en message e-mail avec un fichier attache. 
   */
  public boolean sendMail(String from, String to, String cc,
                          String subject, String msgText,
                          String filename, NSData filedata)
  {
    StringBuffer sb = new StringBuffer();
    String fwdMail = getForwardMail();
    CktlLog.rawTrace("sendMail()");
    CktlLog.rawTrace("  TO : "+to);
    CktlLog.rawTrace("  CC : "+cc);
    CktlLog.rawTrace("  Forward TO : "+fwdMail);
    if (fwdMail == null) return true;
    sb.append("Ce message est redirige par DTForwadMailBus.\n");
    sb.append("Les destinataires originaux sont :\n");
    sb.append("  TO : ").append(to).append("\n");
    if (StringCtrl.normalize(cc).length() > 0)
      sb.append("  CC : ").append(cc).append("\n");
    sb.append("\n-------------- Message original -----------------\n\n").append(msgText);
    msgText = sb.toString();
    return super.sendMail(from, fwdMail, null, subject, msgText, filename, filedata);
  }

  /**
   * Envoie un message mail avec les fichiers attaches dont les chemins
   * d'acces sont donnes dans <code>filenames</code>.
   */
  public boolean sendMail(String from, String to, String cc,
                          String subject, String msgText,
                          String[] filenames)
  {
    StringBuffer sb = new StringBuffer();
    String fwdMail = getForwardMail();
    CktlLog.rawTrace("sendMail()");
    CktlLog.rawTrace("  TO : "+to);
    CktlLog.rawTrace("  CC : "+cc);
    CktlLog.rawTrace("  Forward TO : "+fwdMail);
    if (fwdMail == null) return true;
    sb.append("Ce message est redirige par DTForwadMailBus. ");
    sb.append("Les destinataires originaux sont :\n");
    sb.append("  TO : ").append(to).append("\n");
    if (StringCtrl.normalize(cc).length() > 0)
      sb.append("  CC : ").append(cc).append("\n");
    sb.append("\n-------------- Message original -----------------\n\n").append(msgText);
    msgText = sb.toString();
    return super.sendMail(from, fwdMail, null, subject, msgText, filenames);
  }

}
