package com.mff.droplet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class BopisDeadlineDateDroplet extends DynamoServlet {

	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

		Timestamp pickUpSetDate = (Timestamp) pReq.getObjectParameter("pickReadySentDate");

		vlogDebug("service: pickUpSetDate: " + pickUpSetDate);
		Calendar c = Calendar.getInstance();

		if (pickUpSetDate != null) {
			Date pickUpDate = new Date(pickUpSetDate.getTime());

			c.setTime(pickUpDate); // Now use pickup mail sent date.

		} else {
			c.setTime(new Date()); // Now use today date.
		}
		c.add(Calendar.DATE, 2);

		Date lDate = c.getTime();

		vlogDebug("service: pickUpDate lDate: " + lDate);

		pReq.setParameter("finalDate", lDate);
		pReq.serviceParameter("output", pReq, pRes);
	}
}
