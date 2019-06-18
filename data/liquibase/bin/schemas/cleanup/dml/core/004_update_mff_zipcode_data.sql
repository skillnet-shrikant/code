update mff_zipcode_usa set zipcode = CONCAT('0', TRIM(zipcode)) where length(TRIM(zipcode))=4;
update mff_zipcode_usa set zipcode = CONCAT('00', TRIM(zipcode)) where length(TRIM(zipcode))=3;
commit;
