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
import java.util.Hashtable;

import com.webobjects.appserver.WORequest;

import fr.univlr.cri.dt.services.common.DTServicesRequestCoder;

/**
 * Decode les parametres passes via une requette HTTP envoyee a une directe
 * action prestations.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTServicesRequestParams {
	/** Contient les parametres passes par une requette HTTP */
	private Hashtable params;

	/**
	 * Cree un objet avec contenant les parametres passes dams la requette HTTP
	 * representee par l'objet <code>req</code>.
	 */
	public DTServicesRequestParams(WORequest req) {
		params = DTServicesRequestCoder.decodeParams(req.contentString());
		// tmpdev
		// params = DTServicesRequestCoder.decodeParams(req.queryString());
	}

	/**
	 * Retourne la valeur correspondant au parametre <code>key</code>.
	 */
	public Object getObject(String key) {
		return params.get(key);
	}

	/**
	 * Retourne la chaine de caracteres correspondant au parametre
	 * <code>key</code>.
	 */
	public String getString(String key) {
		return (String) params.get(key);
	}

	/**
	 * Retourne la valeur entiere corrspondant au parametre <code>key</code>.
	 */
	public int getInt(String key) {
		return getInteger(key).intValue();
	}

	/**
	 * Retourne l'objet <code>Integer</code> correspondant au parametre
	 * <code>key</code>.
	 */
	public Integer getInteger(String key) {
		return Integer.valueOf((String) params.get(key));
	}
}
