create table mff_employee ( 
	employee_id   			varchar2(255) not null enable,
	som_card			varchar2(255) not null enable,
	phone_number			varchar2(255) not null enable,
	constraint mff_employee_p primary key (employee_id) enable
); 


CREATE INDEX mff_emp_som_ph_idx ON mff_employee (som_card,phone_number);
