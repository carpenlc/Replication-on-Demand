package mil.nga.rod.interfaces;

/**
 * Define the methods that the <cod>Artwork</code> class must implement in 
 * order to expose all of the data required for the RoD application.
 * 
 * @author L. Craig Carpenter
 */
public interface ArtworkRowI {

	/**
	 * The name of the associated CD.
	 * @return The name of the associated CD.
	 */
	public String getCdName();
	
	/**
	 * Getter method for the path to the artwork image.
	 * @return The path to the artwork image.
	 */
	public String getArtworkPath();
	
	/** 
	 * Getter method for the size of the artwork image.
	 * @return The size of the artwork image.
	 */
	public long getArtworkSize();
}
