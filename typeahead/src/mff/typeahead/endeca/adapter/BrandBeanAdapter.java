package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.ResultBean;
import mff.typeahead.util.StringUtil;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.soleng.urlformatter.UrlState;
import com.endeca.soleng.urlformatter.seo.SeoUrlFormatter;

/**
 * A class to adapt the Endeca query results to a list of ResultBean objects
 * Brand data can be extracted either from navigation refinements, dimension search results, or both.
 *
 * @author foldenburg
 *
 */
public class BrandBeanAdapter extends ResultAdapter {

	// Name for the Brand dimension
	private static final String BRAND_DIM_NAME = "product.brand";

	// Base used for N-values
	protected static final int NVALUE_BASE = 10;

	// UrlState used for creating SEO URLs
	protected UrlState urlState;

	// If true, product counts are concatenated onto brand names
	private boolean showProductCounts = false;

	// Original search terms - needed for creating the SEO URL
	protected String searchTerms;

	// If true, brand names are extracted from navigation refinements
	protected boolean processNavigationSearchResults;

	// If true, brand names are extracted from dimension search results
	protected boolean processDimensionSearchResults;

	/**
	 * Constructor
	 *
	 * @param queryResults Endeca query results object
	 * @param processNavRefinements Pull brands from navigation refinements
	 * @param processDimensionSearchResults Pull brands from dim search results
	 * @param seoUrlFormatter SeoUrlFormatter for instantiating the UrlState
	 * @param searchTerms Search terms for creating SEO URLs
	 * @param showProductCounts Cat product counts onto brand names
	 */
	public BrandBeanAdapter(ENEQueryResults queryResults, boolean processNavigationSearchResults,
			boolean processDimensionSearchResults, SeoUrlFormatter seoUrlFormatter,
			String searchTerms, boolean showProductCounts) {
		super(queryResults);
		this.processDimensionSearchResults = processDimensionSearchResults;
		this.processNavigationSearchResults = processNavigationSearchResults;
		this.searchTerms = searchTerms;
		this.showProductCounts = showProductCounts;
		if(seoUrlFormatter!=null) {
			urlState = new UrlState(seoUrlFormatter,seoUrlFormatter.getDefaultEncoding());
			urlState.inform(queryResults);
		}
		else {
			//
		}
	}

	/**
	 * Generates a list of ResultBeans containing brand data
	 *
	 * @param maxBeans Max number of beans to return
	 * @return ArrayList of ResultBeans with brand data
	 */
	@Override
	public List<ResultBean> addBeansToList(List<ResultBean> beans , int maxBeans) {


		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);

		if(processNavigationSearchResults) {
			ret = addResultsFromNavImplicitLocations(beans, maxBeans, BRAND_DIM_NAME);
			ret = addResultsFromNavRefinements(ret, maxBeans, BRAND_DIM_NAME);

		}
		if(processDimensionSearchResults) {
			ret = addResultsFromDimensionSearch(ret, maxBeans, BRAND_DIM_NAME);
		}


		return ret;
	}

	/**
	 * Gets the seoUrl for a given brand
	 *
	 * @param dimLoc DimLocation object
	 * @return String SEO URL for the brand
	 */
	@Override
	protected String getSeoUrl(DimLocation dimLoc) {
		StringBuffer seoUrl = new StringBuffer();
		seoUrl.append("/brand/");
		seoUrl.append(StringUtil.cleanString(dimLoc.getDimValue().getName()));
		if(urlState!=null) {
			UrlState tempState = urlState.selectRefinement(dimLoc, true);
			seoUrl.append(tempState.toString());
		}
		return seoUrl.toString();
	}

	/**
	 * Generates an SEO URL for brand dimVal
	 *
	 * @param dim Brand dimension object
	 * @param d Brand dimension value
	 * @return SEO URL for the given brand
	 */
	@Override
	protected String getSeoUrl(Dimension dim, DimVal d) {
		StringBuffer seoUrl = new StringBuffer();
		seoUrl.append("/brand");
		if(urlState!=null) {
			UrlState tempState = urlState.selectRefinement(dim, d, true);
			seoUrl.append(tempState.toString());
		}
		return seoUrl.toString();
	}

	/**
	 * Builds the result label from the supplied DimVal
	 *
	 * @param d DimVal object for creating result label
	 * @return String containing Result label
	 */
	@Override
	protected String getResultLabel(DimVal d) {
		String prodCount = (String)d.getProperties().get("DGraph.Bins");
		StringBuilder brandName = new StringBuilder(d.getName());
		if(showProductCounts && prodCount!=null && !prodCount.equals("")) {
			brandName.append(" (");
			brandName.append(prodCount);
			brandName.append(")");
		}
		return brandName.toString();
	}
}
