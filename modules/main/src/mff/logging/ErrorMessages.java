package mff.logging;

/**
 * This is a collection of error code and messages that are logged by the application
 * and need to be monitored by the hosting center.  The error codes consist of a error 
 * type and a unique identifier as shown below. 
 * 
 * The errors will be structured as follows:
 * 
 *      <System>-<Error Type><Error Number>-<Severity> - <Descriptive Message>
 *      
 *      where <System> is "MFF" for all errors.
 *        
 *      Where Error Type is one of the following:      
 *         - "S" - Store 
 *         - "C" - Call Center
 *         - "F" - Fulfillment
 *         - "U" - Util
 *         
 *      Where Severity is one of the following:      
 *         - "blocker"  (BLOCKER)   - Severe, site is not functional 
 *         - "critical" (CRITICAL)  - Prevents functionality from being used, and no workaround exists
 *         - "major"    (MAJOR)     - Prevents functionality from being used, but a workaround does exist
 *         - "normal"   (NORMAL)    - Issue which makes functionality difficult to use, but no workaround is required
 * 
 */
public class ErrorMessages {
	  public static final String MFF_U1000_CRITICAL_CODE = "MFF-U1000-CRITIAL";
	  public static final String MFF_U1000_CRITICAL_MSG = MFF_U1000_CRITICAL_CODE + " - Full Inventory Import failed";
	  
	  public static final String MFF_U1000_1_CRITICAL_CODE = "MFF-U1000.1-CRITIAL";
	  public static final String MFF_U1000_1_CRITICAL_MSG = MFF_U1000_1_CRITICAL_CODE + " - Inventory Create Trans Records failed";

	  public static final String MFF_U1000_2_CRITICAL_CODE = "MFF-U1000.2-CRITIAL";
	  public static final String MFF_U1000_2_CRITICAL_MSG = MFF_U1000_2_CRITICAL_CODE + " - Full Inventory Reset shipped counter failed";

	  public static final String MFF_U1000_3_CRITICAL_CODE = "MFF-U1000.3-CRITIAL";
	  public static final String MFF_U1000_3_CRITICAL_MSG = MFF_U1000_3_CRITICAL_CODE + " - Full Inventory PPS Inventory Adjustment failed";
	  
	  public static final String MFF_U2000_CRITICAL_CODE = "MFF-U2000-MAJOR";
	  public static final String MFF_U2000_CRITICAL_MSG = MFF_U2000_CRITICAL_CODE + " - Delta Inventory Import failed";

	  public static final String MFF_U2000_1_CRITICAL_CODE = "MFF-U2000.1-MAJOR";
	  public static final String MFF_U2000_1_CRITICAL_MSG = MFF_U2000_1_CRITICAL_CODE + " - Delta Inventory Create Trans Records failed";
	  
	  public static final String MFF_U3000_CRITICAL_CODE = "MFF-U3000-MAJOR";
	  public static final String MFF_U3000_CRITICAL_MSG = MFF_U3000_CRITICAL_CODE + " - Price Import failed";	  
	  
	  public static final String MFF_U4000_CRITICAL_CODE = "MFF-U4000-MAJOR";
	  public static final String MFF_U4000_CRITICAL_MSG = MFF_U4000_CRITICAL_CODE + " - Catalog Import failed";
	  
	  public static final String MFF_U5000_CRITICAL_CODE = "MFF-U5000-MAJOR";
	  public static final String MFF_U5000_CRITICAL_MSG = MFF_U5000_CRITICAL_CODE + " - Facet Import failed"; 	  

	  public static final String MFF_U6000_CRITICAL_CODE = "MFF-U6000-MAJOR";
	  public static final String MFF_U6000_CRITICAL_MSG = MFF_U6000_CRITICAL_CODE + " - Computed Properties Service failed"; 
	  
	  public static final String MFF_U7000_CRITICAL_CODE="MFF-U7000-Major";
	  public static final String MFF_U7000_CRITICAL_MSG=MFF_U7000_CRITICAL_CODE +" - BV Import failed";
	  

	  public static final String MFF_U8000_CRITICAL_CODE = "MFF-U8000-MAJOR";
	  public static final String MFF_U8000_CRITICAL_MSG = MFF_U8000_CRITICAL_CODE + " - Planogram Import failed";
	  
	  public static final String MFF_U9000_CRITICAL_CODE="MFF-U9000-Major";
	  public static final String MFF_U9000_CRITICAL_MSG=MFF_U9000_CRITICAL_CODE +" - Employee Import failed";	  

	


}
