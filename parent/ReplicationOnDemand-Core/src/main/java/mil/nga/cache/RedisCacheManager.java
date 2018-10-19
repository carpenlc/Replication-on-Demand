package mil.nga.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Very simple class implementing the Java-based interface to the Redis cache
 * running on the local node.  This class assumes that the cache is bound to 
 * the local interface and running on the default port (6379).
 * 
 * @author L. Craig Carpenter
 */
public class RedisCacheManager 
        implements CacheManagerI, AutoCloseable {

    /**
     * Set up the Log4j system for use throughout the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            RedisCacheManager.class);
    
    /**
     * Private connection pool 
     */
    private JedisPool pool;
    
    // Private internal members used if the target redis server is not local
    private String host = null;
    private int    port = DEFAULT_REDIS_PORT;
    
    /**
     * Default constructor used to set up the Redis connection pool.
     */
    private RedisCacheManager() { 
        PropertyLoader pLoader = PropertyLoader.getInstance();
        try {
            setHost(pLoader.getProperty(REDIS_HOSTNAME_PROPERTY));
        }
        catch (Exception e) {
            setHost(DEFAULT_REDIS_HOST);
        }
        try {
            setPort(Integer.parseInt(
                    pLoader.getProperty(REDIS_PORT_PROPERTY)));
        }
        catch (Exception e) {
            setPort(DEFAULT_REDIS_PORT);
        }
        
        pool = new JedisPool(new JedisPoolConfig(), getHost(), getPort());
    }

    /**
     * Retrieve a request accelerator record from the cache.
     * 
     * @param key Key to query for. 
     * @return The value associated with the input key.  This method
     * returns null of unable to retrieve the key from the cache.
     * @throws JedisConnectionException Runtime exception thrown if a 
     * connection cannot be made to the local Redis cache. 
     */
    public String get(String key) {
        String value = null;
        if ((key != null) && (!key.isEmpty())) {
            try (Jedis jedis = pool.getResource()) {
                value = jedis.get(key);
            }
        }
        else {
            LOGGER.warn("The input key is null or empty.  It will not "
                    + "be used to query the cache.  Return data will be null.");
        }
        return value;
    }

    /**
     * Getter method for the Redis cache host name.
     * 
     * @return The host name of the server running the target Redis cache.
     */
    public String getHost() {
        return host;
    }
    
    /**
     * Get a Set containing all of the keys that are currently stored in the 
     * target cache.
     *   
     * @return A Set containing all of the keys stored in the Redis cache.
     * @throws JedisConnectionException Runtime exception thrown if a 
     * connection cannot be made to the local Redis cache. 
     */
    public Set<String> getKeys() {
        Set<String> keySet = null;
        try (Jedis jedis = pool.getResource()) {
            keySet = jedis.keys("*");
        }
        return keySet;
    }
    
    /**
     * Get a List containing all of the keys that are currently stored in the 
     * target cache.  This was added because I got tired of converting the Set 
     * to a list throughout the code.
     *   
     * @return A List containing all of the keys stored in the Redis cache.
     * @throws JedisConnectionException Runtime exception thrown if a 
     * connection cannot be made to the local Redis cache. 
     */
    public List<String> getKeysAsList() {
    	List<String> keyList = null;
        Set<String>  keySet  = null;
        try (Jedis jedis = pool.getResource()) {
            keySet = jedis.keys("*");
        }
        if ((keySet == null) || (keySet.size() == 0)) { 
        	keyList = new ArrayList<String>();
        }
        else {
        	keyList = new ArrayList<String>(keySet);
        }
        return keyList;
    }
    
    /**
     * Getter method for the Redis cache port.
     * 
     * @return The port on which the Redis cache is listening.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Accessor method for the singleton instance of the 
     * RedisCacheManager class.
     * 
     * @return The singleton instance of the RedisCacheManager.
     * class.
     */
    public static RedisCacheManager getInstance() {
        return RedisCacheManagerHolder.getSingleton();
    } 
    
    /**
     * Store a key/value pair in the target cache.
     * 
     * @param key Key to query for. 
     * @param value The value associated with the input key.  This method
     * returns null of unable to retrieve the key from the cache.
     * @throws JedisConnectionException Runtime exception thrown if a 
     * connection cannot be made to the local Redis cache. 
     */
    public void put(String key, String value) {
        if ((key != null) && (!key.isEmpty())) {
            if ((value != null) && (!value.isEmpty())) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.set(key, value);
                }
            }
            else {
                LOGGER.error("The input value is null or empty.  It will not "
                        + "be stored in the cache.");
            }
        }
        else {
            LOGGER.error("The input key is null or empty.  It will not "
                    + "be used to identify a record in the cache.");
        }
    }
 
    /**
     * Remove a key/value pair from the target cache.
     * 
     * @param key Key to remove. 
     */
    public void remove(String key) {
        if ((key != null) && (!key.isEmpty())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Removing key [ "
                        + key
                        + " ].");
            }
            try (Jedis jedis = pool.getResource()) {
                jedis.del(key);
            }
        }
        else {
            LOGGER.error("The input value is null or empty.  No attempt will "
                    + "be made to remove the key.");
        }
    }
    
    /**
     * Close the Jedis connection pool.
     */
    @Override
    public void close() {
        if (pool != null) {
            LOGGER.info("Closing the Jedis connection pool.");
            pool.destroy();
        }
    }
    
    /**
     * Getter method for the host name on which the Redis cache is running.
     * 
     * @param value The host on which the Redis cache is running.
     */
    public void setHost(String value) {
        if ((value == null) || (value.isEmpty())) { 
            host = DEFAULT_REDIS_HOST;
        }
        host = value;
    }
    
    /**
     * Getter method for the Redis cache port.
     * 
     * @param value The port on which the Redis cache is listening.
     */
    public void setPort(int value) {
        port = value;
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class RedisCacheManagerHolder {
        
        /**
         * Reference to the Singleton instance of the RedisCacheManager.
         */
        private static RedisCacheManager _instance = null;
    
        /**
         * Accessor method for the singleton instance of the 
         * RoDRecordFactory.
         * 
         * @return The Singleton instance of the RedisCacheManager.
         */
        public static RedisCacheManager getSingleton() {
            if (_instance == null) {
                _instance = new RedisCacheManager();
            }
            return _instance;
        }
        
    }
}
