package mff.loader;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import atg.epub.project.Project;
import atg.nucleus.ServiceException;
import atg.versionmanager.WorkingContext;
import mff.logging.ErrorMessages;
import mff.util.DateUtil;

public class VersionedMultiTaskLoader extends VersionedFeedLoader {

	private VersionedFeedLoader tasks[] = null;
	private VersionedFeedLoader failedTask = null;
	private VersionedFeedLoader currentMultiTask = null;
	private Integer currentMultiTaskIndex = null;
	private VersionedFeedLoader currentSoloTask = null;
	private Integer currentSoloTaskIndex = null;
	private VersionedFeedLoader lastSoloTask = null;
	private String lastSoloTaskMessage = null;
	private String lastSoloTaskProjectName = null;
	private Timestamp lastSoloTaskTime = null;
	private int lastSoloTaskStatus = 0;

	public VersionedFeedLoader[] getTasks() {
		return tasks;
	}

	public void setTasks(VersionedFeedLoader[] tasks) {
		this.tasks = tasks;
	}

	public VersionedFeedLoader getFailedTask() {
		return failedTask;
	}

	public void setFailedTask(VersionedFeedLoader failedTask) {
		this.failedTask = failedTask;
	}

	public Integer getCurrentMultiTaskIndex() {
		return currentMultiTaskIndex;
	}

	public void setCurrentMultiTaskIndex(Integer currentMultiTaskIndex) {
		this.currentMultiTaskIndex = currentMultiTaskIndex;
	}

	public Integer getCurrentSoloTaskIndex() {
		return currentSoloTaskIndex;
	}

	public void setCurrentSoloTaskIndex(Integer currentSoloTaskIndex) {
		this.currentSoloTaskIndex = currentSoloTaskIndex;
	}

	public String getLastSoloTaskProjectName() {
		return lastSoloTaskProjectName;
	}

	public void setLastSoloTaskProjectName(String lastSoloTaskProjectName) {
		this.lastSoloTaskProjectName = lastSoloTaskProjectName;
	}

	public int getLastSoloTaskStatus() {
		return lastSoloTaskStatus;
	}

	public void setLastSoloTaskStatus(int lastSoloTaskStatus) {
		this.lastSoloTaskStatus = lastSoloTaskStatus;
	}

	public VersionedFeedLoader getCurrentSoloTask() {
		return currentSoloTask;
	}

	public void setCurrentSoloTask(VersionedFeedLoader currentSoloTask) {
		this.currentSoloTask = currentSoloTask;
	}

	public VersionedFeedLoader getLastSoloTask() {
		return lastSoloTask;
	}

	public void setLastSoloTask(VersionedFeedLoader lastSoloTask) {
		this.lastSoloTask = lastSoloTask;
	}

	public String getLastSoloTaskMessage() {
		return lastSoloTaskMessage;
	}

	public void setLastSoloTaskMessage(String lastSoloTaskMessage) {
		this.lastSoloTaskMessage = lastSoloTaskMessage;
	}

	public Timestamp getLastSoloTaskTime() {
		return lastSoloTaskTime;
	}

	public void setLastSoloTaskTime(Timestamp lastSoloTaskTime) {
		this.lastSoloTaskTime = lastSoloTaskTime;
	}

	public VersionedFeedLoader getCurrentMultiTask() {
		return currentMultiTask;
	}

	public void setCurrentMultiTask(VersionedFeedLoader currentTask) {
		this.currentMultiTask = currentTask;
	}

	public boolean loadThisFile(File file) {
		// Nothing to do... this is Task based
		return false;
	}

	public void processFeedFile(File file) {
		// Nothing to do... this is Task based
	}

	public void init() {
		failedTask = null;
		currentMultiTask = null;
		currentMultiTaskIndex = null;
		super.init();
	}

	public void baseExit() {
		currentMultiTask = null;
		currentMultiTaskIndex = null;
		super.baseExit();
	}

	public void doStartService() throws ServiceException {
		// Make sure each service is not running on its own schedule.
		// Disable single-service scheduling if necessary
		if (tasks != null) {
			for (VersionedFeedLoader task : tasks) {
				task.setupJobForMultiTask();
			}
		}

		// This IS the multitasker. So, by definition it will not run
		// as an individual multitask
		setRunningAsMultiTask(false);
		super.doStartService();
	}

