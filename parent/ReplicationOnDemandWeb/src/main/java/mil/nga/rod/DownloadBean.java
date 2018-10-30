package mil.nga.rod;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import mil.nga.rod.model.DownloadRequest;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.RoDProduct;
import mil.nga.util.FileUtils;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Managed bean class introduced to handle the ISO file download. 
 * This bean is bound to commandButton added to each row in the filtered list 
 * of available ISOs.  When a download is requested this class 
 * 
 * Additional logic was added to enable tracking of metrics associated with
 * file downloads.  Data associated with the requested download and passed to 
 * the MetricsService EJB in order to track what products are being downloaded 
 * by who and when.
 *  
 * @author L. Craig Carpenter
 */
@ManagedBean
public class DownloadBean 
        extends RoDEJBClientUtilities 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -5193223272254043322L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(DownloadBean.class);
    
    /**
     * Length of the hexadecimal token used as the download request ID
     */
    private static int TOKEN_LENGTH = 8;
    
    /**
     * Handle to the streamed content object that will be returned when 
     * the download button is pressed.  This is essentially a stream 
     * attached to a target file.
     */
    private StreamedContent fileToDownload;
    
    /**
     * Extract the filename from the input path.
     * 
     * @param path The full path to the target file.
     * @return The size, in bytes, of the requested file.
     */
    private String getFilename(String path) {
        Path p = Paths.get(path);
        return p.getFileName().toString();
    }
    
    /**
     * Obtain the actual size associated with the on-disk file. 
     *  
     * @param path String defining the full path to the target file.
     * @return The size of the target file in bytes.  Zero is returned
     * if there are any problems accessing the target file.
     */
    private long getSize(String path) {
        long size = 0;
        try { 
            size = Files.size(Paths.get(path));
        } 
        catch (IOException ioe) {
            LOGGER.warn("Unexpected IOException raised while attempting "
                    + "to determine the size of file [ "
                    + path
                    + " ].  Exception message [ "
                    + ioe.getMessage()
                    + " ].");
        }
        return size;
        
    }
    
    /**
     * This method will gather the data necessary to log a download request
     * for metrics tracking purposes. This does not raise any exceptions 
     * and makes an asynchronous call as to not impact the query response
     * times.
     * 
     * @param product The product requested.
     * @param size The size of the target file.
     * @param requestHeaders The incoming request headers.
     */
    private void logDownloadRequest(
            RoDProduct            product,
            Map<String, String[]> requestHeaders) {
        
        // Collect the required data not in the input product object.
        String username = getUser(requestHeaders);
        String clientIP = getSourceIP(requestHeaders);
        String host     = FileUtils.getHostName();
        long   size     = getSize(product.getPath());
        
        // Build the POJO containing the request data.
        DownloadRequest request = new DownloadRequest.DownloadRequestBuilder()
                //.aorCode(product.getAorCode())
                //.countryName(product.getCountryName())
                .requestDate(new Date(System.currentTimeMillis()))
                .fileSize(size)
                .hostName(host)
                .nrn(product.getNRN())
                .nsn(product.getNSN())
                .productType(product.getProductType())
                .username(username)
                .path(product.getPath())
                .source(clientIP)
                .build();
            
        // Persist the data.
        if (getMetricsService() != null) {
            getMetricsService().logDownloadRequest(request);
        }
        else {
            LOGGER.warn("Unable to obtain a reference to the MetricsService "
                    + "EJB.  Download request will not be logged. ");
        }
    }
    
    /**
     * This is the interface utilized by PrimeFaces to obtain a Stream 
     * attached to an on-disk file.  The stream
     * will be used to actually download the file contents.
     * 
     * @param product The Object containing the information associated 
     * with the product to be downloaded.
     * @return A stream attached to the requested file.
     */
    public StreamedContent getFile(
            RoDProduct product) {
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User requested download of file [ "
                    + product.getPath()
                    + " ] with URL [ "
                    + product.getURL()
                    + " ].");
        }
        
        if ((product != null) && 
                (product.getPath() != null) && 
                (!product.getPath().isEmpty())) {
            
            Path p = Paths.get(product.getPath());
            
            if ((p != null) && (Files.exists(p))) {
                
                try {
                    
                    fileToDownload = new DefaultStreamedContent(
                            new FileInputStream(
                                    product.getPath()),
                                    "application/octet-stream", 
                                    getFilename(product.getPath()));
                
                    // Get the request headers 
                    Map<String, String[]> requestHeaders = 
                            FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getRequestHeaderValuesMap();
                    
                    // Log the download request
                    logDownloadRequest(
                            product,
                            requestHeaders);
                }
                catch (FileNotFoundException fnfe) {
                    
                    LOGGER.error("Unexpected FileNotFoundException "
                            + "encountered while attempting to construct "
                            + "a FileInputStream to file [ "
                            + product.getPath()
                            + " ].  Error message details [ "
                            + fnfe.getMessage()
                            + " ].  (This should not happen.)");
                    FacesContext.getCurrentInstance().addMessage(
                            null, 
                            new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR, 
                                    "Error!", 
                                    "Requested file does not exist on the server."));
                }
            }
            else {
                FacesContext.getCurrentInstance().addMessage(
                        null, 
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR, 
                                "Error!", 
                                "Requested file does not exist on the server."));
            }
        }
        else {
            LOGGER.warn("Faces client returned an empty Product object as the "
                    + "target for download.");
            FacesContext.getCurrentInstance().addMessage(
                    null, 
                    new FacesMessage(
                            FacesMessage.SEVERITY_FATAL, 
                            "Fatal!", 
                            "Error validating file."));
        }
        return fileToDownload;
    }
}
