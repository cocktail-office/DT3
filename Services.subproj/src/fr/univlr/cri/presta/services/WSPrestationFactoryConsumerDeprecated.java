/*
 * Copyright (C) 2004 Universite de La Rochelle
 *
 * This file is part of TestService.
 *
 * TestService is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * TestService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package fr.univlr.cri.presta.services;

import java.util.Hashtable;

import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;

/**
 * @deprecated
 * 
 * Classe permettant de se connecter au WebService
 * "WSPrestationFactoryProvider". <br>
 * Cette classe n'est pas documentee et n'est pas utilisee dans l'application
 * PrestationWeb. Elle est fournie pour simplifier les appels au service web, et
 * mapper les differentes operations fournies par le service. <br>
 * <br>
 * Pour avoir la javadoc des differentes operations, aller voir les methodes de
 * {@link WSPrestationFactoryProvider}.<br>
 * <br>
 * 
 * Pour un exemple d'utilisation, voir la javadoc de {@link ZWSConsumerDeprecated}.
 * 
 * @author rprin
 */
public class WSPrestationFactoryConsumerDeprecated extends ZWSConsumerDeprecated {
	public WSPrestationFactoryConsumerDeprecated(String pwsName, String pwsUrl, String appAlias, String password) throws Exception {
		super(pwsName, pwsUrl, appAlias, password);
	}


	/**
	 * Teste si le WebService tourne (exception si non) et renvoie les infos sur le WebService
	 * @return La chaine des param?tres de version du WebService, format:
	 * ws.status=YES/NO
	 * app.version=""
	 * app.ver.num=""
	 * app.ver.date=""
	 * @throws Exception
	 */
	public String checkWebService () throws Exception {
		String res = (String)wsInvoke("checkWebService",null);
		throwLastWsException();
		return res;
	}
	
	/**
	 * Cree un devis et l'affecte comme devis courant. (Le devis n'est pas
	 * enregistre dans la base).
	 * 
	 * 
	 * @param typeDevis
	 *            1: interne, 2:externe public, 3: externe prive
	 * @param structCliPersId
	 *            persId de la structure cliente (facultatif)
	 * @param persCliPersId
	 *            persId de l'individu client (contact) (obligatoire)
	 * @param catOrdre
	 *            Identifiant du catalogue
	 * @param devLibelle
	 *            Libelle du devis
	 * @param commentairePrest
	 *            Commentaire � imprimer en bas de devis
	 */
	public void createCurrentDevis(Integer typeDevis, Integer structCliPersId, Integer indCliPersId, Integer catOrdre, String devLibelle, String devCommentairePrest) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}
		Object[] wsArgs = {typeDevis, structCliPersId, indCliPersId, catOrdre, devLibelle, devCommentairePrest};
//		Integer res =(Integer) getWsClient().invoke(getWsName(),"createCurrentDevis",wsArgs);
    getWsClient().invoke(getWsName(),"createCurrentDevis",wsArgs);
		throwLastWsException();		
	}

	/**
	 * Cree un devis avec un article et l'affecte comme devis courant. (Le devis
	 * n'est pas enregistre dans la base).
	 * 
	 * 
	 * @param typeDevis
	 *            1: interne, 2:externe public, 3: externe prive
	 * @param structCliPersId
	 *            persId de la structure cliente (facultatif pour les devis
	 *            externes, obligatoire pour les devis internes)
	 * @param persCliPersId
	 *            persId de l'individu client (contact) (obligatoire)
	 * @param cartOrdre
	 *            Identifiant de l'article (obligatoire)
	 * @param nbArticles
	 *            Nombre d'articles (obligatoire)
	 * @param orgOrdre
	 *            La ligne budgetaire du client (facultatif - peut etre null si
	 *            pas renseigne)
	 * @param tcdCode
	 *            Le type de credit du client (facultatif - peut etre null si
	 *            pas renseigne)
	 * @param devLibelle
	 *            Libelle du devis (obligatoire)
	 * @param commentairePrest
	 *            Commentaire a imprimer en bas de devis (facultatif)
	 */
	public void createCurrentDevisArticle(Integer typeDevis, Integer structCliPersId, Integer indCliPersId, Integer cartOrdre, Integer nbArticles, Integer orgOrdre, String tcdCode, String devLibelle, String devCommentairePrest) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}
		Object[] wsArgs = {typeDevis, structCliPersId, indCliPersId, cartOrdre, nbArticles, orgOrdre, tcdCode, devLibelle, devCommentairePrest};
