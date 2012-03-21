import java.util.GregorianCalendar;

import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;

import com.webobjects.foundation.NSTimestamp;

/*
 * Copyright Université de La Rochelle 2011
 *
 * Ce logiciel est un programme informatique servant à gérer les demandes
 * d'utilisateurs auprès d'un service.
 * 
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.

 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant 
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */

/**
 * Controleur du composant {@link FooterSwap}
 * 
 * @author ctarade
 */
public abstract class A_FooterSwapCtrl
		extends A_DTWebComponentCtrl {

	// date de réalisation souhaitée
	private NSTimestamp intDateSouhaite;

	// message d'erreur
	public boolean errorDate;

	public A_FooterSwapCtrl(Session aSession) {
		super(aSession);
		clearErrors();
	}

	/**
	 * 
	 */
	public void clearErrors() {
		errorDate = false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		boolean hasErrors = false;

		hasErrors = errorDate;

		return hasErrors;
	}

	/**
	 * Effectue la validation du formulaire. S'il y a une erreur, celle ci est
	 * remontée via {@link #setMainError(String)} et {@link #hasErrors()}
	 * retournera <code>true</code>
	 */
	public void validate() {
		if (getIntDateSouhaite() == null) {
			errorDate = true;
			setMainError("La date de réalisation souhaitée est absente");
		}
		if (errorDate == false) {
			NSTimestamp now = DateCtrl.now();

			// recadrer à minuit
			GregorianCalendar nowGC = new GregorianCalendar();
			nowGC.setTime(now);
			NSTimestamp nowMinuit = now.timestampByAddingGregorianUnits(0, 0, 0,
					-nowGC.get(GregorianCalendar.HOUR_OF_DAY),
					-nowGC.get(GregorianCalendar.MINUTE),
					-nowGC.get(GregorianCalendar.SECOND));

			if (DateCtrl.isBefore(getIntDateSouhaite(), nowMinuit)) {
				errorDate = true;
				setMainError("La date de réalisation souhaitée est avant la date du jour (" + DateCtrl.dateToString(now) + ")");
			}
		}
	}

	public final NSTimestamp getIntDateSouhaite() {
		return intDateSouhaite;
	}

	/**
	 * On interdit la saisie d'une date nulle.
	 */
	public final void setIntDateSouhaite(NSTimestamp intDateSouhaite) {
		if (intDateSouhaite != null) {
			this.intDateSouhaite = intDateSouhaite;
		}
	}

	/**
	 * @deprecated La variable contenant le LOLF ID suite à création de la DT.
	 *             N'est utilisé que si {@link #isAfficherListeDestinationLolf()}
	 *             est a <code>true</code>
	 */
	public final Number lolfId() {
		return null;
	}

}
