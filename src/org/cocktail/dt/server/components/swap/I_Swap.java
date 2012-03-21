package org.cocktail.dt.server.components.swap;

import com.webobjects.foundation.NSArray;

/**
 * La liste des definition des vues DT (Swap)
 * 
 * @author ctarade
 */

public interface I_Swap {

	// l'ordre doit être le même que ce qu'il y a dans
	// le tableau SWAP_LIST
	public final static int SWAP_VIEW_DEFAULT_ID = 0;
	public final static int SWAP_VIEW_COMPTE_ID = 1;
	public final static int SWAP_VIEW_REPRO_ID = 2;
	public final static int SWAP_VIEW_MESSAGE_ID = 3;
	public final static int SWAP_VIEW_INSTALL_COMPOSANT_ID = 4;
	public final static int SWAP_VIEW_INSTALL_MATERIEL_ID = 5;
	public final static int SWAP_VIEW_INSTALL_POSTE_COMPLET_ID = 6;
	public final static int SWAP_VIEW_INDICATEUR_ID = 7;
	public final static int SWAP_VIEW_REFERENT_FONCTIONNEL_ID = 8;
	public final static int SWAP_VIEW_AUT_RECR_ETUDIANT_ID = 9;
	public final static int SWAP_VIEW_SUPPORT_HANDICAP_ID = 10;
	public final static int SWAP_VIEW_INSTALL_POSTE_COMPLET2_ID = 11;
	public final static int SWAP_VIEW_CELLULE_GEOMATIQUE_ID = 12;
	public final static int SWAP_VIEW_LOGICIEL_ID = 13;

	public final static String SWAP_GENERAL = "Générale (par défaut)";
	public final static String SWAP_COMPTE = "Création Compte";
	public final static String SWAP_REPRO = "Reprographie";
	public final static String SWAP_GENERIC_MSG = "Message générique";
	public final static String SWAP_INST_COMP = "Installation composant informatique ULR";
	public final static String SWAP_INST_MAT = "Installation matériel informatique ULR";
	public final static String SWAP_INST_POS = "Installation poste informatique complet ULR";
	public final static String SWAP_INFOCENTRE = "Infocentre";
	public final static String SWAP_REFERENT = "Referent Fonctionnel";
	public final static String SWAP_AUT_REC_ETU = "Autorisation recrutement etudiant ULR";
	public final static String SWAP_SUP_HAND = "Support handicap ULR";
	public final static String SWAP_INST_POS2 = "Installation poste informatique complet ULR (v2)";
	public final static String SWAP_CEL_GEO = "Cellule geomatique ULR";
	public final static String SWAP_LOGICIEL = "Logiciel";

	// le swap par défaut lorsque l'on crée de nouveaux enregistremetns
	public final static String NOUVELLE_ACTIVITE_SWAP_DEFAUT = SWAP_GENERAL;

	// liste des swap view
	public final static NSArray<String> SWAP_LIST = new NSArray<String>(new String[] {
			SWAP_GENERAL, SWAP_COMPTE, SWAP_REPRO,
			SWAP_GENERIC_MSG, SWAP_INST_COMP, SWAP_INST_MAT,
			SWAP_INST_POS, SWAP_INFOCENTRE, SWAP_REFERENT,
			SWAP_AUT_REC_ETU, SWAP_SUP_HAND, SWAP_INST_POS2,
			SWAP_CEL_GEO, SWAP_LOGICIEL });

	/**
	 * L'identifiant de la swap
	 * 
	 * @return
	 */
	public int swapViewId();
}
