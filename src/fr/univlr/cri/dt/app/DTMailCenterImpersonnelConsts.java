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
package fr.univlr.cri.dt.app;

/**
 * @deprecated 
 * Definition de toutes les constantes utilisees par la classe
 * <code>DTMailCenter</code>
 * 
 * @author ctarade
 */
public interface DTMailCenterImpersonnelConsts {

  /** l'adresse email de sam associee au plugin sam-dt */
  public final static String VAR_SAM_MAIL					= "%APP_SAM_MAIL%";
  /** le libelle de la cle pere pour un envoi de message (intOrdre, traOrdre ou disOrdre)*/
  public final static String VAR_PARENT_KEY				= "%VAR_PARENT_KEY%";
  /** la valeur de la cle pere pour un envoi de message */
  public final static String VAR_PARENT_KEY_VALUE = "%VAR_PARENT_KEY_VALUE%";
  /** le patron pour l'ajout de la ligne de contact via SAM en haut du mail (obligatoire)*/
	public final static String PATTERN_SAM_DISCUSSION_TOP = 
		"Merci de donner votre r�ponse via le lien " + VAR_SAM_MAIL + "?subject=" + VAR_PARENT_KEY + "$" + VAR_PARENT_KEY_VALUE + "\n" +
		"\n";
  /** le patron pour l'ajout de la ligne de contact via SAM en bas du mail (facultatif)*/
	public final static String PATTERN_SAM_DISCUSSION_BOTTOM = 
		"--- Commentaires ---\n" +
		"  Vous pouvez ajouter un commentaire en envoyant un e-mail a cette adresse :\n" +
		"  " + VAR_SAM_MAIL + "?subject=" + VAR_PARENT_KEY + "$" + VAR_PARENT_KEY_VALUE;

	
	// --------------------------------------
	// cl�s dictionnaire des paramatres email
	// --------------------------------------
	public final static String PARAM_EMAIL_TO = "to";
	public final static String PARAM_EMAIL_CC = "cc";	

	// -------------------------------------
	// patron pour les informations communes
	// -------------------------------------
  public final static String VAR_INT_CLE_SERVICE 							= "%VAR_INT_CLE_SERVICE%";
  public final static String VAR_NOM_PRENOM_MAIL_DEMANDEUR	 	= "%VAR_NOM_PRENOM_MAIL_DEMANDEUR%";
  public final static String VAR_NOM_PRENOM_MAIL_APPELANT 		= "%VAR_NOM_PRENOM_MAIL_APPELANT%";
  public final static String VAR_DATE_CREATION 								=	"%VAR_DATE_CREATION%";
  public final static String VAR_DATE_SOUHAITEE								=	"%VAR_DATE_SOUHAITEE%";
  public final static String VAR_INT_MOTS_CLEF								=	"%VAR_INT_MOTS_CLEF%";
  public final static String VAR_INT_MOTIF										=	"%VAR_INT_MOTIF%";
  public final static String VAR_CONSULTATION_URL 						= "%VAR_CONSULTATION_URL%";
  public final static String VAR_AFFECTATION_INFO							=	"%VAR_AFFECTATION_INFO%";
  public final static String VAR_INT_COMMENTAIRE_INTERNE			=	"%VAR_INT_COMMENTAIRE_INTERNE%";
  public final static String VAR_DT_DESCRIPTION								=	"%VAR_DT_DESCRIPTION%";
  
	// ----------------------------------------
	// patron pour les fichiers joints a la DT
	// ----------------------------------------
  public final static String NUMBER_VAR_INTERVENTION_FILE_URL_KEY			= "[n]";
  public final static String VAR_INTERVENTION_FILE_URL 					= "%VAR_INTERVENTION_FILE_URL_"+NUMBER_VAR_INTERVENTION_FILE_URL_KEY+"%";
  public final static String PATTERN_INTERVENTION_FILES 				= "\n--- Document(s) attach�(s) ---\n ";
  public final static String VAR_PATTERN_INTERVENTION_FILES 		= "%VAR_PATTERN_INTERVENTION_FILES%";

  // ----------------------------------
	// patron pour les infos budgetaires
	// ----------------------------------
	public final static String VAR_NO_DEVIS 						= "%VAR_NO_DEVIS%";
  public final static String PATTERN_NO_DEVIS 				=	"\nDevis : #" + VAR_NO_DEVIS;
  public final static String VAR_PATTERN_NO_DEVIS 		= "%VAR_PATTERN_NO_DEVIS%";

