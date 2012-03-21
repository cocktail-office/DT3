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

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

/**
 * Gestionnaire des impressions de demande de travaux
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class PageImpression extends DTWebPage {

  // radio de selection du type d'impression
  public final int FORMAT_RESUME = 0;
  public final int FORMAT_DETAIL = 1;
  public int selectedFormat = FORMAT_RESUME;

  // les demandes affichees
  public NSArray recsIntervention;
  public NSArray selectedsRecsIntervention;
  public CktlRecord recIntervention;
  
  // pour le format affichage des demandes
  private int sizeMaxIntCleService;
  
  // maximum d'impression autorisees
  private final static int MAX_PRINT_ELEMENTS = 150;
  
  public PageImpression(WOContext context) {
    super(context);
  }

  public void appendToResponse(WOResponse arg0, WOContext arg1) {
    super.appendToResponse(arg0, arg1);
    // forcer l'affichage du select en police proportionnelles
    addTextCss(arg0, "option {font-family: monospace}");
  }
  
  /**
   * Appeler le gestionnaire d'impression depuis le code d'une autre page
   * 
   * @param aCaller : la page appelante (generalement 'this')
   * @param someRecsIntervention : la liste des DT a afficher
   */
  public void afficherImpression(NSArray someRecsIntervention) {
    recsIntervention = someRecsIntervention;
    selectedsRecsIntervention = new NSArray();
    Integer maxIntCleService = (Integer) recsIntervention.valueForKey("@max.intCleService");
    sizeMaxIntCleService = (maxIntCleService != null ?
        Integer.toString(maxIntCleService.intValue()).length() : 0);
  }
  
  /**
   * Impression des demandes sans detail
   */
  public WOResponse imprimerResume() {
    return imprimer(DTPrintCenter.RPT_LISTE_INTS_RESUME);
  }
  
  /**
   * Impression des demandes avec detail
   */
  public WOResponse imprimerDetail() {
    return imprimer(DTPrintCenter.RPT_LISTE_INTS_DETAILS);
  }
  
  /**
   * Lancer la generation du fichier PDF, si une erreur survient
   * alors une page d'erreur est retournee
   */
  private WOResponse imprimer(String template) {
    
    // verif de la presence de la maquette sur le serveur SIX
    if (!printCenter().testTemplate(template)) {
      return CktlAlertPage.newAlertPageWithCaller(
          this, "Erreur de traitement", 
          "La maquette \""+template+"\"<br>" +
          "n'est pas disponible sur le serveur d'impressions.", 
          "<< Retour",
          CktlAlertPage.ERROR).generateResponse();
    }
    // verifier que des DTs sont selectionnees
    if (selectedsRecsIntervention == null || selectedsRecsIntervention.count() == 0) {
      return CktlAlertPage.newAlertPageWithCaller(
          this, "Impression impossible", 
          "Aucune DT n'est sélectionnée pour l'impression.",
          "<< Retour",
          CktlAlertPage.ERROR).generateResponse();
    }
    // verif du nom depassement maximum
    if (selectedsRecsIntervention.count() > MAX_PRINT_ELEMENTS) {
      return CktlAlertPage.newAlertPageWithCaller(
          this, "Impression impossible", 
          "Vous avez selectionné "+selectedsRecsIntervention.count()+" DTs, la valeur maximale<BR>" +
          "autorisée est de " + MAX_PRINT_ELEMENTS + " selections. Veuillez restreindre <BR>" +
          "les demandes a imprimer, quitte à le faire en plusieurs fois."
          ,
          "<< Retour",
          CktlAlertPage.ERROR).generateResponse();
    }
   
    
    //
    printCenter().setListener(new PrintListener());
    // generer XML
    String xmlFile = null;
    if (template.equals(DTPrintCenter.RPT_LISTE_INTS_DETAILS)) {
      xmlFile = printCenter().xmlForDetailed(selectedsRecsIntervention);
    } else {
      xmlFile = printCenter().xmlForResume(selectedsRecsIntervention);
    }
    // generer PDF
    String resultFile = DTFileManager.getTempFilePath(".pdf", false);
    CktlLog.trace("Document file target : " + resultFile);
    if (!printCenter().hasError()) {
      CktlLog.log("Starting document...");
      printCenter().printReport(template, xmlFile, resultFile);
      if (!printCenter().hasError())
        CktlLog.log("Document succesfully created.");
    }
    // generer la page a afficher
    WOResponse response = null;
    try {
      response = printCenter().printBus().generatePDFResponse(resultFile);
    } catch (Exception e) {
      printCenter().errorMessage = e.getMessage();
    }
    // suppression des fichiers sur le serveur
    DTFileManager.addToTrash(xmlFile);
    DTFileManager.addToTrash(resultFile);
    // affiche erreur si plantage
    if (printCenter().hasError()) {
      return CktlAlertPage.newAlertPageWithCaller(
          this, "Erreur d'impression", 
          "Une erreur est survenue lors de l'impression :\n"+
          printCenter().errorMessage(),
          "<< Retour",
          CktlAlertPage.ERROR).generateResponse();
    }
    return response;
  }
  
  private class PrintListener implements DTPrintCenterListener {
    /*
     * @see DTPrintCenterListener#nextXmlDataEntity()
     */
    public void nextXmlDataEntity() {
    }
    
  }
  
  /**
   * L'affiche d'une DT dans le Browser. 
   */
  public String displayRecIntervention() {
   String display = DTStringCtrl.fitToSize(Integer.toString(recIntervention.intForKey("intCleService")), sizeMaxIntCleService, null) + " | " +
   DTStringCtrl.fitToSize(nomDemandeurCourt(),12, ".") + " | " + DateCtrl.dateToString(recIntervention.dateForKey("intDateCreation"), "%d/%m/%y") + 
      " | " + DTStringCtrl.fitToSize((String)recIntervention.valueForKey("intMotif"), 65, " <...>") + " | " + DTStringCtrl.fitToSize(batimentLibelle(), 3, null);
    display = StringCtrl.replace(display, " ", "&nbsp;");
    display = StringCtrl.replace(display, "\n", "&nbsp;");
    display = StringCtrl.replace(display, "\r", "&nbsp;");
     
    return display;
  }
  
  
  public String nomDemandeurCourt() {
    String nomDemandeur = null;
    if (recIntervention != null) {
      CktlRecord rec = recIntervention.recForKey("toIndividuConcerne");
      nomDemandeur = StringCtrl.formatName(rec.stringForKey("prenom"), rec.stringForKey("nomUsuel"));
    } else
      nomDemandeur = StringCtrl.emptyString();
    return nomDemandeur;
  }

  /**
   * Le batiment ou realiser l'intervention
   * Pour raison historique, cette info etait dans 
   * l'entite intervention ... or c'est dans le contact 
   * qu'il faut regarder ! On fait donc dans l'ordre :
   * 1- le batiment dans la DT
   * 2- celui dans le contact
   */
  public String batimentLibelle() {
    CktlRecord recBatiment = recIntervention.recForKey("toBatiment");
    String libelle = "";
    if (recBatiment == null) {
      // on cherche dans le contact
      Number ctOrdre = recIntervention.numberForKey("ctOrdre");
      if (ctOrdre != null) {
        CktlRecord recContact = dtSession().dataCenter().contactsBus().findContact(ctOrdre);
        if (recContact != null)
          recBatiment = recContact.recForKey("toBatiment");
      }
   } 
    if (recBatiment != null) 
      libelle = recBatiment.stringNormalizedForKey("cLocal");
    return libelle;
  }

  
  // ** les bus de donnees **
  
  private DTPrintCenter printCenter() {
    return dtSession().printCenter();
  }
}