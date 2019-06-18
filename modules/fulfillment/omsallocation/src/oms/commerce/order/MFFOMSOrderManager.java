/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingTools;
import atg.commerce.states.CommerceItemStates;
import atg.commerce.states.StateDefinitions;

import com.google.common.base.Strings;
import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFHardgoodShippingGroup;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.pricing.MFFItemPriceInfo;
import com.mff.commerce.states.MFFCommerceItemStates;
import com.mff.commerce.states.MFFShippingGroupStates;

public class MFFOMSOrderManager extends OMSOrderManager {

  /**
   * Allocate the commerce items to either a store, warehouse or back ordered.
   * 
   * @param pInitialAllocation
   *          Indicates if this is the first allocation for this order
   * @param pOrder
   *          ATG Order
   * @param pItemAllocations
   *          List of allocations
   * @throws CommerceException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public void allocateCommerceItems(boolean pInitialAllocation, Order pOrder, List<ItemAllocation> pItemAllocations) throws CommerceException, InstantiationException, IllegalAccessException {

    MFFOrderImpl lOrder = (MFFOrderImpl) pOrder;

    if (pInitialAllocation) {
      vlogDebug("Calling initial allocation for order number {0}", lOrder.getId());
      allocateInitialCommerceItems(pOrder, pItemAllocations);
    } else {
      vlogDebug("Calling subsequent allocation for order number {0}", lOrder.getId());
      allocateSubsequentCommerceItems(pOrder, pItemAllocations);
    }
  }

  /**
   * Allocate the commerce items to either a store, warehouse or back ordered
   * for first time allocations.
   * 
   * @param pOrder
   *          ATG Order
   * @param pItemAllocations
   *          List of allocations
   * @throws CommerceException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public void allocateInitialCommerceItems(Order pOrder, List<ItemAllocation> pItemAllocations) throws CommerceException, InstantiationException, IllegalAccessException {

    int gcCount = 0;
    int forceAllocateCount = 0;
    int dropShipCount = 0;
    for (ItemAllocation lItem : pItemAllocations) {
      if (lItem.isGCFulfillment()) gcCount++;

      if (lItem.isForceAllocate()) forceAllocateCount++;
      
      if (lItem.isDropShipItem()) dropShipCount++;
    }

    // Loop through and allocate all of the items
    for (ItemAllocation lItemAllocation : pItemAllocations) {

      vlogDebug("Now processing initial item allocation for Commerce Item {0}", lItemAllocation.getCommerceItemId());

      if (lItemAllocation.isSplitItem()) {
        vlogDebug("Skipped Processing ItemAllocation as it's marked for split");
        continue;
      }
      // Find the shipping group for this item
      String lCommerceItemId = lItemAllocation.getCommerceItemId();
      CommerceItem lCommerceItem = pOrder.getCommerceItem(lCommerceItemId);
      ShippingGroup lShippingGroup = getShippingGroupForCommerceItem(pOrder, lCommerceItem);

      if (lItemAllocation.isGCFulfillment()) {
        if (gcCount == pItemAllocations.size()) {
          vlogDebug("Rule #1A : GiftCard - Allocate giftcard item {0} to existing shipping group {1}", lCommerceItemId, lShippingGroup.getId());
          allocateCommerceItem(lShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate order contains gift card item {0} and non-giftcard items", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lShippingGroup, lCommerceItem);
          vlogDebug("Rule #2A : GiftCard - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }

      } else if (lItemAllocation.isForceAllocate()) {
        if (forceAllocateCount == pItemAllocations.size()) {
          vlogDebug("Rule #1A : ForceAllocate - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lShippingGroup.getId());
          allocateCommerceItem(lShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate order contains forceAllocate item {0} and non-giftcard items", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lShippingGroup, lCommerceItem);
          vlogDebug("Rule #2A : ForceAllocate - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }
      } else if(lItemAllocation.isDropShipItem()){
        if (dropShipCount == pItemAllocations.size()) {
          vlogDebug("Rule #1A : DropShip - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lShippingGroup.getId());
          allocateCommerceItem(lShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate order contains dropShip item {0}", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lShippingGroup, lCommerceItem);
          vlogDebug("Rule #2A : DropShip - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }
      } else {
     
        MFFHardgoodShippingGroup lHShippingGroup = (MFFHardgoodShippingGroup) lShippingGroup;
        vlogDebug("Found shipping group {0} for Commerce Item {1}", lHShippingGroup.getId(), lCommerceItemId);

        // See if we need to split this item into a new shipping group.
        String lSGFulfillmentStore = lHShippingGroup.getFulfillmentStore();
        String lItemFulfillmentStore = lItemAllocation.getFulfillmentStore();
        if ((lSGFulfillmentStore != null && lItemFulfillmentStore.equalsIgnoreCase(lSGFulfillmentStore)) || lSGFulfillmentStore == null) {
          vlogDebug("Rule #1A - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lHShippingGroup.getId());
          allocateCommerceItem(lHShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          ShippingGroup lExistingShippingGroup = findShipGroupWithFulfillmentStore(pOrder, lItemFulfillmentStore);
          if (lExistingShippingGroup != null) {
            vlogDebug("Rule #3A = Adding item {0} to existing shipping group {1}", lCommerceItemId, lExistingShippingGroup.getId());

            // Remove relationship from old group and add to new
            vlogDebug("Remove CI Relationship from ShipGroup {0} and add to {1}", lShippingGroup.getId(), lExistingShippingGroup.getId());
            ShippingGroupCommerceItemRelationship lRel = getShippingGroupManager().getShippingGroupCommerceItemRelationship(pOrder, lCommerceItemId, lShippingGroup.getId());
            lRel.setShippingGroup(lExistingShippingGroup);
            lShippingGroup.removeCommerceItemRelationship(lRel.getId());
            allocateCommerceItem(lExistingShippingGroup, lCommerceItem, lItemAllocation);
          } else {
            vlogDebug("Adding new shipping group to accommodate item {0}", lCommerceItemId);
            ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lHShippingGroup, lCommerceItem);
            vlogDebug("Rule #2A - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
            allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
          }
        }
      }
    }
    // Adjust Shipping group Pricing
    adjustShippingGroupPricing(pOrder);
  }

  /**
   * Return the shipping group which has a given fulfillment store.
   * 
   * @param pOrder
   *          ATG Order
   * @param pItemFulfillmentStore
   *          Fulfillment Store
   * @return Shipping group which has the fulfillment store
   */
  @SuppressWarnings("unchecked")
  private ShippingGroup findShipGroupWithFulfillmentStore(Order pOrder, String pItemFulfillmentStore) {
    List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
    for (ShippingGroup lShippingGroup : lShippingGroups) {
      MFFHardgoodShippingGroup lHShippingGroup = (MFFHardgoodShippingGroup) lShippingGroup;
      String lSGFulfillmentStore = lHShippingGroup.getFulfillmentStore();
      vlogDebug("Checking Shipping group {0} Fulfillment store {1} Item fulfillment store {2}", lHShippingGroup.getId(), lSGFulfillmentStore, pItemFulfillmentStore);
      if (lSGFulfillmentStore != null && lSGFulfillmentStore.equalsIgnoreCase(pItemFulfillmentStore)) {
        vlogDebug("Returning Shipping group {0} Fulfillment store {1}", lHShippingGroup.getId(), lSGFulfillmentStore, pItemFulfillmentStore);
        return lHShippingGroup;
      }
    }
    return null;
  }

