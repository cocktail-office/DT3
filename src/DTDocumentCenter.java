/*
 * Copyright CRI - Universite de La Rochelle, 2002-2005 
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
import org.cocktail.fwkcktlwebapp.common.util.GEDDescription;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/**
 * Gere la sauvegarde et la consultation des documents. 
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDocumentCenter {
  /** Code de la categorie des documents DT sur le serveur GEDFS */
  public static final String CATEGORIE_DT_COMMUN = "DT_COMMUN";
  /** Code de la categorie des documents DT-Reprographie sur le serveur GEDFS */
  public static final String CATEGORIE_DT_REPRO = "DT_REPRO";
  /** Reference vers la session en cours */
  private Session dtSession;
  
  /**
   * Cree une instance de gestionnaire des documents pour la session donnee.
   */
  public DTDocumentCenter(Session dtSession) {
    this.dtSession = dtSession;
  }
  
  /**
   * Retourne la reference vers l'intance de l'application en cours.
   */
  private Application dtApp() {
    return (Application)WOApplication.application(); 
  }
  
  /**
   * Enregistre document pour la DT donnee. Retourne la liste des URL (un URL)
   * d'acces au document. Returne <i>null</i>, si l'enregistrement echoue.
   */
  protected NSArray<String> saveAttachements(
      Number intOrdre, Number traOrdre, NSArray<String> chemins, String catCode)
  {
    NSMutableArray<String> filesURL = new NSMutableArray<String>();
    // Retour, si aucun document a enregistrer
    if (chemins.count() == 0) return filesURL;
    NSMutableArray<Integer> noDocuments = new NSMutableArray<Integer>();
    int noDocument = -1;
    DTInterventionBus iBus = dtSession.dataCenter().interventionBus();
    GEDDescription gedDescription;
    // On enregistre le document un par un
    Integer transactionId = iBus.beginECTransaction();
    for(int i=0; i<chemins.count(); i++) {
      // Enregistrer document
      noDocument = dtSession.gedBus().saveDocument(
                     (String)chemins.objectAtIndex(i),
                     "Document DT ("+intOrdre+")", catCode);
      // On arrete, si sa marche pas
      if (noDocument == -1) break;
      noDocuments.addObject(new Integer(noDocument));
      // Memoriser le URL
      gedDescription = dtSession.gedBus().gedDescription();
      if (gedDescription != null)
        filesURL.addObject(gedDescription.reference);
      // Sauvegarde donnees
      if (!iBus.addDocumentDt(null, (Number)noDocuments.lastObject(), intOrdre, traOrdre, null)) {
        // S'il y a un probleme, on marque une erreur et on sort
        noDocument = -1;
        break;
      }
    }
    // Si on sort suite a une erreur
    if (noDocument == -1) {
      // Annuler tous les sauvegardes
      for (int i=0; i<noDocuments.count(); i++)
        dtSession.gedBus().deleteDocumentGED(((Integer)noDocuments.objectAtIndex(i)).intValue());
      iBus.rollbackECTrancsacition(transactionId);
      filesURL.removeAllObjects();
    } else {
      // Sinon, on commit et c'est OK
      iBus.commitECTrancsacition(transactionId);
    }
    return filesURL;
  }

  public NSArray getDocumentsURL(NSArray docRecords) {
    if ((docRecords == null) || (docRecords.count() == 0))
      return new NSArray();
    NSMutableArray docsURL = new NSMutableArray();
    GEDDescription description;
    for(int i=0; i<docRecords.count(); i++) {
      description = dtApp().gedBus().inspectDocumentGED(
          ((CktlRecord)docRecords.objectAtIndex(i)).intForKey("docOrdre"));
      if (description != null)
        docsURL.addObject(description.reference);
    }
    return docsURL;
  }
}
