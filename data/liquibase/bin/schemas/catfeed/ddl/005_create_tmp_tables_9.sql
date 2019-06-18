CREATE
  TABLE TMP_EXT_INV_CSV
  (
    retailer_id         VARCHAR2(40),
    SKU_ID      VARCHAR2(40),
    STORE_ID           VARCHAR2(40),
    THRESHOLD          number,
    AVAIL_QTY      number,
    UNIT_RETAIL number,
    TAX_RATE   VARCHAR2(40),
    SELL_THRU  VARCHAR2(40)
  )
   ORGANIZATION external
 (
   TYPE oracle_loader
   DEFAULT DIRECTORY inv_feed_incoming
   ACCESS PARAMETERS
   (
     RECORDS DELIMITED BY NEWLINE
     BADFILE 'invent.bad'
     LOGFILE 'invent.log'
     READSIZE 1048576
     FIELDS TERMINATED BY "|" 
     missing field values are null
     (
    retailer_id,SKU_ID,STORE_ID,THRESHOLD,AVAIL_QTY,UNIT_RETAIL,TAX_RATE,SELL_THRU
     )
   )
   location
   (
     'ECOM_INV_20160516101425.dat'
   )
 )REJECT LIMIT UNLIMITED;
 
