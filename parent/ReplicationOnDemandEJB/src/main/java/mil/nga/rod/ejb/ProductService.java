package mil.nga.rod.ejb;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.rod.model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ProductService
 * 
 * This session bean encapsulates all of the methods that interface with the 
 * back-end data store. 
 */
@Stateless
@LocalBean
public class ProductService implements Serializable {
	// Note to self: if you want to access this from the web tier you must
	// implement the serializable interface.

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -7096034186291935914L;

	/**
	 * Set up the logging system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			ProductService.class);
	
	/**
	 * The target table to retrieve RoD data from.
	 */
	private static final String TARGET_TABLE_NAME = 
			"GW_PUB.ISO_ROD_CC_AOR_PUB";
	
	/**
	 * Container-injected datasource object.
	 */
	@Resource(name="jdbc/ROD")
	DataSource datasource;
	
    /**
     * Default Eclipse-generated constructor. 
     */
    public ProductService() { }

    /**
     * Get a list of AOR codes from the back end data source.
     * 
     * @return The list of AOR codes.
     */
    public List<String> getAORCodes() {
    	
    	Connection        conn   = null;
		List<String>      aors   = new ArrayList<String>();
		PreparedStatement stmt   = null;
		ResultSet         rs     = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "select distinct(AOR_CODE) from "
				+ TARGET_TABLE_NAME;
		
		if (datasource != null) {
			
			try {
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				rs   = stmt.executeQuery();
				while (rs.next()) {
					aors.add(rs.getString("AOR_CODE"));
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
				try { 
					if (conn != null) { conn.close(); } 
				} catch (Exception e) {}
			}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
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
     * Get a list of countries from the back end data source.
     * 
     * @return The list of countries codes.
     */
    public List<String> getCountries() {
    	
    	Connection        conn      = null;
		List<String>      countries = new ArrayList<String>();
		PreparedStatement stmt      = null;
		ResultSet         rs        = null;
		long              start     = System.currentTimeMillis();
		String            sql       = "select distinct(COUNTRY_NAME) from "
				+ TARGET_TABLE_NAME
				+ " order by COUNTRY_NAME";
		
		if (datasource != null) {
			
			try {
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				rs   = stmt.executeQuery();
				while (rs.next()) {
					countries.add(rs.getString("COUNTRY_NAME"));
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
				try { 
					if (conn != null) { conn.close(); } 
				} catch (Exception e) {}
			}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
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
     * Get a list of all of the product records in the back-end data store that 
     * match the input Area of Interest (AOR).
     * 
     * @param aor The target area of interest.
     * @return A list of all Products in the back-end data store that match 
     * the input AOR.
     */
    public List<Product> getProductsByAOR(String aor) {
    	
    	Connection        conn     = null;
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
				+ " where upper(AOR_CODE)=? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((aor != null) && (!aor.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, aor.toUpperCase());
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
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(
											rs.getString("REL_DESC"))
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected for AOR [ "
					+ aor 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * Get a list of all of the product records in the back-end data store that 
     * match the input country name.
     * 
     * @param country The country name.
     * @return A list of all Products in the back-end data store that match 
     * the input country name.
     */
    public List<Product> getProductsByCountry(String country) {
    	
    	Connection        conn     = null;
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
				+ " where upper(COUNTRY_NAME) = ? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((country != null) && (!country.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, country.toUpperCase());
					rs   = stmt.executeQuery();
					
					while (rs.next()) {
						try {
							Product product = new Product.ProductBuilder()
									.aorCode(rs.getString("AOR_CODE"))
									.classification(rs.getString("SEC_CLASS"))
									.classificationDescription(rs.getString("CLASS_DESC"))
									.countryName(rs.getString("COUNTRY_NAME"))
									.edition(rs.getLong("EDITION"))
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(rs.getString("REL_DESC"))
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected for country [ "
					+ country 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * This method will search the RoD holdings for any products loaded 
     * after the input date (in long format).
     * 
     * @param date The date/time in long format.
     * @return All products with a load date after the input date/time.
     */
    public List<Product> getProductsByDate(Date date) {
    	
    	Connection        conn     = null;
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
				+ " where FILE_DATE >= ? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			try { 
				
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				stmt.setDate(1, date);
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
								.iso3char(rs.getString("ISO3CHR"))
								.loadDate(rs.getDate("LOAD_DATE"))
								.fileDate(rs.getDate("FILE_DATE"))
								.mediaName(rs.getString("MEDIA_NAME"))
								.notes(rs.getString("ALL_NOTES"))
								.nsn(rs.getString("NSN"))
								.nrn(rs.getString("NRN"))
								.path(rs.getString("UNIX_PATH"))
								.productType(rs.getString("PROD_TYPE"))
								.size(rs.getLong("PRODUCT_SIZE_BYTES"))
								.releasability(rs.getString("SEC_REL"))
								.releasabilityDescription(
										rs.getString("REL_DESC"))
								.url(rs.getString("HYPERLINK_URL"))
								.build();
						products.add(product);
					}
					catch (IllegalStateException ise) {
						LOGGER.warn("Unexpected IllegalStateException raised "
								+ "while loading [ "
								+ TARGET_TABLE_NAME
								+ " ] records from the "
								+ "data store.  Error encountered [ "
								+ ise.getMessage()
								+ " ].");
						counter++;
					}
				}
			}
			catch (SQLException se) {
				LOGGER.error("An unexpected SQLException was raised while "
						+ "attempting to retrieve [ "
						+ TARGET_TABLE_NAME
						+ " ] records from the target data source by date.  "
						+ "Error message [ "
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
				try { 
					if (conn != null) { conn.close(); } 
				} catch (Exception e) {}
			}
			
	    }
		else {
	    	LOGGER.error("DataSource object not injected by the container.  "
	    			+ "An empty List will be returned to the caller.");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected after [ "
					+ date 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
    	return products;
    }
    
    /**
     * Get a list of all of the product records in the back-end data store that 
     * match the input Area of Interest (AOR).
     * 
     * @param aor The target area of interest.
     * @return A list of all Products in the back-end data store that match 
     * the input AOR.
     */
    public List<Product> getProductsByDateAndAOR(String aor, Date date) {
    	
    	Connection        conn     = null;
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
				+ " where upper(AOR_CODE) = ? "
				+ "and FILE_DATE >= ? order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((aor != null) && (!aor.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, aor.toUpperCase());
					stmt.setDate(  2, date);
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
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(
											rs.getString("REL_DESC"))
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected for AOR [ "
					+ aor 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * Get a list of all of the product records in the back-end data store that 
     * match the input country name that were loaded 
     * 
     * @param country The country name.
     * @return A list of all Products in the back-end data store that match 
     * the input country name.
     */
    public List<Product> getProductsByDateAndCountry(
    		String country, 
    		Date date) {
    	
    	Connection        conn     = null;
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
				+ " where upper(COUNTRY_NAME) like ? "
				+ "AND FILE_DATE >= ? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((country != null) && (!country.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, country.toUpperCase());
					stmt.setDate(  2, date);
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
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(
											rs.getString("REL_DESC"))
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected for country [ "
					+ country 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * Get a list of all of the product records in the back-end data store that 
     * match the input product type.
     * 
     * @param type The product type.
     * @return A list of all Products in the back-end data store.
     */
    public List<Product> getProductsByDateAndType(String type, Date date) {
    	
    	Connection        conn     = null;
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
				+ " where upper(PROD_TYPE) = ? AND "
				+ "FILE_DATE >= ? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((type != null) && (!type.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, type.toUpperCase());
					stmt.setDate(  2, date);
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
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(
											rs.getString("REL_DESC"))
									.url(rs.getString("HYPERLINK_URL"))
									.build();
							products.add(product);
						}
						catch (IllegalStateException ise) {
							LOGGER.warn("Unexpected IllegalStateException "
									+ "raised while loading [ "
									+ TARGET_TABLE_NAME
									+ " ] records from "
									+ "data store.  Error encountered [ "
									+ ise.getMessage()
									+ " ].");
							counter++;
						}
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] " 
					+ TARGET_TABLE_NAME
					+ " records selected for product type [ "
					+ type 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * Get a list of all of the product records in the back-end data store that 
     * match the input product type.
     * 
     * @param type The product type.
     * @return A list of all Products in the back-end data store.
     */
    public List<Product> getProductsByType(String type) {
    	
    	Connection        conn     = null;
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
				+ " where upper(PROD_TYPE) = ? "
				+ "order by FILE_DATE desc";
		
		if (datasource != null) {
			if ((type != null) && (!type.isEmpty())) {
				try { 
					
					conn = datasource.getConnection();
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, type.toUpperCase());
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
									.iso3char(rs.getString("ISO3CHR"))
									.loadDate(rs.getDate("LOAD_DATE"))
									.fileDate(rs.getDate("FILE_DATE"))
									.mediaName(rs.getString("MEDIA_NAME"))
									.notes(rs.getString("ALL_NOTES"))
									.nsn(rs.getString("NSN"))
									.nrn(rs.getString("NRN"))
									.path(rs.getString("UNIX_PATH"))
									.productType(rs.getString("PROD_TYPE"))
									.size(rs.getLong("PRODUCT_SIZE_BYTES"))
									.releasability(rs.getString("SEC_REL"))
									.releasabilityDescription(
											rs.getString("REL_DESC"))
									.url(rs.getString("HYPERLINK_URL"))
									.build();
							products.add(product);
						}
						catch (IllegalStateException ise) {
							LOGGER.warn("Unexpected IllegalStateException "
									+ "raise while loading [ "
									+ TARGET_TABLE_NAME
									+ " ] records from data store.  Error "
									+ "encountered [ "
									+ ise.getMessage()
									+ " ].");
							counter++;
						}
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
					try { 
						if (conn != null) { conn.close(); } 
					} catch (Exception e) {}
				}
			}
			else {
				LOGGER.error("The input product type was null or an empty "
						+ "string.  Query will not be performed.");
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[ " 
					+ products.size()
					+ " ] records selected for product type [ "
					+ type 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.  Of the records selected [ "
					+ counter
					+ " ] contained data errors.");
		}
		return products;
    }
    
    /**
     * Get a list of product types from the back end data source.
     * 
     * @return The list of unique product types in the back-end data store.
     */
    public List<String> getProductTypes() {
    	
    	Connection        conn     = null;
		List<String>      products = new ArrayList<String>();
		PreparedStatement stmt     = null;
		ResultSet         rs       = null;
		long              start    = System.currentTimeMillis();
		String            sql      = "select distinct(PROD_TYPE) from "
				+ TARGET_TABLE_NAME;
		
		if (datasource != null) {
			
			try {
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				rs   = stmt.executeQuery();
				while (rs.next()) {
					products.add(rs.getString("PROD_TYPE"));
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
				try { 
					if (conn != null) { conn.close(); } 
				} catch (Exception e) {}
			}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
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
     * Get a list of all of the product records in the back-end data store.
     * 
     * @return A list of all Products in the back-end data store.
     */
    public List<Product> getAllProducts() {
    	
    	Connection        conn     = null;
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
		
		if (datasource != null) {
			
			try { 
				
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
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
								.iso3char(rs.getString("ISO3CHR"))
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
				try { 
					if (conn != null) { conn.close(); } 
				} catch (Exception e) {}
			}
		}
		else {
        	LOGGER.error("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
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
}
