package com.mff.search;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mff.util.SMTPEmailUtil;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

public class SuggestedTermsReportMailerService extends SingletonSchedulableService {

  
  @Override
  public void doScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
    if(isEnabled()) {
      vlogInfo("{0} is Enabled - starting",getJobName() );
      runTheJob();
    }else {
      vlogInfo("{0} is disabled",getJobName() );
    }
  }

  public void runTheJob() {
    vlogInfo("{0} is Started --",getJobName() );
    DateFormat lDateFormat = new SimpleDateFormat("-yyyy_MM_dd");
    String lFilenameWithPath = new StringBuilder().append(getFilePath())
                                                  .append(getFileNamePrefix())
                                                  .append(lDateFormat.format(new Date()))
                                                  .append(getFilePostfix())
                                                  .toString();
    boolean isReportCreated = getSuggestedTermsReportGenerator().createReportFile(lFilenameWithPath);
    if(isReportCreated) {
      vlogInfo("{0} -- Report File created successfully -- {1}",getJobName(),lFilenameWithPath);
      File[] lFiles = new File[1];
      lFiles[0]=new File(lFilenameWithPath);
      if(lFiles[0].exists()) {
        boolean isSentEmail = getEmailUtil().sendReportEmail(new StringBuilder().append(getFileNamePrefix()).append(lDateFormat.format(new Date())).toString(),
                                            new StringBuilder().append(getFileNamePrefix()).append(lDateFormat.format(new Date())).toString(),
                                            null,
                                            null,
                                          lFiles);
        if(isSentEmail) {
          vlogInfo("{0} email sent successfullly --",getJobName() );
        }else {
          vlogError("{0} -- Email not sent",getJobName());
        }
        vlogInfo("{0} Cleaning report file {1}",getJobName(), lFilenameWithPath);
        boolean isFileDeleted = lFiles[0].delete();
        if(isFileDeleted) {
          vlogInfo("{0} Cleaning report file {1} -- successful",getJobName(),lFilenameWithPath );
        }else {
          vlogInfo("{0} Cleaning report file {1} -- failed",getJobName(),lFilenameWithPath );
        }
        
      }else {
        vlogError("{0} -- Report File Not created {1} file doesn't exist",getJobName(), lFilenameWithPath);
      }
    }else {
      vlogError("{0} -- Report File Not created",getJobName());
    }
    vlogInfo("{0} is Ended --",getJobName() );
  }

  public void testEmail() {
    DateFormat lDateFormat = new SimpleDateFormat("-yyyy_MM_dd");
    String lFilenameWithPath = new StringBuilder().append(getFilePath())
                                                  .append(getFileNamePrefix())
                                                  .append(lDateFormat.format(new Date()))
                                                  .append(getFilePostfix())
                                                  .toString();
    File[] lFiles = new File[1];
    lFiles[0]=new File(lFilenameWithPath);
    getEmailUtil().sendReportEmail(new StringBuilder().append(getFileNamePrefix()).append(lDateFormat.format(new Date())).toString(),
                                        new StringBuilder().append(getFileNamePrefix()).append(lDateFormat.format(new Date())).toString(),
                                        null,
                                        null,
                                      lFiles);
  }
  

  private boolean mEnabled;
  private SuggestedTermsReportGenerator mSuggestedTermsReportGenerator;
  private String mFilePath = "/tmp/";
  private String mFileNamePrefix = "SuggestedTermsReport";
  private String mFilePostfix = ".csv";
  private SMTPEmailUtil mEmailUtil;
  
  public boolean isEnabled() {
    return mEnabled;
  }

  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  public SuggestedTermsReportGenerator getSuggestedTermsReportGenerator() {
    return mSuggestedTermsReportGenerator;
  }

  public void setSuggestedTermsReportGenerator(SuggestedTermsReportGenerator pSuggestedTermsReportGenerator) {
    mSuggestedTermsReportGenerator = pSuggestedTermsReportGenerator;
  }

  public String getFilePath() {
    return mFilePath;
  }

  public void setFilePath(String pFilePath) {
    mFilePath = pFilePath;
  }

  public String getFileNamePrefix() {
    return mFileNamePrefix;
  }

  public void setFileNamePrefix(String pFileNamePrefix) {
    mFileNamePrefix = pFileNamePrefix;
  }

  public String getFilePostfix() {
    return mFilePostfix;
  }

  public void setFilePostfix(String pFilePostfix) {
    mFilePostfix = pFilePostfix;
  }

  public SMTPEmailUtil getEmailUtil() {
    return mEmailUtil;
  }

  public void setEmailUtil(SMTPEmailUtil pEmailUtil) {
    mEmailUtil = pEmailUtil;
  }

 
}
