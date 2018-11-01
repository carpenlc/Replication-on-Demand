package mil.nga.rod.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import mil.nga.rod.util.ProductUtils;

/**
 * Object used to represent a replication-on-demand record.  The RoDProduct 
 * object is a flattened version of the previous data model which is a
 * concatenation of data collected from 3 different locations.  This object 
 * combines data from the following 3 locations:
 * <ul>
 *     <li>The Oracle table containing all of the RoD NRN/NSN in all 
 *     country/AOR combinations.</li>
 *     <li>The actual on-disk data associated with individual ISO files 
 *     (specifically, the MD5 hash of the ISO).</li>
 *     <li>The artwork information.</li> 
 * </ul>
 *  
 * Updated to remove storage of the AOR code and country name in the cache.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="ROD_PRODUCTS")
@JsonDeserialize(builder = RoDProduct.RoDProductBuilder.class)
public class RoDProduct implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -2049705868120254134L;
	
    /** 
     * Format associated with dates incoming from the target UPG data source.
     * NOTE: It doesn't appear that the DATE stored in the database contains 
     * the time component.  Remove it in the formatter String.
     */
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    /**
     * As of this writing, not all of the date fields are actually populated.
     * Use a 
     */
    private static final String DEFAULT_DATE_STRING = "unavailable";
    
	/**
	 * The primary key field is the concatenation of the NRN/NSN fields. 
	 * @see mil.nga.rod.util.ProductUtils
	 */
	@Id
	@Column(name="KEY", nullable=false)
	private String key;
	
	/**
	 * A flat string containing a list of AORs associated with this
	 * NRN/NSN combination.
	 */
	@Column(name="AOR_CODES")
    private String aorCodes;
	@Column(name="CLASSIFICATION")
    private String classification;
	@Column(name="CLASSIFICATION_DESCRIPTION")
    private String classificationDescription;
	
	/**
	 * A flat string containing a list of countries associated with this
	 * NRN/NSN combination.
	 */
	@Column(name="COUNTRY_NAMES")
	private String countryNames;
	@Column(name="EDITION")
    private long   edition;
	@Column(name="FILE_DATE")
    private Date   fileDate;
	
	/**
	 * A flat string containing a list of ISO 3 character codes associated 
	 * with this NRN/NSN combination.
	 */
	@Column(name="ISO3CHAR_CODES")
	private String iso3CharCodes;
	@Column(name="LOAD_DATE")
    private Date   loadDate;
	@Column(name="MEDIA_NAME")
    private String mediaName;
	@Column(name="NOTES", length=4000)
    private String notes;
	@Column(name="NRN")
    private String nrn;
	@Column(name="NSN")
    private String nsn;
	@Column(name="PATH")
    private String path;
	@Column(name="PRODUCT_TYPE")
    private String productType;
	@Column(name="RELEASABILITY")
    private String releasability;
	@Column(name="RELEASABILITY_DESCRIPTION")
    private String releasabilityDescription;
	@Column(name="ISO_SIZE")
    private long   size;
	@Column(name="URL")
    private String url;
	// Fields from ArtworkRow
	@Column(name="CD_NAME")
	private String cdName;
	// artworkPath is the path to a ZIP file containing the
	@Column(name="ARTWORK_ZIP_PATH")
    private String artworkPath;
	@Column(name="ARTWORK_ZIP_SIZE")
	private long   artworkSize;
	// Fields from Artwork
	@Column(name="THUMBNAIL_IMAGE_URL")
	private String thumbnailImageUrl;
	@Column(name="THUMBNAIL_IMAGE_PATH")
	private String thumbnailImagePath;
	@Column(name="SMALL_IMAGE_URL")
	private String smallImageUrl;
	@Column(name="SMALL_IMAGE_PATH")
	private String smallImagePath;
	@Column(name="SOURCE_IMAGE_PATH")
	private String sourceImagePath;
	@Column(name="SOURCE_IMAGE_URL")
	private String sourceImageUrl;
	// Fields from QueryRequestAccelerator
	@Column(name="HASH")
    private String hash;
    
	/**
	 * Default constructor required by the persistence modules.
	 */
	public RoDProduct() {}
	
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
    private RoDProduct(RoDProductBuilder builder) {
    	// Set fields from product
    	this.aorCodes                  = builder.aorCodes;
        this.countryNames              = builder.countryNames;
    	this.key                       = builder.key;
        this.classification            = builder.classification;
        this.classificationDescription = builder.classificationDescription;
        this.edition                   = builder.edition;
        this.fileDate                  = builder.fileDate;
        this.iso3CharCodes             = builder.iso3CharCodes;
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
        // Set Fields from ArtworkRow
        this.cdName                    = builder.cdName;
        this.artworkPath               = builder.artworkPath;
        this.artworkSize               = builder.artworkSize;
        // Set fields from Artwork
        this.thumbnailImageUrl         = builder.thumbnailImageUrl;
		this.thumbnailImagePath        = builder.thumbnailImagePath;
		this.smallImageUrl             = builder.smallImageUrl;
		this.smallImagePath            = builder.smallImagePath;
		this.sourceImagePath           = builder.sourceImagePath;
		this.sourceImageUrl            = builder.sourceImageUrl;
		// Set fields from QueryRequestAccelerator
		this.hash                      = builder.hash;
    }
   
    /**
     * Getter method for the list of AOR codes associated with this 
     * product.
     * @return The AOR codes for the product.
     */
    public String getAorCodes() {
    	return aorCodes;
    }
    
    /**
     * Getter method for the primary key.
     * @return The primary key for the product.
     */
    public String getKey() {
    	return key;
    }
    
	/**
	 * Getter method for the path to the artwork image.
	 * @return The path to the artwork image.
	 */
	public String getArtworkPath() {
		return artworkPath;
	}
	
	/**
	 * Getter method for the size of the artwork image. 
	 * @return The size of the artwork image.
	 */
	public long getArtworkSize() {
		return artworkSize;
	}
	
	/**
	 * The name of the associated CD.  
	 * @return The name of the associated CD.
	 */
	public String getCdName() {
		return cdName;
	}
    
	/**
     * Getter method for the list of country names associated with 
     * this product.
     * @return The country names for the product.
     */
    public String getCountryNames() {
    	return countryNames;
    }
    
	/**
     * Getter method for the list of ISO 3 char codes associated with 
     * this product.
     * @return The ISO 3 char codes for the product.
     */
    public String getIso3CharCodes() {
    	return iso3CharCodes;
    }
    
	/**
	 * Getter method for the on-disk path to the "small" image.
	 * @return The path to the small image.
	 */
	public String getSmallImagePath() {
		return smallImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the "small" image.
	 * @return The URL of the small image for web-site display.
	 */
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	
	/**
	 * Getter method for the on-disk path for the source image.
	 * @return The on-disk path for the source image used for web-site display.
	 */
	public String getSourceImagePath() {
		return sourceImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the source image.
	 * @return The URL of the source image for web-site display.
	 */
	public String getSourceImageUrl() {
		return sourceImageUrl;
	}
	
	/**
	 * Getter method for the on-disk path for the thumbnail image.
	 * @return The on-disk path for the thumbnail image used for web-site display.
	 */
	public String getThumbnailImagePath() {
		return thumbnailImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the thumbnail image.
	 * @return The URL of the thumbnail image for web-site display.
	 */
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	
	/**
     * Getter method for the size of the ISO file requested.  This is 
     * retrieved from the <code>QueryRequestAccelerator</code> object.
     * @return The size of the file requested.
     */
    public long getSize() {
    	return size;
    }
    
    /**
     * Getter method for the date associated with the on-disk file.  This is 
     * retrieved from the <code>QueryRequestAccelerator</code> object.
     * @return The on-disk file date.
     */
    public Date getFileDate() {
    	return fileDate;
    }

    /**
     * Getter method for the file date.
     * @return The file date.
     */
    @JsonIgnore
    public String getFileDateString() {
        String date = DEFAULT_DATE_STRING;
        if (fileDate != null) {
            date = (new SimpleDateFormat(DATE_FORMAT_STRING)).format(fileDate);
        }
        return date;
    }
    
    /**
     * Getter method for the MD5 hash of the target file.  
     * @return The MD5 hash of the target file.
     */
    public String getHash() {
    	return hash;
    }
    
    /**
     * Getter method for the path to the target RoD file.  This is 
     * retrieved from the <code>Product</code> object.
     * @return The the path to the target RoD file.
     */
    public String getPath() {
    	return path;
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
     * Getter method for the edition number of the product.
     * @return The edition number of the product.
     */
    public long getEdition() {
        return edition;
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
    @JsonIgnore
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
        sb.append("+");
        sb.append(getNSN());
        return sb.toString().hashCode();
    }
    
    /**
     * Convert to a human readable String.
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append(newLine);
        sb.append("RoD Product ( ");
        sb.append(getKey());
        sb.append(" )");
        sb.append(newLine);
        sb.append("  Product data:  NRN => [ ");
        sb.append(getNRN());
        sb.append(" ], NSN => [ ");
        sb.append(getNSN());
        sb.append(" ], Country Names => [ ");
        sb.append(getCountryNames());
        sb.append(" ].");
        sb.append(newLine);
        sb.append("  On-Disk data:  Hash => [ ");
        sb.append(getHash());
        sb.append(" ].");
        sb.append(newLine);
        sb.append("  Artwork data:  CD Name => [ ");
        sb.append(getCdName());
        sb.append(" ], Artwork Size => [ ");
        sb.append(getArtworkSize());
        sb.append(" ], Artwork Path => [ ");
        sb.append(getArtworkPath());
        sb.append(" ].");
        sb.append(newLine);
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new <code>RoDProduct</code> objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class RoDProductBuilder {
    	
        private String     key;
        private String     aorCodes;
        private String     classification;
        private String     classificationDescription;
        private String     countryNames;
        private long       edition;
        private Date       fileDate;
        private String     iso3CharCodes;
        private Date       loadDate;
        private String     mediaName;
        private String     notes;
        private String     nrn;
        private String     nsn;
        private String     path;
        private String     productType;
        private String     releasability;
        private String     releasabilityDescription;
        private long       size;
        private String     url;
    	private String     cdName             = "not available";
        private String     artworkPath        = "not available";
    	private long       artworkSize        = 0;
    	private String     thumbnailImageUrl  = "";
    	private String     thumbnailImagePath = "";
    	private String     smallImageUrl      = "";
    	private String     smallImagePath     = "";
    	private String     sourceImagePath    = "";
    	private String     sourceImageUrl     = "";
    	private String     hash               = "";
    	
        /**
         * Setter method for the list of AORs associated with the product.
         * @param value The AORs associated with the product.
         */
        public RoDProductBuilder aorCodes(String value) {
        	aorCodes = value;
            return this;
        }
        
        /**
         * Setter method for the size (in bytes) of the target artwork file.
         * @param value The size of the target artwork file.
         */
        public RoDProductBuilder artworkSize(long value) {
        	artworkSize = value;
            return this;
        }
        
        /**
         * Setter method for the on-disk path to the target artwork file.
         * @param value The path to the target artwork file.
         */
        public RoDProductBuilder artworkPath(String value) {
        	artworkPath = value;
            return this;
        }
        
        /**
         * Setter method for the SEC_CLASS attribute.
         * @param value The SEC_CLASS attribute.
         */
        public RoDProductBuilder classification(String value) {
            if (value != null) {
                classification = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the CLASS_DESC attribute.
         * @param value The CLASS_DESC attribute.
         */
        public RoDProductBuilder classificationDescription(String value) {
            if (value != null) {
                classificationDescription = value.trim();
            }
            return this;
        }

        /**
         * Setter method for the list of country names associated with the 
         * product.
         * @param value The country names associated with the product.
         */
        public RoDProductBuilder countryNames(String value) {
        	countryNames = value;
            return this;
        }
        
        /**
         * Setter method for the EDITION attribute.
         * @param value The EDITION attribute.
         */
        public RoDProductBuilder edition(long value) {
            edition = value;
            return this;
        }
        
        /**
         * Setter method for the FILE_DATE attribute.
         * @param value The FILE_DATE attribute.
         */
        public RoDProductBuilder fileDate(Date value) {
            if (value == null) {
                fileDate = new Date(0);
            }
            else {
                fileDate = value;
            }
            return this;
        }
        
        /**
         * Setter method for the MD5 hash of the target file.
         * @param value The MD5 hash of the target file.
         */
        public RoDProductBuilder hash(String value) {
            if (value != null) {
                hash = value;
            }
            return this;
        }
        
        /**
         * Setter method for the list of ISO 3 char codes associated with the 
         * product.
         * @param value The ISO 3-char codes associated with the product.
         */
        public RoDProductBuilder iso3CharCodes(String value) {
        	iso3CharCodes = value;
            return this;
        }
        
        /**
         * Setter method for the primary key.
         * @param value The primary key.
         */
        public RoDProductBuilder key(String value) {
            if (value != null) {
                key = value;
            }
            return this;
        }
        
        /**
         * Setter method for the LOAD_DATE attribute.
         * @param value The LOAD_DATE attribute.
         */
        public RoDProductBuilder loadDate(Date value) {
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
        public RoDProductBuilder mediaName(String value) {
            if (value != null) {
                mediaName = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the ALL_NOTES attribute.
         * @param value The ALL_NOTES attribute.
         */
        public RoDProductBuilder notes(String value) {
            if (value != null) {
                notes = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the NRN attribute.
         * @param value The NRN attribute.
         */
        public RoDProductBuilder nrn(String value) {
            if (value != null) {
                nrn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the NSN attribute.
         * @param value The NSN attribute.
         */
        public RoDProductBuilder nsn(String value) {
            if (value != null) {
                nsn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the <code>UNIX_PATH</code> attribute.
         * @param value The <code>UNIX_PATH</code> attribute.
         */
        public RoDProductBuilder path(String value) {
            if (value != null) {
                path = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the PROD_TYPE attribute.
         * @param value The PROD_TYPE attribute.
         */
        public RoDProductBuilder productType(String value) {
            if (value != null) {
                productType = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the SEC_REL attribute.
         * @param value The SEC_REL attribute.
         */
        public RoDProductBuilder releasability(String value) {
            if (value != null) {
                releasability = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the REL_DESC attribute.
         * @param value The REL_DESC attribute.
         */
        public RoDProductBuilder releasabilityDescription(String value) {
            if (value != null) {
                releasabilityDescription = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the PRODUCT_SIZE_BYTES attribute.
         * @param value The PRODUCT_SIZE_BYTES attribute.
         */
        public RoDProductBuilder size(long value) {
            size = value;
            return this;
        }
        
        /**
         * Setter method for the HYPERLINK_URL attribute.
         * @param value The HYPERLINK_URL attribute.
         */
        public RoDProductBuilder url(String value) {
            if (value != null) {
                url = value.trim();
            }
            return this;
        }
        
    	/**
    	 * The name of the associated CD.  Retrieved from the <code>Artwork</code> 
    	 * object.
    	 * @return The name of the associated CD.
    	 */
    	public RoDProductBuilder cdName(String value) {
            cdName = value;
            return this;
    	}
        
    	/**
    	 * Getter method for the on-disk path for the "small" image.
    	 * @return The on-disk path for the small image used for web-site display.
    	 */
    	public RoDProductBuilder smallImagePath(String value) {
    		if (value != null) {
    			smallImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the URL associated with the "small" image.
    	 * @return The URL of the small image for web-site display.
    	 */
    	public RoDProductBuilder smallImageUrl(String value) {
    		if (value != null) {
    			smallImageUrl = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the on-disk path for the source image.
    	 * @return The on-disk path for the small image used for web-site display.
    	 */
    	public RoDProductBuilder sourceImagePath(String value) {
    		if (value != null) {
    			sourceImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the URL associated with the source image.
    	 * @return The URL of the small image for web-site display.
    	 */
    	public RoDProductBuilder sourceImageUrl(String value) {
    		if (value != null) {
    			sourceImageUrl = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the on-disk path for the thumbnail image.
    	 * @return The on-disk path for the thumbnail image used for web-site display.
    	 */
    	public RoDProductBuilder thumbnailImagePath(String value) {
    		if (value != null) {
    			thumbnailImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Setter method for the URL associated with the thumbnail image.
    	 * @param value The URL of the thumbnail image for web-site display.
    	 */
    	public RoDProductBuilder thumbnailImageUrl(String value) {
    		if (value != null) {
    			thumbnailImageUrl = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Populate the relevant internal members from an input 
    	 * <code>Artwork</code> object.
    	 * @param value A populated <code>Artwork</code> object.
    	 * @return Reference to the builder object.
    	 */
    	public RoDProductBuilder queryRequestAccelerator(QueryRequestAccelerator value) {
    		if (value != null) {
	            hash(value.getHash());
	            size(value.getSize());
    		}
    		return this;
    	}
    	
    	/**
    	 * Populate the relevant internal members from an input 
    	 * <code>Artwork</code> object.
    	 * @param value A populated <code>Artwork</code> object.
    	 * @return Reference to the builder object.
    	 */
    	public RoDProductBuilder artwork(Artwork value) {
    		if (value != null) {
	            cdName(value.getCdName());
	            artworkPath(value.getArtworkPath());
	            artworkSize(value.getArtworkSize());
	    		thumbnailImageUrl(value.getThumbnailImageUrl());
	    		thumbnailImagePath(value.getThumbnailImagePath());
	    		smallImageUrl(value.getSmallImageUrl());
	    		smallImagePath(value.getSmallImagePath());
	    		sourceImagePath(value.getSourceImagePath());
	    		sourceImageUrl(value.getSourceImageUrl());
    		}
    		return this;
    	}
    	
    	/**
    	 * Populate the relevant internal members from an input 
    	 * <code>Product</code> object.
    	 * @param value A populated <code>Product</code> object.
    	 * @return Reference to the builder object.
    	 */
    	public RoDProductBuilder product(Product value) {
    		if (value != null) {
    			aorCodes(value.getAorCode());
    			classification(value.getClassification());
    	        classificationDescription(value.getClassificationDescription());
    	        countryNames(value.getCountryName());
    	        edition(value.getEdition());
    	        fileDate(value.getFileDate());
    	        iso3CharCodes(value.getIso3Char());
    	        loadDate(value.getLoadDate()); 
    	        mediaName(value.getMediaName());
    	        notes(value.getNotes());
    	        nrn(value.getNRN());
    	        nsn(value.getNSN());
    	        path(value.getPath());
    	        productType(value.getProductType());
    	        releasability(value.getReleasability());
    	        releasabilityDescription(value.getReleasabilityDescription());
    	        size(value.getSize());
    	        url(value.getURL());
    		}
    		return this;
    	}
    	
    	/**
    	 * Calculate the private key for the individual record.
    	 */
    	private void setKey() {
    		key(ProductUtils.getInstance().getKey(nrn, nsn));
    	}
    	
        /**
         * Method used to actually construct the <code>RoDProduct</code> 
         * object.
         * @return A constructed and validated <code>RoDProduct</code> object.
         */
        public RoDProduct build() throws IllegalStateException {
        	setKey();
        	RoDProduct object = new RoDProduct(this);
            validate(object);
            return object;
        }
    	
        
        /**
         * Placeholder method.  Not all products seem to have an artwork record.
         * @throws IllegalStateException Thrown if internal members fail tests.
         */
        private void validate(RoDProduct value) 
        		throws IllegalStateException { 
        	if ((key == null) || (key.isEmpty())) {
        		throw new IllegalStateException("Primary key field [ KEY ] "
        				+ "is null or not defined.");
        	}
            if (value.getEdition() < 0) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for EDITION "
                        + "was out of range [ "
                        + value.getEdition()
                        + " ] (should be greater than zero).");
            }
            if (value.getSize() < 0) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for "
                        + "PRODUCT_SIZE_BYTES was out of range [ "
                        + value.getSize()
                        + " ] (should be greater than zero).");
            }
            if ((value.getProductType() == null) || 
                    (value.getProductType().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for PRODUCT_TYPE "
                        + "was null.");
            }
            // If the NRN or NSN is missing, include a dump of the entire
            // object for debugging purposes.
            if ((value.getNSN() == null) || (value.getNSN().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for NSN "
                        + "was null.  Product object => [ "
                        + value.toString()
                        + " ].");
            }
            
            if ((value.getNRN() == null) || (value.getNRN().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for NRN "
                        + "was null.   Product object => [ "
                        + value.toString()
                        + " ].");
            }
            
            if ((value.getPath() == null) || (value.getPath().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for UNIX_PATH "
                        + "was null.");
            }
            
            if ((value.getURL() == null) || (value.getURL()).isEmpty()) {
                throw new IllegalStateException("Attempted to build "
                        + "Product object but the value for URL "
                        + "was null.");
            }
        	if ((value.getSourceImagePath() == null) ||
        			(value.getSourceImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for sourceImagePath "
        				+ "not populated.");
        	}
        	if ((value.getSourceImageUrl() == null) ||
        			(value.getSourceImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for sourceImageUrl "
        				+ "not populated.");
        	}
        	if ((value.getSmallImagePath() == null) ||
        			(value.getSmallImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for smallImagePath "
        				+ "not populated.");
        	}
        	if ((value.getSmallImageUrl() == null) ||
        			(value.getSmallImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for smallImageUrl "
        				+ "not populated.");
        	}
        	if ((value.getThumbnailImagePath() == null) ||
        			(value.getSmallImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for thumbnailImagePath "
        				+ "not populated.");
        	}
        	if ((value.getThumbnailImageUrl() == null) ||
        			(value.getSmallImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for thumbnailImageUrl "
        				+ "not populated.");
        	}
        }
    }
}
