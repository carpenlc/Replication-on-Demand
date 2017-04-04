package mil.nga.rod.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import mil.nga.exceptions.InvalidQueryRequestException;
import mil.nga.rod.messages.ISOFile;
import mil.nga.rod.messages.ProductQueryRequest;
import mil.nga.rod.messages.ProductQueryResponse;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.types.HashType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ProductQueryService
 * 
 * SessionBean class responsible for constructing the response associated
 * with an input client product query request.  This class contains the 
 * query validation routines. 
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class ProductQueryService implements Serializable {

    /**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 8841073600682377386L;

	/**
	 * Set up the Log4j system for use throughout the class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(
			ProductQueryService.class);
	
	/**
	 * Container-injected reference to the hash generator service.
	 */
	@EJB
	HashGeneratorService hashGeneratorService;
	
	/**
	 * Container-injected reference to the Product Query Accelerator service.
	 */
	@EJB
	ProductQueryRequestAcceleratorService acceleratorService;
	
	/**
	 * Container-injected reference to the Product service.
	 */
	@EJB
	ProductService productService;
	
	/**
     * Default constructor. 
     */
    public ProductQueryService() { }
    
    /**
     * Access the target file to get the actual date of the on-disk file.  
     * This method was added because the date information stored in the 
     * database is always the same as the load date.
     * 
     * @param file String path to the target file.
     * @return The date of the file.
     * @throws IOException Thrown if there are problems accessing the target
     * file.
     */
    private java.sql.Date getActualFileDate(String file) throws IOException {
    	
    	java.sql.Date fileDate = new java.sql.Date(0);
    	
    	if ((file != null) && (!file.isEmpty())) {
    		Path p = Paths.get(file);
    		BasicFileAttributes attrs = Files.getFileAttributeView(
    				p, 
    				BasicFileAttributeView.class).readAttributes();
    		FileTime t = attrs.lastModifiedTime();
    		// LOGGER.info("File time : " + t);
    		fileDate = new java.sql.Date(t.toMillis());
    	}
    	else {
    		LOGGER.warn("The required input filename was not supplied.");
    	}
    	return fileDate;
    }
     
    /**
     * Simple internal method used to convert the date in String format
     * to a java.sql.Date object for use in querying the target data 
     * store.
     * 
     * @param date Client-supplied date in String format.
     * @return populated <code>java.sql.Date</code> object
     */
    private java.sql.Date getDateSQL(String date) {
    	
    	java.sql.Date sqlDate = null;
    	
    	try {
	    	java.util.Date utilDate = (new SimpleDateFormat(
	    			ProductQueryRequest.INPUT_DATE_FORMAT_STRING))
	    			.parse(date);
	    	sqlDate = new java.sql.Date(utilDate.getTime());
	    }
    	catch (ParseException pe) {
    		LOGGER.error("Unexpected ParseException raised while attempting "
    				+ "to create a java.sql.Date from the input String [ "
    				+ date
    				+ " ].  This exception should never be raised as the "
    				+ "date should have already been validated.");
    	}
    	return sqlDate;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the HashGeneratorService EJB.
     */
    private HashGeneratorService getHashGeneratorService() {
    	if (hashGeneratorService == null) {
    		LOGGER.warn("Application container failed to inject the "
    				+ "reference to HashGeneratorService.  Attempting to "
    				+ "look it up via JNDI.");
    		hashGeneratorService = EJBClientUtilities
    				.getInstance()
    				.getHashGeneratorService();
    	}
    	return hashGeneratorService;
    }
    
    /**
     * Method used to retrieve the list of products contained in the data
     * store that match the incoming parameters.
     * 
     * @param query The validated <code>ProductQueryRequest</code> object 
     * containing the query parameters.
     * @return A List of <code>Product</code> objects (may be null).
     */
    private List<Product> getProductList(ProductQueryRequest query) {
    	
    	List<Product> products = null;
    	
    	if (getProductService() != null) {
    		if ((query.getAORCode() != null) && 
    				(!query.getAORCode().isEmpty())) {
    			
    			LOGGER.info("Initiating query for products that fall within "
    					+ "AOR [ "
    					+ query.getAORCode()
    					+ " ] and have a file date after [ "
    					+ query.getLoadDate()
    					+ " ].");
    			products = getProductService().getProductsByDateAndAOR(
    					query.getAORCode(),
    					getDateSQL(query.getLoadDate()));
    			
    		}
    		else if ((query.getCountryName() != null) &&
    				(!query.getCountryName().isEmpty())) {
      			
    			LOGGER.info("Initiating query for products that fall within "
    					+ "country [ "
    					+ query.getCountryName()
    					+ " ] and have a file date after [ "
    					+ query.getLoadDate()
    					+ " ].");
    			products = getProductService().getProductsByDateAndCountry(
    					query.getCountryName(),
    					getDateSQL(query.getLoadDate()));
    			
    		}
    		else if ((query.getProductType() != null) &&
    				(!query.getProductType().isEmpty())) {
    			
    			LOGGER.info("Initiating query for of type [ "
    					+ query.getProductType()
    					+ " ] and have a file date after [ "
    					+ query.getLoadDate()
    					+ " ].");
    			products = getProductService().getProductsByDateAndType(
    					query.getProductType(),
    					getDateSQL(query.getLoadDate()));
    			
    		}
    		else {
    			
    			LOGGER.info("Initiating query products with a file date "
    					+ "after [ "
    					+ query.getLoadDate()
    					+ " ].");
    			products = getProductService().getProductsByDate(
    					getDateSQL(query.getLoadDate()));
    			
    		}
    	}
    	else {
    		LOGGER.error("Internal application error.  Unable to obtain a "
    				+ "reference to the ProductService EJB.");
    	}
    	return products;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the getProductQueryRequestAcceleratorService EJB.
     */
    private ProductQueryRequestAcceleratorService
    		getProductQueryRequestAcceleratorService() {
    	if (acceleratorService == null) {
    		LOGGER.warn("Application container failed to inject the "
    				+ "reference to ProductService.  Attempting to "
    				+ "look it up via JNDI.");
    		acceleratorService = EJBClientUtilities
    				.getInstance()
    				.getProductQueryRequestAcceleratorService();
    	}
    	return acceleratorService;
    }
    
    /**
     * Construct the query response object containing the results of the 
     * client-initiated query.  
     * 
     * @param query The validated <code>ProductQueryRequest</code> object 
     * containing the query parameters.
     * @return Object encapsulating the list of on-disk ISO files that match 
     * the input query parameters.  May contain an empty list, but will not 
     * be null.
     */
    public ProductQueryResponse getProductQueryResponse(
    		ProductQueryRequest query) {
    	
    	ProductQueryResponse response = new ProductQueryResponse();
    	List<Product>        products = getProductList(query);
    	
    	if ((products != null) && (products.size() > 0)) {
	    	for (Product prod : products) {

	    		try {

		    		String hash     = "unavaliable";
		    		Date   fileDate = getActualFileDate(prod.getPath());
		    		long   size     = prod.getSize();
		    		
		    		if (getProductQueryRequestAcceleratorService() != null) {
		    			QueryRequestAccelerator accelerator = 
		    					getProductQueryRequestAcceleratorService()
		    					.getAcceleratorRecord(prod.getPath());
		    			
		    			// If the hash was pre-computed, use it here otherwise
		    			// compute the hash on the fly.
		    			if (accelerator != null) {
		    				hash     = accelerator.getHash();
		    				fileDate = accelerator.getFileDate();
		    				size     = accelerator.getSize();
		    			}
		    			else {
		    				fileDate = getActualFileDate(prod.getPath());
		    				if (getHashGeneratorService() != null) {
				    			hash     = getHashGeneratorService().getHash(
				    						prod.getPath(), 
				    						HashType.MD5);
				    		}
				    		else {
				    			LOGGER.warn("Internal application error.  Unable to "
				    					+ "obtain a reference to the "
				    					+ "HashGeneratorService EJB.  Hash of target "
				    					+ "file will not be available.");
				    		}
		    			}
		    		}
		    	
		    		ISOFile iso = new ISOFile.ISOFileBuilder()
		    					.aorCode(prod.getAorCode())
		    					.countryName(prod.getCountryName())
		    					.size(size)
		    					.fileDate(fileDate)
		    					.hash(hash)
		    					.loadDate(prod.getLoadDate())
		    					.nrn(prod.getNRN())
		    					.nsn(prod.getNSN())
		    					.productType(prod.getProductType())
		    					.url(prod.getURL())
		    					.build();
		    		response.addISOFile(iso);
		    		
	    		}
	    		catch (IOException ioe) {
	    			LOGGER.warn("Unable to access target file [ "
	    					+ prod.getPath()
	    					+ " ].  Error message [ "
	    					+ ioe.getMessage() 
	    					+ " ].  File will not be included in response.");
	    		}
	    	}
    	}
    	else {
    		LOGGER.warn("No products found matching the input query "
    				+ "parameters.  The returned ProductQueryResponse will "
    				+ "contain an empty list of ISO files.");
    	}
    	return response;
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the MetricsService EJB.
     */
    private ProductService getProductService() {
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
     * Check to see if the input List contains a String that matches the 
     * input element parameter.  This method was changed to be 
     * case-insensitive.
     * 
     * @param list The list that will be searched.
     * @param element The String to check.
     * @return True if the list contains the input element, false otherwise.
     */
    private boolean isPresent(List<String> list, String element) {
    	
    	boolean present = false;
    	
    	if ((list != null) && (list.size() > 0)) {
    		for (String current : list) {
    			if (current.equalsIgnoreCase(element)) {
    				present = true;
    			}
    		}
    	}
    	else {
    		LOGGER.error("The input list is null or contains no elements.  "
    				+ "This is most likely a datasource connection issue.  "
    				+ "This will result in rejection of the client submitted "
    				+ "query request.  Please see previous messages for "
    				+ "more information.");
    	}
    	return present;
    }
    
    /**
     * Input load date must not be null or empty and must be in the format
     * <code>YYYY-MM-DD</code>.
     * 
     * @param date String representing the load date.
     * @return Method will return true if the load date was validated, otherwise 
     * an exception is thrown containing information on why the field was not 
     * valid.
     * @throws InvalidQueryRequestException Thrown if an error was encountered 
     * validating the message.  The exception will contain information on why 
     * the field was not valid.
     */
    private boolean validateLoadDate(String date) 
    		throws InvalidQueryRequestException {
    	
    	boolean valid = false;
    	String  msg;
    	
    	try {
	    	if ((date == null) || (date.isEmpty())) {
	    		msg = "Required \"load_date\" field is not populated.";
	    		LOGGER.error("Invalid query request message : " + msg);
	    		throw new InvalidQueryRequestException(msg);
	    	}
	    	else {
	    		(new SimpleDateFormat(
	    				ProductQueryRequest.INPUT_DATE_FORMAT_STRING))
	    			.parse(date);
	    		valid = true;
	    	}
    	}
    	catch (ParseException pe) {
    		msg = "Unable to parse the \"load_date\".  The \"load_date\" must "
    				+ "be in yyyy-mm-dd format.  Date supplied was [ "
    				+ date
    				+ " ].";
    		LOGGER.error("Invalid query request message : " + msg);
    		throw new InvalidQueryRequestException(msg);
    	}
    	return valid;
    }
    
    /**
     * If the user has supplied a product type, ensure that it is indeed a 
     * valid product type.  
     * 
     * @param type The product type.
     * @return Method will return true if the product type was validated, 
     * false will be returned if the AOR code is not populated at all.  An 
     * exception is thrown if there were errors validating the field.
     * @throws InvalidQueryRequestException Thrown if an error was encountered 
     * validating the message.  The exception will contain information on why 
     * the field was not valid.
     */
    private boolean validateProductType(String type) 
    		throws InvalidQueryRequestException {
    
    	boolean valid = false;
    	String  msg;
    	
    	if ((type != null) && (!type.isEmpty())) {
    		if (getProductService () != null) {
    			List<String> list = getProductService().getProductTypes();
    			if (isPresent(list, type)) {
    				valid = true;
    			}
    			else {
    				msg = "Invalid product type.  Name received [ "
    						+ type
    						+ " ].  Client must supply a valid product type.";
    				LOGGER.error("Invalid query request message : " + msg);
    				throw new InvalidQueryRequestException(msg);
    			}
    		}
    	}
    	
    	return valid;
    }
    
    /**
     * If the user has supplied an AOR code, ensure that it is indeed a 
     * valid AOR code. 
     * 
     * @param code The AOR code. 
     * @return Method will return true if AOR code was validated, false 
     * will be returned if the AOR code is not populated at all.  An 
     * exception is thrown if there were errors validating the field.
     * @throws InvalidQueryRequestException Thrown if an error was encountered 
     * validating the message.  The exception will contain information on why 
     * the field was not valid.
     */
    private boolean validateAORCode(String code) 
    		throws InvalidQueryRequestException {
    	
    	boolean valid = false;
    	String  msg;
    	
    	if ((code != null) && (!code.isEmpty())) {
    		if (getProductService () != null) {
    			List<String> list = getProductService().getAORCodes();
    			if (isPresent(list, code)) {
    				valid = true;
    			}
    			else {
    				msg = "Invalid AOR code.  Name received [ "
    						+ code
    						+ " ].  Client must supply a valid AOR code.";
    				LOGGER.error("Invalid query request message : " + msg);
    				throw new InvalidQueryRequestException(msg);
    			}
    		}
    	}
    	
    	return valid;
    }
    
    /**
     * If the user has supplied the country name, ensure that it is indeed a 
     * valid country name. 
     * 
     * @param name Country name.  
     * @return Method will return true if country name was validated, false 
     * will be returned if the country name is not populated at all.  An 
     * exception is thrown if there were errors validating the field. 
     * @throws InvalidQueryRequestException Thrown if an error was encountered 
     * validating the message.  The exception will contain information on why 
     * the field was not valid.
     */
    private boolean validateCountryName(String name) 
    		throws InvalidQueryRequestException {
    	
    	boolean valid = false;
    	String  msg;
    	
    	if ((name != null) && (!name.isEmpty())) {
    		if (getProductService () != null) {
    			List<String> list = getProductService().getCountries();
    			if (isPresent(list, name)) {
    				valid = true;
    			}
    			else {
    				msg = "Invalid country name.  Name received [ "
    						+ name
    						+ " ].  Client must supply a valid country "
    						+ "name.";
    				LOGGER.error("Invalid query request message : " + msg);
    				throw new InvalidQueryRequestException(msg);
    			}
    		}
    	}
    	
    	return valid;
    }

    /**
     * Ensure the incoming ISO query request is valid and consistent.
     * 
     * The load date must be supplied and in the proper format.
     * Only one of the following can be supplied.  
     * <code>aor_code</code>
     * <code>country_name</code>
     * <code>product_type</code>
     * If more than are supplied an exception is raised to the caller.
     * 
     * @param query Client-supplied <code>ProductQueryRequest</code> object.
     * @return True if the request is valid.  An exception is raised if the 
     * request is not valid.
     * @throws InvalidQueryRequestException Thrown if an error was encountered 
     * validating the message.  The exception will contain information on why 
     * the field was not valid.
     */
    public boolean validateQueryMessage(ProductQueryRequest query) 
    		throws InvalidQueryRequestException {
    	
    	boolean valid = false;
    	String  msg;
    	
    	// The load date is required, check it first.
    	if (validateLoadDate(query.getLoadDate())) {
    		
    		// See how many filters were set.
    		int howManySet = 
    				(validateCountryName(query.getCountryName()) ? 1 : 0)
    				+ (validateAORCode(query.getAORCode()) ? 1 : 0)
    				+ (validateProductType(query.getProductType()) ? 1 : 0);
    		
    		// If more than one filter is set, throw an exception.
    		if (howManySet > 1) {
    			msg = "Only one of \"aor_code\", \"country_name\", or "
    					+ "\"product_type\" can be defined in a single "
    					+ "ISO query request.";
				LOGGER.error("Invalid query request message : " + msg);
				throw new InvalidQueryRequestException(msg);
    		}
    		else {
    			valid = true;
    		}
    	}
    	return valid;
    }
    
}
