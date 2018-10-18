package mil.nga.util;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.PropertiesNotLoadedException;
import mil.nga.exceptions.PropertyNotFoundException;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.jdbc.RoDProductFactory;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.RoDProduct;

public class DumpProducts {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOGGER = LoggerFactory.getLogger(
    		DumpProducts.class);
    
	public DumpProducts() { }
	
	public void execute() {
		
		long start = System.currentTimeMillis();
		int  count  = 0;
		
		try {
			List<Product> products = ProductFactory.getInstance().getUniqueProducts();
			for (Product prod : products) {
				count += 1;
				Optional<RoDProduct> rodRec = RoDProductFactory.getInstance()
						.getRoDProduct(Optional.of(prod));
				if (rodRec.isPresent()) {
					System.out.println(rodRec.get().toString());
				}
			}
			LOGGER.info("[ "
					+ count 
					+ " ] records dumped in [ "
					+ (System.currentTimeMillis() - start)
					+ " ] ms.");
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
	public static void main(String[] args) {
		new DumpProducts().execute();
	}
}
