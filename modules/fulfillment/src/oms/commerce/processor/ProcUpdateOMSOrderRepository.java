/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.mff.commerce.order.MFFOrderImpl;

import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.ShippingGroup;
import atg.commerce.states.StateDefinitions;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.pipeline.PipelineResult;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.states.OMSOrderStates;

public class ProcUpdateOMSOrderRepository extends EXTNPipelineProcessor {

  /**
   * Default Constructor
   */
  public ProcUpdateOMSOrderRepository() {
    super();
  }

  private static final String perfMonOperationName = "ProcUpdateOMSOrderRepository";
  /*
   * ATG OOTB fulfillment ResourceBundle
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcUpdateOMSOrderRepository";
  private OMSOrderManager omsOrderManager;

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  /**
   * @return the omsOrderManager
   */
  public OMSOrderManager getOmsOrderManager() {
    return omsOrderManager;
  }

  /**
   * @param omsOrderManager
   *          the omsOrderManager to set
   */
  public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
    this.omsOrderManager = omsOrderManager;
  }

  /*
   * (non-Javadoc)
   * 
   * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object,
   * atg.service.pipeline.PipelineResult)
   */
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
    String parameterName = "runProcess.ProcUpdateOMSOrderRepository.runProcess";
    if (isLoggingDebug()) {
      logDebug("inside ProcUpdateOMSOrderRepository");
    }
    PerformanceMonitor.startOperation(perfMonOperationName, parameterName);
    HashMap map = (HashMap) pParam;
    MFFOrderImpl pOMSOrder = (MFFOrderImpl) map.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
    String process = (String) map.get(OMSOrderConstants.PIPELINE_OMS_PROCESS);
    if (pOMSOrder == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
    }
    // before saving order to oms, update states of back ordered items, mark tax
    // amounts
    // and shipping priceInfo's to final and update order state
    if (process.equals(OMSOrderConstants.PROCESS_SAVE_OMS)) {
      List items = pOMSOrder.getCommerceItems();
      Iterator it = items.iterator();
      while (it.hasNext()) {
        CommerceItemImpl item = (CommerceItemImpl) it.next();
        item.getPriceInfo().setAmountIsFinal(true);
      }
      // mark shipping price info's
      Iterator iter = pOMSOrder.getShippingGroups().iterator();
      while (iter.hasNext()) {
        ShippingGroup shipGroup = (ShippingGroup) iter.next();
        shipGroup.getPriceInfo().setAmountIsFinal(true);
      }
      // mark order's tax price info
      pOMSOrder.getTaxPriceInfo().setAmountIsFinal(true);
      pOMSOrder.getPriceInfo().setAmountIsFinal(false);
      String newOrderState = (String) map.get(OMSOrderConstants.PIPELINE_NEW_ORDER_STATE);
      if (StringUtils.isEmpty(newOrderState)) {
        newOrderState = OMSOrderStates.IN_REMORSE;
      }
      if (!pOMSOrder.getStateAsString().equalsIgnoreCase(OMSOrderStates.SYSTEM_HOLD)) {
        pOMSOrder.setState(StateDefinitions.ORDERSTATES.getStateValue(newOrderState));
      }
    }
    
    
    vlogInfo("Processing the order id {0}, order number {1}", pOMSOrder.getId(), pOMSOrder.getOrderNumber());
    
    // update order
    getOmsOrderManager().updateOrder(pOMSOrder);
    PerformanceMonitor.endOperation(perfMonOperationName, parameterName);
    return CONTINUE;
  }
}