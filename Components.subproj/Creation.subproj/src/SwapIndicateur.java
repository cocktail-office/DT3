/*
 * Copyright Universit� de La Rochelle 2008
 *
 * Ce logiciel est un programme informatique servant � g�rer les demandes
 * d'utilisateurs aupr�s d'un service.
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

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Interface de saisie d'une demande d'indicateur
 * 
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class SwapIndicateur extends SwapDefault {

	// les liste des titres et des champs
	public NSArray titleList;
	public NSMutableArray fieldList;
	
	// items
	public String titleItem;
	
  // erreurs
  public boolean errorEmpty;
	
	// index
	public int titleIndex;
	
	// le nombre d'items
	private final static int TOTAL_ITEM = 9;
	
	// les titres de chaque item
	private final static String TITLE_1 = "Intitulé de l'indicateur";
	private final static String TITLE_2 = "Utilisation (contrat ou pilotge interne) et par qui";
	private final static String TITLE_3 = "Formule de calcul en précisant les champs informatiques de données utilisées (ex: compte 70)";
	private final static String TITLE_4 = "Dates d'observation (ex: trimestrielle ...)";
	private final static String TITLE_5 = "Source(s) d'information (collecte) (ex: GFC)";
	private final static String TITLE_6 = "Traitement de la source au tableau de bord(ex: module convention)";
	private final static String TITLE_7 = "Forme du tableau de bord (support) et fréquence de production";
	private final static String TITLE_8 = "Lié à quels indicateurs aval et amont";
	private final static String TITLE_9 = "Commentaires";
	
	public SwapIndicateur(WOContext context) {
		super(context);
	}
  
  public boolean hasErrors() {
    return super.hasErrors() || errorEmpty;
  }
  
  public void clearViewErrors() {
    super.clearViewErrors();
    errorEmpty = false;
  }
  
	/**
	 * Initialiser le tableau
	 */
	public void initView() {
		super.initView();
		// les titres
		titleList = new NSArray(new String[]{TITLE_1,TITLE_2,TITLE_3,TITLE_4,TITLE_5,TITLE_6,TITLE_7,TITLE_8,TITLE_9});
		// les champs
		fieldList = new NSMutableArray();
		for (int i = 0; i < TOTAL_ITEM; i++) {
			fieldList.addObject(StringCtrl.emptyString());
		}
	}
	
  public boolean fillDataDictionary() {
  	// pour que la validation fonctionne via super.fillDataDictionary(), il faut un motif "bidon"
  	intMotif = "motif généré automatiquement";
    super.fillDataDictionary();
    // on s'assure qu'il y a au moins un champ de rempli
    boolean isOneFieldFilled = false;
    for (int i = 0; i < fieldList.count(); i++) {
    	String field = (String) fieldList.objectAtIndex(i);
    	if (!StringCtrl.isEmpty(field)) {
    		isOneFieldFilled = true;
    		break;
    	}
		}
    errorEmpty = !isOneFieldFilled;
    if (errorEmpty) {
    	return false;
    }
    
    String intMotif = "";
    // on ne remplit que les champ remplis
    for (int i = 0; i < fieldList.count(); i++) {
    	String field = (String) fieldList.objectAtIndex(i);
    	if (!StringCtrl.isEmpty(field)) {
    		intMotif += titleList.objectAtIndex(i) + " :\n" + field + "\n\n";
    	}
		}
    
    // on test si ca rentre 
    if (!dtSession().dtDataBus().checkForMaxSize(
    		"Intervention", "intMotif", intMotif, "Motif", 0, true, true)) {
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
  
  // setters
  
  /**
   * La mise a jour d'un item de champ va venir modifier
   * le contenu du tableau <code>fieldList</code>
   */
  public void setFieldItem(String value) {
  	if (!StringCtrl.isEmpty(value)) {
  		fieldList.replaceObjectAtIndex(value, titleIndex);
  	} else {
  		fieldList.replaceObjectAtIndex(StringCtrl.emptyString(), titleIndex);	
  	}
  }
  
  // getters
  
  public String fieldItem() {
  	return (String) fieldList.objectAtIndex(titleIndex);
  }

	/* (non-Javadoc)
	 * @see org.cocktail.dt.server.components.swap.I_Swap#swapViewId()
	 */
	public int swapViewId() {
		return SWAP_VIEW_INDICATEUR_ID;
	}
}