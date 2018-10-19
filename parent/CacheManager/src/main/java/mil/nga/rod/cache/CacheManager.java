package mil.nga.rod.cache;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.cache.RedisCacheManager;
import mil.nga.rod.JSONSerializer;
import mil.nga.rod.jdbc.RoDProductRecordFactory;
import mil.nga.rod.model.RoDProduct;
import mil.nga.rod.util.ProductUtils;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Class containing the logic required to load a local Redis cache with data
 * used to increase the performance of product queries.  
 * 
 * @author L. Craig Carpenter
 */
public class CacheManager {

    /**
     * Set up the Log4j system for use throughout the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            CacheManager.class);
    
    /** 
     * Expected format associated with dates coming in from callers.
     */
    private static final String INPUT_DATE_FORMAT_STRING = 
            "yyyy-MM-dd hh:mm:ss";
    
    /**
     * Date formatter objecf for printing output information.
     */
    private static final DateFormat dateFormatter = 
            new SimpleDateFormat(INPUT_DATE_FORMAT_STRING);

    /**
     * Default constructor.
     */
    public CacheManager() { }
    

    /**
     * Remove the records from the cache that no longer exist in the target 
     * datasource.
     * 
     * @param cache List of keys that currently exist in the cache.
     * @param datasource List of keys that currently exist in the datasource.
     */
    private void removeObsoleteRecords(
    		List<String> cache, 
    		List<String> datasource) {
    	
		long start = System.currentTimeMillis();
		int  count = 0;

		List<String> cacheRecordsToRemove = ProductUtils.getInstance().defference(
				cache,
				datasource);
		
		if ((cacheRecordsToRemove != null) && (!cacheRecordsToRemove.isEmpty())) {
			LOGGER.info("Removing [ "
					+ cacheRecordsToRemove.size()
					+ " ] obsolete cache records.");
			for (String key : cacheRecordsToRemove) {
				RedisCacheManager.getInstance().remove(key);
				count++;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("A total of [ "
						+ count 
						+ " ] records removed in [ "
						+ (System.currentTimeMillis() - start)
						+ " ] ms.");
			}
		}
		else {
			LOGGER.info("No obsolete cache records encountered.");
		}
    	
    }

    /**
     * The cache is a slave to contents of the backing data store.  We need to
     * remove any records in the cache that no longer exist in the backing data
     * store, then simply over-write everything else.  This logic simplifies 
     * the logic required keep the cache up-to-date.
     */
    public void updateCache() {
    	
    	long start = System.currentTimeMillis();
    	
    	try (RoDProductRecordFactory datasource = 
    			RoDProductRecordFactory.getInstance();
    		RedisCacheManager cache = 
    				RedisCacheManager.getInstance()) {
    		
    		List<String> datasourceKeys = datasource.getKeys();
    		List<String> cacheKeys      = cache.getKeysAsList();     
    		removeObsoleteRecords(cacheKeys, datasourceKeys);
    		
    		List<RoDProduct> products = datasource.getProducts();
    		if ((products != null) && (!products.isEmpty())) { 
    			for (RoDProduct p : products) {
    				cache.put(
    						p.getKey(), 
    						JSONSerializer.getInstance().serialize(p));
    			}
    		}
    	}
    	catch (JedisConnectionException jce) {
    		LOGGER.error("Unable to connect to the local cache.  "
    				+ "JedisConnectionException message => [ "
    				+ jce.getMessage()
    				+ " ].  Cache will not be updated.");
    	}
        LOGGER.info("Cache update completed at [ "
                + dateFormatter.format(new Date(System.currentTimeMillis()))
                + " ] in [ "
                + (System.currentTimeMillis() - start)
                + " ] ms.");
    }
    
    /**
     * Main method invoked to start the Replication-on-Demand cache management
     * application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            (new CacheManager()).updateCache();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
