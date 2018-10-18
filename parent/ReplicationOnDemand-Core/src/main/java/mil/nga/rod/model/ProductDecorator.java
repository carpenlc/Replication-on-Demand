package mil.nga.rod.model;

import java.util.Optional;

/**
 * Adaptor class allowing us to decorate the <code>Product</code> object with
 * the on-disk file information.
 * 
 * @author L. Craig Carpenter
 */
public class ProductDecorator {

	/**
	 * The Product that will have the decorations applied.
	 */
	protected Optional<Product> product;
	
	/**
	 * Default constructor required by JAX-B.
	 */
	public ProductDecorator() {}
	
	/**
	 * Constructor for the Product data.
	 * @param prod The Product data.
	 */
	protected ProductDecorator(Product value) {
		if (value == null) {
			product = Optional.empty();
		}
		else {
			product = Optional.of(value);
		}
	}
	
	/**
	 * Accessor method for the Product data.
	 * @return The RoD Product data.
	 */
	public Product getProduct() throws IllegalStateException {
		if (product.isPresent()) {
			return product.get();
		}
		else {
			throw new IllegalStateException("Required Product "
					+ "object not provided.");
			}
	}
	
	/**
	 * Mutator method for the internal <code>Product</code> object.
	 * @param The object handle
	 */
	public void setProduct(Product value) {
		if (value == null) {
			product = Optional.empty();
		}
		else {
			product = Optional.of(value);
		}
	}
	
	/**
	 * Convert to human-readable String.
	 */
	public String toString() {
		if (product.isPresent()) {
			return product.get().toString();
		}
		else {
			return "[ parent product object not available ]";
		}
	}
}
