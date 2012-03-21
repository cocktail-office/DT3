import org.cocktail.dt.server.metier.EOGroupesDt;
import org.cocktail.dt.server.metier.EOStructure;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Definit les methodes pour une vue d'administration
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public abstract class A_AdministrationSwapView
		extends DTWebComponent {

	// la liste des service de DT
	public NSArray<EOStructure> serviceList;
	public EOStructure serviceItem;
	public EOStructure serviceSelected;

	/** message d'erreur */
	public String errorMessage;

	public A_AdministrationSwapView(WOContext context) {
		super(context);
		initCommonStuff();
		initComponent();
	}

	/**
	 * Charger les parametres stockees dans la base. Methode appelee au chargement
	 * de la swapview
	 */
	protected abstract void initComponent();

	/**
	 * Initialisations communes
	 */
	private void initCommonStuff() {
		initServiceList();
	}

	/**
	 * Recharger la liste des services depuis la base de donnees
	 */
	public void initServiceList() {
		// liste des services selon le profil connecte
		// super admin : tous les services
		if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN_SUPER)) {
			serviceList = (NSArray<EOStructure>) serviceBus().allGroupesDt(true).valueForKey(
					EOGroupesDt.TO_STRUCTURE_ULR_KEY);
		} else if (dtUserInfo().hasDroit(DTUserInfo.DROIT_ADMIN)) {
			// administrateur simple : seuls les services dont il est responsable
			NSArray cStructureList = preferencesBus().getAllServiceCodes(dtUserInfo().noIndividu(), DTUserInfo.DROIT_ADMIN);
			serviceList = new NSArray();
			for (int i = 0; i < cStructureList.count(); i++) {
				serviceList = serviceList.arrayByAddingObject(
						serviceBus().structureForCode((String) cStructureList.objectAtIndex(i)));
			}
		} else {
			// sinon pas d'acces
			serviceList = new NSArray();
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	//

	public boolean hasErrorMessage() {
		return !StringCtrl.isEmpty(errorMessage);
	}

	// display

	public String serviceDisplay() {
		return serviceItem.stringForKey("lcStructure");
	}

	// bus de donnees

	protected DTPreferencesBus preferencesBus() {
		return dtSession().dataCenter().preferencesBus();
	}

	protected DTServiceBus serviceBus() {
		return dtSession().dataCenter().serviceBus();
	}

	protected DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}

	protected DTActiviteBus activiteBus() {
		return dtSession().dataCenter().activiteBus();
	}
}