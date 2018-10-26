package mil.nga.artwork;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.jdbc.ArtworkRowFactory;
import mil.nga.rod.model.Artwork;
import mil.nga.rod.model.ArtworkRow;
import mil.nga.rod.model.Product;
import mil.nga.rod.util.ProductUtils;
import mil.nga.util.FileUtils;
import mil.nga.util.URIUtils;


/**
 * This class constructs all of the paths and URLs associated with the 
 * artwork data.  It also kicks off the processing associated with the 
 * generation of the reduced-resolution images used for the website.
 * 
 * Normally, the builder would be part of the POJO.  In this case it's a 
 * completely separate class due to the added complexity.
 * 
 * Usage:
 * <pre>
 * {@code
 *     Artwork art = new ArtworkBuilder()
 *     						.nrn(productNRN)
 *                          .nsn(productNSN)
 *                          .productType(productType)
 *                          .build();
 * }
 * </pre>
 * 
 * - or - 
 * 
 * <pre>
 * {@code
 *     Artwork art = new ArtworkBuilder()
 *     						.product(productObj)
 *                          .build();
 * }
 * </pre>
 * 
 * @author L. Craig Carpenter 
 */
public class ArtworkBuilder implements ArtworkProcessorConstants {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		ArtworkBuilder.class);
    
	/**
	 * The base name to use for the output files.
	 */
	private String baseFilename = null;

	/**
	 * This is the base URL used for displaying images in the web browser.
	 */
	private String baseUrl = null;
	
	/**
	 * This is the path to the default image.
	 */
	private String defaultImagePath = null;
	
	/**
	 * This is the path to the default image.
	 */
	private String defaultImageUrl = null;
	
	/**
	 * Product NRN.
	 */
	private String nrn;
	
	/**
	 * Product NSN.
	 */
	private String nsn;
	
	/**
	 * String identifying the product type.
	 */
	private String productType;
	
	/**
	 * Private internal member object defining the output path in which 
	 * all generated files will be stored.
	 */
	private String outputPath = null;
	
	/**
	 * This is the base location for the output files.  It will be obtained
	 * from the default system properties files.
	 */
	private String baseOutputPath = null;
	
	/**
	 * Default constructor used to load the required property data.
	 * 
	 * @throws PropertiesNotLoadedException Thrown if the target properties 
	 * file cannot be loaded.
	 * @throws PropertyNotFoundException Thrown if a required property is 
	 * not supplied.
	 */
	public ArtworkBuilder() 
			throws PropertiesNotLoadedException, PropertyNotFoundException {
		PropertyLoader props = PropertyLoader.getInstance().loadProperties();
		setBaseOutputPath(props.getProperty(ARTWORK_OUTPUT_BASE_PROPERTY));
		setBaseUrl(props.getProperty(ARTWORK_BASE_URL_PROPERTY));
		setDefaultImagePath(props.getProperty(ARTWORK_DEFAULT_IMAGE_PATH_PROPERTY));
		setDefaultImageUrl(props.getProperty(ARTWORK_DEFAULT_IMAGE_URL_PROPERTY));
	}
	
	/**
	 * This method was introduced adding logic to determine whether the source 
	 * image needs to be extracted from a zip file (expected behavior) or
	 * retrieved from an on-disk location (when the artwork doesn't exist).
	 * The logic is essentially, if the <code>pathToSourceImage</code> 
	 * refers to a file that exists and is a zip file, the source image will 
	 * be extracted from the target location.  Oherwise, a default image will 
	 * be utilized. 
	 * 
	 * @param pathToSourceImage Path to the target source image
	 * @return Path to the source image.
	 */
	private Path getSourceImage(String pathToSourceImage) {
		Path sourceImage = null;
		if ((pathToSourceImage != null) && (!pathToSourceImage.isEmpty())) { 
			String extension = FileUtils.getExtension(pathToSourceImage);
			if (extension.trim().equalsIgnoreCase("zip")) {
				// At this point in processing, just create a regular URI as 
				// opposed to a zip URI.  If we create a zip URI we get a 
				// FileSystemNotFoundException.
				Path p = Paths.get(URIUtils.getInstance().getURI(pathToSourceImage));
				if (Files.exists(p)) {
					sourceImage = (new ArtworkUnzipper())
							.unzipArtwork(
									pathToSourceImage, 
									getOutputPath());
					if (sourceImage == null) {
						LOG.warn("Unable to retrieve the source image from "
								+ "target zip file => [ "
								+ pathToSourceImage
								+ " ].  Using default image.");
						sourceImage = Paths.get(
								URIUtils.getInstance().getURI(
										getDefaultImagePath()));
					}
				}
				else {
					LOG.warn("Target artwork zip file => [ "
							+ p.toString()
							+ " ] does not exist.  Using the default image.");
					sourceImage = Paths.get(
							URIUtils.getInstance().getURI(
									getDefaultImagePath()));
				}
			}
			else {
				sourceImage = Paths.get(
						URIUtils.getInstance().getURI(pathToSourceImage));
			}
		}
		return sourceImage;
	}
	
	/**
	 * 
	 * @throws IllegalStateException Thrown if any issues are encountered 
	 * while validating the returned Artwork object.
	 */
	public Artwork build() throws IllegalStateException {
		
		Artwork           art = null;  // return object
		ArtworkRow        row = null;
		ArtworkRowFactory artFactory = ArtworkRowFactory.getInstance();
		
		if ((productType == null) || (productType.isEmpty())) {
			productType = DEFAULT_PRODUCT_TYPE;
		}
		
		try {
			row = artFactory.getArtwork(getNRN(), getNSN());
		}
		catch (NoResultException nre) {
			// If we get a NoResultException we need to log it and set 
			// the artwork data to the default image indicating that the 
			// artwork does not exist.
			LOG.warn("No artwork record exists for NRN => [ "
					+ getNRN() 
					+ " ] and NSN => [ "
					+ getNSN()
					+ " ].  Using default images.");
		}
		
		
		try {			
			
			if (row != null) {
				
				// Calculate the path strings.
				setOutputPath(row, productType);
				updateBaseUrl(row, productType);
				setBaseFilename(row.getPath());
				
				// Extract the source image to a pre-defined location for 
				// further processing.
				Path sourceImage = getSourceImage(row.getPath());
				
				if (sourceImage != null) {	
					art = new Artwork.ArtworkBuilder()
							.artworkRow(row)
							.sourceImagePath(getSourceImagePath(sourceImage.toString()))
							.sourceImageUrl(getSourceImageUrl(sourceImage.toString()))
							.smallImagePath(getSmallImagePath())
							.smallImageUrl(getSmallImageUrl())
							.thumbnailImagePath(getThumbnailImagePath())
							.thumbnailImageUrl(getThumbnailImageUrl())
							.build();
					
				}
				else {
					// Construct the artwork using the default image as the 
					// source image.
					LOG.warn("Unable to obtain the source image.  Using the "
							+ "default image for further artwork processing.");
					art = new Artwork.ArtworkBuilder()
							.artworkRow(row)
							.sourceImagePath(getDefaultImagePath())
							.sourceImageUrl(getDefaultImageUrl())
							.smallImagePath(getSmallImagePath())
							.smallImageUrl(getSmallImageUrl())
							.thumbnailImagePath(getThumbnailImagePath())
							.thumbnailImageUrl(getThumbnailImageUrl())
							.build();
				}
			}
			else {
				LOG.warn("Artwork is not available for product with NRN => [ "
						+ getNRN()
						+ " ] and NSN => [ "
						+ getNSN()
						+ " ].  Using default image information.");
				row = new ArtworkRow.ArtworkBuilder()
						.cdName("00000000")
						.nrn(getNRN())
						.nsn(getNSN())
						.path(getDefaultImagePath())
						.size(getDefaultImageSize())
						.build();
				
				// Calculate the path strings.
				setOutputPath(row, productType);
				updateBaseUrl(row, productType);
				setBaseFilename(row.getPath());
				
				art = new Artwork.ArtworkBuilder()
						.artworkRow(row)
						.sourceImagePath(getDefaultImagePath())
						.sourceImageUrl(getDefaultImageUrl())
						.smallImagePath(getSmallImagePath())
						.smallImageUrl(getSmallImageUrl())
						.thumbnailImagePath(getThumbnailImagePath())
						.thumbnailImageUrl(getThumbnailImageUrl())
						.build();
			}
			
			// Execute the code responsible for building the reduced resolution 
			// artwork images, while avoiding a possible NPE here.
			if (art != null) {
				(new ArtworkProcessor()).process(art);
			}
			else {
				LOG.error("An error was encountered while attempting "
						+ "construct an Artwork object for NRN => [ "
						+ getNRN()
						+ " ] and NSN => [ "
						+ getNSN()
						+ " ].  The constructed Artwork object is "
						+ "null.  This should not happen.");
			}
		}
		catch (IllegalArgumentException iae) {
			// There are several records in the ISO table that have an NRN, 
			// but not an NSN.  The default value for NSN contains spaces 
			// which cannot be converted to a URI.  This situation raises this 
			// exception.  Just issue a warning message and move on.
			LOG.warn("Unexpected IllegalArgumentException raised while "
					+ "attempting to construct a URI.  Attempting to "
					+ "build artwork records for NRN => [ "
					+ getNRN()
					+ " ] and NSN => [ "
					+ getNSN()
					+ " ].  Exception message => [ "
					+ iae.getMessage() 
					+ " ].");
		}
		catch (IOException ioe) {
			// This exception is raised by the ArtworkUnzipper class if there 
			// are issues extracting the target source artwork.  
			LOG.error("IOException encountered while attempting to extract "
					+ "the source artwork from the target ZIP file.  "
					+ "Error message => [ "
					+ ioe.getMessage()
					+ " ].");
		}

		return art;
	}
	
	/**
	 * Construct the on-disk path for the small thumbnail file.
	 * @return The on-disk image path to the small thumbnail file.
	 */
	private String getSmallImagePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getOutputPath());
		if (!(sb.toString().endsWith(File.separator))) {
			sb.append(File.separator);
		}
		sb.append(getBaseFilename());
		sb.append(DEFAULT_THUMBNAIL_SMALL_FILE_APPENDER);
		sb.append(".");
		sb.append(OUTPUT_IMAGE_TYPE.getText());
		return sb.toString();
	}
	
	/**
	 * Construct the URL associated with the small thumbnail file.
	 * @return The URL associated with the small thumbnail file.
	 */
	private String getSmallImageUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		if (!(baseUrl.endsWith("/"))) {
			sb.append("/");
		}
		sb.append(getBaseFilename());
		sb.append(DEFAULT_THUMBNAIL_SMALL_FILE_APPENDER);
		sb.append(".");
		sb.append(OUTPUT_IMAGE_TYPE.getText());
		return sb.toString();
	}
	
	/**
	 * Construct a URL for the artwork source image.
	 * @param srcImagePath The on-disk path for the source image.
	 * @return The URL for the source image.
	 */
	private String getSourceImagePath(String srcImagePath) {
		URI uri = null;
    	if ((srcImagePath != null) && (!srcImagePath.isEmpty())) {
			URI temp = URI.create(srcImagePath);
			if ((temp.getScheme() == null) || (temp.getScheme().isEmpty())) {
				try {
					uri = new URI(
							"file",
							temp.getAuthority(),
							temp.getPath(),
							temp.getFragment(),
							temp.getQuery());
				}
				catch (URISyntaxException use) {}
			}
			else {
				uri = temp;
			}
		}
		else {
			LOG.error("The input file path was null or empty.  Output URI "
					+ "will also be null.");
		}
    	if (uri != null) {
    		return uri.toString();
    	}
    	else {
    		return null;
    	}
	}
	
	/**
	 * Construct a URL for the artwork source image.
	 * @param srcImagePath The on-disk path for the source image.
	 * @return The URL for the source image.
	 */
	private String getSourceImageUrl(String srcImagePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		if (!(baseUrl.endsWith("/"))) {
			sb.append("/");
		}
		sb.append(FileUtils.getFilenameFromPath(srcImagePath));
		return sb.toString();
	}
	
	/**
	 * Construct the URL associated with the thumbnail file.
	 * @return The URL associated with the thumbnail file.
	 */
	private String getThumbnailImageUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseUrl());
		if (!(baseUrl.endsWith("/"))) {
			sb.append("/");
		}
		sb.append(getBaseFilename());
		sb.append(DEFAULT_THUMBNAIL_IMAGE_FILE_APPENDER);
		sb.append(".");
		sb.append(OUTPUT_IMAGE_TYPE.getText());
		return sb.toString();
	}
	
	/**
	 * Construct the on-disk path for the thumbnail file.
	 * @return The on-disk image path to the thumbnail file.
	 */
	private String getThumbnailImagePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(getOutputPath());
		if (!(sb.toString().endsWith(File.separator))) {
			sb.append(File.separator);
		}
		sb.append(getBaseFilename());
		sb.append(DEFAULT_THUMBNAIL_IMAGE_FILE_APPENDER);
		sb.append(".");
		sb.append(OUTPUT_IMAGE_TYPE.getText());
		return sb.toString();
	}
	
	/**
	 * Getter method for the base URL that will be used to access the 
	 * generated image files. 
	 * @param value The base output path.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	
	/**
	 * Getter method for the base filename that will be used to construct the
	 * output filenames. 
	 * @param value The base filename.
	 */
	public String getBaseFilename() {
		return baseFilename;
	}
	
	/**
	 * Getter method for the target output path in which the output files
	 * will be stored.  The output path will be a concatenation of an 
	 * external property, the product type, and the product key.
	 * @return The output path.
	 */
	public String getOutputPath() {
		return outputPath;
	}
	
	/**
	 * Getter method for the base output path in which the output files
	 * will be stored.  This is defined by a system property.
	 * @return The output path.
	 */
	public String getBaseOutputPath() {
		return baseOutputPath;
	}
	
	/**
	 * Getter method for the location of the source image if the target 
	 * source image is not defined.
	 * @return The path to the source image.
	 */
	public String getDefaultImagePath() {
		return defaultImagePath;
	}
	
	/**
	 * Getter method for the location of the source image if the target 
	 * source image is not defined.
	 * @return The path to the source image.
	 */
	public long getDefaultImageSize() {
		
		long size = 0L;
		
		if ((getDefaultImagePath() != null) && 
				(!getDefaultImagePath().isEmpty())) {
			try {
				Path p = Paths.get(
						URIUtils.getInstance().getURI(getDefaultImagePath()));
				if (Files.exists(p)) {
					size = Files.size(p);
				}
				else {
					LOG.error("Target default image file does not exist.  Path => [ "
							+ getDefaultImagePath()
							+ " ].");
				}
			}
			catch (IllegalArgumentException iae) {
				// This could be thrown by the code that attempts to convert
				// the input image path String to a URI
				LOG.warn("Unexpected IllegalArgumentException raised while "
						+ "attempting to construct a URI.  Attempting to "
						+ "contruct a URI out of [ "
						+ getDefaultImagePath()
						+ " ].  Exception message => [ "
						+ iae.getMessage() 
						+ " ].  Using default size of 0.");
			}
			catch (IOException ioe) {
				LOG.warn("Unexpected IOException raised while "
						+ "attempting to determine the size of the target "
						+ "default image => [ "
						+ getDefaultImagePath()
						+ " ].  Exception message => [ "
						+ ioe.getMessage() 
						+ " ].  Using default size of 0.");
			}
		}
		return size;
	}
	
	/**
	 * Getter method for the location of the source image if the target 
	 * source image is not defined.
	 * @return The path to the source image.
	 */
	public String getDefaultImageUrl() {
		return defaultImageUrl;
	}
	
	/**
	 * Getter method for the NRN for which we are building Artwork.
	 * @return The NRN String.
	 * @throws IllegalStateException Thrown if the NRN is not populated.
	 */
	public String getNRN() {
		if ((nrn == null) || (nrn.isEmpty())) {
			throw new IllegalStateException("Value for NRN is null or empty.  "
					+ "Unable to construct the artwork data.");
		}
		return nrn;
	}
	
	/**
	 * Getter method for the NSN for which we are building Artwork.
	 * @return The NSN String.
	 * @throws IllegalStateException Thrown if the NSN is not populated.
	 */
	public String getNSN() {
		if ((nsn == null) || (nsn.isEmpty())) {
			throw new IllegalStateException("Value for NSN is null or empty.  "
					+ "Unable to construct the artwork data.");
		}
		return nsn;
	}
	
	/**
	 * Getter method for the product type for which we are building Artwork.
	 * @return The product type String.
	 * @throws IllegalStateException Thrown if the product type is not populated.
	 */
	public String getProductType() {
		if ((productType == null) || (productType.isEmpty())) {
			throw new IllegalStateException("Value for productType is null or "
					+ "empty.  Unable to construct the artwork data.");
		}
		return productType;
	}
	
	/**
	 * Setter method for the NRN String for the product for which the 
	 * <code>Artwork</code> will be generated.  
	 * @param value The NRN String.
	 * @return A handle to the <code>ArtworkBuilder</code> object.
	 * @throws IllegalStateException Thrown if any of the required fields are 
	 * not populated.
	 */
	public ArtworkBuilder nrn(String value) throws IllegalStateException {
		if ((value == null) || (value.isEmpty())) {
			throw new IllegalStateException("Value for NRN is null or empty.  "
					+ "Unable to construct the artwork data.");
		}
		nrn = value;
		return this;
	}
	
	/**
	 * Setter method for the NSN String for the product for which the 
	 * <code>Artwork</code> will be generated.  
	 * @param value The NSN String.
	 * @return A handle to the <code>ArtworkBuilder</code> object.
	 * @throws IllegalStateException Thrown if any of the required fields are 
	 * not populated.
	 */
	public ArtworkBuilder nsn(String value) throws IllegalStateException {
		if ((value == null) || (value.isEmpty())) {
			throw new IllegalStateException("Value for NSN is null or empty.  "
					+ "Unable to construct the artwork data.");
		}
		nsn = value;
		return this;
	}
	
	/**
	 * Setter method for the <code>Product</code> object for which the 
	 * <code>Artwork</code> will be generated.  
	 * @param prod A populated <code>Product</code> object.  The object cannot
	 * be null and must have the nrn, nsn, and product type internal members 
	 * populated.
	 * @return A handle to the <code>ArtworkBuilder</code> object.
	 * @throws IllegalStateException Thrown if any of the required fields are 
	 * not populated.
	 */
	public ArtworkBuilder product(Product prod) throws IllegalStateException {
		if (prod != null) {
			nrn(prod.getNRN());
			nsn(prod.getNSN());
			productType(prod.getProductType());
		}
		else {
			throw new IllegalStateException("Product object input to "
					+ "ArtworkBuilder.product() is null.");
		}
		return this;
	}
	
	
	/**
	 * Setter method for the NSN String for the product for which the 
	 * <code>Artwork</code> will be generated.  
	 * @param value The NSN String.
	 * @return A handle to the <code>ArtworkBuilder</code> object.
	 * @throws IllegalStateException Thrown if any of the required fields are 
	 * not populated.
	 */
	public ArtworkBuilder productType(String value) throws IllegalStateException {
		if ((value == null) || (value.isEmpty())) {
			throw new IllegalStateException("Value for productType is null or "
					+ "empty.  Unable to construct the artwork data.");
		}
		productType = value;
		return this;
	}
	
	/**
	 * Setter method for the base output path in which the output files
	 * will be stored.  This is defined by a system property.
	 * @param value The base output path.
	 */
	private void setBaseOutputPath(String value) {
		if ((value != null) && (!value.isEmpty())) {
			baseOutputPath = value.trim();
		}
		else {
			baseOutputPath = System.getProperty("java.io.tmpdir");
			LOG.warn("Unable to obtain a user-defined value for the base "
					+ "output path property [ "
					+ ARTWORK_OUTPUT_BASE_PROPERTY
					+ " ].  Using system temporary directory [ "
					+ baseOutputPath
					+ " ].");
		}
		baseOutputPath = value;
	}
	
	/**
	 * Setter method for the base output path in which the output files
	 * will be stored.  This is defined by a system property.
	 * @param value The base output path.
	 */
	private void setBaseUrl(String value) throws PropertyNotFoundException {
		if ((value != null) && (!value.isEmpty())) {
			baseUrl = value.trim();
		}
		else {
			throw new PropertyNotFoundException("Unable to obtain a "
					+ "user-defined value for the base URL for the "
					+ "artwork image data [ "
					+ ARTWORK_BASE_URL_PROPERTY
					+ " ].");
		}
	}
	
	/**
	 * The base filename to use will be the name of the zip file minus the 
	 * extension.
	 * @param pathToZip The full path to the output ZIP file.
	 */
	private void setBaseFilename(String pathToZip) {
		baseFilename = FileUtils.removeExtension(
				FileUtils.getFilenameFromPath(pathToZip));
	}
	
	/**
	 * Setter method for the path to the target default image.
	 * @param path The path to the target default image.
	 */
	public void setDefaultImagePath(String value) {
		defaultImagePath = value;
	}
	
	/**
	 * Setter method for the path to the target default image.
	 * @param path The path to the target default image.
	 */
	public void setDefaultImageUrl(String value) {
		defaultImageUrl = value;
	}
	
	/**
	 * Setter method for the target output path in which the output files
	 * will be stored.  The output path will be a concatenation of an 
	 * external property, the product type, and the product key.
	 * 
	 * @param datasource The row in the data table containing the artwork 
	 * data.
	 * @param productType The type of product we're creating images for.
	 * @throws IOException Thrown if the required output directory could not 
	 * be created.
	 */
	private void setOutputPath(ArtworkRow dataSource, String productType) 
			throws IOException, IllegalArgumentException {
		StringBuilder sb = new StringBuilder();
		sb.append(getBaseOutputPath());
		if (!(sb.toString().endsWith(File.separator))) {
			sb.append(File.separator);
		}
		sb.append(productType.toLowerCase());
		Path p = Paths.get(URIUtils.getInstance().getURI(sb.toString()));
		if (!Files.exists(p)) {
			Files.createDirectory(p);
		};
		sb.append(File.separator);
		sb.append(ProductUtils.getInstance().getKey(getNRN(), getNSN()));
		p = Paths.get(URIUtils.getInstance().getURI(sb.toString()));
		if (!Files.exists(p)) {
			Files.createDirectory(p);
		};
		outputPath = sb.toString();
	}
	
	/**
	 * Update the base URL with the product type and key information that 
	 * will be required for users to display the data.
	 * @param datasource The row in the data table containing the artwork 
	 * data.
	 * @param productType The type of product we're creating images for.
	 */
	private void updateBaseUrl(ArtworkRow dataSource, String productType) {
		StringBuilder sb = new StringBuilder();
		sb.append(baseUrl);
		if (!baseUrl.endsWith("/")) {
			sb.append("/");
		}
		sb.append(productType.toLowerCase());
		sb.append("/");
		sb.append(ProductUtils.getInstance().getKey(
				getNRN(), 
				getNSN()));
		sb.append("/");
		baseUrl = sb.toString();
	}
	
	/**
	 * Convert to human readable String.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ArtworkBuilder Parameters : Base output path => [ ");
		sb.append(getBaseOutputPath());
		sb.append(" ], Base URL => [ ");
		sb.append(getBaseUrl());
		sb.append(" ], Base file name => [ ");
		sb.append(getBaseFilename());
		sb.append(" ], Actual Output Path => [ ");
		sb.append(getOutputPath());
		sb.append(" ], Small Image Path => [ ");
		sb.append(getSmallImagePath()); 
		sb.append(" ], Thumbnail Image Path => [ ");
		sb.append(getThumbnailImagePath()); 
		sb.append(" ], Small Image URL => [ ");
		sb.append(getSmallImageUrl());
		sb.append(" ], Thumbnail Image URL => [ ");
		sb.append(getThumbnailImageUrl());
		sb.append(" ].");
		return sb.toString();
	}
}
