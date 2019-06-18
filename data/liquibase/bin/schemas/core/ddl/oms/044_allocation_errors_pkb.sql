create or replace package body allocation_errors as
  unknown_err exception;
  unknown_num number := -20001;
  unknown_msg varchar2(32767) := 'unknown error specified!';

  procedure raise_err(p_err number, p_msg varchar2 default null) as
    v_msg varchar2(32767);
  begin
    if p_err = unknown_num then
      v_msg := unknown_msg;
    elsif p_err = invalid_zip_num then
      v_msg := invalid_zip_msg;
    else
      raise_err(unknown_num, 'usr' || p_err || ': ' || p_msg);
    end if;

    if p_msg is not null then
      v_msg := v_msg || ' - '||p_msg;
    end if;

    raise_application_error(p_err, v_msg);
  end;
end;