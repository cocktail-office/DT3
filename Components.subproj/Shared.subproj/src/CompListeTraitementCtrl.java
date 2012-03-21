import java.util.GregorianCalendar;

import org.cocktail.dt.server.metier.EODocumentDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.GEDDescription;
import org.cocktail.fwkcktlwebapp.common.util.NSArrayCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.appserver.WOMessage;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

/*
 * Copyright Universit� de La Rochelle 2008
 *
 * Ce logiciel est un programme informatique servant � g�rer les demandes
 * d'utilisateurs aupr�s d'un service.
 * 
 * Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA 
 * sur le site "http://www.cecill.info".

 * En contrepartie de l'accessibilit� au code source et des droits de copie,
 * de modification et de redistribution accord�s par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limit�e.  Pour les m�mes raisons,
 * seule une responsabilit� restreinte p�se sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les conc�dants successifs.

 * A cet �gard  l'attention de l'utilisateur est attir�e sur les risques
 * associ�s au chargement,  � l'utilisation,  � la modification et/ou au
 * d�veloppement et � la reproduction du logiciel par l'utilisateur �tant 
 * donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � 
 * manipuler et qui le r�serve donc � des d�veloppeurs et des professionnels
 * avertis poss�dant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invit�s � charger  et  tester  l'ad�quation  du
 * logiciel � leurs besoins dans des conditions permettant d'assurer la
 * s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement, 
 * � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�. 

 * Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez 
 * pris connaissance de la licence CeCILL, et que vous en avez accept� les
 * termes.
 */

/**
 * Le controleur du composant <code>CompListeTraitement</code>
 * 
 * @author ctarade
 */
public class CompListeTraitementCtrl extends DTComponentCtrl {

	//
	private EOIntervention eoIntervention;

	// la liste des traitements en cours d'inspection
	private NSMutableArray traitementDetailList;

	// la liste des intervenants ayant saisi au moins 1 traitement sur la DT en
	// cours d'inspection
	private NSArray intervenantList;

	/**
	 * Initialiser le controleur pour fonctionner avec une DT particuliere. Le
	 * controleur est reinstancie des qu'on inspecte une nouvelle DT.
	 * 
	 * @param component
	 * @param anIntOrdre
	 */
	public CompListeTraitementCtrl(DTWebComponent component, EOIntervention anEoIntervention) {
		super(component);
		eoIntervention = anEoIntervention;
		initCtrl();
	}

	/**
	 * Initialisation du controleur
	 */
	private void initCtrl() {
		// raz des traitements detailles
		traitementDetailList = new NSMutableArray();
		// classement selon les preferences utilisateur
		getTraitementDg().setSortOrderings(
				interventionBus().getTraitementsSort(EOTraitement.TRA_DATE_DEB_KEY));
		// fetcher les traitement pour cette DT
		getTraitementDg().queryBindings().setObjectForKey(eoIntervention.intOrdre(), "intOrdre");
		getTraitementDg().qualifyDataSource();
		// liste des intervenants
		intervenantList =
				NSArrayCtrl.removeDuplicate(
						NSArrayCtrl.flattenArray(
								(NSArray) getTraitementDg().displayedObjects().valueForKey("toIndividuUlr")));

	}

	// actions

	/**
	 * Ajouter le traitement a la liste des traitements detailles
	 */
	protected void addTraitementDetail(CktlRecord recTraitement) {
		if (!isBeingDetailled(recTraitement)) {
			traitementDetailList.addObject(recTraitement);
		}
	}

	/**
	 * Supprimer le traitement a la liste des traitements detailles
	 */
	protected void removeTraitementDetail(CktlRecord recTraitement) {
		if (isBeingDetailled(recTraitement)) {
			traitementDetailList.removeIdenticalObject(recTraitement);
		}
	}

