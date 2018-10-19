package mil.nga.rod.interfaces;

import java.util.List;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.model.RoDProduct;

/**
 * Interface implemented by the classes that will interact with the datasource
 * (either a backing database, or a cache) to provide the 
 * <code>RoDProduct</code> data utilized by the web tier.
 *  
 * @author L. Craig Carpenter
 */
public interface RoDProductServiceI {

	/**
	 * Retrieve a list of primary keys from the target data source.
	 * 
	 * @return The list of primary keys.  
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
	public List<String> getKeys() throws ServiceUnavailableException;
	
	/**
	 * Retrieve a single <code>RoDProduct</code> based on the input key.
	 * @param key The primary key for a specific <code>RoDProduct</code> 
	 * object.
	 * @return Single <code>RoDProduct</code> matching the input primary 
	 * key.  Null if the key is not supplied by the caller, or the key is 
	 * not found in the database.
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
	public RoDProduct getProduct(String key) throws ServiceUnavailableException;
	
	/**
	 * Simple method to retrieve all products in the target data source.
	 * @return The list of all <code>RoDProduct</code> objects in the target 
	 * data source.
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
	public List<RoDProduct> getProducts() throws ServiceUnavailableException;
	
}
