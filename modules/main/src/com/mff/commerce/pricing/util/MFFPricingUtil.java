package com.mff.commerce.pricing.util;

import java.util.Calendar;
import java.util.List;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingAdjustment;
import atg.repository.RepositoryItem;

public class MFFPricingUtil {

	/**
	 * This method is used to identify the commerceItem is LongLite item or not
	 * @param pCommerceItem
	 * @return
	 */
/*	public boolean isAdditionalHandling(CommerceItem pCommerceItem) {
		
		boolean returnFlag = false;
		// 2427 - Ignore if marked as free freight shipping
		if(isFreeFreightItem(pCommerceItem)) {
			return false;
		}
		MFFCommerceItemImpl mci = (MFFCommerceItemImpl)pCommerceItem;
		if(mci.isFreeShippingPromo()) {
			String currentShipMethod = null;
			if(pCommerceItem.getShippingGroupRelationshipCount() > 0)  {
				ShippingGroupCommerceItemRelationship sgci = (ShippingGroupCommerceItemRelationship)pCommerceItem.getShippingGroupRelationships().get(0);
				currentShipMethod = sgci.getShippingGroup().getShippingMethod();
				if(currentShipMethod.equalsIgnoreCase(MFFConstants.STANDARD)) {
					return false;
				} else {
					if(mci.getExtendToShipMethods().contains(currentShipMethod)) {
						return false;
					}
				}
			}
		}
		RepositoryItem skuItem = (RepositoryItem) pCommerceItem.getAuxiliaryData().getCatalogRef();
		long qty = pCommerceItem.getQuantity();
		Object objLength = skuItem.getPropertyValue(MFFConstants.SKU_LENGTH);
		Object objWidth = skuItem.getPropertyValue(MFFConstants.WIDTH);
		Object objWeight = skuItem.getPropertyValue(MFFConstants.WEIGHT);
		Object objDepth = skuItem.getPropertyValue(MFFConstants.SKU_DEPTH);
		
		double length = 0.0;
		double width = 0.0;
		double weight = 0.0;
		double height = 0.0;
		
		if(objLength != null){
			length = ((Double)objLength).doubleValue();
			if(length > 48){
				return true;
			}
		}
		if(objDepth != null){
			height = ((Double)objDepth).doubleValue();
			if(height > 30){
				return true;
			}
		}
		if(objWeight != null ) {
			weight = ((Double)objWeight).doubleValue();
			double actualWeight = qty * weight;
			if (actualWeight > 70) {
				return true;
			}
		}
		if (objWidth != null){
			width = ((Double)objWidth).doubleValue();
			if (width > 30) {
				return true;
			}
		}
		
		return returnFlag;
	}*/

	/**
	 * This method is used to check the item is OverSized
	 * @param pCommerceItem
	 * @return
	 */
/*	public boolean isOverSized(CommerceItem pCommerceItem) {
		
		// 2427 - Ignore if marked as free freight shipping
		if(isFreeFreightItem(pCommerceItem)) {
			return false;
		}
		long qty = pCommerceItem.getQuantity();
		RepositoryItem skuItem = (RepositoryItem) pCommerceItem.getAuxiliaryData().getCatalogRef();
		Object objLength = skuItem.getPropertyValue(MFFConstants.SKU_LENGTH);
		Object objWidth = skuItem.getPropertyValue(MFFConstants.WIDTH);
		Object objWeight = skuItem.getPropertyValue(MFFConstants.WEIGHT);
		Object objHeight = skuItem.getPropertyValue(MFFConstants.SKU_DEPTH);
		double length = 0.0;
		double width = 0.0;
		double weight = 0.0;
		double height = 0.0;
		double itemDimension = 0.0;
		double totalweight = 0.0;
		double dimensionWeight = 0.0;
		double totalDimensionWeight = 0.0;
		
		// BZ 3063
		if(objLength != null && objWidth != null && objHeight != null){
			length = ((Double)objLength).doubleValue();
			width = ((Double)objWidth).doubleValue();
			height = ((Double)objHeight).doubleValue();
			
			if(length > 96 || width > 96 || height > 96) {
				return true;
			}

			if( (length + width) > 130 || (width + height) > 130 || (height+length) > 130) {
				return true;
			}
			
			dimensionWeight = (length * width * height)/166;
			totalDimensionWeight = qty * dimensionWeight;
		}

		if(objWeight != null){
			weight = ((Double)objWeight).doubleValue();
			totalweight = qty * weight; 
		}
		
		if (totalweight > 90 || totalDimensionWeight > 90) {
			return true;
		}
		
		if(objWeight != null){
			weight = ((Double)objWeight).doubleValue();
			totalweight = qty * weight; 
		} 
		
		if(objLength != null && objWidth != null && objDepth != null){
			length = ((Double)objLength).doubleValue();
			width = ((Double)objWidth).doubleValue();
			depth = ((Double)objDepth).doubleValue();
			//calculate girth
			itemDimension = length + 2*(width + depth);
		}
		
		if(totalweight <= 150 && itemDimension > 130){
			return true;
		}
		
		return false;
	}*/

