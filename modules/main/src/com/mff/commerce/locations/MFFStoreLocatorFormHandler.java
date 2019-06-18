/**
 * 
 */
package com.mff.commerce.locations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.constants.MFFConstants;
import com.mff.zip.MFFZipcodeHelper;

import atg.commerce.inventory.InventoryException;
import atg.commerce.locations.GeoLocatorFormHandler;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.droplet.MFFFormExceptionGenerator;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * @author DMI
 *
 */
public class MFFStoreLocatorFormHandler extends GeoLocatorFormHandler {

  private OrderHolder mShoppingCart;
  private FFRepositoryInventoryManager mInventoryManager;
  private MFFCatalogTools mCatalogTools;
  private String mCatalogRefId;
  private String mQuantity;
  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private List<BopisStore> mResults;
  private MFFZipcodeHelper mZipCodeHelper;
  private boolean mFromProduct;
  private boolean mTestZipCode;
  private double mLat;
  private double mLongi;
  private String mProductId;
  private boolean editMode;
  private String removalCommerceIds;
  
  public String getRemovalCommerceIds() {
	return removalCommerceIds;
}

public void setRemovalCommerceIds(String pRemovalCommerceIds) {
	removalCommerceIds = pRemovalCommerceIds;
}

public boolean isEditMode() {
	  return editMode;
  }

  public void setEditMode(boolean pEditMode) {
	  editMode = pEditMode;
  }

/**
   * 
   */
  @Override
  public boolean handleLocateItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
   
    boolean status=false;
    
    if(getPostalCode().isEmpty()){
      getFormExceptionGenerator().generateException(MFFConstants.MSG_STORE_POSTAL_CODE_MISSING, true, this, pRequest);
      vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No Postal Code found to search for the nearest stores, unable to proceed");
      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
    }
    
