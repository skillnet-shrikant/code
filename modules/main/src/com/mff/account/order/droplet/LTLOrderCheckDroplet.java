package com.mff.account.order.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.calculators.MFFLTLShippingCalculator;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.PricingException;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class LTLOrderCheckDroplet extends DynamoServlet{

  MFFLTLShippingCalculator mffLTLShippingCalculator;
  public MFFLTLShippingCalculator getMffLTLShippingCalculator() {
    return mffLTLShippingCalculator;
  }
  public void setMffLTLShippingCalculator(MFFLTLShippingCalculator pMffLTLShippingCalculator) {
    mffLTLShippingCalculator = pMffLTLShippingCalculator;
  }
  public static final ParameterName ITEMS = ParameterName.getParameterName("items");
  
  /**
   * This method is used to get LTL info of an Order
   * @param : items (Commerce Items in the order)
   * @result : isLTLOrder (True/False), lowWeightRange, highWeightRange, ltlShippingCharges
   */
  @SuppressWarnings("unchecked")
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    vlogDebug("Entered into LTLOrderCheckDroplet service method ....");
    
    MFFOrderImpl order = null;
    boolean isLTLOrder = false;
    
    Object items = pRequest.getObjectParameter(ITEMS);
    Object objOrder = pRequest.getObjectParameter("order");
    
    if(objOrder != null) {
    	order = (MFFOrderImpl)objOrder;
    	//isLTLOrder = order.isLTLOrder();
    	
    	// isLTLOrder is true
    	if(order.isLTLOrder() && !order.isFreeLTLItemsOnly()) {
    		isLTLOrder = true;
    	}
    }
    
    // Order with free ltl items
    // ltlOrder = true
    // freeLTLItemsOnly = true
    
    // LTL should return empty
    
    if(order != null && !isLTLOrder) {
    	pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
    	return;
    }
    
    BigDecimal weightTotal = new BigDecimal("0.0");
    double lowWeightRange = 0.0D;
    String highWeightRange = "0.0";
    double ltlShippingCharges = 0.0D;
    boolean freeFreightShipping=false;
    vlogDebug("items ....{0} --getClass-->{1}",items,items.getClass());
    
    
    if(items!=null && (items instanceof Collection)){
      List<CommerceItem> commrItems=(List<CommerceItem>)items;
      MFFPricingUtil util = new MFFPricingUtil();
      boolean isLTL=util.isLTLOrderByItems(commrItems);
      
      if(isLTL){
	      for (int lI = 0; lI < commrItems.size(); lI++) {
	    	  vlogDebug("Sku Info for the CommerceItem --> : {0}", commrItems.get(lI).getAuxiliaryData().getCatalogRef());
	          vlogDebug("Retreiving Weight and Quantity --");
	          isLTLOrder=true;
	          BigDecimal itemQuantity;
	          BigDecimal itemWeight;
	          try {
	            itemQuantity = BigDecimal.valueOf(commrItems.get(lI).getQuantity());//BigDecimal.valueOf((Long) (DynamicBeans.getPropertyValue(commrItems.get(lI).getAuxiliaryData().getCatalogRef(), "quantity")));
	            vlogDebug("--- itemQuantity-->{0}",itemQuantity);
	            itemWeight = BigDecimal.valueOf(getWeight(commrItems.get(lI)));
	            vlogDebug("itemWeight -->{0}",itemWeight);
	          } catch (Exception e) {
	            logError(e);
	            itemWeight=new BigDecimal("0.0");
	            itemQuantity=new BigDecimal("0.0");
	          }
	          
	          // BZ: 2427. Ignore if item has free freight shipping 
	          if(!util.isFreeFreightItem(commrItems.get(lI)) &&
	        		  !( ((MFFCommerceItemImpl)commrItems.get(lI)).isFreeShippingPromo() && ((MFFCommerceItemImpl)commrItems.get(lI)).getExtendToShipMethods().contains(MFFConstants.LTL_TRUCK))) {
	        	  weightTotal = weightTotal.add(itemQuantity.multiply(itemWeight));
	          }
	        }
      	}
      
      vlogDebug("Total Weight calculated for the order   --->: {0}",weightTotal);
      String[] weightRanges=getMffLTLShippingCalculator().getRanges();
      for (int c = 0; c < weightRanges.length; ++c) {
        String[] subParts = StringUtils.splitStringAtCharacter(weightRanges[c], ':');
        double tmpLowWeight=Double.parseDouble(subParts[0]);
        if(!subParts[1].equalsIgnoreCase("MAX_VALUE")){
          double tmpHighWeight=Double.parseDouble(subParts[1]);
          if(weightTotal.doubleValue()>=tmpLowWeight && weightTotal.doubleValue()<=tmpHighWeight){
            lowWeightRange = tmpLowWeight;
            highWeightRange = String.valueOf(tmpHighWeight);
            ltlShippingCharges = Double.parseDouble(subParts[2]);
            break;
          }
        }else{
          if(weightTotal.doubleValue()>=tmpLowWeight){
            lowWeightRange = tmpLowWeight;
            highWeightRange = subParts[1];
            ltlShippingCharges = Double.parseDouble(subParts[2]);
            break;
          }
        }
      }
      vlogDebug("Total Shipping Charges calculated for the order   --->: {0}",ltlShippingCharges);
    }
    vlogDebug("LTLOrderCheckDroplet isLTLOrder  ...."+isLTLOrder);
    if(isLTLOrder){
      pRequest.setParameter("isLTLOrder", isLTLOrder);
      pRequest.setParameter("totalLTLWeight", weightTotal);
      pRequest.setParameter("rangeLow", lowWeightRange);
      pRequest.setParameter("rangeHigh", highWeightRange);
      pRequest.setParameter("ltlShippingCharges", ltlShippingCharges);
      pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(MFFConstants.EMPTY, pRequest, pResponse);
    }
    vlogDebug("Exitting LTLOrderCheckDroplet ...");
  }
  
  
  protected double getWeight(CommerceItem pCommerceItem) throws PricingException {
    double weight = 0.0D;

    if (pCommerceItem != null) {
      try {
        Object weightObject = DynamicBeans.getPropertyValue(pCommerceItem.getAuxiliaryData().getCatalogRef(), "weight");

        if (weightObject != null) weight = ((Number) weightObject).doubleValue();
      } catch (PropertyNotFoundException pnfe) {
        weight = 0.0D;
      } catch (ClassCastException cce) {
        throw new PricingException("The weight property of the object is not a Number, unable to get the weight.", cce);
      }
    }
    return weight;
  }
}
