package oms.commerce.settlement;

import java.util.HashMap;

import com.firstdata.order.MFFGiftCardPaymentGroup;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.repository.RepositoryException;

public interface SettlementManager {
  
  public void createDebitSettlements(Order pOrder, double pAmountToSettle, HashMap<String, Double> pSettlementMap) throws CommerceException, RepositoryException;

  public double createCCCreditSettlements(CreditCard pg, String pOrderNumber, String pOrderId, double pSettlementAmount, HashMap<String, Double> pSettlementMap) throws RepositoryException;

  public double calcAmountAvailableForRefund(CreditCard pPaymentGroup);

  public double calcAvailableGCFundsForRefund(Order pOrder);

  public double calcAvailableFundsForRefund(Order pOrder);

  public MFFGiftCardPaymentGroup createGiftCardPaymentGroupForRefund(Order pOrder, boolean pReturnsProcess) throws CommerceException;


  public void createPaymentSettlementsForGiftCardRefund(Order pOrder, double pSettlementAmount, MFFGiftCardPaymentGroup pGiftCardPaymentGroup) throws CommerceException;
  
  public void processSettlementRecords();
}
