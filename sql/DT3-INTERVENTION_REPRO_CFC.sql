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