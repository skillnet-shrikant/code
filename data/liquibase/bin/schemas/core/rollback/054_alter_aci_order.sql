ALTER TABLE ACI_ORDER ADD (ACI_ORDER_ID VARCHAR2(40) DEFAULT null);
ALTER TABLE ACI_ORDER DROP(IS_FRAUD_CHALLENGE);
CREATE TABLE MFF_ACI_ORDER_INFO (
    ORDER_ID  VARCHAR2(40),
    ACI_ORDER_ID   VARCHAR2(40)
  );