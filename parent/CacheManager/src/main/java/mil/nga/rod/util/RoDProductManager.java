package mil.nga.rod.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.util.FileUtils;
import mil.nga.rod.cache.AcceleratorRecordFactory;
import mil.nga.rod.jdbc.AcceleratorJDBCRecordFactory;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.util.ProductUtils;

/**
 * Class introduced to manage the "Accelerator" records resident in the 
 * back-end data store.  There is one accelerator record for each unique 
 * NRN/NSN combination.  The accelerator record contains a calculated 
 * hash code and the actual on-disk file size.  These records are used 
 * by clients to determine whether or not their local holdings are the 
 * same as the source holdings.  
 * 
 * In order to simplify processing the management of the backing data 
 * store was segregated from the management of the local cache.  
 * 
 * @author L. Craig Carpenter
 */
public class RoDProductManager {
	
    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		RoDProductManager.class);
    
    /**
     * See if the on-disk file changed in size since the last time the cache 
     * was updated.  
     * 
     * @param value The cached data.
     * @return True if the on-disk data has changed since the last update.
     * @throws IOException Thrown if there are issues accessing the on-disk 
     * file.
     */
    public boolean isUpdateRequired(QueryRequestAccelerator record) throws IOException {
        
        boolean needsUpdate = false;
        
        if ((record != null) && 
        		(record.getPath() != null) && 
        		(!record.getPath().isEmpty())) {          
            long size = FileUtils.getActualFileSize(Paths.get(record.getPath()));
            if (size != record.getSize()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("File [ "
                            + record.getPath()
                            + " ] has changed.  QueryRequestAccelerator "
                            + "record will be updated.");
                }
                needsUpdate = true;
            }
        }
        return needsUpdate;
    }
	/**
	 * Remove orphaned accelerator records.  These are accelerator records 
	 * that do not have an associated Product record.
	 * 
	 * @param products List of unique products.
	 * @param accelerators List of unique query accelerator records.
	 */
	public void removeObsoleteAcceleratorRecords(
			List<String> products, 
			List<String> accelerators) {
	
		long start      = System.currentTimeMillis();
		int  count      = 0;
		int  errorCount = 0;
		List<String> acceleratorsToRemove = ProductUtils.getInstance().defference(
				accelerators,
				products);
		
		if ((acceleratorsToRemove != null) && (acceleratorsToRemove.size() > 0)) {
			LOGGER.info("Removing [ "
					+ acceleratorsToRemove.size()
					+ " ] obsolete QueryRequestAccelerator records.");
			for (String key : acceleratorsToRemove) {
				try {
					AcceleratorJDBCRecordFactory.getInstance().remove(
							ProductUtils.getInstance().getNRNFromKey(key), 
							ProductUtils.getInstance().getNSNFromKey(key));
					count += 1;
				}
				catch (PropertiesNotLoadedException pnle) {
					LOGGER.error("Unexpected PropertiesNotLoadedException "
							+ "encountered.  Please ensure that the "
							+ "application is properly configured.  Unable "
							+ "to remove orphaned records.  Error message => [ "
							+ pnle.getMessage()
							+ " ].");
					errorCount += 1;
				}
				catch (PropertyNotFoundException pnfe) {
					LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
							+ "Please ensure that the application is properly "
							+ "configured.  Unable to remove orphaned records.  "
							+ "Error message => [ "
							+ pnfe.getMessage()
							+ " ].");
					errorCount += 1;
				}
				catch (ClassNotFoundException cnfe) {
					LOGGER.error("Unexpected ClassNotFoundException "
							+ "encountered.  Please ensure that the JDBC "
							+ "drivers are available.  Unable to remove "
							+ "orphaned records.  Error message => [ "
							+ cnfe.getMessage()
							+ " ].");
					errorCount += 1;
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("A total of [ "
						+ count 
						+ " ] records removed with [ "
						+ errorCount 
						+ " ] errors encountered in [ "
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
	 * 
	 * @param products List of unique products.
	 * @param accelerators List of unique query accelerator records.
	 */
	public void addNewAcceleratorRecords(
			List<String> products, 
			List<String> accelerators) {
		
		long start      = System.currentTimeMillis();
		int  count      = 0;
		int  errorCount = 0;
		List<String> acceleratorsToAdd = ProductUtils.getInstance().defference(
				products,
				accelerators);
		
		if ((acceleratorsToAdd != null) && (acceleratorsToAdd.size() > 0)) {
			LOGGER.info("Adding [ "
					+ acceleratorsToAdd.size()
					+ " ] new QueryRequestAccelerator records.");
			for (String key : acceleratorsToAdd) {
				try {
					List<Product> prods = ProductFactory.getInstance().getProducts(key);
					if ((prods != null) && (prods.size() > 0)) {
						AcceleratorJDBCRecordFactory.getInstance().insert(
								AcceleratorRecordFactory.getInstance().buildRecord(prods.get(0)));
					}
					else {
						LOGGER.warn("No product found with NRN => [ "
								+ ProductUtils.getInstance().getNRNFromKey(key)
								+ " ] and NSN => [ "
								+ ProductUtils.getInstance().getNSNFromKey(key)
								+ " ].");
								
					}
				}
				catch (IOException ioe) {
					LOGGER.error("Unexpected IOException "
							+ "encountered.  Unable "
							+ "to add new records.  Error message => [ "
							+ ioe.getMessage()
							+ " ].");
					errorCount += 1;
				}
				catch (PropertiesNotLoadedException pnle) {
					LOGGER.error("Unexpected PropertiesNotLoadedException "
							+ "encountered.  Please ensure that the "
							+ "application is properly configured.  Unable "
							+ "to add new records.  Error message => [ "
							+ pnle.getMessage()
							+ " ].");
					errorCount += 1;
				}
				catch (PropertyNotFoundException pnfe) {
					LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
							+ "Please ensure that the application is properly "
							+ "configured.  Unable to add new records.  "
							+ "Error message => [ "
							+ pnfe.getMessage()
							+ " ].");
					errorCount += 1;
				}
				catch (ClassNotFoundException cnfe) {
					LOGGER.error("Unexpected ClassNotFoundException "
							+ "encountered.  Please ensure that the JDBC "
							+ "drivers are available.  Unable to  "
							+ "add new records.  Error message => [ "
							+ cnfe.getMessage()
							+ " ].");
					errorCount += 1;
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("A total of [ "
						+ count 
						+ " ] records added with [ "
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
	 * 
	 * @param products List of unique products.
	 * @param accelerators List of unique query accelerator records.
	 */
	public void updateAcceleratorRecords(
			List<String> products, 
			List<String> accelerators) {
	
		long start       = System.currentTimeMillis();
		int  count       = 0;
		int  errorCount  = 0;
		int  updatedRecs = 0;
		List<String> acceleratorsToUpdate = ProductUtils.getInstance().intersection(
				products,
				accelerators);
		
		if ((acceleratorsToUpdate != null) && (acceleratorsToUpdate.size() > 0)) {
			LOGGER.info("Updating [ "
					+ acceleratorsToUpdate.size()
					+ " ] new QueryRequestAccelerator records.");
			for (String key : acceleratorsToUpdate) {
				count++;
				try {
					QueryRequestAccelerator record = 
							AcceleratorJDBCRecordFactory.getInstance().getRecord(key);
					if (record != null) {
						if (isUpdateRequired(record)) {
							QueryRequestAccelerator newRecord = AcceleratorRecordFactory
                    				.getInstance()
                    				.buildRecord(record.getProduct());
							if (newRecord != null) {
								AcceleratorJDBCRecordFactory.getInstance().update(newRecord);
								updatedRecs++;
							}
							else {
								errorCount++;
							}
						}
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
	}
	
	/**
	 * 
	 * @return
	 */
	public void update() {
		
		long start = System.currentTimeMillis();
		
		try {
			
			List<String> uniqueProducts = ProductFactory.getInstance()
						.getUniqueKeys();
			
			// Get the map containing a unique list of accelerator records
			//List<String> uniqueRecords = RoDProductRecordFactory.getInstance()
			//			.getUniqueKeys();
			
			//removeObsoleteAcceleratorRecords(uniqueProducts, uniqueRecords);
			//addNewAcceleratorRecords(uniqueProducts, uniqueRecords);
			//updateAcceleratorRecords(uniqueProducts, uniqueRecords);
			
		}
		catch (PropertiesNotLoadedException pnle) {
			
		}
		catch (PropertyNotFoundException pnfe) {
			
		}
		catch (ClassNotFoundException cnfe) {
			
		}
		LOGGER.info("Update completed in [ "
				+ (System.currentTimeMillis() - start)
				+ " ] ms.");
	}
	
	/**
	 * Main entry point for the execution of the code that will update the 
	 * back-end data source.
	 * @param args
	 */
	public static void main(String[] args) {
		new RoDProductManager().update();
	}
}
