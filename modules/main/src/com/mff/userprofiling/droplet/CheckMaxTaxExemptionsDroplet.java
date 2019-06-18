package com.mff.userprofiling.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.mff.constants.MFFConstants;

/**
 * This droplet checks if maximun number of tax exemptions were added to profile and returns a boolean.
 * 
 * @author DMI
 */
public class CheckMaxTaxExemptionsDroplet extends DynamoServlet {

  public static final String OUTPUT_OPARAM = "output";
    
  public static final String OPARAM_TRUE = "true";
	public static final String OPARAM_FALSE = "false";
	private static final String MAX_EXEMPTIONS_REACHED="maxExemptionsReached";
		
	private int mMaxTaxExemptionsAllowed;
	private Profile mProfile;
	
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
																	throws ServletException, IOException {
	  vlogDebug("Entered into CheckMaxTaxExemptionsDroplet");
		Map taxExmptions = (Map) getProfile().getPropertyValue("taxExemptions");
		if (taxExmptions!=null && taxExmptions.keySet().size() >= getMaxTaxExemptionsAllowed()){
			pRequest.setParameter(MAX_EXEMPTIONS_REACHED, OPARAM_TRUE);
		} else {
			pRequest.setParameter(MAX_EXEMPTIONS_REACHED, OPARAM_FALSE);
		}
            
		pRequest.serviceLocalParameter("output", pRequest, pResponse);
		vlogDebug("Exited from CheckMaxTaxExemptionsDroplet");
	}

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		mProfile = pProfile;
	}

	public int getMaxTaxExemptionsAllowed() {
		return mMaxTaxExemptionsAllowed;
	}

	public void setMaxTaxExemptionsAllowed(int pMaxTaxExemptionsAllowed) {
		this.mMaxTaxExemptionsAllowed = pMaxTaxExemptionsAllowed;
	}
}