package mil.nga.exceptions;

/**
 * Exception raised when an unsupported hashing algorithm is requested.
 * 
 * @author L. Craig Carpenter
 */
public class UnknownHashTypeException extends Exception {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -8266532817396136239L;
    
    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public UnknownHashTypeException(String msg) {
        super(msg);
    }

}
