create or replace function f_get_category_product (pProductId in varchar2)
return varchar2
as
oCategoryId varchar2(40);
BEGIN
   select cat.category_id into oCategoryId from (select catprd.category_id,ROW_NUMBER() over (order by catprd.category_id desc) as row_number from ATG_CATA.dcs_category atgcategory
    inner join ATG_CATA.dcs_cat_chldprd catprd on catprd.category_id=atgcategory.category_id
    where atgcategory.start_date<=CURRENT_TIMESTAMP AND (atgcategory.end_date>=CURRENT_TIMESTAMP OR atgcategory.end_date IS NULL) AND catprd.child_prd_id=pProductId)cat
    where cat.row_number <2;
    return oCategoryId;
END;