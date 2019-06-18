package mff.typeahead.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyReaderUtilSingleton {
	
	private static final String CONFIG_FILE_PATH="typeaheadConfigFilePath";
	private static final String CONFIG_FILE_NAME="mdex.properties";
	private static final String ENDECA_PRODUCT_HOST_PROPERTY_NAME="product.mdex.host";
	private static final String ENDECA_PRODUCT_HOST_PORT_PROPERTY_NAME="product.mdex.port";
	private static final String ENDECA_TERM_HOST_PROPERTY_NAME="term.mdex.host";
	private static final String ENDECA_TERM_HOST_PORT_PROPERTY_NAME="term.mdex.port";
	
	private static PropertyReaderUtilSingleton instance =null;
	private static String mEndecaProductIndexHost="localhost";
	private static int mEndecaProductIndexPort=15002;
	private static String mEndecaTermIndexHost="localhost";
	private static int mEndecaTermIndexPort=15002;
	
	public String getEndecaProductIndexHost() {
		return mEndecaProductIndexHost;
	}
	
	public int getEndecaProductIndexPort() {
		return mEndecaProductIndexPort;
	}
	
	public String getEndecaTermIndexHost() {
		return mEndecaTermIndexHost;
	}
		
	public int getEndecaTermIndexPort() {
		return mEndecaTermIndexPort;
	}
		
	private PropertyReaderUtilSingleton(){
		
	}
	
	@Override
	public String toString(){
		return "EndecaProductIndexHost:"+mEndecaProductIndexHost+" EndecaProductIndexPort:"+mEndecaProductIndexPort+" EndecaTermIndexHost:"+mEndecaTermIndexHost+" EndecaTermIndexPort:"+mEndecaTermIndexPort;
	}
	
	public static PropertyReaderUtilSingleton getInstance(){
		if(instance==null){
			synchronized (PropertyReaderUtilSingleton.class) {
				if(instance==null){
					instance=new PropertyReaderUtilSingleton();
					InputStream input=null;
					try {
						ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
						String systemProperty= System.getProperty(CONFIG_FILE_PATH);
						if(systemProperty !=null &&!systemProperty.trim().isEmpty()){
							String filePath=systemProperty+CONFIG_FILE_NAME;
							input = new FileInputStream(filePath);
						}
						else {
							input = classLoader.getResourceAsStream(CONFIG_FILE_NAME);
						}
						
							Properties properties = new Properties();
							if(input!=null){
								properties.load(input);
								String productIndexHost=properties.getProperty(ENDECA_PRODUCT_HOST_PROPERTY_NAME);
								String productIndexPort=properties.getProperty(ENDECA_PRODUCT_HOST_PORT_PROPERTY_NAME);
								String termIndexHost=properties.getProperty(ENDECA_TERM_HOST_PROPERTY_NAME);
								String termIndexPort=properties.getProperty(ENDECA_TERM_HOST_PORT_PROPERTY_NAME);
								
								if(productIndexHost!=null&&!productIndexHost.trim().isEmpty()){
									mEndecaProductIndexHost=productIndexHost;
								}
								if(productIndexPort!=null&&!productIndexPort.trim().isEmpty()){
									mEndecaProductIndexPort=Integer.parseInt(productIndexPort);
								}
								if(termIndexHost!=null&&!termIndexHost.trim().isEmpty()){
									mEndecaTermIndexHost=termIndexHost;
								}
								if(termIndexPort!=null&&!termIndexPort.trim().isEmpty()){
									mEndecaTermIndexPort=Integer.parseInt(termIndexPort);
								}
							}
							
		
					} catch (IOException e) {
								e.printStackTrace();
					}catch (Exception e) {
							e.printStackTrace();
					}finally{
						try {
							input.close();
						}
						catch(IOException e){
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return instance;
	}
	

}
