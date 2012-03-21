/*
 * Copyright CRI - Universite de La Rochelle, 2003-2005 
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

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

/**
 * Cette classe regroupe les operation variees sur la base de donnees.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTGenericBus extends DTDataBus {

  /**
   * Cree et initialise un nouvel objet <code>DTGenericBus</code>.
   */
  public DTGenericBus(EOEditingContext eocontext) {
    super(eocontext);
  }

  /**
   * Retourne une liste des enregistrements de la table <i>Vlans</i> qui
   * correspondent au parametres donnes. Les parametres avec les valeurs
   * <i>null</i> ne sont pas pris en compte lors de la construction d'une
   * condition de selection.
   * 
   * @param priseCompte Indique s'il faut selection uniquement les descriptions
   *   ayant une option de prise en compte (priseCompte='O').
   * @param cVlan Indique le code de reseau a selectionner.
   */
  public NSArray findVlans(Boolean priseCompte, String cVlan) {
    StringBuffer condition = new StringBuffer();
    if (priseCompte != null)
      condition.append("priseCompte='").append(priseCompte.booleanValue()?"O'":"N'");
    if (cVlan != null) {
      if (condition.length() > 0) condition.append(" and ");
      condition.append("cVlan='").append(cVlan).append("'");
    }
    return fetchArray("Vlans", newCondition(condition.toString()),
        CktlSort.newSort("priorite"));
  }
  
}
