package com.mff.commerce.order.purchase;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.aci.payment.creditcard.AciCreditCard;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.constants.MFFConstants;
import com.mff.droplet.InlineFormErrorSupport;
import com.mff.droplet.MFFInlineDropletFormException;

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.ExpressCheckoutFormHandler;
import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFExpressCheckoutFormHandler extends ExpressCheckoutFormHandler implements InlineFormErrorSupport {

  private MFFFormExceptionGenerator mFormExceptionGenerator;
  private MFFCheckoutManager mCheckoutManager;

  /**
   * Overriding Express Checkout to set Contact Email on the Order
   */
  @Override
  public boolean handleExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {

    if (getProfile() == null) {
      vlogError("handleExpressCheckout: No Profile Found for orderId:{0}, can not proceed with express checkout", getOrderId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_EXPRESS_CHECKOUT_ERROR, true, this, pRequest);
      return false;
    }

    if (getOrder() == null) {
      vlogError("handleExpressCheckout: No Order Found for profileId:{0}, can not proceed with express checkout", getProfile().getRepositoryId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_EXPRESS_CHECKOUT_ERROR, true, this, pRequest);
      return false;
    }

    String email = (String) getProfile().getPropertyValue("email");
    if (email == null || email.isEmpty()) {
      vlogError("handleExpressCheckout: No Contact Email Found for orderId:{0}, can not proceed with express checkout", getOrderId());
      getFormExceptionGenerator().generateException(MFFConstants.MSG_EXPRESS_CHECKOUT_ERROR, true, this, pRequest);
      checkFormRedirect(null, getExpressCheckoutErrorURL(), pRequest, pResponse);
    }

    super.handleExpressCheckout(pRequest, pResponse);

    if (getFormError()) {
      getCheckoutManager().authorizeShippingStep();
      vlogError("handleExpressCheckout: Found form errors");
      getFormExceptionGenerator().generateException(MFFConstants.MSG_EXPRESS_CHECKOUT_ERROR, true, this, pRequest);
      checkFormRedirect(null, getExpressCheckoutErrorURL(), pRequest, pResponse);
      return false;
    }else{
      getCheckoutManager().authorizeReviewStep();
    }

    MFFOrderImpl currentOrder = (MFFOrderImpl) getOrder();

    synchronized (currentOrder) {

      try {
        vlogDebug("handleExpressCheckout: Setting concat email:{0} on the order:{1}", email, currentOrder.getId());
        currentOrder.setContactEmail(email);
        // Update the Order
        ((MFFOrderManager) getOrderManager()).updateOrder(currentOrder, "handleExpressCheckout");

      } catch (CommerceException e) {
        if (isLoggingError()) {
          logError(e);
        }
        getFormExceptionGenerator().generateException(MFFConstants.MSG_EXPRESS_CHECKOUT_ERROR, true, this, pRequest);
        checkFormRedirect(null, getExpressCheckoutErrorURL(), pRequest, pResponse);
        return false;
      }
    }
    
    getCheckoutManager().setExpressCheckout(true);
    return checkFormRedirect(getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(), pRequest, pResponse);
  }

  @Override
  public List<DropletException> getNonFormFieldExceptions() {
    // TODO Auto-generated method stub
    return null;
  }

  public MFFFormExceptionGenerator getFormExceptionGenerator() {
    return mFormExceptionGenerator;
  }

  public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator) {
    mFormExceptionGenerator = pFormExceptionGenerator;
  }

  @Override
  public List<MFFInlineDropletFormException> getFormFieldExceptions() {
    // TODO Auto-generated method stub
    return null;
  }

  public MFFCheckoutManager getCheckoutManager() {
    return mCheckoutManager;
  }

  public void setCheckoutManager(MFFCheckoutManager pCheckoutManager) {
    mCheckoutManager = pCheckoutManager;
  }

}
