package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.ResultBean;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.soleng.urlformatter.UrlState;
import com.endeca.soleng.urlformatter.seo.SeoUrlFormatter;

/**
 * An Adapter to create ResultBeans containing Department information from Endeca
 * Results
 *
 * @author foldenburg
 *
 */
public class DepartmentBeanAdapter extends ResultAdapter {


	// UrlState for generating SEO URLs
	private UrlState urlState;

	// Suffix for product list page
	private static final String DEPT_URL_SUFFIX = "";

	// Dimension name for Departments
	private static final String DEPT_DIM_NAME = "product.category";

	// Constructor-specified configuration
	private boolean showProductCounts;
	private boolean processNavigationSearchResults;
	private boolean processDimensionSearchResults;

	/**
	 * Constructor
	 * @param queryResults ENEQueryResults object to pull Department dimension values from
	 * @param processNavigationRefinements If true, pull departments from navigation refinements
	 * @param processDimensionSearchResults If true, pull departments from dim search results
	 * @param seoUrlFormatter SeoUrlFormatter object for creating Seo URLs
	 * @param showProductCounts Flag to show product counts in parentheses next to department name
	 */
	public DepartmentBeanAdapter(ENEQueryResults queryResults, boolean processNavigationSearchResults,
			boolean processDimensionSearchResults, SeoUrlFormatter seoUrlFormatter,
			boolean showProductCounts) {
		super(queryResults);
		this.processDimensionSearchResults = processDimensionSearchResults;
		this.processNavigationSearchResults = processNavigationSearchResults;
		this.showProductCounts = showProductCounts;
		if(seoUrlFormatter!=null) {
			urlState = new UrlState(seoUrlFormatter,seoUrlFormatter.getDefaultEncoding());
			urlState.inform(queryResults);
		}
	}

	/**
	 * Generates a List of ResultBeans from the supplied ENEQueryResults object using
	 * Endeca navigation refinements as well as dimension search results, if applicable
	 *
	 * @return List of ResultBeans with Department data
	 */
	@Override
	public List<ResultBean> addBeansToList(List<ResultBean> beans, int maxBeans) {

		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);

		if(processNavigationSearchResults) {
			ret = addResultsFromNavImplicitLocations(beans, maxBeans, DEPT_DIM_NAME);
			ret = addResultsFromNavRefinements(ret, maxBeans, DEPT_DIM_NAME);
		}
		if(processDimensionSearchResults) {
			ret = addResultsFromDimensionSearch(ret, maxBeans, DEPT_DIM_NAME);
		}

		return ret;
	}

	/**
	 * Gets department name from DVal, adding product counts if enabled
	 *
	 * @param d DimVal object
	 * @return String containing department name
	 */
	@Override
	protected String getResultLabel(DimVal d) {
		StringBuilder deptName = new StringBuilder(d.getName());
		String prodCount = (String)d.getProperties().get("DGraph.Bins");
		if(showProductCounts && prodCount!=null && !prodCount.equals("")) {
			deptName.append(" (");
			deptName.append(prodCount);
			deptName.append(")");
		}
		return deptName.toString();
	}

	/**
	 * Gets the seoUrl for a given department
	 *
	 * @param dim Dimension object
	 * @param d DimVal object
	 * @return String SEO URL for the department
	 */
	@Override
	protected String getSeoUrl(Dimension dim, DimVal d) {
		StringBuffer seoUrl = new StringBuffer();
		seoUrl.append("/category");
		if(urlState!=null) {
			UrlState tempState = urlState.selectRefinement(dim, d, true);
			seoUrl.append(tempState.toString()).append(DEPT_URL_SUFFIX);
		}
		return seoUrl.toString();
	}

	/**
	 * Gets the seoUrl for a given department
	 *
	 * @param dimLoc DimLocation object
	 * @return String SEO URL for the department
	 */
	@Override
	protected String getSeoUrl(DimLocation dimLoc) {
		StringBuffer seoUrl = new StringBuffer();
		seoUrl.append("/category");
		if(urlState!=null) {
			UrlState tempState = urlState.selectRefinement(dimLoc, true);
			seoUrl.append(tempState.toString()).append(DEPT_URL_SUFFIX);
		}
		return seoUrl.toString();
	}
}
