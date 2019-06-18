package mff.typeahead.beans;

import java.util.ArrayList;
import java.util.List;


/**
 * A bean that contains search term suggestions.
 *
 * @author foldenburg
 */


public class SuggestionResultsBean {

	private String searchTerm;
	private List<SuggestionBean> results;



	/**
	 * Constructor
	 */
	public SuggestionResultsBean() {
		this.results = new ArrayList<SuggestionBean>();

	}

	/**
	 * Constructor
	 * @param terms List of term suggestions
	 */
	public SuggestionResultsBean(List<SuggestionBean> results) {
		this.results = results;
	}

	/**
	 * Constructor
	 * @param terms List of term suggestions
	 */
	public SuggestionResultsBean(String pSearchTerm, List<SuggestionBean> results) {
		this.searchTerm = pSearchTerm;
		this.results = results;

	}

	/**
	 * @return the search term
	 */

	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @param search term
	 */

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}



	public List<SuggestionBean> getResults() {
		return results;
	}

	/**
	 * @param terms the terms to set
	 */
	public void setResults(List<SuggestionBean> results) {
		this.results = results;
	}

	/**
	 * Determines whether bean contains any results
	 * @return true if term results are present
	 */
	public boolean containsResults() {
		return (results.size() > 0);
	}



}
