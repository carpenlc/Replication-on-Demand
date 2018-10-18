package mil.nga.rod.jdbc;

/**
 * Interface defining the properties names used by the RoDRecordFactory 
 * class.
 * 
 * @author L. Craig Carpenter
 */
public interface ProductFactoryConstants {

    /**
     * Property containing the class name associated with the database-specific 
     * JDBC driver.
     */
    public static final String JDBC_DRIVER_PROPERTY = 
    		"db.driver";
    
    /**
     * Property containing the database-specific JDBC connection string.
     */
    public static final String JDBC_CONNECTION_STRING = 
    		"db.connection_string";
    
    /**
     * Property containing the database username.
     */
    public static final String DB_USERNAME = 
    		"db.user";
    
    /**
     * Property containing the database password.
     */
    public static final String DB_PASSWORD = 
    		"db.password";
    
    /**
     * The target table to retrieve RoD data from.
     */
    public static final String TARGET_TABLE_NAME = 
            "GW_PUB.ISO_ROD_CC_AOR_PUB";
    

    
}
