/*
 * Copyright CRI - Universite de La Rochelle, 1995-2005 
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
package fr.univlr.cri.dt.services.common;

/* =======================================================================
 * L'original de cette classe se trouve dans les sources de l'application
 * Demande de Travaux Web. 
 * ======================================================================= */

/**
 * Les constantes utilisees dans l'implementation de service "prestations"
 * de la demande de travax. Elles indiquent les noms des attributs, des
 * elements des forumulaires HTTP, les valeurs des parametres. Les memes
 * constantes doivent etre utilisees de cote client et serveur.
 * 
 * <p>Cette classe ne contient que les constantes.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTPrestaServicesConst extends DTServicesConst {
	
	
  // Les noms des services et des actions directes
  /** Le nom du service */
  public static final String ServiceName = "DTDirectPrestaServices";
  /** Le nom de l'action : de creation des devis */
  public static final String ActionCreateDevisName = "createDevis";
  /** Le nom de l'action : mise a jour d'un devis */
  public static final String ActionUpdateDevis = "updateDevis";
  /** Le nom de l'action : de montant d'un devis */
  public static final String ActionSommeTTCDevisName = "sommeTTCDevis";
  /** Le nom de l'action : somme disponible sur une ligne budgetaire */
  public static final String ActionSommeDispoName = "sommeLigneBud";
  /** Le nom de l'action : informations sur un devis */
  public static final String ActionInspectDevisName = "inspectDevis";
  /** Le nom de l'action : test de la realisation d'une prestation */
  public static final String ActionPrestaRealiseeName = "prestaRealisee";
  /** Le nom de l'action : suppression/devalidation d'un devis */
  public static final String ActionDeleteDevisName = "deleteDevis";
  /** Le nom de l'action : suppression/devalidation d'un devis sans controle*/
  public static final String ActionDeleteDevisSansControleName = "deleteDevisSansControle";
  /** Le nom de l'action : estimation de la somme d'un devis */
  public static final String ActionEstimeTTCDevisName = "estimeTTCDevis";
  /** Le nom de l'action : test de disponibilite du service Web presta */
  public static final String ActionCheckWebServiceName = "checkWebService";
  /** Le nom de l'action : la liste des etats possibles des devis */
  public static final String ActionAllDevisEtatsName = "allDevisEtats";
  /** Le nom de l'action : recuperation de l'etat du devis donne */
  public static final String ActionEtatForDevisName = "etatForDevis";
  /** Le nom de l'action : la liste des types des credit autorises */
  public static final String ActionTypcredAutorisesName = "typcredAutorises";
  /** Le nom de l'action : vider les lignes du devis en cours */
  public static final String ActionViderLignes = "viderLignesForCurrentDevis";
  /** Le nom de l'action : valider toute la prestation en cours */
  public static final String ActionValiderToutCurrentDevis = "validerToutCurrentDevis";
  /** Le nom de l'action : recuperer tous les articles d'un catalogue */
  public static final String ActionGetCatalogueArticleCatOrdre = "getCatalogueArticles";
  
  
  // Les cles de la description d'un article
  /** Le nom de la cle indiquant le code d'article */
  public static final String DevisArticleIDKeyNew = "caarId";
  /** Le nom de la cle indiquant le nombre d'articles dans un devis */
  public static final String DevisNbArticlesKey = "dligNbArticles";
  /** Le nom de la cle indiquant la liste des lignes du devis */
  public static final String DevisListeLignesKeyNew = "prestaLignes";
  /** Le nom de la cle indiquant le type d'un article */
  public static final String DevisTypeArticleKey = "typeArticle";
  
  // Les types des devis
  /** Le type de devis d'une prestation "interne" */
  public static final Integer TypeDevisInterne = new Integer(1);
  /** Le type de devis d'une prestation "externe et public" */
  public static final Integer TypeDevisExternePublic = new Integer(2);
  /** Le type de devis d'une prestation "externe et prive" */
  public static final Integer TypeDevisExternePrive = new Integer(3);
  
  // Les types d'articles
  /** Type d'article "article" (par defaut) */
  public static final String TypeArticleArticle = "A";
  /** Type d'article "option" */
  public static final String TypeArticleOption = "O";
  /** Type d'article "option" */
  public static final String TypeArticleRemise = "R";
  
  
  // Les noms des cles a utiliser dans un formulaire HTTP/DirectAction
  /** Cle formulaire : le ticket pour recuperer les infos sur l'utilisateur */
  public static final String FormUITicketKey = "uiTicket";
  /** Cle formulaire : le code du type de devis */
  public static final String FormTypeDevisKey = "typeDevis";
  /** Cle formulaire : le libelle de devis */
  public static final String FormDevisLibelleKey = "devisLibelle";
  /** Cle formulaire : le commentaire a imprimer en bas du devis */
  public static final String FormPrestaCommentKey = "prestaComment";
  /** Cle formulaire : le numero de la ligne budgetaire */
  public static final String FormOrgIdKey = "orgId";
  /** Cle formulaire : le code du type de credit */
  public static final String FormTdcCodeKey = "tcdCode";
  /** Cle formulaire : le code de destination */
  public static final String FormLolfIdKey = "lolfId";
  /** Cle formulaire : le code d'imputation */
  public static final String FormPcoNumKey = "pcoNum";
  /** Cle formulaire : le nombre des lignes */
  public static final String FormNbLignesKey = "nbLignes";
  /** Cle formulaire : le code de l'article dans une ligne */
  public static final String FormCaarIdKey      = "caarId";
  /** Cle formulaire : le nombre d'articles dans une ligne */
  public static final String FormNbArticlesKey = "nbArticles";
  /** Cle formulaire : le code de type de l'article dans une ligne */
  public static final String FormTypeArticleKey = "typeArticle";
  /** Cle formulaire : le code de devis */
  public static final String FormPrestIdKey            = "prestId";
  public static final String FormPrestNumeroKey        = "prestNumero";
  /** Cle formulaire : la somme restant dans une ligne budgetaire */
  public static final String FormSommeKey = "somme";
  /** Cle formulaire : la valeur d'une action avec le resultat boolean */
  public static final String FormBoolResultKey = "boolResult";
  /** Cle formulaire : test de l'etat du service Web */
  public static final String FormStatusKey = "serviceStatus";  
  
  
  
  // les noms des champs du dico provenant du WebService inspectDevis
  public static final String FieldDevisPersIdKey             = "persId";
  public static final String FieldDevisPrestIdKey            = "prestId";
  public static final String FieldDevisPrestNumeroKey        = "prestNumero";
  public static final String FieldDevisDateDevisKey          = "devDateDevis";
  public static final String FieldDevisCtcOrdreKey           = "ctcOrdre";
  public static final String FieldDevisRemiseGlobaleKey      = "pourcentRemiseGlobal";
  public static final String FieldDevisMontantTTCKey         = "devMontantTTC";
  public static final String FieldDevisCaarIdKey           	 = "caarId";
  public static final String FieldDevisCatIdKey           	 = "catId";
  public static final String FieldDevisDevisLibelleKey       = "devLibelle";
  public static final String FieldDevisFouOrdrePrestKey      = "fouOrdrePrest";
  public static final String FieldDevisMontantHTKey          = "devMontantHT";
  public static final String FieldDevisOrgIdClientKey      	 = "clientOrgId";
  public static final String FieldDevisTcdCodeClientKey      = "clientTcdCode";
  
  /** @deprecated */
  public static final String FieldDevisDstCodeKey            = "dstCode";
  public static final String FieldDevisLolfIdKey 						 = "lolfId";
  public static final String FieldDevisPrestOrdreKey         = "prestOrgOrdre";
  public static final String FieldDevisFouPersNomPrenomKey   = "fouPersNomPrenom";
  public static final String FieldDevisCStructureKey         = "cStructure";
  public static final String FieldDevisPrestationLignesKey   = "prestationLignes";
  public static final String FieldDevisFouOrdreKey           = "fouOrdre";
  public static final String FieldDevisMontantTVAKey         = "devMontantTVA";
  
  // les noms des champs pour une ligne d'un devis existant
  public static final String FieldLigneDevisCaarIdIdKey      = "caarId";
  public static final String FieldLigneDevisNbArticlesKey    = "dligNbArticles";
  
  // le nom des champs pour un article issu d'un catalogue
  public static final String FieldCaarIdIdKey              	= "caarId";
  public static final String FieldCartReferenceKey          = "cartReference";
  public static final String FieldCartDescriptionKey        = "cartDescription";
  public static final String FieldCartMontantHTKey          = "cartHt";
  
  // Les methodes directAction ouvertes sur prestation
  /** acces direct a la page du detail du devis pour validation */
  public static final String MethodDirectActionDisplayDevis = "wa/val?id=";
}
