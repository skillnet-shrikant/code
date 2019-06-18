package mff.typeahead.endeca;

/**
 * A super-class Exception to be extended by all custom exceptions. 
 * 
 * The intent of using these custom exceptions is to remove the interaction
 * between resource classes and Endeca-API-specific exception types.
 * 
 * Any other API-specific exceptions should be wrapped in a subclass of
 * SearchService exception. 
 * 
 * @author foldenburg
 * 
 */
public class SearchServiceException extends Exception {

	private static final long serialVersionUID = -1278920340747697347L;

	/**
	 * Constructor
	 * 
	 * @param cause The exception being wrapped by this one
	 */
	public SearchServiceException(Throwable cause) {
		this.initCause(cause);
	}
	
	/**
	 * Constructor
	 * 
	 * @param message The message to wrap in the underlying Exception
	 */
	public SearchServiceException(String message) {
		this.initCause(new Exception(message));
	}
}
