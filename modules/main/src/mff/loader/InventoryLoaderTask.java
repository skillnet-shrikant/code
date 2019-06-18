package mff.loader;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mff.commerce.inventory.FFRepositoryInventoryManager;

import atg.commerce.inventory.InventoryException;
import mff.util.FileUtil;

/**
 * Class provides methods for handling import of CSV feed files
 *  - Optionally pull down feeds from the FTP server
 *  - Process the feeds by invoking a DB stored proc
 *  
 *  Currenly this is being used to process price and inventory feeds
 *  
 * @author KnowledgePath
 *
 */
public class InventoryLoaderTask extends FeedLoaderTask {
	
	@Override
	protected void processImport(String pStoredProc, String pInputDir) {
		super.processImport(pStoredProc, pInputDir);
		
		if(isLoggingDebug()) {
			logDebug("Fire Update Inventory Messages " + isSendUpdateInventoryMessages());
		}
		if(isSendUpdateInventoryMessages()) {
			fireInventoryUpdateMessage();
		} 
	
	}

	@Override
	public void doTask() {
	  super.doTask();
	  if(isFileTriggerEnabled() && !isDeltaFeed()) {
	    try {
        generateReadyFile();
      } catch (IOException e) {
        logError("Error generating ready file",e);
      }
	  }
	}
	
	public void generateReadyFile() throws IOException {
    String lFileNameWithPath = getReadyFileDir().endsWith("/")?getReadyFileDir()+getReadyFileName():getReadyFileDir()+"/"+getReadyFileName();
    FileUtil.createEmptyFile(lFileNameWithPath);
  }

  // Determine if this is a full inventory feed or delta
	private boolean deltaFeed;

	// flag to determine if damaged flags are to be rest
	private boolean resetDamaged;
	
	private FFRepositoryInventoryManager inventoryManager;
	
	private boolean sendUpdateInventoryMessages;
	
	private String[] testSkuIds;
	
	private String backInStockQuery;
	
	private String defaultLocationId;
	
	private boolean mFileTriggerEnabled;
	
	private String mReadyFileDir;
	
	private String mReadyFileName;
	
	private String invAdjustmentProcName;
	
  public String getDefaultLocationId() {
		return defaultLocationId;
	}

	public void setDefaultLocationId(String defaultLocationId) {
		this.defaultLocationId = defaultLocationId;
	}

	public String getBackInStockQuery() {
		return backInStockQuery;
	}

	public void setBackInStockQuery(String backInStockQuery) {
		this.backInStockQuery = backInStockQuery;
	}

	public String[] getTestSkuIds() {
		return testSkuIds;
	}

	public void setTestSkuIds(String[] testSkuIds) {
		this.testSkuIds = testSkuIds;
	}

	public boolean isSendUpdateInventoryMessages() {
		return sendUpdateInventoryMessages;
	}

	public void setSendUpdateInventoryMessages(boolean sendUpdateInventoryMessages) {
		this.sendUpdateInventoryMessages = sendUpdateInventoryMessages;
	}

