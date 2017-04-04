package mil.nga.rod.ejb;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.rod.model.DownloadRequest;
import mil.nga.rod.model.QueryRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class MetricsService
 */
@Stateless
@LocalBean
public class MetricsService implements Serializable {
	
	/**
	 * Eclipse-gnerated serialVersionUID
	 */
	private static final long serialVersionUID = -3736011660295335884L;

	/**
	 * Set up the logging system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			MetricsService.class);
	
	/**
	 * Table in which data on individual download requests will be stored.
	 */
	private static final String DOWNLOAD_REQUEST_TABLE = 
			"ROD_DOWNLOAD_REQUESTS";
	
	/**
	 * Table in which data on individual query requests will be stored.
	 */
	private static final String QUERY_REQUEST_TABLE = 
			"ROD_QUERY_REQUESTS";
	
	/**
	 * DateFormat class used to format data for logging purposes.
	 */
	private static final DateFormat df = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Container-injected datasource object.
	 */
	@Resource(name="jdbc/RODMetrics")
	DataSource datasource;
	
    /**
     * Default constructor. 
     */
    public MetricsService() { }
    
    /**
     * Persist the data associated with the download request.  Eat all 
     * exceptions here to ensure that program flow is not altered by 
     * issues tracking metrics information.
     * 
     * @param request The information associated with the download request.
     */
    @Asynchronous
    public void logDownloadRequest(DownloadRequest request) {
        	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "insert into "
				+ DOWNLOAD_REQUEST_TABLE 
				+ "(REQUEST_ID, PROD_TYPE, AOR_CODE, COUNTRY_NAME, NRN, NSN, "
				+ "DATE_REQUESTED, UNIX_PATH, FILE_SIZE, USERNAME, SOURCE, "
				+ "HOST_NAME) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		if (datasource != null) {
	    	if (request != null) {
	    		if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Persisting DownloadRequest object [ "
	    					+ request.toString()
	    					+ " ].");
	    		}
	    		
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				
    				stmt.setString( 1, request.getRequestId());
    				stmt.setString( 2, request.getProductType());
    				stmt.setString( 3, request.getAorCode());
    				stmt.setString( 4, request.getCountryName());
    				stmt.setString( 5, request.getNRN());
    				stmt.setString( 6, request.getNSN());
    				stmt.setDate(   7, request.getDateRequested());
    				stmt.setString( 8, request.getPath());
    				stmt.setLong(   9, request.getFileSize());
    				stmt.setString(10, request.getUserName());
    				stmt.setString(11, request.getSource());
    				stmt.setString(12, request.getHostName());
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised while "
    						+ "attempting to insert a new "
    						+ DOWNLOAD_REQUEST_TABLE
    						+ " object.  Error message [ "
    						+ se.getMessage() 
    						+ " ].");
    				se.printStackTrace();
    			}
    			finally {
    				try { 
    					if (stmt != null) { stmt.close(); } 
    				} catch (Exception e) {}
    				try { 
    					if (conn != null) { conn.close(); } 
    				} catch (Exception e) {}
    			}
	    	}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "Unable to persist the download request.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Insert of DownloadRequest object into table "
					+ DOWNLOAD_REQUEST_TABLE
					+ " completed in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
        }
    }
    
    /**
     * Persist the data associated with the query request.  Eat all 
     * exceptions here to ensure that program flow is not altered by 
     * issues tracking metrics information.
     * 
     * @param request The information associated with the query request.
     */
    @Asynchronous
    public void logQueryRequest(QueryRequest request) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "insert into "
				+ QUERY_REQUEST_TABLE 
				+ "(LOAD_DATE_REQUESTED, FILTER, DATE_REQUESTED, "
				+ "NUM_RESULTS, USERNAME, SOURCE, HOST_NAME) "
				+ "values (?, ?, ?, ?, ?, ?, ?)";
    	
		if (datasource != null) {
	    	if (request != null) {
	    		if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Persisting QueryRequest object [ "
	    					+ request.toString()
	    					+ " ].");
	    		}
	    		
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				
    				stmt.setDate(  1, request.getLoadDateRequested());
    				stmt.setString(2, request.getFilter());
    				stmt.setDate(  3, request.getDateRequested());
    				stmt.setLong(  4, request.getNumResults());
    				stmt.setString(5, request.getUserName());
    				stmt.setString(6, request.getSource());
    				stmt.setString(7, request.getHostName());
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised while "
    						+ "attempting to insert a new "
    						+ QUERY_REQUEST_TABLE
    						+ " object.  Error message [ "
    						+ se.getMessage() 
    						+ " ].");
    				se.printStackTrace();
    			}
    			finally {
    				try { 
    					if (stmt != null) { stmt.close(); } 
    				} catch (Exception e) {}
    				try { 
    					if (conn != null) { conn.close(); } 
    				} catch (Exception e) {}
    			}
	    	}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "Unable to persist the query request.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Insert of QueryRequest object into table "
					+ QUERY_REQUEST_TABLE
					+ " completed in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
        }
    }
    
    /**
     * Low-level JDBC method used by the metrics cleanup algorithm to delete 
     * download request records prior to the input date object.
     * 
     * @param date Delete all download request records with a DATE_REQUESTED 
     * prior to this date.
     */
    public void deleteDownloadRequestsPriorToDate(long date) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "delete from ROD_DOWNLOAD_REQUESTS "
				+ "where DATE_REQUESTED < ?";
		
		if (datasource != null) {
	    	if (date > 0) {
	    			
	    		if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Deleting ROD_DOWNLOAD_REQUESTS records "
	    					+ "prior to [ "
	    					+ df.format(new java.util.Date(date))
	    					+ " ].");
	    		}
	    		
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setLong(1, date);
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised "
    						+ "while attempting to delete "
    						+ "ROD_DOWNLOAD_REQUESTS records prior to [ "
    						
    						+ " ].  Error message [ "
    						+ se.getMessage() 
    						+ " ].");
    			}
    			finally {
    				try { 
    					if (stmt != null) { stmt.close(); } 
    				} catch (Exception e) {} 
    				try { 
    					if (conn != null) { conn.close(); } 
    				} catch (Exception e) {}
    			}
    		}
    		else {
    			LOGGER.warn("The input date is inconsistent.  Unable to "
    					+ "delete the target records.");
    		}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "Delete will not be performed.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ROD_DOWNLOAD_REQUESTS records deleted in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}	

    }
    
    /**
	 * This method will return a list of all 
	 * <code>mil.nga.rod.model.DownloadRequest</code> objects currently 
	 * persisted in the back-end data store.
	 * 
	 * @return All of the download requests.
     */
    public List<DownloadRequest> getDownloadRequests() {
    	
    	Connection            conn   = null;
		PreparedStatement     stmt   = null;
		ResultSet             rs     = null;
		List<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		long                  start  = System.currentTimeMillis();
		String                sql    = 	"select REQUEST_ID, PROD_TYPE, "
				+ "AOR_CODE, COUNTRY_NAME, NRN, NSN, DATE_REQUESTED, "
				+ "UNIX_PATH, FILE_SIZE, USERNAME, SOURCE, HOST_NAME "
				+ "from ROD_DOWNLOAD_REQUESTS order by DATE_REQUESTED desc";
    	
		if (datasource != null) {
    		try {
	    		
    			if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Selecting all ROD_DOWNLOAD_REQUESTS "
	    					+ "records.");
	    		}
	    		
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				rs   = stmt.executeQuery();
				
				while (rs.next()) {
					DownloadRequest request = new DownloadRequest
							.DownloadRequestBuilder()
							.requestId(rs.getString("REQUEST_ID"))
							.aorCode(rs.getString("AOR_CODE"))
							.countryName(rs.getString("COUNTRY_NAME"))
							.requestDate(rs.getDate("DATE_REQUESTED"))
							.fileSize(rs.getLong("FILE_SIZE"))
							.hostName(rs.getString("HOST_NAME"))
							.nrn(rs.getString("NRN"))
							.nsn(rs.getString("NSN"))
							.productType(rs.getString("PROD_TYPE"))
							.username(rs.getString("USERNAME"))
							.path(rs.getString("UNIX_PATH"))
							.source(rs.getString("SOURCE"))
							.build();
					requests.add(request);
				}
	        }
	        catch (SQLException se) {
				LOGGER.error("An unexpected SQLException was raised while "
						+ "attempting to retrieve a list of download requests " 
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
			LOGGER.debug("Retrieved [ "
					+ requests.size()
					+ " ] DownloadRequest objects from table "
					+ "ROD_DOWNLOAD_REQUESTS in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
        }
		
		return requests;
    }
    
    /**
	 * This method will return a list of all 
	 * <code>mil.nga.rod.model.DownloadRequest</code> objects currently 
	 * persisted in the back-end data store that have a DATE_REQUESTED that 
	 * fall between the input start and end time.
	 * 
	 * @param startTime The "from" parameter 
	 * @param endTime The "to" parameter
	 * @return All of the download requests with a start time that fall in 
	 * between the two input parameters.
     */
	public List<DownloadRequest> getDownloadRequestsByDate(
			long startTime, 
			long endTime) {
    	
		Connection            conn   = null;
		PreparedStatement     stmt   = null;
		ResultSet             rs     = null;
		List<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		long                  start  = System.currentTimeMillis();
		String                sql = "select REQUEST_ID, PROD_TYPE, AOR_CODE, "
				+ "COUNTRY_NAME, NRN, NSN, DATE_REQUESTED, UNIX_PATH, "
				+ "FILE_SIZE, USERNAME, SOURCE, HOST_NAME "
				+ "from ROD_DOWNLOAD_REQUESTS where DATE_REQUESTED > ? "
				+ "and DATE_REQUESTED < ? order by DATE_REQUESTED desc";
		
		if (datasource != null) {
			
			// Ensure the startTime is earlier than the endTime before submitting
	        // the query to the database.
	        if (startTime > endTime) {
	        		LOGGER.warn("The caller supplied a start time that falls "
	        				+ "after the end time.  Swapping start and end "
	        				+ "times.");
	                long temp = startTime;
	                startTime = endTime;
	                endTime = temp;
	        }
	        else if (startTime == endTime) {
	    		LOGGER.warn("The caller supplied the same time for both start "
	    				+ "and end time.  This method will likely yield a null "
	    				+ "job list.");
	        }
	        
	        try {
	        	
    			if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Selecting ROD_DOWNLOAD_REQUESTS "
	    					+ "records between [ "
	    					+ df.format(new java.util.Date(startTime))
	    					+ " ] and [ "
	    					+ df.format(new java.util.Date(endTime))
	    					+ " ].");
	    		}
    			
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				stmt.setLong(1, startTime);
				stmt.setLong(2, endTime);
				rs   = stmt.executeQuery();
				
				while (rs.next()) {
					DownloadRequest request = new DownloadRequest
							.DownloadRequestBuilder()
							.requestId(rs.getString("REQUEST_ID"))
							.aorCode(rs.getString("AOR_CODE"))
							.countryName(rs.getString("COUNTRY_NAME"))
							.requestDate(rs.getDate("DATE_REQUESTED"))
							.fileSize(rs.getLong("FILE_SIZE"))
							.hostName(rs.getString("HOST_NAME"))
							.nrn(rs.getString("NRN"))
							.nsn(rs.getString("NSN"))
							.productType(rs.getString("PROD_TYPE"))
							.username(rs.getString("USERNAME"))
							.path(rs.getString("UNIX_PATH"))
							.source(rs.getString("SOURCE"))
							.build();
					requests.add(request);
				}
	        }
	        catch (SQLException se) {
				LOGGER.error("An unexpected SQLException was raised while "
						+ "attempting to retrieve a list of download requests " 
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
			LOGGER.debug("Retrieved [ "
					+ requests.size()
					+ " ] DownloadRequest objects from table "
					+ "ROD_DOWNLOAD_REQUESTS in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
        }
		
		return requests;
	}

}
