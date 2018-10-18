package mil.nga.rod.accelerator;

import java.util.Set;

/**
 * Interface defining any constants used, and methods that the Cache manager 
 * code must implement.
 * 
 * @author L. Craig Carpenter
 */
public interface CacheManagerI {

    /**
     * The defailt Redis host
     */
    public static final String DEFAULT_REDIS_HOST = "localhost";
    
    /**
     * The defailt Redis port 
     */
    public static final int DEFAULT_REDIS_PORT = 6379;
    
    /**
     * Property containing the host name where the redis cache resides.
     */
    public static final String REDIS_HOSTNAME_PROPERTY = "redis.host";
    
    /**
     * If Redis is not running on the standard port, this property can be set
     * identifying the correct port.  
     */
    public static final String REDIS_PORT_PROPERTY = "redis.port";
    
    /**
     * Retrieve a request accelerator record from the cache.
     * 
     * @param key Key to query for. 
     * @return The value associated with the input key.  This method
     * returns null of unable to retrieve the key from the cache.
     */
    public String get(String key);
    
    /**
     * Get a Set containing all of the keys that are currently stored in the 
     * target cache.
     *   
     * @return A Set containing all of the keys stored in the Redis cache.
     * @throws JedisConnectionException Runtime exception thrown if a 
     * connection cannot be made to the local Redis cache. 
     */
    public Set<String> getKeys();
    
    /**
     * Store a key/value pair in the target cache.
     * 
     * @param key Key to query for. 
     * @param value The value associated with the input key.  This method
     * returns null of unable to retrieve the key from the cache.
     */
    public void put(String key, String value);
    
    /**
     * Remove a key/value pair from the target cache.
     * 
     * @param key Key to remove. 
     */
    public void remove(String key);
}
