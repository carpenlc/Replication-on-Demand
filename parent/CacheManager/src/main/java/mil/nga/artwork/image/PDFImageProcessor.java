package mil.nga.artwork.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete class that will attempt to convert a PDF into a thumbnail image for
 * web site display.  PDFs are common when associated with SUPIR
 * intel product types.  This class has dependencies on the following two Apache
 * projects:
 * 
 * Apache PDFBox (for loading and converting PDF files)
 * 
 * Note: Converting PDFs to a reduced-resolution thumbnail requires a huge 
 * amount of memory.  Maintainers of this software should monitor for heap space 
 * exceptions.  Further, there are limitations to using the PDFBox software.  
 * The most common is conversion of PDF files that contain fonts that are unknown 
 * to, or not handled by the free open source PDFBox library. 
 * 
 * @author L. Craig Carpenter
 */
public class PDFImageProcessor extends ImageProcessor 
		implements ImageProcessorI, Runnable {
		
	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOG = LoggerFactory.getLogger(
			PDFImageProcessor.class);
	
    /**
     * Constructor used to set all of the required internal members.
     * @param builder Populated builder object.
     */
	protected PDFImageProcessor(PDFImageProcessorBuilder builder) {
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
	 * Convert the first page of a PDF document to a BufferedImage.
	 * @param path The on-disk path to the target PDF file.
	 * @return A BufferedImage object representing the first page of the input 
	 * PDF.
	 */
	@Override
	public BufferedImage getSourceImage(String pathToImage) {
		
		BufferedImage image  = null;
		
		if ((pathToImage != null) && (!pathToImage.isEmpty())) {
			Path p = Paths.get(URI.create(pathToImage));
			try (InputStream is = Files.newInputStream(p);
					final PDDocument doc = PDDocument.load(is)) {
				if ((doc != null) && (doc.getNumberOfPages() > 0)) {
					PDFRenderer renderer = new PDFRenderer(doc);
					image = renderer.renderImage(0);
				}
				else {
					LOG.error("Unable to open the target PDF document.  The "
							+ "document was null, or did not contain any pages.");
				}
			}
			catch (IOException ioe) {
				LOG.error("Unexpected IOException encountered while attempting "
						+ "to open PDF document [ "
						+ pathToImage
						+ " ].  Error message => [ "
						+ ioe.getMessage()
						+ " ].");
			}
		}
		else {
			LOG.error("The input image path is null or undefined.  Unable "
					+ "to load the target image.");
		}
		return image;
	}
	
	/**
	 * Overridden run() method that drives the creation of thumbnail images
	 * for use in the Rotator widget.  This method retrieves the target 
	 * PDF (via HTTP Get), converts the PDF into an image, and then creates a 
	 * reduced resolution thumbnail which is stored in the image cache.
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
     * new <code>PDFImageProcessor</code> objects.  
     * 
     * @author L. Craig Carpenter
     */
    public static class PDFImageProcessorBuilder {
    	
    	private int    outputImageWidth  = -1;
    	private int    outputImageHeight = -1;
    	private String outputImagePath   = "";
    	private String sourceImagePath   = "";
    	
        /**
         * Method used to actually construct the 
         * <code>PDFImageProcessor</code> object
         * @return A constructed and <code>PDFImageProcessor</code> Artwork 
         * object.
         */
    	public PDFImageProcessor build() {
    		PDFImageProcessor object = new PDFImageProcessor(this);
    		validate(object);
    		return object;
    	}
    	
    	/**
    	 * Setter method for the client-requested image width.
    	 * @param value The client-requested image width.
    	 */
    	public PDFImageProcessorBuilder outputImageWidth(int value) {
    		outputImageWidth = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the client-requested image height.
    	 * @param value The client-requested image height.
    	 */
    	public PDFImageProcessorBuilder outputImageHeight(int value) {
    		outputImageHeight = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the client-requested path to the output image.
    	 * @param value The client-requested path to the output image.
    	 */
    	public PDFImageProcessorBuilder outputImagePath(String value) {
    		outputImagePath = value;
    		return this;
    	}
    	
    	/**
    	 * Setter method for the path to the source image.
    	 * @param value The path to the source image.
    	 */
    	public PDFImageProcessorBuilder sourceImagePath(String value) {
    		sourceImagePath = value;
    		return this;
    	}
    	
        /**
         * Validate internal member variables.
         * @param object The <code>PDFImageProcessor</code> object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
    	private void validate(PDFImageProcessor object) {
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
