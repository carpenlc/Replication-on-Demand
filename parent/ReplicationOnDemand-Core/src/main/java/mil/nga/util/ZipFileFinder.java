package mil.nga.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZipFileFinder {

	/**
	 * Execute a search on the filesystem for files that match the input 
	 * pattern.
	 * 
	 * @param path The starting location for the search.
	 * @param pattern The file pattern to look for.
	 * @exception IOException Thrown during the search process.
	 */
	public static List<Path> find(URI uri, String pattern) 
			throws IOException {		
		List<Path> matches = new ArrayList<Path>();
		try (Finder finder = new Finder(uri, pattern)) {
			Files.walkFileTree(Paths.get(uri), finder);
			matches = finder.getResults();
		}
		return matches;
	}
	
	/**
	 * Internal class that extends the SimpleFileVisitor class that implements
	 * the actual search.
	 * 
	 * @author carpenlc
	 *
	 */
	public static class Finder extends SimpleFileVisitor<Path> implements AutoCloseable {
		
		/**
		 * Internal PathMatcher object.
		 */
		private final PathMatcher _matcher;
		
		/**
		 * Accumulator saving the list of matches found on the file system.
		 */
		private List<Path> _matches = null;
		
		/**
		 * Class-level reference to the zip file system object.
		 */
		private FileSystem _zipFileSystem = null;
		
		/**
		 * Ensure the target filesystem is closed.
		 */
		public void close() {
			if (_zipFileSystem != null) {
				try { _zipFileSystem.close(); } catch (Exception e) {}
			}
		}
		
		/**
		 * Constructor setting up the search.
		 * 
		 * @param pattern The global search pattern to utilize for the search.
		 * @throws IOException Thrown if the client-supplied pattern is not
		 * defined.
		 */
		public Finder(URI uri, String pattern) throws IOException {
			if ((pattern == null) || (pattern.isEmpty())) {
				throw new IOException("Usage error:  Search pattern not defined.");
			}
			HashMap<String, String> env = new HashMap<String, String>();
			env.put("create", "false");
			_zipFileSystem = FileSystems.newFileSystem(
	    			uri, 
	    			env, 
	    			Thread.currentThread().getContextClassLoader()); 
			_matcher = _zipFileSystem.getPathMatcher(
						"glob:" + pattern);
		}
		
        /** 
         * Compares the glob pattern against the file and/or directory name.
         * 
         * @param file The file to perform the comparison against.
         */
        public void find(Path file) {
            Path name = file.getFileName();
            if ((name != null) && (_matcher.matches(name))) {
                if (_matches == null) {
                	_matches = new ArrayList<Path>();
                }
            	_matches.add(file);
            }
        }
        
        /**
         * Accessor method for the results of the search.
         * 
         * @return Any results that were accumulated during the search 
         * (may be null). 
         */
        public List<Path> getResults() {
        	return _matches;
        }
        
        /**
         * Invoke the pattern matching method on each directory in the file 
         * tree.
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
            find(dir);
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * Invoke the pattern matching method on each file in the file tree.
         */
        @Override
        public FileVisitResult visitFile(
        		Path file,
                BasicFileAttributes attrs) {
            find(file);
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * If the file visit failed issue an informational message to System.err
         */
        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            System.err.println("WARN:  Find command failed visiting file.  " 
            		+ "Error message [ " 
            		+ exc.getMessage()
            		+ " ].");
            return FileVisitResult.CONTINUE;
        }
	}
}
