package mil.nga.rod.util;

import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.cache.RedisCacheManager;
import mil.nga.rod.JSONSerializer;
import mil.nga.rod.jdbc.RoDProductRecordFactory;
import mil.nga.rod.model.RoDProduct;
import mil.nga.rod.util.ProductUtils;

/**
 * Updated version of the cache manager that ensures the cache is synchronized
 * with the backing data store.
 * 
 * @author L. Craig Carpenter
 */
public class CacheManager {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		CacheManager.class);
    
	/**
	 * Remove orphaned cache records.  These are cache records 
	 * that do not have anything associated in the data store.
	 * 
	 * @param products List of unique products.
	 * @param cache List of unique cache records.
	 * @return The number of records removed.
	 */
	public int removeObsoleteCacheRecords(
			List<String> datastore, 
			List<String> cache) {
	
		long start = System.currentTimeMillis();
		int  count = 0;

		List<String> cacheRecordsToRemove = 
				ProductUtils.getInstance().defference(
						cache,
						datastore);
		
		if ((cacheRecordsToRemove != null) && (cacheRecordsToRemove.size() > 0)) {
			LOGGER.info("Removing [ "
					+ cacheRecordsToRemove.size()
					+ " ] obsolete cache records.");
			for (String key : cacheRecordsToRemove) {
				RedisCacheManager.getInstance().remove(key);
				LOGGER.info("Removed cached RoDProduct with key => [ "
						+ key
						+ " ].");
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
			LOGGER.info("No obsolete RoDProduct records in the cache.");
		}
		return count;
	}
    
	/**
	 * Add new records to the cache. 
	 * @param products List of unique products.
	 * @param accelerators List of unique query accelerator records.
	 * @return The number of records added.
	 */
	public int addNewCacheRecords(
			List<String> datastore, 
			List<String> cache) {
	
		long start      = System.currentTimeMillis();
		int  count      = 0;
		int  errorCount = 0;

		List<String> cacheRecordsToAdd = ProductUtils.getInstance().defference(
				datastore,
				cache);
		
		if ((cacheRecordsToAdd != null) && (cacheRecordsToAdd.size() > 0)) {
			
			LOGGER.info("Adding [ "
					+ cacheRecordsToAdd.size()
					+ " ] new cache records records.");
			
			for (String key : cacheRecordsToAdd) {
				try {
					
					RoDProduct prod = RoDProductRecordFactory.getInstance().getProduct(key);
					if (key != null) {
						RedisCacheManager.getInstance().put(
								key,
								JSONSerializer.getInstance().serialize(
										prod));
						count++;
					}
					else {
						LOGGER.warn("No product found with NRN => [ "
								+ ProductUtils.getInstance().getNRNFromKey(key)
								+ " ] and NSN => [ "
								+ ProductUtils.getInstance().getNSNFromKey(key)
								+ " ].");
						errorCount++;
					}
				}
				catch (NoResultException nre) {
					LOGGER.error("Unexpected NoResultsException "
							+ "encountered.  Unable "
							+ "to add new records.  Error message => [ "
							+ nre.getMessage()
							+ " ].");
					errorCount++;
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("A total of [ "
						+ count 
						+ " ] cache records added with [ "
						+ errorCount 
						+ " ] errors encountered in [ "
						+ (System.currentTimeMillis() - start)
						+ " ] ms.");
			}
		}
		else {
			LOGGER.info("No new RoDProduct records to add to the cache.");
		}
		return count;
	}
	
	/**
	 * Update existing cache records. 
	 * @param datastore List of unique products in the data store.
	 * @param cache List of cache records.
	 * @return The number of records that were updated.
	 */
	public int updateCacheRecords(
			List<String> datastore, 
			List<String> cache) {
	
		long start       = System.currentTimeMillis();
		int  count       = 0;
		int  errorCount  = 0;
		int  updatedRecs = 0;
		
		List<String> cacheRecordsToUpdate = 
				ProductUtils.getInstance().intersection(
						datastore,
						cache);
		
		if ((cacheRecordsToUpdate != null) && (cacheRecordsToUpdate.size() > 0)) {
			LOGGER.info("Updating [ "
					+ cacheRecordsToUpdate.size()
					+ " ] cache records.");
			for (String key : cacheRecordsToUpdate) {
				count++;
				// Get the record from the cache and de-serialize it back 
				// to an object.
				String serializedProduct = RedisCacheManager.getInstance()
													.get(key);
				if ((serializedProduct != null) && 
						(!serializedProduct.isEmpty())) {
					
					RoDProduct product = JSONSerializer.getInstance()
							.deserializeToRoDProduct(serializedProduct);
					
					if (product != null) {
						RoDProduct productDS = 
								RoDProductRecordFactory
									.getInstance()
									.getProduct(key);
						if (productDS != null) {
							if ((productDS.getHash() != null) && 
									(productDS.getHash().isEmpty())) {
								if (!productDS.getHash()
										.equalsIgnoreCase(product.getHash())) {
									// Simply replace the existing cache record
									// with a serialized version of the current 
									// record from the datastore.
									RedisCacheManager.getInstance().put(
											key,
											JSONSerializer.getInstance().serialize(
													productDS));
									updatedRecs++;
								}
							}
							else {
								LOGGER.warn("Hash for RoDProduct record with "
										+ "key => [ "
										+ productDS.getKey()
										+ " ] is null or empty.");
							}
						}
						else {
							LOGGER.error("Unable to retrieve the "
									+ "RoDProduct record from the "
									+ "datastore for key [ "
									+ key
									+  " ].");
						}
					}
					else {
						LOGGER.error("Error encountered deserializing " 
								+ "value for key [ "
								+ key
								+ " ].  Output of deserialization process "
								+ "is null.");
					}
				}
				else {
					LOGGER.warn("Unable to get value from cache for key [ "
							+ key
							+ " ].");
				}
			} //end for	
		}
		else {
			LOGGER.info("There are no overlapping records to update.");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("A total of [ "
					+ updatedRecs 
					+ " ] records updated out of [ "
					+ count 
					+ " ] candidates records with [ "
					+ errorCount 
					+ " ] errors encountered in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return updatedRecs;
	}
	
    /**
     * 
     */
    public void update() {
    	long start = System.currentTimeMillis();
		
    	try (RoDProductRecordFactory prodFactory = 
				RoDProductRecordFactory.getInstance()) {
			
			List<String> datastoreKeys = prodFactory.getKeys();
			List<String> cacheKeys     = RedisCacheManager.getInstance().getKeysAsList();
			
			int removed = removeObsoleteCacheRecords(datastoreKeys, cacheKeys);
			int added   = addNewCacheRecords(datastoreKeys, cacheKeys);
			int updated = updateCacheRecords(datastoreKeys, cacheKeys);
			
			LOGGER.info("Cache update complete.  Products added => [ "
					+ added
					+ " ], products updated => [ "
					+ updated
					+ " ], product removed => [ "
					+ removed 
					+ " ].");
		}
		
		LOGGER.info("Cache update completed in [ "
				+ (System.currentTimeMillis() - start)
				+ " ] ms.");
    	
    }
    
    
	/**
	 * Main entry point for the execution of the code that will update the 
	 * back-end data source.
	 * @param args
	 */
	public static void main(String[] args) {
		new CacheManager().update();
	}
}
