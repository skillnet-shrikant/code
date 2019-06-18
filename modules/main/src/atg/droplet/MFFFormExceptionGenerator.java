package atg.droplet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;

import com.mff.constants.MFFConstants;
import com.mff.droplet.MFFDropletFormException;
import com.mff.droplet.MFFInlineDropletFormException;

/**
 * Utility class for generating form exceptions.  It is in the 
 * <code>atg.droplet package</code> because it needs to get access to form 
 * field names from the <code>DropletEventServlet</code>.
 * 
 * @author jureth@KnowledgePath
 *
 */
public class MFFFormExceptionGenerator extends GenericService {
	private static final String DARGS = "_DARGS";
	
	// ------------------------------------------
	// CONFIGURED PROPERTIES
	// ------------------------------------------
	private DropletEventServlet mDropletEventServlet;

	
	// ------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------
	
	/**
	 * @param pMessage The error message
	 * @param pPropertyName The JavaBeans property name 
	 * @param pFormHandler Form handler which is generating the exception
	 * @param request
	 * @param addException If true, the generated exception will be added to the form handler
	 * @return A MFFInlineDropletFormException
	 */
	public MFFInlineDropletFormException generateInlineException(String pMessage, List<String> pPropertyNames, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		String messageId = "";
		String message = pMessage;
		//Splitting the message with id and setting only the messageId
		if(null != pMessage && (pMessage.indexOf("|") != -1)){
			messageId = pMessage.substring(0,pMessage.indexOf("|"));
			message = pMessage.substring(1+pMessage.indexOf("|"));
		}
				
		MFFInlineDropletFormException exception = new MFFInlineDropletFormException(message, pPropertyNames, pFormHandler);
		
		exception.setMessageId(messageId);
		
		exception.setFormId(parseFormId(pRequest));
		if (isLoggingDebug()) {
			logDebug("parsed formId: " + exception.getFormId());
		}

		for (String fullPath : exception.getFullPropertyPaths()) {
			// check for a name alias
			if (isLoggingDebug()) {
				logDebug("Checking for explicit name for " + fullPath);
			}
			String explicitName = getExplicitFormElementName(pRequest, fullPath);
			if (explicitName == null) {
				if (isLoggingDebug()) {
					logDebug("Did not find an explicit name, using default");
				}
			} else {
				exception.getExplicitFormElementNames().put(fullPath, explicitName);
				if (isLoggingDebug()) {
					logDebug("Found explicit name " + explicitName);
				}
			}
		}
		
		pFormHandler.addFormException(exception);
		
		return exception;
	}
	
	/**
	 * Create exception for multiple sub-fields.  Assumes all fields in same container.  This is designed to simplify
	 * working with things like the value map on ProfileFormHandler.
	 * @param pMessage
	 * @param pPropertyNames
	 * @param pContainerName
	 * @param pFormHandler
	 * @param request
	 * @param addException
	 * @return
	 */

	public MFFInlineDropletFormException generateInlineException(String pMessage, List<String> pPropertyNames, 
			String pContainerName, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		List<String> fullPropNames = new ArrayList<String>();
		for (String propName : pPropertyNames) {
			fullPropNames.add(pContainerName + "." + propName);
		}

		return generateInlineException(pMessage, fullPropNames, pFormHandler, pRequest);	
	}
	
	/**
	 * Create a field exception
	 * @param pMessage
	 * @param pPropertyName
	 * @param pFormHandler
	 * @param pRequest
	 * @return
	 */
	public MFFInlineDropletFormException generateInlineException(String pMessage, String pPropertyName, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		
		List<String> propNames = new ArrayList<String>();
		propNames.add(pPropertyName);
		return generateInlineException(pMessage, propNames, pFormHandler, pRequest);
	}
	
	public MFFDropletFormException generateException(String pMessage, String pPropertyName, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		return generateInlineException(pMessage,pPropertyName, pFormHandler, pRequest);
	}
	
	/**
	 * Create an generic non form field exception
	 * @param pMessage
	 * @param pFormHandler
	 * @return
	 */
	public MFFDropletFormException generateException(String pMessage, GenericFormHandler pFormHandler) {
		String messageId = "";
		String message = pMessage;

		//Splitting the message with id and setting only the messageId
		if(null != pMessage && (pMessage.indexOf("|") != -1)){
			messageId = pMessage.substring(0,pMessage.indexOf("|"));
			message = pMessage.substring(1+pMessage.indexOf("|"));
		}
				
		MFFDropletFormException exception = new MFFDropletFormException(message, "");
		exception.setMessageId(messageId);
		pFormHandler.addFormException(exception);
		return exception;
	}
	
	/**
	 * Create an generic non form field exception, if isKey then get message from the Resources.properties
	 * @param pMessageOrKey
	 * @param pIsKey
	 * @param pFormHandler
	 * @param pRequest
	 * @return
	 */
	public MFFDropletFormException generateException(String pMessageOrKey, boolean pIsKey, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		String message = pMessageOrKey;
		if (pIsKey){
			message = MFFConstants.getEXTNResources(pRequest).getString(pMessageOrKey);
		}

		return generateException(message,pFormHandler);
	}
	
