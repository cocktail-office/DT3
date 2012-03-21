
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
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

/**
 * Gere le composant de la selection des individus ou de structure dans l'annuaire de
 * l'etablissement. Ce composant perment de retrouver une personne en fonction
 * de son nom et/ou prenom, libelle long / cours. Les champs de recherche peuvent etre 
 * incomplets.
 * 
 * <p>Ce composant communique avec le composant l'appelant via l'auditeur des
 * evenements qui implement l'interface <code>SelectPersonneListener</code>.</p>
 * 
 * @see SelectPersonneListener
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class SelectPersonne extends DTWebPage {
  /** Le nombre maximal des objets autorises dans la reponse */
  private static int TOO_MANY_OBJECTS_LIMIT = 50;
  /** Le auditeur des evenemets de la selection */
  private SelectPersonneListener listener;
  /** La liste des personnes retrouvees */
  public NSArray personnesListe;
  /** L'attribut utilise pour parcourir la liste des personnes */
  public NSDictionary personneItem;
  /** La liste des personnes selectionnees (contient un element) */
  public NSArray personnesSelected;
  /** Le message/titre a afficher en haut de la page du composant */
  public String titre;
  /** La valeur du champ de recherche */
  public String strRecherche;
  /** Indique si l'erreur "Trop d'objets" est survenue */
  public boolean errTooManyObjects;
  /** Indique si l'erreur "Aucune personne trouvee" est survenue */
  public boolean errNotFound;
  // indique si le composant est en mode individu 
  private boolean isModeIndividu;
  
  
  /**
   * Cree une nouvelle instance du composant.
   */
  public SelectPersonne(WOContext context) {
    super(context);
    resetComponentContent();
  }

  /**
   * Retourne une instance du composant de selection des personnes.
   * 
   * @param newListener L'auditeur des evenements du composant. Ce parametre
   *   doit etre donnee.
   * @param newTitre Le message/titre a afficher en haut de la page du
   *   composant.
   * @param personneId Le numero d'individu / c_structure pre-selectionne. Son nom sera
   *   pre-saisi dans le champ du nom de la personne. 
   * @param modeIndividu Indique si la recherche droit se faire sur des individus
   *  ou sur des structures
   */
  public static SelectPersonne getNewPage(
      SelectPersonneListener newListener, String newTitre, Object personneId, boolean modeIndividu) {
    SelectPersonne newPage = (SelectPersonne)cktlApp.pageWithName(SelectPersonne.class.getName(), newListener.getContext());
    newPage.initComponent(newListener, newTitre, personneId, modeIndividu);
    return newPage;
  }

  /**
   * Initialise le composant par les parametres donnes. Cette methode doit etre
   * appellee seulement si le composant est creee avec une methode
   * "<code>pageWithName</code>". La methode ne doit pas etre appellee si le
   * composant est creee avec la methode satatique <code>getNewPage</code>
   * (cet appel est fait implicitement).
   * 
   * @param newListener L'auditeur des evenements du composant. Ce parametre
   *   doit etre donnee.
   * @param newTitre Le message/titre a afficher en haut de la page du
   *   composant.
   * @param noIndividu Le numero d'individu pre-selectionne. Son nom sera
   *   pre-saisi dans le champ du nom de la personne. 
   */
  private void initComponent(
      SelectPersonneListener newListener, String newTitre, Object personneId, boolean modeIndividu) {
    listener = newListener;
    titre = newTitre;
    isModeIndividu = modeIndividu;
    CktlRecord recPersonne = null; 
    if (isModeIndividu) {
    	recPersonne = dtDataCenter().individuBus().individuForNoIndividu((Number)personneId);
    	if (recPersonne != null) {
        strRecherche = recPersonne.stringForKey("nomEtPrenom");
    	}
    } else {
    	if (personneId != null) {
      	recPersonne = dtDataCenter().serviceBus().serviceForCode((String) personneId);
	    	if (recPersonne != null) {
	        strRecherche = recPersonne.stringForKey("llStructure");
	    	}
    	}
    }
    resetComponentContent();
  }
  
  /**
   * Efface la liste des personnes, les selections et les messages d'erreurs.
   */
  private void resetComponentContent() {
    errNotFound = false;
    errTooManyObjects = false;
    personnesSelected = new NSArray();
    personnesListe = new NSArray();
  }
  
  /**
   * Retrouve les personnes dont le nom et/ou prenom est similaire a celui
   * donne dans <code>noms</code>. Initialise la liste des personne ou
   * definit les messages d'erreurs dans le cas de problemes.
   */
  private void findPersonnesLike(String noms) {
    noms = StringCtrl.normalize(noms);
    resetComponentContent();
    // Si rien, afficher un message d'erreur 
    if (noms.length() == 0) {
      errNotFound = true;
      return;
    }
    // On test d'abord le nombre d'objets dans le resultat
    int count = (isModeIndividu ? dtDataCenter().individuBus().rawIndividuCountForName(noms) : dtDataCenter().serviceBus().rawStructureCountForName(noms));
    if (count == 0) {
      // Rien n'a ete trouve
      errNotFound = true;
    } else if (count > TOO_MANY_OBJECTS_LIMIT) {
      // Trop d'objets trouves
      errTooManyObjects = true;
    } else {
      // Sinon, on recupere les objets elles memes
      personnesListe = (isModeIndividu ? dtDataCenter().individuBus().rawIndividuForName(noms) : dtDataCenter().serviceBus().rawStructureForName(noms));
      if (personnesListe.count() == 0)
        errNotFound = true;
      // Sinon, tout est OK, on affiche le resultat
    }
  }

  /**
   * Teste si la liste des personnes n'est pas vide.
   */
  public boolean hasPersonnesListe() {
    return personnesListe != null && personnesListe.count() > 0;
  }
  
  /**
   * Retourne la chaine de caracteres qui doit etre affichee dans la liste
   * des personnes. Elle a la forme "<em>Nom Prenom &lt;adresse-email&gt;</em>". 
   */
  public String personneItemDisplay() {
    if (personneItem == null) return "<aucune selection>";
    StringBuffer sb = new StringBuffer();
    if (isModeIndividu) {
      sb.append(personneItem.valueForKey("nomUsuel")).append(" ");
      sb.append(personneItem.valueForKey("prenom")).append(" <");
      sb.append(personneItem.valueForKey("cptEmail")).append("@");
      sb.append(personneItem.valueForKey("cptDomaine")).append(">");
    } else {
    	sb.append(personneItem.valueForKey("llStructure")).append(" (");
    	sb.append(personneItem.valueForKey("lcStructure")).append(") - ");
    	sb.append(personneItem.valueForKey("cStructure"));
    }
    return sb.toString();
  }
  
  /**
   * Le titre au dessus de la zone de recherche
   * @return
   */
  public String strRechercheTitle() {
  	if (isModeIndividu) {
  		return "Nom et/ou prénom";
  	} else {
  		return "Libellé (long ou court)";
  	}
  }
  
  /**
   * Effectue l'operation de recherche (click sur le bouton <em>Rechercher</em>).
   */
  public WOComponent rechercher() {
    findPersonnesLike(strRecherche);
    return null;
  }
  
  /**
   * Effectue l'operation de la selection d'une personne dans la liste des
   * personne retrouvees. Fait appel a l'auditeur de la selection pour retourner
   * le composant a la suite de cette acition.
   * 
   * @see SelectPersonneListener#select(Number)
   */
  public WOComponent selectionner() {
    if ((personnesSelected != null) && (personnesSelected.count() > 0)) {
      return listener.select(
          (Number)((NSDictionary)personnesSelected.objectAtIndex(0)).valueForKey("persId"));
    }
    return null;
  }
  
  /**
   * Annule la selection des personnes. Fait appel a l'auditeur de la selection
   * pour retourner le composant a la suite de cette acition.
   * 
   * @see SelectPersonneListener#cancel()
   */
  public WOComponent annuler() {
    return listener.cancel();
  }
}
