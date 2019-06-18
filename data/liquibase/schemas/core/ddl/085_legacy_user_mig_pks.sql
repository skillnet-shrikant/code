create or replace package legacy_user_migration
as

	procedure import_user_record (pSchema varchar2, pForce int);
	procedure update_legacy_flag (pSchema varchar2, pForce int);
	procedure import_address_book (pSchema varchar2, pForce int);
	procedure update_user_addr_book (pSchema varchar2, pForce int);
	procedure import_home_address (pSchema varchar2, pForce int);
	procedure import_billing_address (pSchema varchar2, pForce int);
	procedure import_shipping_address (pSchema varchar2, pForce int);
	procedure update_user_address (pSchema varchar2, pForce int);
	procedure import_tax_exemption (pSchema varchar2, pForce int);
	procedure import_tax_org_address (pSchema varchar2, pForce int);
	procedure update_user_org_address (pSchema varchar2, pForce int);
	procedure import_user_tax_exemption (pSchema varchar2, pForce int);
	procedure update_user_tax_exemption (pSchema varchar2, pForce int);
	procedure scrub_legacy_data (pSchema varchar2);
	procedure clean_legacy_data (pSchema varchar2, pUserIds varchar2 default null);
	procedure clean_imported_data;
	procedure clean_imported_records(pUserIds varchar2);
	procedure clean_legacy_records(pSchema varchar2, pUserIds varchar2);
	procedure delete_imported_users ;
	procedure get_migration_list(pSchema varchar2);
	procedure run (pSchema varchar2, pForce int default 0);
	procedure force_migrate_users (pSchema varchar2);

end legacy_user_migration;