import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervenant;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.CktlSort;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXQ;
import fr.univlr.cri.planning._imports.StringCtrl;

public class PageMylyn extends DTWebComponent {

	public NSArray<EOIntervention> eoInterventionArray;
	public EOIntervention eoInterventionItem;

	private String login;
	private Number actOrdre;

	public PageMylyn(WOContext context) {
		super(context);
	}

	public NSArray<EOIntervention> getEoInterventionArray() {
		if (eoInterventionArray == null) {
			eoInterventionArray = new NSArray<EOIntervention>();
			if (!StringCtrl.isEmpty(getLogin())) {
				try {
					DTUserInfo ui = dtSession().dataCenter().preferencesBus().getUserInfoForLogin(getLogin());

					EOQualifier qual = ERXQ.and(
							ERXQ.equals(EOIntervention.INT_ETAT_KEY, EOEtatDt.ETAT_EN_COURS),
							ERXQ.equals(EOIntervention.TOS_INTERVENANT_KEY + "." + EOIntervenant.NO_INDIVIDU_KEY, ui.noIndividu()));
					if (getActOrdre() != null) {
						qual = ERXQ.and(
								qual,
								ERXQ.equals(EOIntervention.ACT_ORDRE_KEY, getActOrdre()));
					}
					eoInterventionArray = EOIntervention.fetchAll(
							dtSession().defaultEditingContext(), qual);
					// classement selon les preferences
					// TODO prendre aussi la colonne de tri
					if (ui.isSortIntAscending()) {
						eoInterventionArray = CktlSort.sortedArray(
								eoInterventionArray, EOIntervention.INT_CLE_SERVICE_KEY);
					} else {
						eoInterventionArray = CktlSort.sortedArray(
								eoInterventionArray, EOIntervention.INT_CLE_SERVICE_KEY, CktlSort.Descending);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		return eoInterventionArray;
	}

	public String href() {
		return dtSession().appConsultationURL(eoInterventionItem.intOrdre());
	}

	public final String getLogin() {
		return login;
	}

	public final void setLogin(String login) {
		eoInterventionArray = null;
		this.login = login;
	}

	public final Number getActOrdre() {
		return actOrdre;
	}

	public final void setActOrdre(Number actOrdre) {
		eoInterventionArray = null;
		this.actOrdre = actOrdre;
	}

}