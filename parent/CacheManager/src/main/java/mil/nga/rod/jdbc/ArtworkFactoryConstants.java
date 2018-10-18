package mil.nga.rod.jdbc;

/**
 * Define the names of the properties that will contain the database connection 
 * properties information associated with the location of the artwork information.
 * 
 * @author L. Craig Carpenter
 */
public interface ArtworkFactoryConstants {

	/**
	 * The target database table containing the artwork information.
	 */
	public static final String ARTWORK_TARGET_TABLE = "GW_PUB.RPF_CIB_ARTWORK";
	
    /**
     * Property containing the class name associated with the database-specific 
     * JDBC driver.  
     */
    public static final String ARTWORK_JDBC_DRIVER_PROPERTY = 
    		"artwork.db.driver";
    
    /**
     * Property containing the database-specific JDBC connection string.
     */
    public static final String ARTWORK_JDBC_CONNECTION_STRING = 
    		"artwork.db.connection_string";
    
    /**
     * Property containing the database username.  
     */
    public static final String ARTWORK_DB_USERNAME = 
    		"artwork.db.user";
    
    /**
     * Property containing the database username.  
     */
    public static final String ARTWORK_DB_PASSWORD = 
    		"artwork.db.password";
}
