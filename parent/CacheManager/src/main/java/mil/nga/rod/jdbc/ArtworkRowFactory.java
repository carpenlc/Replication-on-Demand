package mil.nga.rod.jdbc;

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

import mil.nga.rod.model.ArtworkRow;

/**
 * Simple class to interact with the existing NPD tables storing the artwork
 * data associated with the replication-on-demand (RoD) products.  
 * 
 * 
 * 
 * @author L. Craig Carpenter
 */
public class ArtworkRowFactory implements AutoCloseable {
    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(
    		ArtworkRowFactory.class);
    
	/**
	 * The name of the persistence unit used to persist RoDProduct objects.
	 */
	public static final String PERSISTENCE_UNIT = "ArtworkRowPersistenceUnit";
	
	/**
	 * Class-level handle to the EntityManager
	 */
	private EntityManager em = null;
	
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
	 * Retrieve a single <code>ArtworkRow</code> based on the input NRN/NSN
	 * combination.
	 * @param nrn The NRN string.
	 * @param nsn The NSN String.
	 * @return Single <code>ArtworkRow</code> matching the input NRN/NSN 
	 * combination.  Null if the parameters are not supplied by the caller, 
	 * or the matching object is not found in the database.
	 */
	public ArtworkRow getArtwork(String nrn, String nsn) throws NoResultException {
		long       start   = System.currentTimeMillis();
		ArtworkRow artwork = null;
		if ((nsn != null) && (!nsn.isEmpty())) {
			if ((nrn != null) && (!nrn.isEmpty())) {
				
				LOG.debug("Using NRN => [ "+ nrn + " ] and NSN => [ " + nsn + " ]");
				EntityManager em = getEntityManager();
				if (em != null) {
					CriteriaBuilder cBuilder = em.getCriteriaBuilder();
					CriteriaQuery<ArtworkRow> cQuery = 
							cBuilder.createQuery(ArtworkRow.class);
					Root<ArtworkRow> root = cQuery.from(ArtworkRow.class);
					cQuery.where(
							cBuilder.and(
							        cBuilder.equal(
									        root.get("nrn"), 
									        cBuilder.parameter(String.class, "nrn")),
									cBuilder.equal(
											root.get("nsn"), 
									        cBuilder.parameter(String.class, "nsn"))
							)
					);
					TypedQuery<ArtworkRow> query = em.createQuery(cQuery);
					query.setParameter("nrn", nrn);
					query.setParameter("nsn", nsn);
					artwork = query.getSingleResult();
					if (LOG.isDebugEnabled()) {
		            	LOG.debug("ArtworkRow with NRN => [ "
		            			+ nrn
		            			+ " ] and NSN => [ "
		            			+ nsn 
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
				LOG.error("Input NRN value is null or not populated.  Query will "
						+ "not be executed.");
			}
		}
		else {
			LOG.error("Input NSN value is null or not populated.  Query will "
					+ "not be executed.");
		}
		return artwork;
	}
	
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
     * <code>ArtworkRowFactory</code> class.
     * 
     * @return The singleton instance of the 
     * <code>ArtworkRowFactory</code>.
     * class.
     */
    public static ArtworkRowFactory getInstance() {
        return ArtworkRowFactoryHolder.getSingleton();
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class ArtworkRowFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the ArtworkRowFactory.
         */
        private static ArtworkRowFactory _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * ArtworkRowFactory.
         */
        public static ArtworkRowFactory getSingleton() {
            if (_instance == null) {
                _instance = new ArtworkRowFactory();
            }
            return _instance;
        }
        
    }
}
