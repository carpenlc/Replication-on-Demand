package mil.nga.rod.accelerator;

/**
 * Constants utilized within Replication-on-Demand caching implementation.
 * 
 * @author L. Craig Carpenter
 */
public interface CacheManagerConstants {

    /**
     * Assuming a key/value backing cache, the following is the key associated
     * with the list of country names available in the Replication-on-Demand 
     * holdings. 
     */
    public static final String COUNTRY_NAMES_KEY = 
            "rod-country-names";
    
    /**
     * Assuming a key/value backing cache, the following is the key associated
     * with the list of product types available in the Replication-on-Demand 
     * holdings. 
     */
    public static final String AVAILABLE_PRODUCT_TYPES_KEY = 
            "rod-product-types";
    
    /**
     * Assuming a key/value backing cache, the following is the key associated
     * with the list of areas of responsibility (AORs) in the 
     * Replication-on-Demand holdings. 
     */
    public static final String AVAILABLE_AORS_KEY = 
            "rod-aors";
    
}
