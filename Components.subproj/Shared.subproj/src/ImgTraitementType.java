import org.cocktail.dt.server.metier.EOTraitement;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class ImgTraitementType extends WOComponent {

	public EOTraitement eoTraitement;

	public ImgTraitementType(WOContext context) {
		super(context);
	}
}