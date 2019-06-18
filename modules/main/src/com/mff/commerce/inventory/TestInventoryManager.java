package com.mff.commerce.inventory;

import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.GenericService;

public class TestInventoryManager extends GenericService{
	
	private FFRepositoryInventoryManager inventoryManager;
	private String skuId;
	private long quantity;
	private String storeNumber;
	private String status;
	private List<String> storeList;
	private List<String> skuIds;
	private Map<String,Long> skuIdQuantityMap;
	
	
	
	public void testPurchase(){
		try {
			getInventoryManager().purchase(getSkuId(), getQuantity());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testSetStockLevel(){
		try {
			getInventoryManager().setStockLevel(getSkuId(), getQuantity());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testIncreaseStockLevel(){
		try {
			getInventoryManager().increaseStockLevel(getSkuId(), getQuantity());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void testDecreaseStockLevel(){
		try {
			getInventoryManager().decreaseStockLevel(getSkuId(), getQuantity());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void testQuerySkuAvailabilityStatus(){
		try {
			 getInventoryManager().querySkuAvailabilityStatus(getSkuId());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testQuerySoldCount(){
		try {
			getInventoryManager().querySkuSoldCount(getSkuId());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testSetStoreStockAvailability(){
    try {
      getInventoryManager().setStoreStockAvailability(getSkuId(),getStoreNumber(),getStatus());
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  
  public void testQueryStoreAvailability(){
    try {
       getInventoryManager().queryStoreAvailability(getSkuId(), getStoreNumber());
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }*/
	
	public void testQuerySkuStockLevel(){
		try {
			 getInventoryManager().querySkuStockLevel(getSkuId());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testQuerySkusStockLevel(){
		try {
			 getInventoryManager().querySkusStockLevel(getSkuIds(), false);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testQueryStoreAvailabilityList(){
		try {
			getInventoryManager().queryStoreAvailability(getSkuId(), getStoreList());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testIncrementStoreAllocated(){
    try {
      getInventoryManager().incrementStoreAllocated(getSkuId(), getStoreNumber(), getQuantity());
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
	
	public void testDecrementStoreAllocated(){
    try {
      getInventoryManager().decrementStoreAllocated(getSkuId(), getStoreNumber(), getQuantity());
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
	
	public void testIncrementStoreShipped(){
    try {
      getInventoryManager().incrementStoreShipped(getSkuId(), getStoreNumber(), getQuantity());
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
	
	public void testBopisInventory(){
	  
	  try {
      Map<String,Boolean> lStoreAvMap = getInventoryManager().getBopisInventory(getSkuIdQuantityMap(), getStoreList());
      
      if(lStoreAvMap!=null){
        for(String storeId: lStoreAvMap.keySet()){
          vlogInfo("StoreId:{0}, Elgibility:{1}",storeId, lStoreAvMap.get(storeId));
        }
      }
      
    } catch (InventoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
	}
	
	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public List<String> getSkuIds() {
		return skuIds;
	}

	public void setSkuIds(List<String> skuIds) {
		this.skuIds = skuIds;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public List<String> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<String> storeList) {
		this.storeList = storeList;
	}

	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public FFRepositoryInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(FFRepositoryInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

  public Map<String,Long> getSkuIdQuantityMap() {
    return skuIdQuantityMap;
  }

  public void setSkuIdQuantityMap(Map<String,Long> pSkuIdQuantityMap) {
    skuIdQuantityMap = pSkuIdQuantityMap;
  }
	
	

}