	/**
	 * This method is used to identify SaturDay delivery eligible or not
	 * @param pShippingGroup
	 * @return
	 */
	@SuppressWarnings("static-access")
	public boolean isSaturdayDelivery(ShippingGroup pShippingGroup) {
		boolean returnFlag = false;
		
		if(pShippingGroup != null){
			String shipMethod = pShippingGroup.getShippingMethod();
			
			Calendar currentTime = Calendar.getInstance();
			int dayofWeek = currentTime.get(Calendar.DAY_OF_WEEK);
			
			String cutOffTime = MFFConstants.SHIPPING_CUT_OFF_TIME;
	        
	        String[] parts = cutOffTime.split(":");
			Calendar configureTime = Calendar.getInstance();
			configureTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
			configureTime.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
	        	
				
				if(shipMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.WEDNESDAY && currentTime.after(configureTime)){
					// Wednesday after 3:30PM
					returnFlag = true;
				} else if(shipMethod.equalsIgnoreCase(MFFConstants.SECOND_DAY) && dayofWeek == currentTime.THURSDAY && currentTime.before(configureTime)){
					// Thursday before 3:30PM
					returnFlag = true;
				} else if(shipMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.THURSDAY && currentTime.after(configureTime)){
					// Thursday after 3:30PM
					returnFlag = true;
				} else if(shipMethod.equalsIgnoreCase(MFFConstants.OVER_NIGHT) && dayofWeek == currentTime.FRIDAY && currentTime.before(configureTime)){
					// Friday before 3:30PM
					returnFlag = true;
				}
				

		}
		return returnFlag;
	}
	
	/**
	 * Verifies if item requires signature
	 * @param pCommerceItem
	 * @return
	 */
	 public boolean isSignatureRequired(CommerceItem pCommerceItem) {
	   
	   MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pCommerceItem;
	   
	   //return true only if minimum age is not null && > 18 or > 21
	   if(lCommerceItem.getMinimumAge() != null && (lCommerceItem.getMinimumAge() >= 18))
	     return true;
	   
	   return false;
	  }
	 
	/**
	 * This method is used to check the item is LTL or not
	 * @param pCurrentCommerceItemRelationship
	 * @return
	 */
	private boolean isLTLItem(ShippingGroupCommerceItemRelationship pCurrentCommerceItemRelationship) {
		
		
		CommerceItem citem = pCurrentCommerceItemRelationship.getCommerceItem();
		return isLTLItem(citem);
	}

	private boolean isFreeFreightItem(ShippingGroupCommerceItemRelationship pCurrentCommerceItemRelationship) {
		
		
		CommerceItem citem = pCurrentCommerceItemRelationship.getCommerceItem();
		return isFreeFreightItem(citem);
	}	
	private double getRelItemWeightByQnty(ShippingGroupCommerceItemRelationship pCurrentCommerceItemRelationship) {
		
		
		CommerceItem citem = pCurrentCommerceItemRelationship.getCommerceItem();
		
		return getItemWeightByQnty(citem);
        
	}
	
	public double getItemWeightByQnty(CommerceItem citem) {
		
		RepositoryItem sku = (RepositoryItem)citem.getAuxiliaryData().getCatalogRef();
		
		Double itemWeight = (Double)sku.getPropertyValue("weight");
        
        if(itemWeight == null){
            itemWeight = Double.valueOf(0.0D);
        }
        long itemQuantity = citem.getQuantity();
        
        double itemTotalWeight = itemWeight.doubleValue() * (double)itemQuantity;
        
        return itemTotalWeight;
        
	}

