package com.aci.pipeline.exception;

import java.util.Locale;

import com.aci.utils.AciUtils;

import atg.core.exception.ContainerException;

public class AciPipelineException extends ContainerException {
	
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

	public AciPipelineException(String pKey) {
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

	public AciPipelineException() {

	}

	public AciPipelineException(String pKey, boolean pParamsAsKeys, Object... pParams) {
		mKey = pKey;
		mParams = pParams;
		mParamsAsKeys = pParamsAsKeys;
	}


	public AciPipelineException(Throwable pSourceException) {
		super(pSourceException);
	}

	public AciPipelineException(String pStr, Throwable pSourceException) {
		super(pStr, pSourceException);
	}

	@Override
	public String getMessage() {
		if (mKey == null) {
			return super.getMessage();
		} else {
			if (mParams == null) {
				return AciUtils.getString(mKey);
			} else {
				if (mParamsAsKeys) return AciUtils.format(mKey, localizeParams(Locale.getDefault(), mParams));
				return AciUtils.format(mKey, mParams);
			}
		}
	}

	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage();
	}

	private Object[] localizeParams(Locale pLocale, Object[] pParams) {
		Object[] localizedParams = new Object[pParams.length];
		for (int i = 0; i < pParams.length; ++i)
			localizedParams[i] = AciUtils.getString((String) pParams[i], pLocale);

		return localizedParams;
	}

}
