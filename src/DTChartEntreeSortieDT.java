import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;

/**
 * Gestion des graphes
 * 
 * @author ctarade
 */
public class DTChartEntreeSortieDT extends DTWebComponent {

	// binding : le service concerné
	public String cStructure = "25";

	// item
	public NSTimestamp dateItem;
	public int dateItemIndex;

	// TODO binding
	private final static int NB_JOUR = 60;

	private NSArray<EOIntervention> eoInterventionArray;
	private NSArray<EOTraitement> eoTraitementArray;

	private NSArray<NSTimestamp> dateArray;

	public DTChartEntreeSortieDT(WOContext context) {
		super(context);
	}

	private NSArray<EOIntervention> eoInterventionArray() {
		if (eoInterventionArray == null) {
			eoInterventionArray = EOIntervention.fetchAll(
					dtSession().defaultEditingContext(),
					ERXQ.and(
							ERXQ.equals(EOIntervention.C_STRUCTURE_KEY, cStructure),
							ERXQ.between(EOIntervention.INT_DATE_CREATION_KEY,
									DateCtrl.now().timestampByAddingGregorianUnits(0, 0, -NB_JOUR, 0, 0, 0),
									DateCtrl.now())));
		}
		return eoInterventionArray;
	}

	private NSArray<EOTraitement> eoTraitementArray() {
		if (eoTraitementArray == null) {
			eoTraitementArray = EOTraitement.fetchAll(
					dtSession().defaultEditingContext(),
					ERXQ.and(
							ERXQ.equals(EOTraitement.TO_INTERVENTION_KEY + "." + EOIntervention.C_STRUCTURE_KEY, cStructure),
							ERXQ.between(EOTraitement.TRA_DATE_FIN_KEY,
									DateCtrl.now().timestampByAddingGregorianUnits(0, 0, -NB_JOUR, 0, 0, 0),
									DateCtrl.now())));
			// ne garder que le dernier traitement
			NSArray<EOIntervention> eoInterventionArrayTraite =
					NSArrayCtrl.removeDuplicate((NSArray) eoTraitementArray.valueForKey(EOTraitement.TO_INTERVENTION_KEY));
			NSArray<EOTraitement> eoTraitementArrayFiltre = new NSArray<EOTraitement>();
			for (int i = 0; i < eoInterventionArrayTraite.count(); i++) {
				EOIntervention eoIntervention = eoInterventionArrayTraite.objectAtIndex(i);
				NSArray<EOTraitement> eoTraitementInterventionArray = EOQualifier.filteredArrayWithQualifier(
						eoTraitementArray,
						ERXQ.equals(EOTraitement.TO_INTERVENTION_KEY, eoIntervention));
				EOTraitement eoTraitementDernier = eoTraitementInterventionArray.lastObject();
				eoTraitementArrayFiltre = eoTraitementArrayFiltre.arrayByAddingObject(eoTraitementDernier);
			}
			eoTraitementArray = eoTraitementArrayFiltre;
		}
		return eoTraitementArray;
	}

	/**
	 * La liste des jours
	 * 
	 * @return
	 */
	public NSArray<NSTimestamp> dateArray() {
		if (dateArray == null) {
			dateArray = new NSArray<NSTimestamp>();
			for (int i = 0; i < NB_JOUR; i++) {
				dateArray = dateArray.arrayByAddingObject(
						DateCtrl.now().timestampByAddingGregorianUnits(0, 0, -NB_JOUR + i, 0, 0, 0));
			}
		}
		return dateArray;
	}

	/**
	 * Nombre de colonnes
	 * 
	 * @return
	 */
	public String getAddRow() {
		String addRow = null;

		addRow = Integer.toString(NB_JOUR);

		return addRow;
	}

	/**
	 * date
	 * 
	 * @return
	 */
	public String getSetValue1() {
		String setValue1 = null;

		setValue1 = "'" + DateCtrl.dateToString(dateItem) + "'";

		return setValue1;
	}

	/**
	 * hauteur chart 1
	 * 
	 * @return
	 */
	public String getSetValue2() {
		String setValue2 = null;

		NSArray<EOIntervention> eoInterventionJour = EOQualifier.filteredArrayWithQualifier(
				eoInterventionArray(),
				ERXQ.between(
						EOIntervention.INT_DATE_CREATION_KEY,
						DateCtrl.stringToDate(DateCtrl.dateToString(dateItem)),
						DateCtrl.stringToDate(DateCtrl.dateToString(dateItem.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0)))));

		setValue2 = Integer.toString(eoInterventionJour.count());

		return setValue2;
	}

	/**
	 * hauteur chart 2
	 * 
	 * @return
	 */
	public String getSetValue3() {
		String setValue3 = null;

		NSArray<EOTraitement> eoTraitementJour = EOQualifier.filteredArrayWithQualifier(
				eoTraitementArray(),
				ERXQ.between(
						EOTraitement.TRA_DATE_FIN_KEY,
						DateCtrl.stringToDate(DateCtrl.dateToString(dateItem)),
						DateCtrl.stringToDate(DateCtrl.dateToString(dateItem.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0)))));

		setValue3 = Integer.toString(eoTraitementJour.count());

		return setValue3;
	}

	/**
	 * 
	 * @return
	 */
	public String getNbJour() {
		String nbJour = "";

		nbJour = Integer.toString(NB_JOUR);

		return nbJour;
	}

}