  /**
   * Allocate the commerce items to either a store, warehouse or back ordered
   * for subsequent allocations.
   * 
   * @param pOrder
   *          ATG Order
   * @param pItemAllocations
   *          List of allocations
   * @throws CommerceException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void allocateSubsequentCommerceItems(Order pOrder, List<ItemAllocation> pItemAllocations) throws CommerceException, InstantiationException, IllegalAccessException {

    // List of shipping groups that have been created
    List<String> lNewShippingGroups = new ArrayList();

    int gcCount = 0;
    int forceAllocateCount = 0;
    for (ItemAllocation lItem : pItemAllocations) {
      if (lItem.isGCFulfillment()) gcCount++;

      if (lItem.isForceAllocate()) forceAllocateCount++;
    }

    // Loop through and allocate all of the items
    for (ItemAllocation lItemAllocation : pItemAllocations) {

      vlogDebug("Now processing subsequent item allocation for Commerce Item {0}", lItemAllocation.getCommerceItemId());

      if (lItemAllocation.isSplitItem()) {
        vlogDebug("Skipped Processing ItemAllocation as it's marked for split");
        continue;
      }

      // Find the shipping group for this item
      String lCommerceItemId = lItemAllocation.getCommerceItemId();
      CommerceItem lCommerceItem = pOrder.getCommerceItem(lCommerceItemId);

      ShippingGroup lShippingGroup = getShippingGroupForCommerceItem(pOrder, lCommerceItem);

      if (lItemAllocation.isGCFulfillment()) {
        if (gcCount <= pItemAllocations.size()) {
          vlogDebug("Rule #1A : GiftCard - Allocate giftcard item {0} to existing shipping group {1}", lCommerceItemId, lShippingGroup.getId());
          allocateCommerceItem(lShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate order contains gift card item {0} and non-giftcard items", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lShippingGroup, lCommerceItem);
          vlogDebug("Rule #2A : GiftCard - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }
      } else if (lItemAllocation.isForceAllocate()) {
        if (forceAllocateCount <= pItemAllocations.size()) {
          vlogDebug("Rule #1A : ForceAllocate - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lShippingGroup.getId());
          allocateCommerceItem(lShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate order contains forceAllocate item {0} and non-giftcard items", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lShippingGroup, lCommerceItem);
          vlogDebug("Rule #2A : ForceAllocate - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }
      } else {
        MFFHardgoodShippingGroup lHShippingGroup = (MFFHardgoodShippingGroup) getShippingGroupForCommerceItem(pOrder, lCommerceItem);
        vlogDebug("Found shipping group {0} for Commerce Item {1}", lHShippingGroup.getId(), lCommerceItemId);

        // See if there is an existing ship group which can be used.
        ShippingGroup lExistingShippingGroup = findNewShippingGroupForStore(pOrder, lItemAllocation, lNewShippingGroups);

        // Get number of items in the shipping group
        int lNumberOfItemsinSG = lHShippingGroup.getCommerceItemRelationships().size();
        vlogDebug("Found {0} item in shipping group {1}", lNumberOfItemsinSG, lHShippingGroup.getId());

        // See if we need to split this item into a new shipping group.
        if (lNumberOfItemsinSG == 1) {
          vlogDebug("Rule #1B - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lHShippingGroup.getId());
          allocateCommerceItem(lHShippingGroup, lCommerceItem, lItemAllocation);
        } else if (lExistingShippingGroup != null) {
          vlogDebug("Rule #3B - Allocate item {0} to existing shipping group {1}", lCommerceItemId, lExistingShippingGroup.getId());

          // Remove relationship from old group and add to new
          vlogDebug("Remove CI Relationship from ShipGroup {0} and add to {1}", lShippingGroup.getId(), lExistingShippingGroup.getId());
          ShippingGroupCommerceItemRelationship lRel = getShippingGroupManager().getShippingGroupCommerceItemRelationship(pOrder, lCommerceItemId, lShippingGroup.getId());
          lRel.setShippingGroup(lExistingShippingGroup);
          lShippingGroup.removeCommerceItemRelationship(lRel.getId());
          allocateCommerceItem(lExistingShippingGroup, lCommerceItem, lItemAllocation);
        } else {
          vlogDebug("Adding new shipping group to accommodate item {0}", lCommerceItemId);
          ShippingGroup lNewShippingGroup = createNewShippingGroup(pOrder, lHShippingGroup, lCommerceItem);
          lNewShippingGroups.add(lNewShippingGroup.getId());
          vlogDebug("Rule #2B - Allocate item {0} to new shipping group {1}", lCommerceItemId, lNewShippingGroup.getId());
          allocateCommerceItem(lNewShippingGroup, lCommerceItem, lItemAllocation);
        }
      }
    }
    // Adjust Shipping group Pricing
    adjustShippingGroupPricing(pOrder);
  }

  /**
   * See if there is an existing shipping group that was created during the
   * current subsequent allocation that we can use to allocate an item that is
   * going to the same store.
   * 
   * Previously, items were allocated to a new shipping group even if a shipping
   * group was created during this pass which can be used to host the item. This
   * is causing multiple shipping groups for the same store which is an issue
   * for CSC.
   * 
   * @param pOrder
   *          ATG Order
   * @param pItemAllocation
   *          Item Allocation
   * @param pNewShippingGroups
   *          List of newly created shipping groups
   * @return
   */
  public ShippingGroup findNewShippingGroupForStore(Order pOrder, ItemAllocation pItemAllocation, List<String> pNewShippingGroups) {

    String lOrderNumber = pItemAllocation.getOrderId();
    String lCommerceItemId = pItemAllocation.getCommerceItemId();
    String lFulfillmentStore = pItemAllocation.getFulfillmentStore();

    // If the list of new shipping groups is null, return as we do not have any
    // existing shipping group to host this new allocation
    if (pNewShippingGroups == null) {
      vlogDebug("There are no newly created ship groups for order number {0} item {1} which can be used for this allocation", lOrderNumber, lCommerceItemId);
      return null;
    }
    vlogDebug("Current newly created shipping groups {0}", pNewShippingGroups);

    // Check if we can find an existing ship group that has this store number
    ShippingGroup lShippingGroup = findShipGroupWithFulfillmentStore(pOrder, lFulfillmentStore);
    if (lShippingGroup == null) {
      vlogDebug("No existing shipping group found for order number {0} item {1} Store {2} (Null SG)", lOrderNumber, lCommerceItemId, lFulfillmentStore);
      return null;
    }

    // Check if this item was allocated in this run
    vlogDebug("Check to see if shipping group {0} is a newly created group", lShippingGroup.getId());
    if (pNewShippingGroups.contains(lShippingGroup.getId())) {
      vlogDebug("Found existing shipping group {0} for order number {1} item {2} Store {3}", lShippingGroup.getId(), lOrderNumber, lCommerceItemId, lFulfillmentStore);
      return lShippingGroup;
    } else {
      vlogDebug("No existing shipping group found for order number {0} item {1} Store {2} (No existing group)", lOrderNumber, lCommerceItemId, lFulfillmentStore);
      return null;
    }
  }

