package mil.nga.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.types.HashType;

/**
 * This class is used to generate a hash for the input file.  It contains a 
 * a couple of different versions of method <code>getHash</code>.  The class
 * will support the generation of any hash types defined in the 
 * <code>HashTypes</code> enumeration type. 
 * 
 * Note: This class has a dependency on the commons codec library because we 
 * ran into issues when converting the output hashes to Base64 using the JDK 
 * classes (specifically, leading 0s were being dropped).
 * 
 * This class was adapted from the HashGenerator EJB removing the bean 
 * annotations and changing it to use the NIO library.
 * 
 * @author L. Craig Carpenter
 */
public class HashGenerator {

    /**
     * Set up the Log4j system for use throughout the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            HashGenerator.class);
    
    /**
     * Default constructor.
     */
    public HashGenerator() { }
    
    /**
     * Compute a hash of the input file.  For available hash types see 
     * @see mil.nga.types.HashType.
     * 
     * @param filename The full path for the file we want the hash computed.
     * @param type The hash type to generate.
     * @return The computed hash value.
     */
    public String getHash(String filename, HashType type) {
        
        String hash = null;
        
        if ((filename != null) && (!filename.isEmpty())) {
            Path p = Paths.get(filename);
            hash = getHash(p, type);
        }
        else {
            LOGGER.error("The input file name is null or empty.  The returned "
                    + "hash will be null.");
        }
        
        return hash;
    }
    
    /**
     * Compute a hash of the input file.  For available hash types see 
     * @see mil.nga.types.HashType.
     * 
     * @param p Path object of file we want the hash computed.
     * @param type The hash type to generate.
     * @return The computed hash value.
     */
    public String getHash(Path p, HashType type) {
        
        String hash = null;
        
        if ((p != null) && (Files.exists(p))) {
            long startTime = System.currentTimeMillis();
            switch (type) {
                case MD5 : 
                    hash = getMD5Hash(p);
                    break;
                case SHA1:
                    hash = getSHA1Hash(p);
                    break;
                case SHA256:
                    hash = getSHA256Hash(p);
                    break;
                case SHA384:
                    hash = getSHA384Hash(p);                        
                    break;
                case SHA512:
                    hash = getSHA512Hash(p);                        
                    break;
                default:
                    LOGGER.error("Client requested hash type [ "
                            + type.getText()
                            + " ] which is not yet implemented.");
            }
            
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                       "Hash type [ "
                        + type.getText()
                        + " ] for file [ "
                        + p.toString()
                        + " ] created in [ "
                        + Long.toString(elapsedTime)
                        + " ] ms.");
            }
            
        }
        else {
            LOGGER.error("The input file is null or does not exist.  Unable "
                    + "to generate the file hash." );
        }
        
        return hash;
    }
    
    /**
     * Calculate the MD5 hash using the Apache Commons Codec classes.  
     * 
     * @param file The file we need the hash for.
     * @return The calculated MD5 hash.
     */
    private String getMD5Hash(Path file) {

        InputStream is   = null;
        String      hash = null;

        try {
            is = Files.newInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                 "Unexpected IOException encountered while generating "
                 + "the [ " 
                 + HashType.MD5.getText() 
                 + " ] hash for file [ "
                 + file.toString()
                 + " ].  Exception message [ "
                 + ioe.getMessage()
                 + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                    try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-1 hash using the Apache Commons Codec classes.
     * Note: SHA-1 hash generation seems to take about twice as long as MD5 
     * hash generation.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA1 hash.
     */
    private String getSHA1Hash(Path file) {

        InputStream is   = null;
        String      hash = null;

        try {
            is = Files.newInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha1Hex(is);
        }
        catch (IOException ioe) {
             LOGGER.error(
                     "Unexpected IOException encountered while generating "
                     + "the [ " 
                     + HashType.SHA1.getText() 
                     + " ] hash for file [ "
                     + file.toString()
                     + " ].  Exception message [ "
                     + ioe.getMessage()
                     + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }

    /**
     * Calculate the SHA-256 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA256 hash.
     */
    private String getSHA256Hash(Path file) {

        InputStream is   = null;
        String      hash = null;

        try {
            is = Files.newInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA256.getText() 
                + " ] hash for file [ "
                + file.toString()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-384 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA384 hash.
     */
    private String getSHA384Hash(Path file) {

        InputStream is   = null;
        String      hash = null;

        try {
            is = Files.newInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha384Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA384.getText() 
                + " ] hash for file [ "
                + file.toString()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
    
    /**
     * Calculate the SHA-512 hash using the Apache Commons Codec classes.
     * 
     * @param file The file we need the hash for.
     * @return The calculated SHA512 hash.
     */
    private String getSHA512Hash(Path file) {

        InputStream is   = null;
        String      hash = null;

        try {
            is = Files.newInputStream(file);
            hash = org.apache.commons.codec.digest.DigestUtils.sha512Hex(is);
        }
        catch (IOException ioe) {
            LOGGER.error(
                "Unexpected IOException encountered while generating "
                + "the [ " 
                + HashType.SHA512.getText() 
                + " ] hash for file [ "
                + file.toString()
                + " ].  Exception message [ "
                + ioe.getMessage()
                + " ].  Method will return a null hash.");
        }
        finally {
            if (is != null) {
                try { is.close(); } catch (Exception e) {}
            }
        }
        return hash;
    }
}
