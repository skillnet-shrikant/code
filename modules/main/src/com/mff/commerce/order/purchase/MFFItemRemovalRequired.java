package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.order.MFFOrderImpl;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This Droplet is used to verify if cart contains any bopisOnly Items
 * if Yes, set ItemRemovalRequired to true, so that in the UI we will trigger a Modal
 * to inform the customer about removal
 * @author DMI
 *
 */
public class MFFItemRemovalRequired extends DynamoServlet {

  OrderHolder mShoppingCart;
  private MFFCatalogTools mCatalogTools;
  
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  public static final ParameterName ERROR = ParameterName.getParameterName("error");
  private static final String IS_ITEM_REMOVAL_REQUIRED = "isItemRemovalRequired";
  private static final String BOPIS_ITEMS_ONLY = "bopisItemsOnly";
  
  @SuppressWarnings("unchecked")
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    boolean isItemRemovalRequired = false;
    boolean bopisItemsOnly = false;
    int bopisItemCount = 0;
    
    if(getShoppingCart().getCurrent()== null){
      vlogError("No order found");
      pRequest.setParameter(IS_ITEM_REMOVAL_REQUIRED, isItemRemovalRequired);
      pRequest.setParameter(BOPIS_ITEMS_ONLY, bopisItemsOnly);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      return;
    }
    
    MFFOrderImpl lOrder = (MFFOrderImpl) getShoppingCart().getCurrent();
    
    //loop through commerce items, if cart contains bopisOnlyItem set Item removal to true
    if (lOrder.getCommerceItemCount() > 0) {
      List<CommerceItem> lItems = lOrder.getCommerceItems();
      for (int i = 0; i < lItems.size(); i++) {
        CommerceItem lItem = lItems.get(i);
        if (lItem != null) {
           String prodId = lItem.getAuxiliaryData().getProductId();
           //call catalog tools to get the bopis only status
           boolean bopisOnly = getCatalogTools().isBopisOnlyProduct(prodId);
           if(bopisOnly){
             bopisItemCount++;
           }
        }
      }
      
    //if bopis item count is equal to count of commerce items in order
      //set the bopisItemsOnly flag
      if(bopisItemCount == getOrder().getCommerceItemCount()){
        bopisItemsOnly=true;
      }else if(bopisItemCount > 0){
        isItemRemovalRequired=true;
      }
      
      vlogDebug("InitCartPageDroplet: Order:{0}, bopisItemsOnly:{1}, isITemRemovalRequired:{2}",getOrder().getId(),bopisItemsOnly,isItemRemovalRequired);
      
    }
    pRequest.setParameter(BOPIS_ITEMS_ONLY, bopisItemsOnly);
    pRequest.setParameter(IS_ITEM_REMOVAL_REQUIRED, isItemRemovalRequired);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
  
  public Order getOrder() {
    return getShoppingCart().getCurrent();
  }

  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
}
