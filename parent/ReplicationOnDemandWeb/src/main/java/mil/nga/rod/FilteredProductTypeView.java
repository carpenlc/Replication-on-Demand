package mil.nga.rod;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.RoDProduct;

/**
 * Backing bean associated with the PrimeFaces <code>index.xhtml</code> file 
 * that provides the front-end web page for allowing users to access/download 
 * the ISO files available under the Replication on Demand project.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@ViewScoped
public class FilteredProductTypeView 
        extends RoDEJBClientUtilities 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 5406199248185203518L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(FilteredProductTypeView.class);
    
    /**
     * List containing all of currently available products.  This list will 
     * not change throughout the life of the current bean.
     */
    private List<RoDProduct> products;
    
    /**
     * List containing the products "filtered" by the user using the tools 
     * available on the JSF page.
     */
    private List<Product> filteredProducts;
    
    /**
     * List containing the distinct AORs available in the back-end data store.
     */
    private List<String> availableAORs;
    
    /**
     * List containing the distinct product types available in the back-end data
     * source.
     */
    private List<String> availableProductTypes;
    
    /**
     * List containing the distinct country names available in the back-end data
     * source.
     */
    private List<String> availableCountryNames;
    
    /**
     * The product that is currently selected in DataTable.
     */
    private Product selectedProduct;
    
    /**
     * This method serves as the constructor which will create and populate 
     * the internal lists that are displayed in the target web page.
     */
    @PostConstruct
    public void initialize() throws ServiceUnavailableException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Constructor called.");
        }
        products = super.loadAllProducts();
        availableProductTypes = super.loadProductTypes();
        //availableCountryNames = super.loadCountries();
        //availableAORs = super.loadAORCodes();
    }
    
    /**
     * Getter method for the list of available countries.
     * @return The list of countries available in the backing data store.
     */
    public List<String> getAvailableAORs() {
        return this.availableAORs;
    }
    
    /**
     * Getter method for the list of available countries.
     * @return The list of countries available in the backing data store.
     */
    public List<String> getAvailableCountryNames() {
        return this.availableCountryNames;
    }
    
    /**
     * Getter method for the list of available product types.
     * @return The list of available product types in the backing data store.
     */
    public List<String> getAvailableProductTypes() {
        return availableProductTypes;
    }    

    /**
     * Setter method for the list of products that have had a "filter" 
     * applied.
     * @return list The filtered list of products.
     */
    public List<Product> getFilteredProducts( ) {
        return filteredProducts;
    }
    
    /**
     * Getter method for the list of all available products.
     * @return The list of all available products.
     */
    public List<RoDProduct> getProducts() {
        return products;
    }
    
    /**
     * Getter method for the product currently selected in the product list.
     * @return The currently selected product.
     */
    public Product getSelectedProduct() {
        return selectedProduct;
    }
    
    /**
     * Setter method for the list of products that have had a "filter" 
     * applied.
     * @param list The filtered list of products.
     */
    public void setFilteredProducts(List<Product> list) {
        filteredProducts = list;
    }
    
    /**
     * Setter method for the Product currently selected in the product list.
     * 
     * @param value The currently selected Product.
     */
    public void setSelectedProduct(Product value) {
        if (value != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Product selected [ "
                        + value.getNSN()
                        + " ].");
            }
        }
        else {
            LOGGER.info("Selected job is null.");
        }
        selectedProduct = value;
    }
    
}
