-- SE CONNECTER EN TANT QUE USER GRHUM


------------
-- GRANTS --
------------
grant execute on chaine_sans_accents to dt3;


---------------------------------------
-- SUPPRESSION DE TABLES TEMPORAIRES --
---------------------------------------
BEGIN
    GRHUM.Drop_Object('DT3', 'INTERVENTION_REPRO_BAK_V310', 'TABLE');
    GRHUM.Drop_Object('DT3', 'TMP_DEVIS', 'TABLE');
    GRHUM.Drop_Object('DT3', 'TMP_DEVIS_ARTICLE', 'TABLE');
END;
/



----------------------
-- NOUVELLES TABLES --
----------------------


-- table DISCUSSION
CREATE TABLE DT3.DISCUSSION
(
  DIS_ORDRE        NUMBER                       NOT NULL,
  INT_ORDRE        NUMBER                       NOT NULL,
  TRA_ORDRE        NUMBER,
  DIS_ORDRE_PERE   NUMBER,
  DIS_NO_INDIVIDU  NUMBER                       NOT NULL,
  DIS_MESSAGE  	   VARCHAR2(4000)               NOT NULL,
  DIS_DATE  	   DATE			                NOT NULL,
  D_CREATION       DATE                         DEFAULT SYSDATE               NOT NULL,
  D_MODIFICATION   DATE                         DEFAULT SYSDATE               NOT NULL
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
NOMONITORING;

CREATE SEQUENCE DT3.SEQ_DISCUSSION NOCACHE;

COMMENT ON TABLE DT3.DISCUSSION IS 'Dialogue entre intervenants et demandeurs';
COMMENT ON COLUMN DT3.DISCUSSION.DIS_ORDRE IS 'Clé primaire';
COMMENT ON COLUMN DT3.DISCUSSION.INT_ORDRE IS 'Demande concernée (table DT3.INTERVENTION)';
COMMENT ON COLUMN DT3.DISCUSSION.TRA_ORDRE IS 'Traitement concerné (table DT3.TRAITEMENT)';
COMMENT ON COLUMN DT3.DISCUSSION.DIS_ORDRE_PERE IS 'Message de discussion auquel s''adresser (table DT3.DISCUSSION)';
COMMENT ON COLUMN DT3.DISCUSSION.DIS_NO_INDIVIDU IS 'Auteur du message (table GRHUM.INDIVIDU_ULR)';
COMMENT ON COLUMN DT3.DISCUSSION.DIS_MESSAGE IS 'Contenu du message';
COMMENT ON COLUMN DT3.DISCUSSION.DIS_DATE IS 'Date du message';
COMMENT ON COLUMN DT3.DISCUSSION.D_CREATION IS 'Date de création de l''enregistrement (interne)';
COMMENT ON COLUMN DT3.DISCUSSION.D_MODIFICATION IS 'Date de dernière modification de l''enregistrement (interne)';

ALTER TABLE DT3.DISCUSSION ADD (
  CONSTRAINT DISCUSSION_PK
 PRIMARY KEY
 (DIS_ORDRE));

ALTER TABLE DT3.DISCUSSION ADD (
  CONSTRAINT FK_DISCUSSION_INTERVENTION 
 FOREIGN KEY (INT_ORDRE) 
 REFERENCES DT3.INTERVENTION (INT_ORDRE),
  CONSTRAINT FK_DISCUSSION_TRAITEMENT 
 FOREIGN KEY (TRA_ORDRE) 
 REFERENCES DT3.TRAITEMENT (TRA_ORDRE),
  CONSTRAINT FK_DISCUSSION_DISCUSSION 
 FOREIGN KEY (DIS_ORDRE_PERE) 
 REFERENCES DT3.DISCUSSION (DIS_ORDRE));



-- TABLE EMAIL
CREATE TABLE DT3.EMAIL
(
  EMAIL_ORDRE     NUMBER,
  INT_ORDRE       NUMBER                        NOT NULL,
  TRA_ORDRE       NUMBER,
  DIS_ORDRE       NUMBER,
  D_CREATION      DATE                          DEFAULT SYSDATE               NOT NULL,
  D_MODIFICATION  DATE                          DEFAULT SYSDATE               NOT NULL,
  MESSAGE_ID      VARCHAR2(4000)                NOT NULL
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
NOMONITORING;


ALTER TABLE DT3.EMAIL ADD (
  CONSTRAINT PK_EMAIL
 PRIMARY KEY
 (EMAIL_ORDRE));

ALTER TABLE DT3.EMAIL ADD (
  CONSTRAINT FK_EMAIL_INTERVENTION 
 FOREIGN KEY (INT_ORDRE) 
 REFERENCES DT3.INTERVENTION (INT_ORDRE),
  CONSTRAINT FK_EMAIL_TRAITEMENT 
 FOREIGN KEY (TRA_ORDRE) 
 REFERENCES DT3.TRAITEMENT (TRA_ORDRE),
  CONSTRAINT FK_EMAIL_DISCUSSION 
 FOREIGN KEY (DIS_ORDRE) 
 REFERENCES DT3.DISCUSSION (DIS_ORDRE));



-- TABLE HISTO_MOTIF
CREATE TABLE DT3.HISTO_MOTIF
(
  INT_ORDRE    NUMBER                           NOT NULL,
  INT_MOTIF    VARCHAR2(4000),
  D_CREATION   DATE                             DEFAULT SYSDATE,
  NO_INDIVIDU  NUMBER                           NOT NULL,
  HIS_ORDRE    NUMBER                           NOT NULL
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
NOMONITORING;

COMMENT ON TABLE DT3.HISTO_MOTIF IS 'Historique des motifs des DTs (pour tracer la  liste des modifications des motifs par les utilisateurs)';
COMMENT ON COLUMN DT3.HISTO_MOTIF.INT_ORDRE IS 'DT concernée';
COMMENT ON COLUMN DT3.HISTO_MOTIF.INT_MOTIF IS 'motif';
COMMENT ON COLUMN DT3.HISTO_MOTIF.D_CREATION IS 'Date de création de l''enregistrement';
COMMENT ON COLUMN DT3.HISTO_MOTIF.NO_INDIVIDU IS 'Individu ayant effectué la modification N+1 (celle qui est en vigueur dans INTERVENTION.INT_MOTIF)';
COMMENT ON COLUMN DT3.HISTO_MOTIF.HIS_ORDRE IS 'Clé primaire';


CREATE SEQUENCE DT3.HISTO_MOTIF_SEQ NOCACHE;

ALTER TABLE DT3.HISTO_MOTIF ADD (
  CONSTRAINT PK_HISTO_MOTIF
 PRIMARY KEY
 (HIS_ORDRE));

ALTER TABLE DT3.HISTO_MOTIF ADD (
  CONSTRAINT FK_HISTO_INTERVENTION 
 FOREIGN KEY (INT_ORDRE) 
 REFERENCES DT3.INTERVENTION (INT_ORDRE));
 
 
 

-- TABLE TRAITEMENT_TYPE
CREATE TABLE DT3.TRAITEMENT_TYPE
(
  TTY_KEY      NUMBER                           NOT NULL,
  TTY_LIBELLE  VARCHAR2(64)                     NOT NULL,
  TTY_CODE     VARCHAR2(1)                      NOT NULL,
  TTY_POSITION  NUMBER                          NOT NULL
)

LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
NOMONITORING;

COMMENT ON TABLE DT3.TRAITEMENT_TYPE IS 'Table de typage des traitements.';
COMMENT ON COLUMN DT3.TRAITEMENT_TYPE.TTY_KEY IS 'Clé primaire';
COMMENT ON COLUMN DT3.TRAITEMENT_TYPE.TTY_LIBELLE IS 'Libellé du type de traitement';
COMMENT ON COLUMN DT3.TRAITEMENT_TYPE.TTY_CODE IS 'Code du type de traitement (ne pas modifier)';


ALTER TABLE DT3.TRAITEMENT_TYPE ADD (
  CONSTRAINT TRAITEMENT_TYPE_PK
 PRIMARY KEY
 (TTY_KEY));
 

Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (1, 'Traitement textuel', 'T', 1);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (2, 'Prestation', 'P', 1);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (3, 'Commande', 'C', 5);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (4, 'Livraison', 'L', 6);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (5, 'Numéro de série', 'S', 7);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (6, 'BdC interne pour signature (émission)', 'E', 2);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (7, 'BdC interne signé (réception)', 'R', 3);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (8, 'Clôture surcôut non signé', 'N', 4);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (9, 'Incohérence demande / nombre de poste', 'I', 3);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (10, 'Remise du poste commandé', 'O', 3);
Insert into DT3.TRAITEMENT_TYPE (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION) Values (11, 'Restitution du poste suite à remplacement', 'Q', 4);
 

ALTER TABLE DT3.TRAITEMENT ADD (TTY_KEY  NUMBER                               DEFAULT 1                     NOT NULL);
ALTER TABLE DT3.TRAITEMENT ADD (TRA_TRAITEMENT_ADDITIONNEL VARCHAR2(2000));


COMMENT ON TABLE DT3.TRAITEMENT IS 'Traitement relative à une demande de travaux';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_ORDRE IS 'Clé primaire';
COMMENT ON COLUMN DT3.TRAITEMENT.NO_INDIVIDU IS 'Individu ayant effectué le traitement (table GRHUM.INDIVIDU_ULR)';
COMMENT ON COLUMN DT3.TRAITEMENT.INT_ORDRE IS 'Demande de travaux concernée (table DT3.INTERVENTION)';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_ETAT IS 'Etat actuel de la demande de travaux (table DT3.ETAT_DT)';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_COMMENTAIRE_INTERNE IS 'Le commentaire interne relatif au traitement (à conserver pour historique)';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_DATE_DEB IS 'Date de début du traitement';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_DATE_FIN IS 'Date de fin du traitement';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_CONSULTABLE IS 'Indique si le traitement est public (O/N)';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_TRAITEMENT IS 'Le détail du traitement';
COMMENT ON COLUMN DT3.TRAITEMENT.TRA_TRAITEMENT_ADDITIONNEL IS 'Complément au traitement';

COMMENT ON COLUMN DT3.TRAITEMENT.TTY_KEY IS 'Le type de traitement (table DT3.TRAITEMENT_TYPE)';

ALTER TABLE DT3.TRAITEMENT
 ADD CONSTRAINT FK_TRAITEMENT_TYPE 
 FOREIGN KEY (TTY_KEY) 
 REFERENCES DT3.TRAITEMENT_TYPE (TTY_KEY);
 
update DT3.TRAITEMENT set tty_key = 2 where int_ordre in (select int_ordre from DT3.INTERVENTION_REPRO); 



-- table INTERVENTION_CODE_ANA
CREATE TABLE DT3.INTERVENTION_CODE_ANA
(
  INT_ORDRE  NUMBER                             NOT NULL,
  CAN_ID     NUMBER                             NOT NULL,
  QUOTITE    NUMBER                             NOT NULL
);

COMMENT ON TABLE DT3.INTERVENTION_CODE_ANA IS 'Description de l''affectation des codes analytiques aux demandes de travaux';
COMMENT ON COLUMN DT3.INTERVENTION_CODE_ANA.INT_ORDRE IS 'la dt (table DT3.INTERVENTION)';
COMMENT ON COLUMN DT3.INTERVENTION_CODE_ANA.CAN_ID IS 'le code analytique';
COMMENT ON COLUMN DT3.INTERVENTION_CODE_ANA.QUOTITE IS 'la quotité';

ALTER TABLE DT3.INTERVENTION_CODE_ANA ADD (
  CONSTRAINT INTERVENTION_CODE_ANA_PK
 PRIMARY KEY
 (INT_ORDRE, CAN_ID));

ALTER TABLE DT3.INTERVENTION_CODE_ANA ADD (
  CONSTRAINT FK_INT_CODE_ANA_INT 
 FOREIGN KEY (INT_ORDRE) 
 REFERENCES DT3.INTERVENTION (INT_ORDRE));


-- table INTERVENTION_REPRO_CFC
-- modification de la table CFC : une clé primaire à elle seule
ALTER TABLE DT3.INTERVENTION_REPRO_CFC RENAME TO INTERVENTION_REPRO_CFC_BCK;

CREATE TABLE DT3.INTERVENTION_REPRO_CFC
(
  INT_ORDRE       NUMBER                        NOT NULL,
  AUTEURS         VARCHAR2(4000),
  EDITEURS        VARCHAR2(4000)                NOT NULL,
  TITRE           VARCHAR2(4000)                NOT NULL,
  NB_EXEMPLAIRES  NUMBER                        NOT NULL,
  NB_PAGES        NUMBER                   		NOT NULL,
  D_CREATION      DATE                          DEFAULT SYSDATE               NOT NULL,
  D_MODIFICATION  DATE                          DEFAULT SYSDATE               NOT NULL,
  CFC_ORDRE       NUMBER                        NOT NULL
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
NOMONITORING;

COMMENT ON TABLE DT3.INTERVENTION_REPRO_CFC IS 'Liste des déclarations CFC concernant les demandes de travaux de reprographie';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.INT_ORDRE IS 'DT concernée (table INTERVENTION)';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.AUTEURS IS 'Auteurs du document';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.EDITEURS IS 'Editeurs du document';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.TITRE IS 'Titre du document';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.NB_EXEMPLAIRES IS 'Nombre d''exemplaires';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.NB_PAGES IS 'Nombre de pages';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.D_CREATION IS 'Date de création de l''enregistrement (interne)';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.D_MODIFICATION IS 'Date de modification de l''enregistrement (interne)';
COMMENT ON COLUMN DT3.INTERVENTION_REPRO_CFC.CFC_ORDRE IS 'Clé primaire';


ALTER TABLE DT3.INTERVENTION_REPRO_CFC ADD (
  CONSTRAINT INTERVENTION_REPRO_CFC_PK
 PRIMARY KEY
 (CFC_ORDRE));

ALTER TABLE DT3.INTERVENTION_REPRO_CFC ADD (
  CONSTRAINT INTERVENTION_REPRO_CFC_FK 
 FOREIGN KEY (INT_ORDRE) 
 REFERENCES DT3.INTERVENTION (INT_ORDRE));

CREATE SEQUENCE DT3.SEQ_INTERVENTION_REPRO_CFC NOCACHE;

-- rappatrier les données de l'ancienne table
INSERT INTO DT3.INTERVENTION_REPRO_CFC(INT_ORDRE, AUTEURS, EDITEURS, TITRE, NB_EXEMPLAIRES, NB_PAGES, D_CREATION, D_MODIFICATION, CFC_ORDRE)
SELECT INT_ORDRE, AUTEURS, EDITEURS, TITRE, NB_EXEMPLAIRES, NB_PAGES, SYSDATE, SYSDATE, DT3.SEQ_INTERVENTION_REPRO_CFC.NEXTVAL
FROM DT3.INTERVENTION_REPRO_CFC_BCK;

DROP TABLE DT3.INTERVENTION_REPRO_CFC_BCK;




----------------------------
-- MODIFICATION DE TABLES --
----------------------------

-- PREF_APPLI
ALTER TABLE DT3.PREF_APPLI ADD (PRF_CTRL_CHEV_PLANNING NUMBER(1) DEFAULT 0 NOT NULL);
ALTER TABLE DT3.PREF_APPLI ADD (PRF_CONFIRMATION_CLOTURE NUMBER(1) DEFAULT 1 NOT NULL);
ALTER TABLE DT3.PREF_APPLI ADD (PRF_SAUVEGARDER_PANIER NUMBER DEFAULT 1);
ALTER TABLE DT3.PREF_APPLI ADD (PRF_PANIER VARCHAR2(4000));

COMMENT ON COLUMN DT3.PREF_APPLI.PRF_CTRL_CHEV_PLANNING IS 'L''utilisateur souhaite que DT controle s''il est disponible à chaque saisie de traitement (pas de chevauchement avec d''autres traitements ou évenements provenant du serveur de planning)';
COMMENT ON COLUMN DT3.PREF_APPLI.PRF_CONFIRMATION_CLOTURE IS 'L''application demande confirmation systématique à l''utilisateur lorsqu''il clot une DT';
COMMENT ON COLUMN DT3.PREF_APPLI.PRF_SAUVEGARDER_PANIER IS 'L''application mémorise dans sa base le panier de l''utilisateur pour exploitation ultérieure';
COMMENT ON COLUMN DT3.PREF_APPLI.PRF_PANIER IS 'Le dernier panier de l''utilisateur pour exploitation ultérieure';

-- GRP_EMAIL
ALTER TABLE DT3.GROUPES_DT MODIFY(GRP_EMAIL VARCHAR2(256));
ALTER TABLE DT3.GROUPES_DT ADD (GRP_EMAIL_SAM  VARCHAR2(256));

COMMENT ON COLUMN DT3.GROUPES_DT.C_STRUCTURE IS 'Le groupe de l''annuaire associée (table GRHUM.STRUCTURE_ULR)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_SEQ IS 'Le numéro visible de la prochaine DT créée sur ce service (attribut DT3.INTERVENTION.INT_CLE_SERVICE)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_POSITION IS 'Le positionnement du service dans les onglets (le plus petit=le plus à gauche)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_AFFICHABLE IS 'Indique si le service est affiché dans l''interface de création (O/N)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_EMAIL IS 'L''adresse email de contact du service (pour y envoyer les mail de création, affectation ...)';
COMMENT ON COLUMN DT3.GROUPES_DT.GT_CODE IS '** pas utilisé **';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_MAIL_SERVICE IS 'Indique s''il faut envoyer des mails à l''adresse GRP_EMAIL (à priori oui, sinon à quoi ça sert ...)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_SWAP_VIEW IS 'Le numéro de l''interface dédiée à la création de DT pour ce service (0=défaut)';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_VISIBILITE IS 'La liste des VLAN d''appartenance des personnes autorisées à voir l''interface de création de ce service';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_VISIBILITE_STRUCTURE IS 'La liste des structure dont les membres sont autorisés à voir l''interface de création de ce service';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_NB_LIG_BROWSER_ACT IS 'Le nombre de lignes visibles en meme temps dans l''ecran de selection des activites pour ce service';
COMMENT ON COLUMN DT3.GROUPES_DT.GRP_EMAIL_SAM IS 'Le point d''entrée SAM pour la récupération de toutes les réponses des demandeurs. C''est alors l''adresse email "from" des messages envoyés';





----------------
-- PROCEDURES --
----------------
CREATE OR REPLACE PROCEDURE DT3.INS_INTERVENTION
(
  ACT_ORDRE               IN NUMBER,
  C_LOCAL                 IN VARCHAR2,
  C_STRUCTURE             IN VARCHAR2,
  CT_ORDRE                IN NUMBER,
  DST_CODE                IN VARCHAR2,
  INT_CLE_SERVICE	        OUT NUMBER,
  INT_COMMENTAIRE_INTERNE IN CHAR,
  INT_DATE_BUTOIR         IN DATE,
  INT_DATE_CREATION       IN DATE,
  INT_DATE_SOUHAITE       IN DATE,
  INT_ETAT                IN CHAR,
  INT_MOTIF               IN VARCHAR2,
  INT_MOTS_CLEFS          IN CHAR,
  INT_NO_IND_APPELANT     IN NUMBER,
  INT_NO_IND_CONCERNE     IN NUMBER,
  INT_ORDRE		  		  OUT NUMBER,
  INT_PRIORITE			  IN NUMBER,
  INT_SERVICE_CONCERNE    IN VARCHAR2,
  MOD_CODE				  IN CHAR
)
IS
  seq_numero 			  INTEGER;
  seq_numero_service 	  INTEGER;
BEGIN
	SELECT SEQ_INTERVENTION.NEXTVAL INTO seq_numero FROM dual;
	INC_SEQ_GROUPES_DT(C_STRUCTURE, 'I', seq_numero_service);

	INSERT INTO INTERVENTION
	    (INT_ORDRE, MOD_CODE, C_STRUCTURE, ACT_ORDRE, INT_CLE_SERVICE, INT_NO_IND_APPELANT,
		 INT_NO_IND_CONCERNE, INT_PRIORITE, INT_ETAT, INT_COMMENTAIRE_INTERNE,
         INT_MOTIF, INT_MOTS_CLEFS, INT_DATE_CREATION, INT_DATE_SOUHAITE, INT_DATE_BUTOIR,
         INT_SERVICE_CONCERNE, C_LOCAL, CT_ORDRE, DST_CODE)
	  VALUES(seq_numero,MOD_CODE,C_STRUCTURE,ACT_ORDRE,seq_numero_service,
	         INT_no_ind_APPELANT,INT_no_ind_CONCERNE,INT_PRIORITE,INT_ETAT,
			 INT_COMMENTAIRE_INTERNE,INT_MOTIF,INT_MOTS_CLEFS,INT_DATE_CREATION,
			 INT_DATE_SOUHAITE,INT_DATE_BUTOIR,INT_SERVICE_CONCERNE, c_local, CT_ORDRE, DST_CODE);

	INT_CLE_SERVICE:=seq_numero_service;
	INT_ORDRE:=seq_numero;
END;
/




---------------
-- FONCTIONS --
---------------


-- FONCTION int_masquee_liste
CREATE OR REPLACE FUNCTION DT3.int_masquee_liste(
in_int_ordre NUMBER
)
RETURN VARCHAR2
IS
-- ------------
-- donner la liste des personnes ayant masqué une DT, respectant le format
-- suivant : NO_INDIVIDU[,NO_INDIVIDU]
-- ------------

nb INTEGER;
chaine VARCHAR2(4000);

CURSOR cur IS
SELECT NO_INDIVIDU
FROM DT3.INTERVENTION_MASQUEE I
WHERE I.INT_ORDRE = in_int_ordre;

BEGIN
  chaine := '';
  OPEN cur;
    LOOP
        FETCH cur INTO nb;
        EXIT WHEN cur%NOTFOUND;
        chaine := chaine || nb || ',';
    END LOOP;
  CLOSE cur;
  RETURN chaine;
END int_masquee_liste;



-- FONCTION Chaine_Sans_Accents
CREATE OR REPLACE FUNCTION DT3.Chaine_Sans_Accents(texte VARCHAR2)
RETURN VARCHAR2
IS
-- --------------------------------------
-- Projet : Renvoi une chaine sans les caracteres accentues (complements
-- par rapport a la fonction identique de GRHUM)
-- Auteur : CRI
-- Date : 02/07/2008
-- Modif : 02/07/2008
--
-- --------------------------------------

-- ------------
-- DECLARATIONS
-- ------------

txt VARCHAR2(500);

BEGIN

-- Le caractere = est remplace par un souligne
txt := REPLACE(texte,'=','_');

-- Le caractere / est remplace par un souligne
txt := REPLACE(txt,'/','_');

-- Le caractere < est remplace par un souligne
txt := REPLACE(txt,'<','_');

-- Le caractere > est remplace par un souligne
txt := REPLACE(txt,'>','_');

-- Le caractere [ est remplace par un souligne
txt := REPLACE(txt,'[','_');

-- Le caractere ] est remplace par un souligne
txt := REPLACE(txt,']','_');

-- Le caractere # est remplace par un souligne
txt := REPLACE(txt,'#','_');

-- Le caractere ; est remplace par un souligne
txt := REPLACE(txt,';','_');

-- Le caractere ∞ est remplace par un souligne
txt := REPLACE(txt,'∞','_');

return grhum.chaine_sans_accents(txt);

END;



CREATE OR REPLACE FUNCTION DT3.Act_Ordre_Liste(
in_act_ordre NUMBER
)
RETURN VARCHAR2
IS
-- ------------
-- donner la liste des act_ordre hierarchique pour aller jusqu'a une activite
-- ------------

le_act_ordre_pere NUMBER;
nb INTEGER;
la_act_liste VARCHAR2(2000);

BEGIN

	 -- on ignore les services (act_ordre negatif)
	 IF (in_act_ordre <= 0) THEN
	 	RETURN NULL;
	 END IF;

 	 SELECT COUNT(act_ordre) INTO nb
	 FROM dt3.ACTIVITES
	 WHERE act_ordre = in_act_ordre;
	 
	 IF (nb > 0) THEN
	    SELECT act_pere INTO le_act_ordre_pere
	 	FROM dt3.ACTIVITES
	 	WHERE act_ordre = in_act_ordre;
	
		la_act_liste := Act_Ordre_Liste(le_act_ordre_pere);
		
		IF (la_act_liste IS NOT NULL) THEN
		   RETURN la_act_liste || '; ' || TO_CHAR(in_act_ordre);
		ELSE
			RETURN TO_CHAR(in_act_ordre);
		END IF;
		
	 ELSE
	 	RETURN NULL;
	 END IF;

END;
/



----------
-- VUES --
----------

CREATE OR REPLACE FORCE VIEW dt3.v_activites (act_ordre,
                                              act_ordre_hierarchie,
                                              act_libelle,
                                              act_code,
                                              act_pere,
                                              c_structure,
                                              act_swap_view,
                                              grp_position,
                                              act_resp,
                                              act_pref,
                                              grp_affichable,
                                              act_mail_service,
                                              act_creer_mail,
                                              act_swap_message,
                                              grp_visibilite,
                                              cart_ordre,
                                              grp_visibilite_structure
                                             )
AS
   SELECT -TO_NUMBER (g.c_structure), NULL, s.ll_structure, s.lc_structure, 0,
          g.c_structure, g.grp_swap_view, g.grp_position,
          -TO_NUMBER (g.c_structure), -TO_NUMBER (g.c_structure),
          g.grp_affichable, g.grp_mail_service, NULL, NULL, g.grp_visibilite,
          NULL, grp_visibilite_structure
     FROM dt3.groupes_dt g, grhum.structure_ulr s
    WHERE g.c_structure = s.c_structure
   UNION
   SELECT a.act_ordre, act_ordre_liste (a.act_ordre), a.act_libelle,
          a.act_libelle,
          DECODE (a.act_pere, 0, -TO_NUMBER (a.c_structure), a.act_pere),
          a.c_structure, a.act_swap_view, g.grp_position,
          DECODE (a.act_resp, 0, -TO_NUMBER (a.c_structure), a.act_resp),
          DECODE (a.act_pref, 0, -TO_NUMBER (a.c_structure), a.act_pref),
          a.act_affichable, a.act_mail_service, a.act_creer_mail,
          a.act_swap_message, g.grp_visibilite, a.cart_ordre, NULL
     FROM dt3.activites a, dt3.groupes_dt g
    WHERE a.act_ordre <> 0 AND a.c_structure = g.c_structure;


CREATE OR REPLACE FORCE VIEW dt3.v_activites_intervenant (act_ordre,
                                                          act_pere,
                                                          no_ind_intervenant,
                                                          act_libelle,
                                                          c_structure_dest,
                                                          int_date_creation,
                                                          int_etat
                                                         )
AS
   SELECT DISTINCT a.act_ordre, a.act_pere, i2.no_individu, a.act_libelle,
                   i1.c_structure c_structure_dest, i1.int_date_creation,
                   i1.int_etat
              FROM dt3.intervention i1, dt3.activites a, dt3.intervenant i2
             WHERE i1.act_ordre = a.act_ordre
               AND i1.int_ordre = i2.int_ordre
               AND i1.int_ordre > 0
               AND a.act_ordre > 0;


CREATE OR REPLACE FORCE VIEW dt3.v_contact_intervenant (no_ind_intervenant,
                                                        ct_ordre,
                                                        nom_et_prenom,
                                                        ct_email,
                                                        lc_structure,
                                                        c_structure_dest,
                                                        int_date_creation,
                                                        int_etat,
                                                        no_ind_contact,
                                                        c_structure_contact,
                                                        c_local
                                                       )
AS
   SELECT DISTINCT intervenant.no_individu no_ind_intervenant, c.ct_ordre,
                   nom_usuel || ' ' || prenom nom_et_prenom, ct_email,
                   lc_structure, intervention.c_structure c_structure_dest,
                   int_date_creation, int_etat, c.no_individu,
                   c.c_struct_service, c.c_local
              FROM dt3.intervenant,
                   dt3.intervention,
                   grhum.individu_ulr i,
                   grhum.structure_ulr s,
                   dt3.contact c
             WHERE intervenant.int_ordre = intervention.int_ordre
               AND c.ct_ordre = intervention.ct_ordre
               AND c.no_individu = i.no_individu
               AND c.c_struct_service = s.c_structure(+);


CREATE OR REPLACE FORCE VIEW dt3.v_destin (dst_code,
                                           dst_lib,
                                           dst_dlib,
                                           dst_abrege,
                                           dst_type,
                                           dst_niv
                                          )
AS
   SELECT dst_code, dst_lib, dst_dlib, dst_abrege, dst_type, dst_niv
     FROM jefy.v_destin
    WHERE annee = TO_CHAR (SYSDATE, 'YYYY');
    


CREATE OR REPLACE FORCE VIEW dt3.v_intervenant_tra_count (tra_count,
                                                          int_ordre,
                                                          no_individu
                                                         )
AS
   SELECT   COUNT (*), t.int_ordre, t.no_individu
       FROM dt3.traitement t
   GROUP BY t.int_ordre, t.no_individu
   UNION
   SELECT   0 AS tra_count, i.int_ordre, iv.no_individu
       FROM dt3.intervention i, dt3.intervenant iv
      WHERE i.int_ordre = iv.int_ordre
        AND NOT EXISTS (
               SELECT *
                 FROM dt3.traitement t
                WHERE t.int_ordre = i.int_ordre
                  AND t.no_individu = iv.no_individu)
   GROUP BY i.int_ordre, iv.no_individu;




CREATE OR REPLACE FORCE VIEW dt3.v_intervention_intervenant (int_ordre,
                                                             mod_code,
                                                             c_structure,
                                                             act_ordre,
                                                             int_cle_service,
                                                             int_no_ind_appelant,
                                                             int_no_ind_concerne,
                                                             int_priorite,
                                                             int_etat,
                                                             int_commentaire_interne,
                                                             int_motif,
                                                             int_mots_clefs,
                                                             int_date_creation,
                                                             int_date_souhaite,
                                                             int_date_butoir,
                                                             int_service_concerne,
                                                             c_local,
                                                             ct_ordre,
                                                             dst_code,
                                                             no_ind_intervenant
                                                            )
AS
   SELECT   i.int_ordre, mod_code, c_structure, act_ordre, int_cle_service,
            int_no_ind_appelant, int_no_ind_concerne, int_priorite, int_etat,
            int_commentaire_interne, int_motif, int_mots_clefs,
            int_date_creation, int_date_souhaite, int_date_butoir,
            int_service_concerne, c_local, ct_ordre, dst_code,
            a.no_individu no_ind_intervenant
       FROM dt3.intervention i, dt3.intervenant a
      WHERE i.int_ordre = a.int_ordre
   ORDER BY i.int_ordre;
   
   
CREATE OR REPLACE FORCE VIEW dt3.v_organ (org_ordre,
                                          org_unit,
                                          org_comp,
                                          org_lbud,
                                          org_uc,
                                          org_lib,
                                          org_niv,
                                          org_stat,
                                          org_date,
                                          org_tva,
                                          sct_code,
                                          org_rat,
                                          dst_code,
                                          org_lucrativite,
                                          org_type_ligne,
                                          org_type_flechage,
                                          org_ouverture_credits,
                                          ooc_code,
                                          org_observation,
                                          otc_code,
                                          org_delegation
                                         )
AS
   SELECT org_ordre, org_unit, org_comp, org_lbud, org_uc, org_lib, org_niv,
          org_stat, org_date, org_tva, sct_code, org_rat, dst_code,
          org_lucrativite, org_type_ligne, org_type_flechage,
          org_ouverture_credits, ooc_code, org_observation, otc_code,
          org_delegation
     FROM jefy.v_organ
    WHERE annee = TO_CHAR (SYSDATE, 'YYYY');


CREATE OR REPLACE FORCE VIEW dt3.v_prest_id_dt_non_cloturee (prest_id)
AS
   SELECT prest_id
     FROM dt3.intervention_infin ii, dt3.intervention i
    WHERE ii.int_ordre = i.int_ordre
      AND prest_id IS NOT NULL
      AND i.int_etat <> 'T';
      

CREATE OR REPLACE FORCE VIEW dt3.v_traitement (tra_ordre,
                                               no_individu,
                                               int_ordre,
                                               tra_etat,
                                               tra_traitement,
                                               tra_commentaire_interne,
                                               tra_date_deb,
                                               tra_date_fin,
                                               tra_consultable,
                                               c_structure,
                                               int_cle_service,
                                               act_ordre,
                                               act_libelle,
                                               lc_structure
                                              )
AS
   SELECT t.tra_ordre, t.no_individu, t.int_ordre, t.tra_etat,
          t.tra_traitement, t.tra_commentaire_interne, t.tra_date_deb,
          t.tra_date_fin, t.tra_consultable, i.c_structure, int_cle_service,
          a.act_ordre, act_libelle, s.lc_structure
     FROM dt3.traitement t, dt3.intervention i, dt3.activites a, grhum.structure_ulr s
    WHERE t.int_ordre = i.int_ordre
      AND i.act_ordre = a.act_ordre
      AND s.c_structure = i.c_structure;
      
      
COMMIT;