package com.mff.commerce.order.purchase;

import atg.commerce.CommerceException;

public class MFFOrderUpdateException extends CommerceException
{
  //-------------------------------------
  /**
   * Constructs a new MFFOrderUpdateException
   */
  public MFFOrderUpdateException() {
    super();
  }

    /**
   * Constructs a new MFFOrderUpdateException with the given
   * explanation.
   **/
  public MFFOrderUpdateException(String pStr) {
    super(pStr);
  }

  /**
   * Constructs a new MFFOrderUpdateException.
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public MFFOrderUpdateException(Throwable pSourceException) {
    super(pSourceException);
  }

  /**
   * Constructs a new MFFOrderUpdateException with the given
   * explanation.
   * @param pStr an explanation of the exception
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public MFFOrderUpdateException(String pStr, Throwable pSourceException) {
    super(pStr, pSourceException);
  }
} // end of class
