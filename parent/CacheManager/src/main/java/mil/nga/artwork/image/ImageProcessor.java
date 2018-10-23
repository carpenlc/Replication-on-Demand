package mil.nga.artwork.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortennobel.imagescaling.ResampleOp;

import mil.nga.util.URIUtils;

/**
 * Superclass containing reduced resolution image creation algorithms common 
 * to the various image processors.
 * 
 * @author L. Craig Carpenter 
 */
public abstract class ImageProcessor {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOG = LoggerFactory.getLogger(
			ImageProcessor.class);
	
	// Internal members required for generation of the reduced 
	// resolution images.
	protected final int    outputImageWidth;
	protected final int    outputImageHeight;
	protected final String outputImagePath;
	protected final String sourceImagePath;
	
	/**
	 * Default constructor enforcing the population of required input 
	 * parameters.
	 * @param outputImageHeight The height of the output image.
	 * @param outputImageWidth The width of the output image.
	 * @param outputImagePath The path to the output image.
	 * @param sourceImagePath The path to the source image.
	 */
	protected ImageProcessor(
			int    outputImageHeight, 
            int    outputImageWidth, 
            String outputImagePath, 
            String sourceImagePath) {
		this.outputImageHeight = outputImageHeight;
		this.outputImageWidth  = outputImageWidth;
		this.outputImagePath   = outputImagePath;
		this.sourceImagePath   = sourceImagePath;	
	}
	
	/**
	 * This calculates the width of the image to maintain the original
	 * image size ratio.  This is used if the source image is not square.
	 * 
	 * @param width The width of the original image.
	 * @param height The height of the original image.
	 * @return The image thumbnail width.
	 */
	protected int getScaleWidth(int width, int height) {
		
		int    val        = getOutputImageWidth();
		double imageRatio = (double)getOutputImageWidth()/(double)getOutputImageHeight();
		double ratio      = (double)width/(double)height;
		
		if (ratio < imageRatio) {
			val = (getOutputImageHeight()*width)/height;
		}
		
		return val;
	}
	
	/**
	 * This calculates the height of the image to maintain the original
	 * image size ratio.  This is used if the source image is not square. 
	 * 
	 * @param width The width of the original image.
	 * @param height The height of the original image.
	 * @return The image thumbnail height.
	 */
	protected int getScaleHeight(int width, int height) {
		
		int    val        = getOutputImageHeight();
		double imageRatio = (double)getOutputImageWidth()/(double)getOutputImageHeight();
		double ratio      = (double)width/(double)height;
		
		if (ratio > imageRatio) {
			val = (getOutputImageWidth()*height)/width;
		}
		
		return val;
	}
	
	/**
	 * Create a thumbnail representation of the input full size image.
	 * This method downscales in a single-step using rendering hints 
	 * to maximize image quality.  
	 * 
	 * @param image Original image
	 * @return A thumbnail representation of the original image.
	 */
	public BufferedImage getThumbnailOneStep(BufferedImage image) {
		long          start         = System.currentTimeMillis();
		int           width, height = -1;
		BufferedImage thumbnail     = null;
		
		width  = image.getWidth();
		height = image.getHeight();
		
		// Create the image
		thumbnail = new BufferedImage(
				getOutputImageWidth(),
				getScaleHeight(width, height),
				BufferedImage.TYPE_INT_RGB);
		
		// Set up the graphics context associated with the thumbnail
		Graphics2D g = thumbnail.createGraphics();
		
		// Set the rendering hints to create a higher quality thumbnail
		g.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(
				RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Render the original image to the thumbnail 
		g.drawImage(
				image, 
				0, 
				0, 
				getOutputImageWidth(), 
				getScaleHeight(width, height), 
				null);
		g.dispose();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Source image [ "
					+ getSourceImagePath() 
					+ " ] rescaled in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		
		return thumbnail;
	}
	
	/**
	 * Create a thumbnail representation of the input full size image.
	 * This method utilizes Google's Java image scaling library 
	 * (code.google.com/p/java-image-scaling).  This method produces
	 * much higher quality reduced images than the methods available
	 * through the JDK (without JAI).
	 * 
	 * @param image Original image
	 * @return A thumbnail representation of the original image.
	 */
	public BufferedImage getResizedImage(BufferedImage image) {
		
		long start = System.currentTimeMillis();
		BufferedImage outputImage = null;
		
		if (image != null) {
			
			int width  = image.getWidth();
			int height = image.getHeight();
			
			ResampleOp resampleOp = new ResampleOp(
					this.getScaleWidth(width, height),
					this.getScaleHeight(width, height));
			
			outputImage = resampleOp.filter(image, null);
			
		}
		else {
			LOG.error("Input source image is null.  Output image will also "
					+ "be null.");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Source image [ "
					+ getSourceImagePath() 
					+ " ] rescaled in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return outputImage;
	}
	
	/**
	 * The default image is the image used if the image or PDF is not
	 * available for processing.  The target image is read from the classpath.
	 * 
	 * @return A default image to be displayed in the Rotator.
	 */
	/*public BufferedImage getDefaultImage() {
		
		BufferedImage image  = null;
		InputStream   is     = null;
		
		try {
			is    = getClass().getResourceAsStream(this._defaultImageFilename);
			image = ImageIO.read(is);
		} 
		catch (IOException ioe) {
			LOGGER.error(method
					+ "Unable to read the target default image from disk.  "
					+ "Image filename: "
					+ this._defaultImageFilename);
		}
		return image;
			
	}*/
	

	
	/**
	 * Simple method to write the newly rendered thumbnail image to the 
	 * target output file.
	 * 
	 * @param image The image to write to disk.
	 */
	protected void saveImage(BufferedImage image) {
		
		long start = System.currentTimeMillis();
		
		if (image != null) {
			LOG.info("Writing thumbnail to [ " 
					+ getOutputImagePath()
					+ " ].");
			Path p = Paths.get(URIUtils.getInstance().getURI(getOutputImagePath()));
			if (p != null) {
				try (OutputStream os = Files.newOutputStream(p)){
					
					ImageIO.write(
							image, 
							"jpg", 
							os);
				}
				catch (IOException ioe) {
					LOG.error("Unexpected IOException encountered while trying to write " 
							+ "thumbnail image to [ "
							+ getOutputImagePath()
							+ " ].  Error message => [ "
							+ ioe.getMessage()
							+ " ].");
				}
			}
			else {
				LOG.error("Unable to construct a URI for target output "
						+ "file => [ "
						+ getOutputImagePath()
						+ " ].");
			}
		}
		else {
			LOG.error("Input BufferedImage is null.  Unable to save image [ "
					+ getOutputImagePath() 
					+ " ] to the file system.");
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Output image [ "
					+ getOutputImagePath() 
					+ " ] written to disk in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
	}
	
	/**
	 * Accessor method allowing clients to obtain the full URL of the source
	 * image processed.
	 * @param return The full URL to the source image.
	 */
	public abstract String getSourceImagePath();
	
	/**
	 * Accessor method allowing clients to obtain the full URL of the source
	 * image processed.
	 * @param return The full URL to the source image.
	 */
	public abstract String getOutputImagePath();
	
	/**
	 * Getter method for the output image height.
	 * @return The height (in pixels) of the output image.
	 */
	public abstract int getOutputImageHeight();
	
	/**
	 * Getter method for the output image width.
	 * @return The width (in pixels) of the output image.
	 */
	public abstract int getOutputImageWidth();
	
}
