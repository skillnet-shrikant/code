package com.mff.droplet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;


/**
 * This Droplet extends the GsonSupportDroplet to create the JSON structure for error messages
 *
 */
public class JSONFormExceptionDroplet extends GsonSupportDroplet {
	
	//------------------------------------------
	// PRIVATE VARIABLES
	// ------------------------------------------
	private static final ParameterName FORM_EXCEPTIONS = ParameterName.getParameterName("formExceptions");
	private static final String UNKNOWN_FORM_ID = "unknown";

	/**
	 * Create the JSON structure we want for error messages.
	 */
	@Override
	protected Object createObjectForRender(DynamoHttpServletRequest pRequest)
			throws IllegalArgumentException {
		Collection<MFFInlineDropletFormException> formExceptions = 
				(Collection<MFFInlineDropletFormException>) pRequest.getObjectParameter(FORM_EXCEPTIONS);
		if (formExceptions == null) {
			throw new IllegalArgumentException("formExceptions is a required parameter");
		}
		if (formExceptions.isEmpty()) {
			return null;
		}
		
		String formId = null;
		Structure structure = new Structure();
		Map<String, List<String>> fieldsWithErrors = new HashMap<String, List<String>>();
		structure.setFieldsWithErrors(fieldsWithErrors);
		
		for (MFFInlineDropletFormException exc : formExceptions) {
			if (isLoggingDebug()) {
				logDebug("using exception: " + exc);
			}
			if (formId == null) {
				if (!StringUtils.isBlank(exc.getFormId())) {
					formId = exc.getFormId();
				} else {
					formId = UNKNOWN_FORM_ID;
				}
			} else {
				if (!formId.equals(exc.getFormId()) && isLoggingWarning()) {
					logWarning("Found unexpected additional formId: " + exc.getFormId() + ", already had (and keeping): " + formId);
				}
			}

			for (String fieldName : exc.getFormElementNames()) {
				if (isLoggingDebug()) {
					logDebug("handling field name: " + fieldName);
				}
				List<String> errorMessagesForField = fieldsWithErrors.get(fieldName);
				if (errorMessagesForField == null) {
					errorMessagesForField = new ArrayList<String>();
					fieldsWithErrors.put(fieldName, errorMessagesForField);
				}
				if (isLoggingDebug()) {
					logDebug("adding " + exc.getMessage() + " to errorMessagesForField, field: " + fieldName);
				}
				errorMessagesForField.add(exc.getMessage());
			}
		}
		
		structure.setFormId(formId);
		
		if (isLoggingDebug()) {
			logDebug("Created structure: " + structure);
		}
		
		return structure;
	}
	
	/**
	 * Inner Class used by the createObjectForRender method
	 *
	 */
	public static class Structure {
		private String formId;
		private Map<String, List<String>> fieldsWithErrors;
		
		public String getFormId() {
			return formId;
		}
		public void setFormId(String pFormId) {
			formId = pFormId;
		}
		public Map<String, List<String>> getFieldsWithErrors() {
			return fieldsWithErrors;
		}
		public void setFieldsWithErrors(Map<String, List<String>> pFieldsWithErrors) {
			fieldsWithErrors = pFieldsWithErrors;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("formId: ").append(getFormId()).append("; ");
			
			if (getFieldsWithErrors() != null) {
				for (Map.Entry<String, List<String>> entry : getFieldsWithErrors().entrySet()) {
					builder.append(entry.getKey()).append(": ");
					Iterator<String> messageIter = entry.getValue().iterator();
					while (messageIter.hasNext()) {
						builder.append(messageIter.next());
						if (messageIter.hasNext()) {
							builder.append(',');
						}
					}
				}
			}
			
			return builder.toString();
		}
	}
}
