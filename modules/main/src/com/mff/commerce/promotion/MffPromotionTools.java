package com.mff.commerce.promotion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.definition.DiscountStructureElem;
import atg.commerce.pricing.definition.PricingModelElem;
import atg.commerce.promotion.PromotionException;
import atg.commerce.promotion.PromotionTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.purchase.AppliedPromotion;
import com.mff.constants.MFFConstants;

public class MffPromotionTools extends PromotionTools {
	
	private static ResourceBundle sResourceBundle = LayeredResourceBundle
			.getBundle("atg.commerce.promotion.PromotionResources", LangLicense.getLicensedDefault());

	private ClaimableTools mClaimableTools;
	
	public boolean checkPromotionGrant(RepositoryItem pProfile, RepositoryItem pPromotion, RepositoryItem pCoupon,
			String[] pCheckProps) throws PromotionException {
		if (pProfile == null) {
			String msg = ResourceUtils.getMsgResource("nullProfile", "atg.commerce.promotion.PromotionResources",
					sResourceBundle);

			throw new PromotionException("nullProfile", msg);
		}

		if (pPromotion == null) {
			String msg = ResourceUtils.getMsgResource("nullPromotion", "atg.commerce.promotion.PromotionResources",
					sResourceBundle);

			throw new PromotionException("nullPromotion", msg);
		}

		if (checkPromotionExpiration(pPromotion, getCurrentDate().getTimeAsDate()) && pCoupon != null) {
			String[] msgArgs = { pCoupon.getRepositoryId() };

			String msg = ResourceUtils.getMsgResource("expiredPromotion", "atg.commerce.promotion.PromotionResources",
					sResourceBundle, msgArgs);

			throw new PromotionException("expiredPromotionUserResource", msg);
		}

		return super.checkPromotionGrant(pProfile, pPromotion, pCoupon, pCheckProps);
	}
	
	public double getShippingDiscount(MFFOrderImpl pOrderImpl,ShippingGroup pShippingGroup){
    double shippingPromoAmount = 0.0d;
    
    if(pOrderImpl != null && pShippingGroup != null){
      List<PricingAdjustment> shippingPromos = new ArrayList<PricingAdjustment>();
      getShippingPromotions(pShippingGroup, shippingPromos, null, true);
      if(!shippingPromos.isEmpty()){
        for(int i=0;i<shippingPromos.size();i++){
          PricingAdjustment priceAdj=(PricingAdjustment) shippingPromos.get(i);
          shippingPromoAmount += priceAdj.getTotalAdjustment();
        }
        shippingPromoAmount = shippingPromoAmount * -1; // multiply by -1 to change sign from -ve to +ve
      }
    }
    
    return shippingPromoAmount;
	}
	
