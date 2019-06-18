package com.aci.payment.creditcard;



import atg.commerce.order.CreditCard;
import atg.core.util.StringUtils;

import com.aci.constants.AciConstants;

/**
 * @see atg.commerce.order.CreditCard
 */
public class AciCreditCard extends CreditCard {
	
	private static final long serialVersionUID = 1L;
	
	public AciCreditCard() {
		super();
	}
	
	public String getTokenNumber() {
		return (String) getPropertyValue(AciConstants.PROPERTY_CC_TOKEN_NUMBER);
	}

	public void setTokenNumber(String pTokenNumber) {
		setPropertyValue(AciConstants.PROPERTY_CC_TOKEN_NUMBER, pTokenNumber != null ? ((Object) (StringUtils.removeWhiteSpace(pTokenNumber))) : ((Object) (pTokenNumber)));
	}
	
	public String getNameOnCard() {
		return (String) getPropertyValue(AciConstants.PROPERTY_CC_NAME_ON_CARD);
	}

	public void setNameOnCard(String pNameOnCard) {
		setPropertyValue(AciConstants.PROPERTY_CC_NAME_ON_CARD, pNameOnCard != null ? ((Object)(pNameOnCard.trim())) : ((Object) (pNameOnCard)));
	}
	
	public String getMopTypeCode(){
		return (String) getPropertyValue(AciConstants.PROPERTY_ACI_MOP_TYPE_CODE);
	}
	
	public void setMopTypeCode(String pMopTypeCode){
		setPropertyValue(AciConstants.PROPERTY_ACI_MOP_TYPE_CODE,pMopTypeCode != null ? ((Object) (StringUtils.removeWhiteSpace(pMopTypeCode))) : ((Object) (pMopTypeCode)));
	}
	
}
