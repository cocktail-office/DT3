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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.URLCtrl;

/**
 * Propose des methodes pour encoder ou decoder le contenu des requettes HTTP.
 * Cette classe propose un implementation compatible entre differentes versions
 * de Java (1.1+).
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTServicesRequestCoder {
  /**
   * Convertie le contenu <code>content</code> d'une requette/reponse HTTP en
   * un dictionnaire des couples cle-valeur.
   * 
   * <p>
   * Cette methode doit etre utilisee pour decoder les parametres encodes avec
   * la methode <code>encodeParams</code>.
   * </p>
   * 
   * @see #encodeParams(Hashtable)
   */
  public static Hashtable decodeParams(String content) {
    Hashtable newResp = new Hashtable();
    Properties resp = new Properties();
    content = StringCtrl.replace(content, "&", "\n");
    try {
      resp.load(new ByteArrayInputStream(content.getBytes()));
    } catch (IOException ex) {
      // En principe, on ne devrait pas avoir d'erreur
    }
    Enumeration keys = resp.keys();
    String key, value;
    while(keys.hasMoreElements()) {
      key = URLCtrl.decode((String)keys.nextElement());
      value = URLCtrl.decode((String)resp.get(key));
      newResp.put(key, value);
    }
    return newResp;
  }

  /**
   * Encode les parametres donnes dans <code>params</code> dans une chaine de
   * caracteres. La valeur obtenue peut etre envoyee via une requette HTTP.
   * 
   * <p>
   * Les valeurs encodees par cette methode peuvent etre decodees en appelant
   * <code>decodeParams</code>.
   * </p>
   * 
   * @param params
   *        Les couples cle-valeur correspondant aux parametres d'une requette
   *        HTTP.
   * 
   * @see #decodeParams(String)
   */
  public static String encodeParams(Hashtable params) {
    StringBuffer content = new StringBuffer();
    Enumeration keys = params.keys();
    String value, key;
    while(keys.hasMoreElements()) {
      key = (String)keys.nextElement();
      value = params.get(key).toString();
      if (value != null) {
        if (content.length() > 0) content.append("&");
        content.append(URLCtrl.encode(key)).append("=");
        content.append(URLCtrl.encode(value));
      }
    }
    return content.toString();
  }
}
