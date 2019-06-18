package com.mff.browse;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;
import com.mff.userprofiling.MFFProfileTools;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * This class is used to submit the users request to be notified when an item is back in stock.
 *
 * @author DMI
 */
public class MFFBackInStockFormHandler extends GenericFormHandler {

	private MFFProfileTools mProfileTools;
	private TransactionManager mTransactionManager;

	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	/**
	 * @param pTransactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * @return the profileTools
	 */
	public MFFProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param pProfileTools the profileTools to set
	 */
	public void setProfileTools(MFFProfileTools pProfileTools) {
		mProfileTools = pProfileTools;
	}

	/**
	 * property: catalogRefId
	 */
	private String mCatalogRefId;

	/**
	 * @return the catalog reference id.
	 */
	public String getCatalogRefId() {
		return mCatalogRefId;
	}

	/**
	 * @param pCatalogRefId - the catalog reference id.
	 */
	public void setCatalogRefId(String pCatalogRefId) {
		mCatalogRefId = pCatalogRefId;
	}

	/**
	 * property: emailAddress.
	 */
	private String mEmailAddress;

	/**
	 * @return the e-mail address.
	 */
	public String getEmailAddress() {
		return mEmailAddress;
	}

	/**
	 * @param pEmailAddress - the e-mail address to set.
	 */
	public void setEmailAddress(String pEmailAddress) {
		mEmailAddress = pEmailAddress;
	}

	/**
	 * property: productId
	 */
	private String mProductId;

	/**
	 * @return the product id.
	 */
	public String getProductId() {
		return mProductId;
	}

	/**
	 * @param pProductId - the product id to set.
	 */
	public void setProductId(String pProductId) {
		mProductId = pProductId;
	}

	/**
	 * property: profileRepository.
	 */
	private MutableRepository mProfileRepository;

	/**
	 * @return the profile repository.
	 */
	public MutableRepository getProfileRepository() {
		return mProfileRepository;
	}

	/**
	 * @param pProfileRepository - the profile repository to set.
	 */
	public void setProfileRepository(MutableRepository pProfileRepository) {
		mProfileRepository = pProfileRepository;
	}

	/**
	 * property: successURL.
	 */
	private String mSuccessURL;

	/**
	 * @return the success redirect URL.
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}

	/**
	 * @param pSuccessURL - the success redirect URL to set.
	 */
	public void setSuccessURL(String pSuccessURL) {
		mSuccessURL = pSuccessURL;
	}

	/**
	 * property: errorURL.
	 */
	private String mErrorURL;

	/**
	 * @return the error redirect URL.
	 */
	public String getErrorURL() {
		return mErrorURL;
	}

	/**
	 * @param pErrorURL - the error redirect URL to set.
	 */
	public void setErrorURL(String pErrorURL) {
		mErrorURL = pErrorURL;
	}


	/** 
	 * property: inventoryManager
	 */
	private FFRepositoryInventoryManager mInventoryManager;

	/**
	 * @return the StoreInventoryManager.
	 */
	public FFRepositoryInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param pInventoryManager - the StoreInventoryManager to set
	 */
	public void setInventoryManager(FFRepositoryInventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}


	/**
	 * This method will handle "notify when back in stock" requests.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 * @return
	 */
	public boolean handleNotifyMe(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		if (StringUtils.isBlank(getCatalogRefId())) {
			vlogDebug("SkuId is null. backInStockNotifyItem was not created.");
			addFormException(new DropletException("SkuId is not available"));
		}

		if (StringUtils.isBlank(getProductId())) {
			vlogDebug("productId is null. backInStockNotifyItem was not created.");
			addFormException(new DropletException("ProductId is not available"));
		}

		if (StringUtils.isBlank(getEmailAddress()) || !getProfileTools().validateEmailAddress(getEmailAddress())) {
			vlogDebug("User email Addredd :: "+getEmailAddress());
			addFormException(new DropletFormException("Invalid email Address", null));
		}

		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();

		try {
			if (tm != null){
				td.begin(tm, 3);
			}
			boolean alreadyExists = getInventoryManager().isBackInStockItemExists(
					getProfileRepository(), getCatalogRefId(), getEmailAddress(), getProductId());

			if (alreadyExists) {
				vlogDebug("backInStockNotifyItem already exists for this combination of catalogRefId, email and productId.");
				return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
			}

			getInventoryManager().createBackInStockNotifyItem(
					getProfileRepository(), getCatalogRefId(), getEmailAddress(), getProductId(), SiteContextManager.getCurrentSiteId());
		} 
		catch (RepositoryException ex) {
			vlogError("RepositoryException occurred :: "+ex.getMessage());
		} catch (TransactionDemarcationException e) {
			vlogError("TransactionDemarcationException occurred :: "+e.getMessage());
		} finally {
			try {
				if (tm != null){
					td.end();
				}
			} catch (TransactionDemarcationException e) {
					vlogDebug("Ignoring exception", e);
			}
		}
		return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
	}

}
