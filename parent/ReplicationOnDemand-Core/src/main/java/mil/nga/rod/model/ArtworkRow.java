package mil.nga.rod.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Simple POJO containing the data associated with a single artwork record
 * associated with the "Replication on Demand" project.  This POJO is the 
 * data stored in a database table.  It does not contain all of the information
 * required for the RoD project.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="GW_PUB.RPF_CIB_ARTWORK")
@IdClass(ArtworkRowKey.class)
@JsonDeserialize(builder = ArtworkRow.ArtworkBuilder.class)
public class ArtworkRow implements Serializable {

	/**
	 * Eclipse-generated SerialVersionUID
	 */
	private static final long serialVersionUID = 3782674460717016201L;

	/**
	 * The name of the product CD
	 */
	@Column(name="CD_NAME")
	private String cdName;
	
	/**
	 * NRN String for the product.  Also part of the composite key.
	 */
	@Id
	@Column(name="NRN")
    private String nrn;
	
	/**
	 * NSN String for the product.  Also part of the composite key.
	 */
	@Id
	@Column(name="NSN")
    private String nsn;
	/**
	 * The path to the target Artwork ZIP file.
	 */
    @Column(name="FOLDER_PATH")
    private String path;
    
    /**
     * The size of the file containing the Artwork.
     */
	@Column(name="FILE_SIZE")
    private Long size;
	
	/**
	 * Default public constructor required for Hibernate.
	 */
	public ArtworkRow(){}
	
    /**
     * Constructor used to set all of the required internal members.
     * 
     * @param builder Populated builder object.
     */
	protected ArtworkRow(ArtworkBuilder builder) {
		cdName   = builder.cdName;
	    path     = builder.path;
		size     = builder.size;
		nrn      = builder.nrn;
		nsn      = builder.nsn;
	}

	/**
	 * The name of the associated CD.
	 * @return The name of the associated CD.
	 */
	public String getCDName() {
		return cdName;
	}
	
    /**
     * Getter method for the NRN number.
     * @return The NRN number.
     */
	public String getNrn() {
		return nrn;
	}
	
    /**
     * Getter method for the NSN number.
     * @return The NSN number.
     */
	public String getNsn() {
		return nsn;
	}
	
	/**
	 * Getter method for the path to the artwork image.
	 * @return The path to the artwork image.
	 */
	public String getPath() {
		return path;
	}
	
	/** 
	 * Getter method for the size of the artwork image.
	 * @return The size of the artwork image.
	 */
	public long getSize() {
		if (size == null) {
			size = new Long(0);
		}
		return size.longValue();
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Artwork:  NRN => [ ");
		sb.append(getNrn());
		sb.append(" ], NSN => [ ");
		sb.append(getNsn());
		sb.append(" ], CD Name => [ ");
		sb.append(getCDName());
		sb.append(" ], size => [ ");
		sb.append(getSize());
		sb.append(" ], path => [ ");
		sb.append(getPath());
		sb.append(" ].");
		return sb.toString();
	}
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new Artwork objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class ArtworkBuilder {
    	
    	private String cdName;
        private String nrn;
        private String nsn;
        private String path;
    	private Long   size;
    	
        /**
         * Method used to actually construct the UPGData object.
         * @return A constructed and validated UPGData object.
         */
        public ArtworkRow build() throws IllegalStateException {
        	ArtworkRow object = new ArtworkRow(this);
            validate(object);
            return object;
        }
        
        /**
         * Setter method for the <code>CD_NAME</code> attribute.
         * @param value The <code>CD_NAME</code> attribute.
         */
        public ArtworkBuilder cdName(String value) {
            if (value != null) {
            	cdName = value.trim();
            }
            return this;
        }
    	
        /**
         * Setter method for the <code>NRN</code> attribute.
         * @param value The <code>NRN</code> attribute.
         */
        public ArtworkBuilder nrn(String value) {
            if (value != null) {
                nrn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the <code>NSN</code> attribute.
         * @param value The <code>NSN</code> attribute.
         */
        public ArtworkBuilder nsn(String value) {
            if (value != null) {
                nsn = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the <code>FOLDER_PATH</code> attribute.
         * @param value The <code>FOLDER_PATH</code> attribute.
         */
        public ArtworkBuilder path(String value) {
            if (value != null) {
                path = value.trim();
            }
            return this;
        }
        
        /**
         * Setter method for the <code>FILE_SIZE</code> attribute.
         * @param value The <code>FILE_SIZE</code> attribute.
         */
        public ArtworkBuilder size(Long value) {
        	if (value != null) {
        		size = value;
        	}
        	else {
        		size = new Long(0);
        	}
            return this;
        }
        
        /**
         * Validate internal member variables.
         * @param object The Artwork object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
    	public void validate(ArtworkRow object) {
    		
            if ((object.getCDName() == null) || (object.getCDName().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Artwork object but the value for CD_NAME "
                        + "was null.  Artwork object => [ "
                        + object.toString()
                        + " ].");
            }
            if ((object.getNsn() == null) || (object.getNsn().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Artwork object but the value for NSN "
                        + "was null.  Artwork object => [ "
                        + object.toString()
                        + " ].");
            }
            
            if ((object.getNrn() == null) || (object.getNrn().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Artwork object but the value for NRN "
                        + "was null.   Artwork object => [ "
                        + object.toString()
                        + " ].");
            }
            
            if ((object.getPath() == null) || (object.getPath().isEmpty())) {
                throw new IllegalStateException("Attempted to build "
                        + "Artwork object but the value for FOLDER_PATH "
                        + "was null.");
            }
    	}
    }
}
