package com.mff.commerce.payment;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.mff.commerce.payment.tax.MFFTaxStatusImpl;
import com.mff.commerce.payment.tax.MFFTaxStatusItem;
import com.mff.integration.ws.service.vertex.CalculateTaxService;
import com.mff.integration.ws.service.vertex.LineItemTax;
import com.mff.integration.ws.service.vertex.Tax;

import atg.commerce.payment.DummyTaxProcessor;
import atg.payment.tax.ShippingDestination;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxStatus;

public class MFFTaxProcessor extends DummyTaxProcessor {

	private CalculateTaxService mCalculateTaxService;

	public CalculateTaxService getCalculateTaxService() {
		return mCalculateTaxService;
	}

	public void setCalculateTaxService(CalculateTaxService pCalculateTaxService) {
		this.mCalculateTaxService = pCalculateTaxService;
	}

	@Override
	public TaxStatus calculateTax(TaxRequestInfo pTaxReqInfo) {
	MFFTaxStatusImpl extnTaxStatus = new MFFTaxStatusImpl();

	Tax mTax = null;
		try {
			mTax = getCalculateTaxService().calculateTax(pTaxReqInfo);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}

		if (null != mTax) {
		extnTaxStatus = buildTaxStatus(mTax);
		} else {

			return null;
		}

		return extnTaxStatus;
	}

	@Override
	public TaxStatus[] calculateTaxByShipping(TaxRequestInfo pTaxReqInfo) {
		ShippingDestination[] mShippingDestination = pTaxReqInfo
				.getShippingDestinations();
		if (0 == mShippingDestination.length) {
			return super.calculateTaxByShipping(pTaxReqInfo);
		}

		return super.calculateTaxByShipping(pTaxReqInfo);
	}

	public MFFTaxStatusImpl buildTaxStatus(Tax pEXTNTax) {
		Map<String, MFFTaxStatusItem> lEXTNTaxStatusItems = new HashMap<String, MFFTaxStatusItem>();
		MFFTaxStatusImpl mTaxStatus = new MFFTaxStatusImpl();

		List<LineItemTax> mLineItemTaxes = pEXTNTax.getLineItemTax();
		double mTotalCountyTax = 0.0D;
		double mTotalStateTax = 0.0D;
		double mTotalCityTax = 0.0D;
		double mTotalDistrictTax = 0.0D;
		double mTotalCountryTax = 0.0D;

		if (null != mLineItemTaxes) {
			for (LineItemTax extnLineItemTax : mLineItemTaxes) {
				if (isLoggingDebug()) {
					logDebug("EXTNTaxProcessor - CITY TAX : " + extnLineItemTax.getCityTax());
					logDebug("EXTNTaxProcessor - STATE TAX : " + extnLineItemTax.getStateTax());
					logDebug("EXTNTaxProcessor - COUNTY TAX : " + extnLineItemTax.getCountyTax());
					logDebug("EXTNTaxProcessor - DISTRICT TAX : " + extnLineItemTax.getDistrictTax());
				}
				
				mTotalCityTax += extnLineItemTax.getCityTax();
				mTotalStateTax += extnLineItemTax.getStateTax();
				mTotalCountyTax += (extnLineItemTax.getCountyTax());
				mTotalDistrictTax += extnLineItemTax.getDistrictTax();

			MFFTaxStatusItem extnTaxStatusItem = new MFFTaxStatusItem();
			extnTaxStatusItem.setCommerceItemId(extnLineItemTax
						.getLineItemId());
			extnTaxStatusItem.setTax(extnLineItemTax.getTotalTax());
			extnTaxStatusItem.setCityTax(extnLineItemTax.getCityTax());
			extnTaxStatusItem.setCountyTax(extnLineItemTax.getCountyTax());
			extnTaxStatusItem
						.setDistrictTax(extnLineItemTax.getDistrictTax());
			extnTaxStatusItem.setStateTax(extnLineItemTax.getStateTax());
			extnTaxStatusItem.setCountyTaxInfo(extnLineItemTax.getCountyTaxInfo());
			
				lEXTNTaxStatusItems.put(extnLineItemTax.getLineItemId(),
					extnTaxStatusItem);
			}
		}

		if (isLoggingDebug()) {
			logDebug("Total City Tax : " + mTotalCityTax);
			logDebug("Total State Tax : " + mTotalStateTax);
			logDebug("Total County Tax : " + mTotalCountyTax);
			logDebug("Total District Tax : "+mTotalDistrictTax);
		}
		if(mLineItemTaxes != null && mLineItemTaxes.size() > 0){
		  mTaxStatus.setCountyTaxInfo(mLineItemTaxes.get(0).getCountyTaxInfo());
		}
		mTaxStatus.setEXTNTaxStatusItems(lEXTNTaxStatusItems);

		mTaxStatus.setAmount(pEXTNTax.getTotalTax());
		mTaxStatus.setCountyTax(mTotalCountyTax);
		mTaxStatus.setCityTax(mTotalCityTax);
		mTaxStatus.setDistrictTax(mTotalDistrictTax);
		mTaxStatus.setStateTax(mTotalStateTax);
		mTaxStatus.setCountryTax(mTotalCountryTax);

		return mTaxStatus;
	}
}
