package mil.nga.rod.model;

import java.util.Optional;

/**
 * Adaptor class allowing us to decorate the <code>QueryRequestAccelerator</code> object with
 * the Artwork information.  This was added after an additional requirement was received to 
 * add the artwork data to the replication-on-demand interface.
 * 
 * @author L. Craig Carpenter
 */
public class QueryRequestAcceleratorDecorator {

	/**
	 * Object that will have the decorations applied.
	 */
	protected Optional<QueryRequestAccelerator> requestAccelerator;
	
	/**
	 * Default no-arg constructor required by JAX-B.
	 */
	public QueryRequestAcceleratorDecorator() {
		requestAccelerator = Optional.empty();
	}
	
	/**
	 * Constructor allowing the internal member to be set on construction.
	 */
	public QueryRequestAcceleratorDecorator(QueryRequestAccelerator value) {
		if (value == null) {
			requestAccelerator = Optional.empty();
		}
		else {
			requestAccelerator = Optional.of(value);
		}
	}
	
	/**
	 * Accessor method for the internal <code>QueryRequestAccelerator</code> object.
	 * @return The object handle
	 */
	public QueryRequestAccelerator getQueryRequestAccelerator() {
		if (requestAccelerator.isPresent()) {
			return requestAccelerator.get();
		}
		else {
			throw new IllegalStateException("Required QueryRequestAccelerator "
					+ "object not provided.");
		}
	}
	
	/**
	 * Mutator method for the internal <code>QueryRequestAccelerator</code> object.
	 * @param The object handle
	 */
	public void setQueryRequestAccelerator(QueryRequestAccelerator value) {
		if (value == null) {
			requestAccelerator = Optional.empty();
		}
		else {
			requestAccelerator = Optional.of(value);
		}
	}
	
	/**
	 * Convert to human-readable String.
	 */
	public String toString() {
		if (requestAccelerator.isPresent()) {
			return requestAccelerator.get().toString();
		}
		else {
			return "[ QueryRequestAccelerator not available ]";
		}
	}
}
