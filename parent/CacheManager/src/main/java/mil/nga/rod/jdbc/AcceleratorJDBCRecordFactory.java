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
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.util.ProductUtils;

/**
 * This is kind of messy because the accelerator record data is stored in a 
 * different schema than the actual product data.  
 * 
 * Non-EJB version of the code used to interface the back-end Oracle database 
 * that stores the information on the ISO files created for "Replication on 
 * Demand".
 * 
 * @author L. Craig Carpenter
 */
public class AcceleratorJDBCRecordFactory 
		extends ConnectionProperties
        implements AcceleratorJDBCRecordFactoryConstants, AutoCloseable {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
            AcceleratorJDBCRecordFactory.class);
    
	/**
	 * Data structure used to notify the superclass what properties to load.
	 */
	@SuppressWarnings("serial")
	private static final HashMap<String, String> DB_CONN_PROPERTIES = 
			new HashMap<String, String>() {
		{
			put(USERNAME_PROPERTY,   ACCELERATOR_DB_USERNAME);
			put(PASSWORD_PROPERTY,   ACCELERATOR_DB_PASSWORD);
			put(DRIVER_PROPERTY,     ACCELERATOR_JDBC_DRIVER_PROPERTY);
			put(CONNECTION_PROPERTY, ACCELERATOR_JDBC_CONNECTION_STRING);	
		}
	};
    
    /**
     * Connection to the target database.
     */
    private Connection connection = null;

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
    private AcceleratorJDBCRecordFactory () 
            throws PropertyNotFoundException, 
                PropertiesNotLoadedException, 
                ClassNotFoundException {
    	super(DB_CONN_PROPERTIES);
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug(this.toString());
    	}
    }
    
    
    /**
     * Simple wrapper method allowing clients to retrieve a 
     * <code>QueryRequestAccelerator</code> record by key.  They key 
     * is a concatenation of NRN and NSN.
     *   
     * @param key Unique product key.
     * @return A <code>QueryRequestAccelerator</code> record matching the 
     * input key.
     */
    public QueryRequestAccelerator getRecord(String key) {
    	QueryRequestAccelerator record = null;
    	if ((key != null) && (!key.isEmpty())) {
	    	record = getRecord(
	    			ProductUtils.getInstance().getNRNFromKey(key), 
	    			ProductUtils.getInstance().getNSNFromKey(key));
    	}
    	else {
    		LOGGER.warn("Input key is null or undefined.  Returned record will "
    				+ "be null.");
    	}
    	return record;
    }
    
    /**
     * Retrieve a single record from the appropriate backing data store 
     * based on NRN/NSN combination.
     * 
     * @param nrn Client requested NRN.
     * @param nsn Client requested NSN.
     * @return A single <code>QueryRequestAccelerator</code> based on the 
     * input NSN/NRN.
     */
    public QueryRequestAccelerator getRecord(String nrn, String nsn) {
    	
    	QueryRequestAccelerator record = null;
        
        if ((nrn != null) && (!nrn.isEmpty())) {
        	if ((nsn != null) && (!nsn.isEmpty())) {
        		
        		try {
        			
	        		// First, get the product based on NRN/NSN.
	        		List<Product> prods = ProductFactory
	        				.getInstance()
	        				.getProducts(nrn, nsn);
	            	if (prods.size() > 0) {
	            		record = getRecord(prods.get(0));
	            	}
	            	else {
	            		LOGGER.warn("Unable to retrieve a product from the "
	            				+ "datasource with NRN => [ " 
	            				+ nrn 
	            				+ " ] and NSN => [ "
	            				+ nsn
	            				+ " ].  The returned QueryRequestAccelerator "
	            				+ "record will be null.  This is likely an "
	            				+ "orphaned record and should be removed.");
	            	}
				}
                catch (ClassNotFoundException cnfe) {
                	LOGGER.error("Configuration error encountered.  "
                			+ "Database unavailable.  Unexpected "
                			+ "ClassNotFoundException raised.  "
                			+ "Error message => [ "
                			+ cnfe.getMessage()
                			+ " ].");
                }
                catch (PropertiesNotLoadedException pnle) {
                	LOGGER.error("Configuration error encountered.  "
                			+ "Database unavailable.  Unexpected "
                			+ "PropertiesNotLoadedException raised.  "
                			+ "Error message => [ "
                			+ pnle.getMessage()
                			+ " ].");
                }
                catch (PropertyNotFoundException pnfe) {
                	LOGGER.error("Configuration error encountered.  "
                			+ "Database unavailable.  Unexpected "
                			+ "PropertyNotFoundException raised.  "
                			+ "Error message => [ "
                			+ pnfe.getMessage()
                			+ " ].");
                }
        	}
        	else {
    			LOGGER.warn("Product NSN is null or empty.  Unable to "
    					+ "generate the cache accelerator record.  Return "
    					+ "value will be null.");
        	}
        }
   		else {
			LOGGER.warn("Product NRN is null or empty.  Unable to "
					+ "generate the cache accelerator record.  Return "
					+ "value will be null.");
		}
        return record;
    }
    		
    /**
     * Retrieve the query request accelerator data from the backing data
     * source.   
     * 
     * @param prod The product that has been selected.
     * @return The associated QueryRequestAccelerator or null if errors 
     * were encountered retrieving the data.
     */
    public QueryRequestAccelerator getRecord(Product prod) {
    	
    	QueryRequestAccelerator record = null;
        PreparedStatement       stmt   = null;
        ResultSet               rs     = null;
        long                    start  = System.currentTimeMillis();
        String                  sql    = 
        		"select NRN, NSN, FILE_DATE, FILE_SIZE, HASH from "
                + ACCELERATOR_TARGET_TABLE_NAME
                + " where NRN=? and NSN=?";
        
    	if (prod != null) {
    		if ((prod.getNRN() != null) && (!prod.getNRN().isEmpty())) {
    			if ((prod.getNSN() != null) && (!prod.getNSN().isEmpty())) {
    				
    				try {
	    				if (getConnection() != null) {
	    					
		                    stmt = getConnection().prepareStatement(sql);
		                    stmt.setString(1, prod.getNRN());
		                    stmt.setString(2, prod.getNSN());
		                    rs   = stmt.executeQuery();
		                    
		                    while (rs.next()) {
		                    	record = new QueryRequestAccelerator
		                    			.QueryRequestAcceleratorBuilder()
		                    				.product(prod)
		                    				.fileDate(rs.getDate("FILE_DATE"))
		                    				.size(rs.getLong("FILE_SIZE"))
		                    				.hash(rs.getString("HASH"))
		                    			.build();
		                    }
		                 
		    				if (LOGGER.isDebugEnabled()) {
		    	                LOGGER.debug("Accelerator record for file [ " 
		    	                        + prod.getPath()
		    	                        + " ] retrieved in [ "
		    	                        + (System.currentTimeMillis() - start) 
		    	                        + " ] ms.");
		    	            }
	    				}
    				}
    				catch (IllegalStateException ise) {
                        LOGGER.warn("Unexpected IllegalStateException raised "
                                + "while loading [ "
                                + ACCELERATOR_TARGET_TABLE_NAME
                                + " ] records from "
                                + "data store.  Error encountered [ "
                                + ise.getMessage()
                                + " ].");
    				}
    				catch (SQLException se) {
    		            LOGGER.error("An unexpected SQLException was raised "
    		            		+ "while attempting to retrieve accelerator "
    		            		+ "record for NRN => [ "
    		                    + prod.getNRN()
    		                    + " ] and NSN => [ "
    		                    + prod.getNSN()
    		                    + " ].  Error message [ "
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
    			}
    			else {
        			LOGGER.warn("Product NSN is null or empty.  Unable to "
        					+ "generate the cache accelerator record.  Return "
        					+ "value will be null.");
    			}
    		}
    		else {
    			LOGGER.warn("Product NRN is null or empty.  Unable to "
    					+ "generate the cache accelerator record.  Return "
    					+ "value will be null.");
    		}
    	}
    	else {
    		LOGGER.warn("Input product is null.  Unable to generate the "
    				+ "cache accelerator record.  Return value will be "
    				+ "null.");
    	}
    	return record;
    }
    
    /**
     * Insert the data associated with the query request accelerator record 
     * into the backing data store.   
     * @param record The record to insert.
     */
    public void insert (QueryRequestAccelerator record) {
    	
    	String sql = "INSERT INTO " + ACCELERATOR_TARGET_TABLE_NAME 
    			+ " (NRN, NSN, FILE_DATE, FILE_SIZE, HASH) VALUES (?, ?, ?, ?, ?)";
    	PreparedStatement stmt     = null;
    	
    	try {
	    	if (getConnection() != null) {
	    		
	    		stmt = getConnection().prepareStatement(sql);
	    		stmt.setString(1, record.getProduct().getNRN());
	    		stmt.setString(2, record.getProduct().getNSN());
	    		stmt.setDate(  3, new java.sql.Date(record.getFileDate().getTime()));
	    		stmt.setLong(  4, record.getSize());
	    		stmt.setString(5, record.getHash());
	    		
	    		stmt.executeUpdate();
	    	}
    	}
    	catch (SQLException se) {
	        LOGGER.error("An unexpected SQLException was raised while "
	                + "attempting to insert a single [ "
	                + ACCELERATOR_TARGET_TABLE_NAME
	                + " ] record in the target data source.  Error "
	                + "message [ "
	                + se.getMessage() 
	                + " ].");
	    }
	    finally {
	        try { 
	            if (stmt != null) { stmt.close(); } 
	        } catch (Exception e) {}
	    }
    }
    
    /**
     * Remove any <code>QueryRequestAccelerator</code> records from the 
     * backing data store that match the input NRN/NSN combination.
     * 
     * @param nrn The NRN
     * @param nsn The NSN
     */
    public void remove(String nrn, String nsn) {

        PreparedStatement       stmt   = null;
        ResultSet               rs     = null;
        long                    start  = System.currentTimeMillis();
        String                  sql    = 
        		"delete from "
                + ACCELERATOR_TARGET_TABLE_NAME
                + " where NRN=? and NSN=?";
    	try {
	    	if (getConnection() != null) {
	    		stmt = getConnection().prepareStatement(sql);
	    		stmt.setString(1, nrn);
	    		stmt.setString(2, nsn);
	    		stmt.executeUpdate();
	    	}
    	}
    	catch (SQLException se) {
	        LOGGER.error("An unexpected SQLException was raised while "
	                + "attempting to insert a single [ "
	                + ACCELERATOR_TARGET_TABLE_NAME
	                + " ] record in the target data source.  Error "
	                + "message => [ "
	                + se.getMessage() 
	                + " ].");
	    }
	    finally {
	        try { 
	            if (stmt != null) { stmt.close(); } 
	        } catch (Exception e) {}
	    }
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Record with NRN => [ "
    				+ nrn
    				+ " ], NSN => [ "
    				+ nsn
    				+ " ] deleted from [ "
    				+ ACCELERATOR_TARGET_TABLE_NAME 
    				+ " ] in [ "
    				+ (System.currentTimeMillis() - start)
    				+ " ] ms.");
    	}
        
    }
    
    /**
     * Because the cache is maintained on multiple nodes, we found that it 
     * was possible (even likely) to end up with duplicate records in the data
     * source.  This method was introduced to clean up those duplicates. 
     */
    public void removeDuplicates() {
    	
    	long   start = System.currentTimeMillis();
    	String sql   = "DELETE FROM " + ACCELERATOR_TARGET_TABLE_NAME 
    			+ " WHERE rowid not in (SELECT MIN(rowid) FROM " 
    			+ ACCELERATOR_TARGET_TABLE_NAME
    			+ " GROUP BY nrn, nsn)";
        PreparedStatement stmt     = null;
    	
    	try {
	    	if (getConnection() != null) {
	    		stmt = getConnection().prepareStatement(sql);
	    		stmt.executeUpdate();
	    	}
    	}
    	catch (SQLException se) {
	        LOGGER.error("An unexpected SQLException was raised while "
	                + "attempting to insert a single [ "
	                + ACCELERATOR_TARGET_TABLE_NAME
	                + " ] record in the target data source.  Error "
	                + "message => [ "
	                + se.getMessage() 
	                + " ].");
	    }
	    finally {
	        try { 
	            if (stmt != null) { stmt.close(); } 
	        } catch (Exception e) {}
	    }
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Duplicates removed from table [ "
    				+ ACCELERATOR_TARGET_TABLE_NAME 
    				+ " ] in [ "
    				+ (System.currentTimeMillis() - start)
    				+ " ] ms.");
    	}
    }
    
    /**
     * Update an existing query request accelerator record in the backing 
     * data store.   
     * @param record The record to insert.
     */
    public void update (QueryRequestAccelerator record) {
    	
    	String sql = "UPDATE " 
    			+ ACCELERATOR_TARGET_TABLE_NAME 
    			+ " SET FILE_DATE=?, FILE_SIZE=?, HASH=? WHERE NRN=? AND NSN=?";
    	PreparedStatement stmt     = null;
    	
    	try {
	    	if (getConnection() != null) {
	    		stmt = getConnection().prepareStatement(sql);
	    		stmt.setDate(  1, new java.sql.Date(record.getFileDate().getTime()));
	    		stmt.setLong(  2, record.getSize());
	    		stmt.setString(3, record.getHash());
	    		stmt.setString(4, record.getProduct().getNRN());
	    		stmt.setString(5, record.getProduct().getNSN());
	    		stmt.executeUpdate();
	    	}
    	}
    	catch (SQLException se) {
	        LOGGER.error("An unexpected SQLException was raised while "
	                + "attempting to update a single [ "
	                + ACCELERATOR_TARGET_TABLE_NAME
	                + " ] record in the target data source.  Error "
	                + "message [ "
	                + se.getMessage() 
	                + " ].");
	    }
	    finally {
	        try { 
	            if (stmt != null) { stmt.close(); } 
	        } catch (Exception e) {}
	    }
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
     * Accessor method for the singleton instance of the 
     * ProductQueryResponseMarshaller class.
     * 
     * @return The singleton instance of the ProductQueryResponseMarshaller .
     * class.
     */
    public static AcceleratorJDBCRecordFactory getInstance() 
            throws PropertyNotFoundException, 
                PropertiesNotLoadedException, 
                ClassNotFoundException {
        return RoDRecordFactoryHolder.getSingleton();
    } 
    
    /**
     * 
     * @return A Map of unique <code>QueryRequestAccelerator</code> records.
     */
    public Map<String, QueryRequestAccelerator> getUniqueRecordsMap() {
       	Map<String, QueryRequestAccelerator> records     = 
       			new HashMap<String, QueryRequestAccelerator>();
       	long startTime = System.currentTimeMillis();
    	
       	List<QueryRequestAccelerator> recordList = getUniqueRecords();
    	
    	if ((recordList != null) && (recordList.size() > 0)) {
    		for (QueryRequestAccelerator record : recordList) {
    			records.put(
    					ProductUtils.getInstance().getKey(record.getProduct()), 
    					record);
    		}
    	}
    	else {
    		LOGGER.warn("List of QueryRequestAccelerator retrieved from the backing data "
    				+ "store is null, or empty.  The return product map "
    				+ "will be empty.");
    	}
    	
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Unique record map containing [ "
    				+ records.size() 
    				+ " ] QueryRequestAccelerator records created in [ "
    				+ (System.currentTimeMillis() - startTime)
    				+ " ].");
    	}
    	return records;
    }
    
    /**
     * In order to synchronize the accelerator records, we don't need to fully 
     * materialize the lists to see what needs to be done.  This method will 
     * return a list of keys that can then be used to determine what needs to 
     * be added/updated/removed.
     * 
     * @return A list of unique keys.
     */
    public List<String> getUniqueKeys() {
    	
    	List<String> keys = new ArrayList<String>();
    	
    	PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select distinct NSN, NRN from "
                + ACCELERATOR_TARGET_TABLE_NAME;
        
        try { 
            if (getConnection() != null) {
            	
            	stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                // Load the map containing the unique products.
                while (rs.next()) {
                	keys.add(ProductUtils.getInstance().getKey(
                    		rs.getString("NRN"), 
                    		rs.getString("NSN")));
                }
                
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug("Loaded [ "
                			+ keys.size()
                			+ " ] unique keys in [ "
                			+ (System.currentTimeMillis() - start)
                			+ " ] ms.");
                }
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve the list of unique products "
                    + "from the target data source.  Error message => [ "
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
        return keys;
        
    }
    
    /**
     * 
     * @return A <code>List</code> of unique <code>QueryRequestAccelerator</code> 
     * records.
     */
    public List<QueryRequestAccelerator> getUniqueRecords() {
    	
    	List<QueryRequestAccelerator> records       = 
    			new ArrayList<QueryRequestAccelerator>();
    	Map<String, String>           uniqueRecords = 
    			new HashMap<String, String>();
    	
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select distinct NSN, NRN from "
                + ACCELERATOR_TARGET_TABLE_NAME;
        
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
                	LOGGER.debug("Loaded intermediate QueryRequestAccelerator "
                			+ "map containing [ "
                			+ uniqueRecords.size()
                			+ " ] products in [ "
                			+ (System.currentTimeMillis() - start)
                			+ " ] ms.");
                }
                
                // Now load the return product list.
                if (uniqueRecords.size() > 0) {
	                for (String nrn : uniqueRecords.keySet()) {
	                	
	                	QueryRequestAccelerator record = getRecord(
	                			nrn, 
	                			uniqueRecords.get(nrn));
	                	
	                	if (record != null) {
	                		records.add(record);
	                	}
	                	else {
	                		LOGGER.warn("Unable to retrieve unique product "
	                				+ "with key (i.e. NRN) => [ "
	                				+ nrn
	                				+ " ], and value (i.e. NSN) => [ "
	                				+ uniqueRecords.get(nrn)
	                				+ " ].");
	                	}
	                }
	                if (LOGGER.isDebugEnabled()) {
	                	LOGGER.debug("Loaded [ "
	                			+ records.size()
	                			+ " ] unique QueryRequestAccelerator records in [ "
	                			+ (System.currentTimeMillis() - start)
	                			+ " ] ms.");
	                }
                }
                else {
                	LOGGER.warn("Found 0 unique QueryRequestAccelerator "
                			+ "records.  Return list will be empty.");
                }
                
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve the list of unique products "
                    + "from the target data source.  Error message => [ "
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
        return records;
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
    	sb.append("RoD Product Datasource: ");
    	sb.append(super.toString());
    	sb.append(", Target Table Name => [ ");
    	sb.append(ACCELERATOR_TARGET_TABLE_NAME);
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
    public static class RoDRecordFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the RoDRecordFactory.
         */
        private static AcceleratorJDBCRecordFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * RoDRecordFactory.
         * 
         * @return The Singleton instance of the RoDRecordFactory.
         * @throws PropertyNotFoundException Thrown if any of the required 
         * properties are not supplied.
         * @throws PropertiesNotLoadedException Thrown if all required 
         * properties are not supplied. 
         * @throws ClassNotFoundException Thrown if the defined JDBC driver 
         * could not be found. 
         */
        public static AcceleratorJDBCRecordFactory getSingleton() 
                throws PropertyNotFoundException, 
                	PropertiesNotLoadedException, 
                	ClassNotFoundException {
            if (_instance == null) {
                _instance = new AcceleratorJDBCRecordFactory();
            }
            return _instance;
        }
        
    }
}
