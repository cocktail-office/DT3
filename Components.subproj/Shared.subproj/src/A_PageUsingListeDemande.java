import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.CktlSort;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;

/*
 * Copyright Universit� de La Rochelle 2006
 *
 * ctarade@univ-lr.fr
 *
 * Ce logiciel est un programme informatique servant � g�rer les comptes
 * informatiques des utilisateurs. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

/**
 * Page g�n�rique qui rassemble l'ensemble des traitements communs a des pages
 * utilisant un composant CompListeDemande
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public abstract class A_PageUsingListeDemande
		extends DTWebPage
		implements InspecteurDTReferer {

	// le display group contenant toutes les DT a afficher
	private WODisplayGroup interventionDisplayGroup;

	// configuration de la recherche
	private RechercheDTConfig configEnCours;

	// type de tri
	protected String triSelected;

	// ordre de tri
	private int ordreSelected;

	// etats
	public NSArray<String> etatsLibelles;
	public String etatLibelleItem;

	/** faut-il recharger les donnees du display group */
	private boolean shouldRefresh;

	// le controleur de la liste des demandes
	public I_CompListeDemandeListener listener;

	// temoin de depassement du nombre max d'objets a retourner
	public boolean limitFetchExceeded;

	/** nombre de DTs affichées */
	private Integer numberOfObjectsPerBatch;

	public A_PageUsingListeDemande(WOContext context) {
		super(context);
		initComponent();
	}

	/**
	 * On ajout le code javascript permettant d'ouvrir le popup d'impression
	 * (appel via la methode <code>afficheImpression()</code>)
	 */
	public void appendToResponse(WOResponse response, WOContext context) {
		super.appendToResponse(response, context);
		// script pour l'ouverture de la popup d'impression
		addLocalJScript(response, "js/popup.js");
	}

	/**
	 * Initialisation de la page. Les donnees sont recuperees depuis la DB et
	 * formulaire remplit avec les valeurs par defaut de l'utilisateur.
	 */
	protected void initComponent() {

		// ** initialisations generiques **
		int nb = 10;
		if (dtUserInfo().nbIntPerPage() != null) {
			nb = dtUserInfo().nbIntPerPage().intValue();
		}
		setNumberOfObjectsPerBatch(nb);

		ordreSelected = -1;
		triSelected = EOIntervention.TRI_ATTRIBUT_NUMERO;

		// etats
		if (etatsLibelles == null) {
			etatsLibelles = dtDataCenter().etatBus().findLibellesAll();
		}

		// init des prefs utilisateur
		initDefaultConfig();
		//
		setShouldRefresh(false);
	}

	/**
	 * Les initialisations generiques a partir de la structure
	 * <code>RechercheDTConfig</code>
	 */
	public void initFromConfig() {
		setEtatSelectedLibelle(dtDataCenter().etatBus().libelleForEtat(
				configEnCours.etatDtCode()));
	}

	/**
	 * Methode qui initialise l'objet configEnCours
	 */
	protected abstract void initDefaultConfig();

	/**
   *
   */
	protected CktlSort getSortForParams() {
		CktlSort sort = null;

		sort = CktlSort.newSort(triSelected, getOrdreSelected());

		return sort;

	}

	/**
	 * Afficher le gestionnaire d'impression
	 */
	public PageImpression afficherImpression() {
		PageImpression pageImpression = (PageImpression) pageWithName(PageImpression.class.getName());
		NSArray<EOIntervention> interventionsClassees = EOSortOrdering.sortedArrayUsingKeyOrderArray(
				interventionDisplayGroup.allObjects(),
				interventionDisplayGroup.sortOrderings());
		pageImpression.afficherImpression(interventionsClassees);
		return pageImpression;
	}

	// rafraichissement demande

	public void awake() {
		if (shouldRefresh) {
			// dtSession().dataBus().refetchTable(interventionDisplayGroup);
			listener.doFetchDisplayGroup();
			setShouldRefresh(false);
		}
		super.awake();
	}

	public void setShouldRefresh(boolean value) {
		shouldRefresh = value;
	}

	// getters / setter correspondance config

	public String getEtatSelectedLibelle() {
		return dtDataCenter().etatBus().libelleForEtat(configEnCours.etatDtCode());
	}

	public void setEtatSelectedLibelle(String value) {
		configEnCours.setEtatDtCode(dtDataCenter().etatBus().etatForLibelle(value));
	}

	protected RechercheDTConfig configEnCours() {
		return configEnCours;
	}

	public void setConfigEnCours(RechercheDTConfig value) {
		configEnCours = value;
	}

	public final int getOrdreSelected() {
		if (ordreSelected == -1) {
			if (dtUserInfo().isSortIntAscending()) {
				ordreSelected = CktlSort.Ascending;
			} else {
				ordreSelected = CktlSort.Descending;
			}
		}
		return ordreSelected;
	}

	public final void setOrdreSelected(int ordreSelected) {
		this.ordreSelected = ordreSelected;
	}

	public final WODisplayGroup getInterventionDisplayGroup() {
		return interventionDisplayGroup;
	}

	public final void setInterventionDisplayGroup(WODisplayGroup interventionDisplayGroup) {
		this.interventionDisplayGroup = interventionDisplayGroup;
	}

	public final Integer getNumberOfObjectsPerBatch() {
		return numberOfObjectsPerBatch;
	}

	/**
	 * Surcharge pour interdire la mise à <code>null</code> (fait automatiquement
	 * par les setters de l'interface)
	 * 
	 * @param value
	 */
	public final void setNumberOfObjectsPerBatch(Integer value) {
		if (value != null) {
			numberOfObjectsPerBatch = value;
			if (interventionDisplayGroup != null) {
				interventionDisplayGroup.setNumberOfObjectsPerBatch(numberOfObjectsPerBatch);
			}
		}
	}
}
