package mil.nga.rod.accelerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.JSONSerializer;
import mil.nga.rod.jdbc.AcceleratorJDBCRecordFactory;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.util.ProductUtils;
import mil.nga.util.FileUtils;
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
     * See if the on-disk file changed in size since the last time the cache 
     * was updated.  
     * 
     * @param value The cached data.
     * @return True if the on-disk data has changed since the last update.
     * @throws IOException Thrown if there are issues accessing the on-disk 
     * file.
     */
    public boolean isUpdateRequired(String value) throws IOException {
        
        boolean needsUpdate = false;
        
        if ((value != null) && (!value.isEmpty())) {
            
            QueryRequestAccelerator record = JSONSerializer
                    .getInstance()
                    .deserializeToQueryRequestAccelerator(value);
            long size = FileUtils.getActualFileSize(Paths.get(record.getPath()));
            
            if (size != record.getSize()) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("File [ "
                            + record.getPath()
                            + " ] has changed.  Cache record will be updated.");
                }
                
                needsUpdate = true;
            }
        }
        return needsUpdate;
    }
    
    /**
     * See if the on-disk file changed in size since the last time the cache 
     * was updated.  
     * 
     * @param value The existing data.
     * @return True if the on-disk data has changed since the last update.
     * @throws IOException Thrown if there are issues accessing the on-disk 
     * file.
     */
    public boolean isUpdateRequired(QueryRequestAccelerator product) 
    		throws IOException {
    	
        boolean needsUpdate = false;
        
        if (product != null) {
            
            long size = FileUtils.getActualFileSize(
            		Paths.get(product.getPath()));
            
            if (size != product.getSize()) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("File [ "
                            + product.getPath()
                            + " ] has changed.  Cache record will be updated.");
                }
                
                needsUpdate = true;
            }
        }
        return needsUpdate;
    }
    
    /**
     * Get a list of all products in the backing data store.
     * 
     * @return The list of all products in the backing data store.
     */
    public List<Product> getAllProducts() {

        List<Product> products = null;
        
        try (ProductFactory factory = ProductFactory.getInstance()) {
            products = factory.getUniqueProducts();
        }
        catch (PropertyNotFoundException pnfe) {
            LOGGER.error("PropertyNotFoundException raised "
                    + "while attempting to establish a connection to the "
                    + "back end data store.  Please ensure the required "
                    + "properties are available.  Property-specific error "
                    + "message => [ "
                    + pnfe.getMessage()
                    + " ].");
        }
        catch (PropertiesNotLoadedException pnle) {
            LOGGER.error("Unexpected PropertiesNotLoadedException raised "
                    + "while attempting to establish a connection to the "
                    + "back end data store.  Error message [ "
                    + pnle.getMessage()
                    + " ].  Please ensure the system properties file "
                    + "is available.");
        }
        catch (ClassNotFoundException cnfe) {
            LOGGER.error("Unexpected ClassNotFoundException raised while "
                    + "attempting to establish a connection to the back "
                    + "end data store.  Error message [ "
                    + cnfe.getMessage()
                    + " ].  Please ensure the data store JDBC driver "
                    + "library is on the class path.");
        }
        catch (JedisConnectionException jce) {
            LOGGER.error("Unexpected JedisConnectionException raised while "
                    + "attempting to establish a connection to the Redis "
                    + "cache.  Error message [ "
                    + jce.getMessage()
                    + " ].  Please ensure that the cache is available "
                    + "or disable the caching feature.");
        }
        
        return products;
    }
    
    /**
     * Main method containing the logic required to update the accelerator cache.
     */
    public void updateAcceleratorCache() {
    
        long start          = System.currentTimeMillis();
        int  successCounter = 0;
        int  failedCounter  = 0;
        int  totalCounter   = 0;
        
        LOGGER.info("Cache update started at [ "
                + dateFormatter.format(new Date(System.currentTimeMillis()))
                + " ].");
            
        List<Product> records = getAllProducts();
        
        if ((records != null) && (records.size() > 0)) {
            
            try (RedisCacheManager cacheManager = RedisCacheManager.getInstance()) {
                for (Product record : records) {
                    
                    totalCounter++;
                    try {
                        
                        String key = ProductUtils.getInstance().getKey(record);
                        QueryRequestAccelerator value = 
                        		JSONSerializer
                        			.getInstance()
                        			.deserializeToQueryRequestAccelerator(
                        					RedisCacheManager.getInstance().get(key));
                        
                        //Not in cache? 
                        if (value == null) {
                        	// Check the database
                        	value = AcceleratorJDBCRecordFactory.getInstance().getRecord(record);
                        	// Not in database?
                        	if (value == null) {
                        		// Generate the record.
                        		value = AcceleratorRecordFactory
                        				.getInstance()
                        				.buildRecord(record);
                        		if (value != null) {
	                        		RedisCacheManager.getInstance().put(
	                        				key, 
	                        				AcceleratorRecordFactory.getInstance().getValue(value));
	                        		AcceleratorJDBCRecordFactory.getInstance().insert(value);
	                        		successCounter++;
                        		}
                        		else {
                                    failedCounter++;
                                }
                        	}
                        	// It's in the database.  Is an update necessary?
                        	else if (isUpdateRequired(value)) {
                        		value = AcceleratorRecordFactory
                        				.getInstance()
                        				.buildRecord(record);
                        		if (value != null) {
	                        		RedisCacheManager.getInstance().put(
	                        				key, 
	                        				AcceleratorRecordFactory.getInstance().getValue(value));
	                        		AcceleratorJDBCRecordFactory.getInstance().update(value);
	                        		successCounter++;
                        		}
                        		else {
                                    failedCounter++;
                                }
                        	}
                        	else {
                        		// Ensure it's put back in the cache.
                        		RedisCacheManager.getInstance().put(
                        				key, 
                        				AcceleratorRecordFactory.getInstance().getValue(value));
                        	}
                        }
                        // It's in the cache.  Is an update necessary?
                        else if (isUpdateRequired(value)) {
                    		value = AcceleratorRecordFactory
                    				.getInstance()
                    				.buildRecord(record);
                    		if (value != null) {
                        		RedisCacheManager.getInstance().put(
                        				key, 
                        				AcceleratorRecordFactory.getInstance().getValue(value));
                        		AcceleratorJDBCRecordFactory.getInstance().update(value);
                        		successCounter++;
                    		}
                    		else {
                                failedCounter++;
                            }
                    	}
                    }
                    catch (ClassNotFoundException cnfe) {
                    	failedCounter++;
                    	LOGGER.error("Configuration error encountered.  "
                    			+ "Database unavailable.  Unexpected "
                    			+ "ClassNotFoundException raised.  "
                    			+ "Error message => [ "
                    			+ cnfe.getMessage()
                    			+ " ].");
                    }
                    catch (PropertiesNotLoadedException pnle) {
                    	failedCounter++;
                    	LOGGER.error("Configuration error encountered.  "
                    			+ "Database unavailable.  Unexpected "
                    			+ "PropertiesNotLoadedException raised.  "
                    			+ "Error message => [ "
                    			+ pnle.getMessage()
                    			+ " ].");
                    }
                    catch (PropertyNotFoundException pnfe) {
                    	failedCounter++;
                    	LOGGER.error("Configuration error encountered.  "
                    			+ "Database unavailable.  Unexpected "
                    			+ "PropertyNotFoundException raised.  "
                    			+ "Error message => [ "
                    			+ pnfe.getMessage()
                    			+ " ].");
                    }
                    catch (IOException ioe) {
                        failedCounter++;
                        LOGGER.error("Unexpected IOException raised while "
                                + "attempting to access on-disk file [ "
                                + record.getPath()
                                + " ].  Error message [ "
                                + ioe.getMessage()
                                + " ].  Cache record not updated.");
                    }
                } // end for
            } // end try-with-resources
            try {
            	AcceleratorJDBCRecordFactory.getInstance().removeDuplicates();
            }
            catch (Exception e) {}
        } 
        else {
            LOGGER.error("Data store unavailable.  (Query did not return "
                    + "any records).");
        }       
        
        LOGGER.info("Cache update completed at [ "
                + dateFormatter.format(new Date(System.currentTimeMillis()))
                + " ] in [ "
                + (System.currentTimeMillis() - start)
                + " ] ms.");
        LOGGER.info("Processed [ "
                + totalCounter
                + " ] records.  [ "
                + successCounter 
                + " ] were successfully updated, [ "
                + failedCounter
                + " ] records failed to update.");
    }
    
    /**
     * Main method invoked to start the Replication-on-Demand cache management
     * application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            (new CacheManager()).updateAcceleratorCache();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
