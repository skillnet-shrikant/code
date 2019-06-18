create table csr_site_config (
        id      varchar2(40)    not null,
        dis_priority    number(10)      null
,constraint csr_site_config_p primary key (ID)
,constraint csr_site_config_f foreign key (id) references site_configuration (id));