    if(isFromProduct()){
      if(getCatalogRefId() == null || getCatalogRefId().isEmpty()){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_MISSING, true, this, pRequest);
        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No Skus found to Add, unable to proceed");
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
      
      if(getProductId() == null || getProductId().isEmpty()){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_PRODUCT_MISSING, true, this, pRequest);
        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No Products found to Add, unable to proceed");
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
      
      //if FFL product then do not allow for store pickup
      boolean isFFL = getCatalogTools().isFFLProduct(getProductId());
      if (isFFL) {
        vlogDebug("Product:{0} is FFL and can not be picked up in store", getProductId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_FFL_BOPIS_ERROR, true, this, pRequest);
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
      
    //if GiftCard product then do not allow for store pickup
      boolean isGiftCard = getCatalogTools().isGCProduct(getProductId());
      if (isGiftCard) {
        vlogDebug("Product:{0} is GiftCard and can not be picked up in store", getProductId());
        getFormExceptionGenerator().generateException(MFFConstants.MSG_GC_BOPIS_ERROR, true, this, pRequest);
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
      
      if(getQuantity() == null || getQuantity().isEmpty()){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_QTY_MISSING, true, this, pRequest);
        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: Invalid Quantity to add, unable to proceed");
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
      
    }else{
      if(getShoppingCart().getCurrent() == null && getShoppingCart().getCurrent().getCommerceItemCount() <= 0){
        getFormExceptionGenerator().generateException(MFFConstants.MSG_CART_NO_ITEMS, true, this, pRequest);
        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No Items found in the cart, unable to proceed");
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
    }
    
    RepositoryItem lZipCodeRepItem = getZipCodeHelper().retrieveZipcodeItembyZipcode(getPostalCode().trim());
    
    if(lZipCodeRepItem == null){
      if(isTestZipCode()){
        setLatitude(getLat());
        setLongitude(getLongi());
      }else{
        getFormExceptionGenerator().generateException(MFFConstants.MSG_INVALID_ZIP_CODE, true, this, pRequest);
        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: Unable to find lattitude and longitude of the postal code");
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }
    }else{    
        //set latitude and longtitude
        setLatitude((double)lZipCodeRepItem.getPropertyValue("latitude"));
        setLongitude((double)lZipCodeRepItem.getPropertyValue("longitude"));
    }
    vlogDebug("MFFStoreLocatorFormHandler:handleLocateItems: Latitude:{0} and Longitude:{1} found for Postal code:{2}",getLatitude(), getLongitude(), getPostalCode());
    
    //call super method and get the result set
    super.handleLocateItems(pRequest, pResponse);
    
    
    /*vlogError("MFFStoreLocatorFormHandler:handleLocateItems: There is an issue while finding nearest stores for postal code: {0}", getPostalCode());
    return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);*/
    
    //if no nearest stores found return false 
    if(getLocationResults() == null || getLocationResults().size() <= 0){
      getFormExceptionGenerator().generateException(MFFConstants.MSG_NO_STORES_FOUND, true, this, pRequest);
      vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No Nearest stores found for postal code: {0}", getPostalCode());
      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
    }
    
    //get the nearest stores and get only bopis stores
    List<String> storeIds = new ArrayList<String>();
    vlogDebug("No of Nearest stores: {0}",getLocationResults().size());
    for(Object locItem : getLocationResults()){
      RepositoryItem locRepItem = (RepositoryItem) locItem;
      boolean isBopisStore = (boolean)locRepItem.getPropertyValue("isPickup");
      boolean isBopisOnlyStore = (boolean)locRepItem.getPropertyValue("isBOPISOnly");
      //add only if the store is ready for pick up
      if(isBopisStore || isBopisOnlyStore)
        storeIds.add(locRepItem.getRepositoryId());
      else
        vlogDebug("Nearest Store:{0} and isPickUp flag:{1}",locRepItem.getRepositoryId(),isBopisStore);
    }
    
    //get the current item/sku customer is trying to add with quantity
    Map<String,Long> lSkusWithQty = new HashMap<String,Long>();
     
    vlogDebug("MFFStoreLocatorFormHandler:handleLocateItems: FromProduct:{0}", isFromProduct());
    
    if(isFromProduct()){
      long quantity = Long.valueOf(getQuantity());
      vlogDebug("MFFStoreLocatorFormHandler:handleLocateItems:From Product: Adding skuId:{0}, quantity:{1} to Map",getCatalogRefId(), getQuantity());
      lSkusWithQty.put(getCatalogRefId(), quantity);
    }

    //check if user is trying to add the same sku that is already in the cart, if yes we might need to increase the quantity 
    //get the items in the cart with quantity
    List lItems = getShoppingCart().getCurrent().getCommerceItems();
    for(Object item : lItems){
       CommerceItem ci = (CommerceItem) item;
       //ci.getAuxiliaryData().getProductId()
       boolean isGiftCard = getCatalogTools().isGCProduct(ci.getAuxiliaryData().getProductId());
       
       if(!isGiftCard) {
	       if(lSkusWithQty.get(ci.getCatalogRefId()) == null){
	         lSkusWithQty.put(ci.getCatalogRefId(), ci.getQuantity());
	         vlogDebug("MFFStoreLocatorFormHandler:handleLocateItems:From Cart: Adding skuId:{0}, quantity:{1} to Map",ci.getCatalogRefId(), ci.getQuantity());
	       }else{
	         //get req qty from pdp, which is already in the map
	         long reqQty = lSkusWithQty.get(ci.getCatalogRefId());
	         //get cart quantity
	         long existingQty = ci.getQuantity();
	         if(isEditMode()) {
	        	 lSkusWithQty.put(ci.getCatalogRefId(), reqQty);
	         } else {
	        	 lSkusWithQty.put(ci.getCatalogRefId(), reqQty + existingQty);
	         }
	         vlogDebug("MFFStoreLocatorFormHandler:handleLocateItems:From Cart: Sku:{0} with reqqty:{1} and existingQty:{2}",ci.getCatalogRefId(),reqQty,existingQty);
	       }
       }
    }
    
    if(lSkusWithQty.size() > 0){
      //call inventory manager to filter the stores based on the inventory.
      try {
        Map<String, Boolean> bopisStores = getInventoryManager().getBopisInventory(lSkusWithQty, storeIds);
        List<BopisStore> bopisStoreResults = new ArrayList<BopisStore>();
        //print result set
        for(String storeId: bopisStores.keySet()){
          vlogDebug("StoreId: {0} BopisElgible: {1}", storeId, bopisStores.get(storeId));
        }
        
        for(Object locItem : getLocationResults()){
          RepositoryItem locRepItem = (RepositoryItem) locItem;
          BopisStore store = new BopisStore();
          store.setStore(locRepItem);
          if(bopisStores.get(locRepItem.getRepositoryId()) == null){
            vlogDebug("No Results Found for storeId:{0} defaulting it to bopis elgible false",locRepItem.getRepositoryId());
            store.setBopisElgible(false);
          }
          else{
            vlogDebug("storeId:{0}, bopisFlag:{1}",locRepItem.getRepositoryId(), bopisStores.get(locRepItem.getRepositoryId()));
            store.setBopisElgible(bopisStores.get(locRepItem.getRepositoryId()));
          }
          bopisStoreResults.add(store);
        }
        setResults(bopisStoreResults);
      } catch (InventoryException e) {
          vlogError("There is an Error while finding nearest stores with inventory");
      }
    }else{
      vlogError("MFFStoreLocatorFormHandler:handleLocateItems: No items found to check the inventory");
      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
    }
    
    return status;
  }
  
  public boolean handleFindStores (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	   
	  	vlogDebug("handleFindStores: Called with: " + getPostalCode());
	    boolean status=false;
	    
	    if(getPostalCode().isEmpty()){
	      getFormExceptionGenerator().generateException(MFFConstants.MSG_STORE_POSTAL_CODE_MISSING, true, this, pRequest);
	      vlogError("handleFindStores: No Postal Code found to search for the nearest stores, unable to proceed");
	      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
	    }
	    
	    RepositoryItem lZipCodeRepItem = getZipCodeHelper().retrieveZipcodeItembyZipcode(getPostalCode().trim());
	    
	    if(lZipCodeRepItem == null){
	      if(isTestZipCode()){
	        setLatitude(getLat());
	        setLongitude(getLongi());
	      }else{
	        getFormExceptionGenerator().generateException(MFFConstants.MSG_INVALID_ZIP_CODE, true, this, pRequest);
	        vlogError("MFFStoreLocatorFormHandler:handleLocateItems: Unable to find lattitude and longitude of the postal code");
	        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
	      }
	    }else{
	        //set latitude and longtitude
	        setLatitude((double)lZipCodeRepItem.getPropertyValue("latitude"));
	        setLongitude((double)lZipCodeRepItem.getPropertyValue("longitude"));
	    }
	    vlogDebug("handleFindStores: Latitude:{0} and Longitude:{1} found for Postal code:{2}",getLatitude(), getLongitude(), getPostalCode());
	    
	    //call super method and get the result set
	    super.handleLocateItems(pRequest, pResponse);
	   
	    //if no nearest stores found return false 
	    if(getLocationResults() == null || getLocationResults().size() <= 0){
	      getFormExceptionGenerator().generateException(MFFConstants.MSG_NO_STORES_FOUND, true, this, pRequest);
	      vlogError("handleFindStores: No Nearest stores found for postal code: {0}", getPostalCode());
	      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
		}

		vlogDebug("No of Nearest stores: {0}", getLocationResults().size());
        
        List<BopisStore> bopisStoreResults = new ArrayList<BopisStore>();
        
        for(Object locItem : getLocationResults()){
          RepositoryItem locRepItem = (RepositoryItem) locItem;
          boolean isBopisStore = (boolean)locRepItem.getPropertyValue("isPickup");
          boolean isBopisOnlyStore = (boolean)locRepItem.getPropertyValue("isBOPISOnly");
	      //add only if the store is ready for pick up
	      if(isBopisStore || isBopisOnlyStore){
	    	  BopisStore store = new BopisStore();
	          store.setStore(locRepItem);
	          bopisStoreResults.add(store);
	      }
        }
        setResults(bopisStoreResults);
     
	    
	    return status;
	  }

  /**
   * Helper Object to hold the results
   * @author DMI
   *
   */
  public static class BopisStore {
    RepositoryItem store;
    
    public RepositoryItem getStore() {
      return store;
    }
    public void setStore(RepositoryItem pStore) {
      store = pStore;
    }
    public boolean isBopisElgible() {
      return bopisElgible;
    }
    public void setBopisElgible(boolean pBopisElgible) {
      bopisElgible = pBopisElgible;
    }
    boolean bopisElgible;
  }
  
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  public FFRepositoryInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  public String getCatalogRefId() {
    return mCatalogRefId;
  }

  public void setCatalogRefId(String pCatalogRefId) {
    mCatalogRefId = pCatalogRefId;
  }

  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

  public MFFZipcodeHelper getZipCodeHelper() {
    return mZipCodeHelper;
  }

  public void setZipCodeHelper(MFFZipcodeHelper pZipCodeHelper) {
    mZipCodeHelper = pZipCodeHelper;
  }

  public boolean isFromProduct() {
    return mFromProduct;
  }

  public void setFromProduct(boolean pFromProduct) {
    mFromProduct = pFromProduct;
  }

  public boolean isTestZipCode() {
    return mTestZipCode;
  }

  public void setTestZipCode(boolean pTestZipCode) {
    mTestZipCode = pTestZipCode;
  }

  public double getLat() {
    return mLat;
  }

  public void setLat(double pLat) {
    mLat = pLat;
  }

  public double getLongi() {
    return mLongi;
  }

  public void setLongi(double pLongi) {
    mLongi = pLongi;
  }

  public String getProductId() {
    return mProductId;
  }

  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  public String getQuantity() {
    return mQuantity;
  }

  public void setQuantity(String pQuantity) {
    mQuantity = pQuantity;
  }

  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  public List<BopisStore> getResults() {
    return mResults;
  }

  public void setResults(List<BopisStore> pResults) {
    mResults = pResults;
  }
}