	public void runSoloJob(int jobIndex) {
		if (jobIndex > tasks.length) {
			lastSoloTaskMessage = "The chosen task is invalid";
			lastSoloTaskStatus = RC_ERROR;
			return;
		}

		// Assign the current solo task and call it in solo mode. The call will
		// block if
		// a job is already running, so make sure whoever is calling this is in
		// a separate
		// thread and won't react badly if we're blocking for awhile
		currentSoloTask = tasks[jobIndex];
		currentSoloTaskIndex = new Integer(jobIndex);
		logDebug("Running solo job " + currentSoloTask.getJobName());
		lastSoloTaskStatus = currentSoloTask.runTheJob(true);
		logDebug("Completed solo job " + currentSoloTask.getJobName());
		lastSoloTask = currentSoloTask;
		lastSoloTaskMessage = currentSoloTask.getLastRunMessage();
		lastSoloTaskTime = new Timestamp(System.currentTimeMillis());
		if (currentSoloTask.getProcess() != null) {
			lastSoloTaskProjectName = currentSoloTask.getProcess().getProject().getDisplayName();
		}
		currentSoloTask = null;
		currentSoloTaskIndex = null;
	}

	public synchronized int runTheJob() {
		try {
			logInfoToDB("**** Combined Loader Tasks Started ****", LOG_SERVICE_START);
			int retCode = runTheJobInternal();
			if (retCode != RC_ERROR) {
				logInfoToDB("**** Combined Loader Tasks Completed Successfully ****", LOG_SERVICE_END);
			} else {
				throw new RuntimeException("Getting error code: " + retCode);
			}
			return retCode;
		} catch (Exception ex) {
			logError(ErrorMessages.MFF_U4000_CRITICAL_MSG, ErrorMessages.MFF_U4000_CRITICAL_CODE, ex);
			logError("!!!! Combined Loader Exited with Error !!!!", LOG_SERVICE_END, null);
			return RC_ERROR;
		}
	}

	@Override
	public String getErrorCode() {
		return ErrorMessages.MFF_U4000_CRITICAL_CODE;
	}

	@Override
	public String getBCCDeploymentErrorCode() {
		return ErrorMessages.MFF_U4000_CRITICAL_CODE;
	}

	@SuppressWarnings("rawtypes")
	private synchronized int runTheJobInternal() {
		// Initialize the job
		baseInit();

		setStartTimeMillis(System.currentTimeMillis());
		logInfo("MultiTask Job Starting [" + DateUtil.getDateTime(new Date(getStartTimeMillis())) + "]");
		VersionedFeedLoader currentTask = null;

		// Keep track of task status
		HashMap<String, Boolean> failedStatus = new HashMap<>();

		try {
			// Initialize job variables
			init();
			createProject();

			// Run all the tasks
			if (tasks != null) {
				int i = 0;

				// Maintain a map of asset types changed by each loader
				TreeMap<String, ChangedAsset> beforeAssets = null;
				for (VersionedFeedLoader task : tasks) {
					currentTask = task;
					logDebug("Starting job " + task.getAbsoluteName());
					currentMultiTask = task;
					currentMultiTaskIndex = new Integer(i++);

					int retCode = task.runTheJob(false);

					if (retCode == RC_ERROR) {
						// There was a problem, so throw an exception so the
						// thing won't deploy
						failedTask = task;
						failedStatus.put(task.getAbsoluteName(), new Boolean(true));
						throw new Exception("The job " + task.getAbsoluteName() + " could not complete");
					} else {
						failedStatus.put(task.getAbsoluteName(), new Boolean(false));
					}

					TreeMap<String, ChangedAsset> afterAssets = task.countProjectAssets(getProcess());
					TreeMap<String, ChangedAsset> diffAssets = compareAssetCounts(beforeAssets, afterAssets);
					task.setChangedAssets(diffAssets);
					beforeAssets = afterAssets;
				}

				// Deploy the project
				if (isAdvanceWorkflow()) {
					// Deploy the project. If the project is empty, then
					// 'deployProject()' will delete the
					// project and return NULL
					atg.epub.project.Process proc = deployProject();

					// Wait for the deployment, if configured, and if there is a
					// project to deploy
					if (getWaitForDeployment() && proc != null) {
						try {
							deploymentComplete = waitForDeployment(proc);
						} catch (InterruptedException e) {
							logError("InterruptedException while waitForDeployment for process " + proc, e);
							deploymentComplete = false;
						} catch (Exception e) {
							logError("Exception while waitForDeployment for process " + proc, e);
							deploymentComplete = false;
						}
					}
				} else {
					// We might be able to delete this project if it's empty
					if (getProcess() != null) {
						Project project = getProcess().getProject();
						Set assets = project.getAssets();
						if (assets == null || assets.size() == 0) {
							logDebug("Attempting to delete empty import project");
							deleteProject();
						}
					}
				}

				// Log out...
				WorkingContext.popDevelopmentLine();
				releaseUserIdentity();
			}
		} catch (Exception ex) {
			logError("Unrecoverable exception occurred while processing job " + currentTask.getAbsoluteName()
					+ " [message=" + ex.getMessage() + "]", ex);
			setJobReturnCode(RC_ERROR);
		} catch (Error err) {
			logError("Unrecoverable error occurred while processing job " + currentTask.getAbsoluteName() + " [message="
					+ err.getMessage() + "]", err);
			setJobReturnCode(RC_ERROR);
		} finally {
			try {
				cleanup();

				// If any tasks have reported non-fatal issues, flag the whole
				// job as WARN
				if (getJobReturnCode() != RC_ERROR) {
					for (VersionedFeedLoader task : tasks) {
						if (task.getLockedAssetList().size() > 0 || task.getReportWarnList().size() > 0
								|| task.getReportErrorList().size() > 0) {
							setJobReturnCode(RC_WARN);
							break;
						}
					}
				}
			} catch (Exception cleanupEx) {
				logError("Exception occurred during cleanup of job " + currentTask.getAbsoluteName() + " [message="
						+ cleanupEx.getMessage() + "]", cleanupEx);
				setJobReturnCode(RC_ERROR);
			}
		}

		exit();

		// Archive the files for all tasks
		if (getJobReturnCode() == RC_ERROR) {
			logDebug("Not archiving files due to error...  resetting feeds...");
			if (tasks != null) {
				for (VersionedFeedLoader task : tasks) {
					logDebug("Set error status for task: " + task.getAbsoluteName());
					Boolean failed = failedStatus.get(task.getAbsoluteName());
					// when a task fails, messages is already set to failed
					// status
					// calling this again would log redundant warning
					if (!failed)
						task.resetFeedFiles();
				}
			}
		} else {
			logDebug("Archiving all current files for tasks...");
			if (tasks != null) {
				for (VersionedFeedLoader task : tasks) {
					task.archiveFeedFiles();
				}
			}
		}

		setEndTimeMillis(System.currentTimeMillis());
		logInfo("Job Ending [" + DateUtil.getDateTime(new Date(getEndTimeMillis())) + "] with Return Code ["
				+ getJobReturnCode() + "]");
		logInfo("===============================================================================");
		int retCode = getJobReturnCode();

		baseExit();

		// Do final cleanup of all tasks
		if (tasks != null) {
			for (VersionedFeedLoader task : tasks) {
				task.cleanupMultiTask();
			}
		}

		return retCode;
	}

