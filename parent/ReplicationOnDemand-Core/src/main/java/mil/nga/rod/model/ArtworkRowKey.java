package mil.nga.rod.model;

import java.io.Serializable;

/**
 * The legacy table containing the artwork data does not have a primary
 * key.  This class was created in order to utilize JPA/Hibernate for 
 * retrieving Artwork information from the legacy Oracle database tables.
 *  
 * @author L. Craig Carpenter
 */
public class ArtworkRowKey implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -4528940966301458855L;
	
    private String nrn;
    private String nsn;

	public ArtworkRowKey() {}
	public ArtworkRowKey(String nrn, String nsn) {
		setNrn(nrn);
		setNsn(nsn);
	}
	
    /**
     * Getter method for the NRN attribute.
     * @return The NRN attribute.
     */
    public String getNrn() {
    	return nrn;
    }
    
    /**
     * Getter method for the NSN attribute.
     * @return The NSN attribute.
     */
    public String getNsn() {
    	return nsn;
    }
    
    /**
     * Setter method for the NRN attribute.
     * @param value The NRN attribute.
     */
    public void setNrn(String value) {
    	nrn = value;
    }
    
    /**
     * Setter method for the NSN attribute.
     * @param value The NSN attribute.
     */
    public void setNsn(String value) {
    	nsn = value;
    }
}
