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

import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebComponent;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

/**
 * Regroupe les references vers les objets le plus souvent utilises.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTWebComponent
		extends CktlWebComponent {

	public final String SANS_OBJET = EOTraitement.SANS_OBJET;
	public final String OUI = EOTraitement.OUI;
	public final String NON = EOTraitement.NON;

	/**
	 * Cree une nouvelle instance de composant.
	 */
	public DTWebComponent(WOContext context) {
		super(context);
	}

	/**
	 * Retourne la reference vers l'application DT Web en cours d'execution.
	 */
	public Application dtApp() {
		return (Application) cktlApp;
	}

	/**
	 * Retourne la reference vers la session actuellement ouverte.
	 */
	public Session dtSession() {
		return (Session) cktlSession();
	}

	/**
	 * Retourne la reference vers l'objet contenant les informations specifiques a
	 * l'application DT sur l'utilisateur actuellement connecte a l'application.
	 */
	public DTUserInfo dtUserInfo() {
		return dtSession().dtUserInfo();
	}

	/**
	 * Retourne la reference vers l'objet contenant les informations sur
	 * l'utilisateur actuellement connecte a l'application.
	 */
	public CktlUserInfo connectedUserInfo() {
		return dtSession().connectedUserInfo();
	}

	/**
	 * Retourne la reference vers l'objet centralisant l'acces a la base de
	 * donnees.
	 */
	public DTDataCenter dtDataCenter() {
		return dtSession().dataCenter();
	}

	/**
	 * Indique si le navigateur utilise est INTERNET EXPLORER. Test effectue pour
	 * supprimer certaines zones AJAX qui ne marchent pas avec ce navigateur de
	 * m****
	 * 
	 * @return
	 */
	public boolean isNavigatorInternetExplorer() {
		String userAgent = context().request().headerForKey("user-agent");
		return StringCtrl.containsIgnoreCase(userAgent, "MSIE");
	}

	/**
	 * Action neutre pour les actions ajax
	 * 
	 * @return
	 */
	public WOComponent neFaitRien() {
		return null;
	}

	/**
	 * Sauvegarder
	 * 
	 * @param ec
	 */
	protected void sauvegarder(EOEditingContext ec) throws Exception {
		try {
			ec.lock();
			ec.saveChanges();
		} catch (Exception e) {
			e.printStackTrace();
			ec.revert();
			throw e;
		} finally {
			ec.unlock();
		}
	}
}
