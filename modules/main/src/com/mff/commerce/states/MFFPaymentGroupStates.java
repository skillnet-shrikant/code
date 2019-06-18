package com.mff.commerce.states;

import atg.commerce.states.PaymentGroupStates;

public class MFFPaymentGroupStates extends PaymentGroupStates {

  public static final String TO_BE_ACTIVATED = "to_be_activated";

  /**
   * The following payment group state is used by the gift card payment group.
   * When a return or an appeasement is processed on an order which was placed
   * using a gift card a new gift card is issued to the customer. This gift card
   * is represented as a payment group with $0 amount along with the original
   * payment group. When the settlement process runs this payment group's state
   * is set to TO_BE_FULFILLED so that the CSR can issue a gift card against the
   * gift card. Once the CSRs enters the gift card number and is ready for
   * activation the payment group state is set to TO_BE_ACTIVATED.
   */
  public static final String TO_BE_FULFILLED = "to_be_fulfilled";
  public static final String ERROR_GC_ACTIVATION = "error_gc_activation";

}
