/*
 * Copyright CRI - Universite de La Rochelle, 1995-2005 
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
import java.util.Vector;

import org.cocktail.dt.server.metier.EODiscussion;
import org.cocktail.dt.server.metier.EOEmail;
import org.cocktail.dt.server.metier.EOIntervention;
import org.cocktail.fwkcktlwebapp.common.CktlLog;
import org.cocktail.fwkcktlwebapp.common.CktlUserInfo;
import org.cocktail.fwkcktlwebapp.common.database.CktlRecord;
import org.cocktail.fwkcktlwebapp.common.util.DateCtrl;
import org.cocktail.fwkcktlwebapp.common.util.GEDDescription;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.cocktail.fwkcktlwebapp.server.database.CktlDataBus;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import fr.univlr.cri.dt.services.common.DTLocalServicesConst;

/**
 * Implementation des services pour les directes actions et Web Services propres
 * a l'application Demande de Travaux.
 * 
 * <p>
 * Cette classe propose une implementation des operations de gestion des
 * demandes : creation, inspection, suppression.
 * </p>
 * 
 * @see DTWebServices
 * @see DTDirectLocalServices
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTLocalServicesImp extends DTServicesImpBase {

	/**
	 * Cree une nouvelle instance de gestionnaire des service de la DT.
	 */
	public DTLocalServicesImp() {
		super();
	}

	// ======= Les methodes d'interface "public" du service =======

	/**
	 * Cree une demande de travaux.
	 * 
	 * <p>
	 * Cette methode cree une demande de travaux en lui specifiant explicitement
	 * tous les parametres. Si les documents attaches sont envoyes avec la
	 * demande, ils doivent etre prealablement enregistres dans GEDFS.
	 * </p>
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param actOrdre
	 *          Le code de l'activite auquelle la demande doit etre associee.
	 * @param noDemandeur
	 *          L'identifiant de la personne pour le nom de laquelle la demande
	 *          est cree (<em>noIndividu</em>).
	 * @param noAppelant
	 *          L'identifiant de la personne qui cree la demande (
	 *          <em>noIndividu</em>). Typiquement, demandeur et appelant est la
	 *          meme personne.
	 * @param motif
	 *          Le motif de la demande. Il contient sa description detaillee.
	 * @param dateSouhaitee
	 *          La date limite jusqu'a laquelle la demande devrait etre realisee.
	 *          C'est une chaine de caracteres en format "JJ/MM/AAAA".
	 * @param noDocuments
	 *          Les numeros des documents attaches au message. C'est une chaine de
	 *          caracteres avec les codes des documents enregistres dans GEDFS.
	 *          Plusieurs codes doivent etre separes par virgule. La chaine peut
	 *          etre <em>null</em> ou vide.
	 * @return Retourne le dictionnaire contenant le code interne de la demande
	 *         (cle "<em>intOrdre</em>") et son numero dans le service (cle "
	 *         <em>intCleService</em>"). Dans le cas d'erreur, retourne la
	 *         description de l'erreur (cle "<em>error</em>").
	 */
	public Hashtable create(String appId,
													int actOrdre, int noDemandeur, int noAppelant,
													String motif, String dateSouhaitee,
													String noDocuments) {
		logStart(appId, noAppelant, "noIndividu", "create");
		sResults.clear();
		if (openSession(new Integer(noAppelant)))
			doCreate(actOrdre, noDemandeur, noAppelant, motif,
								dateSouhaitee, noDocuments, "S", true);
		closeSession();
		logEnd("create");
		return sResults;
	}

	/**
	 * Cree une demande de travaux.
	 * 
	 * <p>
	 * Cette methode est generalement appellee pour creer une demande a partir
	 * d'un message envoye par email. L'activite de la demande sera retrouvee a
	 * partir l'adresse email qui lui est associee dans la configuration des
	 * activites. Si les documents attaches sont envoyes avec la demande, ils
	 * doivent etre prealablement enregistres dans GEDFS.
	 * </p>
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param mailDemandeur
	 *          L'adresse email du demandeur ("from" du message email).
	 * @param mailActivite
	 *          L'adresse email de l'activite associee ("to" du message email).
	 * @param motif
	 *          Le motif de la demande (le contenu du message email).
	 * @param noDocuments
	 *          Les numeros des documents attaches au message. C'est une chaine de
	 *          caracteres avec les codes des documents enregistres dans GEDFS.
	 *          Plusieurs codes doivent etre separes par virgule. La chaine peut
	 *          etre <em>null</em> ou vide.
	 * @return Retourne le dictionnaire contenant le code interne de la demande
	 *         (cle <em>intOrdre</em>) et son numero dans le service (cle
	 *         <em>intCleService</em>). Dans le cas d'erreur, retourne la
	 *         description de l'erreur (cle <em>error</em>).
	 */
	public Hashtable createMail(String appId,
															String mailDemandeur, String mailActivite,
															String motif, String noDocuments) {
		logStart(appId, mailDemandeur, "createMail");
		sResults.clear();
		// On retrouve le demandeur en fonction de son mail. On le fait ici,
		// car il nous faut ouvrir la session avant les autres operations.
		Number noIndividu =
				new DTIndividuBus(new EOEditingContext()).noIndividuForEmail(mailDemandeur);
		CktlLog.trace("noIndividu for '" + mailDemandeur + "' : " + noIndividu);
		if ((noIndividu == null) || (noIndividu.intValue() == -1)) {
			setError("Le demandeur avec l'adresse email \"" + mailDemandeur + "\" est inconnu.");
		} else {
			if (openSession(noIndividu)) {
				doCreateMail(noIndividu, mailActivite, motif, noDocuments);
			}
			closeSession();
		}
		logEnd("createMail");
		return sResults;
	}

	/**
	 * Enregistrer un message.
	 * 
	 * <p>
	 * Cette methode est generalement appellee pour enregistrer un message a
	 * partir d'un message envoye par email. La demande sera retrouvee a partir
	 * des identificateurs contenus dans le titre du mail. Le message peut aussi
	 * concerne directement un traitement ou un autre message, la encore definit
	 * dans le message.
	 * 
	 * Si les documents attaches sont envoyes avec la demande, ils doivent etre
	 * prealablement enregistres dans GEDFS.
	 * </p>
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param mailFrom
	 *          L'adresse email du posteur ("from" du message email).
	 * @param discussionReference
	 *          Le titre du mail (c'est ici qu'il faudra extraire les codes)
	 * @param mailMessage
	 *          Le contenu du message email.
	 * @param noDocuments
	 *          Les numeros des documents attaches au message. C'est une chaine de
	 *          caracteres avec les codes des documents enregistres dans GEDFS.
	 *          Plusieurs codes doivent etre separes par virgule. La chaine peut
	 *          etre <em>null</em> ou vide.
	 * @return Retourne le dictionnaire contenant le code interne du nouveau
	 *         message (cle <em>disOrdre</em>). Dans le cas d'erreur, retourne la
	 *         description de l'erreur (cle <em>error</em>).
	 */
	public Hashtable replyMail(String appId,
			String mailFrom, String discussionReference,
			String mailMessage, String noDocuments) {
		logStart(appId, mailFrom, "replyMail");
		sResults.clear();
		// On retrouve le demandeur en fonction de son mail. On le fait ici,
		// car il nous faut ouvrir la session avant les autres operations.
		Number noIndividu =
				new DTIndividuBus(new EOEditingContext()).noIndividuForEmail(mailFrom);
		CktlLog.trace("noIndividu for '" + mailFrom + "' : " + noIndividu);
		if ((noIndividu == null) || (noIndividu.intValue() == -1)) {
			setError("La personne avec l'adresse email \"" + mailFrom + "\" est inconnue.");
		} else {
			if (openSession(noIndividu)) {
				doReplyMail(noIndividu, discussionReference, mailMessage, noDocuments);
			}
			closeSession();
		}
		logEnd("replyMail");
		return sResults;
	}

	/**
	 * Recupere les informations sur la demande avec le code <code>actOrdre</code>
	 * .
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param intOrdre
	 *          Le code interne de la demande.
	 * @param noIndividu
	 *          L'identifiant de l'utilisateur qui appelle le service.
	 * @return Retourne le dictionnaire avec la description de la demande . Dans
	 *         le cas d'erreur, retourne la description d'erreur (cle "
	 *         <em>error</em> ").
	 */
	public Hashtable inspect(String appId, int intOrdre, int noIndividu) {
		logStart(appId, noIndividu, "noIndividu", "inspect");
		sResults.clear();
		if (openSession(new Integer(noIndividu)))
			doInspect(intOrdre);
		closeSession();
		logEnd("inspect");
		return sResults;
	}

	/**
	 * Supprime la demande avec le code <code>intOrdre</code>. La suppression est
	 * effectuee si la personne <code>noIndividu</code> est autorisee a supprimer
	 * les demandes, si la demande n'est pas encore definitivement traitee et si
	 * elle n'a aucun traitement associe.
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param intOrdre
	 *          Le code interne de la demande.
	 * @param noIndividu
	 *          L'identifiant de l'utilisateur qui appelle le service.
	 * @return Retourne le dictionnaire vide dans le cas de succes de l'operation.
	 *         Sinon, retourne la description de l'erreur (cle "<em>error</em> ").
	 */
	public Hashtable delete(String appId, int intOrdre, int noIndividu) {
		logStart(appId, noIndividu, "noIndividu", "delete");
		sResults.clear();
		if (openSession(new Integer(noIndividu))) {
			dtDataCenter().performDelete(intOrdre, noIndividu);
			if (dtDataCenter().hasError())
				setError(dtDataCenter().errorMessage());
			else
				DTLogger.logDelete(appId, intOrdre, noIndividu);
		}
		closeSession();
		logEnd("delete");
		return sResults;
	}

	/**
	 * Retourne la liste des adresses email de toutes les activites actuellement
	 * valides. Une activite est valide si elle et sont service sont
	 * "affichables", et si son service est consultable sur le Web.
	 * 
	 * <p>
	 * La liste des adresses est retournee sous forme d'une chaine de caracteres
	 * ou les adresses sont separees par ",".
	 * </p>
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param mailDemandeur
	 *          L'adresse email de la personne qui demande executer cette action.
	 * 
	 * @return Retourne le dictionnaire avec la liste des adresses enregistree
	 *         avec la clee <code>DTLocalServicesConst.FormMailActiviteKey</code>.
	 *         Dans le cas d'erreur, retourne la description d'erreur (cle
	 *         "<i>error</i>").
	 */
	public Hashtable activitesMails(String appId, String mailDemandeur) {
		logStart(appId, mailDemandeur, "activitesMails");
		sResults.clear();
		// On retrouve le demandeur en fonction de son mail. On le fait ici,
		// car il nous faut ouvrir la session avant les autres operations.
		Number noIndividu =
				new DTIndividuBus(new EOEditingContext()).noIndividuForEmail(mailDemandeur);
		CktlLog.trace("noIndividu for '" + mailDemandeur + "' : " + noIndividu);
		if ((noIndividu == null) || (noIndividu.intValue() == -1)) {
			setError("Le demandeur avec l'adresse email \"" + mailDemandeur + "\" est inconnu.");
		} else {
			if (openSession(noIndividu))
				doActivitesMail();
			closeSession();
		}
		logEnd("activitesMails");
		return sResults;
	}

	/**
	 * Retourne les informations sur l'utilisateur ayant l'adresse email.
	 * 
	 * <p>
	 * L'implementation actuelle retourne le numero de l'individu uniquement (la
	 * cle <code>DTLocalServicesConst.FormNoIndividuKey</code>).
	 * </p>
	 * 
	 * @param appId
	 *          L'identifiant de l'application qui appel le service.
	 * @param mailDemandeur
	 *          L'adresse email de la personne dont les informations sont
	 *          recherchees.
	 * 
	 * @return Retourne le dictionnaire avec les informations utilisateur. Dans le
	 *         cas d'erreur, retourne la description d'erreur (cle
	 *         "<i>error</i>").
	 */
	public Hashtable userForMail(String appId, String mailDemandeur) {
		logStart(appId, mailDemandeur, "userForMail");
		sResults.clear();
		// On retrouve le demandeur en fonction de son mail. On le fait ici,
		// car il nous faut ouvrir la session avant les autres operations.
		Number noIndividu =
				new DTIndividuBus(new EOEditingContext()).noIndividuForEmail(mailDemandeur);
		CktlLog.trace("noIndividu for '" + mailDemandeur + "' : " + noIndividu);
		if ((noIndividu == null) || (noIndividu.intValue() == -1)) {
			setError("Le demandeur avec l'adresse email \"" + mailDemandeur + "\" est inconnu.");
		} else {
			sResults.put(DTLocalServicesConst.FormNoIndividuKey, noIndividu);
		}
		logEnd("userForMail");
		return sResults;
	}

	/**
	 * Teste si le support des services est disponible. Les applications peuvent
	 * appeler cette methode pour verifier que l'application repond bien aux
	 * services.
	 * 
	 * <p>
	 * Cette methode retourne le dictionnaire avec la description du
	 * service&nbsp;:
	 * <ul>
	 * <li><code>DTLocalServicesConst.FormAppIDKey</code> - la version de
	 * l'application. Cette cle donne une courte description de
	 * l'application&nbsp;;</li>
	 * <li><code>DTLocalServicesConst.FormAppIDKey</code> - la version des
	 * services Web fournis par l'application. Cette cle donne la valeur flotant
	 * (<i>float</i>) presente sous forme d'une chaine de caracteres.</li>
	 * </ul>
	 * </p>
	 */
	public Hashtable check() {
		// Pas de logs...
		sResults.clear();
		String version = dtApp().config().stringForKey("APP_ALIAS");
		if (version == null)
			version = "DT Web, v" + dtApp().version();
		sResults.put(DTLocalServicesConst.FormAppIDKey, version);
		sResults.put(
				DTLocalServicesConst.FormWSVersionKey, Float.toString(dtApp().wsDTVersion()));
		return sResults;
	}

	// ======= Les methode "internes" =======

	/**
	 * Effectue l'operation d'enregistrement des donnees dans la base de donnees,
	 * mise a jour des references vers les documents attaches, envoi d'email suite
	 * a la creation d'une demande.
	 * 
	 * <p>
	 * Avant l'appel de cette methode, il faut initiailser la session avec
	 * <code>noAppelant</code> comme l'individu connecte.
	 * </p>
	 * 
	 * <p>
	 * Le <code>noDocuments</code> est une suite des numeros des documents separes
	 * par virgule.
	 * </p>
	 */
	private void doCreate(int actOrdre, int noDemandeur, int noAppelant,
												String motif, String dateSouhaitee,
												String noDocuments, String mode, boolean checkForSize) {
		NSMutableDictionary dataDico = new NSMutableDictionary();
		// -- Remplissage de dictionnaire des donnees --
		// Le motif
		// On verifie que le motif ne depasse pas la longeur autorise
		if (checkForSize) {
			if (!checkForMotifLength(motif))
				return;
		}
		// On verifie le demandeur et l'appelant
		// La session doit deja etre initialisee avec les infos de l'appelant
		CktlUserInfo uInfo = dtSession().connectedUserInfo();
		if (uInfo.hasError()) {
			setError(uInfo.errorMessage());
			return;
		}
		dataDico.setObjectForKey(uInfo.noIndividu(), "intNoIndAppelant");
		// ...demandeur
		if (noDemandeur != noAppelant) {
			uInfo.individuForNoIndividu(new Integer(noDemandeur), true);
			if (uInfo.hasError()) {
				setError(uInfo.errorMessage());
				return;
			}
		}
		dataDico.setObjectForKey(uInfo.noIndividu(), "intNoIndConcerne");
		// On verifie l'activite
		CktlRecord rec = dtDataCenter().activiteBus().findVActivite(new Integer(actOrdre));
		if (rec == null) {
			setError("L'activite pour cette demande n'existe pas");
			return;
		}
		dataDico.setObjectForKey(rec.valueForKey("actOrdre"), "actOrdre");
		dataDico.setObjectForKey(rec.valueForKey("cStructure"), "cStructure");
		dataDico.setObjectForKey(
				dtDataCenter().activiteBus().findActivitePathString(rec.numberForKey("actOrdre"), true, ";"), "intMotsClefs");
		// Les dates
		NSTimestamp nowDate = DateCtrl.now();
		NSTimestamp souhDate;
		if (StringCtrl.normalize(dateSouhaitee).length() > 0)
			souhDate = DateCtrl.stringToDate(dateSouhaitee);
		else
			souhDate = nowDate.timestampByAddingGregorianUnits(0, 0, 10, 0, 0, 0);
		dataDico.setObjectForKey(nowDate, "intDateCreation");
		dataDico.setObjectForKey(DTDataBus.nullValue(), "intDateButoir");
		dataDico.setObjectForKey(souhDate, "intDateSouhaite");
		// ...sinon, OK
		dataDico.setObjectForKey(StringCtrl.normalize(motif), "intMotif");
		dataDico.setObjectForKey(
				dtDataCenter().activiteBus().newEtatCodeForActivite(new Integer(actOrdre)), "intEtat");
		// Les infos sur le contact
		rec = dtDataCenter().contactsBus().findDefaultContact(uInfo.noIndividu(), true);
		if (rec != null) {
			dataDico.setObjectForKey(
					rec.valueForKey("cStructService") == null ? "-1" : rec.valueForKey("cStructService"),
					"intServiceConcerne");
			dataDico.setObjectForKey(
					rec.valueForKey("cLocal") == null ? "-1" : rec.valueForKey("cLocal"),
					"cLocal");
			dataDico.setObjectForKey(rec.valueForKey("ctOrdre"), "ctOrdre");
		} else { // Sinon, toutes les references de la localisation sont vides
			dataDico.setObjectForKey("-1", "intServiceConcerne");
			dataDico.setObjectForKey("-1", "cLocal");
			dataDico.setObjectForKey(DTDataBus.nullValue(), "ctOrdre");
		}
		// Autres informations - Par defaut
		dataDico.setObjectForKey(StringCtrl.emptyString(), "intCommentaireInterne");
		dataDico.setObjectForKey(new Integer(9), "intPriorite");
		dataDico.setObjectForKey(mode, "modCode");
		//
		// Sauvegarde de donnees
		CktlLog.trace("saveDataDico : " + dataDico);
		dtSession().dataBus().beginTransaction();
		if (!(dtSession().dataBus().executeProcedure("insIntervention", dataDico))) {
			dtSession().dataBus().rollbackTransaction();
			setError((String) dtSession().dataBus().executedProcResult().objectForKey(CktlDataBus.ERROR_KEY));
			return;
		}
		// Si tout est OK, on recupere le numero de la nouvelle DT
		sResults.putAll(dtSession().dataBus().executedProcResult().hashtable());
		dtSession().dataBus().commitTransaction();
		NSMutableArray paths = new NSMutableArray();
		// Sauvegarde des documents attaches
		noDocuments = StringCtrl.normalize(noDocuments);
		if (noDocuments.length() > 0) {
			boolean hasErrors = false;
			Integer transId = dtDataCenter().interventionBus().beginECTransaction();
			Vector allNo = StringCtrl.toVector(noDocuments, ",", true);
			Number no;
			GEDDescription desc;
			for (int i = 0; i < allNo.size(); i++) {
				no = StringCtrl.toInteger((String) allNo.elementAt(i), -1);
				if (!dtDataCenter().interventionBus().addDocumentDt(
							transId, no, (Number) sResults.get("intOrdre"), null, null)) {
					hasErrors = true;
					break;
				}
				// On recupere le chemin d'acces au document
				desc = dtSession().gedBus().inspectDocumentGED(no.intValue());
				if ((desc.documentId != -1) && (desc.reference.length() > 0))
					paths.addObject(desc.reference);
			}
			if (hasErrors) {
				dtDataCenter().individuBus().rollbackECTrancsacition(transId);
				setError("Les fichiers attaches n'ont pas pu etre enregistres");
				return;
			}
			dtDataCenter().interventionBus().commitECTrancsacition(transId);
		}
		// Envoi du mail au service
		dtSession().mailCenter().reset();
		CktlLog.trace("Session.context() : " + dtSession().context());
		dataDico.takeValueForKey(sResults.get("intCleService"), "intCleService");
		dataDico.takeValueForKey(sResults.get("intOrdre"), "intOrdre");
		dtSession().mailCenter().setIntervention(dataDico, false);
		dtSession().mailCenter().setInterventionFiles(paths);
		if (!dtSession().mailCenter().mailCreerDT())
			setError("Le mail au service concerne n'a pas pu etre envoye");
		// Final log
		DTLogger.logCreation(
				dataDico.valueForKey("intCleService"),
				dataDico.valueForKey("intOrdre"),
				dataDico.valueForKey("cStructure"),
				uInfo.login(),
				uInfo.noIndividu());
	}

	/**
	 * Recupere les informations sur la demande avec le code interne
	 * <code>intOrdre</code> et les enregistre dans le tableau de resultat.
	 */
	private void doInspect(int intOrdre) {
		CktlRecord rec =
				dtDataCenter().interventionBus().findIntervention(null, new Integer(intOrdre));
		if (rec == null)
			setError("Demande inconnue");
		else
			sResults = CktlRecord.recordToHashtable(rec, true);
	}

	/**
	 * Effectue la creation d'une demande a partir des infos donnees. Cette
	 * methode est utilisee pour creer une demande a partir d'un message email.
	 */
	private void doCreateMail(Number noIndividu, String mailActivite,
														String motif, String noDocuments) {
		// On verifie que le motif ne depasse pas la longeur autorise.
		if (!checkForMotifLength(motif))
			return;
		// La date de realisation souhaitee
		String dateSouhaite = DateCtrl.dateToString(
					DateCtrl.now().timestampByAddingGregorianUnits(0, 0, 10, 0, 0, 0));
		// On retrouve l'activite en fonction d'email
		Number actOrdre = dtDataCenter().activiteBus().findActiviteForMail(mailActivite, true);
		if (actOrdre == null) {
			setError("L'activite pour l'adresse email \"" + mailActivite + "\" n'existe pas.");
			return;
		}
		// On effetue la creation classique
		doCreate(actOrdre.intValue(), noIndividu.intValue(),
				noIndividu.intValue(), motif, dateSouhaite, noDocuments, "S", false);
	}

	/**
	 * Enregistrer un message a partir des infos donnees. Cette methode est
	 * utilisee pour faire une reponse a partir d'un message email.
	 */
	private void doReplyMail(
			Number noIndividu, String discussionReference, String mailMessage, String noDocuments) {

		// On verifie le demandeur et l'appelant
		// La session doit deja etre initialisee avec les infos de l'appelant
		CktlUserInfo uInfo = dtSession().connectedUserInfo();
		if (uInfo.hasError()) {
			setError(uInfo.errorMessage());
			return;
		}

		// On verifie que le message ne depasse pas la longeur autorise.
		// TODO faire la verification sur l'attribut de l'entite Discussion
		if (!checkForMotifLength(mailMessage))
			return;

		// extraire les identifiants du titre du message
		CktlLog.log("discussionReference=" + discussionReference);

		//
		EOEmail eoEmail = EOEmail.fetchByKeyValue(
				new EOEditingContext(), EOEmail.MESSAGE_ID_KEY, discussionReference);

		// sauvegarde du message
		Number disOrdre = dtDataCenter().interventionBus().addDiscussion(
				null, eoEmail.intOrdre(), eoEmail.traOrdre(), eoEmail.disOrdre(), noIndividu, mailMessage, DateCtrl.now());
		// memoriser le numero généré
		sResults.put(DTLocalServicesConst.FormDisOrdreKey, disOrdre);

		// Sauvegarde des documents attaches
		NSMutableArray paths = new NSMutableArray();
		noDocuments = StringCtrl.normalize(noDocuments);
		if (noDocuments.length() > 0) {
			boolean hasErrors = false;
			Integer transId = dtDataCenter().interventionBus().beginECTransaction();
			Vector allNo = StringCtrl.toVector(noDocuments, ",", true);
			Number no;
			GEDDescription desc;
			for (int i = 0; i < allNo.size(); i++) {
				no = StringCtrl.toInteger((String) allNo.elementAt(i), -1);
				if (!dtDataCenter().interventionBus().addDocumentDt(
							transId, no, (Number) sResults.get(EODiscussion.INT_ORDRE_KEY), null, null)) {
					hasErrors = true;
					break;
				}
				// On recupere le chemin d'acces au document
				desc = dtSession().gedBus().inspectDocumentGED(no.intValue());
				if ((desc.documentId != -1) && (desc.reference.length() > 0))
					paths.addObject(desc.reference);
			}
			if (hasErrors) {
				dtDataCenter().individuBus().rollbackECTrancsacition(transId);
				setError("Les fichiers attaches n'ont pas pu etre enregistres");
				return;
			}
			dtDataCenter().interventionBus().commitECTrancsacition(transId);
		}

		// Envoi du mail aux personnes interessees
		// TODO

		// Recuperation de l'enregistrement dans la base de donn�es
		EOIntervention rec =
				dtDataCenter().interventionBus().findIntervention(null, eoEmail.intOrdre());

		// Final log
		DTLogger.logReply(
				disOrdre,
				rec.intCleService(),
				eoEmail.intOrdre(),
				eoEmail.traOrdre(),
				eoEmail.disOrdre(),
				rec.cStructure(),
				uInfo.login(),
				uInfo.noIndividu());
	}

	/**
	 * Recupere la liste des activites pour lesquels les demandes peuvent etre
	 * creees par email.
	 */
	private void doActivitesMail() {
		StringBuffer mailString = new StringBuffer();
		NSArray mails =
				dtDataCenter().activiteBus().findAllActivitesMails(false, false, true, null);
		CktlLog.trace("mails : " + mails);
		for (int i = 0; i < mails.count(); i++) {
			if (mailString.length() > 0)
				mailString.append(", ");
			mailString.append(mails.objectAtIndex(i));
		}
		sResults.put(DTLocalServicesConst.FormMailActiviteKey, mailString.toString());
	}

	/**
	 * Teste si le texte <code>motif</code> ne depasse pas la longueur maximale
	 * autorisee pour le champ "motif" de l'entite des demandes dans la base de
	 * donnees.
	 */
	private boolean checkForMotifLength(String motif) {
		if (motif != null) {
			int maxLen = CktlRecord.maxLengthForAttribute("Intervention", "intMotif");
			if (motif.length() >= maxLen) {
				setError("La longueur de motif de la demande depasse la longueur maximale autorisee (" + maxLen + " caracteres)");
				return false;
			}
		}
		if ((motif == null) || (motif.length() == 0)) {
			setError("Le motif de la demande est vide");
			return false;
		}
		return true;
	}
}