	public final static String VAR_LIGNE_BUDGETAIRE 				= "%VAR_LIGNE_BUDGETAIRE%";
  public final static String PATTERN_LIGNE_BUDGETAIRE 		= "\nLigne budg�taire : " + VAR_LIGNE_BUDGETAIRE;
  public final static String VAR_PATTERN_LIGNE_BUDGETAIRE = "%PATTERN_LIGNE_BUDGETAIRE%";

	// --------------------------------
	// patron pour la fin des messages
	// --------------------------------
  public final static String VAR_FOOTER_CONTENT 			= "%VAR_FOOTER_CONTENT%";
  public final static String PATTERN_FOOTER 					= "\n----" + VAR_FOOTER_CONTENT; 
  public final static String VAR_PATTERN_FOOTER 			= "%PATTERN_FOOTER%";

	// -------------------------
	// patron pour la signature
	// -------------------------
  public final static String VAR_NOM_PRENOM_TRAITEUR	=	"%VAR_NOM_PRENOM_TRAITEUR%";
  public final static String VAR_EMAIL_TRAITEUR				=	"%VAR_EMAIL_TRAITEUR%";
  public final static String PATTERN_SIGNATURE				=	
  	"\n\n--\n" + VAR_NOM_PRENOM_TRAITEUR + "\n" + VAR_EMAIL_TRAITEUR;
  public final static String VAR_PATTERN_SIGNATURE		= "%PATTERN_SIGNATURE%";
  
	// ----------------------------
	// patron pour les traitements
	// ---------------------------
  public final static String VAR_NOM_PRENOM_AFFECTE		=	"%VAR_NOM_PRENOM_AFFECTE%";
  public final static String VAR_TOTAL_DT_EN_COURS 		= "%VAR_TOTAL_DT_EN_COURS%";
  public final static String VAR_BLOC_AFFECTATION_CONTENT_KEY		= "[n]";
  public final static String PATTERN_BLOC_AFFECTATIONS_ITEM			= 
  	"\n" + VAR_NOM_PRENOM_AFFECTE + VAR_BLOC_AFFECTATION_CONTENT_KEY +
  	"\n  (Attention ! Cette personne a " + VAR_TOTAL_DT_EN_COURS + VAR_BLOC_AFFECTATION_CONTENT_KEY +
  	" demandes en cours)";
  public final static String PATTERN_BLOC_AFFECTATIONS_TOP			= "\n--- Les personnes suivantes r�aliseront l'intervention ---";
  public final static String VAR_PATTERN_BLOC_AFFECTATIONS 			= "%VAR_PATTERN_BLOC_AFFECTATIONS%";
  
	// --------------------------------
	// patron pour le mail de creation
	// --------------------------------
  // le sujet du mail
  public final static String PATTERN_SUBJECT_CREATION_A_VALIDER 					= " (à valider)";
  public final static String VAR_PATTERN_SUBJECT_CREATION_A_VALIDER 	= "%VAR_SUBJECT_CREATION_A_VALIDER%";
  public final static String PATTERN_SUBJECT_CREATION = 
  	"Création de la DT #" + VAR_INT_CLE_SERVICE + VAR_PATTERN_SUBJECT_CREATION_A_VALIDER;

	// les infos sur la validation si l'URL de validation existe
	public final static String VAR_URL_VALIDATION = "%VAR_URL_VALIDATION%";
	public final static String PATTERN_MAIL_CREATION_VALIDATION_URL = 
		"Les demandes en attente de validation sont disponibles à l'adresse :\n" +
		"  " + VAR_URL_VALIDATION + "\n";
	public final static String VAR_PATTERN_MAIL_CREATION_VALIDATION_URL = "%PATTERN_MAIL_CREATION_VALIDATION_URL%";
	
	// les infos sur la validation
	public final static String PATTERN_MAIL_CREATION_VALIDATION = 
		"CETTE DEMANDE DOIT ETRE VALIDEE AVANT D'ETRE PRISE EN COMPTE PAR LE SERVICE CONCERNE.\n" +
		VAR_PATTERN_MAIL_CREATION_VALIDATION_URL +
		"\n\n";
	public final static String VAR_PATTERN_MAIL_CREATION_VALIDATION = "%PATTERN_MAIL_CREATION_VALIDATION%";
  
