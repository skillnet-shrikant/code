package mff.typeahead.beans;

/**
 * A bean that contains typeahead results.
 *
 * @author foldenburg
 */



public class ResultBean {

	private String title;
	private String brand;
	private String url;
	private String image;

	@SuppressWarnings("unused")
	private ResultBean() {  }

	
	public ResultBean(String title,String brand, String url, String image) {
		this.title = title;
		this.brand = brand;
		this.url = url;
		this.image = image;
	}
	/**
	 * Constructor
	 * @param title Result title
	 * @param url Result URL
	 * @param image Result Image URL
	 */
	public ResultBean(String title, String url, String image) {
		this.title = title;
		this.url = url;
		this.image = image;
	}

	public ResultBean(String title, String url) {
		this.title = title;
		this.url = url;
	}

	@Override
	public String toString() {
		return title + " : " + url + " : " + image;
	}


	/**
	 * Overriding equals to enable duplicate checking when
	 * generating results.
	 *
	 * Only title and url are considered when determining equality.
	 *
	 * @param o Object to compare to
	 * @return true if equals, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof ResultBean) {
			ResultBean bean = (ResultBean)o;
			String beanBrand = bean.getBrand();
			String beanTitle = bean.getTitle();
			String beanImage = bean.getImage();
			String beanUrl = bean.getUrl();
			return (beanTitle==null || beanTitle.equals(this.title)) &&
					(beanImage==null || beanImage.equals(this.image)) &&
					(beanBrand==null || beanBrand.equals(this.brand)) &&
					(beanUrl==null || beanUrl.equals(this.url));
		}
		return false;
	}

	/**
	 * Overriding hashCode to correspond to overridden equals() method.
	 * Hash code is the sum of name and desc, where applicable
	 * @return Hash code integer sum of name and desc
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		if (this.title!=null) {
			hash += this.title.hashCode();
		}
		if (this.url!=null) {
			hash += this.url.hashCode();
		}
		return hash;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
