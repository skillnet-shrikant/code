package mff.util.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPClientImpl extends FTPClientBase {

	private Connection mConnection;
	private SftpProgressMonitorImpl mSftpProgressMonitor;

	@Override
	public void doStartService () {
		mSftpProgressMonitor = new SftpProgressMonitorImpl ();
	}

	/**
	 *  Connect to SFTP server
	 */
	public boolean connectToServer() throws IOException {
		// Reset Monitor
		mSftpProgressMonitor.reset();

		if (mConnection != null && mConnection.session.isConnected()) {
			return true;
		}
		if (isLoggingDebug()) {
			logDebug("Connecting to SFTP server " + mRemoteServer);
		}

		boolean retVal = false;
		JSch jsch = new JSch();
		Properties props = new Properties();
		props.put("StrictHostKeyChecking", "no");

		try {
			Session session = jsch.getSession(mRemoteUser, mRemoteServer, mRemotePort);
			session.setConfig(props);
			session.setPassword(mRemotePassword);
			if (mConnectTimeout != 0) {
				session.setTimeout(mConnectTimeout);
			}
			session.connect();

			ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
			channel.connect();
			if(isLoggingDebug())
				logDebug("CONFIGURED REMOTE PATH IS " + mRemotePath);
			channel.cd(mRemotePath);
			if(isLoggingDebug())
				logDebug("CONFIGURED LOCAL PATH IS " + mLocalPath);
			if(mLocalPath != null)
				channel.lcd(mLocalPath);
			mConnection = new Connection();
			mConnection.session = session;
			mConnection.channel = channel;
			retVal = true;
		}
		catch (JSchException e) {
			if (isLoggingError()) {
				logError(e);
			}
			throw new IOException(e);
		}
		catch (SftpException e) {
			if (isLoggingError()) {
				logError(e);
			}
			throw new IOException(e);
		}
		return retVal;
	}
	
	public List<String> downloadFeedFiles(String pRemoteDir, String pLocalDir, String pFilePrefix) throws IOException {
		List<String> fileList = new ArrayList<String>();
		if(isLoggingDebug())
			logDebug("Inside SFTPClient downloadFeedFiles");
		if(mConnection == null) {
			if(isLoggingDebug())
				logDebug("Connecting to SFTP server.....");
			connectToServer();
		}
		try {
			if(isLoggingDebug())
				logDebug("CD to " + pRemoteDir);
			mConnection.channel.cd(pRemoteDir);

			if(isLoggingDebug())
				logDebug("CD to local " + pLocalDir);
			mConnection.channel.lcd(pLocalDir);
			
			
			fileList = dir(pFilePrefix);
			
			for(String file: fileList) {
				if(isLoggingDebug())
					logDebug("Dowloading file " + file);
				//downloadFile(file, file);
				doDownloadFile(file, file);
			}
			disconnectFromServer();
		} catch (SftpException e) {
			if(isLoggingError())
				logError(e);
			throw new IOException(e);
		}
		return fileList;
	}
	/*
	 * Downloads all files from a given remote dir on FTP server
	 * to dir location of the client
	 */
/*	public void downloadFeedFiles(String pRemoteDir, String pLocalDir, String pFilePrefix) throws IOException {
		if(isLoggingDebug())
			logDebug("Inside SFTPClient downloadFeedFiles");
		if(mConnection == null) {
			if(isLoggingDebug())
				logDebug("Connecting to SFTP server.....");
			connectToServer();
		}
		try {
			if(isLoggingDebug())
				logDebug("CD to " + pRemoteDir);
			mConnection.channel.cd(pRemoteDir);

			if(isLoggingDebug())
				logDebug("CD to local " + pLocalDir);
			mConnection.channel.lcd(pLocalDir);
			
			List<String> fileList = new ArrayList<String>();
			fileList = dir(pFilePrefix);
			
			for(String file: fileList) {
				if(isLoggingDebug())
					logDebug("Dowloading file " + file);
				//downloadFile(file, file);
				doDownloadFile(file, file);
			}
			disconnectFromServer();
		} catch (SftpException e) {
			if(isLoggingError())
				logError(e);
			throw new IOException(e);
		}
	}*/
	/* downloadAllFiles(ftpServerConfig.server,ftpServerConfig.port,pRemote,pLocal)
	 *  connectToServer(server,port, userName,password)
	 *  lcd(pLocal)
	 *  cd(pRemote)
	 *  Vector ls(pRemote)
	 *  iterate over vector
	 *      download(fileName,fileName)
	 *  finally disconnect
	 */
	/**
	 *  disconnect from SFTP server
	 */
	public void disconnectFromServer() {
		if (mConnection == null) {
			return;
		}
		if (isLoggingDebug()) {
			logDebug("Disconnecting from SFTP server " + mRemoteServer);
		}
		if (mConnection.channel != null) {
			mConnection.channel.quit();
		}
		if (mConnection.session != null) {
			mConnection.session.disconnect();
		}
		mConnection = null;
		return;
	}

	/**
	 *  Upload a single file
	 */
	public boolean uploadFile(String pLocalFileName, String pRemoteFileName) throws IOException {
		boolean retValue = false;

		// Set file name being transferred
		mFileBeingTransferred = pLocalFileName;

		connectToServer();

		if (mConnection == null || !mConnection.session.isConnected()) {
			return retValue;
		}

		for (int i = 0; i < getMaxRetries() && !retValue && !mCancelTransfer && !mTimeExceeded && mConnection != null; i++) {
			try {
				retValue = doUploadFile(pLocalFileName, pRemoteFileName);
			}
			catch (IOException e) {
				if (isTemporaryError(e) && i < getMaxRetries()) {
					continue;
				}
				throw e;
			}
		}

		disconnectFromServer();
		return retValue;
	}

	/**
	 *  This method is for sending many files.
	 *  We connect to the server once, and then call this method in a loop.
	 */
	public boolean uploadFiles(String pLocalFileName, String pRemoteFileName) throws IOException {
		if (mConnection == null)
			connectToServer();

		// Set file name being transferred
		mFileBeingTransferred = pLocalFileName;

		boolean retValue = false;
		for (int i = 0; i < getMaxRetries() && !retValue && !mCancelTransfer && !mTimeExceeded && mConnection != null; i++) {
			try {
				retValue = doUploadFile(pLocalFileName, pRemoteFileName);
			}
			catch (IOException e) {
				if (isTemporaryError(e) && i < getMaxRetries()) {
					continue;
				}
				throw e;
			}
		}
		disconnectFromServer();
		return retValue;
	}

	/**
	 *  Download a single file
	 */
	public boolean downloadFile(String pRemoteFileName, String pLocalFileName) throws IOException {
		boolean retValue = false;

		// Set file name being transferred
		mFileBeingTransferred = pLocalFileName;
		if(isLoggingDebug())
			logDebug("File download being attempted " + pLocalFileName + " remote is " + pRemoteFileName);
		
		if(mConnection== null) {
			if(isLoggingDebug())
				logDebug("Download File Connection DOES NOT exist connecting.....");
			connectToServer();
		} else {
			if(isLoggingDebug())
				logDebug("Download File Connection already exists");
		}

		if (mConnection == null || !mConnection.session.isConnected()) {
			return retValue;
		}

		for (int i = 0; i < getMaxRetries() && !retValue && !mCancelTransfer && !mTimeExceeded && mConnection != null; i++) {
			try {
				if(isLoggingDebug())
					logDebug("Calling doDownloadfile with remote " + pRemoteFileName + " local " + pLocalFileName);
				retValue = doDownloadFile(pRemoteFileName, pLocalFileName);
			}
			catch (IOException e) {
				if (isTemporaryError(e) && i < getMaxRetries()) {
					continue;
				}
				throw e;
			}
		}
		if(isLoggingDebug())
			logDebug("Disconnecting from server......");
		disconnectFromServer();
		return retValue;
	}

	/**
	 *  This method is for downloading many files.
	 *  We connect to the server once, and then call this method in a loop.
	 */
	public boolean downloadFiles(String pRemoteFileName, String pLocalFileName) throws IOException {
		if (mConnection == null)
			connectToServer();

		// Set file name being transferred
		mFileBeingTransferred = pLocalFileName;

		boolean retValue = false;
		for (int i = 0; i < getMaxRetries() && !retValue && !mCancelTransfer && !mTimeExceeded && mConnection != null; i++) {
			try {
				retValue = doDownloadFile(pRemoteFileName, pLocalFileName);
			}
			catch (IOException e) {
				if (isTemporaryError(e) && i < getMaxRetries()) {
					continue;
				}
				throw e;
			}
		}
		return retValue;
	}

	/**
	 *  List files in the remote directory whose names start with the pattern (not case sensitive).
	 */
	@SuppressWarnings("unchecked")
	public List<String> dir(String pPattern) throws IOException {
		if(isLoggingDebug())
			logDebug("In DIR() method checking for patter " + pPattern);
		ArrayList<String> result = new ArrayList<String>();
		if (mConnection == null) {
			connectToServer();
		}
		try {
			if(isLoggingDebug()) {
				logDebug("remote directory is " + mConnection.channel.pwd());
				logDebug("local directory is " + mConnection.channel.lpwd());
			}
		} catch (SftpException e1) {
			logError(e1);
			throw new IOException(e1);
		}
		

		String uppercasePattern = pPattern.toUpperCase();
		try {
			Vector<LsEntry> fileNames = mConnection.channel.ls(".");
			for (LsEntry name : fileNames) {
				if(isLoggingDebug())
					logDebug("Inside for loop");
				String uppercaseName = name.getFilename().toUpperCase();
				if (uppercaseName.startsWith(uppercasePattern)) {
					if(isLoggingDebug())
						logDebug("Adding file " + name.getFilename());
					result.add(name.getFilename());
				}
			}
		}
		catch (SftpException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		return result;
	}

	/**
	 *  Delete a remote file
	 */
	public boolean deleteFile(String pRemoteFileName) throws IOException {
		boolean retValue = false;

		connectToServer();
		if (mConnection == null || !mConnection.channel.isConnected()) {
			return retValue;
		}

		try {
			mConnection.channel.rm(pRemoteFileName);
		}
		catch (SftpException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}

		disconnectFromServer();
		return retValue;
	}


	/**
	 * Perform the actual file transfer
	 */
	private boolean doUploadFile(String pLocalFileName, String pRemoteFileName) throws IOException {
		boolean retValue = false;

		if (isLoggingDebug()) {
			logDebug("Uploading file: " + pLocalFileName + " -> " + pRemoteFileName);
		}

		FileInputStream fis = new FileInputStream(pLocalFileName);
		try {
			mConnection.channel.put(fis, pRemoteFileName, mSftpProgressMonitor);
			retValue = true;
		}
		catch (SftpException e) {
			if (isLoggingError()) {
				logError(e);
			}
			mLastStatus = "Failed";
		}
		fis.close();
		// Check if the transfer was cancelled by the user
		if (mCancelTransfer) {
			mLastStatus = "Cancelled by User";
			retValue = false;
		}
		// Check if the transfer was cancelled due to time
		if (mTimeExceeded) {
			mLastStatus = "Cancelled by Time";
			retValue = false;
		}

		if (isLoggingDebug()) {
			if (retValue) {
				logDebug("Uploaded file: " + pLocalFileName + " -> " + pRemoteFileName);
			}
			else {
				logDebug("Failed to upload file: " + pLocalFileName);
			}
		}
		return retValue;
	}

	/**
	 * Perform the actual file transfer
	 */
	public boolean doDownloadFile(String pRemoteFileName, String pLocalFileName) throws IOException {
		boolean retValue = false;

		if (isLoggingDebug()) {
			try {
				logDebug("Downloading file: from " + mConnection.channel.pwd() +"/" + pRemoteFileName + " to -> " + mConnection.channel.lpwd() + "/" + pLocalFileName);
			} catch (SftpException e) {
				logError(e);
				throw new IOException(e);
			}
		}

		OutputStream fos = new FileOutputStream(mConnection.channel.lpwd() + "/" +pLocalFileName);
		try {
			mConnection.channel.get(pRemoteFileName, fos, mSftpProgressMonitor);
			if (mDeleteRemoteFile) {
				mConnection.channel.rm(pRemoteFileName);
			}
			retValue = true;
		}
		catch (SftpException e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		fos.close();
		// Check if the transfer was cancelled by the user
		if (mCancelTransfer) {
			mLastStatus = "Cancelled by User";
			retValue = false;
		}
		// Check if the transfer was cancelled due to time
		if (mTimeExceeded) {
			mLastStatus = "Cancelled by Time";
			retValue = false;
		}
		if (isLoggingDebug()) {
			if (retValue) {
				logDebug("Downloaded file: " + pRemoteFileName + " -> " + pLocalFileName);
			}
			else {
				logDebug("Failed to download file: " + pRemoteFileName);
			}
		}
		return retValue;
	}

	// Methods that exposure key information to the Dynamo Admin
	// console so the FTP transfer can be monitored.

	public boolean isConnectionClosed() {
		if (mConnection != null)
			return mConnection.channel.isClosed();
		else
			return true;
	}

	public boolean isConnectionConnected() {
		if (mConnection != null)
			return mConnection.channel.isConnected();
		else
			return false;
	}

	public boolean isConnectionEOF() {
		if (mConnection != null)
			return mConnection.channel.isEOF();
		else
			return true;
	}

	private String mFileBeingTransferred;
	public String getFileBeingTransferred() {
		return mFileBeingTransferred;
	}

	public long getBytesTransferred () {
		if (mSftpProgressMonitor != null)
			return mSftpProgressMonitor.getBytesTransferred();
		else
			return 0;
	}

	public long getBytesToTransfer () {
		long lLength = 0;
		if (mFileBeingTransferred != null) {
			File lFile = new File (mFileBeingTransferred);
			if (lFile.exists())
				lLength = lFile.length();
		}
		return lLength;
	}

	String mLastStatus = "Not Started";
	public String getLastStatus () {
		return mLastStatus;
	}

	long mTransferStart = 0;
	long mTransferLastUpdate = 0;
	public String getLastTransferTime () {
		if (mTransferStart == 0)
			return "N/A";
		long lDurationInMillis = mTransferLastUpdate - mTransferStart;
		String lDuration = String.format("%d hours, %d min, %d sec",
				TimeUnit.MILLISECONDS.toHours(lDurationInMillis),
				TimeUnit.MILLISECONDS.toMinutes(lDurationInMillis) -
						(TimeUnit.MILLISECONDS.toHours(lDurationInMillis) * 60),
				TimeUnit.MILLISECONDS.toSeconds(lDurationInMillis) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(lDurationInMillis)));
		return lDuration;
	}

	boolean mCancelTransfer = false;
	public void cancelTransfer () {
		mCancelTransfer = true;
	}

	boolean mTimeExceeded = false;

	public void forceCancelTransfer () {
		vlogInfo (String.format("Force cancelling the FTP transfer with %s bytes transferred", getBytesTransferred ()));
		disconnectFromServer();
	}


	private class Connection {
		Session session;
		ChannelSftp channel;
	}

	public class SftpProgressMonitorImpl
			implements SftpProgressMonitor {

		long bytesTransferred = 0;

		public long getBytesTransferred () {
			return bytesTransferred;
		}

		public void reset () {
			bytesTransferred = 0;
			mLastStatus = "Not Started";
			mCancelTransfer = false;
			mTimeExceeded = false;
		}

		public boolean count(long arg0) {
			bytesTransferred = bytesTransferred + arg0;
			mTransferLastUpdate = System.currentTimeMillis();
			mLastStatus = "Transferring";
			vlogDebug ("Currently transferred " + bytesTransferred + " bytes");
			if (mCancelTransfer) {
				vlogInfo ("Cancelling transfer at user request");
				mLastStatus = "Cancelling";
				return false;
			}
			if (mMaxTransferTime != 0 && (mTransferLastUpdate - mTransferStart) > mMaxTransferTime) {
				vlogInfo ("Cancelling transfer since the transfer time has been exceeded");
				if (!StringUtils.isEmpty(mErrorCode))
					vlogError (String.format("%s - FTP cancelled since it took longer than %s milliseconds", mErrorCode, mMaxTransferTime));
				mLastStatus = "Cancelling";
				mTimeExceeded = true;
				return false;
			}
			return true;
		}

		public void end() {
			vlogDebug ("FTP transfer is complete");
			mLastStatus = "Completed";
		}

		public void init(int arg0, String arg1, String arg2, long arg3) {
			vlogDebug ("FTP is starting");
			mTransferStart 		= System.currentTimeMillis();
			mTransferLastUpdate = System.currentTimeMillis();
			mLastStatus = "Started";
		}
	}

}