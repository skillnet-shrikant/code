package mff.returns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.returns.MFFReturnManager;
import com.mff.returns.MFFReturnItem;
import com.mff.returns.ReturnInput;

import atg.commerce.csr.returns.ProcessName;
import atg.commerce.csr.returns.ReturnException;
import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ReturnShippingGroup;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;

public class MFFRestReturnManager extends MFFReturnManager {
  
  
  @SuppressWarnings("rawtypes")
  public boolean processReturnByReturnInput(MFFOrderImpl pOrder,String pStoreId, ReturnInput[] pReturnInputArr, String pOriginOfReturn) {
    
    vlogDebug("Entering processReturnByReturnInput - pOrderNumber {0}",pOrder.getId());

    // assumption all validation performed prior to calling this API
    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      HashMap<String, Object> params = new HashMap<String, Object>();

      //CommerceItem lCommerceItem = pOrder.getCommerceItem(pCommerceItemId);

      // CSREnvironmentTools envtools = getCSREnvironmentTools();
      // params.put("priceList", envtools.getCurrentPriceList());
      // params.put("salePriceList", envtools.getCurrentSalePriceList());
      if(!Strings.isNullOrEmpty(pOriginOfReturn)){
        params.put("originOfReturn", pOriginOfReturn);
      }else{
        params.put("originOfReturn", "default");
      }

      //getReturnTools().addDisableReturnTotalValidationParameter(params);
      ReturnRequest returnRequest = createReturnRequest(pOrder.getId(), null, params);
      
      getReturnTools().determineOriginatingOrder(returnRequest);
      String strProcessName = "Return";

      ProcessName returnProcessName = ProcessName.valueOf(strProcessName);
      Map selectExtraParameters = new HashMap();
      getReturnTools().addDisableReturnTotalValidationParameter(selectExtraParameters);

      setQuantityReturned(pOrder,pStoreId,returnRequest,pReturnInputArr);
      applySelectedItems(returnRequest, returnProcessName, selectExtraParameters);
      setQuantityReceived(pOrder,returnRequest,pReturnInputArr);
      updateStateOfReturnItems(returnRequest);
      applyRefundMethodAllocations(returnRequest, new HashMap());
      
      HashMap<String, Object> confReturnParam = new HashMap<String, Object>();
      confReturnParam.put("returnInputArray", pReturnInputArr);
      confirmReturn(returnRequest, confReturnParam);
      rollback = false;

    } catch (Throwable e) {

      vlogError(e, "processReturnByReturnInput : Transaction demarcation exception occurred while processing the returns for order number {0}", pOrder.getId());

    } finally {
      try {
        td.end(rollback);
      } catch (Exception e) {
        vlogError(e, "processReturnByReturnInput : Error commiting or rolling back the transaction for returns processing");
      }
    }
    vlogDebug("Exiting processReturnByReturnInput - pOrderNumber {0}",pOrder.getId());
    return !rollback;

  }
  
