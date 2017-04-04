package mil.nga.rod.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO used to hold the contents of a client-initiated query for ISO
 * products under the replication-on-demand project.
 * 
 * This object utilizes both JAX-B and Jackson annotations for 
 * marshalling/unmarshalling client supplied JSON data.  Object is populated
 * by a RESTful (JAX-RS) service call via POST.
 * 
 * The date field must be supplied by the client.  The rest of the fields are 
 * optional and allow the client narrow down the search to specific combinations
 * of product, AOR, or country.
 * 
 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductQueryRequest implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 6875790219878911447L;
	
	/** 
	 * Expected format associated with dates coming in from callers.
	 */
	public static final String INPUT_DATE_FORMAT_STRING = "yyyy-MM-dd";
	
	// Private internal members
	private String aorCode;
	private String countryName;
	private String loadDate;
	private String productType;
	
	/**
	 * Default constructor.
	 */
	public ProductQueryRequest () { }
	
	/**
	 * Getter method for the AOR code attribute.
	 * @return The AOR code attribute.
	 */
	@XmlElement(name="aor_code")
	@JsonProperty(value="aor_code")
	public String getAORCode() {
		return aorCode;
	}
	
	/**
	 * Getter method for the country name.
	 * @return The country name.
	 */
	@XmlElement(name="country_name")
	@JsonProperty(value="country_name")
	public String getCountryName() {
		return countryName;
	}
	
	/**
	 * Getter method for the load date.
	 * @return The load date.
	 */
	@XmlElement(name="load_date")
	@JsonProperty(value="load_date")
	public String getLoadDate() {
		return loadDate;
	}
	
	/**
	 * Getter method for the product type.
	 * @return The product type.
	 */
	@XmlElement(name="product_type")
	@JsonProperty(value="product_type")
	public String getProductType() {
		return productType;
	}
	
	/**
	 * Setter method for the AOR code attribute.
	 * @param value The AOR code attribute.
	 */
	public void setAORCode(String value) {
		if ((value != null) && (!value.isEmpty())) { 
			aorCode = value.toUpperCase();
		}
	}
	
	/**
	 * Setter method for the country name.
	 * @param value The country name.
	 */
	public void setCountryName(String value) {
		if (value != null) {
			countryName = value.trim();
		}
	}
	
	/**
	 * Setter method for the load date.
	 * 
	 * @param value The load date.
	 */
	public void setLoadDate(String value) {
		if (value != null) {
			loadDate = value.trim();
		}
	}
	
	/**
	 * Setter method for the product type.
	 * @param value The product type.
	 */
	public void setProductType(String value) {
		if ((value != null) && (!value.isEmpty())) { 
			productType = value.toUpperCase();
		}
	}
	
	/**
	 * Convert the incoming message to human-readable format.  This method
	 * does not validate, or assume validation is done.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ISO query : Load Date => [ ");
		sb.append(getLoadDate());
		sb.append(" ]");
		if ((getAORCode() != null) && (!getAORCode().isEmpty())) { 
			sb.append(", AOR => [ ");
			sb.append(getAORCode());
			sb.append(" ]");
		}
		else { 
			sb.append(", AOR => [ null ]");
		}
		if ((getCountryName() != null) && (!getCountryName().isEmpty())) { 
			sb.append(", Country => [ ");
			sb.append(getCountryName());
			sb.append(" ]");
		}
		else { 
			sb.append(", Country => [ null ]");
		}
		if ((getProductType() != null) && (!getProductType().isEmpty())) { 
			sb.append(", Product => [ ");
			sb.append(getProductType());
			sb.append(" ]");
		}
		else { 
			sb.append(", Product => [ null ]");
		}
		return sb.toString();
	}
}
