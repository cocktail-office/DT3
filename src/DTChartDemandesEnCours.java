import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.dt.server.metier.EOVStatEnCours;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXQ;

public class DTChartDemandesEnCours
		extends A_DTChartComponent {

	public NSArray<NSTimestamp> dateArray;

	public NSArray<Integer> countEnCoursArray;
	public Integer countEnCoursItem;
	public int countEnCoursItemIndex;

	// cache
	private NSArray<EOVStatEnCours> eoVStatEnCoursArray;

	public DTChartDemandesEnCours(WOContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeDDebut()
	 */
	@Override
	public void onChangeDDebut() {
		dateArray = null;
		countEnCoursArray = null;
		eoVStatEnCoursArray = null;
		setInterventionArrayForService(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeDFin()
	 */
	@Override
	public void onChangeDFin() {
		dateArray = null;
		countEnCoursArray = null;
		eoVStatEnCoursArray = null;
		setInterventionArrayForService(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeService()
	 */
	@Override
	public void onChangeService() {
		countEnCoursArray = null;
		eoVStatEnCoursArray = null;
		setInterventionArrayForService(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#onChangeActivite()
	 */
	@Override
	public void onChangeActivite() {
		countEnCoursArray = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_DTChartComponent#titre()
	 */
	@Override
	public String titre() {
		// TODO Auto-generated method stub
		return "Nombre de DTs sans traitement";
	}

	/**
	 * Pas utilisé
	 * 
	 * @see A_DTChartComponent#qualifierInterventionService()
	 */
	@Override
	public EOQualifier qualifierInterventionService() {
		return null;
	}

	public final NSArray<NSTimestamp> getDateArray() {
		if (dateArray == null) {
			dateArray = new NSArray<NSTimestamp>();
			if (DateCtrl.isBefore(getDDebut(), getDFin())) {
				NSTimestamp date = getDDebut();
				while (DateCtrl.isBeforeEq(date, getDFin())) {
					dateArray = dateArray.arrayByAddingObject(date);
					date = date.timestampByAddingGregorianUnits(0, 0, 1, 0, 0, 0);
				}
			}
		}
		return dateArray;
	}

	public final void setDateArray(NSArray<NSTimestamp> dateArray) {
		this.dateArray = dateArray;
	}

	public final NSArray<Integer> getCountEnCoursArray() {
		if (countEnCoursArray == null) {
			countEnCoursArray = new NSArray<Integer>();
			for (int i = 0; i < getDateArray().count(); i++) {
				NSTimestamp date = getDateArray().objectAtIndex(i);

				// les DTs en cours à la date donnée
				EOQualifier qual = ERXQ.and(
						ERXQ.lessThan(EOVStatEnCours.INT_DATE_CREATION_KEY, date),
						ERXQ.or(
								ERXQ.greaterThan(EOVStatEnCours.TRA_DATE_FIN_KEY, date),
								ERXQ.isNull(EOVStatEnCours.TRA_DATE_FIN_KEY)));

				int count = EOQualifier.filteredArrayWithQualifier(getEoVStatEnCoursArray(), qual).count();

				countEnCoursArray = countEnCoursArray.arrayByAddingObject(new Integer(count));
			}
		}
		return countEnCoursArray;
	}

	public final void setCountEnCoursArray(NSArray<Integer> countEnCoursArray) {
		this.countEnCoursArray = countEnCoursArray;
	}

	public String getDateItem() {
		String strDate = "";

		strDate = DateCtrl.dateToString(getDateArray().objectAtIndex(countEnCoursItemIndex));
		strDate = "'" + strDate + "'";

		return strDate;
	}

	/**
	 * La liste des objets stat en cours pour un meme service et une meme période
	 * 
	 * @return
	 */
	private final NSArray<EOVStatEnCours> getEoVStatEnCoursArray() {
		if (eoVStatEnCoursArray == null) {

			errMessage = null;

			EOQualifier qual = ERXQ.and(
					ERXQ.equals(EOVStatEnCours.C_STRUCTURE_KEY, getEoVActivites().cStructure()),
					ERXQ.lessThan(EOVStatEnCours.INT_DATE_CREATION_KEY, getDFin()),
					ERXQ.or(
							ERXQ.isNull(EOVStatEnCours.TRA_DATE_FIN_KEY),
							ERXQ.greaterThan(EOVStatEnCours.TRA_DATE_FIN_KEY, getDDebut())));

			CktlLog.log("qual=" + qual);

			// on controle la fetch limit pour eviter les OutOfMemory
			DTEOEditingContextHandler handler = new DTEOEditingContextHandler();

			eoVStatEnCoursArray = EOVStatEnCours.fetchAll(
							dtSession().defaultEditingContext(),
							qual,
							null,
							true,
							EOVStatEnCours.MAX_FETCH_LIMIT_STATS,
							handler);

			if (handler.getFetchLimitWasExceeded()) {
				errMessage = "Attention, la limite maximum de " + EOVStatEnCours.MAX_FETCH_LIMIT_STATS + " traitées a été atteinte.\n" +
								"Seuls ces résultats seront traités par la statistique ... réduisez la période d'interrogation pour avoir une statistique plus précise";
			}
		}
		return eoVStatEnCoursArray;
	}
}