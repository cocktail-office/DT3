
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
import com.webobjects.appserver.WOContext;

/**
 * Permette d'afficher un message d'erreur de type "erreur de saisie d'une
 * valeur dans un champ". Ce composant est utilise pour afficher les erreurs
 * ou avertissements dans les formulaires.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTFormErrorBox extends DTWebComponent {

  /**
   * Cree une nouvelle instance du composant.
   */
  public DTFormErrorBox(WOContext context) {
    super(context);
  }

  /**
   * Retourne <em>false</em> pour indiquer que la synchronisation
   * (lecture/ecriture) des valeurs des connecteurs ne doit pas etre utilisee.
   */
  public boolean synchronizesVariablesWithBindings() {
    return false;
  }

  /**
   * Retourne <em>true</em> pour indiquer que le composant est sans etat.
   */
  public boolean isStateless() {
    return true;
  }
}