	/**
	 * Supprimer le traitement de la base de donnees et ses fichiers joints de
	 * GEDFS
	 * 
	 * @param recTraitement
	 * @return <em>true</em> si tout s'est bien passe
	 */
	protected boolean delete(EOTraitement recTraitement) {
		// suppression de gedfs
		NSArray<EODocumentDt> eoDocumentDtArray = interventionBus().findDocuments(
				eoIntervention.intOrdre(), recTraitement.traOrdre(), true);
		for (int i = 0; i < eoDocumentDtArray.count(); i++) {
			EODocumentDt eoDocumentDt = eoDocumentDtArray.objectAtIndex(i);
			dtApp().gedBus().deleteDocumentGED(eoDocumentDt.docOrdre().intValue());
		}
		// on controle pas si la suppression de GEDFS marche ou pas ...
		return interventionBus().deleteTraitements(
				null, recTraitement, null, eoIntervention) > 0;
	}

	/**
	 * Filtrer la liste des traitements selon la selection d'un intervenant. Si
	 * <code>null</code>, alors on affiche toute les DT
	 */
	protected void doFiltrerTraitementDgParIntervenant(CktlRecord intervenantSelected) {
		// raz des traitements detailles
		traitementDetailList = new NSMutableArray();
		// passage du binding a la fetchSpec
		if (intervenantSelected == null) {
			getTraitementDg().queryBindings().remove("toIndividuUlr");
		} else {
			getTraitementDg().queryBindings().setObjectForKey(intervenantSelected, "toIndividuUlr");
		}
		// fetcher
		getTraitementDg().qualifyDataSource();
	}

	// boolean

	/**
	 * Indique si l'enegistrement fait partie de la liste des traitements
	 * detailles
	 */
	protected boolean isBeingDetailled(CktlRecord recTraitement) {
		return traitementDetailList.containsObject(recTraitement);
	}

	/**
	 * Indique si le contenu du traitement peut etre modifie par l'utilisateur
	 */
	protected boolean canModifierTraitement(EOTraitement recTraitement) {
		return interventionBus().canModifierTraitement(
				recTraitement, dtUserInfo().noIndividu(), recTraitement.toIntervention().cStructure());
	}

	/**
	 * Indique si le traitement peut etre supprime par l'utilisateur. C'est la
	 * meme chose que pour la modification
	 */
	protected boolean canDeleteTraitement(EOTraitement recTraitement) {
		return interventionBus().canModifierTraitement(
				recTraitement, dtUserInfo().noIndividu(), recTraitement.toIntervention().cStructure());
	}

	/**
	 * Indique si le traitement est declare public (consultable)
	 */
	protected boolean isPublic(EOTraitement recTraitement) {
		return recTraitement.stringForKey("traConsultable").equals("O");
	}

	// operation sur la base de donnees

	/**
	 * Effectuer une synchronisation de la liste des traitements avec ceux de la
	 * base de donnees.
	 */
	protected void doReloadTraitementDg() {
		getTraitementDg().qualifyDataSource();
	}

	// getters

	private WODisplayGroup getTraitementDg() {
		return ((CompListeTraitement) getComponent()).getTraitementDg();
	}

	/**
	 * La liste des intervenants de la DT ayant saisi au moins 1 traitement.
	 * 
	 * @return {@link NSArray} de {@link CktlRecord} de l'entité Individu
	 */
	protected NSArray getIntervenantList() {
		return intervenantList;
	}

	// display

	/**
	 * date(s) du traitement
	 */
	protected String dateDisplay(CktlRecord recTraitement) {
		return interventionBus().traitementDateDisplay(recTraitement);
	}

	/**
	 * Le nom de l'intervenant pour un traitement
	 */
	protected String traitementIntervenantDisplay(CktlRecord recTraitement) {
		return intervenantDisplay(recTraitement.recForKey("toIndividuUlr"));
	}

	/**
	 * Le nom d'un individu formatté première lettre du prénom + nom
	 * 
	 * @param recIndividu
	 * @return
	 */
	protected String intervenantDisplay(CktlRecord recIndividu) {
		return StringCtrl.formatName(
				recIndividu.stringForKey("prenom"),
				recIndividu.stringForKey("nomUsuel"));
	}

	/**
	 * Affiche le contenu reduit de traitement, non formatte sans saut de ligne
	 */
	protected String traitementShortDisplay(CktlRecord recTraitement) {
		String disp = getTraTraitementForUser(recTraitement);
		// reduction a 150 car
		disp = StringCtrl.compactString(disp, 150, " <...>");
		return disp;
	}

