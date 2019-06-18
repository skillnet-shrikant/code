package com.mff.tires.parts.scheduler;

import java.util.concurrent.TimeUnit;

import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import com.mff.tires.parts.service.FitmentDataService;

public class PartsFitmentDataScheduler extends SingletonSchedulableService {

	private static final String JOB_DESCRIPTION = "Scheduled enrichment of Parts Fitment Data";
	private static final String JOB_NAME = "Parts Fitment Data Scheduler";
	private boolean schedulerEnabled;
	boolean exception = false;
	private FitmentDataService fitmentDataService;

	/**
	 * This method will get invoke MAMAutoCat Service automatically after
	 * certain time period
	 */
	public void doScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
		long totalTime = 0L;
		long elapsedTimeInNanoSecond = 0L;
		try {
			PerformanceMonitor.startOperation(JOB_NAME);
			if (isSchedulerEnabled()) {
				vlogDebug("PartsFitmentDataScheduler:doScheduledTask:started to perform task with job name=[" + pJob.getJobName() + "]");
				long startTime = System.nanoTime();
				getFitmentDataService().invokeMAMAutoCatService();
				long endTime = System.nanoTime();
				elapsedTimeInNanoSecond = endTime - startTime;
				totalTime=converNanoSecondsToSeconds(elapsedTimeInNanoSecond);
			} else {
				vlogDebug("Scheduler Task is Disabled");
			}
		} catch (Exception e) {
			PerformanceMonitor.cancelOperation(JOB_NAME, JOB_DESCRIPTION);
			exception = true;
			vlogError("PartsFitmentDataScheduler:doScheduledTask:Error occurred while processing MAMAutoCat Data",e);
		} finally {
			vlogDebug("Total execution time : " + totalTime);
			if (!exception)
				PerformanceMonitor.endOperation(JOB_NAME, JOB_DESCRIPTION);

		}
	}

	/**
	 * This method is to invoke MAMAutoCat Service manually from dyn/admin
	 */
	public void invokeMAMAutoCat() {
		long elapsedTimeInNanoSecond = 0L;
		long totalTime = 0L;
		try {
			long startTime = System.nanoTime();
			getFitmentDataService().invokeMAMAutoCatService();
			long endTime = System.nanoTime();
			elapsedTimeInNanoSecond = endTime - startTime;
			totalTime=converNanoSecondsToSeconds(elapsedTimeInNanoSecond);
		} finally {
			vlogDebug("Total execution time : " + totalTime);
		}
	}

	public boolean isSchedulerEnabled() {
		return schedulerEnabled;
	}

	public void setSchedulerEnabled(boolean schedulerEnabled) {
		this.schedulerEnabled = schedulerEnabled;
	}

	public FitmentDataService getFitmentDataService() {
		return fitmentDataService;
	}

	public void setFitmentDataService(FitmentDataService pFitmentDataService) {
		fitmentDataService = pFitmentDataService;
	}

	/**
	 * This method will convert nanoseconds to seconds
	 */
	public long converNanoSecondsToSeconds(long totalTime){	
        // TimeUnit
        long convert = TimeUnit.SECONDS.convert(totalTime, TimeUnit.NANOSECONDS);
        vlogDebug(convert + " seconds");
        return convert;
	}
}
