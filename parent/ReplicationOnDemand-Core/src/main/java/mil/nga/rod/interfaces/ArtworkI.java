package mil.nga.rod.interfaces;

public interface ArtworkI {

	/**
	 * Getter method for the on-disk path associated with the actual image.
	 * @return The on-disk path associated with the actual image.
	 */
	
	
	/**
	 * Getter method for the on-disk path for the "small" image.
	 * @return The on-disk path for the small image used for web-site display.
	 */
	public String getSmallImagePath();
	
	/**
	 * Getter method for the URL associated with the "small" image.
	 * @return The URL of the small image for web-site display.
	 */
	public String getSmallImageUrl();
	
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
	
	/**
	 * The name of the associated CD.
	 * @return The name of the associated CD.
	 */
	public String getCDName();
	
    /**
     * Getter method for the NRN number.
     * @return The NRN number.
     */
	public String getNRN();
	
    /**
     * Getter method for the NSN number.
     * @return The NSN number.
     */
	public String getNSN();
	
	/**
	 * Getter method for the path to the artwork image.
	 * @return The path to the artwork image.
	 */
	public String getPath();
	
	/** 
	 * Getter method for the size of the artwork image.
	 * @return The size of the artwork image.
	 */
	public long getSize();
}
