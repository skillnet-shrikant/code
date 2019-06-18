package com.aci.payment.creditcard.processor;

import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcCreateCreditCardInfo;
import atg.payment.creditcard.GenericCreditCardInfo;

import atg.core.util.StringUtils;

import com.aci.payment.creditcard.AciCreditCard;
import com.aci.payment.creditcard.AciCreditCardInfo;

/**
 * This processor will create a ACI credit card info to be used in the pipeline.
 * 
 * @author DMI
 *
 */

public class ProcCreateAciCreditCardInfo extends ProcCreateCreditCardInfo  {
	
	public static final int SUCCESS = 1;

	@Override
	protected void addDataToCreditCardInfo(Order pOrder, CreditCard pPaymentGroup, double pAmount, PaymentManagerPipelineArgs pParams, GenericCreditCardInfo pCreditCardInfo)
    {
			((AciCreditCardInfo)pCreditCardInfo).setNameOnCard(((AciCreditCard)pPaymentGroup).getNameOnCard());
			((AciCreditCardInfo)pCreditCardInfo).setTokenNumber(((AciCreditCard)pPaymentGroup).getTokenNumber());
			((AciCreditCardInfo)pCreditCardInfo).setMopTypeCd(((AciCreditCard)pPaymentGroup).getMopTypeCode());
			String agentId=pOrder.getAgentId();
			if(StringUtils.isEmpty(agentId)){
				((AciCreditCardInfo)pCreditCardInfo).setOnlineOrder(true);
			}
			else {
				((AciCreditCardInfo)pCreditCardInfo).setOnlineOrder(false);
			}
			super.addDataToCreditCardInfo(pOrder, pPaymentGroup, pAmount, pParams, pCreditCardInfo);
    }

	
}