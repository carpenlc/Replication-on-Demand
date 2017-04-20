package mil.nga.rod.ejb;

public interface ApplicationInfoI {
    
    /**
     * The specific JNDI interface to look up.
     */
    public static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
    
    /**
     * The server MBean name used for obtaining information about the running 
     * server. 
     */
    public static final String SERVER_MBEAN_OBJECT_NAME = 
            "jboss.as:management-root=server";

    /**
     * MBean attribute that contains the JVM server name.
     */
    public static final String SERVER_NAME_ATTRIBUTE = "name";
    
    /**
     * The name of the EAR file in which the EJBs are packaged.
     */
    public static final String EAR_APPLICATION_NAME = "ReplicationOnDemand";
    
    /**
     * The name of the module (i.e. JAR) containing the EJBs
     */
    public static final String EJB_MODULE_NAME = "ReplicationOnDemandEJB";
    
}
