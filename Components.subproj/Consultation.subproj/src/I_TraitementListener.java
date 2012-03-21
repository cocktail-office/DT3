/*
 * Copyright CRI - Universite de La Rochelle, 2005 
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
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitement;

import com.webobjects.appserver.WOComponent;

/**
 * Definit les methodes qui vont permettre la communication entre le composant
 * d'ajout d'un traitement et le composant principal (l'inspecteur de la
 * demande).
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public interface I_TraitementListener {
  
  /**
   * Retourne la demande en cours de consultation. Le traitement pourra etre
   * ajoute a cette demande.
   */
  public EOIntervention recDemande();
  
  /**
   * Retourne le traitement en cours. Il est forcement null en mode ajout
   */
  public EOTraitement recTraitement();
  
  /**
   * Indique si le contenu de composant doit etre "remis a zero". Cette valeur
   * peut etre change avec la methode <code>setShouldReset</code>.
   * 
   * @see #setShouldReset(boolean)
   */
  public boolean shouldReset();
  
  /**
   * Definit si le contenu du composant doit etre "remis a zero".
   */
  public void setShouldReset(boolean doReset);
  
  /**
   * Definit un message d'erreurs pour le composant englobant.
   */
  public void setErrorMessage(String message);
  
  /**
   * Definit un message d'avertissement pour le composant englobant.
   */
  public void setWarnMessage(String message);

  /**
   * Retourne la page qui doit etre affichee suite a l'annulation de saisie
   * d'un nouveau traitement.
   */
  public WOComponent cancelPage();
  
  /**
   * Retourne la page qui doit etre affiche suite a l'ajout d'un nouveau
   * traitement.
   */
  public WOComponent addPage();
  
  /**
   * Retourne la page qui doit etre affiche suite a la mise a jour
   * d'un traitement.
   */
  public WOComponent updatePage();
  
  /**
   * Indique si le composant est en  mode edition ou ajout
   */
  public boolean isUpdating();
  
  /**
   * Faut-il envoyer un mail quand le traitement sera ajoute
   */
  public void setSendMailTraitement(boolean doMailTraitement);
  
  /**
   * Retourne la page qui doit etre affiche suite a la cloture de la demande.
   */
  public WOComponent closePage();

  /**
   * Retourne la page qui doit s'afficher suite a une erreur lors
   * de l'ajout d'un traitement ou lors de la cloture.
   */
  public WOComponent errorPage();
  
  /**
   * Le message de warning
   */
  public String warnMessage();

}
