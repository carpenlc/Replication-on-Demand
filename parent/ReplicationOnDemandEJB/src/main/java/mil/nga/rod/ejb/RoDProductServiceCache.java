package mil.nga.rod.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.JSONSerializer;
import mil.nga.cache.RedisCacheManager;
import mil.nga.rod.interfaces.RoDProductServiceI;
import mil.nga.rod.model.RoDProduct;

/**
 * This version of the RoDProductService attempts to retrieve the relevant 
 * <code>RoDProduct</code> data from the target Redis cache.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class RoDProductServiceCache implements RoDProductServiceI, Serializable {
	
	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -6691351805313482929L;

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductServiceCache.class);
    
	/**
	 * Retrieve a list of primary keys from the target data source.
	 * 
	 * @return The list of primary keys.  
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
    @Override
	public List<String> getKeys() throws ServiceUnavailableException {
		
		long         start      = System.currentTimeMillis();
		List<String> keys       = null;
		Set<String>  productSet = RedisCacheManager.getInstance().getKeys();
		
		if ((productSet != null) && (productSet.size() > 0)) {
			keys = new ArrayList<String>(productSet);
		}
		else {
			LOG.warn("The key set retrieved from the cache is empty.");
			keys = new ArrayList<String>();
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Key set retrieved from cache in [ "
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
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
	@Override
	public RoDProduct getProduct(String key) throws ServiceUnavailableException {
		
		long       start   = System.currentTimeMillis();
		RoDProduct product = null;
		
		if ((key != null) && (!key.isEmpty())) {
			String jsonValue = RedisCacheManager.getInstance().get(key);
			if ((jsonValue != null) && (!jsonValue.isEmpty())) {
				product = JSONSerializer.getInstance().deserializeToRoDProduct(jsonValue);
			}
			else {
				LOG.warn("Unable to find RoDProduct with key [ "
						+ key
						+ " ] in the cache.");
			}
		}
		else {
			LOG.warn("Input key was null.  Returned RoDProduct will be null.");
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("RoDProduct with key [ "
					+ key 
					+ " ] retrieved from cache in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return product;
	}
	
	/**
	 * Simple method to retrieve all products in the target data source.
	 * @return The list of all <code>RoDProduct</code> objects in the target 
	 * data source.
	 * @throws ServiceUnavailableException Thrown if we cannot initialize
	 * the target service.
	 */
	@Override
	public List<RoDProduct> getProducts() throws ServiceUnavailableException {
		long             start    = System.currentTimeMillis();
		List<RoDProduct> products = new ArrayList<RoDProduct>();
		List<String>     keys     = getKeys();
		
		if ((keys != null) && (keys.size() > 0)) { 
			for (String key : keys) {
				RoDProduct p = getProduct(key);
				if (p != null) {
					products.add(p);
				}
			}
		}
		else {
			LOG.warn("The key set retrieved from the cache is empty.");
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("RoDProduct list retrieved from cache in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return products;
	}
}
