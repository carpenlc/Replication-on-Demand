package mil.nga.rod.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.model.ArtworkRow;

/**
 * Non-EJB version of the code used to interface the back-end Oracle database 
 * that stores the information on the artwork associated with the "Replication
 * n Demand" project.  This class is responsible for interacting with the 
 * Oracle tables which contain the contents used to construct 
 * <code>ArtworkRow</code> objects.
 * 
 * @author L. Craig Carpenter
 */
public class ArtworkFactory 
		extends ConnectionProperties 
		implements ConnectionPropertiesConstants, ArtworkFactoryConstants, AutoCloseable {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		ArtworkFactory.class);
   
    /**
     * Connection to the target database.
     */
    private Connection connection = null;
    
	/**
	 * Data structure used to notify the superclass what properties to load.
	 */
	@SuppressWarnings("serial")
	private static final HashMap<String, String> DB_CONN_PROPERTIES = 
			new HashMap<String, String>() {
		{
			put(USERNAME_PROPERTY,   ARTWORK_DB_USERNAME);
			put(PASSWORD_PROPERTY,   ARTWORK_DB_PASSWORD);
			put(DRIVER_PROPERTY,     ARTWORK_JDBC_DRIVER_PROPERTY);
			put(CONNECTION_PROPERTY, ARTWORK_JDBC_CONNECTION_STRING);	
		}
	};
	
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
	private ArtworkFactory() 
			throws PropertyNotFoundException, 
				PropertiesNotLoadedException, 
				ClassNotFoundException {
		super(DB_CONN_PROPERTIES);
	}
	
    /**
     * Construct a <code>java.sql.Connection</code> from the input database
     * connection properties.
     * 
     * @return A populated <code>java.sql.Connection</code> object.
     * @throws SQLException Thrown if problems were encountered establishing 
     * the database connection. 
     */
    private Connection getConnection() throws SQLException {
        
        if (connection == null) {
            connection = DriverManager.getConnection(
                    getConnectionString(),
                    getUser(),
                    getPassword());
        }
        return connection;
    }
	
    /**
     * Get a list of products that match the input NRN/NSN.  
     * 
     * @param nrn The NRN to select.
     * @param nsn The NSN to select.
     * @return A list of products matching the input NRN/NSN.
     */
    public List<ArtworkRow> getArtwork(String nrn, String nsn) {
        
        List<ArtworkRow>     artwork = new ArrayList<ArtworkRow>();
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        int               counter  = 0;
        String            sql      = "select FOLDER_PATH, CD_NAME, NSN, NRN, "
        		+ "FILE_SIZE from "
                + ARTWORK_TARGET_TABLE
                + " where NRN=? and NSN=?";
        
            
        try { 
            if ((nrn != null) && (!nrn.isEmpty())) {
                if ((nsn != null) && (!nsn.isEmpty())) {
                    if (getConnection() != null) {
        
                        stmt = getConnection().prepareStatement(sql);
                        stmt.setString(1, nrn);
                        stmt.setString(2, nsn);
                        rs   = stmt.executeQuery();
                        
                        while (rs.next()) {
                            try {
                            	ArtworkRow art = new ArtworkRow.ArtworkBuilder()
                            			.cdName(rs.getString("CD_NAME"))
                            			.nrn(rs.getString("NRN"))
                            			.nsn(rs.getString("NSN"))
                            			.path(rs.getString("FOLDER_PATH"))
                            			.size(rs.getLong("FILE_SIZE"))
                            			.build();
                            	artwork.add(art);
                            }
                            catch (IllegalStateException ise) {
                                LOGGER.warn("Unexpected IllegalStateException raised "
                                        + "while loading [ "
                                        + ARTWORK_TARGET_TABLE
                                        + " ] records from "
                                        + "data store.  Error encountered [ "
                                        + ise.getMessage()
                                        + " ].");
                                counter++;
                            }
                        }
                    }
                    else {
                        LOGGER.warn("Unable to obtain a connection to the target "
                                + "database.  An empty List will be returned to "
                                + "the caller.");
                    }
                }
                else {
                    LOGGER.warn("Input NSN is null.  Query wasn't executed.  "
                            + "Return array is empty.");
                }
            }
            else {
                LOGGER.warn("Input NRN is null.  Query wasn't executed.  "
                        + "Return array is empty.");     
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve record from target table [ "
                    + ARTWORK_TARGET_TABLE
                    + " ] with NRN => [ "
                    + nrn
                    + " ], and NSN => [ "
                    + nsn
                    + " ].  Error message => [ "
                    + se.getMessage() 
                    + " ].");
        }
        finally {
            try { 
                if (rs != null) { rs.close(); }
            } catch (Exception e) {}
            try { 
                if (stmt != null) { stmt.close(); } 
            } catch (Exception e) {}
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ " 
                    + artwork.size()
                    + " ] records selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.  Of the records selected [ "
                    + counter
                    + " ] contained data errors.");
        }
        return artwork;
    }
    
    /**
     * Retrieve a list of the unique records (by NRN/NSN combination) in the
     * target table.
     * 
     * @return A list of unique Artwork records.
     */
    public List<ArtworkRow> getUnique() {
            
    	List<ArtworkRow>       artwork       = new ArrayList<ArtworkRow>();
    	Map<String, String> uniqueRecords = new HashMap<String, String>();
    	
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select distinct NSN, NRN from "
                + ARTWORK_TARGET_TABLE;
        
        try { 
            if (getConnection() != null) {
            	stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                // Load the map containing the unique products.
                while (rs.next()) {
                	uniqueRecords.put(
                    		rs.getString("NRN"), 
                    		rs.getString("NSN"));
                }
                
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug("Loaded intermediate artwork map "
                			+ "containing [ "
                			+ uniqueRecords.size()
                			+ " ] artwork records in [ "
                			+ (System.currentTimeMillis() - start)
                			+ " ] ms.");
                }
                
                // Now load the return product list.
                if (uniqueRecords.size() > 0) {
	                for (String nrn : uniqueRecords.keySet()) {
	                	List<ArtworkRow> tempList = getArtwork(
	                			nrn, 
	                			uniqueRecords.get(nrn));
	                	if (tempList.size() > 0) {
	                		artwork.add(tempList.get(0));
	                	}
	                	else {
	                		LOGGER.warn("Unable to retrieve unique artwork record "
	                				+ "with key (i.e. NRN) => [ "
	                				+ nrn
	                				+ " ], and value (i.e. NSN) => [ "
	                				+ uniqueRecords.get(nrn)
	                				+ " ].");
	                	}
	                }
	                if (LOGGER.isDebugEnabled()) {
	                	LOGGER.debug("Loaded [ "
	                			+ artwork.size()
	                			+ " ] unique artwork records in [ "
	                			+ (System.currentTimeMillis() - start)
	                			+ " ] ms.");
	                }
                }
                else {
                	LOGGER.warn("Found 0 unique artwork records.  Return "
                			+ "list will be empty.");
                }
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve the list of unique artwork "
                    + "records from the target data source.  Error "
                    + "message => [ "
                    + se.getMessage() 
                    + " ].");
        }
        finally {
            try { 
                if (rs != null) { rs.close(); } 
            } catch (Exception e) {}
            try { 
                if (stmt != null) { stmt.close(); } 
            } catch (Exception e) {}
        }
        
        return artwork;
    }
    
    /**
     * Accessor method for the singleton instance of the ArtworkFactory class.
     * @return The singleton instance of the ArtworkFactory class.
     */
    public static ArtworkFactory getInstance() 
            throws PropertyNotFoundException, 
                PropertiesNotLoadedException, 
                ClassNotFoundException {
        return ArtworkFactoryHolder.getSingleton();
    } 
    
    /**
     * Close the database connection if open.
     */
    @Override
    public void close() {
        if (connection != null) {
            LOGGER.info("Closing JDBC connection.");
            try { connection.close(); } catch (Exception e) {}
        }
    }
    
    /**
     * Debugging method to print out database connection parameters.
     */
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Artwork Product Datasource: ");
    	sb.append(super.toString());
    	sb.append(", Target Table Name => [ ");
    	sb.append(ARTWORK_TARGET_TABLE);
    	sb.append(" ].");
    	return sb.toString();
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class ArtworkFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the ArtworkFactory.
         */
        private static ArtworkFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * ArtworkFactory.
         * 
         * @return The Singleton instance of the ArtworkFactory.
         * @throws PropertyNotFoundException Thrown if any of the required 
         * properties are not supplied.
         * @throws PropertiesNotLoadedException Thrown if all required 
         * properties are not supplied. 
         * @throws ClassNotFoundException Thrown if the defined JDBC driver 
         * could not be found. 
         */
        public static ArtworkFactory getSingleton() 
                throws PropertyNotFoundException, PropertiesNotLoadedException, ClassNotFoundException {
            if (_instance == null) {
                _instance = new ArtworkFactory();
            }
            return _instance;
        }
    }
}
