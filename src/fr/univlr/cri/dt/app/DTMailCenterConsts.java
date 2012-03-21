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
package fr.univlr.cri.dt.app;

/**
 * Definition de toutes les constantes utilisees par la classe
 * <code>DTMailCenter</code>
 * 
 * @author ctarade
 */
public interface DTMailCenterConsts {

  /** l'adresse email de sam associee au plugin sam-dt */
  public final static String VAR_SAM_MAIL					= "%APP_SAM_MAIL%";
  /** le libelle de la cle pere pour un envoi de message (intOrdre, traOrdre ou disOrdre)*/
  public final static String VAR_PARENT_KEY				= "%VAR_PARENT_KEY%";
  /** la valeur de la cle pere pour un envoi de message */
  public final static String VAR_PARENT_KEY_VALUE = "%VAR_PARENT_KEY_VALUE%";
  /** le patron pour l'ajout de la ligne de contact via SAM en haut du mail (obligatoire)*/
	public final static String PATTERN_SAM_DISCUSSION_TOP = 
		"Merci de donner votre réponse via le lien " + VAR_SAM_MAIL + "?subject=" + VAR_PARENT_KEY + "$" + VAR_PARENT_KEY_VALUE + "\n" +
		"\n";
  /** le patron pour l'ajout de la ligne de contact via SAM en bas du mail (facultatif)*/
	public final static String PATTERN_SAM_DISCUSSION_BOTTOM = 
		"--- Commentaires ---\n" +
		"  Vous pouvez ajouter un commentaire en envoyant un e-mail a cette adresse :\n" +
		"  " + VAR_SAM_MAIL + "?subject=" + VAR_PARENT_KEY + "$" + VAR_PARENT_KEY_VALUE;
}
