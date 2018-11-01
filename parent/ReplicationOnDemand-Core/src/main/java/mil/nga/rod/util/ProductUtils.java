package mil.nga.rod.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.rod.model.Product;

/**
 * Simple class encapsulating a few methods dealing with the product key data
 * that are utilize by several classes throughout the RoD project.
 * 
 * @author L. Craig Carpenter
 */
public class ProductUtils {

    /**
     * Set up the Log4j system for use throughout the class.
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		ProductUtils.class);
    
    /**
     * Private constructor to enforce the singleton design pattern.
     */
    private ProductUtils() { }
    
    /**
     * Accessor method for the singleton instance of the ProductUtils class.
     * 
     * @return The singleton instance of the ProductUtils .
     * class.
     */
    public static ProductUtils getInstance() {
        return ProductUtilsHolder.getSingleton();
    } 
    
	/**
	 * Construct a list containing A defference B (i.e. the elements in list
	 * A that are not in list B.)
	 * @param A A list of String objects.
	 * @param B A list of String objects.
	 * @return A list containing the Strings that are in A but not B.
	 */
	public List<String> defference(List<String> A, List<String> B) {
		long start = System.currentTimeMillis();
		List<String> defference = new LinkedList<String>();
		if ((A != null) && (!A.isEmpty())) {
			defference.addAll(A);
			if ((B!= null) && (!B.isEmpty())) {
				defference.removeAll(B);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deference calculated in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return defference;
	}
	
	/**
	 * Calculate the intersection of two String lists.  
	 * @param A A list of String objects.
	 * @param B A list of String objects.
	 * @return A list containing the Strings that are in both lists.
	 */
	public List<String> intersection(List<String> A, List<String> B) {
		long start = System.currentTimeMillis();
		List<String> intersection = new LinkedList<String>();
		if ((A != null) && (B != null) && (A.size() > 0) && (B.size() > 0)) {
			for (String obj : A) {
				if (B.contains(obj)) {
					intersection.add(obj);
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Intersection calculated in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
		}
		return intersection;
	}
	
	/**
	 * Method used to construct the primary key in the key/value pair that 
	 * is stored in the cache.  The key is a concatentation of the NRN and NSN
	 * of the unique product. 
	 * 
	 * Note: In the datastore, there are several products that are missing the
	 * NRN, NSN, or both.  The team maintaining the data store replace these 
	 * missing Strings with "XXX not avail".  The spaces cause problems later 
	 * when we attempt to construct URIs to locations on the file system.  
	 * 
	 * @param nrn Product NRN
	 * @param nsn Product NSN
	 * @return
	 */
	public String getKey(String nrn, String nsn) {
		StringBuilder sb = new StringBuilder();
        if ((nsn != null) && (!nsn.isEmpty())) {
            if ((nrn != null) && (!nrn.isEmpty())) {
                sb.append(nsn.trim().replaceAll("\\ ", "-"));
                sb.append("+");
                sb.append(nrn.trim().replaceAll("\\ ", "-"));
            }
            else {
                LOGGER.error("The input product object contains a null "
                        + "(or empty) value for the NRN.  This is not "
                        + "supposed to happen.");
            }
        }
        else {
            LOGGER.error("The input product object contains a null "
                    + "(or empty) value for the NSN.  This is not "
                    + "supposed to happen.");
        }
        return sb.toString();
	}
	
    /**
     * Calculate the key that will be used for storage/lookup of the query
     * accelerator records.  
     * 
     * @return The key used to store the accelerator record.  May be empty
     * so callers must check it before attempting to store in the cache.
     */
    public String getKey(Product prod){
        StringBuilder sb = new StringBuilder();
        if (prod != null) {
            if ((prod.getNSN() != null) && (!prod.getNSN().isEmpty())) {
                if ((prod.getNRN() != null) && (!prod.getNRN().isEmpty())) {
                    sb.append(getKey(prod.getNRN(), prod.getNSN()));
                }
                else {
                    LOGGER.error("The input product object contains a null "
                            + "(or empty) value for the NRN.  This is not "
                            + "supposed to happen.");
                }
            }
            else {
                LOGGER.error("The input product object contains a null "
                        + "(or empty) value for the NSN.  This is not "
                        + "supposed to happen.");
            }
        }
        else {
            LOGGER.error("The input product object is null.  Nothing to "
                    + "store.");
        }
        return sb.toString();
    }
    
    /**
     * Based on the key design in which keys are the combination of NSN and 
     * NRN of a given product, the NRN is the second half of the key.  The
     * NRN is used to extract the data from the backing data store.
     * 
     * @param key The target key.
     * @return The product NRN.
     */
    public String getNRNFromKey(String key) {
        String NRN = null;
        if ((key != null) && (!key.isEmpty())) {
        String[] array = key.split("\\+");
	        if (array.length == 2) {
	            NRN = array[1];
	        }
	    }
	    else {
	    	LOGGER.error("The input key is null.  Unable to obtain NRN.");
	    }
        return NRN;
    }
    
    /**
     * Based on the key design in which keys are the combination of NSN and 
     * NRN of a given product, the NSN is the first half of the key.  The
     * NSN is used to extract the data from the backing data store.
     * 
     * @param key The target key.
     * @return The product NRN.
     */
    public String getNSNFromKey(String key) { 
        String NSN = null;
        if ((key != null) && (!key.isEmpty())) {
	        String[] array = key.split("\\+");
	        if (array.length == 2) {
	            NSN = array[0];
	        }
        }
        else {
        	LOGGER.error("The input key is null.  Unable to obtain NSN.");
        }
        return NSN;
    }
    
    /**
     * Concert a list of Strings into a comma-separated String.
     * @param list A list of String values.
     * @return A comma-separated list.
     */
    public String getSimpleString(Set<String> list) {
    	String simpleString = "";
    	if ((list != null) && (list.size() > 0)) { 
    		simpleString = String.join(",", list);
    	}
    	return simpleString;
    }
    
    /**
     * Take a <code>List</code> of <code>Product</code> objects and 
     * construct a <code>Set</code> containing the list of various 
     * country names residing in the list of Products.
     * 
     * Using a Set instead of a List because we don't want duplicates.
     * @param products
     * @return
     */
    public Set<String> getCountryNames(List<Product> products) {
    	Set<String> names = new HashSet<String>();
    	if ((products != null) && (products.size() > 0)) {
    		for (Product p : products) {
    			names.add(p.getCountryName());
    		}
    	}
    	return names;
    }
    
    /**
     * Take a <code>List</code> of <code>Product</code> objects and 
     * construct a <code>Set</code> containing the list of various 
     * ISO 3 character codes residing in the list of Products.
     * 
     * Using a Set instead of a List because we don't want duplicates.
     * @param products
     * @return
     */
    public Set<String> getISO3CharCodes(List<Product> products) {
    	Set<String> codes = new HashSet<String>();
    	if ((products != null) && (products.size() > 0)) {
    		for (Product p : products) {
    			codes.add(p.getIso3Char().toUpperCase());
    		}
    	}
    	return codes;
    }
    
    /**
     * Take a <code>List</code> of <code>Product</code> objects and 
     * construct a <code>Set</code> containing the list of various 
     * AOR codes residing in the list of Products.
     * 
     * Using a Set instead of a List because we don't want duplicates.
     * @param products
     * @return
     */
    public Set<String> getAorCodes(List<Product> products) {
    	Set<String> codes = new HashSet<String>();
    	if ((products != null) && (products.size() > 0)) {
    		for (Product p : products) {
    			codes.add(p.getAorCode().toUpperCase());
    		}
    	}
    	return codes;
    }
    
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class ProductUtilsHolder {
        
        /**
         * Reference to the Singleton instance of the ProductUtils.
         */
        private static ProductUtils _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * ProductUtils.
         */
        public static ProductUtils getSingleton() {
            if (_instance == null) {
                _instance = new ProductUtils();
            }
            return _instance;
        }
    }
}
