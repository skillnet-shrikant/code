package com.mff.commerce.payment;

import atg.commerce.payment.DummyTaxStatus;
import atg.nucleus.GenericService;
import atg.payment.tax.ShippingDestination;
import atg.payment.tax.TaxProcessor;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxStatus;

/**
 * Delete once Vertex integration with store is complete
 * 
 */
public class MFFDummyTaxProcessor extends GenericService implements TaxProcessor{

	private double dummyTaxRate;
	
	public double getDummyTaxRate() {
		return dummyTaxRate;
	}

	public void setDummyTaxRate(double dummyTaxRate) {
		this.dummyTaxRate = dummyTaxRate;
	}
	
	public TaxStatus calculateTax(TaxRequestInfo pTaxInfo) {
        double taxableAmount = 0.0D;
        for(int i = 0; i < pTaxInfo.getShippingDestinations().length; i++)
        {
        	ShippingDestination dest = pTaxInfo.getShippingDestinations()[i];
        	taxableAmount += dest.getTaxableItemAmount();
        }


        if(isLoggingDebug())
            logDebug((new StringBuilder()).append("taxable amount: ").append(taxableAmount).toString());
        DummyTaxStatus ret = new DummyTaxStatus();
        ret.setAmount(taxableAmount * getDummyTaxRate());
        //ret.setAmount(pEXTNTax.getTotalTax());
        ret.setCountyTax(taxableAmount * 0.4);
        ret.setCityTax(taxableAmount * 0.25);
        ret.setDistrictTax(taxableAmount * 0.3);
        ret.setStateTax(taxableAmount * 0.43);
        ret.setCountryTax(taxableAmount * 0.43);
        ret.setTransactionSuccess(true);
        
        return ret;
	}

	public TaxStatus[] calculateTaxByShipping(TaxRequestInfo pTaxInfo) {
        DummyTaxStatus ret[] = new DummyTaxStatus[pTaxInfo.getShippingDestinations().length];
        for(int i = 0; i < pTaxInfo.getShippingDestinations().length; i++)
        {
        	ShippingDestination dest = pTaxInfo.getShippingDestinations()[i];
        	double taxableAmount = dest.getTaxableItemAmount();
            if(isLoggingDebug())
                logDebug((new StringBuilder()).append("taxable amount: ").append(taxableAmount).toString());
            ret[i] = new DummyTaxStatus();
            ret[i].setAmount(taxableAmount * getDummyTaxRate());
            ret[i].setCountyTax(taxableAmount * 0.4);
            ret[i].setCityTax(taxableAmount * 0.25);
            ret[i].setDistrictTax(taxableAmount * 0.3);
            ret[i].setStateTax(taxableAmount * 0.43);
            ret[i].setCountryTax(taxableAmount * 0.43);
            ret[i].setTransactionSuccess(true);
        }

        return ret;
	}
}
