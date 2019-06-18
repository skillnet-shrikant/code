package com.mff.droplet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.constants.MFFConstants;

/**
 * This class is used to get the Expiry years from current year
 * @author MastanReddy
 *
 */
public class MFFExpiryYearDroplet extends DynamoServlet {
	
	private int mNoOfYears;

	/**
	 * @return the noOfYears
	 */
	public int getNoOfYears() {
		return mNoOfYears;
	}

	/**
	 * @param pNoOfYears the noOfYears to set
	 */
	public void setNoOfYears(int pNoOfYears) {
		mNoOfYears = pNoOfYears;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String type = pRequest.getParameter("type");
		if(type.equals("month")){
			for (int i = 01; i <= 12; i++) {
				if(i < 10){
					pRequest.setParameter("ExpMonth", "0"+Integer.toString(i));
				} else {
					pRequest.setParameter("ExpMonth", Integer.toString(i));
				}
				pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
			}
		} else if(type.equals("year")){
			
			Calendar calender = Calendar.getInstance();
			int currentYear = calender.get(Calendar.YEAR);
			
			for (int i = currentYear; i <= currentYear + getNoOfYears(); i++) {
				pRequest.setParameter("ExpYear", Integer.toString(i));
				pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
			}
		}
	}
}
