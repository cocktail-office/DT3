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
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.cocktail.dt.server.metier.EOIndividu;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.kava.server.ws.consumer.WSPrestationFactoryConsumer;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import fr.univlr.cri.dt.app.I_ApplicationConsts;
import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;

/**
 * Implementation des procedures pour la creation des prestations. Cette classe
 * permet de creer les devis et de verifier leur etat. Ces methodes peuvent etre
 * utilisees dans les services Web et les direct actions.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTPrestaServicesImp extends DTServicesImpBase {
	/** Le nom de service Web */
	private static String PrestaProviderName = "WSPrestation";
	/** Le URL d'acces au service Web prestations. */
	private String serviceURL;
	/** Le mot de passe d'acces au service Web de prestations. */
	private String servicePass;
	/** L'identifiant d'utilisateur creant le devis */
	private Integer userPersId;
	/**
	 * Le identifiant <code>fouOrdre</code> de la structure de l'utilisateur
	 * creant la demande
	 */
	private Integer userStructFouOrdre;
	/**
	 * La reference vers l'objet utilise pour communiquer avec le service Web
	 * prestations.
	 */
	private WSPrestationFactoryConsumer wsp;

	/** */
	private final static Integer REPRO_UTL_ORDRE =
			new Integer(((Application) Application.application()).config().intForKey(I_ApplicationConsts.REPRO_UTL_ORDRE_KEY));
	/** */
	private final static Integer REPRO_CAT_ID =
			new Integer(((Application) Application.application()).config().intForKey(I_ApplicationConsts.REPRO_CAT_ID_KEY));

	/**
	 * Cree une nouvelle instance d'acces au service Web de prestations pour les
	 * prestations.
	 * 
	 * @param serviceURL
	 *          Le URL d'acces au service Web. Le URL doit inclure le suffix
	 *          "/ws/".
	 * @param servicePass
	 *          Le mot de passe pour acceder au service.
	 */
	public DTPrestaServicesImp(String url, String pass) {
		// URL = http://site//cgi-bin/WebObjects/PieWeb.woa/ws/
		if (StringCtrl.normalize(url).length() == 0)
			throw new IllegalArgumentException("Le URL de service \"Prestations\" n'est pas precise");
		if (StringCtrl.normalize(pass).length() == 0)
			throw new IllegalArgumentException("Le mot de passe pour le service \"Prestations\" n'est pas precise");
		serviceURL = checkServiceAddress(url, "/ws/" + PrestaProviderName + "?wsdl");
		servicePass = pass;
		setServiceKey("wa-ws PIE");
	}

	/**
	 * Test et corrige l'adresse HTTP du service. L'adresse correcte doit avoir la
	 * forme "http://.../Appli.woa/wa/serviceName" pour les direct actions et
	 * "http://.../Appli.woa/ws/serviceName" pour les services Web.
	 */
	protected String checkServiceAddress(String addr, String suffix) {
		addr = addr.trim();
		if (!addr.endsWith(suffix)) {
			if (addr.endsWith("/"))
				addr = addr.substring(0, addr.length() - 1);
			if (addr.indexOf(".woa") > 0)
				addr = addr.substring(0, addr.indexOf(".woa") + 4);
			if (!suffix.startsWith("/"))
				addr += "/";
			addr += suffix;
		}
		return addr;
	}

	/**
	 * Definit les informations sur l'utilisateur qui creera un devis.
	 * 
	 * @param persId
	 *          L'identifiant personnel de l'utilisateur.
	 * @param structPersId
	 *          L'identifiant de la structure auquelle appartient l'utilisateur.
	 * @param structFouOrdre
	 *          L'identifiant <code>fouOrdre</code> de la structure auquelle
	 *          appartient l'utilisateur.
	 */
	public void setUser(Integer persId, Integer structFouOrdre) {
		userPersId = persId;
		userStructFouOrdre = structFouOrdre;
	}

	/**
	 * Effectue la creation d'un nouveau devis pour l'article donne. Le devis sera
	 * cree au nom de l'utilsateur qui a ete indique avec la methode
	 * <code>setUser</code>.
	 * 
	 * <p>
	 * Le devis cree ne pourra contenir qu'un seul article. Utilisez une autre
	 * version de la methode pour creer un devis avec plusieurs articles.
	 * </p>
	 * 
	 * @param typeDevis
	 *          Le type de la prestation pour laquelle le devis est cree.
	 * @param devisLibelle
	 *          Le libelle du devis.
	 * @param prestaComment
	 *          Le commentaire a imprimer en bas de devis.
	 * @param orgId
	 *          Le code de la ligne budgetaire. Peut etre <em>null</em>.
	 * @param tcdOrdre
	 *          Le type de credit de la ligne budgetaire. Peut etre <em>null</em>.
	 * @param lolfId
	 *          Le code de destination. Peut etre <em>null</em>.
	 * @param pcoNum
	 *          Le code d'imputation. Peut etre <em>null</em>.
	 * @param caarId
	 *          Le code d'article. Le catalogue pour le devis sera celui de
	 *          l'article.
	 * @param nbArticles
	 *          Le nombre d'articles dans le devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient le code de devis accessible via la cle
	 *         <code>DTPrestaServiceConst.ResultDevisKey</code>.
	 * 
	 * @see #createDevis(Integer, String, String, Integer, String, String, String,
	 *      Vector)
	 * @see #setUser(Integer, Integer)
	 */
	public Hashtable createDevis(Integer typeDevis, String devisLibelle,
			String prestaComment, Integer orgId, Integer tcdOrdre, Integer lolfId,
			String pcoNum, Integer caarId, BigDecimal nbArticles) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionCreateDevisName);
		try {
			openConnection();
			// On enregistre l'article
			wsp.createCurrentDevisArticle(
					REPRO_UTL_ORDRE, typeDevis, userStructFouOrdre, userPersId,
					REPRO_CAT_ID, devisLibelle, prestaComment, caarId, nbArticles);
			// On associe le code de destination et d'imputation, s'ils sont donnes
			if (lolfId != null)
				wsp.affecteDestinationClientForCurrentDevis(lolfId);
			if (pcoNum != null)
				wsp.affecteImputationClientForCurrentDevis(pcoNum);
			sResults.putAll(closeConnection(true));
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionCreateDevisName);
		return sResults;
	}

	/**
	 * Cree un devis avec les articles dont les definitions sont donnees dans la
	 * liste <code>lignes</code>. La liste <code>lignes</code> est constitue
	 * d'objets <code>NSDictionary</code>. Chaque objet contient les informations
	 * enregistres avec les cles dont les noms sont indiques par les constantes
	 * <code>Devis...Key</code> de la classe <code>DTPrestaServiceConst</code>.
	 * 
	 * <p>
	 * La definition d'un objet-dictionnaire est composee de : code d'article (
	 * <code>DevisArticleIDKey</code>), nombre d'articles (
	 * <code>DevisNbArticlesKey</code>), type de l'article (
	 * <code>DevisTypeArticleKey</code>).
	 * </p>
	 * 
	 * <p>
	 * Le type de l'article doit indiquer une des constantes
	 * <code>TypeArticle...</code> de la classe <code>DTPrestaServiceConst</code>.
	 * Cette definition est obtionnelle. Si elle n'est pas donnee, alors le type
	 * <code>TypeArticleArticle</code> est utilise.
	 * </p>
	 * 
	 * <p>
	 * Le devis est cree au nom de l'utilisateur dont les informations sont
	 * disponibles via le ticket <code>uiTicket</code>.
	 * </p>
	 * 
	 * @param uiTicket
	 *          Le ticket pour acceder aux informations de l'utilisateur. Les
	 *          informations doivent prealablement etre enregistrees dans une
	 *          table partagee.
	 * @param typeDevis
	 *          Le type de la prestation pour laquelle le devis est cree. C'est
	 *          une des constantes <code>TypeDevis...</code>.
	 * @param devisLibelle
	 *          Le libelle du devis.
	 * @param prestaComment
	 *          Le commentaire a imprimer en bas de devis.
	 * @param orgId
	 *          Le code de la ligne budgetaire. Peut etre <em>null</em>.
	 * @param tcdCode
	 *          Le type de credit de la ligne budgetaire. Peut etre <em>null</em>.
	 * @param lolfId
	 *          Le code de destination. Peut etre <em>null</em>.
	 * @param pcoNum
	 *          Le code d'imputation. Peut etre <em>null</em>.
	 * @param lignes
	 *          Les ligne de devis. Chaque entre de cette liste contient un objet
	 *          <code>NSDictionary</code> avec la definition de l'article.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient le code de devis accessible via la cle
	 *         <code>DTPrestaServiceConst.ResultDevisKey</code>.
	 * 
	 * @see #createDevis(Integer, String, String, Integer, String, String, String,
	 *      Integer, Integer)
	 * @see #setUser(Integer, Integer)
	 */
	public Hashtable createDevis(Integer typeDevis, String devisLibelle,
			String prestaComment, Integer orgId, String tcdCode,
			Integer lolfId, String pcoNum, Vector lignes) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionCreateDevisName);
		if ((lignes != null) && (lignes.size() > 0)) {
			Hashtable artDef;
			String artType;
			try {
				openConnection();
				// On ajoute le premier article
				artDef = (Hashtable) lignes.elementAt(0);
				wsp.createCurrentDevisArticle(
						REPRO_UTL_ORDRE, wsp.TYPE_PUBLIC_INTERNE, userStructFouOrdre,
						userPersId, REPRO_CAT_ID, devisLibelle, prestaComment,
						(Integer) artDef.get(DTPrestaServicesConst.DevisArticleIDKeyNew),
						(BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey));
				// Les autres articles, s'il y en a
				for (int i = 1; i < lignes.size(); i++) {
					artDef = (Hashtable) lignes.elementAt(i);
					artType = (String) artDef.get(DTPrestaServicesConst.DevisTypeArticleKey);
					if (artType == null)
						artType = DTPrestaServicesConst.TypeArticleArticle;
					wsp.ajouteLignePourArticleInCurrentDevis(
							(Integer) artDef.get(DTPrestaServicesConst.DevisArticleIDKeyNew),
							(BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey));
				}
				// On associe la ligne budgetaire le code de
				// destination et d'imputation, s'ils sont donnes
				if (orgId != null) {
					wsp.affecteOrganClientForCurrentDevis(orgId);
				}
				if (tcdCode != null) {
					wsp.affecteTypcredClientForCurrentDevis(tcdCode);
				}
				if (lolfId != null) {
					wsp.affecteDestinationClientForCurrentDevis(lolfId);
				}
				if (pcoNum != null) {
					wsp.affecteImputationClientForCurrentDevis(pcoNum);
				}
				sResults.putAll(closeConnection(true));
			} catch (Throwable ex) {
				ex.printStackTrace();
				setError(CktlLog.getMessageForException(ex));
			}
		} else {
			setError("Aucun article n'est indique");
		}
		logEnd(DTPrestaServicesConst.ActionCreateDevisName);
		return sResults;
	}

	/**
	 * @deprecated
	 * 
	 *             Modidie un devis avec les nouveaux articles dont les
	 *             definitions sont donnees dans la liste <code>lignes</code>. La
	 *             liste <code>lignes</code> est constitue d'objets
	 *             <code>NSDictionary</code>. Chaque objet contient les
	 *             informations enregistres avec les cles dont les noms sont
	 *             indiques par les constantes <code>Devis...Key</code> de la
	 *             classe <code>DTPrestaServiceConst</code>.
	 * 
	 *             <p>
	 *             La definition d'un objet-dictionnaire est composee de : code
	 *             d'article (<code>DevisArticleIDKey</code>), nombre d'articles (
	 *             <code>DevisNbArticlesKey</code>), type de l'article (
	 *             <code>DevisTypeArticleKey</code>).
	 *             </p>
	 * 
	 *             <p>
	 *             Le type de l'article doit indiquer une des constantes
	 *             <code>TypeArticle...</code> de la classe
	 *             <code>DTPrestaServiceConst</code>. Cette definition est
	 *             obtionnelle. Si elle n'est pas donnee, alors le type
	 *             <code>TypeArticleArticle</code> est utilise.
	 *             </p>
	 * 
	 *             <p>
	 *             Le devis est cree au nom de l'utilisateur dont les informations
	 *             sont disponibles via le ticket <code>uiTicket</code>.
	 *             </p>
	 * 
	 * @param prestId
	 *          Le numero du devis a modifier.
	 * @param persId
	 *          Le persId du client du devis.
	 * @param lignes
	 *          Les ligne de devis. Chaque entre de cette liste contient un objet
	 *          <code>NSDictionary</code> avec la definition de l'article.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient le code de devis accessible via la cle
	 *         <code>DTPrestaServiceConst.ResultDevisKey</code>.
	 * 
	 * @see #createDevis(Integer, String, String, Integer, String, String, String,
	 *      Integer, Integer)
	 * @see #setUser(Integer, Integer)
	 */
	public Hashtable updateDevis(Integer prestId, Integer persId, Vector lignes) {
		logStart(null, userPersId.intValue(), persId.intValue(), prestId.intValue(),
				"persId", "persId", "prestId", DTPrestaServicesConst.ActionUpdateDevis);
		if ((lignes != null) && (lignes.size() > 0)) {
			Hashtable artDef;
			String artType;
			try {
				openConnection();
				// charger le devis
				wsp.chargeCurrentDevisForClient(prestId, persId);
				// vider toutes les lignes du devis
				wsp.viderLignesForCurrentDevis();
				// Les autres articles, s'il y en a
				for (int i = 0; i < lignes.size(); i++) {
					artDef = (Hashtable) lignes.elementAt(i);
					artType = (String) artDef.get(DTPrestaServicesConst.DevisTypeArticleKey);
					if (artType == null)
						artType = DTPrestaServicesConst.TypeArticleArticle;
					wsp.ajouteLignePourArticleInCurrentDevis(
							(Integer) artDef.get(DTPrestaServicesConst.DevisArticleIDKeyNew),
							(BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey));
				}
				sResults.putAll(closeConnection(true));
			} catch (Throwable ex) {
				ex.printStackTrace();
				setError(CktlLog.getMessageForException(ex));
			}
		} else {
			setError("Aucun article n'est indique");
		}
		logEnd(DTPrestaServicesConst.ActionUpdateDevis);
		return sResults;
	}

	/**
	 * Modidie un devis avec les nouveaux articles dont les definitions sont
	 * donnees dans la liste <code>lignes</code>. La liste <code>lignes</code> est
	 * constitue d'objets <code>NSDictionary</code>. Chaque objet contient les
	 * informations enregistres avec les cles dont les noms sont indiques par les
	 * constantes <code>Devis...Key</code> de la classe
	 * <code>DTPrestaServiceConst</code>.
	 * 
	 * <p>
	 * La definition d'un objet-dictionnaire est composee de : code d'article (
	 * <code>DevisArticleIDKey</code>), nombre d'articles (
	 * <code>DevisNbArticlesKey</code>), type de l'article (
	 * <code>DevisTypeArticleKey</code>).
	 * </p>
	 * 
	 * <p>
	 * Le type de l'article doit indiquer une des constantes
	 * <code>TypeArticle...</code> de la classe <code>DTPrestaServiceConst</code>.
	 * Cette definition est obtionnelle. Si elle n'est pas donnee, alors le type
	 * <code>TypeArticleArticle</code> est utilise.
	 * </p>
	 * 
	 * <p>
	 * Le devis est cree au nom de l'utilisateur dont les informations sont
	 * disponibles via le ticket <code>uiTicket</code>.
	 * </p>
	 * 
	 * @param prestId
	 *          Le numero du devis a modifier.
	 * @param lignes
	 *          Les ligne de devis. Chaque entre de cette liste contient un objet
	 *          <code>NSDictionary</code> avec la definition de l'article.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient le code de devis accessible via la cle
	 *         <code>DTPrestaServiceConst.ResultDevisKey</code>.
	 * 
	 * @see #createDevis(Integer, String, String, Integer, String, String, String,
	 *      Integer, Integer)
	 * @see #setUser(Integer, Integer)
	 */
	public Hashtable updateDevis(Integer prestId, Vector lignes) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionUpdateDevis);
		if ((lignes != null) && (lignes.size() > 0)) {
			Hashtable artDef;
			String artType;
			try {
				openConnection();
				// charger le devis
				wsp.chargeCurrentDevis(prestId);
				// on devalide au cas ou
				if (wsp.currentPrestationRealisee() == Boolean.TRUE) {
					wsp.decloreCurrentPrestation(REPRO_UTL_ORDRE);
				}
				if (wsp.currentDevisValideCotePrestataire() == Boolean.TRUE) {
					wsp.devalideCurrentDevisPrest(REPRO_UTL_ORDRE);
				}
				if (wsp.currentDevisValideCoteClient() == Boolean.TRUE) {
					// on ne met pas le REPRO_UTL_PRDRE ici, car s'il y a un engagement,
					// Pie refuse le num d'edouige, si vide, alors sont retrouves
					// automatiquement les numeros dans Pie
					wsp.devalideCurrentDevisClient(null);
				}
				// vider toutes les lignes du devis
				wsp.viderLignesForCurrentDevis();
				// Les autres articles, s'il y en a
				for (int i = 0; i < lignes.size(); i++) {
					artDef = (Hashtable) lignes.elementAt(i);
					artType = (String) artDef.get(DTPrestaServicesConst.DevisTypeArticleKey);
					if (artType == null)
						artType = DTPrestaServicesConst.TypeArticleArticle;
					wsp.ajouteLignePourArticleInCurrentDevis(
							(Integer) artDef.get(DTPrestaServicesConst.DevisArticleIDKeyNew),
							(BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey));
				}
				sResults.putAll(closeConnection(true));
			} catch (Throwable ex) {
				ex.printStackTrace();
				setError(CktlLog.getMessageForException(ex));
			}
		} else {
			setError("Aucun article n'est indique");
		}
		logEnd(DTPrestaServicesConst.ActionUpdateDevis);
		return sResults;
	}

	/**
	 * @deprecated
	 * @see #inspectDevis(Integer)
	 * 
	 *      Retourne la description d'un devis.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * @param persId
	 *          Le persId du client du devis.
	 * @param structPersId
	 *          Le persId de la structure cliente du devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable inspectDevis(Integer prestId, Integer persId, Integer structPersId) {
		logStart(null, userPersId.intValue(), persId.intValue(), structPersId.intValue(),
				"persId", "persId", "persId", DTPrestaServicesConst.ActionInspectDevisName);
		try {
			sResults.clear();
			openConnection();
			NSArray devis = wsp.getDevis(structPersId, persId, prestId);
			Hashtable unDevis = null;
			if (devis != null && devis.count() > 0) {
				unDevis = ((NSDictionary) devis.objectAtIndex(0)).hashtable();
				CktlLog.trace("devis : " + unDevis);
			}
			if (unDevis == null)
				setError("Le devis avec le code  \"" + prestId + "\" n'a pas ete trouve.");
			else
				sResults.putAll(unDevis);
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionInspectDevisName);
		return sResults;
	}

	/**
	 * Retourne la description d'un devis.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable inspectDevis(Integer prestId) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionInspectDevisName);
		try {
			sResults.clear();
			openConnection();
			// on passe null pour les pers_id pour ne pas avoir de pb si le
			// contacts du devis sont differents dans DT et dans PIE
			NSArray devis = wsp.getDevis(null, null, prestId);
			Hashtable unDevis = null;
			if (devis != null && devis.count() > 0) {
				unDevis = ((NSDictionary) devis.objectAtIndex(0)).hashtable();
				CktlLog.trace("devis : " + unDevis);
			}
			if (unDevis == null)
				setError("Le devis avec le code  \"" + prestId + "\" n'a pas ete trouve.");
			else
				sResults.putAll(unDevis);
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionInspectDevisName);
		return sResults;
	}

	/**
	 * Retourne tous les articles d'un catalogue.
	 * 
	 * @param REPRO_CAT_ID
	 *          : la cle du catalogue.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>.
	 */
	public Hashtable getCatalogueArticles(Integer catId) {
		logStart(null, catId.intValue(), I_ApplicationConsts.REPRO_CAT_ID_KEY,
				DTPrestaServicesConst.ActionGetCatalogueArticleCatOrdre);
		try {
			sResults.clear();
			openConnection();
			NSArray articles = wsp.getCatalogueArticles(catId);
			if (articles == null) {
				articles = new NSArray();
			}
			// On memorise d'abord le nombre des codes des types
			sResults.put(
					DTPrestaServicesConst.FormNbLignesKey,
					new Integer(articles.count()));
			// Ensuite, on memorise les types eux m�me
			for (int i = 0; i < articles.count(); i++) {
				sResults.put(
						DTPrestaServicesConst.FormCaarIdKey + i,
						articles.objectAtIndex(i));
			}
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionGetCatalogueArticleCatOrdre);
		return sResults;

	}

	/**
	 * Teste si une prestation est realisee.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable prestaRealisee(Integer prestId) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionPrestaRealiseeName);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			sResults.put(
					DTPrestaServicesConst.FormBoolResultKey,
					wsp.currentPrestationRealisee());
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionPrestaRealiseeName);
		return sResults;
	}

	/**
	 * @deprecated
	 * @see #prestaRealisee(Integer)
	 * 
	 *      Teste si une prestation est realisee.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * @param persId
	 *          Le persId du client.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable prestaRealisee(Integer prestId, Integer persId) {

		logStart(null, userPersId.intValue(), persId.intValue(), prestId.intValue(),
				"persId", "persId", "prestId", DTPrestaServicesConst.ActionPrestaRealiseeName);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			sResults.put(
					DTPrestaServicesConst.FormBoolResultKey,
					wsp.currentPrestationRealisee());
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionPrestaRealiseeName);
		return sResults;
	}

	/**
	 * Retourne la montant TTC estime d'un devis lorsqu'il sera cree avec les
	 * articles et les quantites donnes. Les listes <code>articles</code> et
	 * <code>quantites</code> doivent avoir le meme nombre d'elements.
	 * 
	 * @param articles
	 *          Les identifiants d'articles qu'un devis pourra comporter.
	 * @param nbArticles
	 *          Les quantites de chaque article dans un devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable estimeTTCDevis(Vector articles, Vector nbArticles) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionEstimeTTCDevisName);
		try {
			sResults.clear();
			openConnection();
			sResults.put(
					DTPrestaServicesConst.FormSommeKey,
					wsp.getMontantTTCForDevis(
							articles.toArray(),
							(BigDecimal[]) nbArticles.toArray()));
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionEstimeTTCDevisName);
		return sResults;
	}

	/**
	 * Retourne la montant TTC estime d'un devis lorsqu'il sera cree avec les
	 * articles donnees dans la liste <code>lines</code>. Chaque entree de la
	 * liste lines est un objet <code>Hashtable</code>. Le numero et le nombre
	 * d'articles y sont accessibles via les cles donnees dans les constantes
	 * <code>FormCartOrdreKey</code> et <code>FormNbArticlesKey</code> de la
	 * classe <code>DTPrestaServicesConst</code>.
	 * 
	 * @param lines
	 *          Les ligne de devis avec les articles.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable estimeTTCDevis(Vector lines) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionEstimeTTCDevisName);
		try {
			sResults.clear();
			// On cree deux listes separes : articles et nombres d'articles
			Object[] articles = new Object[lines.size()];
			BigDecimal[] nbArticles = new BigDecimal[lines.size()];
			Hashtable artDef;
			for (int i = 0; i < lines.size(); i++) {
				artDef = (Hashtable) lines.elementAt(i);
				articles[i] = artDef.get(DTPrestaServicesConst.FormCaarIdKey);
				nbArticles[i] = (BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey);
				if (nbArticles[i] == null) {
					nbArticles[i] = (BigDecimal) artDef.get(DTPrestaServicesConst.DevisNbArticlesKey);
				}
			}
			// On effectue la communication avec le service
			openConnection();
			sResults.put(
					DTPrestaServicesConst.FormSommeKey,
					wsp.getMontantTTCForDevis(articles, nbArticles));
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionEstimeTTCDevisName);
		return sResults;
	}

	/**
	 * Retourne la montant TTC du devis.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable sommeTTCDevis(Integer prestId) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionSommeTTCDevisName);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			sResults.put(
					DTPrestaServicesConst.FormSommeKey,
					wsp.getMontantTTCForCurrentDevis());
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionSommeTTCDevisName);
		return sResults;
	}

	/**
	 * @deprecated
	 * @see #sommeTTCDevis(Integer)
	 * 
	 *      Retourne la montant TTC du devis.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * @param persId
	 *          Le persId du client.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable sommeTTCDevis(Integer prestId, Integer persId) {
		logStart(null, userPersId.intValue(), persId.intValue(), prestId.intValue(),
				"persId", "persId", "prestId", DTPrestaServicesConst.ActionSommeTTCDevisName);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			sResults.put(
					DTPrestaServicesConst.FormSommeKey,
					wsp.getMontantTTCForCurrentDevis());
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionSommeTTCDevisName);
		return sResults;
	}

	/**
	 * Retourne la somme disponible sur une ligne budgetaire.
	 * 
	 * @param orgId
	 *          Le code de la ligne budgetaire.
	 * @param tcdCode
	 *          Le type de credit.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 *         dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable sommeLigneBud(Integer orgId, String tcdCode) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionSommeDispoName);
		try {
			sResults.clear();
			openConnection();
			sResults.put(
					DTPrestaServicesConst.FormSommeKey,
					wsp.getDisponible(orgId, tcdCode));
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionSommeDispoName);
		return sResults;
	}

	/**
	 * Archive le devis avec le code donne (la suppression des devis est
	 * interdite). Suite a cette operation le devis est devalide.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * 
	 * @return Returne le dictionnaire vide dans le cas du succes de l'operation.
	 *         Sinon, le dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable deleteDevis(Integer prestId) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionDeleteDevisName);
		try {
			sResults.clear();
			openConnection();
			wsp.deleteDevis(prestId, userStructFouOrdre, REPRO_UTL_ORDRE);
			closeConnection(true);
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.TRUE);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.FALSE);
		}
		logEnd(DTPrestaServicesConst.ActionDeleteDevisName);
		return sResults;
	}

	/**
	 * Archive le devis avec le code donne (la suppression des devis est
	 * interdite). Suite a cette operation le devis est devalide.
	 * 
	 * @param prestId
	 *          Le code du devis.
	 * 
	 * @return Returne le dictionnaire vide dans le cas du succes de l'operation.
	 *         Sinon, le dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable deleteDevisSansControle(Integer prestId) {
		logStart(null, -1, "persId",
				DTPrestaServicesConst.ActionDeleteDevisSansControleName);
		try {
			sResults.clear();
			openConnection();
			wsp.deleteDevisSansControle(prestId, REPRO_UTL_ORDRE);
			closeConnection(true);
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.TRUE);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.FALSE);
		}
		logEnd(DTPrestaServicesConst.ActionDeleteDevisSansControleName);
		return sResults;
	}

	/**
	 * Supprimer toutes les lignes du devis en cours.
	 * 
	 * @return Returne le dictionnaire vide dans le cas du succes de l'operation.
	 *         Sinon, le dictionnaire contient la somme accessible via la cle
	 *         <code>DTPrestaServiceConst.FormSommeKey</code>.
	 */
	public Hashtable viderLignes(Integer prestId) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionViderLignes);
		try {
			sResults.clear();
			openConnection();
			wsp.chargeCurrentDevis(prestId);
			wsp.viderLignesForCurrentDevis();
			closeConnection(true);
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.TRUE);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.FALSE);
		}
		logEnd(DTPrestaServicesConst.ActionViderLignes);
		return sResults;
	}

	/**
	 * Valider le devis dans son integralite, depuis la validation du client
	 * jusqu'a la cloture totale de la prestation.
	 */
	public Hashtable validerToutDevis(Integer prestId, Integer aPersId) {
		logStart(null, REPRO_UTL_ORDRE.intValue(), prestId.intValue(),
				I_ApplicationConsts.REPRO_UTL_ORDRE_KEY, "prestId", DTPrestaServicesConst.ActionValiderToutCurrentDevis);
		//
		Integer utlOrdreDemandeur = EOIndividu.utlOrdreForPersId(new EOEditingContext(), aPersId);
		try {
			sResults.clear();
			openConnection();

			// charger le devis
			wsp.chargeCurrentDevis(prestId);

			// on devalide au cas ou
			if (wsp.currentPrestationRealisee() == Boolean.TRUE) {
				wsp.decloreCurrentPrestation(REPRO_UTL_ORDRE);
			}

			if (wsp.currentDevisValideCotePrestataire() == Boolean.TRUE) {
				wsp.devalideCurrentDevisPrest(REPRO_UTL_ORDRE);
			}

			if (wsp.currentDevisValideCoteClient() == Boolean.TRUE) {
				// on ne met pas le REPRO_UTL_ORDRE ici, car s'il y a un engagement,
				// Pie refuse le num d'edouige, si vide, alors sont retrouves
				// automatiquement les numeros dans Pie
				wsp.devalideCurrentDevisClient(null);
			}

			if (utlOrdreDemandeur == null) {
				wsp.valideToutCurrentDevis(REPRO_UTL_ORDRE);
			} else {
				wsp.valideCurrentDevisClient(utlOrdreDemandeur);
				wsp.valideToutCurrentDevis(REPRO_UTL_ORDRE);
			}
			closeConnection(true);
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.TRUE);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.FALSE);
		}
		logEnd(DTPrestaServicesConst.ActionValiderToutCurrentDevis);
		return sResults;
	}

	/**
	 * Valider le devis dans son integralite, depuis la validation du client
	 * jusqu'a la cloture totale de la prestation.
	 */
	public Hashtable validerToutDevis(Integer prestId) {
		logStart(null, prestId.intValue(), "prestId", DTPrestaServicesConst.ActionValiderToutCurrentDevis);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			wsp.valideToutCurrentDevis(REPRO_UTL_ORDRE);
			closeConnection(true);
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.TRUE);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormBoolResultKey, Boolean.FALSE);
		}
		logEnd(DTPrestaServicesConst.ActionValiderToutCurrentDevis);
		return sResults;
	}

	/**
	 * Teste la disponibilite du service Web "prestations" et retourne sa
	 * description.
	 * 
	 * <p>
	 * Parmis les cles du dictionnaire, il y a toujours une valeur avec la cle
	 * donnee par la constante <code>DTPrestaServicesConst.FormStatusKey</code>
	 * ("serviceStatus" dans cette implementation). Cette valeur est "YES" dans le
	 * cas si le service est bien disponible, et "NON" dans le cas contraire.
	 * </p>
	 */
	public Hashtable checkWebService() {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionCheckWebServiceName);
		try {
			sResults.clear();
			openConnection();
			// On transforme la reponse du service Web en Hashtable
			String s = wsp.checkWebService();
			ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
			Properties dico = new Properties();
			dico.load(in);
			in.close();
			sResults.putAll(dico);
			if (dico.get("ws.status") != null)
				sResults.put(DTPrestaServicesConst.FormStatusKey, dico.get("ws.status"));
			else
				sResults.put(DTPrestaServicesConst.FormStatusKey, "NO");
			closeConnection(false);
		} catch (Throwable ex) {
			setError(CktlLog.getMessageForException(ex));
			sResults.put(DTPrestaServicesConst.FormStatusKey, "NO");
		}
		logEnd(DTPrestaServicesConst.ActionCheckWebServiceName);
		return sResults;
	}

	/**
	 * Retourne les etats possibles pour un devis. La reponse est constituee de la
	 * liste des couples cle-valeur. Le resultat peut etre utilisee pour "decoder"
	 * l'etant d'un devis.
	 * 
	 * <p>
	 * Dans le cas d'une erreur, le disctionnaire contient sa description avec la
	 * cle donnee dans <code>DTServicesConst.ErrorKey</code>.
	 * </p>
	 */
	public Hashtable allDevisEtats() {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionAllDevisEtatsName);
		try {
			sResults.clear();
			openConnection();
			// On transforme la reponse du service Web en Hashtable
			// sResults.putAll(wsp.getDevisEtats());
			closeConnection(false);
		} catch (Throwable ex) {
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionAllDevisEtatsName);
		return sResults;
	}

	/**
	 * Retourne le disctionnaire contenat l'etat du devis avec le code
	 * <code>devOrdre</code>.
	 * 
	 * <p>
	 * Dans le cas d'une erreur, le disctionnaire contient sa description avec la
	 * cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 * dictionnaire contient l'etat du devis accessible via la cle
	 * <code>DTPrestaServiceConst.FormStatusKey</code>. Voir la methode
	 * <code>getDevisEtats</code> pour la signification d'un code.
	 * </p>
	 * 
	 * @see #allDevisEtats()
	 */
	public Hashtable etatForDevis(Integer prestId) {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionEtatForDevisName);
		try {
			sResults.clear();
			openConnection();
			wsp.chargeCurrentDevisForClient(prestId, userPersId);
			/*
			 * sResults.put( DTPrestaServicesConst.FormStatusKey,
			 * wsp.getEtatCurrentDevis());
			 */
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionEtatForDevisName);
		return sResults;
	}

	/**
	 * Retourne le dictionnaire contenat l'etat du devis avec le code
	 * <code>devOrdre</code> dont le createur est pointe par <code>
	 * persId</code>.
	 * 
	 * <p>
	 * Dans le cas d'une erreur, le disctionnaire contient sa description avec la
	 * cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 * dictionnaire contient l'etat du devis accessible via la cle
	 * <code>DTPrestaServiceConst.FormStatusKey</code>. Voir la methode
	 * <code>getDevisEtats</code> pour la signification d'un code.
	 * </p>
	 * 
	 * @see #allDevisEtats()
	 */
	public Hashtable etatForDevis(Integer prestId, Integer persId) {
		logStart(null, userPersId.intValue(), prestId.intValue(),
				"persId", "prestId", DTPrestaServicesConst.ActionEtatForDevisName);
		try {
			sResults.clear();
			openConnection();
			// wsp.chargeCurrentDevisForClient(devOrdre, persId);
			wsp.chargeCurrentDevis(prestId);
			/*
			 * sResults.put( DTPrestaServicesConst.FormStatusKey,
			 * wsp.getEtatCurrentDevis());
			 */
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionEtatForDevisName);
		return sResults;
	}

	/**
	 * Retourne le disctionnaire contenat la liste des types des credits autorises
	 * pour les prestations. Ces valeurs peuvent etre utilisees pour autoriser la
	 * selection des lignes budgetaires lors de l'enregistrement des prestations.
	 * 
	 * <p>
	 * Dans le cas d'une erreur, le disctionnaire contient sa description avec la
	 * cle donnee dans <code>DTServicesConst.ErrorKey</code>. Sinon, le
	 * dictionnaire contient la descriotion des types de credits. L'entre
	 * <code>DTPrestaServicesConst.FormNbLignesKey</code> donne le nombre des
	 * codes dans la liste. Chaque code de type est accessible via la cle
	 * <code>DTPrestaServicesConst.FormTdcCodeKey&lt;n&gt;</code>.
	 * </p>
	 */
	public Hashtable typcredAutorises() {
		logStart(null, userPersId.intValue(), "persId",
				DTPrestaServicesConst.ActionTypcredAutorisesName);
		try {
			sResults.clear();
			openConnection();
			Vector types = wsp.getTypcredAutorises();
			if (types == null)
				types = new Vector();
			// On memorise d'abord le nombre des codes des types
			sResults.put(
					DTPrestaServicesConst.FormNbLignesKey,
					new Integer(types.size()));
			// Ensuite, on memorise les types eux m�me
			for (int i = 0; i < types.size(); i++) {
				sResults.put(
						DTPrestaServicesConst.FormTdcCodeKey + i,
						types.elementAt(i));
			}
			closeConnection(false);
		} catch (Throwable ex) {
			ex.printStackTrace();
			setError(CktlLog.getMessageForException(ex));
		}
		logEnd(DTPrestaServicesConst.ActionTypcredAutorisesName);
		return sResults;
	}

	/**
	 * Ouvre la connexion avec le service Web de la prestation.
	 */
	private void openConnection() throws Exception {
		wsp = new WSPrestationFactoryConsumer(
				PrestaProviderName, serviceURL, servicePass, getClass().getName());
		wsp.connect();
	}

	/**
	 * Clore la connexion avec le service Web de prestations. Si le parametre
	 * <code>save</code> est <em>true</em>, alors enregistre les modifications
	 * apportees au devis en cours. Retourne un dictionnaire contenant les
	 * identifiants de devis cree ou modifie ou <em>null</em>, si aucune
	 * modification n'a ete enregistree.
	 * 
	 * @param save
	 *          Indique si le devis doit etre enregistree avant la cloture de
	 *          connexion.
	 * 
	 * @return Le dictionnaire contenant les codes de devis s'il a ete enregistre.
	 */
	private Hashtable closeConnection(boolean save) throws Exception {
		Hashtable result = new Hashtable();
		if (save) {
			result.putAll(wsp.saveChanges());
		} else {
			result = null;
		}
		wsp.deconnect();
		return result;
	}
}
