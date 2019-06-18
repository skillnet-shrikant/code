/**
 * COPYRIGHT (C) KnowledgePath Solutions, Inc. April 2010
 */

package oms.util.fileprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import mff.util.ftp.FTPClient;

public class OMSFileProcessorUtil extends GenericService implements FileFilter {
	
	protected static final String FILE_PROCESSED_SUFFIX = ".done";
	
	protected static final String ERROR_FILE_SUFFIX = ".error";

	/**
	 * Define the files which meet the criteria for extraction.
	 */
	public boolean accept(File pFileName) {
		if (pFileName == null) {
			return false;
		}
		String fileName = pFileName.getName();
		return fileName.startsWith(getExtractPrefix()) && !fileName.endsWith(FILE_PROCESSED_SUFFIX);
	}
	
	/**
	 * Check to see if the input directory is valid.
	 * 
	 * @param pInputDirectory		File location of input directory
	 * @return
	 */
	public boolean checkInputDirectory(String pInputDirectory) {
		if (StringUtils.isBlank(pInputDirectory)) {
			vlogError("Unable to check the input directory " + pInputDirectory);
			return false;
		}
		if (!new File(pInputDirectory).exists()) {
			vlogError("The input directory " + pInputDirectory + " does not exist");
			return false;
		}
		return true;
	}

	

	/**
	 * Append the processed suffix to the file and move the file 
	 * to the done directory.
	 * 
	 * @return			Boolean flag of success
	 */
	public boolean moveToDoneDirectory(File pFile) {
		boolean lReturn = false;
		
		//File lFile = new File(mFilename);
		
		String FILE_PROCESSED_SUFFIX = ".done";
		
		if (StringUtils.isBlank(getDoneDirectory())) {
			vlogError("Done directory is not specified - Unable to move file");
			return lReturn;
		}
		File archiveDir = new File(getDoneDirectory());
		if (!archiveDir.exists()) {
			vlogError("Done directory " + getDoneDirectory() + " does not exit - Unable to move file");
			return lReturn;
		}
		String lOrigFileName 	= pFile.getName();
		String newFullFileName 	= getDoneDirectory() + File.separator + lOrigFileName + FILE_PROCESSED_SUFFIX;
		File lNewFile 			= new File(newFullFileName);
		lReturn 				= pFile.renameTo(lNewFile);
		return lReturn;
	}
	
	/**
	 * Append the processed suffix to the file and move the file 
	 * to the done directory.
	 * 
	 * @return			Boolean flag of success
	 */
	public boolean moveToErrorDirectory(File pFile) {
		boolean lReturn = false;
		
		
		if (StringUtils.isBlank(getErrorDirectory())) {
			vlogError("Done directory is not specified - Unable to move file");
			return lReturn;
		}
		File archiveDir = new File(getErrorDirectory());
		if (!archiveDir.exists()) {
			vlogError("Done directory " + getDoneDirectory() + " does not exit - Unable to move file");
			return lReturn;
		}
		String lOrigFileName 	= pFile.getName();
		String newFullFileName 	= getErrorDirectory() + File.separator + lOrigFileName + ERROR_FILE_SUFFIX;
		File lNewFile 			= new File(newFullFileName);
		lReturn 				= pFile.renameTo(lNewFile);
		return lReturn;
	}	
	/**
	 * Download the files to the local files system for processing.
	 * 
	 * @param pInputDirectory		Directory where files will be placed
	 * @param pFilenamePrefix		File name prefix
	 * @return						Number of files found
	 * @throws IOException
	 */
	public int downloadFiles() throws IOException {
		
		if (!checkInputDirectory(getInputDirectory())) 
			return 0;
	
		// download the files
		List<String> lFiles = getFtpClient().dir(getExtractPrefix());
		vlogDebug("Downloading " + lFiles.size() + " files");
	
		int lFileCount = 0;
		for (String lFile : lFiles) {
			String localFilename = getInputDirectory() + File.separator + lFile;
			boolean lStatus = getFtpClient().downloadFiles(lFile, localFilename);
			if (lStatus) 
				lFileCount++;
			else 
				vlogError("Unable to download Ship Notification files");
		}
		getFtpClient().disconnectFromServer(); 
		return lFileCount;
	}
	/**
	 * Get the list of files to be processed.
	 * 
	 * @param pInputDirectory		Input directory where files reside
	 * @return						List of files which meet file name format
	 */
	public File[] getFilesToProcess() {
		if (!checkInputDirectory(getInputDirectory())) {
			return new File[0];
		}
		return new File(getInputDirectory()).listFiles(this);
	}
	/**
	 * Read the file and put the results into a list of 
	 * strings.
	 * 
	 * @param pFile			File name to be read
	 * @return				List of strings containing file contents
	 * @throws IOException
	 */
	protected List<String> readFile(File pFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(pFile));
		List<String> retValue = new ArrayList<String>();
		String line;
		while ((line = in.readLine()) != null) {
			retValue.add(line);
		}
		in.close();
		return retValue;
	}
	
	/** FTP Client **/
	FTPClient mFtpClient;
	public FTPClient getFtpClient() {
		return mFtpClient;
	}
	public void setFtpClient(FTPClient pFtpClient) {
		this.mFtpClient = pFtpClient;
	}
	
	/** Location where processed files are stored **/
	String mDoneDirectory;
	public String getDoneDirectory() {
		return mDoneDirectory;
	}
	public void setDoneDirectory(String pDoneDirectory) {
		this.mDoneDirectory = pDoneDirectory;
	}
	
	/** Location where error files are stored **/
	String errorDirectory;
	public String getErrorDirectory() {
		return errorDirectory;
	}

	public void setErrorDirectory(String errorDirectory) {
		this.errorDirectory = errorDirectory;
	}	
	
	/** Is FTP enabled */
	boolean mFtpEnabled;		
	public boolean isFtpEnabled() {
		return mFtpEnabled;
	}
	public void setFtpEnabled(boolean pFtpEnabled) {
		this.mFtpEnabled = pFtpEnabled;
	}
	
	/** Input directory where the files to be processed are located **/
	String mInputDirectory;
	public String getInputDirectory() {
		return mInputDirectory;
	}
	public void setInputDirectory(String pInputDirectory) {
		this.mInputDirectory = pInputDirectory;
	}
	
	/** Prefix for the files to be processed **/
	String mExtractPrefix;
	public String getExtractPrefix() {
		return mExtractPrefix;
	}
	public void setExtractPrefix(String pExtractPrefix) {
		this.mExtractPrefix = pExtractPrefix;
	}


}
