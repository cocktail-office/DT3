import org.cocktail.dt.server.components.swap.I_Swap;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Vue concernant les demandes de travaux "logiciel" : un motif standard puis
 * une spécification "ECS" : "Evolution", "Correctif", "Support"
 * 
 * @author ctarade
 */
public class SwapLogiciel extends SwapDefault {

	private final static String TYPE_ECS_EVOLUTION = "Evolution";
	private final static String TYPE_ECS_CORRECTIF = "Correctif";
	private final static String TYPE_ECS_SUPPORT = "Support";

	private final static NSArray<String> TYPE_ECS_ARRAY = new NSArray<String>(new String[] {
			TYPE_ECS_EVOLUTION, TYPE_ECS_CORRECTIF, TYPE_ECS_SUPPORT });

	public NSArray<String> typeEcsArray;
	public String typeEcsSelected;
	public String typeEcsItem;

	private boolean shouldIgnoreSetterTypeEcsSelected;

	public SwapLogiciel(WOContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SwapDefault#initView()
	 */
	@Override
	public void initView() {
		super.initView();
		typeEcsArray = TYPE_ECS_ARRAY;
		typeEcsSelected = TYPE_ECS_SUPPORT;
		// interdire au premier appel car c'est Null qui est systématiquement passé
		shouldIgnoreSetterTypeEcsSelected = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SwapDefault#fillDataDictionary()
	 */
	@Override
	public boolean fillDataDictionary() {
		if (super.fillDataDictionary() == false) {
			return false;
		}

		// préfixer par le type de demande
		String suffixIntMotif = "";
		suffixIntMotif += "Type de demande logicielle : ";
		if (!StringCtrl.isEmpty(typeEcsSelected)) {
			suffixIntMotif += typeEcsSelected;
		} else {
			suffixIntMotif += "non précisé";
		}

		intMotif += "\n\n" + suffixIntMotif;

		// on test si ca rentre
		if (!dtSession().dtDataBus().checkForMaxSize(
				EOIntervention.ENTITY_NAME, EOIntervention.INT_MOTIF_KEY, intMotif, "Motif", 0, true, true)) {
			setMainError(dtSession().dtDataBus().getErrorMessage());
			return false;
		}

		if (!hasErrors()) {
			// hop on le remet
			saveDataDico.setObjectForKey(intMotif, "intMotif");
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return I_Swap.SWAP_VIEW_LOGICIEL_ID;
	}

	public final String getTypeEcsSelected() {
		return typeEcsSelected;
	}

	public final void setTypeEcsSelected(String value) {
		if (shouldIgnoreSetterTypeEcsSelected == false) {
			typeEcsSelected = value;
		}
		shouldIgnoreSetterTypeEcsSelected = false;
	}
}