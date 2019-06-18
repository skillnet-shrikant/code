package com.mff.tires.parts.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.transaction.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.mff.tires.parts.constants.PartsFitmentDataConstants;
import com.mff.tires.parts.vo.PartsFitmentDataVo;

public class FitmentDataService extends GenericService {

	private String httpsProxyHost;
	private String httpsProxyPort;
	private String httpProxyHost;
	private String httpProxyPort;
	private String grantType;
	private String userName;
	private String passWord;
	private String bearerTokenURL;
	private String zipFileURL;
	private String clientId;
	private String zipFile;
	private String destDir;

	private Repository fitmentDataRepository;

	private TransactionManager transactionManager;

	public void invokeMAMAutoCatService() {
		vlogDebug("FitmentDataService:invokeMAMAutoCatService:Start");
		try {
			setProxyProperties();
			String bearerToken = extractBearerToken();
			String zipUrl = extractZipFileURL(bearerToken);
			String zipFilePath = downloadZipFile(zipUrl);
			File vcFile = unzip(zipFilePath);
			// File vcFile = new
			// File("C:\\AE112\\PartsFitmentData\\FullVCFile.txt");
			ArrayList<PartsFitmentDataVo> fitmentDataVosList = readPartsFitmentData(vcFile);
			storeUniqueRecords(fitmentDataVosList);
			renameProcessedFiles(vcFile);
		} catch (Exception e) {
			vlogError(
					"FitmentDataService:invokeMAMAutoCatService:Error occurred while invoking MAMAutoCatService",
					e);
		}
		vlogDebug("FitmentDataService:invokeMAMAutoCatService:End");
	}

	/**
	 * This method will set proxy
	 * 
	 **/
	public void setProxyProperties() {
		System.setProperty(PartsFitmentDataConstants.HTTPS_PROXYHOST,
				getHttpsProxyHost());
		System.setProperty(PartsFitmentDataConstants.HTTPS_PROXYPORT,
				getHttpsProxyPort());
		System.setProperty(PartsFitmentDataConstants.HTTP_PROXYHOST,
				getHttpProxyHost());
		System.setProperty(PartsFitmentDataConstants.HTTP_PROXYPORT,
				getHttpProxyPort());
	}

	/**
	 * extract Bearer Token from MAM-AutoCat WebService Hit the MAM-AutoCat
	 * WebService and get the bearer token POST Service
	 * https://mamcatalogapi.azurewebsites.net/auth
	 * 
	 * @return bearerToken
	 */
	public String extractBearerToken() {
		vlogDebug("FitmentDataService:extractBearerToken:Start");
		String bearerToken = null;
		try {
			String urlParameters = PartsFitmentDataConstants.GRANT_TYPE
					+ getGrantType() + PartsFitmentDataConstants.AND_SIGN
					+ PartsFitmentDataConstants.USERNAME + getUserName()
					+ PartsFitmentDataConstants.AND_SIGN
					+ PartsFitmentDataConstants.PASSWORD + getPassWord();
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			URL url = new URL(getBearerTokenURL());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod(PartsFitmentDataConstants.POST);
			conn.setRequestProperty(PartsFitmentDataConstants.CONTENT_TYPE,
					PartsFitmentDataConstants.APPLICATION_JSON);
			conn.setRequestProperty(PartsFitmentDataConstants.CHARSET,
					PartsFitmentDataConstants.UTF_8);
			conn.setRequestProperty(PartsFitmentDataConstants.CONTENT_LENGTH,
					Integer.toString(postDataLength));
			conn.setUseCaches(false);
			try {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(postData);
			} catch (Exception e) {
				vlogError(
						"FitmentDataService:extractBearerToken:Error occurred while extracting Bearer Token",
						e);
			}
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			JSONObject jsonObject = null;
			while ((output = br.readLine()) != null) {
				//vlogDebug("output :: " + output);
				jsonObject = new JSONObject(output);
			}
			bearerToken = jsonObject
					.getString(PartsFitmentDataConstants.ACCESS_TOKEN);
			vlogDebug("bearerToken :: " + bearerToken);
			vlogDebug("bearerToken extracted successfully");
			conn.disconnect();
		} catch (MalformedURLException e) {
			vlogError(
					"FitmentDataService:extractBearerToken:Error occurred while extracting Bearer Token",
					e);
		} catch (IOException e) {
			vlogError(
					"FitmentDataService:extractBearerToken:Error occurred while extracting Bearer Token",
					e);
		} catch (JSONException e) {
			vlogError(
					"FitmentDataService:extractBearerToken:Error occurred while extracting Bearer Token",
					e);
		}
		vlogDebug("FitmentDataService:extractBearerToken:End");
		return bearerToken;
	}

