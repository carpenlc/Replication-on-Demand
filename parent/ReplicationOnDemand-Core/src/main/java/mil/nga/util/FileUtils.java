package mil.nga.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Pattern;


public class FileUtils {
 
    /**
     * Access the target file to get the actual size of the on-disk file. 
     * 
     * @param file String path to the target file.
     * @return The size of the files in bytes.
     * @throws IOException Thrown if there are problems accessing the target
     * file.
     */
    public static long getActualFileSize(Path path) throws IOException {
        
        long size = 0L;
        
        if (path != null) {
        	if (Files.exists(path)) {
        		size = Files.size(path);
        	}
        	else {
        		throw new FileNotFoundException("File [ "
        				+ path
        				+ " ] does not exist.");
        	}
        }
        return size;
    }
    
    /**
     * Access the target file to get the actual size of the on-disk file. 
     * 
     * @param file String path to the target file.
     * @return The size of the files in bytes.
     * @throws IOException Thrown if there are problems accessing the target
     * file.
     */
    public static long getActualFileSize(String path) throws IOException {
        
        long size = 0L;
        
        if ((path != null) && (!path.isEmpty())) {
        	Path p = Paths.get(URIUtils.getInstance().getURI(path));
        	if (Files.exists(p)) {
        		size = Files.size(p);
        	}
        	else {
        		throw new FileNotFoundException("File [ "
        				+ path
        				+ " ] does not exist.");
        	}
        }
        return size;
    }
    
    /**
     * Access the target file to get the actual date of the on-disk file.  
     * This method was added because the date information stored in the 
     * database is always the same as the load date.
     * 
     * @param file String path to the target file.
     * @return The date of the file.
     * @throws IOException Thrown if there are problems accessing the target
     * file.
     */
    public static java.util.Date getActualFileDate(Path path) throws IOException {
        
        java.util.Date fileDate = new java.util.Date(0);
        
        if ((path != null) && (Files.exists(path))) {
            BasicFileAttributes attrs = Files.getFileAttributeView(
                    path, 
                    BasicFileAttributeView.class).readAttributes();
            FileTime t = attrs.lastModifiedTime();
            // LOGGER.info("File time : " + t);
            fileDate = new java.util.Date(t.toMillis());
        }
        return fileDate;
    }
    
    /**
     * Access the target file to get the actual date of the on-disk file.  
     * This method was added because the date information stored in the 
     * database is always the same as the load date.
     * 
     * @param file String path to the target file.
     * @return The date of the file.
     * @throws IOException Thrown if there are problems accessing the target
     * file.
     */
    public static java.sql.Date getActualFileDate(String file) throws IOException {
        
        java.sql.Date fileDate = new java.sql.Date(0);
        
        if ((file != null) && (!file.isEmpty())) {
            Path p = Paths.get(file);
            BasicFileAttributes attrs = Files.getFileAttributeView(
                    p, 
                    BasicFileAttributeView.class).readAttributes();
            FileTime t = attrs.lastModifiedTime();
            // LOGGER.info("File time : " + t);
            fileDate = new java.sql.Date(t.toMillis());
        }
        return fileDate;
    }
    
    /**
     * Extract the extension from the input filename.
     * @param path The full path to the source image file.
     * @return The extension of the source image file.
     */
    public static String getExtension(String path) {
    	String extension = "";
    	if ((path != null) && (!path.isEmpty())) {
    		int dot = path.lastIndexOf('.');
    		extension = (dot > 0 && dot < path.length()) ?
    				path.substring(dot + 1) : "";
    	}
    	return extension;
    }
    
    /**
     * Get the host name.
     * 
     * Updated:  InetAddress.getLocalHost().getHostName() does a DNS query for
     * the local IP address.  The returned value is the first PTR record.  The 
     * problem is that if you have multiple PTR records, the first one returned
     * need not be the same every time.  This turned out to be a problem on the 
     * classified networks in that nearly every time this method was called, it
     * received a different host name.  Method was restructured to first use 
     * the value of the HOSTNAME environment variable, and then if that doesn't
     * work, then use the DNS lookup results.
     * 
     * @return The host name.
     */
    public static String getHostName() {
        
        String host = null;
        
        // This environment variable is for linux/unix
        host = System.getenv("HOSTNAME");
        if ((host == null) || (host.isEmpty())) { 
            // If we're running on Windows the following environment 
            // variable will be set
            host = System.getenv("COMPUTERNAME");
            if ((host == null) || (host.isEmpty())) {
                // Finally, try the portable method.  Know that results may be
                // questionable.
                try {
                    host = InetAddress.getLocalHost().getHostName();
                }
                catch (UnknownHostException uhe) { }
            }
        }
        // If it's still empty just set it to "unavailable"
        if ((host == null) || (host.isEmpty())) { 
            host = "unavailable";
        }
        return host;
    }
    