protected void setQuantityReturned(MFFOrderImpl pOrder,String pStoreId,ReturnRequest pReturnRequest, ReturnInput[] pReturnInputArr)
      throws ReturnException, CommerceItemNotFoundException, InvalidParameterException {
    
    vlogDebug("Entering setQuantityReturned - pReturnRequest");
    for(ReturnInput returnInp : pReturnInputArr){
      CommerceItem lCommerceItem = pOrder.getCommerceItem(returnInp.getCommerceItemId());
      ReturnItem item = getReturnItem(pReturnRequest, lCommerceItem);
  
      if (item != null) {
        item.setQuantityToReturn(Long.valueOf(returnInp.getQuantityReturned()));
        if (StringUtils.isBlank(returnInp.getReturnReason())) {
          item.setReturnReason(getDefaultReturnReason());
        } else {
          item.setReturnReason(returnInp.getReturnReason());
        }
        
        MFFReturnItem lItem = (MFFReturnItem) item;
        //lItem.setReturnShipping(returnInp.isReturnShipping());
        lItem.setStoreId(pStoreId);
        if(returnInp.getProRatedItemIds() != null && returnInp.getProRatedItemIds().size() > 0){
          lItem.setProRatedItemIds(returnInp.getProRatedItemIds());
        }
        
        if(!Strings.isNullOrEmpty(returnInp.getComments())){
         vlogInfo("Returned reason corresponding comments: "+returnInp.getComments());
          lItem.setComments(returnInp.getComments());
        }
  
      } else {
        throw new ReturnException("Could not determine the return item corresponding to the commerce item");
      }
      vlogDebug("Exiting setQuantityReturned - pReturnRequest, pCommerceItem, pChargeReturnShipping");
    }
  }
  
  protected void setQuantityReceived(MFFOrderImpl pOrder,ReturnRequest pReturnRequest, ReturnInput[] pReturnInputArr) 
      throws ReturnException, CommerceItemNotFoundException, InvalidParameterException {
    
    vlogDebug("Entering setQuantityReceived - pReturnRequest, pCommerceItem");
    for(ReturnInput returnInp : pReturnInputArr){
      CommerceItem lCommerceItem = pOrder.getCommerceItem(returnInp.getCommerceItemId());
      ReturnItem item = getReturnItem(pReturnRequest, lCommerceItem);
    
      if (item != null) {
        item.setQuantityReceived(Long.valueOf(returnInp.getQuantityReturned()));
        if (StringUtils.isBlank(returnInp.getReturnReason())) {
          item.setReturnReason(getDefaultReturnReason());
        } else {
          item.setReturnReason(returnInp.getReturnReason());
        }
        
        // set the disposition code
        if(getReturnReasonToDispCode().get(item.getReturnReason()) != null){
          item.setDisposition(getReturnReasonToDispCode().get(item.getReturnReason()));
        }
      } else {
        throw new ReturnException("Could not determine the return item corresponding to the commerce item");
      }
    }

    vlogDebug("Exiting setQuantityReceived - pReturnRequest");
  }
  
  @SuppressWarnings("rawtypes")
  protected ReturnItem getReturnItem(ReturnRequest pReturnRequest, CommerceItem pCommerceItem) {
    vlogDebug("Entering getReturnItem - pReturnRequest, pCommerceItem");
    ArrayList shippingList = pReturnRequest.getShippingGroupList();
    ReturnItem lMatchingReturnItem = null;
    Iterator shippingGroupsIterator = shippingList.iterator();
    while (shippingGroupsIterator.hasNext() && (lMatchingReturnItem == null)) {

      ReturnShippingGroup returnShippingGroup = (ReturnShippingGroup) shippingGroupsIterator.next();
      if (returnShippingGroup != null && returnShippingGroup.getItemList() != null && returnShippingGroup.getItemList().size() > 0) {

        List itemList = returnShippingGroup.getItemList();
        if (itemList != null && itemList.size() > 0) {
          for (int i = 0; i < itemList.size(); ++i) {
            ReturnItem lReturnItem = (ReturnItem) itemList.get(i);
            MFFCommerceItemImpl commerceItem = (MFFCommerceItemImpl) lReturnItem.getCommerceItem();
            if (pCommerceItem.getId().equalsIgnoreCase(commerceItem.getId())) {
              lMatchingReturnItem = lReturnItem;
              break;
            }
          }
        }
      }
    }

    vlogDebug("Exiting getReturnItem - pReturnRequest, pCommerceItem");
    return lMatchingReturnItem;
  }
  
  private String defaultReturnReason;
  public String getDefaultReturnReason() {
    return defaultReturnReason;
  }

  public void setDefaultReturnReason(String defaultReturnReason) {
    this.defaultReturnReason = defaultReturnReason;
  }
  
  private HashMap<String,String> posReasonCodeMap;
  public HashMap<String, String> getPosReasonCodeMap() {
    return posReasonCodeMap;
  }

  public void setPosReasonCodeMap(HashMap<String, String> posReasonCodeMap) {
    this.posReasonCodeMap = posReasonCodeMap;
  }
  
  

}
