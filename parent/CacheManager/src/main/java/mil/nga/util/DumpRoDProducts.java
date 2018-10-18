package mil.nga.util;

import java.util.List;

import mil.nga.rod.model.RoDProduct;
import mil.nga.rod.jdbc.RoDProductRecordFactory;

/**
 * Test class for testing connectivity to the datasource storing the 
 * <code>RoDProduct</code> data.
 * 
 * @author L. Craig Carpenter
 */
public class DumpRoDProducts {

	public void execute() {
		List<RoDProduct> products = null;
		try (RoDProductRecordFactory factory = RoDProductRecordFactory.getInstance()) {
			products = factory.getProducts();
			if ((products != null) && (products.size() > 0)) {
				for (RoDProduct product : products) {
					System.out.println(product.toString());
				}
			}
		}

	}
	
	public static void main(String[] args) {
		new DumpRoDProducts().execute();
	}
}
