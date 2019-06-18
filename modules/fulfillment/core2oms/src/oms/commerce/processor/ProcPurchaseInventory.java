package oms.commerce.processor;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.mff.commerce.inventory.FFInventoryManager;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Make a purchase call to inventory manager to check availability and block the
 * inventory
 * 
 * @author Knowledgepath Solutions Inc
 */
public class ProcPurchaseInventory extends GenericService implements PipelineProcessor {

	private static final int SUCCESS = 1;
	private final int[] mRetCodes = { SUCCESS };
	FFInventoryManager inventoryManager;

	private boolean inventoryEnabled = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
	 */
	public int[] getRetCodes() {
		return mRetCodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object,
	 * atg.service.pipeline.PipelineResult)
	 */
	@SuppressWarnings("rawtypes")
	public int runProcess(Object pArg0, PipelineResult pResult) throws Exception {
		
		vlogDebug("Entering runProcess");
		if (isInventoryEnabled()) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>) pArg0;
			Order order = (Order) map.get(PipelineConstants.ORDER);
	
			if (order == null) {
				throw new InvalidParameterException("Invalid null pipeline parameter: " + PipelineConstants.ORDER);
			}
	
			List ciList = order.getCommerceItems();
			ListIterator ciIterator = ciList.listIterator();
			while (ciIterator.hasNext()) {
				CommerceItem ci = (CommerceItem) ciIterator.next();
				String skuId = ci.getCatalogRefId();
				long qty = ci.getQuantity();
				int retValue = getInventoryManager().purchase(skuId, qty);
	
				if (retValue < 1) {
					if (isLoggingWarning()) {
						logWarning("ProcPurchaseInventory: Insufficient inventory for Sku " + skuId);
					}
					//pResult.addError("outOfStock", "Insufficient inventory for Sku " + skuId);
					//return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
				}else{
  				if (isLoggingDebug()) {
  					logDebug("Purchased inventory for item " + skuId);
  				}
				}
			}
		}
		vlogDebug("Exiting runProcess");
		
		return SUCCESS;
	}

	public FFInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(FFInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/**
	 * 
	 * @return the inventoryEnabled flag
	 */
	public boolean isInventoryEnabled() {
		return inventoryEnabled;
	}

	/**
	 * Set the inventoryEnabled. When set to false this processor will suppress
	 * the calls to the inventory manager and checks to make sure the inventory
	 * is available.
	 * 
	 * @param inventoryEnabled the inventory flag.
	 */
	public void setInventoryEnabled(boolean inventoryEnabled) {
		this.inventoryEnabled = inventoryEnabled;
	}
}
