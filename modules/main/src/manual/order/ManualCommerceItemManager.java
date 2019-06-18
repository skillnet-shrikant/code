package manual.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.PurchaseProcessConfiguration;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.PipelineErrorHandler;
import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;

public class ManualCommerceItemManager extends GenericService {
	
	private String mOrderType;
	AddCommerceItemInfo mItems[];
	
	String mCatalogRefIds[];
	String mProductId;
	long mQuantity;
	private long mMinQuantity;
    private long mMaxQuantity;
    
    PurchaseProcessConfiguration mConfiguration;
    
    PurchaseProcessHelper mPurchaseProcessHelper;
    HardgoodShippingGroup mShippingGroup;
    PricingModelHolder mUserPricingModels;
    @SuppressWarnings("rawtypes")
	private List mAddItemsToOrderResult;
    String mAddItemInfoClass = "atg.commerce.order.purchase.AddCommerceItemInfo";
    int mAddItemCount;
    private String mCommerceItemType;
    private String mShippingGroupType;
    String mMoveToPurchaseInfoChainId;
    PipelineManager mPipelineManager;
    TransactionManager mTransactionManager;
    MutableRepository profileRepository;
    CommerceItemManager mCommerceItemManager;
    CommercePropertyManager mCommercePropertyManager;
	PriceListManager mPriceListManager;
	private String mPriceListId;
    
