package com.listrak.service.exception;

import java.text.MessageFormat;
import com.listrak.service.constants.ListrakConstants;

import atg.core.exception.ContainerException;

public class ListrakException extends ContainerException {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String CLASS_VERSION = "$Id$ $Change$";

	private String mKey;

	private Object[] mParams;

	private boolean mParamsAsKeys;

	public boolean isParamsAsKeys() {
		return mParamsAsKeys;
	}

	public ListrakException(String pKey) {
		mKey = pKey;
	}

	public Object getParams() {
		return mParams;
	}

	public void setParams(Object... pParams) {
		mParams = pParams;
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String pKey) {
		mKey = pKey;
	}

	public ListrakException() {

	}

	public ListrakException(String pKey, boolean pParamsAsKeys, Object... pParams) {
		mKey = pKey;
		mParams = pParams;
		mParamsAsKeys = pParamsAsKeys;
	}


	public ListrakException(Throwable pSourceException) {
		super(pSourceException);
	}

	public ListrakException(String pStr, Throwable pSourceException) {
		super(pStr, pSourceException);
	}

	@Override
	public String getMessage() {
		if (mKey == null) {
			return super.getMessage();
		} else {
			if (mParams == null) {
				return ListrakConstants.getResources().getString(mKey);
			} else {
				String standardDetails=ListrakConstants.getResources().getString(mKey);
				String standardTemplate=MessageFormat.format(standardDetails, mParams);
				return standardTemplate;
			}
		}
	}

	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage();
	}

	
	

}
