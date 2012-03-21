
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;

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

/**
 * Decrit une entree dans la liste de drop box. Contient les donnees associees
 * a l'entree (methode <code>record</code>) et definit la chaine a utiliser
 * lorsque l'entree est affichee dans la liste.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class ContactsListItem {
  /** Le donnees */
  private CktlRecord record;
  private Session dtSession;
  /**
   * Cree un objet a inserer dans une liste <code>NSComboBox</code>.
   * 
   * @param rec L'enregistrement avec les donnees d'une entree.
   *   Peut etre <i>null</i>.
   * @param type Le type de l'entre. A utiliser au cas ou il faut typer
   *   differents entrees.
   */
  public ContactsListItem(CktlRecord rec, Session session) {
    this.record = rec;
    this.dtSession = session;
  }

  /**
   * Retourne enregistrement avec les donnees associees avec cette entree, ou
   * <i>null </i> si aucune donnee n'etait associee.
   */
  public CktlRecord record() {
    return record;
  }

  public String service() {
    if (record.valueForKey("ctUserLocal").equals("O")) {
      if (DTDataBus.isNullValue(record.valueForKey("cStructService")))
        return record.stringForKey("ctLibelleEtab");
      else
        return record.stringForKeyPath("toService.lcStructure");
    } else {
      return record.stringForKey("ctLibelleEtab");
    }
  }
  
  public String batiment() {
    if (DTDataBus.isNullValue(record.valueForKey("cLocal")))
      return null;
    else
      return record.stringForKeyPath("toBatiment.appellation");
  }

  public String bureau() {
    CktlRecord rec = record.recForKeyPath("toSalles");
    if (rec != null)
      return dtSession.dataCenter().contactsBus().getLibelleForSalle(rec);
    return null;
  }

  public String telephone() {
    return record.stringForKey("ctNoTelephone");
  }
  
  public String email() {
    return record.stringForKey("ctEmail");
  }
  
  public Number ctOrdre() {
    return record.numberForKey("ctOrdre");
  }
  
  public String toString() {
    return record.valueForKey("ctOrdre").toString();
  }
}
