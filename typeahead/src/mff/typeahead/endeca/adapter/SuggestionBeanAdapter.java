package mff.typeahead.endeca.adapter;

import java.util.ArrayList;
import java.util.List;

import mff.typeahead.beans.SuggestionBean;
import mff.typeahead.util.UrlBuilder;

import com.endeca.navigation.AggrERec;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;

/**
 * An Adapter to create ResultBeans containing term information from Endeca
 * Results
 *
 * @author foldenburg
 *
 */
public class SuggestionBeanAdapter extends SuggestionResultAdapter {

	private static final String CORRECTION_PROP = "term";

	/**
	 * Constructor
	 * @param queryResults A reference to the Query Results object
	 */
	public SuggestionBeanAdapter(ENEQueryResults queryResults) {
		super(queryResults);
	}

	/**
	 * Generates a List of ResultBeans containing terms suggestions
	 *
	 * @return List of ResultBeans with autocomplete term data
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SuggestionBean> addBeansToList(List<SuggestionBean> beans, int maxBeans) {
		// Instantiate a new list from the passed-in list (defensive copy)
		List<SuggestionBean> ret = new ArrayList<SuggestionBean>(beans);

		Navigation nav = queryResults.getNavigation();
		if(nav!=null) {
			if(nav.getAggrERecs()!=null && nav.getAggrERecs().size() > 0) {
				// If using aggregate search (rel rank enabled), use the representative
				// records to build results
				for(AggrERec aRec : (List<AggrERec>)nav.getAggrERecs()) {
					if(ret.size()<maxBeans) {
						ERec rec = aRec.getRepresentative();
						SuggestionBean bean = buildBeanFromERec(rec);
						if(!ret.contains(bean)) {
							ret.add(bean);
						}
					}
				}
			} else if(nav.getERecs()!=null && nav.getERecs().size() > 0) {
				// If navigation search, build records from standard ERecs
				for(ERec rec : (List<ERec>)nav.getERecs()) {
					if(ret.size()<maxBeans) {
						SuggestionBean bean = buildBeanFromERec(rec);
						if(!ret.contains(bean)) {
							ret.add(bean);
						}
					}
				}
			}

		}
		return ret;
	}

	/**
	 * Builds a new Term ResultBean from a supplied ERec
	 * @param rec Endeca record object
	 * @return new ResultBean object
	 */
	private SuggestionBean buildBeanFromERec(ERec rec) {
		PropertyMap props = rec.getProperties();
		String term = (String)props.get(CORRECTION_PROP);
		String url = UrlBuilder.buildSearchUrl(term);
		String detailUrl = UrlBuilder.buildTypeaheadDetailUrl(term);
		return new SuggestionBean(term, url, detailUrl);
	}
}