//		Integer res = (Integer) getWsClient().invoke(getWsName(),"createCurrentDevisArticle",wsArgs);
    getWsClient().invoke(getWsName(),"createCurrentDevisArticle",wsArgs);
		throwLastWsException();		
	}


	/**
	 * Enregistre les changements et renvoie la cle du devis (devOrdre)
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer saveChanges() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}		
		Integer res = (Integer) wsInvoke("saveChanges",null);
		throwLastWsException();
		return res;
	}
	
	
	/**
	 * 
	 * Ajoute une ligne au devis en cours. Le devis en cours doit etre charge ou
	 * cree.
	 * 
	 * @param typeArticle
	 *            (A:Article, O:Option, R:Remise)
	 * @param artOrdre
	 *            Identifiant de l'article
	 * @param artOrdreParent
	 *            Identifiant de l'article parent (uniquement si c'est une
	 *            Option ou une Remise (typeArticle = O ou R), null sinon)
	 * @param nbArticles
	 *            j'crois que c'est clair
	 */
	public void ajouteLignePourArticleInCurrentDevis(String typeArticle, Integer artOrdre, Integer artOrdreParent, Integer nbArticles) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}		
		Object[] wsArgs = {typeArticle, artOrdre, artOrdreParent, nbArticles};
		wsInvoke("ajouteLignePourArticleInCurrentDevis",wsArgs);
		throwLastWsException();			
	}

