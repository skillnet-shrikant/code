package com.mff.password.reset;

public class PasswordResetTokenException extends Exception {
  private static final long serialVersionUID = 1L;
  
  /**
   * Default Constructor
   */
  public PasswordResetTokenException () {
    super();
  }
  
  /**
   * Constructs a new exception with the specified detail message.
   * @param pMessage
   *    Exception description 
   */
  public PasswordResetTokenException (String pMessage) {
    super (pMessage);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   * @param pMessage
   *    Exception Text
   * @param pCause
   *    The cause of the exception  
   */
  public PasswordResetTokenException (String pMessage, Exception pCause) {
    super (pMessage, pCause);
  }
}
