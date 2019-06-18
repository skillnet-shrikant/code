package mff.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang3.StringUtils;

import atg.adapter.gsa.GSARepository;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectEnumStatus;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.DebugLogEvent;
import atg.nucleus.logging.ErrorLogEvent;
import atg.nucleus.logging.InfoLogEvent;
import atg.nucleus.logging.TraceLogEvent;
import atg.nucleus.logging.WarningLogEvent;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;
import atg.userdirectory.UserDirectoryUserAuthority;
import atg.versionmanager.VersionManager;
import atg.versionmanager.VersionManagerURI;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.WorkingVersion;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;
import atg.versionmanager.factories.AssetFactory;
import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;
import mff.MFFException;
import mff.logging.LogConstants;
import mff.logging.LogLevel;
import mff.logging.Logger;
import mff.util.DateUtil;
import mff.util.EmailUtil;
import mff.util.FileUtil;
import mff.util.LogUtil;
import mff.util.StringUtil;

/*
 * Simple Batch Job framework which you can extend to write scheduled jobs.
 * 
 * This is the KP version of the BaseBatchApp service, which is designed specifically
 * to run under the BCC instance.  This implementation combines the functionality of
 * BaseBatchApp (feed file handling and archiving, job logging, scheduling) with the
 * ProgramaticImportService (file loading, project creation and deployment) into a simple
 * single base implementation.
 * 
 * Services based on this implementation can run either as a standalone scheduled loader,
 * or can be included as part of a MultiTaskVersionedFeedLoader, in which case, the project
 * management pieces will be ignored
 * 
 * Class provides the following hooks methods which you can override to
 * implement your custom logic:
 * 
 * init() : Place any logic here to initialize variables or files or data.
 * runTheJob() : The main logic of your program goes here.
 * cleanup() : Clean up any variables, close file resources, delete temporary data, etc.
 * exit() : This is called after the job has completed and cleaned up. Allows you to
 * perform any post-execution related activities.
 * 
 * In addition, many of the methods below can be replaced or extended by implementing
 * classes in order to provide specialized behavior for a particular loader function.
 * Each method below is commented accordingly...
 */
public abstract class VersionedFeedLoader extends SingletonSchedulableService {

	/* Return Codes */
	public static final int RC_SUCCESS = 0;
	public static final int RC_WARN = 1;
	public static final int RC_ERROR = 2;

	/* Status Codes */
	public static final int STATUS_IDLE = 0;
	public static final int STATUS_RUNNING = 1;

	public static final long DEFAULT_MAX_LOG_SIZE_KB = 5120;

	private Scheduler scheduler;
	private Schedule schedule;

	private TransactionManager transactionManager;

	int jobId;
	int jobReturnCode;
	int jobStatus;
	long startTimeMillis;
	long endTimeMillis;
	boolean runningAsMultiTask;
	boolean enabled;

	int logFileLevel;
	String loggingOutputFilePath;
	long maxLogFileSizeKB;
	File loggingOutputFile;
	BufferedWriter loggingOutputWriter;
	Logger loggerService;

	private String notificationEmailList;
	private String notificationEmailFromAddress = "bogus@whatever.foo";
	private int notificationLevel = 2;
	private boolean notifyJobStarting = true;

	private String runServer;
	private String lastRunMessage = null;
	protected String currentFilename = "All Files";
	private TreeMap<String, ChangedAsset> changedAssets = null;

	protected static String LOG_SERVICE_START = LogConstants.LOG_SERVICE_START;
	protected static String LOG_SERVICE_END = LogConstants.LOG_SERVICE_END;

	// -------------------------
	// Abstract methods
	// -------------------------
	public abstract String getErrorCode();

	public abstract String getBCCDeploymentErrorCode();

	/**
	 * This abstract method is meant to be overridden in the user's subclass.
	 * This is where all the logic for importing the user's data is to be done.
	 */
	public abstract void processFeedFile(File file) throws Exception;

	/**
	 * This abstract method is meant to be overridden in the user's subclass.
	 * This method should return 'true' if the given file should processed by
	 * this loader Basically, the implementing class should name-check the file
	 */
	public abstract boolean loadThisFile(File file);

	// The name of the server this service is running on, for email
	// notifications
	public String getRunServer() {
		return runServer;
	}

	public void setRunServer(String runServer) {
		this.runServer = runServer;
	}

	// Contains the contents of the last notification email sent, for debugging
	// purposes
	public String getLastRunMessage() {
		return lastRunMessage;
	}

	public void setLastRunMessage(String lastRunMessage) {
		this.lastRunMessage = lastRunMessage;
	}

	public VersionedFeedLoader() {
		runningAsMultiTask = false;
		enabled = true;
		jobReturnCode = RC_SUCCESS;
		logFileLevel = LogUtil.LOG_LEVEL_ERROR;
		maxLogFileSizeKB = DEFAULT_MAX_LOG_SIZE_KB;
	}

	// This method will set up the job as one that will be executed by a
	// MultiTask job, so configure
	// the job so that it is not independently scheduled, and so it will not
	// autonomously run as a
	// standalone job.
	public void setupJobForMultiTask() {
		// Turn off scheduling if it's on. That way we don't have to remember
		// each time.
		logDebug("Configuring job!!!!!!!!!!! " + getJobName()
				+ " as a MultiTask job.  Turn off independent scheduling.");
		setRunningAsMultiTask(true);
		stopScheduledJob();
	}

	public void doScheduledTask(Scheduler scheduler, ScheduledJob scheduledJob) {
		runTheJob();
	}

	// Called by CtrlM to run the job
	public void executeTask() throws Exception {
		// Run the job in standalone mode
		int retCode = runTheJob();

		// VersionedFeedLoader will trap all exceptions, so check the return
		// code and throw an exception of our own
		// back to CtrlM so that it can report on job health correctly
		if (retCode == RC_ERROR) {
			throw new Exception("The job " + getJobName()
					+ " ended with errors...  please look at the email notification and logs for details");
		}
	}

	// This entry point can be used via /dyn/admin to force the job to run in
	// standalone mode
	public int runTheJob() {
		return runTheJob(true);
	}

	// Process all files gathered for the current run of the job
	// This method is often overridden to provide job-specific behavior. For
	// example, if the data for multiple
	// files should be processed all at once, instead of per file, then we
	// should override this to call an
	// implementation of 'processFeedFile' that prepares the data for
	// application, and then applies the changes
	// once all files are prepared
	protected void processFeedFiles() throws Exception {
		sortFilesByDate(mFeedFiles);
		for (File file : mFeedFiles) {
			logDebug("Loading file " + file.getName());
			currentFilename = file.getName();
			processFeedFile(file);
			logDebug("Completed loading file " + file.getName());
		}
	}

