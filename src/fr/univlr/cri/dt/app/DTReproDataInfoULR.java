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
package fr.univlr.cri.dt.app;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;

import fr.univlr.cri.dt.services.common.DTPrestaServicesConst;

/**
 * Definit les regles de gestion da la demande Reprographie pour l'Universite
 * de la Rochelle.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTReproDataInfoULR extends DTReproDataInfo {
  // Les codes des parametres de configuration
  public static final String NbCouvA4R = "REPRO_NB_COUV_A4R_COD";
  public static final String NbCouvA4RV = "REPRO_NB_COUV_A4RV_COD";
  public static final String NbCouvA3R = "REPRO_NB_COUV_A3R_COD";
  public static final String NbCouvA3RV = "REPRO_NB_COUV_A3RV_COD";
  public static final String NbTranspA4 = "REPRO_NB_TRANSP_A4_COD";
  public static final String ClCouvA4R = "REPRO_CL_COUV_A4R_COD";
  public static final String ClCouvA4RV = "REPRO_CL_COUV_A4RV_COD";
  public static final String ClCouvA3R = "REPRO_CL_COUV_A3R_COD";
  public static final String ClCouvA3RV = "REPRO_CL_COUV_A3RV_COD";
  public static final String ClTranspA4 = "REPRO_CL_TRANSP_A4_COD";
  public static final String PlastifA4 = "REPRO_PLASTIF_A4_COD";
  public static final String PlastifA3 = "REPRO_PLASTIF_A3_COD";
  public static final String Reliure = "REPRO_RELIURE_COD";
  private Vector lignes = null;
  
  /**
   * Efface les definitions des lignes de devis precedement definies.
   */
  private void resetLignes() {
    if (lignes == null) lignes = new Vector();
    else lignes.removeAllElements();
  }
  
  /**
   * Ajoute une entree dans la liste des lignes d'un devis.
   */
  private void addLine(Number idArticle, BigDecimal nbArticles) {
    Hashtable entry = new Hashtable();
    entry.put(DTPrestaServicesConst.FormCaarIdKey, idArticle);
    entry.put(DTPrestaServicesConst.DevisNbArticlesKey, nbArticles);
    lignes.addElement(entry);
  }

  /**
   * Ajoute une entree dans la definition des lignes d'un devis. Le code
   * de l'article doit etre disponible dans la configuration avec le code
   * donne dans <code>artConfKey</code>. Le parametre <code>nbArticles</code>
   * indique le nombre des articles dans une ligne.
   * 
   * <p>Si le code de l'article n'est pas disponible dans la configuration,
   * la ligne n'est pas ajoutee.</p>
   */
  public void addLineConf(String artConfKey, int nbArticles) {
    if (config().get(artConfKey) != null) {
      addLine(
          Integer.valueOf((String)config().get(artConfKey)),
          new BigDecimal(nbArticles));
    }
  }
  
  /**
   * Indique si l'impression est "recto-verso".
   */
  public boolean isFormatRectoVerso() {
    return format.toLowerCase().indexOf("recto-verso;") >= 0;
  }

  /**
   * Indique si l'impression est "recto". Cette valeur est ignoree si
   * l'impression est "transparent" (elle est "recto" dans ce cas).
   */
  public boolean isFormatRecto() {
    return format.toLowerCase().indexOf("recto;") >= 0;
  }

  /**
   * Indique si l'impression est en noir et blanc.
   */
  public boolean isFormatNoirBlanc() {
    return format.toLowerCase().indexOf("nb;") >= 0;
  }

  /**
   * Indique si l'impression est en couleur.
   */
  public boolean isFormatCouleur() {
    return format.toLowerCase().indexOf("couleur") > 0;
  }

  /**
   * Indique si l'impression est en papier.
   */
  public boolean isFormatPapier() {
    return format.toLowerCase().indexOf("papier;") >= 0;
  }

  /**
   * Indique si l'impression est sur le transparent.
   */
  public boolean isFormatTransparent() {
    return format.toLowerCase().indexOf("transparent;") >= 0;
  }

  /**
   * Indique si l'impression est en format A4.
   */
  public boolean isFormatA4() {
    return format.toLowerCase().indexOf("a4") >= 0;
  }

  /**
   * Indique si l'impression est en format A3.
   */
  public boolean isFormatA3() {
    return format.toLowerCase().indexOf("a3;") >= 0;
  }

  /*
   * @see fr.univlr.cri.dt.app.SwapReproDataInfo#isReliureGratuite()
   */
  public boolean isReliureGratuite() {
    // tout est gratuit sauf reliure
    return (typeReliure != RELIURE_RELIURE);
  }

  /*
   * @see fr.univlr.cri.dt.app.SwapReproDataInfo#isCouvertureGratuite()
   */
  public boolean isCouvertureGratuite() {
    // Toutes les couvertures sont payantes
    return false;
  }
  
  /*
   * @see fr.univlr.cri.dt.app.SwapReproDataInfo#getLignes()
   */
  public Vector getLignes() {
    // Si Nb.Copies == 0, on considere qu'aucune copie ne sera necessaire
    // mais il faudra traiter le nb de pages indique.
    int totalPages = ((nbCopies == 0)?nbPages:(nbCopies * nbPages));
    resetLignes();
    // L'impression principale
    // Principe : rien si le type d'impression est gratuit ou si le document
    // est "papier" et nb de copies est = 0 (rien a copier)
    if (!isActGratuite() &&
        (((typeDocSupport == SUPPORT_PAPIER) && (nbCopies > 0)) ||
         ((typeDocSupport == SUPPORT_NUMERIC) && (totalPages > 0))))
    {
      // Si RV, on a deux fois moins de pages
      if (isFormatRectoVerso()) {
      	// dans le cas particuler de totalPage = 1, on le met a 2 pour avoir 2/2=1
      	if (totalPages == 1) {
      		totalPages = 2;
      	}
      	addLine(new Integer(cartOrdre), new BigDecimal(totalPages/2));
      }
      else
        addLine(new Integer(cartOrdre), new BigDecimal(totalPages));
    }
    // S'il s'agit de la plastification, alors tout les reste est ignore
    if (plastification) {
      if (isFormatA4()) { // Plastification A4
        addLineConf(PlastifA4, totalPages);
      } else if (isFormatA3()) { // Plastification A3
        addLineConf(PlastifA3, totalPages);
      }
    // Sinon, on peut enregistrer les couvertures et les relures 
    } else {
      // S'il y a une couverture
      if (hasCouverture()) {
        // La couverture cartonne
        if (typeCouverture == COUV_TYPE_CARTONNE) {
          if (isFormatNoirBlanc() && isFormatA4()) { // N&B,A4,R-RV
            if (couvDessus && couvDessous) // RV
              addLineConf(NbCouvA4RV, nbCopies);
            else // R seulement
              addLineConf(NbCouvA4R, nbCopies);
          } else if (isFormatNoirBlanc() && isFormatA3()) { // N&B,A3,R-RV 
            if (couvDessus && couvDessous) // RV
              addLineConf(NbCouvA3RV, nbCopies);
            else // R seulement
              addLineConf(NbCouvA3R, nbCopies);
          } else if (isFormatCouleur() && isFormatA4()) { // Couleur,A4,R-RV 
            if (couvDessus && couvDessous) // RV
              addLineConf(ClCouvA4RV, nbCopies);
            else // R seulement
              addLineConf(ClCouvA4R, nbCopies);
          } else if (isFormatCouleur() && isFormatA3()) { // Couleur,A3,R-RV 
            if (couvDessus && couvDessous) // RV
              addLineConf(ClCouvA3RV, nbCopies);
            else // R seulement
              addLineConf(ClCouvA3R, nbCopies);
          }
        } else {
          // Couverture transparent pour A4 uniquement
          if (isFormatA4()) {
            if (isFormatNoirBlanc()) {
              if (couvDessus && couvDessous) // RV
                addLineConf(NbTranspA4, nbCopies*2); // Deux transparent
              else // R seulement
                addLineConf(NbTranspA4, nbCopies); // Un seul transparent
            } else if (isFormatCouleur()) {
              if (couvDessus && couvDessous) // RV
                addLineConf(ClTranspA4, nbCopies*2); // Deux transparent
              else // R seulement
                addLineConf(ClTranspA4, nbCopies); // Un seul transparent
            }
          }
        }
      }
      // Reliure, une seule reliure est payante
      if (typeReliure == RELIURE_RELIURE) {
        addLineConf(Reliure, nbCopies);
      }
    }
    return lignes;
  }

  
  /**
   * Resume de la demande DTRepro sous forme textuelle.
   */
  public String getResume() {
    StringBuffer texte = new StringBuffer("Document :\n");
    if (typeDocSupport == DTReproDataInfo.SUPPORT_PAPIER) {
      texte.append("  <document papier>");
    } else {
      Vector allPaths = filePaths;
      if (allPaths.size() == 0) {
        texte.append("  <non indiqué>");
      } else {
        for(int i=0; i<allPaths.size(); i++) {
          texte.append("  <");
          texte.append(FileCtrl.getFileName((String)allPaths.elementAt(i))).append(">");
        }
      }
    }
    texte.append("\nFormat du document : ").append(format);
    texte.append("\nNombre de copies : ");
    texte.append(getNbCopiesLibelle());
    texte.append("\nNombre de pages : ");
    texte.append(getNbPagesLibelle());
    texte.append("\nCouverture : ");
    texte.append(getTypeCouvLibelle(true));
    texte.append("\nReliure : ");
    texte.append(getReliureLibelle());
    texte.append("\nPlastification : ");
    texte.append(getPlastificationLibelle());
    texte.append("\nLivret : ");
    texte.append(getLivretLibelle());
    return texte.toString();
    
//    buf.append("Format : ").append(format);
//      .append(isFormatPapier() ? "Papier" : "Transparent").append(" / ")
//      .append(isFormatNoirBlanc() ? "Noir&Blanc" : "Couleur").append(" / ")
//      .append(isFormatA4() ?  "A4" : "A3");
//    // seul le papier a une 4eme activite (recto - recto/verso)
//    if (isFormatPapier()) {
//      buf.append(" / ").append(isFormatRecto() ? "Recto" : "Recto-Verso");
//    }
//    buf.append("\n");
//    buf.append("Pages : ").append(getNbPagesLibelle()).append("\n");
//    buf.append("Copies : ").append(getNbCopiesLibelle()).append("\n");
//    if (hasCouverture()) {
//      buf.append("Couverture : ").append(getTypeCouvLibelle(true)).append("\n");
//    }
//    buf.append("Reliure : ").append(getReliureLibelle()).append("\n");
//    if (plastification) {
//      buf.append("Plastification : ").append(getPlastificationLibelle()).append("\n");
//    }
//    if (livret) {
//      buf.append("Livret : ").append(getLivretLibelle());
//    }
//
//    return buf.toString();
  }
  
  /**
   * Pour La Rochelle, seule les DTRepro avec devis sont acceptees
   */
  public boolean mustCreateDevis() {
    return true;
  }
}
