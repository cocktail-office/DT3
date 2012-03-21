import com.webobjects.appserver.WOContext;

/**
 * Page rassemblant l'ensemble des outils de l'application DT
 * 
 * @author Cyril TARADE <cyril.tarade at univ-lr.fr>
 */
public class PageOutils extends DTWebComponent {

	// gestion de la selection des onglets
  private int selectedOnglet = ONGLET_OUTILS_NONE;
  public final static int ONGLET_OUTILS_NONE            	= -1;
  public final static int ONGLET_OUTILS_DTFAST       			=	0;
  public final static int ONGLET_OUTILS_VENTILATION     	= 1;
 
  public final static String ONGLET_OUTILS_TITLE_DTFAST        		= "Enregistrement rapide de DT";
  public final static String ONGLET_OUTILS_TITLE_VENTILATION     = "Reaffectation globale des DT";
  
  public PageOutils(WOContext context) {
    super(context);
  }
  
  public void setSelectedOnglet(int id) {
    selectedOnglet = id;
  }
  
  public int getSelectedOnglet() {
    return selectedOnglet;
  }
      
  // quel swap afficher
  public boolean showSwapDTFast()        		{   return selectedOnglet == ONGLET_OUTILS_DTFAST;}
  public boolean showSwapVentilation()     	{   return selectedOnglet == ONGLET_OUTILS_VENTILATION;}
 
  // titre du swap en cours
  public String swapTitle() {
    String title = "";
    if (showSwapDTFast()) {
      title = ONGLET_OUTILS_TITLE_DTFAST;
    } else if (showSwapVentilation()) {
      title = ONGLET_OUTILS_TITLE_VENTILATION;
    } 
    return title;
  }

}