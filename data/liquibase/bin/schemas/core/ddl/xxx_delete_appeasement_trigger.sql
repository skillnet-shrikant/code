CREATE OR REPLACE TRIGGER appeasement_trigger
  BEFORE DELETE OR INSERT OR UPDATE ON dcspp_appeasement_reasons
  FOR EACH ROW
BEGIN
	Raise_Application_Error(-20000, 'No deletes allowed');	
END;
/

--DROP TRIGGER appeasement_trigger;