	/**
	 * @param pMessageOrKey The error message
	 * @param pIsKey
	 * @param pPropertyNames The List of property name 
	 * @param pFormHandler Form handler which is generating the exception
	 * @param pRequest
	 * @param addException If true, the generated exception will be added to the form handler
	 * @return A MFFInlineDropletFormException
	 */
	public MFFInlineDropletFormException generateInlineException(String pMessageOrKey, boolean pIsKey, List<String> pPropertyNames, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		String message = pMessageOrKey;
		if (pIsKey){
			message = MFFConstants.getEXTNResources(pRequest).getString(pMessageOrKey);
		}
		return generateInlineException(message,pPropertyNames,pFormHandler,pRequest);
	}
	
	/**
	 * @param pMessageOrKey The error message
	 * @param pIsKey
	 * @param pPropertyName The JavaBeans property name 
	 * @param pFormHandler Form handler which is generating the exception
	 * @param pRequest
	 * @param addException If true, the generated exception will be added to the form handler
	 * @return A MFFInlineDropletFormException
	 */
	public MFFInlineDropletFormException generateInlineException(String pMessageOrKey, boolean pIsKey, String pPropertyName, GenericFormHandler pFormHandler, DynamoHttpServletRequest pRequest) {
		String message = pMessageOrKey;
		if (pIsKey){
			message = MFFConstants.getEXTNResources(pRequest).getString(pMessageOrKey);
		}
		List<String> propNames = new ArrayList<String>();
		propNames.add(pPropertyName);
		return generateInlineException(message, propNames, pFormHandler, pRequest);
	}

	/**
	 * Attempt to extract the formId attribute from the _DARGS param.  This will only work 
	 * if the formid attribute is actually set on the form.
	 * 
	 * @param pRequest
	 * @return
	 */
	private String parseFormId(DynamoHttpServletRequest pRequest) {
		String dargsParam = pRequest.getParameter(DARGS);
		if (dargsParam == null) {
			return null;
		}

		FormTag sender = (FormTag) getDropletEventServlet().getEventSender(dargsParam);
		if (sender == null || sender.mFormId == null) {
			return null;
		}

		// the formId value contains the full _DARGS param.  We want to parse for just the formId attribute part.
		final String jspString = ".jsp.";
		String fullFormId = sender.mFormId;
		int idx = fullFormId.indexOf(jspString);
		String formId = null;
		if (idx != -1 && fullFormId.length() > (idx + jspString.length())) {
			formId = fullFormId.substring(idx + jspString.length());
		}

		return formId;
	}	
	
	/**
	 * Returns a form element name that was explicitly specified in the form rather than generated by ATG.
	 * Returns null if the form element name was in fact generated by ATG.
	 * 
	 * @param request
	 * @param fullPropertyPath
	 * @return
	 */
	public String getExplicitFormElementName(DynamoHttpServletRequest pRequest, String pFullPropertyPath) {
		if (pRequest == null || pFullPropertyPath == null) {
			return null;
		}

		// get the form identifier
		String dargsParam = pRequest.getParameter(DARGS);
		if (dargsParam == null) {
			return null;
		}

		EventSender sender = getDropletEventServlet().getEventSender(dargsParam);
		if (sender == null) {
			return null;
		}

		@SuppressWarnings("rawtypes")
		Enumeration receivers = sender.getEventReceivers();
		while (receivers.hasMoreElements()) {
			EventReceiver receiver = (EventReceiver) receivers.nextElement();
			if (pFullPropertyPath.equals(receiver.getPropertyPath())) {
				if (pFullPropertyPath.equals(receiver.getName())) {
					return null;
				} else {
					return receiver.getName();
				}
			}
		}

		return null;
	}


	/**
	 * Given a list of form exceptions, return those that are MFFInlineDropletFormException
	 * and that have at least one form element associated.
	 * 
	 * @param formExceptions
	 * @return
	 */
	public List<MFFInlineDropletFormException> getFormFieldExceptions(List<?> pFormExceptions) {
		List<MFFInlineDropletFormException> formFieldExceptions = new ArrayList<MFFInlineDropletFormException>();
		if (pFormExceptions != null) {
			Iterator<?> iter = pFormExceptions.iterator();
			while (iter.hasNext()) {
				Object error = iter.next();
				if (error instanceof MFFInlineDropletFormException) {
					MFFInlineDropletFormException rhdfe = (MFFInlineDropletFormException) error;
					if (rhdfe.getFormElementNames() != null && !rhdfe.getFormElementNames().isEmpty()) {
						formFieldExceptions.add(rhdfe);
					}
				}
			}
		}

		return formFieldExceptions;
	}

	/**
	 * given a list of form exceptions, returns all those that are not a MFFInlineDropletFormException,
	 * and that are MFFDropletFormException
	 * 
	 * @param formExceptions
	 * @return
	 */
	public List<DropletException> getNonFormFieldExceptions(List<?> pFormExceptions) {
		List<DropletException> nonFormFieldExceptions = new ArrayList<DropletException>();

		if (pFormExceptions != null) {
			Iterator<?> iter = pFormExceptions.iterator();
			while (iter.hasNext()) {
				Object error = iter.next();
				if (!(error instanceof MFFInlineDropletFormException) && (error instanceof DropletException)) {
					nonFormFieldExceptions.add((DropletException)error);					
				}
			}
		}

		return nonFormFieldExceptions;
	}




	
	// ------------------------------------------
	// GETTERS AND SETTERS
	// ------------------------------------------
	public DropletEventServlet getDropletEventServlet() {
		return mDropletEventServlet;
	}

	public void setDropletEventServlet(DropletEventServlet pDropletEventServlet) {
		mDropletEventServlet = pDropletEventServlet;
	}

}
