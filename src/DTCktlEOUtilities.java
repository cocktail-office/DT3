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
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.cocktail.fwkcktlwebapp.common.CktlLog;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Les extentions de la classe EOUtlities y ajoutant des fonctionnalites
 * supplementaires (notamment, des methodes de WebObject version 5.x).
 * 
 * 
 * <p>Cette classe est introduite pour garder la compatibilite de code
 * entre les versions 4.5.1, 5.2.1 et 5.2.3 de WebObjects.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTCktlEOUtilities  {
  
  private static String SQLDelimiters = " ,;";
  private static Boolean useWO523 = null;
  
  public static void main(String[] args) {
    String sql = "SELECT MOI me, you, bd.him, NOUS we, DB.VOUS da_you FROM TOUS";
//    String sql = "SELECT TOUS.* FROM TOUS";
    CktlLog.rawLog("SQL : "+sql);
    CktlLog.rawLog("KEYS : "+getKeysFromSQLSelect(sql));
  }
  
  /**
   * Execute la requete donnee et retourne les objets correspondants. Chaque
   * entre de la liste represente par un dictionnaire.
   * 
   * @param ec
   *        Un editing context
   * @param modelName
   *        Le nom du model a utiliser
   * @param sqlString
   *        La requete SQL a executer
   * @param newKeys
   *        Les noms des attributs correspondant a la liste SELECT. Ils seront
   *        utilises comme les cles des valeurs dans les dictionnaires.
   * @return La liste des objets (<code>NSArray</code> des
   *         <code>NSDictionnary</code>).
   */
  public static NSArray rawRowsForSQL(EOEditingContext ec, String modelName,
                                      String sqlString, NSArray newKeys)
  {
    NSArray dicos = new NSArray();
    // On essaye d'abord d'appeler la verson 5.2.3 de la methode
    if ((useWO523 == null) || useWO523.booleanValue()) {
      dicos = callRawRowsWO523(ec, modelName, sqlString, newKeys);
    }
    // Si le WO 5.2.3 echoue, on utilisera l'appel de la version 5.2.1.  
    if (!useWO523.booleanValue()) {
      dicos = callRawRowsWO521(ec, modelName, sqlString);
      if ((dicos.count() > 0) && (!isEmptyArray(newKeys))) {
        NSArray oldKeys = getKeysFromSQLSelect(sqlString);
        dicos = renameRawRowsKeys(dicos, oldKeys, newKeys);
      }
    }
    return dicos;
  }
  
  /**
   * Evoque la methode <code>EOUtilities.rawRowsForSQL</code> avec la signature
   * de la version disponible dans WebObjects 5.2.3. L'implementation utilise
   * la reflection de Java afin d'appeler effectivement la methode. Ceci evite
   * d'avoir les erreurs dans le cas ou une application est deploiee dans
   * l'environement WebObjects 5.2.1. 
   * 
   * Retourne un tableau des dictionnaires avec le resultat de la requette.
   * Retourne le tableau vide (count() == 0) si l'expression
   * <code>sqlString</code> ne selectionne aucun objet.
   */
  private static NSArray callRawRowsWO523(EOEditingContext ec, String modelName,
                                          String sqlString, NSArray newKeys)
  {
    NSArray result = new NSArray();
    CktlLog.trace("fetching with 523 : "+sqlString);
    try {
      Class[] argTypes = {
          EOEditingContext.class,
          String.class, // modelName
          String.class, // sqlString
          NSArray.class, // keys
          };
      CktlLog.trace("newKeys : "+newKeys);
      Object[] argValues = { ec, modelName, sqlString, newKeys };
      Method m = EOUtilities.class.getMethod("rawRowsForSQL", argTypes);
      result = (NSArray)m.invoke(null, argValues);
      useWO523 = Boolean.TRUE;
      CktlLog.trace("EOUtilities.rawRowsForSQL : using 5.2.3 version");
    } catch (Exception ex) {
      useWO523 = Boolean.FALSE;
    }
    return result;
  }
  
  /**
   * Evoque la methode <code>EOUtilities.rawRowsForSQL</code> avec la signature
   * de la version disponible dans WebObjects 5.2.1. L'implementation utilise
   * la reflection de Java afin d'appeler effectivement la methode. Ceci evite
   * d'avoir les messages "deprecated" lors de la compilation d'une application. 
   * 
   * Retourne un tableau des dictionnaires avec le resultat de la requette.
   * Retourne le tableau vide (count() == 0) si l'expression
   * <code>sqlString</code> ne selectionne aucun objet.
   */
  private static NSArray callRawRowsWO521(EOEditingContext ec, String modelName,
      String sqlString)
  {
    NSArray result = new NSArray();
    CktlLog.trace("fetching with 521 : "+sqlString);
    try {
      Class[] argTypes = {
          EOEditingContext.class,
          String.class, // modelName
          String.class  // sqlString
      };
      Object[] argValues = { ec, modelName, sqlString };
      Method m = EOUtilities.class.getMethod("rawRowsForSQL", argTypes);
      result = (NSArray)m.invoke(null, argValues);
      CktlLog.trace("EOUtilities.rawRowsForSQL : using 5.2.1 version");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  /**
   * Retourne le table avec les noms des colonnes seletionnees dans une
   * expression SQL. Cette methode analyse la partie SELECT d'une requette
   * SQL. 
   */
  private static NSArray getKeysFromSQLSelect(String sql) {
    NSMutableArray keys = new NSMutableArray();
    StringTokenizer st = new StringTokenizer(sql, SQLDelimiters, true);
    String token;
    boolean inSelect = false;
    String currentKey = null;
    while(st.hasMoreTokens()) {
      token = st.nextToken();
      // Si on est entre dans la partie SELECT
      if (inSelect) {
        // Si on est arrive jusqu'a FROM, on arrete
        if (token.equalsIgnoreCase("FROM")) {
          // On ajoute le nom de la colonne, s'il ne l'etait pas encore
          if (currentKey != null)
            keys.addObject(getSuffix(currentKey, "."));
          break;
        }
        // Si ceci n'est pas en separateur des colonnes (",")
        if (!token.equals(",")) {
          // .. et si ce n'est pas une etoile "*" (on ne peut rien faire avec
          // les etoiles), alors on memorise le nom de la colonne
          if ((SQLDelimiters.indexOf(token) == -1) && (token.indexOf("*") == -1))
            currentKey = token;
        } else {
          // Si on est arrive jusqu'a ",", alors c'est la fin de la
          // definition d'une colonne
          if (currentKey != null) {
            // Avant d'ajouter, on enleve le nom de la table ou de son alias,
            // s'il est present. C'est le fonctionnement de EOUtilities sous
            // WO 5.2.3.
            keys.addObject(getSuffix(currentKey, "."));
            currentKey = null;
          }
        }
      } else if(token.equalsIgnoreCase("SELECT")) {
        // On marque qu'on est entre dans la zone "SELECT"
        inSelect = true;
      }
    }
    return keys;
  }
  
  /**
   * Renome les cles des objets de la liste.
   * 
   * @param rows La liste des objets (des <code>NSDictionary</code>).
   * @param keys Les nouveaux noms des cles des objets.
   * 
   * @return La liste de memes objets ayant les cles renomes.
   */
  private static NSArray renameRawRowsKeys(NSArray rows, NSArray oldKeys, NSArray newKeys) {
    if ((rows.count() > 0) && (oldKeys != null) && (oldKeys.count() > 0)) {
      NSDictionary oldRec;
      NSMutableDictionary newRec;
      NSMutableArray newRows = new NSMutableArray();
      for(int i=0; i<rows.count(); i++) {
        oldRec = (NSDictionary)rows.objectAtIndex(i);
        newRec = new NSMutableDictionary();
        for(int k=0; k<oldKeys.count(); k++) {
          newRec.setObjectForKey(
              oldRec.objectForKey(oldKeys.objectAtIndex(k)),
              newKeys.objectAtIndex(k));
        }
        newRows.addObject(newRec);
      }
      return newRows;
    }
    return rows;
  }

  // TODO Migrer cette methode dans LRArray
  private static boolean isEmptyArray(NSArray array) {
    return ((array == null) || (array.count() == 0));
  }
  
  // TODO Cette correction doit etre apportee a la methode de StringCtrl
  public static String getSuffix(String line, String substring) {
    int i = line.indexOf(substring);
    if (i >= 0) {
      return line.substring(i+substring.length());
    } else {
      return line;
    }
  }
}
