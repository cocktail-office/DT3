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
package fr.univlr.cri.dt.app;

/**
 * Rassemble toutes les constantes de l'application. Distinctes de la classe
 * application car cette dernière n'est pas dans un package et donc non visible
 * par les classes qui ne sont pas le default package ...
 * 
 * @author ctarade
 */
public interface I_ApplicationConsts {

	// Repro
	public final static String REPRO_UTL_ORDRE_KEY = "REPRO_UTL_ORDRE";
	public final static String REPRO_CAT_ID_KEY = "REPRO_CAT_ID";

	// écran de commande de matériel
	public final static String VALEUR_SURCOUT_POSTE_NON_RESTITUE_KEY = "VALEUR_SURCOUT_POSTE_NON_RESTITUE";
	public final static String PHRASE_SURCOUT_POSTE_NON_RESTITUE_KEY = "PHRASE_SURCOUT_POSTE_NON_RESTITUE";
	public final static String PHRASE_SURCOUT_POSTE_NON_RESTITUE_CLOTURE_KEY = "PHRASE_SURCOUT_POSTE_NON_RESTITUE_CLOTURE";
	public final static String PHRASE_INCOHERENCE_DEMANDE_NB_POSTE_KEY = "PHRASE_INCOHERENCE_DEMANDE_NB_POSTE";

	// GLPI
	public final static String GLPI_WS_URL_KEY = "GLPI_WS_URL";
	public final static String GLPI_WS_USER_KEY = "GLPI_WS_USER";
	public final static String GLPI_WS_PASSWORD_KEY = "GLPI_WS_PASSWORD";
	public final static String GLPI_FRONT_COMPUTER_URL_KEY = "GLPI_FRONT_COMPUTER_URL";

}
