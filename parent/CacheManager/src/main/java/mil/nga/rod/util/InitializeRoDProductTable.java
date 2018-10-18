package mil.nga.rod.util;

import java.util.List;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.artwork.ArtworkBuilder;
import mil.nga.rod.accelerator.AcceleratorRecordFactory;
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
 * Class used to initialize the 
 * @author L. Craig Carpenter
 *
 */
public class InitializeRoDProductTable {
	
    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductFactory.class);
    
	public void init() {
		
		int  count = 0;
		long start = System.currentTimeMillis();
		
		// Initialize the database connections in a try-with-resources so 
		// they are closed properly.
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

					QueryRequestAccelerator accelerator = 
							AcceleratorRecordFactory
								.getInstance()	
								.buildRecord(product);
					
					Artwork art = (new ArtworkBuilder())
							.product(product)
							.build();
					
					RoDProduct rodProduct = new RoDProduct.RoDProductBuilder()
							.product(product)
							.queryRequestAccelerator(accelerator)
							.artwork(art)
							.build();
					
					//rodProdFactory.persist(rodProduct);
					count++;
				}
				
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
