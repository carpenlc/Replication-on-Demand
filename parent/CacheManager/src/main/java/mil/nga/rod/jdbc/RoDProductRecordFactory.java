package mil.nga.rod.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.rod.model.RoDProduct;

/**
 * Class implementing methods to read/write to the backing data store 
 * containing the <code>RoDProduct</code> data.  This class requires 
 * JPA 2.x or higher (javax.persistence) and a JPA provider.  It was 
 * originally tested with Hibernate as the JPA provider.  Also note 
 * that this class assumes the data source is non-JTA and resource 
 * local.
 * 
 * @author L. Craig Carpenter
 */
public class RoDProductRecordFactory implements AutoCloseable {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		RoDProductRecordFactory.class);
    
	/**
	 * The name of the persistence unit used to persist RoDProduct objects.
	 */
	private static final String PERSISTENCE_UNIT = "RoDProductPersistenceUnit";
	
	/**
	 * Class-level handle to the EntityManager
	 */
	private EntityManager em = null;
	
	/**
	 * Default constructor required by the persistence API.
	 */
	private RoDProductRecordFactory() {}
	
	/**
	 * Accessor method for the class-level <code>EntityManager</code> object.
	 * If the EntityManager object is not yet populated it will be created 
	 * here.
	 * @return A constructed EntityManager object.
	 */
	private EntityManager getEntityManager() {
		if (em == null) {
	        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
			em = emf.createEntityManager();
		}
		return em;
	}
	
    /**
     * Accessor method for the singleton instance of the 
     * <code>RoDProductRecordFactory</code> class.
     * 
     * @return The singleton instance of the 
     * <code>RoDProductRecordFactory</code>.
     * class.
     */
    public static RoDProductRecordFactory getInstance() {
        return RoDProductRecordFactoryHolder.getSingleton();
    } 
    
	/**
	 * Retrieve a list of primary keys from the target persistence unit.
	 * @return The list of primary keys.  
	 */
	public List<String> getKeys() {
		long         start = System.currentTimeMillis();
		List<String> keys  = new ArrayList<String>();
		EntityManager em   = getEntityManager();
		if (em != null) {
			CriteriaBuilder cBuilder = em.getCriteriaBuilder();
			CriteriaQuery<String> cQuery = 
					cBuilder.createQuery(String.class);
			Root<RoDProduct> root = cQuery.from(RoDProduct.class);
			cQuery.select(root.get("key"));
			TypedQuery<String> query = em.createQuery(cQuery);
			keys = query.getResultList();
			if (LOG.isDebugEnabled()) {
            	LOG.debug("[ "
            			+ keys.size()
            			+ " ] RoDProducts retrieved in [ "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
		}
		else {
			LOG.error("Unable to construct the EntityManager object.  "
					+ "Resulting list will be null.");
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
	 */
	public RoDProduct getProduct(String key) {
		long       start   = System.currentTimeMillis();
		RoDProduct product = null;
		if ((key != null) && (!key.isEmpty())) {
			EntityManager em = getEntityManager();
			if (em != null) {
				CriteriaBuilder cBuilder = em.getCriteriaBuilder();
				CriteriaQuery<RoDProduct> cQuery = 
						cBuilder.createQuery(RoDProduct.class);
				Root<RoDProduct> root = cQuery.from(RoDProduct.class);
				cQuery.where(
						cBuilder.equal(
								root.get("key"), 
								cBuilder.parameter(String.class, "key")));
				TypedQuery<RoDProduct> query = em.createQuery(cQuery);
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
				LOG.error("Unable to construct the EntityManager object.  "
						+ "Resulting list will be null.");
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
	 */
	public List<RoDProduct> getProducts() {
		long start = System.currentTimeMillis();
		List<RoDProduct> products = new ArrayList<RoDProduct>();
		EntityManager em = getEntityManager();
		if (em != null) {
			CriteriaBuilder cBuilder = em.getCriteriaBuilder();
			CriteriaQuery<RoDProduct> cQuery = 
					cBuilder.createQuery(RoDProduct.class);
			cQuery.from(RoDProduct.class);
			products = em.createQuery(cQuery).getResultList();
			if (LOG.isDebugEnabled()) {
            	LOG.debug("[ "
            			+ products.size()
            			+ " ] RoDProducts retrieved in [ "
            			+ (System.currentTimeMillis() - start)
            			+ " ] ms.");
            }
		}
		else {
			LOG.error("Unable to construct the EntityManager object.  "
					+ "Resulting list will be null.");
		}
		return products;
	}

	/**
	 * Simple method to remove a single <code>RoDProduct</code> from the 
	 * backing data store.  all products in the target persistence unit.
	 * @param key The primary key to delete.
	 */
	public void remove(String key) {
		EntityManager em = getEntityManager();
		try {
			RoDProduct prod = getProduct(key);
			if (prod != null) {
				
				em.getTransaction().begin();
				em.remove(prod);
				em.getTransaction().commit();
				
			}
			else {
				LOG.error("Unable to find product with key [ "
						+ key
						+ " ].  Nothing to remove.");
			}
		}
		catch (NoResultException nre) {
			LOG.error("Unable to find product with key [ "
					+ key
					+ " ].  NoResultException encountered.  "
					+ "Nothing to remove.");
		}
	}
	
	/**
	 * Method used to store an input <code>RoDProduct</code> object in the 
	 * backing data store.  It uses the merge function of 
	 * <code>EntityManager</code> so it can be used for insert or update.
	 * @param product The product to persist.
	 */
	public void persist(RoDProduct product) {
		long start = System.currentTimeMillis();
		if (product != null) {
			EntityManager em = getEntityManager();
			if (em != null) {
				
				em.getTransaction().begin();
				em.merge(product);
                                em.flush();
				em.getTransaction().commit();
				
                if (LOG.isDebugEnabled()) {
                	LOG.debug("Persisted RoDProduct with key [ "
                			+ product.getKey()
                			+ " ] in "
                			+ (System.currentTimeMillis() - start)
                			+ " ] ms.");
                }
			}
		}
		else {
			LOG.error("Input RoDProduct object is null.  Persist not "
					+ "performed.");
		}
	}
	
	/**
	 * Close the class-level EntityManager object.
	 */
	public void close() {
		if (em != null) {
			em.getEntityManagerFactory().close();
			em.close();
		}
	}
	
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class RoDProductRecordFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the RoDRecordFactory.
         */
        private static RoDProductRecordFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * RoDProductRecordFactory.
         */
        public static RoDProductRecordFactory getSingleton() {
            if (_instance == null) {
                _instance = new RoDProductRecordFactory();
            }
            return _instance;
        }
        
    }
}
