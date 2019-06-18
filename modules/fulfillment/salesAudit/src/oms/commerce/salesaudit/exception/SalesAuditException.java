package oms.commerce.salesaudit.exception;

/**
 * This class extends the generic exception class for Sales Audit 
 * specific exceptions.
 * 
 * @author jvose
 *
 */
public class SalesAuditException 
  extends Exception {

    private static final long serialVersionUID  = 1L;

    public SalesAuditException() {
        super();
    }

    public SalesAuditException (String pMessage) {
        super(pMessage);
    }

    public SalesAuditException (String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }

    public SalesAuditException (Throwable pCause, String pMessage) {
        super(pMessage, pCause);
    }
    
    public SalesAuditException (Throwable pCause) {
        super(pCause);
    }

}


