create table TMP_EMP_CSV
(
    employee_id         	VARCHAR2(255),
    som_card      		VARCHAR2(255),
    phone_number       		VARCHAR2(255),
    is_changed			number(1) default 0,
    is_new			number(1) default 0
);