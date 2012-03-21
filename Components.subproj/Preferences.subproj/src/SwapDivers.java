/*
 * Copyright CRI - Universite de La Rochelle, 2001-2006
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

/**
 * Options "Autres"
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

public class SwapDivers extends PreferencesSwapView {

	private int awakeCountSinceAppendToResponse;

	public SwapDivers(WOContext context) {
		super(context);
	}

	/**
	 * Lors de la sortie de cette page, par un clic sur le WOBrowser des
	 * preferences, la methode awake() est re-appelee. Cela pose probleme car les
	 * setters sont tous appeles avec value=false au prochain affichage de la
	 * page. Il faut donc inhiber les setters dans ce cas particulier.
	 */
	public void awake() {
		super.awake();
		awakeCountSinceAppendToResponse++;
	}

	/**
	 * On note si oui ou non l'appendToResponse vient d'etre appele. Sequence
	 * normale = 1. awake 2. appendToResponse et 3.awake bugge lors du depart de
	 * cette page par le WOBrowser
	 */
	public void appendToResponse(WOResponse arg0, WOContext arg1) {
		super.appendToResponse(arg0, arg1);
		awakeCountSinceAppendToResponse = 0;
	}

	/**
	 * Meme bug a la creation de la page, il fait appel aux setters avec false ...
	 * on bidouille donc pour qu'il ignore cela en considerant qu'un awake a deja
	 * ete fait
	 */
	protected void initComponent() {
		awakeCountSinceAppendToResponse = 1;
	}

	public boolean isAfficherPhoto() {
		return dtUserInfo().usePhoto();
	}

	public boolean isEnvoyerMailTraitement() {
		return dtUserInfo().sendMailTraitement();
	}

	public boolean isUseMailInterne() {
		return dtUserInfo().useMailInterne();
	}

	public boolean isInsertDtSig() {
		return dtUserInfo().insertDtSig();
	}

	public boolean isExportPlanning() {
		return dtUserInfo().exportPlanning();
	}

	public boolean isControleChevauchementPlanning() {
		return dtUserInfo().controleChevauchementPlanning();
	}

	public boolean isConfirmationCloture() {
		return dtUserInfo().confirmationCloture();
	}

	public boolean isSauvegarderPanier() {
		return dtUserInfo().sauvegarderPanier();
	}

	public void setIsAfficherPhoto(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null, null,
					value, null, null, null, null, null, null, null, null, null, null, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsEnvoyerMailTraitement(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, value,
					null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsUseMailInterne(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, value, null, null, null, null, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsInsertDtSig(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, null, null, value, null, null, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsExportPlanning(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, null, null, null, value, null, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsControleChevauchementPlanning(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, value, null, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsConfirmationCloture(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, value, null, null);
			dtSession().doReloadPreferences();
		}
	}

	public void setIsSauvegarderPanier(Boolean value) {
		if (awakeCountSinceAppendToResponse != 2) {
			pBus().updatePrefAppli(null, dtUserInfo().noIndividu(), null, null, null,
					null, null, null, null, null, null, null, null, null, null, null, null, null, value, null);
			dtSession().doReloadPreferences();
		}
	}
}