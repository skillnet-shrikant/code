create or replace package mff_settlement_failures
as
	procedure cleanup_settlement_failures;
	procedure load_nosettlement_records;
	procedure load_noauthcode_records;
	procedure run;
end mff_settlement_failures;