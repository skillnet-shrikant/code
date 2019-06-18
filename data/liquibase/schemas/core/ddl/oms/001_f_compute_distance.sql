-- *************************************************************************************
--  Procedure: p_compute_distance
-- Parameters
--   pLatitude1       - Latitude for position 1
--   pLongitude1      - Longitude for position 1
--   pLatitude2       - Latitude for position 2
--   pLongitude2      - Longitude for position 2
-- 
--   Summary
--   This procedure will compute the distance in miles between two positions on earth.
--
--   NOTE:
--   You cannot use Google maps to test the distances returned from the stored procedure,
--   since Google returns driving directions and this procedure returns the straight line 
--   distance between two points.
--
--   Use http://www.daftlogic.com/projects-google-maps-distance-calculator.htm to test
--   the results from this stored procedure.
--
-- *************************************************************************************
create or replace
function f_compute_distance (pLatitude1  in number,
                              pLongitude1 in number,
                              pLatitude2  in number,
                              pLongitude2 in number) 
	return number 
as
  DEFAULT_RADIUS  	number := 3963;
  DEGREES_TO_RAD  	number := 57.29577951;
  distance 			number (5,1);

BEGIN
    SELECT (NVL(DEFAULT_RADIUS,0) * ACOS((sin(NVL(pLatitude1,0) / DEGREES_TO_RAD) * SIN(NVL(pLatitude2,0) / DEGREES_TO_RAD)) + 
		   (COS(NVL(pLatitude1,0) / DEGREES_TO_RAD) * COS(NVL(pLatitude2,0) / DEGREES_TO_RAD) * COS(NVL(pLongitude2,0) / DEGREES_TO_RAD - NVL(pLongitude1,0)/ DEGREES_TO_RAD)))) 
		   into distance 
		   from dual;
	return distance; 
END;