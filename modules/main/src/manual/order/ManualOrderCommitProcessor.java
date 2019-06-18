package manual.order;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.PipelineErrorHandler;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.RunProcessException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;

public class ManualOrderCommitProcessor extends GenericService {

	PipelineManager mPipelineManager;
	
	PurchaseProcessHelper mPurchaseProcessHelper;
	TransactionManager mTransactionManager;
	CommercePropertyManager mCommercePropertyManager;
	PriceListManager mPriceListManager;
	private String mPriceListId;

	/**
	 * This method submits the order.
	 */
	@SuppressWarnings("rawtypes")
	public void processOrderCommit(MFFOrderImpl pOrder,
			RepositoryItem pProfileItem, MFFOrderManager mOrderManager,
			TransactionManager transactionManager) throws RunProcessException {

		logDebug("processOrderCommit(): Called with order version: "
				+ pOrder.getVersion());

		updateProfileIdOnOrder(mOrderManager, pOrder, pProfileItem);

		// reprice order before commit to be sure that shipping prices and
		// taxes are included

		Map extraParams = this.createRepriceParameterMap();
		try {
			runProcessRepriceOrder(pOrder, null, Locale.getDefault(),
					pProfileItem, extraParams);

		} catch (RunProcessException e) {
			if (isLoggingError()) {
				logError("processOrderCommit():RunProcessException while order commit: "
						+ e);
			}
		}
		commitOrder(pOrder, mOrderManager);
	}
	

	@SuppressWarnings("rawtypes")
	protected void runProcessRepriceOrder(Order pOrder,
			PricingModelHolder pPricingModels, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws RunProcessException {

		logDebug("runProcessRepriceOrder(): Called");
		
		getPurchaseProcessHelper().runProcessRepriceOrder("ORDER_TOTAL",
				pOrder, pPricingModels, pLocale, pProfile, pExtraParameters,
				new PipelineErrorHandler() {
					
					@Override
					public void handlePipelineError(Object arg0, String arg1) {
						vlogError("pipeline error during repricing order.");
					}
				});
	}

	@SuppressWarnings("unused")
	public void commitOrder(Order pOrder, MFFOrderManager mOrderManager) {

		logDebug("commitOrder(): Called");

		try {
			atg.service.pipeline.PipelineResult result = mOrderManager.processOrder(pOrder, getProcessOrderMap(Locale.getDefault(), mOrderManager));

		} catch (Exception exc) {			
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map createRepriceParameterMap() {
        RepositoryItem pricelist = getPriceList("US");
        if(pricelist == null){
        	logDebug ("priceList is null, hence returning null..");
            return null;
        }
        HashMap parameters = new HashMap();
        CommercePropertyManager props = getCommercePropertyManager();
        if(props != null)
        {
            String priceListPropertyName = getCommercePropertyManager().getPriceListPropertyName();
            
            logDebug ("priceListPropertyName::" + priceListPropertyName);
            logDebug ("pricelist item::" + pricelist);
            parameters.put(priceListPropertyName, pricelist);
        } else{
        	logDebug ("props is null, hence ABNORMAL scenario..");
        }
        return parameters;
    }
	
	protected RepositoryItem getPriceList(String pShippingCntry) {

		try {
            RepositoryItem priceList = getPriceListManager().getPriceList(getPriceListId());
            
            if (priceList !=null){
            	logDebug (" getPriceList(): priceList item found with pricelist id:: " + priceList.getRepositoryId());
            }
            
            return priceList;
        } catch(PriceListException e) {
            if(isLoggingError()){
                logError(" getPriceList(): Error while fecting priceList with priceListId: "+ getPriceListId() );
            }
        }
        return null;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap getProcessOrderMap(Locale pLocale,
			MFFOrderManager mOrderManager) throws CommerceException {

		logDebug("getProcessOrderMap(): Called");

		HashMap pomap = mOrderManager.getProcessOrderMap(pLocale, null);
		pomap.put("SiteId", "mffSite");
		pomap.put("SalesChannel", "javaprogram");
		return pomap;
	}
	
	public void updateProfileIdOnOrder(MFFOrderManager mOrderManager, Order pOrder, RepositoryItem pProfile) {
	    String profileId = pProfile.getRepositoryId();
	    
	    if (!pOrder.getProfileId().equals(profileId)) {
	      if (isLoggingDebug()) {
	        logDebug("Setting profile_id on order.");
	      }

	      pOrder.setProfileId(pProfile.getRepositoryId());

	      try {
	    	  mOrderManager.updateOrder(pOrder);
	      } 
	      catch (CommerceException ce) {
	        if (isLoggingError()) {
	          logError("Could not update order: " + ce, ce);
	        }
	      }
	    }
	  }

	public void setPurchaseProcessHelper(
			PurchaseProcessHelper pPurchaseProcessHelper) {
		mPurchaseProcessHelper = pPurchaseProcessHelper;
	}

	public PurchaseProcessHelper getPurchaseProcessHelper() {
		return mPurchaseProcessHelper;
	}

	public void setPipelineManager(PipelineManager pPipelineManager) {
		mPipelineManager = pPipelineManager;
	}

	public PipelineManager getPipelineManager() {
		return mPipelineManager;
	}
	
	public void setPriceListManager(PriceListManager pPriceListManager) {
		mPriceListManager = pPriceListManager;
	}

	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}
	
	public void setCommercePropertyManager(
			CommercePropertyManager pCommercePropertyManager) {
		mCommercePropertyManager = pCommercePropertyManager;
	}

	public CommercePropertyManager getCommercePropertyManager() {
		return mCommercePropertyManager;
	}	
	
	public void setPriceListId(String pPriceListId) {
		mPriceListId = pPriceListId;
	}

	public String getPriceListId() {
		return mPriceListId;
	}	
}