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
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSharedEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Gere la liste des etats des demandes.
 * Cette classe simplifie l'acces aux informations sur les etats.
 */
// TODO Trouver les solutions statiques pour les methodes d'acces aux etats
public class DTEtatBus extends DTDataBus {
  /**
   * L'ordre de tri des etats.
   */
  private static NSArray etatsSort = CktlSort.newSort("etatPosition");
  
  /**
   * Tous les objets etats.
   */
  private NSArray etats;
  
  /**
   * Les etats lies a des affectations
   */
  private NSArray etatsAffectation;
  
  /**
   * Cree un nouveau objet. Utiliser le <code>defaultSharedEditingContext</code>.
   *
   */
  public DTEtatBus(EOEditingContext econtext) {
    super(EOSharedEditingContext.defaultSharedEditingContext());
  }

  /**
   * Teste si l'etat <code>etat</code> correcpond a l'etat
   * <code>dansCode</code>. Cette methode compare les deux parametres.
   */
  public static boolean isEtat(String etat, String dansEtat) {
    return etat.equals(dansEtat);
  }

  /**
   * Teste si l'etat donnee corrspond a un etat final. Cet etat peut
   * etre considere comme l'etat ou les informations sur une DT ne peuvent
   * plus etre modifiees. L'etat non modifiable est TRAITE ou REJETE.
   */
  public static boolean isEtatFinal(String etat) {
    if (etat == null) return false;
    return (etat.equals(EOEtatDt.ETAT_TRAITEES) || etat.equals(EOEtatDt.ETAT_REJETEES));
  }
  
  /**
   * Teste si l'etat donne correspond a l'etat initial d'une DT, i.e., l'etat
   * ou la DT n'est pas encore traitee. L'etat initial est un des
   * NON_VALIDEES, NON_AFFECTEE ou ATTENTE_AFFECTATION.
   */
  public static boolean isEtatInitial(String etat) {
    if (etat == null) return false;
    return (etat.equals(EOEtatDt.ETAT_NON_VALIDEES) || etat.equals(EOEtatDt.ETAT_NON_AFFECTEES)
            || etat.equals(EOEtatDt.ETAT_ATTENTE_AFFECTER));
  }

  /**
   * Teste si l'etat permet une affectation de demande.
   * Ceci est un des etats EN_COURS ou EN_ATTENTE ou ETAT_ATTENTE_AFFECTER ou ETAT_NON_AFFECTEES
   */  
  public static boolean isEtatAffectation(String etat) {
    if (etat == null) return false;
    return (etat.equals(EOEtatDt.ETAT_ATTENTE) || etat.equals(EOEtatDt.ETAT_ATTENTE_AFFECTER) ||
        etat.equals(EOEtatDt.ETAT_NON_AFFECTEES) || etat.equals(EOEtatDt.ETAT_EN_COURS));
  }
  
  /**
   * Teste si l'etat donne corrspond a l'etat en cours de traitement.
   * Ceci est un des etats EN_COURS ou EN_ATTENTE.
   */  
  public static boolean isEtatTraitement(String etat) {
    if (etat == null) return false;
    return (etat.equals(EOEtatDt.ETAT_EN_COURS) || etat.equals(EOEtatDt.ETAT_ATTENTE));
  }
  
  /**
   * Teste si l'etat donne corrspond a l'etat en cours de discussion.
   * Ceci est tous les etats sauf REJETEES ou TRAITEES
   */  
  public static boolean isEtatDiscussion(String etat) {
    if (etat == null) return false;
    return !(etat.equals(EOEtatDt.ETAT_REJETEES) || etat.equals(EOEtatDt.ETAT_TRAITEES));
  }
  
  /**
   * Retourne la liste de tous les etats.
   */
  public NSArray findEtatsAll() {
    // TODO Faire un truc static. La meme chose pour d'autres methodes
    if (etats == null) {
      setRefreshFetchedObjects(false);
      etats = fetchArray("EtatDt", null, etatsSort);
    }
    return etats;
  }
  
  
  /**
   * Retourne la liste des etats dont les DTs surlesquelles
   * des intervenants peuvent etre affectes
   */
  public NSArray findEtatsAffectation() {
  	if (etatsAffectation == null) {
      setRefreshFetchedObjects(false);
      etatsAffectation = fetchArray("EtatDt", 
      		newCondition("etatCode = '"+ EOEtatDt.ETAT_ATTENTE +"' or etatCode='"+ EOEtatDt.ETAT_EN_COURS +"' " +
      				"or etatCode='" + EOEtatDt.ETAT_REJETEES + "' or etatCode='" + EOEtatDt.ETAT_TRAITEES + "')"), etatsSort);
    }
    return etatsAffectation;
  }

  /**
   * Retourne la liste des libelles de tous les etats.
   */
  public NSArray findLibellesAll() {
    return (NSArray)findEtatsAll().valueForKey("etatLibelle");
  }
  
