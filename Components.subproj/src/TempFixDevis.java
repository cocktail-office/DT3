// Generated by the WOLips TemplateEngine Plug-in at 29 mars 2005 10:40:02

import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebPage;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

/**
 * Ce composant a ete ajoute afin de permettre la recreation des devis "perdus"
 * a partir des demandes de la reprographie.
 * 
 * <p>Cette fonctionnalite n'etant plus necessaire, le developpement de ce
 * composant est stoppe.</p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class TempFixDevis extends CktlWebPage {
  private EOEditingContext econtext = new EOEditingContext();
  /** @TypeInfo BadDTDescription */
  public NSMutableArray listeDT;
  public BadDTDescription uneDT;
  public int uneDTindex;
  private DTPrestaBusWeb pieBus;
  private DTIndividuBus individuBus;
  private DTContactsBus contactBus;
  private DTServiceBus serviceBus;
  private DTJefyBus jefyBus;
  
  public TempFixDevis(WOContext context) {
    super(context);
    listeDT = new NSMutableArray();
  }
  
  private Application dtApp() {
    return (Application)WOApplication.application();
  }
  
  private DTIndividuBus individuBus() {
    if (individuBus == null)
      individuBus = new DTIndividuBus(econtext);
    return individuBus;
  }

  private DTContactsBus contactBus() {
    if (contactBus == null)
      contactBus = new DTContactsBus(econtext);
    return contactBus;
  }

  private DTServiceBus serviceBus() {
    if (serviceBus == null)
      serviceBus = new DTServiceBus(econtext);
    return serviceBus;
  }
  
  private DTJefyBus jefyBus() {
    if (jefyBus == null)
    	jefyBus = new DTJefyBus(econtext);
    return jefyBus;
  }

  /**
   * Initialise la liste de toutes les demandes "suspectes".
   */
  private void fillBadDTListe() {
    NSArray objects, devListe, keys;
    NSDictionary dico;
    // On initialise la liste des DT, pour lesquelles les devis n'existent plus
    keys = new NSArray(new Object[]{
    	"intOrdre", "devOrdre", "intDateCreation", "intCleService", "ctOrdre", "intNoIndConcerne"});
    objects = 
      DTCktlEOUtilities.rawRowsForSQL(econtext, dtApp().mainModelName(),
        "select infin.int_ordre, infin.dev_ordre," +
        "  itv.INT_DATE_CREATION, itv.int_cle_service, itv.ct_ordre, itv.INT_NO_IND_CONCERNE "+
        "from intervention_infin infin, intervention itv "+
        "where "+
        "(itv.int_ordre = infin.int_ordre) and "+
        "(infin.dev_ordre is not null) and "+
        "not exists (select pdv.DEV_ORDRE from prestation.devis pdv where pdv.dev_ordre = infin.dev_ordre)",
        keys);
    listeDT.removeAllObjects();
    for(int i=0; i<objects.count(); i++) {
      dico = (NSDictionary)objects.objectAtIndex(i);
      CktlLog.trace("object to add : "+dico);
      listeDT.addObject(getNewBadListeEntry(dico, "devis inexistant"));
    }
    // Plus la liste des devis avec le numero double
    devListe = DTCktlEOUtilities.rawRowsForSQL(econtext, dtApp().mainModelName(),
        "select dev_ordre from intervention_infin infin "+
        "where dev_ordre is not null "+
        "group by dev_ordre "+
        "having count(*) > 1",
        new NSArray("devOrdre"));
    for(int i=0; i<devListe.count(); i++) {
      dico = (NSDictionary)devListe.objectAtIndex(i);
      objects = 
        DTCktlEOUtilities.rawRowsForSQL(econtext, dtApp().mainModelName(),
          "select infin.int_ordre, infin.dev_ordre," +
          "  itv.INT_DATE_CREATION, itv.int_cle_service, itv.ct_ordre, itv.INT_NO_IND_CONCERNE "+
          "from intervention_infin infin, intervention itv "+
          "where "+
          "(itv.int_ordre = infin.int_ordre) and "+
          "(infin.dev_ordre = "+dico.valueForKey("devOrdre")+")",
          keys);
      for(int j=0; j<objects.count(); j++) {
        dico = (NSDictionary)objects.objectAtIndex(j);
        CktlLog.trace("object to add : "+dico);
        listeDT.addObject(getNewBadListeEntry(dico, "devis double"));
      }
    }
  }
  
  private BadDTDescription getNewBadListeEntry(NSDictionary dico, String etat) {
    BadDTDescription uneDesc = new BadDTDescription(
        (Number)dico.valueForKey("intOrdre"),
        (Number)dico.valueForKey("intCleService"),
        (Number)dico.valueForKey("devOrdre"),
        (NSTimestamp)dico.valueForKey("intDateCreation"),
        (Number)dico.valueForKey("ctOrdre"),
        (Number)dico.valueForKey("intNoIndConcerne")
      );
    uneDesc.etat = etat;
    return uneDesc;
  }
  
  public WOComponent reinit() {
    fillBadDTListe();
    return null;
  }
  
  protected DTPrestaBusWeb pieBus() {
    if (pieBus == null) pieBus = new DTPrestaBusWeb(null, null, true);
    return pieBus;
  }

  private String initDTpieBus() {
    CktlRecord rec;
    Number ctOrdre = uneDT.ctOrdre;
    if (ctOrdre == null) return "no contact";
    Integer persId = (Integer)individuBus().persIdForNoIndividu(uneDT.intNoIndConcerne);
    rec = contactBus().findContact(ctOrdre);
    if (rec == null) return "no contact rec";
    String cStructure = rec.stringForKey("cStructService");
    if (cStructure == null) return "no structure";
    rec = serviceBus().structureForCode(cStructure);
    if (rec == null) return "no structure rec";
    Integer servicePersId = (Integer)rec.valueForKey("persId");
    CktlLog.trace("persId : "+persId+", servicePersId : "+servicePersId);
    Integer fouOrdreServiceDemandeur = jefyBus().getFouOrdreForStructure(cStructure);
    pieBus().setUser(persId, fouOrdreServiceDemandeur);
    return null;
  }
  
  /**
   * Teste l'etat du devis pour la demande selectionnee.
   */
  public WOComponent checkDevis() {
    String etat = initDTpieBus(); 
    if (etat == null) {
      CktlLog.trace("devOrdreClass : "+uneDT.devOrdre.getClass().getName());
      etat = pieBus().etatForDevis(uneDT.devOrdre);
      if (!pieBus().hasError())
        etat = pieBus().allDevisEtats().get(etat)+" ["+etat+"]";
      else
        etat = "erreur WS-PIE";
    }
    if (etat != null) uneDT.etat = etat;
    return null;
  }
  
  public WOComponent createDevis() {
    // Cette methode doit : 
    //   1. a partir des infos dans intervention_repro creer un devis (le meme
    //      principe que lors de la creation d'une nouvelle DT;
    //   2. memoriser le devOrdre du nouveau devis dans la table intervention_infin
    //      (passer par la DTInterventionBus).
    return null;
  }
  
  /**
   * Permet de regroupper une description d'une demande avec un devis "suspect".
   * 
   * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
   */
  public class BadDTDescription {
    public Number intOrdre;
    public Number intCleService;
    public Integer devOrdre;
    public NSTimestamp dateCreation;
    public Number ctOrdre;
    public Number intNoIndConcerne;
    public String etat;
    
    public BadDTDescription(Number intOrdre, Number intCleService,
                            Number devOrdre, NSTimestamp dateCreation,
                            Number ctOrdre, Number intNoIndConcerne)
    {
      this.intOrdre = intOrdre;
      this.intCleService = intCleService;
      this.devOrdre = new Integer(devOrdre.intValue());
      this.dateCreation = dateCreation;
      this.ctOrdre = ctOrdre;
      this.intNoIndConcerne = intNoIndConcerne; 
      this.etat = "inconnu";
    }
  }
}