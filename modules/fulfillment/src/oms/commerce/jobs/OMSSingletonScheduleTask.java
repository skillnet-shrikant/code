/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.commerce.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import atg.nucleus.ServiceException;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * Base class for all SingletonSchedulableService used in OMS. It adds the 
 * following customizations:
 * <li> Adds a schedulerEnabled flag and starts the service only if it is 
 * enabled.
 * <li> Wraps the core functionalities within performance monitor
 *  
 * @author KnowledgePath Solutions Inc.
 */
public abstract class OMSSingletonScheduleTask extends SingletonSchedulableService{
	
	// ------------------------------------------
	// CONFIGURED PROPERTIES
	// ------------------------------------------
	private boolean mSchedulerEnabled = true;
	private String mOperationName;

	// ------------------------------------------
	// PUBLIC METHODS
	// ------------------------------------------
	/**
	 * This method performs the scheduled task. It is an abstract method and
	 * must be implemented by any subclasses.
	 */
	abstract protected void performTask();
	
	/**
	 * This service starts if the service is enabled.
	 * 
	 * @exception atg.nucleus.ServiceException
	 */
	public void doStartService() throws ServiceException {
		if (getSchedulerEnabled()) {
			if (isLoggingDebug())
				logDebug("Scheduler Enabled");
			super.doStartService();
		} else {
			if (isLoggingDebug())
				logDebug("Scheduler Disabled");
		}
	}

	/**
	 * When this service stops we should deregister it from the scheduler and
	 * the file system data manager. Do it only if the service was enabled.
	 * 
	 * @exception ServiceException
	 */
	public void doStopService() throws ServiceException {
		if (getSchedulerEnabled())
			super.doStopService();
	}

	/**
	 * This method performs the scheduled task. It calls the doTask method
	 * 
	 * @param pScheduler The scheduler
	 * @param pScheduledJob The scheduled job
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
		doTask("doScheduledTask()");
	}

	/**
	 * This method forces the scheduled task to be called without interfering
	 * with the schedule. It calls the doTask method.
	 */
	public void forceScheduledTask() {
		doTask("forceScheduledTask()");
	}

	/**
	 * It calls the performTask method that should be implemented in any 
	 * subclasses.
	 * 
	 * @param parameter The calling method name
	 */
	private void doTask(String parameter) {
		if (isLoggingDebug())
			logDebug(parameter + ": START scheduled process");

		PerformanceMonitor.startOperation(getOperationName(), parameter);
		// Peform the task
		performTask();
		
		try {
			PerformanceMonitor.endOperation(getOperationName(), parameter);
		} catch (PerfStackMismatchException psme) {
			if(isLoggingError())
				logError(psme.getMessage());
		}

		if (isLoggingDebug())
			logDebug(parameter + ": END scheduled process");
	}

	/**
	 * @return The current date
	 */
	protected Date getCurrentDate() {
		Calendar c = new GregorianCalendar();
		return c.getTime();
	}
	
	// ------------------------------------------
	// GETTERS AND SETTERS
	// ------------------------------------------
	/**
	 * @return Is the scheduled process enabled
	 */
	public boolean getSchedulerEnabled() {
		return mSchedulerEnabled;
	}

	/**
	 * @param pSchedulerEnabled Is the scheduled process enabled
	 */
	public void setSchedulerEnabled(boolean pSchedulerEnabled) {
		mSchedulerEnabled = pSchedulerEnabled;
	}	

	/**
	 * @return A name to identify the process
	 */
	protected String getOperationName() {
		return mOperationName;
	}

	/**
	 * @param pOperationName A name to identify the process
	 */
	protected void setOperationName(String pOperationName) {
		mOperationName = pOperationName;
	}
}
