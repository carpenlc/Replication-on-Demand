package mil.nga.rod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.RoDProduct;

import org.primefaces.model.DualListModel;

@ManagedBean
@ViewScoped
public class ProductTypeView 
        extends RoDEJBClientUtilities 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -1836036318596590376L;

    /**
     * Get a list of product types currently available in the data store.
     */
    private DualListModel<String> productTypes;
    
    /**
     * List of AOR codes currently available in the data store.
     */
    private DualListModel<String> aorCodes;
    
    /**
     * List of countries currently available in the data store.
     */
    private DualListModel<String> countries;
    
    /**
     * List containing all of currently available products.  This list will 
     * not change throughout the life of the current bean.
     */
    private List<RoDProduct> allProducts;
    
    /**
     * List containing the list of products after filtering is applied.  This
     * is the list that is displayed in the data table.  On construction the 
     * filtered list and allProducts list are the same.
     */
    private List<RoDProduct> filteredProducts;
    
    /**
     * Indicated whether the filters selected by the user should be combined 
     * as either logical AND or logical OR. 
     */
    private String logicalOperator;
    
    /**
     * The product that is currently selected in DataTable.
     */
    private Product currentlySelectedProduct;
    
    /**
     * Holds the value associated with the text that the user entered into
     * the NRN text box.
     */
    private String nrnFilter;
    
    /**
     * Holds the value associated with the text that the user entered into
     * the NSN text box.
     */
    private String nsnFilter;
    
    /**
     * This method serves as the constructor which will create and populate 
     * the internal lists that are displayed in the target web page.
     */
    @PostConstruct
    public void initialize() throws ServiceUnavailableException {
        //productTypes = initProductTypes();
        //aorCodes = initAorCodes();
        //countries = initCountries();
        allProducts = initProducts();
        filteredProducts = allProducts;
        logicalOperator = "AND";
    }
    
    /**
     * Getter method for the list of available AOR codes.
     * @return The list of product types available.
     */
    /*
    public DualListModel<String> getAorCodes() {
        if ((aorCodes == null) || (aorCodes.getSource().size() == 0)) {
            aorCodes = initAorCodes();
        }
        return aorCodes;
    }
    */
    
    /**
     * Getter method for the list of available AOR codes.  Rather than 
     * return a DualListModel (which is associated with the Prime Faces 
     * pickList component), this method only returns the source list.
     * @return The list of AOR codes available.
     */
    /*
    public List<String> getAorCodeList() {
        if ((aorCodes == null) || (aorCodes.getSource().size() == 0)) {
            aorCodes = initAorCodes();
        }
        return aorCodes.getSource();
    }
    */
    
    /**
     * Getter method for the list of available countries
     * @return The list of countries available.
     */
    /*
    public DualListModel<String> getCountries() {
        if ((countries == null) || (countries.getSource().size() == 0)) {
            countries = initCountries();
        }
        return countries;
    }
    */
    /**
     * Getter method for the list of filtered products.
     * 
     * @return The list of filtered products.
     */
    public List<RoDProduct> getFilteredProducts() {
        return filteredProducts;
    }
    
    /**
     * Getter method for the total number of products available in the 
     * back-end data store.
     * @return The total number of products in the back-end data store.
     */
    public int getProductCount() {
        int count = 0;
        if ((allProducts != null) && (allProducts.size() > 0)) {
            count = allProducts.size();
        }
        return count;
    }
    
    /**
     * Getter method for the list of available product types.
     * @return The list of product types available.
     */
    public DualListModel<String> getProductTypes() {
        if ((productTypes == null) || (productTypes.getSource().size() == 0)) {
            productTypes = initProductTypes();
        }
        return productTypes;
    }
    
    /**
     * Getter method for the list of available product types.  Rather than 
     * return a DualListModel (which is associated with the Prime Faces 
     * pickList component), this method only returns the source list.
     * 
     * @return The list of product types available.
     */
    public List<String> getProductTypeList() {
        if ((productTypes == null) || (productTypes.getSource().size() == 0)) {
            productTypes = initProductTypes();
        }
        return productTypes.getSource();
    }
    
    /**
     * Getter method for the selected logical operator.
     * @return The user-selected logical operator.
     */
    public String getLogicalOperator() {
        return logicalOperator;
    }
    
    /**
     * Getter method for the list of all available products.
     * @return The list of all available products.
     */
    public List<RoDProduct> getProducts() {
        return filteredProducts;
    }
    
    /**
     * Getter method for the value entered in the NRN text box.
     * 
     * @return The requested NRN.
     */
    public String getNrnFilter() {
        return nrnFilter;
    }
    
    /**
     * Getter method for the value entered in the NSN text box.
     * 
     * @return The requested NSN.
     */
    public String getNsnFilter() {
        return nsnFilter;
    }
    
    /**
     * Getter method for the product currently selected in the product list.
     * @return The currently selected product.
     */
    public Product getSelectedProduct() {
        return currentlySelectedProduct;
    }
    
    /**
     * Setter method for the list of filtered products.
     * 
     * @return The list of filtered products.
     */
    public void setFilteredProducts(List<RoDProduct> value) {
        filteredProducts = value;
    }
    
    /**
     * Getter method for the selected logical operator.
     * @param value The user-selected logical operator.
     */
    public void setLogicalOperator(String value) {
        logicalOperator = value;
    }
    
    /**
     * 
     * Note: This method was only added because...
     * @param value
     */
    public void setCountries(DualListModel<String> value) {
        countries = value;
    }
    
    /**
     * 
     * Note: This method was only added because...
     * @param value
     */
    public void setAorCodes(DualListModel<String> value) {
        aorCodes = value;
    }
    /**
     * 
     * Note: This method was only added because...
     * @param value
     */
    public void setProductTypes(DualListModel<String> value) {
        productTypes = value;
    }
    
    /**
     * Setter method for the NRN filter.
     * 
     * @return The user-supplied NRN filter.
     */
    public void setNrnFilter(String value) {
        if (value != null) {
            nrnFilter = value.trim();
        }
    }
    
    /**
     * Setter method for the NSN filter.
     * 
     * @return The user-supplied NSN filter.
     */
    public void setNsnFilter(String value) {
        if (value != null) {
            nsnFilter = value.trim();
        }
    }
    
    
    private List<RoDProduct> initProducts() throws ServiceUnavailableException {
        return super.loadAllProducts();
    }
    
    /*
    private DualListModel<String> initAorCodes() {
        List<String> aorCodesSource = super.loadAORCodes();
        List<String> aorCodesTarget = new ArrayList<String>();
        return new DualListModel<String>(aorCodesSource, aorCodesTarget);
    }
    
    private DualListModel<String> initCountries() {
        List<String> source = super.loadCountries();
        List<String> target = new ArrayList<String>();
        return new DualListModel<String>(source, target);
    }
    */
    private DualListModel<String> initProductTypes() {
        List<String> prodTypeSource = super.loadProductTypes();
        List<String> prodTypeTarget = new ArrayList<String>();
        return new DualListModel<String>(prodTypeSource, prodTypeTarget);
    }
    
    
    /**
     * See if there are any selected filters that need to be applied.
     * @return True if there are any filters selected, false otherwise.
     */
    private boolean applyAnyFilter() {
        return (applyAorFilter() || 
                applyCountryFilter() || 
                applyProductTypeFilter() ||
                applyNrnFilter() ||
                applyNsnFilter());
    }
    
    /**
     * Determine whether there is a filter for AOR selected by 
     * the user.
     * 
     * @return True if an AOR filter was selected, false otherwise.
     */
    private boolean applyAorFilter() {
        boolean apply = false;
        if ((aorCodes != null) && 
                (aorCodes.getTarget() != null) &&
                (aorCodes.getTarget().size() > 0)) {
            apply = true;
        }
        return apply;
    }
    
    /**
     * Determine whether there is a filter for country name selected by 
     * the user.
     * 
     * @return True if a country filter was selected, false otherwise.
     */
    private boolean applyCountryFilter() {
        boolean apply = false;
        if ((countries != null) && 
                (countries.getTarget() != null) &&
                (countries.getTarget().size() > 0)) {
            apply = true;
        }
        return apply;
    }
    
    /**
     * Determine whether there is a filter for NRN entered by 
     * the user.
     * 
     * @return True if a NRN filter was selected, false otherwise.
     */
    private boolean applyNrnFilter() {
        boolean apply = false;
        if ((getNrnFilter() != null) && (!getNrnFilter().isEmpty())) {
            apply = true;
        }
        return apply;
    }
    
    /**
     * Determine whether there is a filter for NRN entered by 
     * the user.
     * 
     * @return True if a NRN filter was selected, false otherwise.
     */
    private boolean applyNsnFilter() {
        boolean apply = false;
        if ((getNsnFilter() != null) && (!getNsnFilter().isEmpty())) {
            apply = true;
        }
        return apply;
    }
    
    /**
     * Determine whether there is a filter for product types selected by 
     * the user.
     * 
     * @return True if a product type filter was selected, false otherwise.
     */
    private boolean applyProductTypeFilter() {
        boolean apply = false;
        if ((productTypes != null) && 
                (productTypes.getTarget() != null) &&
                (productTypes.getTarget().size() > 0)) {
            apply = true;
        }
        return apply;
    }
    /**
     * 
     * @return
     */
    /*
    private List<Product> filterByCountry() {

        List<Product> filtered = new ArrayList<Product>();
        
        if ((allProducts == null) || (allProducts.size() == 0)) {
            initProducts();
        }
        if ((allProducts != null) && (allProducts.size() != 0)) {
            for (String country : countries.getTarget()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Filtering on country [ "
                            + country 
                            + " ].");
                }
                for (Product product : allProducts) {
                    if (product
                            .getCountryName()
                            .equalsIgnoreCase(country)) {
                        filtered.add(product);
                    }
                }
            }
        }
        
        if (LOGGER.isDebugEnabled()) { 
            LOGGER.debug("Country filter(s) matched [ "
                    + filtered.size()
                    + " ] products.");
        }
        
        return filtered;
    }
    */
    
    /*
    private List<Product> filterByAor() {

        List<Product> filtered = new ArrayList<Product>();
        
        if ((allProducts == null) || (allProducts.size() == 0)) {
            initProducts();
        }
        if ((allProducts != null) && (allProducts.size() != 0)) {
            for (String aor : aorCodes.getTarget()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Filtering on AOR [ "
                            + aor 
                            + " ].");
                }
                for (Product product : allProducts) {
                    if (product
                            .getAorCode()
                            .equalsIgnoreCase(aor)) {
                        filtered.add(product);
                    }
                }
            }    
        }
        
        if (LOGGER.isDebugEnabled()) { 
            LOGGER.debug("AOR filter(s) matched [ "
                    + filtered.size()
                    + " ] products.");
        }
        
        return filtered;
    }
    
    */
    
    
    private List<RoDProduct> filterByNrn() throws ServiceUnavailableException {

        List<RoDProduct> filtered = new ArrayList<RoDProduct>();
        
        if ((allProducts == null) || (allProducts.size() == 0)) {
            initProducts();
        }
        if ((allProducts != null) && (allProducts.size() != 0)) {
                
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Filtering on NRN [ "
                        + getNrnFilter() 
                        + " ].");
            }
            
            for (RoDProduct product : allProducts) {
                
                if (product
                        .getNRN()
                        .toLowerCase()
                        .contains(getNrnFilter().toLowerCase())) {
                    filtered.add(product);
                }
            }
        }
        else {
            LOGGER.warn("Unable to initialize the list of products.");
        }
        
        if (LOGGER.isDebugEnabled()) { 
            LOGGER.debug("NRN filter(s) matched [ "
                    + filtered.size()
                    + " ] products.");
        }
        return filtered;
    }
    
    private List<RoDProduct> filterByNsn() throws ServiceUnavailableException {

        List<RoDProduct> filtered = new ArrayList<RoDProduct>();
        
        
        if ((allProducts == null) || (allProducts.size() == 0)) {
            initProducts();
        }
        if ((allProducts != null) && (allProducts.size() != 0)) {
                
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Filtering on NSN [ "
                        + getNsnFilter()
                        + " ].");
            }
            
            for (RoDProduct product : allProducts) {
                
                if (product
                        .getNSN()
                        .toLowerCase()
                        .contains(getNsnFilter().toLowerCase())) {
                    filtered.add(product);
                }
            }
        }
        else {
            LOGGER.warn("Unable to initialize the list of products.");
        }
        
        if (LOGGER.isDebugEnabled()) { 
            LOGGER.debug("NSN filter(s) matched [ "
                    + filtered.size()
                    + " ] products.");
        }
        return filtered;
    }
    
    private List<RoDProduct> filterByProductType() throws ServiceUnavailableException {

        List<RoDProduct> filtered = new ArrayList<RoDProduct>();
        
        if ((allProducts == null) || (allProducts.size() == 0)) {
            initProducts();
        }
        if ((allProducts != null) && (allProducts.size() != 0)) {
            for (String productType : productTypes.getTarget()) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Filtering on product type [ "
                            + productType 
                            + " ].");
                }
                
                for (RoDProduct product : allProducts) {
                    if (product
                            .getProductType()
                            .equalsIgnoreCase(productType)) {
                        filtered.add(product);
                    }
                }
            }
        }
        else {
            LOGGER.warn("Unable to initialize the list of products.");
        }
        
        if (LOGGER.isDebugEnabled()) { 
            LOGGER.debug("Product type filter(s) matched [ "
                    + filtered.size()
                    + " ] products.");
        }
        return filtered;
    }
    
    /**
     * Generic method that will calculate the intersection of two input list
     * objects.
     * 
     * @param A Populated list.
     * @param B Populated list.
     * 
     * @return The intersection of lists A and B.
     */
    public List<Product> intersection(int iteration, List<Product> A, List<Product> B) {
        List<Product> intersection = new ArrayList<Product>();
        if ((A != null) && (B != null)) {
            LOGGER.debug("Round [ " + iteration + " ] A and B are NOT null.");
            for (Product product : A) {
                if (B.remove(product)) {
                    intersection.add(product);
                }
            }
        }
        else if ((A == null) && (B == null)) { 
            // If both A and B are null set the return value to null.
            // This ensures that nested calls compute the intersection
            // correctly.
            intersection = null;
            LOGGER.debug("Round [ " + iteration + " ] A and B are null.");
        }
        else if (A == null) {
            LOGGER.debug("Round [ " + iteration + " ] A is null.");
            if (B != null) {
                intersection.addAll(B);
                LOGGER.debug("Round [ " + iteration + " ] B is NOT null.");
            }
        }
        else if (B == null) {
            LOGGER.debug("Round [ " + iteration + " ] B is null.");
            if (A != null) {
                intersection.addAll(A);
                LOGGER.debug("Round [ " + iteration + " ] A is NOT null.");
            }
        }
        return intersection;
    }
    
    /**
     * This method is called when the "AND" radio button is selected in the 
     * View.  It will accept the lists that were filtered based on 
     * user-selected criteria, then calculate the intersection of the lists
     * producing a list that matches all of the user-supplied search criteria.
     * 
     * @param byCountry List filtered by country.  
     * @param byAor List filtered by AOR.
     * @param byProductType List filtered by product type.
     * @param byNrn List filtered by NRN.
     * @param byNsn List filtered by NSN.
     * 
     * @return A list created by combining the input lists.
     */
    public List<Product> intersection (
            List<Product> byCountry,
            List<Product> byAor,
            List<Product> byProductType,
            List<Product> byNrn,
            List<Product> byNsn) {
        return intersection(4, byNsn, 
                    intersection(3, byNrn, 
                        intersection(2, byCountry, 
                                intersection(1, byAor, byProductType))));
    }
    
    /**
     * This method is called when the "OR" radio button is selected in the 
     * View.  It will accept the lists that were filtered based on 
     * user-selected criteria, then combine them in a single list that is 
     * used to update the data table in the view.
     * 
     * @param byCountry List filtered by country.  
     * @param byAor List filtered by AOR.
     * @param byProductType List filtered by product type.
     * @param byNrn List filtered by NRN.
     * @param byNsn List filtered by NSN.
     * 
     * @return A list created by combining the input lists.
     */
    public List<Product> combine(
            List<Product> byCountry,
            List<Product> byAor,
            List<Product> byProductType,
            List<Product> byNrn,
            List<Product> byNsn) {
        
        Set<Product> combined = new HashSet<Product>();
        
        if ((byCountry != null) && (byCountry.size() > 0)) {
            for (Product product : byCountry) {
                combined.add(product);
            }
        }
        if ((byAor != null) && (byAor.size() > 0)) {
            for (Product product : byAor) {
                combined.add(product);
            }
        }
        if ((byProductType != null) && (byProductType.size() > 0)) {
            for (Product product : byProductType) {
                combined.add(product);
            }
        }
        if ((byNrn != null) && (byNrn.size() > 0)) {
            for (Product product : byNrn) {
                combined.add(product);
            }
        }
        if ((byNsn != null) && (byNsn.size() > 0)) {
            for (Product product : byNsn) {
                combined.add(product);
            }
        }
        return new ArrayList<Product>(combined);
    }
    
    
    public void applyFilters() throws ServiceUnavailableException {
        
        List<RoDProduct> filteredByCountry     = null;
        List<RoDProduct> filteredByAor         = null;
        List<RoDProduct> filteredByProductType = null;
        List<RoDProduct> filteredByNRN         = null;
        List<RoDProduct> filteredByNSN         = null;
        
        if (applyAnyFilter()) {
            
            // Reset the filtered products list.
            filteredProducts = new ArrayList<RoDProduct>();
            
            //if (applyCountryFilter()) {
            //    filteredByCountry = filterByCountry();
            //}
            //if (applyAorFilter()) {
            //    filteredByAor = filterByAor();
            //}
            if(applyProductTypeFilter()) {
                filteredByProductType = filterByProductType();
            }
            if(applyNrnFilter()) {
                filteredByNRN = filterByNrn();
            }
            if (applyNsnFilter()) {
                filteredByNSN = filterByNsn();
            }
            
            /*
            if (this.getLogicalOperator().equalsIgnoreCase("AND")) {
                filteredProducts = intersection (
                        filteredByCountry, 
                        filteredByAor, 
                        filteredByProductType,
                        filteredByNRN,
                        filteredByNSN);
            }
            else {
                filteredProducts = combine (
                        filteredByCountry, 
                        filteredByAor, 
                        filteredByProductType,
                        filteredByNRN,
                        filteredByNSN);
            }
            */
        }
        else {
            filteredProducts = allProducts;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After applying filters [ "
                    + filteredProducts.size()
                    + " ] products selected.");
        }
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
        currentlySelectedProduct = value;
    }
}
