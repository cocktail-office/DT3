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

import java.io.File;
import java.io.IOException;

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.print.CktlPrinter;
import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;

import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSData;

/**
 * Propose les methodes permettant de simplifier l'acces au gestionnaire des
 * impressions SIX.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class LRPrintBus extends DTDataBus {
  
   public LRPrintBus(EOEditingContext editingContext) {
    super(editingContext);
   }

  /**
   * Impression d'un report avec l'idetifiant <code>templateID</code> un
   * utilisant le serveur SIX. Retourne <i>null</i> si l'impression est
   * effectue avec succes, sinon retourne le message d'erreur. 
   */
  public String printPDFReport(String templateID, String dataFile, String resultFile) {
    // On cree et on initialise le pilote d'impression
    CktlPrinter printer;
    try {
      printer = CktlPrinter.newDefaultInstance(dtApp().config());
    } catch (ClassNotFoundException e) {
      // Le pilote n'a pas ete trouve
      return e.getMessage();
    }
    // On fait appel a l'impression. On restera bloque jusqu'a la
    // fin de l'impression.
    printer.printFileImmediate(templateID, dataFile, resultFile);
    // On verifie si l'operation est OK
    if (printer.hasSuccess()) {
      //NSWorkspace.sharedWorkspace().openFile(resultFile);
      return null; 
    } else {
      return printer.getMessage();
    }
  }
  
  /**
   * Teste si la maquette avec le ID templateId est bien installe
   * sur le serveur d'impression. Teste egalement si le service
   * d'impression SIX est disponible.
   */
  public String checkTemplate(String templateId) {
    String message = null;
    try {
      // On cree et on initialise le pilote d'impression
      CktlPrinter printer = CktlPrinter.newDefaultInstance(dtApp().config());
      if (!printer.checkTemplate(templateId))
        // Le service ou la maquette indisponible
        message = printer.getMessage();
    } catch (ClassNotFoundException e) {
      // Le pilote n'a pas ete trouve
      message = CktlLog.getMessageForException(e);
      e.printStackTrace();
    }
    return message;
  }
  
  /**
   * Generer la reponse HTML pour ouvrir un fichier PDF.
   * 
   * @param file : fichier PDF a ouvrir
   * @return
   * @throws IOException 
   */
  public WOResponse generatePDFResponse(String file) throws IOException {
    NSData pdfData = new NSData(new File(file));
    CktlDataResponse resp = new CktlDataResponse();
    if (pdfData != null) {
    	resp.setContent(pdfData, CktlDataResponse.MIME_PDF);
      resp.setHeader(String.valueOf(pdfData.length()), "Content-Length");
      resp.setFileName("DemandesDeTravaux_"+hashCode()+".pdf");
      // bidouille poitiers https et ie
      resp.disableClientCaching();
      resp.removeHeadersForKey("Cache-Control");
      resp.removeHeadersForKey("pragma");
    } else {
      resp.setHeader("0", "Content-Length");
    }
    WOResponse response = resp.generateResponse();
    return response;
  }
}