	/**
	 * 
	 * Pass the bearer token to below service in Authorization Header and get
	 * the Zip file url GET Service
	 * https://mamcatalogapi.azurewebsites.net/api/vehicles
	 * /cache/link?api-version=1.0&clientId=FWCO
	 * 
	 * @param pBearerToken
	 * @return zipFileURL
	 * 
	 */
	public String extractZipFileURL(String pBearerToken) {
		vlogDebug("FitmentDataService:extractZipFileURL:Start");
		String zipFileURL = null;
		try {
			String request = getZipFileURL()
					+ PartsFitmentDataConstants.AND_SIGN
					+ PartsFitmentDataConstants.CLIENT_ID + getClientId();
			vlogDebug(request);
			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod(PartsFitmentDataConstants.GET);
			conn.setRequestProperty(PartsFitmentDataConstants.CONTENT_TYPE,
					PartsFitmentDataConstants.APPLICATION_JSON);
			conn.setRequestProperty(PartsFitmentDataConstants.CHARSET,
					PartsFitmentDataConstants.UTF_8);
			conn.setRequestProperty(PartsFitmentDataConstants.CONTENT_LENGTH,
					Integer.toString(1024));
			conn.setUseCaches(false);
			vlogDebug(PartsFitmentDataConstants.BEARER + " " + pBearerToken);
			conn.setRequestProperty(PartsFitmentDataConstants.AUTHORIZATION,
					PartsFitmentDataConstants.BEARER + " " + pBearerToken);
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			JSONObject jsonObject = null;
			while ((output = br.readLine()) != null) {
				//vlogDebug("output :: " + output);
				jsonObject = new JSONObject(output);
			}
			zipFileURL = jsonObject
					.getString(PartsFitmentDataConstants.VEHICLE_CACHE_FILE_URL);
			conn.disconnect();
		} catch (MalformedURLException e) {
			vlogError(
					"FitmentDataService:extractZipFileURL:Error occurred while extracting ZipFileURL",
					e);
		} catch (IOException e) {
			vlogError(
					"FitmentDataService:extractZipFileURL:Error occurred while extracting ZipFileURL",
					e);
		} catch (JSONException e) {
			vlogError(
					"FitmentDataService:extractZipFileURL:Error occurred while extracting ZipFileURL",
					e);
		}
		vlogDebug("zipFileURL extracted successfully");
		vlogDebug("zipFileURL :: " + zipFileURL);
		vlogDebug("FitmentDataService:extractZipFileURL:End");
		return zipFileURL;
	}

