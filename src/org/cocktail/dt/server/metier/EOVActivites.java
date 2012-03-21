/*
 * Copyright Cocktail, 2001-2008 
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

package org.cocktail.dt.server.metier;

import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSValidation;

public class EOVActivites extends _EOVActivites {

		public EOVActivites() {
        super();
    }

    public void validateForInsert() throws NSValidation.ValidationException {
        this.validateObjectMetier();
        validateBeforeTransactionSave();
        super.validateForInsert();
    }

    public void validateForUpdate() throws NSValidation.ValidationException {
        this.validateObjectMetier();
        validateBeforeTransactionSave();
        super.validateForUpdate();
    }

    public void validateForDelete() throws NSValidation.ValidationException {
        super.validateForDelete();
    }

    /**
     * Apparemment cette methode n'est pas appel̩e.
     * @see com.webobjects.eocontrol.EOValidation#validateForUpdate()
     */    
    public void validateForSave() throws NSValidation.ValidationException {
        validateObjectMetier();
        validateBeforeTransactionSave();
        super.validateForSave();
        
    }

    /**
     * Peut etre appele �� partir des factories.
     * @throws NSValidation.ValidationException
     */
    public void validateObjectMetier() throws NSValidation.ValidationException {
      

    }
    
    /**
     * A appeler par les validateforsave, forinsert, forupdate.
     *
     */
    private final void validateBeforeTransactionSave() throws NSValidation.ValidationException {
           
    }
    
    // ajouts

    /** les conditions à rajouter aux qualifier pour masquer les activites cachees */
  	public final static String COND_HIDE_ACTIVITE =  " and " + EOVActivites.GRP_AFFICHABLE_KEY + "='O'";
  	public final static String COND_HIDE_UNDERSCORE = " and not(" + EOVActivites.ACT_LIBELLE_KEY + " isCaseInsensitiveLike '_*')";

  	public final static String DISPLAY_ARBORESENCE_KEY = "displayArboresence";
  	
  	/**
  	 * TODO attention doublon avec la methode DTActiviteBus#findActivitePathString() => A FUSIONNER
  	 * @return
  	 */
  	public String displayArboresence() {
  		if (StringCtrl.isEmpty(actOrdreHierarchie()) || !actOrdreHierarchie().contains(";")) {
  			return actLibelle();
  		} else {
  			return toActPere().displayArboresence() + "; " + actLibelle();
  		}
  	}

  	/**
  	 * Classer alphabetiquement une liste d'actvités (classsement des parents, grand parents ...)
  	 * @param list
  	 * @return
  	 */
  	public static NSArray<EOVActivites> sort(NSArray<EOVActivites> list) {
  		return CktlSort.sortedArray(list, DISPLAY_ARBORESENCE_KEY);
  	}

  	/**
  	 * 
  	 * @return
  	 */
  	public String getGrpMailSiExistantEtUtilisable() {
  		String grpMail = null;
  		
  		EOVActivites eoVActiviteResp = this;
  		// Se positionner sur l'activite avec la definition des responsables
      if(actOrdre().intValue() != actResp().intValue()) {
      	eoVActiviteResp = toActResp();
      }
      // Verifier qu'il faut bien envoyer le mail au service
      if (eoVActiviteResp.actMailService().equals("O")) {
      	EOGroupesDt eoGroupesDt = eoVActiviteResp.toGroupesDt();
      	if (eoGroupesDt != null && 
      			StringCtrl.normalize(eoGroupesDt.grpEmail()).length() > 0) {
      		grpMail = eoGroupesDt.grpEmail();
      	}
      }
      
      return grpMail;
  	}
}
