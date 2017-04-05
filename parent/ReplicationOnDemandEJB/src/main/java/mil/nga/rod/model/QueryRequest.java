package mil.nga.rod.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Simple POJO used to hold information associated with an individual query 
 * request.  This POJO is for tracking metrics only.  Each query is logged.
 * 
 * @author L. Craig Carpenter
 */
public class QueryRequest implements Serializable {

    /* Sample command for creating the backing data table (Oracle):
     
        create table ROD_QUERY_REQUESTS (
            LOAD_DATE_REQUESTED DATE,
            FILTER         NVARCHAR2(50),
            DATE_REQUESTED TIMESTAMP,
            NUM_RESULTS    NUMBER,      
            USERNAME       VARCHAR2(240),
            SOURCE         VARCHAR2(240),
            HOST_NAME      VARCHAR2(240)
            CONSTRAINT REQUEST_ID_PK PRIMARY KEY (REQUEST_ID)
        );
    
    */
    
    /**
     * Eclipse-generated serialVersionUID.
     */
    private static final long serialVersionUID = -403708397446236025L;

    /** 
     * Format associated with request dates.
     */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd hh:mm:ss";
    
    /** 
     * Format associated with date the client wanted to query.
     */
    private static final String QUERY_DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    // Private internal members.
    private final Date   dateRequested;
    private final long   elapsedTime;
    private final String filter;
    private final String hostName;
    private final Date   loadDateRequested;
    private final long   numResults;
    private final String source;
    private final String username;

    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private QueryRequest(QueryRequestBuilder builder) {
        this.dateRequested     = builder.dateRequested;
        this.elapsedTime       = builder.elapsedTime;
        this.filter            = builder.filter;
        this.hostName          = builder.hostName;
        this.loadDateRequested = builder.loadDateRequested;
        this.numResults        = builder.numResults;
        this.source            = builder.source;
        this.username          = builder.username;
    }
    
    /**
     * Getter method for the date when the query was received.
     * @return The date when the query was received. 
     */
    public Date getDateRequested() {
        return dateRequested;
    }
    
    /**
     * Getter method for the date that the query was submitted.
     * @return The date the query was submitted.
     */
    public String getDateRequestedString() {
        return new SimpleDateFormat(DATE_FORMAT_STRING)
                .format(dateRequested);
    }
    
    /**
     * Getter method for the elapsed time (length of time required to 
     * process the query request).
     * @return The elapsed time.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }    
    
    /**
     * Getter method for the filter used to narrow down the results.
     * @return The filter used for narrowing down the results.
     */
    public String getFilter() {
        return filter;
    }
    
    /**
     * Getter method for the host name processing the request.
     * @return The host name processing the request.
     */
    public String getHostName() {
        return hostName;
    }
    
    /**
     * Getter method for the load date requested (i.e. the date used in the
     * query).
     * @return The load date requested.
     */
    public Date getLoadDateRequested() {
        return loadDateRequested;
    }
    
    /**
     * Getter method for the load date requested (i.e. the date used in the
     * query).
     * @return The load date requested (in String format).
     */
    public String getLoadDateRequestedString() {
        return new SimpleDateFormat(QUERY_DATE_FORMAT_STRING)
                .format(dateRequested);
    }
    
    /**
     * Getter method for the number of results returned to the caller.
     * @return The number of results returned to the caller.
     */
    public long getNumResults() {
        return numResults;
    }
    
    /**
     * Getter method for the IP or host name of the source requesting the
     * target ISO file.
     * @return The IP or host name of the source requesting the target ISO
     * file.
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Getter method for the user name who requested the target file.
     * @return The user name who requested the target file.
     */
    public String getUserName() {
        return username;
    }
    
