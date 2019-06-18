package com.mff.commerce.csr.order;

import com.firstdata.order.MFFGiftCardPaymentGroup;

import atg.commerce.CommerceException;
import atg.commerce.csr.order.CSRPaymentGroupRemainingAmount;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;

public class MFFCSRPaymentGroupRemainingAmount extends CSRPaymentGroupRemainingAmount {

  @Override
  protected double getMaxAllowedAmount(double pRemainingAmount, PaymentGroup pPaymentGroup, Order pOrder) throws CommerceException {
    
    if (pPaymentGroup instanceof MFFGiftCardPaymentGroup) {
      return pRemainingAmount;
    } else {
      return super.getMaxAllowedAmount(pRemainingAmount, pPaymentGroup, pOrder);
    }
  }

  @Override
  public double getRemainingAmount(PaymentGroup pPaymentGroup, Order pOrder) throws CommerceException {
    
    if (pPaymentGroup instanceof MFFGiftCardPaymentGroup) {
      MFFGiftCardPaymentGroup giftCardPG = (MFFGiftCardPaymentGroup) pPaymentGroup;
      vlogDebug("Returning remaining balance amount = {0}", giftCardPG.getBalanceAmount());
      return giftCardPG.getBalanceAmount();
    } else {
      return super.getRemainingAmount(pPaymentGroup, pOrder);
    }
  }
  
  

}
