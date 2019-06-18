package com.mff.commerce.order;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.csr.returns.MFFReturnTools;

import atg.commerce.csr.returns.RefundMethod;
import atg.commerce.csr.returns.ReturnException;
import atg.commerce.csr.returns.ReturnTools;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroupNotFoundException;
import atg.commerce.order.RefundTools;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

public class MFFGiftCardRefundMethod extends RefundMethod {

  /**
   * 
   */
  private static final long serialVersionUID = -5172031505721487095L;

  /**
   * The payment group associated with the gift card
   */
  private MFFGiftCardPaymentGroup paymentGroup;

  public MFFGiftCardRefundMethod(String pRefundType, double pAmount) {
    super(pRefundType, pAmount);
  }
  
  public MFFGiftCardRefundMethod (String pType) {
    this (pType, 0.0d);
  }

  /**
   * Returns a new refund method based whose payment group is associated with
   * the refund method. The payment group in this case will be the new payment
   * group that is being refunded as the refund
   * 
   * @param pPaymentGroup
   *          the gift card payment group with which the refund method is
   *          associated with.
   * @return the gift card refund method
   */
  public static MFFGiftCardRefundMethod getInstance(MFFGiftCardPaymentGroup pPaymentGroup) {
    MFFGiftCardRefundMethod lMethod = new MFFGiftCardRefundMethod(MFFReturnTools.GIFT_CARD_REFUND_TYPE, 0.0d);
    lMethod.setPaymentGroup(pPaymentGroup);
    return lMethod;
  }
  
  

  /* (non-Javadoc)
   * @see atg.commerce.csr.returns.RefundMethod#loadRefundMethod(atg.repository.RepositoryItem, atg.commerce.order.Order)
   */
  @Override
  public void loadRefundMethod(RepositoryItem pSource, Order pOrder) throws ReturnException {

    super.loadRefundMethod(pSource, pOrder);
    ReturnTools lReturnTools = ReturnTools.getReturnTools();
    String paymentGroupId = (String) pSource.getPropertyValue(lReturnTools.getPaymentGroupIdPropertyName());
    if (paymentGroupId != null) {
      MFFGiftCardPaymentGroup lGiftCard;
      try {
        lGiftCard = (MFFGiftCardPaymentGroup) pOrder.getPaymentGroup(paymentGroupId);
        setPaymentGroup(lGiftCard);
      } catch (PaymentGroupNotFoundException | InvalidParameterException e) {
        throw new ReturnException(e);
      }
      
    }
  }

  /* (non-Javadoc)
   * @see atg.commerce.csr.returns.RefundMethod#saveRefundMethod(atg.repository.MutableRepositoryItem)
   */
  @Override
  public void saveRefundMethod(MutableRepositoryItem pDestination) throws ReturnException {
    super.saveRefundMethod(pDestination);
    RefundTools refundTools = RefundTools.getRefundTools();
    pDestination.setPropertyValue(refundTools.getPaymentGroupIdPropertyName(), getPaymentGroup().getId());
  }

  public MFFGiftCardPaymentGroup getPaymentGroup() {
    return paymentGroup;
  }

  public void setPaymentGroup(MFFGiftCardPaymentGroup pPaymentGroup) {
    paymentGroup = pPaymentGroup;
  }

}
