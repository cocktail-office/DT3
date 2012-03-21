/*
 * Copyright CRI - Universite de La Rochelle, 2001-2005 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use, 
 * modify and/or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and, more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
import java.util.Hashtable;

import org.cocktail.dt.server.metier.EOActivites;
import org.cocktail.dt.server.metier.EOEmail;
import org.cocktail.dt.server.metier.EOEtatDt;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.dt.server.metier.EOInterventionInfin;
import org.cocktail.dt.server.metier.EOTraitement;
import org.cocktail.dt.server.metier.EOVActivites;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlSort;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.dt.app.DTMailCenterConsts;

/**
 * Cette classe centralise l'envoi des messages mail : informations sur les DT,
 * traitement, etc.
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTMailCenter
		extends DTComponent
		implements DTMailCenterConsts {

	private static NSArray traSort = CktlSort.newSort(EOTraitement.TRA_DATE_DEB_KEY);
	private static NSArray EmptyArray = new NSArray();
	private static NSDictionary EmptyDictionnary = new NSDictionary();
	/** Le chemin de la DA vers PieWeb pour l'acces direct au devis */
	// private static String urlPrestaWeb;
	private NSKeyValueCoding intervention;
	private NSArray interventionFiles;
	private NSArray traitements;
	private NSDictionary traitementsFiles;
	private NSArray noIntervenants;

	/** La session en cours */
	private Session dtSession;

	/** Le point d'entree de SAM pour les messages de discussions */
	private static String appSamMessageMail;

	/**
	 * Cree un nouvel objet. Prend comme parametre les informations de
	 * l'utilisateur en cours. Par exemple, l'utilisateur actuellement connecte a
	 * l'application. Ces informations sont enregistrees dans l'objet session.
	 */
	public DTMailCenter(Session session, String anUrlPrestaWeb) {
		dtSession = session;
		appSamMessageMail = dtApp().config().stringForKey("APP_SAM_MESSAGES_MAIL");
		// XXX bidouille en attendant PieWeb, on donne l'adresse pour lancer PieJC
		/*
		 * urlPrestaWeb = dtSession.pieBus().servicePerformer().checkServiceAddress(
		 * anUrlPrestaWeb, DTPrestaServicesConst.MethodDirectActionDisplayDevis);
		 */
		/*
		 * urlPrestaWeb = "http://www.univ-lr.fr/cgi-bin/WebObjects/Pie.woa/" +
		 * "1/eowebstart/com.webobjects.eodistribution._EOWebStartAction/webStart/JavaClient.jnlp"
		 * ;
		 */
	}

	/**
	 * Retourne la reference vers l'objet regroupant tous les gestionnaires
	 * d'acces a la base de donnees.
	 */
	private DTDataCenter dataCenter() {
		return dtSession.dataCenter();
	}

	/**
	 * Retourne la reference vers un objet contenant les informations sur
	 * l'utilisateur connecte a l'application.
	 */
	private DTUserInfo userInfo() {
		return dtSession.dtUserInfo();
	}

	/**
	 * Retourne une reference vers une instance de gestionnaire des informations
	 * sur les individus.
	 */
	private DTIndividuBus indBus() {
		return dtSession.dataCenter().individuBus();
	}

	/**
	 * Retourne une reference vers une instance de gestionnaire des informations
	 * sur les interventions.
	 */
	private DTInterventionBus iBus() {
		return dataCenter().interventionBus();
	}

	/**
	 * Retourne une reference vers une instance de gestionnaire des informations
	 * sur les activites.
	 */
	private DTActiviteBus actBus() {
		return dataCenter().activiteBus();
	}

	/**
	 * Retourne une reference vers une instance de gestionnaire des informations
	 * sur les contacts.
	 */
	private DTContactsBus contactsBus() {
		return dataCenter().contactsBus();
	}

	/**
	 * Retourne la reference vers une instance de gestionnaire des informations
	 * dans la base de JEFYCO.
	 */
	private DTJefyBus jefyBus() {
		return dataCenter().jefyBus();
	}

	/**
	 * Retourne la reference vers une instance de gestionnaire des informations
	 * sur les structures et services de l'annuaire.
	 */
	private DTServiceBus structBus() {
		return dataCenter().serviceBus();
	}

	/**
	 * Returne la reference vers une instance de gestionnaire des documents.
	 */
	private DTDocumentCenter documentCenter() {
		return dtSession.dtDocumentCenter();
	}

	/**
	 * Retourne le lien de la directe action pour la validation des demandes.
	 */
	private String appValidationURL() {
		// remplacement de "/wa/DADemandes/valider" par "/wa/DAValidation" (Richard
		// 30/8/2006)
		return dtApp().getApplicationURL(dtSession.context()) + "/wa/DAValidation";
	}

	/**
	 * Retourne le lien de la directe action pour la consultation des demandes.
	 */
	private String appConsultationURL(int intOrdre) {
		return dtApp().getApplicationURL(dtSession.context()) + "/wa/DAConsultation?intOrdre=" + intOrdre;
	}

	/**
	 * Retourne la chaine de caracteres correspondant a la date et le format
	 * donnees. L'objet date doit etre une instance de la classe
	 * <code>NSTimestamp</code>.
	 */
	private String dateToString(Object date, String format) {
		if (format == null)
			return DateCtrl.dateToString((NSTimestamp) date, format);
		else
			return DateCtrl.dateToString((NSTimestamp) date, format);
	}

	/**
	 * Teste si le tableau anArray est vide.
	 */
	private boolean isEmtyArray(NSArray anArray) {
		return ((anArray == null) || (anArray.count() == 0));
	}

	/**
	 * Supprime toutes les initialisations faites precedement pour l'envoi de
	 * mail. Cette methode devrait etre appellee avant chaque operation d'envoi de
	 * mail.
	 */
	public void reset() {
		intervention = null;
		interventionFiles = EmptyArray;
		traitements = EmptyArray;
		traitementsFiles = EmptyDictionnary;
		noIntervenants = EmptyArray;
	}

	/**
	 * Definit la definition de l'intervention qui sera utilisee pour l'operation
	 * d'envoi de mail suivante.
	 */
	public void setIntervention(NSKeyValueCoding recIntervention, boolean initFiles) {
		intervention = recIntervention;
		if (initFiles) {
			initInterventionFiles();
		}
	}

	public void setInterventionFiles(NSArray filesURL) {
		interventionFiles = filesURL;
	}

	/**
	 * Definit la liste des traitements pour une operation d'envoi de mail
	 * suivante.
	 */
	public void setTraitements(NSArray recTraitements, boolean resort, boolean initFiles) {
		if (!isEmtyArray(recTraitements)) {
			if (resort)
				traitements = EOSortOrdering.sortedArrayUsingKeyOrderArray(recTraitements, traSort);
			else
				traitements = recTraitements;
		} else {
			traitements = EmptyArray;
		}
		if ((traitements.count() > 0) && (initFiles)) {
			initTraitementsFiles();
		}
	}

	/**
   * 
   */
	public void setTraitementsFiles(NSDictionary traFilesURL) {
		traitementsFiles = traFilesURL;
	}

	/**
   * 
   */
	public void setNoIntervenants(NSArray noInts) {
		noIntervenants = (isEmtyArray(noInts) ? EmptyArray : noInts);
	}

	/**
   * 
   */
	private EOInterventionInfin recInfin(NSKeyValueCoding recIntervention) {
		CktlLog.trace("recIntervention : " + recIntervention);
		CktlLog.trace("recIntervention.intOrdre : " + recIntervention.valueForKey("intOrdre"));
		if (recIntervention == null)
			return null;
		else
			return iBus().findInterventionInfin(
					DTDataBus.DefaultTransactionId, (Number) recIntervention.valueForKey("intOrdre"));
	}

	/**
	 * Reformat le sujet des messages email de l'application DT. Voir le parametre
	 * <code>APP_MAIL_SUBJECT_FORMAT</code> dans la configuration de
	 * l'application.
	 */
	public String formatSubject(String subject, String codeService) {
		String subjFormat =
				StringCtrl.normalize(dtApp().config().stringForKey("APP_MAIL_SUBJECT_FORMAT"));
		// Si le format n'est pas donne, on laisse le sujet original
		// Sinon, on le formatte
		if (subjFormat.length() > 0) {
			// S'il faut, on recupere les infos sur le service
			if (subjFormat.indexOf("${service}") >= 0) {
				// On recupere les infos sur le service
				CktlRecord recService = structBus().structureForCode(codeService);
				String service = StringCtrl.emptyString();
				if (recService != null)
					service = recService.stringNormalizedForKey("lcStructure");
				if (service.length() == 0)
					service = "SERVICE INCONNU";
				// On met le vrai service dans le sujet
				subjFormat = StringCtrl.replace(subjFormat, "${service}", service);
			}
			// Ensuite, on met le vrai sujet
			if (subjFormat.indexOf("${sujet}") >= 0)
				subject = StringCtrl.replace(subjFormat, "${sujet}", subject);
			else {
				if (!subjFormat.endsWith(" "))
					subjFormat += " ";
				subject = subjFormat + subject;
			}
		}
		return subject;
	}

	/**
	 * Envoie d'un message mail apres la creation d'une DT.
	 * 
	 */
	public boolean mailCreerDT() {
		if (!dtApp().useMail() || (intervention == null))
			return true;
		//
		String intEtat = (String) intervention.valueForKey("intEtat");
		String sujet = formatSubject(
				"Création de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		StringBuffer message = new StringBuffer();
		if (intEtat.equals(EOEtatDt.ETAT_NON_VALIDEES)) {
			sujet += " (à valider)";
			message.append("CETTE DEMANDE DOIT ETRE VALIDEE AVANT D'ETRE PRISE EN COMPTE PAR LE SERVICE CONCERNE.\n");
			if (appValidationURL() != null) {
				message.append("Les demandes en attente de validation sont disponibles à l'adresse :\n");
				message.append("  ").append(appValidationURL()).append("\n");
			}
			message.append("\n\n");
		}

		String dtDescription = getDTDescription(
				intervention,
				true, true,
				true, true,
				true, true,
				true, true);

		message.append(dtDescription);

		String footer = getDefaultFooter(intEtat.equals(EOEtatDt.ETAT_NON_VALIDEES));
		if (footer.length() > 0) {
			message.append("\n----").append(footer);
		}

		Number actOrdre = (Number) intervention.valueForKey("actOrdre");

		String dstList = sendMailForService(
				sujet, message.toString(), actOrdre, intEtat.equals(EOEtatDt.ETAT_NON_VALIDEES) ? EOActivites.TYPE_RESP_FONCTIONNEL : EOActivites.TYPE_RESP_TECHNIQUE);

		boolean isEnvoiOk = false;
		if (!StringCtrl.isEmpty(dstList)) {
			isEnvoiOk = true;
		}

		// Les adresses mail / destinataire / CC
		String to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));

		// Envoi du mail a l'interesse s'il n'est pas déjà prévenu
		// On change le from en mettant l'adresse du service si elle est disponible
		if (StringCtrl.isEmpty(dstList) ||
				(!StringCtrl.isEmpty(dstList) && !StringCtrl.containsIgnoreCase(dstList, to))) {
			String from = getDefaultFrom();
			// mail de service exploitable ?
			EOVActivites eoVActivites = actBus().findVActivite(actOrdre);
			String mailService = eoVActivites.getGrpMailSiExistantEtUtilisable();
			if (!StringCtrl.isEmpty(mailService)) {
				from = mailService;
			}
			_localSendMail(intervention, from, to, null, sujet, message.toString());
		}

		return isEnvoiOk;
	}

	/**
	 * Envoie un message email suite a la validation d'une DT par le resposable
	 * fonctionnel.
	 */
	public void mailRejeterDT() {
		// Infos pour le mail
		String to, cc, sujet;
		StringBuffer motif = new StringBuffer();

		if ((!dtApp().useMail()) || (intervention == null))
			return;

		// Collecte des infos pour le mail
		// le destinaire et le sujet
		sujet = formatSubject(
				"Rejet de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		// motif = getDTmotif(recIntervention);
		motif.append("\nDemande rejetée par ");
		motif.append(getDTAffectantInfo()).append("\n");
		motif.append("\n--- Motif du rejet ---\n");
		motif.append(StringCtrl.normalize((String) intervention.valueForKey("intCommentaireInterne")));
		motif.append("\n\n");
		motif.append(getDTDescription(intervention, true, true, false, true, true, false, true, false));
		// Les adresses mail / destinataire / CC
		to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		cc = getCCforDT(intervention, true);
		// Envoi du mail a l'interesse et aux intervenants
		_localSendMail(intervention, getDefaultFrom(), to, cc, sujet, motif.toString());
		// le mail pour le service
		sendMailForService(sujet, motif.toString(), (Number) intervention.valueForKey("actOrdre"),
				EOActivites.TYPE_RESP_FONCTIONNEL);
	}

	/**
	 * Email suite a la validation d'une DT par le resposable fonctionnel.
	 */
	public boolean mailValiderDT() {
		// Infos pour le mail
		String to, cc, sujet;
		StringBuffer motif = new StringBuffer();

		if (!dtApp().useMail()) {
			return true;
		}

		if (intervention == null) {
			return false;
		}

		// Collecte des infos pour le mail
		// le destinaire et le sujet
		sujet = formatSubject(
				"Validation de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		// motif = getDTmotif(recIntervention);
		motif.append("\nDemande validée par ");
		motif.append(getDTAffectantInfo()).append("\n\n");
		motif.append(getDTDescription(intervention, true, true, true, true, true, true, true, false));
		// Les adresses mail / destinataire / CC
		to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		cc = getCCforDT(intervention, true);
		// La fin du message avec le lien vers le site Web
		String footer = getDefaultFooter(false);
		if (footer.length() > 0)
			motif.append("\n----").append(footer);
		// Envoi du mail a l'interesse et aux intervenants
		_localSendMail(intervention, getDefaultFrom(), to, cc, sujet, motif.toString());

		// le mail pour le service
		String dstList = sendMailForService(sujet, motif.toString(), (Number) intervention.valueForKey("actOrdre"),
				EOActivites.TYPE_RESP_TECHNIQUE);

		boolean isEnvoiOk = false;
		if (!StringCtrl.isEmpty(dstList)) {
			isEnvoiOk = true;
		}
		return isEnvoiOk;

	}

	/**
	 * Envoi du messages mails apres la validation et la cloture d'une DT a
	 * destination du service.
	 * 
	 * @param mode
	 *          : indique les mails particuliers a envoyer
	 */
	public void mailCloreDT(int mode) {
		// Infos pour le mail
		Hashtable params = collectInfoMailCloreDT();

		if ((!dtApp().useMail()) || (intervention == null || params == null))
			return;

		// le mail pour le service
		sendMailForService((String) params.get("sujet"), (String) params.get("motif"),
				(Number) intervention.valueForKey("actOrdre"),
				EOActivites.TYPE_RESP_TECHNIQUE);

		// le mail pour la secretaire (DTRepro)
		if (mode != DT_CLASSIC) {
			sendMailForSecretaire(mode);
		}
	}

	/**
	 * Envoi du message de cloture pour les destinataires facultatifs : -
	 * demandeur - autres intervenants
	 */
	public void mailCloreDTFacultatif() {
		// Infos pour le mail
		Hashtable params = collectInfoMailCloreDT();

		if ((!dtApp().useMail()) || (intervention == null || params == null))
			return;

		//
		_localSendMail(intervention, getDefaultFrom(), (String) params.get("to"),
				(String) params.get("cc"), (String) params.get("sujet"), (String) params.get("motif"));
	}

	/**
	 * Collecte les infos pour le mail de cloture d'une DT
	 */
	protected Hashtable collectInfoMailCloreDT() {
		// le destinaire et le sujet
		String sujet = formatSubject(
				"Cloture de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		StringBuffer motif = new StringBuffer();
		motif.append(getDTDescription(intervention, true, true, false, true, false, false, true, false));
		// Collection des infos sur les traitements
		for (int i = 0; i < traitements.count(); i++)
			motif.append("\n").append(getTraitementDescription((NSKeyValueCoding) traitements.objectAtIndex(i)));
		appendSignatureIfPrefs(motif);
		// Les adresses mail / destinataire / CC
		String to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		String cc = getCCforDT(intervention, true);

		Hashtable hTable = new Hashtable();
		hTable.put("sujet", sujet);
		hTable.put("motif", motif.toString());
		hTable.put("to", to);
		hTable.put("cc", cc);

		return hTable;
	}

	/**
	 * Collecte les infos pour le mail d'ajour de traitement d'une DT
	 */
	protected Hashtable collectInfoMailAjouterTraitementDT() {

		// le destinaire et le sujet
		String sujet = formatSubject(
				"Traitement en cours de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		// le message
		StringBuffer motif = new StringBuffer();
		motif.append(getDTDescription(intervention, true, true, false, true, true, false, true, false));
		NSKeyValueCoding recTraitement = (NSKeyValueCoding) traitements.objectAtIndex(0);
		motif.append(getTraitementDescription(recTraitement));
		// ajouter la messagerie par SAM
		// appendDiscussionSAMAction("traOrdre", (Number)
		// recTraitement.valueForKey("traOrdre"), motif, false);
		// ajouter la signature generique "DT" si l'utilisateur le souhaite
		appendSignatureIfPrefs(motif);
		// Les adresses mail / destinataire / CC
		String to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		String cc = getCCforDT(intervention, true);

		Hashtable hTable = new Hashtable();
		hTable.put("sujet", sujet);
		hTable.put("motif", motif.toString());
		hTable.put("to", to);
		hTable.put("cc", cc);

		return hTable;
	}

	/**
	 * Collecte les infos pour le mail la demande d'info sur une DT
	 */
	protected Hashtable collectInfoMailInformationsDT() {

		// le destinaire et le sujet
		String sujet = formatSubject(
				"Demande d'informations DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		// le message
		StringBuffer motif = new StringBuffer("\n\n\n");
		motif.append(getDTDescription(intervention, true, true, false, false, false, true, true, false));
		// signature
		appendSignatureIfPrefs(motif);
		// Les adresses mail / destinataire / CC
		String to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));

		Hashtable hTable = new Hashtable();
		hTable.put("sujet", sujet);
		hTable.put("motif", motif.toString());
		hTable.put("to", to);

		return hTable;
	}

	/**
	 * Envoie un message mail apres ajout d'un nouveau traitement TODO : l'appel
	 * se fait avec la préference utilisateur sendMail. Il faut créer un nouveau
	 * parametre
	 */
	public void mailAjouterTraitementDT(boolean sendToService) {
		// Infos pour le mail
		Hashtable params = collectInfoMailAjouterTraitementDT();

		if (!dtApp().useMail() ||
				(intervention == null) ||
				(traitements.count() == 0))
			return;

		// le mail pour le service
		if (sendToService) {
			sendMailForService(
					(String) params.get("sujet"), (String) params.get("motif"),
					(Number) intervention.valueForKey("actOrdre"),
					EOActivites.TYPE_RESP_TECHNIQUE);
		}
	}

	/**
	 * Envoi de messages mails apres la suppression d'une DT.
	 * 
	 * @param informConcerne
	 *          envoyer le message à l'individu demandeur
	 */
	public void mailSupprimerDT(boolean informConcerne) {
		String dest, cc, sujet;
		StringBuffer motif;

		if ((!dtApp().useMail()) || (intervention == null))
			return;

		// Collecte des infos pour le mail
		// le destinaire et le sujet
		sujet = formatSubject(
				"Suppression de la DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		motif = new StringBuffer();
		motif.append("\n").append(sujet);
		motif.append("\n\n").append(getDTDescription(intervention, true, true, false, false, true, true, false, false));
		appendSignatureIfPrefs(motif);
		// Les adresses mail / destinataire / CC
		dest = getCCforDT(intervention, false);
		cc = "";
		if (informConcerne) {
			cc = dest;
			dest = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		}
		// Envoi du mail a l'interesse et aux intervenants
		if (!StringCtrl.isEmpty(dest)) {
			_localSendMail(intervention, getDefaultFrom(), dest, cc, sujet, motif.toString());
		}
		// le mail pour le service
		sendMailForService(sujet, motif.toString(), (Number) intervention.valueForKey("actOrdre"),
				EOActivites.TYPE_RESP_TECHNIQUE);
	}

	/**
	 * Envoie le message d'affectation d'une demande aux individus dont les
	 * identifiants sont donnes dans la liste <code>noIndividu</code>.
	 */
	public void mailAffecterDT(NSArray allNoIndividus) {
		StringBuffer footer, message, cc;
		String sujet, to, aStr, ccIntervenants; // , ccMail;
		Number noIndividu;

		if (!dtApp().useMail() ||
				(intervention == null) ||
				(allNoIndividus.count() == 0))
			return;

		// On construit la liste des intervenants
		footer = new StringBuffer();
		cc = new StringBuffer();
		footer.append("\n--- Les personnes suivantes réaliseront l'intervention ---");
		for (int i = 0; i < allNoIndividus.count(); i++) {
			noIndividu = (Number) allNoIndividus.objectAtIndex(i);
			aStr = indBus().mailForNoIndividu(noIndividu);
			footer.append("\n").append(indBus().nomPrenomEmailForNoIndividu(noIndividu));
			footer.append("\n  (Attention ! Cette personne a ");
			footer.append(iBus().countInterventionsForIndividu(noIndividu,
												EOEtatDt.ETAT_EN_COURS, (String) intervention.valueForKey("cStructure")));
			footer.append(" demandes en cours)");
			cc.append(aStr).append(", ");
		}
		if (dtApp().appURL() != null) {
			footer.append("\n\nLes demandes de travaux sont consultables sur le Web à l'adresse ");
			footer.append(dtApp().appURL()).append("\n");
		}
		// On envoie le message pour chaque intervention
		ccIntervenants = cc.toString();
		ccIntervenants = ccIntervenants.substring(0, ccIntervenants.length() - 2);
		// Construction du message
		sujet = formatSubject(
				"Affectation de DT #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		message = new StringBuffer();
		message.append("\nDemande de travaux affectée par ");
		message.append(getDTAffectantInfo()).append("\n\n");
		message.append(getDTDescription(intervention, true, true, true, true, true, true, true, false));
		// Ajouter les adresses des personnes interessees
		noIndividu = (Number) intervention.valueForKey("intNoIndConcerne");
		to = indBus().mailForNoIndividu(noIndividu);
		noIndividu = (Number) intervention.valueForKey("intNoIndAppelant");
		aStr = indBus().mailForNoIndividu(noIndividu);
		if ((to.indexOf(aStr) == -1) && (ccIntervenants.indexOf(aStr) == -1)) {
			if (ccIntervenants.length() > 0)
				ccIntervenants += ", ";
			ccIntervenants += aStr;
		}
		// OK, on peut envoyer le message
		_localSendMail(intervention, getDefaultFrom(), to, ccIntervenants, sujet, message.toString() + footer.toString());
		// s'il y a un commentaire interne a cette DT, on va l'envoyer aux
		// intervenants uniquement
		String commentaire = (String) intervention.valueForKey("intCommentaireInterne");
		if (!StringCtrl.isEmpty(commentaire)) {

			StringBuffer bufMessage = new StringBuffer();
			bufMessage.append("Cette demande vous a été affectée; un commentaire interne ");
			bufMessage.append("a été saisi par l'expéditeur de ce message\n\n");
			bufMessage.append("--- Commentaire interne ---\n");
			bufMessage.append(commentaire);
			_localSendMail(null, getDefaultFrom(), ccIntervenants, null,
					sujet + " [Commentaire Interne]", bufMessage.toString());
		}
	}

	/**
	 * Envoie le message mail de transmission d'une demande a un autre service.
	 * 
	 * @param recService
	 *          Le service destinataire
	 * @param newCleService
	 *          Le nouveau numero de la DT apres son affectation au nouveau
	 *          service.
	 */
	public void mailTransmettreDTAuService(CktlRecord recService,
																					Number newCleService) {
		String sujet, to, from, cc;
		StringBuffer message;

		if (!dtApp().useMail() || (intervention == null))
			return;

		sujet = formatSubject(
				"Transmission de la DT #" + newCleService,
				(String) intervention.valueForKey("cStructure"));
		from = getDefaultFrom();
		// Envoi du mail au service destinataire
		message = new StringBuffer();
		message.append("\nDemande de travaux transmise par ");
		message.append(getDTAffectantInfo()).append("\n\n");
		message.append(getDTDescription(intervention, false, true, true, true, false, true, true, false));
		// TODO Voir ces valeurs : cStructure, actOrdre, type de responsable
		sendMailForService(sujet, message.toString(), (Number) intervention.valueForKey("actOrdre"),
				EOActivites.TYPE_RESP_TECHNIQUE);

		// Envoi du mail au(x) demandeur(s)
		to = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndAppelant"));
		cc = indBus().mailForNoIndividu((Number) intervention.valueForKey("intNoIndConcerne"));
		if (to.equals(cc))
			cc = null;
		message = new StringBuffer();
		message.append("\nVotre demande de travaux a ete transmise au service ");
		message.append(recService.valueForKey("llStructure"));
		message.append("\nDemande de travaux transmise par ");
		message.append(getDTAffectantInfo()).append("\n\n");
		message.append(getDTDescription(intervention, false, true, false, true, false, false, true, false));
		_localSendMail(null, from, to, cc, sujet, message.toString());
	}

	/**
	 * Envoie le mail "pour suite a donner" concernant une DT.
	 * 
	 * @param allNoIndividus
	 *          La liste des <code>noIndividu</code> des destinataires.
	 */
	public void mailSuiteADonner(NSArray allNoIndividus) {
		String sujet, from;
		StringBuffer cc, message;

		if (!dtApp().useMail())
			return;

		from = getDefaultFrom();
		cc = new StringBuffer();
		for (int i = 0; i < allNoIndividus.count(); i++) {
			if (cc.length() > 0)
				cc.append(", ");
			cc.append(indBus().mailForNoIndividu((Number) allNoIndividus.objectAtIndex(i)));
		}
		sujet = formatSubject(
				"Pour suite à donner : Demande de Travaux #" + intervention.valueForKey("intCleService"),
				(String) intervention.valueForKey("cStructure"));
		message = new StringBuffer();
		message.append("\n").append("Message transmis par ").append(getDTAffectantInfo()).append("\n\n");
		message.append(getDTDescription(intervention, false, true, false, true, false, false, true, false));
		_localSendMail(intervention, from, cc.toString(), null, sujet, message.toString());
	}

	/**
	 * Retourne le nom, le prenom et le service de l'utilisateur connecte.
	 */
	private String getDTAffectantInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append(userInfo().prenom()).append(" ");
		sb.append(userInfo().nom()).append(" (");
		sb.append(userInfo().dtServiceLibelle()).append(")");
		return sb.toString();
	}

	/**
	 * Retourne la description textuelle d'une DT. La description est domposee de
	 * numero, nom et adresse de demandeur, motif de la demande, et les dates de
	 * creation et realisation.
	 * 
	 * @param recIntervention
	 *          La DT.
	 * @param useAppelant
	 *          TODO
	 * @param useDates
	 *          Indique s'il faut ajouter les dates a la description.
	 */
	private String getDTDescription(NSKeyValueCoding recIntervention,
																	boolean useNumero, boolean useDateCreation,
																	boolean useDateSouhaitee, boolean useFiles,
																	boolean useMotsCles, boolean useInfin,
																	boolean useConsultAction, boolean useAppelant) {
		StringBuffer dtInfo = new StringBuffer();

		dtInfo.append("--- Demande de travaux ---");

		if (useNumero) {
			dtInfo.append("\nNuméro d'enregistrement : ");
			dtInfo.append(recIntervention.valueForKey(EOIntervention.INT_CLE_SERVICE_KEY));
		}

		dtInfo.append("\nDemandeur : ");
		dtInfo.append(indBus().nomPrenomEmailForNoIndividu((Number) recIntervention.valueForKey(EOIntervention.INT_NO_IND_CONCERNE_KEY)));

		if (useAppelant) {
			dtInfo.append("\nAppelant : ");
			dtInfo.append(indBus().nomPrenomEmailForNoIndividu((Number) recIntervention.valueForKey(EOIntervention.INT_NO_IND_APPELANT_KEY)));
		}

		if (useDateCreation) {
			dtInfo.append("\nDate de création : ");
			dtInfo.append(dateToString(recIntervention.valueForKey(EOIntervention.INT_DATE_CREATION_KEY), "%d/%m/%Y %H:%M"));
		}

		if (useDateSouhaitee) {
			dtInfo.append("\nDate de réalisation souhaitée : ");
			dtInfo.append(dateToString(recIntervention.valueForKey(EOIntervention.INT_DATE_SOUHAITE_KEY), "%d/%m/%Y"));
		}

		if (useMotsCles) {
			dtInfo.append("\nMots clés : " + recIntervention.valueForKey(EOIntervention.INT_MOTS_CLEFS_KEY));
		}

		if (useInfin) {
			EOInterventionInfin rec = recInfin(recIntervention);
			if (rec != null) {

				if (rec.orgId() != null) {
					String lignDesc = jefyBus().getLigneBudDescription(null, rec.orgId(), rec.tcdCode(), false, null);
					if (StringCtrl.isEmpty(lignDesc)) {
						lignDesc = "[Aucune ligne budgétaire]";
					}
					dtInfo.append("\nLigne budgétaire : " + lignDesc);
				}

				if (rec.lolfId() != null) {
					String destination = jefyBus().libelleDestinationInfin(rec.lolfId());
					dtInfo.append("\nAction Lolf : " + destination);
				}

				if (rec.prestNumero() != null) {
					dtInfo.append("\nDevis : #" + rec.prestNumero());
				}
			}
		}

		dtInfo.append("\n\n--- Motif de la demande ---\n");
		String motif = StringCtrl.trimText((String) recIntervention.valueForKey(EOIntervention.INT_MOTIF_KEY));
		dtInfo.append(StringCtrl.quoteText(motif, "  "));
		if (!motif.endsWith("\n")) {
			dtInfo.append("\n");
		}

		if (useFiles && (!isEmtyArray(interventionFiles))) {
			dtInfo.append("--- Documents attachés ---\n");
			dtInfo.append(StringCtrl.quoteText(getAttachementsText(interventionFiles), " "));
		}

		if (useConsultAction) {
			dtInfo.append("\n\n--- Consultation de la DT ---\n");
			dtInfo.append("  ").append(appConsultationURL(((Number) recIntervention.valueForKey(EOIntervention.INT_ORDRE_KEY)).intValue()));
			dtInfo.append("\n\n");
		}

		return dtInfo.toString();
	}

	/**
	 * Collecte la liste des adresse des personnes concernees par la DT (personne
	 * concernee et les intervenants). Cette liste ne contient pas l'adresse du
	 * demandeur.
	 * 
	 * @param recIntervention
	 *          La DT.
	 */
	public String getCCforDT(NSKeyValueCoding recIntervention, boolean takeFromIntervention) {
		// Les adresses mail / destinataire
		String mailIndConcerne = indBus().mailForNoIndividu((Number) recIntervention.valueForKey("intNoIndConcerne"));
		String mailIndAppelant = indBus().mailForNoIndividu((Number) recIntervention.valueForKey("intNoIndAppelant"));
		String cc = "";
		// on met la personne appelante en copie si ce n'est pas la personne
		// concernée
		if (!mailIndConcerne.equals(mailIndAppelant)) {
			cc += mailIndAppelant;
		}
		// les cc (intervenants/concernes)
		if (takeFromIntervention)
			noIntervenants = (NSArray) ((NSArray) recIntervention.valueForKey("tosIntervenant")).valueForKey("noIndividu");
		if (!isEmtyArray(noIntervenants)) {
			for (int i = 0; i < noIntervenants.count(); i++) {
				Number noIntervenant = (Number) noIntervenants.objectAtIndex(i);
				String mailIntervenant = indBus().mailForNoIndividu(noIntervenant);
				// ne pas faire doublons et ne pas mettre la personne concernée si elle
				// est intervenante
				if (!StringCtrl.containsIgnoreCase(cc, mailIntervenant) && !mailIndConcerne.equals(mailIntervenant)) {
					if (cc.length() > 0) {
						cc += ", ";
					}
					cc += mailIntervenant;
				}
			}
		}

		return cc;
	}

	// /**
	// * Extrait et format le motif de la DT. Le text contient le numero, la date
	// et le motif
	// * de la DT.
	// *
	// * @param recDT La DT.
	// */
	// private String getDTmotif(EOEnterpriseObject recDT) {
	// StringBuffer sb = new StringBuffer();
	// String motif = StringCtrl.normalize((String)recDT.valueForKey("intMotif"));
	// if (motif.length()>0) {
	// sb.append("\n--- Demande de travaux (#").append(recDT.valueForKey("intCleService"));
	// sb.append(", ").append(dateToString(recDT.valueForKey("intDateCreation"),
	// null));
	// sb.append(") ---\n\n").append(motif);
	// if (!motif.endsWith("\n")) sb.append("\n");
	// }
	// return sb.toString();
	// }

	/**
	 * Format le texte du traitement et l'ajoute au text donne en parametre. Les
	 * infos sur le traitement sont passees dans un objet
	 * <code>EOEnterpriseObject</code>.
	 * 
	 * @param text
	 *          Le texte auquel le traitement sera ajoute. Peut etre <i>null</i>.
	 * @param recTra
	 *          Le traitement.
	 */
	private String getTraitementDescription(NSKeyValueCoding recTra) {
		String traText;
		NSArray paths;
		StringBuffer sb = new StringBuffer();
		// Description de traitement
		sb.append("--- Traitement ---");
		sb.append("\n  Date : ").append(iBus().traitementDateDisplay((CktlRecord) recTra));
		sb.append("\n  Intervenant : ");
		sb.append(indBus().nomEtPrenomForNoIndividu((Number) recTra.valueForKey("noIndividu")));
		// Texte de traitement
		traText = StringCtrl.trimText(iBus().traitementContentDisplay((CktlRecord) recTra));
		sb.append("\n\n ").append(StringCtrl.quoteText(traText, "  "));
		if (!traText.endsWith("\n"))
			sb.append("\n");
		// Les fichiers attaches, s'ils existent
		paths = (NSArray) traitementsFiles.valueForKey(recTra.valueForKey("traOrdre").toString());
		if (!isEmtyArray(paths)) {
			sb.append("  - Documents attachés au traitement -\n");
			for (int i = 0; i < paths.count(); i++)
				sb.append("    ").append(paths.objectAtIndex(i)).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Ajout de la signature par defaut de DT a un flux. Rien n'est fait si les
	 * preferences de l'utilisateur l'indique. La signature se compose de nom et
	 * prenom, ainsi que de son adresse mail.
	 */
	private void appendSignatureIfPrefs(StringBuffer out) {
		if (dtSession.dtUserInfo().insertDtSig()) {
			String sign = StringCtrl.normalize(userInfo().nomEtPrenom());
			String mail = StringCtrl.normalize(userInfo().email());
			if (mail.length() > 0) {
				if (sign.length() > 0)
					sign += "\n";
				sign += mail;
			}
			out.append("\n\n--\n").append(sign);
		}
	}

	/**
	 * Le pied de page, utilise a la creation et a la validation
	 * 
	 * @param useValidation
	 * @return
	 */
	private String getDefaultFooter(boolean useValidation) {
		StringBuffer footer = new StringBuffer();
		if (intervention == null) {
			String value = dtApp().appURL();
			if (value != null) {
				footer.append("\nLes demandes de travaux sont consultables sur le Web à l'adresse :\n  ");
				footer.append(value);
			}
			if (useValidation) {
				value = appValidationURL();
				if (value != null) {
					footer.append("\nLes demandes de travaux peuvent être validées via l'adresse :\n  ");
					footer.append(value);
				}
			}
		}
		return footer.toString();
	}

	/**
	 * Envoie le message concernant une DT au service donne.
	 * 
	 * @param sujet
	 *          Le sujet du message.
	 * @param text
	 *          Le text du message.
	 * @return la liste des emails des destinataires si l'envoi s'est bien passé
	 */
	private String sendMailForService(
			String sujet, String text, Number actOrdre, String typeResponsables) {
		String strEmailList = null;

		NSArray<String> mails = actBus().findActiviteMails(actOrdre, typeResponsables);
		if (mails.count() > 0) {
			// On recupere les mails relatives a l'activite donnee
			String to = mails.objectAtIndex(0);
			StringBuffer cc = new StringBuffer();
			for (int i = 1; i < mails.count(); i++) {
				if (cc.length() > 0) {
					cc.append(", ");
				}
				cc.append(mails.objectAtIndex(i));
			}

			// OK, envoi de mail
			boolean isEnvoiOk = _localSendMail(intervention, getDefaultFrom(), to, cc.toString(), sujet, text);
			if (isEnvoiOk) {
				strEmailList = to;
				if (cc.length() > 0) {
					strEmailList += ", " + cc;
				}
			}
		}
		return strEmailList;
	}

	/** cloture classique de DT */
	public final static int DT_CLASSIC = 0;
	/** cloture de dt prestation dont le devis est a verifier par la secretaire */
	public final static int DT_PRESTA_VERIF = 1;
	/** cloture de dt prestation dont des infos budgetaires manquent */
	public final static int DT_PRESTA_MISSING = 2;
	/** cloture de dt prestation dont le disponible financiers est insuffisant */
	public final static int DT_PRESTA_NODISP = 3;

	/**
	 * Envoie le message d'information a destination des secretaires pour qu'elles
	 * valident et/ou saisissent la ligne budgetaire sur le devis attache a la
	 * DTRepro
	 * 
	 * @param mode
	 *          : preciser le message a expedier
	 */
	private boolean sendMailForSecretaire(int mode) {
		// donnees budgetaires
		EOInterventionInfin recInfin = recInfin(intervention);
		Number prestNumero = recInfin.numberForKey("prestNumero");
		// recherche structure correspondante
		String cStructContact = null;
		Number ctOrdre = (Number) intervention.valueForKey("ctOrdre");
		if (ctOrdre != null)
			cStructContact = (String) contactsBus().findContact((Number) intervention.
					valueForKey("ctOrdre")).stringForKey("cStructService");
		else
			cStructContact = recInfin.stringForKey("cStructure");
		String mailSec = "";
		NSArray noSecretaires = (NSArray) dtSession.dataCenter().serviceBus().
				responsablesPrestationForStructure(cStructContact).valueForKey("noIndividu");
		if (!isEmtyArray(noSecretaires)) {
			for (int i = 0; i < noSecretaires.count(); i++) {
				Number noIndividu = (Number) noSecretaires.objectAtIndex(i);
				String addr = indBus().mailForNoIndividu(noIndividu);
				if (mailSec.indexOf(addr) == -1) {
					if (mailSec.length() > 0)
						mailSec += ", ";
					mailSec += addr;
				}
			}
			String sujet = formatSubject(
					"Prestation a clore pour la DT #" + intervention.valueForKey("intCleService"),
					(String) intervention.valueForKey("cStructure"));
			String nomService = structBus().libelleForServiceCode(cStructContact, true, true);
			// remplir le contenu d'apres ce qui a ete saisi
			StringBuffer text = new StringBuffer();
			if (mode == DT_PRESTA_VERIF) {
				text.append("\n*Merci de verifier et valider la prestation dans l'application Pie*");
			} else if (mode == DT_PRESTA_MISSING) {
				if (recInfin.valueForKey("orgId") == null) {
					text.append("\n   *Ligne budgetaire.*");
				}
				if (recInfin.lolfId() == null || recInfin.lolfId().intValue() == -1) {
					text.append("\n   *Action Lolf.*");
				}
				if (recInfin.valueForKey("tcdCode") == null) {
					text.append("\n   *Type de credit.*");
				}
				text.insert(0, "\n\nATTENTION, il manque des donnees budgetaires dans le devis !\n\n--- Informations a saisir ---");
				text.append("\n\nRenseignez les informations manquantes et validez la prestation dans l'application Pie");
			} else if (mode == DT_PRESTA_NODISP) {
				text.append("\n\nATTENTION, Il n'y a pas de disponible sur la ligne budgetaire affectee au devis !");
				text.append("\n\nSaisissez une autre ligne puis validez la prestation dans l'application Pie");
			}
			text.insert(0, "Une demande de travaux vient d'etre realisee par le service Reprographie (devis n°" + prestNumero.intValue() + ")");
			// text.append("\n  ").append(urlPrestaWeb);//.append(prestNumero.intValue());
			text.append("\n\n");
			text.append(getDTDescription(intervention, true, true, false, true, false, true, true, false));
			text.append("\n\n--- Information relatives a ce message ---\n");
			text.append("Les destinataires de ces messages sont les personnes membres du groupe\n");
			text.append("'Responsables Prestations DT' sous '" + nomService + "'\n");
			text.append("A defaut de membres, ce message sera envoye a toutes les personnes\n");
			text.append("declarees dans sa partie Administration.");
			return _localSendMail(intervention, getDefaultFrom(), mailSec, null, sujet, text.toString());
		}
		return false;
	}

	/**
	 * Renvoie l'adresse mail de l'utilisateur connecte. Il correspond a la valeur
	 * avec la cle "email" dans le dictionnaire de l'utilisateur.
	 */
	private String getDefaultFrom() {
		return userInfo().email();
	}

	/**
	 * Retourne les URL d'acces aux fichiers attaches a une DT
	 * 
	 * @param recDT
	 *          La DT.
	 */
	private String getAttachementsText(NSArray attachements) {
		StringBuffer sb = new StringBuffer();

		if (isEmtyArray(attachements))
			return null;
		for (int i = 0; i < attachements.count(); i++) {
			if (sb.length() > 0)
				sb.append("\n");
			sb.append((String) attachements.objectAtIndex(i));
		}
		return sb.toString();
	}

	/**
	 * Pour utiliser StringCtrl.replaceWithDico(), il faut recuperer les noms de
	 * variable sans les caraceteres '%'. Cette methode enleve ce caractere avant
	 * et apres.
	 * 
	 * @param varName
	 * @return
	 */
	private static String clean(String varName) {
		return StringCtrl.replace(varName, "%", "");
	}

	/**
	 * Ajoute la chaine pour l'envoi d'un message de discussion via SAM. L'adresse
	 * de destination est celle du fichier de config, parametre
	 * <code>APP_SAM_MESSAGE_MAIL</code>. Si cette variable n'est pas renseignee,
	 * alors cette partie sera vide.
	 * 
	 * @param parentKey
	 * @param parentKeyValue
	 * @param out
	 * @param isTop
	 *          : indique si l'action doit etre preciser en debut du mail (pour
	 *          forcer l'utilisateur), ou bien a la fin (pour gadget)
	 * 
	 * @return
	 */
	private void appendDiscussionSAMAction(
			String parentKey, Number parentKeyValue, StringBuffer out, boolean isTop) {
		if (!StringCtrl.isEmpty(appSamMessageMail)) {
			// construire le dico de correspondance
			Hashtable dico = new Hashtable();
			dico.put(clean(VAR_SAM_MAIL), appSamMessageMail);
			dico.put(clean(VAR_PARENT_KEY), parentKey);
			dico.put(clean(VAR_PARENT_KEY_VALUE), parentKeyValue);
			// ajout aux flux
			out.append(StringCtrl.replaceWithDico(
					isTop ? PATTERN_SAM_DISCUSSION_TOP : PATTERN_SAM_DISCUSSION_BOTTOM, dico));
		}
	}

	/**
	 * Initialise la liste des URL vers les documents attaches � une intervention.
	 */
	private void initInterventionFiles() {
		if (intervention != null) {
			NSArray docs = iBus().findDocuments((Number) intervention.valueForKey("intOrdre"), null, true);
			interventionFiles = documentCenter().getDocumentsURL(docs);
		} else {
			interventionFiles = EmptyArray;
		}
	}

	/**
	 * Initialise la liste des URL vers les documents attaches aux traitements.
	 */
	private void initTraitementsFiles() {
		if (!isEmtyArray(traitements)) {
			NSMutableDictionary traFiles = new NSMutableDictionary();
			NSKeyValueCoding traRec;
			NSArray docRecs;
			for (int i = 0; i < traitements.count(); i++) {
				traRec = (NSKeyValueCoding) traitements.objectAtIndex(i);
				docRecs = (NSArray) traRec.valueForKey("tosDocumentDt");
				if (!isEmtyArray(docRecs)) {
					traFiles.takeValueForKey(
							documentCenter().getDocumentsURL(docRecs),
							traRec.valueForKey("traOrdre").toString());
				}
			}
			traitementsFiles = traFiles;
		} else {
			traitementsFiles = EmptyDictionnary;
		}
	}

	/**
	 * Les versions locales d'envoie des messages mail. C'est pour pouvoir gerer
	 * les exceptions et d'autres situations extremes.
	 */
	private boolean _localSendMail(
			NSKeyValueCoding object, String from, String to, String cc, String subject, String text) {
		// memorisation dans la base de données
		if (object != null) {
			NSTimestamp now = DateCtrl.now();
			EOEmail eoEmail = EOEmail.create(
					new EOEditingContext(), now, now);
			eoEmail.setFrom(from);
			eoEmail.setCc(cc);
			eoEmail.setTo(to);
		}
		try {
			return dtSession.mailBus().sendMail(from, to, cc, subject, text);
		} catch (Throwable ex) {
			// dtApp().handleException(ex);
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Renseigner l'objet {@link DTMailCenter} avec les données de la DT pour le
	 * cas d'une cloture
	 */
	public void prepareMailAjoutTraitement(
			EOIntervention recDemande,
			Number traOrdre,
			boolean traite,
			boolean isSendMailsAutomatiquesIfTraite,
			boolean isDTPrestation,
			boolean isSecretaire,
			boolean isInfosBudgetairesCompletes,
			boolean isEnoughDisponible,
			boolean isSendMailToService) {

		// Informer sur le changement des la liste des interventions
		reset();
		setIntervention(recDemande, true);
		// Si la DT est definitivement traitee, on envoi le mail de la cloture
		if (traite) {
			setTraitements(recDemande.tosTraitement(), true, true);
			// cas particulier de la DTRepro, si la secretaire est le
			// demandeur, alors on valide completement la prestation
			// sinon on envoi un mail a la secretaire et on ne valide rien
			if (isSendMailsAutomatiquesIfTraite) {
				if (isDTPrestation) {
					if (isSecretaire) {
						if (isInfosBudgetairesCompletes) {
							if (isEnoughDisponible) {
								// Le demandeur est secretaire, et la cloture de prestation
								// s'est bien passee
								mailCloreDT(DTMailCenter.DT_CLASSIC);
							} else {
								// Le demandeur est secretaire, mais il n'y a plus de sous
								mailCloreDT(DTMailCenter.DT_PRESTA_NODISP);
							}
						} else {
							// secretaire mais manque des infos budgetaires
							mailCloreDT(DTMailCenter.DT_PRESTA_MISSING);
						}
					} else {
						// Le demandeur n'est pas secretaire : verif quoi qu'il en soit
						mailCloreDT(DTMailCenter.DT_PRESTA_VERIF);
					}
				} else {
					// DT classique
					mailCloreDT(DTMailCenter.DT_CLASSIC);
				}
			}
		} else {
			setTraitements(iBus().findTraitements(traOrdre, null, null, null), false, true);
			// setTraitementsFiles(new NSDictionary(attachedFiles,
			// recTraitement.traOrdre().toString()));
			mailAjouterTraitementDT(isSendMailToService);
		}
	}

}
