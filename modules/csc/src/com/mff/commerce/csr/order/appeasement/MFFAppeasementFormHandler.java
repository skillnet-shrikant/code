package com.mff.commerce.csr.order.appeasement;

import java.io.IOException;

import javax.servlet.ServletException;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.email.MFFEmailManager;

import atg.commerce.CommerceException;
import atg.commerce.csr.appeasement.Appeasement;
import atg.commerce.csr.order.appeasement.AppeasementFormHandler;
import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 *
 */
public class MFFAppeasementFormHandler extends AppeasementFormHandler {

  /* (non-Javadoc)
   * @see atg.commerce.csr.order.appeasement.AppeasementFormHandler#applyAppeasementRefunds(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   */

	private MFFEmailManager mEmailManager;
	
  public MFFEmailManager getEmailManager() {
		return mEmailManager;
	}

	public void setEmailManager(MFFEmailManager pEmailManager) {
		mEmailManager = pEmailManager;
	}

@Override
  public void applyAppeasementRefunds(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
    Appeasement appeasement;
    appeasement = getAppeasement();
    MFFAppeasementManager appeasementManager = (MFFAppeasementManager) getAppeasementManager();
    if(isLoggingDebug())
        logDebug((new StringBuilder()).append("applyAppeasementRefunds begins for order:").append(getOrder().getId()).toString());
    if(appeasement == null || appeasement.getAppeasementType() == null || appeasement.getAppeasementAmount() == 0.0D || getOrder() == null)
    {
        addFormException(new DropletException(getAppeasementManager().getUserLocalizedResource("noAppeasementExists", null, getUserLocale())));
        return;
    }
    try
    {
        if(!appeasement.getAppeasementType().equalsIgnoreCase("taxes"))
        {
            if(appeasement.getAppeasementAmount() > getAppeasementManager().calculateRefundAdjustedTotal(getOrder().getId(), appeasement.getAppeasementType()))
            {
                addFormException(new DropletException(getAppeasementManager().getUserLocalizedResource("amountExceedsAvailableBalance", null, getUserLocale())));
                return;
            }
        }
        else
        {
          if(appeasement.getAppeasementAmount() > appeasementManager.calculateTaxesRefundAdjustedTotal(getOrder().getId(), appeasement.getAppeasementType()))
          {
              addFormException(new DropletException(getAppeasementManager().getUserLocalizedResource("amountExceedsAvailableBalance", null, getUserLocale())));
              return;
          }
        }
    }
    catch(CommerceException e)
    {
        if(isLoggingError())
            logError((new StringBuilder()).append("Error occurred in calculating the available balance amounts for order :").append(getOrder().getId()).toString(), e);
        String msg = getAppeasementManager().getUserLocalizedResource("pipelineErrorInitializingRefundAmts", null, getUserLocale());
        addFormException(new DropletException(msg));
        return;
    }
    catch(RepositoryException e)
    {
        if(isLoggingError())
            logError((new StringBuilder()).append("Error occurred in retrieving appeasement properties for order :").append(getOrder().getId()).toString(), e);
        String msg = getAppeasementManager().getUserLocalizedResource("pipelineErrorInitializingRefundAmts", null, getUserLocale());
        addFormException(new DropletException(msg));
        return;
    }
    try
    {
        PipelineResult result = getAppeasementManager().applyRefundAmounts(appeasement, null);
        if(result.hasErrors())
            processPipelineErrors(result);
        if(isLoggingDebug())
            logDebug((new StringBuilder()).append("Apply appeasement refunds ends for order :").append(getOrder().getId()).toString());
    }
    catch(CommerceException e)
    {
        if(isLoggingError())
            logError((new StringBuilder()).append("Apply appeasement refunds failed for order :").append(getOrder().getId()).toString(), e);
        String msg = getAppeasementManager().getUserLocalizedResource("pipelineErrorInitializingRefundAmts", null, getUserLocale());
        addFormException(new DropletException(msg));
        return;
    }
    return;
  }
  
  @Override
  public void sendConfirmationMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  {
	  if(isLoggingDebug())
		  logDebug((new StringBuilder()).append("sendConfirmationMessage begins for order:").append(getOrder().getId()).toString());
	  	MFFOrderImpl mffOrder=(MFFOrderImpl)getAppeasement().getOriginatingOrder();
	  		try
      {			
	  			getEmailManager().sendAppeasementEmail(mffOrder, getAppeasement(), getOverrideEmailAddress());
	  			if(isLoggingDebug())
	  				logDebug((new StringBuilder()).append("Appeasement confirmation message has been successfully sent for order :").append(getOrder().getId()).toString());
      }
	  		catch(Exception e)
      {
	  			if(isLoggingError())
	  				logError((new StringBuilder()).append(" Failed to send appeasement confirmation message for order :").append(getOrder().getId()).toString(), e);
	  				String msg = getAppeasementManager().getUserLocalizedResource("confirmationMessageFailed", null, getUserLocale());
	  				addFormException(new DropletException(msg));
	  				return;
      }
  }

}
