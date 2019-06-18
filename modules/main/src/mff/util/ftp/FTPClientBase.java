package mff.util.ftp;

import atg.nucleus.GenericService;

import java.io.File;
import java.io.IOException;

/**
 * Base class for FTP and SFTP implementations.
 * Contains methods common to all.
 */
public abstract class FTPClientBase extends GenericService implements FTPClient {

	protected String  mRemoteServer;
	protected int     mRemotePort;
	protected String  mRemotePath;
	protected String  mLocalPath;
	protected String  mRemoteUser;
	protected String  mRemotePassword;
	protected String  mConnectionType;
	protected boolean mDeleteRemoteFile = false;
	protected int     mConnectTimeout = 0;
	protected int     mMaxRetries = 3;
	protected String  mErrorCode = " ";
	protected int	  mMaxTransferTime = 0;

	/**
	 * Check exception's nature. If it's temporary, return true.
	 */
	protected boolean isTemporaryError(Exception pException) {
		if ((pException instanceof java.net.SocketTimeoutException) ||
				(pException instanceof java.io.InterruptedIOException)  ||
				(pException instanceof java.net.UnknownHostException)) {
			if (isLoggingDebug()) {
				logDebug("Temporary exception: " + pException + ", retrying...");
			}
			return true;
		}
		return false;
	}

	public boolean uploadFile(String pLocalFileName) throws IOException {
		String remoteFileName = pLocalFileName.substring(pLocalFileName.lastIndexOf(File.separatorChar) + 1);
		return uploadFile(pLocalFileName, remoteFileName);
	}

	public boolean uploadFiles(String pLocalFileName) throws IOException {
		String remoteFileName = pLocalFileName.substring(pLocalFileName.lastIndexOf(File.separatorChar) + 1);
		return uploadFiles(pLocalFileName, remoteFileName);
	}

	public void setRemoteServer(String pRemoteServer) {
		mRemoteServer = pRemoteServer;
	}
	public void setRemotePort(int pRemotePort) {
		mRemotePort = pRemotePort;
	}
	public void setRemotePath(String pRemotePath) {
		mRemotePath = pRemotePath;
	}
	public void setLocalPath(String pLocalPath) {
		mLocalPath = pLocalPath;
	}	
	public void setRemoteUser(String pRemoteUser) {
		mRemoteUser = pRemoteUser;
	}
	public void setRemotePassword(String pRemotePassword) {
		mRemotePassword = pRemotePassword;
	}
	public void setConnectionType(String pConnectionType) {
		mConnectionType = pConnectionType;
	}
	public void setDeleteRemoteFile(boolean pDeleteRemoteFile) {
		mDeleteRemoteFile = pDeleteRemoteFile;
	}
	public void setConnectTimeout(int pConnectTimeout) {
		mConnectTimeout = pConnectTimeout * 1000;
	}

	public String  getRemoteServer() {
		return mRemoteServer;
	}
	public int     getRemotePort() {
		return mRemotePort;
	}
	public String  getRemotePath() {
		return mRemotePath;
	}
	public String  getLocalPath() {
		return mLocalPath;
	}	
	public String  getRemoteUser() {
		return mRemoteUser;
	}
	public String  getRemotePassword() {
		return mRemotePassword;
	}
	public String  getConnectionType() {
		return mConnectionType;
	}
	public boolean isDeleteRemoteFile() {
		return mDeleteRemoteFile;
	}
	public int     getConnectTimeout() {
		return mConnectTimeout/1000;
	}
	public int getMaxRetries() {
		return mMaxRetries;
	}
	public void setMaxRetries(int pMaxRetries) {
		mMaxRetries = pMaxRetries;
	}

	public String getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(String pErrorCode) {
		this.mErrorCode = pErrorCode;
	}

	public int getMaxTransferTime() {
		return mMaxTransferTime;
	}
	public void setMaxTransferTime(int pMaxTransferTime) {
		this.mMaxTransferTime = pMaxTransferTime;
	}

}