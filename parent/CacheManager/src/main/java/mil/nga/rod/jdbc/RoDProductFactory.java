package mil.nga.rod.jdbc;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.model.ArtworkRow;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.model.RoDProduct;
import mil.nga.rod.util.ProductUtils;

/**
 * 
 * @author L. Craig Carpenter
 */
public class RoDProductFactory {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		RoDProductFactory.class);
	
    /**
     * Accessor method for the singleton instance of the RoDProductFactory class.
     * @return The singleton instance of the RoDProductFactory class.
     */
    public static RoDProductFactory getInstance() {
        return RoDProductFactoryHolder.getSingleton();
    } 
    
    /**
     * Convenience method allowing clients to extract the 
     * <code>RoDProduct</code> object based on the input key.
     * 
     * @param key The key associated with the backing product.
     * @return An Optional object wrapping an actual RoDProduct object.
     */
	public Optional<RoDProduct> getRoDProduct(String key) {
		Optional<RoDProduct> rodProd = Optional.empty();
		if ((key != null) && (!key.isEmpty())) {
			rodProd = getRoDProduct(
					ProductUtils.getInstance().getNRNFromKey(key), 
	    			ProductUtils.getInstance().getNSNFromKey(key));
		}
		return rodProd;
	}
	
    /**
     * Assemble a RoDProduct object from the backing data store.  This is a 
     * 3 step process.  First, we have to retrieve the Product data, then we 
     * have to get the Query Accelerator data, then finally the Artwork 
     * information.
     * 
     * @param nrn The NRN of a given product.
     * @param nsn The NSN of a given product.
     * @return An Optional object wrapping an actual RoDProduct object.
     */
	public Optional<RoDProduct> getRoDProduct(String nrn, String nsn) {
		
		long                 start   = System.currentTimeMillis(); 
		Optional<RoDProduct> rodProd = Optional.empty();
		
		try {
			List<Product> p = ProductFactory.getInstance().getProducts(nrn, nsn);
			if ((p != null) && (p.size() > 0)) {
				QueryRequestAccelerator pAcc = AcceleratorJDBCRecordFactory
						.getInstance()
						.getRecord(p.get(0));
				List<ArtworkRow> art = ArtworkFactory.getInstance().getArtwork(nrn, nsn);
				if ((art != null) && (art.size() > 0)) {
					rodProd = Optional.of(
							new RoDProduct.RoDProductBuilder()
								.queryRequestAccelerator(pAcc)
								.artworkPath(art.get(0).getPath())
								.artworkSize(art.get(0).getSize())
								.cdName(art.get(0).getCDName())
							.build());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.warn("RoDProduct object for NRN => [ "
								+ nrn
								+ " ] and NSN => [ "
								+ nsn
								+ " ] assembled in [ "
								+ (System.currentTimeMillis() - start)
								+ " ] ms.");
					}
				}
				else {
					LOGGER.warn("Unable to find artwork for NRN => [ "
							+ nrn
							+ " ] and NSN => [ "
							+ nsn
							+ " ].  Returned object will be empty.");
				}
			}
			else {
				LOGGER.error("Unable to find a Product object matching NRN => [ "
						+ nrn
						+ " ] and NSN => [ "
						+ nsn
						+ " ].  Returned object will be empty.");
			}
		}
		catch (PropertiesNotLoadedException pnle) {
			LOGGER.error("Unexpected PropertiesNotLoadedException encountered.  "
					+ "Please ensure that the application is properly "
					+ "configured.  The returned object will be empty.  "
					+ "Error message => [ "
					+ pnle.getMessage()
					+ " ].");
		}
		catch (PropertyNotFoundException pnfe) {
			LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
					+ "Please ensure that the application is properly "
					+ "configured.  The returned object will be empty.  "
					+ "Error message => [ "
					+ pnfe.getMessage()
					+ " ].");
		}
		catch (ClassNotFoundException cnfe) {
			LOGGER.error("Unexpected ClassNotFoundException encountered.  "
					+ "Please ensure that the JDBC drivers are available.  The "
					+ "returned object will be empty.  Error message => [ "
					+ cnfe.getMessage()
					+ " ].");
		}
		return rodProd;
	}
	
    /**
     * Assemble a RoDProduct object for a given 
     * <code>QueryRequestAccelerator</code> object. This is a 1 step process.  
     * We essentially just have to decorate the incoming 
     * <code>QueryRequestAccelerator</code> with the artwork information.
     * 
     * @param prod A populated <code>Product</code> object.
     * @return An Optional object wrapping an actual RoDProduct object.
     */
	public Optional<RoDProduct> getRoDProductOneStep(
			Optional<QueryRequestAccelerator> accelerator) {
		
		long                 start   = System.currentTimeMillis(); 
		Optional<RoDProduct> rodProd = Optional.empty();
		
		if (accelerator.isPresent()) {
			try {
				List<ArtworkRow> art = ArtworkFactory.getInstance().getArtwork(
						accelerator.get().getProduct().getNRN(), 
						accelerator.get().getProduct().getNSN());
				if ((art != null) && (art.size() > 0)) {
					rodProd = Optional.of(
							new RoDProduct.RoDProductBuilder()
								.queryRequestAccelerator(accelerator.get())
								.artworkPath(art.get(0).getPath())
								.artworkSize(art.get(0).getSize())
								.cdName(art.get(0).getCDName())
								.build());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.warn("RoDProduct object for NRN => [ "
								+ accelerator.get().getNRN()
								+ " ] and NSN => [ "
								+ accelerator.get().getNSN()
								+ " ] assembled in [ "
								+ (System.currentTimeMillis() - start)
								+ " ] ms.");
					}
				}
			}
			catch (PropertiesNotLoadedException pnle) {
				LOGGER.error("Unexpected PropertiesNotLoadedException encountered.  "
						+ "Please ensure that the application is properly "
						+ "configured.  The returned object will be empty.  "
						+ "Error message => [ "
						+ pnle.getMessage()
						+ " ].");
			}
			catch (PropertyNotFoundException pnfe) {
				LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
						+ "Please ensure that the application is properly "
						+ "configured.  The returned object will be empty.  "
						+ "Error message => [ "
						+ pnfe.getMessage()
						+ " ].");
			}
			catch (ClassNotFoundException cnfe) {
				LOGGER.error("Unexpected ClassNotFoundException encountered.  "
						+ "Please ensure that the JDBC drivers are available.  The "
						+ "returned object will be empty.  Error message => [ "
						+ cnfe.getMessage()
						+ " ].");
			}
		}
		else {
			LOGGER.error("Input QueryRequestAccelerator object is empty.  "
					+ "Output will also be empty.");
		}
		return rodProd;
	}
	
    /**
     * Assemble a RoDProduct object for a given <code>Product</code> object. 
     * This is a 2 step process.  First, we have to Query Accelerator data, 
     * then decorate with the Artwork information.
     * 
     * @param prod A populated <code>Product</code> object.
     * @return An Optional object wrapping an actual RoDProduct object.
     */
	public Optional<RoDProduct> getRoDProduct(Optional<Product> prod) {
		
		long                 start   = System.currentTimeMillis(); 
		Optional<RoDProduct> rodProd = Optional.empty();
		
		try {
			if (prod.isPresent()) {
				QueryRequestAccelerator pAcc = AcceleratorJDBCRecordFactory
							.getInstance()
							.getRecord(prod.get());
				List<ArtworkRow> art = ArtworkFactory.getInstance().getArtwork(
										prod.get().getNRN(), 
										prod.get().getNSN());
				if ((art != null) && (art.size() > 0)) {
					rodProd = Optional.of(
							new RoDProduct.RoDProductBuilder()
								.queryRequestAccelerator(pAcc)
								.artworkPath(art.get(0).getPath())
								.artworkSize(art.get(0).getSize())
								.cdName(art.get(0).getCDName())
							.build());
					if (LOGGER.isDebugEnabled()) {
						LOGGER.warn("RoDProduct object for NRN => [ "
								+ prod.get().getNRN()
								+ " ] and NSN => [ "
								+ prod.get().getNSN()
								+ " ] assembled in [ "
								+ (System.currentTimeMillis() - start)
								+ " ] ms.");
					}
				}
				else {
					LOGGER.warn("Unable to find artwork for NRN => [ "
							+ prod.get().getNRN()
							+ " ] and NSN => [ "
							+ prod.get().getNSN()
							+ " ].  Returned object will be empty.");
				}
			}
			else {
				LOGGER.error("Input Product object is empty.  Output will "
						+ "also be empty.");
			}
		}
		catch (PropertiesNotLoadedException pnle) {
			LOGGER.error("Unexpected PropertiesNotLoadedException encountered.  "
					+ "Please ensure that the application is properly "
					+ "configured.  The returned object will be empty.  "
					+ "Error message => [ "
					+ pnle.getMessage()
					+ " ].");
		}
		catch (PropertyNotFoundException pnfe) {
			LOGGER.error("Unexpected PropertyNotFoundException encountered.  "
					+ "Please ensure that the application is properly "
					+ "configured.  The returned object will be empty.  "
					+ "Error message => [ "
					+ pnfe.getMessage()
					+ " ].");
		}
		catch (ClassNotFoundException cnfe) {
			LOGGER.error("Unexpected ClassNotFoundException encountered.  "
					+ "Please ensure that the JDBC drivers are available.  The "
					+ "returned object will be empty.  Error message => [ "
					+ cnfe.getMessage()
					+ " ].");
		}
		return rodProd;
	}
	
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class RoDProductFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the RoDProductFactory.
         */
        private static RoDProductFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * RoDProductFactory.
         * 
         * @return The Singleton instance of the RoDProductFactory.
         */
        public static RoDProductFactory getSingleton() {
            if (_instance == null) {
                _instance = new RoDProductFactory();
            }
            return _instance;
        }
    }
}