  /**
   * Get the shipping group associated with this commerce item.
   * 
   * @param pOrder
   *          ATG Order
   * @param pCommerceItem
   *          Commerce item
   * @return Shipping group
   */
  @SuppressWarnings("unchecked")
  public ShippingGroup getShippingGroupForCommerceItem(Order pOrder, CommerceItem pCommerceItem) {
    ShippingGroup lShippingGroup = null;

    List<ShippingGroupCommerceItemRelationship> lRelationships = pCommerceItem.getShippingGroupRelationships();
    for (ShippingGroupCommerceItemRelationship lRelationship : lRelationships) {
      lShippingGroup = lRelationship.getShippingGroup();
      vlogDebug("Found relationship for Commerce item {0} with Shipping group {1}", pCommerceItem.getId(), lShippingGroup.getId());
    }
    if (lShippingGroup == null) vlogError("Could not find shipping group for Commerce item {0}", pCommerceItem.getId());
    return lShippingGroup;
  }

  /**
   * This method does the following: 1.) Adds a new shipping group 2.) Removes
   * the commerce item from the source shipping group 3.) Adds the item to the
   * new shipping group
   * 
   * @param pOrder
   *          ATG Order
   * @param pSourceShippingGroup
   *          Source shipping group
   * @param pCommerceItem
   *          Commerce item
   * @return
   * @throws CommerceException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  protected ShippingGroup createNewShippingGroup(Order pOrder, ShippingGroup pSourceShippingGroup, CommerceItem pCommerceItem) throws CommerceException, InstantiationException, IllegalAccessException {

    // Get ID's
    String lCommerceItemId = pCommerceItem.getId();
    String lSourceShippingGroupId = pSourceShippingGroup.getId();

    // Clone the primary shipping group
    MFFHardgoodShippingGroup lNewShippingGroup = (MFFHardgoodShippingGroup) cloneShippingGroup((HardgoodShippingGroup) pSourceShippingGroup, false);

    // Add Shipping group to Order
    pOrder.addShippingGroup(lNewShippingGroup);

    // Remove relationship from old group and add to new
    ShippingGroupCommerceItemRelationship lRel = getShippingGroupManager().getShippingGroupCommerceItemRelationship(pOrder, lCommerceItemId, lSourceShippingGroupId);
    lRel.setShippingGroup(lNewShippingGroup);
    pSourceShippingGroup.removeCommerceItemRelationship(lRel.getId());

    return lNewShippingGroup;
  }

  /**
   * This method handles the allocation of a commerce item 1.) Sets the commerce
   * item/shipping group fulfillment store 2.) Sets the state of the commerce
   * item/shipping group
   * 
   * @param pShippingGroup
   *          Shipping group
   * @param pCommerceItem
   *          Commerce Item
   * @param pItemAllocation
   *          Item Allocation
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void allocateCommerceItem(ShippingGroup pShippingGroup, CommerceItem pCommerceItem, ItemAllocation pItemAllocation) {

    MFFCommerceItemImpl lCommerceItem = (MFFCommerceItemImpl) pCommerceItem;
    MFFHardgoodShippingGroup lHGShippingGroup = (MFFHardgoodShippingGroup) pShippingGroup;
    if (pItemAllocation.isGCFulfillment()) {
     
      // Set Commerce Item Fulfillment Store
      lCommerceItem.setFulfillmentStore(pItemAllocation.getFulfillmentStore());
      lHGShippingGroup.setFulfillmentStore(pItemAllocation.getFulfillmentStore());

      pShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.PENDING_GC_FULFILLMENT));
      lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_GC_FULFILLMENT));
    } else if (pItemAllocation.isForceAllocate()) {
      lHGShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.FORCED_ALLOCATION));
      lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.FORCED_ALLOCATION));
    } else if(pItemAllocation.isDropShipItem()){
      // Set Commerce Item Fulfillment Store
      lCommerceItem.setFulfillmentStore(pItemAllocation.getFulfillmentStore());
      lHGShippingGroup.setFulfillmentStore(pItemAllocation.getFulfillmentStore());
      lHGShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.PENDING_DROP_SHIP_FULFILLMENT));
      lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_DROP_SHIP_FULFILLMENT));
    } else {

      // Set Shipping group Fulfillment Store
      lHGShippingGroup.setFulfillmentStore(pItemAllocation.getFulfillmentStore());
      // Set Commerce Item Fulfillment Store
      lCommerceItem.setFulfillmentStore(pItemAllocation.getFulfillmentStore());
      Set allocationsHistory = new HashSet<String>();
      if (null != lCommerceItem.getPreviousAllocation()) {
        allocationsHistory = (Set) lCommerceItem.getPreviousAllocation();
      }
      if (!Strings.isNullOrEmpty(pItemAllocation.getFulfillmentStore())) {
        allocationsHistory.add(pItemAllocation.getFulfillmentStore());
        lCommerceItem.setPreviousAllocation(allocationsHistory);
      }

      lHGShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.SENT_TO_STORE));
      lCommerceItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.SENT_TO_STORE));

    }
  }

  // --------------------------------------------------------------
  //
  // End of
  // Allocation Procedures
  //
  // --------------------------------------------------------------

  /**
   * Get a list of all of the stores that will be fulfilling for this order, and
   * the items they will be fulfilling.
   * 
   * @param pItemAllocations
   *          List of Allocations from stored procedure
   * @return Hash table of store and a list of commerce items
   */
  protected Hashtable<String, List<String>> getFulfillmentStores(List<ItemAllocation> pItemAllocations) {
    Hashtable<String, List<String>> lStoreHash = new Hashtable<String, List<String>>();
    for (ItemAllocation lItemAllocation : pItemAllocations) {
      String lStoreId = lItemAllocation.getFulfillmentStore();
      if (lStoreHash.get(lStoreId) == null) {
        List<String> lList = new ArrayList<String>();
        lList.add(lItemAllocation.getCommerceItemId());
        lStoreHash.put(lStoreId, lList);
      } else {
        List<String> lList = (List<String>) lStoreHash.get(lStoreId);
        lList.add(lItemAllocation.getCommerceItemId());
        lStoreHash.put(lStoreId, lList);
      }
    }
    vlogDebug("We need " + lStoreHash.size() + " Shipping groups for allocation");
    return lStoreHash;
  }

