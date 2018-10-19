package mil.nga.rod.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.rod.JSONSerializer;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.types.HashType;
import mil.nga.util.FileUtils;
import mil.nga.util.HashGenerator;

/**
 * Class containing the logic required to generate the key/value pair for the
 * query accelerator records.  
 * 
 * @author L. Craig Carpenter
 */
public class AcceleratorRecordFactory {

    /**
     * Set up the Log4j system for use throughout the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            AcceleratorRecordFactory.class);
    
    /**
     * The hash type to calculate.
     */
    public static final HashType HASH_TYPE = HashType.MD5;

    /**
     * Default constructor enforcing the singleton design pattern.
     */
    private AcceleratorRecordFactory () {}
    
    /**
     * Accessor method for the singleton instance of the 
     * AcceleratorRecordFactory class.
     * 
     * @return The singleton instance of the AcceleratorRecordFactory.
     * class.
     */
    public static AcceleratorRecordFactory getInstance() {
        return AcceleratorRecordFactoryHolder.getSingleton();
    } 
  
    
    /**
     * Serialize the input QueryRequestAccelerator record into a JSON String 
     * that will be cached.
     * 
     * @param record The QueryRequestAccelerator record to be cached.
     * @return The serialized version of the QueryRequestAccelerator record.
     */
    public String getValue(QueryRequestAccelerator record) {
        String value = null;
        if (record != null) {
            value = JSONSerializer.getInstance().serialize(record);
        }
        return value;
    }
    
    
    /**
     * Generate a <code>QueryRequestAccelerator</code> record for storage 
     * in the target cache.
     * 
     * @param prod The database record identifying an on-disk ISO file.
     * @return A QueryRequestAccelerator record to add to the cache.
     * @throws IOException Thrown if there are problems accessing the target 
     * file.
     */
    public QueryRequestAccelerator buildRecord(Product prod) 
            throws IOException {
        
        QueryRequestAccelerator record    = null;
        HashGenerator           generator = new HashGenerator();
        
        if (prod != null) {
            String path = prod.getPath();
            if ((path != null) && (!path.isEmpty())) {
                try {
                    Path p = Paths.get(path);
                    if (Files.exists(p)) {
                        String hash = generator.getHash(p, HASH_TYPE);
                        if (hash != null) {
                            record = new QueryRequestAccelerator
                                    .QueryRequestAcceleratorBuilder()
                                    	.product(prod)
                                        .fileDate(FileUtils.getActualFileDate(p))
                                        .hash(hash)
                                        .size(FileUtils.getActualFileSize(p))
                                        .build();
                        }
                        else {
                            LOGGER.error("Unable to generate a hash for file [ "
                                    + path
                                    + " ].  See previous error messages for more "
                                    + "information.");
                        }
                    }
                    else {
                    LOGGER.error("Target file [ "
                            + path
                            + " ] does not exist.  Unable to generate an "
                            + "accelerator record.");
                
                    }
                }
                catch (IOException ioe) {
                    LOGGER.error("An unexpected IOException was raised while "
                            + "attempting to access file [ "
                            + path
                            + " ].  Exception message [ "
                            + ioe.getMessage()
                            + " ].  Accelerator record not created.");
                }
            }
            else {
                LOGGER.error("Target file name is null or empty.  Unable to "
                        + "generate an accelerator record.");
            }
        }
        else {
            LOGGER.error("The input product object is null.  Nothing to "
                    + "store.");
        }
        return record;
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class AcceleratorRecordFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the AcceleratorRecordFactory.
         */
        private static AcceleratorRecordFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * AcceleratorRecordFactory.
         * 
         * @return The Singleton instance of the AcceleratorRecordFactory.
         */
        public static AcceleratorRecordFactory getSingleton() {
            if (_instance == null) {
                _instance = new AcceleratorRecordFactory();
            }
            return _instance;
        }
        
    }
}