  // patron global du mail de creation
  public final static String PATTERN_MAIL_CREATION = 
  	VAR_PATTERN_MAIL_CREATION_VALIDATION +
  	"--- Demande de Travaux ---" +
  	"\nNuméro d'enregistrement : " + VAR_INT_CLE_SERVICE +
  	"\nDemandeur : " + VAR_NOM_PRENOM_MAIL_DEMANDEUR +
  	"\nAppelant : " + VAR_NOM_PRENOM_MAIL_APPELANT +
  	"\nDate d'appel : " + VAR_DATE_CREATION  +
  	"\nDate de réalisation souhaitée : " + VAR_DATE_SOUHAITEE +
  	"\nMots clés : " + VAR_INT_MOTS_CLEF +
  	VAR_PATTERN_LIGNE_BUDGETAIRE +
  	VAR_PATTERN_NO_DEVIS +
  	"\n\n--- Motif de la demande ---\n" +
  	VAR_INT_MOTIF + 
  	VAR_PATTERN_INTERVENTION_FILES +
  	"\n\n--- Consultation et validation de la DT ---\n" + 
  	"  " + VAR_CONSULTATION_URL + 
  	VAR_PATTERN_FOOTER;

	// -----------------------------
	// patron pour le mail de rejet
	// -----------------------------
  // le sujet du mail
  public final static String PATTERN_SUBJECT_REJET = "Rejet de la DT #" + VAR_INT_CLE_SERVICE;
  
  // patron global du mail de rejet
  public final static String PATTERN_MAIL_REJET = 
  	"\nDemande rejetée par " + 
  	VAR_AFFECTATION_INFO + "\n" +
  	"\n--- Motif du rejet ---\n" +
  	VAR_INT_COMMENTAIRE_INTERNE + 
  	"\n\n" + 
  	VAR_DT_DESCRIPTION;

	// ----------------------------------
	// patron pour le mail de validation
	// ----------------------------------
  // le sujet du mail
  public final static String PATTERN_SUBJECT_VALIDATION = "Validation de la DT #" + VAR_INT_CLE_SERVICE;
  
  // patron global du mail de validation
  public final static String PATTERN_MAIL_VALIDATION = 
  	"\nDemande validée par " + 
  	VAR_AFFECTATION_INFO + 
  	"\n\n" +
  	VAR_DT_DESCRIPTION + 
  	VAR_PATTERN_FOOTER;

	// ----------------------------------
	// patron pour le mail d'affectation
	// ----------------------------------
  // le sujet du mail
  public final static String PATTERN_SUBJECT_AFFECTATION = "Affectation de la DT #" + VAR_INT_CLE_SERVICE;

  // patron concernant nominativement la personne affectee
  public final static String PATTERN_BLOC_MAIL_AFFECTE = 
  	"\n--- Les personnes suivantes réaliseront l'intervention ---";
  public final static String VAR_PATTERN_BLOC_MAIL_AFFECTE = "%VAR_PATTERN_BLOC_MAIL_AFFECTE%";
  
  // patron global du mail de validation
  public final static String PATTERN_MAIL_AFFECTATION = 
  	"\nDemande de travaux affectée par " + 
  	VAR_AFFECTATION_INFO + 
  	"\n\n" +
  	VAR_DT_DESCRIPTION + 
  	VAR_PATTERN_BLOC_AFFECTATIONS;
  
	// ------------------------------------------
	// patron pour le mail d'ajout de traitement
	// ------------------------------------------
  public final static String VAR_TRAITEMENT_DATE_DEBUT					=	"%VAR_TRAITEMENT_DATE_DEBUT%";
  public final static String VAR_TRAITEMENT_DATE_FIN 						=	"%VAR_TRAITEMENT_DATE_FIN%";
  public final static String VAR_TRAITEMENT_TRAITEUR 						=	"%VAR_TRAITEMENT_TRAITEUR%";
  public final static String VAR_TRAITEMENT_CONTENT							=	"%VAR_TRAITEMENT_CONTENT%";
  public final static String VAR_PATTERN_BLOC_MAIL_TRAITEMENT 	= "%VAR_PATTERN_BLOC_MAIL_TRAITEMENT%";
  // le sujet du mail
  public final static String PATTERN_SUBJECT_TRAITEMENT = "Traitement en cours de la DT #" + VAR_INT_CLE_SERVICE;
  
  // patron du bloc contenant un traitement
  public final static String PATTERN_BLOC_MAIL_TRAITEMENT = 
  	"--- Traitement ---" + 
  	"\n  Date : " + VAR_TRAITEMENT_DATE_DEBUT +
  	" - " + VAR_TRAITEMENT_DATE_FIN +
  	"\n  Intervenant : " + VAR_TRAITEMENT_TRAITEUR + 
  	"\n\n " + 
  	VAR_PATTERN_INTERVENTION_FILES +
  	VAR_PATTERN_SIGNATURE;
  
  // patron global du mail de traitement
  public final static String PATTERN_MAIL_TRAITMENT = 
  	VAR_DT_DESCRIPTION + 
  	VAR_PATTERN_BLOC_MAIL_TRAITEMENT;
}
