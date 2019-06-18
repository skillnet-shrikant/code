package com.mff.account.order.forcepasswordreset;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import mff.task.Task;

public class ForcePasswordResetTask extends Task {

	private int mDuration;
	
	private boolean mUseLastActivity;
	
	// The main stored proc that processes the import
	private String storedProcName;
	
	// CATFEED datasource component
	private DataSource feedDataSource;
	
	// Connection to schema
	protected Connection conn;
	
	@Override
	public void doTask() {
		performTask();
	}
	
	private void performTask() {
		vlogInfo("Start: InactiveAccountCCRemovalTask: performTask()");
		boolean hasError = false;
		CallableStatement cs;
		try (Connection conn = feedDataSource.getConnection()) {
			this.conn = conn;
			conn.setAutoCommit(false);
			cs = conn.prepareCall(getStoredProcName());
			if(getDuration()==0){
				mDuration=6;
			}
			cs.setInt(1, getDuration());
			
			if(isUseLastActivity()){
				cs.setInt(2,1);
			}
			
			else if(!isUseLastActivity()){
				cs.setInt(2,0);
			}
			
			if(isLoggingInfo()) {
				logInfo(getTaskName() + " Executing stored proc");
			}
			
			boolean retValue =cs.execute();
			conn.commit();

			if(isLoggingInfo()) 
				logInfo(getTaskName() + " StoreProc completed with return code " + retValue);
		}
		catch (Throwable e) {
			hasError = true;
			if(isLoggingError())
				logError(e);
		}
		finally {
			if (!hasError) {
				if(isLoggingInfo())
					logInfo(getTaskName() + " Scheduled Task  collect data completed Successfully");
			}
			else {
				if(isLoggingError())
					logError(getTaskName() + " Scheduled Task collect data completed with Error");
			}
			closeConnection(this.conn);
			vlogInfo("End: InactiveAccountCCRemovalTask: performTask()");
		}		
		
	}
	
	protected void closeConnection(Connection pConnection) {
		try {
			if (pConnection != null)
				pConnection.close();
		} catch (SQLException sqle) {
			if (isLoggingError())
				logError(sqle);
		}
	}
	
	public int getDuration() {
		return mDuration;
	}
	public void setDuration(int pDuration) {
		mDuration = pDuration;
	}
	public boolean isUseLastActivity() {
		return mUseLastActivity;
	}

	public void setUseLastActivity(boolean pUseLastActivity) {
		mUseLastActivity = pUseLastActivity;
	}

	public String getStoredProcName() {
		return storedProcName;
	}
	public void setStoredProcName(String pStoredProcName) {
		storedProcName = pStoredProcName;
	}
	public DataSource getFeedDataSource() {
		return feedDataSource;
	}
	public void setFeedDataSource(DataSource pFeedDataSource) {
		feedDataSource = pFeedDataSource;
	}

}
