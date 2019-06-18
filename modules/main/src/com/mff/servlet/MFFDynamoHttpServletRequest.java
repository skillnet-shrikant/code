package com.mff.servlet;

import com.mff.util.MFFUtils;

import atg.servlet.DynamoHttpServletRequest;

public class MFFDynamoHttpServletRequest extends DynamoHttpServletRequest {

  @Override
  public boolean isBrowserType(String pFeature) {
    
    if (getBrowserTyper() != null) {

      return super.isBrowserType(pFeature);
    } else {
      if (isLoggingDebug()) {
        logDebug(MFFUtils.printRequestInfo(this));
      }
      return false;
    }
  }

}