	// This is the main entrypoint into the job.
	// It is synchronized in order to ensure that multiple threads are not
	// trying to process the same files
	// simultaneously in case the job is running slowly.
	// If the job is part of a MultiTask job, it can be forced to run standalone
	// by passing in 'true' as an argument.
	public synchronized int runTheJob(boolean forceStandalone) {

		//List<String> fileNames = loadFilesToSharedLocation();

		// Setup job for standalone mode if necessary
		boolean oldMultiTaskStatus = isRunningAsMultiTask();
		if (forceStandalone) {
			setRunningAsMultiTask(false);
		}

		// Initialize the job
		baseInit();

		setStartTimeMillis(System.currentTimeMillis());
		logInfo("Job Starting [" + DateUtil.getDateTime(new Date(getStartTimeMillis())) + "]");

		try {
			// Initialize job variables
			init();

			// Find the feed files
			mFeedFiles = findFeedFiles();
			logDebug("Found " + mFeedFiles + " files to process for this feed");

			// Are there files to process? If so, let's do it!
			if (mFeedFiles != null && mFeedFiles.size() > 0) {
				// Create the project, if requested, but only if the job is
				// running standalone
				if (!isRunningAsMultiTask()) {
					createProject();
				}

				// Send a job start notification
				if (this.notifyJobStarting) {
					sendJobStartingNotification();
				}

				// Process and apply the changes from all feed files
				processFeedFiles();

				// If we've made it this far without a problem, and we're
				// running standalone, then deploy
				// the project and archive the files
				if (!isRunningAsMultiTask()) {

					// Tally up the assets that have been changed and pull the
					// email notification for
					// debugging purposes
					changedAssets = countProjectAssets();
					lastRunMessage = createEmailBody();

					// If we're configured to deploy the project (if it exists
					// and isn't empty),
					// then deploy it now and optionally wait for the deployment
					if (isAdvanceWorkflow()) {
						// Deploy the project. If the project is empty, then
						// 'deployProject()' will delete the
						// project and return NULL
						Process proc = deployProject();

						// Wait for the deployment, if configured, and if there
						// is a project to deploy
						WorkingContext.popDevelopmentLine();
						releaseUserIdentity();
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
						// If we're not deploying the project, then still check
						// to see if it's empty
						// If it is, then delete the project anyway
						if (mProcess != null) {
							Project project = mProcess.getProject();
							@SuppressWarnings("rawtypes")
							Set assets = project.getAssets();
							if (assets == null || assets.size() == 0) {
								logDebug("Attempting to delete empty import project");
								deleteProject();
							}
						}
					}

					// If we've made it this far without a problem, then archive
					// the feed files...
					archiveFeedFiles();

					// Stash the notification message for debugging purposes
					lastRunMessage = createEmailBody();
				}
			}
		} catch (Exception ex) {
			// If ANY Exceptions are thrown, then we're done... set the job
			// status accordingly
			logError("Unrecoverable exception occurred while processing job [message=" + ex.getMessage() + "]", ex);
			setJobReturnCode(RC_ERROR);
		} catch (Error err) {
			logError("Unrecoverable error occurred while processing job [message=" + err.getMessage() + "]", err);
			setJobReturnCode(RC_ERROR);
		} finally {
			try {
				// When we're done, cleanup after ourselves
				cleanup();

				// Only report Exceptions as fatal. Regular reported data
				// load/validation errors are treated as strong warnings that
				// are reported via email
				// But, these are not enough to stop the job from deploying....
				// Note that this behavior can be modified on a per-client basis
				// if a particular client wants a more
				// severe reaction to data issues. But, we've found that most
				// customers want to keep the loads flowing, even
				// if there are detected data issues. So, the general rule, is
				// Always Deploy, and Report Verbosely
				if (getJobReturnCode() != RC_ERROR) {
					if (lockedAssetList.size() > 0 || reportWarnList.size() > 0 || reportErrorList.size() > 0)
						setJobReturnCode(RC_WARN);
				}
			} catch (Exception cleanupEx) {
				logError("Exception occurred during cleanup [message=" + cleanupEx.getMessage() + "]", cleanupEx);
				setJobReturnCode(RC_ERROR);
			}
		}

		// Another entrypoint for the job to close itself out and cleanup after
		// itself
		exit();

		setEndTimeMillis(System.currentTimeMillis());
		logInfo("Job Ending [" + DateUtil.getDateTime(new Date(getEndTimeMillis())) + "] with Return Code ["
				+ getJobReturnCode() + "]");
		logInfo("===============================================================================");
		int retCode = getJobReturnCode();

		baseExit();

		// Restore the multitask status
		setRunningAsMultiTask(oldMultiTaskStatus);

		return retCode;
	}

	// This method should be called and extended by the implementing class
	// The base implementation of the method simply initializes class variables
	protected void init() {
		logDebug("Step Init");
		mProcess = null;
		mFeedFiles = null;
		reportErrorList = new TreeMap<String, ArrayList<ReportInfo>>();
		reportWarnList = new TreeMap<String, ArrayList<ReportInfo>>();
		lockedAssetList = new TreeMap<String, ArrayList<ReportInfo>>();
		deploymentComplete = false;
	}

	// This method should be called and extended by the implementing class
	// Here, the extending class should clean up after itself
	protected void cleanup() {
		logDebug("Step Cleanup!!!!!");
	}

	// This method should be called and extended by the implementing class
	// Here, the extending class can perform post job actions that are specific
	// to itself
	// The base implementation will perform email notifications and additional
	// job cleanup
	protected void exit() {
		logDebug("Step Exit");

		// If the job is running standalone, send notification emails
		if (!isRunningAsMultiTask()) {
			sendEmailMessage();
		}

		// Clean up the project if there was a failure
		try {
			if (getJobReturnCode() == RC_SUCCESS) {
				logInfo("Process Completed - SUCCESS");
			} else if (getJobReturnCode() == RC_WARN) {
				logInfo("Process Completed - WARNING(s)");
			} else if (getJobReturnCode() == RC_ERROR) {
				// Specifically, if the job ended in a fatal ERROR, always
				// delete the project and reset the feeds
				logInfo("Process Completed - ERROR(s)");
				deleteProject();
				resetFeedFiles();
			}
		} catch (Exception e) {
			logError("Problem while cleaning up after loading feeds", e);
		}
	}

	// Do not extend this... base class initialization, log rotation, etc.
	protected void baseInit() {
		jobReturnCode = RC_SUCCESS;
		setJobStatus(STATUS_RUNNING);
		startTimeMillis = 0;
		endTimeMillis = 0;

		logFileSizeCheck();
	}

	// Do not extend this... base class cleanup...
	protected void baseExit() {
		if (!isRunningAsMultiTask()) {
			// If this is not a multitask job, we can fully reset
			// Otherwise, we want to hang on to some of this stuff for the
			// multitask job rollup
			// MultiTask should call 'cleanupMultiTask' at the end of a full run
			// when it's done
			cleanupMultiTask();
		}
		startTimeMillis = 0;
		endTimeMillis = 0;
	}

	// Perform any additional cleanup required of jobs running as a task of a
	// MultiTask job
	public void cleanupMultiTask() {
		jobReturnCode = RC_SUCCESS;
		setJobStatus(STATUS_IDLE);
		mFeedFiles = null;
		reportWarnList = null;
		reportErrorList = null;
		lockedAssetList = null;
	}

	/*
	 * On startup add this job to the scheduler.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see atg.nucleus.GenericService#doStartService()
	 */
	public void doStartService() throws ServiceException {
		logTrace("doStartService::invoked [multiTask?=" + runningAsMultiTask + "]");
		// Only invoke superclass doStartService if the service is enabled and
		// is running standalone
		// This will add the service to the scheduler, if a schedule is
		// configured
		if (!isRunningAsMultiTask() && isEnabled()) {
			super.doStartService();
		}

		// Initialize the loader log file
		initLogFile();

		// Look for the Logger service
		try {
			loggerService = Logger.getInstance();
		} catch (MFFException e) {
			logError("There was a problem starting the DB Logger service.  Will only log to files.", e);
		}
	}

