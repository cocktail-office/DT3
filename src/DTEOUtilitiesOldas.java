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
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTEOUtilitiesOldas {
  
  /**
   * Execute la requete donnee et retourne les objets correspondants. Chaque
   * represente par un dictionnaire.
   * 
   * @param ec
   *        Un editing context
   * @param modelName
   *        Le nom du model a utiliser
   * @param sqlString
   *        La requete SQL a executer
   * @param oldKeys
   *        Les noms des atttributs selectionnes. Si cette liste est <em>null</em>,
   *        l'ordre des attributs sera "devine" (elle peut etre incorrecte).
   * @param newKeys
   *        Les noms des attributs correspondant a la liste SELECT. Ils seront
   *        utilises comme les cles des valeurs dans les dictionnaires.
   * @return La liste des objets (<code>NSArray</code> des
   *         <code>NSDictionnary</code>).
   */
  public static NSArray rawRowsForSQL(EOEditingContext ec, String modelName,
                                      String sqlString, NSArray oldKeys,
                                      NSArray newKeys)
  {
    // TODO Utiliser Java reflection pour ne pas avoir le message deprecated
    return
      renameRawRowsKeys(
          EOUtilities.rawRowsForSQL(ec, modelName, sqlString), oldKeys, newKeys);
  }
  
  /**
   * Renome les cles des objets de la liste.
   * 
   * @param rows La liste des objets (des <code>NSDictionary</code>).
   * @param keys Les nouveaux noms des cles des objets.
   * 
   * @return La liste de memes objets ayant les cles renomes.
   */
  public static NSArray renameRawRowsKeys(NSArray rows, NSArray oldKeys, NSArray newKeys) {
    if (rows.count() > 0) {
      NSDictionary oldRec;
      NSMutableDictionary newRec;
      NSMutableArray newRows = new NSMutableArray();
      for(int i=0; i<rows.count(); i++) {
        oldRec = (NSDictionary)rows.objectAtIndex(i);
//        CktlLog.trace("old.rec : "+oldRec);
        if (oldKeys == null)
          oldKeys = getAttributesFromRec(oldRec);
//        CktlLog.trace("all keys : "+oldKeys);
        newRec = new NSMutableDictionary();
        for(int k=0; k<oldKeys.count(); k++) {
          // Il parait que les cles sont dans l'ordre inverse
          newRec.setObjectForKey(
              oldRec.objectForKey(oldKeys.objectAtIndex(k)),
              newKeys.objectAtIndex(k));
        }
//        CktlLog.trace("new.rec : "+newRec);
        newRows.addObject(newRec);
      }
      return newRows;
    }
    return rows;
  }
  
  /**
   * 
   */
  private static NSArray getAttributesFromRec(NSDictionary oldRec) {
    /* Implementation differente entre les versions WO4 et WO5 */
    // Sous WO5, on retourne simplement les objets
    return oldRec.allKeys();
  }
}
