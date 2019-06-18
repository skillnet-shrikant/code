package com.mff.commerce.order.processor;

import java.util.HashMap;
import java.util.Map;

import com.firstdata.order.MFFGiftCardPaymentGroup;
import com.mff.constants.MFFConstants;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class MFFProcValidateGiftCard extends ApplicationLoggingImpl implements PipelineProcessor {

	private static final int SUCCESS = 1;

	protected int mRetCodes[] = { SUCCESS };
	String mLoggingIdentifier;

	public MFFProcValidateGiftCard() {
		mLoggingIdentifier = "ProcValidateGiftCard";
	}

	public int[] getRetCodes() {
		return mRetCodes;
	}

	public int runProcess(Object pParam, PipelineResult pResult) throws InvalidParameterException {

		vlogDebug ("Start of MFFProcValidateGiftCard.runProcess...");

		ValidatePaymentGroupPipelineArgs args = (ValidatePaymentGroupPipelineArgs) pParam;
		PaymentGroup paymentGroup = args.getPaymentGroup();

		if (paymentGroup == null) {
			vlogError ("PaymentGroup is NULL is MFFProcValidateGiftCard.");
			throw new InvalidParameterException("MFFProcValidateGiftCard shouldn't be passed a NULL payment group");
		}
		if (!(paymentGroup instanceof MFFGiftCardPaymentGroup)) {
			vlogError ("PaymentGroup is not a GiftCard is MFFProcValidateGiftCard.  Why is that?  Throwing an InvalidParameterException!");
			throw new InvalidParameterException("MFFProcValidateGiftCard shouldn't be passed a non Gift Card payment group");
		}

		vlogDebug("Validating one GiftCard of type " + paymentGroup.getPaymentGroupClassType());
		
		validateGiftCardFields((MFFGiftCardPaymentGroup) paymentGroup, false, pResult);

		return SUCCESS;
	}

	protected void validateGiftCardFields(MFFGiftCardPaymentGroup pGiftCard, boolean pCheckPin, PipelineResult pResult) {
		String giftCardNumber = pGiftCard.getCardNumber();
		
		vlogDebug("Validating Gift Card fields...");

		if (StringUtils.isEmpty(giftCardNumber)) {
			String msg = MFFConstants.getEXTNResources().getString(MFFConstants.NO_GIFT_CARD_NUMBER);
			addHashedError(pResult, "MissingGiftCardInformation", pGiftCard.getId(), msg);
			return;
		}
		
		String pinNumber = pGiftCard.getEan();

		if (pCheckPin && StringUtils.isEmpty(pinNumber)) {
			String msg = MFFConstants.getEXTNResources().getString(MFFConstants.NO_GIFT_CARD_PIN);
			addHashedError(pResult, "MissingGiftCardInformation", pGiftCard.getId(), msg);
			return;
		}
	}

	protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError) {
		Object error = pResult.getError(pKey);
		if (error == null) {
			HashMap map = new HashMap(5);
			pResult.addError(pKey, map);
			map.put(pId, pError);
		}
		else if (error instanceof Map) {
			Map map = (Map) error;
			map.put(pId, pError);
		}
	}

	public void setLoggingIdentifier(String pLoggingIdentifier) {
		mLoggingIdentifier = pLoggingIdentifier;
	}

	public String getLoggingIdentifier() {
		return mLoggingIdentifier;
	}
}