    /**
     * Convert to human-readable format.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ISO Query Received : ");
        sb.append(" Date requested => [ ");
        sb.append(getDateRequestedString());
        sb.append(" ], Load Date Requested => [ ");
        sb.append(getLoadDateRequestedString());
        sb.append(" ], Filter => [ ");
        sb.append(getFilter());
        sb.append(" ], Elapsed Time => [ ");
        sb.append(getElapsedTime());
        sb.append(" ] ms, Num Results => [ ");
        sb.append(getNumResults());
        sb.append(" ], Host name => [ ");
        sb.append(getHostName());
        sb.append(" ], User name => [ ");
        sb.append(getUserName());
        sb.append(" ], Source IP => [ ");
        sb.append(getSource());
        sb.append(" ].");
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new QueryRequest objects.  
     * 
     * @author L. Craig Carpenter
     */
    public static class QueryRequestBuilder {
        
        private Date   dateRequested;
        private long   elapsedTime;
        private String filter;
        private String hostName;
        private Date   loadDateRequested;
        private long   numResults;
        private String source;
        private String username;
        
        /**
         * Method used to actually construct the QueryRequest object.
         * @return A constructed and validated QueryRequest object.
         */
        public QueryRequest build() throws IllegalStateException {
            QueryRequest object = new QueryRequest(this);
            validateQueryRequestObject(object);
            return object;
        }
        
        /**
         * Setter method for the request date attribute.
         * @param value The request date.
         */
        public QueryRequestBuilder dateRequested(Date value) {
            if (value == null) {
                dateRequested = new Date(0);
            }
            else {
                dateRequested = value;
            }
            return this;
        }
        
        /**
         * Setter method for the time required to complete the query.
         * @param value The time required to complete the query.
         */
        public QueryRequestBuilder elapsedTime(long value) {
            if (value >= 0) {
                elapsedTime = value;
            }
            else {
                elapsedTime = 0;
            }
            return this;
        }
        
        /**
         * Setter method for the FILTER attribute.
         * @param value The FILTER attribute.
         */
        public QueryRequestBuilder filter(String value) {
            if (value != null) {
                filter = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the host name processing the request.
         * @param value The host name handling the request.
         */
        public QueryRequestBuilder hostName(String value) {
            if (value != null) {
                hostName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the load date requested (query attribute).
         * @param value The load date requested (query attribute).
         */
        public QueryRequestBuilder loadDateRequested(Date value) {
            if (value == null) {
                loadDateRequested = new Date(0);
            }
            else {
                loadDateRequested = value;
            }
            return this;
        }
        
        /**
         * Setter method for the number of results returned to the caller.
         * @param value The number of results returned to the caller.
         */
        public QueryRequestBuilder numResults(long value) {
            if (value >= 0) { 
                numResults = value;
            }
            else {
                numResults = 0;
            }
            return this;
        }
        
        /**
         * Setter method for the IP or host name of the source requester.
         * @param value The IP or host name of the source requester.
         */
        public QueryRequestBuilder source(String value) {
            if (value != null) {
                source = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the user requesting the ISO file.
         * @param value The user requesting the ISO file.
         */
        public QueryRequestBuilder username(String value) {
            if (value != null) {
                username = value.trim();
            }
            return this;
        }
        
        /**
         * Validate internal member variables.
         * 
         * @param object The QueryRequest object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateQueryRequestObject(QueryRequest object) 
                throws IllegalStateException {
            
            if (object != null) {
                if (object.getElapsedTime() < 0) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequest object but the value for [ "
                            + "elapsedTime"
                            + " ] was out of range.  Value [ "
                            + object.getElapsedTime()
                            + " ].");
                }
                if (object.getNumResults() < 0) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequest object but the value for [ "
                            + "numResults"
                            + " ] was out of range.  Value [ "
                            + object.getNumResults()
                            + " ].");
                }
                if (object.getDateRequested() == null) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequest object but the value for [ "
                            + "dateRequested"
                            + " ] was null.");
                }
                if (object.getLoadDateRequested() == null) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequest object but the value for [ "
                            + "loadDateRequested"
                            + " ] was null.");
                }
            }
            else {
                throw new IllegalStateException("Construction of  "
                        + "QueryRequest object failed.  Object was null.");
            }
        }    
    }
}
