package com.mff.droplet;

import java.util.List;

import atg.droplet.DropletException;
import atg.droplet.MFFFormExceptionGenerator;

/**
 * Interface that defines methods that support displaying form errors inline with the form.
 *
 */
public interface InlineFormErrorSupport {
	/**
	 * 
	 * @return errors that are associated with a particular form field, and have form field information 
	 * available
	 */
	public List<MFFInlineDropletFormException> getFormFieldExceptions();
	
	/**
	 * 
	 * @return errors that are not associated with any particular form field
	 */
	public List<DropletException> getNonFormFieldExceptions();
	
	
	/**
	 * Mandating the use of FormExceptionGenerator for generating errors. 
	 * The implementation should add the property 'formExceptionGenerator' to the component of custom form handler
	 * 
	 * @return
	 */
	public MFFFormExceptionGenerator getFormExceptionGenerator();
	public void setFormExceptionGenerator(MFFFormExceptionGenerator pFormExceptionGenerator);
}