import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;

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
 * Classe assurant l'interface entre PIE et une page sollicitant une
 * modification sur la liste d'articles d'un devis.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class DTPrestaGateway {

	// exemple la Hashtable datasDevis
	// {persId=3065, devOrdre=3481, devDateDevis=2006-01-30 14:37:10 Etc/GMT,
	// ctcOrdre=1, pourcentRemiseGlobal=0.00,
	// devMontantTTC=710.00, catOrdre=37, devLibelle=Devis DT-Repro #1938,
	// fouOrdrePrest=52421, devMontantHT=710.00,
	// dstCode=103, prestOrgOrdre=105323, fouPersNomPrenom=Centre De Ressources
	// Informatiques , cStructure=25,
	// devisLignes=[Ljava.lang.Object;@1485542, fouOrdre=50928,
	// devMontantTVA=0.00}

	/** donnees brutes du devis * */
	private Hashtable datasDevis;

	/** Liste des articles possibles **/
	private NSArray articlesCatalogue;

	/** Lignes du devis lues dans l'interface **/
	private NSArray lignes;
	private BigDecimal sommeTTCDevis;

	/** gestion des erreurs * */
	private String errorMessage;
	private DTPrestaBusWeb pieBus;

	// on supprime ces infos car ca ne marche plus si le devis est modifie dans
	// PIE (contact)
	private Integer indPersId;
	private Integer prestId;

	// private Integer structFouOrdre;
	private Integer catId;

	// les infos budgetaires existantes du devis
	/** ligne budgetaire */
	private Integer existingOrgId;
	/** type de credit */
	private String existingTcdCode;

	/**
	 * 
	 * @param aPieBus
	 *          une instance de DTPrestaBusWeb
	 * 
	 * @param aPrestId
	 *          le numero du devis concerne
	 * 
	 * @param anIndPersId
	 *          le persId de la personne qui a cree le devis
	 * 
	 * @param aStructFouOrdre
	 *          le persId de la structure de facturation de la personne qui a cree
	 *          le devis
	 * 
	 * @param catId
	 *          le catalogue surlequel le devis se fait
	 * 
	 * @param utlOrdreDemandeur
	 *          l'utl ordre du demandeur
	 * 
	 */
	public DTPrestaGateway(DTPrestaBusWeb aPieBus, Integer aPrestId,
			Integer anIndPersId, Integer aStructFouOrdre, Integer aCatId) {
		super();
		pieBus = aPieBus;
		prestId = aPrestId;
		indPersId = anIndPersId;
		// structFouOrdre = aStructFouOrdre;
		catId = aCatId;
	}

	/**
	 * Recuperer le contenu du devis a jour du devis via WebService. Recuperer la
	 * liste des articles disponibles. depuis l'application PIE.
	 * 
	 * Si une erreur survient le traitement est stoppe
	 * 
	 * @throws Exception
	 */
	public void getOriginalDevisFromPie() throws Exception {
		datasDevis = pieBus.inspectDevis(prestId);
		if (pieBus.hasError()) {
			errorMessage = pieBus.errorMessage();
			throw new Exception(errorMessage());
		}
		extractAllFields();
		articlesCatalogue = nsarrayHasMapToNSArrayHashTable(
				pieBus.getCatalogueArticles(catId));
	}

	/**
	 * Analyser et stocker les donnees du devis dans les variables de la classe
	 * pour traitement.
	 */
	private void extractAllFields() {
		extractLignesDevis();
		extractDatasBudgetaires();
	}

	public String errorMessage() {
		return errorMessage;
	}

	/**
	 * Teste s'il y a eu des erreurs.
	 */
	public boolean hasErrors() {
		return !StringCtrl.isEmpty(errorMessage);
	}

	/**
	 * Re-construire les lignes du devis a partir de son contenu.
	 */
	private void extractLignesDevis() {
		lignes = new NSArray();
		NSArray objLignes = (NSArray) datasDevis.get(
				DTPrestaServicesConst.FieldDevisPrestationLignesKey);
		if (objLignes != null) {
			try {
				lignes = getFormattedLignes(objLignes);
			} catch (Exception e) {
				errorMessage =
						"Impossible de recuperer les lignes du devis :\n" +
								e.getMessage();
			}
		}
	}

	/**
	 * Recuperer le Total TTC du devis et si besoin, verifier qu'il y a assez de
	 * disponible sur la ligne budgetaire.
	 */
	private void extractDatasBudgetaires() {
		sommeTTCDevis = (BigDecimal) datasDevis.get(DTPrestaServicesConst.FieldDevisMontantTTCKey);
		existingOrgId = (Integer) datasDevis.get(DTPrestaServicesConst.FieldDevisOrgIdClientKey);
		existingTcdCode = (String) datasDevis.get(DTPrestaServicesConst.FieldDevisTcdCodeClientKey);
	}

	/**
	 * Convertit un tableau d'objets Map representant les lignes du devis en
	 * NSArray d'objets Hashtable. Les objets crees seront des couples (cartOrdre,
	 * quantite)
	 * 
	 * @param objs
	 *          Tableau de Map
	 */
	public NSArray getFormattedLignes(NSArray objs) throws Exception {
		NSArray tmpres = new NSArray();
		for (int i = 0; i < objs.count(); i++) {
			NSDictionary object = (NSDictionary) objs.objectAtIndex(i);
			// creer le couple (catId, quantite)
			Integer caarId = (Integer) object.valueForKey(DTPrestaServicesConst.FieldLigneDevisCaarIdIdKey);
			BigDecimal nbArticles = (BigDecimal) object.valueForKey(DTPrestaServicesConst.FieldLigneDevisNbArticlesKey);
			Hashtable table = new Hashtable();
			table.put(DTPrestaServicesConst.DevisArticleIDKeyNew, caarId);
			table.put(DTPrestaServicesConst.DevisNbArticlesKey, new BigDecimal(nbArticles.intValue()));
			tmpres = tmpres.arrayByAddingObject(table);
		}
		return tmpres;
	}

	/**
	 * Convertit un tableau d'objets HasMap representant les lignes du devis en
	 * NSArray d'objets Hashtable.
	 * 
	 * @param objs
	 *          Tableau de Map
	 */
	private NSArray nsarrayHasMapToNSArrayHashTable(NSArray objs) {
		NSMutableArray tmpres = new NSMutableArray();
		for (int i = 0; i < objs.count(); i++) {
			NSDictionary object = (NSDictionary) objs.objectAtIndex(i);
			tmpres.addObject(object.hashtable());
		}
		return tmpres.immutableClone();
	}

	/** Lignes du devis **/

	// lignes du devis
	public NSArray getLignes() {
		return lignes;
	}

	public BigDecimal getSommeTTCDevis() {
		return sommeTTCDevis;
	}

	/**
	 * Les lignes sous forme de Vector pour utilisation dans les WebServices.
	 */
	public Vector getVectorLignes() {
		Vector vect = new Vector();
		for (int i = 0; i < getLignes().count(); i++) {
			vect.add(getLignes().objectAtIndex(i));
		}
		return vect;
	}

	/** Ligne du devis en cours d'analyse. **/
	// de la forme Hashtable={nbArticles=100, cartOrdre=185}
	private Hashtable ligneDevisItem;

	public void setLigneDevisItem(Hashtable value) {
		// effacer le cache de l'article associe
		ligneDevisItemArticle = null;
		ligneDevisItem = value;
	}

	// cache de l'article associe a la ligne du devis
	private Hashtable ligneDevisItemArticle;

	private Hashtable ligneDevisItemArticle() {
		if (ligneDevisItemArticle == null) {
			ligneDevisItemArticle = findArticleInArticlesCatalogue(ligneDevisItemCartOrdre());
		}
		return ligneDevisItemArticle;
	}

	// accesseur aux valeurs provenant pour chaque ligne du devis
	public Integer ligneDevisItemCartOrdre() {
		return (Integer) ligneDevisItem.get(DTPrestaServicesConst.DevisArticleIDKeyNew);
	}

	public String ligneDevisItemReference() {
		return (String) ligneDevisItemArticle().get(DTPrestaServicesConst.FieldCartReferenceKey);
	}

	public String ligneDevisItemDescription() {
		return (String) ligneDevisItemArticle().get(DTPrestaServicesConst.FieldCartDescriptionKey);
	}

	public BigDecimal ligneDevisItemNbArticles() {
		return (BigDecimal) ligneDevisItem.get(DTPrestaServicesConst.DevisNbArticlesKey);
	}

	public void setLigneDevisItemNbArticles(BigDecimal value) {
		ligneDevisItem.put(DTPrestaServicesConst.DevisNbArticlesKey, value);
	}

	public BigDecimal ligneDevisItemArtHT() {
		return (BigDecimal) ligneDevisItemArticle().get(DTPrestaServicesConst.FieldCartMontantHTKey);
	}

	public BigDecimal ligneDevisItemTotal() {
		return (BigDecimal) ligneDevisItemArtHT().multiply(new BigDecimal(ligneDevisItemNbArticles().doubleValue()));
	}

	/** Les articles du catalogue **/
	public NSArray catalogueArticles() {
		return articlesCatalogue;
	}

	/** Ligne courante d'article (du catalogue), en cours d'analyse. **/

	// exemple d'article (Hashtable)
	// {cartOrdre=194, tauTaux=0.00, cartpType=A, cartDescription=Couleur A3
	// Recto, ctcOrdre=1,
	// cartValide=V, tauCode=0, catOrdre=37, cartHt=0.500, cartInvisible=N,
	// cartReference=CL-A3-R, pcoNum=70681}
	public Hashtable catalogueArticleItem;

	public void setCatalogueArticleItem(Hashtable value) {
		catalogueArticleItem = value;
	}

	protected Hashtable catalogueArticleSelection;

	// accesseur aux valeurs provenant pour chaque article du catalogue
	public Integer catalogueArticleItemCartOrdre() {
		return (Integer) catalogueArticleItem.get(DTPrestaServicesConst.FieldDevisCaarIdKey);
	}

	public String catalogueArticleItemReference() {
		return (String) catalogueArticleItem.get(DTPrestaServicesConst.FieldCartReferenceKey);
	}

	public String catalogueArticleItemDescription() {
		return (String) catalogueArticleItem.get(DTPrestaServicesConst.FieldCartDescriptionKey);
	}

	public Integer catalogueArticleSelectionCartOrdre() {
		return (Integer) catalogueArticleSelection.get(DTPrestaServicesConst.FieldDevisCaarIdKey);
	}

	/**
	 * Synchroniser le contenu du devis PIE avec les lignes decrites dans
	 * <code>getVectorLignes()</code>.
	 */
	public void commitChanges() {
		// supprimer les lignes du devis et ajouter les nouvelles lignes
		pieBus.updateDevis(prestId, getVectorLignes());
	}

	/**
	 * Estimer le nouveau devis.
	 */
	public void estimer() {
		sommeTTCDevis = new BigDecimal(pieBus.estimeTTCDevis(getVectorLignes()).doubleValue());
	}

	/**
	 * Valider toute la prestation : - client - prestataire - fermeture prestation
	 */
	public void validerToutDevis() throws Exception {
		pieBus.validerToutDevis(prestId, indPersId);
		if (pieBus.hasError()) {
			errorMessage = pieBus.errorMessage();
			throw new Exception(errorMessage());
		}
	}

	/**
	 * Indique si la ligne budgetaire associee a suffisament de disponible pour le
	 * total du devis.
	 */
	public boolean isEnoughDisponible() {
		if (existingOrgId != null && existingTcdCode != null) {
			estimer();
			Double disponible = pieBus.sommeLigneBud(existingOrgId, existingTcdCode);
			return (disponible.doubleValue() >= sommeTTCDevis.doubleValue());
		} else {
			return true;
		}

	}

	// manipulation du devis

	/**
	 * Supprimer la ligne en cours du devis
	 */
	public void removeLigneDevisItem() {
		// retrouver l'enregistrement dans le tableau
		Hashtable ligneToDelete = findArticleInDevis(ligneDevisItemCartOrdre());
		// suppression
		if (ligneToDelete != null) {
			NSMutableArray lignesMutable = new NSMutableArray(getLignes());
			lignesMutable.removeIdenticalObject(ligneToDelete);
			lignes = lignesMutable.immutableClone();
			// rafraichir le total
			estimer();
		}
	}

	/**
	 * Ajouter l'article en cours au devis Retourne <code>true</code> si tout
	 * s'est bien passe.
	 * 
	 * @param count
	 *          nombre d'articles
	 */
	public boolean addArticleToDevis(BigDecimal count) {
		// quantite valide et pas deja dans le devis
		if (count != null && count.intValue() > 0 &&
				findArticleInDevis(catalogueArticleSelectionCartOrdre()) == null) {
			Hashtable newArticle = new Hashtable();
			// rajouter le couple (cartOrdre, quantite)
			newArticle.put(DTPrestaServicesConst.DevisArticleIDKeyNew, catalogueArticleSelectionCartOrdre());
			newArticle.put(DTPrestaServicesConst.DevisNbArticlesKey, count);
			lignes = lignes.arrayByAddingObject(newArticle);
			// rafraichir le total
			estimer();
			return true;
		}
		return false;
	}

	/**
	 * Retourne l'enregistrement designe par <code>cartOrdre</code> parmi la liste
	 * des lignes du devis. Retourne null si l'article n'existe pas dans le devis.
	 * 
	 * @param catId
	 *          L'identifiant de l'article
	 */
	private Hashtable findArticleInDevis(Integer catId) {
		Hashtable result = null;
		int i = 0;
		while (i < getLignes().count() && result == null) {
			Hashtable currentLigne = (Hashtable) getLignes().objectAtIndex(i);
			if (((Integer) currentLigne.get(DTPrestaServicesConst.DevisArticleIDKeyNew)).intValue() == catId.intValue()) {
				result = currentLigne;
			}
			i++;
		}
		return result;
	}

	/**
	 * Retourne l'enregistrement designe par <code>cartOrdre</code> parmi la liste
	 * connus. Retourne null si l'article n'existe pas .
	 * 
	 * @param cartOrdre
	 *          L'identifiant de l'article
	 */
	private Hashtable findArticleInArticlesCatalogue(Integer cartOrdre) {
		Hashtable result = null;
		int i = 0;
		while (i < articlesCatalogue.count() && result == null) {
			Hashtable currentArticle = (Hashtable) articlesCatalogue.objectAtIndex(i);
			if (((Integer) currentArticle.get(DTPrestaServicesConst.DevisArticleIDKeyNew)).intValue() == cartOrdre.intValue()) {
				result = currentArticle;
			}
			i++;
		}
		return result;
	}

	/**
	 * Transformer le contenu du devis en texte, reutilisable pour le traitement
	 * de la DT.
	 * 
	 * Si le tableau getLignes() est vide, un erreur est survenue lors de la
	 * lecture du devis dans PIE
	 */
	public String getLignesTexte() {
		StringBuffer txt = new StringBuffer();
		for (int i = 0; getLignes() != null && i < getLignes().count(); i++) {
			Hashtable currentLigne = (Hashtable) getLignes().objectAtIndex(i);
			int nbArticles = ((BigDecimal) currentLigne.
					get(DTPrestaServicesConst.DevisNbArticlesKey)).intValue();
			txt.append(nbArticles).append(" ");
			Integer cartOrdre = (Integer) currentLigne.
					get(DTPrestaServicesConst.DevisArticleIDKeyNew);
			Hashtable article = findArticleInArticlesCatalogue(cartOrdre);
			if (article != null) {
				txt.append(article.get(DTPrestaServicesConst.FieldCartDescriptionKey));
			} else {
				txt.append("<article inconnu>");
			}
			txt.append("\n");
		}
		return txt.toString();
	}

}
