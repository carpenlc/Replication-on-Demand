package mil.nga.rod.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * POJO used to hold the response associated with a client-initiated 
 * query of the ISO files generated under the "Replication on Demand"
 * project.
 * 
 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonRootName(value="results")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductQueryResponse implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -2146231769774999045L;
    
    /**
     * Private internal list containing the ISO files matching the client
     * initiated product query.
     */
    @JsonProperty(value="iso_files")
    private List<ISOFile> queryResponse = new ArrayList<ISOFile>();
    
    /**
     * Default constructor.
     */
    public ProductQueryResponse() {}
    
    /**
     * Getter method for the list ISO files.
     * @return The list of ISO files.  Will not be null, but may be empty.
     */
    public List<ISOFile> getISOFiles() {
        return queryResponse;
    }
    
    /**
     * Method allowing users to add individual ISO file objects to the 
     * list that will be returned to the client.
     * 
     * @param file A populated ISOFile object.
     */
    public void addISOFile(ISOFile file) {
        if (file != null) {
            queryResponse.add(file);
        }
    }
    
    /**
     * Convert the object into a human-readable string.
     */
    public String toString() {
        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("Product Query Response Object: (Num Results [ ");
        sb.append(getISOFiles().size());
        sb.append(" ])");
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        for (ISOFile file : getISOFiles()) {
            sb.append(file.toString());
        }
        return sb.toString();
    }
}
