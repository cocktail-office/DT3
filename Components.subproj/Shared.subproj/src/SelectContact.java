

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertPage;
import org.cocktail.fwkcktlwebapp.server.components.CktlAlertResponder;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Gestionnaire de la page de definition et selection des contactes des
 * utilisateurs.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class SelectContact extends DTWebPage {
  // Constantes pour indiquer le mode d'utilisation de la fenetre
  public static final int DLG_MODE_EDIT = 0;
  public static final int DLG_MODE_READ = 1;
  // Constantes indiquant le mode d'edition des informations
  private static final int EDIT_STATUS_NONE = 0;
  private static final int EDIT_STATUS_ADD = 1;
  private static final int EDIT_STATUS_UPDATE = 2;
  // Le mode d'utilisation en cours
  private int dialogMode;
  private int editStatus;
  // Les donnees sur l'individu
  public DTUserInfo contactUserInfo;
  // Les donnees sur les contacts
  private CktlRecord recDefaultContact;
  public CktlRecord recContact;
  private NSDictionary annuaireContactInfo;
  public NSMutableArray allContactsItems;
  public ContactsListItem contactItem;
  public ContactsListItem contactItemSelected;
  private SelectContactListener listener;
  // Les elements utilises dans l'edition de contact
  public String ctEtablissement;
  //
  public NSArray ctServiceList;
  public CktlRecord ctServiceItem;
  public CktlRecord ctServiceSelected;
  public boolean chTousServices;
  //
  public NSArray ctPoleList;
  public CktlRecord ctPoleItem;
  public CktlRecord ctPoleSelected;
  public String ctPoleLibelle;
  //
  public NSArray ctBatimentList;
  public CktlRecord ctBatimentItem;
  public CktlRecord ctBatimentSelected;
  public String ctBatimentLibelle;
  public boolean chTousBatiments;
  //
  public NSArray ctBureauList;
  public CktlRecord ctBureauItem;
  public CktlRecord ctBureauSelected;
  public String ctBureauLibelle;
  //
  public String ctTelephone;
  public String ctEmail;
  //
  private boolean prevChServices;
  private boolean prevChBatiments;
  
  // Les erreurs
  public boolean errorEtab, errorService, errorBatiment, errorTelephone;
  public String errorMessage;

  // gestion des poles geographique O/N
  public boolean appShowContactPole;
  
  /**
   * Cree une nouvelle instance de composant.
   */
  public SelectContact(WOContext context) {
    super(context);
    allContactsItems = new NSMutableArray();
    prevChBatiments = prevChServices = chTousBatiments = chTousServices = false; 
    String strAppShowContactPole = dtApp().config().stringForKey("APP_SHOW_CONTACT_POLE");
    appShowContactPole = !StringCtrl.isEmpty(strAppShowContactPole) && strAppShowContactPole.equals("YES");
  }

  public void setListener(SelectContactListener newListener) {
    listener = newListener;
  }
  
  /**
   * Retourne la reference vers le bus de la gestion de donnees sur les
   * contacts.
   */
  private DTContactsBus cBus() {
    return dtSession().dataCenter().contactsBus();
  }

  private DTDataCenter dataCenter() {
    return dtSession().dataCenter();
  }
  
  private void clearErrors() {
    errorEtab = errorService = errorBatiment = errorTelephone = false;
    errorMessage = null;
  }
  
  public boolean hasErrors() {
    return (errorEtab || errorService || errorBatiment ||
            errorTelephone || hasGeneralError());
  }
  
  public boolean hasGeneralError() {
    return (errorMessage != null);
  }

  /**
   * Retourne l'enregistrement qui correspond a celui selectionne par
   * l'utilisateur.
   */
  public CktlRecord selectedRecord() {
    if (contactItemSelected != null)
      return contactItemSelected.record();
    else
      return null;
  }
  
  /**
   * Retourne l'enregistrement du contact en cours. Il est recherche dans
   * l'ordre suivant :
   * <ul>
   *   <li>le contact selectionne,</li>
   *   <li>sinon, le contact donne a l'ouverture de la fenetre,</li>
   *   <li>sinon, le contact par defaut</li>
   * </ul> 
   * 
   * La methode retourne la valeur <i>null</i> si aucun contact ne peut
   * etre determine. 
   */
  private CktlRecord currentRecord() {
    if (contactItemSelected != null)
      return contactItemSelected.record();
    else if (recContact != null)
      return recContact;
    else
      return recDefaultContact;
  }
  
  /**
   * Retourne les informations sur le contact en cours de l'utilisateur sous
   * forme d'un dictionnaire. Les informations peuvent etre accedees via la
   * methode <code>valueForKey</code>. La structure du dictionnaire correspond
   * a celle de l'entite <i>Contact</i>.
   */
  private NSKeyValueCoding currentContactInfo() {
    // On essaie d'abord les contacts enregistres/selectionnes
    NSKeyValueCoding infoContact = currentRecord();
    // S'il n'y a aucun contact, alors on essaye les infos directement
    // a partir de l'annuaire.
    if (infoContact == null) {
      // On met dans le cache, si c'est pas encore memorise
      if (annuaireContactInfo == null)
        annuaireContactInfo = cBus().getAnnuaireContactInfo(contactUserInfo.noIndividu());
      infoContact = annuaireContactInfo;
    }
    return infoContact;
  }
  
  public boolean isListContactSelected() {
    CktlLog.trace("contactItemSelected : "+contactItemSelected);
    CktlLog.trace("contactItem : "+contactItem);
    return ((contactItem == contactItemSelected) && (contactItemSelected != null));
  }
  
  public String titreEdition() {
    if (editStatus == EDIT_STATUS_ADD)
      return "Ajout d'un nouveau contact";
    else if (editStatus == EDIT_STATUS_UPDATE)
      return "Mise &agrave; jour d'un contact";
    else
      return "Contact s&eacute;lectionn&eacute;";
  }
  
  public String ctServiceLibelle() {
    if (ctServiceItem == null) {
      return null;
    } else {
      StringBuffer sb = new StringBuffer();
      String libelle = ctServiceItem.stringNormalizedForKey("llStructure");
      if (libelle.length() > 50)
        sb.append(libelle.substring(0, 50)).append("...");
      else
        sb.append(libelle);
      sb.append(" (").append(ctServiceItem.valueForKey("lcStructurePere")).append(")");
      return sb.toString();
    }
  }
  
  public String ctEtabLibelle() {
    if (ctEtablissement.length() == 0)
      return null;
    else
      return ctEtablissement;
  }
  
  public String ctBureauItemLibelle() {
    if (ctBureauItem != null) {
      CktlRecord rec =
        cBus().findBureauForCode(ctBureauItem.numberForKey("salNumero"));
      if (rec != null)
        return cBus().getLibelleForSalle(rec);
    }
    return "<inconnu>";
  }
  
  /**
   * Teste si actuellement un contact est en cours de modification.
   */
  public boolean isEditing() {
    return (editStatus != EDIT_STATUS_NONE);
  }
  
  /**
   * Teste si les informations sont affichees en mode "read-only".
   */
  public boolean isReadOnly() {
    return (dialogMode == DLG_MODE_READ);
  }

  /**
   * Teste si la liste des contacts n'est pas vide.
   */
  public boolean hasContacts() {
    return (allContactsItems.count() > 0);
  }
  
  public boolean hasSelectedContact() {
    return (contactItemSelected != null);
  }
  
  public boolean canShowToolBar() {
    // Peux afficher les boutons 
    return (!isEditing());
  }
  
  public boolean canShowSelect() {
    return canShowToolBar() && !isReadOnly() && hasSelectedContact();
  }
  
  public boolean canShowAddUpdateDelete() {
    return (!isReadOnly() && !isEditing() && hasSelectedContact());
  }

  public boolean isNetLocalEdit() {
    return (contactUserInfo.isNetLocal() && isEditing()); 
  }
  
  public boolean isNetExterneEdit() {
    return (!contactUserInfo.isNetLocal() && isEditing());
  }
  
  /**
   * Remplit les elements d'inteface de consultation des contacts.
   */
  private void refillInterface(boolean reloadItems) {
    remplirEtablissement(reloadItems);
    remplirService(reloadItems);
    if (appShowContactPole) {
      remplirPole(reloadItems);
    }
    remplirBatiment(reloadItems);
    remplirBureau(reloadItems);
    remplirTelephone(reloadItems);
    remplireEmail();
  }

  /**
   * Remplit la liste des etablissement de l'utilisateur. Si l'utilisateur
   * appartient a l'etablissement en cours, cette liste ne contient qu'une
   * seul entr�e, non modifiable. Pour les personnes externes, l'etablissement
   * peut etre selectionne ou un nouvel etablissement ajoute.
   */
  private void remplirEtablissement(boolean reloadItems) {
    if (contactUserInfo.isNetLocal()) {
      // Si l'utilisateur est local, alors sont etablissement reste
      // toujours le meme
      ctEtablissement = 
        StringCtrl.normalize(dataCenter().serviceBus().nomEtablissement());
    } else if (!isEditing()) {
      // Si en lecture seule, on n'affiche que la valeur 
      ctEtablissement = StringCtrl.emptyString();
      // Si la valeur est connue...
      if ((currentContactInfo() != null) &&
          (currentContactInfo().valueForKey("ctLibelleEtab") != null))
        ctEtablissement = (String)currentContactInfo().valueForKey("ctLibelleEtab");
    } else {
      // On remplit le contact
      if (selectedRecord() == null) 
        ctEtablissement = StringCtrl.emptyString();
      else
        ctEtablissement = selectedRecord().stringForKey("ctLibelleEtab");
    }
  }

  /**
   * Remplit la liste des services de l'utilisateur. Si la case "Tous" est
   * cochee, alors la liste est remplise par tous les service connus dans
   * l'annuaire. Sinon, la liste est remplit uniquement par les services
   * de l'utilisateur connus dans l'annuaire ou deja utilises dans les
   * contacts. Le service utilise pour la derniere selection de contact
   * est selectionne par defaut. 
   */
  private void remplirService(boolean reloadItems) {
    if (reloadItems) ctServiceList = new NSArray();

    if (!contactUserInfo.isNetLocal()) {
      // Pas de services, si l'utilisateur n'est pas local
      ctServiceItem = null;
    } else if (!isEditing()) {
      // Si on ne modifie pas, alors on va juste recuperer le libelle
      ctServiceItem = null;
      // Si la valeur est connue
      if ((currentContactInfo() != null) &&
          (currentContactInfo().valueForKey("cStructService") != null))
      {
        ctServiceItem = dataCenter().serviceBus().structureForCode(
            (String)currentContactInfo().valueForKey("cStructService"));
      }
    } else {
      // Sinon, on remplit la liste et tout ca kua...
      CktlRecord rec;
      // On re-remplit le combo box s'il le faut
      if ((ctServiceList.count() == 0) || reloadItems) {
        // On initialise la liste des services a afficher
        if (chTousServices) { // Tous les services
          ctServiceList = dataCenter().serviceBus().allServices();
        } else { // Les services associes a l'utilisateur ou deja utilises
          ctServiceList = cBus().findServicesForIndividu(contactUserInfo.noIndividu());
        }
      } // On trouve l'element a selectionner
      rec = selectedRecord();
      ctServiceSelected = null;
      if (rec != null) {
        for (int i=0; i < ctServiceList.count(); i++) {
          ctServiceItem = (CktlRecord)ctServiceList.objectAtIndex(i);
          if (ctServiceItem.valueForKey("cStructure").equals(
                rec.valueForKey("cStructService")))
            ctServiceSelected = ctServiceItem;
        }
      }
//      lbService.setStringValue("Service ["+cmbService.numberOfItems()+"] :");
    }
  }
  
  /**
   * Remplit la liste des poles geographiques. Selectionne le pole en cours.
   * La liste est vide, s'il s'agit d'une personne "externe".
   */
  private void remplirPole(boolean reloadItems) {
    if (reloadItems) ctPoleList = new NSArray();
    ctPoleList = cBus().findAllImplantationGeos();
  }
  
  /**
   * Remplit la liste des batiments. Selectionne le batiment en cours.
   * La liste est vide, s'il s'agit d'une personne "externe".
   */
  private void remplirBatiment(boolean reloadItems) {
    CktlRecord rec;
    if (reloadItems) ctBatimentList = new NSArray();
    
    if (!contactUserInfo.isNetLocal()) {
      // Pas de batiment, si l'utilisateur n'est pas local
      ctBatimentLibelle = null;
    } else if (!isEditing()) {
      // Si pas en edition, on n'affiche que le titre du batiment
      ctBatimentLibelle = null;
      // Si la valeur est connue
      if ((currentContactInfo() != null) &&
          (currentContactInfo().valueForKey("cLocal") != null))
      {
        rec = cBus().findBatimentForCode((String)currentContactInfo().valueForKey("cLocal"));
        if (rec != null) {
        	ctBatimentLibelle = rec.stringForKey("appellation");
      		NSArray recsImpGeo = rec.arrayForKey("tosImplantationGeo");
       		if (!NSArrayCtrl.isEmpty(recsImpGeo)) {
      			CktlRecord lastPole = (CktlRecord) recsImpGeo.lastObject();
          	ctPoleLibelle = lastPole.stringForKey("llImplantationGeo");
       		}
        }
      }
    } else {
      // Sinon, on remplit le popUp
      String selectedLocalCode;
      // On re-remplit la liste s'il le faut
      if ((ctBatimentList.count() == 0) || reloadItems) {
        // On initialise la liste des batimets a afficher
        if (chTousBatiments) { // Tous les batiments
        	if (appShowContactPole) {
        		if (ctPoleSelected != null) {
        			ctBatimentList = ctPoleSelected.arrayForKey("tosBatiment");
        		}
        	} else {
          	ctBatimentList = cBus().findAllBatiments();
        	}
        } else { // Les batiments associes a l'utilisateur ou deja utilises
          ctBatimentList = cBus().findBatimentsForIndividu(contactUserInfo.noIndividu());
        }
      }
 
      // On retrouve le code du batiment a selectionner par defaut
      if ((selectedRecord() != null) && (selectedRecord().valueForKey("cLocal") != null))
        selectedLocalCode = selectedRecord().stringForKey("cLocal");
      else
        selectedLocalCode = cBus().getDefaultLocalForIndividu(contactUserInfo.noIndividu());
      if (ctBatimentList.count() > 0) {
        // Ensuite, on le retrouve, s'il existe
        for (int i=0; i < ctBatimentList.count(); i++) {
          ctBatimentItem = (CktlRecord)ctBatimentList.objectAtIndex(i);
          if (ctBatimentItem.valueForKey("cLocal").equals(selectedLocalCode))
            ctBatimentSelected = ctBatimentItem;
        }
        // Sinon, on selection la premiere entre
        if (ctBatimentSelected == null)
          ctBatimentSelected = (CktlRecord)ctBatimentList.objectAtIndex(0);
        
      }
      
      // filtrage par rapport pole selectionne si l'appli est parametree comme ca
      if (appShowContactPole) {
      	// selection du pole associe au batiement
      	if (ctBatimentSelected != null) {
      		NSArray recsImpGeo = ctBatimentSelected.arrayForKey("tosImplantationGeo");
       		if (!NSArrayCtrl.isEmpty(recsImpGeo)) {
      			ctPoleSelected = (CktlRecord) recsImpGeo.lastObject();
      		}
      	}
      }
 
//      lbBatiment.setStringValue("B�timent ["+(cmbBatiment.numberOfItems()-1)+"]:");
    }
    //CktlLog.trace("selected batiment : "+ctBatimentSelected);
    //CktlLog.trace("batimentLibelle : "+ctBatimentLibelle);
  }

  /**
   * Remplit la liste des bureaux de l'utilisateur. La liste correspond au
   * batiment selectionne.
   * 
   * <p>Si le batiment selectionne corresponde a celui associe a l'utilisateur,
   * alors le bureau associe sera selectionne. Sinon, on selectionne le premier
   * bureau de la liste.</p>  
   */
  private void remplirBureau(boolean reloadItems) {
    CktlRecord rec;
    if (reloadItems) ctBureauList = new NSArray();
    if (!contactUserInfo.isNetLocal()) {
      // Pas de bureau, si l'utilisateur n'est pas local
      ctBureauLibelle = null;
    } else if (!isEditing()) {
      // Si en lecture seule, on n'affiche que le numero du bureau
      ctBureauLibelle = null;
      // Si le bureau peut etre trouve
      if ((currentContactInfo() != null) &&
          (currentContactInfo().valueForKey("salNumero") != null))
      {
        rec = cBus().findBureauForCode((Number)currentContactInfo().valueForKey("salNumero"));
        if (rec != null)
          ctBureauLibelle = cBus().getLibelleForSalle(rec);
      }
    } else {
      Number selectedBureauCode = null;
      // On retrouve le code du bureau a selectionner par defaut
      if (selectedRecord() != null)
        selectedBureauCode = selectedRecord().numberForKey("salNumero");
      if (ctBatimentSelected != null)
        selectedBureauCode =
          cBus().getValideBureauForIndividu(contactUserInfo.noIndividu(),
              ctBatimentSelected.stringForKey("cLocal"), selectedBureauCode);
      // On remplit la liste des bureaux, s'il le faut
      if ((ctBatimentList.count() > 0) &&
          ((ctBureauList.count() == 0) || reloadItems)) {
        // On rempli la liste des bureaux de ce batiment
        if (ctBatimentSelected == null)
          ctBureauList = new NSArray();
        else
          ctBureauList =
            CktlSort.sortedArray(
              cBus().findBureauxForBatiment(ctBatimentSelected.stringForKey("cLocal")),
              "salEtage, salPorte");
      }
      //
      if (ctBureauList.count() > 0) {
        // On retrouve l'index de l'element a selectionner
        if (selectedBureauCode != null) {
          for (int i=0; i < ctBureauList.count(); i++) {
            ctBureauItem = (CktlRecord)ctBureauList.objectAtIndex(i);
            if (ctBureauItem.intForKey("salNumero") == selectedBureauCode.intValue())
              ctBureauSelected = ctBureauItem;
          }
        }
        // S'il n'existe pas, on selectionne le premier element
        if (ctBureauSelected == null)
          ctBureauSelected = (CktlRecord)ctBureauList.objectAtIndex(0);
        
      }
    }
    CktlLog.trace("ctBureauSelected : "+ctBureauSelected);
  }

  /**
   * Remplit la liste des telephones de la personne. Les infos sont
   * recuperees a partir de la table <i>PersonneTelephone</i>.
   */
  private void remplirTelephone(boolean reloadItems) {
    // Si en lecture seule, on n'affiche que le numero du bureau
    ctTelephone = StringCtrl.emptyString();
    if (dialogMode == DLG_MODE_READ) {
      // Si le telephone est connu
      if ((currentContactInfo() != null) &&
          (currentContactInfo().valueForKey("ctNoTelephone") != null))
        ctTelephone =
          StringCtrl.normalize((String)currentContactInfo().valueForKey("ctNoTelephone"));
    } else {
      // On retrouve le numero de telephone
      if (selectedRecord() != null)
        ctTelephone = selectedRecord().stringNormalizedForKey("ctNoTelephone");
      if (ctTelephone.length() == 0)
        ctTelephone = cBus().getDefaultTelephoneForIndividu(contactUserInfo.noIndividu());
    }
  }

  /**
   * Remplire le champ "email". Si l'adresse email n'est pas donnee dans la
   * definition de contact selectionne, alors le email de l'utilisateur
   * connecte sera utilisee comme l'adresse.
   */
  private void remplireEmail() {
    ctEmail = null;
    if (currentContactInfo() != null)
      ctEmail = (String)currentContactInfo().valueForKey("ctEmail");
    if (ctEmail == null)
      ctEmail = contactUserInfo.email();
  }

  /**
   * Recharge la liste des contacts. Recupere la reference vers le contact
   * par defaut de l'individu.
   */
  private void reloadContacts(Number noIndividu, Number ctOrdre) {
    CktlRecord rec;
    recContact = cBus().findContact(ctOrdre);
    recDefaultContact = cBus().findDefaultContact(noIndividu, false);
    // Charger les contacts a partir de la base de donnees
    NSArray allContacts = CktlSort.sortedArray(
        cBus().findAllContacts(noIndividu), "dModification", CktlSort.Descending);
    // Reinitialiser la liste des entres de la liste
    allContactsItems.removeAllObjects();
    contactItemSelected = null;
    for(int i=0; i<allContacts.count(); i++) {
      rec = (CktlRecord)allContacts.objectAtIndex(i);
      contactItem = new ContactsListItem(rec, dtSession());
      allContactsItems.addObject(contactItem);
      if ((ctOrdre != null) && (rec.intForKey("ctOrdre") == ctOrdre.intValue()))
        contactItemSelected = contactItem;
    }
    if ((contactItemSelected == null) && (allContactsItems.count() > 0))
      contactItemSelected = (ContactsListItem)allContactsItems.objectAtIndex(0);
//    if ((recContact == null) && (contactItemSelected != null))
//      recContact = contactItemSelected.record();
    CktlLog.trace("contactItemSelected : "+contactItemSelected);
  }
  
  /**
   * Initialise les donnees du composant. Remplit les listes, definit
   * les selections.
   */
  public void setData(Number noIndividu, Number ctOrdre) {
    clearErrors();
    // On recupere les enregistrements concernes
    annuaireContactInfo = null;
    // Object pour pouvoir rappatrier les infos sur l'individu
    contactUserInfo = new DTUserInfo(cBus());
    // On recupere les infos sur l'individu et son compte
    contactUserInfo.compteForPersId(
        dtSession().dataCenter().individuBus().persIdForNoIndividu(noIndividu), true);
    // Recharger les donnes dans la liste des contacts
    reloadContacts(noIndividu, ctOrdre);
    // On initialisae les elements d'interface HTML
    refillInterface(true);
  }

  /**
   * Test la validite des donnees saisies. Affiche les message d'erreurs si
   * necessaire. Retourne <i>true</i> si les donnees sont saisies correctement.
   */
  public void verifieData() {
    // Si la personne est externe : etablissement obligatoire
    if (contactUserInfo.isNetExterne()) {
      if (StringCtrl.normalize(ctEtablissement).length() == 0)
        errorEtab = true;
    }
    // Le service doit etre indique pour les profs et l'administration
    if (contactUserInfo.isNetRecherche() || contactUserInfo.isNetAdmin()) {
      if (ctServiceSelected == null)
        errorService = true;
    }
    // Le batiment est necessaire pour tous les memebres de l'etablissement
    if (contactUserInfo.isNetLocal()) {
      if (ctBatimentSelected == null)
        errorBatiment = false;
    }
    // Le telephone est obligatoire pour prof, administration et les externes
    if (contactUserInfo.isNetAdmin() || contactUserInfo.isNetRecherche() || contactUserInfo.isNetExterne()) {
      if (StringCtrl.normalize(ctTelephone).length() == 0)
        errorTelephone = true;
    }
    // Les bureau  - uniqiement pour les profs et le personnel
    // et c'est pas obligatoire... Alors on ne teste rien.
//    if (contactUserInfo.isNetRecherche() || contactUserInfo.isNetAdmin()) {
//      listItem = (ContactsListItem)cmbBureau.objectValueOfSelectedItem();
//      if ((listItem == null) || (listItem.type() == ContactsListItem.ITEM_EMPTY)) {
//        if (LRGUI.askMessage("Le num�ro de bureau devrait �tre indiqu�,\n" +
//            "mais vous ne l'avez pas s�lectionn� !\n\n" +
//            "Souhaitez vous quand m�me enregistrer\n" +
//            "les informations incompl�tes ?",
//            "Enregistrer", "Annuler") == LRAlertPanel.BUTTON2)
//        return false;
//      }
//    }
    // On reformat le numero de telephone, s'il le faut
    if (StringCtrl.normalize(ctTelephone).length() > 0)
      ctTelephone = StringCtrl.formatPhoneNumber(StringCtrl.normalize(ctTelephone));
  }
  
  /**
   * Enregistre les informations actuellement selectionnees comme une nouvelle
   * definition de contact. Lors de l'appel de cette methode, on suppose que
   * la validite de donnees est deja verifiee (methode <code>verifieData</code>).
   * 
   * <p>Retourne le code de nouvel contact enregistre.</p>
   * 
   * @see #verifieData()
   */
  public Number saveData() {
    NSMutableDictionary dico = new NSMutableDictionary();
    // On remplit le dico des donnees
    // noIndividu
    dico.takeValueForKey(contactUserInfo.noIndividu(), "noIndividu");
    // ctDefault -> null -> "N"
//    // ctEmail
//    dico.takeValueForKey(contactUserInfo.email(), "ctEmail");
    // ctLibelleEtab
    dico.takeValueForKey(ctEtablissement, "ctLibelleEtab");
    // Les infos suivant sont connues si l'utilisateur est local
    if (contactUserInfo.isNetLocal()) {
      // cStructEtab
      dico.takeValueForKey(
          dtSession().dataCenter().serviceBus().structureEtablissement().valueForKey("cStructure"), "cStructEtab");
      // cStructService
      takeValueFromItemRecord(dico, ctServiceSelected, "cStructure", "cStructService", true);
      // cLocal
      takeValueFromItemRecord(dico, ctBatimentSelected, "cLocal", "cLocal", true);
      // salNumero
      takeValueFromItemRecord(dico, ctBureauSelected, "salNumero", "salNumero", true);
    }
    // ctNoTelephone -> on suppose que le numero est deja formate
    if (StringCtrl.normalize(ctTelephone).length() == 0)
      dico.takeValueForKey(DTDataBus.nullValue(), "ctNoTelephone");
    else
      dico.takeValueForKey(ctTelephone, "ctNoTelephone");
    // email
    CktlLog.trace("ctEmail : "+ctEmail);
    if (StringCtrl.normalize(ctEmail).length() == 0)
      dico.takeValueForKey(DTDataBus.nullValue(), "ctEmail");
    else
      dico.takeValueForKey(ctEmail, "ctEmail");
    // persId
    dico.takeValueForKey(contactUserInfo.persId(), "persId");
    // adrOrdre -> null pour cette version
    // ctUserLocal
    dico.takeValueForKey(new Boolean(contactUserInfo.isNetLocal()), "ctUserLocal");
    CktlLog.trace("Contact dico :\n  "+dico);
    if (editStatus == EDIT_STATUS_ADD) {
      return cBus().addContact(null, dico);
    } else {
      cBus().updateContact(null, selectedRecord().numberForKey("ctOrdre"), dico);
      return selectedRecord().numberForKey("ctOrdre");
    }
  }
  
  /**
   * Enregistre dans <code>dico</code> la valeur de l'attribut
   * <code>itemAttrib</code> de l'enregistrement associe a l'entre
   * <code>item</code>. La valeur est enregistree sous le nom
   * <code>dbAttrib</code>.
   * 
   * <p>Si la valeur de l'attribut est <i>null</i> et le parametre
   * <code>ignoreIfNull</code> est <i>false</i>, alors la valeur n'est pas
   * enregistree. Sinon, <code>EONullValue</code> est enregistree.</p>
   */
   private void takeValueFromItemRecord(NSMutableDictionary dico,
                                        CktlRecord rec, String itemAttrib,
                                        String dbAttrib, boolean ignoreIfNull)
   {
     Object value = null; 
     if (rec != null) value = rec.valueForKey(itemAttrib);
     if (!ignoreIfNull) value = DTDataBus.valueIfNull(value);
     if (value != null)
       dico.takeValueForKey(value, dbAttrib);
   }

  public WOComponent doFormChangeSubmit() {
    CktlLog.trace(null);
    clearErrors();
    if (prevChBatiments != chTousBatiments) {
      // Click sur le checkBox "Tous les batiments"
      remplirBatiment(true);
      remplirBureau(true);
      prevChBatiments = chTousBatiments;
    } else if (prevChServices != chTousServices) {
      // Click sur le checkBox "Tous les services"
      remplirService(true);
      prevChServices = chTousServices;
    } else {
      // Click dans la liste des batiments
      remplirBureau(true);
    }
    return null;
  }
  
  public WOComponent doAdd() {
    if (!isReadOnly()) {
      editStatus = EDIT_STATUS_ADD;
      refillInterface(true);
    }
    return null;
  }
  
  public WOComponent doUpdate() {
    if (!isReadOnly()) {
      editStatus = EDIT_STATUS_UPDATE;
      refillInterface(true);
    }
    return null;
  }
  
  public WOComponent doDelete() {
    // L'utilisateur doit avoir au moins un contact
    if (allContactsItems.count() == 1) {
      errorMessage = "Le dernier contact ne peut pas &ecirc;tre supprim&eacute;.";
      return null;
    }
    // Test si le contact n'est pas utilise.
    String condition = "ctOrdre="+selectedRecord().valueForKey("ctOrdre");
    if (dtSession().dataCenter().interventionBus().findInterventions(
        null, DTDataBus.newCondition(condition)).count() > 0)
    {
      errorMessage = "Ce contact est utilis&eacute; dans certains demandes<br>"+
          "et il ne peut pas &ecirc;tre supprim&eacute;.";
      return null;
    }
    // Demande d'abord
    return CktlAlertPage.newAlertPageWithResponder(this, "Suppression d'un contact",
        "Voulez-vous vraiment supprimer ce contact ?<br>"+
        cBus().getContactDescription(selectedRecord().numberForKey("ctOrdre"), "<br>"),
        "Oui", "Non", null, CktlAlertPage.QUESTION, new DeleteResponder());
  }

  public WOComponent doEditOK() {
    clearErrors();
    verifieData();
    if (!hasErrors()) {
      Number ctOrdre = saveData();
      if (ctOrdre != null) {
        editStatus = EDIT_STATUS_NONE;
        reloadContacts(contactUserInfo.noIndividu(), ctOrdre);
        refillInterface(true);
      }
    }
    return null;
  }
  
  public WOComponent doEditCancel() {
    editStatus = EDIT_STATUS_NONE;
    clearErrors();
    refillInterface(false);
    return null;
  }
  
  public WOComponent doSelectListContact() {
    if (contactItem != null)
      contactItemSelected = contactItem;
    else
      contactItemSelected = null;
    editStatus = EDIT_STATUS_NONE;
    refillInterface(false);
    return null;
  }
  
  public WOComponent doSelect() {
    if ((listener != null) && (selectedRecord() != null))
      return listener.select(selectedRecord().numberForKey("ctOrdre"));
    else
      return null;
  }
  
  public WOComponent doCancel() {
    if (listener != null)
      return listener.cancel();
    else
      return null;
  }
  
  private class DeleteResponder implements CktlAlertResponder {
    /*
     * @see CktlAlertResponder#respondToButton(int)
     */
    public WOComponent respondToButton(int button) {
      if (button == 1) {
        if (!cBus().deleteContact(null, selectedRecord().numberForKey("ctOrdre"))) {
          errorMessage = "Une erreur est survenue lors de la suppression du contact.";
          if (cBus().hasError())
            errorMessage += "<br>"+cBus().getErrorMessage();
        }
        reloadContacts(contactUserInfo.noIndividu(), null);
        refillInterface(true);
      }
      // Pour ne pas avoir des problemes de backrack
      SelectContact.this.ensureAwakeInContext(context());
      return SelectContact.this;
    }
  }
  
  // synchro des contacts avec l'annuaire
  
  /**
   * Permet a l'utilisateur de faire la synchronisation dans le sens
   * annuaire > contact. L'ancien contact est archive, et un nouveau
   * est cree a partir de l'ancien, avec les champs mis a jour.
   * @return
   */
  public WOComponent createSynchronizedContact() {
  	/*// dictionnaire du differentiel
  	NSDictionary dicoDiff = cBus().getDicoDiffContactAnnuaire(
  			contactItemSelected.numberForKey("noIndividu"), contactItemSelected.numberForKey("ctOrdre"));
  	// raz du message d'erreur
  	cBus().setErrorMessage(null);
    if (cBus().shouldDuplicate(dicoDiff)) {
      // duplication
    	Number newCtOrdre = cBus().addContact(null, contactItemSelected);
    	// maj avec le differentiel
    	cBus().updateContact(null, newCtOrdre, dicoDiff);
    	// selection du nouveau contact
    	setContactDemandeur(cBus().findContact(newCtOrdre));
    } else if (cBus().shouldUpdate(dicoDiff)) {
    	// on fait une simple mise a jour si seulement le mail est different
    	cBus().updateContact(null, contactItemSelected.numberForKey("ctOrdre"), dicoDiff);
    	// selection du contact mis a jour
    	setContactDemandeur(cBus().findContact(contactItemSelected.numberForKey("ctOrdre")));
    }*/
  	return null;
  }
  
  // setter
  
  /**
   * Interception de la selection du pole, afin de refetcher 
   * la liste des batiments associes
   */
  public void setCtPoleSelected(CktlRecord value) {
  	ctPoleSelected = value;
  	if (ctPoleSelected != null) {
  		NSArray recsBat = ctPoleSelected.arrayForKey("tosBatiment");
  		ctBatimentList = recsBat;
  	}
  }
}