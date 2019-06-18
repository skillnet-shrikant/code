/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.processor;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.constants.MFFConstants;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.core.util.StringUtils;
import atg.service.pipeline.PipelineResult;
import oms.Configuration;
import oms.commerce.order.OMSOrderConstants;
import oms.commerce.order.OMSOrderManager;
import oms.commerce.states.OMSOrderStates;

/**
 * This processor sets a pipeline parameter - newOrderState - which is the new
 * state to which the order state is to be updated to.
 * 
 * <p> Normally all orders go into IN_REMORSE state. But if the order contains
 * items with Overnight shipping, they are not put in remorse period. They
 * directly go to PRE_SENT_WMS state
 * 
 * @author KnowledgePath Solutions Inc.
 */
public class ProcDecideOrderState extends EXTNPipelineProcessor{
	// ------------------------------------------
	// CONFIGURED PROPERTIES
	// ------------------------------------------
	List<String> exceptionShipMethods;
	OMSOrderManager omsOrderManager;
	
	Configuration mConfiguration;
	
	// ------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Override
	public int runProcess(Object pParam, PipelineResult arg1) throws Exception {
		logDebug("ProcDecideOrderState.runProcess - begin");
		
		Map pipelineParams = (Map) pParam;
		OrderImpl omsOrder = (OrderImpl) pipelineParams.get(OMSOrderConstants.PIPELINE_OMS_ORDER);
		
		String orderSGId = getOmsOrderManager().getEXTNHGShippingGroupId(omsOrder);
		
		if (StringUtils.isBlank(orderSGId)) {
			pipelineParams.put(OMSOrderConstants.PIPELINE_NEW_ORDER_STATE, OMSOrderStates.PENDING_ALLOCATION);	
		} else {
			HardgoodShippingGroup ordersg = null;
			try {
				ordersg = (HardgoodShippingGroup) omsOrder.getShippingGroup(orderSGId);
			} catch (ShippingGroupNotFoundException e) {
				vlogError("Error looking up SG from order {0} : {1}", omsOrder.getId(), e.getMessage());
			} catch (InvalidParameterException e) {
				vlogError("Error looking up SG from order {0} : {1}", omsOrder.getId(), e.getMessage());
			}
			String orderState = null;
			boolean isBopisOrder = ((MFFOrderImpl)omsOrder).isBopisOrder();
			String fraudStatus = ((MFFOrderImpl)omsOrder).getFraudStat(); 
			if(!Strings.isNullOrEmpty(fraudStatus) && (fraudStatus.equalsIgnoreCase(MFFConstants.FRAUD_REJECT))){
			  orderState = OMSOrderStates.FRAUD_REJECT;
			}else if(!Strings.isNullOrEmpty(fraudStatus) && (fraudStatus.equalsIgnoreCase(MFFConstants.FRAUD_REVIEW))){
			  orderState = OMSOrderStates.FRAUD_REVIEW;
			}else if(isBopisOrder){
			  orderState = OMSOrderStates.PENDING_ALLOCATION;
			}
			/*else if (getConfiguration().isRemorsePeriodEnabled()) {
				orderState = OMSOrderStates.IN_REMORSE;
			}*/ 
			else {
				orderState = OMSOrderStates.PENDING_ALLOCATION;
			}
			
			vlogDebug("order state set to {0}", orderState);
			
			if (ordersg == null) {
				vlogError("Error looking up SG from order {0} : Null SG",
						omsOrder.getId());
			} else {
				String orderShipMethod = ordersg.getShippingMethod();
				vlogDebug("The Shipping method in order {0} is {1}",
						omsOrder.getId(), orderShipMethod);
	
				if (getExceptionShipMethods().contains(orderShipMethod)) {
					vlogDebug(
							"The order {0} has an exceptionShipMethod. It will not go IN_REMORSE",
							omsOrder.getId());
					orderState = OMSOrderStates.PENDING_ALLOCATION;
				}
			}
			pipelineParams.put(OMSOrderConstants.PIPELINE_NEW_ORDER_STATE, orderState);	
		}
		
		

		logDebug("ProcDecideOrderState.runProcess - end");
		return CONTINUE;
	}
	
	// ------------------------------------------
	// GETTERS AND SETTERS
	// ------------------------------------------

	public OMSOrderManager getOmsOrderManager() {
		return omsOrderManager;
	}

	public List<String> getExceptionShipMethods() {
		return exceptionShipMethods;
	}

	public void setExceptionShipMethods(List<String> exceptionShipMethods) {
		this.exceptionShipMethods = exceptionShipMethods;
	}

	public void setOmsOrderManager(OMSOrderManager omsOrderManager) {
		this.omsOrderManager = omsOrderManager;
	}

	public Configuration getConfiguration() {
		return mConfiguration;
	}

	public void setConfiguration(Configuration configuration) {
		this.mConfiguration = configuration;
	}	
}
