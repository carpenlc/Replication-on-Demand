package mil.nga.rod.ejb;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.rod.model.QueryRequestAccelerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ProductQueryRequestAcceleratorService
 * 
 * Class implemented to manage the database interface to the tables containing
 * the query acceleration data.
 */
@Stateless
@LocalBean
public class ProductQueryRequestAcceleratorService 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 7551756164388246093L;

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ProductQueryRequestAcceleratorService.class);
    
    /**
     * The target table to retrieve RoD data from.
     */
    private static final String TARGET_TABLE_NAME = 
            "ROD_QUERY_REQUEST_ACCELERATOR";
    
    /**
     * Container-injected datasource object.
     */
    @Resource(name="jdbc/ROD")
    DataSource datasource;
    
    /**
     * Eclipse-generated default constructor. 
     */
    public ProductQueryRequestAcceleratorService() { }

    /**
     * Retrieve the accelerator record associated with the input file path.
     * 
     * @param targetFile The target file path.
     * @return Method will return the requested record, or null if it was not
     * found.
     */
    public QueryRequestAccelerator getAcceleratorRecord(String targetFile) {
        
        Connection              conn        = null;
        QueryRequestAccelerator accelerator = null;
        PreparedStatement       stmt        = null;
        ResultSet               rs          = null;
        long                    start       = System.currentTimeMillis();
        String                  sql         = 
                "select FILE_DATE, FILE_SIZE, UNIX_PATH, HASH from "
                + TARGET_TABLE_NAME
                + " where UNIX_PATH = ? order by FILE_DATE desc";
        
        if (datasource != null) {
            if ((targetFile != null) && (!targetFile.isEmpty())) {
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, targetFile.trim());
                    rs   = stmt.executeQuery();
                    
                    while (rs.next()) {
                        try {
                            accelerator = new QueryRequestAccelerator
                                    .QueryRequestAcceleratorBuilder()
                                    .fileDate(rs.getDate("FILE_DATE"))
                                    .path(rs.getString("UNIX_PATH"))
                                    .size(rs.getLong("FILE_SIZE"))
                                    .hash(rs.getString("HASH"))
                                    .build();
                        }
                        catch (IllegalStateException ise) {
                            LOGGER.warn("Unexpected IllegalStateException raised "
                                    + "while loading [ "
                                    + TARGET_TABLE_NAME
                                    + " ] records from "
                                    + "data store.  Error encountered [ "
                                    + ise.getMessage()
                                    + " ].");
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
                LOGGER.error("The input target file path was null or an empty "
                        + "string.  Query will not be performed.");
            }
        }
        else {
            LOGGER.error("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ " 
                    + TARGET_TABLE_NAME
                    + " ] records selected for file path [ "
                    + targetFile 
                    + " ] in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return accelerator;
    }
    
    /**
     * Insert a new accelerator record.
     * 
     * @param accelerator The data to persist.
     */
    public void insertAcceleratorRecord(QueryRequestAccelerator accelerator) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "insert into "
                + TARGET_TABLE_NAME
                + "(FILE_DATE, FILE_SIZE, UNIX_PATH, HASH) values "
                + "(?, ?, ?, ?)";

        if (datasource != null) {
            if (accelerator != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Persisting QueryRequestAccelerator object [ "
                            + accelerator.toString()
                            + " ].");
                }
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    
                    stmt.setDate(   1, accelerator.getFileDate());
                    stmt.setLong(   2, accelerator.getSize());
                    stmt.setString( 3, accelerator.getPath());
                    stmt.setString( 4, accelerator.getHash());
                    stmt.executeUpdate();
                
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to insert a new "
                            + TARGET_TABLE_NAME
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
            LOGGER.debug("Insert of QueryRequestAccelerator object into table "
                    + TARGET_TABLE_NAME
                    + " completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
    /**
     * Update an existing accelerator record.
     * 
     * @param accelerator The data to persist.
     */
    public void updateAcceleratorRecord(QueryRequestAccelerator accelerator) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "update "
                + TARGET_TABLE_NAME
                + "set FILE_DATE = ?, FILE_SIZE = ?, HASH = ?"
                + "where UNIX_PATH = ?";

        if (datasource != null) {
            if (accelerator != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Persisting QueryRequestAccelerator object [ "
                            + accelerator.toString()
                            + " ].");
                }
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    
                    stmt.setDate(   1, accelerator.getFileDate());
                    stmt.setLong(   2, accelerator.getSize());
                    stmt.setString( 3, accelerator.getHash());
                    stmt.setString( 4, accelerator.getPath());
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to update an existing [ "
                            + TARGET_TABLE_NAME
                            + " ] object.  Error message [ "
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
            LOGGER.debug("Insert of QueryRequestAccelerator object into table "
                    + TARGET_TABLE_NAME
                    + " completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
}
