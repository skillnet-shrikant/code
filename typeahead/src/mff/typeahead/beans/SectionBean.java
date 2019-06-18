package mff.typeahead.beans;

import java.util.List;

public class SectionBean {

	private String title;
	private List<ResultBean> links;

	/**
	 * Constructor
	 * @param title Result title
	 * @param links Result URLs list
	 */

	public SectionBean(String title, List<ResultBean> links) {
		super();
		this.title = title;
		this.links = links;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<ResultBean> getLinks() {
		return links;
	}
	public void setLinks(List<ResultBean> links) {
		this.links = links;
	}
	public SectionBean() {
		super();
	}



}
