-- a faire en tant que GRHUM
grant execute on chaine_sans_accents to dt3;
CREATE OR REPLACE FUNCTION DT3.Chaine_Sans_Accents(texte VARCHAR2)
RETURN VARCHAR2
IS
-- --------------------------------------
-- Projet : Renvoi une chaine sans les caractères accentués (complements
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

-- Le caractère = est remplacé par un souligné
txt := REPLACE(texte,'=','_');

-- Le caractère / est remplacé par un souligné
txt := REPLACE(txt,'/','_');

-- Le caractère < est remplacé par un souligné
txt := REPLACE(txt,'<','_');

-- Le caractère > est remplacé par un souligné
txt := REPLACE(txt,'>','_');

-- Le caractère [ est remplacé par un souligné
txt := REPLACE(txt,'[','_');

-- Le caractère ] est remplacé par un souligné
txt := REPLACE(txt,']','_');

-- Le caractère # est remplacé par un souligné
txt := REPLACE(txt,'#','_');

-- Le caractère ; est remplacé par un souligné
txt := REPLACE(txt,';','_');

-- Le caractère ° est remplacé par un souligné
txt := REPLACE(txt,'°','_');

return grhum.chaine_sans_accents(txt);

END;