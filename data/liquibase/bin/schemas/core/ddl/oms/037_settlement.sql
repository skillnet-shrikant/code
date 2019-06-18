-- Creates the tables used for settlement information

CREATE TABLE oms_settlement
(
        settlement_id                   VARCHAR(40) NOT NULL,
        create_date                     TIMESTAMP,
        order_id                        VARCHAR(40) NOT NULL,
        order_number                    VARCHAR(40) NOT NULL,
        pg_id                           VARCHAR(40) NOT NULL,
        settlement_status               NUMBER(1) NOT NULL,
        settlement_retry_count          NUMBER(1),
        pg_desc                         VARCHAR(50),
        part_settlement                 NUMBER(1),
        settlement_type                 NUMBER(1),
        amount                          NUMBER(19,7),
        settle_date                     TIMESTAMP,
        error_message                   VARCHAR(1000),
        CONSTRAINT oms_settlement_pk PRIMARY KEY (settlement_id)
);

CREATE INDEX oms_settlement_idx1 ON oms_settlement(order_id,pg_id,create_date);

CREATE TABLE oms_pg_settlement
(
        settlement_id   VARCHAR(40) NOT NULL,
        invoice_id      VARCHAR(100) NOT NULL,
        create_date     TIMESTAMP,      
        amount          NUMBER(19,7),
        CONSTRAINT oms_pg_settlement_pk PRIMARY KEY (settlement_id)
);

CREATE TABLE oms_pg_settlement_rel
(
        payment_group_id   VARCHAR(40) NOT NULL,   
        settlement_id      VARCHAR(40) NOT NULL,
        seq_nbr            NUMBER,
        CONSTRAINT oms_pg_settlement_rel_pk PRIMARY KEY (payment_group_id, seq_nbr),
        CONSTRAINT oms_pg_settlement_rel_fk1 FOREIGN KEY (payment_group_id) REFERENCES dcspp_pay_group(payment_group_id),
        CONSTRAINT oms_pg_settlement_rel_fk2 FOREIGN KEY (settlement_id) REFERENCES oms_pg_settlement(settlement_id)
);

CREATE TABLE oms_pg_gc_settlement
(
        gc_settlement_id   VARCHAR(40) NOT NULL,
        gc_num             VARCHAR(100) NOT NULL,
        gc_pin             VARCHAR(100) NOT NULL,
        create_date        TIMESTAMP,      
        amount             NUMBER(19,7),
        CONSTRAINT oms_pg_gc_settlement_pk PRIMARY KEY (gc_settlement_id)
);

CREATE TABLE oms_pg_gc_settlement_rel
(
        payment_group_id   VARCHAR(40) NOT NULL,   
        gc_settlement_id   VARCHAR(40) NOT NULL,
        seq_nbr            NUMBER,
        CONSTRAINT oms_pg_gc_settlement_rel_pk PRIMARY KEY (payment_group_id, seq_nbr),
        CONSTRAINT oms_pg_gc_settlement_rel_fk1 FOREIGN KEY (payment_group_id) REFERENCES dcspp_pay_group(payment_group_id),
        CONSTRAINT oms_pg_gc_settlement_rel_fk2 FOREIGN KEY (gc_settlement_id) REFERENCES oms_pg_gc_settlement(gc_settlement_id)
);
