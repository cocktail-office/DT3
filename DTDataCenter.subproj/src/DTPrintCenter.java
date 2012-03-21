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
import java.io.FileWriter;

import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.CktlXMLWriter;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * Gestionnaire des impressions pour les demandes de travaux.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTPrintCenter extends DTDataCenter {
  public DTPrintCenter(Session session) {
    super(session);
  }

  /** L'identifiant de la maquette d'impression de "resume des demandes". */
  public final static String RPT_LISTE_INTS_RESUME = "DT_LISTE_INTS_RESUME";
  /** L'dentifiant de la maquette d'impression de "la liste detaillee des
   * demandes". */
  public final static String RPT_LISTE_INTS_DETAILS = "DT_LISTE_INTS_DETAILS";
  /** Le suffix a ajouter au cas ou le texte du motif ou de traitement
   * depasse la longeur demandee. */
  private static String CompactSuffix = CktlXMLWriter.SpecChars.nbsp+"[...]";

  /** Observateur des evenements d'impression. */
  private DTPrintCenterListener listener;
  
  /**
   * Definit un nouveau observateur des evenements d'impression.
   */
  public void setListener(DTPrintCenterListener newListener) {
    listener = newListener;
  }
  
  /**
   * Genere le document XML contenant la description des demandes donnees
   * dans la liste <code>interventions</code>. La methode retourne le chemin
   * d'acces au fichier XML genere.
   */
  public String xmlForDetailed(NSArray interventions) {
    CktlRecord recNew, recIntervention, recIntervenant, recTraitement, recInfin;
    NSArray objects, intervenants, traitements;
    String s;
    //
    CktlXMLWriter xmlWriter;
    String xmlFile;
    
    errorMessage = null;
    xmlFile = DTFileManager.getTempFilePath(".xml", false);
    try {
      xmlWriter = new CktlXMLWriter(new FileWriter(xmlFile));
      xmlWriter.setEscapeSpecChars(true);
      xmlWriter.setCharsToEscape("<>");
      xmlWriter.startDocument();
      xmlWriter.startElement("liste-detail-dt");  // ** <liste-detail-dt> **
      xmlWriter.writeElement("service", dtSession.dtUserInfo().dtServiceLibelle());
      xmlWriter.writeElement("date-impression", DateCtrl.dateToString(DateCtrl.now()));
      xmlWriter.startElement("liste"); // ** <liste> **
      for(int i=0; i<interventions.count(); i++) {
        xmlWriter.startElement("dt");  // ** <dt> **
        recIntervention = (CktlRecord)interventions.objectAtIndex(i);
        xmlWriter.writeElement("cle-service", recIntervention.valueForKey("intCleService").toString());
        xmlWriter.writeElement("date-appel", recIntervention.dateTimeStringForKey("intDateCreation"));
        xmlWriter.writeElement("date-realisation", recIntervention.dateStringForKey("intDateSouhaite"));
        xmlWriter.startElement("demandeur");  // ** <demandeur> **
        recNew = (CktlRecord)recIntervention.valueForKey("toIndividuConcerne");
        xmlWriter.writeElement("nom", recNew.stringForKey("nomEtPrenom"));
        xmlWriter.writeElement("email", individuBus().mailForNoIndividu(recNew.numberForKey("noIndividu")));
        xmlWriter.writeElement("contact",
            contactsBus().getInterventionContactDescription(
                recIntervention.numberForKey("intOrdre"), recIntervention.numberForKey("ctOrdre"), null));
        xmlWriter.endElement();  // ** </demandeur> **
        xmlWriter.startElement("appelant"); // ** <appelant> **
        recNew = recIntervention.recForKey("toIndividuAppelant");
        xmlWriter.writeElement("nom", recNew.stringForKey("nomEtPrenom"));
        xmlWriter.endElement(); // ** </appelant> **
        xmlWriter.writeElement("etat", etatBus().libelleForEtat(recIntervention.stringForKey("intEtat")));
        // Ligne budgetaire et devis
        objects = recIntervention.arrayForKey("tosInterventionInfin");
        if (objects.count() > 0) {
          recInfin = (CktlRecord)objects.objectAtIndex(0);
          if (recInfin.valueForKey("orgId") != null) {
            s = jefyBus().getLigneBudDescription(null,
                recInfin.numberForKey("orgId"),
                recInfin.stringForKey("tcdCode"), false, null);
            if (s != null)
              xmlWriter.writeElement("ligne-budgetaire", s);
          }
          if (recInfin.valueForKey("prestNumero") != null)
            xmlWriter.writeElement("devis", recInfin.valueForKey("prestNumero").toString());
        }
        s = formatXMLText(recIntervention.stringForKey("intMotsClefs"), false, 0);
        xmlWriter.writeElement("mots-cles", s);
        xmlWriter.startElement("motif"); // ** <motif> **
        s = formatXMLText(recIntervention.stringForKey("intMotif"), true, 0);
        xmlWriter.writeCharacters(s);
        xmlWriter.endElement(); // ** </motif> **
        //
        intervenants = (NSArray)recIntervention.valueForKey("tosIntervenant");
        traitements = (NSArray)recIntervention.valueForKey("tosTraitement");
        if (intervenants.count() > 0) {
          xmlWriter.startElement("intervenants"); // ** <intervenants> **
          StringBuffer sb = new StringBuffer();
          for(int j=0; j<intervenants.count(); j++) {
            xmlWriter.startElement("intervenant"); // ** <intervenant> **
            recIntervenant = (CktlRecord)intervenants.objectAtIndex(j);
            recIntervenant = recIntervenant.recForKey("toIndividuUlr");
            xmlWriter.writeElement("nom", recIntervenant.stringForKey("nomEtPrenom"));
            objects = traitementsForIndividu(recIntervenant.intForKey("noIndividu"), traitements);
            if (objects.count() > 0) {
              xmlWriter.startElement("traitements"); // ** <traitements> **
              for(int k=0; k<objects.count(); k++) {
                xmlWriter.startElement("traitement"); // ** <traitement> **
                recTraitement = (CktlRecord)objects.objectAtIndex(k);
                sb.setLength(0);
                sb.append(recTraitement.dateStringForKey("traDateDeb", "%d/%m/%y"));
                sb.append(", ").append(recTraitement.dateStringForKey("traDateDeb", "%H:%M"));
                sb.append(" - ").append(recTraitement.dateStringForKey("traDateFin", "%H:%M"));
                xmlWriter.writeElement("date", sb.toString());
                s = formatXMLText(recTraitement.stringForKey("traTraitement"), true, 0);
                s = StringCtrl.replace(s, "\n", CktlXMLWriter.SpecChars.br);
                xmlWriter.writeElement("commentaire", s);
                xmlWriter.writeElement("etat", etatBus().libelleForEtat(recTraitement.stringForKey("traEtat")));
                xmlWriter.endElement(); // ** </traitement> **
              }
              xmlWriter.endElement(); // ** </traitements> **
            }
            xmlWriter.endElement(); // ** </intervenant> **
          }
          xmlWriter.endElement(); // ** </intervenants> **
        }
        if (listener != null) listener.nextXmlDataEntity();
        xmlWriter.endElement(); // ** </dt> **
      }
      xmlWriter.endElement(); // ** </liste> **
      xmlWriter.endElement(); // ** </liste-detail-dt> **
      xmlWriter.endDocument();
      xmlWriter.close();
    } catch(Exception e) {
      e.printStackTrace();
      errorMessage = CktlLog.getMessageForException(e);
    }
    return xmlFile;
  }

  /**
   * Impression de la liste des DT en forme "resume"
   */
  public String xmlForResume(NSArray interventions) {
    CktlXMLWriter xmlWriter;
    String xmlFile;
    NSArray datas;
    int reportType;
    String s, s2;
    CktlRecord rec, recSrc;
    NSTimestamp dtDe, dtA, date;
    NSTimestamp trDe = null, trA = null;
    
    errorMessage = null;
    xmlFile = DTFileManager.getTempFilePath(".xml", false);
    try {
      xmlWriter = new CktlXMLWriter(new FileWriter(xmlFile));
      xmlWriter.setEscapeSpecChars(true);
      xmlWriter.setCharsToEscape("<>");
      xmlWriter.startDocument();
      xmlWriter.startElement("liste-resume-dt");
      //     xmlWriter.writeElement("service", dtApp().userInfo().nomService());
      xmlWriter.writeElement("service", dtSession.dtUserInfo().dtServiceLibelle());
      xmlWriter.writeElement("date-impression", DateCtrl.dateToString(DateCtrl.now()));
      // Determiner, de quel type sont les DT imprimees
      recSrc = (CktlRecord)interventions.objectAtIndex(0);
      if ((recSrc.stringForKey("intEtat")).equals(EOEtatDt.ETAT_TRAITEES)) {  // Traitees
        reportType = 2;
        xmlWriter.writeElement("libelle-date", "Date"+CktlXMLWriter.SpecChars.br+"réalisation");
      } else {                // Toutes les autres
        reportType = 1;
        xmlWriter.writeElement("libelle-date", "Date"+CktlXMLWriter.SpecChars.br+"souhaitée");
      }
      dtDe = recSrc.dateForKey("intDateCreation");
      dtA = dtDe;
      // Ajouter les infos pour le titre de l'impression
      xmlWriter.startElement("liste");
      for (int i=0; i<interventions.count(); i++) {
        xmlWriter.startElement("dt");
        recSrc = (CktlRecord)interventions.objectAtIndex(i);
        // Num DT.
        xmlWriter.writeElement("cle-service", recSrc.valueForKey("intCleService").toString());
        // Date de creation de la DT
        date = recSrc.dateForKey("intDateCreation");
        //if (dtDe.timeIntervalSinceDate(date) > 0) dtDe = date;
        //if (dtA.timeIntervalSinceDate(date) < 0) dtA = date;
        if (DateCtrl.isAfter(dtDe,date)) dtDe = date;
        if (DateCtrl.isBefore(dtA,date)) dtA = date;
        
        xmlWriter.writeElement("date-appel", DateCtrl.dateToString(date));
        // Nom de la personne appelant
        rec = recSrc.recForKey("toIndividuAppelant");
        s = formatUserName(rec);
        // ...Ajouter le login de la personne
        s2 = individuBus().loginForPersId(rec.numberForKey("persId"));
        if (s2.length() > 0)
          s += CktlXMLWriter.SpecChars.br+"("+s2+")";
        xmlWriter.writeElement("demandeur", s);
        // Telephone de la personne appelante
        datas = individuBus().telephonesForPersId(rec.numberForKey("persId"), "PRF", "TEL");
        if (datas.count() > 0)
          s = CktlRecord.recordStringForKey(datas.objectAtIndex(0), "noTelephone");
        else
          s = StringCtrl.emptyString();
        xmlWriter.writeElement("demandeur-telephone", s);
        // Intervenants
        s = formatIntervenants(recSrc.arrayForKey("tosIntervenant"), CktlXMLWriter.SpecChars.br);
        xmlWriter.writeElement("intervenants", s);
        // Motif de la DT
        xmlWriter.startElement("motif");
        s = formatXMLText(recSrc.stringForKey("intMotif"), false, 200);
        xmlWriter.writeCharacters(s);
        xmlWriter.endElement(); // motif
        // Dates de realisation ou souhaitees
        if (reportType == 1) // Prevue
          xmlWriter.writeElement("date-realisation", recSrc.dateStringForKey("intDateSouhaite"));
        else {
          date = getDateRealisation(recSrc.numberForKey("intOrdre"));
          if (date != null) {
            if (trDe == null) {
              trDe = date;
              trA = date;
            }
            //if (trDe.timeIntervalSinceDate(date) > 0) trDe = date;
            //if (trA.timeIntervalSinceDate(date) < 0) trA = date;
            if (DateCtrl.isAfter(trDe,date)) trDe = date;
            if (DateCtrl.isBefore(trA,date)) trA = date;
            xmlWriter.writeElement("date-realisation", DateCtrl.dateToString(date));
          }
        }
        xmlWriter.endElement(); // dt
        if (listener != null) listener.nextXmlDataEntity();
      }
      xmlWriter.endElement(); // liste
      xmlWriter.startElement("total");
      xmlWriter.writeElement("total-dt", String.valueOf(interventions.count()));
      xmlWriter.writeElement("date-int-de", DateCtrl.dateToString(dtDe));
      xmlWriter.writeElement("date-int-a", DateCtrl.dateToString(dtA));
      if (trDe != null) {
        xmlWriter.writeElement("date-tra-de", DateCtrl.dateToString(trDe));
        xmlWriter.writeElement("date-tra-a", DateCtrl.dateToString(trA));
      } else {
        xmlWriter.writeElement("date-tra-de", "-");
        xmlWriter.writeElement("date-tra-a", "-");
      }
      xmlWriter.endElement(); // total
      xmlWriter.endElement(); // liste-resume-dt
      xmlWriter.endDocument();
      xmlWriter.close();
    } catch(Exception e) {
      e.printStackTrace();
      errorMessage = CktlLog.getMessageForException(e);
    }
    return xmlFile;
  }

  /**
   * Teste si la maquette avec l'identifiant <code>templateId</code> est
   * installee sur le serveur d'impression.
   */
  public boolean testTemplate(String templateId) {
    // Test si la maquette est installee
    errorMessage = printBus().checkTemplate(templateId);
    if (errorMessage != null)
      errorMessage =
        "L'impression ne peut pas être effectuée. Le message d'erreur :\n"+errorMessage;
    return (!hasError());
  }

  /**
   * Effecture la creation de document PDF en utilisant la maquette avec
   * l'identifiant <code>reportId</code> et le fichier des donnees
   * <code>xmlDataFile</code>. Si le parametre <code>deleteDataFile</code> est
   * <i>true</i>, alors le ficher des donnees est supprime a la fin de creation
   * de PDF.  
   */
  public void printReport(String reportId, String xmlDataFile, String resultFile) {
    if (testTemplate(reportId))
      errorMessage =
        printBus().printPDFReport(reportId, xmlDataFile, resultFile);
  }

  /**
   * Prepare le texte <code>comment</code> a etre ajoute dans le document XML.
   */
  private String formatXMLText(String comment,
      boolean preserveNewLine,
      int maxLength)
  {
    if (!StringCtrl.isEmpty(comment)) {
      // On ellimine les symboles speciaux. Il faut le faire ici, car
      // le XMLWriter ne peut pas le faire automatiquement (par exemple,
      // cnfusion entre "&" qu'il faut remplacer et ceux qu'il faut garder
      comment = StringCtrl.replace(comment, "&", CktlXMLWriter.SpecChars.amp);
      if (preserveNewLine)
        comment = StringCtrl.replace(comment, "\n", CktlXMLWriter.SpecChars.br);
      if (maxLength > 0)
        comment = StringCtrl.compactString(comment, maxLength, CompactSuffix);
    }
    return comment;
  }

  /**
   * Filtre la liste des traitements <code>allTraitements</code> ne prenent
   * que ceux qui appartiennent a l'utilisateur <code>noIndividu</code>. 
   */
  private NSArray traitementsForIndividu(int noIndividu, NSArray allTraitements) {
    NSMutableArray objects = new NSMutableArray();
    CktlRecord rec;
    if (allTraitements.count() > 0) {
      for(int i=0; i<allTraitements.count(); i++) {
        rec = (CktlRecord)allTraitements.objectAtIndex(i);
        if (rec.intForKey("noIndividu") == noIndividu)
          objects.addObject(rec);
      }
    }
    return objects;
  }
  
  /**
   * Recuperer le nom de la personne sous la forme "P.Nom".
   * Le parametre <code>rec</code> est un objet <code>NSKeyValueCoding</code>
   * ave les champs <i>prenom</i> et <i>nomUsuel</i>.
   */
  private String formatUserName(NSKeyValueCoding rec) {
    // Nom de la personne appelante
    String s = (String)rec.valueForKey("prenom");
    String s2 = (String)rec.valueForKey("nomUsuel");
    return StringCtrl.formatName(s, s2);
  }
  
  /**
   * Recuperer les noms des intervenants de la liste des affectations donnees.
   */
  private String formatIntervenants(NSArray liste, String newLineChar) {
    StringBuffer sb = new StringBuffer();
    if (liste.count() > 0) {
      CktlRecord rec;
      for(int i=0; i < liste.count(); i++) {
        rec = CktlRecord.recordRecForKey(liste.objectAtIndex(i), "toIndividuUlr");
        if (sb.length() > 0) sb.append(newLineChar);
        sb.append(formatUserName(rec));
      }
      return sb.toString();
    }
    return "-";
  }
  
  /**
   * Recupere la date de la realisation de la demande avec l'identifiant
   * <code>intOrdre</code>. C'est la date de dernier traitement effectue.
   */
  private NSTimestamp getDateRealisation(Number intOrdre) {
    NSArray datas = interventionBus().fetchArray("Traitement",
        DTDataBus.newCondition("intOrdre="+intOrdre+" and traEtat='T'"),
        CktlSort.newSort("traDateFin", CktlSort.Descending));
    if (datas.count() > 0)
      return CktlRecord.recordDateForKey(datas.objectAtIndex(0), "traDateFin");
    else
      return null;
  }
}
