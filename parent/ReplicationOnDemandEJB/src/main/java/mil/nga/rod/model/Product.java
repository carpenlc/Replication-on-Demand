package mil.nga.rod.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Simple POJO containing the data associated with a single Product record
 * associated with the "Replication on Demand" project.
 * 
 * @author L. Craig Carpenter
 */
public class Product implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 7695475766892377682L;

    /**
     * As of this writing, not all of the date fields are actually populated.
     * Use a 
     */
    private static final String DEFAULT_DATE_STRING = "unavailable";
    
    /** 
     * Format associated with dates incoming from the target UPG data source.
     * NOTE: It doesn't appear that the DATE stored in the database contains 
     * the time component.  Remove it in the formatter String.
     */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    // Private internal members
    private final String aorCode;
    private final String classification;
    private final String classificationDescription;
    private final String countryName;
    private final long   edition;
    private final Date   fileDate;
    private final String iso3char;
    private final Date   loadDate;
    private final String mediaName;
    private final String notes;
    private final String nrn;
    private final String nsn;
    private final String path;
    private final String productType;
    private final String releasability;
    private final String releasabilityDescription;
    private final long   size;
    private final String url;
    
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private Product(ProductBuilder builder) {
        this.aorCode                   = builder.aorCode;
        this.classification            = builder.classification;
        this.classificationDescription = builder.classificationDescription;
        this.countryName               = builder.countryName;
        this.edition                   = builder.edition;
        this.fileDate                  = builder.fileDate;
        this.iso3char                  = builder.iso3char;
        this.loadDate                  = builder.loadDate;
        this.mediaName                 = builder.mediaName;
        this.notes                     = builder.notes;
        this.nrn                       = builder.nrn;
        this.nsn                       = builder.nsn;
        this.path                      = builder.path;
        this.productType               = builder.productType;
        this.releasability             = builder.releasability;
        this.releasabilityDescription  = builder.releasabilityDescription;
        this.size                      = builder.size;
        this.url                       = builder.url;
    }
    
    /**
     * Getter method for the AOR code attribute.
     * @return The AOR code attribute.
     */
    public String getAorCode() {
        return aorCode;
    }
    
    /**
     * Getter method for the classification string (abbreviation).
     * @return The abbreviated classification string.
     */
    public String getClassification() {
        return classification;
    }
    
    /**
     * Getter method for the classification string (description).
     * @return The classification string.
     */
    public String getClassificationDescription() {
        return classificationDescription;
    }    
    
    /**
     * Getter method for the country name.
     * @return The country name.
     */
    public String getCountryName() {
        return countryName;
    }
    
    /**
     * Getter method for the edition number of the product.
     * @return The edition number of the product.
     */
    public long getEdition() {
        return edition;
    }
    
    /**
     * Getter method for the file date.
     * @return The file date.
     */
    public Date getFileDate() {
        return fileDate;
    }
    /**
     * Getter method for the file date.
     * @return The file date.
     */
    public String getFileDateString() {
        String date = DEFAULT_DATE_STRING;
        if (fileDate != null) {
            date = (new SimpleDateFormat(DATE_FORMAT_STRING)).format(fileDate);
        }
        return date;
    }
    
    /**
     * Getter method for the ISO 3 character code.
     * @return The ISO 3 character code.
     */
    public String getIso3Char() {
        return iso3char;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    public Date getLoadDate() {
        return loadDate;
    }
    
    /**
     * Getter method for the load date.
     * @return The load date.
     */
    public String getLoadDateString() {
        String date = DEFAULT_DATE_STRING;
        if (fileDate != null) {
            date = (new SimpleDateFormat(DATE_FORMAT_STRING)).format(fileDate);
        }
        return date;
    }
    
    /**
     * Getter method for the name of the media.
     * @return The name of the media.
     */
    public String getMediaName() {
        return mediaName;
    }
    
    /**
     * Getter method for the notes associated with the product.
     * @return The notes associated with the product.
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Getter method for the NRN number.
     * @return The NRN number.
     */
    public String getNRN() {
        return nrn;
    }
    
    /**
     * Getter method for the NSN number.
     * @return The NSN number.
     */
    public String getNSN() {
        return nsn;
    }
    
    /**
     * Getter method for the on-disk path of the ISO image.
     * @return The on-disk path of the ISO image.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Getter method for the product type.
     * @return The product type.
     */
    public String getProductType() {
        return productType;
    }
    
    /**
     * Getter method for the abbreviated releasability code of the product.
     * @return The abbreviated releasability code of the product.
     */
    public String getReleasability() {
        return releasability;
    }
    
    /**
     * Getter method for the releasability of the product.
     * @return The releasability of the product.
     */
    public String getReleasabilityDescription() {
        return releasabilityDescription;
    }
    
    /**
     * Getter method for the size of the on-disk ISO file.
     * @return The size (in bytes) of the on-disk ISO file.
     */
    public long getSize() {
        return size;
    }
    
    /**
     * Getter method for the URL attribute.
     * @return The URL attribute.
     */
    public String getURL() {
        return url;
    }
    
    /**
     * Overriden method used to calculate equivalency based on both the 
     * NSN and NRN values.
     */
    @Override
    public boolean equals(Object product) {
        boolean equals = false;
        if (product instanceof Product) {
            if ((getNSN().equalsIgnoreCase(((Product)product).getNSN())) &&
                    (getNRN().equalsIgnoreCase(((Product)product).getNRN()))) {
                equals = true;
            }
        }
        return equals;
    }
    
    /**
     * Construct the hash code based on a combination of the NRN and NSN.
     * @return The computed hash code of the Product object.
     */
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNRN());
        sb.append("-");
        sb.append(getNSN());
        return sb.toString().hashCode();
    }
    
    /**
     * Convert to human-readable format.
     */
    @Override
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
        sb.append(" ], Edition => [ ");
        sb.append(getEdition());
        sb.append(" ], ISO => [ ");
        sb.append(getIso3Char());
        sb.append(" ], Media Name => [ ");
        sb.append(getMediaName());
        sb.append(" ], Load Date => [ ");
        sb.append(newLine);
        sb.append(" ].");
        
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new Product objects.  
     * 
     * @author L. Craig Carpenter
     */
    public static class ProductBuilder {
        
        private String aorCode;
        private String classification;
        private String classificationDescription;
        private String countryName;
        private long   edition;
        private Date   fileDate;
        private String iso3char;
        private Date   loadDate;
        private String mediaName;
        private String notes;
        private String nrn;
        private String nsn;
        private String path;
        private String productType;
        private String releasability;
        private String releasabilityDescription;
        private long   size;
        private String url;
        
        /**
         * Method used to actually construct the UPGData object.
         * @return A constructed and validated UPGData object.
         */
        public Product build() throws IllegalStateException {
            Product object = new Product(this);
            validateProductObject(object);
            return object;
        }
        
        /**
         * Setter method for the AOR_CODE attribute.
         * @param value The AOR_CODE attribute.
         */
        public ProductBuilder aorCode(String value) {
            if (value != null) {
                aorCode = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the SEC_CLASS attribute.
         * @param value The SEC_CLASS attribute.
         */
        public ProductBuilder classification(String value) {
            if (value != null) {
                classification = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the CLASS_DESC attribute.
         * @param value The CLASS_DESC attribute.
         */
        public ProductBuilder classificationDescription(String value) {
            if (value != null) {
                classificationDescription = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the COUNTRY_NAME attribute.
         * @param value The COUNTRY_NAME attribute.
         */
        public ProductBuilder countryName(String value) {
            if (value != null) {
                countryName = value.trim();
            }
            return this;
        }

        /**
         * Setter method for the EDITION attribute.
         * @param value The EDITION attribute.
         */
        public ProductBuilder edition(long value) {
            edition = value;
            return this;
        }
        
        /**
         * Setter method for the FILE_DATE attribute.
         * @param value The FILE_DATE attribute.
         */
        public ProductBuilder fileDate(Date value) {
            if (value == null) {
                fileDate = new Date(0);
            }
            else {
                fileDate = value;
            }
            return this;
        }
        
        /**
         * Setter method for the ISO3CHR attribute.
         * @param value The ISO3CHR attribute.
         */
        public ProductBuilder iso3char(String value) {
            if (value != null) {
                iso3char = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the LOAD_DATE attribute.
         * @param value The LOAD_DATE attribute.
         */
        public ProductBuilder loadDate(Date value) {
            if (value == null) {
                loadDate = new Date(0);
            }
            else {
                loadDate = value;
            }
            return this;
        }
        
        /**
         * Setter method for the "MEDIA NAME" attribute.
         * @param value The "MEDIA NAME" attribute.
         */
        public ProductBuilder mediaName(String value) {
            if (value != null) {
                mediaName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the ALL_NOTES attribute.
         * @param value The ALL_NOTES attribute.
         */
        public ProductBuilder notes(String value) {
            if (value != null) {
                notes = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the NRN attribute.
         * @param value The NRN attribute.
         */
        public ProductBuilder nrn(String value) {
            if (value != null) {
                nrn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the NSN attribute.
         * @param value The NSN attribute.
         */
        public ProductBuilder nsn(String value) {
            if (value != null) {
                nsn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the UNIX_PATH attribute.
         * @param value The UNIX_PATH attribute.
         */
        public ProductBuilder path(String value) {
            if (value != null) {
                path = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the PROD_TYPE attribute.
         * @param value The PROD_TYPE attribute.
         */
        public ProductBuilder productType(String value) {
            if (value != null) {
                productType = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the SEC_REL attribute.
         * @param value The SEC_REL attribute.
         */
        public ProductBuilder releasability(String value) {
            if (value != null) {
                releasability = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the REL_DESC attribute.
         * @param value The REL_DESC attribute.
         */
        public ProductBuilder releasabilityDescription(String value) {
            if (value != null) {
                releasabilityDescription = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the PRODUCT_SIZE_BYTES attribute.
         * @param value The PRODUCT_SIZE_BYTES attribute.
         */
        public ProductBuilder size(long value) {
            size = value;
            return this;
        }
        
        /**
         * Setter method for the HYPERLINK_URL attribute.
         * @param value The HYPERLINK_URL attribute.
         */
        public ProductBuilder url(String value) {
            if (value != null) {
                url = value.trim();
            }
            return this;
        }

        /**
         * Validate internal member variables.
         * @param object The Product object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateProductObject(Product object) 
                throws IllegalStateException {
            
            if (object.getEdition() < 0) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for EDITION "
                        + "was out of range [ "
                        + object.getEdition()
                        + " ] (should be greater than zero).");
            }
            if (object.getSize() < 0) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for "
                        + "PRODUCT_SIZE_BYTES was out of range [ "
                        + object.getSize()
                        + " ] (should be greater than zero).");
            }
            if ((object.getAorCode() == null) || 
                    (object.getAorCode().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for AOR_CODE "
                        + "was null.");
            }
            if ((object.getCountryName() == null) || 
                    (object.getCountryName().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for COUNTRY_NAME "
                        + "was null.");
            }
            if ((object.getProductType() == null) || 
                    (object.getProductType().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for PRODUCT_TYPE "
                        + "was null.");
            }
            // If the NRN or NSN is missing, include a dump of the entire
            // object for debugging purposes.
            if ((object.getNSN() == null) || (object.getNSN().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for NSN "
                        + "was null.  Product object => [ "
                        + object.toString()
                        + " ].");
            }
            
            if ((object.getNRN() == null) || (object.getNRN().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for NRN "
                        + "was null.   Product object => [ "
                        + object.toString()
                        + " ].");
            }
            
            if ((object.getPath() == null) || (object.getPath().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for UNIX_PATH "
                        + "was null.");
            }
            path = path.trim();
            
            if ((object.getURL() == null) || (object.getURL()).isEmpty()) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for URL "
                        + "was null.");
            }
            url = url.trim();
        }
    }
}
