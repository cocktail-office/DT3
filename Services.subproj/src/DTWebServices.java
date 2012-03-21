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

import com.webobjects.appserver.WOWebServiceUtilities;

import fr.univlr.cri.dt.services.common.DTLocalServicesConst;

/**
 * Implemente les methodes pour les services Web offerts par l'application
 * demande de travaux.
 * 
 * <p>
 * Les methodes de cette classe font appel a celles de <code>DTServices</code>
 * pour realisation des actions.
 * </p>
 * 
 * @see DTServicesImpBase
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTWebServices {

	/** Le nom donne au service Demande de Travaux */
	public static final String ServiceExternalName = "dtws";

	/** La liste des noms des methodes publiees dans le service Web */
	public static final String[] ServiceMethods = {
			DTLocalServicesConst.ActionReplyMailName,
			DTLocalServicesConst.ActionCreateMailName,
			DTLocalServicesConst.ActionCreateName,
			DTLocalServicesConst.ActionInspectName,
			DTLocalServicesConst.ActionDeleteName,
			DTLocalServicesConst.ActionActivitesMailsName,
			DTLocalServicesConst.ActionUserForMailName,
			DTLocalServicesConst.ActionCheckName };

	/**
	 * Retourne l'instance de l'objet qui realise les actions du service.
	 */
	private DTLocalServicesImp servicePerformer() {
		DTLocalServicesImp sp = new DTLocalServicesImp();
		sp.setServiceKey("ws DT"); // Le type de service
		sp.setContext(WOWebServiceUtilities.currentWOContext());
		return sp;
	}

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
	 * 
	 * @return Retourne le dictionnaire contenant le code interne de la demande
	 *         (cle "<em>intOrdre</em>") et son numero dans le service (cle "
	 *         <em>intCleService</em>"). Dans le cas d'erreur, retourne la
	 *         description de l'erreur (cle "<em>error</em>").
	 */
	public Hashtable create(String appId,
													int actOrdre, int noDemandeur, int noAppelant,
													String motif, String dateSouhaitee,
													String noDocuments) {
		return servicePerformer().create(
				appId, actOrdre, noDemandeur, noAppelant, motif, dateSouhaitee, noDocuments);
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
	 * 
	 * @return Retourne le dictionnaire contenant le code interne de la demande
	 *         (cle <em>intOrdre</em>) et son numero dans le service (cle
	 *         <em>intCleService</em>). Dans le cas d'erreur, retourne la
	 *         description de l'erreur (cle <em>error</em>).
	 */
	public Hashtable createMail(String appId,
															String mailDemandeur, String mailActivite,
															String motif, String noDocuments) {
		return servicePerformer().createMail(
				appId, mailDemandeur, mailActivite, motif, noDocuments);
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
		return servicePerformer().replyMail(
				appId, mailFrom, discussionReference, mailMessage, noDocuments);
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
	 * 
	 * @return Retourne le dictionnaire avec la description de la demande . Dans
	 *         le cas d'erreur, retourne la description d'erreur (cle
	 *         <em>error</em>).
	 */
	public Hashtable inspect(String appId, int intOrdre, int noIndividu) {
		return servicePerformer().inspect(appId, intOrdre, noIndividu);
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
	 * 
	 * @return Retourne le dictionnaire vide dans le cas de succes de l'operation.
	 *         Sinon, retourne la description de l'erreur (cle <em>error</em>).
	 */
	public Hashtable delete(String appId, int intOrdre, int noIndividu) {
		return servicePerformer().delete(appId, intOrdre, noIndividu);
	}

	/**
	 * Retourne la liste des adresses email de toutes les activites actuellement
	 * valides. Une activite est valide si elle et sont service est "affichable",
	 * et si son service est consultable sur le Web.
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
		return servicePerformer().activitesMails(appId, mailDemandeur);
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
		return servicePerformer().userForMail(appId, mailDemandeur);
	}

	/**
	 * Teste si le support des services est disponible. Les applications peuvent
	 * appeler cette methode pour verifier que l'application repond bien aux
	 * services.
	 * 
	 * <p>
	 * Cette methode retourne le dictionnaire avec la description du service (cle
	 * <em>dtServices</em>).
	 * </p>
	 */
	public Hashtable check() {
		return servicePerformer().check();
	}
}