	/**
	 * This method will download Zip File
	 * 
	 * @param pZipUrl
	 * @return getZipFile()
	 **/
	public String downloadZipFile(String pZipUrl) {
		vlogDebug("FitmentDataService:downloadZipFile:Start");
		try {
			URL url = new URL(pZipUrl);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			FileOutputStream fis = new FileOutputStream(getZipFile());
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = bis.read(buffer, 0, 1024)) != -1) {
				fis.write(buffer, 0, count);
			}
			fis.close();
			bis.close();
		} catch (MalformedURLException e) {
			vlogError(
					"FitmentDataService:downloadZipFile:Error occurred while downloading Zip File",
					e);
		} catch (FileNotFoundException e) {
			vlogError(
					"FitmentDataService:downloadZipFile:Error occurred while downloading Zip File",
					e);
		} catch (IOException e) {
			vlogError(
					"FitmentDataService:downloadZipFile:Error occurred while downloading Zip File",
					e);
		}
		vlogDebug("ZIP file downloaded successfully on given location ::"
				+ getZipFile());
		vlogDebug("FitmentDataService:downloadZipFile:End");
		return getZipFile();
	}

	/**
	 * This method will unzip the Zip File
	 * 
	 * @param pZipFilePath
	 * @return inputVCFile
	 **/
	public File unzip(String pZipFilePath) {
		vlogDebug("FitmentDataService:unzip:Start");
		File dir = new File(getDestDir());
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		File inputVCFile = null;
		try {
			fis = new FileInputStream(pZipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				inputVCFile = new File(getDestDir() + File.separator + fileName);
				vlogDebug("Unzipping to " + inputVCFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(inputVCFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(inputVCFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			vlogError(
					"FitmentDataService:unzip:Error occurred while unzipping Zip File",
					e);
		}
		vlogDebug("FitmentDataService:unzip:End");
		return inputVCFile;
	}

	/**
	 * This method will read FullVC.txt file and create list of YMME Value
	 * Objects
	 * 
	 * @param pVcFile
	 * @return fitmentDataVosList
	 */
	public ArrayList<PartsFitmentDataVo> readPartsFitmentData(File pVcFile) {
		vlogDebug("FitmentDataService:readPartsFitmentData:Start");
		ArrayList<PartsFitmentDataVo> fitmentDataVosList = new ArrayList<PartsFitmentDataVo>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(pVcFile));
			String readLine;
			while ((readLine = br.readLine()) != null) {
				String[] values = readLine
						.split(PartsFitmentDataConstants.DELIMITER);
				PartsFitmentDataVo fitmentDataVo = new PartsFitmentDataVo();
				fitmentDataVo.setYear(values[0]);
				fitmentDataVo.setMake(values[1]);
				fitmentDataVo.setModel(values[2]);
				fitmentDataVo.setEngine(values[3]);
				fitmentDataVo.setCountry(values[8]);
				fitmentDataVo.setVehicleType(values[9]);
				fitmentDataVo.setUniqueKey(values[0] + ":" + values[1] + ":"
						+ values[2] + ":" + values[3] + ":" + values[8]);
				fitmentDataVosList.add(fitmentDataVo);
			}
			br.close();
		} catch (FileNotFoundException e) {
			vlogError(
					"FitmentDataService:readPartsFitmentData:Error occurred while reading fitment data",
					e);
		} catch (IOException e) {
			vlogError(
					"FitmentDataService:readPartsFitmentData:Error occurred while reading fitment data",
					e);
		}
		vlogDebug("FitmentDataService:readPartsFitmentData:End");
		return fitmentDataVosList;
	}

	/**
	 * This method will store unique parts fitment records into database
	 * 
	 * @param pFitmentDataVosList
	 */
	public void storeUniqueRecords(
			ArrayList<PartsFitmentDataVo> pFitmentDataVosList) {
		vlogDebug("FitmentDataService:storeUniqueRecords:Start");
		RepositoryItem[] fitmentItems = null;
		for (PartsFitmentDataVo lPartsFitmentDataVo : pFitmentDataVosList) {
			fitmentItems = queryFitmentRepository(lPartsFitmentDataVo
					.getUniqueKey());
			if (fitmentItems != null) {
				for (RepositoryItem fitmentItm : fitmentItems) {
					vlogDebug("fitmentId :: " + fitmentItm.getRepositoryId());
				}
			} else if (fitmentItems == null) {
				enrichFitmentRecords(lPartsFitmentDataVo);
			}
		}
		vlogDebug("FitmentDataService:storeUniqueRecords:End");
	}

	/**
	 * This method will compare input YMME data with actual values stored in
	 * database. if it exists then it will return matching fitment records.
	 * 
	 * @param pUniqueKey
	 * @return fitmentItems
	 */
	public RepositoryItem[] queryFitmentRepository(String pUniqueKey) {
		vlogDebug("FitmentDataService:queryFitmentRepository:Start");
		RepositoryItem[] fitmentItems = null;
		try {
			String itemDescriptor = PartsFitmentDataConstants.PARTS_FITMENT_DATA_ITEM_DESC;
			RepositoryView view = getFitmentDataRepository().getView(
					itemDescriptor);
			QueryBuilder b = view.getQueryBuilder();
			QueryExpression id = b
					.createPropertyQueryExpression(PartsFitmentDataConstants.ID_PROPERTY);
			QueryExpression uniqueKey = b
					.createConstantQueryExpression(pUniqueKey);
			if (view != null) {
				Query uniqueKeyQuery = b.createComparisonQuery(id, uniqueKey,
						QueryBuilder.EQUALS);
				fitmentItems = view.executeQuery(uniqueKeyQuery);
			}
		} catch (RepositoryException e) {
			vlogError(
					"FitmentDataService:queryFitmentRepository:Error occurred while fetching records from Fitment Repository",
					e);
		}
		vlogDebug("FitmentDataService:queryFitmentRepository:End");
		return fitmentItems;
	}

	/**
	 * This method will enrich Fitment Repository with fitment data
	 * 
	 * @param lPartsFitmentDataVo
	 */
	public void enrichFitmentRecords(PartsFitmentDataVo lPartsFitmentDataVo) {
		vlogDebug("FitmentDataService:enrichFitmentRecords:Start");
		TransactionDemarcation td = new TransactionDemarcation();
		MutableRepository mutableRepository = (MutableRepository) getFitmentDataRepository();
		MutableRepositoryItem partsFitmentDataItem = null;
		try {
			td.begin(getTransactionManager());
			String itemDescriptor = PartsFitmentDataConstants.PARTS_FITMENT_DATA_ITEM_DESC;
			partsFitmentDataItem = mutableRepository.createItem(itemDescriptor);
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.YEAR_PROPERTY,
					lPartsFitmentDataVo.getYear());
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.MAKE_PROPERTY,
					lPartsFitmentDataVo.getMake());
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.MODEL_PROPERTY,
					lPartsFitmentDataVo.getModel());
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.ENGINE_PROPERTY,
					lPartsFitmentDataVo.getEngine());
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.COUNTRY_PROPERTY,
					lPartsFitmentDataVo.getCountry());
			partsFitmentDataItem.setPropertyValue(
					PartsFitmentDataConstants.VEHICLETYPE_PROPERTY,
					lPartsFitmentDataVo.getVehicleType());
			mutableRepository.addItem(partsFitmentDataItem);
			vlogDebug(partsFitmentDataItem.getRepositoryId()+ " Added Successfully");
		} catch (TransactionDemarcationException tde) {
			vlogError(
					"FitmentDataService:enrichFitmentRecords:Error occurred while enriching records in Fitment Repository",
					tde);
		} catch (RepositoryException re) {
			vlogError(
					"FitmentDataService:enrichFitmentRecords:Error occurred while enriching records in Fitment Repository",
					re);
		} finally {
			try {
				if (td != null) {
					td.end();
				}
			} catch (TransactionDemarcationException tde) {
				vlogError(
						"FitmentDataService:enrichFitmentRecords:Error occurred while enriching records in Fitment Repository",
						tde);
			}
		}
		vlogDebug("FitmentDataService:enrichFitmentRecords:End");
	}

	/**
	 * This method will rename processed file with current TimeStamp e.g.
	 * FullVC-2019-06-18-13-12-pm.txt
	 * 
	 * @param pVcFile
	 */
	public void renameProcessedFiles(File pVcFile) {
		vlogDebug("FitmentDataService:renameProcessedFiles:Start");
		File processedfile = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(Instant.now()));
		String processedfileName = String.format(
				PartsFitmentDataConstants.FILE_FORMAT, cal);
		processedfile = new File(getDestDir() + File.separator
				+ processedfileName);
		if (pVcFile.renameTo(processedfile)) {
			vlogDebug("File is renamed to " + processedfile);
		} else {
			vlogDebug("File cannot be renamed");
		}
		vlogDebug("FitmentDataService:renameProcessedFiles:End");
	}

	public String getHttpsProxyHost() {
		return httpsProxyHost;
	}

	public void setHttpsProxyHost(String pHttpsProxyHost) {
		httpsProxyHost = pHttpsProxyHost;
	}

	public String getHttpsProxyPort() {
		return httpsProxyPort;
	}

	public void setHttpsProxyPort(String pHttpsProxyPort) {
		httpsProxyPort = pHttpsProxyPort;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String pGrantType) {
		grantType = pGrantType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String pUserName) {
		userName = pUserName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String pPassWord) {
		passWord = pPassWord;
	}

	public String getBearerTokenURL() {
		return bearerTokenURL;
	}

	public void setBearerTokenURL(String pBearerTokenURL) {
		bearerTokenURL = pBearerTokenURL;
	}

	public String getZipFileURL() {
		return zipFileURL;
	}

	public void setZipFileURL(String pZipFileURL) {
		zipFileURL = pZipFileURL;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String pClientId) {
		clientId = pClientId;
	}

	public String getZipFile() {
		return zipFile;
	}

	public void setZipFile(String pZipFile) {
		zipFile = pZipFile;
	}

	public String getDestDir() {
		return destDir;
	}

	public void setDestDir(String pDestDir) {
		destDir = pDestDir;
	}

	public Repository getFitmentDataRepository() {
		return fitmentDataRepository;
	}

	public void setFitmentDataRepository(Repository pFitmentDataRepository) {
		fitmentDataRepository = pFitmentDataRepository;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		transactionManager = pTransactionManager;
	}

	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String pHttpProxyHost) {
		httpProxyHost = pHttpProxyHost;
	}

	public String getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(String pHttpProxyPort) {
		httpProxyPort = pHttpProxyPort;
	}

}
