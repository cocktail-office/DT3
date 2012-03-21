

import com.webobjects.appserver.WOContext;


/**
 * Une page qui est controlee par un controleur
 */

public abstract class A_ComponentControled 
	extends DTWebComponent {

	/**
	 * Le controleur de cette page
	 */
	public A_ComponentControler ctrl;
	
	public A_ComponentControled(WOContext context) {
		super(context);
	}

	/**
	 * associer ce composant a son controleur des 
	 * que le binding est appele
	 */
	public void setCtrl(A_ComponentControler value) {
		ctrl = value;
		ctrl.setComponent(this);
	}

}
