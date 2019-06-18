package com.reports.dashboard;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import atg.commerce.order.ChangedProperties;
import atg.commerce.order.Constants;
import atg.repository.MutableRepositoryItem;

public class PerformanceStatsItem implements ChangedProperties{

	private MutableRepositoryItem mRepositoryItem;
	
	private boolean mSaveAllProperties = false;

	private boolean mChanged = false;

	private HashSet mChangedProperties = null;
	
	public static final String ID        = "id"; 
	public static final String STORE_ID				= "storeId"; 

	public MutableRepositoryItem getRepositoryItem() {
		return this.mRepositoryItem;
	}

	public void setRepositoryItem(MutableRepositoryItem pRepositoryItem) {
		this.mRepositoryItem = pRepositoryItem;
	}
	public String getId() {
    return ((String) getPropertyValue(ID));
  }
	
	public void setId(String pId) {
    setPropertyValue(ID, pId);
  }


	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return ((String) getPropertyValue(STORE_ID));
	}

	/**
	 * @param pChangedProperties the changedProperties to set
	 */
	public void setChangedProperties(HashSet pChangedProperties) {
		mChangedProperties = pChangedProperties;
	}

	/**
	 * @param pStoreId the storeId to set
	 */
	public void setStoreId(String pStoreId) {
		setPropertyValue(STORE_ID, pStoreId);
	}

	public Object getPropertyValue(String pPropertyName) {
		MutableRepositoryItem mutItem = getRepositoryItem();
		if (mutItem == null) {
			throw new RuntimeException(MessageFormat.format(
					Constants.NULL_REPITEM_IN_COMMERCEITEM,
					new Object[] {  }));
		}
		return mutItem.getPropertyValue(pPropertyName);
	}

	public void setPropertyValue(String pPropertyName, Object pPropertyValue) {
		MutableRepositoryItem mutItem = getRepositoryItem();

		if (mutItem == null) {
			throw new RuntimeException(MessageFormat.format(
					Constants.NULL_REPITEM_IN_COMMERCEITEM,
					new Object[] {  }));
		}
		mutItem.setPropertyValue(pPropertyName, pPropertyValue);
		setChanged(true);
	}

	@Override
	public void update(Observable pO, Object pArg) {
		if (pArg instanceof String)
			addChangedProperty((String) pArg);
		else
			throw new RuntimeException("Observable update for "
					+ super.getClass().getName()
					+ " was received with arg type " + pArg.getClass().getName()
					+ ":" + pArg);
		
	}

	@Override
	public boolean getSaveAllProperties() {
		return this.mSaveAllProperties;
	}

	@Override
	public void setSaveAllProperties(boolean pSaveAllProperties) {
		this.mSaveAllProperties = pSaveAllProperties;
	}

	@Override
	public boolean isChanged() {
		return ((this.mChanged) || ((this.mChangedProperties != null) && (!(getChangedProperties()
				.isEmpty()))));
	}

	@Override
	public void setChanged(boolean pChanged) {
		this.mChanged = pChanged;
		
	}

	@Override
	public Set getChangedProperties() {
		if (this.mChangedProperties == null)
			this.mChangedProperties = new HashSet(7);
		return this.mChangedProperties;
	}

	@Override
	public void addChangedProperty(String pPropertyName) {
		getChangedProperties().add(pPropertyName);
	}

	@Override
	public void clearChangedProperties() {
		if (this.mChangedProperties == null)
			return;
		this.mChangedProperties.clear();
	}
}
