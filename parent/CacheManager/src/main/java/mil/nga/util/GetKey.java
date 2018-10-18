package mil.nga.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.rod.JSONSerializer;
import mil.nga.rod.accelerator.RedisCacheManager;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.util.Options.Multiplicity;
import mil.nga.util.Options.Separator;

/**
 * Command line tool used to dump the value associated with a key in the 
 * local redis cache.  This tool was written for debugging issues with 
 * data stored in the cache.
 * 
 * @author L. Craig Carpenter
 */
public class GetKey {
    
    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
            GetKey.class);
    
    /**
     * Usage String presented when the command line arguments do not make 
     * sense.
     */
    public static final String USAGE_STRING = 
            "Usage : java mil.nga.util.GetKey -key=<key name> "
            + "[ -deserialize ] [ -h ] [ -help ]";
    
    /**
     * Usage String presented when the command line arguments do not make 
     * sense.
     */
    public static final String HELP_STRING = 
            "Usage : java mil.nga.util.GetKey -key=<key name> "
            + "[ -deserialize ] [ -h ] [ -help ]";
    
    /**
     * Default constructor requiring clients to supply the key to retrieve 
     * from the cache and whether or not to print the corresponding backing
     * record from the data store.
     * 
     * @param key
     * @param getISORecords If true 
     */
    public GetKey(String key, boolean deserialize) {
    	long start = System.currentTimeMillis();
        printKeyValue(key, deserialize);
        LOGGER.info("Key/value retrieved in [ "
        		+ (System.currentTimeMillis() - start)
        		+ " ] ms.");
    }
    
    /**
     * Based on the key design in which keys are the combination of NSN and 
     * NRN of a given product, the NRN is the second half of the key.  The
     * NRN is used to extract the data from the backing data store.
     * 
     * @param key The target key.
     * @return The product NRN.
     */
    public String getNRNFromKey(String key) {
        String NRN = null;
        String[] array = key.split("\\+");
        if (array.length == 2) {
            NRN = array[1];
        }
        return NRN;
    }
    
    /**
     * Based on the key design in which keys are the combination of NSN and 
     * NRN of a given product, the NSN is the first half of the key.  The
     * NSN is used to extract the data from the backing data store.
     * 
     * @param key The target key.
     * @return The product NRN.
     */
    public String getNSNFromKey(String key) { 
        String NSN = null;
        String[] array = key.split("\\+");
        if (array.length == 2) {
            NSN = array[0];
        }
        return NSN;
    }
    
    /**
     * Print out the records from the backing data store that match the NSN and
     * NRN contained in the key. 
     *  
     * @param key The target key.
     */
    public void printISORecords(String key) {
        
        try (ProductFactory factory = ProductFactory.getInstance()) {
            List<Product> products = factory.getProducts(
                    getNRNFromKey(key), 
                    getNSNFromKey(key));
            if (products.size() > 0) {
                for (Product prod : products) {
                    System.out.println(prod.toString());
                }
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Print out the key/value pair.
     * 
     * @param key The key to query for.
     */
    public void printKeyValue(String key, boolean deserialize) {
        try (RedisCacheManager manager = RedisCacheManager.getInstance()) { 
            String value = manager.get(key);
            if ((value == null) || (value.isEmpty())) {
                LOGGER.warn("Input key [ "
                        + key 
                        + " ] does not exist in the cache.");
            }
            else {
                System.out.println("Key => [ "
                        + key 
                        + " ], value => [ "
                        + value
                        + " ].");
                if (deserialize) {
                	QueryRequestAccelerator record = 
                			JSONSerializer.getInstance()
                				.deserializeToQueryRequestAccelerator(value);
                	if (record != null) {
                		System.out.println(record.toString());
                	}
                	else {
                		System.err.println("Error encountered while deserializing the "
                				+ "requested key!");
                	}
                }
            }
            
        }
    }
    
    /**
     * Main method used to process the command line arguments.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        
        String  key         = null;
        boolean deserialize = false;
        
        // set up the command line options
        Options opt = new Options(args, 0);
        opt.getSet().addOption("key", Separator.EQUALS, Multiplicity.ONCE);
        opt.getSet().addOption("deserialize", Multiplicity.ZERO_OR_MORE);
        opt.getSet().addOption("h", Multiplicity.ZERO_OR_MORE);
        opt.getSet().addOption("help", Multiplicity.ZERO_OR_MORE);
        
        // Ensure the command line options presented make sense.
        if (!opt.check(true, false)) {
            System.err.println(USAGE_STRING);
            System.exit(1);
        }
        
        if ((opt.getSet().isSet("h")) || (opt.getSet().isSet("help"))) {
            System.out.println(HELP_STRING);
            System.exit(0);
        }
        
        if (opt.getSet().isSet("key")) {
            key = opt.getSet().getOption("key").getResultValue(0);
            if ((key == null) || (key.isEmpty())) {
                LOGGER.error("Required input parameter [ key ] is null or not "
                        + "defined.");
            }
            else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("User requested key [ "
                            + key
                            + " ].");
                }
            }
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Required input parameter [ key ] is not defined.");
            }
            System.err.println(USAGE_STRING);
            System.exit(1);
        }
        if (opt.getSet().isSet("deserialize")) {
        	deserialize = true;
        }
        new GetKey(key, deserialize);
     
    }
}
