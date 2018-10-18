package mil.nga.artwork.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.types.ImageType;

/**
 * Factory class providing a concrete instance of the 
 * <code>ImageProcessorI</code> interface that will be used to generate scaled
 * images for we-site display.
 *  
 * @author L. Craig Carpenter
 */
public class ImageProcessorFactory {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		ImageProcessorFactory.class);
    
    /**
     * Hidden constructor enforcing the Singleton design pattern.
     */
    private ImageProcessorFactory () {}
    
    /**
     * Public method providing access to the singleton instance of the factory
     * object.
     * @return The ImageProcessorFactory reference.
     */
    public static ImageProcessorFactory getInstance() {
    	return ImageProcessorFactoryHolder.getSingleton();
    }
    
    /**
     * Method building a concrete instance of the class responsible for 
     * processing the input image type.
     * 
     * @param type The type of the source image.
     * @param outputWidth The requested width of the output file.
     * @param outputHeight The requested height of the output file.
     * @param outputImage Path to the output image.
     * @param sourceImage Path to the input image.
     * @return Concrete class used to perform the image scaling functions.
     */
    public Runnable getImageProcessor(
    		ImageType type,
    		int       outputWidth,
    		int       outputHeight,
    		String    outputImage,
    		String    sourceImage) {
    	
    	Runnable processor = null;
    	
    	switch (type) {
    		case PDF: 
    			processor = new PDFImageProcessor.PDFImageProcessorBuilder()
    				.outputImageHeight(outputHeight)
    				.outputImageWidth(outputWidth)
    				.outputImagePath(outputImage)
    				.sourceImagePath(sourceImage)
    				.build();
    			break;
    		case JPG:
    		case BMP:
    		case PNG:
    		case GIF:
    			processor = new JPGImageProcessor.JPGImageProcessorBuilder()
					.outputImageHeight(outputHeight)
					.outputImageWidth(outputWidth)
					.outputImagePath(outputImage)
					.sourceImagePath(sourceImage)
					.build();
    			break;
    		default:
    			throw new IllegalStateException("Input type object is "
    					+ "unknown.");
    	}
    	return processor;
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class ImageProcessorFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the ImageProcessorFactory.
         */
        private static ImageProcessorFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * RoDProductFactory.
         * 
         * @return The Singleton instance of the RoDProductFactory.
         */
        public static ImageProcessorFactory getSingleton() {
            if (_instance == null) {
                _instance = new ImageProcessorFactory();
            }
            return _instance;
        }
    }
}
