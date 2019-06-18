package com.mff.commerce.prorate;

import com.mff.constants.MFFConstants;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class MFFProrateItemManager extends GenericService{
  
  public RepositoryItem[] getProrateItemByCIAndLineNumber(String pCommerceItemId, Double pLineNumber) throws CommerceException {
    
    vlogDebug("MFFProrateItemManager Inside getProrateItemByCIAndLineNumber");
    RepositoryItem[] lItems = null;
    try {
      Object[] lParams = new Object[2];
      lParams[0] = pCommerceItemId;
      lParams[1] = pLineNumber;
      RepositoryView lView = getOmsOrderRepository().getView(MFFConstants.ITEM_DESC_PRORATE_ITEM);
      RqlStatement lStatement = RqlStatement.parseRqlStatement("commerceItemId EQUALS ?0 AND lineNumber = ?1");
      lItems = lStatement.executeQuery(lView, lParams);
    } catch (RepositoryException e) {
      String lErrorMessage = String.format("getProrateItemByCIAndLineNumber - Error locating Commerce Item Id: %s LineNumber: %s", pCommerceItemId, pLineNumber);
      vlogError(e, lErrorMessage);
      throw new CommerceException(lErrorMessage, e);
    }
    
    return lItems;
  }
  
  public RepositoryItem[] getProrateItemByCIAndStatus(String pCommerceItemId, String pStatus) throws CommerceException {
    
    vlogDebug("MFFProrateItemManager Inside getProrateItemByCIAndLineNumber");
    RepositoryItem[] lItems = null;
    try {
      Object[] lParams = new String[2];
      lParams[0] = pCommerceItemId;
      lParams[1] = pStatus;
      RepositoryView lView = getOmsOrderRepository().getView(MFFConstants.ITEM_DESC_PRORATE_ITEM);
      RqlStatement lStatement = RqlStatement.parseRqlStatement("commerceItemId EQUALS ?0 AND state EQUALS ?1");
      lItems = lStatement.executeQuery(lView, lParams);
    } catch (RepositoryException e) {
      String lErrorMessage = String.format("getProrateItemByCIAndLineNumber - Error locating Commerce Item Id: %s LineNumber: %s", pCommerceItemId, pStatus);
      vlogError(e, lErrorMessage);
      throw new CommerceException(lErrorMessage, e);
    }
    
    return lItems;
  }
  
  /** OMSOrderRepository **/
  MutableRepository mOmsOrderRepository;
  public MutableRepository getOmsOrderRepository() {
    return mOmsOrderRepository;
  }
  public void setOmsOrderRepository(MutableRepository pOmsOrderRepository) {
    this.mOmsOrderRepository = pOmsOrderRepository;
  }

}