	/**
	   * Gets the promotion from adjustment.
	   *
	   * @param adjustments
	   *          the adjustments
	   * @param orderAppliedPromotions
	   *          the order applied promotions
	   * @return the promotion from adjustment
	   * @throws ClaimableException
	   *           the claimable exception
	   */
	  @SuppressWarnings("rawtypes")
	public void getPromotionFromAdjustment(List<?> adjustments,
			List<AppliedPromotion> orderAppliedPromotions,
			Map<String, String> globalPromotions,
			Map<String, Double> globalPromotionToAdjustments,
			Map<String, String> shippingPromotions,
			Map<String, Double> shippingPromotionToAdjustments,
			String discountType) throws ClaimableException {
		
	    RepositoryItem[] couponItems = null;
	    Iterator adjIter = adjustments.iterator();

	    while (adjIter.hasNext()) {
	      PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter.next();
	      if (pricingAdjustment == null) {
	        continue;
	      }
	      RepositoryItem promotion = pricingAdjustment.getPricingModel();
	      vlogDebug("getPromotionFromAdjustment(): promotion: " + promotion);

	      if (promotion != null) {

	    	// 2414 - Check if promo if a GWP promo
	    	PricingModelElem model=null;
	    	DiscountStructureElem dse = null;
	
	    	String calcType=null;
	    	try {
	    		model = (PricingModelElem) getQualifierService().getPMDLCache().get(promotion);
	    		if(model!=null) {
	    		  if(model.getOffer()!=null ) {
	    		    if(model.getOffer().getSubElements()!=null) { 
	    		       dse = (DiscountStructureElem)model.getOffer().getSubElements()[0];
	    		       calcType = dse.getCalculatorType();
	    		    }
  	    		}
	    		}
	    	} catch (Exception e) {
	    		  e.printStackTrace();
	    	}
	        Boolean isGlobal = (Boolean) promotion.getPropertyValue(MFFConstants.GLOBAL);
	        if (isGlobal == null) continue;
	        
	        String description = (String) promotion
					.getPropertyValue(MFFConstants.DESCRIPTION);
			
			String promoName = (String) promotion
					.getPropertyValue(MFFConstants.PROMO_DISP_NAME);
			
			if (StringUtils.isEmpty(promoName)) {
				promoName = description;
			}
			
	        if (isGlobal.booleanValue()){
	        	if (StringUtils.isEmpty(description)) {
					description = promotion.getRepositoryId();
				}
	        	
		        if( discountType.equalsIgnoreCase("shipping") || (calcType != null && calcType.equalsIgnoreCase("freeItemShipping"))){
		        	shippingPromotions.put(promoName,promoName);
					shippingPromotionToAdjustments.put(promoName, pricingAdjustment.getTotalAdjustment() * -1);
		        }else{
		        	
		        	// 2414 - Add only for non-GWP promos. GWP promo details are already shown in the cart
		        	if(calcType != null && !calcType.equalsIgnoreCase("gwp")) {
		        		globalPromotions.put(promoName,description);
		        		globalPromotionToAdjustments.put(promoName, pricingAdjustment.getTotalAdjustment() * -1);
		        	}
		        }
	        	
	        } else {
				
	        	//2414 - Factor only non-gwp promos. GWP promo details are already shown in the cart item
	        	if(calcType != null && !calcType.equalsIgnoreCase("gwp")) {
	        		String promoShortDesc = (String) promotion
	        				.getPropertyValue(MFFConstants.PROMO_SHORT_DESC);
	        		if (StringUtils.isEmpty(promoShortDesc)) {
	        			promoShortDesc = description;
	        		}
	        		couponItems = getClaimableTools().getCouponsForPromotion(
	        				promotion.getRepositoryId());
	        		AppliedPromotion promo = new AppliedPromotion();
	        		if (couponItems != null) {
	        			promo.setCouponCode(couponItems[0].getRepositoryId());
	        		} else {
	        			promo.setCouponCode(promotion.getRepositoryId());
	        		}
	        		promo.setDescription(description);
	        		promo.setPromoName(promoName);
	        		promo.setPromoShortDesc(promoShortDesc);
	        		promo.setPromoId(promotion.getRepositoryId());
	        		if(discountType.equalsIgnoreCase("item") && pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase("Item Free Shipping Discount")) {
	        			shippingPromotions.put(promoName,promoName);
	        			shippingPromotionToAdjustments.put(promoName, 1.0);
	        		}
	        		promo.setDiscountType(discountType);
	        		promo.setDiscountAmount(getDiscountedAmount(adjustments));

	                if(couponItems != null) {
	                	if(!isCouponExists(orderAppliedPromotions, couponItems[0].getRepositoryId())) {
	                		orderAppliedPromotions.add(promo);
	                	}
	                } else {
	                	orderAppliedPromotions.add(promo);
	                }
	        	}
			}
	      }
	    }
	  }
	  private boolean isCouponExists (List<AppliedPromotion> orderAppliedPromotions, String couponCode) {
		  boolean couponCodeExists = false;
		  Iterator<AppliedPromotion> iter = orderAppliedPromotions.iterator();
		  AppliedPromotion promo=null;
		  while(iter.hasNext()) {
			  promo=iter.next();
			  if(promo!= null & promo.getCouponCode() != null && promo.getCouponCode().equalsIgnoreCase(couponCode)) {
				  couponCodeExists=true;
				  break;
			  }
		  }
		  return couponCodeExists;
	  }	  
	  public boolean isGlobalPromoExists(List<?> adjustments)
			throws ClaimableException {
		  
		    Iterator adjIter = adjustments.iterator();

		    while (adjIter.hasNext()) {
		      PricingAdjustment pricingAdjustment = (PricingAdjustment) adjIter.next();
		      if (pricingAdjustment == null) {
		        continue;
		      }

		      RepositoryItem promotion = pricingAdjustment.getPricingModel();

		      if (promotion != null) {
		    	  
		        Boolean isGlobal = (Boolean) promotion.getPropertyValue(MFFConstants.GLOBAL);
				if (isGlobal != null && isGlobal.booleanValue()) {
					return true;
		        }
		      }
		    }
		    return false;
	  }

