import org.cocktail.dt.server.metier.DTEOEditingContextHandler;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOVActivites;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
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
 * @author ctarade
 * 
 */
public abstract class A_DTChartComponent
		extends DTWebComponent {

	/** l'activité dont on va examiner les fils directs */
	private EOVActivites eoVActivites;

	/** période d'interrogation */
	private NSTimestamp dDebut, dFin;

	/** les intervention pour un meme service */
	private NSArray<EOIntervention> interventionArrayForService;

	/** message d'erreur */
	public String errMessage;

	/**
	 * @param context
	 */
	public A_DTChartComponent(WOContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public final NSTimestamp getDDebut() {
		return dDebut;
	}

	public final void setDDebut(NSTimestamp value) {
		NSTimestamp prevDDebut = dDebut;
		dDebut = value;
		if (prevDDebut != dDebut) {
			onChangeDDebut();
		}
	}

	public final NSTimestamp getDFin() {
		return dFin;
	}

	public final void setDFin(NSTimestamp value) {
		NSTimestamp prevDFin = dFin;
		dFin = value;
		if (prevDFin != dFin) {
			onChangeDFin();
		}
	}

	public final EOVActivites getEoVActivites() {
		return eoVActivites;
	}

	private String prevCStructure, cStructure;
	private Integer prevActOrdre, actOrdre;

	public final void setEoVActivites(EOVActivites value) {
		if (eoVActivites != null) {
			prevCStructure = eoVActivites.cStructure();
			prevActOrdre = eoVActivites.actOrdre();
		}
		eoVActivites = value;
		cStructure = eoVActivites.cStructure();
		actOrdre = eoVActivites.actOrdre();
		// si changement de service, on recharge toutes DTs
		if (prevCStructure == null ||
				prevCStructure.equals(cStructure) == false) {
			onChangeService();
		}
		// si changement d'activité, on recharge juste les comptes
		if (prevActOrdre == null ||
				prevActOrdre.intValue() != actOrdre.intValue()) {
			onChangeActivite();
		}
	}

	public final NSArray<EOIntervention> getInterventionArrayForService() {
		if (interventionArrayForService == null) {
			errMessage = null;

			// on controle la fetch limit pour eviter les OutOfMemory
			DTEOEditingContextHandler handler = new DTEOEditingContextHandler();

			interventionArrayForService = EOIntervention.fetchAll(
					dtSession().defaultEditingContext(),
					qualifierInterventionService(),
					null,
					true,
					EOIntervention.MAX_FETCH_LIMIT_STATS,
					handler);

			if (handler.getFetchLimitWasExceeded()) {
				errMessage = "Attention, la limite maximum de " + EOIntervention.MAX_FETCH_LIMIT_STATS + " traitées a été atteinte.\n" +
						"Seuls ces résultats seront traités par la statistique ... réduisez la période d'interrogation pour avoir une statistique plus précise";
			}
		}
		return interventionArrayForService;
	}

	public final void setInterventionArrayForService(NSArray<EOIntervention> interventionArrayForService) {
		this.interventionArrayForService = interventionArrayForService;
	}

	/** Traitement à effectuer pour le changement de date de début */
	public abstract void onChangeDDebut();

	/** Traitement à effectuer pour le changement de date de fin */
	public abstract void onChangeDFin();

	/** Traitemetn à effectuer si le service de l'activité a changé */
	public abstract void onChangeService();

	/** Traitemetn à effectuer si le service de l'activité a changé */
	public abstract void onChangeActivite();

	/** Titre du chart */
	public abstract String titre();

	/** Le qualifier permettant d'obtenir les DTs lié à un même service (cache) */
	public abstract EOQualifier qualifierInterventionService();

}
