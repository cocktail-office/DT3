import org.cocktail.dt.server.metier.EOBatiment;
import org.cocktail.dt.server.metier.EOContact;
import org.cocktail.dt.server.metier.EOSalles;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Composante de selection d'un batiment et d'un bureau
 * 
 * @author ctarade
 */
public class SelectBatimentBureau
		extends DTWebComponent {

	// bindings
	public EOBatiment batimentSelected;
	public EOSalles bureauSelected;

	// batiments
	public NSArray<EOBatiment> batimentList;
	public EOBatiment batimentItem;
	public EOBatiment localBatimentSelected;

	// bureaux
	public EOSalles bureauItem;
	public EOSalles localBureauSelected;

	// contact (optionnel) : si cette valeur est non vide, alors
	// on initialise la selection avec celle definies dans celui ci
	public EOContact contact;

	public SelectBatimentBureau(WOContext context) {
		super(context);
		initComponent();
	}

	/**
	 * Initialisation des données
	 */
	private void initComponent() {
		batimentList = contactsBus().findAllBatiments();
	}

	/**
	 * Affichage du libelle d'un bureau
	 * 
	 * @return
	 */
	public String bureauItemLibelle() {
		if (bureauItem != null) {
			CktlRecord rec =
					contactsBus().findBureauForCode(bureauItem.salNumero());
			if (rec != null)
				return contactsBus().getLibelleForSalle(rec);
		}
		return "<inconnu>";
	}

	// navigation

	public WOComponent neFaitRien() {
		return null;
	}

	// getters

	/**
	 * Liste des bureaux d'un batiment
	 */
	public NSArray<EOSalles> getBureauList() {
		NSArray<EOSalles> bureauList = new NSArray<EOSalles>();

		if (localBatimentSelected != null) {
			bureauList = CktlSort.sortedArray(
					contactsBus().findBureauxForBatiment(
							batimentSelected.cLocal()),
							EOSalles.SAL_ETAGE_KEY + "," + EOSalles.SAL_PORTE_KEY);
		}

		return bureauList;
	}

	// bus

	private DTContactsBus contactsBus() {
		return dtSession().dataCenter().contactsBus();
	}

	public final EOContact getContact() {
		return contact;
	}

	private EOContact prevContact;

	public final void setContact(EOContact value) {
		prevContact = contact;
		contact = value;
		if (prevContact != contact &&
				contact != null) {
			String cLocal = contact.cLocal();
			if (!StringCtrl.isEmpty(cLocal)) {
				// batimentSelected = contactsBus().findBatimentForCode(cLocal);
				setLocalBatimentSelected(contactsBus().findBatimentForCode(cLocal));
				Number salNumero = (cLocal != null ? contact.salNumero() : null);
				// bureaux (on prend celui de son contact par defaut)
				if (salNumero != null) {
					// bureauSelected = contactsBus().findBureauForCode(salNumero);
					setLocalBureauSelected(contactsBus().findBureauForCode(salNumero));
				}
			}
		}
	}

	public final void setBatimentSelected(EOBatiment value) {
		// variable en lecture seule
	}

	public final void setBureauSelected(EOSalles value) {
		// variable en lecture seule
	}

	public final void setLocalBatimentSelected(EOBatiment value) {
		if (value != null) {
			localBatimentSelected = value;
			batimentSelected = localBatimentSelected;
		}
	}

	public final void setLocalBureauSelected(EOSalles value) {
		if (value != null) {
			localBureauSelected = value;
			bureauSelected = localBureauSelected;
		}
	}
}