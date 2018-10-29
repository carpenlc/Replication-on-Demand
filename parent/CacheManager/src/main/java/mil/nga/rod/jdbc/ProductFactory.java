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
import mil.nga.rod.util.ProductUtils;

/**
 * Non-EJB version of the code used to interface the back-end Oracle database 
 * that stores the information on the ISO files created for "Replication on 
 * Demand".
 * 
 * @author L. Craig Carpenter
 */
public class ProductFactory extends ConnectionProperties
        implements ProductFactoryConstants, ConnectionPropertiesConstants, AutoCloseable {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
            ProductFactory.class);
   
    /**
     * Connection to the target database.
     */
    private Connection rodConnection = null;

	/**
	 * Data structure used to notify the superclass what properties to load.
	 */
	@SuppressWarnings("serial")
	private static final HashMap<String, String> DB_CONN_PROPERTIES = 
			new HashMap<String, String>() {
		{
			put(USERNAME_PROPERTY,   DB_USERNAME);
			put(PASSWORD_PROPERTY,   DB_PASSWORD);
			put(DRIVER_PROPERTY,     JDBC_DRIVER_PROPERTY);
			put(CONNECTION_PROPERTY, JDBC_CONNECTION_STRING);	
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
    private ProductFactory () 
            throws PropertyNotFoundException, 
                PropertiesNotLoadedException, 
                ClassNotFoundException {
       super(DB_CONN_PROPERTIES);
	   	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(this.toString());
		}
    }
    
    /**
     * Get a list of all of the product records in the back-end data store.
     * 
     * @return A list of all Products in the back-end data store.
     */
    public List<Product> getAllProducts() {
        
        List<Product>     products = new ArrayList<Product>();
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        int               counter  = 0;
        String            sql      = "select PROD_TYPE, MEDIA_NAME, NRN, "
                + "NSN, EDITION, LOAD_DATE, FILE_DATE, SEC_CLASS, CLASS_DESC, "
                + "SEC_REL, REL_DESC, UNIX_PATH, HYPERLINK_URL, ALL_NOTES, "
                + "ISO3CHR, AOR_CODE, COUNTRY_NAME, PRODUCT_SIZE_BYTES from "
                + TARGET_TABLE_NAME
                + " order by FILE_DATE desc";
        
            
        try { 
            if (getConnection() != null) {

                stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                while (rs.next()) {
                    try {
                        Product product = new Product.ProductBuilder()
                                .aorCode(rs.getString("AOR_CODE"))
                                .classification(rs.getString("SEC_CLASS"))
                                .classificationDescription(
                                        rs.getString("CLASS_DESC"))
                                .countryName(rs.getString("COUNTRY_NAME"))
                                .edition(rs.getLong("EDITION"))
                                .fileDate(rs.getDate("FILE_DATE"))
                                .iso3Char(rs.getString("ISO3CHR"))
                                .loadDate(rs.getDate("LOAD_DATE"))
                                .mediaName(rs.getString("MEDIA_NAME"))
                                .notes(rs.getString("ALL_NOTES"))
                                .nsn(rs.getString("NSN"))
                                .nrn(rs.getString("NRN"))
                                .path(rs.getString("UNIX_PATH"))
                                .productType(rs.getString("PROD_TYPE"))
                                .releasability(rs.getString("SEC_REL"))
                                .releasabilityDescription(
                                        rs.getString("REL_DESC"))
                                .size(rs.getLong("PRODUCT_SIZE_BYTES"))
                                .url(rs.getString("HYPERLINK_URL"))
                            .build();
                        products.add(product);
                    }
                    catch (IllegalStateException ise) {
                        LOGGER.warn("Unexpected IllegalStateException raised "
                                + "while loading [ "
                                + TARGET_TABLE_NAME
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
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve all [ "
                    + TARGET_TABLE_NAME
                    + " ] records from the target data source.  Error "
                    + "message [ "
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
                    + products.size()
                    + " ] records selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.  Of the records selected [ "
                    + counter
                    + " ] contained data errors.");
        }
        return products;
    }
    
    
    /**
     * Simple wrapper method allowing clients to retrieve a list of products 
     * by key.  They key is a concatenation of NRN and NSN.
     *   
     * @param key Unique product key.
     * @return A list of products matching the input key.
     */
    public List<Product> getProducts(String key) {
    	List<Product> prods = new ArrayList<Product>();
    	if ((key != null) && (!key.isEmpty())) {
	    	String nrn = ProductUtils.getInstance().getNRNFromKey(key);
	    	String nsn = ProductUtils.getInstance().getNSNFromKey(key);
	    	prods = getProducts(nrn, nsn);
    	}
    	else {
    		LOGGER.warn("Input key is null or undefined.  Returned list will "
    				+ "be empty.");
    	}
    	return prods;
    }
    
    /**
     * Get a list of products that match the input NRN/NSN.  
     * 
     * @param nrn The NRN to select.
     * @param nsn The NSN to select.
     * @return A list of products matching the input NRN/NSN.
     */
    public List<Product> getProducts(String nrn, String nsn) {
        
        List<Product>     products = new ArrayList<Product>();
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        int               counter  = 0;
        String            sql      = "select PROD_TYPE, MEDIA_NAME, NRN, "
                + "NSN, EDITION, LOAD_DATE, FILE_DATE, SEC_CLASS, CLASS_DESC, "
                + "SEC_REL, REL_DESC, UNIX_PATH, HYPERLINK_URL, ALL_NOTES, "
                + "ISO3CHR, AOR_CODE, COUNTRY_NAME, PRODUCT_SIZE_BYTES from "
                + TARGET_TABLE_NAME
                + " where NRN=? and NSN=? order by FILE_DATE desc";
        
            
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
                                Product product = new Product.ProductBuilder()
                                        .aorCode(rs.getString("AOR_CODE"))
                                        .classification(rs.getString("SEC_CLASS"))
                                        .classificationDescription(
                                                rs.getString("CLASS_DESC"))
                                        .countryName(rs.getString("COUNTRY_NAME"))
                                        .edition(rs.getLong("EDITION"))
                                        .fileDate(rs.getDate("FILE_DATE"))
                                        .iso3Char(rs.getString("ISO3CHR"))
                                        .loadDate(rs.getDate("LOAD_DATE"))
                                        .mediaName(rs.getString("MEDIA_NAME"))
                                        .notes(rs.getString("ALL_NOTES"))
                                        .nsn(rs.getString("NSN"))
                                        .nrn(rs.getString("NRN"))
                                        .path(rs.getString("UNIX_PATH"))
                                        .productType(rs.getString("PROD_TYPE"))
                                        .releasability(rs.getString("SEC_REL"))
                                        .releasabilityDescription(
                                                rs.getString("REL_DESC"))
                                        .size(rs.getLong("PRODUCT_SIZE_BYTES"))
                                        .url(rs.getString("HYPERLINK_URL"))
                                        .build();
                                products.add(product);
                            }
                            catch (IllegalStateException ise) {
                                LOGGER.warn("Unexpected IllegalStateException raised "
                                        + "while loading [ "
                                        + TARGET_TABLE_NAME
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
                    + TARGET_TABLE_NAME
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
        
        //if (LOGGER.isDebugEnabled()) {
        //   LOGGER.debug("[ " 
        //            + products.size()
        //            + " ] records selected in [ "
        //            + (System.currentTimeMillis() - start) 
        //            + " ] ms.  Of the records selected [ "
        //            + counter
        //            + " ] contained data errors.");
        //}
        return products;
    }
    
    /**
     * Get a list of AOR codes from the back end data source.
     * 
     * @return The list of AOR codes.
     */
    public List<String> getAORCodes() {
        
        List<String>      aors   = new ArrayList<String>();
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "select distinct(AOR_CODE) from "
                + TARGET_TABLE_NAME;
        
        try {
            if (getConnection() != null) {
                stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    aors.add(rs.getString("AOR_CODE"));
                }
            }
            else {
                LOGGER.warn("Unable to obtain a connection to the target "
                        + "database.  An empty List will be returned to "
                        + "the caller.");
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve a list of AOR Codes from "
                    + "the target data source.  Error message [ "
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
                    + aors.size() 
                    + " ] AOR_CODES selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return aors;
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
                + TARGET_TABLE_NAME;
        
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
     * Get a list of all unique NSN/NRN combinations but return the 
     * results as a Map (as opposed to a list).  The key will be in 
     * the format calculated by <code>ProductUtils</code> and the 
     * value will be the actual <code>Product</code> object.
     * 
     * @return A list of products with a unique NSN/NRN combination.
     */
    public Map<String, Product> getUniqueProductsMap() {
    	
    	Map<String, Product> products    = new HashMap<String, Product>();
    	List<Product>        productList = getUniqueProducts();
    	long                 startTime   = System.currentTimeMillis();
    	
    	if ((productList != null) && (productList.size() > 0)) {
    		for (Product prod : productList) {
    			products.put(
    					ProductUtils.getInstance().getKey(prod), 
    					prod);
    		}
    	}
    	else {
    		LOGGER.warn("List of products retrieved from the backing data "
    				+ "store is null, or empty.  The return product map "
    				+ "will be empty.");
    	}
    	
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug("Unique product map containing [ "
    				+ products.size() 
    				+ " ] products created in [ "
    				+ (System.currentTimeMillis() - startTime)
    				+ " ].");
    	}
    	return products;
    }
    
    /**
     * The database team decided to store multiple records associated with 
     * each unique NSN/NRN combination.  The intent was to allow easier 
     * searching based on country and/or AOR.  The issue was the 15k or 
     * so unique records exploded into the millions.  This method was added
     * to get only the unique NSN/NRN combinations.
     * 
     * @return A list of products with a unique NSN/NRN combination.
     */
    public List<Product> getUniqueProducts() {
        
    	List<Product>       products       = new ArrayList<Product>();
    	Map<String, String> uniqueProducts = new HashMap<String, String>();
    	
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select distinct NSN, NRN from "
                + TARGET_TABLE_NAME;
        
        try { 
            if (getConnection() != null) {
            	
            	stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                // Load the map containing the unique products.
                while (rs.next()) {
                    uniqueProducts.put(
                    		rs.getString("NRN"), 
                    		rs.getString("NSN"));
                }
                
                if (LOGGER.isDebugEnabled()) {
                	LOGGER.debug("Loaded intermediate product map "
                			+ "containing [ "
                			+ uniqueProducts.size()
                			+ " ] products in [ "
                			+ (System.currentTimeMillis() - start)
                			+ " ] ms.");
                }
                
                // Now load the return product list.
                if (uniqueProducts.size() > 0) {
	                for (String nrn : uniqueProducts.keySet()) {
	                	List<Product> prods = getProducts(
	                			nrn, 
	                			uniqueProducts.get(nrn));
	                	if (prods.size() > 0) {
	                		products.add(prods.get(0));
	                	}
	                	else {
	                		LOGGER.warn("Unable to retrieve unique product "
	                				+ "with key (i.e. NRN) => [ "
	                				+ nrn
	                				+ " ], and value (i.e. NSN) => [ "
	                				+ uniqueProducts.get(nrn)
	                				+ " ].");
	                	}
	                }
	                if (LOGGER.isDebugEnabled()) {
	                	LOGGER.debug("Loaded [ "
	                			+ products.size()
	                			+ " ] unique products in [ "
	                			+ (System.currentTimeMillis() - start)
	                			+ " ] ms.");
	                }
                }
                else {
                	LOGGER.warn("Found 0 unique products.  Return product "
                			+ "list will be empty.");
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
        return products;
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
        
        if (rodConnection == null) {
            rodConnection = DriverManager.getConnection(
                    getConnectionString(),
                    getUser(),
                    getPassword());
        }
        return rodConnection;
    }
    
    
    /**
     * Get a list of countries from the back end data source.
     * 
     * @return The list of countries codes.
     */
    public List<String> getCountries() {

        List<String>      countries = new ArrayList<String>();
        PreparedStatement stmt      = null;
        ResultSet         rs        = null;
        long              start     = System.currentTimeMillis();
        String            sql       = "select distinct(COUNTRY_NAME) from "
                + TARGET_TABLE_NAME
                + " order by COUNTRY_NAME";
            
        try {
            if (getConnection() != null) {
                stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    countries.add(rs.getString("COUNTRY_NAME"));
                }
            }
            else {
                LOGGER.warn("Unable to obtain a connection to the target "
                        + "database.  An empty List will be returned to "
                        + "the caller.");
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve a list of COUNTRY_NAME "
                    + "from the target data source.  Error message [ "
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
                    + countries.size() 
                    + " ] COUNTRY_NAME selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return countries;
    }
    
    /**
     * Accessor method for the singleton instance of the 
     * ProductQueryResponseMarshaller class.
     * 
     * @return The singleton instance of the ProductQueryResponseMarshaller .
     * class.
     */
    public static ProductFactory getInstance() 
            throws PropertyNotFoundException, 
                PropertiesNotLoadedException, 
                ClassNotFoundException {
        return RoDRecordFactoryHolder.getSingleton();
    } 
    
    /**
     * Get a list of product types from the back end data source.
     * 
     * @return The list of unique product types in the back-end data store.
     */
    public List<String> getProductTypes() {
        
        List<String>      products = new ArrayList<String>();
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select distinct(PROD_TYPE) from "
                + TARGET_TABLE_NAME;
        
        try {
            if (getConnection() != null) {
                stmt = getConnection().prepareStatement(sql);
                rs   = stmt.executeQuery();
                while (rs.next()) {
                    products.add(rs.getString("PROD_TYPE"));
                }
            }
            else {
                LOGGER.warn("Unable to obtain a connection to the target "
                        + "database.  An empty List will be returned to "
                        + "the caller.");
            }
        }
        catch (SQLException se) {
            LOGGER.error("An unexpected SQLException was raised while "
                    + "attempting to retrieve a list of PROD_TYPE records "
                    + "from the target data source.  Error message [ "
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
                    + products.size() 
                    + " ] PROD_TYPE records selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return products;
    }
    
    /**
     * Close the database connection if open.
     */
    @Override
    public void close() {
        if (rodConnection != null) {
            LOGGER.info("Closing JDBC connection.");
            try { rodConnection.close(); } catch (Exception e) {}
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
    	sb.append(TARGET_TABLE_NAME);
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
        private static ProductFactory _instance = null;
    
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
        public static ProductFactory getSingleton() 
                throws PropertyNotFoundException, PropertiesNotLoadedException, ClassNotFoundException {
            if (_instance == null) {
                _instance = new ProductFactory();
            }
            return _instance;
        }
    }
}