	/**
	 * L'infobulle de detail du traitement. En caracteres plus gros, avec le texte
	 * integral
	 */
	protected String toolTipHtmlTraitement(EOTraitement recTraitement) {
		return DTStringCtrl.getHtmlToolTipBox("Traitement " + (isPublic(recTraitement) ? "public" : "non consultable"),
				getTraTraitementForUser(recTraitement));
	}

	/**
	 * Affiche le contenu du traitement selon la personne connectee. Un traitement
	 * prive peut ne pas etre affiche selon le cas
	 * 
	 * @param recTraitement
	 * @return
	 */
	private String getTraTraitementForUser(CktlRecord recTraitement) {
		String comment = DTInterventionBus.HiddenCommentMessage;
		boolean canReadComment = interventionBus().canReadTraitement(
				recTraitement, dtUserInfo().noIndividu(), recTraitement.stringForKeyPath("toIntervention.cStructure"));
		if (canReadComment) {
			comment = interventionBus().traitementContentDisplay(recTraitement);
		}
		return comment;
	}

	/**
	 * Donne total de fichiers attaches au traitement
	 * 
	 * @param recTraitement
	 * @return
	 */
	protected int countAttachmentsForTraitement(CktlRecord recTraitement) {
		return interventionBus().findDocuments(
				eoIntervention.intOrdre(), recTraitement.numberForKey("traOrdre"), false).count();
	}

	/**
	 * Affiche le contenu complet de traitement (quand le traitement est detaille)
	 */
	protected String traitementLongDisplay(CktlRecord recTraitement) {
		return StringCtrl.replace(
				WOMessage.stringByEscapingHTMLAttributeValue(getTraTraitementForUser(recTraitement)), "&#10;", "<br/>");
	}

	/**
	 * Donne la liste des chemins des fichiers attaches a un traitement TODO faire
	 * du cache
	 */
	protected NSArray traitementFilePaths(CktlRecord recTraitement) {
		NSArray documents = interventionBus().findDocuments(
				eoIntervention.intOrdre(), recTraitement.numberForKey("traOrdre"), false);
		NSMutableArray filePaths = new NSMutableArray();
		for (int i = 0; i < documents.count(); i++) {
			CktlRecord recDocument = (CktlRecord) documents.objectAtIndex(i);
			GEDDescription gedDescription = dtSession().gedBus().inspectDocumentGED(
					recDocument.intForKey("docOrdre"));
			if (gedDescription != null) {
				filePaths.addObject(gedDescription.reference);
			}
		}
		return filePaths.immutableClone();
	}

	/**
	 * La cle primaire (pour constituer une ancre HTML par exemple)
	 * 
	 * @return
	 */
	protected Number getTraitementKey(CktlRecord recTraitement) {
		return recTraitement.numberForKey("traOrdre");
	}

	/**
	 * Donne le temps total passe pour la liste des traitements
	 */
	protected String getTotalTimeTraitement(NSArray recsTraitement) {
		int totalMinutes = 0;
		// somme de toutes les differences
		for (int i = 0; i < recsTraitement.count(); i++) {
			CktlRecord rec = (CktlRecord) recsTraitement.objectAtIndex(i);
			GregorianCalendar gcDeb = new GregorianCalendar();
			gcDeb.setTime(rec.dateForKey("traDateDeb"));
			GregorianCalendar gcFin = new GregorianCalendar();
			gcFin.setTime(rec.dateForKey("traDateFin"));
			totalMinutes += (gcFin.getTimeInMillis() - gcDeb.getTimeInMillis()) / (1000 * 60);
		}
		// transformer jour-heures-minutes
		int minutes = totalMinutes % 60;
		int heures = totalMinutes / 60;
		int jours = 0;
		if (heures > 24) {
			jours = heures / 24;
			heures = heures - 24 * jours;
		}
		String strTime = (heures < 10 ? "0" : "") + heures + "h " + (minutes < 10 ? "0" : "") + minutes + "min";
		if (jours > 0) {
			strTime = jours + "j " + strTime;
		}
		return strTime;
	}

	// bus

	private DTInterventionBus interventionBus() {
		return dtSession().dataCenter().interventionBus();
	}
}
