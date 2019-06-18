package mff.loader;


import java.io.IOException;

import atg.nucleus.GenericService;
import mff.util.ZipUtility;
import mff.util.ftp.SFTPClientImpl;

/**
 * Class provides methods for handling import of CSV feed files
 *  - Optionally pull down feeds from the FTP server
 *  - Process the feeds by invoking a DB stored proc
 *  
 *  Currenly this is being used to process price and inventory feeds
 *  
 * @author KnowledgePath
 *
 */
public class BVRatingsLoader extends GenericService {

	private SFTPClientImpl sftpClient;
	private boolean downloadFeedsFromSFTP;
	private String mInputFileName;
	private String mOutputFileName;
	private String mRemoteDirectory;
	private String mLocalDirectory;
	private String mFilePrefix;
	private String mBvInputFileName;
	private String mBvOutputFileName;
	
	public String getBvOutputFileName() {
		return mBvOutputFileName;
	}
	public void setBvOutputFileName(String pBvOutputFileName) {
		mBvOutputFileName = pBvOutputFileName;
	}
	public String getBvInputFileName() {
		return mBvInputFileName;
	}
	public void setBvInputFileName(String pBvInputFileName) {
		mBvInputFileName = pBvInputFileName;
	}
	public String getFilePrefix() {
		return mFilePrefix;
	}
	public void setFilePrefix(String pFilePrefix) {
		mFilePrefix = pFilePrefix;
	}
	public String getRemoteDirectory() {
		return mRemoteDirectory;
	}
	public void setRemoteDirectory(String pRemoteDirectory) {
		mRemoteDirectory = pRemoteDirectory;
	}
	public String getLocalDirectory() {
		return mLocalDirectory;
	}
	public void setLocalDirectory(String pLocalDirectory) {
		mLocalDirectory = pLocalDirectory;
	}
	public String getInputFileName() {
		return mInputFileName;
	}
	public void setInputFileName(String pInputFileName) {
		mInputFileName = pInputFileName;
	}
	public String getOutputFileName() {
		return mOutputFileName;
	}
	public void setOutputFileName(String pOutputFileName) {
		mOutputFileName = pOutputFileName;
	}
	public SFTPClientImpl getSftpClient() {
		return sftpClient;
	}
	public void setSftpClient(SFTPClientImpl pSftpClient) {
		sftpClient = pSftpClient;
	}
	public boolean isDownloadFeedsFromSFTP() {
		return downloadFeedsFromSFTP;
	}
	public void setDownloadFeedsFromSFTP(boolean pDownloadFeedsFromSFTP) {
		downloadFeedsFromSFTP = pDownloadFeedsFromSFTP;
	}

	

	public void processFiles() throws IOException{
		// Determine if we need to download feeds from an FTP server
		try{
			if(isDownloadFeedsFromSFTP()) {
				if(isLoggingInfo()) {
					logInfo("mff.loader.BVRatingsLoader:processFiles:Downloading feeds from FTP server"
							+ "Remote dir " + getRemoteDirectory()
							+ "Local dir " + getLocalDirectory()
							+ "File prefix " + getFilePrefix());
				}
				downloadFeedFiles(getRemoteDirectory(),getLocalDirectory(), getFilePrefix());
				ZipUtility utility=new ZipUtility();
				utility.setLocation(getLocalDirectory());
				utility.setInputFileName(getBvInputFileName());
				utility.setOutputFileName(getBvOutputFileName());
				utility.unzip(true);
			} else {
				if(isLoggingInfo()) {
					logInfo("mff.loader.BVRatingsLoader:processFiles:FTPDownload disabled. Using files from " + getLocalDirectory());
				}
			}
		}catch(Exception ex){
			vlogError("mff.loader.BVRatingsLoader:processFiles:Error occurred",ex);
		}
	}
	
	protected void downloadFeedFiles(String pRemoteDir, String pLocalDir, String pFilePrefix) {

		if(isLoggingInfo())
			logInfo("mff.loader.BVRatingsLoader:downloadFeedFiles Pulling feeds from " + pRemoteDir + " to " + pLocalDir);
		
		try {
			getSftpClient().downloadFeedFiles(pRemoteDir, pLocalDir, pFilePrefix);
		} catch (IOException e) {
			if(isLoggingError())
				logError(e);
		}
	}
	
	

}
