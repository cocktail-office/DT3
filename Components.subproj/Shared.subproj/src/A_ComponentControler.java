


import com.webobjects.appserver.WOComponent;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * @author ctarade 27 mai 2005
 *
 */
public abstract class A_ComponentControler {

	private Application app;
	private Session session;
	private EOEditingContext ec;
	
	/**
	 * Le composant associe au controleur
	 */
	private WOComponent component;
	
	public DTUserInfo dtUserInfo() {
		return session.dtUserInfo();
	}

	/**
	 * La page qui affiche le composant
	 */		
	public WOComponent caller;

	public A_ComponentControler(Session aSession) {
		super();
		session = aSession;
		this.initComponentControler();
	}
	
	private void initComponentControler() {
		ec = session.defaultEditingContext();
		app = (Application)Application.application();
	}

	/**
	 * Effectue la sauvegarde de {@link #ec()}
	 * @return <code>true</code> si pas de probleme
	 * @throws Throwable
	 */
	public boolean sauvegarde() throws Throwable {
		try {
			ec().lock();
			ec().saveChanges();
		} catch (Exception exception) {
			exception.printStackTrace();
			ec().revert();
			return false;
		} finally {
			ec().unlock();
		}
		return true;
	}
	
	
	// setters
	
	/**
	 * Indiquer le composant sous controle
	 */
	public void setComponent(WOComponent value) {
		component = value;
	}
	
	// getters
	
	protected final Application dtApp() {
		return app;
	}

	protected final Session dtSession() {
		return session;
	}

	protected final EOEditingContext ec() {
		return ec;
	}

	/**
	 * Surchageable par chaque controleur (pour caster)
	 * @return
	 */
	protected WOComponent getComponent() {
		return component;
	}
	
}
