package com.mff.util.alerts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import javax.sql.DataSource;

import com.mff.util.SMTPEmailUtil;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.CalendarSchedule;
import atg.service.scheduler.PeriodicSchedule;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

public class AlertQueryScheduler extends GenericService implements Schedulable{

	Scheduler scheduler;
	String alertJobName;
	String alertScheduleString;
	GSARepository alertQueryRepository;
	boolean enabled;
	DataSource dataSource;
	String alertId;
	SMTPEmailUtil smtpEmailUtil;

	public String getAlertId() {
		return alertId;
	}
	public void setAlertId(String pAlertId) {
		alertId = pAlertId;
	}
	public GSARepository getAlertQueryRepository() {
		return alertQueryRepository;
	}
	public void setAlertQueryRepository(GSARepository pAlertQueryRepository) {
		alertQueryRepository = pAlertQueryRepository;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean pEnabled) {
		enabled = pEnabled;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource pDataSource) {
		dataSource = pDataSource;
	}
	public SMTPEmailUtil getSmtpEmailUtil() {
		return smtpEmailUtil;
	}
	public void setSmtpEmailUtil(SMTPEmailUtil pSmtpEmailUtil) {
		smtpEmailUtil = pSmtpEmailUtil;
	}


	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler pScheduler) {
		scheduler = pScheduler;
	}
	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		if(isEnabled()) {
			addMonitoringJobs();
		} else {
			logInfo("Exiting... service is not enabled");
		}
	}

	@Override
	public void performScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
		if(isLoggingDebug()) {
			logDebug("Execute query with job name " + pJob.getJobName());
		}

		RepositoryItem alertQueryItem = getAlertQueryByJobName(pJob.getJobName());
		if(alertQueryItem != null) {
			String alertQuery 		= (String) 	alertQueryItem.getPropertyValue("alertQuery");
			String senderEmail		= (String) 	alertQueryItem.getPropertyValue("senderEmail");
			String emailDistro		= (String) 	alertQueryItem.getPropertyValue("emailDistro");
			String scheduleString 	= (String) 	alertQueryItem.getPropertyValue("scheduleString");
			boolean enabled 		= (Boolean) alertQueryItem.getPropertyValue("enabled");
			String emailSubject 	= (String) 	alertQueryItem.getPropertyValue("emailSubject");
			String emailBody 		= (String) 	alertQueryItem.getPropertyValue("emailBody");

			if(enabled) {
				if(isLoggingDebug()) {
					logDebug("**************** Alert Query " 	+ alertQuery);
					logDebug("**************** Send email to " 	+ emailDistro);
					logDebug("**************** Schedule " 		+ scheduleString);
				}
				executeQuery(alertQuery, senderEmail, emailDistro, emailSubject, emailBody);
			} else {
				if(isLoggingError()) {
					logError("Alert item with jobName " + pJob.getJobName() + " is not enabled. Exiting....");
				}
				return;
			}
		} else {
			if(isLoggingError()) {
				logError("Alert Query Item not found for job name " + pJob.getJobName() + ". Exiting.....");
			}
			return;
		}
	}

	public RepositoryItem getAlertQueryByJobName (String pJobName) {
		try {
			if(isLoggingDebug()) {
				logDebug("Looking for alert item with jobName = " + pJobName);
			}
			RepositoryView alertQueryView = getAlertQueryRepository().getView("alertQuery");
			RqlStatement stmtJobName = RqlStatement.parseRqlStatement("jobName = ?0");
			Object queryParams[] = {new String(pJobName)};

			RepositoryItem [] alertQueryItems = stmtJobName.executeQuery (alertQueryView, queryParams);

			if(isLoggingDebug()) {
				if(alertQueryItems != null) {
					logDebug("Found " + alertQueryItems.length + " items.");
				} else {
					logDebug("No records found.");
				}
			}

			for(RepositoryItem alertQueryItem : alertQueryItems) {
				return alertQueryItem;
			}
			return null;

		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError(e);
			}
			return null;
		}
	}

	public RepositoryItem getAlertQueryById (String pAlertId) {
		try {
			if(isLoggingDebug()) {
				logDebug("Looking for alert item with id = " + pAlertId);
			}
			RepositoryView alertQueryView = getAlertQueryRepository().getView("alertQuery");
			RqlStatement stmtJobName = RqlStatement.parseRqlStatement("id = ?0");
			Object queryParams[] = {new String(pAlertId)};

			RepositoryItem [] alertQueryItems = stmtJobName.executeQuery (alertQueryView, queryParams);

			if(isLoggingDebug()) {
				if(alertQueryItems != null) {
					logDebug("Found " + alertQueryItems.length + " items.");
				} else {
					logDebug("No records found.");
				}
			}

			for(RepositoryItem alertQueryItem : alertQueryItems) {
				return alertQueryItem;
			}
			return null;

		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError(e);
			}
			return null;
		}
	}

	public void addMonitoringJobs() {
		// Loop thru' repository item
		// for each entry
		// call
		try {

			if(isLoggingDebug()) {
				logDebug("Querying for enabled alert jobs to be scheduled");
			}
			RepositoryView alertQueryView = getAlertQueryRepository().getView("alertQuery");
			RqlStatement stmtEnabledJobs = RqlStatement.parseRqlStatement("enabled = ?0");
			Object queryParams[] = {new Boolean(Boolean.TRUE)};

			RepositoryItem [] alertQueryItems = stmtEnabledJobs.executeQuery (alertQueryView, queryParams);
			if(isLoggingDebug()) {
				if(alertQueryItems != null) {
					logDebug("Found " + alertQueryItems.length + " items.");
				} else {
					logDebug("No records found.");
				}
			}
			if(alertQueryItems != null) {
  			for(RepositoryItem alertQueryItem : alertQueryItems) {
  				if(isLoggingDebug()) {
  					logDebug("Adding alert job with id " + alertQueryItem.getRepositoryId());
  				}
  				addMonitoringJob(alertQueryItem);
  			}
			}

		} catch (RepositoryException e) {
			if(isLoggingError()) {
				logError(e);
			}
		}
	}

	public void executeQuery(String pQuery, String senderEmail, String pEmailDistro, String pEmailSubject, String pEmailBody) {
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      conn = getDataSource().getConnection();
	      stmt = conn.createStatement();

	      String emailBody = pEmailBody;
	      String headers="";
	      String results="";
	      ResultSet rset = stmt.executeQuery(pQuery);
	      ResultSetMetaData rsMetaData = rset.getMetaData();
	      int colCount = rsMetaData.getColumnCount();
	      
	      for(int i=0; i < colCount; i++) {
	    	  if(headers.equalsIgnoreCase("")) {
	    		  headers = rsMetaData.getColumnName(i+1);
	    	  } else {
	    		  headers = headers + "|" + rsMetaData.getColumnName(i+1);
	    	  }
	      }
	      headers=headers+"\n\n";
	      
	      if(rset.next()) {
	    	  do {
		    	  for (int col=1; col <= colCount; col++) {
		    		  Object value = rset.getObject(col);
		    		  if (value != null) {
		    			  if(results.equalsIgnoreCase("")) {
		    				  results = value.toString() + "|";
		    			  } else {
		    				  if(col < colCount) {
		    					  results = results + value.toString() + "|";
		    				  } else {
		    					  results = results + value.toString();
		    				  }
		    			  }
		    		  }
		    	  }
		    	  results = results + "\n";	    		  
	    	  } while(rset.next());
	    	  emailBody = emailBody + "\n\n\n" + headers + results ;
	          getSmtpEmailUtil().sendEmailAlertForJob(pEmailSubject,emailBody, senderEmail, pEmailDistro);

	      }

	    } catch (SQLException e) {
	      vlogError(e, "SQL Exception occurred while executing the following sql {0}",pQuery);
	    } finally {
	      try {
	        if (stmt != null) {
	          stmt.close();
	        }

	        if (conn != null) {
	          conn.close();
	        }
	      } catch (Throwable e) {
	        vlogError(e, "Error occurred while closing the connection");
	      }
	    }
	}

	public void addMonitoringJob(RepositoryItem pAlertQuery) {
		String  jobName 			= (String) pAlertQuery.getPropertyValue("jobName");
		String  jobDesc 			= (String) pAlertQuery.getPropertyValue("jobDesc");
		String  scheduleString 		= (String) pAlertQuery.getPropertyValue("scheduleString");
		Boolean calendarSchedule 	= (Boolean) pAlertQuery.getPropertyValue("calendarSchedule");

		Schedule schedule=null;
		if(calendarSchedule) {
			schedule = new CalendarSchedule(scheduleString,Locale.US);
		} else {
			schedule = new PeriodicSchedule(scheduleString,Locale.US);
		}
		
		ScheduledJob job = new ScheduledJob(jobName, jobDesc, getAbsoluteName(), schedule, this, false);
		int jobId = getScheduler().addScheduledJob(job);
		if(isLoggingDebug()) {
			logDebug("Added alert query with id " + pAlertQuery.getRepositoryId());
			logDebug("Created job id is " + jobId);
		}
	}

	public void addOneOffJob() {

		if(isEnabled()) {
			if(getAlertId() == null) {
				if(isLoggingError()) {
					logError("alertId attribute of this component has not been set. Exiting....");
					return;
				}
			} else {
				if(isLoggingDebug()) {
					logDebug("adding one off job for alert id " + getAlertId());
				}
				RepositoryItem alertQueryItem = getAlertQueryById(getAlertId());
	
				if(alertQueryItem != null) {
					addMonitoringJob(alertQueryItem);
				} else {
					if(isLoggingError()) {
						logError("No items found for alert id " + getAlertId());
					}
				}
			}
		} else {
			logInfo("Exiting.... service is not enabled");
		}

	}

}
