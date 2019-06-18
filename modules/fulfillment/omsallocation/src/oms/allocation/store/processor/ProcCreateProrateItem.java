package oms.allocation.store.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingAdjustment;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.MFFOMSOrderManager;

/**
 * 
 * @author vsingh
 *
 */
public class ProcCreateProrateItem extends GenericService implements PipelineProcessor{
  
  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    
    vlogDebug("Entering ProcCreateStoreAllocations - runProcess");
    
    Map lParams       = (Map) pPipelineParams;
    Order lOrder      = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    List<String> lItemsToShip = (List<String>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP);
    
    if (lItemsToShip == null || lItemsToShip.size() < 1) {
      vlogDebug("ProcCreateProrateItem : No items to ship were found for orderId {0}", lOrder.getId());
      return SUCCESS;
    }
    
    double orderTotal = getItemTotal((MFFOrderImpl)lOrder);
    for (String lCommerceItemId : lItemsToShip) {
      processCommerceItem(lOrder,lCommerceItemId,orderTotal);
    }
    return SUCCESS;
  }
  
  protected void processCommerceItem(Order pOrder, String pCommerceItemId,double pOrderTotal) throws Exception{
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    MFFCommerceItemImpl lCommerceItem = getCommerceItem(lOrder, pCommerceItemId);
    
    /*if(getOmsOrderManager().isGiftCardItem(lCommerceItem)){
      vlogInfo("Skipped creating prorate item for order {0} giftCard commerceItem {1}",lOrder.getId(),lCommerceItem.getId());
      return;
    }*/
    // split CommerceItem and iterate over the list
    List<SplitItem> splitItems = splitCommerceItem(lOrder,lCommerceItem, pOrderTotal);
    Set<String> returnItemIds = new HashSet<String>();
    
    for(SplitItem splitItem : splitItems){
      addProrateItem(lOrder,lCommerceItem,splitItem,returnItemIds);
    }
    lCommerceItem.setReturnItemIds(returnItemIds);
  }
  
  private List<SplitItem> splitCommerceItem(MFFOrderImpl pOrder, MFFCommerceItemImpl pCommerceItem,double pOrderTotal){
    
    MFFOMSOrderManager orderManager = getOmsOrderManager();
    MFFItemPriceInfo itemPriceInfo = (MFFItemPriceInfo)pCommerceItem.getPriceInfo();
    
    long quantity = pCommerceItem.getQuantity();
    int qty= (int) quantity;
    
    double itemTotal = 0.0d;
    boolean isProrated = false;
    double itemProratedPrice = itemPriceInfo.getProratedDiscountPrice();
    if(itemProratedPrice > 0 && !getOmsOrderManager().isGiftCardItem(pCommerceItem)){
      itemTotal = itemProratedPrice;
      isProrated = true;
    }else{
      itemTotal = itemPriceInfo.getAmount();
    }
    
    double itemCityTax = pCommerceItem.getTaxPriceInfo() != null ? pCommerceItem.getTaxPriceInfo().getCityTax() : 0.0d;
    double itemCountyTax = pCommerceItem.getTaxPriceInfo() != null ? pCommerceItem.getTaxPriceInfo().getCountyTax() : 0.0d;
    double itemStateTax = pCommerceItem.getTaxPriceInfo() != null ? pCommerceItem.getTaxPriceInfo().getStateTax() : 0.0d;
    double itemDistrictTax = pCommerceItem.getTaxPriceInfo() != null ? pCommerceItem.getTaxPriceInfo().getDistrictTax() : 0.0d;
    double itemCountryTax = pCommerceItem.getTaxPriceInfo() != null ? pCommerceItem.getTaxPriceInfo().getCountryTax() : 0.0d;
    
    // Make sure order discount share is valid otherwise calculate it
    double orderDiscountShare = pCommerceItem.getPriceInfo().getOrderDiscountShare();
    double itemTotalAfterOrderDiscount = itemTotal - orderDiscountShare;
    double gcAmount=0.0;
    double gwpDiscount=itemPriceInfo.getDiscountAmount();
    double effectivePrice = itemPriceInfo.getEffectivePrice();
    //double itemTotalAfterGWPDiscounts = itemTotalAfterOrderDiscount - itemPriceInfo.getDiscountAmount();
    double finalTotal = itemTotalAfterOrderDiscount;
    // Need a way to handle old orders in the system
    // before this change is deployed
    // if effectivePrice is set, then its a new order
    // else use the old way of computing these numbers
    if(effectivePrice > 0) {
    	finalTotal = effectivePrice;
    } 
   
    
/*    // 2414: if gift item, then use the value of the gift before gwp promo priced it to $0
    if( (Boolean)pCommerceItem.getPropertyValue("gwp")) {
    	itemTotalAfterGWPDiscounts = itemPriceInfo.getEffectivePrice();
    } 
*/    
    HashMap<Integer,Double> proratedItemAmount = orderManager.prorateOnQuantity(finalTotal,qty);
    
    // If there is a discount of $10 on an item with qty 4
    // during the return flow... each unit of the qualifier could be returned
    // We prorate the discount on quantiy to ensure the right split of the discount is being returned
    HashMap<Integer,Double> proratedGwpAmount = orderManager.prorateOnQuantity(gwpDiscount,qty);
    HashMap<Integer,Double> proratedOrderDiscountShare = orderManager.prorateOnQuantity(orderDiscountShare,qty);
    HashMap<Integer,Double> proratedDiscountPrice = orderManager.prorateOnQuantity(itemProratedPrice,qty);
    HashMap<Integer,Double> proratedShipping = orderManager.prorateOnQuantity(pCommerceItem.getShipping(),qty);
    HashMap<Integer,Double> proratedShippingCityTax = orderManager.prorateOnQuantity(pCommerceItem.getShippingCityTax(),qty);
    HashMap<Integer,Double> proratedShippingCountyTax = orderManager.prorateOnQuantity(pCommerceItem.getShippingCountyTax(),qty);
    HashMap<Integer,Double> proratedShippingStateTax = orderManager.prorateOnQuantity(pCommerceItem.getShippingStateTax(),qty);
    HashMap<Integer,Double> proratedShippingDistrictTax = orderManager.prorateOnQuantity(pCommerceItem.getShippingDistrictTax(),qty);
    HashMap<Integer,Double> proratedShippingCountryTax = orderManager.prorateOnQuantity(pCommerceItem.getShippingCountryTax(),qty);
    HashMap<Integer,Double> proratedItemCityTax = orderManager.prorateOnQuantity(itemCityTax, qty);
    HashMap<Integer,Double> proratedItemCountyTax = orderManager.prorateOnQuantity(itemCountyTax, qty);
    HashMap<Integer,Double> proratedItemStateTax = orderManager.prorateOnQuantity(itemStateTax, qty);
    HashMap<Integer,Double> proratedItemDistrictTax = orderManager.prorateOnQuantity(itemDistrictTax, qty);
    HashMap<Integer,Double> proratedItemCountryTax = orderManager.prorateOnQuantity(itemCountryTax, qty);
    List<SplitItem> splitItemList = new ArrayList<SplitItem>();
    
    
    for(int i = 1 ; i <= quantity; i++) {
      
      SplitItem splitItem = new SplitItem();
      splitItem.setLineNumber(i);
      splitItem.setQuantity(1);
      
      if(proratedItemAmount != null && proratedItemAmount.size() > 0 && proratedItemAmount.get(i) != null){
        splitItem.setUnitPrice(proratedItemAmount.get(i));
      }

      if(proratedGwpAmount != null && proratedGwpAmount.size() > 0 && proratedGwpAmount.get(i) != null){
          splitItem.setGwpAmount(proratedGwpAmount.get(i));
        }
      
      if(proratedOrderDiscountShare != null && proratedOrderDiscountShare.size() > 0 && proratedOrderDiscountShare.get(i) != null){
        splitItem.setOrderDiscountShare(proratedOrderDiscountShare.get(i));
      }
      
      if(proratedDiscountPrice != null && proratedDiscountPrice.size() > 0 && proratedDiscountPrice.get(i) != null){
        splitItem.setDiscountPrice(proratedDiscountPrice.get(i));
      }
      
      if(proratedShipping != null && proratedShipping.size() > 0 && proratedShipping.get(i) != null){
        splitItem.setShipping(proratedShipping.get(i));
      }
      
      if(proratedShippingCityTax != null && proratedShippingCityTax.size() > 0 && proratedShippingCityTax.get(i) != null){
        splitItem.setShippingCityTax(proratedShippingCityTax.get(i));
      }
      
      if(proratedShippingCountyTax != null && proratedShippingCountyTax.size() > 0 && proratedShippingCountyTax.get(i) != null){
        splitItem.setShippingCountyTax(proratedShippingCountyTax.get(i));
      }
      
      if(proratedShippingStateTax != null && proratedShippingStateTax.size() > 0 && proratedShippingStateTax.get(i) != null){
        splitItem.setShippingStateTax(proratedShippingStateTax.get(i));
      }
      
      if(proratedShippingDistrictTax != null && proratedShippingDistrictTax.size() > 0 && proratedShippingDistrictTax.get(i) != null){
        splitItem.setShippingDistrictTax(proratedShippingDistrictTax.get(i));
      }
      
      if(proratedShippingCountryTax != null && proratedShippingCountryTax.size() > 0 && proratedShippingCountryTax.get(i) != null){
        splitItem.setShippingCountryTax(proratedShippingCountryTax.get(i));
      }
      
      if(proratedItemCityTax != null && proratedItemCityTax.size() > 0 && proratedItemCityTax.get(i) != null){
        splitItem.setItemCityTax(proratedItemCityTax.get(i));
      }
      
      if(proratedItemCountyTax != null && proratedItemCountyTax.size() > 0 && proratedItemCountyTax.get(i) != null){
        splitItem.setItemCountyTax(proratedItemCountyTax.get(i));
      }
      
      if(proratedItemStateTax != null && proratedItemStateTax.size() > 0 && proratedItemStateTax.get(i) != null){
        splitItem.setItemStateTax(proratedItemStateTax.get(i));
      }
      
      if(proratedItemDistrictTax != null && proratedItemDistrictTax.size() > 0 && proratedItemDistrictTax.get(i) != null){
        splitItem.setItemDistrictTax(proratedItemDistrictTax.get(i));
      }
      
      if(proratedItemCountryTax != null && proratedItemCountryTax.size() > 0 && proratedItemCountryTax.get(i) != null){
        splitItem.setItemCountryTax(proratedItemCountryTax.get(i));
      }
      
      /*if(i != quantity){
        splitItem.setUnitPrice(proratedItemAmount[0]);
        splitItem.setShipping(proratedShipping[0]);
        //splitItem.setTax(proratedTax[0]);
        //splitItem.setShippingTax(proratedShippingTax[0]);
        splitItem.setShippingCityTax(proratedShippingCityTax[0]);
        splitItem.setShippingCountyTax(proratedShippingCountyTax[0]);
        splitItem.setShippingStateTax(proratedShippingStateTax[0]);
        splitItem.setShippingDistrictTax(proratedShippingDistrictTax[0]);
        splitItem.setShippingCountryTax(proratedShippingCountryTax[0]);
        
        splitItem.setItemCityTax(proratedItemCityTax[0]);
        splitItem.setItemCountyTax(proratedItemCountyTax[0]);
        splitItem.setItemStateTax(proratedItemStateTax[0]);
        splitItem.setItemDistrictTax(proratedItemDistrictTax[0]);
        splitItem.setItemCountryTax(proratedItemCountryTax[0]);
        
        splitItem.setOrderDiscountShare(proratedOrderDiscountShare[0]);
        splitItem.setDiscountPrice(proratedDiscountPrice[0]);
      }else{
        splitItem.setUnitPrice(proratedItemAmount[0] + proratedItemAmount[1]);
        splitItem.setShipping(proratedShipping[0] + proratedShipping[1]);
        //splitItem.setTax(proratedTax[0] + proratedTax[1]);
        //splitItem.setShippingTax(proratedShippingTax[0] + proratedShippingTax[1]);
        splitItem.setShippingCityTax(proratedShippingCityTax[0] + proratedShippingCityTax[1]);
        splitItem.setShippingCountyTax(proratedShippingCountyTax[0] + proratedShippingCountyTax[1]);
        splitItem.setShippingStateTax(proratedShippingStateTax[0] + proratedShippingStateTax[1]);
        splitItem.setShippingDistrictTax(proratedShippingDistrictTax[0] + proratedShippingDistrictTax[1]);
        splitItem.setShippingCountryTax(proratedShippingCountryTax[0] + proratedShippingCountryTax[1]);
        splitItem.setItemCityTax(proratedItemCityTax[0] + proratedItemCityTax[1]);
        splitItem.setItemCountyTax(proratedItemCountyTax[0] + proratedItemCountyTax[1]);
        splitItem.setItemStateTax(proratedItemStateTax[0] + proratedItemStateTax[1]);
        splitItem.setItemDistrictTax(proratedItemDistrictTax[0] + proratedItemDistrictTax[1]);
        splitItem.setItemCountryTax(proratedItemCountryTax[0] + proratedItemCountryTax[1]);
        
        splitItem.setOrderDiscountShare(proratedOrderDiscountShare[0] + proratedOrderDiscountShare[1]);
        splitItem.setDiscountPrice(proratedDiscountPrice[0] + proratedDiscountPrice[1]);
      }*/
      
      splitItemList.add(splitItem);
    }
    
    return splitItemList;
  }
  
  private void addProrateItem(MFFOrderImpl pOrder, MFFCommerceItemImpl pCommerceItem,SplitItem pSplitItem,Set<String> pReturnItemIds) 
        throws CommerceException {    
      vlogDebug ("Begin addProrateItem");
     
      MutableRepositoryItem lMutableRepositoryItem = null;
      try {
        lMutableRepositoryItem = getOmsOrderRepository().createItem (MFFConstants.ITEM_DESC_PRORATE_ITEM);
        
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_COMMERCE_ITEM_ID, pCommerceItem.getId());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ORDER_ID, pOrder.getId());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_LINE_NUMBER,pSplitItem.getLineNumber());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_QUANTITY,pSplitItem.getQuantity());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_UNIT_PRICE,pSplitItem.getUnitPrice());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_LIST_PRICE,pCommerceItem.getPriceInfo().getListPrice());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SALE_PRICE,pCommerceItem.getPriceInfo().getSalePrice());
        
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING,pSplitItem.getShipping());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_TAX,pSplitItem.getShippingTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_CITY_TAX,pSplitItem.getShippingCityTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_COUNTY_TAX,pSplitItem.getShippingCountyTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_STATE_TAX,pSplitItem.getShippingStateTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_DISTRICT_TAX,pSplitItem.getShippingDistrictTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_SHIPPING_COUNTRY_TAX,pSplitItem.getShippingCountryTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_TAX,pSplitItem.getItemTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_CITY_TAX,pSplitItem.getItemCityTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_COUNTY_TAX,pSplitItem.getItemCountyTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_STATE_TAX,pSplitItem.getItemStateTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_DISTRICT_TAX,pSplitItem.getItemDistrictTax());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ITEM_COUNTRY_TAX,pSplitItem.getItemCountryTax());
        
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_ORDER_DISCOUNT_SHARE,pSplitItem.getOrderDiscountShare());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_DISCOUNT_PRICE,pSplitItem.getDiscountPrice());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_GWP_AMOUNT,pSplitItem.getGwpAmount());
        
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_TOTAL,pSplitItem.getTotal());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_TOTAL_WITHOUT_SHIPPING,pSplitItem.getTotalWithoutShipping());
        lMutableRepositoryItem.setPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE,MFFConstants.PRORATE_STATE_INITIAL);
        
        getOmsOrderRepository().addItem(lMutableRepositoryItem);
        
        pReturnItemIds.add(lMutableRepositoryItem.getRepositoryId());
        
      } catch (RepositoryException e) {     
        String lErrorMessage = String.format("Unable to create prorateItem - Order Number: %s Commerce Item Id: %s", pOrder.getId(), pCommerceItem.getId());
        vlogError (e, lErrorMessage);
        throw new CommerceException (lErrorMessage, e);
      }
      
      vlogDebug ("End addProrateItem");
  }
  
  
  
  /**
   * Sums the commerce items total
   * 
   * @param pOrder
   * @return
   */
  @SuppressWarnings("unchecked")
  private double getItemTotal(MFFOrderImpl pOrder){
    
    double itemTotal = 0.0d;
    List<CommerceItem> lCommerceItems = pOrder.getCommerceItems();
    for (CommerceItem lCommerceItem : lCommerceItems) {
      itemTotal += lCommerceItem.getPriceInfo().getAmount();
    }
    
    return itemTotal;
  }
  
  /**
   * Get the commerce item from the order given the Commerce Item ID.
   * 
   * @param pOrder
   *            ATG Order
   * @param lCommerceItemId
   *            Commerce Item Id
   * @return Commerce item
   * @throws Exception
   */
  protected MFFCommerceItemImpl getCommerceItem(Order pOrder, String lCommerceItemId) throws Exception {
    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;
    MFFCommerceItemImpl lCommerceItem;
    try {
      lCommerceItem = (MFFCommerceItemImpl) pOrder.getCommerceItem(lCommerceItemId);
    } catch (CommerceItemNotFoundException ex) {
      String lErrorMessage = String.format("Commerce item not found for orderId: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    } catch (InvalidParameterException ex) {
      String lErrorMessage = String.format("Commerce item not found for orderId: %s Item: %s", lOrder.getId(), lCommerceItemId);
      vlogError(ex, lErrorMessage);
      throw new Exception(lErrorMessage);
    }
    return lCommerceItem;
  }
  
  private MFFOMSOrderManager  mOmsOrderManager;

  public MFFOMSOrderManager getOmsOrderManager() {
    return mOmsOrderManager;
  }

  public void setOmsOrderManager(MFFOMSOrderManager pOmsOrderManager) {
    this.mOmsOrderManager = pOmsOrderManager;
  }
  
  private MutableRepository getOmsOrderRepository(){
    return (MutableRepository)getOmsOrderManager().getOmsOrderRepository();
  }
  
  public class SplitItem {
    double lineNumber;
    double unitPrice;
    double shipping;
    //double shippingTax;
    //double tax;
    double shippingCityTax;
    double shippingCountyTax;
    double shippingStateTax;
    double shippingDistrictTax;
    double shippingCountryTax;
    double itemCityTax;
    double itemCountyTax;
    double itemStateTax;
    double itemDistrictTax;
    double itemCountryTax;
    double orderDiscountShare;
    double discountPrice;
    double gwpAmount;

    long quantity;

    SplitItem() {
      super();
    }

    public double getLineNumber() {
      return lineNumber;
    }

    public void setLineNumber(double lineNumber) {
      this.lineNumber = lineNumber;
    }

    public double getUnitPrice() {
      return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
      this.unitPrice = unitPrice;
    }

    public double getShipping() {
      return shipping;
    }

    public void setShipping(double shipping) {
      this.shipping = shipping;
    }
    
    public double getShippingCityTax() {
      return shippingCityTax;
    }

    public void setShippingCityTax(double pShippingCityTax) {
      shippingCityTax = pShippingCityTax;
    }

    public double getShippingCountyTax() {
      return shippingCountyTax;
    }

    public void setShippingCountyTax(double pShippingCountyTax) {
      shippingCountyTax = pShippingCountyTax;
    }

    public double getShippingStateTax() {
      return shippingStateTax;
    }

    public void setShippingStateTax(double pShippingStateTax) {
      shippingStateTax = pShippingStateTax;
    }

    public double getShippingDistrictTax() {
      return shippingDistrictTax;
    }

    public void setShippingDistrictTax(double pShippingDistrictTax) {
      shippingDistrictTax = pShippingDistrictTax;
    }
    
    public double getShippingCountryTax() {
      return shippingCountryTax;
    }

    public void setShippingCountryTax(double pShippingCountryTax) {
      shippingCountryTax = pShippingCountryTax;
    }

    public double getItemCityTax() {
      return itemCityTax;
    }

    public void setItemCityTax(double pItemCityTax) {
      itemCityTax = pItemCityTax;
    }

    public double getItemCountyTax() {
      return itemCountyTax;
    }

    public void setItemCountyTax(double pItemCountyTax) {
      itemCountyTax = pItemCountyTax;
    }

    public double getItemStateTax() {
      return itemStateTax;
    }

    public void setItemStateTax(double pItemStateTax) {
      itemStateTax = pItemStateTax;
    }

    public double getItemDistrictTax() {
      return itemDistrictTax;
    }

    public void setItemDistrictTax(double pItemDistrictTax) {
      itemDistrictTax = pItemDistrictTax;
    }
    
    public double getItemCountryTax() {
      return itemCountryTax;
    }

    public void setItemCountryTax(double pItemCountryTax) {
      itemCountryTax = pItemCountryTax;
    }
    
    public double getOrderDiscountShare() {
      return orderDiscountShare;
    }

    public void setOrderDiscountShare(double pOrderDiscountShare) {
      orderDiscountShare = pOrderDiscountShare;
    }

    public double getDiscountPrice() {
      return discountPrice;
    }

    public void setDiscountPrice(double pDiscountPrice) {
      discountPrice = pDiscountPrice;
    }

    public long getQuantity() {
      return quantity;
    }

    public void setQuantity(long quantity) {
      this.quantity = quantity;
    }
    
    public double getItemTax() {
      return getItemCityTax() + getItemCountyTax() + getItemStateTax() + getItemDistrictTax() + getItemCountryTax();
    }
    
    public double getShippingTax() {
      return getShippingCityTax() + getShippingCountyTax() + getShippingStateTax() + getShippingDistrictTax() + getShippingCountryTax();
    }
    
    public double getTotal(){
      return getUnitPrice() + getShipping() + getShippingTax() + getItemTax();
    }
    
    public double getTotalWithoutShipping(){
      return getUnitPrice() + getItemTax();
    }

    public double getGwpAmount() {
		return gwpAmount;
	}

	public void setGwpAmount(double pGwpAmount) {
		gwpAmount = pGwpAmount;
	}
    
  }
  

}