	protected TreeMap<String, ChangedAsset> compareAssetCounts(TreeMap<String, ChangedAsset> before,
			TreeMap<String, ChangedAsset> after) {
		// Compute the differences between after and before
		TreeMap<String, ChangedAsset> assetCounts = new TreeMap<String, ChangedAsset>();
		if (after == null)
			return assetCounts;
		else if (before == null) {
			assetCounts.putAll(after);
			return assetCounts;
		}

		logDebug("before-->" + before.toString() + "   after-->" + after.toString());
		for (String afterType : after.keySet()) {
			ChangedAsset afterCount = after.get(afterType);
			ChangedAsset beforeCount = before.get(afterType);
			int diff = 0;
			if (afterCount != null && beforeCount != null)
				diff = afterCount.numChanged - beforeCount.numChanged;
			else if (afterCount != null && beforeCount == null)
				diff = afterCount.numChanged;

			if (diff > 0) {
				ChangedAsset diffCount = new ChangedAsset();
				diffCount.itemType = afterType;
				diffCount.numChanged = diff;
				assetCounts.put(afterType, diffCount);
			}
		}
		return assetCounts;
	}

	public String createEmailBody() {
		logDebug("enter MultiTask creating email body function");
		StringBuffer emailBody = new StringBuffer();
		emailBody.append(getAbsoluteName() + " MultiTask Job Report\n\n\n");
		boolean reachedFailedTask = false;
		if (tasks != null) {
			// Add all the common stuff to the message
			emailBody.append(generateDeploymentMessageBody());
			emailBody.append("===========================================================\n");
			emailBody.append("Results from individual import tasks follow\n");
			emailBody.append("===========================================================\n\n");
			for (VersionedFeedLoader task : tasks) {
				if (reachedFailedTask) {
					emailBody.append("===========================================================\n");
					emailBody.append(" Results of Job " + task.getAbsoluteName() + "\n");
					emailBody.append("===========================================================\n\n");
					emailBody.append("This job was not executed because an earlier task failed\n\n\n\n\n");
				} else {
					emailBody.append("===========================================================\n");
					emailBody.append(" Results of Job " + task.getAbsoluteName() + "\n");
					emailBody.append("===========================================================\n\n");
					emailBody.append(task.createEmailBody() + "\n\n\n\n\n");
				}
				if (failedTask != null && task.equals(failedTask)) {
					reachedFailedTask = true;
				}
			}
		}
		return emailBody.toString();
	}

}
