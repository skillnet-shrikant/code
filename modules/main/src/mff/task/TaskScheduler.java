package mff.task;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * An extension of the ATG SingletonSchedulableService. This is used
 * as the Schedulable class for all batch jobs. It defines two extension properties:
 * 
 * <p><code>tasks</code>: An array of tasks that will be executed by the scheduled job.
 * <p><code>enable</code>: Setting to control if the job should execute. 
 * @author KnowledgePath Inc.
 */
public class TaskScheduler extends SingletonSchedulableService {

	/**
	 * Executes the tasks configured for the schedulable process. The tasks are
	 * executed only if <code>enable</code> is set to true
	 */
	public void doTask(){
		if (!isEnable()){
			if (isLoggingDebug()){
				logInfo("The job is not enabled. The tasks will not be executed");
			}			
			return;
		}

		if (getTasks() == null || getTasks().length == 0){
			if (isLoggingDebug()){
				logInfo("There are no tasks configured under the job.");
			}			
			return;			
		}

		if (isLoggingInfo()){
			logInfo("Starting : " + getJobName());
		}

		// execute all enabled tasks
		for (int i=0; i<getTasks().length; i++){
			Task task = getTasks()[i];
			if (!task.isEnable()) {
			  if (isLoggingDebug()) {
			    logDebug("skipping disabled task: " + task.getAbsoluteName());
			  }
			  continue;
			}
			  
			if (isLoggingDebug()){
				logDebug("Executing task : " + task.getTaskName());
			}
			task.doTask();
		}

		if (isLoggingInfo()){
			logInfo("Ending : " + getJobName());
		}		
	}

	/**
	 * @see atg.service.scheduler.SingletonSchedulableService#doScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 * Calls the doTask() method
	 */
	@Override
	public void doScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		doTask();
	}

	/**
	 * @return An array of tasks that are configured for the process
	 */
	public Task[] getTasks() {
		return tasks;
	}

	/**
	 * @param tasks An array of tasks that are configured for the process
	 */
	public void setTasks(Task[] tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return True if the job is to be executed
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * @param enable True if the job is to be executed
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	private Task[] tasks;
	private boolean enable;
}
