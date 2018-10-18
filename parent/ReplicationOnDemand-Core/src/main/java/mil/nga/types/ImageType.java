package mil.nga.types;

import mil.nga.exceptions.UnknownImageTypeException;

/**
 * Enumeration type identifying the image types supported by the image 
 * processing/scaling algorithms.
 * 
 * @author L. Craig Carpenter
 */
public enum ImageType {
	JPG("jpg"),
	BMP("bmp"),
	PNG("png"),
	GIF("gif"),
	PDF("pdf");
    
    /**
     * The text field.
     */
    private final String text;
    
    /**
     * Default constructor.
     * 
     * @param text Text associated with the enumeration value.
     */
    private ImageType(String text) {
        this.text = text;
    }
    
    /**
     * Getter method for the text associated with the enumeration value.
     * 
     * @return The text associated with the instanced enumeration type.
     */
    public String getText() {
        return this.text;
    }
	    
    /**
     * Convert an input String to it's associated enumeration type.  There
     * is no default type, if an unknown value is supplied an exception is
     * raised.
     * 
     * @param text Input text information
     * @return The appropriate ImageType enum value.
     * @throws UnknownImageTypeException Thrown if the caller submitted a String 
     * that did not match one of the existing HashTypes. 
     */
    public static ImageType fromString(String text) 
            throws UnknownImageTypeException {
        if (text != null) {
            for (ImageType type : ImageType.values()) {
                if (text.trim().equalsIgnoreCase(type.getText())) {
                    return type;
                }
            }
        }
        throw new UnknownImageTypeException("Unknown image type requested!  " 
                + "Image requested [ " 
                + text
                + " ].");
    }
}
