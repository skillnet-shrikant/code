create or replace
package body legacy_user_migration as

procedure scrub_legacy_data (pSchema varchar2)
as
v_sql varchar2(2000);
begin
	v_sql:='update ' || pSchema || '.dps_user ' ||
		'set login=substr(login,0,instr(login,''@'')-1) || replace(substr(login,instr(login,''@''),length(login)),''.'',''^'') ' ||
		'where login like ''%@%''';

	dbms_output.put_line(v_sql);
	execute immediate v_sql;
		
	v_sql:='update ' || pSchema || '.dps_user ' ||
		'set email=substr(email,0,instr(email,''@'')-1) || replace(substr(email,instr(email,''@''),length(email)),''.'',''^'') ' ||
		'where email like ''%@%''';
	
	dbms_output.put_line(v_sql);
	execute immediate v_sql;	
	
	commit;
end;

procedure clean_legacy_data (pSchema varchar2, pUserIds varchar2)
as
v_sql varchar2(2000);
v_filter varchar2(4000);
begin
	if pUserIds is not null then
		v_filter:= ' and id in (' || pUserIds || ')';
	end if;
	
	v_sql:='update ' || pSchema || '.dps_user ' ||
		'set login=replace(login,''^'',''.''), ' ||
			'email=replace(email,''^'',''.'') ' ||
		'where (login like ''%^%'' or email like ''%^%'')' || v_filter;

	dbms_output.put_line(v_sql);
	execute immediate v_sql;
	
	commit;
end;

procedure clean_imported_data 
as
v_sql varchar2(2000);
begin
	clean_legacy_data ('atg_core');

end;

procedure clean_imported_records (pUserIds varchar2)
as
v_sql varchar2(2000);
begin
	clean_legacy_data ('atg_core',pUserIds);

end;

procedure clean_legacy_records (pSchema varchar2, pUserIds varchar2) 
as
v_sql varchar2(2000);
begin
	clean_legacy_data (pSchema,pUserIds);

end;

procedure delete_imported_users
as
v_sql varchar2(2000);
begin
	delete from mff_tax_exemption where id like 'sfc%';
	delete from mff_user_org_address where id like 'sfc%';
	delete from mff_tax_exemptions where id like 'sfc%';
	delete from mff_user_tax_exmption where id like 'sfc%';
	delete from dps_user_address where id like 'sfc%';
	delete from dps_other_addr where user_id like 'sfc%';
	delete from dps_contact_info where id like 'sfc%';
	delete from mff_user where user_id like 'sfc%';
	delete from dcs_user where user_id like 'sfc%';
	delete from dps_user where id like 'sfc%';
	
	commit;
end;