  /**
   * Retourne la liste des etats en excluant ceux dont le code
   * se trouve dans le filtre donne.
   */
  public NSArray findEtatsExclude(String excludeFilter) {
    NSMutableArray filteredEtats = new NSMutableArray();
    NSArray etats = findEtatsAll();
    if (excludeFilter == null) excludeFilter = StringCtrl.emptyString();
    for (int i = 0; i < findEtatsAll().count(); i++) {
      if (!(excludeFilter.indexOf(CktlRecord.recordStringForKey(etats.objectAtIndex(i), "etatCode")) > 0))
        filteredEtats.addObject(etats.objectAtIndex(i));
    }
    return filteredEtats;
  }
  
  /**
   * Retourne l'objet etat ayant le code donne. Retourne <i>null</i> si aucun
   * objet n'existe. 
   */
  private CktlRecord objectForKeyValue(String key, Object value) {
    CktlRecord rec;
    // Aucun objet si la valeur ou cle sont vides
    if ((StringCtrl.normalize(key).length() == 0) || (value == null))
      return null;
    NSArray allEtats = findEtatsAll();
    for(int i = 0; i < allEtats.count(); i++) {
      rec = (CktlRecord)allEtats.objectAtIndex(i);
      if (value.equals(rec.valueForKey(key)))
        return rec;
  }
    return null;
  }
  
  /**
   * Retourne le libelle qui correspond au code de l'etat.
   * Retourne une chaine vide ("") si l'etat n'existe pas.
   * La valeur <code>code</code> doit correspondre a une des valeurs
   * <code>ETAT_xxx</code>. 
   */  
  public String libelleForEtat(String code) {
    CktlRecord rec = objectForKeyValue("etatCode", code);
    if (rec == null)
      return StringCtrl.emptyString();
    else
    return rec.stringForKey("etatLibelle");
  }

  /**
   * Retourne le code d'etat portant le libelle donne.
   * Retourne une chaine vide ("") si l'etat n'existe pas.
   */
  public String etatForLibelle(String libelle) {
    CktlRecord rec = objectForKeyValue("etatLibelle", libelle);
    if (rec == null)
      return StringCtrl.emptyString();
    return
    rec.stringForKey("etatCode"); 
  }
  
  /**
   * Retourne l'enregistrement d'etat portant le code donne.
   */
  public CktlRecord recEtatForCode(String code) {
    return objectForKeyValue("etatCode", code);
  }

  /**
   * Retourne le code d'une vue qui par defaut est affichee pour
   * le libelle donne.
   */
  public int swapWiewForLibelle(String libelle) {
    CktlRecord rec = objectForKeyValue("etatLibelle", libelle);
    if (rec == null)
      return -1;
    else
    return rec.intForKey("etatSwapview"); 
  }

  /**
   * Retourne le code d'une vue qui par defaut est affichee pour
   * le code d'etat donne.
   */
  public int swapWiewForEtat(String code) {
    CktlRecord rec = objectForKeyValue("etatCode", code);
    if (rec == null)
      return -1;
    else
    return rec.intForKey("etatSwapview");
  }
  
  /**
   * Teste si le changement d'etat est autorise. Cette methode se base
   * uniquement sur l'arbre des changements possibles entre les etats d'une
   * demande. Elle ne prend pas en compte les donnees concretes d'une demande :
   * les affectations, les traitements, le statut de la personne qui effectue
   * les operations.
   */
  public static boolean isAllowedEtatChange(String oldEtat, String newEtat) {
    if (isEtat(oldEtat, EOEtatDt.ETAT_NON_VALIDEES)) {
      // "NON VALIDEE" : vers acun autre etat
      return false;
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_NON_AFFECTEES)) {
      // "NON AFFECTEE" : que vers "en attente d'affectation"
      return (isEtat(newEtat, EOEtatDt.ETAT_ATTENTE_AFFECTER));
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_ATTENTE_AFFECTER)) {
      // "EN ATTENTE D'AFFECT." : que vers "non affecte"
      return (isEtat(newEtat, EOEtatDt.ETAT_NON_AFFECTEES));
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_EN_COURS)) {
      // "EN COURS" : vers "non affectee" ou "en attente" (...de traitement) 
      return (isEtat(newEtat, EOEtatDt.ETAT_NON_AFFECTEES) ||
              isEtat(newEtat, EOEtatDt.ETAT_ATTENTE));
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_ATTENTE)) {
      // "EN ATTENTE" : seulement vers "En Cours"
      return (isEtat(newEtat, EOEtatDt.ETAT_EN_COURS));
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_TRAITEES)) {
      // "TRAITES" : remettre dans "en cours" uniquement
      return (isEtat(newEtat, EOEtatDt.ETAT_EN_COURS));
    } else if (isEtat(oldEtat, EOEtatDt.ETAT_REJETEES)) {
      // "REJETES" : seulement vers "non valides"
      return (isEtat(newEtat, EOEtatDt.ETAT_NON_VALIDEES));
    } else {
      // Tout le reste est interdit
      return false;
    }
  }
}
