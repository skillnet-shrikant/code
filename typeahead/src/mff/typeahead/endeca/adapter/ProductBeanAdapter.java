package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.ResultBean;
import mff.typeahead.util.UrlBuilder;

import com.endeca.navigation.AssocDimLocations;
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;

/**
 * An Adapter to create ResultBeans containing product information from Endeca
 * Results
 * @author foldenburg
 *
 */
public class ProductBeanAdapter extends ResultAdapter {

 //Name for the Brand dimension
 private static final String BRAND_DIM_NAME = "product.brand";

	/**
	 * Constructor
	 *
	 * @param results Endeca query results object
	 */
	public ProductBeanAdapter(ENEQueryResults results) {
		super(results);
	}

	/**
	 * Generates a List of ResultBeans containing product information
	 * from the supplied ENEQueryResults object using Endeca navigation refinements as well
	 * as dimension search results, if applicable
	 *
	 * @return List of ResultBeans with product data
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResultBean> addBeansToList(List<ResultBean> beans, int maxBeans) {
//		final String methodName = "getBeansAsList";

		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);

		if(queryResults!=null && queryResults.containsNavigation()) {
			for(ERec rec : (List<ERec>)queryResults.getNavigation().getERecs()) {
				// Return if desired number of beans has been reached
				if(ret.size()>=maxBeans) {
					return ret;
				}

				String imageUrl;
				String brandName=null;
				String  productNameWithoutBrand;
			// Get brand name from ERec object
				for(Object lAssocDimLocationsObj: rec.getDimValues()) {
				  AssocDimLocations lAssocDimLocations = ((AssocDimLocations)lAssocDimLocationsObj);
				  for(int i=0;i<lAssocDimLocations.size();i++) {
				    DimVal lDimVal = ((DimLocation)lAssocDimLocations.get(i)).getDimValue();
				    if(lDimVal.getDimensionName().equals(BRAND_DIM_NAME)) {
	            brandName = lDimVal.getName();
	            break;
				    }
				  }
				  if(brandName!=null) {
				    break;
				  }
				}
				// Get needed properties from ERec object
				String productName = (String) rec.getProperties().get("product.description");
				String repositoryId = (String) rec.getProperties().get("product.repositoryId");
				//Getting new String removing Brand Name from Product displayname
				productNameWithoutBrand = productName;
				if(brandName!=null && productName.startsWith(brandName)) {
					productNameWithoutBrand = productName.replace(brandName, "").trim();
				}
				
				if (rec.getProperties().get("product.thumbnailUrl") !=null) {
					imageUrl = (String) rec.getProperties().get("product.thumbnailUrl");
				}
				else {
					imageUrl = "/images/product/unavailable/th.jpg";
				}

				// Generate a product url
				String url = UrlBuilder.buildProductUrl(repositoryId,productName);

				// Generate the image url
				//String imageUrl = UrlBuilder.buildProductImageUrl(imageName);
				//String imageUrl = "http://placehold.it/250x250";


				//ResultBean bean = new ResultBean(buildFullProductName(brandName, productDesc),url,imageUrl);
				ResultBean bean = new ResultBean(productNameWithoutBrand,brandName,url,imageUrl);

				if(!ret.contains(bean)) {
					ret.add(bean);
				}
			}
		}

		return ret;
	}

	/**
	 * Concatenates the non-null components of brand name and product description together
	 * @param brandName Brand name
	 * @param productDesc Product description
	 * @return Concatenated string
	 */
	@SuppressWarnings("unused")
	private static String buildFullProductName(String brandName, String productDesc) {
		StringBuilder fullProductName = new StringBuilder();
		if(brandName!=null && !brandName.isEmpty()) {
			fullProductName.append(brandName);
			fullProductName.append(' ');
		}
		if(productDesc!=null && !productDesc.isEmpty()) {
			fullProductName.append(productDesc);
		}
		return fullProductName.toString();
	}
}
