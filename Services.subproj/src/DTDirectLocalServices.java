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

import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import fr.univlr.cri.dt.services.common.DTLocalServicesConst;
import fr.univlr.cri.dt.services.common.DTServicesRequestCoder;

/**
 * Implemente les directs actions de l'application demande de travaux. Cette
 * classe propose les directes actions equivalentes aux services Web offerts par
 * la classe <code>DTWebServices</code>.
 * 
 * <p>
 * Les methodes de cette classe font appel a celles de <code>DTServices</code>
 * pour realisation des actions.
 * </p>
 * 
 * <p>
 * Les methodes des directes actions traitent les messages envoyes en mode
 * HTTP-POST. Les valeurs des parametres doivent etre encodes en format UTF-8
 * </p>
 * 
 * <p>
 * La reponse de chaque action est envoye comme un document en format
 * "properties" de Java. Chaque ligne de ce document est composee de couple
 * "cle=valeur". Ce resultat peut ensuite etre lu a l'aide de la classe
 * <code>Properties</code>.
 * </p>
 * 
 * @see DTServicesImpBase
 * @see DTWebServices
 * @see java.util.Properties
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTDirectLocalServices extends DTDirectServicesBase {

	/**
	 * Cree une nouvelle instance de l'objet.
	 */
	public DTDirectLocalServices(WORequest request) {
		super(request);
	}

	/**
	 * Retourne l'instance de l'objet qui donne une implementation concrete de la
	 * gestion de donnees demandee par les actions.
	 */
	private DTLocalServicesImp servicePerformer() {
		DTLocalServicesImp sp = new DTLocalServicesImp();
		sp.setServiceKey("wa DT");
		sp.setContext(context());
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
	 * <p>
	 * Les parametres doivent etre passes en utilisant la methode HTTP-POST. Les
	 * valeurs des parametres doivent etre codees en UTF-8. Les parametres
	 * suivants sont attendus par la methode&nbsp;:
	 * </p>
	 * 
	 * <ul>
	 * <li><em>appId</em> - L'identifiant de l'application qui appel le service.</li>
	 * <li><em>actOrdre</em> - Le code de l'activite auquelle la demande doit etre
	 * associee.</li>
	 * <li><em>noDemandeur</em> - L'identifiant de la personne pour le nom de
	 * laquelle la demande est cree (<em>noIndividu</em>).</li>
	 * <li><em>noAppelant</em> - L'identifiant de la personne qui cree la demande
	 * (<em>noIndividu</em>). Typiquement, demandeur et appelant est la meme
	 * personne.</li>
	 * <li><em>motif</em> - Le motif de la demande. Il contient sa description
	 * detaillee.</li>
	 * <li><em>dateSouhaitee</em> - La date limite jusqu'a laquelle la demande
	 * devrait etre realisee. C'est une chaine de caracteres en format
	 * "JJ/MM/AAAA".</li>
	 * <li><em>noDocuments</em> - Les numeros des documents attaches au message.
	 * C'est une chaine de caracteres avec les codes des documents enregistres
	 * dans GEDFS. Plusieurs codes doivent etre separes par virgule. La chaine
	 * peut etre <em>null</em> ou vide.</li>
	 * </ul>
	 * 
	 * <p>
	 * La reponse retournee par la methode est compose du code interne de la
	 * demande (cle "<i>intOrdre</i>") et son numero dans le service (cle
	 * "<i>intCleService</i>"). Dans le cas d'erreur, retourne la description de
	 * l'erreur (cle "<i>error</i>").
	 * </p>
	 */
	public WOActionResults createAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			int actOrdre =
					params.getInt(DTLocalServicesConst.FormActOrdreKey);
			int noDemandeur =
					params.getInt(DTLocalServicesConst.FormNoDemandeurKey);
			int noAppelant =
					params.getInt(DTLocalServicesConst.FormNoAppelantKey);
			String motif =
					params.getString(DTLocalServicesConst.FormMotifKey);
			String dateSouhaitee =
					params.getString(DTLocalServicesConst.FormDateSouhaiteKey);
			String noDocuments =
					params.getString(DTLocalServicesConst.FormNoDocumentsKey);
			// Appeler le service
			Hashtable result = servicePerformer().create(
					appId, actOrdre, noDemandeur, noAppelant,
					motif, dateSouhaitee, noDocuments);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
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
	 * <p>
	 * Les parametres doivent etre passes en utilisant la methode HTTP-POST. Les
	 * valeurs des parametres doivent etre codees en UTF-8. Les parametres
	 * suivants sont attendus par la methode&nbsp;:
	 * </p>
	 * 
	 * <ul>
	 * <li><em>appId</em> - L'identifiant de l'application qui appel le service.</li>
	 * <li><em>mailDemandeur</em> - L'adresse email du demandeur ("from" du
	 * message email).</li>
	 * <li><em>mailActivite</em> - L'adresse email de l'activite associee ("to" du
	 * message email).</li>
	 * <li><em>motif</em> - Le motif de la demande (le contenu du message email).</li>
	 * <li><em>noDocuments</em>- Les numeros des documents attaches au message.
	 * C'est une chaine de caracteres avec les codes des documents enregistres
	 * dans GEDFS. Plusieurs codes doivent etre separes par virgule. La chaine
	 * peut etre <em>null</em> ou vide.</li>
	 * </ul>
	 * 
	 * <p>
	 * La reponse retournee par la methode est compose du code interne de la
	 * demande (cle "<i>intOrdre</i>") et son numero dans le service (cle
	 * "<i>intCleService</i>"). Dans le cas d'erreur, retourne la description de
	 * l'erreur (cle "<i>error</i>").
	 * </p>
	 */
	public WOActionResults createMailAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			String mailDemandeur =
					params.getString(DTLocalServicesConst.FormMailDemandeurKey);
			String mailActivite =
					params.getString(DTLocalServicesConst.FormMailActiviteKey);
			String motif =
					params.getString(DTLocalServicesConst.FormMotifKey);
			String noDocuments =
					params.getString(DTLocalServicesConst.FormNoDocumentsKey);
			// Appeler le service
			Hashtable result = servicePerformer().createMail(
					appId, mailDemandeur, mailActivite, motif, noDocuments);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
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
	public WOActionResults replyMailAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			String mailFrom =
					params.getString(DTLocalServicesConst.FormMailFromKey);
			String discussionReference =
					params.getString(DTLocalServicesConst.FormDiscussionReferenceKey);
			String mailMessage =
					params.getString(DTLocalServicesConst.FormMailMessageKey);
			String noDocuments =
					params.getString(DTLocalServicesConst.FormNoDocumentsKey);
			// Appeler le service
			Hashtable result = servicePerformer().replyMail(
					appId, mailFrom, discussionReference, mailMessage, noDocuments);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}

	/**
	 * Recupere les informations sur la demande avec le code <code>intOrdre</code>
	 * .
	 * 
	 * <ul>
	 * <li><em>appId</em> - L'identifiant de l'application qui appel le service.</li>
	 * <li><em>intOrdre</em> - Le code interne de la demande.</li>
	 * <li><em>noIndividu</em> - L'identifiant de l'utilisateur qui appelle le
	 * service.</li>
	 * </ul>
	 * 
	 * <p>
	 * La reponse retournee par la methode contient la description de la demande.
	 * Dans le cas d'erreur, retourne la description de l'erreur (cle
	 * "<i>error</i>").
	 * </p>
	 */
	public WOActionResults inspectAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			int intOrdre =
					params.getInt(DTLocalServicesConst.FormIntOrdreKey);
			int noIndividu =
					params.getInt(DTLocalServicesConst.FormNoIndividuKey);
			// Appeler le service
			Hashtable result = servicePerformer().inspect(appId, intOrdre, noIndividu);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}

	/**
	 * Supprime la demande indiquee. La suppression est effectuee si la personne
	 * est autorisee a supprimer les demandes, si la demande n'est pas encore
	 * definitivement traitee et si elle n'a aucun traitement associe.
	 * 
	 * <ul>
	 * <li><i>appId</i> - L'identifiant de l'application qui appel le service.</li>
	 * <li><i>intOrdre</i> - Le code interne de la demande.</li>
	 * <li><i>noIndividu</i> - L'identifiant de l'utilisateur qui appelle le
	 * service.</li>
	 * </ul>
	 * 
	 * <p>
	 * Retourne la reponse vide dans le cas de succes de l'operation. Sinon,
	 * retourne la description de l'erreur (cle "<i>error</i>").
	 * </p>
	 */
	public WOActionResults deleteAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			int intOrdre =
					params.getInt(DTLocalServicesConst.FormIntOrdreKey);
			int noIndividu =
					params.getInt(DTLocalServicesConst.FormNoIndividuKey);
			// Appeler le service
			Hashtable result = servicePerformer().delete(appId, intOrdre, noIndividu);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}

	/**
	 * Retourne la liste des adresses email des activites valides.
	 * 
	 * <p>
	 * Une activite est valide si elle et sont service sont "affichables", et si
	 * son service est consultable sur le Web. La liste des adresses est retournee
	 * sous forme d'une chaine de caracteres ou les adresses sont separees par
	 * ",".
	 * </p>
	 * 
	 * <ul>
	 * <li><em>appId</em> - L'identifiant de l'application qui appel le service.</li>
	 * <li><em>mailDemandeur</em> - L'adresse email de la personne qui demande
	 * executer cette action.</li>
	 * </ul>
	 * 
	 * <p>
	 * Cette methode retourne la liste des adresses sous forme d'une chaine de
	 * caracteres ou les adresses sont separees par ",". Cette valeur est
	 * retournee avec le code "mailActivite".
	 * </p>
	 */
	public WOActionResults activitesMailsAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			String mailDemandeur =
					params.getString(DTLocalServicesConst.FormMailDemandeurKey);
			// Appeler le service
			Hashtable result = servicePerformer().activitesMails(
					appId, mailDemandeur);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}

	/**
	 * Retourne les informations sur l'utilisateur ayant l'adresse email.
	 * 
	 * <ul>
	 * <li><em>appId</em> - L'identifiant de l'application qui appel le service.</li>
	 * <li><em>mailDemandeur</em> - L'adresse email de la personne dont les
	 * informations sont recherchees.</li>
	 * </ul>
	 * 
	 * <p>
	 * L'implementation actuelle retourne le numero de l'individu uniquement (la
	 * cle <code>DTLocalServicesConst.FormNoIndividuKey</code>).
	 * </p>
	 */
	public WOActionResults userForMailAction() {
		CktlDataResponse response;
		DTServicesRequestParams params =
				new DTServicesRequestParams(context().request());
		try {
			// Recupere les parametres de l'action (valeurs de formulaire)
			String appId =
					params.getString(DTLocalServicesConst.FormAppIDKey);
			String mailDemandeur =
					params.getString(DTLocalServicesConst.FormMailDemandeurKey);
			// Appeler le service
			Hashtable result = servicePerformer().userForMail(
					appId, mailDemandeur);
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(DTServicesRequestCoder.encodeParams(result));
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}

	/**
	 * Teste si le support des services est disponible. Les applications peuvent
	 * appeler cette methode pour verifier que l'application repond bien aux
	 * services.
	 * 
	 * <p>
	 * Cette methode retourne la reponse avec la description de service (les cles
	 * <code>DTLocalServicesConst.FormAppIDKey</code> et
	 * <code>DTLocalServicesConst.FormWSVersionKey</code>).
	 * </p>
	 */
	public WOActionResults checkAction() {
		CktlDataResponse response;
		try {
			// Appeler le service
			Hashtable result = servicePerformer().check();
			// Generer la reponse en fonction du resultat
			response = new CktlDataResponse();
			response.setContent(
					DTServicesRequestCoder.encodeParams(result).getBytes(),
					CktlDataResponse.MIME_TXT);
		} catch (Throwable ex) {
			response = getResponseForException(ex);
		}
		return response;
	}
}