	/*
	 * On shutdown removes this job from the scheduler.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see atg.nucleus.GenericService#doStopService()
	 */
	public void doStopService() throws ServiceException {
		logTrace("doStopService::invoked");
		closeLogFile();
		super.doStopService();
	}

	protected void sortFilesByDate(List<File> files) {
		// sort the file by date.
		if (files == null)
			return;
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				if (((File) f1).lastModified() < ((File) f2).lastModified()) {
					return -1;
				} else if (((File) f1).lastModified() > ((File) f2).lastModified()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	protected void initLogFile() {
		try {
			if (!StringUtil.isEmpty(getLoggingOutputFilePath())) {
				logDebug("Opening Logging Output File [filePath=" + getLoggingOutputFilePath() + "]");
				loggingOutputFile = new File(getLoggingOutputFilePath());
				if (!loggingOutputFile.exists()) {
					loggingOutputFile.createNewFile();
				}
				loggingOutputWriter = FileUtil.getBufferedFileWriter(loggingOutputFile, true);
			} else {
				logDebug("Logging Output File Path not specified. File will not be opened.");
			}

		} catch (IOException ioEx) {
			logError("IOException while trying to initialize batch log.", ioEx);
			logError("Logging will not be written to specified file [filePath=" + getLoggingOutputFilePath() + "]");

			try {
				if (loggingOutputWriter != null)
					loggingOutputWriter.close();
			} catch (IOException closeException) {
				logError("Error attempting to close log file", closeException);
			} finally {
				loggingOutputWriter = null;
			}
		}
	}

	protected void closeLogFile() {
		try {
			if (loggingOutputWriter != null) {
				loggingOutputWriter.close();
			}
		} catch (IOException closeException) {
			logError("Error attempting to close log file", closeException);
		} finally {
			loggingOutputWriter = null;
		}
	}

	/**
	 * Creates a new file if the log file is greater than the configured size.
	 * 
	 * @throws IOException
	 */
	protected void logFileSizeCheck() {

		if (maxLogFileSizeKB < 1)
			return;

		try {
			if (loggingOutputFile != null && loggingOutputWriter != null) {
				File outfile = loggingOutputFile;
				BufferedWriter outwriter = loggingOutputWriter;
				if (outfile.length() > (maxLogFileSizeKB * 1024)) {
					loggingOutputFile = null;
					loggingOutputWriter = null;
					outwriter.close();
					String backupFileName = getLoggingOutputFilePath() + "."
							+ DateUtil.getDateTime(DateUtil.NUMERIC_DATE_TIME_SEC_PATTERN, new Date());
					FileUtil.renameFile(getLoggingOutputFilePath(), backupFileName);
					initLogFile();
				}
			}
		} catch (IOException ioEx) {
			logError("IOException while trying to cut a new batch log.", ioEx);
			logError("Logging will not be written to specified file [filePath=" + getLoggingOutputFilePath() + "]");
			loggingOutputWriter = null;
			loggingOutputFile = null;
		}

	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isRunningAsMultiTask() {
		return runningAsMultiTask;
	}

	public void setRunningAsMultiTask(boolean runningAsMultiTask) {
		this.runningAsMultiTask = runningAsMultiTask;
	}

	// --------------------------------
	// Logging Support
	//
	// This logging implementation overrides every GenericService logX method
	// with our own.
	// The messages get passed along in the usual way if the appropriate
	// loggingX flag is set,
	// but are also diverted to the loader's own log file...
	// --------------------------------

	public void logError(String msg, Throwable e) {
		logError(msg, getErrorCode(), e);
	}

	public void logError(String msg, String msgCode, Throwable e) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_ERROR, e);
		if (isLoggingError())
			sendLogEvent(new ErrorLogEvent(newMsg, getAbsoluteName(), e));

		if (loggerService != null) {
			// Log to the DB logger service
			try {
				Logger.LogEntry logEntry = loggerService.createLogEntry(this.getName(), LogLevel.ERROR, msgCode, msg,
						(Exception) e);
				loggerService.log(logEntry);
			} catch (MFFException ee) {
				loggerService = null;
				logError("Logger service is messed up. Disabling.", ee);
			}
		}
	}

	public void logWarning(String msg, Throwable e) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_WARN, e);
		if (isLoggingWarning())
			sendLogEvent(new WarningLogEvent(newMsg, getAbsoluteName(), e));

