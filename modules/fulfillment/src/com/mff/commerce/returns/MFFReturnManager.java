package com.mff.commerce.returns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.commerce.csr.returns.MFFReturnTools;
import com.mff.commerce.order.MFFGiftCardRefundMethod;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.prorate.MFFProrateItemManager;
import com.mff.constants.MFFConstants;
import com.mff.returns.MFFReturnItem;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.csr.returns.CreditCardRefundMethod;
import atg.commerce.csr.returns.RefundMethod;
import atg.commerce.csr.returns.ReturnException;
import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnManager;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.core.util.Range;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import oms.commerce.settlement.SettlementManager;


public class MFFReturnManager extends ReturnManager {

  private MFFProrateItemManager prorateItemManager;
  private Repository omsOrderRepository;
  private Map<String, String> returnReasonToShipRefund = new HashMap<String, String>();
  private Map<String, String> returnReasonToDispCode = new HashMap<String, String>();
  private SettlementManager settlementManager;

  private static String POS_ORIGIN_OF_RETURN = "pos";
  public SettlementManager getSettlementManager() {
    return settlementManager;
  }

  public void setSettlementManager(SettlementManager pSettlementManager) {
    settlementManager = pSettlementManager;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.returns.ReturnManager#getMaximumRefundAmount(atg.commerce.
   * order.CreditCard, java.lang.String)
   * 
   */
  @Override
  public double getMaximumRefundAmount(CreditCard pCreditCard, String pCurrencyCode) {
    vlogDebug("Entering getMaximumRefundAmount : pCreditCard, pCurrencyCode");
    double maxRefundAmount = -1D;

    if (pCreditCard != null) {
      maxRefundAmount = getSettlementManager().calcAmountAvailableForRefund(pCreditCard);
    }

    vlogDebug("maxRefundAmount set to {0}", maxRefundAmount);

    vlogDebug("Exiting getMaximumRefundAmount : pCreditCard, pCurrencyCode");
    return maxRefundAmount;
  }



  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.returns.ReturnManager#getRefundAmountRemaningRefundMethod(
   * )
   * 
   * Remove the remaining refund method (store credit) from the list
   */
  @Override
  protected RefundMethod getRefundAmountRemaningRefundMethod() throws ReturnException {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.returns.ReturnManager#calculateReturnItemSuggestedRefunds(
   * atg.commerce.csr.returns.ReturnRequest)
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public void calculateReturnItemSuggestedRefunds(ReturnRequest pReturnRequest) {
    vlogDebug("Inside calculateReturnItemSuggestedRefunds");
    List<MFFReturnItem> returnItemList = (List<MFFReturnItem>) pReturnRequest.getReturnItemList();
    // Look at the return items associated to the return request
    for (MFFReturnItem retItem : returnItemList) {
      if (retItem.getProRatedItemIds() == null || (retItem.getProRatedItemIds() != null && retItem.getProRatedItemIds().size() == 0)) {

        vlogDebug("Inside calculateReturnItemSuggestedRefunds: returnItem prorateItems are null/empty");
        try {
          RepositoryItem[] items = getProrateItemManager().getProrateItemByCIAndStatus(retItem.getCommerceItem().getId(), MFFConstants.PRORATE_STATE_INITIAL);
          if (null != items) {
            Set<String> pRatedItems = new HashSet<String>();
            for (int i = 0; i < retItem.getQuantityToReturn(); i++) {
              pRatedItems.add((String) items[i].getRepositoryId());
            }
            if (null != pRatedItems && pRatedItems.size() > 0) {
              retItem.setProRatedItemIds(pRatedItems);
            }
          }
        } catch (CommerceException e) {
          e.printStackTrace();
        }
      }
    }

    super.calculateReturnItemSuggestedRefunds(pReturnRequest);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.returns.ReturnManager#calculateRefundForItem(atg.commerce.
   * csr.returns.ReturnRequest, atg.commerce.csr.returns.ReturnItem,
   * atg.core.util.Range)
   */
  @Override
  protected double calculateRefundForItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem, Range pReturnRange) {
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateRefundForItem: started for return item: ").append(pReturnItem).toString());
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateRefundForItem: Range highbound: ").append(pReturnRange.getHighBound()).append(" highboundWithFraction: ").append(pReturnRange.getHighBoundWithFraction()).toString());
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateRefundForItem: Range lowbound: ").append(pReturnRange.getLowBound()).append(" lowboundWithFraction: ").append(pReturnRange.getLowBoundWithFraction()).toString());
    return calculateRefundFromProRatedItem(pReturnRequest, pReturnItem, pReturnRange, MFFConstants.PROPERTY_PRORATE_UNIT_PRICE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see atg.commerce.csr.returns.ReturnManager#calculateTaxRefundForItem(atg.
   * commerce.csr.returns.ReturnRequest, atg.commerce.csr.returns.ReturnItem,
   * atg.core.util.Range)
   */
  @Override
  protected double calculateTaxRefundForItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem, Range pReturnRange) {
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateTaxRefundForItem: started for return item: ").append(pReturnItem).toString());
    
    double totalrefund = calculateRefundFromProRatedItem(pReturnRequest, pReturnItem, pReturnRange, MFFConstants.PROPERTY_PRORATE_TAX);
    if (isReturnItemEligibleForShippingRefund(pReturnItem)) {
      totalrefund += calculateRefundFromProRatedItem(pReturnRequest, pReturnItem, pReturnRange, MFFConstants.PROPERTY_PRORATE_SHIPPING_TAX);
    }
    return totalrefund;
  }
  
  /**
   * The following method determines if a specific return reason code is
   * eligible for shipping refund. This in turn uses
   * {@link #getReturnReasonToShipRefund()} to determine if the return reason
   * code qualifies for a shipping refund.
   * 
   * @param pReturnReasonCode
   *          the return reason code
   * @return <code>true</true> if the return reason code qualifies for shipping
   *         return. It returns a <code>false</false> if the return reason code
   *         is blank or null or it does not qualify as defined by
   *         {@link #getReturnReasonToShipRefund()}.
   */
  public boolean isReturnItemEligibleForShippingRefund(String pReturnReasonCode) {

    boolean refundShipping = false;

    if (StringUtils.isNotBlank(pReturnReasonCode)) {
      refundShipping = Boolean.parseBoolean((String) getReturnReasonToShipRefund().get(pReturnReasonCode));
    } else {
      vlogWarning("Return reason is set to blank");
    }
    return refundShipping;
  }
  
  /**
   * The following method is responsible for determining if the return item is
   * eligible for shipping refund.
   * 
   * @param pReturnItem the return item
   * @return <code>true</code> if the item qualifies for shipping refund
   */
  public boolean isReturnItemEligibleForShippingRefund(ReturnItem pReturnItem) {

    boolean retValue = false;
    MFFReturnItem retItem = (MFFReturnItem) pReturnItem;

    if (retItem.isReturnShipping()) {
      retValue = true;
    } else {
      String lReturnReasonCode = retItem.getReturnReason();
      retValue = isReturnItemEligibleForShippingRefund(lReturnReasonCode);
    }
    return retValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * atg.commerce.csr.returns.ReturnManager#calculateRefundForItem(atg.commerce.
   * csr.returns.ReturnRequest, atg.commerce.csr.returns.ReturnItem,
   * atg.core.util.Range)
   */
  @Override
  protected double calculateShippingRefundForItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem, Range pReturnRange) {
    double totalrefund = 0.0D;
    if (isLoggingDebug()) logDebug((new StringBuilder()).append("calculateShippingRefundForItem: started for return item: ").append(pReturnItem).toString());
    
    if (isReturnItemEligibleForShippingRefund(pReturnItem)) {
      return calculateRefundFromProRatedItem(pReturnRequest, pReturnItem, pReturnRange, MFFConstants.PROPERTY_PRORATE_SHIPPING);
    } else {
      return totalrefund;
    }
  }

  @SuppressWarnings("rawtypes")
  public double calculateRefundFromProRatedItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem, Range pReturnRange, String pProRateItemProperty) {
    double totalrefund = 0.0D;
    MFFReturnItem retItem = (MFFReturnItem) pReturnItem;
    if (null != retItem.getProRatedItemIds()) {
      Order order = pReturnRequest.getOrder();
      String currencyCode = getPricingTools().getCurrencyCode(order, null, null);
      for (Iterator iterator = retItem.getProRatedItemIds().iterator(); iterator.hasNext();) {
        String proRatedItemId = (String) iterator.next();
        try {
          RepositoryItem prorateItem = getOmsOrderRepository().getItem(proRatedItemId, MFFConstants.ITEM_DESC_PRORATE_ITEM);
          if (prorateItem != null) {
            Double propAmount = (Double) prorateItem.getPropertyValue(pProRateItemProperty);
            totalrefund = getPricingTools().round(totalrefund + propAmount.doubleValue(), currencyCode);
          }
        } catch (RepositoryException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    if (Double.isNaN(totalrefund) || Double.isInfinite(totalrefund)) totalrefund = 0.0D;
    vlogDebug("Calculated refund for ci : {0} property : {1} total refund : {2}", pReturnItem.getCommerceItem().getId(), pProRateItemProperty, totalrefund);
    return totalrefund;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List issueCredits(Order pOrder, ReturnRequest pReturnRequest) throws ClaimableException, CommerceException, ReturnException, RepositoryException {
    
    return pReturnRequest.getRefundMethodList();
  }

  @Override
  public void buildRefundMethodList(Order pOriginalOrder, ReturnRequest pReturnRequest, double pStoreCreditAmount, double pTotalRefundAmount) throws ReturnException {
    vlogDebug("Entering buildRefundMethodList : pOriginalOrder, pReturnRequest, pStoreCreditAmount, pTotalRefundAmount");

    double remainingRefundAmount = pTotalRefundAmount;
    double maxSettledAmount = getSettlementManager().calcAvailableFundsForRefund(pOriginalOrder);

    // First determine if the maximum funds available for settlement does not
    // exceed that of the refund this can happen if appeasements were made to
    // the order prior to returns

    if (remainingRefundAmount > maxSettledAmount) {
      vlogDebug("The remaining amount is more that the funds available for settlement");
      // we allocate the "otherrefund" method an amount that is equal to
      // difference in amounts
      pReturnRequest.setOtherRefund(maxSettledAmount - remainingRefundAmount);
      remainingRefundAmount = maxSettledAmount;
    }
    vlogDebug("remaining refund amount set to {0}", remainingRefundAmount);
    populateRefundMethods(pOriginalOrder, pReturnRequest, remainingRefundAmount);
    vlogDebug("Exiting buildRefundMethodList : pOriginalOrder, pReturnRequest, pStoreCreditAmount, pTotalRefundAmount");

  }

  /**
   * 
   * The following method is responsible for creating appropriate refund
   * methods for a Return. This method first tries to refund the amount to the
   * credit card. After all credit cards funds are exhausted this method will
   * refund the balance amount to the gift card.
   * 
   * @param pOriginalOrder
   *          the order associated with the return
   * @param pRefundMethodList
   *          the list of valid refund methods
   * @param pAmtToBeRefunded
   *          the amount to be refunded
   * @throws ReturnException
   *           when commerce exception is thrown while creating the refund
   */
  @SuppressWarnings("unchecked")
  protected void populateRefundMethods(Order pOriginalOrder, ReturnRequest pRequest, double pAmtToBeRefunded) throws ReturnException {

    vlogDebug("Entering populateRefundMethods : pOriginalOrder, pRefundMethodList, pAmtToBeRefunded");

    // first we refund the credit cards followed by gift cards
    // the OOTB implementation does not generate a gift card refund payment
    // group since the getMaximumRefund
    List<RefundMethod> lRefundMethods = generateRefundMethodsFromOrder(pOriginalOrder);
    double allocatedAmt = 0.0d;

    // allocate the refund to the max allowable refund amounts on the credit
    // cards. The OOTB allocateRefundAmountForReturn only allocates the refund
    // to credit cards and not to Gift cards.
    if (lRefundMethods != null && !lRefundMethods.isEmpty()) {
      allocatedAmt = allocateRefundAmountForReturn(pOriginalOrder, pRequest, pAmtToBeRefunded, lRefundMethods);
    }

    // if the allocatedAmt does not cover all the funds that needs to be
    // refunded the balance amount needs to be refunded on a gift card.
    if (allocatedAmt < pAmtToBeRefunded) {

      double lRemainingRefundAmount = getPricingTools().round(pAmtToBeRefunded - allocatedAmt);
      vlogDebug("Remaining funds to be refunded = {0}", lRemainingRefundAmount);
      
      
      double lFundsAvailableForGCRefund = getSettlementManager().calcAvailableFundsForRefund(pOriginalOrder);
      vlogDebug("Funds available for GC refund = {0}", lFundsAvailableForGCRefund);
      
      if (lFundsAvailableForGCRefund >= lRemainingRefundAmount) {

        MFFGiftCardPaymentGroup refundPaymentGroup;

        try {

          refundPaymentGroup = getSettlementManager().createGiftCardPaymentGroupForRefund(pOriginalOrder, true);
          
          getOrderManager().updateOrder(pOriginalOrder);

        } catch (CommerceException e) {
          vlogError(e, "Error occurred while creating the gift card payment group");
          throw new ReturnException(e);
        }

        // now construct the refund method for the gift card
        MFFGiftCardRefundMethod giftCardRefundMethod = MFFGiftCardRefundMethod.getInstance(refundPaymentGroup);

        giftCardRefundMethod.setAmount(lRemainingRefundAmount);
        giftCardRefundMethod.setMaximumRefundAmount(lFundsAvailableForGCRefund);

        lRefundMethods.add(giftCardRefundMethod);

      } else {
        vlogError("The return funds could not be allocated to the available refund methods order id = {0}, amt to be refunded {1}", pOriginalOrder.getId(), pAmtToBeRefunded);
        throw new ReturnException("The return funds could not be allocated");
      }
    }
    pRequest.getRefundMethodList().clear();
    pRequest.getRefundMethodList().addAll(lRefundMethods);
    vlogDebug("Exiting populateRefundMethods : pOriginalOrder, pRefundMethodList, pAmtToBeRefunded");
  }

  /**
   * The following method is responsible for creating the settlement records for
   * the returns
   * 
   * @param pReturnRequest
   *          the return request
   * @throws ReturnException
   *           when a commerce exception when trying to create the refund amount
   */
  public void createSettlements(ReturnRequest pReturnRequest) throws ReturnException {

    vlogDebug("Entering createSettlements : pReturnRequest");

    if (pReturnRequest.getOriginOfReturn().equalsIgnoreCase(POS_ORIGIN_OF_RETURN)) {
      
      vlogDebug("We don't apply the credits for POS returns");
      
    } else {

      List<RefundMethod> lRefundMethodList = pReturnRequest.getRefundMethodList();
      MFFOrderImpl lOrder = (MFFOrderImpl) pReturnRequest.getOrder();
      MFFGiftCardPaymentGroup lGiftCardPaymentGroup = null;

      for (RefundMethod lRefundMethod : lRefundMethodList) {
        if (lRefundMethod.getRefundType().equals("creditCard")) {
          CreditCardRefundMethod lCCRefundMethod = (CreditCardRefundMethod) lRefundMethod;
          CreditCard lCC = lCCRefundMethod.getCreditCard();
          String lOrderNumber = lOrder.getOrderNumber();
          String lOrderId = lOrder.getId();
          double lRefundAmount = lCCRefundMethod.getAmount();

          try {
            getSettlementManager().createCCCreditSettlements(lCC, lOrderNumber, lOrderId, lRefundAmount, null);
          } catch (RepositoryException e) {
            vlogError("A repository exception occurred while creating a credit card settlement payment group id {0}, settlement amount {1}", lCC.getId(), lRefundAmount);
            throw new ReturnException(e);
          }

          // end of credit card refund type
        } else if (lRefundMethod.getRefundType().equals(MFFReturnTools.GIFT_CARD_REFUND_TYPE)) {

          lGiftCardPaymentGroup = ((MFFGiftCardRefundMethod) lRefundMethod).getPaymentGroup();
          // handle gift card refund type
          try {
            getSettlementManager().createPaymentSettlementsForGiftCardRefund(lOrder, lRefundMethod.getAmount(), lGiftCardPaymentGroup);
          } catch (CommerceException e) {
            vlogError("Commerce exception occurred while trying to refund {0} to gift card for the order {1}", lRefundMethod.getAmount(), pReturnRequest.getOrder().getId());
            throw new ReturnException(e);
          }
        } // end of else if loop
      } // end of the for loop

      // update the order
      try {
        getOrderManager().updateOrder(lOrder);
      } catch (CommerceException e) {
        vlogError("An exception occurred while updating the order {0}", lOrder.getId());
        throw new ReturnException(e);
      }
    }
    vlogDebug("Exiting createSettlements : pReturnRequest");
  }


  /**
   * @return the prorateItemManager
   */
  public MFFProrateItemManager getProrateItemManager() {
    return prorateItemManager;
  }

  /**
   * @param prorateItemManager
   *          the prorateItemManager to set
   */
  public void setProrateItemManager(MFFProrateItemManager prorateItemManager) {
    this.prorateItemManager = prorateItemManager;
  }

  /**
   * @return the omsOrderRepository
   */
  public Repository getOmsOrderRepository() {
    return omsOrderRepository;
  }

  /**
   * @param omsOrderRepository
   *          the omsOrderRepository to set
   */
  public void setOmsOrderRepository(Repository omsOrderRepository) {
    this.omsOrderRepository = omsOrderRepository;
  }

  /**
   * @return the returnReasonToShipRefund
   */
  public Map<String, String> getReturnReasonToShipRefund() {
    return returnReasonToShipRefund;
  }

  /**
   * @param returnReasonToShipRefund
   *          the returnReasonToShipRefund to set
   */
  public void setReturnReasonToShipRefund(Map<String, String> returnReasonToShipRefund) {
    this.returnReasonToShipRefund = returnReasonToShipRefund;
  }

  /**
   * @return the returnReasonToDispCode
   */
  public Map<String, String> getReturnReasonToDispCode() {
    return returnReasonToDispCode;
  }

  /**
   * @param returnReasonToDispCode
   *          the returnReasonToDispCode to set
   */
  public void setReturnReasonToDispCode(Map<String, String> returnReasonToDispCode) {
    this.returnReasonToDispCode = returnReasonToDispCode;
  }

}
