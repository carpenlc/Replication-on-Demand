package mil.nga.exceptions;

/**
 * Custom exception raised if any issues are encountered during validation 
 * of the incoming query request.
 * 
 * @author L. Craig Carpenter
 */
public class InvalidQueryRequestException extends Exception {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -654435725406653208L;
	
	/** 
	 * Default constructor requiring a message String.
	 * @param msg Information identifying why the exception was raised.
	 */
	public InvalidQueryRequestException(String msg) {
		super(msg);
	}
}
