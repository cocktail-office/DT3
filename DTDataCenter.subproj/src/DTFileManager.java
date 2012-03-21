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
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.FileCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.common.util.SystemCtrl;

import com.webobjects.foundation.NSTimestamp;

/**
 * Le gestionnaire des fichiers temporaires generes lors de l'execution
 * de l'application DT.
 * 
 * <p>Cette classe permet de gerer la liste des fichiers temporaires (les
 * fichiers PDF generes lors de l'edition, par exemple). Ces fichiers sont
 * supprimes lors de la fin de l'execution de l'application.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTFileManager {
  /** Le prefix utilise pour les noms des fichiers temporaires. */
  private static String TempFilePrefix = "DTempDoc_";
  /** Le prefix des fichiers temporaires des anciennes versions de la DT. */
  private static String OldTempFilePrefix = "dtdoc";
  /** La Liste de tous les fichiers temporaires */ 
  private static Vector trashFiles = new Vector();
  
  /**
   * Initialise la liste des fichiers temporaires precedement crees. Cette
   * methode cherche le repertoire temporaire pour les fichier ayant les
   * prefixes "dtdoc" ou "DTempDoc_" et les ajoute dans la liste des fichiers
   * a supprimer.
   */
  public static void initOldTrash() {
    addToVector(
        FileCtrl.findFiles(SystemCtrl.tempDir(), OldTempFilePrefix+"*", false),
        trashFiles);
    addToVector(
        FileCtrl.findFiles(SystemCtrl.tempDir(), TempFilePrefix+"*", false),
        trashFiles);
  }
  
  /**
   * Copie les elements du vecteur <code>source</code> dans le vecteur
   * <code>dest</code>.
   */
  private static void addToVector(Vector source, Vector dest) {
    if ((source != null) && (dest != null) && (source.size() != 0)) {
      for(int i=0; i<source.size(); i++)
         addToTrash((String)source.elementAt(i));
    }
  }
  
  /**
   * Enregistre un nouveau fichier dans la liste des fichiers temporaires.
   * Tous ces fichiers sont supprimes lors de l'appel a la methode
   * <code>purgeTrash</code>.
   * 
   * @see #purgeTrash()
   */
  public static void addToTrash(String filePath) {
    if ((StringCtrl.normalize(filePath).length() > 0) &&
        (!trashFiles.contains(filePath)))
    {
      // [LRAppTasks] : @CktlLog.trace(@"New File to Trash : "+filePath);
      trashFiles.addElement(filePath);
    }
  }
  
  /**
   * Retourne la liste des fichiers temporaires enregistres pour la
   * suppression. La liste est vide, si aucun fichier n'y est enregistre.
   * 
   * <p>Attention, cette methode retourne une copie de la liste originale.
   * Toute modification apportee a la liste retournee ne sera pas prise en
   * dans la liste geree par la classe.</p>
   * 
   * @see #addToTrash(String)
   * @see #cleanTrash()
   */
  public static Vector allTrashFiles() {
    return (Vector)trashFiles.clone();
  }
  
  
  /**
   * Declanche la suppression des fichiers temporaires enregistres pour la
   * suppression. Dans le cas ou un fichier ne peut pas etre supprime, il n'est
   * pas enleve de la liste des fichiers temporaires.
   * 
   * @see #cleanTrash()
   */
  public static void purgeTrash() {
    if (trashFiles.size() > 0) {
      String filePath;
      Vector newTrashFiles = new Vector();
      for(int i=0; i<trashFiles.size(); i++) {
        filePath = (String)trashFiles.elementAt(i);
        // [LRAppTasks] : @CktlLog.trace(@"Suppression du fichier temporaire : "+filePath);
        if (FileCtrl.existsFile(filePath) && (!FileCtrl.deleteFile(filePath))) {
          newTrashFiles.addElement(filePath);
          // [LRAppTasks] : @CktlLog.trace(@"---> le fichier n'a pa pu etre supprime !");
        }
      }
    }
  }
  
  /**
   * Efface la liste des fichiers temporaires sans supprimes les fichiers
   * correspondants.
   * 
   * @see #purgeTrash()
   */
  public static void cleanTrash() {
    trashFiles.removeAllElements();
  }
  
  /**
   * Retourne le nom d'un fichier temporaire ayant une extension donnee. Le
   * fichier est place dans le repertoire temporaire. Si
   * <code>useUrlExpression</code> est <i>true</i>, la reference vers le fichier
   * est retourne sous forme d'un URL "fichier".
   * 
   * <p>Par defaut, un fichier temporaire a le prefix
   * "<code>DTempFile_</code>".</p>
   */
  public static String getTempFilePath(String extension, boolean useUrlExpression) {
    StringBuffer sb = new StringBuffer();
    sb.append(FileCtrl.normalizeDirName(SystemCtrl.tempDir()));
    sb.append(TempFilePrefix).append((new NSTimestamp().secondOfMinute()));
    sb.append((new NSTimestamp().minuteOfHour()));
    if (useUrlExpression) sb.insert(0, "file:/");
    if (!extension.startsWith(".")) sb.append(".");
    return sb.append(extension).toString();
  }
}
