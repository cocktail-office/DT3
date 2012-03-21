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
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

/**
 * Gere l'interface HTML de la vue affichan un message.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class SwapMessage
		extends A_CreationSwapView {

	/**
	 * Cree une nouvelle instance de la vue.
	 */
	public SwapMessage(WOContext context) {
		super(context);
	}

	/*
	 * @see A_CreationSwapView#initView()
	 */
	public void initView() {
	}

	/*
	 * @see A_CreationSwapView#fillDataDictionary()
	 */
	protected boolean fillDataDictionary() {
		return true;
	}

	/*
	 * @see A_CreationSwapView#sendMail(com.webobjects.foundation.NSArray)
	 */
	protected void sendMail(NSArray<String> filesURL) {
	}

	/*
	 * @see A_CreationSwapView#createDevis()
	 */
	protected boolean createDevis() {
		return false;
	}

	/*
	 * @see A_CreationSwapView#getCategorieGED()
	 */
	protected String getCategorieGED() {
		return null;
	}

	/*
	 * @see A_CreationSwapView#resetDestin()
	 */
	protected void resetDestin() {
	}

	/*
	 * @see A_CreationSwapView#attachedFilePaths()
	 */
	protected NSArray attachedFilePaths() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see A_CreationSwapView#clearViewErrors()
	 */
	protected void clearViewErrors() {
	}

	/*
	 * Pas de fichiers attaches avec les demandes.
	 */
	protected boolean ignoreFileSaveErrors() {
		return true;
	}

	/*
	 * @see A_CreationSwapView#getPostCreateWarning()
	 */
	protected String getPostCreateWarning() {
		return null;
	}

	/**
	 * Retourne le message qui doit etre affiche dans la vue.
	 */
	public String viewMessage() {
		String msg = null;
		if (browserRecord() != null)
			msg = browserRecord().stringForKeyPath("toActPref.actSwapMessage");
		if (msg == null) {
			msg = "(Aucun message defini)";
		} else {
			msg = StringCtrl.replace(msg, "\n", "<br>");
			msg = StringCtrl.activateURL(msg, "linkPage", "dtMsgViewWin");
		}
		return msg;
	}

	public boolean mustCreateDevis() {
		return false;
	}

	/**
	 * On ne traite pas les informations financières
	 */
	public boolean shouldProcessDataInfin() {
		return false;
	}

	/**
	 * @see A_CreationSwapView#hasErrors()
	 */
	@Override
	public boolean hasErrors() {
		return false;
	}

	/**
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_MESSAGE_ID;
	}

	/**
	 * @see A_CreationSwapView#destinationLolfCtrl()
	 */
	@Override
	public A_ListeDestinationLolfCtrl getDestinationLolfCtrl() {
		return null;
	}

}