procedure get_migration_list (pSchema varchar2)
as
v_sql varchar2(2000);
begin
	v_sql := 'truncate table migration_log';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;  
	
	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select a.id, a.login, a.email ' ||
			'from ' || pSchema || '.dps_user a ' ||
			'where exists ( ' ||
  				'select 1 from ' || pSchema || '.dps_user ' ||
  				'where lower(login) = lower(a.login) ' ||
  				'and rowid != a.rowid ' ||
			') ' ||
			'order by lower(a.login) ' ||
		') src ' ||
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''dupe login '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''dupe login '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;  	

	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select a.id, a.email, a.login ' ||
			'from ' || pSchema || '.dps_user a ' ||
			'where exists ( ' ||
  				'select 1 from ' || pSchema || '.dps_user ' ||
  				'where lower(email) = lower(a.email) ' ||
  				'and rowid != a.rowid ' ||
			') ' ||
			'order by lower(a.email) ' ||
		') src ' ||
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''dupe email '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''dupe email '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
	
	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select a.id, a.email, a.login ' ||
			'from ' || pSchema || '.dps_user a ' ||
			'where replace(login,''^'',''.'') NOT LIKE ''%_@_%.__%'' ' ||
		') src ' ||
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''malformed login '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''malformed login '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;

	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select a.id, a.email, a.login ' ||
			'from ' || pSchema || '.dps_user a ' ||
			'where replace(email,''^'',''.'') NOT LIKE ''%_@_%.__%'' ' ||
		') src ' ||
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''malformed email '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''malformed email '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;

	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select id, email, login ' ||
			'from ' || pSchema || '.dps_user ' ||
			'where replace(lower(login),''^'',''.'') in ( ' ||
				'select replace(lower(login),''^'',''.'') ' ||
					'from dps_user ' ||
					'where id not like ''sfc%'') ' ||
		') src ' || 
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''Login exists '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''Login exists '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;

	v_sql := 'merge into migration_log dest ' ||
		'using ( ' ||
			'select id, email, login ' ||
			'from ' || pSchema || '.dps_user ' ||
			'where replace(lower(email),''^'',''.'') in ( ' ||
				'select replace(lower(email),''^'',''.'') ' ||
					'from dps_user ' ||
					'where id not like ''sfc%'') ' ||
		') src ' || 
		'on (dest.id=src.id) ' ||
		'when matched then ' ||
  			'update set migrate_user=0, ' ||
  				'login=src.login, ' ||
  				'email=src.email, ' ||
  				'reject_reason = reject_reason || ''Email exists '' ' ||
		'when not matched then ' ||
  			'insert(id, ' ||
  				'login, ' ||
  				'email, ' ||
  				'migrate_user, ' ||
  				'reject_reason) ' ||
  			'values(src.id, ' ||
  				'src.login, ' ||
  				'src.email, ' ||
  				'0, ' ||
  				'''Email exists '')';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;

end;

procedure run (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
begin
	if pForce=0 then 
		get_migration_list(pSchema);
	end if;
	import_user_record (pSchema, pForce);
	update_legacy_flag (pSchema, pForce);
	import_address_book (pSchema, pForce);
	update_user_addr_book (pSchema, pForce);
	import_home_address (pSchema, pForce);
	import_billing_address (pSchema, pForce);
	import_shipping_address (pSchema, pForce);
	update_user_address (pSchema, pForce);
	import_tax_exemption (pSchema, pForce);
	import_tax_org_address (pSchema, pForce);
	update_user_org_address (pSchema, pForce);
	import_user_tax_exemption (pSchema, pForce);
	update_user_tax_exemption (pSchema, pForce);	
	commit;
end;

procedure force_migrate_users (pSchema varchar2)
as
begin
	run(pSchema,1);
end;

procedure import_user_record (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql := 'merge into dps_user dest ' ||
		 'using ( ' ||
			'select ''sfc-'' || id as id, ' ||
	        		'login, ' ||
	        		'auto_login, ' ||
	        		'member, ' ||
	        		'first_name, ' ||
	        		'middle_name, ' ||
	        		'last_name, ' ||
	        		'user_type, ' ||
	        		'locale, ' ||
	        		'registration_date, ' ||
	        		'email, ' ||
	        		'email_status, ' ||
	        		'receive_email, ' ||
	        		'gender, ' ||
	        		'date_of_birth, ' ||
	        		'securitystatus, ' ||
	        		'description ' ||
	  		'from ' || pSchema || '.dps_user ' ||
	  		v_filter ||
		  ') src ' ||
		  'on (dest.id=src.id) ' ||
		  'when not matched then ' ||
			'insert(id, login, auto_login, member, first_name, middle_name, last_name, user_type, ' ||
				'locale, registration_date, email, email_status, receive_email, gender, ' ||
				'date_of_birth, securitystatus, description ) ' ||
			'values(src.id, src.login, src.auto_login, src.member, src.first_name, src.middle_name, ' ||
				'src.last_name, src.user_type, ' ||
				'src.locale, src.registration_date, src.email, src.email_status, src.receive_email, src.gender, ' ||
				'src.date_of_birth, src.securitystatus, src.description)' ||
		  'when matched then ' ||
			'update set ' ||
				'dest.login=src.login, ' ||
				'dest.auto_login=src.auto_login, ' ||
				'dest.member=src.member, ' ||
				'dest.first_name=src.first_name, ' ||
				'dest.middle_name=src.middle_name, ' ||
				'dest.last_name=src.last_name, ' ||
				'dest.user_type=src.user_type, ' ||
				'dest.locale=src.locale, ' ||
				'dest.registration_date=src.registration_date, ' ||
				'dest.email=src.email, ' ||
				'dest.email_status=src.email_status, ' ||
				'dest.receive_email=src.receive_email, ' ||
				'dest.gender=src.gender, ' ||
				'dest.date_of_birth=src.date_of_birth, ' ||
				'dest.securitystatus=src.securitystatus, ' ||
				'dest.description=src.description';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure update_legacy_flag (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:= 'merge into mff_user dest ' ||
		'using ( ' ||
			'select ''sfc-'' || id as id from ' || pSchema || '.dps_user ' ||
			v_filter ||
		') src ' ||
		'on(src.id=dest.user_id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.is_legacy=1 ' ||
		'when not matched then ' ||
			'insert(user_id,is_legacy) ' ||
			'values(src.id,1)';
			
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_address_book (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:= 'merge into dps_contact_info dest ' ||
		'using ( ' ||
	 		'select ''sfc-'' || dci.id as id, ' ||
				'(case when dci.user_id is not null then ''sfc-'' || dci.user_id end) as user_id, ' ||
				'dci.prefix, ' ||
				'dci.first_name, ' ||
				'dci.middle_name, ' ||
				'dci.last_name, ' ||
				'dci.suffix, ' ||
				'dci.job_title, ' ||
				'dci.company_name, ' ||
				'dci.address1, ' ||
				'dci.address2, ' ||
				'dci.address3, ' ||
				'dci.city, ' ||
				'dci.state, ' ||
				'dci.postal_code, ' ||
				'dci.county, ' ||
				'dci.country, ' ||
				'dci.phone_number, ' ||
				'dci.fax_number ' ||
			'from ' || pSchema || '.dps_contact_info dci ' ||
			'where dci.id in ( ' ||
  				'select address_id from ' || pSchema || '.dps_other_addr ' ||
  				'where user_id in ( ' ||
  					'select id from ' || pSchema || '.dps_user ' ||
  					v_filter ||
  					') ' ||
  				') ' ||		
			') src ' ||
			'on (src.id=dest.id) ' ||
			'when matched then ' ||
				'update set ' ||
					'dest.prefix=src.prefix, ' ||
					'dest.first_name=src.first_name, ' ||
					'dest.middle_name=src.middle_name, ' ||
					'dest.last_name=src.last_name, ' ||
					'dest.suffix=src.suffix, ' ||
					'dest.job_title=src.job_title, ' ||
					'dest.company_name=src.company_name, ' ||
					'dest.address1=src.address1, ' ||
					'dest.address2=src.address2, ' ||
					'dest.address3=src.address3, ' ||
					'dest.city=src.city, ' ||
					'dest.state=src.state, ' ||
					'dest.postal_code=src.postal_code, ' ||
					'dest.county=src.county, ' ||
					'dest.country=src.country, ' ||
					'dest.phone_number=src.phone_number, ' ||
					'dest.fax_number=src.fax_number ' ||		
			 'when not matched then ' ||
				'insert(id, user_id, prefix, first_name, middle_name, last_name, suffix, job_title, ' ||
					'company_name, address1, address2, address3, city, state, postal_code, county, ' ||
					'country, phone_number, fax_number) ' ||
				'values(src.id, ' ||
					'src.user_id, ' ||
					'src.prefix, ' ||
					'src.first_name, ' ||
					'src.middle_name, ' || 
					'src.last_name, ' ||
					'src.suffix, ' ||
					'src.job_title, ' ||
					'src.company_name, ' ||
					'src.address1, ' ||
					'src.address2, ' ||
					'src.address3, ' ||
					'src.city, ' ||
					'src.state, ' ||
					'src.postal_code, ' ||
					'src.county, ' ||
					'src.country, ' ||
					'src.phone_number, ' ||
					'src.fax_number)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure update_user_addr_book (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql := 'merge into dps_other_addr dest ' ||
		 'using ( ' ||
			'select ''sfc-'' || user_id as user_id, ' ||
				'doa.tag, ' ||
				'''sfc-'' || address_id as address_id ' ||
			'from ' || pSchema || '.dps_other_addr doa ' ||
			'where user_id in ( ' ||
				'select id from ' || pSchema || '.dps_user ' ||
				v_filter ||
			') ' ||
		  ') src ' ||
		 'on (src.tag=dest.tag and src.user_id=dest.user_id) ' ||
		 'when matched then ' ||
			'update set ' ||
				'dest.address_id=src.address_id ' ||
		 'when not matched then ' ||
			'insert(user_id, tag, address_id) ' ||
			'values( src.user_id, ' ||
				'src.tag, ' ||
				'src.address_id)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;	
