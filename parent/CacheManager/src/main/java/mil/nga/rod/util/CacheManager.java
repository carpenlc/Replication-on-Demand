package mil.nga.rod.util;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.JSONSerializer;
import mil.nga.rod.accelerator.AcceleratorRecordFactory;
import mil.nga.rod.accelerator.RedisCacheManager;
import mil.nga.rod.jdbc.AcceleratorJDBCRecordFactory;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.jdbc.RoDProductFactory;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
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
	 */
	public void removeObsoleteCacheRecords(
			List<String> datastore, 
			List<String> cache) {
	
		long start = System.currentTimeMillis();
		int  count = 0;

		List<String> cacheRecordsToRemove = ProductUtils.getInstance().defference(
				cache,
				datastore);
		
		if ((cacheRecordsToRemove != null) && (cacheRecordsToRemove.size() > 0)) {
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
			LOGGER.info("No obsolete QueryRequestAccelerator records "
					+ "encountered.");
		}
	}
    
	/**
	 * Add new records to the cache. 
	 * @param products List of unique products.
	 * @param accelerators List of unique query accelerator records.
	 */
	public void addNewCacheRecords(
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
					List<Product> prods = ProductFactory.getInstance().getProducts(key);
					if ((prods != null) && (prods.size() > 0)) {
						
						QueryRequestAccelerator accel = 
								AcceleratorRecordFactory
									.getInstance()
									.buildRecord(
										prods.get(0));
						
						if (accel != null) {
							
							Optional<RoDProduct> rodProduct = 
									RoDProductFactory.getInstance()
										.getRoDProductOneStep(
											Optional.of(accel));
							
							if (rodProduct.isPresent()) {
								RedisCacheManager.getInstance().put(
										key,
										JSONSerializer.getInstance().serialize(
												rodProduct.get()));
							}
							
						}
						else {
							LOGGER.warn("Unable to build the "
									+ "QueryRequestAccelerator record for "
									+ "key [ "
									+ key 
									+ " ].");
						}
						

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
				catch (IOException ioe) {
					LOGGER.error("Unexpected IOException "
							+ "encountered.  Unable "
							+ "to add new records.  Error message => [ "
							+ ioe.getMessage()
							+ " ].");
					errorCount++;
				}
				catch (PropertiesNotLoadedException pnle) {
					LOGGER.error("Unexpected PropertiesNotLoadedException "
							+ "encountered.  Please ensure that the "
							+ "application is properly configured.  Unable "
							+ "to add new records.  Error message => [ "
							+ pnle.getMessage()
							+ " ].");
					errorCount++;
				}
				catch (PropertyNotFoundException pnfe) {
					LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
							+ "Please ensure that the application is properly "
							+ "configured.  Unable to add new records.  "
							+ "Error message => [ "
							+ pnfe.getMessage()
							+ " ].");
					errorCount++;
				}
				catch (ClassNotFoundException cnfe) {
					LOGGER.error("Unexpected ClassNotFoundException "
							+ "encountered.  Please ensure that the JDBC "
							+ "drivers are available.  Unable to  "
							+ "add new records.  Error message => [ "
							+ cnfe.getMessage()
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
			LOGGER.info("No QueryRequestAccelerator records to add.");
		}
	}
	
	/**
	 * Update existing cache records. 
	 * @param datastore List of unique products in the data store.
	 * @param cache List of cache records.
	 */
	public void updateCacheRecords(
			List<String> datastore, 
			List<String> cache) {
	
		long start       = System.currentTimeMillis();
		int  count       = 0;
		int  errorCount  = 0;
		int  updatedRecs = 0;
		
		List<String> cacheRecordsToUpdate = ProductUtils.getInstance().intersection(
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
						Optional<RoDProduct> productDS  = 
								RoDProductFactory.getInstance()
									.getRoDProduct(key);
						if (productDS.isPresent()) {
							if (!productDS.get()
									.getHash()
									.equalsIgnoreCase(product.getHash())) {
								// Simply replace the existing cache record
								// with a serialized version of the current 
								// record from the datastore.
								RedisCacheManager.getInstance().put(
										key,
										JSONSerializer.getInstance().serialize(
												productDS.get()));
								updatedRecs++;
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
			}
				
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
	}
	
    /**
     * 
     */
    public void update() {
    	try {

			List<String> datastoreRecords = 
					AcceleratorJDBCRecordFactory.getInstance()
						.getUniqueKeys();
			Set<String> cacheRecordSet = 
					RedisCacheManager.getInstance()
					.getKeys();
			List<String> cacheRecords = 
					cacheRecordSet.stream().collect(Collectors.toList());
			removeObsoleteCacheRecords(datastoreRecords, cacheRecords);
			addNewCacheRecords(datastoreRecords, cacheRecords);
    	}
		catch (PropertiesNotLoadedException pnle) {
			LOGGER.error("Unexpected PropertiesNotLoadedException "
					+ "encountered.  Please ensure that the "
					+ "application is properly configured.  Unable "
					+ "to add new records.  Error message => [ "
					+ pnle.getMessage()
					+ " ].");
		}
		catch (PropertyNotFoundException pnfe) {
			LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
					+ "Please ensure that the application is properly "
					+ "configured.  Unable to add new records.  "
					+ "Error message => [ "
					+ pnfe.getMessage()
					+ " ].");
		}
		catch (ClassNotFoundException cnfe) {
			LOGGER.error("Unexpected ClassNotFoundException "
					+ "encountered.  Please ensure that the JDBC "
					+ "drivers are available.  Unable to  "
					+ "add new records.  Error message => [ "
					+ cnfe.getMessage()
					+ " ].");
		}
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
