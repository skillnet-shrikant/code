package mff.logging;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import atg.nucleus.GenericService;
import atg.service.ServerName;
import mff.MFFException;

import com.codahale.metrics.SlidingTimeWindowReservoir;

public class Logger extends GenericService {

  // static instance
  private static Logger instance;

  // member vars
  
  // map of message code to how many times that message code has been seen in the past X minutes
  private HashMap<String, SlidingTimeWindowReservoir> logEntryReservoirs;
  
  // properties
  private boolean logToDB;
  private DataSource dataSource;
  private ServerName serverName;
  private String dbLoggerPackageName;
  private int throttlingNumberOfMessages;
  private int timeWindowInMinutes;

  @Override
  public void doStartService() {
    if (Logger.instance != null) {
      throw new RuntimeException("Cannot start Logger service with name: " + getAbsoluteName() + ". A Logger service with name " + Logger.instance.getAbsoluteName() + " is already instantiated");
    } else {
    	System.out.println("Logger is NULL");
    }

    Logger.instance = this;
    
    logEntryReservoirs = new HashMap<String, SlidingTimeWindowReservoir>();
  }
  
  /**
   * Static instance accessor
   * @return
   * @throws MFFException
   */
  public static Logger getInstance() throws MFFException {
    if (Logger.instance == null) {
      throw new MFFException("Logger instance not yet instantiated. Perhaps the service is still starting up?");
    } else {
    	System.out.println("Logger.getInstance != null");
    }
    return Logger.instance;
  }
  
  /**
   * Factory method for creating log entries.  Any non-primitive parameters may be null.
   * @param componentName
   * @param logLevel
   * @param messageCode - required.  must not be null
   * @param message
   * @param exception
   * @return LogEntry
   */
  public LogEntry createLogEntry(String componentName, int logLevel, String messageCode, String message, Exception exception) throws MFFException {
    if (messageCode == null) {
      throw new MFFException("messageCode must not be null");
    }
    
    return new LogEntry(getServerName().serverName(), componentName, logLevel, messageCode, message, exception);
  }

  /**
   * Write the given logEntry to the DB now
   * @param logEntry
   * @param now
   */
  public void log(LogEntry logEntry) {
    log(logEntry, true);
  }
  
  /**
   * Write the given log entry to the configured destinations (file and/or DB).
   * 
   * @param logEntry
   * @param now
   *          If true, the log entry is written to the DB immediately (if
   *          applicable). If false, the entry is added to the DB logger's
   *          queueing system.
   */
  public void log(LogEntry logEntry, boolean now) {

    // always write the log entry to the log file
    writeLogEntryToFile(logEntry);

    if (isLogToDB()) {
      
      // write the logEntry to the logEntryReservoir for this messageCode
      // don't write the entry if there's already X entries in the reservoir - this prevents runaway logging
      SlidingTimeWindowReservoir logEntryReservoir = getLogEntryReservoirForMessageCode(logEntry.getMessageCode());
      
      if (isLoggingDebug()) {
        logDebug("there are " + logEntryReservoir.size() + " messages in the reservoir for that messageCode in the time period");
      }
      
      if (logEntryReservoir.size() <= getThrottlingNumberOfMessages()) {
        if (isLoggingDebug()) {
          logDebug("adding message with messageCode " + logEntry.getMessageCode() + " to reservoir");
        }
        logEntryReservoir.update(1);
        writeLogEntryToDB(logEntry, now);
      } else {
        if (isLoggingDebug()) {
          logDebug("The message code " + logEntry.getMessageCode() + " has been seen more than " 
              + throttlingNumberOfMessages + " in the last " + getTimeWindowInMinutes() + " minutes. No log will be written to the DB");
        }
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("logging to DB is disabled. cannot write to DB logfile");
      }
    }
    
  }

  /**
   * Purge log entries in the DB (if isLogToDB is true).  No component name filtering is done
   * @param numberOfDays
   */
  public void purge(int numberOfDays) {
    purge(numberOfDays, null);
  }
  