    /**
     * Delete method that will recursively delete the input file.  If the file
     * is a directory the method will recurse through all of the files in that 
     * directory deleting each one prior to attempting deletion of the input 
     * directory.
     * 
     * @param filename The file to delete.
     */
    public static void delete(String filename) 
            throws IOException {
        if ((filename != null) && (!filename.isEmpty())) {
            delete(new File(filename));
        }
    }
    
    
    /**
     * Java implementation of the <code>mkdir</code> command to create a 
     * target directory.
     * 
     * @param directory Target directory to create.
     * @throws IOException Raised if there are issues performing the directory
     * creation.
     */
    public static void mkdir(String directory) throws IOException {
        // Make sure the directory is unique
        File file = new File(directory);
        
        if (!file.exists()) {
            
            // Updated to ensure directory permissions are wide open
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.mkdir();
            
            if (!file.exists()) {
                throw new IOException(
                        "Unable to create the output archive directory.  "
                        + "Attempted to create [" 
                        + file.getAbsolutePath()
                        + "].");
            }
        }
    }
    
    /**
     * Remove the input directory only if it is empty.
     * 
     * @param directory Directory to delete.
     */
    public static final void rmdirIfEmpty(String directory) {
        if ((directory != null) && (!directory.isEmpty())) {
            File file = new File(directory);
            if (file.exists()) {
                if (file.isDirectory()) {
                    if (file.list().length == 0) {
                        file.delete();
                    }
                }
            }
        }
    }
    
    /**
     * Java implementation of the <code>move</code> command to move a file
     * from one place to another with overwrite.
     * 
     * @param fromFile  Source file for move.
     * @param toFile Target location.
     * @throws IOException Raised if there are issues performing the move.
     */
    public static void move(
            String fromFile, 
            String toFile) throws IOException {
        
        Path from = Paths.get(fromFile);
        Path to   = Paths.get(toFile);
        Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        
    }
    
    
    /**
     * Delete method that will recursively delete the input file.  If the file
     * is a directory the method will recurse through all of the files in that 
     * directory deleting each one prior to attempting deletion of the input 
     * directory.
     * 
     * @param file The file to delete.
     */
    public static void delete(File file) throws IOException {
        String method = "delete() - ";
        if ((file != null) && (file.exists())) {
            if (file.isDirectory()) {
                if (file.list().length == 0) {
                    file.delete();
                }
                else {
                    String files[] = file.list();
                    for (String current : files) {
                        File fileToDelete = new File(file, current);
                        delete(fileToDelete);
                        if (file.list().length == 0) {
                            file.delete();
                        }
                    }
                }
            }
            else {
                file.delete();
            }
        }
        else {
            throw new IOException(method 
                    + "The input file is null or does not exist.");
        }
    }
    
    /**
     * Check to see if the input file path contains a file extension.
     * 
     * @param path A full file path.
     * @return True if the file contains an extension, false otherwise.
     */
    public static boolean hasExtension(String path) {
        if ((path == null) || (path.trim().equalsIgnoreCase(""))) {
            return false;
        }
        int dotPos = path.lastIndexOf(".");
        if ( dotPos < 0 )
            return false;
        int dirPos = path.lastIndexOf( File.separator );
        if ( dirPos < 0 && dotPos == 0 )
            return false;
        if ( dirPos >= 0 && dirPos > dotPos )
            return false;
        return true;
    }
    
    /**
     * String manipulation function to remove any extensions from the input
     * archive file designator.  The archiver classes will add an extension 
     * based on the type of archive that was requested.
     * 
     * @param path The full path to the output archive file.
     * @return The path sans extensions.
     */
    public static String removeExtension(String path) {
        int dotPos = path.lastIndexOf(".");
        if (dotPos < 0) {
            return path;
        }
        int dirPos = path.lastIndexOf( File.separator );
        if ((dirPos < 0) && (dotPos == 0)) {
            return path;
        }
        if ((dirPos >= 0) && (dirPos > dotPos)) {
            return path;
        }
        return path.substring( 0, dotPos );
    }
    
