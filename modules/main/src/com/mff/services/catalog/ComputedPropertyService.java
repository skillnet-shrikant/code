package com.mff.services.catalog;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.sql.DataSource;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import mff.MFFException;
import mff.logging.ErrorMessages;
import mff.logging.LogConstants;
import mff.logging.LogLevel;
import mff.logging.Logger;

/*
 * A scheduled job that invokes a stored procedure in CATFEED schema
 * to populate a non-versioned SKU table (mff_sku_computed) with computed values.
 * This is primarily intended for endeca indexing.
 * 
 * For example: To determine if a SKU is in-stock or not, involves
 * quering mff_inventory table for the sku... and doing the math
 * (stock_level - (sold+shipped+allocated)). 
 * 
 * Instead.. we have a computed prop on the sku.. that is a boolean
 * which is set/un-set based on the math performed by the stored proc
 * invoked by this job
 * 
 */

public class ComputedPropertyService extends SingletonSchedulableService {

	// flag to turn service on/off
	private boolean enable;

	// connection to the CATFEED schema
	private DataSource feedDataSource;

	// Logger component that logs either to file or DB
	private Logger logger;

	// Constants used in logging messsages
	private static String LOG_SERVICE_START = LogConstants.LOG_SERVICE_START;
	private static String LOG_SERVICE_END = LogConstants.LOG_SERVICE_END;


	/*
	 * Main controller method.
	 * 
	 */
	public void doTask() {


		if (enable == false) {
			vlogInfo("----> Service is disabled");
			return;
		}

		logInfo("Scheduled Task Started", LOG_SERVICE_START);

		try (Connection conn = feedDataSource.getConnection()) {

			Long startTime = System.currentTimeMillis();

			// Turn off auto-commit
			// Depending on exceptions.. the job will commit
			// the changes
			conn.setAutoCommit(false);

			// Invoke the stored proc that does
			// all the computations
			execute(conn);

			// Commit the changes
			conn.commit();

			// Track time of execution as part of logging
			Long endTime = System.currentTimeMillis();
			String sectext = String.format("%-7.2f", (endTime - startTime) / 1000f);

			logInfo("Scheduled Task Completed Successfully in " + sectext.trim() + " sec", LOG_SERVICE_END);
		}
		catch (Throwable e) {
			logError(ErrorMessages.MFF_U6000_CRITICAL_MSG,
					ErrorMessages.MFF_U6000_CRITICAL_CODE, e);
			logError("Scheduled Task Exited with Error", LOG_SERVICE_END, null);
		}

	}


	public void logError(String msg, String msgCode, Throwable ex) {
		if (isLoggingError()) {
			try {
				Logger.LogEntry logEntry = logger.createLogEntry(this.getName(),
						LogLevel.ERROR, msgCode, msg, (Exception) ex);
				logger.log(logEntry);
			}
			catch (MFFException me) {
				throw new RuntimeException(me);
			}
		}
	}

	public void logInfo(String msg, String msgCode) {
		if (isLoggingInfo()) {
			try {
				Logger.LogEntry logEntry = logger.createLogEntry(this.getName(),
						LogLevel.INFO, msgCode, msg, null);
				logger.log(logEntry);
			}
			catch (MFFException me) {
				throw new RuntimeException(me);
			}
		}
	}

	private void execute(Connection conn) {
		String sp = "{ call mff_computed_property.compute }";
		vlogDebug("Stored Procedure: " + sp);

		try (CallableStatement cs = conn.prepareCall(sp)) {
			cs.executeUpdate();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		doTask();
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}


	public DataSource getFeedDataSource() {
		return feedDataSource;
	}

	public void setFeedDataSource(DataSource feedDataSource) {
		this.feedDataSource = feedDataSource;
	}

}
