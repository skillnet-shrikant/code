package mff.typeahead.beans;


/**
 * A class that encapsulates a single error.
 *
 * @author foldenburg
 */
public class ErrorBean {

	private String code;
	private String message;

	/**
	 * Constructor
	 */
	public ErrorBean() { }

	/**
	 * Constructor
	 * @param code Error code
	 * @param message Error message
	 */
	public ErrorBean(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */

	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */

	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
