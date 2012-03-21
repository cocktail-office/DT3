import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXCustomObject;
import er.extensions.eof.ERXQ;

/**
 * Chart "Pie"
 * 
 * @author ctarade
 * 
 */
public class DTChartRepartitionActivite
		extends A_DTChartComponent {

	private NSArray<ActCount> actCountArray;

	public ActCount actCountItem;
	public int actCountItemIndex;

	public DTChartRepartitionActivite(WOContext context) {
		super(context);
	}

	public class ActCount extends ERXCustomObject {
		private EOVActivites eoVActivites;
		private Integer count;

		public final static String COUNT_KEY = "count";
		public final static String EOVACTIVITES_KEY = "eoVActivites";

		public ActCount(EOVActivites anEoVActivites, int aCount) {
			super();
			eoVActivites = anEoVActivites;
			count = new Integer(aCount);
		}

		public final EOVActivites getEoVActivites() {
			return eoVActivites;
		}

		public final Integer getCount() {
			return count;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String libelle() {
		String libelle = actCountItem.getEoVActivites().actLibelle();

		libelle = StringCtrl.replace(libelle, "'", "");
		libelle = StringCtrl.replace(libelle, "&", "et");
		libelle = "'" + libelle + "'";

		return libelle;
	}

	/**
	 * 
	 * @return
	 */
	public String titre() {
		String titre = "Repartition creation de DT sur ";

		titre += DateCtrl.dateToString(getDDebut()) + " au " + DateCtrl.dateToString(getDFin());

		titre += " (" + getActCountArray().valueForKey("@sum." + ActCount.COUNT_KEY) + ")";

		return titre;
	}

	private NSArray<EOIntervention> filterInterventionArrayForActivite(
			NSArray<EOIntervention> interventionArray, Integer actOrdre) {
		NSArray<EOIntervention> array = null;

		String actOrdreHierarchie =
				EOIntervention.TO_ACTIVITES_KEY + "." + EOActivites.TO_V_ACTIVITES_KEY + "." + EOVActivites.ACT_ORDRE_HIERARCHIE_KEY;

		String strActOrdre = Integer.toString(actOrdre);

		EOQualifier qual = ERXQ.and(
				ERXQ.or(
						ERXQ.equals(EOIntervention.ACT_ORDRE_KEY, actOrdre),
						ERXQ.like(actOrdreHierarchie, "*; " + strActOrdre + ";*"),
						ERXQ.like(actOrdreHierarchie, "*; " + strActOrdre),
						ERXQ.like(actOrdreHierarchie, strActOrdre + ";*"),
						ERXQ.equals(actOrdreHierarchie, strActOrdre)));

		array = EOQualifier.filteredArrayWithQualifier(
				interventionArray, qual);

		return array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeDDebut()
	 */
	@Override
	public void onChangeDDebut() {
		setInterventionArrayForService(null);
		actCountArray = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeDFin()
	 */
	@Override
	public void onChangeDFin() {
		setInterventionArrayForService(null);
		actCountArray = null;
	}

	public final NSArray<ActCount> getActCountArray() {
		if (actCountArray == null) {

			actCountArray = new NSArray<DTChartRepartitionActivite.ActCount>();

			NSArray<EOVActivites> eoVActivitesArray = getEoVActivites().tosActFils();
			// si c'est une feuille on la prend
			if (eoVActivitesArray.count() == 0) {
				eoVActivitesArray = new NSArray<EOVActivites>(getEoVActivites());
			}

			for (int i = 0; i < eoVActivitesArray.count(); i++) {
				EOVActivites act = (EOVActivites) eoVActivitesArray.objectAtIndex(i);
				int count = filterInterventionArrayForActivite(
						getInterventionArrayForService(), act.actOrdre()).count();
				actCountArray = actCountArray.arrayByAddingObject(
						new ActCount(act, count));
			}

			// classement décroissant
			actCountArray = CktlSort.sortedArray(actCountArray, ActCount.COUNT_KEY, CktlSort.Descending);
		}
		return actCountArray;
	}

	public final void setActCountArray(NSArray<ActCount> actCountArray) {
		this.actCountArray = actCountArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeService()
	 */
	@Override
	public void onChangeService() {
		// TODO Auto-generated method stub
		setInterventionArrayForService(null);
		actCountArray = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeActivite()
	 */
	@Override
	public void onChangeActivite() {
		// TODO Auto-generated method stub
		actCountArray = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#qualifierInterventionService()
	 */
	@Override
	public EOQualifier qualifierInterventionService() {
		EOQualifier qual = null;

		qual = ERXQ.and(
				ERXQ.equals(
						EOIntervention.C_STRUCTURE_KEY,
						getEoVActivites().cStructure()),
				ERXQ.between(
						EOIntervention.INT_DATE_CREATION_KEY,
						getDDebut(),
						getDFin()));

		return qual;
	}

}