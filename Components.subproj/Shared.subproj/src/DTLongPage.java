


import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.woextensions.WOLongResponsePage;

/**
	This class and the associated component creates a simple long request page.
	This page takes the information from the main page and performs the calculation
	of the prime numbers.  Note that for a long request a developer must inherit
	from the WOLongResponse page and, in this case, implement methods for the 
	long task, the refresh rate, and the task to perform when the long request is
	complete.
 */

/**
 * Page d'attente pour un traitement long
 *  
 * @author Cyril Tarade <cyril.tarade at univ-lr.fr>
 */
public class DTLongPage extends WOLongResponsePage {
	
	private static final double REFRESH_INTERVAL = 2.0;

	// reference vers le composant final a afficher
	private IDTLongPageCaller component;
	
	/**
	 * Constructor - initializes the instance variables to default
	 * values, which will be overridden by the passed in arguments.  This
	 * is done to ensure that if any are not set by the arguments the example
	 * can continue.
	 */
	public DTLongPage(WOContext context) {
		super(context);
		setRefreshInterval(REFRESH_INTERVAL);
		setCachingEnabled(false);
	}
	
	/**
	 * Override of appendToResponse - this method increments the count (the total
	 * number of refreshes) and sets the refresh interval to the passed in
	 * value.
	 */
	public void appendToResponse(WOResponse aResponse, WOContext aContext) {
		setRefreshInterval(REFRESH_INTERVAL);
		super.appendToResponse( aResponse, aContext );
	}

	/**
	 * Override of performAction method - this is where the main computation is done
	 * for the example.  By placing the computation in invokeAction is is automatically
	 * performed when the component loads.  Here we use the current values of the
	 * start and stop values to calculate all of the primes.
	 */
	public Object performAction() {
		component.doTheJob();
		return null;
	}

	/**
	 * Method to return the result page when the computation is complete.  This
	 * methods sets the result page, passes all of the computation information,
	 * and then returns the page.
	 */
	public WOComponent pageForResult(Object result) {
		return component.finishPage();
	}

	public void setComponent(IDTLongPageCaller component) {
		this.component = component;
	}
	
	/**
	 * L'avancement de la tache
	 * @return
	 */
	public String htmlAdvanceString() {
		return component.htmlAdvanceString();
	}
}