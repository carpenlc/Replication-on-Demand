package mil.nga.rod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.rod.ejb.EJBClientUtilities;
import mil.nga.rod.ejb.MetricsService;
import mil.nga.rod.ejb.ProductQueryService;
import mil.nga.rod.ejb.ProductService;
import mil.nga.rod.model.Product;

public class RoDEJBClientUtilities {

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(RoDEJBClientUtilities.class);
    
    /**
     * Common header names in which the client CN is inserted
     */
    public static final String[] CLIENT_CN_HEADERS = {
        "X-SSL-Client-CN",
        "SSL_CLIENT_S_DN_CN",
        "USER_CN",
        "SM_USER",
        "SM_USER_CN"
    };
    
    /**
     * Common header names in which the client IP is inserted
     */
    public static final String[] CLIENT_IP_HEADERS = {
        "REMOTE_ADDR",
        "HTTP_CLIENT_IP",
        "X-FORWARDED-FOR",
        "HTTP_X_FORWARDED_FOR",
    };
    
    /**
     * Inject the EJB used to look up the store/retrieve product information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB
    protected ProductService productService;
    
    /**
     * Inject the EJB used to store/retrieve metrics information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB
    protected MetricsService metricsService;
    
    /**
     * Inject the EJB used to store/retrieve metrics information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB
    protected ProductQueryService productQueryService;
    
    /**
     * Debugging method used to dump the contents of the request headers 
     * retrieved from the JAX-RS framework.  The logic differs from the similar
     * method below in that the list of values is a <code>List</code> object 
     * as opposed to a simple array.
     * 
     * @param headers The request headers.
     * @return String listing the request headers.
     */
    protected String dumpHeaders(MultivaluedMap<String, String> headers) {
        
        int           count   = 0;
        String        newLine = System.getProperty("line.separator");
        StringBuilder sb      = new StringBuilder("Request Headers : ");
        
        if (headers != null) {
            if ((headers.keySet() != null) && 
                    (headers.keySet().size() > 0)) {
                for (String key : headers.keySet()) { 
                    List<String> values = headers.get(key);
                    if ((values != null) && (values.size() > 0)) {
                        sb.append("key => [ ");
                        sb.append(key);
                        sb.append(" ], values => [ ");
                        for (String value : values) {
                            if (count != 0) {
                                sb.append(", ");
                            }
                            sb.append(value);
                            count++;
                        }
                        sb.append(" ].");
                        sb.append(newLine);
                        count = 0;
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Test method used to dump the contents of the HTTP request headers.
     * 
     * @param headers The request headers.
     * @return String-based version of the request headers.
     */
    protected String dumpHeaders(Map<String, String[]> headers) {
        
        int           count   = 0;
        String        newLine = System.getProperty("line.separator");
        StringBuilder sb      = new StringBuilder("Request Headers : ");
        
        sb.append(newLine);
        
        if (headers != null) {
            if ((headers.keySet() != null) && 
                    (headers.keySet().size() > 0)) {
                
                for (String key : headers.keySet()) { 
                    String[] values = headers.get(key);
                    if ((values != null) && (values.length > 0)) {
                        sb.append("key => [ ");
                        sb.append(key);
                        sb.append(" ], values => [ ");
                        for (String value : values) {
                            if (count != 0) {
                                sb.append(", ");
                            }
                            sb.append(value);
                            count++;
                        }
                        sb.append(" ].");
                        sb.append(newLine);
                        count = 0;
                    }
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the MetricsService EJB.
     */
    protected MetricsService getMetricsService() {
        if (metricsService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to MetricsService.  Attempting to "
                    + "look it up via JNDI.");
            metricsService = EJBClientUtilities
                    .getInstance()
                    .getMetricsService();
        }
        return metricsService;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the ProductService EJB.
     */
    protected ProductService getProductService() {
        if (productService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to ProductService.  Attempting to "
                    + "look it up via JNDI.");
            productService = EJBClientUtilities
                    .getInstance()
                    .getProductService();
        }
        return productService;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the ProductQueryService EJB.
     */
    protected ProductQueryService getProductQueryService() {
        if (productQueryService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to ProductQueryService.  Attempting to "
                    + "look it up via JNDI.");
            productQueryService = EJBClientUtilities
                    .getInstance()
                    .getProductQueryService();
        }
        return productQueryService;
    }
    
    /**
     * Try a couple of different headers to see if we can get the users  
     * IP source for the incoming request.  About 50% of the time this function 
     * doesn't work because the AJAX callers do not insert the request
     * headers.
     * 
     * @param headers The request headers.
     * @return The client IP address if it could be extracted.
     */
    protected String getSourceIP(Map<String, String[]> headers) {
        
        String sourceIP = "unavailable";
        
        if ((headers != null) && 
                (headers.keySet() != null) && 
                (headers.keySet().size() > 0)) {
            for (String key : headers.keySet()) {
                for (String header : CLIENT_IP_HEADERS) {
                    if (header.equalsIgnoreCase(key)) {
                        sourceIP = headers.get(key)[0];
                        break;
                    }
                }
            }
        }
        else {
            LOGGER.warn("HTTP request headers are not available.");
        }
        return sourceIP;
    }
    
    /**
     * Try a couple of different headers to see if we can get the users  
     * IP source for the incoming request.  About 50% of the time this function 
     * doesn't work because the AJAX callers do not insert the request
     * headers.
     * 
     * @param headers The request headers.
     * @return The client IP address if it could be extracted.
     */
    protected String getSourceIP(MultivaluedMap<String, String> headers) {
        
        String sourceIP = "unavailable";
        
        if ((headers != null) && 
                (headers.keySet() != null) && 
                (headers.keySet().size() > 0)) {
            for (String key : headers.keySet()) {
                for (String header : CLIENT_IP_HEADERS) {
                    if (header.equalsIgnoreCase(key)) {
                        List<String> values = headers.get(key);
                        sourceIP = values.get(0);
                        break;
                    }
                }
            }
        }
        else {
            LOGGER.warn("HTTP request headers are not available.");
        }
        return sourceIP;
    }
    
    /**
     * Try a couple of different headers to see if we can get a user 
     * name for the incoming request.  About 50% of the time this function 
     * doesn't work because the AJAX callers do not insert the request
     * headers.
     * 
     * @param headers HTTP request headers
     * @return The username if it could be extracted from the headers
     */
    protected String getUser(Map<String, String[]> headers) {
        
        String user = "unavailable";
        
        if ((headers != null) && 
                (headers.keySet() != null) && 
                (headers.keySet().size() > 0)) {
            for (String key : headers.keySet()) {
                for (String header : CLIENT_CN_HEADERS) {
                    if (header.equalsIgnoreCase(key)) {
                        String[] values = headers.get(key);
                        user = values[0];
                        break;
                    }
                }
            }
        }
        else {
            LOGGER.warn("HTTP request headers are not available.");
        }
        return user;
    }
    
    /**
     * Try a couple of different headers to see if we can get a user 
     * name for the incoming request.  About 50% of the time this function 
     * doesn't work because the AJAX callers do not insert the request
     * headers.
     * 
     * @param headers HTTP request headers
     * @return The username if it could be extracted from the headers
     */
    protected String getUser(MultivaluedMap<String, String> headers) {
        
        String user = "unavailable";
        
        if ((headers != null) && 
                (headers.keySet() != null) && 
                (headers.keySet().size() > 0)) {
            for (String key : headers.keySet()) {
                for (String header : CLIENT_CN_HEADERS) {
                    if (header.equalsIgnoreCase(key)) {
                        List<String> values = headers.get(key);
                        user = values.get(0);
                        break;
                    }
                }
            }
        }
        else {
            LOGGER.warn("HTTP request headers are not available.");
        }
        return user;
    }
    
    /**
     * Utilize the EJB session beans to look up a list of available
     * product types.
     * 
     * @return The list of available product types.
     */
    protected List<Product> loadAllProducts() {
        List<Product> products = new ArrayList<Product>();
        if (getProductService() != null) {
            products = getProductService().getAllProducts();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of product types will be empty.");
        }
        return products;
    }
    
    /**
     * Utilize the EJB session beans to look up a list of available
     * countries.
     * 
     * @return The list of available product types.
     */
    protected List<String> loadCountries() {
        List<String> countries = new ArrayList<String>();
        if (getProductService() != null) {
            countries = getProductService().getCountries();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of countries will be empty.");
        }
        return countries;
    }
    
    /**
     * Utilize the EJB session beans to look up a list of available
     * product types.
     * 
     * @return The list of available product types.
     */
    protected List<String> loadProductTypes() {
        List<String> types = new ArrayList<String>();
        if (getProductService() != null) {
            types = getProductService().getProductTypes();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of product types will be empty.");
        }
        return types;
    }
    
    /**
     * Utilize the EJB session beans to look up a list of available
     * product types.
     * 
     * @return The list of available product types.
     */
    protected List<String> loadAORCodes() {
        List<String> codes = new ArrayList<String>();
        if (getProductService() != null) {
            codes = getProductService().getAORCodes();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the target EJB.  "
                    + "The returned list of AOR codes will be empty.");
        }
        return codes;
    }
    
}
