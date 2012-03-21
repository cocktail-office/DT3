import com.webobjects.appserver.WOContext;

/**
 * Le bas de page d'un onglet de création de demande de travaux. Il regroupe les
 * fonctionnalités communes : date de réalisation souhaitée, destination
 * associée, document attaché ...
 * 
 * @author ctarade
 */
public class FooterSwap
		extends DTWebComponent {

	/**
	 * Le controleur de cette page
	 */
	public A_FooterSwapCtrl ctrl;

	public FooterSwap(WOContext context) {
		super(context);
	}

}