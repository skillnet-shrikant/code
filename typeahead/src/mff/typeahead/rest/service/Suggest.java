package mff.typeahead.rest.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

//import atg.nucleus.Nucleus;
import mff.typeahead.beans.SuggestionBean;
import mff.typeahead.beans.SuggestionResultsBean;
import mff.typeahead.endeca.EndecaTypeaheadService;
import mff.typeahead.endeca.SearchServiceException;
import mff.typeahead.endeca.adapter.SuggestionBeanAdapter;
import mff.typeahead.util.StringUtil;

import com.endeca.navigation.ENEQueryResults;

/**
 * Resource class for term suggestion service. Generates term suggestion results
 * from the supplied search term.
 *
 * @author foldenburg
 */

@Path("suggest")
public class Suggest {
	private EndecaTypeaheadService endecaService;
	private int maxTerms = 10; // default to 10
	private int absoluteMaxResultCount;

	/**
	 * Main resource method for the term suggestion service. Queries custom
	 * Endeca index for term suggestion results.
	 *
	 * @return A SuggestionResultsBean containing term suggestions
	 */

	@GET
	@Path("{term}.js")
	@Produces("application/json")

	public SuggestionResultsBean getTerms(@PathParam("term") String term,
			@QueryParam("maxTerms") String pMaxTerms) {

		endecaService = new EndecaTypeaheadService();

		// Set some values from the endeca typeahead service.
		// If maxTerms is passed in, use it. Otherwise use the default

		setAbsoluteMaxResultCount(endecaService.getAbsoluteMaxTermResultCount());
		if (pMaxTerms != null && !pMaxTerms.isEmpty()) {
			setMaxTerms(Integer.parseInt(pMaxTerms));
		}

		SuggestionResultsBean response = new SuggestionResultsBean();
		String sanitizedSearchTerm = StringUtil.sanitize(term);


		try {
			// Do the endeca query

			ENEQueryResults eneResults = endecaService.performTermQuery(
					sanitizedSearchTerm,
					Math.min(maxTerms, absoluteMaxResultCount));


			// Adapt the raw ENEQuery

			SuggestionBeanAdapter termAdapter = new SuggestionBeanAdapter(eneResults);
			List<SuggestionBean> results = termAdapter.getBeansAsList(Math.min(
					maxTerms, absoluteMaxResultCount));


			response.setSearchTerm(term);
			response.setResults(results);

		} catch (SearchServiceException e) {
			// On exception, log and return a 500 error

		}

		return response;

	}

	public int getMaxTerms() {
		return maxTerms;
	}

	public void setMaxTerms(int maxTerms) {
		this.maxTerms = maxTerms;
	}

	public int getAbsoluteMaxResultCount() {
		return absoluteMaxResultCount;
	}

	public void setAbsoluteMaxResultCount(int absoluteMaxResultCount) {
		this.absoluteMaxResultCount = absoluteMaxResultCount;
	}

}
