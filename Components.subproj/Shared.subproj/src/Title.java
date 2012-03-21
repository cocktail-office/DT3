
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
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;

/**
 * Gere l'entete d'une page de la DT.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class Title extends DTWebComponent {

	public Title(WOContext context) {
		super(context);
	}
	
  /**
   * Retourne la description sur l'utilisateur actuellement connecte a
   * l'application.
   */
	public String userDescription() {
		StringBuffer sb = new StringBuffer();
		CktlUserInfo uInfo = connectedUserInfo();

//		switch (uInfo.userStatus()) {
//			case CktlUserInfo.STATUS_PERSONNEL :
//				sb.append("Utilisateur : ");
//				break;
//			case CktlUserInfo.STATUS_ETUDIANT :
//				sb.append("Etudiant : ");
//				break;
//			case CktlUserInfo.STATUS_EXTERIEUR :
//				sb.append("Personne externe : ");
//				break;
//			default :
//				sb.append("Personne : ");
//		}
    sb.append("Utilisateur connect&eacute; : ");
    // si la personne est une personne morale, le prenom est vide
    if (!StringCtrl.isEmpty(uInfo.prenom())) {
      sb.append(StringCtrl.formatName(uInfo.prenom(), uInfo.nom()));
    } else {
      sb.append(uInfo.nom());
    }
    sb.append("<br/>").append(DateCtrl.dateToString(DateCtrl.now(), "%d.%m.%Y"));
		return sb.toString();
	}
	
	/**
	 * Indique s'il faut afficher le message d'arret
	 */
	public boolean shouldShowAppStopWarning() {
		return dtApp().appWillStop();
	}
	
	/**
	 * Retourne le message informant le temps restant avant arret
	 */
	public String appStopWarning() {
		return "ATTENTION - arr&ecirc;t de l'application dans " + dtApp().appTtl();
	}
}