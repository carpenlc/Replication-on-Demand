package mil.nga.exceptions;

import java.io.Serializable;

/**
 * Exception thrown where there are catastrophic system level issues that 
 * cannot be dealt with in code.  Such as: cannot obtain a reference to an 
 * EJB, cannot start JPA, etc.
 *  
 * @author L. Craig Carpenter
 */
public class ServiceUnavailableException 
		extends Exception implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 8562727955943265674L;

	/**
	 * Default constructor expecting an error message.
	 * @param msg Description of the error causing the exception.
	 */
	public ServiceUnavailableException(String msg) {
		super(msg);
	}
}
