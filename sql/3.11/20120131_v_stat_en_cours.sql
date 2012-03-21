create or replace force view dt3.v_stat_en_cours as
select i.int_ordre, int_date_creation, tra_date_fin, act_ordre, c_structure
from dt3.intervention i, (
    select t.int_ordre int_ordre, max(t.tra_date_fin) tra_date_fin
    from dt3.traitement t
    group by int_ordre) t
where i.int_ordre = t.int_ordre (+)
and i.int_ordre not in (
    select int_ordre
    from intervention
    where int_etat = 'T'
    and int_ordre not in (
        select int_ordre
        from traitement));