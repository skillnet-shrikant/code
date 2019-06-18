package mff.commerce.pricing;

import java.util.Map;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.pricing.Constants;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingEngineService;
import atg.commerce.pricing.PricingModelEvaluationVetoer;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class OrderTypePromotionVetoer extends GenericService implements PricingModelEvaluationVetoer {

	public static final String ORDER_TYPE_NOT_SET = "Order Type not set in Promotion";
	public static final String VETO_REASON_ORDER_TYPE_NOT_MATCHED = "Promotion is applicable only to BOPIS orders and the current order is not one.";
	
	@Override
	public Object prepareForVetoing(PricingEngineService pParamPricingEngineService,
			PricingContext pParamPricingContext, Map pParamMap) {

		MFFOrderImpl order = (MFFOrderImpl)pParamPricingContext.getOrder();
		if(order != null)
			return order.isBopisOrder();
		else
			return false;
		
	}

	@Override
	public String vetoPromotion(PricingEngineService pParamPricingEngineService, PricingContext pParamPricingContext,
			Map pParamMap, Object pParamObject) {

		if(pParamObject == null) {
			return null;
		} else if(!(pParamObject instanceof Boolean)) {
			return ORDER_TYPE_NOT_SET;
		}
		
		
		boolean bBopisOrder = (Boolean) pParamObject;
		
		RepositoryItem promotion = null;
		if (pParamPricingContext != null) {
			promotion = pParamPricingContext.getPricingModel();
		} else {
				if (isLoggingError()) {
					logError(Constants.NO_PRICING_CONTEXT);
				}
				return Constants.VETO_REASON_NO_PRICING_CONTEXT;
		}
		
		if (promotion == null) {
			  if (isLoggingError()) {
			    logError(Constants.NULL_PROMOTION);
			  }
			  return Constants.VETO_REASON_NULL_PROMOTION;
		}

		boolean bPromoBopisOnly = (Boolean)promotion.getPropertyValue("bopisOnly");
			
		
		if(bPromoBopisOnly && !bBopisOrder) {
			return VETO_REASON_ORDER_TYPE_NOT_MATCHED;
		}
		return null;
	}
	
}