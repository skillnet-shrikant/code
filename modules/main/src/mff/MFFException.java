package mff;

public class MFFException extends Exception {
  private static final long serialVersionUID = 1L;    // Version 
  
  /**
   * Default Constructor
   */
  public MFFException () {
    super();
  }
  
  /**
   * Constructs a new exception with the specified detail message.
   * @param pMessage
   *    Exception description 
   */
  public MFFException (String pMessage) {
    super (pMessage);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   * @param pMessage
   *    Exception Text
   * @param pCause
   *    The cause of the exception  
   */
  public MFFException (String pMessage, Exception pCause) {
    super (pMessage, pCause);
  }

  /**
   * Constructs a new exception with the specified cause.
   * @param pCause
   *    The cause of the exception
   */
  public MFFException (Exception pCause) {
    super (pCause);
  }
  
  public String toString() {
    String retValue = super.toString();
    if (getCause() != null) {
      retValue += " (cause: " + getCause() + ")";
    }
    return retValue;
  }
}
