package mil.nga.artwork;

import mil.nga.types.ImageType;

public interface ArtworkProcessorConstants {

	/**
	 * Property containing the base location in which display artwork data 
	 * will be stored.  The value of this location will have a product type 
	 * and key appended to it for actual on-disk storage.
	 */
	public static final String ARTWORK_OUTPUT_BASE_PROPERTY = 
			"artwork.output_path";
	
	/**
	 * The base URL to use for calculating references to the output image 
	 * files.
	 */
	public static final String ARTWORK_BASE_URL_PROPERTY = "artwork.base_url";
	
	/** 
	 * The path to the image that will be used as default if the product does
	 * not have artwork. 
	 */
	public static final String ARTWORK_DEFAULT_IMAGE_PATH_PROPERTY = "artwork.default_image";
	
	/** 
	 * The URL associated with the image that will be used as default if the product does
	 * not have artwork. 
	 */
	public static final String  ARTWORK_DEFAULT_IMAGE_URL_PROPERTY = "artwork.default_image_url";
	
	/**
	 * Product type utilized in the ArtworkProcessor if the product type is 
	 * unknown or not supplied by callers.
	 */
	public static final String DEFAULT_PRODUCT_TYPE = 
			"unavailable";
	
	/**
	 * Appender for the name of the output thumbnail image.
	 */
	public static final String DEFAULT_THUMBNAIL_IMAGE_FILE_APPENDER = "-thumbnail";
	
	/**
	 * Appender for the name of the output small image.
	 */
	public static final String DEFAULT_THUMBNAIL_SMALL_FILE_APPENDER = "-small";
	
	/**
	 * Output images will be formatted in JPEG.
	 */
	public static final ImageType OUTPUT_IMAGE_TYPE = ImageType.JPG;
	
	/**
	 * Default width/height for the thumbnail images.
	 */
	public static final int DEFAULT_THUMBNAIL_IMAGE_WIDTH = 50;
	public static final int DEFAULT_THUMBNAIL_IMAGE_HEIGHT = 50;
	
	/**
	 * Default width/height for the small images.
	 */
	public static final int DEFAULT_SMALL_IMAGE_WIDTH = 500;
	public static final int DEFAULT_SMALL_IMAGE_HEIGHT = 500;
	
	
}
