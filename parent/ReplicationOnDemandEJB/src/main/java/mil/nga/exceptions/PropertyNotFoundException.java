package mil.nga.exceptions;

/**
 * Custom exception thrown if we were a requested property was not found
 * in the target Properties file.
 * 
 * @author L. Craig Carpenter
 */
public class PropertyNotFoundException extends Exception {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -6991871513486495993L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public PropertyNotFoundException(String msg) {
        super(msg);
    }
}