  /**
   * purge all log entries from the DB (if isLogToDB is true) whose timestamps are before the given date
   * 
   * @param purgeBefore
   * @param componentName only purge log entries whose componentName exactly matches the given string.  May be null
   */
  public void purge(int numberOfDays, String componentName) {
    // we can only purge DB logs
    if (isLogToDB()) {
      DataSource ds = getDataSource();
      PreparedStatement ps = null;
      
      try (Connection conn = ds.getConnection()) {
        ps = preparePurgeStatement(conn, numberOfDays, componentName);
        ps.execute();

      } catch (Exception ex) {
        vlogError(ex, "Failed to store log entry.");
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("logging to DB is disabled. cannot purge");
      }
    }
  }
  
  /**
   * Flush the log buffer in the DB (if isLogToDB is true)
   * The buffer will be flushed automatically every so often.  If you want this to happen immediately, call this method
   */
  public void flush() {
    if (isLogToDB()) {
      DataSource ds = getDataSource();
      PreparedStatement ps = null;
      
      try (Connection conn = ds.getConnection()) {
        ps = prepareFlushStatement(conn);
        ps.execute();

      } catch (Exception ex) {
        vlogError(ex, "Failed to store log entry.");
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("logging to DB is disabled. cannot flush");
      }
    }
  }
  
  private void writeLogEntryToDB(LogEntry logEntry, boolean now) {
    DataSource ds = getDataSource();
    PreparedStatement ps = null;
    
    try (Connection conn = ds.getConnection()) {
      ps = prepareLogStatement(conn, logEntry, now);
      ps.execute();

    } catch (Exception ex) {
      vlogError(ex, "Failed to store log entry to DB");
    }
  }
  
  private PreparedStatement prepareLogStatement(Connection conn, LogEntry logEntry, boolean now) throws SQLException {
    String functionName = now ? getDbLoggerPackageName() + ".log_msg" : getDbLoggerPackageName() + ".add_msg";
    
    PreparedStatement ps = conn.prepareStatement("call " + functionName + "(?,?,?,?,?,?)");
    
    if (getServerName() == null || getServerName().getServerName() == null)
      ps.setNull(1, java.sql.Types.VARCHAR);
    else
      ps.setString(1, getServerName().getServerName()); // server name
    
    if (logEntry.getComponentName() == null)
      ps.setNull(2, java.sql.Types.VARCHAR);
    else
      ps.setString(2, logEntry.getComponentName()); // component name
    
    ps.setInt(3, logEntry.getLogLevel()); // log_level
    
    if (logEntry.getMessageCode() == null)
      ps.setNull(4, java.sql.Types.VARCHAR);
    else
      ps.setString(4, logEntry.getMessageCode()); // msg_code
    
    if (logEntry.getMessage() == null)
      ps.setNull(5, java.sql.Types.VARCHAR);
    else
      ps.setString(5, logEntry.getMessage()); // message
    
    if (logEntry.getException() == null)
      ps.setNull(6, java.sql.Types.CLOB);
    else {
      Clob clob = conn.createClob();
      clob.setString(1, ExceptionUtils.getStackTrace(logEntry.getException()));
      ps.setClob(6, clob); // stack_trace
    }
    return ps;
  }
  
  private PreparedStatement preparePurgeStatement(Connection conn, int numberOfDays, String componentName) throws SQLException {
    PreparedStatement ps = conn.prepareStatement("call " + getDbLoggerPackageName() +".purge(?,?)");
    
    ps.setInt(1, numberOfDays);
    
    if (componentName == null) {
      ps.setNull(2, java.sql.Types.VARCHAR);
    } else {
      ps.setString(2, componentName);
    }
    
    return ps;
  }
  
  private PreparedStatement prepareFlushStatement(Connection conn) throws SQLException {
    return conn.prepareStatement("call " + getDbLoggerPackageName() +".flush()");
  }

