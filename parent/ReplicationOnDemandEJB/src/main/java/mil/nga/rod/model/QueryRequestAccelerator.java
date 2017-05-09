package mil.nga.rod.model;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Object used to pre-compute information associated with on-disk files in 
 * in order to speed up execution time when a query is initiated.  We found 
 * that due to the size of the target ISO files, computing file hashes 
 * could take as long as 5 seconds per file.  It doesn't take many files 
 * before we start running into timeouts. 
 *  
 * @author L. Craig Carpenter
 */
@JsonDeserialize(builder = QueryRequestAccelerator.QueryRequestAcceleratorBuilder.class)
public class QueryRequestAccelerator implements Serializable {

    /* Sample command for creating the backing data table (Oracle):
     
    create table ROD_QUERY_REQUEST_ACCELERATOR (
        FILE_DATE      TIMESTAMP,
        FILE_SIZE      NUMBER,      
        UNIX_PATH      VARCHAR2(200),
        HASH           VARCHAR2(200),
        CONSTRAINT UNIX_PATH_PK PRIMARY KEY (UNIX_PATH)
    );

*/
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 1426696426746319654L;
    
    /** 
     * Format associated with dates of the actual on-disk file.
     */
    private static final String FILE_DATE_FORMAT_STRING = 
            "yyyy-MM-dd hh:mm:ss";
    
    // Private internal members.
    private final Date   fileDate;
    private final String hash;
    private final long   size;
    private final String path;
    
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private QueryRequestAccelerator(QueryRequestAcceleratorBuilder builder) {
        this.fileDate = builder.fileDate;
        this.hash     = builder.hash;
        this.size     = builder.size;
        this.path     = builder.path;
    }
    
    /**
     * Getter method for the size of the ISO file requested.
     * @return The size of the file requested.
     */
    public long getSize() {
        return size;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    public Date getFileDate() {
        return fileDate;
    }

    /**
     * Getter method for the MD5 hash of the target file.
     * @return The MD5 hash of the target file.
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Getter method for the file path attribute.
     * @return The file path attribute.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Convert to a human readable String.
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append(newLine);
        sb.append("File => [ ");
        sb.append(getPath());
        sb.append(" ], Size => [ ");
        sb.append(getSize());
        sb.append(" ], Hash => [ ");
        sb.append(getHash());
        sb.append(" ], File Date => [ ");
        sb.append((new SimpleDateFormat(FILE_DATE_FORMAT_STRING))
                .format(getFileDate()));
        sb.append(" ].");
        sb.append(newLine);
        
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new QueryRequestAccelerator objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class QueryRequestAcceleratorBuilder {
        
        // Private internal members.
        private Date   fileDate;
        private String hash;
        private long   size;
        private String path;
        
        /**
         * Method used to actually construct the
         * QueryRequestAccelerator object.
         * @return A constructed and validated 
         * QueryRequestAccelerator object.
         */
        public QueryRequestAccelerator build() 
                throws IllegalStateException {
            
            QueryRequestAccelerator object = 
                    new QueryRequestAccelerator(this);
            validateQueryRequestAcceleratorObject(object);
            return object;
        }
        
        /**
         * Setter method for the size (in bytes) of the target file.
         * @param value The size of the target file.
         */
        public QueryRequestAcceleratorBuilder size(long value) {
            size = value;
            return this;
        }
        
        /**
         * Setter method for the product load date attribute.
         * @param value The product load date attribute.
         */
        public QueryRequestAcceleratorBuilder fileDate(Date value) {
            fileDate = value;
            return this;
        }
        
        /**
         * Setter method for the MD5 hash of the target file.
         * @param value The MD5 hash of the target file.
         */
        public QueryRequestAcceleratorBuilder hash(String value) {
            if (value != null) {
                hash = value;
            }
            return this;
        }
        
        /**
         * Setter method for the HYPERLINK_URL attribute.
         * @param value The HYPERLINK_URL attribute.
         */
        public QueryRequestAcceleratorBuilder path(String value) {
            if (value != null) {
                path = value;
            }
            return this;
        }
        
        /**
         * Validate internal member variables.
         * 
         * @param object The Product object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateQueryRequestAcceleratorObject(
                QueryRequestAccelerator object) 
                    throws IllegalStateException {
            
            if (object != null) {
            
                if (object.getFileDate() == null) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequestAccelerator object but the value for "
                            + "FILE_DATE was null.");
                }
            
                if (object.getSize() < 0) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequestAccelerator object but the value for "
                            + "file size was null.");
                }
            
                if ((object.getHash() == null) || 
                        (object.getHash().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequestAccelerator object but the value "
                            + "for the MD5 hash "
                            + "was null.");
                }
            
                if ((object.getPath() == null) || 
                        (object.getPath().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "QueryRequestAccelerator object but the value "
                            + "for FILE NAME was null or not supplied.");
                }
            }
            else {
                throw new IllegalStateException("Construction of "
                        + "QueryRequestAccelerator object failed.  Object "
                        + "was null.");
            }
        }
    }
}
