package com.mff.droplet;

import atg.droplet.DropletFormException;
/**
 * This is the generic form field exception extended for for handling form errors which 
 * are not associated to any form fields and adding support for messageId.
 *  
 * @author jureth@Knowledgepath
 *
 */
public class MFFDropletFormException extends DropletFormException{

	private static final long serialVersionUID = 4286237539423659454L;
	
	private String messageId = "";	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public MFFDropletFormException(String pStr, String pPath) {
		super(pStr, pPath);
	}
	
	public MFFDropletFormException(String pStr, String pPath, String mMessageCode) {
		super(pStr, pPath, mMessageCode);
	}	
}
