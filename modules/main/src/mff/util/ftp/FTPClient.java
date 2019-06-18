package mff.util.ftp;

import java.io.IOException;
import java.util.List;

/**
 * Base FTP functionality definitions.
 */
public interface FTPClient {

	public boolean connectToServer() throws IOException;
	public void disconnectFromServer();

	// Upload a single file
	public boolean uploadFile(String pLocalFileName, String pRemoteFileName) throws IOException;
	public boolean uploadFile(String pLocalFileName) throws IOException;

	// This method is for sending many files.
	// We connect to the server once, and then call this method in a loop.
	public boolean uploadFiles(String pLocalFileName, String pRemoteFileName) throws IOException;
	public boolean uploadFiles(String pLocalFileName) throws IOException;

	// Download a single file
	public boolean downloadFile(String pRemoteFileName, String pLocalFileName) throws IOException;

	// This method is for downloading many files.
	// We connect to the server once, and then call this method in a loop.
	public boolean downloadFiles(String pRemoteFileName, String pLocalFileName) throws IOException;

	// List files in the remote directory whose names start with the pattern (not case sensitive).
	public List<String> dir(String pPattern) throws IOException;

	// Delete a remote file
	public boolean deleteFile(String pRemoteFileName) throws IOException;

	// Setters
	public void setRemoteServer(String pRemoteServer);
	public void setRemotePort(int pRemotePort);
	public void setRemotePath(String pRemotePath);
	public void setLocalPath(String pLocalPath);
	public void setRemoteUser(String pRemoteUser);
	public void setRemotePassword(String pRemotePassword);
	public void setConnectionType(String pConnectionType);
	public void setDeleteRemoteFile(boolean pDeleteRemoteFile);
	public void setConnectTimeout(int pConnectTimeout);
	public void setMaxRetries(int pMaxRetries);

	// Getters
	public String  getRemoteServer();
	public int     getRemotePort();
	public String  getRemotePath();
	public String  getLocalPath();
	public String  getRemoteUser();
	public String  getRemotePassword();
	public String  getConnectionType();
	public boolean isDeleteRemoteFile();
	public int     getConnectTimeout();
	public int     getMaxRetries();

}