package com.mff.endeca.index.accessor;

import java.io.File;

import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * This method will returns a thumbnail URL if it exists:
 *
 * @author foldenburg
 *
 */
public class ProductThumnailAccessor extends PropertyAccessorImpl {

	String imageSize;
	String baseImagePath;
	String urlPrefix;


	@Override
	public Object getMetaPropertyValue(Context context, RepositoryItem product, String propertyName,PropertyTypeEnum type) {


		String thumbnailUrl = null;

		File imageDir = new File(getBaseImagePath() +
				"/" + product.getRepositoryId() + "/" +
				getImageSize());

		if (imageDir.exists()) {
			thumbnailUrl = getUrlPrefix() + "/" +
			product.getRepositoryId() + "/" +
			getImageSize() + "/1.jpg";
		}

		return thumbnailUrl;
	}


	/**
	 * @return the imageSize
	 */
	public String getImageSize() {
		return imageSize;
	}


	/**
	 * @param pImageSize the imageSize to set
	 */
	public void setImageSize(String pImageSize) {
		imageSize = pImageSize;
	}


	/**
	 * @return the baseImagePath
	 */
	public String getBaseImagePath() {
		return baseImagePath;
	}


	/**
	 * @param pBaseImagePath the baseImagePath to set
	 */
	public void setBaseImagePath(String pBaseImagePath) {
		baseImagePath = pBaseImagePath;
	}


	/**
	 * @return the urlPrefix
	 */
	public String getUrlPrefix() {
		return urlPrefix;
	}


	/**
	 * @param pUrlPrefix the urlPrefix to set
	 */
	public void setUrlPrefix(String pUrlPrefix) {
		urlPrefix = pUrlPrefix;
	}



}



