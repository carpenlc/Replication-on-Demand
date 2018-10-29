package mil.nga.rod.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.interfaces.RoDProductServiceI;
import mil.nga.rod.model.RoDProduct;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * EJB implementing the logic to retrieve the <code>RoDProduct</code> data 
 * utilized by the web tier.  The basic logic is that the code will attempt 
 * to first retrieve the data from the local cache which is substantially 
 * faster than reaching back to the backing data store.  If the data cannot 
 * be retrieved from the cache, we then go back to data store.  
 * 
 * The local cache is managed by a separate process.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class RoDProductService implements RoDProductServiceI, Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 2512499892917425300L;

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductService.class);
    
	/**
	 * Container-injected handle to EJB responsible for interacting with the 
	 * backing data source.
	 */
	@EJB
	RoDProductServiceJPA   datasource;
	
	/**
	 * Container-injected handle to the EJB responsible for interacting with 
	 * the local cache.
	 */
	@EJB
	RoDProductServiceCache cache;
	
	/**
	 * Default constructor.
	 */
	public RoDProductService() {}
	
	/**
	 * Retrieve a list of primary keys from the target persistence unit.
	 * 
	 * @return The list of primary keys.  
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	@Override
	public List<String> getKeys() throws ServiceUnavailableException {
		
		long         start = System.currentTimeMillis();
		List<String> keys  = new ArrayList<String>();

		if (cache != null) {
			try {
				keys = cache.getKeys();
			}
			catch (JedisConnectionException jce) {
				LOG.warn("Unable to connect to the target cache.  Trying "
						+ "to retrieve keys from the datasource.");
			}
		}
		else {
			LOG.error("Container failed to inject the reference to the "
					+ "RoDProductServiceCache EJB.  Unable to retrieve data "
					+ "from the cache.");
		}
		
		if ((keys == null) || (keys.size() == 0)) { 
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unable to retrieve key set from the cache.  The "
						+ "key set is null or empty.  Trying the backing "
						+ "data source.");
			}
			if (datasource != null) {
				try {
					keys = datasource.getKeys();
				}
				catch (NoResultException nre) {
					LOG.error("The backing datasource is empty.");
				}
			}
			else {
				LOG.error("Container failed to inject the reference to the "
						+ "RoDProductServiceJPA EJB.  Unable to retrieve data "
						+ "from the cache.");
			}
		}

		if (LOG.isDebugEnabled()) {
        	LOG.debug("[ "
        			+ keys.size()
        			+ " ] RoDProducts retrieved in [ "
        			+ (System.currentTimeMillis() - start)
        			+ " ] ms.");
        }
		return keys;
	}
	
	/**
	 * Retrieve a single <code>RoDProduct</code> based on the input key.
	 * @param key The primary key for a specific <code>RoDProduct</code> 
	 * object.
	 * @return Single <code>RoDProduct</code> matching the input primary 
	 * key.  Null if the key is not supplied by the caller, or the key is 
	 * not found in the database.
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	@Override
	public RoDProduct getProduct(String key) 
			throws ServiceUnavailableException {
		
		long       start   = System.currentTimeMillis();
		RoDProduct product = null;
		
		if ((key != null) && (!key.isEmpty())) {
			
			if (cache != null) {
				try {
					product = cache.getProduct(key);
				}
				catch (JedisConnectionException jce) {
					LOG.warn("Unable to connect to the target cache.  Trying "
							+ "to retrieve product from the datasource.");
				}
			}
			else {
				LOG.error("Container failed to inject the reference to the "
						+ "RoDProductServiceCache EJB.  Unable to retrieve data "
						+ "from the cache.");
			}
			if (product == null) { 
				if (LOG.isDebugEnabled()) {
					LOG.debug("Unable to retrieve RoDProduct with key [ "
							+ key
							+ " ] from the cache.  Trying the backing "
							+ "data source.");
				}
				if (datasource != null) {
					try {
						product = cache.getProduct(key);
					}
					catch (NoResultException nre) {
						LOG.error("The backing datasource is empty.");
					}
				}
				else {
					LOG.error("Container failed to inject the reference to the "
							+ "RoDProductServiceJPA EJB.  Unable to retrieve data "
							+ "from the cache.");
				}
			}
			
			if (LOG.isDebugEnabled()) {
            	LOG.debug("RoDProduct with key [ "
            			+ key
            			+ " ] retrieved in [ "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
		}
		else {
			LOG.error("Input key value is null or not populated.  Query will "
					+ "not be executed.");
		}
		return product;
	}
	
	/**
	 * Simple method to retrieve all products in the target persistence unit.
	 * @return The list of all <code>RoDProduct</code> objects in the target 
	 * persistence unit.
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	@Override
	public List<RoDProduct> getProducts() 
			throws ServiceUnavailableException {
		
		long start = System.currentTimeMillis();
		List<RoDProduct> products = new ArrayList<RoDProduct>();
		
		if (cache != null) {
			try {
				products = cache.getProducts();
			}
			catch (JedisConnectionException jce) {
				LOG.warn("Unable to connect to the target cache.  Trying "
						+ "to retrieve products from the datasource.");
			}
		}
		else {
			LOG.error("Container failed to inject the reference to the "
					+ "RoDProductServiceCache EJB.  Unable to retrieve data "
					+ "from the cache.");
		}
		if ((products == null) || (products.size() == 0)) { 
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unable to retrieve list of RoDProducts from the "
						+ "cache.  Trying the backing "
						+ "data source.");
			}
			if (datasource != null) {
				try {
					products = cache.getProducts();
				}
				catch (NoResultException nre) {
					LOG.error("The backing datasource is empty.");
				}
			}
			else {
				LOG.error("Container failed to inject the reference to the "
						+ "RoDProductServiceJPA EJB.  Unable to retrieve data "
						+ "from the cache.");
			}
		}
		
		if (LOG.isDebugEnabled()) {
        	LOG.debug("[ "
        			+ products.size()
        			+ " ] RoDProducts retrieved in [ "
        			+ (System.currentTimeMillis() - start)
        			+ " ] ms.");
        }

		return products;
	}
	
}
