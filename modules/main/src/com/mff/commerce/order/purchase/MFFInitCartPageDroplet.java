package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.inventory.StockLevel;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.commerce.order.MFFOrderManager.UpdateCartForInventoryResult;
import com.mff.commerce.pricing.util.MFFPricingUtil;
import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.PricingModelHolder;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;

public class MFFInitCartPageDroplet extends DynamoServlet {
  private static final ParameterName PRICING_OP = ParameterName.getParameterName("pricingOp");
  private static final String STOCKLEVEL_FOR_SKUS_INORDER = "stockLevelForSkusInOrder";
  private static final String IS_ITEM_REMOVAL_REQUIRED = "isItemRemovalRequired";
  private static final String BOPIS_ITEMS_ONLY = "bopisItemsOnly";
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  private MFFOrderManager mOrderManager;
  private TransactionManager mTransactionManager;
  private PricingModelHolder mUserPricingModels;
  private String mDefaultPricingOp;
  private Profile mProfile;
  private MFFOrderImpl mOrder;
  private MFFPurchaseProcessHelper mPurchaseProcessHelper;
  private MFFCheckoutManager mCheckoutManager = null;
  private MFFCatalogTools mCatalogTools;

  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;
    boolean isItemRemovalRequired = false;
    boolean bopisItemsOnly = false;
    int bopisItemCount = 0;

    Map<String, StockLevel> stockLevelForSkusInOrder = new HashMap<String, StockLevel>();

    // loop through commerce items, if cart contains bopisOnlyItem set Item
    // removal to true
    if (getOrder().getCommerceItemCount() > 0) {
      @SuppressWarnings("unchecked")
      List<CommerceItem> lItems = getOrder().getCommerceItems();
      for (int i = 0; i < lItems.size(); i++) {
        CommerceItem lItem = lItems.get(i);
        if (lItem != null) {
          String prodId = lItem.getAuxiliaryData().getProductId();
          // call catalog tools to get the bopis only status
          boolean bopisOnly = getCatalogTools().isBopisOnlyProduct(prodId);
          if (bopisOnly) {
            bopisItemCount++;
          }
        }
      }

      // if bopis item count is equal to count of commerce items in order
      // set the bopisItemsOnly flag
      if (bopisItemCount == getOrder().getCommerceItemCount()) {
        bopisItemsOnly = true;
      } else if (bopisItemCount > 0) {
        isItemRemovalRequired = true;
      }

      vlogDebug("InitCartPageDroplet: Order:{0}, bopisItemsOnly:{1}, isITemRemovalRequired:{2}", getOrder().getId(), bopisItemsOnly, isItemRemovalRequired);
    }

    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      MFFOrderImpl order = getOrder();

      vlogInfo("Action: In InitCartPageDroplet for order id ({0}) and profile id ({1})", getOrder().getId(), getProfile().getRepositoryId());

      synchronized (order) {
        // This data is passed out to the cart for re-use in the
        stockLevelForSkusInOrder = getOrderManager().getStockLevelForSkusInOrder(order);

        // if it is a bopis order default the shipping method to bopis, so that
        // it will remove any shipping charges on the order
        if (((MFFOrderImpl) getOrder()).isBopisOrder()) {
          ((MFFHardgoodShippingGroup) getOrder().getShippingGroups().get(0)).setShippingMethod("Bopis");
        } else {
          List<CommerceItem> lItems = getOrder().getCommerceItems();
          boolean isLTL = false;
          boolean isFreeFreight =false;
          MFFPricingUtil util = new MFFPricingUtil();
          for (CommerceItem lItem : lItems) {
            RepositoryItem lSku = (RepositoryItem) lItem.getAuxiliaryData().getCatalogRef();
            isLTL = (Boolean) lSku.getPropertyValue(MFFConstants.SKU_LTL);
            isFreeFreight = util.isFreeFreightItem(lItem);
            if(isLTL && !isFreeFreight)
              break;
          }
          
          // 2427 - Set ship method only if order has ltl items that are not marked for free freight
          // else this uses ltl charges for non-ltl items
          if(isLTL && !isFreeFreight){
            ((MFFHardgoodShippingGroup) getOrder().getShippingGroups().get(0)).setShippingMethod("LTL-Truck");
          }else{
            ((MFFHardgoodShippingGroup) getOrder().getShippingGroups().get(0)).setShippingMethod("Standard");
          }
        }

        String pricingOp = getPricingOp(pRequest);

        // Finally, do a reprice.
        try {
          runRepriceOrder(pricingOp, order, getUserPricingModels(), getProfile(), pRequest);
        } catch (RunProcessException e) {
          if (isLoggingError()) {
            logError(e);
          }
        }
      }

      rollback = false;
    } catch (TransactionDemarcationException e) {
      if (isLoggingError()) {
        logError(e);
      }
    } finally {
      try {
        td.end(rollback);
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError()) {
          logError(tde);
        }
      }
    }

    // reset CheckoutStep to cart
    getCheckoutManager().authorizeShippingStep();
    pRequest.setParameter(BOPIS_ITEMS_ONLY, bopisItemsOnly);
    pRequest.setParameter(IS_ITEM_REMOVAL_REQUIRED, isItemRemovalRequired);
    pRequest.setParameter(STOCKLEVEL_FOR_SKUS_INORDER, stockLevelForSkusInOrder);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    // initialize the checkout values
    //getCheckoutManager().resetCheckoutValues();
  }

  private void runRepriceOrder(String pPricingOp, MFFOrderImpl pOrder, PricingModelHolder pUserPricingModels, RepositoryItem pProfile, DynamoHttpServletRequest pRequest) throws RunProcessException {

    RequestLocale requestLocale = pRequest.getRequestLocale();
    Locale locale = null;
    if (requestLocale != null) {
      locale = requestLocale.getLocale();
    }

    getPurchaseProcessHelper().runProcessRepriceOrder(pPricingOp, pOrder, pUserPricingModels, locale, pProfile, null, null);
  }

  protected String getPricingOp(DynamoHttpServletRequest pRequest) {
    String requestOp = pRequest.getParameter(PRICING_OP);
    if (StringUtils.isNotBlank(requestOp)) {
      return requestOp;
    }

    return getDefaultPricingOp();
  }

  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  public String getDefaultPricingOp() {
    return mDefaultPricingOp;
  }

  public void setDefaultPricingOp(String pDefaultPricingOp) {
    mDefaultPricingOp = pDefaultPricingOp;
  }

  public Profile getProfile() {
    return mProfile;
  }

  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  public MFFOrderManager getOrderManager() {
    return mOrderManager;
  }

  public void setOrderManager(MFFOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public MFFOrderImpl getOrder() {
    return mOrder;
  }

  public void setOrder(MFFOrderImpl pOrder) {
    mOrder = pOrder;
  }

  public MFFPurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

  public void setPurchaseProcessHelper(MFFPurchaseProcessHelper pPurchaseProcessHelper) {
    mPurchaseProcessHelper = pPurchaseProcessHelper;
  }

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
}