		if (loggerService != null) {
			// Log to the DB logger service
			try {
				Logger.LogEntry logEntry = loggerService.createLogEntry(this.getName(), LogLevel.WARN, "BL_LD001_MINOR",
						msg, (Exception) e);
				loggerService.log(logEntry);
			} catch (MFFException ee) {
				loggerService = null;
				logError("Logger service is messed up. Disabling.", ee);
			}
		}
	}

	public void logInfoToDB(String msg, String msg_code) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_INFO, null);
		if (isLoggingInfo())
			sendLogEvent(new InfoLogEvent(newMsg, getAbsoluteName()));

		if (loggerService != null) {
			// Log to the DB logger service
			try {
				Logger.LogEntry logEntry = loggerService.createLogEntry(this.getName(), LogLevel.INFO, msg_code, msg,
						null);
				loggerService.log(logEntry);
			} catch (MFFException ee) {
				loggerService = null;
				logError("Logger service is messed up. Disabling.", ee);
			}
		}
	}

	public void logInfo(String msg, Throwable e) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_INFO, e);
		if (isLoggingInfo())
			sendLogEvent(new InfoLogEvent(newMsg, getAbsoluteName(), e));
	}

	public void logDebug(String msg, Throwable e) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_DEBUG, e);
		if (isLoggingDebug())
			sendLogEvent(new DebugLogEvent(newMsg, getAbsoluteName(), e));
	}

	public void logDebug(String msg) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_DEBUG, null);
		if (isLoggingDebug())
			sendLogEvent(new DebugLogEvent(newMsg, getAbsoluteName()));
	}

	public void logTrace(String msg, Throwable e) {
		String newMsg = logMessage(msg, LogUtil.LOG_LEVEL_TRACE, e);
		if (isLoggingTrace())
			sendLogEvent(new TraceLogEvent(newMsg, getAbsoluteName(), e));
	}

	public String logMessage(String msg, int level, Throwable e) {
		String newMsg = "[JOB=" + getJobName() + "]" + " " + msg;
		if (e != null) {
			newMsg += " [Exception=" + e.getMessage();
		}

		if (level >= getLogFileLevel()) {
			logToFile(msg, level);
		}
		return newMsg;
	}

	public String getLoggingOutputFilePath() {
		return loggingOutputFilePath;
	}

	public void setLoggingOutputFilePath(String loggingOutputFilePath) {
		this.loggingOutputFilePath = loggingOutputFilePath;
	}

	public BufferedWriter getLoggingOutputWriter() {
		return loggingOutputWriter;
	}

	public void setLoggingOutputWriter(BufferedWriter loggingOutputWriter) {
		this.loggingOutputWriter = loggingOutputWriter;
	}

	public int getLogFileLevel() {
		return logFileLevel;
	}

	public void setLogFileLevel(int logFileLevel) {
		this.logFileLevel = logFileLevel;
	}

	public long getMaxLogFileSizeKB() {
		return maxLogFileSizeKB;
	}

	public void setMaxLogFileSizeKB(long maxLogFileSizeKB) {
		this.maxLogFileSizeKB = maxLogFileSizeKB;
	}

	// This actually dumps a log entry to the log file, but only if the log
	// level is set high enough for the message
	public void logToFile(String msg, int level) {

		BufferedWriter writer = getLoggingOutputWriter();
		if (writer == null)
			return;

		String levelPrefix = LogUtil.LOG_LEVEL_INFO_LOG_PREFIX;
		switch (level) {
		case LogUtil.LOG_LEVEL_ERROR:
			levelPrefix = LogUtil.LOG_LEVEL_ERROR_LOG_PREFIX;
			break;
		case LogUtil.LOG_LEVEL_INFO:
			levelPrefix = LogUtil.LOG_LEVEL_INFO_LOG_PREFIX;
			break;
		case LogUtil.LOG_LEVEL_DEBUG:
			levelPrefix = LogUtil.LOG_LEVEL_DEBUG_LOG_PREFIX;
			break;
		case LogUtil.LOG_LEVEL_TRACE:
			levelPrefix = LogUtil.LOG_LEVEL_TRACE_LOG_PREFIX;
			break;
		}

		String newMsg = "[" + DateUtil.getDateTimeNow() + "] [" + levelPrefix + "] " + msg;

		try {
			writer.append(newMsg);
			writer.newLine();
			writer.flush();
		} catch (IOException ioEx) {
			closeLogFile();
			logError("IOException while trying to write to batch log.", ioEx);
			logError("Closed batch log writer.");
		}

	}

	// ------------------------------
	// Feed parsing helpers
	//
	// Use these helper methods to parse Dates, Timestamps, numbers and Booleans
	// ------------------------------

	protected Date parseDateValue(String dateString) {
		Date retDate = null;
		if (dateString != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

			// parse the string into Date object
			try {
				retDate = sdfSource.parse(dateString);
			} catch (ParseException e) {
				logError("Error parsing date in feed file " + dateString, e);
			}
		}
		return retDate;
	}

	protected Timestamp parseTimestampValue(String dateString) {
		Timestamp retTime = null;
		if (dateString != null) {
			SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

			// parse the string into Date object
			try {
				Date date = null;
				date = sdfSource.parse(dateString);
				retTime = new Timestamp(date.getTime());
			} catch (ParseException e) {
				logError("Error parsing date in feed file " + dateString, e);
			}
		}
		return retTime;
	}

	protected Integer parseIntValue(String intString) {
		if (intString == null) {
			return null;
		} else {
			return Integer.parseInt(intString);
		}
	}

	protected Double parseDoubleValue(String doubleString) {
		if (doubleString == null) {
			return null;
		} else {
			return Double.parseDouble(doubleString);
		}
	}

	protected Boolean parseBooleanValue(String booleanString) {
		if (booleanString == null) {
			return new Boolean(Boolean.FALSE);
		} else if (booleanString.equals("1") || booleanString.equalsIgnoreCase("true")) {
			return new Boolean(Boolean.TRUE);
		}
		return new Boolean(Boolean.FALSE);
	}

	// ==============================
	// Property check helpers
	//
	// Use this method to perform property comparison checks... handy for
	// ensuring that we're only creating
	// versions of assets that have actually changed
	// ==============================
	protected boolean wouldPropertyChange(RepositoryItem item, String propertyName, Object propertyValue,
			boolean ignoreNull) {
		// If we're ignoring NULL and the value is null... well
		if (ignoreNull && (propertyValue == null
				|| (propertyValue instanceof String && StringUtil.isEmpty((String) propertyValue)))) {
			return false;
		}

		boolean updated = false;
		// We need to check if the property needs to be changed

		// Get the current value
		Object curValue = item.getPropertyValue(propertyName);

		// Compare
		if ((curValue == null && propertyValue == null)
				|| (curValue != null && propertyValue != null && curValue.equals(propertyValue))) {
			// It doesn't need to change
		} else {
			// It needs to change
			updated = true;
		}
		return updated;
	}

	// ------------------------------
	// Scheduled job handling stuff
	// ------------------------------

	/**
	 * This will only update the job return code if the specified code is more
	 * severe then the current jobReturnCode.
	 * 
	 * @param code
	 */
	public void updateReturnCode(int code) {
		if (code > this.jobReturnCode)
			this.jobReturnCode = code;
	}

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}

	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}

	public int getJobReturnCode() {
		return jobReturnCode;
	}

	public void setJobReturnCode(int jobReturnCode) {
		logInfo("Setting Return Code [" + jobReturnCode + "]");
		this.jobReturnCode = jobReturnCode;
	}

	public int getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(int jobStatus) {
		this.jobStatus = jobStatus;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	// -------------------------------
	// Email handling support
	//
	// Notifications support the following information:
	// - A list of assets locked at the time of the load
	// - A list of incidents logged as warnings or errors, and the assets that
	// generated them
	// - A count of assets affected by the load, by type
	// - The name of the Project created by the load, if one exists
	// - The completion status of the load
	// -------------------------------

	public TreeMap<String, ArrayList<ReportInfo>> reportErrorList = null;
	public TreeMap<String, ArrayList<ReportInfo>> reportWarnList = null;
	public TreeMap<String, ArrayList<ReportInfo>> lockedAssetList = null;

	public String getNotificationEmailList() {
		return notificationEmailList;
	}

	public void setNotificationEmailList(String notificationEmailList) {
		this.notificationEmailList = notificationEmailList;
	}

	public String getNotificationEmailFromAddress() {
		return notificationEmailFromAddress;
	}

	public void setNotificationEmailFromAddress(String pNotificationEmailFromAddress) {
		notificationEmailFromAddress = pNotificationEmailFromAddress;
	}

	public int getNotificationLevel() {
		return notificationLevel;
	}

	public void setNotificationLevel(int notificationLevel) {
		this.notificationLevel = notificationLevel;
	}

	public boolean isNotifyJobStarting() {
		return notifyJobStarting;
	}

	public void setNotifyJobStarting(boolean notifyJobStarting) {
		this.notifyJobStarting = notifyJobStarting;
	}

	public boolean shouldNotify(int returnCode) {
		return getNotificationLevel() <= returnCode;
	}

	public void sendTextNotificationEmail(String subject, String body, String to) throws EmailException {
		String type = "text/plain";
		sendNotificationEmail(subject, body, to, type);
	}

	private void sendEmailMessage() {
		// Notification E-Mails.
		if (!StringUtil.isEmpty(getNotificationEmailList())) {

			String toAddresses = getNotificationEmailList();
			String subject = "";
			String body = "";
			boolean sendEmail = false;
			if (getJobReturnCode() == RC_ERROR && shouldNotify(RC_ERROR)) {
				sendEmail = true;
				subject = "ERROR: " + getJobName();
				body = "A fatal Exception was encountered while running the job titled: " + getJobName()
						+ ".  Please check the log for more details.\n\n";
			} else if (getJobReturnCode() == RC_WARN && shouldNotify(RC_WARN)) {
				sendEmail = true;
				subject = "WARN: " + getJobName();
				body = "Locked assets, warnings and/or non-fatal Errors were encountered while running the job titled, which completed successfully: "
						+ getJobName() + "\n\n";
			} else if (getJobReturnCode() == RC_SUCCESS && shouldNotify(RC_SUCCESS)) {
				sendEmail = true;
				subject = "SUCCESS: " + getJobName();
				body = "The job titled: " + getJobName()
						+ " ran successfully, with no Warnings or non-fatal Errors.\n\n";
			}
			if (sendEmail) {
				logInfo("Sending Email to Notification List: " + toAddresses);
				try {
					String emailMessage = createEmailBody();
					if (emailMessage != null) {
						body += emailMessage;
					}
					String server = getRunServer();
					if (server != null) {
						subject = subject + "(" + server + ")";
					}
					sendTextNotificationEmail(subject, body, toAddresses);
				} catch (EmailException ee) {
					logError("EmailException occurred while trying to send an email to the Notification Email List",
							ee);
				}
			}
		}
	}

	protected void sendJobStartingNotification() {
		// Notification E-Mails.
		if (!StringUtil.isEmpty(getNotificationEmailList())) {

			String toAddresses = getNotificationEmailList();
			String subject = "STARTING: " + getJobName() + "(" + getRunServer() + ")";
			String body = "The job " + getJobName() + " has started\n";
			if (mProcess != null) {
				body += "The project " + mProcess.getDisplayName() + " has been created for use by this job.\n";
				body += "Please do not delete or deploy this project until you receive another email stating the job has completed\n\n";
			}
			logInfo("Sending Job Starting Email to Notification List: " + toAddresses);
			try {
				sendTextNotificationEmail(subject, body, toAddresses);
			} catch (EmailException ee) {
				logError("EmailException occurred while trying to send job start email to the Notification Email List",
						ee);
			}
		}
	}

	// This method actually sends the email
	public void sendNotificationEmail(String subject, String body, String to, String type) throws EmailException {

		if (disableEmail) {
			logDebug("Email disabled.");
			return;
		}

		EmailEvent emailEvent = new EmailEvent();
		Message msg;
		try {
			EmailUtil emailService = EmailUtil.getInstance();
			msg = MimeMessageUtils.createMessage(getNotificationEmailFromAddress(), subject);
			MimeMessageUtils.setRecipient(msg, Message.RecipientType.TO, to);
			ContentPart[] content = { new ContentPart(body, type) };
			MimeMessageUtils.setContent(msg, content);
			emailEvent.setMessage(msg);

			emailEvent.setRecipient(to);
			emailEvent.setFrom("");
			emailEvent.setBody(body);
			emailEvent.setSubject(subject);

			if (null != emailService) {
				emailService.sendEmailEvent(emailEvent);
			} else {
				logDebug("EmailUtil is not available");
			}
		} catch (MessagingException e) {
			logError("EmailException occurred while trying to send an email to the Notification Email List", e);
		}
	}

	protected String generateChangeListBody() {
		String emailBody = "";
		if (changedAssets == null || changedAssets.size() == 0)
			emailBody += "NO ASSETS were changed by this job\n\n";
		else {
			emailBody += "The following number of assets of these types were changed by this job:\n\n";
			for (String itemType : changedAssets.keySet()) {
				ChangedAsset changes = changedAssets.get(itemType);
				emailBody += "---> " + changes.itemType + " : " + changes.numChanged + "\n";
			}
			emailBody += "\n";
		}
		return emailBody;
	}

	protected String generateDeploymentMessageBody() {
		// Deployment success message
		String emailBody = "";
		if (getJobReturnCode() == RC_WARN || getJobReturnCode() == RC_SUCCESS) {
			if (mAdvanceWorkflow) {
				if (waitForDeployment) {
					if (mProcess == null) {
						emailBody += "The changes have been successfully fully deployed without the need for a Project\n";
					} else {
						if (deploymentComplete) {
							emailBody += "The changes have been successfully fully deployed\n";
						} else {
							emailBody += "Direct DB updates have been successfully deployed, but deployment of new assets was unsuccessful, or still in progress, via the following Project\n";
						}
						emailBody += "Project Name: " + mProcess.getDisplayName() + "\n";
					}
				} else {
					if (mProcess == null) {
						emailBody += "The changes have been successfully fully deployed without the need for a Project\n";
					} else {
						emailBody += "Direct DB updates have been successfully deployed, but deployment of new assets is still in progress via the following Project\n";
						emailBody += "Project Name: " + mProcess.getDisplayName() + "\n";
					}
				}
			} else {
				if (mProcess == null) {
					emailBody += "The changes have been successfully fully deployed without the need for a Project\n";
				} else {
					emailBody += "Direct DB updates have been successfully deployed, but the following Project containing new assets must be manually deployed\n";
					emailBody += "Project Name: " + mProcess.getDisplayName() + "\n";
				}
			}
			emailBody += "\n";
		}
		return emailBody;
	}

	// This method creates the main body of the email to be sent
	// Subclasses may extend this to append or prepend their own custom
	// information about the load
	public String createEmailBody() {
		logDebug("enter creating email body function");
		String emailBody = getJobName() + " Job Report\n\n";

		if (getFeedFiles() == null || getFeedFiles().size() == 0) {
			emailBody += "There were NO files to process this time around\n\n";
		} else {
			emailBody += "The following files were processed\n";
			for (File file : getFeedFiles()) {
				emailBody += "---> " + file.getName() + "\n";
			}
			emailBody += "\n";

			// Add the change list stuff
			emailBody += generateChangeListBody();

			if (!isRunningAsMultiTask()) {
				// For solo jobs, show the deployment success message here
				emailBody += generateDeploymentMessageBody();
			}
		}

		//
		// Render the list of locked assets, and warnings and errors encountered
		//
		if (getJobReturnCode() == RC_ERROR) {
			emailBody += "The import failed due to an unexpected error.  Check the server logs for details.\n";
		} else if (getJobReturnCode() == RC_WARN) {
			emailBody += "The following warnings were reported:\n";
			if (lockedAssetList != null && lockedAssetList.size() > 0) {
				emailBody += "--> locked assets\n";
			}
			if (reportWarnList != null && reportWarnList.size() > 0) {
				emailBody += "--> load warnings\n";
			}
			if (reportErrorList != null && reportErrorList.size() > 0) {
				emailBody += "--> non-fatal errors\n";
			}
		}
		emailBody += "\n";

		// Render the list of errors
		if (null != reportErrorList && reportErrorList.size() > 0) {
			emailBody += "\n\nThe following specific issues were reported\n\n";
			emailBody += padRight("Item Type", 20);
			emailBody += padRight("Item ID", 15);
			emailBody += padRight("Item Name", 50);
			emailBody += "Message\n";
			for (String filename : reportErrorList.keySet()) {
				if (filename != null)
					emailBody += "\n---------- Errors reported in file " + filename + " ----------\n\n";
				for (ReportInfo bean : reportErrorList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() + "\n";
				}
			}
			emailBody += "\n\n";
		}

		// Render the list of warnings
		if (null != reportWarnList && reportWarnList.size() > 0) {
			emailBody += "\n\nThe following specific warnings were reported\n\n";
			emailBody += padRight("Item Type", 20);
			emailBody += padRight("Item ID", 15);
			emailBody += padRight("Item Name", 50);
			emailBody += "Message\n";
			for (String filename : reportWarnList.keySet()) {
				if (filename != null)
					emailBody += "\n---------- Warnings reported in file " + filename + " ----------\n\n";
				for (ReportInfo bean : reportWarnList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() + "\n";
				}
			}
			emailBody += "\n\n";
		}

		// Render the list of locked assets
		if (lockedAssetList != null && lockedAssetList.size() > 0) {
			emailBody += "\n\nThe following assets were locked and, therefore, not updated\n\n";
			emailBody += padRight("Item Type", 20);
			emailBody += padRight("Item ID", 15);
			emailBody += padRight("Item Name", 50);
			emailBody += "Message\n";
			for (String filename : lockedAssetList.keySet()) {
				if (filename != null)
					emailBody += "\n---------- Locked assets reported from file " + filename + " ----------\n\n";
				for (ReportInfo bean : lockedAssetList.get(filename)) {
					emailBody += padRight((bean.getItemType() == null ? "" : bean.getItemType()), 20);
					emailBody += padRight((bean.getItemID() == null ? "" : bean.getItemID()), 15);
					emailBody += padRight((bean.getItemName() == null ? "" : bean.getItemName()), 50);
					emailBody += bean.getMessage() + "\n";
				}
			}
		}
		return emailBody;
	}

	public static String padRight(String s, int n) {
		return StringUtils.rightPad(s, n, "*");
	}

	// Use this method to report a locked asset
	// item - the RepositoryItem that is locked
	// message - a message describing the impact on the feed of this asset being
	// locked
	protected void reportLockedAsset(RepositoryItem item, String message) {
		if (lockedAssetList == null)
			lockedAssetList = new TreeMap<String, ArrayList<ReportInfo>>();

		try {
			String name = item.getItemDescriptor().getItemDescriptorName();
			ReportInfo rBean = new ReportInfo(name, item.getRepositoryId(), item.getItemDisplayName(), message);
			ArrayList<ReportInfo> beanList = lockedAssetList.get(currentFilename);
			if (beanList == null) {
				beanList = new ArrayList<ReportInfo>();
				lockedAssetList.put(currentFilename, beanList);
			}
			beanList.add(rBean);
			logWarning("Lock conflict reported in file " + currentFilename + " - Type=" + name + " : ID="
					+ item.getRepositoryId() + " : Name=" + item.getItemDisplayName() + " : Message=" + message);
		} catch (RepositoryException e) {
			// This should never ever happen
		}
	}

	// Use this method to report an asset-related error...
	// The reporting of "errors" is completely determined by the implementing
	// loader
	// item - the RepositoryItem that is related to the error (or null if there
	// is none)
	// message - a message describing the issue that occurred
	// e - a caught Exception... please reserve this for non-fatal exceptions,
	// like parsing errors, etc. Fatal Exceptions should be thrown up the call
	// chain
	// such that the load will rollback
	protected void reportError(RepositoryItem item, String message) {
		if (item != null)
			reportError(item, message, null);
	}

	protected void reportError(RepositoryItem item, String message, Exception e) {
		try {
			String name = item.getItemDescriptor().getItemDescriptorName();
			reportError(name, item.getRepositoryId(), item.getItemDisplayName(), message, e);
		} catch (RepositoryException ee) {
			// This should never ever happen
		}
	}

	// Same function, but use this if we don't have a RepositoryItem to pass
	// along
	// It's ok to pass along NULL for any of these arguments if they are not
	// appropriate for the call. Just include
	// as much information as possible
	protected void reportError(String itemType, String itemId, String itemName, String message) {
		reportError(itemType, itemId, itemName, message, null);
	}

	protected void reportError(String itemType, String itemId, String itemName, String message, Exception e) {
		if (reportErrorList == null)
			reportErrorList = new TreeMap<String, ArrayList<ReportInfo>>();

		ReportInfo rBean = new ReportInfo(itemType, itemId, itemName, message);
		ArrayList<ReportInfo> beanList = reportErrorList.get(currentFilename);
		if (beanList == null) {
			beanList = new ArrayList<ReportInfo>();
			reportErrorList.put(currentFilename, beanList);
		}
		beanList.add(rBean);

		String logMessage = "Data error reported in feed " + currentFilename + " - type=" + itemType + " : ID=" + itemId
				+ " : Name=" + itemName + " : Message=" + message;
		if (e != null) {
			logError(logMessage, e);
		} else {
			logError(logMessage);
		}
	}

	// Use this method to report an asset-related warning...
	// The reporting of "warning" is completely determined by the implementing
	// loader
	// itemType - the item type that is related to the warning (or null if there
	// is none)
	// itemId - the repository ID of the item that is related to the warning (or
	// null if there is none)
	// itemName - the repository display name of the item that is related to the
	// warning (or null if there is none)
	// message - a message describing the issue that occurred
	// e - a caught Exception... please reserve this for non-fatal exceptions,
	// like parsing errors, etc. Fatal Exceptions should be thrown up the call
	// chain
	// such that the load will rollback
	// It's ok to pass along NULL for any of these arguments if they are not
	// appropriate for the call. Just include
	// as much information as possible
	protected void reportWarning(String itemType, String itemId, String itemName, String message) {
		reportWarning(itemType, itemId, itemName, message, null);
	}

	protected void reportWarning(String itemType, String itemId, String itemName, String message, Exception e) {
		if (reportWarnList == null)
			reportWarnList = new TreeMap<String, ArrayList<ReportInfo>>();

		ReportInfo rBean = new ReportInfo(itemType, itemId, itemName, message);
		ArrayList<ReportInfo> beanList = reportWarnList.get(currentFilename);
		if (beanList == null) {
			beanList = new ArrayList<ReportInfo>();
			reportWarnList.put(currentFilename, beanList);
		}
		beanList.add(rBean);

		String logMessage = "Data warning reported in feed " + currentFilename + " - type=" + itemType + " : ID="
				+ itemId + " : Name=" + itemName + " : Message=" + message;
		if (e != null) {
			logWarning(logMessage, e);
		} else {
			logWarning(logMessage);
		}
	}

	public TreeMap<String, ArrayList<ReportInfo>> getReportErrorList() {
		return reportErrorList;
	}

	public TreeMap<String, ArrayList<ReportInfo>> getReportWarnList() {
		return reportWarnList;
	}

	public TreeMap<String, ArrayList<ReportInfo>> getLockedAssetList() {
		return lockedAssetList;
	}

	// -------------------------------
	// Project and Versioning suport stuff
	// -------------------------------
	private boolean skipLockedAssets = true;
	private AssetFactory assetFactory;
	private String mWorkflowName = "/Content Administration/import.wdl";
	private String mBaseProjectName = "Content Administration Import";
	private String mTaskOutcomeId = "4.1.1";
	private boolean mAdvanceWorkflow = true;
	protected boolean waitForDeployment;
	private String mPersonaPrefix = "Profile$login$";
	private String mUserName = "publishing";
	private WorkflowManager mWorkflowManager = null;
	private UserDirectoryUserAuthority mUserAuthority = null;
	private VersionManager mVersionManager = null;
	private Process mProcess = null;
	protected boolean deploymentComplete = false;
	protected int deploymentWaitInMilliseconds;
	protected int deploymentWaitAttempts;
	private boolean continueImports = true;

	public Process getProcess() {
		return mProcess;
	}

	public void setProcess(Process process) {
		this.mProcess = process;
	}

	public VersionManager getVersionManager() {
		return mVersionManager;
	}

	public void setVersionManager(VersionManager pVersionManager) {
		mVersionManager = pVersionManager;
	}

	public WorkflowManager getWorkflowManager() {
		return mWorkflowManager;
	}

	public void setWorkflowManager(WorkflowManager pWorkflowManager) {
		mWorkflowManager = pWorkflowManager;
	}

	public UserDirectoryUserAuthority getUserAuthority() {
		return mUserAuthority;
	}

	public void setUserAuthority(UserDirectoryUserAuthority pUserAuthority) {
		mUserAuthority = pUserAuthority;
	}

	public String getPersonaPrefix() {
		return mPersonaPrefix;
	}

	public void setPersonaPrefix(String pPersonaPrefix) {
		mPersonaPrefix = pPersonaPrefix;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String pUserName) {
		mUserName = pUserName;
	}

	public AssetFactory getAssetFactory() {
		return assetFactory;
	}

	public void setAssetFactory(AssetFactory assetFactory) {
		this.assetFactory = assetFactory;
	}

	public boolean isSkipLockedAssets() {
		return skipLockedAssets;
	}

	public void setSkipLockedAssets(boolean skipLockedAssets) {
		this.skipLockedAssets = skipLockedAssets;
	}

	public String getWorkflowName() {
		return mWorkflowName;
	}

	public void setWorkflowName(String string) {
		mWorkflowName = string;
	}

	public String getTaskOutcomeId() {
		return mTaskOutcomeId;
	}

	public void setTaskOutcomeId(String pTaskOutcomeId) {
		mTaskOutcomeId = pTaskOutcomeId;
	}

	public String getBaseProjectName() {
		return mBaseProjectName;
	}

	public void setBaseProjectName(String pProjectName) {
		mBaseProjectName = pProjectName;
	}

	public boolean isAdvanceWorkflow() {
		return mAdvanceWorkflow;
	}

	public void setAdvanceWorkflow(boolean pAdvanceWorkflow) {
		this.mAdvanceWorkflow = pAdvanceWorkflow;
	}

	public boolean getWaitForDeployment() {
		return waitForDeployment;
	}

	public void setWaitForDeployment(boolean waitForDeployment) {
		this.waitForDeployment = waitForDeployment;
	}

	public boolean getDeploymentComplete() {
		return deploymentComplete;
	}

	public int getDeploymentWaitInMilliseconds() {
		return deploymentWaitInMilliseconds;
	}

	public void setDeploymentWaitInMilliseconds(int deploymentWaitInMilliseconds) {
		this.deploymentWaitInMilliseconds = deploymentWaitInMilliseconds;
	}

	public int getDeploymentWaitAttempts() {
		return deploymentWaitAttempts;
	}

	public void setDeploymentWaitAttempts(int deploymentWaitAttempts) {
		this.deploymentWaitAttempts = deploymentWaitAttempts;
	}

	public boolean isContinueImports() {
		return continueImports;
	}

	public void setContinueImports(boolean continueImports) {
		this.continueImports = continueImports;
	}

	// Create a project using the base name + a current timestamp
	protected void createProject() throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		String dateString = formatter.format(new Date());
		String projectName = getBaseProjectName() + " - " + dateString;
		logDebug("Creating Project " + projectName);

		assumeUserIdentity();
		ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
		mProcess = processHome.createProcessForImport(projectName, getWorkflowName());
		String wkspName = mProcess.getProject().getWorkspace();
		Workspace wksp = getVersionManager().getWorkspaceByName(wkspName);
		WorkingContext.pushDevelopmentLine(wksp);
		logDebug("Completed Creating Project " + projectName);
	}

	// Delete a project
	protected void deleteProject() throws Exception {
		if (mProcess == null)
			return;
		Project project = mProcess.getProject();
		logDebug("Deleting project " + project.getDisplayName());
		try {
			project.delete("admin");
			mProcess = null;
		} catch (Exception e) {
			logError("Problem deleting project " + project.getDisplayName(), e);
			throw e;
		}
	}

	// Count the list of assets by type in the Project created by the loader
	// This method can be extended if the loader affects assets outside the
	// project, ie. via direct DB calls
	protected TreeMap<String, ChangedAsset> countProjectAssets() {
		return countProjectAssets(null);
	}

	protected TreeMap<String, ChangedAsset> countProjectAssets(Process pProcess) {
		// Count up the number of assets of each type in the project
		logInfo("Counting project assets for task...");
		TreeMap<String, ChangedAsset> assetCounts = new TreeMap<String, ChangedAsset>();
		try {
			if (pProcess != null) {
				Project proj = pProcess.getProject();
				@SuppressWarnings("unchecked")
				Set<WorkingVersion> assets = proj.getAssets();
				for (WorkingVersion asset : assets) {
					RepositoryItem item = asset.getRepositoryItem();
					String itemType = item.getItemDescriptor().getItemDescriptorName();
					ChangedAsset itemCount = assetCounts.get(itemType);
					if (itemCount == null) {
						itemCount = new ChangedAsset();
						itemCount.itemType = itemType;
						itemCount.numChanged = 0;
						assetCounts.put(itemType, itemCount);
					}
					itemCount.numChanged++;
				}
			}
		} catch (Exception e) {
			logError("There was a problem getting the asset counts for the project", e);
		}
		return assetCounts;
	}

	public TreeMap<String, ChangedAsset> getChangedAssets() {
		return changedAssets;
	}

	public void setChangedAssets(TreeMap<String, ChangedAsset> changedAssets) {
		logDebug("Setting changedAssets to " + changedAssets);
		this.changedAssets = changedAssets;
	}

	// -------------------------------------
	/**
	 * This method advances the workflow to the next state. If using an
	 * unaltered copy of the import-late or import-early workflows, then the
	 * taskOutcomeId property should not need to be changed (default is
	 * '4.1.1'). If you are using a different workflow or an altered version of
	 * the import-xxxx workflows, then the taskOutcomeId can be found in the wdl
	 * file for the respective workflow.
	 */
	protected Process deployProject() throws Exception {
		// Now check to see if the project has any assets. If not, then there's
		// nothing to do and we
		// can delete the project
		if (mProcess == null)
			return null;
		Project project = mProcess.getProject();
		logDebug("Scheduling Deployment of Project " + project.getDisplayName());
		@SuppressWarnings("rawtypes")
		Set assets = project.getAssets();
		if (assets == null || assets.size() == 0) {
			logDebug("Attempting to delete empty import project");
			deleteProject();
			return null;
		}

		// Project has assets, so deploy it
		RepositoryItem processWorkflow = mProcess.getProject().getWorkflow();
		String workflowProcessName = processWorkflow.getPropertyValue("processName").toString();
		String subjectId = mProcess.getId();

		try {
			// an alternative would be to use the global workflow view at
			WorkflowView wv = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());

			wv.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT, subjectId,
					getTaskOutcomeId(), ActionConstants.ERROR_RESPONSE_DEFAULT);

		} catch (MissingWorkflowDescriptionException e) {
			logError("There was a problem deploying the project " + mProcess, e);
			throw e;
		} catch (ActorAccessException e) {
			logError("There was a problem deploying the project " + mProcess, e);
			throw e;
		} catch (ActionException e) {
			logError("There was a problem deploying the project " + mProcess, e);
			throw e;
		} catch (UnsupportedOperationException e) {
			logError("There was a problem deploying the project " + mProcess, e);
			throw e;
		}
		return mProcess;
	}

	protected boolean waitForDeployment(Process pProcess) throws InterruptedException, Exception {
		boolean deployed = false;

		logDebug("Waiting for project deployment : " + pProcess.getProject().getDisplayName());

		Project project = pProcess.getProject();
		for (int i = 0; i < deploymentWaitAttempts; i++) {
			if (project.getStatus().equals(ProjectEnumStatus.getCompleted())) {
				break;
			} else {
				logDebug("Waiting attempt " + i + " ... Process is going to sleep for " + deploymentWaitInMilliseconds
						+ " milliseconds");
				Thread.sleep(deploymentWaitInMilliseconds);
			}
		}

		if (project.getStatus().equals(ProjectEnumStatus.getCompleted())) {
			logDebug("Project completed successfully : " + pProcess.getProject().getDisplayName());
			deployed = true;
		} else {
			logError(getBCCDeploymentErrorCode() + ": Automatic project "
					+ " was not successfully completed after all wait attempts: "
					+ pProcess.getProject().getDisplayName(), getBCCDeploymentErrorCode(), null);
			setContinueImports(false);
			throw new Exception(
					"The automatic project not completed after all wait attempts. Check project status in BCC.");
		}
		return deployed;
	}

	// -------------------------------------
	/**
	 * This method sets the security context for the current thread so that the
	 * code executes correctly against secure resources.
	 *
	 * @return true if the identity was assumed, false otherwise
	 */
	protected boolean assumeUserIdentity() {
		if (getUserAuthority() == null)
			return false;

		User newUser = new User();
		Persona persona = (Persona) getUserAuthority().getPersona(getPersonaPrefix() + getUserName());
		if (persona == null)
			return false;

		// create a temporary User object for the identity
		newUser.addPersona(persona);

		// replace the current User object
		ThreadSecurityManager.setThreadUser(newUser);

		return true;
	}

	// -------------------------------------
	/**
	 * This method unsets the security context on the current thread.
	 */
	protected void releaseUserIdentity() {
		ThreadSecurityManager.setThreadUser(null);
	}

	// this method will be called to find out the asset is locked or not
	public boolean isAssetLocked(RepositoryItem asset) {
		// If we're not skipping locked assets, always return false, as we don't
		// care about the lock
		if (!isSkipLockedAssets() || asset == null)
			return false;

		try {
			String itemDescriptorName = asset.getItemDescriptor().getItemDescriptorName();
			String itemId = asset.getRepositoryId();
			VersionManagerURI versionmanageruri = null;
			if (asset.getRepository().getRepositoryName().contains("Price"))
				versionmanageruri = VersionManagerURI.getURI(assetFactory.getVersionManager().getName(), "PriceLists",
						itemDescriptorName, itemId);
			else
				versionmanageruri = VersionManagerURI.getURI(assetFactory.getVersionManager().getName(),
						"CustomProductCatalog", itemDescriptorName, itemId);

			logDebug("Uri String:" + versionmanageruri.getURIString());
			if (versionmanageruri != null) {
				Workspace workspace = assetFactory.lockedBy(versionmanageruri);
				if (workspace != null) {
					return true;
				}
			}
		} catch (RepositoryException e) {
			reportError(asset, "RepositoryException when checking asset lock on asset", e);
		} catch (VersionException e) {
			reportError(asset, "RepositoryException when checking asset lock on asset", e);
		}
		return false;
	}

	// -------------------------------
	// Access to the catalog and helper methods.
	// Every implementation is going to need these.
	// -------------------------------

	private GSARepository catalogRepository;

	public GSARepository getCatalogRepository() {
		return this.catalogRepository;
	}

	public void setCatalogRepository(GSARepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public CustomCatalogTools catalogTools;

	public CustomCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(CustomCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	private GSARepository catalogRepositoryProduction;

	public GSARepository getCatalogRepositoryProduction() {
		return this.catalogRepositoryProduction;
	}

	public void getCatalogRepositoryProduction(GSARepository catalogRepository) {
		this.catalogRepositoryProduction = catalogRepository;
	}

	// -------------------------------
	// Feed file handling
	// -------------------------------

	private String defaultInputFileDir;
	protected ArrayList<File> mFeedFiles = null;

	public ArrayList<File> getFeedFiles() {
		return mFeedFiles;
	}

	public void setFeedFiles(ArrayList<File> pFeedFiles) {
		this.mFeedFiles = pFeedFiles;
	}

	public void setDefaultInputFileDir(String defaultInputFileDir) {
		this.defaultInputFileDir = defaultInputFileDir;
	}

	public String getDefaultInputFileDir() {
		return defaultInputFileDir;
	}

	/*
	 * public ArrayList<File> getListFeedFiles() { return findFeedFiles(); }
	 */

	protected ArrayList<File> findFeedFiles() {
		// Determine if there are files to be processed...
		ArrayList<File> feedFiles = null;
		if (getDefaultInputFileDir() == null)
			return null;

		File dir = new File(getDefaultInputFileDir());
		if (!dir.exists() || !dir.isDirectory()) {
			// Either dir does not exist or is not a directory
			logDebug("Directory doesn't exist at: " + getDefaultInputFileDir());
		} else {
			File[] children = dir.listFiles();
			if (children != null) {
				for (File file : children) {
					if (file.isFile()) {
						if (loadThisFile(file)) {
							if (feedFiles == null) {
								feedFiles = new ArrayList<File>();
							}
							feedFiles.add(file);
						}
					}
				}
			}
		}
		return feedFiles;
	}

	protected void archiveFeedFiles() {
		if (mFeedFiles != null) {
			for (File file : mFeedFiles) {
				archiveFeedFile(file);
			}
		}
	}

	protected void resetFeedFiles() {
		// If there is a problem, then this returns the feed assets to their
		// original state for reprocessing
	}

	// Default behavior is simply to rename the file to a subdirectory called
	// "archive" of the directory
	// that it is already in. Feel free to override, as needed...
	protected void archiveFeedFile(File file) {
		// Rename the file to mark it as processed
		logDebug("Archiving feed file " + file.getAbsolutePath());
		String newFile = file.getParent() + "/archive/" + file.getName();
		logDebug("Renaming feed file to " + newFile);
		file.renameTo(new File(newFile));
	}

	public class ChangedAsset {
		public String itemType;
		public int numChanged;

		public String toString() {
			return "[" + itemType + "=" + numChanged + "]";
		}
	}

	// ================

	private boolean disableEmail;

	public boolean getDisableEmail() {
		return disableEmail;
	}

	public void setDisableEmail(boolean pDisableEmail) {
		disableEmail = pDisableEmail;
	}

	public List<String> loadFilesToSharedLocation() {
		return null;
	}

	public void archiveFilesInFTPLocation(List<String> fileNames) {

	}

}
