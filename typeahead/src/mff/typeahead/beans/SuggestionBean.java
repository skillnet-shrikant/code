package mff.typeahead.beans;

/**
 * A bean that contains individual term suggestions.
 *
 * @author foldenburg
 */


public class SuggestionBean {

	private String term;
	private String url;
	private String detailUrl;

	@SuppressWarnings("unused")
	private SuggestionBean() {  }

	/**
	 * Constructor
	 * @param term Result
	 * @param url Result URL
	 * @param detailUrl Result Detail URl
	 */
	public SuggestionBean(String pTerm, String pUrl, String pDetailUrl) {
		this.term = pTerm;
		this.url = pUrl;
		this.detailUrl = pDetailUrl;
	}

	@Override
	public String toString() {
		return term + " : " + " : " + url + " : " + detailUrl;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String pTerm) {
		this.term = pTerm;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String pUrl) {
		this.url = pUrl;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String pDetailUrl) {
		this.detailUrl = pDetailUrl;
	}



	/**
	 * Overriding equals to enable duplicate checking when
	 * generating results.
	 *
	 * @param o Object to compare to
	 * @return true if equals, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof SuggestionBean) {
			SuggestionBean bean = (SuggestionBean)o;
			String beanTerm = bean.getTerm();
			String beanDetailUrl = bean.getDetailUrl();
			String beanUrl = bean.getUrl();
			return  (beanTerm==null || beanTerm.equals(this.term)) &&
					(beanDetailUrl==null || beanDetailUrl.equals(this.detailUrl)) &&
					(beanUrl==null || beanUrl.equals(this.url));
		}
		return false;
	}

	/**
	 * Overriding hashCode to correspond to overridden equals() method.
	 * Hash code is the sum of name and detailUrl, where applicable
	 * @return Hash code integer sum of name and detailUrl
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.term!=null) {
			hash += this.term.hashCode();
		}
		if (this.detailUrl!=null) {
			hash += this.detailUrl.hashCode();
		}
		return hash;
	}


}
