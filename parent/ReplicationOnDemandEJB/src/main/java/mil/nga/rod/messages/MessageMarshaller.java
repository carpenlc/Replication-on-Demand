package mil.nga.rod.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class was created for testing purposes.  It will "marshall"
 * an input Object into it's String-based JSON equivalent.
 * 
 * @author L. Craig Carpenter
 */
public class MessageMarshaller {

    /**
     * Set up the LogBack system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            MessageMarshaller.class);
    
    /**
     * Accessor method for the singleton instance of the 
     * ProductQueryResponseMarshaller class.
     * 
     * @return The singleton instance of the ProductQueryResponseMarshaller .
     * class.
     */
    public static MessageMarshaller getInstance() {
        return MessageMarshallerHolder.getSingleton();
    }    
    
    /**
     * Convert the input object into JSON format. 
     * 
     * @param obj A populated object.
     * @return A JSON String representation of the input Object.
     */
    public String marshall(Object obj) {
        
        String json = "null";
        
        if (obj != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(obj);
            }
            catch (JsonProcessingException jpe) {
                LOGGER.error("Unexpected JsonProcessingException encountered "
                        + "while attempting to marshall the input "
                        + "object to JSON.  Exception message [ "
                        + jpe.getMessage()
                        + " ].");
            }
        }
        else {
            LOGGER.warn("Input object is null.  Unable to "
                    + "marshall the object to JSON.");
        }
        return json;
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class MessageMarshallerHolder {
        
        /**
         * Reference to the Singleton instance of the 
         * ProductQueryResponseMarshaller
         */
        private static MessageMarshaller _instance = 
                new MessageMarshaller();
    
        /**
         * Accessor method for the singleton instance of the 
         * ProductQueryResponseMarshaller.
         * @return The Singleton instance of the 
         * ProductQueryResponseMarshaller.
         */
        public static MessageMarshaller getSingleton() {
            return _instance;
        }
        
    }
}
