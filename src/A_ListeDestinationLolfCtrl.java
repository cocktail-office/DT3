import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/*
 * Copyright Université de La Rochelle 2011
 *
 * Ce logiciel est un programme informatique servant à gérer les demandes
 * d'utilisateurs auprès d'un service.
 * 
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.

 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant 
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */

/**
 * @author ctarade
 * 
 */
public abstract class A_ListeDestinationLolfCtrl
		extends A_DTWebComponentCtrl {

	/**
	 * La constante indiquant une entre "aucune destination" (vide) dans la liste
	 * des destinations.
	 */
	protected static DTDestinListItem EmptyDestinItem;

	// Les destinations
	public NSArray<DTDestinListItem> listDestin;
	public DTDestinListItem listDestinItem;
	private DTDestinListItem listDestinSelected;

	// message d'erreur
	public boolean errorDstCode;

	/**
	 * @param aSession
	 */
	public A_ListeDestinationLolfCtrl(Session aSession) {
		super(aSession);
		clearErrors();
		fillDestin();
	}

	/**
	 * Alimenter la liste des destinations
	 */
	private void fillDestin() {

		if (shouldInitDestinListRepro()) {
			listDestin = initListDestinRepro(fillAllDestinOnReset());
		} else {
			listDestin = initListDestin(fillAllDestinOnReset());
		}
	}

	/**
	 * 
	 */
	public void clearErrors() {
		errorDstCode = false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		return errorDstCode;
	}

	/**
	 * Initialise la liste des codes de destination. Si <code>fillAll</code> est
	 * <i>true</i>, alors initialise toute la liste des destinations connues.
	 */
	private NSArray<DTDestinListItem> initListDestin(boolean fillAll) {
		NSMutableArray<DTDestinListItem> destins = new NSMutableArray<DTDestinListItem>();
		// Si la selection de destination optionnelle
		// alors on ajoute une entre qui permet ne rien selectionner
		if (dtSession().checkJefyDestinSupport() == 1) {
			if (EmptyDestinItem == null) {
				EmptyDestinItem = new DTDestinListItem("<aucune action Lolf>", new Integer(-1));
			}
			destins.addObject(EmptyDestinItem);
		}
		// Ensuite, on rempli le resultat par les vraies destinations
		// si le choix des destinations est active et le Jefyco est disponible
		if (dtSession().checkJefyDestinSupport() != 0) {
			/* if (fillAll) */
			destins.addObjectsFromArray(
						jefyBus().destinRecToListItems(jefyBus().findAllDestin()));
			/*
			 * else destins.addObjectsFromArray( jefyBus().destinRecToListItems(
			 * jefyBus().findDestin(newDtLBudOrdre(), newDtTcdCode())));
			 */
		}
		return destins;
	}

	/**
	 * Initialise la liste des destinations. Cette methode verifie si le choix de
	 * la ligne budgetaire est obligatoire, auquel cas le choix de la destination
	 * l'est aussi.
	 * 
	 * @see A_CreationSwapView#initListDestin(boolean)
	 */
	private NSArray<DTDestinListItem> initListDestinRepro(boolean fillAll) {
		NSMutableArray<DTDestinListItem> liste = (NSMutableArray<DTDestinListItem>) initListDestin(fillAll);
		if (dtSession().checkJefyLBudSupport() == 2) {
			liste.removeObject(EmptyDestinItem);
		}
		return liste;
	}

	// on ignore le setter dans certains cas pour eviter que l'interface
	// affecte null a tort
	private boolean shouldIgnoreSetterListDestinSelected = false;

	/**
	 * Retroune l'entree de la liste des destinations <code>listDestin</code>
	 * ayant le code <code>lolfId</code>. Retourne <em>null</em> si la liste ne
	 * contient aucun element avec le code donne.
	 */
	protected DTDestinListItem findItemToSelect(NSArray<DTDestinListItem> listDestin, Number lolfId) {
		DTDestinListItem anItem = null;
		// [LRAppTasks] : @CktlLog.trace(@"ListeDestin : "+listDestin);
		// [LRAppTasks] : @CktlLog.trace(@"DestinCode : "+lolfId);
		if (!(isEmptyArray(listDestin) || lolfId == null)) {
			for (int i = 0; i < listDestin.count(); i++) {
				anItem = (DTDestinListItem) listDestin.objectAtIndex(i);
				if (anItem.lolfId().intValue() == lolfId.intValue())
					break;
			}
			if (anItem == null)
				anItem = (DTDestinListItem) listDestin.objectAtIndex(0);
		}
		return anItem;
	}

	/**
	 * Teste si l'element <code>item</code> correspond a une entre "aucune
	 * destination" (entree vide).
	 */
	protected boolean isEmptyDestin(DTDestinListItem item) {
		return (item == null ||
						item == EmptyDestinItem || item.lolfId().intValue() == -1);
	}

	/**
	 * Teste si la liste donnees est vide. Elle l'est si la raference est
	 * <em>null</em> ou si la liste n'a aucun element.
	 */
	protected boolean isEmptyArray(NSArray<?> anArray) {
		return ((anArray == null) || (anArray.count() == 0));
	}

	/**
	 * Teste si la selection des destinations doit etre utilsee.
	 */
	public boolean useDestins() {
		return (dtSession().checkJefyDestinSupport() > 0);
	}

	/**
	 * Teste si la liste des codes de destinations n'est pas vide.
	 */
	public boolean hasDestin() {
		return ((listDestin != null) && (listDestin.count() > 0));
	}

	/**
	 * Indique le paramètre lors de l'appel à {@link #initListDestin(boolean)}
	 */
	public abstract boolean fillAllDestinOnReset();

	private Number lolfId;

	/**
	 * Retourne le code de destination associe a la demande. Est appelé par la
	 * méthode {@link #findItemToSelect(NSArray, Number)} par
	 * {@link #resetDestin()}
	 * 
	 * @return
	 */
	public final Number lolfId() {
		return lolfId;
	}

	public final void setLolfId(Number value) {
		lolfId = value;
		shouldIgnoreSetterListDestinSelected = true;
		if (lolfId != null) {
			listDestinSelected = findItemToSelect(listDestin, lolfId);
		} else {
			listDestinSelected = null;
		}
	}

	/**
	 * Effectue la validation du formulaire. S'il y a une erreur, celle ci est
	 * remontée via {@link #setMainError(String)} et {@link #hasErrors()}
	 * retournera <code>true</code>
	 */
	public void validate() {
		// Le code de destination doit etre selectionne
		if (isEmptyDestin(getListDestinSelected()) &&
				(dtSession().checkJefyDestinSupport() == 2)) {
			errorDstCode = true;
			setMainError("Le code d'action Lolf est absent");
		}
	}

	/**
	 * 
	 * @return
	 */
	private DTJefyBus jefyBus() {
		return dtSession().dataCenter().jefyBus();
	}

	/**
	 * Indique s'il faut appeler {@link #initListDestinRepro(boolean)} ou
	 * {@link #initListDestin(boolean)} lors de l'appel à la méthode
	 * {@link #resetDestin()}
	 * 
	 * @return
	 */
	public abstract boolean shouldInitDestinListRepro();

	public final DTDestinListItem getListDestinSelected() {
		return listDestinSelected;
	}

	public final void setListDestinSelected(DTDestinListItem value) {
		if (shouldIgnoreSetterListDestinSelected == false) {
			listDestinSelected = value;
			if (listDestinSelected != null) {
				lolfId = listDestinSelected.lolfId();
			} else {
				lolfId = null;
			}
		}
		if (shouldIgnoreSetterListDestinSelected) {
			shouldIgnoreSetterListDestinSelected = false;
		}
	}

}
