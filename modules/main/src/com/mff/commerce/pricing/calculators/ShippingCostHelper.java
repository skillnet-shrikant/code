package com.mff.commerce.pricing.calculators;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.droplet.security.GetAssignedPrincipalsDroplet;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class ShippingCostHelper extends GenericService {

	//Oversized calculations - configurations
	
	int oversizedSingleSideMax = 0;
	int oversizedTwoSideMax = 0;
	int oversizedWeightMax = 0;
	boolean factorQtyForOversizedActualWeight;
	boolean factorQtyForOversizedDimensionWeight;
	int dimensionWeightDivisor;
	
	int addlnHandlingMaxLength;
	int addlnHandlingMaxHeight;
	int addlnHandlingMaxWidth;
	


	int addlnHandlingMaxWeight;	
	boolean factorQtyForAddlnHandlingActualWeight;
	boolean factorQtyForAddlnHandlingDimensionWeight;
	boolean includeDimWeightForAddlHandling;
	
	public boolean isIncludeDimWeightForAddlHandling() {
		return includeDimWeightForAddlHandling;
	}
	public void setIncludeDimWeightForAddlHandling(boolean pIncludeDimWeightForAddlHandling) {
		includeDimWeightForAddlHandling = pIncludeDimWeightForAddlHandling;
	}
	public int getAddlnHandlingMaxLength() {
		return addlnHandlingMaxLength;
	}
	public void setAddlnHandlingMaxLength(int pAddlnHandlingMaxLength) {
		addlnHandlingMaxLength = pAddlnHandlingMaxLength;
	}
	public int getAddlnHandlingMaxHeight() {
		return addlnHandlingMaxHeight;
	}
	public void setAddlnHandlingMaxHeight(int pAddlnHandlingMaxHeight) {
		addlnHandlingMaxHeight = pAddlnHandlingMaxHeight;
	}
	public int getAddlnHandlingMaxWidth() {
		return addlnHandlingMaxWidth;
	}
	public void setAddlnHandlingMaxWidth(int pAddlnHandlingMaxWidth) {
		addlnHandlingMaxWidth = pAddlnHandlingMaxWidth;
	}
	public int getAddlnHandlingMaxWeight() {
		return addlnHandlingMaxWeight;
	}
	public void setAddlnHandlingMaxWeight(int pAddlnHandlingMaxWeight) {
		addlnHandlingMaxWeight = pAddlnHandlingMaxWeight;
	}
	
	public int getDimensionWeightDivisor() {
		return dimensionWeightDivisor;
	}
	public void setDimensionWeightDivisor(int pDimensionWeightDivisor) {
		dimensionWeightDivisor = pDimensionWeightDivisor;
	}

	public boolean isFactorQtyForOversizedActualWeight() {
		return factorQtyForOversizedActualWeight;
	}
	public void setFactorQtyForOversizedActualWeight(boolean pFactorQtyForOversizedActualWeight) {
		factorQtyForOversizedActualWeight = pFactorQtyForOversizedActualWeight;
	}
	public boolean isFactorQtyForOversizedDimensionWeight() {
		return factorQtyForOversizedDimensionWeight;
	}
	public void setFactorQtyForOversizedDimensionWeight(boolean pFactorQtyForOversizedDimensionWeight) {
		factorQtyForOversizedDimensionWeight = pFactorQtyForOversizedDimensionWeight;
	}
	public boolean isFactorQtyForAddlnHandlingActualWeight() {
		return factorQtyForAddlnHandlingActualWeight;
	}
	public void setFactorQtyForAddlnHandlingActualWeight(boolean pFactorQtyForAddlnHandlingActualWeight) {
		factorQtyForAddlnHandlingActualWeight = pFactorQtyForAddlnHandlingActualWeight;
	}
	public boolean isFactorQtyForAddlnHandlingDimensionWeight() {
		return factorQtyForAddlnHandlingDimensionWeight;
	}
	public void setFactorQtyForAddlnHandlingDimensionWeight(boolean pFactorQtyForAddlnHandlingDimensionWeight) {
		factorQtyForAddlnHandlingDimensionWeight = pFactorQtyForAddlnHandlingDimensionWeight;
	}
	public int getOversizedSingleSideMax() {
		return oversizedSingleSideMax;
	}
	public void setOversizedSingleSideMax(int pOversizedSingleSideMax) {
		oversizedSingleSideMax = pOversizedSingleSideMax;
	}
	public int getOversizedTwoSideMax() {
		return oversizedTwoSideMax;
	}
	public void setOversizedTwoSideMax(int pOversizedTwoSideMax) {
		oversizedTwoSideMax = pOversizedTwoSideMax;
	}
	public int getOversizedWeightMax() {
		return oversizedWeightMax;
	}
	public void setOversizedWeightMax(int pOversizedWeightMax) {
		oversizedWeightMax = pOversizedWeightMax;
	}
	
	/**
	 * Given a commerce item, determines is the item is eligible for oversized charges
	 * @param pCommerceItem
	 * @return
	 */
	
	public boolean isOverSized(CommerceItem pCommerceItem) {
		
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
		double totalweight = 0.0;
		double dimensionWeight = 0.0;
		double totalDimensionWeight = 0.0;
		
		// BZ 3063
		if(objLength != null && objWidth != null && objHeight != null){
			length = ((Double)objLength).doubleValue();
			width = ((Double)objWidth).doubleValue();
			height = ((Double)objHeight).doubleValue();
			
			// Rule #1
			if(length > getOversizedSingleSideMax() || 
					width > getOversizedSingleSideMax() || 
					height > getOversizedSingleSideMax()) {
				return true;
			}

			// Rule #2
			if( (length + width) > getOversizedTwoSideMax() || 
					(width + height) > getOversizedTwoSideMax() || 
					(height+length) > getOversizedTwoSideMax()) {
				return true;
			}
			
			dimensionWeight = (length * width * height)/getDimensionWeightDivisor();
			
			if(isFactorQtyForOversizedDimensionWeight()) {
				totalDimensionWeight = qty * dimensionWeight;
			} else { 
				totalDimensionWeight = dimensionWeight;
			}
		}

		if(objWeight != null){
			weight = ((Double)objWeight).doubleValue();
			if(isFactorQtyForOversizedActualWeight()) {
				totalweight = qty * weight;
			} else {
				totalweight = weight;
			}
		}
		
		// Rule #3
		if (totalweight > getOversizedWeightMax() || 
				totalDimensionWeight > getOversizedWeightMax()) {
			return true;
		}
		
		return false;
	}	

	/**
	 * Given a commerce item, checks if it has the free freight flag set
	 * or if there is a free item shipping promo on the item
	 * 
	 * @param pCurrentCommerceItem
	 * @return
	 */
	
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
	
	/**
	 * Given a commerce item, checks if it is eligible for additional handling
	 * 
	 * @param pCommerceItem
	 * @return
	 */
	public boolean isAdditionalHandling(CommerceItem pCommerceItem) {
		
		boolean returnFlag = false;
		// 2427 - Ignore if marked as free freight shipping
		if(isFreeFreightItem(pCommerceItem)) {
			return false;
		}
		
		if(isOverSized(pCommerceItem)) {
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
			if(length > getAddlnHandlingMaxLength() ){
				return true;
			}
		}
		
		if(objDepth != null){
			height = ((Double)objDepth).doubleValue();
			if(height > getAddlnHandlingMaxHeight()){
				return true;
			}
		}

		if (objWidth != null){
			width = ((Double)objWidth).doubleValue();
			if (width > getAddlnHandlingMaxWidth()) {
				return true;
			}
		}

		if(objWeight != null ) {
			weight = ((Double)objWeight).doubleValue();
			double actualWeight = weight;
			if(isFactorQtyForAddlnHandlingActualWeight()) {
				actualWeight = qty * weight;
			}
			
			double dimensionWeight = (length * width * height)/getDimensionWeightDivisor();
			if(isFactorQtyForAddlnHandlingDimensionWeight()) {
				dimensionWeight = qty * dimensionWeight;
			}
			
			if(isIncludeDimWeightForAddlHandling()) {
				if (actualWeight > getAddlnHandlingMaxWeight() ||
						dimensionWeight > getAddlnHandlingMaxWeight()) {
					return true;
				}				
			} else {
				if (actualWeight > getAddlnHandlingMaxWeight()) {
					return true;
				}				
			}

		}		
		return returnFlag;
	}	
	
}
