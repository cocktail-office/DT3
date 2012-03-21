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

import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuItemSet;
import org.cocktail.fwkcktlwebapp.server.components.CktlMenuListener;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * 
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class Menu
		extends DTWebPage {

	private NSArray<EOVActivites> servicesDispoListe;
	private int consultationSubMenu;
	private int prevMenuId;

	// les id des items du menu
	private final static int ITEM_CREATION = 1;
	private final static int ITEM_CONSULTATION = 2;
	private final static int ITEM_PREFERENCES = 3;
	private final static int ITEM_PANIER = 8;
	private final static int ITEM_RECHERCHE = 4;
	private final static int ITEM_OUTILS = 7;
	private final static int ITEM_ADMINISTRATION = 5;
	private final static int ITEM_QUITTER = 6;

	private final static int ITEM_PREFERENCES_LANCEMENT = 16;
	private final static int ITEM_PREFERENCES_LISTES = 17;
	private final static int ITEM_PREFERENCES_INTERVENANTS = 18;
	private final static int ITEM_PREFERENCES_COULEURS = 19;
	private final static int ITEM_PREFERENCES_DIVERS = 20;

	private final static int ITEM_OUTILS_DTFAST = 30;
	private final static int ITEM_OUTILS_VENTILATION = 31;

	private final static int ITEM_ADMINISTRATION_DROITS = 21;
	private final static int ITEM_ADMINISTRATION_ACTIVITES = 22;
	private final static int ITEM_ADMINISTRATION_SYSTEME = 23;
	private final static int ITEM_ADMINISTRATION_STATS = 24;

	public Menu(WOContext context) {
		super(context);
		consultationSubMenu = 0;
		prevMenuId = 0;
	}

	public class DTMenuListener extends CktlMenuListener {

		// Methodes de listener
		public void initMenu() {
			// definition du mode par defaut d'apres les preferences
			if (dtSession().getMode() == Session.MODE_OUTILS) {
				// cas particulier de la DA DTFast
				listener().selectItemWithId(ITEM_OUTILS_DTFAST);
			} else if (dtSession().getMode() == Session.MODE_CONSULT ||
											(dtUserInfo().prefOnglet() != null &&
												dtUserInfo().prefOnglet().intValue() == DTPreferencesBus.NUMERO_ONGLET_CONSULTATION)) {
				listener().selectItemWithId(ITEM_CONSULTATION);
			} else {
				listener().selectItemWithId(ITEM_CREATION);
			}
		}

		public WOComponent selectMenu(int id) {
			WOComponent resultPage;
			switch (id) {
			case ITEM_CREATION:
				resultPage = dtSession().selectCreat();
				break;
			case ITEM_CONSULTATION:
				resultPage = dtSession().selectConsult(true, -1);
				break;
			case ITEM_RECHERCHE:
				resultPage = dtSession().selectRecherche();
				break;
			case ITEM_PANIER:
				resultPage = dtSession().selectPanier();
				break;
			case ITEM_PREFERENCES_LANCEMENT:
				resultPage = dtSession().selectPreferences(PagePreferences.ONGLET_PREF_LANCEMENT);
				break;
			case ITEM_PREFERENCES_LISTES:
				resultPage = dtSession().selectPreferences(PagePreferences.ONGLET_PREF_LISTES);
				break;
			case ITEM_PREFERENCES_INTERVENANTS:
				resultPage = dtSession().selectPreferences(PagePreferences.ONGLET_PREF_INTERVENANTS);
				break;
			case ITEM_PREFERENCES_COULEURS:
				resultPage = dtSession().selectPreferences(PagePreferences.ONGLET_PREF_COULEURS);
				break;
			case ITEM_PREFERENCES_DIVERS:
				resultPage = dtSession().selectPreferences(PagePreferences.ONGLET_PREF_DIVERS);
				break;
			case ITEM_OUTILS_DTFAST:
				resultPage = dtSession().selectOutil(PageOutils.ONGLET_OUTILS_DTFAST);
				break;
			case ITEM_OUTILS_VENTILATION:
				resultPage = dtSession().selectOutil(PageOutils.ONGLET_OUTILS_VENTILATION);
				break;
			case ITEM_ADMINISTRATION_DROITS:
				resultPage = dtSession().selectAdministration(PageAdministration.ONGLET_ADM_DROITS);
				break;
			case ITEM_ADMINISTRATION_ACTIVITES:
				resultPage = dtSession().selectAdministration(PageAdministration.ONGLET_ADM_ACTIVITES);
				break;
			case ITEM_ADMINISTRATION_SYSTEME:
				resultPage = dtSession().selectAdministration(PageAdministration.ONGLET_ADM_SYSTEME);
				break;
			case ITEM_ADMINISTRATION_STATS:
				resultPage = dtSession().selectAdministration(PageAdministration.ONGLET_ADM_STATS);
				break;
			case ITEM_QUITTER:
				resultPage = doExit();
				break;
			default: {
				// c'est la selection d'un service
				if (prevMenuId >= 1 && prevMenuId <= 3) {
					id = consultationSubMenu;
				} else {
					consultationSubMenu = id;
				}
				resultPage = dtSession().selectCreat(id);
				listener().selectItemWithId(id);
				// indiquer la selection dans la page de creation
				((PageCreation) dtSession().pageContenu()).setCodeServiceSelectedItem(Integer.toString(id));
			}
			}
			prevMenuId = id;
			return resultPage;
		}

		public WOComponent disconnect() {
			return dtSession().logout();
		}

	}

	public CktlMenuItemSet menuItemSet() {
		if (dtSession().getDtMenuItemSet() == null)
			dtSession().setDtMenuItemSet(createMenu());
		return dtSession().getDtMenuItemSet();
	}

	private CktlMenuItemSet createMenu() {
		NSArray<EOVActivites> services;
		CktlRecord rec;

		CktlMenuItemSet menuItemSet = new CktlMenuItemSet();
		// Initialisation de menu
		menuItemSet.addMenuItem(ITEM_CREATION, "Faire une demande", "Faire une demande de travaux", null);
		menuItemSet.addMenuItem(ITEM_CONSULTATION, "Consultation", "Consultation des demandes de travaux", null);
		menuItemSet.addMenuItem(ITEM_RECHERCHE, "Recherche", "Recherche de demandes de travaux", null);
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_LIMITE)) {
			menuItemSet.addMenuItem(ITEM_PANIER, "Panier", "Panier de demandes de travaux", null);
			menuItemSet.addMenuItem(ITEM_PREFERENCES, "Preferences", "R&eacute;gler vos param&grave;tres personnels pour l'application", null);
			menuItemSet.addMenuSubItem(ITEM_PREFERENCES, ITEM_PREFERENCES_LANCEMENT, "Lancement", PagePreferences.ONGLET_PREF_TITLE_LANCEMENT, null);
			menuItemSet.addMenuSubItem(ITEM_PREFERENCES, ITEM_PREFERENCES_LISTES, "Listes", PagePreferences.ONGLET_PREF_TITLE_LISTES, null);
			menuItemSet.addMenuSubItem(ITEM_PREFERENCES, ITEM_PREFERENCES_INTERVENANTS, "Intervenants", PagePreferences.ONGLET_PREF_TITLE_INTERVENANTS, null);
			menuItemSet.addMenuSubItem(ITEM_PREFERENCES, ITEM_PREFERENCES_COULEURS, "Couleurs", PagePreferences.ONGLET_PREF_TITLE_COULEURS, null);
			menuItemSet.addMenuSubItem(ITEM_PREFERENCES, ITEM_PREFERENCES_DIVERS, "Divers", PagePreferences.ONGLET_PREF_TITLE_DIVERS, null);
		}
		// onglet outils : disponible pour ceux qui ont un droit >=
		// DTUserInfo.DROIT_INTERVENANT_SUPER
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_INTERVENANT_SUPER)) {
			menuItemSet.addMenuItem(ITEM_OUTILS, "Outils", "Modules pour une utilisation avanc&eacute;e de l'application", null);
			menuItemSet.addMenuSubItem(ITEM_OUTILS, ITEM_OUTILS_VENTILATION, "Ventilation", PageAdministration.ONGLET_ADM_TITLE_ACTIVITES, null);
			// temporaire, DTFast en cours de dev
			String dtFastAcceptLogin = dtApp().config().stringForKey("DTFAST_ACCEPT_LOGIN");
			if (!StringCtrl.isEmpty(dtFastAcceptLogin)) {
				NSArray<String> validLogins = NSArray.componentsSeparatedByString(dtFastAcceptLogin, ",");
				if (validLogins.containsObject(dtUserInfo().login())) {
					menuItemSet.addMenuSubItem(ITEM_OUTILS, ITEM_OUTILS_DTFAST, "DTFast", PageAdministration.ONGLET_ADM_TITLE_DROITS, null);
				}
			}
		}

		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN)) {
			menuItemSet.addMenuItem(ITEM_ADMINISTRATION, "Administration", "R&eacute;gler les param&egrave;tres g&eacute;n&eacute;raux de l'application", null);
			menuItemSet.addMenuSubItem(ITEM_ADMINISTRATION, ITEM_ADMINISTRATION_DROITS, "Droits", PageAdministration.ONGLET_ADM_TITLE_DROITS, null);
			menuItemSet.addMenuSubItem(ITEM_ADMINISTRATION, ITEM_ADMINISTRATION_ACTIVITES, "Activites", PageAdministration.ONGLET_ADM_TITLE_ACTIVITES, null);
			if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER)) {
				menuItemSet.addMenuSubItem(ITEM_ADMINISTRATION, ITEM_ADMINISTRATION_SYSTEME, "Systeme", PageAdministration.ONGLET_ADM_TITLE_SYSTEME, null);
			}
			menuItemSet.addMenuSubItem(ITEM_ADMINISTRATION, ITEM_ADMINISTRATION_STATS, "Statistiques", PageAdministration.ONGLET_ADM_TITLE_STATS, null);
		}
		menuItemSet.addMenuItem(ITEM_QUITTER, "Quitter", "Fermer votre session dans cette application", null);

		// Les sous menus pour la creation - les services
		services = getServicesList();
		if (services.count() > 0)
			consultationSubMenu = ((CktlRecord) services.objectAtIndex(0)).intForKey("actOrdre");
		for (int i = 0; i < services.count(); i++) {
			rec = (CktlRecord) services.objectAtIndex(i);
			menuItemSet.addMenuSubItem(ITEM_CREATION, rec.intForKey("actOrdre"),
																rec.stringForKey("actCode"),
																rec.stringForKey("actLibelle"), null);
		}
		return menuItemSet;
	}

	private NSArray<EOVActivites> getServicesList() {
		if (servicesDispoListe == null || servicesDispoListe.count() == 0) {
			CktlSort activiteSort = CktlSort.newSort(EOVActivites.GRP_POSITION_KEY + "," + EOVActivites.ACT_LIBELLE_KEY);
			servicesDispoListe = EOVActivites.fetchAll(
					dtSession().dataBus().editingContext(),
					DTDataBus.newCondition(getVisibleServicesCondition()),
					activiteSort);
		}
		return servicesDispoListe;
	}

	/**
	 * Retourne la condition que doivent satisfaire les services visibles dans le
	 * menu de creation des demandes.
	 */
	private String getVisibleServicesCondition() {
		StringBuffer sb = new StringBuffer();
		// Uniquement les service affichables
		sb.append("(actOrdre<0 and grpAffichable='O')");
		// Si rien n'est definit, alors visible par defaut
		sb.append(" and (");
		// Sinon, visible sur le Web...
		sb.append("grpVisibilite=nil or (grpVisibilite like '*WEB*'");
		// ...et pour le vLan de l'utilisateur
		if (dtSession().connectedUserInfo().vLan() != null) {
			sb.append(" and grpVisibilite like '*|");
			sb.append(dtSession().connectedUserInfo().vLan()).append("|*'");
		}
		sb.append(")"); // fin OR
		// ...et pour le groupe de l'utilisateur
		sb.append(" and (grpVisibiliteStructure=nil");
		NSArray<CktlRecord> listRepart = dtSession().dataCenter().serviceBus().repartStructuresForPersId(dtSession().connectedUserInfo().persId());
		if (listRepart.count() > 0) {
			sb.append(" or (");
			for (int i = 0; i < listRepart.count(); i++) {
				CktlRecord recRepart = (CktlRecord) listRepart.objectAtIndex(i);
				sb.append("grpVisibiliteStructure like '*|");
				sb.append(recRepart.stringForKey("cStructure")).append("|*'");
				if (i < listRepart.count() - 1) {
					sb.append(" or ");
				}
			}
			sb.append(")");
		}
		sb.append(")"); // fin AND
		sb.append(")"); // fin AND
		return sb.toString();
	}

	public CktlMenuListener listener() {
		if (dtSession().getDtMenuLister() == null)
			dtSession().setDtMenuLister(new DTMenuListener());
		return dtSession().getDtMenuLister();
	}

	/**
	 * Execute l'action de la deconnexion suite au click sur le lien de la
	 * deconnexion.
	 */
	public WOComponent doExit() {
		return dtSession().doExit();
	}

}