  private void writeLogEntryToFile(LogEntry logEntry) {
    String formattedLogEntry = formatLogEntry(logEntry);
    switch (logEntry.getLogLevel()) {
    case LogLevel.DEBUG:
      vlogDebug(logEntry.getException(), formattedLogEntry);
      break;
    case LogLevel.INFO:
      vlogInfo(logEntry.getException(), formattedLogEntry);
      break;
    case LogLevel.WARN:
      vlogWarning(logEntry.getException(), formattedLogEntry);
      break;
    case LogLevel.ERROR:
      vlogError(logEntry.getException(), formattedLogEntry);
      break;
    default:
      vlogError("Bad LogLevel: " + logEntry.getLogLevel());
    }
  }

  private String formatLogEntry(LogEntry logEntry) {
    String ret = "";
    ret += "Component Name: " + logEntry.getComponentName() + "\n";
    ret += "Message Code: " + logEntry.getMessageCode() + "\n";
    ret += "Message: " + logEntry.getMessage() + "\n";
    return ret;
  }
  
  private SlidingTimeWindowReservoir getLogEntryReservoirForMessageCode(String messageCode) {
    if (logEntryReservoirs.get(messageCode) == null) {
      if (isLoggingDebug()) {
        logDebug("creating new reservoir for message code " + messageCode + " and over " + getTimeWindowInMinutes() + " minutes");
      }
      synchronized (logEntryReservoirs) {
        // better check again in case someone beat us to it while we were waiting
        if (logEntryReservoirs.get(messageCode) == null) {
          logEntryReservoirs.put(messageCode, new SlidingTimeWindowReservoir(getTimeWindowInMinutes(), TimeUnit.MINUTES));
        }
      }
    }
    return logEntryReservoirs.get(messageCode); 
  }
  
  // Properties
  
  public boolean isLogToDB() {
    return logToDB;
  }

  public void setLogToDB(boolean logToDB) {
    this.logToDB = logToDB;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public ServerName getServerName() {
    return serverName;
  }

  public void setServerName(ServerName serverName) {
    this.serverName = serverName;
  }

  public String getDbLoggerPackageName() {
    return dbLoggerPackageName;
  }

  public void setDbLoggerPackageName(String dbLoggerPackageName) {
    this.dbLoggerPackageName = dbLoggerPackageName;
  }

  public int getThrottlingNumberOfMessages() {
    return throttlingNumberOfMessages;
  }

  public void setThrottlingNumberOfMessages(int throttlingNumberOfMessages) {
    this.throttlingNumberOfMessages = throttlingNumberOfMessages;
  }

  public int getTimeWindowInMinutes() {
    return timeWindowInMinutes;
  }

  public void setTimeWindowInMinutes(int timeWindowInMinutes) {
    this.timeWindowInMinutes = timeWindowInMinutes;
  }

  /**
   * You have to use the factory method of Logger to instantiate this class
   * 
   * @author grahammather
   * 
   */
  public class LogEntry {
    private String serverName;
    private String componentName;
    private int logLevel;
    private String messageCode;
    private String message;
    private Exception exception;
    
    private LogEntry(String serverName, String componentName, int logLevel, String messageCode, String message, Exception exception) {
      this.serverName = serverName;
      this.componentName = componentName;
      this.logLevel = logLevel;
      this.messageCode = messageCode;
      this.message = message;
      this.exception = exception;
    }

    public String getServerName() {
      return serverName;
    }

    public void setServerName(String serverName) {
      this.serverName = serverName;
    }

    public String getComponentName() {
      return componentName;
    }

    public void setComponentName(String componentName) {
      this.componentName = componentName;
    }

    public int getLogLevel() {
      return logLevel;
    }

    public String getMessageCode() {
      return messageCode;
    }

    public String getMessage() {
      return message;
    }

    public Exception getException() {
      return exception;
    }

  }
}
