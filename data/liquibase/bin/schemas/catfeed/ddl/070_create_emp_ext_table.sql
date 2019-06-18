CREATE
  TABLE TMP_EXT_EMP_CSV
  (
    employee_id         	VARCHAR2(255),
    som_card      		VARCHAR2(255),
    phone_number       		VARCHAR2(255)
  )
   ORGANIZATION external
 (
   TYPE oracle_loader
   DEFAULT DIRECTORY emp_feed_incoming
   ACCESS PARAMETERS
   (
     RECORDS DELIMITED BY NEWLINE
     BADFILE 'emp.bad'
     LOGFILE 'emp.log'
     READSIZE 1048576
     FIELDS TERMINATED BY "|" 
     missing field values are null
     (
    employee_id,som_card,phone_number
     )
   )
   location
   (
     'ECOM_EMP_20160516101425.dat'
   )
 )REJECT LIMIT UNLIMITED;