	  /**
	   * In case order does have any qualified items to be applied to the cart but
	   * yet user has applied promotion, make sure promotion is picked up from
	   * activePromotions and displayed to the user.
	   *
	   * @param activePromotions
	   *          the active promotions
	   * @param orderAppliedPromotions
	   *          the order applied promotions
	   * @return the map
	   * @throws ClaimableException
	   *           the claimable exception
	   */
	  @SuppressWarnings("rawtypes")
	  public List<AppliedPromotion> applyRemainingActivePromotions(List activePromotions, List<AppliedPromotion> orderAppliedPromotions) throws ClaimableException {

	    RepositoryItem[] couponItems = null;
	    if (orderAppliedPromotions == null || orderAppliedPromotions.size() == 0) {
	      if (orderAppliedPromotions == null) {
	        orderAppliedPromotions = new ArrayList<AppliedPromotion>();
	      }

	      if (activePromotions != null) {

	        List<RepositoryItem> userPromotions = new ArrayList<RepositoryItem>();
	        RepositoryItem promotionStatus = null;
	        RepositoryItem promotion = null;
	        for (int i = 0; i < activePromotions.size(); i++) {
	          promotionStatus = (RepositoryItem) activePromotions.get(i);
	          promotion = (RepositoryItem) promotionStatus.getPropertyValue(MFFConstants.PROMOTION);
	          
	          if (promotionStatus != null && promotion != null) {
	            Boolean isGlobal = (Boolean) promotion.getPropertyValue(MFFConstants.GLOBAL);
	            if (isGlobal != null && !isGlobal.booleanValue()) {
	            	userPromotions.add(promotion);
	            }
	          }
	        }
	        
	        if (userPromotions.size() > 0) {
	          String description = null;
	          for (RepositoryItem pPromotion : userPromotions) {
	            if (pPromotion != null) {

	              if (pPromotion.getPropertyValue(MFFConstants.DESCRIPTION) != null) {
	                description = (String) pPromotion.getPropertyValue(MFFConstants.DESCRIPTION);
	              }

	              if (StringUtils.isEmpty(description)) {
	                description = pPromotion.getRepositoryId();
	              }
	              
	              String promoName = (String) promotion.getPropertyValue(MFFConstants.PROMO_DISP_NAME);
	              if (StringUtils.isEmpty(promoName)) {
	              	promoName = description;
	              }
	              String promoShortDesc = (String) promotion.getPropertyValue(MFFConstants.PROMO_SHORT_DESC);
	              if (StringUtils.isEmpty(promoShortDesc)) {
	              	promoShortDesc = description;
	              }
	              
	              couponItems = getClaimableTools().getCouponsForPromotion(pPromotion.getRepositoryId());
	              if (couponItems != null && !isCouponExists(orderAppliedPromotions, couponItems[0].getRepositoryId())) {
	                AppliedPromotion promo = new AppliedPromotion();
	                promo.setCouponCode(couponItems[0].getRepositoryId());
	                promo.setPromoName(promoName);
	                promo.setPromoShortDesc(promoShortDesc);
	                promo.setDescription(description);
	                promo.setPromoId(promotion.getRepositoryId());
	                promo.setDiscountType("");
	                orderAppliedPromotions.add(promo);
	              }
	            }
	          }
	        }
	      }
	    }
	    return orderAppliedPromotions;
	  }
	  
