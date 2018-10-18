package mil.nga.artwork;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.artwork.image.ImageProcessorFactory;
import mil.nga.exceptions.UnknownImageTypeException;
import mil.nga.rod.model.Artwork;
import mil.nga.types.ImageType;
import mil.nga.util.FileUtils;

/**
 * The ISO generation process creates a ZIP file that contains the artwork
 * that is to be used in the generation of a hardcopy CD/DVD containing the 
 * ISO.  The information about the ZIP file is contained in a database table.
 * This class is responsible for taking that artwork information and 
 * generating thumbnail images of the artwork for display purposes.
 * 
 * This class assumes that the source artwork has been extracted and is 
 * ready for further processing. 
 * 
 * @author L. Craig Carpenter
 */
public class ArtworkProcessor implements ArtworkProcessorConstants {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		ArtworkProcessor.class);
	
	/**
	 * Default constructor.
	 */
	public ArtworkProcessor() { }
	
	/**
	 * Determine the type of image contained in the source image file. 
	 * @param pathToSource The on-disk path to the target image file.
	 * @return The ImageType object.
	 * @throws UnknownImageTypeException Thrown if we're unable to 
	 * determine the type of the source image file.
	 */
	private ImageType getSourceImageType(String pathToSource) 
			throws UnknownImageTypeException {
		ImageType imgType = null;
		if ((pathToSource != null) && (!pathToSource.isEmpty())) {
			String type = FileUtils.getExtension(pathToSource);
			imgType = ImageType.fromString(type.toLowerCase());
		}
		return imgType;
	}
	
	/**
	 * Entry point for the code used to actually construct the reduced 
	 * resolution images for display on the website. 
	 * 
	 * @param artworkData The constructed <code>Artwork</code> object 
	 * which contains the paths for all of the output images.
	 */
	public void process(Artwork artworkData) {
		if (artworkData != null) {
			LOG.info("Initializing image processing for source image => [ "
					+ artworkData.getSourceImagePath()
					+ " ].");
			if ((artworkData.getSourceImagePath() != null) && 
					(!artworkData.getSourceImagePath().isEmpty())) {
				Path source = Paths.get(
						URI.create(
								artworkData.getSourceImagePath()));
				if (Files.exists(source)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Source image exists.  Proceeding...");
					}
					generateReducedResolutionImages(artworkData);
				}
				else {
					LOG.error("The source image defined by "
							+ "path [ "
							+ source.toString()
							+ " ] does not exist.  Unable to proceed with the "
							+ "generation of reduced resolution artwork.");
				}
			}
			else {
				LOG.error("The source image path is "
						+ "null or not defined.  Unable to proceed with the "
						+ "generation of reduced resolution artwork.");
			}
		}
	}
	
	/**
	 * Start the threads responsible for generating the reduced resolution
	 * images.
	 * @param art The Artwork object.
	 */
	private void generateReducedResolutionImages(Artwork art) {
		
		long start = System.currentTimeMillis();
		
		// Run the code to build the output thumbnail image.
		try {
			ImageProcessorFactory factory = ImageProcessorFactory.getInstance();
			LOG.info("Generating thumbnail image...");
			new Thread(
					factory.getImageProcessor(
							getSourceImageType(art.getSourceImagePath()),
							DEFAULT_THUMBNAIL_IMAGE_WIDTH,
							DEFAULT_THUMBNAIL_IMAGE_HEIGHT,
							art.getThumbnailImagePath(),
							art.getSourceImagePath())).run();
			LOG.info("Generating small image...");
			new Thread(
					factory.getImageProcessor(
							getSourceImageType(art.getSourceImagePath()),
							DEFAULT_SMALL_IMAGE_WIDTH,
							DEFAULT_SMALL_IMAGE_HEIGHT,
							art.getSmallImagePath(),
							art.getSourceImagePath())).run();
		}
		catch (UnknownImageTypeException uite) {
			LOG.error("Unexpected UnknownImageTypeException encountered while "
					+ "attempting to generate the reduced-resolution images "
					+ "from source image [ "
					+ art.getSourceImagePath()
					+ " ].  Please ensure the source image type is supported.");
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resized images created in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
	}
}
