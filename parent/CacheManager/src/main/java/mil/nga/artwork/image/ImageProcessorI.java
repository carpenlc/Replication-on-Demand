package mil.nga.artwork.image;

import java.awt.image.BufferedImage;

/**
 * Interface that must be implemented by the ImageProcessor class.
 * 
 * @author carpenlc
 */
public interface ImageProcessorI {
	
	/**
	 * The process for obaining the source image will differ from one image 
	 * type to another.
	 * @param path The path to the source image.
	 * @return A BufferedImage representation of the source image.
	 */
	public BufferedImage getSourceImage(String path);
	
	
}
