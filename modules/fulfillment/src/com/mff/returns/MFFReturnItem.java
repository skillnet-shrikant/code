package com.mff.returns;

import java.util.Set;

import atg.commerce.csr.returns.ReturnException;
import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.order.CommerceItem;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

public class MFFReturnItem extends ReturnItem{

  private static final long serialVersionUID = 1L;
  
  private boolean returnShipping;
  private String comments;
  private Set<String> proRatedItemIds;
  private String storeId;
  
  public MFFReturnItem(CommerceItem pCommerceItem) {
    super(pCommerceItem);
    returnShipping = false;
  }
  
  @Override
  public void saveReturnItem(MutableRepositoryItem pDestination)
      throws ReturnException{
    super.saveReturnItem(pDestination);
    pDestination.setPropertyValue("comments", getComments());
    pDestination.setPropertyValue("storeId", getStoreId());
    pDestination.setPropertyValue("proRatedItemIds", getProRatedItemIds());
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void loadReturnItem(RepositoryItem pSource) throws ReturnException{
    super.loadReturnItem(pSource);
    setProRatedItemIds((Set<String>)pSource.getPropertyValue("proRatedItemIds"));
    setComments((String)pSource.getPropertyValue("comments"));
    setStoreId((String)pSource.getPropertyValue("storeId"));
  }
  
  public boolean isReturnShipping() {
    return returnShipping;
  }
  
  public void setReturnShipping(boolean returnShipping) {
    this.returnShipping = returnShipping;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  /**
   * @return the proRatedItemIds
   */
  public Set<String> getProRatedItemIds() {
    return proRatedItemIds;
  }

  /**
   * @param proRatedItemIds the proRatedItemIds to set
   */
  public void setProRatedItemIds(Set<String> proRatedItemIds) {
    this.proRatedItemIds = proRatedItemIds;
  }

  /**
   * @return the storeId
   */
  public String getStoreId() {
    return storeId;
  }

  /**
   * @param storeId the storeId to set
   */
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

}