end;

procedure import_home_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql := 'merge into dps_contact_info dest ' ||
		 'using ( ' ||
	 		'select ''sfc-'' || dci.id as id, ' ||
				'(case when dci.user_id is not null then ''sfc-'' || dci.user_id end) as user_id, ' ||
				'dci.prefix, ' ||
				'dci.first_name, ' ||
				'dci.middle_name, ' ||
				'dci.last_name, ' ||
				'dci.suffix, ' ||
				'dci.job_title, ' ||
				'dci.company_name, ' ||
				'dci.address1, ' ||
				'dci.address2, ' ||
				'dci.address3, ' ||
				'dci.city, ' ||
				'dci.state, ' ||
				'dci.postal_code, ' ||
				'dci.county, ' ||
				'dci.country, ' ||
				'dci.phone_number, ' ||
				'dci.fax_number ' ||
			 'from ' || pSchema || '.dps_contact_info dci ' ||
			 'where dci.id in ( ' ||
  				'select home_addr_id from ' || pSchema || '.dps_user_address ' ||
  				'where id in (select id from ' || pSchema || '.dps_user ' ||
  				v_filter ||
  				') ' ||
  			 ') ' ||	
		 ') src ' ||
		 'on (src.id=dest.id) ' ||
		 'when matched then ' ||
			'update set ' ||
				'dest.prefix=src.prefix, ' ||
				'dest.first_name=src.first_name, ' ||
				'dest.middle_name=src.middle_name, ' ||
				'dest.last_name=src.last_name, ' ||
				'dest.suffix=src.suffix, ' ||
				'dest.job_title=src.job_title, ' ||
				'dest.company_name=src.company_name, ' ||
				'dest.address1=src.address1, ' ||
				'dest.address2=src.address2, ' ||
				'dest.address3=src.address3, ' ||
				'dest.city=src.city, ' ||
				'dest.state=src.state, ' ||
				'dest.postal_code=src.postal_code, ' ||
				'dest.county=src.county, ' ||
				'dest.country=src.country, ' ||
				'dest.phone_number=src.phone_number, ' ||
				'dest.fax_number=src.fax_number	' ||
			  'when not matched then ' ||
				'insert(id, user_id, prefix, first_name, middle_name, last_name, suffix, job_title, ' ||
					'company_name, address1, address2, address3, city, state, postal_code, county, ' ||
					'country, phone_number, fax_number) ' ||
				'values(src.id, ' ||
					'src.user_id, ' ||
					'src.prefix, ' ||
					'src.first_name, ' ||
					'src.middle_name, ' ||
					'src.last_name, ' ||
					'src.suffix, ' ||
					'src.job_title, ' ||
					'src.company_name, ' ||
					'src.address1, ' ||
					'src.address2, ' ||
					'src.address3, ' ||
					'src.city, ' ||
					'src.state, ' ||
					'src.postal_code, ' ||
					'src.county, ' ||
					'src.country, ' ||
					'src.phone_number, ' ||
					'src.fax_number)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_billing_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:= 'merge into dps_contact_info dest ' ||
		'using ( ' ||
	 		'select ''sfc-'' || dci.id as id, ' ||
				'(case when dci.user_id is not null then ''sfc-'' || dci.user_id end) as user_id, ' ||
				'dci.prefix, ' ||
				'dci.first_name, ' ||
				'dci.middle_name, ' ||
				'dci.last_name, ' ||
				'dci.suffix, ' ||
				'dci.job_title, ' ||
				'dci.company_name, ' ||
				'dci.address1, ' ||
				'dci.address2, ' ||
				'dci.address3, ' ||
				'dci.city, ' ||
				'dci.state, ' ||
				'dci.postal_code, ' ||
				'dci.county, ' ||
				'dci.country, ' ||
				'dci.phone_number, ' ||
				'dci.fax_number ' ||
			'from ' || pSchema || '.dps_contact_info dci ' ||
			'where dci.id in ( ' ||
  				'select billing_addr_id from ' || pSchema || '.dps_user_address ' ||
  				'where id in (select id from ' || pSchema || '.dps_user ' ||
  				v_filter ||
  				') ' ||
  			') ' ||	
		 ') src ' ||
		'on (src.id=dest.id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.prefix=src.prefix, ' ||
				'dest.first_name=src.first_name, ' ||
				'dest.middle_name=src.middle_name, ' ||
				'dest.last_name=src.last_name, ' ||
				'dest.suffix=src.suffix, ' ||
				'dest.job_title=src.job_title, ' ||
				'dest.company_name=src.company_name, ' ||
				'dest.address1=src.address1, ' ||
				'dest.address2=src.address2, ' ||
				'dest.address3=src.address3, ' ||
				'dest.city=src.city, ' ||
				'dest.state=src.state, ' ||
				'dest.postal_code=src.postal_code, ' ||
				'dest.county=src.county, ' ||
				'dest.country=src.country, ' ||
				'dest.phone_number=src.phone_number, ' ||
				'dest.fax_number=src.fax_number	' ||
		 'when not matched then ' ||
			'insert(id, user_id, prefix, first_name, middle_name, last_name, suffix, job_title, ' ||
				'company_name, address1, address2, address3, city, state, postal_code, county, ' ||
				'country, phone_number, fax_number) ' ||
			'values(src.id, ' ||
				'src.user_id, ' ||
				'src.prefix, ' ||
				'src.first_name, ' ||
				'src.middle_name, ' || 
				'src.last_name, ' ||
				'src.suffix, ' ||
				'src.job_title, ' ||
				'src.company_name, ' ||
				'src.address1, ' ||
				'src.address2, ' ||
				'src.address3, ' ||
				'src.city, ' ||
				'src.state, ' ||
				'src.postal_code, ' ||
				'src.county, ' ||
				'src.country, ' ||
				'src.phone_number, ' ||
				'src.fax_number)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_shipping_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql := 'merge into dps_contact_info dest ' ||
		'using ( ' ||
	 		'select ''sfc-'' || dci.id as id, ' ||
				'(case when dci.user_id is not null then ''sfc-'' || dci.user_id end) as user_id, ' ||
				'dci.prefix, ' ||
				'dci.first_name, ' ||
				'dci.middle_name, ' ||
				'dci.last_name, ' ||
				'dci.suffix, ' ||
				'dci.job_title, ' ||
				'dci.company_name, ' ||
				'dci.address1, ' ||
				'dci.address2, ' ||
				'dci.address3, ' ||
				'dci.city, ' ||
				'dci.state, ' ||
				'dci.postal_code, ' ||
				'dci.county, ' ||
				'dci.country, ' ||
				'dci.phone_number, ' ||
				'dci.fax_number ' ||
			'from ' || pSchema || '.dps_contact_info dci ' ||
			'where dci.id in ( ' ||
  				'select shipping_addr_id from ' || pSchema || '.dps_user_address ' ||
  				'where id in (select id from ' || pSchema || '.dps_user ' ||
  				v_filter ||
  				') ' ||
  			') ' ||
		') src ' ||
		'on (src.id=dest.id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.prefix=src.prefix, ' ||
				'dest.first_name=src.first_name, ' ||
				'dest.middle_name=src.middle_name, ' ||
				'dest.last_name=src.last_name, ' ||
				'dest.suffix=src.suffix, ' ||
				'dest.job_title=src.job_title, ' ||
				'dest.company_name=src.company_name, ' ||
				'dest.address1=src.address1, ' ||
				'dest.address2=src.address2, ' ||
				'dest.address3=src.address3, ' ||
				'dest.city=src.city, ' ||
				'dest.state=src.state, ' ||
				'dest.postal_code=src.postal_code, ' ||
				'dest.county=src.county, ' ||
				'dest.country=src.country, ' ||
				'dest.phone_number=src.phone_number, ' ||
				'dest.fax_number=src.fax_number ' ||
		'when not matched then ' ||
			'insert(id, user_id, prefix, first_name, middle_name, last_name, suffix, job_title, ' ||
				'company_name, address1, address2, address3, city, state, postal_code, county, ' ||
				'country, phone_number, fax_number) ' ||
			'values(src.id, ' ||
				'src.user_id, ' || 
				'src.prefix, ' || 
				'src.first_name, ' || 
				'src.middle_name, ' || 
				'src.last_name, ' || 
				'src.suffix, ' || 
				'src.job_title, ' || 
				'src.company_name, ' || 
				'src.address1, ' || 
				'src.address2, ' || 
				'src.address3, ' || 
				'src.city, ' || 
				'src.state, ' || 
				'src.postal_code, ' || 
				'src.county, ' || 
				'src.country, ' || 
				'src.phone_number, ' || 
				'src.fax_number)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure update_user_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into dps_user_address dest ' ||
		'using ( ' ||
			'select ''sfc-'' || id as id, ' ||
				'(case when home_addr_id is not null then ''sfc-'' || home_addr_id end) as home_addr_id, ' ||
				'(case when billing_addr_id is not null then ''sfc-'' || billing_addr_id end) as billing_addr_id, ' ||
				'(case when shipping_addr_id is not null then ''sfc-'' || shipping_addr_id end) as shipping_addr_id ' ||
			'from ' || pSchema || '.dps_user_address ' ||
			'where id in (select id from ' || pSchema || '.dps_user ' ||
			v_filter ||
			') ' ||
		') src ' ||
		'on(src.id=dest.id) ' ||
		'when not matched then ' ||
			'insert(id, home_addr_id, billing_addr_id, shipping_addr_id) ' ||
			'values(src.id, src.home_addr_id, src.billing_addr_id, src.shipping_addr_id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.home_addr_id=src.home_addr_id, ' ||
				'dest.billing_addr_id=src.billing_addr_id, ' ||
				'dest.shipping_addr_id=src.shipping_addr_id';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_tax_exemption (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into mff_tax_exemption dest ' ||
		'using ( ' ||
			'select ''sfc-org-'' || mte.id as id, ' ||
      				'mte.nick_name as nick_name, ' ||
      				'mte.tax_id as tax_id, ' ||
      				'mte.organization_name as organization_name, ' ||
      				'mte.business_description as business_desc, ' ||
      				'mte.merch_to_purchase_desc as mechandise, ' ||
      				'mte.tax_jurisdiction_city as tax_city, ' ||
      				'mte.tax_jurisdiction_state as tax_state, ' ||
      				'tm.current_id as tax_classification_id, ' ||  
      				'mtec.display_name as tax_classification_name, ' ||
      				'mtec.exmp_code as tax_classification_code ' || 
      			'from ' || pSchema || '.mff_tax_exemption mte, ' || 
  				'tax_mappings tm, ' || 
  				'atg_cata.mff_tax_exmp_classification mtec ' || 
  			'where mte.id in ( ' ||
      				'select tax_exemption_id ' || 
      				'from ' || pSchema || '.mff_user_tax_exemption ' || 
      				'where user_id in (select id from ' || pSchema || '.dps_user ' ||
      				v_filter ||
      				') ' ||
			') ' ||
			'and tm.legacy_id=mte.classification ' ||
			'and tm.current_id=mtec.id ' ||
		') src ' ||
		'on (src.id=dest.id) ' ||
		'when matched then ' || 
			'update set ' ||
				'dest.nick_name=src.nick_name, ' ||
				'dest.tax_id=src.tax_id, ' ||
				'dest.organization_name=src.organization_name, ' ||
				'dest.business_desc=src.business_desc, ' ||
				'dest.mechandise=src.mechandise, ' ||
				'dest.tax_city=src.tax_city, ' ||
				'dest.tax_state=src.tax_state, ' ||
				'dest.tax_classification_id=src.tax_classification_id, ' ||
				'dest.tax_classification_name=src.tax_classification_name, ' ||
				'dest.tax_classification_code=src.tax_classification_code ' ||
		'when not matched then ' ||
			'insert(id, nick_name, tax_id, organization_name, business_desc, mechandise, ' || 
				'tax_city, tax_state, tax_classification_id, tax_classification_name, ' || 
				'tax_classification_code) ' ||
			'values(src.id, src.nick_name, src.tax_id, src.organization_name, src.business_desc, ' || 
				'src.mechandise, src.tax_city, src.tax_state, src.tax_classification_id, ' || 
				'src.tax_classification_name, src.tax_classification_code)';

	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_tax_org_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into dps_contact_info dest ' ||
		'using ( ' ||
			'select ''sfc-org-addr-'' || mte.id as id, ' ||
      				'''sfc-org-'' || mte.id as user_id, ' ||
      				'mte.first_name as first_name, ' ||
				'mte.last_name as last_name, ' ||
				'mte.organization_address_1 as address1, ' ||
				'mte.organization_address_2 as address2, ' ||
				'mte.city as city, ' ||
				'mte.state_code as state, ' ||
				'mte.zip_code as postal_code ' ||
			'from ' || pSchema || '.mff_tax_exemption mte ' || 
			'where mte.id in ( ' ||
      				'select tax_exemption_id ' || 
      				'from ' || pSchema || '.mff_user_tax_exemption ' || 
      				'where user_id in (select id from ' || pSchema || '.dps_user ' ||
      				v_filter ||
      				') ' ||
			') ' ||
		') src ' ||
		'on(src.id=dest.id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.first_name=src.first_name, ' ||
				'dest.last_name=src.last_name, ' ||
				'dest.address1=src.address1, ' ||
				'dest.address2=src.address2, ' ||
				'dest.city=src.city, ' ||
				'dest.state=src.state, ' ||
				'dest.postal_code=src.postal_code ' ||
		'when not matched then ' ||
			'insert(id, user_id, first_name, last_name, address1, address2, city, state, postal_code) ' ||
			'values(src.id,src.user_id, src.first_name, src.last_name, src.address1, src.address2, src.city, src.state, src.postal_code)';

	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure update_user_org_address (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into mff_user_org_address dest ' ||
		'using ( ' ||
			'select ''sfc-org-'' || id as id, ' ||
  				'''sfc-org-addr-'' || id as org_addr_id ' ||
  			'from ' || pSchema || '.mff_tax_exemption where id in ( ' ||
        			'select tax_exemption_id ' ||
        			'from ' || pSchema || '.mff_user_tax_exemption ' ||
        			'where user_id in (select id from ' || pSchema || '.dps_user ' ||
        			v_filter ||
        			') ' ||
			') ' ||
		') src ' ||
		'on(src.id=dest.id) ' ||
		'when matched then ' || 
			'update set ' ||
				'dest.org_addr_id=src.org_addr_id ' ||
		'when not matched then ' ||
			'insert(id, org_addr_id) ' ||
			'values(src.id, src.org_addr_id)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure import_user_tax_exemption (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into mff_tax_exemptions dest ' ||
		'using ( ' ||
			'select distinct ''sfc-'' || mute.user_id as id, ' || 
				'nick_name as nick_name, ' || 
				'id as exemption_info_id ' || 
			'from mff_tax_exemption mte, ' || 
				pSchema || '.mff_user_tax_exemption mute ' ||
			'where mte.id in ( ' ||
				'select ''sfc-org-'' || tax_exemption_id as id ' ||
  				'from ' || pSchema || '.mff_user_tax_exemption ' || 
  				'where user_id in (select id from ' || pSchema || '.dps_user ' ||
  				v_filter ||
  				') ' ||
  			') ' ||
			'and mte.id=''sfc-org-''||mute.tax_exemption_id ' ||
		') src ' ||
		'on (src.id=dest.id and src.exemption_info_id=dest.exemption_info_id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.nick_name=src.nick_name ' ||
		'when not matched then ' ||
			'insert(id, nick_name, exemption_info_id) ' ||
			'values(src.id, src.nick_name, src.exemption_info_id)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

procedure update_user_tax_exemption (pSchema varchar2, pForce int)
as
v_sql varchar2(2000);
v_filter varchar2(2000);
begin
	v_filter := 'where id not in (select id from migration_log where migrate_user=0) ';
	if pForce=1 then
		v_filter := 'where id in (select id from migration_log where migrate_user=1) ';
	end if;
	
	v_sql:='merge into mff_user_tax_exmption dest ' ||
		'using ( ' ||
			'select ''sfc-'' || user_id as id, ' ||
  				'''sfc-org-'' || tax_exemption_id as tax_exmpt_id ' ||
			'from ' || pSchema || '.mff_user_tax_exemption ' || 
			'where user_id in (select id from ' || pSchema || '.dps_user ' ||
			v_filter ||
			') ' ||
				'and sequence_num=0 ' ||
		') src ' ||
		'on (src.id=dest.id) ' ||
		'when matched then ' ||
			'update set ' ||
				'dest.tax_exmpt_id=src.tax_exmpt_id ' ||
		'when not matched then ' ||
			'insert(id, tax_exmpt_id) ' ||
			'values(src.id, src.tax_exmpt_id)';
	dbms_output.put_line(v_sql);
	execute immediate v_sql;
end;

	

--*****************************************************************************
-- Initialization - only execute once
--*****************************************************************************
begin
  dbms_output.put_line('legacy_user_migration initalized');
end legacy_user_migration;