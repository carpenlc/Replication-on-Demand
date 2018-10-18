package mil.nga.rod.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.ServiceUnavailableException;
import mil.nga.rod.model.RoDProduct;

/**
 * This class is responsible for providing the Java interface to the back-end 
 * data tables containing the pre-constructed Replication-on-Demand product 
 * information for display in the target web site.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class RoDProductService implements Serializable {

	/**
	 * Persistence context for the datasource containing the replication-on-demand
	 */
	public static final String ROD_PRODUCT_PERSISTENCE_CONTEXT = 
			"RoD-Product-JPA";
	
    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductService.class);
    
	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -622068736906538812L;
	
	/**
	 * JPA persistence entity manager used throughout the class.
	 */
	@PersistenceContext(unitName=ROD_PRODUCT_PERSISTENCE_CONTEXT)
	private EntityManager em;
	
	/**
	 * Required default no-arg constructor.
	 */
	public RoDProductService() {}
	
	/**
	 * Getter method to attempt to obtain a handle to data source 
	 * <code>EntityManager</code>
	 * @return A handle to the constructed <code>EntityManager</code> object.
	 */
	public EntityManager getEntityManager() 
			throws ServiceUnavailableException {
		if (em == null) {
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Container-injected EntityManager is null.  "
						+ "Creating an un-managed EntityManager.");
			}
			
			EntityManagerFactory emFactory = 
					Persistence.createEntityManagerFactory(
							ROD_PRODUCT_PERSISTENCE_CONTEXT);
			if (emFactory != null) {
				em = emFactory.createEntityManager();
			}
			else {
				LOG.error("Unable to create an instance of the required "
						+ "EntityManagerFactory.  EntityManager will also "
						+ "be null.");
			}
			if (em == null) {
				LOG.error("Class-level EntityManager is still null.  Throwing "
						+ "exception to callers.");
				throw new ServiceUnavailableException("Unable to start the "
						+ "JPA subsystem.  Cannot create the EntityManger.");
			}
		}
		return em;
	}
	
	/**
	 * Retrieve a list of primary keys from the target persistence unit.
	 * 
	 * @return The list of primary keys.  
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	public List<String> getKeys() throws ServiceUnavailableException {
		
		long         start = System.currentTimeMillis();
		List<String> keys  = new ArrayList<String>();

		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<String> cQuery = 
				cBuilder.createQuery(String.class);
		Root<RoDProduct> root = cQuery.from(RoDProduct.class);
		cQuery.select(root.get("key"));
		TypedQuery<String> query = getEntityManager().createQuery(cQuery);
		keys = query.getResultList();
		
		if (LOG.isDebugEnabled()) {
        	LOG.debug("[ "
        			+ keys.size()
        			+ " ] RoDProducts retrieved in [ "
        			+ (System.currentTimeMillis() - start)
        			+ " ] ms.");
        }
		return keys;
	}
	
	/**
	 * Retrieve a single <code>RoDProduct</code> based on the input key.
	 * @param key The primary key for a specific <code>RoDProduct</code> 
	 * object.
	 * @return Single <code>RoDProduct</code> matching the input primary 
	 * key.  Null if the key is not supplied by the caller, or the key is 
	 * not found in the database.
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	public RoDProduct getProduct(String key) 
			throws ServiceUnavailableException {
		
		long       start   = System.currentTimeMillis();
		RoDProduct product = null;
		
		if ((key != null) && (!key.isEmpty())) {
			
			CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<RoDProduct> cQuery = 
					cBuilder.createQuery(RoDProduct.class);
			Root<RoDProduct> root = cQuery.from(RoDProduct.class);
			cQuery.where(
					cBuilder.equal(
							root.get("key"), 
							cBuilder.parameter(String.class, "key")));
			TypedQuery<RoDProduct> query = getEntityManager().createQuery(cQuery);
			query.setParameter("key", key);
			product = query.getSingleResult();
			
			if (LOG.isDebugEnabled()) {
            	LOG.debug("RoDProduct with key [ "
            			+ key
            			+ " ] retrieved in [ "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
		}
		else {
			LOG.error("Input key value is null or not populated.  Query will "
					+ "not be executed.");
		}
		return product;
	}
	
	/**
	 * Simple method to retrieve all products in the target persistence unit.
	 * @return The list of all <code>RoDProduct</code> objects in the target 
	 * persistence unit.
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	public List<RoDProduct> getProducts() 
			throws ServiceUnavailableException {
		
		long start = System.currentTimeMillis();
		List<RoDProduct> products = new ArrayList<RoDProduct>();
		
		CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RoDProduct> cQuery = 
				cBuilder.createQuery(RoDProduct.class);
		cQuery.from(RoDProduct.class);
		products = getEntityManager().createQuery(cQuery).getResultList();
		
		if (LOG.isDebugEnabled()) {
        	LOG.debug("[ "
        			+ products.size()
        			+ " ] RoDProducts retrieved in [ "
        			+ (System.currentTimeMillis() - start)
        			+ " ] ms.");
        }

		return products;
	}
	
	/**
	 * Method used to store an input <code>RoDProduct</code> object in the 
	 * backing data store.  It uses the merge function of 
	 * <code>EntityManager</code> so it can be used for insert or update.
	 * @param product The product to persist.
	 * @throws ServiceUnavailableException Thrown if we cannot start the 
	 * JPA subsystem.
	 */
	public void persist(RoDProduct product) 
			throws ServiceUnavailableException {
		
		long start = System.currentTimeMillis();
		if (product != null) {

			// getEntityManager().getTransaction().begin();
			getEntityManager().merge(product);
			// getEntityManager().getTransaction().commit();
				
            if (LOG.isDebugEnabled()) {
            	LOG.debug("Persisted RoDProduct with key [ "
            			+ product.getKey()
            			+ " ] in "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
		}
		else {
			LOG.error("Input RoDProduct object is null.  Persist not "
					+ "performed.");
		}
	}
}
