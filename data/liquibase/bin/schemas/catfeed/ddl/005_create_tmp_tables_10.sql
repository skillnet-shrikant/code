 CREATE
   TABLE tmp_ext_price_csv
   (
 	  sku_id      		varchar2(40),
 	  effective_date    	varchar2(40),
 	  retail_price   	number(19,7),
 	  sale_price   		number(19,7),
 	  batch_id  		varchar2(40)
   )
    ORGANIZATION external
  (
    TYPE oracle_loader
    DEFAULT DIRECTORY price_feed_incoming
    ACCESS PARAMETERS
    (
      RECORDS DELIMITED BY NEWLINE
      BADFILE 'price.bad'
      LOGFILE 'price.log'
      READSIZE 1048576
      FIELDS TERMINATED BY "|" 
      missing field values are null
      (
 	sku_id,effective_date,retail_price,sale_price,batch_id
      )
    )
    location
    (
      'ECOM_PRC_20160516111040.dat'
    )
  )REJECT LIMIT UNLIMITED;