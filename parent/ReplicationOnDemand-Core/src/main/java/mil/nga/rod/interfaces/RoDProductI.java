package mil.nga.rod.interfaces;

import java.util.Date;

/**
 * Interface defining the methods required to be implemented in order to 
 * define a Replication-on-Demand product.
 * 
 * @author L. Craig Carpenter
 */
public interface RoDProductI {
    
    /**
     * Getter method for the country name.
     * @return The country name.
     */
    public String getCountryName();
    
    /**
     * Getter method for the date associated with the on-disk file.  This is 
     * retrieved from the <code>QueryRequestAccelerator</code> object.
     * @return The on-disk file date.
     */
    public Date getFileDate();

    /**
     * Getter method for the MD5 hash of the target file.  This is 
     * retrieved from the <code>QueryRequestAccelerator</code> object.
     * @return The MD5 hash of the target file.
     */
    public String getHash();
    
    /**
     * Getter method for the path to the target RoD file.  This is 
     * retrieved from the <code>Product</code> object.
     * @return The the path to the target RoD file.
     */
    public String getPath();
	
	/**
	 * Getter method for the path to the artwork image.  Retrieved from the 
	 * <code>Artwork</code> object.
	 * @return The path to the artwork image.
	 */
	public String getArtworkPath();
	
	/**
	 * Getter method for the size of the artwork image.  Retrieved from the 
	 * <code>Artwork</code> object.
	 * @return The size of the artwork image.
	 */
	public long getArtworkSize(); 
	
	/**
	 * The name of the associated CD.  Retrieved from the <code>Artwork</code> 
	 * object.
	 * @return The name of the associated CD.
	 */
	public String getCdName();
    
    /**
     * Getter method for the classification string (abbreviation).
     * @return The abbreviated classification string.
     */
    public String getClassification();
    
    /**
     * Getter method for the classification string (description).  
     * This data is extracted from the <code>Product</code> object.
     * @return The classification string.
     */
    public String getClassificationDescription();   
    
    /**
     * Getter method for the edition number of the product.  
     * This data is extracted from the <code>Product</code> object.
     * @return The edition number of the product.
     */
    public long getEdition();
    
    /**
     * Getter method for the ISO 3 character code.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The ISO 3 character code.
     */
    public String getIso3Char();
    
    /**
     * Getter method for the load date.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The load date.
     */
    public Date getLoadDate();
    
    /**
     * Getter method for the name of the media.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The name of the media.
     */
    public String getMediaName();
    
    /**
     * Getter method for the notes associated with the product.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The notes associated with the product.
     */
    public String getNotes();
    
    /**
     * Getter method for the NRN number.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The NRN number.
     */
    public String getNRN();
    
    /**
     * Getter method for the NSN number.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The NSN number.
     */
    public String getNSN();
    
    /**
     * Getter method for the product type.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The product type.
     */
    public String getProductType();
    
    /**
     * Getter method for the abbreviated releasability code of the product.  
     * This data is extracted from the <code>Product</code> object.
     * @return The abbreviated releasability code of the product.
     */
    public String getReleasability();
    
    /**
     * Getter method for the releasability of the product.  This data is 
     * extracted from the <code>Product</code> object.
     * @return The releasability of the product.
     */
    public String getReleasabilityDescription();
    
    /**
     * Getter method for the URL attribute.  This data is extracted from the
     * <code>Product</code> object.
     * @return The URL attribute.
     */
    public String getURL();
    
	/**
	 * Getter method for the URL associated with the "small" image.
	 * @return The URL of the small image for web-site display.
	 */
	public String getSmallImageUrl();
	
	/**
	 * Getter method for the on-disk path for the source image.
	 * @return The on-disk path for the source image used for web-site display.
	 */
	public String getSourceImagePath();
	
	/**
	 * Getter method for the URL associated with the source image.
	 * @return The URL of the source image for web-site display.
	 */
	public String getSourceImageUrl();
	
	/**
	 * Getter method for the on-disk path for the thumbnail image.
	 * @return The on-disk path for the thumbnail image used for web-site display.
	 */
	public String getThumbnailImagePath();
	
	/**
	 * Getter method for the URL associated with the thumbnail image.
	 * @return The URL of the thumbnail image for web-site display.
	 */
	public String getThumbnailImageUrl();
}
