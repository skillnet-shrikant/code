package mff.returns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.prorate.MFFProrateItemManager;
import com.mff.constants.MFFConstants;
import com.mff.returns.ReturnInput;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import mff.MFFEnvironment;
import oms.commerce.order.OMSOrderManager;

/**
 * 
 * @author vsingh
 *
 */
public class MFFReturnProcessFormHandler extends PurchaseProcessFormHandler {

  private String successURL;
  private String errorURL;
  private String orderNumber;
  private ReturnInput[] returnInput;
  private int commerceItemCount;
  private String storeId;
  private OMSOrderManager mOrderManager;
  private MFFRestReturnManager returnManager;
  private MFFProrateItemManager prorateItemManager;
  private ReturnResponse returnResponse = new ReturnResponse();
  private ReturnResponsePOS returnResponsePOS = new ReturnResponsePOS();
  private MFFEnvironment mEnvironment;

  public boolean handleProcessReturns(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "MFFProcessReturnFormHandler.handleProcessReturns";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        if(Strings.isNullOrEmpty(getOrderNumber())){
          addFormException(new DropletException("OrderNumber is null so cannot continue processing."));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
        
        Order lOmsOrder = null;
        String orderNumber = getOrderNumber();
        try {
          String orderId = getOmsOrderManager().getOrderIdByOrderNumber(getOrderNumber());
          lOmsOrder = getOmsOrderManager().loadOrder(orderId);
        } catch (CommerceException e) {
          vlogError (e, "Unable to load order Number: {0}", orderNumber);
          addFormException(new DropletException("Unable to load order for orderNumber - " + orderNumber));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
         
        MFFOrderImpl order = (MFFOrderImpl) lOmsOrder;
       
        //Validate ReturnInput
        validateInput(order);
        if(getFormError()){
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse); 
        }
        
        vlogDebug("handleProcessReturns - Current Order Number : {0}", orderNumber);
        
        synchronized(order) {
          String storeId = getStoreId();
          if(Strings.isNullOrEmpty(getStoreId())){
            vlogInfo("Using default return storeId for orderNumber {0}",orderNumber);
            storeId = getEnvironment().getDefaultReturnStoreID();
          }
          
          if(getReturnInput()!=null){
        	  vlogInfo("Return input is "+getReturnInput());
          }
          boolean result = getReturnManager().processReturnByReturnInput(order,storeId,getReturnInput(), "pps");
          if(!result){
            addFormException(new DropletException("Error processing return for Order -" + orderNumber + ". Please try again later."));
          }else{
            getReturnResponse().setOrder(order);
          }
          
        } // synchronized
        
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSuccessURL(), getErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
          if (rrm != null)
              rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  
  }
  
  public boolean handleProcessReturnsPOS(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "MFFProcessReturnFormHandler.handleProcessReturnsPOS";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        if(Strings.isNullOrEmpty(getOrderNumber())){
          addFormException(new DropletException("OrderNumber is null so cannot continue processing."));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
        
        Order lOmsOrder = null;
        String orderNumber = getOrderNumber();
        try {
          String orderId = getOmsOrderManager().getOrderIdByOrderNumber(getOrderNumber());
          lOmsOrder = getOmsOrderManager().loadOrder(orderId);
        } catch (CommerceException e) {
          vlogError (e, "Unable to load order Number: {0}", orderNumber);
          addFormException(new DropletException("Unable to load order for orderNumber - " + orderNumber));
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse);
        }
         
        MFFOrderImpl order = (MFFOrderImpl) lOmsOrder;
        //Validate ReturnInput
        validateInputPOS(order);
        if(getFormError()){
          return checkFormRedirect (null, getErrorURL(), pRequest, pResponse); 
        }
        
        vlogDebug("handleProcessReturnsPOS - Current Order Number : {0}", orderNumber);
        
        synchronized(order) {
          
          boolean result = getReturnManager().processReturnByReturnInput(order, getStoreId(),getReturnInput(), "pos");
          if(!result){
            addFormException(new DropletException("Error processing return for Order -" + orderNumber + ". Please try again later."));
          }else{
            ReturnResponsePOS returnResponse = getReturnResponsePOS();
            returnResponse.setOrderNumber(orderNumber);
            for(ReturnInput retInput : getReturnInput()){
              ReturnResponseItemPOS returnItem = new ReturnResponseItemPOS();
              returnItem.setCommerceItemId(retInput.getCommerceItemId());
              returnItem.setReturnSuccess(true);
              returnItem.setLinesReturned(retInput.getLinesReturnedList());
              returnResponse.getReturnItems().add(returnItem);
            }
          }
        } // synchronized
        
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSuccessURL(), getErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
          if (rrm != null)
              rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  
  }

  private void validateInputPOS(MFFOrderImpl pOrder){
  
  ReturnInput[] returnInputArr = getReturnInput();
  int index = 0;
 
  for(ReturnInput returnInp : returnInputArr){     
    
    validateReturnInput(pOrder,returnInp,index);
    if(getFormError()){
      return;
    }
    
    //set returnReasonId in returnInput rather than description
    HashMap<String,String> returnReasonMap = getReturnManager().getPosReasonCodeMap();
    if(returnReasonMap != null && returnReasonMap.size() > 0){
      if(returnReasonMap.get(returnInp.getReturnReason()) != null){
        returnInp.setReturnReason(returnReasonMap.get(returnInp.getReturnReason()));
      }else{
        returnInp.setReturnReason(getReturnManager().getDefaultReturnReason());
      }
    }
    
    vlogDebug("validateInputPOS : returnInp.getLinesReturned() - {0}",returnInp.getLinesReturned());
    
    if(Strings.isNullOrEmpty(returnInp.getLinesReturned())){
      addFormException(new DropletException("LinesReturned cannot be null for return index " + index));
      return;
    }else{
      String tokens[] = StringUtils.splitStringAtCharacter(returnInp.getLinesReturned(), '|');
      returnInp.setLinesReturnedList(Arrays.asList(tokens));
    }
    
    // check lines passed are in initial state
    if(!returnInp.getLinesReturnedList().isEmpty()){
      Set<String> pRatedItems = new HashSet<String>();
      for (String lineNumber : returnInp.getLinesReturnedList()) {
        RepositoryItem[] items;
        try {
          items = getProrateItemManager().getProrateItemByCIAndLineNumber(returnInp.getCommerceItemId(), Double.valueOf(lineNumber));
          double totalAmountReturned = 0.0d;
          if (items != null && items.length > 0) {
            String prorateItemState = (String)items[0].getPropertyValue(MFFConstants.PROPERTY_PRORATE_STATE);
            if(!prorateItemState.equalsIgnoreCase(MFFConstants.PRORATE_STATE_RETURN)){
              if(returnInp.isReturnShipping()){
                totalAmountReturned += (Double)items[0].getPropertyValue(MFFConstants.PROPERTY_PRORATE_TOTAL);
              }else{
                totalAmountReturned += (Double)items[0].getPropertyValue(MFFConstants.PROPERTY_PRORATE_TOTAL_WITHOUT_SHIPPING);
              }
              pRatedItems.add(items[0].getRepositoryId());
            }else{
              addFormException(new DropletException("LinesNumber " + lineNumber + " is already in return state for commerceItem " + returnInp.getCommerceItemId()));
              return;
            }
          }
          
          // validate amountReturn
          if(!Strings.isNullOrEmpty(returnInp.getAmountReturned())){
            double amountReturned = Double.valueOf(returnInp.getAmountReturned());
            if(amountReturned != totalAmountReturned){
              vlogInfo("amountReturned - {0} does not match calculated refund {1} based on input",amountReturned,totalAmountReturned );
            }
          }else{
            vlogInfo("getAmountReturned() is missing for commerceItemId - {0}",returnInp.getCommerceItemId());
          }
          
        } catch (NumberFormatException e) {
          logError(e);
        } catch (CommerceException e) {
          logError(e);
        }
          
      }
      //set pro ratedItemIds
      returnInp.setProRatedItemIds(pRatedItems);  
    }
    
    index++;
  }
}

  private void validateInput(MFFOrderImpl pOrder){
    
    ReturnInput[] returnInputArr = getReturnInput();
    int index = 0;
   
    for(ReturnInput returnInp : returnInputArr){     
     
      validateReturnInput(pOrder,returnInp,index);
      if(getFormError()){
        return;
      }
      
     // set setLinesReturnedList by picking prorate lines in initial state
     // if no prorate items exist in initial state throw an error
      try {
        RepositoryItem[] items = getProrateItemManager().getProrateItemByCIAndStatus(
            returnInp.getCommerceItemId(), MFFConstants.PRORATE_STATE_INITIAL);
        
        if(items != null){
          List<String> linesReturnedList = new ArrayList<String>();
          Set<String> pRatedItems = new HashSet<String>();
          double qtyRet = Double.valueOf(returnInp.getQuantityReturned());
          for(RepositoryItem prorateItem : items){
            Double lineNumber = (Double)prorateItem.getPropertyValue(MFFConstants.PROPERTY_PRORATE_LINE_NUMBER);
            linesReturnedList.add(String.valueOf(lineNumber));
            pRatedItems.add(prorateItem.getRepositoryId());
            if(linesReturnedList.size() == qtyRet){
              break;
            }
          }
          if(linesReturnedList.size() > 0){
            returnInp.setLinesReturnedList(linesReturnedList);
            returnInp.setProRatedItemIds(pRatedItems);
          }else{
            addFormException(new DropletException("Cannot find ProrateItems in initial state for commerceItemId " + returnInp.getCommerceItemId()));
            return;
          }
        }
      } catch (CommerceException e) {
        vlogError(e,"Error while validating input");
      }
     
      index++;
    }
  }
  
  private void validateReturnInput(MFFOrderImpl pOrder,ReturnInput pReturnInput,int pIndex){
    
    vlogDebug("validateInput : returnInput {0}",pReturnInput.toString());
    
    if(Strings.isNullOrEmpty(pReturnInput.getCommerceItemId())){
      addFormException(new DropletException("CommerceItemId cannot be null for return index " + pIndex));
      return;
    }
    
    CommerceItem lCommerceItem = null;
    try {
      lCommerceItem = pOrder.getCommerceItem(pReturnInput.getCommerceItemId());
      if(lCommerceItem != null && !getReturnManager().getReturnTools().isItemReturnable(lCommerceItem)){
        addFormException(new DropletException("CommerceItemId - " +pReturnInput.getCommerceItemId() + " is not returnable"));
        return;
      }
      
    } catch (CommerceItemNotFoundException e) {
      logError(e);
    } catch (InvalidParameterException e) {
      logError(e);
    }
    
    if(Strings.isNullOrEmpty(pReturnInput.getQuantityReturned())){
      addFormException(new DropletException("QuantityReturned cannot be null for return index " + pIndex));
      return;
    }
    
    double qtyRet = Double.valueOf(pReturnInput.getQuantityReturned());
    // Check for returned quantity greater than available quantity
    if(lCommerceItem != null &&  qtyRet > lCommerceItem.getQuantity()){
      addFormException(new DropletException("QuantityReturned is greater than quantity available for return for commerceItem " + 
                                              pReturnInput.getCommerceItemId()));
      return;
    }
    
    if(Strings.isNullOrEmpty(pReturnInput.getReturnReason())){
      vlogWarning("ReturnReason is null for return. Default ReasonCode will be used");
    }
  }

  public String getSuccessURL() {
    return successURL;
  }

  public void setSuccessURL(String successURL) {
    this.successURL = successURL;
  }

  public String getErrorURL() {
    return errorURL;
  }

  public void setErrorURL(String errorURL) {
    this.errorURL = errorURL;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public ReturnInput[] getReturnInput() {
    return returnInput;
  }

  public void setReturnInput(ReturnInput[] returnInput) {
    this.returnInput = returnInput;
  }

  public int getCommerceItemCount() {
    return commerceItemCount;
  }

  public void setCommerceItemCount(int pCommerceItemCount) {
    if (pCommerceItemCount <= 0) {
      commerceItemCount = 0;
      returnInput = null;
    } else {
      commerceItemCount = pCommerceItemCount;
      returnInput = new ReturnInput[commerceItemCount];
      Throwable caught = null;
      try {
        for (int index = 0; index < commerceItemCount; index++) {
          returnInput[index] = new ReturnInput();
        }
      } catch (Throwable thrown) {
        caught = thrown;
      }
      if (caught != null) {
        if (isLoggingError()) {
          logError(caught);
        }

        // Throw away partially built array.
        returnInput = null;
      }
    }

  }
  
  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String pStoreId) {
    storeId = pStoreId;
  }

  public void setOmsOrderManager(OMSOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public OMSOrderManager getOmsOrderManager() {
    return mOrderManager;
  }

  public MFFRestReturnManager getReturnManager() {
    return returnManager;
  }

  public void setReturnManager(MFFRestReturnManager returnManager) {
    this.returnManager = returnManager;
  }

  public ReturnResponse getReturnResponse() {
    return returnResponse;
  }

  public void setReturnResponse(ReturnResponse returnResponse) {
    this.returnResponse = returnResponse;
  }
  
  public ReturnResponsePOS getReturnResponsePOS() {
    return returnResponsePOS;
  }

  public void setReturnResponsePOS(ReturnResponsePOS returnResponsePOS) {
    this.returnResponsePOS = returnResponsePOS;
  }

  public MFFProrateItemManager getProrateItemManager() {
    return prorateItemManager;
  }

  public void setProrateItemManager(MFFProrateItemManager prorateItemManager) {
    this.prorateItemManager = prorateItemManager;
  }

  public MFFEnvironment getEnvironment() {
    return mEnvironment;
  }

  public void setEnvironment(MFFEnvironment pEnvironment) {
    mEnvironment = pEnvironment;
  }
  
}
