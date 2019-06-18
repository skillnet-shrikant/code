package com.mff.browse.droplet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.mff.commerce.catalog.MFFCatalogTools;
import com.mff.constants.MFFConstants;
import com.mff.util.MFFDynamicAttributesUtil;

/**
 * This Droplet returns image file names based on the MFF file name conventions
 *
 * Product Images : /product/productId/imageSize/imageNumber.jpg
 *
 * @author DMI
 *
 */
public class ProductImageDroplet extends DynamoServlet {

  private static final String PRODUCTID = "productId";
  private static final String IMAGESIZE = "imageSize";
  private static final String PRODUCT = "product";
  private static final String EMPTY = "empty";
  private int mNoOfImagesPerImageSize;
  private String mImageFileExtension;
  private String mDefaultImageSize;
  private ArrayList<String> mSizesList;
  private boolean mUseSharedFilePath;
  private String mImagesSharedPath;

  private MFFDynamicAttributesUtil mDynamicAttributeUtil;

  private MFFCatalogTools mCatalogTools;

  public String getImagesSharedPath(){
	  return mImagesSharedPath;
  }

  public void setImagesSharedPath(String pImagesSharedPath){
	  mImagesSharedPath=pImagesSharedPath;
  }

  public boolean isUseSharedFilePath(){
	  return mUseSharedFilePath;
  }

  public void setUseSharedFilePath(boolean pUseSharedFilePath){
	  mUseSharedFilePath=pUseSharedFilePath;
  }

  /*
   * Returns the image filenames along with path as a list params : productId
   * (String), imageType(String) returns : ArrayList
   */
  @SuppressWarnings({ "unchecked" })
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    String productId = pRequest.getParameter(PRODUCTID);
    String imageSize = pRequest.getParameter(IMAGESIZE);
    vlogDebug("ProductId :{0} - imageSize :{1}",productId);
    if (StringUtils.isNotEmpty(productId)) {
      List<String> imagesList = new ArrayList<String>();
      if (StringUtils.isEmpty(imageSize)) {
        imageSize = getDefaultImageSize();
      }
      if (getSizesList().contains(imageSize)) {
	      if(isUseSharedFilePath()){
	    	  String imageFileStartsWith = File.separator + PRODUCT + File.separator + productId + File.separator + imageSize;
	    	  imagesList.addAll(getImageNamewithNumberFromPath(imageFileStartsWith));
	      }
	      else {
	    	  MFFCatalogTools catalogTools=getCatalogTools();
        	  if(catalogTools!=null){
        		  imagesList.addAll(getImageNamewithNumber(imageSize,catalogTools.numberOfImages(productId)));
              }
	      }
      }

      if (imagesList != null && !imagesList.isEmpty()) {
        pRequest.setParameter("productImages", imagesList);
        pRequest.serviceLocalParameter(MFFConstants.OUTPUT, pRequest, pResponse);
      } else {
        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      }

    } else {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }

  }

  @SuppressWarnings({ "rawtypes"})
  private List getImageNamewithNumber(String imageSize,int numberOfImages) {
    List<String> imageNames = new ArrayList<String>();
    String fileNameWithRelativePath=null;
    for(int i=1;i<=numberOfImages;i++){
    	fileNameWithRelativePath=new StringBuffer("").append(Integer.toString(i)).append(getImageFileExtension()).toString();
    	imageNames.add(fileNameWithRelativePath);
    }
    return imageNames;
  }

  @SuppressWarnings({ "rawtypes"})
  private List getImageNamewithNumberFromPath(String pImageNameStartsWith) {
    List<String> imageNames = new ArrayList<String>();
    String filename=null;
    String webRootPath=getImagesSharedPath();
    pImageNameStartsWith=new StringBuffer(webRootPath).append(File.separator).append(pImageNameStartsWith).append(File.separator).toString();
    for (int i = 1; i <= getNoOfImagesPerImageSize(); i++) {
      filename=new StringBuffer(pImageNameStartsWith).append(Integer.toString(i)).append(getImageFileExtension()).toString();

      File fileExists=new File(filename);
      if(fileExists!=null && fileExists.exists()){
        imageNames.add(new StringBuffer(Integer.toString(i)).append(getImageFileExtension()).toString());
      }
    }

    return imageNames;
  }

  public MFFCatalogTools getCatalogTools(){
	  return mCatalogTools;
  }

  public void setCatalogTools(MFFCatalogTools pCatalogTools){
	  mCatalogTools=pCatalogTools;
  }

  public int getNoOfImagesPerImageSize() {
    return mNoOfImagesPerImageSize;
  }

  public void setNoOfImagesPerImageSize(int pNoOfImagesPerImageSize) {
    this.mNoOfImagesPerImageSize = pNoOfImagesPerImageSize;
  }

  public String getImageFileExtension() {
    return mImageFileExtension;
  }

  public void setImageFileExtension(String pImageFileExtension) {
    this.mImageFileExtension = pImageFileExtension;
  }

  public MFFDynamicAttributesUtil getDynamicAttributeUtil() {
    return mDynamicAttributeUtil;
  }

  public void setDynamicAttributeUtil(MFFDynamicAttributesUtil pDynamicAttributeUtil) {
    this.mDynamicAttributeUtil = pDynamicAttributeUtil;
  }

  public String getDefaultImageSize() {
    return mDefaultImageSize;
  }

  public void setDefaultImageSize(String pDefaultImageSize) {
    this.mDefaultImageSize = pDefaultImageSize;
  }

  public ArrayList<String> getSizesList() {
    return mSizesList;
  }

  public void setSizesList(ArrayList<String> pSizesList) {
    this.mSizesList = pSizesList;
  }

}