	@SuppressWarnings("rawtypes")
	public void addItemToOrder(MFFOrderImpl pOrder, RepositoryItem pProfileItem) throws Exception {
		
		/**OrderLines orderLines = pOrderType.getOrderLines();
		
		List<LineItemType> lineItems = orderLines.getLineItem();
		
		int addItemCount = 0;
		
		for (int i=0; i < lineItems.size(); i++){
			
			LineItemType lineItem = lineItems.get(i);
			addItemCount = addItemCount + lineItem.getQuantity();
		}*/
		
		logDebug ("addItemToOrder(): Called.");
		
		this.setAddItemCount(1);
		
		for (int i=0; i < 1; i++){
			
			//LineItemType lineItem = lineItems.get(i);
			
			//String skuId = lineItem.getSkuId();
			
			
			getItems()[i].setCatalogRefId("000030080");

			String productId = getCommerceItemManager().getProductIdFromSkuId("000030080");
			logDebug ("addItemToOrder(): productId:" + productId);
			
			getItems()[i].setProductId(productId);
			getItems()[i].setCommerceItemType(getCommerceItemType());
	        getItems()[i].setShippingGroupType(getShippingGroupType());
	        getItems()[i].setSiteId("mffSite");
	        getItems()[i].setQuantity(2);
		}
		
		validateOrderQuantity();
		validateSelectedSKUs();
		
		Map extraParams = this.createRepriceParameterMap();
		
		HardgoodShippingGroup hgsgObj = (HardgoodShippingGroup) pOrder.getShippingGroups().get(0);
		
		logDebug("Repair Shipping Group::" +hgsgObj);
		
		String userLocale = (String) pProfileItem.getPropertyValue("locale");
		logDebug("userLocale::" + userLocale);
		
		List items = null;
		
		items = getPurchaseProcessHelper().addItemsToOrder(pOrder,
					hgsgObj, pProfileItem, getItems(),
					Locale.getDefault(), userLocale, getUserPricingModels(),
					new PipelineErrorHandler() {
						
						@Override
						public void handlePipelineError(Object arg0, String arg1) {
							
						}
					}, extraParams);
		
		setAddItemsToOrderResult(items);

		//mOrderManager.updateOrder(pOrder);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void runProcessMoveToPurchaseInfo(Order pOrder,
			PricingModelHolder pPricingModels, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws RunProcessException {
		
		logDebug("runProcessMoveToPurchaseInfo() called.");
		
		HashMap map = new HashMap(19);
		map.put("ValidateCommerceItemInOrder", Boolean.TRUE);
		map.put("ValidateShippingGroupInOrder", Boolean.FALSE);
		map.put("ValidatePaymentGroupInOrder", Boolean.FALSE);
		atg.service.pipeline.PipelineResult result = runProcess(
				getMoveToPurchaseInfoChainId(), pOrder, pPricingModels,
				pLocale, pProfile, map, pExtraParameters);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PipelineResult runProcess(String pChainId, Order pOrder,
			PricingModelHolder pPricingModels, Locale pLocale,
			RepositoryItem pProfile, Map pParameters, Map pExtraParameters)
			throws RunProcessException {
		logDebug("runProcess() called.");
		if (pChainId == null) {
			logDebug("runProcess skipped because chain ID is null");
			return null;
		}
		Map params = null;
		if (pParameters == null)
			params = new HashMap();
		else
			params = pParameters;
		try {
			
			MFFOrderManager orderManager = (MFFOrderManager) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/order/OrderManager");

			OrderTools orderTools = orderManager.getOrderTools();
			params.put("Order", pOrder);
			params.put("PricingModels", pPricingModels);
			params.put("Locale", pLocale);
			params.put("Profile", pProfile);
			params.put("OrderManager", orderManager);
			params.put("CatalogTools", orderTools.getCatalogTools());
			params.put("InventoryManager", orderTools.getInventoryManager());
			params.put("ExtraParameters", pExtraParameters);
			return runProcess(pChainId, params);
		} catch (RunProcessException exc) {
			throw exc;
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

	@SuppressWarnings("rawtypes")
	protected PipelineResult runProcess(String pChainId, Map pParameters) throws RunProcessException {
		
		//if (isLoggingDebug())
			logDebug((new StringBuilder())
					.append("runProcess called with chain ID = ")
					.append(pChainId).toString());
		if (pChainId == null)
			return null;
		try {
			return getPipelineManager().runProcess(pChainId, pParameters);
		} catch (RunProcessException exc) {
			try {
				setTransactionToRollbackOnly();
			} catch (SystemException e) {
				if (isLoggingError())
					logError(e);
			}
			throw exc;
		}
	}
	
	protected void setTransactionToRollbackOnly() throws SystemException {
		TransactionManager tm = getTransactionManager();
		if (tm != null) {
			Transaction t = tm.getTransaction();
			if (t != null)
				t.setRollbackOnly();
		}
	}
	
	public ShippingGroup getShippingGroup(MFFOrderImpl pOrder) {
		if (mShippingGroup == null)
			mShippingGroup = (HardgoodShippingGroup) getPurchaseProcessHelper()
					.getFirstShippingGroup(pOrder);
		return mShippingGroup;
	}
	
	
    
	protected void validateSelectedSKUs() {
		if (getItems() == null || getItems().length == 0) {
			if (getCatalogRefIds() == null || getCatalogRefIds().length == 0
					|| getCatalogRefIds()[0].isEmpty())
				logError("*****No SKUs to add");
		} else {
			AddCommerceItemInfo arr$[] = getItems();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				AddCommerceItemInfo itemInfo = arr$[i$];
				if (itemInfo.getCatalogRefId() == null
						|| itemInfo.getCatalogRefId().length() == 0)
					logError("*****No SKUs to add");
			}
		}
	}
    
	protected void validateOrderQuantity() throws IOException {
		AddCommerceItemInfo items[] = getItems();
		if (items != null) {
			long itemsSelected = 0L;
			for (int i = 0; i < items.length; i++) {
				AddCommerceItemInfo item = items[i];
				try {
					long quantity = item.getQuantity();
					if (isQuantityValid(quantity))
						itemsSelected += quantity;
				} catch (NumberFormatException nfe) {
					logError("invalidQuantity");
				}
			}

			if (itemsSelected == 0L)
				logError("noItemsToAdd");
		} else {
			long quantity = getQuantity();
			if (isQuantityValid(quantity) && quantity <= 0L)
				logError("invalidQuantity");
		}
		if (isLoggingDebug()) {
			// logDebug("validateQuantity(): Quantity is less than -1");
		}
	}
	
	public void setAddItemCount(int pAddItemCount) {
		if (pAddItemCount <= 0) {
			mAddItemCount = 0;
			mItems = null;
		} else {
			mAddItemCount = pAddItemCount;
			mItems = new AddCommerceItemInfo[mAddItemCount];
			Throwable caught = null;
			try {
				for (int index = 0; index < pAddItemCount; index++)
					mItems[index] = (AddCommerceItemInfo) Class.forName(
							getAddItemInfoClass()).newInstance();

			} catch (Throwable thrown) {
				caught = thrown;
			}
			if (caught != null) {
				if (isLoggingError())
					logError(caught);
				mItems = null;
			}
		}
	}

	public AddCommerceItemInfo[] getItems() {
		return mItems;
	}
	
	protected boolean isQuantityValid(long pQuantity) {
		if (mMinQuantity > -1L && pQuantity < mMinQuantity) {
			logError("quantityLessThanMin");
			return false;
		}
		if (mMaxQuantity > -1L && pQuantity > mMaxQuantity) {
			logError("quantityMoreThanMax");
			return false;
		}
		if (pQuantity < 0L) {
			logError("invalidQuantity");
			return false;
		} else {
			return true;
		}
	}

	public String getOrderType() {
		return mOrderType;
	}

	public void setOrderType(String pOrderType) {
		this.mOrderType = pOrderType;
	}

	public void setProductId(String pProductId) {
		mProductId = pProductId;
	}

	public String getProductId() {
		return mProductId;
	}

	public void setQuantity(long pQuantity) {
		mQuantity = pQuantity;
	}

	public long getQuantity() {
		return mQuantity;
	}

	public void setMinQuantity(long pMinQuantity) {
		mMinQuantity = pMinQuantity;
	}

	public long getMinQuantity() {
		return mMinQuantity;
	}

	public void setMaxQuantity(long pMaxQuantity) {
		mMaxQuantity = pMaxQuantity;
	}

	public long getMaxQuantity() {
		return mMaxQuantity;
	}

	public void setCatalogRefIds(String pCatalogRefIds[]) {
		mCatalogRefIds = pCatalogRefIds;
	}

	public String[] getCatalogRefIds() {
		return mCatalogRefIds;
	}

	public void setConfiguration(PurchaseProcessConfiguration pConfiguration) {
		mConfiguration = pConfiguration;
	}

	public PurchaseProcessConfiguration getConfiguration() {
		return mConfiguration;
	}

	public void setPurchaseProcessHelper(
			PurchaseProcessHelper pPurchaseProcessHelper) {
		mPurchaseProcessHelper = pPurchaseProcessHelper;
	}

	public PurchaseProcessHelper getPurchaseProcessHelper() {
		return mPurchaseProcessHelper;
	}

	public void setShippingGroup(HardgoodShippingGroup pShippingGroup) {
		mShippingGroup = pShippingGroup;
	}

	public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
		mUserPricingModels = pUserPricingModels;
	}

	public PricingModelHolder getUserPricingModels() {
		return mUserPricingModels;
	}

	@SuppressWarnings("rawtypes")
	protected List getAddItemsToOrderResult() {
		return mAddItemsToOrderResult;
	}

	@SuppressWarnings("rawtypes")
	protected void setAddItemsToOrderResult(List pAddItemsToOrderResult) {
		mAddItemsToOrderResult = pAddItemsToOrderResult;
	}

	public void setAddItemInfoClass(String pAddItemInfoClass) {
		mAddItemInfoClass = pAddItemInfoClass;
	}

	public String getAddItemInfoClass() {
		return mAddItemInfoClass;
	}

	public int getAddItemCount() {
		return mAddItemCount;
	}

	public void setCommerceItemType(String pCommerceItemType) {
		mCommerceItemType = pCommerceItemType;
	}

	public String getCommerceItemType() {
		return mCommerceItemType;
	}

	public String getShippingGroupType() {
		return mShippingGroupType;
	}

	public void setShippingGroupType(String pShippingGroupType) {
		this.mShippingGroupType = pShippingGroupType;
	}
		
	public void setMoveToPurchaseInfoChainId(String pMoveToPurchaseInfoChainId) {
		mMoveToPurchaseInfoChainId = pMoveToPurchaseInfoChainId;
	}

	public String getMoveToPurchaseInfoChainId() {
		return mMoveToPurchaseInfoChainId;
	}

	public void setPipelineManager(PipelineManager pPipelineManager) {
		mPipelineManager = pPipelineManager;
	}

	public PipelineManager getPipelineManager() {
		return mPipelineManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public CommerceItemManager getCommerceItemManager() {
		return mCommerceItemManager;
	}

	public void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
		this.mCommerceItemManager = pCommerceItemManager;
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