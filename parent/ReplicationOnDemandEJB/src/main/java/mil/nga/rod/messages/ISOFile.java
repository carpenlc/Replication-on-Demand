package mil.nga.rod.messages;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO used to hold the data associated with a single on-disk ISO file.
 * 
 * This object utilizes both JAX-B and Jackson annotations for 
 * marshalling/unmarshalling JSON data.  This particular object is built 
 * in code rather than retrieved from the database. 
 * 
 * @author L. Craig Carpenter
 */
public class ISOFile implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -4658786401641928793L;

    // Private internal members
    private final String aorCode;
    private final String countryName;
    private final Date   fileDate;
    private final String hash;
    private final Date   loadDate;
    private final String nrn;
    private final String nsn;
    private final String productType;
    private final long   size;
    private final String url;

    /** 
     * Format associated with dates of the actual on-disk file.
     */
    private static final String FILE_DATE_FORMAT_STRING = "yyyy-MM-dd hh:mm:ss";
            
    /** 
     * Format associated with dates of the load date from the target database.
     * NOTE: The DATE stored in the database does not contain 
     * the time component.  Remove it in the formatter String.
     */
    private static final String LOAD_DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private ISOFile(ISOFileBuilder builder) {
        this.aorCode     = builder.aorCode;
        this.countryName = builder.countryName;
        this.fileDate    = builder.fileDate;
        this.hash        = builder.hash;
        this.loadDate    = builder.loadDate;
        this.nrn         = builder.nrn;
        this.nsn         = builder.nsn;
        this.productType = builder.productType;
        this.size        = builder.size;
        this.url         = builder.url;
    }
    
    /**
     * Getter method for the AOR code attribute.
     * @return The AOR code attribute.
     */
    @XmlElement(name="aor")
    @JsonProperty(value="aor")
    public String getAorCode() {
        return aorCode;
    }    
    
    /**
     * Getter method for the country name.
     * @return The country name.
     */
    @XmlElement(name="country")
    @JsonProperty(value="country")
    public String getCountryName() {
        return countryName;
    }
    
    /**
     * Getter method for the size of the ISO file requested.
     * @return The size of the file requested.
     */
    @XmlElement(name="size")
    @JsonProperty(value="size")
    public long getSize() {
        return size;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    @JsonIgnore
    public Date getFileDate() {
        return fileDate;
    }
    
    /**
     * Getter method for the file date.
     * @return The file date.
     */
    @XmlElement(name="file_date")
    @JsonProperty(value="file_date")
    public String getFileDateString() {
        return (new SimpleDateFormat(FILE_DATE_FORMAT_STRING))
                .format(fileDate);
    }
    /**
     * Getter method for the MD5 hash of the target file.
     * @return The MD5 hash of the target file.
     */
    @XmlElement(name="hash")
    @JsonProperty(value="hash")
    public String getHash() {
        return hash;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    @JsonIgnore
    public Date getLoadDate() {
        return loadDate;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    @XmlElement(name="load_date")
    @JsonProperty(value="load_date")
    public String getLoadDateString() {
        return (new SimpleDateFormat(LOAD_DATE_FORMAT_STRING))
                .format(loadDate);
    }
    
    /**
     * Getter method for the NRN number.
     * @return The NRN number.
     */
    @XmlElement(name="nrn")
    @JsonProperty(value="nrn")
    public String getNRN() {
        return nrn;
    }
    
    /**
     * Getter method for the NSN number.
     * @return The NSN number.
     */
    @XmlElement(name="nsn")
    @JsonProperty(value="nsn")
    public String getNSN() {
        return nsn;
    }
    
    /**
     * Getter method for the product type.
     * @return The product type.
     */
    @XmlElement(name="product_type")
    @JsonProperty(value="product_type")
    public String getProductType() {
        return productType;
    }
    
    /**
     * Getter method for the URL attribute.
     * @return The URL attribute.
     */
    @XmlElement(name="url")
    @JsonProperty(value="url")
    public String getURL() {
        return url;
    }
    
    /**
     * Convert to a human readable String.
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append(newLine);
        sb.append("AOR => [ ");
        sb.append(getAorCode());
        sb.append(" ], Country Name => [ ");
        sb.append(getCountryName());
        sb.append(" ], NSN => [ ");
        sb.append(getNSN());
        sb.append(" ], NRN => [ ");
        sb.append(getNRN());
        sb.append(" ], Size => [ ");
        sb.append(getSize());
        sb.append(" ], URL => [ ");
        sb.append(getURL());
        sb.append(" ], Load Date => [ ");
        sb.append(getLoadDateString());
        sb.append(" ], File Date => [ ");
        sb.append(getFileDateString());
        sb.append(" ], Hash => [ ");
        sb.append(getHash());
        sb.append(" ].");
        sb.append(newLine);
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new ISOFile objects.  
     * 
     * @author L. Craig Carpenter
     */
    public static class ISOFileBuilder {
        
        // Private internal members
        private String aorCode;
        private String countryName;
        private Date   fileDate;
        private String hash;
        private Date   loadDate;
        private String nrn;
        private String nsn;
        private String productType;
        private long   size;
        private String url;
        
        /**
         * Method used to actually construct the UPGData object.
         * @return A constructed and validated UPGData object.
         */
        public ISOFile build() throws IllegalStateException {
            ISOFile object = new ISOFile(this);
            validateISOFileObject(object);
            return object;
        }
        
        /**
         * Setter method for the AOR code attribute.
         * @param value The AOR code attribute.
         */
        public ISOFileBuilder aorCode(String value) {
            if (value != null) {
                aorCode = value.trim().toUpperCase();
            }
            return this;
        }
        
        /**
         * Setter method for the country name attribute.
         * @param value The country name attribute.
         */
        public ISOFileBuilder countryName(String value) {
            if (value != null) {
                countryName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the size (in bytes) of the target file.
         * @param value The size of the target file.
         */
        public ISOFileBuilder size(long value) {
            size = value;
            return this;
        }
        
        /**
         * Setter method for the product load date attribute.
         * @param value The product load date attribute.
         */
        public ISOFileBuilder fileDate(Date value) {
            fileDate = value;
            return this;
        }
        
        /**
         * Setter method for the MD5 hash of the target file.
         * @param value The MD5 hash of the target file.
         */
        public ISOFileBuilder hash(String value) {
            if (value != null) {
                hash = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the product load date attribute.
         * @param value The product load date attribute.
         */
        public ISOFileBuilder loadDate(Date value) {
            loadDate = value;
            return this;
        }
        
        /**
         * Setter method for the NRN attribute.
         * @param value The NRN attribute.
         */
        public ISOFileBuilder nrn(String value) {
            if (value != null) {
                nrn = value.trim().toUpperCase();
            }
            return this;
        }
        
        /**
         * Setter method for the NSN attribute.
         * @param value The NSN attribute.
         */
        public ISOFileBuilder nsn(String value) {
            if (value != null) {
                nsn = value.trim().toUpperCase();
            }
            return this;
        }
        
        /**
         * Setter method for the PROD_TYPE attribute.
         * @param value The PROD_TYPE attribute.
         */
        public ISOFileBuilder productType(String value) {
            if (value != null) {
                productType = value.trim().toUpperCase();
            }
            return this;
        }
        
        /**
         * Setter method for the HYPERLINK_URL attribute.
         * @param value The HYPERLINK_URL attribute.
         */
        public ISOFileBuilder url(String value) {
            if (value != null) {
                url = value.trim();
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
        private void validateISOFileObject(ISOFile object) 
                throws IllegalStateException {
            
            if (object != null) {
                if (object.getLoadDate() == null) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for LOAD_DATE "
                            + "was null.");
                }
                
                if (object.getFileDate() == null) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for FILE_DATE "
                            + "was null.");
                }
                
                if ((object.getAorCode() == null) || 
                        (object.getAorCode().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for "
                            + "AOR code was null.");
                }
                
                if ((object.getCountryName() == null) || 
                        (object.getCountryName().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for COUNTRY_NAME "
                            + "was null.");
                }
                
                if ((object.getHash() == null) || 
                        (object.getHash().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for the MD5 hash "
                            + "was null.");
                }
                
                if ((object.getNSN() == null) || (object.getNSN().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for NSN "
                            + "was null.  ISOFile object => [ "
                            + object.toString()
                            + " ].");
                }
                
                if ((object.getNRN() == null) || (object.getNRN().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for NRN "
                            + "was null.   ISOFile object => [ "
                            + object.toString()
                            + " ].");
                }
                
                if ((object.getURL() == null) || (object.getURL().isEmpty())) {
                    throw new IllegalStateException("Attempted to build "
                            + "ISOFile object but the value for URL "
                            + "was null.");
                }
            }
            else {
                throw new IllegalStateException("Construction of  "
                        + "ISOFile object failed.  Object "
                        + "was null.");
            }
        }
    }
}
