package mil.nga.rod.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import mil.nga.rod.interfaces.ArtworkRowI;

/**
 * This class "decorates" the production database row containing the 
 * Artwork information for a given product with the path and URL for
 * the reduced resolution images.
 * 
 * @author L. Craig Carpenter
 */
public class Artwork extends ArtworkDecorator implements ArtworkRowI, Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 3782674460717016201L;

	private final String thumbnailImageUrl;
	private final String thumbnailImagePath;
	private final String smallImageUrl;
	private final String smallImagePath;
	private final String sourceImagePath;
	private final String sourceImageUrl;
	
    /**
     * Constructor used to set all of the required internal members.
     * @param builder Populated builder object.
     */
	protected Artwork (ArtworkBuilder builder) {
		super(builder.artworkRow);
		thumbnailImageUrl  = builder.thumbnailImageUrl;
		thumbnailImagePath = builder.thumbnailImagePath;
		smallImageUrl      = builder.smallImageUrl;
		smallImagePath     = builder.smallImagePath;
		sourceImagePath    = builder.sourceImagePath;
		sourceImageUrl     = builder.sourceImageUrl;
	}
	
	/**
	 * The name of the associated CD.
	 * @return The name of the associated CD.
	 */
	@Override
	public String getCdName() {
		return getArtworkRow().getCDName();
	}
	
	/**
	 * Getter method for the path to the artwork image.
	 * @return The path to the artwork image.
	 */
	@Override
	public String getArtworkPath() { 
		return getArtworkRow().getPath();
	}
	
	/** 
	 * Getter method for the size of the artwork image.
	 * @return The size of the artwork image.
	 */
	@Override
	public long getArtworkSize() { 
		return getArtworkRow().getSize();
	}
	
	/**
	 * Getter method for the on-disk path for the "small" image.
	 * @return The on-disk path for the small image used for web-site display.
	 */
	public String getSmallImagePath() {
		return smallImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the "small" image.
	 * @return The URL of the small image for web-site display.
	 */
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	
	/**
	 * Getter method for the on-disk path for the source image.
	 * @return The on-disk path for the source image used for web-site display.
	 */
	public String getSourceImagePath() {
		return sourceImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the source image.
	 * @return The URL of the source image for web-site display.
	 */
	public String getSourceImageUrl() {
		return sourceImageUrl;
	}
	
	/**
	 * Getter method for the on-disk path for the thumbnail image.
	 * @return The on-disk path for the thumbnail image used for web-site display.
	 */
	public String getThumbnailImagePath() {
		return thumbnailImagePath;
	}
	
	/**
	 * Getter method for the URL associated with the thumbnail image.
	 * @return The URL of the thumbnail image for web-site display.
	 */
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	
	/**
	 * Generate human-readable String.
	 */
	public String toString() {
		
		StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        
        sb.append(newLine);
        sb.append(super.toString());
        sb.append(" Artwork reduced images:  ");
        sb.append("Source Image Path => [ ");
        sb.append(getSourceImagePath());
        sb.append(" ], Source image URL => [ ");
        sb.append(getSourceImageUrl());
        sb.append(" ], Small Image Path => [ ");
        sb.append(getSmallImagePath());
        sb.append(" ], Small image URL => [ ");
        sb.append(getSmallImageUrl());
        sb.append(" ], Thumbnail Image Path => [ ");
        sb.append(getThumbnailImagePath());
        sb.append(" ], Thumbnail Image URL => [ ");
        sb.append(getThumbnailImageUrl());
        sb.append(" ].");
        sb.append(newLine);
        return sb.toString();
	}
	
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new Product objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class ArtworkBuilder {
    	
    	private ArtworkRow artworkRow         = null; 
    	private String     thumbnailImageUrl  = null;
    	private String     thumbnailImagePath = null;
    	private String     smallImageUrl      = null;
    	private String     smallImagePath     = null;
    	private String     sourceImagePath    = null;
    	private String     sourceImageUrl     = null;
    	
        /**
         * Method used to actually construct the Artwork object
         * @return A constructed and validated Artwork object.
         */
    	public Artwork build() {
    		Artwork obj = new Artwork(this);
    		validate(obj);
    		return obj;
    	}
    	
    	/**
    	 * Setter method for the artwork information stored in the datasource.
    	 * @return The artwork information stored in the datasource.
    	 */
    	public ArtworkBuilder artworkRow(ArtworkRow value) {
    		if (value != null) {
    			artworkRow = value;
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the on-disk path for the "small" image.
    	 * @return The on-disk path for the small image used for web-site display.
    	 */
    	public ArtworkBuilder smallImagePath(String value) {
    		if (value != null) {
    			smallImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the URL associated with the "small" image.
    	 * @return The URL of the small image for web-site display.
    	 */
    	public ArtworkBuilder smallImageUrl(String value) {
    		if (value != null) {
    			smallImageUrl = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the on-disk path for the source image.
    	 * @return The on-disk path for the small image used for web-site display.
    	 */
    	public ArtworkBuilder sourceImagePath(String value) {
    		if (value != null) {
    			sourceImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the URL associated with the source image.
    	 * @return The URL of the small image for web-site display.
    	 */
    	public ArtworkBuilder sourceImageUrl(String value) {
    		if (value != null) {
    			sourceImageUrl = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the on-disk path for the thumbnail image.
    	 * @return The on-disk path for the thumbnail image used for web-site display.
    	 */
    	public ArtworkBuilder thumbnailImagePath(String value) {
    		if (value != null) {
    			thumbnailImagePath = value.trim();
    		}
    		return this;
    	}
    	
    	/**
    	 * Getter method for the URL associated with the thumbnail image.
    	 * @return The URL of the thumbnail image for web-site display.
    	 */
    	public ArtworkBuilder thumbnailImageUrl(String value) {
    		if (value != null) {
    			thumbnailImageUrl = value.trim();
    		}
    		return this;
    	}
    	
        /**
         * Validate internal member variables.
         * @param object The Artwork object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validate(Artwork obj) 
        		throws IllegalStateException {
        	if ((obj.getSourceImagePath() == null) ||
        			(obj.getSourceImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for sourceImagePath "
        				+ "not populated.");
        	}
        	if ((obj.getSourceImageUrl() == null) ||
        			(obj.getSourceImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for sourceImageUrl "
        				+ "not populated.");
        	}
        	if ((obj.getSmallImagePath() == null) ||
        			(obj.getSmallImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for smallImagePath "
        				+ "not populated.");
        	}
        	if ((obj.getSmallImageUrl() == null) ||
        			(obj.getSmallImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for smallImageUrl "
        				+ "not populated.");
        	}
        	if ((obj.getThumbnailImagePath() == null) ||
        			(obj.getSmallImagePath().isEmpty())) {
        		throw new IllegalStateException("Value for thumbnailImagePath "
        				+ "not populated.");
        	}
        	if ((obj.getThumbnailImageUrl() == null) ||
        			(obj.getSmallImageUrl().isEmpty())) {
        		throw new IllegalStateException("Value for thumbnailImageUrl "
        				+ "not populated.");
        	}
        }
    }
}
