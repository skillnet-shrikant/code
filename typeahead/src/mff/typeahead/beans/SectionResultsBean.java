package mff.typeahead.beans;

import java.util.List;

/**
 * A bean that contains section results.
 *
 * @author foldenburg
 */


public class SectionResultsBean {

	private boolean hasContent;
	private String searchTerm;
	private List<SectionBean> sections;

	/**
	 * Constructor
	 * @param searchTerm result search term
	 * @param sections Result section list
	 */



	public SectionResultsBean(String searchTerm, List<SectionBean> sections) {
		super();
		this.searchTerm = searchTerm;
		this.sections = sections;
	}

	public SectionResultsBean() {
		super();

	}

	public List<SectionBean> getSections() {
		return sections;
	}

	public void setSections(List<SectionBean> sections) {
		this.sections = sections;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean isHasContent() {
		return hasContent;
	}

	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}

	/**
	 * Determines whether bean contains any results
	 * @return true if term results are present
	 */
	public boolean containsResults() {
		return (sections.size() > 0);
	}

}
