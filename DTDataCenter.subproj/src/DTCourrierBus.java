/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
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
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;

/**
 * Gestion d'acces aux informations disponibles dans l'utilisateur Courrier.
 *
 * Cette implementation gere l'acces aux mots cles.
 * 
 * @deprecated utiliser le framework GediBus
 */
public class DTCourrierBus extends DTDataBus {

  /**
   * Le tri des mots cles.
   */
  private static NSArray sort = null;
  
  /**
   * Cree un nouveau gestionnaire.
   */
  public DTCourrierBus(EOEditingContext econtext) {
    super(econtext);
  }
  
  /**
   * Retourne la definition de tri. Cree une nouvelle definition, si
   * la methode est appelee pour la premiere fois.
   */
  private NSArray libelleSort() {
    if (sort == null) sort = CktlSort.newSort("mcLibelle");
    return sort;
  }
  
  /**
   * Retourne la liste des mots cles qui sont similaires au mot cle
   * donne. Le mot cle peut utiliser les caracteres '*' et '?'.
   */
  public NSArray findMotsClefsLike(String motCle) {
    EOQualifier qualif = null;
    if (StringCtrl.normalize(motCle).length() > 0)
      qualif = newCondition("mcLibelle caseInsensitiveLike '"+motCle+"'");
    setRefreshFetchedObjects(false);
    return fetchArray("MotsCles", qualif, libelleSort());
  }
  
  /**
   * Retourne la liste des mots cles qui correspondent a la condition donnee.
   * Si la condition est <i>null</i>, alors la liste de tous les mots cles
   * est retournee.
   */
  public NSArray findMotsClefs(String condition) {
    EOQualifier qualif = null;
    if (StringCtrl.normalize(condition).length() > 0)
      qualif = newCondition(condition);
    setRefreshFetchedObjects(false);
    return fetchArray("MotsCles", qualif, libelleSort());
  }
  
  /**
   * Ajoute le mot cle. Retourne <i>true</i> si l'operation reussit et
   * <i>false</i> si le mot cle ne peut pas etre ajoute.
   */
  public boolean addMotCle(Integer transId, String motCle) {
    if (findMotsClefsLike(motCle).count() > 0) return true;
    try {
      Integer localTransId = getTransaction(transId);
      EOEditingContext ec = econtextForTransaction(localTransId, true);
      EOEnterpriseObject rec = EOUtilities.createAndInsertInstance(ec, "MotsCles");
      rec.takeValueForKey(motCle, "mcLibelle");
      rec.takeValueForKey("N", "mcType");
      if (transId == null) commitECTransaction(localTransId);
      return true;
    } catch(Throwable ex) {
      throwError(ex);
      return false;
    }
  }

  /**
   * Supprime le mot cle. Retourne <i>true</i> si l'operation reussit et
   * <i>false</i> si le mot cle ne peut pas etre supprime. Si le mot cle
   * n'existe pas, la methode retourne toujours <i>true</i>.
   */
  public boolean deleteMotCle(Integer transId, String motCle) {
    try {
      Integer localTransId = getTransaction(transId);
      EOEditingContext ec = econtextForTransaction(localTransId, true);
      EOEnterpriseObject rec = fetchObject(ec, "MotsCles", newCondition("mcLibelle='"+motCle+"'"));
      if (rec == null) return true;
      ec.deleteObject(rec);
      if (transId == null) commitECTransaction(localTransId);
      return true;
    } catch(Throwable ex) {
      throwError(ex);
      return false;
    }
  }
}
