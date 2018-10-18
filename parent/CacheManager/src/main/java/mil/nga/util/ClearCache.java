package mil.nga.util;

import java.util.Set;

import mil.nga.rod.accelerator.RedisCacheManager;

/**
 * Simple application used to remove all of the key/value pairs from the 
 * Cache.
 * 
 * @author L. Craig Carpenter
 */
public class ClearCache {

    /**
     * Main method.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        int keysRemoved = 0;
        try (RedisCacheManager manager = RedisCacheManager.getInstance()) { 
            Set<String> keySet = manager.getKeys();
            if (keySet.size() > 0) {
                for (String key : keySet) {
                    manager.remove(key);
                    keysRemoved++;
                }
            }
            else {
                System.out.println("The cache is empty.");
            }
        }
        System.out.println("Removed [ "
                + keysRemoved
                + " ] keys.");
    }
}
