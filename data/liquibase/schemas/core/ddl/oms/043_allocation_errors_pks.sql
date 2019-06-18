create or replace package allocation_errors as
  invalid_zip_err exception;
  invalid_zip_num number := -20123;
  invalid_zip_msg varchar2(32767) := 'invalid zip!';
  pragma exception_init(invalid_zip_err, -20123);  

  procedure raise_err(p_err number, p_msg varchar2 default null);
end;