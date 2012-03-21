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

import java.util.Hashtable;
import java.util.Vector;

import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

/**
 * Definit les regles de gestion des informations pour la vue Reprographie.
 * Contient le resume des informations selectionnees ou indiquees dans
 * l'interface graphique.
 * 
 * <p>
 * Cette classe peut etre etendu pour indiquer les regles de gestion propres a
 * chaque etablissement. Dans ce cas, le parametre de configuration
 * <code>REPRO_DATA_INFO_CLASS</code> doit indiquer le nom de la classe a
 * utiliser.
 * </p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public abstract class DTReproDataInfo {
  /** La constante pour indiquer le chemin vide des fichiers attaches */
  private static final Vector EmptyVector = new Vector();
  // Le nom du parametre de configuration utilise pour initialiser
  // une instance de la classe derive
  private static final String DataInfoClassKey = "REPRO_DATA_INFO_CLASS";
  // Les code de reliures a utiliser pour la base de donnees
  public static final String DB_RELIURE_NON = "NON";
  public static final String DBReliureEncolage = "ENC";
  public static final String DBReliureReliure = "REL";
  public static final String DBReliureAgraf1pt = "AGR1";
  public static final String DBReliureAgraf2pt = "AGR2";
  // Les valeurs des tags des boutons radio "Support"
  public static final int SUPPORT_NUMERIC = 0;
  public static final int SUPPORT_PAPIER = 1;
  // Les types de couvertures
  public static final int COUV_TYPE_CARTONNE = 0;
  public static final int COUV_TYPE_TRANSPARENT = 1;
  // Les types des reliures
  public static final int RELIURE_NON = 0;
  public static final int RELIURE_ENCOLAGE = 1;
  public static final int RELIURE_RELIURE = 2;
  public static final int RELIURE_AGRAF_1PT = 3;
  public static final int RELIURE_AGRAF_2PT = 4;
  
  // Le code de l'activite / format d'impression
  public int actOrdre = -1;
  public int cartOrdre = -1;
  public String format = "";
  // Les infos sur le document
  public int typeDocSupport = SUPPORT_NUMERIC;
  public Vector filePaths = EmptyVector;
  public int nbCopies = 0;
  public int nbPages = 0;
  // Les infos sur la finition : couverture
  public int typeCouverture = COUV_TYPE_CARTONNE;
  public boolean couvDessus = false;
  public boolean couvDessous = false;
  // Les infos sur la finition : reliure
  public int typeReliure = RELIURE_NON;
  // Les infos sur la finition : plastification
  public boolean plastification = false;
  // Les infos sur la finition : format livret
  public boolean livret = false; 
  // Les parametres de configuration
  private Hashtable config; 
  // Le message d'erreur, si on veut le memoriser
  private String errorMessage;
  
  /**
   * La methode a utiliser pour cree une nouvelle instance de la classe
   * SwapReproDataInfo ou bien de son derivee.
   * 
   * @param config
   *        Contient la configuration de la classe. Cette objet peut
   *        correspondre a la configuration dans l'application Demande de
   *        Travaux.
   * 
   * @param useDefautClass
   *        Indique si l'instance de l'implementation par defaut doit etre
   *        utilisee dans tous les cas. Les parametres de configuration sont
   *        dans ce cas ignorees et la methode retourne toujours une instance de
   *        la classe <code>SwapReproDataInfoULR</code>.
   *  
   */
  public static DTReproDataInfo getNewInstance(
      Hashtable config, boolean useDefautClass)
  {
    DTReproDataInfo instance = null;
    if (!useDefautClass) {
      String className = (String)config.get(DataInfoClassKey);
      try {
        if (className != null)
          instance = (DTReproDataInfo)Class.forName(className).newInstance();
      } catch(Exception e) {
        instance = new DTReproDataInfoULR();
        instance.setErrorMessage(
            "La classe de la gestion des informations reprographie n'a pa pu être initialisée :\n"+
            "  "+messageForException(e)+
            "\nLa classe par defaut sera utilisée.");
      }
    }
    if (instance == null) instance = new DTReproDataInfoULR();
    instance.init(config);
    return instance;
  }
  
  /**
   * Retourne le message d'erreur correspondant a l'exception <code>ex</code>.
   */
  private static String messageForException(Throwable ex) {
    // On ne fait pas d'appel aux CktlLog, car cette classe n'est pas visible
    // lorsqu'on se trouve dans un package
    StringBuffer msg = new StringBuffer(ex.getClass().getName());
    if (ex.getMessage() != null)
      msg.append(" : ").append(ex.getMessage());
    return msg.toString();
  }

  /**
   * Initialise l'intance de gestionnaire des information pour une demande
   * reprographie.
   */
  public void init(Hashtable config) {
    this.config = config;
  }
  
  /**
   * Retourne la reference vers le gestionnaire de la configuration.
   */
  public Hashtable config() {
    // On va cree une instance pour eviter NullPointerException
    // Mais il faudrait initialiser la configuration explicitement
    if (config == null) config = new Hashtable();
    return config;
  }
  
  /**
   * Retourne le message d'erreur ou <i>null</i> si la derniere operation a ete
   * executee sans erreurs.
   */
  public String errorMessage() {
    return errorMessage;
  }
  
  /**
   * Memorise le message d'erreur. Il peut ensuite etre obtenu avec la methode
   * <code>errorMessage</code>.
   * 
   * @see #errorMessage()
   */
  public void setErrorMessage(String message) {
    errorMessage = message;
  }
  
  /**
   * Teste si une erreur est survenu lors de l'execution des dernieres
   * operations. Le message peut etre obtenu avec la methode
   * <code>errorMessage</code>.
   */
  public boolean hasError() {
    return (errorMessage != null);
  }
  
  /**
   * Initialise la description a partir des informations contenues dans
   * l'enregirtrement <code>recRepro</code>. Ceci doit etre un eregistrement
   * de l'entitee <code>InterventionRepro</code>.
   */
  public void takeInfoFromRecord(Hashtable recRepro) {
    // Type de document
    String s = (String)recRepro.get("typeDocument");
    if (s.equals("N"))  // Numeric
      typeDocSupport = SUPPORT_NUMERIC;
    else // Tout le reste est considere comme papier
      typeDocSupport = SUPPORT_PAPIER;
    nbCopies = ((Number)recRepro.get("nbCopies")).intValue();
    nbPages = ((Number)recRepro.get("nbPages")).intValue();
    // Couverture
    s = (String)recRepro.get("typeCouv");
    if (s.equals("T")) // Transparent
      typeCouverture = COUV_TYPE_TRANSPARENT;
    else // Sinon, on considere que c'est de carton
      typeCouverture = COUV_TYPE_CARTONNE;
    couvDessus = recRepro.get("couvDessus").equals("O");
    couvDessous = recRepro.get("couvDessous").equals("O");
    // Reliures
    s = (String)recRepro.get("reliure");
    if (s.equals("ENC"))
      typeReliure = RELIURE_ENCOLAGE;
    else if (s.equals("REL"))
      typeReliure = RELIURE_RELIURE;
    else if (s.equals("AGR1"))
      typeReliure = RELIURE_AGRAF_1PT;
    else if (s.equals("AGR2"))
      typeReliure = RELIURE_AGRAF_2PT;
    else // Inconnu est considere comme NON
      typeReliure = RELIURE_NON;
    // Plastification
    plastification = recRepro.get("plastific").equals("O");
    // Livret
    livret = recRepro.get("livret").equals("O");
    // format
    //actOrdre = ((Integer)recRepro.get("actOrdre")).intValue();
    //cartOrdre = ((Integer)recRepro.get("cartOrdre")).intValue();
    //format = (String) recRepro.get("format");
  }
  
  /**
   * Retourne le contenu des informations.
   */
  public String toString() {
    String separator = ";\n";
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append("actOrdre=").append(actOrdre).append(separator);
    sb.append("actGratuite=").append(isActGratuite()).append(separator);
    sb.append("typeDocSupport=").append(typeDocSupport).append(separator);
    sb.append("filePath=").append(filePaths).append(separator);
    sb.append("nbCopies=").append(nbCopies).append(separator);
    sb.append("nbPages=").append(nbPages).append(separator);
    sb.append("typeCouverture=").append(typeCouverture).append(separator);
    sb.append("couvertureDessus=").append(couvDessus).append(separator);
    sb.append("couvertureDessous=").append(couvDessous).append(separator);
    sb.append("typeReliure=").append(typeReliure).append(separator);
    sb.append("plastification=").append(plastification).append(separator);
    sb.append("}");
    return sb.toString();
  }
  
  /**
   * Test si tous les objets de la liste <code>what</code> sont inclus
   * dans la liste who.
   */
  private boolean containsAllFromVector(Vector who, Vector what) {
    // Pour etre sur...
    if ((who == null) && (who != what)) return false;
    // Sinon, on teste
    for(int i=0; i<what.size(); i++) {
      if (!who.contains(what.elementAt(i))) return false;
    }
    return true;
  }
  
  /**
   * Test si les chemins des fichier de l'objet sont les memes que ceux
   * donnees dans la liste paths.
   */
  private boolean isSameFilePaths(Vector paths) {
    // Si les deux contient qque chose
    if ((paths.size() > 0) && (filePaths.size() > 0))
    {
      // Alors il faut comparer le contenu
      if (!containsAllFromVector(paths, filePaths)) return false;
      if (!containsAllFromVector(filePaths, paths)) return false;
    }
    // Sinon, il sont egaux
    return true;
  }
  
  /**
   * Test si l'information contenue dans l'objet est identique a celle
   * de <code>info</code>.
   */
  public boolean isSameInfo(DTReproDataInfo info) {
    if (actOrdre != info.actOrdre) return false;
    if (typeDocSupport != info.typeDocSupport) return false;
    if (!isSameFilePaths(info.filePaths)) return false;
    if ((nbCopies != info.nbCopies) || (nbPages != info.nbPages)) return false;
    if (typeCouverture != info.typeCouverture) return false;
    if ((couvDessus != info.couvDessus) || (couvDessous != info.couvDessous)) return false;
    if (typeReliure != info.typeReliure) return false;
    if (plastification != info.plastification) return false;
    return true;
  }
  
  /**
   * Test si l'information contenue dans l'objet decrit une demande du meme
   * montant que celle de <code>info</code>. Le contenu des deux objets peut
   * etre different, mais donner le meme cout.
   * 
   * <p>Cette methode ne verifie pas les couts eux memes, mais donne juste
   * une estimation en fonction des elements selectionnes : nombre des
   * pages, operations gratuites, etc...</p>
   */
  public boolean isSameCout(DTReproDataInfo info) {
    if (info == null) return false;
    // Pa le meme format et les deux ne sont pas gratuits
    if ((actOrdre != info.actOrdre) &&
        (isActGratuite() != info.isActGratuite())) return false;
    // Pas le meme nombre des pages et des copies
    if ((nbPages != info.nbPages) || (nbCopies != info.nbCopies)) return false;
    // Pas les memes couvertures et elles ne sont pas gratuites
    if (hasCouverture() || info.hasCouverture()) {
      if ((!isCouvertureGratuite()) || (!info.isCouvertureGratuite())) {
        if (typeCouverture != info.typeCouverture) return false;
        if ((couvDessus != info.couvDessus) ||
            (couvDessous != info.couvDessous)) return false;
      }
    }
    // Pas la meme reliure et elle n'est pas gratuite
    if ((!isReliureGratuite()) || (!info.isReliureGratuite())) return false;
    // Pas la meme plastification
    if (plastification != info.plastification) return false;
    // Sinon, le prix sera le meme
    return true;
  }
  
  /**
   * Retourne le code de la reliure representee dans cette description.
   * La valeur retournee peut etre utilisee pour enregistrer les informations
   * dans la base de donnees.
   */
  public String getReliureCode() {
    switch (typeReliure) {
      case RELIURE_NON :
        return "NON";
      case RELIURE_ENCOLAGE :
        return "ENC";
      case RELIURE_RELIURE :
        return "REL";
      case RELIURE_AGRAF_1PT :
        return "AGR1";
      case RELIURE_AGRAF_2PT :
        return "AGR2";
      default : // Inconnu
        return "UKNW";
    }
  }

  /**
   * Retourne le libelle de la reliure representee dans cette description.
   */
  public String getReliureLibelle() {
    switch (typeReliure) {
      case RELIURE_NON :
        return "Non relié";
      case RELIURE_ENCOLAGE :
        return "Encolage";
      case RELIURE_RELIURE :
        return "Reliure";
      case RELIURE_AGRAF_1PT :
        return "Agrafé 1 point";
      case RELIURE_AGRAF_2PT :
        return "Agrafé 2 point";
      default : // Inconnu
        return "[Inconnu]";
    }
  }

  /**
   * Retourne le code du type de la couverture represente dans cette
   * description. La valeur retournee peut etre utilisee pour enregistrer
   * les informations dans la base de donnees.
   */
  public String getTypeCouvCode() {
    switch(typeCouverture) {
      case COUV_TYPE_CARTONNE :
        return "C"; 
      case COUV_TYPE_TRANSPARENT :
        return "T";
      default : // Inconnu
        return "U";
    }
  }

  /**
   * Retourne le libelle du type de la couverture represente dans cette
   * description. Si la parametre <code>fullLibelle</code> est <i>true</i>,
   * alors l'indication sur la couverture dessus et/ou dessous est ajoutee.
   */
  public String getTypeCouvLibelle(boolean fullLibelle) {
    StringBuffer sb = new StringBuffer();
    if (couvDessus || couvDessous) {
      if (typeCouverture == COUV_TYPE_CARTONNE)
        sb.append("Cartonné"); 
      else if (typeCouverture == COUV_TYPE_TRANSPARENT)
        sb.append("Transparent");
      else
        sb.append("Inconnu");
      if (fullLibelle) {
        sb.append(" (");
        if (couvDessus && couvDessous)
          sb.append("dessus-dessous");
        else if (couvDessus)
          sb.append("dessus");
        else
          sb.append("dessous");
        sb.append(")");
      }
    } else {
      sb.append("[Aucune]");
    }
    return sb.toString();
  }

  /**
   * Retourne le code du type du document represente dans cette description.
   * La valeur retournee peut etre utilisee pour enregistrer les informations
   * dans la base de donnees.
   */
  public String getTypeDocumentCode() {
    switch(typeCouverture) {
      case SUPPORT_NUMERIC :
        return "N"; 
      case SUPPORT_PAPIER :
        return "P";
      default : // Inconnu
        return "U";
    }
  }

  /**
   * Retourne le libelle indiquant le nombre des pages dans la definition.
   * Si le nombre des pages n'est pas indique, retourne "non indique".
   */
  public String getNbPagesLibelle() {
    if (nbPages <= 0)
      return "<non indiqué>";
    else
      return Integer.toString(nbPages);
  }
  
  /**
   * Retourne le libelle indiquant le nombre des copies dans la definition.
   * Si le nombre des copies n'est pas indique, retourne "non indique".
   */
  public String getNbCopiesLibelle() {
    if (nbCopies <= 0)
      return "<non indiqué>";
    else
      return Integer.toString(nbCopies);
  }
  
  /**
   * Retourne le libelle indiquant le type de la plastification d'un document.
   * Pour un document on peut ne pas avoir de plastification, effectuer la
   * plastification uniquement et imprimer avant de plastifier.
   */
  public String getPlastificationLibelle() {
    if (!plastification)
      return "[Aucune]";
    else if ((typeDocSupport == SUPPORT_PAPIER) && (nbCopies < 2))
      return "Plastification";
    else
      return "Copie/Plastification";
  }
  
  /**
   * Retourne le libelle indiquant si le format livret doit ou non etre utilise.
   */
  public String getLivretLibelle() {
    return ((livret)?"Oui":"Non");
  }

  /**
   * Teste si le type de document enregistre dans cette description correspond
   * au support numeric (fichiers attaches).
   */
  public boolean isSupportNumeric() {
    return (typeDocSupport == SUPPORT_NUMERIC);
  }

  /**
   * Test si l'impression correspondant au format selectionne est gratuite.
   */
  public boolean isActGratuite() {
    return (cartOrdre == -1);
  }
  
  /**
   * Teste si la definition indique que les documents doivent avoir les
   * couvertures. C'est le cas s'il y a au moins une couverture dessus ou
   * dessous.  
   */
  public boolean hasCouverture() {
    return (couvDessus || couvDessous);
  }
  
  /**
   * Test si la couverture choisie est gratuite.
   */
  public abstract boolean isCouvertureGratuite();

  /**
   * Teste si la reliure indique est gtatuite.
   */
  public abstract boolean isReliureGratuite();
  
  /**
   * Retourne la liste des ligne d'un devis. Chaque entre de cette liste
   * est un objet <code>Hashtable</code> contenant le code et la quantite de
   * l'article. Les deux objets doivent etre de type <code>Integer</code>.
   * Il sont accessible via les cles
   * <code>DTPrestaServicesConst.FormCartOrdreKey</code> et
   * <code>DTPrestaServicesConst.FormNbArticlesKey</code> respectivement.
   */
  public abstract Vector getLignes();
  
  /**
   * Resume de la demande DTRepro sous forme textuelle.
   */
  public abstract String getResume();
  
  /**
   * La creation d'un devis est-elle obligatoire pour que la DT soit valide?
   */
  public abstract boolean mustCreateDevis();
  


  private final static String PREFIX_LIBELLE_DEVIS = "Devis DT-Repro #";
  // compte 10 caractres pour le numero ...
  private final static String INT_CLE_SERVICE_FAKE = "9999999999";
  private final static String PREFIX_LIBELLE = "(";
  private final static String SUFFIX_LIBELLE = ")";
  public final static int LIBELLE_DEVIS_MAX_SIZE = 100;
  
  /**
   * Le titre du devis tel qu'il apparaitra dans PIE. Si le numero de DT
   * n'est pas encore connu, on remplace par le numero "fake" qui ne fait
   * que du remplissage (dans ce cas, c'est juste pour du test de taille)
   */
  public static String getLibelleDevis(Number newDtCleService, String libelle) {
    return PREFIX_LIBELLE_DEVIS +
    (newDtCleService != null ? Integer.toString(newDtCleService.intValue()) : INT_CLE_SERVICE_FAKE) +
    (!StringCtrl.isEmpty(libelle) ? PREFIX_LIBELLE + libelle + SUFFIX_LIBELLE : "");
  }
  
  /**
   * La longueur maximale autorisee pour le titre du document. Il faut
   * soustraire la taille des elements reserves pour la construction
   * du libelle du devis de PIE pour l'obtenir.
   */
  public int getTitreMaxSize() {
    return LIBELLE_DEVIS_MAX_SIZE - PREFIX_LIBELLE_DEVIS.length() - INT_CLE_SERVICE_FAKE.length()
      - PREFIX_LIBELLE.length() - SUFFIX_LIBELLE.length();
  }
}