	public boolean isLTLOrderByCiRelationships(List CIRelationships) {
		double orderWeight = 0.0D;
		boolean isLTL = false;
		boolean isFreeFreight=false;
		if ((CIRelationships != null) && (CIRelationships.size() > 0)) {
			int listSize = CIRelationships.size();
			for (int i = 0; i < listSize; ++i) {
				ShippingGroupCommerceItemRelationship currentCommerceItemRelationship = 
						(ShippingGroupCommerceItemRelationship)CIRelationships.get(i);
				
				isLTL = isLTLItem(currentCommerceItemRelationship);
				// 2427 - Ignore if marked as free freight shipping
				isFreeFreight = isFreeFreightItem (currentCommerceItemRelationship);
				
				// order is considered LTL for shipping calc
				// only if it has LTL items not marked for free shipping
				if(isLTL){
					break;
				} else {
					//if(!isFreeFreight) {
						orderWeight += getRelItemWeightByQnty(currentCommerceItemRelationship);
					//}
				}
				
				if (orderWeight > 150){
					isLTL = true;
					break;
				}
			}
		}
		return isLTL;
	}
	
	public double getLTLWeightForShipping(Order pOrder, String pShipMethod) {
		double orderWeight = 0.0D;
		boolean isFreeFreight=false;
		boolean isFreeShipPromo = false;
		MFFOrderImpl order = (MFFOrderImpl)pOrder;
		if(order.isLTLOrder()) {
			List<CommerceItem> commerceItems = order.getCommerceItems();
			if(commerceItems != null && commerceItems.size() > 0) {
				for (CommerceItem commerceItem : commerceItems) {
					isFreeFreight = isFreeFreightItem (commerceItem);
					isFreeShipPromo = ((MFFCommerceItemImpl)commerceItem).isFreeShippingPromo();
					if(!isFreeFreight && !(isFreeShipPromo && ((MFFCommerceItemImpl)commerceItem).getExtendToShipMethods().contains(MFFConstants.LTL_TRUCK))) {
						orderWeight += getItemWeightByQnty(commerceItem);
					}
				}
			}
		}
		return orderWeight;
	}
	