  /**
   * Get the primary shipping group which contains all of the commerce items for
   * the order.
   * 
   * @param pOrder
   * @return
   */
  @SuppressWarnings("unchecked")
  protected ShippingGroup getPrimaryShippingGroup(Order pOrder) {
    List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
    MFFOrderImpl lOrder = ((MFFOrderImpl) pOrder);

    // If this is the only shipping group, set the state to removed and
    // return
    if (lShippingGroups.size() == 1) {
      ShippingGroup lShippingGroup = lShippingGroups.get(0);
      vlogDebug("Primary Shipping group {0} found for order {1} based on rule #1", lShippingGroup.getId(), lOrder.getId());
      lShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.REMOVED));
      return lShippingGroup;
    }

    // If there are more than one, find the removed one and return
    for (ShippingGroup lShippingGroup : lShippingGroups) {
      if (lShippingGroup.getState() == StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.REMOVED)) {
        vlogDebug("Primary Shipping group {0} found for order {1} based on rule #2", lShippingGroup.getId(), lOrder.getId());
        return lShippingGroup;
      }
    }
    return null;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void splitCommerceItemsForAllocation(OrderImpl pOrder, List<ItemAllocation> pItemAllocations,String pCommerceItemStateAferSplit) throws CommerceException {
    if (isLoggingDebug()) {
      logDebug("OrderManager:::inside splitCommerceItemsForAllocation");
    }

    try {

      List itemsToSplit = new ArrayList();

      for (ItemAllocation lItemAllocation : pItemAllocations) {
        // identify the items to split
        if (lItemAllocation.isSplitItem()) {
          String lCommerceItemId = lItemAllocation.getCommerceItemId();
          CommerceItem lCommerceItem = pOrder.getCommerceItem(lCommerceItemId);
          splitItemForAllocation(pOrder, (MFFCommerceItemImpl) lCommerceItem,pCommerceItemStateAferSplit);
          itemsToSplit.add(lCommerceItem);
        }
      }
      
      if (itemsToSplit.size() > 0) {
        // remove orphan shipping group from order after split
        List<ShippingGroup> lShippingGroups = pOrder.getShippingGroups();
        for (ShippingGroup lShippingGroup : lShippingGroups) {
          List<CommerceItemRelationship> lRelationships = lShippingGroup.getCommerceItemRelationships();
          if(lRelationships == null || lRelationships.size() == 0){
            vlogDebug("Remove Orphan Shipping Group - {0} from the order - {0}",lShippingGroup.getId(),pOrder.getId());
            //getShippingGroupManager().removeShippingGroupFromOrder(pOrder, lShippingGroup.getId());
            lShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.REMOVED));
          }
        }
        
        this.updateOrder(pOrder);
      } else {
        vlogDebug("splitCommerceItemsForAllocation : no items to split");
      }

    } catch (Exception e) {
      if (isLoggingDebug()) {
        logError("splitCommerceItemsForAllocation Exception::", e);
      }
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void splitItemForAllocation(OrderImpl pOrder, MFFCommerceItemImpl pItem,String pCommerceItemStateAferSplit) throws Exception {

    int totalItemsCloned = 0;
    List clonedItems = new ArrayList();
    List removedItems = new ArrayList();
    
    if (isLoggingDebug()) {
      logDebug("comm item id is:::" + pItem.getId());
      logDebug("comm item's qunatity is is:::" + pItem.getQuantity());
    }

    pItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(CommerceItemStates.REMOVED));
    removedItems.add(pItem);

    Iterator relIter = pItem.getShippingGroupRelationships().iterator();
    while (relIter.hasNext()) {
      ShippingGroupCommerceItemRelationship shipRelation = (ShippingGroupCommerceItemRelationship) relIter.next();
      MFFHardgoodShippingGroup cuShipGroup = (MFFHardgoodShippingGroup) shipRelation.getShippingGroup();

      if (isLoggingDebug()) {
        logDebug("ship group id iss:::" + shipRelation.getShippingGroup().getId());
      }

      MFFHardgoodShippingGroup shipGroup = null;
      vlogDebug("Ship Group Fulfillment Store ::: {0}", cuShipGroup.getFulfillmentStore());
      // If SG has fulfillmentStore then create new SG for the cloned items
      if (!Strings.isNullOrEmpty(cuShipGroup.getFulfillmentStore())) {

        // Clone the primary shipping group
        shipGroup = (MFFHardgoodShippingGroup) cloneShippingGroup(cuShipGroup, false);
        // set SG state to anything other than INITIAL as that would indicate to
        // Allocation that this is initial allocation rather than subsequent
        // Pick up the appropriate state for shipping group after cloning
        shipGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(MFFShippingGroupStates.PROCESSING));
        // Add Shipping group to Order
        this.getShippingGroupManager().addShippingGroupToOrder(pOrder, shipGroup);
      } else {
        shipGroup = (MFFHardgoodShippingGroup) cuShipGroup;
      }

      if (isLoggingDebug()) {
        logDebug("quantity with this ship group is:::" + shipRelation.getQuantity());
      }

      long quantity = shipRelation.getQuantity();
      for (int i = 1; i <= quantity; i++) {
        if (isLoggingDebug()) {
          logDebug("creating item #:::" + i);
        }
        // clone comm item
        CommerceItemImpl newItem = cloneCommerceItem(pItem);
        // copy priceInfo objects
        copyPriceInfo(newItem, pItem, i);
        if (isLoggingDebug()) {
          logDebug("splitItemForAllocation : adding to order");
        }
        
        
        if(!Strings.isNullOrEmpty(pCommerceItemStateAferSplit)){
          newItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(pCommerceItemStateAferSplit));
        }else{
          newItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_ALLOCATION));
        }
        
        this.getCommerceItemManager().addAsSeparateItemToOrder(pOrder, newItem);
        this.getCommerceItemManager().addItemQuantityToShippingGroup(pOrder, newItem.getId(), shipGroup.getId(), newItem.getQuantity());
        clonedItems.add(newItem);
        totalItemsCloned++;
      }
    }
    if (isLoggingDebug()) {
      logDebug("totalItemsCloned:::" + totalItemsCloned);
    }
    // now split pricing
    splitPriceInfos(clonedItems,  pItem);
    
    if(isProcessPriceOverrides()) {
    	splitPriceOverrides(clonedItems, pItem);
    }
    
    splitShippingAmount(clonedItems, pItem);
    // Now split promotion amount in promo amount map.
    // if
    // (pItem.getPropertyValue(OMSOrderConstants.ITEM_PROPERTY_PROMO_AMOUNT_MAP)
    // != null)
    // splitPromoAmount(pOrder, clonedItems, pItem);

    // Now delete the removed Items
    Iterator remIter = removedItems.iterator();
    while (remIter.hasNext()) {
      CommerceItemImpl item = (CommerceItemImpl) remIter.next();
      if (isLoggingDebug()) {
        logDebug("removing item from order with Id::" + item.getId());
      }
      getCommerceItemManager().removeItemFromOrder(pOrder, item.getId());
    }
  }
  
  @SuppressWarnings("rawtypes")
  private void splitShippingAmount(List pItemsList, MFFCommerceItemImpl pItem) throws CommerceException{
    if (isLoggingDebug()) {
      logDebug("inside splitShippingAmount");
    }
    try {
      int listSize = pItemsList.size();

      double[] proratedShipping = splitAmount(pItem.getShipping(),listSize);
      double[] proratedShippingCityTax = splitAmount(pItem.getShippingCityTax(),listSize);
      double[] proratedShippingCountyTax = splitAmount(pItem.getShippingCountyTax(),listSize);
      double[] proratedShippingStateTax = splitAmount(pItem.getShippingStateTax(),listSize);
      double[] proratedShippingDistrictTax = splitAmount(pItem.getShippingDistrictTax(),listSize);
      double[] proratedShippingCountryTax =splitAmount(pItem.getShippingCountryTax(),listSize);

      // set split amounts among items
      Iterator iter = pItemsList.iterator();
      int itemNum = 1;
      while (iter.hasNext()) {
        MFFCommerceItemImpl newItem = (MFFCommerceItemImpl) iter.next(); 
      
        if (itemNum != listSize) {
          newItem.setShipping(proratedShipping[0]);
          
          newItem.setShippingCityTax(proratedShippingCityTax[0]);
          newItem.setShippingCountyTax(proratedShippingCountyTax[0]);
          newItem.setShippingStateTax(proratedShippingStateTax[0]);
          newItem.setShippingDistrictTax(proratedShippingDistrictTax[0]);
          newItem.setShippingCountryTax(proratedShippingCountryTax[0]);
          double amount = proratedShippingCityTax[0] + proratedShippingCountyTax[0] + proratedShippingStateTax[0] + proratedShippingDistrictTax[0] + proratedShippingCountryTax[0];
          newItem.setShippingTax(amount);
        } else {
          newItem.setShipping(proratedShipping[0] + proratedShipping[1]);
          
          newItem.setShippingCityTax(proratedShippingCityTax[0] + proratedShippingCityTax[1]);
          newItem.setShippingCountyTax(proratedShippingCountyTax[0] + proratedShippingCountyTax[1]);
          newItem.setShippingStateTax(proratedShippingStateTax[0] + proratedShippingStateTax[1]);
          newItem.setShippingDistrictTax(proratedShippingDistrictTax[0] + proratedShippingDistrictTax[1]);
          newItem.setShippingCountryTax(proratedShippingCountryTax[0] + proratedShippingCountryTax[1]);
          double amount = proratedShippingCityTax[0] + proratedShippingCityTax[1] + proratedShippingCountyTax[0] + proratedShippingCountyTax[1] + 
                          proratedShippingStateTax[0] + proratedShippingStateTax[1] + 
                          proratedShippingDistrictTax[0] + proratedShippingDistrictTax[1] + 
                          proratedShippingCountryTax[0] +  proratedShippingCountryTax[1];
          newItem.setShippingTax(amount);
        }
        itemNum++;
      }
    } catch (Exception e) {
      if (isLoggingError()) {
        logError("splitShippingAmount Exception::" + e);
      }
      throw new CommerceException(e);
    }
  }

  /**
   * Pro ration logic based for quantity
   * 
   * @param totalAmount
   * @param pQuantityToSplit
   * @return
   */
  /*public HashMap<Integer, Double> prorateOnQuantity(double totalAmount, double pQuantityToSplit) {

    vlogDebug("prorateOnQuantity : totalAmount - {0},pQuantityToSplit - {1} ", totalAmount, pQuantityToSplit);

    if (Double.isNaN(totalAmount)) {
      totalAmount = 0.0d;
    }

    HashMap<Integer, Double> proratedAmounts = new HashMap<Integer, Double>();
    BigDecimal runningAmount = new BigDecimal(totalAmount);
    BigDecimal quantity = new BigDecimal(pQuantityToSplit);

    if (pQuantityToSplit == 1) {
      proratedAmounts.put(1, totalAmount);
      return proratedAmounts;
    }

    for (int i = 1; i <= pQuantityToSplit; i++) {

      BigDecimal price = runningAmount.divide(quantity, 2, RoundingMode.HALF_UP);
      proratedAmounts.put(i, price.doubleValue());

      runningAmount = runningAmount.subtract(price);
      quantity = quantity.subtract(BigDecimal.ONE);

    }

    vlogDebug("prorateOnQuantity : proratedAmounts - {0}", proratedAmounts);
    return proratedAmounts;
  }*/

  /**
   * The following method is used to compute the amount that needs to be settled
   * when an item is shipped. The amount that is to be settled has to take into
   * account the following.
   * <ul>
   * <li>the amount on the item's price info
   * <li>the prorated amount if the item has been discounted
   * <li>the prorated shipping charge
   * <li>the prorated tax on the item
   * </ul>
   * 
   * @param itemToShip
   *          the commerce item id of the item that is being shipped
   * @param pOrder
   *          the order that is being shipped
   * @return the total amount to be settled
   * @throws CommerceItemNotFoundException
   *           when the commerce item is not found
   * @throws InvalidParameterException
   *           the commerce item is not valid
   */
  public double computeAmountToSettle(String itemToShip, Order pOrder) throws CommerceItemNotFoundException, InvalidParameterException {

    vlogDebug("Entering computeAmountToSettle : listOfItemsToShip, pOrder");

    CommerceItem lCommerceItem;
    double lOrderDiscountShare;
    double lItemTotal;
    double lProratedItemTotal = 0.0d;
    double lShippingCharge;
    double lItemTotalTax;
    double lTotal = 0.0d;
    double lShippingChargeTax = 0.0d;
    double lEffectivePrice = 0.0d;
    

    lCommerceItem = pOrder.getCommerceItem(itemToShip);
    MFFItemPriceInfo lPriceInfo = (MFFItemPriceInfo) lCommerceItem.getPriceInfo();
    
    lItemTotal = lPriceInfo.getAmount();
    

    lProratedItemTotal = lPriceInfo.getProratedDiscountPrice();
    
    
    /**
     * if there is a prorated discount associated with the commerce item then we
     * will use the prorated amount instead of the price info's amount
     */
    if (lProratedItemTotal > 0.0) {
      lItemTotal = lProratedItemTotal;
    }

    lEffectivePrice = lPriceInfo.getEffectivePrice();
    if(lEffectivePrice > 0.0) {
    	lItemTotal = lEffectivePrice;
    }
    
    
    lItemTotalTax = getItemTax(lCommerceItem);
    lShippingCharge = ((MFFCommerceItemImpl) lCommerceItem).getShipping();
    lOrderDiscountShare = lCommerceItem.getPriceInfo().getOrderDiscountShare();
    lShippingChargeTax = ((MFFCommerceItemImpl) lCommerceItem).getShippingTax();

    /**
     * To determine the total amount to be settled first we compute the item to
     * totals using the following formula settlement amount = proratedAmount +
     * shipping charges + item total tax - order discount share
     */
    lTotal = lItemTotal + lItemTotalTax + lShippingCharge + lShippingChargeTax - lOrderDiscountShare;
    
    lTotal = getPricingTools().round(lTotal);

    if (isLoggingDebug()) {
      StringBuilder sb = new StringBuilder();
      sb.append(LINE_SEPARATOR);
      sb.append("******************************************************************");
      sb.append(LINE_SEPARATOR);
      sb.append("        lItemTotalTax = " + lItemTotal          + LINE_SEPARATOR);
      sb.append("      lShippingCharge = " + lShippingCharge     + LINE_SEPARATOR);
      sb.append("                  Tax = " + lItemTotalTax       + LINE_SEPARATOR);
      sb.append("  lOrderDiscountShare = " + lOrderDiscountShare + LINE_SEPARATOR);
      sb.append("  lShippingChargeTax  = " + lShippingChargeTax  + LINE_SEPARATOR);
      sb.append("               lTotal = " + lTotal              + LINE_SEPARATOR);
      sb.append("******************************************************************");
      sb.append(LINE_SEPARATOR);
      
      logDebug(sb.toString());
    }

    vlogDebug("Exiting computeAmountToSettle : listOfItemsToShip, pOrder");
    return lTotal;
  }
  
  /**
   * The following method returns the total tax that needs to be collected for a
   * given item
   * 
   * @param pCommerceItem
   *          the commerce item for which the tax needs to be computed
   * @return the tax associated with the item
   */
  @SuppressWarnings("unchecked")
  public double getItemTax (CommerceItem pCommerceItem) {
    double itemTotalTax = 0.0d;
    MFFCommerceItemImpl lCommerceItemImpl = (MFFCommerceItemImpl)pCommerceItem;
    
    if(lCommerceItemImpl.getTaxPriceInfo() != null){
      itemTotalTax = lCommerceItemImpl.getTaxPriceInfo().getAmount();
    }else{
      List<DetailedItemPriceInfo> detailedPriceInfos = pCommerceItem.getPriceInfo().getCurrentPriceDetails();
      
      for (DetailedItemPriceInfo lDetailPriceInfo : detailedPriceInfos) {
        itemTotalTax += lDetailPriceInfo.getTax();
      }
    }
    
    return itemTotalTax;    
  }
  

  public HashMap<String, Double> createDebitSettlements(Order pOrder, String pCommerceItemId, String pInvoiceId, double pAmountToSettle) {
    
    return null;
  }
  
  PricingTools mPricingTools;

  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  public void setPricingTools(PricingTools pPricingTools) {
    this.mPricingTools = pPricingTools;
  }

  CommerceItemManager mCommerceItemManager;

  public CommerceItemManager getCommerceItemManager() {
    return mCommerceItemManager;
  }

  public void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
    this.mCommerceItemManager = pCommerceItemManager;
  }

  ShippingGroupManager mShippingGroupManager;

  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    this.mShippingGroupManager = pShippingGroupManager;
  }

}
