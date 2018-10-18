package mil.nga.rod.jdbc;

import java.util.HashMap;

import mil.nga.PropertyLoader;
import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;

public class ConnectionProperties implements ConnectionPropertiesConstants {

    // JDBC Connection properties
    private String jdbcDriver       = null;
    private String connectionString = null;
    private String dbUser           = null;
    private String password         = null;
    
    /**
     * Default constructor loading the required system properties.
     * 
     * @throws PropertyNotFoundException Thrown if any of the required 
     * properties are not defined. 
     * @throws PropertiesNotLoadedException Thrown if the system properties
     * cannot be loaded.
     * @throws ClassNotFoundException Thrown if the JDBC driver cannot be
     * loaded.
     */
	protected ConnectionProperties(HashMap<String, String> map) 
			throws PropertyNotFoundException, 
    				PropertiesNotLoadedException, 
    				ClassNotFoundException {

		if ((map != null) && (map.size() > 0)) {
			PropertyLoader props = PropertyLoader.getInstance();
			
			setJdbcDriver(
					props.getProperty(
							map.get(DRIVER_PROPERTY)),
							map.get(DRIVER_PROPERTY));
			setConnectionString(
					props.getProperty(
							map.get(CONNECTION_PROPERTY)), 
							map.get(CONNECTION_PROPERTY));
			setUser(
					props.getProperty(
							map.get(USERNAME_PROPERTY)), 
							map.get(USERNAME_PROPERTY));
			setPassword(
					props.getProperty(
							map.get(PASSWORD_PROPERTY)),
							map.get(PASSWORD_PROPERTY));
			Class.forName(getJdbcDriver());
		}
	}
	
    /**
     * Getter method for the JDBC database connection string.
     * 
     * @return The JDBC database connection string.
     */
    protected String getConnectionString() {
        return connectionString;
    }
    
    /**
     * Getter method for the JDBC driver class name.

     * @return The JDBC driver class name.
     */
    protected String getJdbcDriver() {
        return jdbcDriver;
    }
    
    /**
     * Getter method for the password associated with the database user.
     * 
     * @return The password associated with the database user.
     */
    protected String getPassword() {
        return password;
    }
    
    /**
     * Getter method for the database user.
     * 
     * @return The database user.
     */
    protected String getUser() {
        return dbUser;
    }
    
    /**
     * Setter method for the JDBC database connection string.
     * 
     * @param value The value for the JDBC database connection string.
     * @param propertyName Used for logging purposes.
     * @throws PropertyNotFoundException Thrown if the input value is null or
     * empty.
     */
    private void setConnectionString(String value, String propertyName) 
            throws PropertyNotFoundException {
        
        if ((value == null) || (value.isEmpty())) {
            throw new PropertyNotFoundException("Required property [ "
                    + propertyName
                    + " ] was not supplied.");
        }
        else {
            connectionString = value;
        }    
        
    }
    
    /**
     * Setter method for the JDBC driver class name.
     * 
     * @param value The value for the JDBC driver class name.
     * @param propertyName Used for logging purposes.
     * @throws PropertyNotFoundException Thrown if the input value is null or
     * empty.
     */
    private void setJdbcDriver(String value, String propertyName) 
            throws PropertyNotFoundException {
        
        if ((value == null) || (value.isEmpty())) {
            throw new PropertyNotFoundException("Required property [ "
                    + propertyName
                    + " ] was not supplied.");
        }
        else {
            jdbcDriver = value;
        }    
        
    }
    
    /**
     * Setter method for the password associated with the database user.
     * 
     * @param value The value for the password associated with the database 
     * user.
     * @param propertyName Used for logging purposes.
     * @throws PropertyNotFoundException Thrown if the input value is null or
     * empty.
     */
    private void setPassword(String value, String propertyName) 
            throws PropertyNotFoundException {
        
        if ((value == null) || (value.isEmpty())) {
            throw new PropertyNotFoundException("Required property [ "
                    + propertyName
                    + " ] was not supplied.");
        }
        else {
            password = value;
        }    
        
    }
    
    /**
     * Setter method for the database user.
     * 
     * @param value The value for the database user.
     * @param propertyName Used for logging purposes.
     * @throws PropertyNotFoundException Thrown if the input value is null or
     * empty.
     */
    private void setUser(String value, String propertyName) 
            throws PropertyNotFoundException {
        
        if ((value == null) || (value.isEmpty())) {
            throw new PropertyNotFoundException("Required property [ "
                    + propertyName
                    + " ] was not supplied.");
        }
        else {
            dbUser = value;
        }    
    }
    
    /**
     * Convert the connection parameters to a printable String.
     */
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("JDBC Connection String => [ ");
    	sb.append(getConnectionString());
    	sb.append(" ], Database User => [ ");
    	sb.append(getUser());
    	sb.append(" ], Password => [ <hidden> ], JDBC Driver => [ ");
    	sb.append(getJdbcDriver());
    	sb.append(" ]");
    	return sb.toString();
    }
}
