/*
 * Copyright CRI - Universite de La Rochelle, 1995-2004 
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
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOWebServiceUtilities;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;
import fr.univlr.cri.dt.services.common.DTServicesConst;

/**
 * Le gestionnaire d'acces au service Web Prestations.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTPrestaBusWeb {
	// /** L'instance du gestionnaire d'acces au service */
	// private static DTPrestaBusWeb theInstance;

	private DTPrestaServicesImp servicePerformer;
	/** Indique si le service Web Prestations est disponible */
	private Boolean pieServiceDispo;
	/** Le dictionnaire de tous les etats possibles pour un devis */
	private Hashtable allDevisEtats;
	/**
	 * La liste des codes des types de credit autorises a etre selectionnes avec
	 * une ligne budgetaire.
	 */
	private NSMutableArray typcredAutorises;

	private String errorMessage;

	private Integer userPersId;
	private Integer userServiceFouOrdre;
	private boolean showLogs;

	public DTPrestaBusWeb(Session session) {
		userPersId = (Integer) session.dtUserInfo().persId();
		showLogs = true;
	}

	public DTPrestaBusWeb(Integer userId, Integer userServiceFouOrdre, boolean showLogs) {
		this.userPersId = userId;
		this.userServiceFouOrdre = userServiceFouOrdre;
		this.showLogs = showLogs;
	}

	public void setUser(Integer userId, Integer userServiceFouOrdre) {
		this.userPersId = userId;
		this.userServiceFouOrdre = userServiceFouOrdre;
		// forcer la mise a jour du lien vers pie avec des nouvelles infos
		servicePerformer = null;
	}

	// /**
	// * Retourne l'instance du gestionnaire de communication avec le service Web
	// * Prestations. Il n'existe qu'une seule instance par application.
	// */
	// public static DTPrestaBusWeb getInstance() {
	// if (theInstance == null) theInstance = new DTPrestaBusWeb();
	// return theInstance;
	// }

	/**
	 * Retourne la reference vers l'applicetion en cours d'execution.
	 */
	private Application dtApp() {
		return (Application) WOApplication.application();
	}

	/**
	 * Retourne l'instance de l'objet qui realise les actions du service.
	 */
	public DTPrestaServicesImp servicePerformer() {
		if (servicePerformer == null) {
			servicePerformer =
					new DTPrestaServicesImp(
							dtApp().config().stringForKey("APP_PRESTA_WS_URL"),
							dtApp().config().stringForKey("APP_PRESTA_WS_PASS"));
			servicePerformer.setContext(WOWebServiceUtilities.currentWOContext());
			servicePerformer.setUser(userPersId, userServiceFouOrdre);
			servicePerformer.setShowLogs(showLogs);
			servicePerformer.setServiceKey("ws PIE");
		}
		return servicePerformer;
	}

	/**
	 * Retourne le dernier message survenue lors de la communication avec les
	 * service prestations.
	 */
	public String errorMessage() {
		return errorMessage;
	}

	/**
	 * Teste si la derniere operation de communication avec le service Web
	 * prestations s'est deroulee sans erreurs.
	 */
	public boolean hasError() {
		return (errorMessage != null);
	}

	/**
	 * Memorise le message d'erreur enregistre dans le dictionnaire <em>aMap</em>.
	 * Le message est enregistre avec la cle dont la valeur est donnee dans la
	 * constante <code>ErrorKey</code>.
	 * 
	 * @see #errorMessage()
	 */
	protected void takeError(Hashtable aMap) {
		errorMessage = (String) aMap.get(DTServicesConst.ErrorKey);
		logError(errorMessage);
	}

	/**
	 * Memorise le message d'erreur a partir de l'exception <code>ex</code>.
	 * 
	 * @see #errorMessage()
	 */
	protected void takeError(Throwable ex) {
		StringBuffer msg = new StringBuffer();
		msg.append(ex.getClass().getName());
		if (ex.getMessage() != null)
			msg.append(" : ").append(ex.getMessage());
		errorMessage = msg.toString();
		logError(errorMessage);
		ex.printStackTrace();
	}

	private void logError(String message) {
		if (errorMessage != null)
			DTLogger.logError("Service Web Prestations : " + message);
	}

	/**
	 * Test la disponibilite des services Web Prestations.
	 */
	public synchronized boolean checkPIEService() {
		// On verifie si la config indique que le service PIE est dispo
		// On ne fera le test qu'une fois pour une instance du bus
		if (pieServiceDispo == null) {
			boolean dispo = true;
			// Test si les presta sont utilisee et le URL est bien defini
			dispo = dtApp().config().booleanForKey("APP_USE_PIE") && dtApp().appURL() != null;
			// On test egalement si le service repond vraiment
			if (dispo) {
				Hashtable hash = checkWebService();
				if (!hasError())
					dispo = hash.get(DTPrestaServicesConst.FormStatusKey).equals("YES");
				else
					dispo = false;
			}
			pieServiceDispo = new Boolean(dispo);
		}
		return pieServiceDispo.booleanValue();
	}

	/**
	 * Forcer l'application a retester la disponibilité du service PIE
	 */
	public void resetPieServiceDispo() {
		pieServiceDispo = null;
	}

	/**
	 * Teste si le service Web Prestations est disponible. Retourne le
	 * dictionnaire contenant la description du service.
	 */
	public Hashtable checkWebService() {
		Hashtable result = null;
		try {
			// Recuperer la reponse
			result = servicePerformer().checkWebService();
			takeError(result);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return result;
	}

	/**
	 * Cree un nouveau devis avec les article donnes dans la liste
	 * <code>lignes</code>. Retourne les codes de nouveau devis sous forme d'une
	 * <code>Hashtable</code>.
	 * 
	 * <p>
	 * Cette methode permet d'indiquer explicitement le type de devis et le
	 * commentaire de la prestation.
	 * </p>
	 */
	public Hashtable createDevis(Integer typeDevis, String devisLibelle,
			String prestaComment, Integer orgId, String tcdCode, Integer lolfId,
			String pcoNum, Vector lignes) {
		Hashtable prestResult = new Hashtable();
		try {
			// Recuperer la reponse
			Hashtable result =
					servicePerformer().createDevis(
							typeDevis, devisLibelle, prestaComment, orgId, tcdCode,
							lolfId, pcoNum, lignes);
			takeError(result);
			if (!hasError()) {
				prestResult.put(
						DTPrestaServicesConst.FormPrestIdKey,
						Integer.valueOf(result.get(DTPrestaServicesConst.FormPrestIdKey).toString()));
				prestResult.put(
						DTPrestaServicesConst.FormPrestNumeroKey,
						Integer.valueOf(result.get(DTPrestaServicesConst.FormPrestNumeroKey).toString()));
			}
		} catch (Throwable ex) {
			takeError(ex);
		}
		return prestResult;
	}

	/**
	 * Cree un nouveau devis avec les article donnes dans la liste
	 * <code>lignes</code>. Retourne les codes de nouveau devis sous forme d'une
	 * <code>Hashtable</code>
	 * 
	 * <p>
	 * Cette methode cree un devis "interne" sans aucun commentaire.
	 * </p>
	 */
	public Hashtable createDevis(String devisLibelle,
			Integer orgId, String tcdCode, Integer lolfId, String pcoNum,
			Vector lignes) {
		return createDevis(DTPrestaServicesConst.TypeDevisInterne,
				devisLibelle, null, orgId, tcdCode, lolfId, pcoNum, lignes);
	}

	/**
	 * Remplace les article d'un devis par ceux donnes dans la liste
	 * <code>lignes</code>. Retourne <code>true</code> si l'operation est un
	 * succes.
	 */
	public Boolean updateDevis(Integer prestId, Vector lignes) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result =
					servicePerformer().updateDevis(prestId, lignes);
			takeError(result);
			if (!hasError()) {
				success = new Boolean(result.get(DTPrestaServicesConst.FormPrestIdKey) != null);
			}
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	/**
	 * @deprecated
	 * 
	 *             Remplace les article d'un devis par ceux donnes dans la liste
	 *             <code>lignes</code>. Retourne <code>true</code> si l'operation
	 *             est un succes.
	 */
	public Boolean updateDevis(Integer prestId, Integer persId, Vector lignes) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result =
					servicePerformer().updateDevis(prestId, persId, lignes);
			takeError(result);
			if (!hasError()) {
				success = new Boolean(result.get(DTPrestaServicesConst.FormPrestIdKey) != null);
			}
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	/**
	 * Calcule et retourne le prix estimatif que pourrait avoir le devis compose
	 * des articles donnes dans la liste <code>lignes</code>.
	 */
	public Double estimeTTCDevis(Vector lignes) {
		Double somme = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().estimeTTCDevis(lignes);
			takeError(result);
			if (!hasError())
				somme = (Double) result.get(DTPrestaServicesConst.FormSommeKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return somme;
	}

	/**
	 * Teste si la demande en cours est payante et/ou si le devis est valide.
	 */
	public Boolean prestaRealisee(Integer prestId) {
		Boolean flag = Boolean.FALSE;
		if (prestId != null) {
			try {
				// Recuperer la reponse
				Hashtable result = servicePerformer().prestaRealisee(prestId);
				takeError(result);
				if (!hasError())
					flag = (Boolean) result.get(DTPrestaServicesConst.FormBoolResultKey);
			} catch (Throwable ex) {
				takeError(ex);
			}
		} else {
			flag = Boolean.TRUE;
		}
		return flag;
	}

	/**
	 * @deprecated
	 * @see #prestaRealisee(Integer)
	 * 
	 *      Teste si la demande en cours est payante et/ou si le devis est valide.
	 */
	public Boolean prestaRealisee(Integer prestId, Integer persId) {
		Boolean flag = Boolean.FALSE;
		if (prestId != null) {
			try {
				// Recuperer la reponse
				Hashtable result = servicePerformer().prestaRealisee(prestId, persId);
				takeError(result);
				if (!hasError())
					flag = (Boolean) result.get(DTPrestaServicesConst.FormBoolResultKey);
			} catch (Throwable ex) {
				takeError(ex);
			}
		} else {
			flag = Boolean.TRUE;
		}
		return flag;
	}

	/**
	 * Devalide le devis associe avec la demande.
	 */
	public Boolean deleteDevis(Integer prestId) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().deleteDevis(prestId);
			takeError(result);
			success = new Boolean(!hasError());
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	/**
	 * Devalide le devis associe avec la demande sans verification du demandeur
	 * dans prestation
	 */
	public Boolean deleteDevisSansControle(Integer prestId) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().deleteDevisSansControle(prestId);
			takeError(result);
			success = new Boolean(!hasError());
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	/**
	 * Retourne le code de l'etat du devis avec l'ordre <code>prestId</code>.
	 */
	public BigDecimal sommeTTCDevis(Integer prestId) {
		BigDecimal somme = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().sommeTTCDevis(prestId);
			takeError(result);
			if (!hasError())
				somme = (BigDecimal) result.get(DTPrestaServicesConst.FormSommeKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return somme;
	}

	/**
	 * @deprecated
	 * 
	 *             Retourne le code de l'etat du devis come cle
	 *             <code>prestId</code> dont le createur est pointe par
	 *             <code>persId</code>.
	 */
	public BigDecimal sommeTTCDevis(Integer prestId, Integer persId) {
		BigDecimal somme = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().sommeTTCDevis(prestId, persId);
			takeError(result);
			if (!hasError())
				somme = (BigDecimal) result.get(DTPrestaServicesConst.FormSommeKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return somme;
	}

	/**
	 * Supprimer tous les articles du devis <code>prestId</code> dont le createur
	 * est pointe par <code>persId</code>. Retourne <code>true</code> si
	 * l'operation a reussit.
	 */
	public Boolean viderLignes(Integer prestId) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().viderLignes(prestId);
			takeError(result);
			if (!hasError())
				success = (Boolean) result.get(DTPrestaServicesConst.FormBoolResultKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	/**
	 * Valider le devis d'ordre <code>prestId</code> dont le createur est pointe
	 * par <code>persId</code>. Retourne <code>true</code> si l'operation reussit.
	 */
	public Boolean validerToutDevis(Integer prestId, Integer persId) {
		Boolean success = Boolean.FALSE;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().validerToutDevis(prestId, persId);
			takeError(result);
			if (!hasError())
				success = (Boolean) result.get(DTPrestaServicesConst.FormBoolResultKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return success;
	}

	//
	// /**
	// * Valider le devis d'ordre <code>devOrdre</code> dont le createur est
	// pointe
	// * par <code>persId</code> et celui le valideur est designe par
	// <code>agtOrde</code>.
	// * Retourne <code>true</code> si l'operation reussit.
	// */
	// public Boolean validerToutDevis(Integer devOrdre, Integer agtOrdre) {
	// Boolean success = Boolean.FALSE;
	// try {
	// // Recuperer la reponse
	// Hashtable result = servicePerformer().validerToutDevis(devOrdre, agtOrdre);
	// takeError(result);
	// if (!hasError())
	// success = (Boolean) result.get(DTPrestaServicesConst.FormBoolResultKey);
	// } catch (Throwable ex) {
	// takeError(ex);
	// }
	// return success;
	// }

	/**
	 * Retourne le code de l'etat du devis avec l'ordre <code>prestId</code>. On
	 * peut utiliser la methode etatForDevisLibelle pour recuperer le libelle qui
	 * correspond a cet etat.
	 */
	public String etatForDevis(Integer prestId) {
		String etat = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().etatForDevis(prestId);
			takeError(result);
			if (!hasError())
				etat = (String) result.get(DTPrestaServicesConst.FormStatusKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return etat;
	}

	/**
	 * Retourne le code de l'etat du devis avec l'ordre <code>prestId</code>. On
	 * peut utiliser la methode etatForDevisLibelle pour recuperer le libelle qui
	 * correspond a cet etat.
	 */
	public String etatForDevis(Integer prestId, Integer persId) {
		String etat = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().etatForDevis(prestId, persId);
			takeError(result);
			if (!hasError())
				etat = (String) result.get(DTPrestaServicesConst.FormStatusKey);
		} catch (Throwable ex) {
			takeError(ex);
		}
		return etat;
	}

	/**
	 * Retourne la description de l'etat actuel du devis avec le code
	 * <code>prestId</code>.
	 * 
	 * @see #etatForDevis(Integer)
	 * @see #allDevisEtats()
	 */
	public String etatForDevisLibelle(Integer prestId) {
		String etat = etatForDevis(prestId);
		if (etat != null)
			return (String) allDevisEtats().get(etat);
		else
			return "Inconnue";
	}

	/**
	 * Retourne la description de l'etat actuel du devis avec le code
	 * <code>prestId</code> et le <code>persId</code> du createur du devis.
	 * 
	 * @see #etatForDevis(Integer, Integer)
	 * @see #allDevisEtats()
	 */
	public String etatForDevisLibelle(Integer prestId, Integer persId) {
		String etat = etatForDevis(prestId, persId);
		if (etat != null)
			return (String) allDevisEtats().get(etat);
		else
			return "Inconnue";
	}

	/**
	 * Retourne tous les articles d'un catalogue.
	 * 
	 * @param catId
	 *          : la cle du catalogue.
	 * 
	 * @return Returne le dictionnaire avec les resultats de l'operation. Dans le
	 *         cas d'une erreur, le disctionnaire contient sa description avec la
	 *         cle donnee dans <code>DTServicesConst.ErrorKey</code>.
	 */
	public NSArray getCatalogueArticles(Integer catId) {
		NSMutableArray articles = new NSMutableArray();
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().getCatalogueArticles(catId);
			takeError(result);
			if (!hasError()) {
				// On parcure la reponse et on extrait les codes des types.
				int nbArticles = StringCtrl.toInt(
						result.get(DTPrestaServicesConst.FormNbLignesKey).toString(), -1);
				for (int i = 0; i < nbArticles; i++)
					articles.addObject(result.get(DTPrestaServicesConst.FormCaarIdKey + i));
			}
		} catch (Throwable ex) {
			takeError(ex);
		}
		return articles.immutableClone();
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
		Hashtable devis = null;
		try {
			// Recuperer la reponse
			devis = servicePerformer().inspectDevis(prestId);
			takeError(devis);
		} catch (Throwable ex) {
			takeError(ex);
		}
		if (hasError()) {
			devis = new Hashtable();
		}
		return devis;
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
		Hashtable devis = null;
		try {
			// Recuperer la reponse
			devis = servicePerformer().inspectDevis(prestId, persId, structPersId);
			takeError(devis);
		} catch (Throwable ex) {
			takeError(ex);
		}
		if (hasError()) {
			devis = new Hashtable();
		}
		return devis;
	}

	/**
	 * Retourne le dictionaire des etats possibles pour un devis. Les cles de
	 * dictionnaire sont composees des codes d'etats, et le valeurs indiquent les
	 * libelles associes. Le resultat de cette methode peut etre utilisee pour
	 * recuperer la description "explicite" d'un etat avec un code donne.
	 */
	public Hashtable allDevisEtats() {
		if (allDevisEtats == null) {
			try {
				// Recuperer la reponse
				allDevisEtats = servicePerformer().allDevisEtats();
				takeError(allDevisEtats);
			} catch (Throwable ex) {
				takeError(ex);
			}
			if (hasError())
				allDevisEtats = new Hashtable();
		}
		return allDevisEtats;
	}

	/**
	 * Retourne la liste de tous les types de credits autorise a utiliser avec les
	 * lignes budgetaires. La liste est composee des objets <code>Integer</code>.
	 */
	public NSArray allTypcredAutorises() {
		if (typcredAutorises == null) {
			typcredAutorises = new NSMutableArray();
			try {
				// Recuperer la reponse
				Hashtable result = servicePerformer().typcredAutorises();
				takeError(result);
				if (!hasError()) {
					// On parcure la reponse et on extrait les codes des types.
					int nbTypes = StringCtrl.toInt(
							result.get(DTPrestaServicesConst.FormNbLignesKey).toString(), -1);
					for (int i = 0; i < nbTypes; i++)
						typcredAutorises.addObject(result.get(DTPrestaServicesConst.FormTdcCodeKey + i));
				}
			} catch (Throwable ex) {
				takeError(ex);
			}
		}
		return typcredAutorises;
	}

	/**
	 * Retourne le total disponible sur la ligne budgetaire passee en parametre.
	 */
	public Double sommeLigneBud(Integer orgId, String tcdCode) {
		Double somme = null;
		try {
			// Recuperer la reponse
			Hashtable result = servicePerformer().sommeLigneBud(orgId, tcdCode);
			takeError(result);
			if (!hasError())
				somme = (Double) result.get(DTPrestaServicesConst.FormSommeKey);
		} catch (Throwable ex) {
			ex.printStackTrace();
			takeError(ex);
		}
		return somme;

	}
}
