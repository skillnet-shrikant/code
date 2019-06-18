package mff.typeahead.endeca;

/**
 * A custom exception indicating an issue with the remote service being queried.
 *  
 * @author foldenburg
 * 
 */
public class QueryException extends SearchServiceException {

	private static final long serialVersionUID = 4784162889392972363L;

	/**
	 * Constructor
	 * 
	 * @param cause The exception being wrapped by this one
	 */
	public QueryException(Throwable cause) {
		super(cause);
	}
}
