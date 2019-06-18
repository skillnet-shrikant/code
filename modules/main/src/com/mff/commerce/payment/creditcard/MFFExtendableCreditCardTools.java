package com.mff.commerce.payment.creditcard;

import atg.payment.creditcard.CreditCardInfo;
import atg.payment.creditcard.ExtendableCreditCardTools;

/**
 * The following class overrides the default implementation to support
 * tokenization of credit card
 * 
 * @author savula
 *
 */
public class MFFExtendableCreditCardTools extends ExtendableCreditCardTools {

  @Override
  protected int verifyCreditCard(CreditCardInfo pCreditCard, String pNumber, String pType) {

    // Check to see if the credit card is tokenized. If it is we bypass the OOTB
    // check. The tokenized card has only the last 4 digits of the card number which
    // will not pass validation
    
    if (pNumber.length() > 4) {
      return super.verifyCreditCard(pCreditCard, pNumber, pType);
    } else {
      return SUCCESS;
    }

  }
  
}
