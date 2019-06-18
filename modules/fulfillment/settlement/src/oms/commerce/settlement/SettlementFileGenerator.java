package oms.commerce.settlement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.aci.utils.SettlementGenerator;

import atg.commerce.CommerceException;
import atg.nucleus.GenericService;

/**
 * The following class encapsulates and stores a reference to the file that is
 * being generated for settlement. The assumption is that only one process runs
 * the settlement process at any given point of time.
 * 
 * The sequence in which the API needs to be called is the following
 * 
 * @author savula
 *
 */
public class SettlementFileGenerator extends GenericService implements SettlementGenerator {

  private String mOutputDirectory;
  private PrintWriter mPrintWriter;
  private String mFilenamePrefix;
  private String mFileExtension;

  private String mFileName = null;
  private boolean __filedescriptorOpen = false;
  private HashMap<String, Integer> mSequence = new HashMap<>();

  /**
   * Initialize the PrintWriter for writing if it is not open and returns the
   * name of the file
   * 
   * @return the name of the file
   * @throws CommerceException
   */
  public String getCurrentFilename() throws CommerceException {
    if (!__filedescriptorOpen) {

      try {
        initializeFileOutputStream();
      } catch (IOException e) {
        vlogWarning(e, "Could not open the file for writing {0}", mFileName);
        throw new CommerceException(e);
      }

    }
    return mFileName;
  }

  /**
   * Returns the print writer
   * 
   * @return the print
   * @throws CommerceException
   */
  public PrintWriter getPrintWriter() throws CommerceException {
    
    vlogDebug("Entering getFileOutputStream : ");
    
    if (!__filedescriptorOpen) {
      new CommerceException("File has not been opened for writing. This API should only be called within the context of the SettlementManager.processSettlementRecords");
    }
    
    if (mPrintWriter == null) {
      try {
        initializeFileOutputStream();
      } catch (IOException e) {
        vlogError(e, "Could not create the file named {0}", mFileName);
        throw new CommerceException(e);
      }
    }

    vlogDebug("Exiting getFileOutputStream : ");
    
    return mPrintWriter;
  }

  /**
   * The following method keeps track of the number of Trigger files that was
   * generated for the day. This is used to generate the name of the trigger
   * file that is generated which needs to be unique for the day.
   * 
   * @return a 4 digit number prepaded with zeroes
   */
  protected String getNextSequence() {
    Date date = new Date();
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String hashMapKey = sf.format(date);
    int nextSeqVal = 0;
    if (mSequence.containsKey(hashMapKey)) {

      Integer currentValue = mSequence.get(hashMapKey);
      nextSeqVal = currentValue.intValue() + 1;
    }
    mSequence.clear();
    mSequence.put(hashMapKey, new Integer(nextSeqVal));
    return String.format("%04d", new Integer(nextSeqVal));

  }

  /**
   * The following opens a PrintWriter for writing to the file.
   * 
   * @throws IOException
   */
  protected void initializeFileOutputStream() throws IOException {
    
    vlogDebug("Entering initializeFileOutputStream : ");
    
    if (mPrintWriter != null) {
      vlogWarning("Filename yet to be closed please ensure that you close the output stream filename = {0}", mFileName);
    }
    
    Date date = new Date();
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    StringBuilder sb = new StringBuilder();
    sb.append(mOutputDirectory);
    sb.append(File.separator);
    sb.append(mFilenamePrefix);
    sb.append(sf.format(date));
    sb.append(getNextSequence());
    sb.append(mFileExtension);
    mFileName = sb.toString();
    
    vlogDebug("Filename set to {0}", mFileName);
    
    mPrintWriter = new PrintWriter(new FileWriter(mFileName));
    
    __filedescriptorOpen = true;
    vlogDebug("Exiting initializeFileOutputStream : ");
  }

  /**
   * The following method closes the current files
   */
  public void closeWriter() {

    try {
      if (mPrintWriter != null) mPrintWriter.close();
    } catch (Throwable e) {
      vlogError(e, "Error occurred while closing the file for writing");
    }

    mPrintWriter = null;
    mFileName = null;
    __filedescriptorOpen = false;
  }

  public String getOutputDirectory() {
    return mOutputDirectory;
  }

  public void setOutputDirectory(String pOutputDirectory) {
    mOutputDirectory = pOutputDirectory;
  }

  public String getFilenamePrefix() {
    return mFilenamePrefix;
  }

  public void setFilenamePrefix(String pFilenamePrefix) {
    mFilenamePrefix = pFilenamePrefix;
  }

  public String getFileExtension() {
    return mFileExtension;
  }

  public void setFileExtension(String pFileExtension) {
    mFileExtension = pFileExtension;
  }

}