//    
//    /**
//     * 
//     * Modifie la quantite pour un article dans un devis.
//     * 
//     * @param artOrdre
//     *            Identifiant de l'article
//     * @param nbArticles
//     *            j'crois que c'est clair
//     *            
//     * erreur si l'article n'a pas ete trouve dans le devis
//     */
//    public void modifieQuantitePourArticleInCurrentDevis(Integer artOrdre, Integer nbArticles) throws Exception {
//        if (!isConnected()) {
//            throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
//        }       
//        Object[] wsArgs = {artOrdre, nbArticles};
//        wsInvoke("modifieQuantitePourArticleInCurrentDevis",wsArgs);
//        throwLastWsException();         
//    }

	/**
	 * Imprime le devis en cours si celui-ci a ete cree ou charge. Le resultat
	 * renvoie un NSData correspondant au fichier PDF � imprimer.
	 */
	public NSData imprimerCurrentDevis() throws Exception {
		if (!isConnected()) {
				throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		NSData res = (NSData) wsInvoke("imprimerCurrentDevis",null);
		throwLastWsException();				
		return res;
	}
	
	
	public WOResponse imprimerCurrentDevisAsWOResponse() {
		NSData pdfData;
		try {
			pdfData = imprimerCurrentDevis();
			WOResponse resp = new WOResponse();
			if (pdfData != null) {
				resp.setContent(pdfData);
				resp.setHeader("application/pdf" , "Content-Type");
				resp.setHeader(String.valueOf(pdfData.length()) , "Content-Length");
			} else {
				resp.setHeader("0" , "Content-Length");
			}			
			return resp.generateResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public NSArray getCatalogues(Integer fouOrdre, String typeUser) throws Exception {
		Object[] wsArgs = {fouOrdre,typeUser};
		//On recup?re un tableau de Map
		Object[] res = (Object[]) wsInvoke("getCatalogues", wsArgs);
		throwLastWsException();		
		return arrayMapsToNSArrayNSDictionarys(res);
	}


//	public NSArray getCatalogueArticles(Integer catOrdre) throws Exception {
//		Object[] wsArgs = {catOrdre};
//		//On recup?re un tableau de Map
//		Object[] res = (Object[]) wsInvoke("getCatalogueArticles", wsArgs);
//		throwLastWsException();		
//		return arrayMapsToNSArrayNSDictionarys(res);
//	}
//	
	/**
	 * @return Les types de credit autorises pour les prestations (vecteur de
	 *         <code>String</code>)
	 */
	public Object[] getCatalogueArticles(Integer catOrdre) throws Exception {
	  if (!isConnected()) {
	    throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
      }
	  Object[] wsArgs = {catOrdre};
      Object[] res = (Object[]) wsInvoke("getCatalogueArticles", wsArgs);
	  throwLastWsException();
	  return res;
	}
    
	public NSArray getDevis(Integer structCliPersId, Integer persCliPersId, Integer devOrdre) throws Exception {
		Object[] wsArgs = {structCliPersId,persCliPersId,devOrdre};
		Object[] res = (Object[]) wsInvoke("getDevis", wsArgs);
		throwLastWsException();		
		return arrayMapsToNSArrayNSDictionarys(res);	
	}	
	
	public NSArray getDevisForConvOrdre(Integer convOrdre) throws Exception {
		Object[] wsArgs = {convOrdre};
		Object[] res = (Object[]) wsInvoke("getDevisForConvOrdre", wsArgs);
		throwLastWsException();		
		return arrayMapsToNSArrayNSDictionarys(res);			
	}	

	public void valideCurrentDevisClient() throws Exception {
		wsInvoke("valideCurrentDevisClient", null);
		throwLastWsException();				
	}

	public void devalideCurrentDevisClient() throws Exception {
		wsInvoke("devalideCurrentDevisClient", null);
		throwLastWsException();				
	}


	public void valideCurrentDevisPrest() throws Exception {
		wsInvoke("valideCurrentDevisPrest", null);
		throwLastWsException();			
	}

	public void chargeCurrentDevisForClient(Integer devOrdre,  Integer persCliPersId) throws Exception {
		Object[] wsArgs = {devOrdre,persCliPersId};
		wsInvoke("chargeCurrentDevisForClient", wsArgs);
		throwLastWsException();			
	}
    
    public void chargeCurrentDevis(Integer devOrdre) throws Exception {
        Object[] wsArgs = {devOrdre};
        wsInvoke("chargeCurrentDevis", wsArgs);
        throwLastWsException();         
    }

	public void fermerCurrentPrestation() throws Exception {	
		wsInvoke("fermerCurrentPrestation", null);
		throwLastWsException();	
	}	

	public void affecteOrganPrestForCurrentDevis(Integer orgOrdre) throws Exception {
		Object[] wsArgs = {orgOrdre};
		wsInvoke("affecteOrganPrestForCurrentDevis", wsArgs);
		throwLastWsException();	
	}	
	
	public void affecteAgent(Integer agtOrdre) throws Exception {
		Object[] wsArgs = {agtOrdre};
		wsInvoke("affecteAgent", wsArgs);
		throwLastWsException();		
	}
	
	public void affecteConvOrdre(Integer convOrdre) throws Exception {
		Object[] wsArgs = {convOrdre};
		wsInvoke("affecteConvOrdre", wsArgs);
		throwLastWsException();		
	}
	
	public NSArray getOrgansForAgtOrdre(Integer agtOrdre) throws Exception {
		Object[] wsArgs = {agtOrdre};
		//On recup?re un tableau de Map
		Object[] res = (Object[]) wsInvoke("getOrgansForAgtOrdre", wsArgs);
		throwLastWsException();		
		return arrayMapsToNSArrayNSDictionarys(res);		
	}	

	public NSData imprimerFactureTitreByFtOrdre(Integer ftOrdre) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Object[] wsArgs = {ftOrdre};
		NSData res = (NSData) wsInvoke("imprimerFactureTitreByFtOrdre",wsArgs);
		throwLastWsException();				
		return res;
	}
	
	
	public WOResponse imprimerFactureTitreByFtOrdreAsWOResponse(Integer ftOrdre) {
		NSData pdfData;
		try {
			pdfData = imprimerFactureTitreByFtOrdre(ftOrdre);
			WOResponse resp = new WOResponse();
			if (pdfData != null) {
				resp.setContent(pdfData);
				resp.setHeader("application/pdf" , "Content-Type");
				resp.setHeader(String.valueOf(pdfData.length()) , "Content-Length");
			} else {
				resp.setHeader("0" , "Content-Length");
			}			
			return resp.generateResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public NSArray getDevisArray(Object[] devOrdres) throws Exception {
		Object[] wsArgs = {devOrdres};
		Object[] res = (Object[]) wsInvoke("getDevisArray", wsArgs);
		throwLastWsException();		
		return arrayMapsToNSArrayNSDictionarys(res);		
	
	}

    /**
     * Supprimer tous les articles du devis en cours
     * @throws Exception
     */
	public void viderLignesForCurrentDevis() throws Exception  {
		wsInvoke("viderLignesForCurrentDevis", null);
		throwLastWsException();				
	}

	/**
	 * Renvoie le montant HT du devis charge.
	 * 
	 * @return BigDecimal : montant HT
	 */
	public java.math.BigDecimal getMontantHTForCurrentDevis() throws Exception {
		java.math.BigDecimal res = (java.math.BigDecimal)wsInvoke("getMontantHTForCurrentDevis", null);
		throwLastWsException();
		return res;
	}

	/**
	 * Renvoie le montant TVA du devis charge.
	 * 
	 * @return BigDecimal : montant TVA
	 */
	public java.math.BigDecimal getMontantTVAForCurrentDevis() throws Exception {
		java.math.BigDecimal res = (java.math.BigDecimal)wsInvoke("getMontantTVAForCurrentDevis", null);
		throwLastWsException();
		return res;
	}

	/**
	 * Renvoie le montant TTC du devis charge.
	 * 
	 * @return BigDecimal : montant TTC
	 */
	public java.math.BigDecimal getMontantTTCForCurrentDevis() throws Exception {
		java.math.BigDecimal res = (java.math.BigDecimal)wsInvoke("getMontantTTCForCurrentDevis", null);
		throwLastWsException();
		return res;
	}

	public Boolean currentDevisValideCoteClient() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Boolean res = (Boolean) wsInvoke("currentDevisValideCoteClient",null);
		throwLastWsException();				
		return res;
	}
	
	public Boolean currentDevisValideCotePrestataire() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Boolean res = (Boolean) wsInvoke("currentDevisValideCotePrestataire",null);
		throwLastWsException();				
		return res;
	}
	
	public Boolean currentPrestationRealisee() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Boolean res = (Boolean) wsInvoke("currentPrestationRealisee",null);
		throwLastWsException();				
		return res;
	}
	
	/**
	 * 
	 * Renvoie le disponible a partir de la fonction stockee. Se base sur la
	 * ligne budgetaire et le type de credit.
	 * 
	 * @param orgOrdre
	 *            La ligne budgetaire
	 * @param tcdCode
	 *            Le type de credit
	 * @return Le disponible (Double)
	 */
	public Double getDisponible(Integer orgOrdre, String tcdCode) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Object[] wsArgs = {orgOrdre, tcdCode};
		Double res = (Double) wsInvoke("getDisponible",wsArgs);
		throwLastWsException();				
		return res;
	}

	/**
	 * Retourne le montant TTC prevu pour un devis qui comporterait les
	 * articles/quantites passes. Ne cree aucun devis, ne cree rien dans la base !
	 * 
	 * @param articles
	 *            Tableau des articles qui constituent le devis (cartOrdre) -
	 *            Obligatoire
	 * @param quantites
	 *            Tableau des quantites des articles du premier tableau -
	 *            Facultatif, toutes quantites � 1 par defaut si absent
	 * @return Le montant TTC, en tenant compte des remises eventuelles
	 */
	public Double getMontantTTCForDevis(Object[] articles, Object[] quantites) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Object[] wsArgs = {articles, quantites};
		Double res = (Double) wsInvoke("getMontantTTCForDevis", wsArgs);
		throwLastWsException();		
		return res;		
	}

	/**
	 * Archive le devis en cours (ne le supprime pas, la suppression d'un devis
	 * est interdite). Necessite un saveChanges pour prendre le changement en
	 * compte.
	 *  
	 */
	public void deleteCurrentDevis() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		wsInvoke("deleteCurrentDevis", null);
		throwLastWsException();				
	}
	
	/**
	 * Charge en devis courant et archive le devis donne (ne le supprime pas, la suppression d'un devis est
	 * interdite). Necessite un saveChanges pour prendre le changement en
	 * compte.
	 * 
	 * @param devOrdre
	 *            Identifiant du devis
	 * @param persCliPersId
	 *            Identifiant de la personne cliente.
	 */
	public void deleteDevis(Integer devOrdre, Integer persCliPersId) throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Object[] wsArgs = {devOrdre, persCliPersId};
		wsInvoke("deleteDevis", wsArgs);
		throwLastWsException();		
	}
    
    
    /**
     * Charge en devis courant et archive le devis donne (ne le supprime pas, la suppression d'un devis est
     * interdite). Necessite un saveChanges pour prendre le changement en
     * compte.
     * 
     * @param devOrdre
     *            Identifiant du devis
     */
    public void deleteDevisSansControle(Integer devOrdre) throws Exception {
        if (!isConnected()) {
            throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
        }   
        Object[] wsArgs = {devOrdre};
        wsInvoke("deleteDevisSansControle", wsArgs);
        throwLastWsException();     
    }

	/**
   * @return La liste des differents etats d'un devis sous forme de
   *         <code>Hashtable</code> (un code d'une lettre, un libelle)
   */
	public Hashtable getDevisEtats() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		Hashtable res = mapToNSDictionary((java.util.Map) wsInvoke("getDevisEtats", null)).hashtable();
		throwLastWsException();
		return res;
	}
	
	/**
   * @return l'etat du devis courant (Cf. <code>getDevisEtats</code> pour la
   *         signification des codes)
   */
	public String getEtatCurrentDevis() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}	
		String res = (String) wsInvoke("getEtatCurrentDevis", null);
		throwLastWsException();
		return res;
	}
  
  /**
   * @return Les types de credit autorises pour les prestations (vecteur de
   *         <code>String</code>)
   */
	public java.util.Vector getTypcredAutorises() throws Exception {
	  if (!isConnected()) {
	    throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
	  }          java.util.Vector res = (java.util.Vector) wsInvoke("getTypcredAutorises", null);
	  throwLastWsException();
	  return res;
	}
 
	public void affecteOrganClientForCurrentDevis(Integer orgOrdre) 
	throws Exception {
	  Object[] wsArgs = {orgOrdre};
	  wsInvoke("affecteOrganClientForCurrentDevis", wsArgs);
	  throwLastWsException();    
	}    
	
	public void affecteTypcredClientForCurrentDevis(String tcdCode) 
	throws Exception {
	  Object[] wsArgs = {tcdCode};
	  wsInvoke("affecteTypcredClientForCurrentDevis", wsArgs);
	  throwLastWsException();    
	}    
	
	public void affecteImputationClientForCurrentDevis(String pcoNum) 
	throws Exception {
	  Object[] wsArgs = {pcoNum};
	  wsInvoke("affecteImputationClientForCurrentDevis", wsArgs);
	  throwLastWsException();    
	}    
	
	public void affecteDestinationClientForCurrentDevis(String dstCode) 
	throws Exception {
	  Object[] wsArgs = {dstCode};
	  wsInvoke("affecteDestinationClientForCurrentDevis", wsArgs);
	  throwLastWsException();    
	}    
    
    public void valideToutCurrentDevis(Integer agtOrdre) 
    throws Exception {
      Object[] wsArgs = {agtOrdre};
      wsInvoke("valideToutCurrentDevis", wsArgs);
       throwLastWsException();    
    }  
}
 