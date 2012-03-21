import com.webobjects.appserver.WOContext;

public class ListeDestinationLolf
		extends A_DTWebComponentControled {

	public ListeDestinationLolf(WOContext context) {
		super(context);
	}

	public A_ListeDestinationLolfCtrl ctrl() {
		return (A_ListeDestinationLolfCtrl) ctrl;
	}
}