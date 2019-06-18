package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.SuggestionBean;

import com.endeca.navigation.ENEQueryResults;


/**
 * An abstract class to be extended into adapters for extracting typeahead result information
 * from Endeca results objects
 *
 * @author foldenburg
 *
 */
public abstract class SuggestionResultAdapter {

	// Endeca Query Results object
	protected ENEQueryResults queryResults;

	/**
	 * Constructor
	 *
	 * @param queryResults Endeca query results object
	 */
	public SuggestionResultAdapter(ENEQueryResults queryResults) {
		this.queryResults = queryResults;
	}

	/**
	 * Private constructor to force subclasses to use the parameterized constructor
	 */
	@SuppressWarnings("unused")
	private SuggestionResultAdapter() { }

	/**
	 * The central method to be used in suggestion subclasses.
	 * Extracts result information from the Endeca results object and processes it into SuggestionBeans
	 * @param ret List of SuggestionBean to clone and add to
	 * @param maxBeans Maximum number of beans to return
	 * @return A List of SuggestionBean objects
	 */
	public abstract List<SuggestionBean> addBeansToList(List<SuggestionBean> ret, int maxBeans);


	/**
	 * A wrapper method that calls addBeansToList with a new List of ResultBeans
	 * @param maxBeans Maximum number of beans to return
	 * @return A List of ResultBean objects
	 */
	public List<SuggestionBean> getBeansAsList(int maxBeans) {
		// Call addBeansToList with an empty List of ResultBean
		return addBeansToList(new ArrayList<SuggestionBean>(), maxBeans);
	}


}
