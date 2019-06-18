create table mff_zipcode_usa as (SELECT * FROM ZIPCODE_USA WHERE id IN (SELECT MIN(id) FROM ZIPCODE_USA GROUP BY zipcode));
alter table mff_zipcode_usa add constraint mff_zipcode_usa_pk primary key(zipcode);