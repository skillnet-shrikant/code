package com.mff.commerce.csr.order.appeasement;

import java.util.List;
import java.util.Set;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.order.MFFGiftCardRefundMethod;
import com.mff.commerce.order.MFFRefundTools;

import atg.commerce.CommerceException;
import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.order.appeasement.CSRAppeasementTools;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.Refund;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import oms.commerce.settlement.SettlementManager;

public class MFFCSRAppeasementTools extends CSRAppeasementTools {
  
  private SettlementManager settlementManager;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public MutableRepositoryItem createAppeasementItem(Appeasement pAppeasement) throws CommerceException {
    vlogDebug("Entering createAppeasementItem : pAppeasement");

    // The OOTB API does not handle the Gift Card refunds. We use the OOTB
    // API to handle the credit card transactions and create the gift card
    // appeasements in this method
    TransactionDemarcation td = new TransactionDemarcation();
    MutableRepositoryItem lAppeasementItem = super.createAppeasementItem(pAppeasement);

    boolean rollback = true;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      MutableRepository orderRepository = (MutableRepository) getCSRAgentTools().getOrderManager().getOrderTools().getOrderRepository();

      Set refundItems = (Set) lAppeasementItem.getPropertyValue(getRefundMethodsPropertyName());

      // Determine if there are any gift card appeasement refund methods
      List<Refund> refunds = pAppeasement.getRefundList();

      for (Refund refund : refunds) {
        
        double lRefundAmount = refund.getAmount();
        if (refund instanceof MFFGiftCardRefundMethod && lRefundAmount > 0) {
          vlogDebug("Persisting the gift card refund method and adding it to the appeasement refunds");

          MutableRepositoryItem giftCardRefundItem;

          MFFRefundTools lRefundTools = (MFFRefundTools) getRefundTools();

          // create the gift card payment group and attach it to the order
          MFFGiftCardPaymentGroup giftCardRefundPaymentGroup = getSettlementManager().createGiftCardPaymentGroupForRefund(pAppeasement.getOriginatingOrder(), false);

          ((MFFGiftCardRefundMethod) refund).setPaymentGroup(giftCardRefundPaymentGroup);

          try {
            giftCardRefundItem = orderRepository.createItem(lRefundTools.getAppeasementGiftCardRefundItemDescriptorName());
            refund.saveRefund(giftCardRefundItem);
            refundItems.add(giftCardRefundItem);

            // there is only one gift card refund item so we just save and break
            // the loop
            lAppeasementItem.setPropertyValue(getRefundMethodsPropertyName(), refundItems);
            orderRepository.updateItem(lAppeasementItem);

            // exit the for loop
            break;
          } catch (RepositoryException e) {
            vlogError(e, "A repository error occurred while creating gift card refund for {0}", pAppeasement);
            throw new CommerceException(e);
          }

        }
      }
      rollback = false;
    } catch (TransactionDemarcationException e) {
      vlogError(e, "A transaction demarcation occurred while trying to being the transaction");
      throw new CommerceException(e);
    } finally {
      try {
        td.end(rollback);
      } catch (TransactionDemarcationException e) {
        vlogError(e, "A transaction demarcation occurred while trying to commit the transaction");
        throw new CommerceException(e);
      }
    }
    vlogDebug("Exiting createAppeasementItem : pAppeasement");
    return lAppeasementItem;
  }

  @Override
  protected double getMaximumRefundAmount(PaymentGroup pPaymentGroup) throws CommerceException {
    double maxRefundAmount = -1.0D;
    
    if (pPaymentGroup instanceof CreditCard) {
      maxRefundAmount = getSettlementManager().calcAmountAvailableForRefund((CreditCard)pPaymentGroup);
    }
    return maxRefundAmount;
  }

  @Override
  public List<Refund> generateRefundMethodsFromOrder(Order pOrder) throws CommerceException {

    vlogDebug("Entering generateRefundMethodsFromOrder : pOrder");

    // The OOTB api generates the credit refund methods
    List<Refund> refundMethods = super.generateRefundMethodsFromOrder(pOrder);

    double gcRefundAmount = getSettlementManager().calcAvailableGCFundsForRefund(pOrder);

    vlogDebug("The gift card refund method has the following amount available {0}", gcRefundAmount);
    if (gcRefundAmount > 0.0d) {

      // there is amount available to be refunded to gift card
      // At this point of time we just create the refund method and create the
      // payment group as part of saveAppeasmenet processor (createAppeasement)
      // method.

      MFFGiftCardRefundMethod giftCardRefundMethod = MFFGiftCardRefundMethod.getInstance(null);
      giftCardRefundMethod.setAmount(0.0d);
      giftCardRefundMethod.setMaximumRefundAmount(gcRefundAmount);

      refundMethods.add(giftCardRefundMethod);
    }

    vlogDebug("Exiting generateRefundMethodsFromOrder : pOrder");
    return refundMethods;
  }

    
  
  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }
  
}
