package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.ResultBean;

import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.Navigation;


/**
 * An abstract class to be extended into adapters for extracting typeahead result information
 * from Endeca results objects
 *
 * @author foldenburg
 *
 */
public abstract class ResultAdapter {

	// Endeca Query Results object
	protected ENEQueryResults queryResults;

	/**
	 * Constructor
	 *
	 * @param queryResults Endeca query results object
	 */
	public ResultAdapter(ENEQueryResults queryResults) {
		this.queryResults = queryResults;
	}

	/**
	 * Private constructor to force subclasses to use the parameterized constructor
	 */
	@SuppressWarnings("unused")
	private ResultAdapter() { }

	/**
	 * The central method to be used in all subclasses.
	 * Extracts result information from the Endeca results object and processes it into ResultBeans
	 * @param ret List of ResultBean to clone and add to
	 * @param maxBeans Maximum number of beans to return
	 * @return A List of ResultBean objects
	 */
	public abstract List<ResultBean> addBeansToList(List<ResultBean> ret, int maxBeans);


	/**
	 * A wrapper method that calls addBeansToList with a new List of ResultBeans
	 * @param maxBeans Maximum number of beans to return
	 * @return A List of ResultBean objects
	 */
	public List<ResultBean> getBeansAsList(int maxBeans) {
		// Call addBeansToList with an empty List of ResultBean
		return addBeansToList(new ArrayList<ResultBean>(), maxBeans);
	}

	/**
	 * Process the navigation refinements and create ResultBean objects for typeahead results
	 * @param ret A list of ResultBeans which is copied and added to
	 * @param maxBeans Maximum number of results to retrieve
	 * @param dimensionName Name of dimension to get refinements from
	 * @return A new ArrayList of ResultBean containing the passed-in objects as well
	 *  as those added from the navigation refinements
	 */
	@SuppressWarnings("unchecked")
	protected List<ResultBean> addResultsFromNavRefinements(List<ResultBean> beans, int maxBeans,
			String dimensionName) {

		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);

		if(queryResults==null || queryResults.getNavigation()==null
				|| queryResults.getNavigation().getCompleteDimensions()==null) {
			return ret;
		}
		Dimension dim =
				queryResults.getNavigation().getCompleteDimensions().getDimension(dimensionName);
		if(dim!=null) {
			for(DimVal d : (List<DimVal>)dim.getRefinements()) {
				// Return if desired number of beans has been reached
				if(ret.size()>=maxBeans) {
					return ret;
				}

				String seoUrl = getSeoUrl(dim, d);
				String resultLabel = getResultLabel(d);
				ResultBean bean = new ResultBean(resultLabel,seoUrl,"");
				if(!ret.contains(bean)) {
					ret.add(bean);
				}
			}
		}
		return ret;
	}

	/**
	 * Processes dimension results and creates ResultBean objects containing result information
	 * @param ret A return value List passed by reference
	 * @param maxBeans Maximum number of beans to retrieve
	 * @param dimensionName Name of the dimension to extract results from
	 * @return A new ArrayList of ResultBean containing the passed-in objects as well
	 *  as those added from the dimension search results
	 */
	@SuppressWarnings("unchecked")
	protected List<ResultBean> addResultsFromDimensionSearch(List<ResultBean> beans,
			int maxBeans, String dimensionName) {
		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);

		if (queryResults!=null && queryResults.containsDimensionSearch()) {
			// Iterate over dimension search results to find DSRG with specified dim
			for (DimensionSearchResultGroup dimGroup : (List<DimensionSearchResultGroup>)
					queryResults.getDimensionSearch().getResults()) {
				for (DimLocationList dimLocList : (List<DimLocationList>) dimGroup) {
					for (DimLocation dimLoc : (List<DimLocation>) dimLocList) {
						// Return if desired number of beans has been
						// reached
						if (ret.size() >= maxBeans) {
							return ret;
						}
						DimVal dVal = dimLoc.getDimValue();
						if (dVal.getDimensionName().equals(dimensionName)) {
							String seoUrl = getSeoUrl(dimLoc);
							String resultLabel = getResultLabel(dVal);
							ResultBean bean = new ResultBean(resultLabel,seoUrl,"");
							if(!ret.contains(bean) && isLabelUnique(resultLabel,ret)) {
								ret.add(bean);
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * @param pLabel The string which has to searched in results
	 * @param pResultBeanList A List of result beans added so far
	 * @return boolean if the exists a bean with title same a input string
	 */
	protected boolean isLabelUnique(String pLabel, List<ResultBean> pResultBeanList) {
	  for(ResultBean pResultBean : pResultBeanList) {
	    if(pResultBean.getTitle().equals(pLabel)) {
	      return false;
	    }
	  }
	  return true;
	}

	/**
	 * Process the navigation implicit locations and create ResultBean objects
	 * for typeahead results
	 * @param ret A list of ResultBeans which is copied and added to
	 * @param maxBeans Maximum number of results to retrieve
	 * @param dimensionName Name of dimension to get implicit locations from
	 * @return A new ArrayList of ResultBean containing the passed-in objects as well
	 *  as those added from the navigation implicit locations
	 */
	@SuppressWarnings("unchecked")
	protected List<ResultBean> addResultsFromNavImplicitLocations(List<ResultBean> beans, int maxBeans,
			String dimensionName) {
		// Instantiate a new list from the passed-in list (defensive copy)
		List<ResultBean> ret = new ArrayList<ResultBean>(beans);
		if (queryResults!=null && queryResults.containsNavigation()) {
			Navigation nav = queryResults.getNavigation();
			Dimension dim = nav.getCompleteDimensions().getDimension(dimensionName);
			if(dim!=null && dim.getImplicitLocations()!=null) {
				DimLocationList implicits = dim.getImplicitLocations();
				for(DimLocation dimLoc : (List<DimLocation>)implicits) {
					// Return if desired number of beans has been reached
					if(ret.size()>=maxBeans) {
						return ret;
					}

					String seoUrl = getSeoUrl(dimLoc);
					String resultLabel = getResultLabel(dimLoc.getDimValue());
					ResultBean bean = new ResultBean(resultLabel,seoUrl,"");
					if(!ret.contains(bean)) {
						ret.add(bean);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * This method is intended to be overridden in subclasses to generate an appropriate SEO URL.
	 * @param dimLoc A DimLocation object to use in generating the SEO URL
	 * @return A string containing the SEO URL
	 */
	protected String getSeoUrl(DimLocation dimLoc) {
		return "";
	}

	/**
	 * This method is intended to be overridden in subclasses to generate an appropriate SEO URL.
	 * @param dim A Dimension object to use in generating the SEO URL
	 * @param dVal A DimVal object to use in generating the SEO URL
	 * @return A string containing the SEO URL
	 */
	protected String getSeoUrl(Dimension dim, DimVal dVal) {
		return "";
	}

	/**
	 * This method is intended to be overridden in subclasses to generate an appropriate ResultBean
	 * label.
	 * @param dVal A DimVal object to use in generating the Result label
	 * @return A string containing the Result label
	 */
	protected String getResultLabel(DimVal dVal) {
		return "";
	}
}