	public FFRepositoryInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(FFRepositoryInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public boolean isDeltaFeed() {
		return deltaFeed;
	}

	public void setDeltaFeed(boolean deltaFeed) {
		this.deltaFeed = deltaFeed;
	}

	public boolean isResetDamaged() {
		return resetDamaged;
	}

	public void setResetDamaged(boolean resetDamaged) {
		this.resetDamaged = resetDamaged;
	}
	
  public boolean isFileTriggerEnabled() {
    return mFileTriggerEnabled;
  }

  public void setFileTriggerEnabled(boolean pFileTriggerEnabled) {
    mFileTriggerEnabled = pFileTriggerEnabled;
  }

  public String getReadyFileDir() {
    return mReadyFileDir;
  }

  public void setReadyFileDir(String pReadyFileDir) {
    mReadyFileDir = pReadyFileDir;
  }

  public String getReadyFileName() {
    return mReadyFileName;
  }

  public void setReadyFileName(String pReadyFileName) {
    mReadyFileName = pReadyFileName;
  }

	public String getInvAdjustmentProcName() {
		return invAdjustmentProcName;
	}

	public void setInvAdjustmentProcName(String pInvAdjustmentProcName) {
		invAdjustmentProcName = pInvAdjustmentProcName;
	}  
	/*
	 * Constructs callableStatement for importing feeds
	 */
	public CallableStatement getCallableStmt() throws SQLException {
		if(isLoggingInfo()) {
			logInfo(getTaskName() + " creating callable statement - "
					+ "proc name is " + getStoredProcName()
					+ "passing param " + getLocalDir());
		}
		
		CallableStatement cs=null;
		
		if(getStoredProcName() != null && !getStoredProcName().equalsIgnoreCase("")) {
			cs = conn.prepareCall(getStoredProcName());
			
			cs.setString(1, getLocalDir());
			cs.setInt(2, (isDeltaFeed() ? 1:0));
			cs.setInt(3, (isResetDamaged() ? 1:0));
		} else if(getTransStoredProcName() != null && ! getTransStoredProcName().equalsIgnoreCase("")) {
			cs = conn.prepareCall(getTransStoredProcName());
		} else if (getResetShippedProcName() != null && ! getResetShippedProcName().equalsIgnoreCase("")) {
			cs = conn.prepareCall(getResetShippedProcName());
		} else if(getInvAdjustmentProcName() != null && !getInvAdjustmentProcName().equalsIgnoreCase("")) {
			cs = conn.prepareCall(getInvAdjustmentProcName());
		}

		return cs;
	}

	public void forceBackInStockNotifications() {
		fireInventoryUpdateMessage();
	}
	
	public void sendTestUpdateInventoryMsgs() {
		ArrayList<String> backInStockSkuIds = new ArrayList<String>();
		if(getTestSkuIds() != null && getTestSkuIds().length > 0) {
			try {
				for(int i=0; i < getTestSkuIds().length; i ++) {
					if(isLoggingDebug()) {
						logDebug("Adding skuId --" + getTestSkuIds()[i] + "--");
					}
					backInStockSkuIds.add(getTestSkuIds()[i]);
				}
				if(isLoggingDebug()) {
					logDebug("Firing updateInventory message");
				}
				getInventoryManager().inventoryWasUpdated(backInStockSkuIds,getDefaultLocationId());

			} catch (InventoryException e) {
				logError(e);
			}		
		} else {
			if(isLoggingDebug()) {
				logDebug("testSkuIds is not defined");
			}
		}
	}
	
	private void fireInventoryUpdateMessage() {
		ArrayList<String> backInStockSkuIds = getBackInStockSkus();
		try {
			if(backInStockSkuIds != null && backInStockSkuIds.size() > 0) {
				getInventoryManager().inventoryWasUpdated(backInStockSkuIds,getDefaultLocationId());
			}
			
		} catch (InventoryException e) {
			logError(e);
		}
	}
	
	private ArrayList<String> getBackInStockSkus() {
		ArrayList<String> backInStockSkuIds = new ArrayList<String>();
		
		Statement stmt = null;
		try {
			Connection conn = getFeedDataSource().getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getBackInStockQuery());
			while(rs.next()) {
				backInStockSkuIds.add(rs.getString(1));
			}
			if(isLoggingDebug())
				logDebug("There are "+(backInStockSkuIds != null ? backInStockSkuIds.size() : 0)+" items in the result set");
			rs.close();
		}
		catch ( SQLException e) {
			logError("Couldn't execute item query "+ getBackInStockQuery(), e);
		}
		finally {
			try {
				if ( stmt != null) stmt.close();
			}
			catch ( SQLException e) {
				logError("There was a problem closing the connection to catfeed", e);
				return backInStockSkuIds;
			}
	        closeConnection(conn);
		}		
		return backInStockSkuIds;
	}
}
