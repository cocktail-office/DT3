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
package fr.univlr.cri.presta.services;

import java.util.Map;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.webservices.client.WOWebService;
import com.webobjects.webservices.client.WOWebServiceClient;

/**
 *  @deprecated
 *  
 * Classe abstraite pour les clients de Webservices.
 * 
 * @author Rodolphe Prin
 */
public abstract class ZWSConsumerDeprecated {
	
	/**
	 * Operations qui doivent etre accessibles sur le WebService pour pouvoir utiliser cette classe.
	 */
	public static final String[] REQUIREDOPERATIONS={"getLastExceptionMessage", "getProperties"};
	
	private WOWebServiceClient wsClient;
	private String applicationAlias;
	private String wsPassword;
	private boolean isConnected;
	private String wsName;
	private String wsUrl;
	
	/**
	 * On garde une r�f�rence � la session distante. 
	 * Ca permet de r�affecter la session d'un autre webservice 
	 * h�berg� sur la m?me application, ce qui permet au deux 
	 * webservices de se partager des infos puisqu'ils se retrouvent 
	 * sur la m?me session.  
	 */
	private WOWebService.SessionInfo mySessionInfo;
	
	/**
	 * Constructeur.
	 * 
	 * @param pwsName Nom du WebService
	 * @param pwsUrl Url d'acc?s au WebService
	 * @param appAlias Alias repr�sentant votre application (utilis�e par le WebService pour enregistrer votre client, et les logs).
	 * @param password Mot de passe pour utiliser le WebService 
	 * @throws Exception
	 */
	public ZWSConsumerDeprecated(String pwsName, String pwsUrl, String appAlias, String password) throws Exception {
		super();
		isConnected = false;
		wsName = pwsName;
		wsUrl = pwsUrl;
		applicationAlias = appAlias;
		wsPassword=password;
		wsClient = new WOWebServiceClient(new java.net.URL(wsUrl));
		checkWSRequirements();
	}
	
	/**
	 * Tente de se connecter au webService. 
	 */
	public void connect() throws Exception {
		//Appeler une m�thode de base pour cr�er une session distante
		Object[] wsArgs = {applicationAlias, wsPassword};
		wsClient.invoke(wsName,"connect",wsArgs);
		//V�rifier s'il y a eu des erreurs
		throwLastWsException();
		//M�moriser la session
		mySessionInfo = wsClient.sessionInfoForServiceNamed(wsName);
		isConnected = true;
	}
	
	/**
	 * Se d�connecter du Webservice (� appeler �ventuellement lorsque toutes les op�rations sont effectu�es). Cette action supprime la session sur le serveur.
	 * 
	 * @throws Exception
	 */
	public void deconnect() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ wsName +" n'est pas active. Impossible de se d�connecter.");
		}	
		wsClient.invoke(wsName,"deconnect",null);		
		throwLastWsException();		
		setConnected(false);
	}	
	
	/**
	 * Indique si on est connect� au webservice/
	 * @return
	 */
	public boolean isConnected() {
		return isConnected;
	}
	
	/**
	 * @param b
	 */
	protected void setConnected(boolean b) {
		isConnected = b;
	}
	
	/**
	 * R�cup?re le message de la derni?re exception g�n�r�e par le webService. 
	 */
	protected void throwLastWsException() throws Exception {
		String s = (String)wsClient.invoke(wsName,"getLastExceptionMessage",null);
		if (s!=null) {
			throw new Exception("["+wsName+"] " + s);	
		}
	}
	
	/**
	 * @return
	 */
	private String[] requiredOperations() {	
		return REQUIREDOPERATIONS;
	}
	
	/**
	 * V�rifie que le WebService poss?de les op�rations requises � l'utilisation de ce client.
	 * 
	 * @throws Exception
	 */
	private void checkWSRequirements() throws Exception {
		NSDictionary dic = wsClient.operationsDictionaryForService(wsName);
		for (int i = 0; i < requiredOperations().length; i++) {
			String string = requiredOperations()[i];
			if (dic.valueForKey(string)==null) {
				throw new Exception("L'operation " + string +" n'est pas d�finie pour le WebService "+wsName+". Votre classe d'invocation ne peut pas h�riter de ZWSInvocator.");
			}			
		}
	}
	
	/**
	 * G�n?re une exception si isConnected() renvoie false.
	 */
	protected void checkConnection() throws Exception {
		if (!isConnected()) {
			throw new Exception("La connexion au service"+ getWsName() +" n'est pas active");
		}		
	}
	
	/**
	 * M�thode utilitaire qui convertit un tableau d'objets Map en NSArray d'objets NSDictionary. 
	 * 
	 * @param objs Tableau de Map
	 * @return 
	 */
	public NSArray arrayMapsToNSArrayNSDictionarys(Object[] objs) throws Exception {
		//On transforme le tableau de map en NSArray de NSDictionary
		NSMutableArray tmpres = new NSMutableArray();
		for (int i = 0; i < objs.length; i++) {
			Map object = (Map)objs[i];
			tmpres.addObject( mapToNSDictionary(object));
		}
		return tmpres.immutableClone();		
	}	
	
	
	public NSDictionary mapToNSDictionary(Object obj  ) throws Exception  {
		Map object = (Map)obj;
		return  new NSDictionary( object.values().toArray(), object.keySet().toArray());
	}
	
	/**
	 * Equivaut �  wsClient.invoke(getWsName(),operationName,args);
	 * 
	 * @param operationName
	 * @param args
	 * @return
	 */
	public Object wsInvoke(String operationName, Object[] args) {
		return wsClient.invoke(getWsName(),operationName,args);
	}
	
	/**
	 * @return
	 */
	public String getApplicationAlias() {
		return applicationAlias;
	}
	
	/**
	 * @return
	 */
	public WOWebService.SessionInfo getMySessionInfo() {
		return mySessionInfo;
	}
	
	/**
	 * @return
	 */
	public WOWebServiceClient getWsClient() {
		return wsClient;
	}
	
	/**
	 * @return Le nom du WebService
	 */
	public String getWsName() {
		return wsName;
	}
	
	/**
	 * @return L'url du WebService.
	 */
	public String getWsUrl() {
		return wsUrl;
	}
	
	/**
	 * @return Les infos du Webservice contenant le n� de version, etc. sous forme de Map
	 */
	public Map getProperties() {
		return (Map)wsInvoke("getProperties", null);
	}	
}
