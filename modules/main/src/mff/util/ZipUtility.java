package mff.util;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtility extends GenericService {

	private String mLocation;
	private String mInputFileName;
	private String mOutputFileName;
	
	public ZipUtility(){
		setLoggingInfo(true);
		setLoggingError(true);
	}
	
	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String pLocation) {
		mLocation = pLocation;
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
	

	public void unzip(boolean isLoggingDebug){
		
		setLoggingDebug(isLoggingDebug);
		vlogInfo("mff.util.ZipUtility:unzip:Start");
		byte[] buffer = new byte[1024];
		try{
			
			if(StringUtils.isEmpty(getLocation())){
				vlogError("mff.util.ZipUtility:unzip:Location path is empty");
				throw new FileNotFoundException("Location path is empty");
			}
			if(StringUtils.isEmpty(getInputFileName())){
				vlogError("mff.util.ZipUtility:unzip:InputFile name is empty");
				throw new FileNotFoundException("Input file name is empty");
			}
			
			if(StringUtils.isEmpty(getOutputFileName())){
				vlogError("mff.util.ZipUtility:unzip:Output name is empty");
				throw new FileNotFoundException("Output file name is empty");
			}
			
			String inputFileName=getLocation().trim()+getInputFileName();
			String outputFileName=getLocation().trim()+getOutputFileName();
			
	    	 GZIPInputStream gzis =
	    		new GZIPInputStream(new FileInputStream(inputFileName));

	    	 FileOutputStream out =
	            new FileOutputStream(outputFileName);

	        int len;
	        while ((len = gzis.read(buffer)) > 0) {
	        	out.write(buffer, 0, len);
	        }

	        gzis.close();
	    	out.close();
	    	moveFileToArchive();
	    	vlogInfo("mff.util.ZipUtility:unzip:End");
	    	

	    }catch(FileNotFoundException ex){
	    	vlogError("mff.util.ZipUtility:unzip:Filenotfound exception occurred",ex);
	    }
		catch(IOException ex){
	       vlogError("mff.util.ZipUtility:unzip:IOException occurred",ex);
	    }
	}
	
	public void zip(boolean isLoggingDebug){
		
		vlogInfo("mff.util.ZipUtility:unzip:Start");
		byte[] buffer = new byte[1024];
		try{
			
			if(StringUtils.isEmpty(getLocation())){
				vlogError("mff.util.ZipUtility:unzip:Location path is empty");
				throw new FileNotFoundException("Location path is empty");
			}
			if(StringUtils.isEmpty(getInputFileName())){
				vlogError("mff.util.ZipUtility:unzip:InputFile name is empty");
				throw new FileNotFoundException("Input file name is empty");
			}
			
			if(StringUtils.isEmpty(getOutputFileName())){
				vlogError("mff.util.ZipUtility:unzip:Output name is empty");
				throw new FileNotFoundException("Output file name is empty");
			}
			
			String inputFileName=getLocation().trim()+getInputFileName();
			String outputFileName=getLocation().trim()+getOutputFileName();
			
			GZIPOutputStream gzos =
		    		new GZIPOutputStream(new FileOutputStream(outputFileName));

	        FileInputStream in =
	            new FileInputStream(inputFileName);

	        int len;
	        while ((len = in.read(buffer)) > 0) {
	        	gzos.write(buffer, 0, len);
	        }

	        in.close();
	    	gzos.finish();
	    	gzos.close();
	    	
	    	vlogInfo("mff.util.ZipUtility:unzip:End");
	    	

	    }catch(FileNotFoundException ex){
	    	vlogError("mff.util.ZipUtility:unzip:Filenotfound exception occurred",ex);
	    }
		catch(IOException ex){
	       vlogError("mff.util.ZipUtility:unzip:IOException occurred",ex);
	    }
	}
	
	public void moveFileToArchive(){
		try{
			vlogInfo("mff.util.ZipUtility:moveFileToArchive:Start");
			File afile=new File(getLocation()+getInputFileName());
			if(afile.renameTo(new File(getLocation()+"archive/"+getInputFileName()))){
				vlogInfo("mff.util.ZipUtility:moveFileToArchive:File Moved");
			}
			else {
				vlogInfo("mff.util.ZipUtility:moveFileToArchive:File not moved");
			}
			vlogInfo("mff.util.ZipUtility:moveFileToArchive:End");
		}catch(Exception ex){
			vlogError("mff.util.ZipUtility:moveFileToArchive:exception occurred",ex);
		}
	}
	
	
	
}
