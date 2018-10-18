package mil.nga.exceptions;

/**
 * Exception raised when an unsupported image type is requested.
 * 
 * @author L. Craig Carpenter
 */
public class UnknownImageTypeException extends Exception {

    /**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 5612905918236386625L;

	/** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public UnknownImageTypeException(String msg) {
        super(msg);
    }
}
