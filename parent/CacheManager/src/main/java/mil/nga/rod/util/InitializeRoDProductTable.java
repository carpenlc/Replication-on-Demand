package mil.nga.rod.util;

import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.artwork.ArtworkBuilder;
import mil.nga.rod.cache.AcceleratorRecordFactory;
import mil.nga.rod.jdbc.ArtworkRowFactory;
import mil.nga.rod.jdbc.ProductFactory;
import mil.nga.rod.jdbc.RoDProductFactory;
import mil.nga.rod.jdbc.RoDProductRecordFactory;
import mil.nga.rod.model.Artwork;
import mil.nga.rod.model.ArtworkRow;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.model.RoDProduct;

/**
 * Class used to initialize the backing data table containing the list of 
 * <code>RoDProducts</code>.  Once the backing table is populated, it will 
 * be managed by a separate process.
 * 
 * @author L. Craig Carpenter
 */
public class InitializeRoDProductTable {
	
    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductFactory.class);
    
    /**
     * Main method used to read data from the target product table, then
     * decorate it with the required on-disk and artwork data, then store
     * the record in a separate table.
     */
	public void init() {
		
		int  count = 0;
		long start = System.currentTimeMillis();
		
		// Initialize the database connections in a try-with-resources so 
		// they are all closed properly.
		try (RoDProductRecordFactory rodProdFactory = 
				RoDProductRecordFactory.getInstance();
			 ArtworkRowFactory       artFactory     = 
					 ArtworkRowFactory.getInstance();
		     ProductFactory          prodFactory    = 
		    		 ProductFactory.getInstance()) {
			
			// Get a list of available products
			List<Product> products = prodFactory.getUniqueProducts();
			if ((products == null) || (products.size() > 0)) {
				
				LOG.info("Processing [ "
						+ products.size()
						+ " ] unique products.");
				
				for (Product product : products) {

					// Get the on-disk information (size, hash, etc.)
					QueryRequestAccelerator accelerator = 
							AcceleratorRecordFactory
								.getInstance()	
								.buildRecord(product);
					
					// Get/process the artwork information
					Artwork art = (new ArtworkBuilder())
							.product(product)
							.build();
					
					// Construct the record that will be inserted in the 
					// target data store.
					RoDProduct rodProduct = new RoDProduct.RoDProductBuilder()
							.product(product)
							.queryRequestAccelerator(accelerator)
							.artwork(art)
							.build();
					
					// Store the record.
					rodProdFactory.persist(rodProduct);
					count++;
				}
				
			}
			else {
				LOG.error("Unable to retrieve list of products from the target data source.");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.info("Table initialized with [ "
				+ count 
				+ " ] records in [ "
				+ (System.currentTimeMillis() - start)
				+ " ] ms.");
	}
	
	public static void main(String[] args) {
		new InitializeRoDProductTable().init();
	}
}
