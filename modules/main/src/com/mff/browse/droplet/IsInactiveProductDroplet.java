/**
 * This Droplet is used to check if product is active or not
 */
package com.mff.browse.droplet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.constants.MFFConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author DMI
 *
 */
public class IsInactiveProductDroplet extends DynamoServlet {

  private static final String PRODUCTID = "productId";
  private static final String IS_INACTIVE_PRODUCT="isInActiveProduct";
  private static final String IS_ACTIVE_TEASER="isActiveTeaser";
  private MFFCatalogTools mCatalogTools;
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    String productId = pRequest.getParameter(PRODUCTID);
    vlogDebug("ProductId :{0}",productId);
    if (StringUtils.isNotEmpty(productId)) {
      boolean isTeaserActive = getCatalogTools().isProductTeaserActive(productId);
      pRequest.setParameter(IS_ACTIVE_TEASER, isTeaserActive);
      
      if (getCatalogTools().isProductActive(productId)) {
        pRequest.setParameter(IS_INACTIVE_PRODUCT, false);
        pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
      } else {
        pRequest.setParameter(IS_INACTIVE_PRODUCT, true);
        pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
      }
    } else {
      pRequest.setParameter(IS_INACTIVE_PRODUCT, false);
      pRequest.serviceLocalParameter("empty", pRequest, pResponse);
    }

  }
  
  public MFFCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  public void setCatalogTools(MFFCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
}