	  /**
	   * Gets the discounted amount.
	   *
	   * @param adjustments
	   *          the adjustments
	   * @return the discounted amount
	   */
	  @SuppressWarnings("rawtypes")
	  public double getDiscountedAmount(List<?> adjustments) {
	    double discountAdjustment = 0.0;
	    if (adjustments != null) {
	      Iterator itAdj = adjustments.iterator();
	      PricingAdjustment adj = null;
	      RepositoryItem promotion = null;

	      while (itAdj.hasNext()) {
	        adj = (PricingAdjustment) itAdj.next();
	        promotion = adj.getPricingModel();

	        if (promotion != null) {

	          Boolean isGlobal = (Boolean) promotion.getPropertyValue(MFFConstants.GLOBAL);
	          if (isGlobal != null && isGlobal.booleanValue()) {
	            continue;
	          }

	          double adjustedAmount = adj.getTotalAdjustment();
	          if (adjustedAmount > 0.0) {
	            discountAdjustment += adjustedAmount;
	          } else {
	            discountAdjustment += (adjustedAmount * -1);
	          }

	        }
	      }
	    }
	    return discountAdjustment;
	  }
	  
	  /**
	   * <p>
	   * Method to handle calculation of Item Discount by coupon.
	   *
	   * @param commerceItems
	   *          the commerce items
	   * @return The discount amount by coupon id.
	   */
	  @SuppressWarnings("rawtypes")
	  public double calculateCouponItemDiscountAdjustments(List commerceItems) {
	    double discountAdjustment = 0.0d;
	    if (commerceItems != null && commerceItems.size() > 0) {
	      Iterator iter = commerceItems.iterator();
	      while (iter.hasNext()) {
	        MFFCommerceItemImpl pItem = (MFFCommerceItemImpl) iter.next();
	        if (pItem.getPriceInfo() != null) {
	          List adjustments = pItem.getPriceInfo().getAdjustments();
	          if (adjustments != null && adjustments.size() > 0) {
	            discountAdjustment = getDiscountedAmount(adjustments);
	          }
	        }
	      }
	    }
	    return discountAdjustment;
	  }

	  /**
	   * Calculate coupon shipping discount adjustments.
	   *
	   * @param sgList
	   *          the sg list
	   * @return the double
	   */
	  @SuppressWarnings("rawtypes")
	  public double calculateCouponShippingDiscountAdjustments(List<?> sgList) {
	    double discountAdjustment = 0.0d;
	    if (sgList.size() >= 1) {
	      Iterator<?> sgiter = sgList.iterator();
	      while (sgiter.hasNext()) {
	        ShippingGroup sg = (ShippingGroup) sgiter.next();
	        ShippingPriceInfo shipPricingInfo = sg.getPriceInfo();
	        if (shipPricingInfo != null) {
	          List adjustments = shipPricingInfo.getAdjustments();
	          if (adjustments != null && adjustments.size() > 0) {
	            discountAdjustment = getDiscountedAmount(adjustments);
	          }
	        }
	      }
	    }
	    return discountAdjustment;
	  }

	  /**
	   * <p>
	   * Method to handle calculation of Item Discount by coupon.
	   *
	   * @param order
	   *          the order
	   * @return The discount amount by coupon id.
	   */
	  @SuppressWarnings("rawtypes")
	  public double calculateCouponOrderDiscountAdjustments(Order order) {
	    double discountAdjustment = 0.0d;

	    if (order != null) {
	      List orderAdjustments = order.getPriceInfo().getAdjustments();
	      if (orderAdjustments != null && orderAdjustments.size() > 0) {
	        discountAdjustment = getDiscountedAmount(orderAdjustments);
	      }
	    }
	    return discountAdjustment;
	  }

	  /**
	   * Sets the claimable tools.
	   *
	   * @param pClaimableTools
	   *          the new claimable tools
	   */
	  public void setClaimableTools(ClaimableTools pClaimableTools) {
	    mClaimableTools = pClaimableTools;
	  }

	  /**
	   * Gets the claimable tools.
	   *
	   * @return the claimable tools
	   */
	  public ClaimableTools getClaimableTools() {
	    return mClaimableTools;
	  }
    
}
