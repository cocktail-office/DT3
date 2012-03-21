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