    /** 
     * Remove all file extensions from the input file path.
     * 
     * @param path The file path.
     * @return A full file path with all extensions removed.
     */
    public static String removeExtensions(String path) {
        while (FileUtils.hasExtension(path)) {
            path = FileUtils.removeExtension(path);
        }
        return path;
    }
    
    /**
     * Given a valid URL, this method will extract the filename from 
     * that URL.  This is a very simple one-line command but it saves
     * me from remembering.
     * 
     * @param url Assumes a valid URL.
     * @return The filename extracted from the end of the URL.
     */
    public static final String getFilenameFromURL(String url) {
        String filename = "";
        if ((url != null) && (!url.isEmpty())) {
            filename = url.substring(url.lastIndexOf('/')+1, url.length());
        }
        return filename;
    }
    
    /**
     * Given a valid file path, this method will extract the filename from 
     * that path.  This is a very simple one-line command but it saves
     * me from remembering.
     * 
     * @param url Assumes a valid path.
     * @return The filename extracted from the end of the path.
     */
    public static final String getFilenameFromPath(String path) {
        String filename = "";
        if ((path != null) && (!path.isEmpty())) {
            filename = path.substring(path.lastIndexOf(File.separator)+1, path.length());
        }
        return filename;
    }
    
    /**
     * Simple method to convert a time (in milliseconds) to a printable
     * String.
     * 
     * @param format The format to pass into the SimpleDateFormat class.
     * @param time The time in milliseconds from the epoch.
     * @return The date in String format.
     */
    public static String getTimeAsString(String format, long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(cal.getTime());
    }
    
    /**
     * The File.getLength() method returns file sizes in bytes.  This 
     * method will convert the size information to a long representation
     * in the units of MByte.  If the file is actually less than 1MByte, 1
     * will be returned.
     * 
     * @param bytes The size of the file in bytes.
     * @param si If true output calculation is made on bytes/1000, if false 
     * binary sizes are used (i.e. bytes/1024)
     * @return The size in human readable format
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    /**
     * Generate a random hex encoded string token of the specified length.
     * Since there are two hex characters per byte, the random hex string 
     * returned will be twice as long as the user-specified length.
     *  
     * @param length The number of random bytes to use
     * @return random hex string
     */
    public static synchronized String generateUniqueToken(int length) {

        byte         random[]        = new byte[length];
        Random       randomGenerator = new Random();
        StringBuffer buffer          = new StringBuffer();

        randomGenerator.nextBytes(random);

        for (int j = 0; j < random.length; j++)
        {
            byte b1 = (byte) ((random[j] & 0xf0) >> 4);
            byte b2 = (byte) (random[j] & 0x0f);
            if (b1 < 10)
                buffer.append((char) ('0' + b1));
            else
                buffer.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                buffer.append((char) ('0' + b2));
            else
                buffer.append((char) ('A' + (b2 - 10)));
        }

        return (buffer.toString());
    }

    
    /**
     * This method is used to calculate the entry path to be added to the
     * output archive.  This class will also enforce the requirement that 
     * entry paths cannot exceed 100 characters.
     * 
     * @param targetPath The absolute path to the target file.
     * @param baseDir The base directory.
     * @return The absolute path minus the base directory. 
     */
    public static String getEntryPath(String targetPath, String baseDir) {

        if ((baseDir == null) || (baseDir.trim().equalsIgnoreCase(""))) {
            return targetPath;
        }
        
        // find common path
        String[] target = targetPath.split(Pattern.quote(File.separator));
        String[] base = baseDir.split(Pattern.quote(File.separator));

        String common = "";
        int commonIndex = 0;
        for (int i = 0; i < target.length && i < base.length; i++) {
            if (target[i].equals(base[i])) {
                common += target[i] + File.separator;
                commonIndex++;
            }
        }
        
        String relative = "";
        // is the target a child directory of the base directory?
        // i.e., target = /a/b/c/d, base = /a/b/
        if (commonIndex == base.length) {
            relative = targetPath.substring(common.length());
            // relative = "." + File.separator + targetPath.substring(common.length());
        }
        else {
            // determine how many directories we have to backtrack
            for (int i = 1; i <= commonIndex; i++) {
                relative += "";
                //relative += ".." + File.separator;
            }
            relative += targetPath.substring(common.length());
        }

        return relative;
    }
}
