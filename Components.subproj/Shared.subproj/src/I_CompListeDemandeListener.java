import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WODisplayGroup;

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
 * Classe qui permet de piloter le composant CompListeDemande, a savoir une
 * liste de demande de travaux
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public interface I_CompListeDemandeListener {

	/**
	 * Le DG contenant les DTs a afficher
	 */
	public WODisplayGroup interventionDisplayGroup();

	/**
	 * Lancer la recherche des DT et mettre a jour le display group
	 */
	public WOComponent doFetchDisplayGroup();

	/**
	 * La page "maitre", depuis laquelle est appelee le composant
	 */
	public A_PageUsingListeDemande caller();

	/**
	 * Le mode d'utilisation : soit en intervenant, soit en demandeur
	 */
	public int modeUtilisateur();

	/**
	 * Les colonnes a afficher
	 */
	public boolean showColumnIntervenants();

	public boolean showColumnSupprimer();

	public boolean showColumnValider();

	public boolean showColumnAffecter();

	public boolean showColumnTraiter();

	public boolean showColumnDiscussion();

	public boolean showColumnAjoutTraitement();

	public boolean showColumnEtat();

	/**
	 * La liste permet-elle de masquer les demandes
	 */
	public boolean useInterventionMasquee();

	/**
	 * La liste affiche-t-elle aussi les intervention masquees. N'est utilise que
	 * si <code>useInterventionMasquee()</code> retourne <code>true</code>.
	 */
	public boolean showInterventionMasquee();

	/**
	 * Un message d'avertissment a afficher.
	 */
	public String warnMessage();

	/**
	 * Un complement d'information situe a cote du nombre total de DTs trouvees.
	 */
	public String strResultCommentSuffix();

	/**
	 * Autoriser l'ajout de DT dans le panier
	 */
	public boolean showColumnAjouterPanier();

	/**
	 * Autoriser la suppression de DT du panier
	 */
	public boolean showColumnSupprimerPanier();
}
