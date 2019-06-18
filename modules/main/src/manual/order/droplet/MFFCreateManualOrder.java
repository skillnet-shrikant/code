package manual.order.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import manual.order.ManualOrderCreator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class MFFCreateManualOrder extends DynamoServlet {
	
	private static final String OUTPUT_OPARAM = "output";
	private static final String CONTENTS_OUTPUT = "orderId";
	
	private ManualOrderCreator mManualOrderCreator;
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		if (isLoggingDebug()){
			logDebug("MFFCreateManualOrder: Called.");
		}
		
		String orderId = getManualOrderCreator().createManualOrder();
		
		pRequest.setParameter(CONTENTS_OUTPUT, orderId);
		pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
	}

	public ManualOrderCreator getManualOrderCreator() {
		return mManualOrderCreator;
	}

	public void setManualOrderCreator(ManualOrderCreator pManualOrderCreator) {
		this.mManualOrderCreator = pManualOrderCreator;
	}

}