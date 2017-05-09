package mil.nga;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import mil.nga.exceptions.PropertiesNotLoadedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class used to load a Properties file from the classpath and 
 * provide static access to those properties.  This class was meant to 
 * replace the ridiculously complex "Config" classes that were over
 * engineered.
 * 
 * @author L. Craig Carpenter
 */
public class PropertyLoader {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(
            PropertyLoader.class);
    
    /**
     * Static system properties object
     */
    private static Properties properties = null;
    
    /**
     * Default property file name.
     */
    public static final String DEFAULT_PROPERTY_FILE_NAME = "system.properties";
    
    /**
     * The name of the property file to load.
     */
    private String propertyFileName = null;
    
    /**
     * Default constructor allowing that uses the default properties file.
     */
    private PropertyLoader() {
        setPropertyFileName(DEFAULT_PROPERTY_FILE_NAME);
    }
    
    /**
     * Alternate constructor allowing clients to supply the name of the target
     * properties file.
     * 
     * @param propertyFileName The name of the property file to load.
     * @throws PropertiesNotLoadedException Thrown if the target properties file
     * was not loaded.
     */
    private PropertyLoader(String propertyFileName) {
        setPropertyFileName(propertyFileName);
    }
    
    /**
     * Simple method to convert a ResourceBundle object into a Properties object.
     * This was done so we didn't have to re-write this class.
     * 
     * @param bundle ResourceBundle object.
     * @return Properties object.
     */
    private static Properties convertBundleToProperties(ResourceBundle bundle) {
        Properties props = new Properties();
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, bundle.getString(key));
        }
        return props;
    }
    
    /**
     * Load the target properties file from the classpath.
     * @throws PropertiesNotLoadedException Thrown if the target properties 
     * file was not loaded.
     */
    public PropertyLoader loadProperties() throws PropertiesNotLoadedException {
        
        InputStream stream = null;
        
        LOGGER.info("Initiating load of properties file [ "
                + getPropertyFileName()
                + " ].");
        
        try {
            
            
            //stream = PropertyLoader.class.getResourceAsStream(getPropertyFileName());
            stream = ClassLoader.getSystemResourceAsStream(getPropertyFileName());
            
            // stream = new FileInputStream(new File(getPropertyFileName()));
            if (stream != null) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.load(stream);
            }
            else {
                LOGGER.warn("Unable to load properties using System "
                        + "class loader.  Trying ResourceBundle...");
                ResourceBundle bundle = ResourceBundle.getBundle(
                        getPropertyFileName(), 
                        Locale.getDefault());
                if (bundle != null) {
                    properties = convertBundleToProperties(bundle);
                }
                else {
                    String msg = "Unable to open Stream to target property file [ "
                            + getPropertyFileName() 
                            + " ].  Stream is null.";
                    LOGGER.error(msg);
                    throw new PropertiesNotLoadedException(msg);
                }
            }
            
        }
        catch (FileNotFoundException fnfe) {
            String msg = "Unexpected FileNotFoundException raised while "
                    + "attempting to load the target properties file.  "
                    + "Missing file [ "
                    + getPropertyFileName() 
                    + " ], exception message [ "
                    + fnfe.getMessage()
                    + " ].";
            LOGGER.error(msg);
            throw new PropertiesNotLoadedException(msg);
        }
        catch (IOException ioe) {
            String msg = "Unexpected IOException raised while "
                    + "attempting to load the target properties file.  "
                    + "Target properties file [ "
                    + getPropertyFileName() 
                    + " ], exception message [ "
                    + ioe.getMessage()
                    + " ].";
            LOGGER.error(msg);
            throw new PropertiesNotLoadedException(msg);
        }
        finally {
            if (stream != null) {
                try { stream.close(); } catch (Exception e) {}
            }
        }
        return this;
    }
    
    /**
     * Accessor method for the singleton instance of the 
     * ProductQueryResponseMarshaller class.
     * 
     * @return The singleton instance of the ProductQueryResponseMarshaller .
     * class.
     */
    public static PropertyLoader getInstance() {
        return PropertyLoaderHolder.getSingleton();
    }  
    
    /**
     * Getter method for the name of the target properties file.
     * @return The name of the target properties file.
     */
    public String getPropertyFileName() {
        if ((propertyFileName == null) || (propertyFileName.isEmpty())) {
            propertyFileName = DEFAULT_PROPERTY_FILE_NAME;
        }
        return propertyFileName;
    }
    
    /**
     * Getter method for the system properties.
     * @return The populated system properties object. 
     * @throws PropertiesNotLoadedException Thrown if the target properties 
     * file was not loaded.
     */
    public Properties getProperties() 
            throws PropertiesNotLoadedException {
        if (properties == null) {
            loadProperties();
        }
        return properties;
    }
    
    /**
     * Getter method for a single property.
     * @param key The key of the property to look up.
     * @throws PropertiesNotLoadedException Thrown if the target properties 
     * file was not loaded.
     */
    public String getProperty(String key) 
            throws PropertiesNotLoadedException {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }
    
    /**
     * Getter method for a single property.
     * @param key The key of the property to look up.
     * @param value The default value for the key.
     * @throws PropertiesNotLoadedException Thrown if the target properties 
     * file was not loaded.
     */
    public String getProperty(String key, String value) 
            throws PropertiesNotLoadedException {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key, value);
    }
    
    /**
     * Setter method for the name of the target properties file.
     * @param value The name of the target properties file.
     */
    public void setPropertyFileName(String value) {
        if ((value == null) || (value.isEmpty())) {
            LOGGER.warn("Null or empty name supplied for property file.  "
                    + "Using the default name [ "
                    + DEFAULT_PROPERTY_FILE_NAME
                    + " ].");
            propertyFileName = DEFAULT_PROPERTY_FILE_NAME;
        }
        propertyFileName = value;
    }
    
    /**
     * Construct a String representation of the input system 
     * properties.  If the properties are not populated, a String 
     * containing "NULL" is returned.
     */
    @Override
    public String toString() {
        
        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        
        if ((properties == null) || (properties.isEmpty())) {
            sb.append("NULL");
        }
        else {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key   = (String)entry.getKey();
                String value = (String)entry.getValue();
                sb.append("Key => [ ");
                sb.append(key);
                sb.append(" ], ");
                sb.append("Value => [ ");
                sb.append(value);
                sb.append(" ]");
                sb.append(newLine);
            }
        }
        return sb.toString();
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class PropertyLoaderHolder {
        
        /**
         * Reference to the Singleton instance of the PropertyLoader.
         */
        private static PropertyLoader _instance = 
                new PropertyLoader();
    
        /**
         * Accessor method for the singleton instance of the 
         * PropertyLoader.
         * 
         * @return The Singleton instance of the 
         * PropertyLoader.
         */
        public static PropertyLoader getSingleton() {
            return _instance;
        }
        
    }
    
}
