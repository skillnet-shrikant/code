package com.aci.payment.creditcard.processor;

import atg.commerce.CommerceException;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessCreditCard;
import atg.payment.PaymentStatus;

import com.aci.payment.creditcard.AciCreditCardInfo;

/**
 * 
 * This pipeline processor will call ACI credit card processor.
 *
 */

public class ProcProcessAciCreditCard extends ProcProcessCreditCard {

	@Override
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
		
		vlogDebug("authorizePaymentGroup(): Called.");

		AciCreditCardInfo ccInfo = (AciCreditCardInfo)pParams.getPaymentInfo();
        try{
        	return pParams.getPaymentManager().getCreditCardProcessor().authorize(ccInfo);
        }catch(Exception e){
        	vlogError("Error authorizing credit card Paymentech Group", e);
        	throw new PaymentException("Invalid Authorization Status");
        }
    
	}
	
	
}