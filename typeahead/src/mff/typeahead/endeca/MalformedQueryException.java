package mff.typeahead.endeca;

/**
 * A custom exception indicating a malformed or invalid query to a remote service.
 * 
 * @author foldenburg
 * 
 */
public class MalformedQueryException extends SearchServiceException {

	private static final long serialVersionUID = -5710150944014219354L;
	
	/**
	 * Constrcutor
	 * 
	 * @param cause The exception being wrapped by this one
	 */
	public MalformedQueryException(Throwable cause) {
		super(cause);
	}
}
