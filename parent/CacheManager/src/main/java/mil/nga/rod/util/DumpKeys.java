package mil.nga.rod.util;

import java.util.Set;

import mil.nga.cache.RedisCacheManager;

/**
 * Simple application used to output a list of all keys in the cache.
 * 
 * @author L. Craig Carpenter
 */
public class DumpKeys {

    /**
     * Main method.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
    	long start = System.currentTimeMillis();
        try (RedisCacheManager manager = RedisCacheManager.getInstance()) { 
            Set<String> keySet = manager.getKeys();
            if (keySet.size() > 0) {
                for (String key : keySet) {
                    System.out.println(key);
                }
                System.out.println("The cache contains [ "
                        + keySet.size()
                        + " ] elements.");
            }
            else {
                System.out.println("The cache is empty.");
            }
        }
        System.out.println("Keys retreived in [ "
        		+ (System.currentTimeMillis() - start)
        		+ " ] ms.");
    }
}
