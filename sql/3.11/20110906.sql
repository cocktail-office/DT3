ALTER TABLE DT3.PREF_APPLI ADD (PRF_CTRL_CHEV_PLANNING NUMBER(1) DEFAULT 0 NOT NULL);

COMMENT ON COLUMN DT3.PREF_APPLI.PRF_CTRL_CHEV_PLANNING IS 
'L''utilisateur souhaite que DT controle s''il est disponible à chaque saisie de traitement (pas de chevauchement avec d''autres traitements ou évenements provenant du serveur de planning)';

ALTER TABLE DT3.PREF_APPLI ADD (PRF_CONFIRMATION_CLOTURE NUMBER(1) DEFAULT 1 NOT NULL);

COMMENT ON COLUMN DT3.PREF_APPLI.PRF_CONFIRMATION_CLOTURE IS 
'L''application demande confirmation systématique à l''utilisateur lorsqu''il clot une DT';

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
 
-- TODO ajouter le grant references de DT3 vers GRHUM.INDIVIDU_ULR
/*ALTER TABLE DT3.HISTO_MOTIF ADD (
  CONSTRAINT FK_HISTO_INDIVIDU 
 FOREIGN KEY (NO_INDIVIDU) 
 REFERENCES GRHUM.INDIVIDU_ULR (NO_INDIVIDU));*/


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



SET DEFINE OFF;

alter table DT3.TRAITEMENT DISABLE constraint FK_TRAITEMENT_TYPE;

DELETE FROM DT3.TRAITEMENT_TYPE;

ALTER TABLE DT3.TRAITEMENT_TYPE ADD (TTY_POSITION  NUMBER NOT NULL);


Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (6, 'BdC interne pour signature (émission)', 'E', 2);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (7, 'BdC interne signé (réception)', 'R', 3);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (8, 'Clôture surcôut non signé', 'N', 4);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (1, 'Traitement textuel', 'T', 1);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (2, 'Prestation', 'P', 1);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (3, 'Commande', 'C', 5);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (4, 'Livraison', 'L', 6);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (5, 'Numéro de série', 'S', 7);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (9, 'Incohérence demande / nombre de poste', 'I', 3);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (10, 'Remise du poste commandé', 'O', 3);
Insert into DT3.TRAITEMENT_TYPE
   (TTY_KEY, TTY_LIBELLE, TTY_CODE, TTY_POSITION)
 Values
   (11, 'Restitution du poste suite à remplacement', 'Q', 4);

alter table DT3.TRAITEMENT ENABLE constraint FK_TRAITEMENT_TYPE;



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
