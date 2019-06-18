package oms.commerce.salesaudit.test;

import java.util.ArrayList;
import java.util.List;

import com.mff.commerce.order.MFFCommerceItemImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import oms.commerce.salesaudit.job.SalesAuditScheduler;
import oms.commerce.test.TestSettlementManager;



public class TestSalesAuditManager extends TestSettlementManager {

  SalesAuditScheduler salesAuditScheduler;
  
  public void testSalesAuditShippingDiscounts() throws Throwable {
    //createOrderWithCreditCard(2);
    createOrderWithGiftCard(2);
    submitOrder();
    forceAllocateOrder();
    
    shipItemAndDeclineOrder();
  }
  
  @SuppressWarnings("unchecked")
  protected void shipItemAndDeclineOrder () throws Throwable {
    getTestAllocationPipeline().setStoreProcessOrderId(getOrderId());
    Order omsOrder = getOmsOrder();
    List<MFFCommerceItemImpl> lCommerceItems = omsOrder.getCommerceItems();
    if (lCommerceItems.size() < 2) {
      throw new CommerceException("Number of commerce item is less than zero");
    }
    ArrayList<String> itemsToShip = new ArrayList<String>();
    ArrayList<String> itemsToDecline = new ArrayList<String>();
    
    itemsToShip.add(lCommerceItems.get(0).getId());
    itemsToDecline.add(lCommerceItems.get(1).getId());
    
    getTestAllocationPipeline().setItemsToShip(itemsToShip);
    getTestAllocationPipeline().setItemsToDecline(itemsToDecline);
    getTestAllocationPipeline().runStoreProcessPipeline();
    //getSalesAuditScheduler().forceScheduledTask();
    
    forceAllocateOrder();
    
    shipCommerceItems(itemsToDecline);
    
    getSalesAuditScheduler().forceScheduledTask();
  }

  public SalesAuditScheduler getSalesAuditScheduler() {
    return salesAuditScheduler;
  }

  public void setSalesAuditScheduler(SalesAuditScheduler pSalesAuditScheduler) {
    salesAuditScheduler = pSalesAuditScheduler;
  }
   
}
