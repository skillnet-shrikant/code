/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.allocation.item;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import atg.commerce.order.ChangedProperties;
import atg.repository.MutableRepositoryItem;

public class AllocationItem 
	implements ChangedProperties {
	
    private boolean mSaveAllProperties;
    private boolean mChanged;
    private HashSet mChangedProperties;
    private MutableRepositoryItem mRepositoryItem;

    public MutableRepositoryItem getRepositoryItem()   {
		return mRepositoryItem;
    }
    
	public void setRepositoryItem(MutableRepositoryItem pRepositoryItem)  {
		mRepositoryItem = pRepositoryItem;
    }
	
    public Object getPropertyValue(String pPropertyName)  {
		MutableRepositoryItem lMutableItem = getRepositoryItem();
        if(lMutableItem == null)
			throw new RuntimeException("Unable to get value for property " + pPropertyName);
        else
            return lMutableItem.getPropertyValue(pPropertyName);
    }

    public void setPropertyValue(String pPropertyName, Object pPropertyValue)  {
		MutableRepositoryItem lMutableItem = getRepositoryItem();
        if(lMutableItem == null) {
			throw new RuntimeException("Unable to set property " + pPropertyName + " to value " + pPropertyValue);
        } 
        else {
        	lMutableItem.setPropertyValue(pPropertyName, pPropertyValue);
            setChanged(true);
            return;
        }
    }

    public void update(Observable o, Object arg)  {
    	if(arg instanceof String)
    		addChangedProperty((String)arg);
    	else
    		throw new RuntimeException((new StringBuilder()).append("Observable update for ").append(getClass().getName()).append(" was received with arg type ").append(arg.getClass().getName()).append(":").append(arg).toString());
    }

    public boolean getSaveAllProperties()  {
    	return mSaveAllProperties;
    }

    public void setSaveAllProperties(boolean pSaveAllProperties)  {
    	mSaveAllProperties = pSaveAllProperties;
    }

    public boolean isChanged()  {
    	return mChanged || mChangedProperties != null && !getChangedProperties().isEmpty();
    }

    public void setChanged(boolean pChanged)  {
    	mChanged = pChanged;
    }

    public Set getChangedProperties()  {
    	if(mChangedProperties == null)
    		mChangedProperties = new HashSet(7);
    	return mChangedProperties;
    }

    public void addChangedProperty(String pPropertyName)  {
    	getChangedProperties().add(pPropertyName);
    }

    public void clearChangedProperties()  {
    	if(mChangedProperties == null)  {
    		return;
        } 
    	else   {
    		mChangedProperties.clear();
    		return;
        }
    }
}