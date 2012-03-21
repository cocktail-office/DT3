import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.planning.PartagePlanning;
import fr.univlr.cri.planning.SPOccupation;

/*
 * Copyright Universit� de La Rochelle 2006
 *
 * ctarade@univ-lr.fr
 *
 * Ce logiciel est un programme informatique servant � g�rer les comptes
 * informatiques des utilisateurs. 
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

/**
 * Classes rassemblant les methodes qui exportent les donn�es
 * pour le serveur de planning
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */

public class DAServeurPlanning extends DirectAction {

  public DAServeurPlanning(WORequest aRequest) {
    super(aRequest);
  }
  
  /**
   * donne la liste des traitements d'un agent sur une periode
   */
  public WOActionResults traitementsPourPeriodeAction() {
    NSDictionary dicoParams = PartagePlanning.dicoParams(request());
    
    Number noIndividu   = (Number) dicoParams.valueForKey("noIndividu");
    NSTimestamp debut   = (NSTimestamp) dicoParams.valueForKey("debut");
    NSTimestamp fin     = (NSTimestamp) dicoParams.valueForKey("fin");
    
    NSArray spOccupations = new NSArray();
    int status = 0;
    String errMessage = "";
    
    if (noIndividu != null) {
      if (debut != null) {
        if (fin != null) {
        	if (noIndividu != null) {
            if (DateCtrl.isBeforeEq(debut, fin)) {
            	
            	// determiner si l'intervenant autorise la publication du contenu de ses traitements
            	boolean isExportPlanning = (new DTPreferencesBus(dtSession().defaultEditingContext())).exportPlanning(noIndividu);
            	
            	// la liste des traitements
              NSArray recsOccupation = interventionBus().findVTraitementsForPeriodeIgnoring(noIndividu, debut, fin, null);

              for (int i = 0; i < recsOccupation.count(); i++) {
                CktlRecord recVTra = (CktlRecord) recsOccupation.objectAtIndex(i);
                // prefix : DT/service - activite #numero
                String prefix = "DT/" + recVTra.stringForKey("lcStructure") + " - " + recVTra.stringForKey("actLibelle") + " #" + recVTra.intForKey("intCleService");
                // afficher O/N le traitement
                String strDetail = prefix;
                if (isExportPlanning) {
                	// est-il consultable
                  boolean showDetail = "O".equals(recVTra.stringForKey("traConsultable"));
                  strDetail += " : ";
                  if (showDetail) {
                  	// detail du traitement : suppression des sauts de ligne
                  	strDetail += DTStringCtrl.replace(DTStringCtrl.replace(recVTra.stringForKey("traTraitement"), "\n", ""), "\r", "");
                  } else {
                  	strDetail += "<traitement masqué>";
                  }
                }
                SPOccupation spOccupation = new SPOccupation(
                		recVTra.dateForKey("traDateDeb"), recVTra.dateForKey("traDateFin"), "DT", strDetail);

                spOccupations = spOccupations.arrayByAddingObject(spOccupation);
              }
              
              status = 1;

            } else {
              // erreurs de dates
              errMessage = "Date debut après la date de fin";
            }
          } else {
            // individu non trouv�
            errMessage = "Individu non trouvé";
          }
        } else {
          // fin absent
          errMessage = "Date de fin absente";
       }
      } else {
        // debut absent
        errMessage = "Date de début absente";
     }
    } else {
      // noIndividu absent
      errMessage = "noIndividu absent";
   }

    // creation Reponse a retourner
    
    WOResponse resultat = new WOResponse();
    resultat = PartagePlanning.reponsePlanning(spOccupations, status, errMessage);
  
    return resultat;
  }

  // ** les BUS de donn�es **
  
  private DTInterventionBus interventionBus() {
    return dtSession().dataCenter().interventionBus();
  }
}
