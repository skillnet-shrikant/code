package oms.commerce.salesaudit.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;

/**
 * This class will perform all of the file handling for the sales audit extract 
 * file.
 * 
 * @author jvose
 *
 */
public class SalesAuditFile 
  extends GenericService {
  
  private String mFilename;                 // File name built from the properties
  private BufferedWriter mBufferedWriter;   // Buffered writer for file I/O

  /**
   * Creates the extract file name for the Sales Audit file.
   * 
   */
  public void createExtractFile () {
    SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(getDateFormat());
    Date lNow       = new Date();
    String lDate    = lSimpleDateFormat.format(lNow);
    mFilename       = getOutputDirectory() + File.separator + getFilenamePrefix() + lDate + getFilenameSuffix();
  }
  
  /**
   * Open the extract file for output.
   * @throws IOException 
   */
  public void openExtractFile () 
    throws IOException {
    vlogDebug ("Opening Sales Audit File " + mFilename + " for output");   
    mBufferedWriter = new BufferedWriter(new FileWriter(mFilename));
  }
  
  /**
   * Write a record to the extract file.  The write will add an environment
   * specific line terminator.
   * 
   * @param pRecord   Record to be written
   * @throws IOException
   */
  public void writeExtractFile (String pRecord) 
    throws IOException {
    mBufferedWriter.write (pRecord + System.getProperty("line.separator"));
    mBufferedWriter.flush();
  }

  /**
   * Write an array of records to the pick file.
   * @param pRecords    Array of pick record strings
   * @throws IOException
   */
  public void writeExtractFile (ArrayList <String> pRecords) 
    throws IOException {
    for (String lRecord : pRecords) {
      mBufferedWriter.write (lRecord + System.getProperty("line.separator"));
      mBufferedWriter.flush();
    }
  }
  
  /**
   * Close the extract file.
   * @throws IOException
   */
  public void closeExtractFile () 
    throws IOException {
    mBufferedWriter.close();
  }
  
  /**
   * Get the name of the constructed file name
   * @return    File name
   */
  public String getFileName () {
    return mFilename;
  }
  
  /**
   * Move the file to the final location where the file will be picked up
   * by the client.  The files are created in a separate directory in order
   * to prevent Sales Audit from pulling partial files.
   * 
   * @return
   *      true - File move was successful
   *      false - File move failed
   */
  public boolean moveToDoneDirectory () {
    boolean lReturn = false;
    
    // Get the current file to be moved
    File lFile = new File(mFilename);
    
    // Copy file to the done directory
    if (StringUtils.isBlank(getDoneDirectory())) {
      vlogError("Done directory is not specified - Unable to move the file");
      return lReturn;
    }
    File archiveDir = new File(getDoneDirectory());
    if (!archiveDir.exists()) {
      vlogError("Done directory " + getDoneDirectory() + " does not exist - Unable to move file");
      return lReturn;
    }
    String lOrigFileName    = lFile.getName();
    String newFullFileName  = getDoneDirectory() + File.separator + lOrigFileName;
    File lNewFile           = new File(newFullFileName);
    lReturn                 = lFile.renameTo(lNewFile); 
    if (!lReturn) {
      vlogError ("Unable to move file from {0} to {1}", lFile.getName(), lNewFile.getName());
      return lReturn;
    }    
    return true;
  }

  
  // ***************************************************************
  //              Getter/Setter Methods 
  // ***************************************************************
  String mFilenamePrefix;
  public String getFilenamePrefix() {
    return mFilenamePrefix;
  }
  public void setFilenamePrefix(String pFilenamePrefix) {
    this.mFilenamePrefix = pFilenamePrefix;
  }

  String mDateFormat;
  public String getDateFormat() {
    return mDateFormat;
  }
  public void setDateFormat(String pDateFormat) {
    this.mDateFormat = pDateFormat;
  }

  String mFilenameSuffix;
  public String getFilenameSuffix() {
    return mFilenameSuffix;
  }
  public void setFilenameSuffix(String pFilenameSuffix) {
    this.mFilenameSuffix = pFilenameSuffix;
  }
  
  String mOutputDirectory;
  public String getOutputDirectory() {
    return mOutputDirectory;
  }
  public void setOutputDirectory(String pOutputDirectory) {
    this.mOutputDirectory = pOutputDirectory;
  }
  
  String mDoneDirectory;
  public String getDoneDirectory() {
    return mDoneDirectory;
  }
  public void setDoneDirectory(String pDoneDirectory) {
    this.mDoneDirectory = pDoneDirectory;
  }
  
  String mArchiveDirectory;
  public String getArchiveDirectory() {
    return mArchiveDirectory;
  }
  public void setArchiveDirectory(String pArchiveDirectory) {
    this.mArchiveDirectory = pArchiveDirectory;
  }
  
}
