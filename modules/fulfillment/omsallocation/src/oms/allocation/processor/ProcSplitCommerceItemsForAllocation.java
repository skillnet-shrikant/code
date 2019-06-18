/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.processor;

import java.util.List;
import java.util.Map;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.ItemAllocation;
import oms.commerce.order.MFFOMSOrderManager;

public class ProcSplitCommerceItemsForAllocation extends ApplicationLoggingImpl implements PipelineProcessor {

  /**
   * Default Constructor
   */
  public ProcSplitCommerceItemsForAllocation() {
    super();
  }

  private final int SUCCESS = 1;
  /*
   * ATG OOTB fulfillment ResourceBundle
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSplitCommerceItems";
  private MFFOMSOrderManager omsOrderManager;

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
  public MFFOMSOrderManager getOmsOrderManager() {
    return omsOrderManager;
  }

  /**
   * @param omsOrderManager
   *          the omsOrderManager to set
   */
  public void setOmsOrderManager(MFFOMSOrderManager omsOrderManager) {
    this.omsOrderManager = omsOrderManager;
  }

  /*
   * (non-Javadoc)
   * 
   * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
   */
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object,
   * atg.service.pipeline.PipelineResult)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception {

    if (isLoggingDebug()) {
      logDebug("Inside ProcSplitCommerceItemsForAllocation");
    }

    Map lParams = (Map) pParam;
    Order pOMSOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    List<ItemAllocation> lItemAllocations = (List<ItemAllocation>) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ALLOCATIONS);

    if (pOMSOrder == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
    }

    getOmsOrderManager().splitCommerceItemsForAllocation((OrderImpl) pOMSOrder, lItemAllocations,null);

    return SUCCESS;
  }
  
}