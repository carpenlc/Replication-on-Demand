package mil.nga.rod.jdbc;

public interface AcceleratorJDBCRecordFactoryConstants {

    /**
     * Property containing the database-specific JDBC connection string.  This 
     * is for the hash code/accelerator data which doesn't have to be in the 
     * same database.
     */
    public static final String ACCELERATOR_JDBC_CONNECTION_STRING = 
    		"accelerator.db.connection_string";
    
    /**
     * Property containing the database username.  This 
     * is for the hash code/accelerator data which doesn't have to be in the 
     * same database.
     */
    public static final String ACCELERATOR_DB_USERNAME = 
    		"accelerator.db.user";
    
    /**
     * Property containing the class name associated with the database-specific 
     * JDBC driver.  This is for the hash code/accelerator data which doesn't 
     * have to be in the same database.
     */
    public static final String ACCELERATOR_JDBC_DRIVER_PROPERTY = 
    		"accelerator.db.driver";
    
    /**
     * Property containing the database password.  This 
     * is for the hash code/accelerator data which doesn't have to be in the 
     * same database.
     */
    public static final String ACCELERATOR_DB_PASSWORD = 
    		"accelerator.db.password";
    
    /**
     * The target table to retrieve RoD accelerator data from.
     */
    public static final String ACCELERATOR_TARGET_TABLE_NAME = 
            "ROD_QUERY_REQUEST_ACCELERATOR";
    
}
