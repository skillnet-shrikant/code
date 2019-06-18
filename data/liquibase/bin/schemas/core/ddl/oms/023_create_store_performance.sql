CREATE TABLE MFF_STORE_PERFORMANCE(
  store_id 			varchar2(40) NOT NULL,
  submitted_date	timestamp null,
  p_assigned		number(5,0),
  b_assigned		number(5,0),
  p_open			number(5,0),
  b_open			number(5,0),
  p_shipped			number(5,0),	
  b_shipped			number(5,0),
  p_rejected		number(5,0),
  b_rejected		number(5,0)
);
