/**
 * 
 */
package mff.typeahead.endeca;

/**
 * @author foldenburg
 *
 */
public class ConfigurationException extends SearchServiceException {

	private static final long serialVersionUID = -1278920340747697348L;

	/**
	 * Constructor
	 * 
	 * @param cause Wrapped Exception/Cause
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor
	 * 
	 * @param message Wrapped message
	 */
	public ConfigurationException(String message) {
		super(message);
	}
}
