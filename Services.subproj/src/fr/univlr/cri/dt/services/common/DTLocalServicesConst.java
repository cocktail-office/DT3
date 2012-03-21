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
package fr.univlr.cri.dt.services.common;

/* =======================================================================
 * L'original de cette classe se trouve dans les sources de l'application
 * Demande de Travaux Web. 
 * ======================================================================= */

/**
 * Les constantes utilisees dans l'implementation de service propres a
 * l'application de la demande de travax. Elles indiquent les noms des
 * attributs, des elements des forumulaires HTTP, les valeurs des parametres.
 * Les memes constantes doivent etre utilisees de cote client et serveur.
 * 
 * <p>
 * Cette classe ne contient que les constantes.
 * </p>
 * 
 * @author Arunas STOCKUS <arunas.stockus at univ-lr.fr>
 */
public class DTLocalServicesConst extends DTServicesConst {
	// Les noms des actions directes
	/** Le nom du service */
	public static final String ServiceName = "DTDirectLocalServices";
	/** Le nom de l'action de creation d'une demande */
	public static final String ActionCreateName = "create";
	/** Le nom de l'action de creation d'une demande par email */
	public static final String ActionCreateMailName = "createMail";
	/** Le nom de l'action de discussion par mail */
	public static final String ActionReplyMailName = "replyMail";
	/** Le nom de l'action d'inspection d'une demande */
	public static final String ActionInspectName = "inspect";
	/** Le nom de l'action de suppression d'une demande */
	public static final String ActionDeleteName = "delete";
	/**
	 * Le nom de l'action de test de disponibilite des services demande de travaux
	 */
	public static final String ActionCheckName = "check";
	/** Le nom de l'action de reception des adresses email des activites */
	public static final String ActionActivitesMailsName = "activitesMails";
	/** Le nom de l'action de demande des informations utilisateur */
	public static final String ActionUserForMailName = "userForMail";

	// Les noms des parametres dans les formulaires pour les direct actions
	/** Cle formulaire : l'identifiant de l'application */
	public static final String FormAppIDKey = "appId";
	/** Cle formulaire : le code de l'activite pour la demande */
	public static final String FormActOrdreKey = "actOrdre";
	/** Cle formulaire : l'identifiant du demandeur */
	public static final String FormNoDemandeurKey = "noDemandeur";
	/** Cle formulaire : l'identifiant de la personne appelante */
	public static final String FormNoAppelantKey = "noAppelant";
	/** Cle formulaire : le motif de la demande */
	public static final String FormMotifKey = "motif";
	/** Cle formulaire : la date de realisation souhaite */
	public static final String FormDateSouhaiteKey = "dateSouhaitee";
	/** Cle formulaire : les identifiants des documents attaches */
	public static final String FormNoDocumentsKey = "noDocuments";
	/** Cle formulaire : l'adresse email du demandeur */
	public static final String FormMailDemandeurKey = "mailDemandeur";
	/** Cle formulaire : l'adresse email de l'activite de la demande */
	public static final String FormMailActiviteKey = "mailActivite";
	/** Cle formulaire : l'identifiant d'une personne */
	public static final String FormNoIndividuKey = "noIndividu";
	/** Cle formulaire : l'identifiant d'une demande */
	public static final String FormIntOrdreKey = "intOrdre";
	/** Cle formulaire : l'identifiant de la version du service Web */
	public static final String FormWSVersionKey = "dtwsVersion";
	/** Cle formulaire : l'adresse email du posteur du message */
	public static final String FormMailFromKey = "mailFrom";
	/**
	 * @deprecated ? Cle formulaire : le titre du message
	 */
	public static final String FormMailTitreKey = "mailTitre";
	/** Cle formulaire : le contenu du message */
	public static final String FormMailMessageKey = "mailMessage";
	/** Cle formulaire : l'identifiant d'un message */
	public static final String FormDisOrdreKey = "disOrdre";
	/** Cle formulaire : la référence liée à une discussion */
	public static final String FormDiscussionReferenceKey = "discussionReference";
}