	public boolean hasLTLItem (Order pOrder, String pShipMethod) {
		boolean hasLTLItem = false;
		boolean isFreeFreight=false;
		boolean isFreeShipPromo = false;
		boolean isLTLItem = false;
		MFFOrderImpl order = (MFFOrderImpl)pOrder;
		if(order.isLTLOrder()) {
			List<CommerceItem> commerceItems = order.getCommerceItems();
			if(commerceItems != null && commerceItems.size() > 0) {
				for (CommerceItem commerceItem : commerceItems) {
					isFreeFreight = isFreeFreightItem (commerceItem);
					isFreeShipPromo = ((MFFCommerceItemImpl)commerceItem).isFreeShippingPromo();
					isLTLItem = isLTLItem(commerceItem);
					if(isLTLItem && !isFreeFreight && 
							!(isFreeShipPromo && ((MFFCommerceItemImpl)commerceItem).getExtendToShipMethods().contains(MFFConstants.LTL_TRUCK))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean isCalculateLTLShippingCosts(Order pOrder, String pShipMethod) {
		boolean calcLTLRates = false;
		if(getLTLWeightForShipping(pOrder,pShipMethod) > 150) {
			calcLTLRates = true;
		}
		if(!calcLTLRates) {
			calcLTLRates = hasLTLItem(pOrder, pShipMethod);
		}
		return calcLTLRates;
	}
	
	public boolean isLTLOrderByItems(List<CommerceItem> pItems) {
		double orderWeight = 0.0D;
		boolean isLTL = false;
		//boolean isFreeFreight=false;
		if ((pItems != null) && (pItems.size() > 0)) {
			
			for (int i = 0; i < pItems.size(); ++i) {
				
				CommerceItem citem = pItems.get(i);
				
				isLTL = isLTLItem(citem);
				// 2427 - Ignore if marked as free freight shipping
				//isFreeFreight = isFreeFreightItem (citem);
				if(isLTL){
					break;
				} else {
					//if(!isFreeFreight) {
						orderWeight += getItemWeightByQnty(citem);
					//}
				}
				
				if (orderWeight > 150){
					isLTL = true;
					break;
				}
			}
		}
		return isLTL;
	}
	
	public boolean isFreeFreightItem (CommerceItem pCurrentCommerceItem) {
		RepositoryItem sku = null;
		boolean isFreeFreight = false;
		boolean isFreeShipPromo = false;
		if(pCurrentCommerceItem != null){
			sku = (RepositoryItem)pCurrentCommerceItem.getAuxiliaryData().getCatalogRef();
			if(sku != null) {
				Object objFreeFreight = sku.getPropertyValue(MFFConstants.FREE_SHIPPING);
				isFreeFreight = (Boolean)objFreeFreight;
				
				if(isFreeFreight) {
					return true;
				}
				
				MFFCommerceItemImpl mci = (MFFCommerceItemImpl)pCurrentCommerceItem;
				isFreeShipPromo = mci.isFreeShippingPromo();
				if(isFreeShipPromo) {
					//
					String currentShipMethod = null;
					if(pCurrentCommerceItem.getShippingGroupRelationshipCount() > 0) {
						ShippingGroupCommerceItemRelationship sgci = (ShippingGroupCommerceItemRelationship)pCurrentCommerceItem.getShippingGroupRelationships().get(0);
						currentShipMethod = sgci.getShippingGroup().getShippingMethod();
						if(currentShipMethod.equalsIgnoreCase(MFFConstants.STANDARD)) {
							return true;
						} else {
							if(mci.getExtendToShipMethods().contains(currentShipMethod)) {
								return true;
							}
						}
					}
				}
			}
		}
		return isFreeFreight;
	}
	public boolean isFreeFreightItemForShipMethod (CommerceItem pCurrentCommerceItem, String pShipMethod) {
		RepositoryItem sku = null;
		boolean isFreeFreight = false;
		boolean isFreeShipPromo = false;
		if(pCurrentCommerceItem != null){
			sku = (RepositoryItem)pCurrentCommerceItem.getAuxiliaryData().getCatalogRef();
			if(sku != null) {
				Object objFreeFreight = sku.getPropertyValue(MFFConstants.FREE_SHIPPING);
				isFreeFreight = (Boolean)objFreeFreight;
				
				if(isFreeFreight) {
					return true;
				}
				
				MFFCommerceItemImpl mci = (MFFCommerceItemImpl)pCurrentCommerceItem;
				isFreeShipPromo = mci.isFreeShippingPromo();
				if(isFreeShipPromo) {
					//
						if(pShipMethod.equalsIgnoreCase(MFFConstants.STANDARD)) {
							return true;
						} else {
							if(mci.getExtendToShipMethods().contains(pShipMethod)) {
								return true;
							}
						}
				}
			}
		}
		return isFreeFreight;
	}
	public boolean isFreeFreightShipping (CommerceItem pCurrentCommerceItem) {
		RepositoryItem sku = null;
		boolean isFreeFreight = false;
		if(pCurrentCommerceItem != null){
			sku = (RepositoryItem)pCurrentCommerceItem.getAuxiliaryData().getCatalogRef();
			if(sku != null) {
				Object objFreeFreight = sku.getPropertyValue(MFFConstants.FREE_SHIPPING);
				isFreeFreight = (Boolean)objFreeFreight;
				return isFreeFreight;
			}
		}
		return isFreeFreight;
	}	
	public boolean isLTLItem(CommerceItem pCurrentCommerceItem) {
	  RepositoryItem sku = null;
	  double length = 0.0;
	  double width = 0.0;
	  double weight = 0.0;
	  double depth = 0.0;
	  boolean ltl = false;
	  long qty=pCurrentCommerceItem.getQuantity();
	  
	  if(pCurrentCommerceItem != null){
	      sku = (RepositoryItem)pCurrentCommerceItem.getAuxiliaryData().getCatalogRef();
	      Object objLength = sku.getPropertyValue(MFFConstants.SKU_LENGTH);
	      Object objWidth = sku.getPropertyValue(MFFConstants.WIDTH);
	      Object objWeight = sku.getPropertyValue(MFFConstants.WEIGHT);
	      Object objDepth = sku.getPropertyValue(MFFConstants.SKU_DEPTH);
	      Object objLTL = sku.getPropertyValue(MFFConstants.SKU_LTL);
	      
	      if(objLTL != null) {
	    	  ltl = ((Boolean)objLTL).booleanValue();
	    	  if(ltl) {
	    		  return true;
	    	  }
	      }
      
	      // check for weight
	      if(objWeight != null){
	        weight = ((Double)objWeight).doubleValue();
	        double totalWeight = weight * qty;
	        if(totalWeight > 150){
	          return true;
	        } 
	      }
	      // length check
	      if(objLength != null){
	        length = ((Double)objLength).doubleValue();
	        if(length > 108){
	          return true;
	        }
	      }
	      // width & depth check
	      if(objWidth != null){
	        width = ((Double)objWidth).doubleValue();
	      }
	      if(objDepth != null){
	        depth = ((Double)objDepth).doubleValue();
	      }
      
	      double size = length + 2*(width + depth);
	      if(size > 164){
	        return true;
	      }
      
	  }
	  return false;
	}

}
