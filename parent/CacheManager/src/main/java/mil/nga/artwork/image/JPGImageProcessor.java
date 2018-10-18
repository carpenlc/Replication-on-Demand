package mil.nga.artwork.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to read an original full-size image from
 * a supplied location and create a smaller reduced-resolution
 * thumbnail representation of that image.  Processing is done in the
 * run() method so that it can be executed in a separate Thread.
 * 
 * Note: If this class is executed within a JVM version lower than 1.7 you 
 * will see exceptions raised within getImage().  Prior to JDK 1.7 there is a bug 
 * in the ImageIO implementation when reading images that contain certain types 
 * of embedded color profiles.  The impact is that a number of images will not be 
 * able to be converted to a thumbnail image.
 * 
 * @author L. Craig Carpenter
 */
public class JPGImageProcessor extends ImageProcessor 
		implements ImageProcessorI, Runnable {
	
	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOG = LoggerFactory.getLogger(
			JPGImageProcessor.class);
	
    /**
     * Constructor used to set all of the required internal members.
     * @param builder Populated builder object.
     */
	protected JPGImageProcessor(JPGImageProcessorBuilder builder) {
		super(builder.outputImageHeight, 
				builder.outputImageWidth, 
				builder.outputImagePath, 
				builder.sourceImagePath);
	}
	
	/**
	 * Accessor method for the requested height of the output image.
	 * @return The requested height of the output image.
	 */
	@Override
	public int getOutputImageHeight() {
		return outputImageHeight;
	}
	
	/**
	 * Accessor method for the requested width of the output image.
	 * @return The requested width of the output image.
	 */
	@Override  
	public int getOutputImageWidth() {
		return outputImageWidth;
	}
	
	/**
	 * Accessor method for the path to the output image.
	 * @return The on-disk path to the output image.
	 */
	@Override
	public String getOutputImagePath() {
		return outputImagePath;
	}
	
	/**
	 * Accessor method for the path to the source image.
	 * @return The on-disk path to the source image.
	 */
	@Override
	public String getSourceImagePath() {
		return sourceImagePath;
	}
	
	/**
	 * Logic required to retrieve the full-res image from the target 
	 * file system.
	 * 
	 * @return The full-res buffered image.
	 */
	public BufferedImage getSourceImage(String pathToImage) {
		
		long          start = System.currentTimeMillis();
		BufferedImage image = null;
		
		Path p = Paths.get(URI.create(pathToImage));
	
		if (Files.exists(p)) {
			try (InputStream is = Files.newInputStream(p)) {
				image = ImageIO.read(is);
			}
			catch (FileNotFoundException fnfe) {
				LOG.error("Unexpected FileNotFoundException encountered while "
						+ "reading source image [ "
						+ pathToImage
						+ " ].  Error message => [ "
						+ fnfe.getMessage()
						+ " ].");
			}
			catch (IOException ioe) {
				LOG.error("Unexpected IOException encountered while "
						+ "reading source image [ "
						+ pathToImage
						+ " ].  Error message => [ "
						+ ioe.getMessage()
						+ " ].");
				ioe.printStackTrace();
			}
		}
		else {
			LOG.error("Target JPG image file does not exist.  "
					+ "Target file => [ "
					+ p.toString()
					+ " ].");
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Retreived source image [ "
					+ getSourceImagePath() 
					+ " ] in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		
		return image;
		
	}
	
	/**
	 * Overridden run() method that drives the creation of thumbnail images.
	 * This method retrieves the target image from disk, then creates a 
	 * reduced resolution thumbnail, then saves the image to the target 
	 * disk file. 
	 */
	@Override
	public void run() {
		
		long start = System.currentTimeMillis();
		
		saveImage(
				getResizedImage(
						getSourceImage(getSourceImagePath())));
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resized image [ "
					+ getOutputImagePath() 
					+ " ] resized in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		
	}
	
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new <code>JPGImageProcessor</code> objects.  
     * 
     * @author L. Craig Carpenter
     */
    public static class JPGImageProcessorBuilder {
    	
    	private int    outputImageWidth  = -1;
    	private int    outputImageHeight = -1;
    	private String outputImagePath   = "";
    	private String sourceImagePath   = "";
    	
        /**
         * Method used to actually construct the 
         * <code>JPGImageProcessor</code> object
         * @return A constructed and <code>JPGImageProcessor</code> Artwork 
         * object.
         */
    	public JPGImageProcessor build() {
    		JPGImageProcessor object = new JPGImageProcessor(this);
    		validate(object);
    		return object;
    	}
    	
    	/**
    	 * Setter method for the client-requested image width.
    	 * @param value The client-requested image width.
    	 */
    	public JPGImageProcessorBuilder outputImageWidth(int value) {
    		outputImageWidth = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the client-requested image height.
    	 * @param value The client-requested image height.
    	 */
    	public JPGImageProcessorBuilder outputImageHeight(int value) {
    		outputImageHeight = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the client-requested path to the output image.
    	 * @param value The client-requested path to the output image.
    	 */
    	public JPGImageProcessorBuilder outputImagePath(String value) {
    		outputImagePath = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the path to the source image.
    	 * @param value The path to the source image.
    	 */
    	public JPGImageProcessorBuilder sourceImagePath(String value) {
    		sourceImagePath = value;
    		return this;
    	}
    	
        /**
         * Validate internal member variables.
         * @param object The <code>PDFImageProcessor</code> object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
    	private void validate(JPGImageProcessor object) {
    		if ((object.getOutputImagePath() == null) || 
    				(object.getOutputImagePath().isEmpty())) {
    			throw new IllegalStateException("Output image path is not "
    					+ "defined.");
    		}
    		if ((object.getSourceImagePath() == null) || 
    				(object.getSourceImagePath().isEmpty())) {
    			throw new IllegalStateException("Source image path is not "
    					+ "defined.");
    		}
    		if (object.getOutputImageHeight() < 10) {
    			throw new IllegalStateException("Invalid value for "
    					+ "outputImageHeight => [ "
    					+ object.getOutputImageHeight()
    					+ " ].  Must be > 10.");  			
    		}
    		if (object.getOutputImageWidth() < 10) {
    			throw new IllegalStateException("Invalid value for "
    					+ "outputImageWidth => [ "
    					+ object.getOutputImageWidth()
    					+ " ].  Must be > 10.");
    		}
    	}
    }
}
