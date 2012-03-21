/*
 * Copyright CRI - Universite de La Rochelle, 2001-2006 
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

import java.math.BigDecimal;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

/**
 * Gestion du traitement pour la DT de prestation en cours.
 * Permet de modifier le devis si la demande est incorrecte.
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class TraitementPrestation extends TraitementIntervention {

  /** le pointeur va la page parente (conteneur InspecteurRepro)**/
  private InspecteurDT parentPage;
  
  /** Partie ajout d'article au devis**/
  public boolean showAddArticle;
  public String tfNbArticles;
  public boolean errorNbArticles;
  public boolean errorArticleExiste;
  
  /** disponibilite des elements d'interface **/
  public boolean isDisabledTraitement;
  public boolean isDisabledDevis;
  
  public TraitementPrestation(WOContext context) {
    super(context);
    parentPage = (InspecteurDT) context().page();
    isTraitementPrestation = true;
  }
 
  /**
   * Remet a zero le contenu du composant d'apres
   * la demande originale.
   */
  public void resetContent() {
    super.resetContent();
    //XXX AJOUT POUR TRANSITION JEFYCO2007
    tfTraitement = dtPrestaGateway() != null ? dtPrestaGateway().getLignesTexte() : "-";
    resetInterfacePermissions();
  }

  /**
   * Surcharge pour les DTPrestation. On passe aux traitements du composant 
   * parent en specifiant que c'est une DT Prestation pour la cloture.
   */
  protected WOComponent confirmClose(boolean traite, boolean bidon) {
    return super.confirmClose(traite, true);
  }
  
  /**
   * Pointeur sur la passerelle de devis
   */
  public DTPrestaGateway dtPrestaGateway() {
    if (super.dtPrestaGateway() == null) {
        initDtPrestaGateway(
          new Integer(dtApp().config().intForKey("REPRO_CATALOGUE_COD")));
    }
    return super.dtPrestaGateway();
  }
  
  private void clearErrors() {
    errorNbArticles = errorArticleExiste = false;
  }
  
  public WOComponent clicShowAddArticle() {
    showAddArticle = true;
    return null;
  }
  
  /**
   * Ajouter l'article en cours au devis.
   * Controle de la validite de la quantite
   * @return
   */
  public WOComponent addArticle() {
    clearErrors();
    if (StringCtrl.toInt(tfNbArticles, 0) == 0) {
      errorNbArticles = true;
    } else {
      errorArticleExiste = !dtPrestaGateway().addArticleToDevis(
      		new BigDecimal(StringCtrl.toInteger(tfNbArticles, 0).intValue()));
      if (errorArticleExiste == false) 
        showAddArticle = false;
    }
    return null;
  }
  
  /**
   * Modifier le devis reel au travers PIE.
   */
  public WOComponent apply() {
    DTConfirmApplyResponder responder = new DTConfirmApplyResponder();
    return CktlAlertPage.newAlertPageWithResponder(
       this, "Confirmation de modification du devis<br>", 
       "Voulez-vous vraiment modifier le devis associé a cette DT ?", 
       "Confirmer", "Annuler", null, CktlAlertPage.QUESTION, responder);
  }

  
  /**
   * Classe de gestion de la page de confirmation O/N d'appliquer
   * les modifications vers le devis PIE
   */
  public class DTConfirmApplyResponder implements CktlAlertResponder {
    public DTConfirmApplyResponder() {
      super();
    }
    public WOComponent respondToButton(int buttonNo) {
      switch (buttonNo) {
      case 1: // APPLIQUER : retour aux traitements pour cloture
        // modification devis dans Prestation
        if (pieBus().checkPIEService()) {
          // recuperation des informations liees au devis via PIE
          dtPrestaGateway().commitChanges();
          if (pieBus().hasError()) {
            // erreur : on remet les donnees telles qu'elle sont dans PIE
            listener().setErrorMessage(getMessageFormatte(pieBus().errorMessage()));
            try {
              dtPrestaGateway().getOriginalDevisFromPie();
            } catch (Exception e) {
              listener().setErrorMessage(getMessageFormatte(dtPrestaGateway().errorMessage()));
              e.printStackTrace();
            }
          } 
        }
        resetInterfacePermissions();
        tfTraitement = dtPrestaGateway().getLignesTexte();
        return parentPage;
      case 2: // ANNULER
        // aucun changement, on revient a la page precedente
        return parentPage;
      default:
        return null;
      }
    }
  }
  
  /**
   * Activer les elements d'interface tel qu'ils doivent
   * etre a la premiere ouverture du composant.
   */
  private void resetInterfacePermissions() {
    isDisabledTraitement = false;
    isDisabledDevis = true;
  }

  /**
   * Passer dans le mode de modification du devis.
   */
  public WOComponent modifyDevis() {
    isDisabledTraitement = true;
    isDisabledDevis = false;
    return null;
  }
  
  /**
   * Revenir au devis original et quitter le mode edition.
   */
  public WOComponent restoreDevis() {
    try {
      dtPrestaGateway().getOriginalDevisFromPie();
    } catch (Exception e) {
      listener().setErrorMessage(getMessageFormatte(dtPrestaGateway().errorMessage()));
    }
    resetInterfacePermissions();
    return null;
  }
  

  /**
   * TODO a mettre dans DTPrestaGateway
   * Formatage des message de PIE
   * @param message
   * @return
   */
  private String getMessageFormatte(String message) {     
  	 if (message != null) {
  	  message = message.replaceAll("\n", " ");
  	  message = message.replaceAll("\"", "''");

  	  int index = message.indexOf("ORA-20001:");
  	  if (index > -1) {
  	   message = message.substring(index+10);
  	   index = message.indexOf("ORA-");
  	   if (index > -1) {
  	    message = message.substring(0, index);
  	   }
  	  }
  	 }
  	 return message;
  	}
}