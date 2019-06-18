package com.mff.commerce.pricing;

import atg.commerce.pricing.TaxPriceInfo;

public class MFFTaxPriceInfo extends TaxPriceInfo{

  private static final long serialVersionUID = 1L;
  
  private String mCountyTaxInfo;
  
  public String getCountyTaxInfo() {
    return mCountyTaxInfo;
  }

  public void setCountyTaxInfo(String pCountyTaxInfo) {
    mCountyTaxInfo = pCountyTaxInfo;
  }

}
