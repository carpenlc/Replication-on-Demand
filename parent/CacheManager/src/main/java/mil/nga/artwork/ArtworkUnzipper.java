package mil.nga.artwork;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.util.FileUtils;
import mil.nga.util.ZipFileFinder;

/**
 * This class leverages the Java NIO2 API for reading and extracting the 
 * artwork from a target ZIP file.  This code is written assuming that 
 * the artwork is in PDF format and only one PDF exists inside each of the
 * artwork ZIP files.
 *  
 * @author L. Craig Carpenter
 */
public class ArtworkUnzipper {

    /**
     * Set up the Log4j system for use throughout the class
     */     
    static final Logger LOG = LoggerFactory.getLogger(ArtworkUnzipper.class);
    
    /**
     * Default constructor.
     */
    public ArtworkUnzipper() { }

	/**
	 * Construct a URI for the output artwork file.
	 * @param path The output location for the target file.
	 * @return The URI associated with output location of the target file.
	 */
    private URI getFileURI(String path) {
    	URI uri = null;
    	if ((path != null) && (!path.isEmpty())) {
			URI temp = URI.create(path);
			if ((temp.getScheme() == null) || (temp.getScheme().isEmpty())) {
				try {
					uri = new URI(
							"file",
							temp.getAuthority(),
							temp.getPath(),
							temp.getFragment(),
							temp.getQuery());
				}
				catch (URISyntaxException use) {}
			}
			else {
				uri = temp;
			}
		}
		else {
			LOG.error("The input file path was null or empty.  Output URI "
					+ "will also be null.");
		}
		return uri;
    }
    
	/**
	 * Construct a URI for the target zip file.
	 * @param pathToZip The on-disk path to the target zip file.
	 * @return The URI associated with the target zip file.
	 */
	private URI getURI(String pathToZip) {
		URI zipURI = null;
		if ((pathToZip != null) && (!pathToZip.isEmpty())) {
			// Don't know if it's a bug in the JDK or what, but the 
			// ZipFileSystemProvider requires the existence of "!/"
			// in the URI or you get an IllegalArgmentException when
			// attempting to get a Path from a URI.  The following 
			// line of code works around that issue.
			pathToZip = pathToZip + "!/";
			URI temp = URI.create(pathToZip);
			if ((temp.getScheme() == null) || (temp.getScheme().isEmpty())) {
				try {
					zipURI = new URI(
							"jar:file",
							temp.getAuthority(),
							temp.getPath(),
							temp.getFragment(),
							temp.getQuery());
				}
				catch (URISyntaxException use) {}
			}
		}
		else {
			LOG.error("The input file path was null or empty.  Output URI "
					+ "will also be null.");
		}
		return zipURI;
	}
	
	/**
	 * Construct the <code>Path</code> object for the target output file 
	 * from a client supplied target output path, and the name of the PDF 
	 * file within the 
	 * @param outputPath The location in which the artwork PDF will be stored.
	 * @param zipFile The target ZIP file to read.
	 * @return The Path object representing the output file.
	 */
	private Path getOutputPath(String outputPath, String zipFile) {
		Path p = null;
		StringBuilder sb = new StringBuilder();
		if ((outputPath != null) && (!outputPath.isEmpty())) {
			if ((zipFile != null) && (!zipFile.isEmpty())) {
				sb.append(outputPath);
				if (!(sb.toString().endsWith(File.separator))) {
					sb.append(File.separator);
				}
				sb.append(FileUtils.getFilenameFromPath(zipFile));
				p = Paths.get(getFileURI(sb.toString()));
			}
		}
		return p;
	}
	
	/**
	 * Entry point for the algorithms that will extract the artwork PDF from 
	 * the file identified by the input path.
	 * 
	 * @param pathToZip The path to the target ZIP file.
	 * @return The Path object representing the output artwork PDF.
	 */
	public Path unzipArtwork(String pathToZip, String outputPath) {
		Path target = null;
		URI  uri    = getURI(pathToZip);
		if (uri != null) {
			try {
				// LOG.info("Target ZIP file => [ " + uri.toString() + " ].");
				List<Path> targetArtworkFile = ZipFileFinder.find(uri, "*.pdf");
				if ((targetArtworkFile != null) && (targetArtworkFile.size() > 0)) {
					// Note to self.  The Zip filesystem was loaded when we 
					// searched it for a PDF.  
					//List<FileSystemProvider> providers = FileSystemProvider.installedProviders();	
					//for (FileSystemProvider provider : providers) {
					//	System.out.println(provider.getScheme());
					//}
					Path fileToExtract = targetArtworkFile.get(0);
					
					if (targetArtworkFile.size() > 1) {
						LOG.warn("More than one PDF file exists in ZIP file [ "
								+ uri.toString()
								+ " ].  Using the first one found [ "
								+ fileToExtract.toString()
								+ " ].");
					}
					
					target = getOutputPath(
							outputPath, 
							fileToExtract.toString());
					
					if (target != null) {
						if (!Files.exists(target)) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("Extracting [ "
										+ fileToExtract.toString()
										+ " ] from ZIP file [ "
										+ uri.toString()
										+ " ] to location [ "
										+ target.toString()
										+ " ].");
							}
							Files.copy(fileToExtract, target);
						}
						else {
							LOG.info("Target output file [ "
									+ target.toString()
									+ " ] already exists.");
						}
					}
					else {
						LOG.error("Unable to construct a Path to the target "
								+ "output file.  Artwork will not be "
								+ "extracted.");
					}
				}
			}
			catch (IOException ioe) {
				LOG.error("An unexpected IOException was raised while attempting "
						+ "to open the target ZIP file [ "
						+ uri.toString()
						+ " ].  Error message => [ "
						+ ioe.getMessage()
						+ " ].");
			}
			catch (FileSystemNotFoundException fsnf) {
				LOG.error("Unable to access the source ZIP file. "
						+ "Error message => [ "
						+ fsnf.getMessage()
						+ " ].");
			}
		}
		else {
			LOG.error("Unable to construct a URI for the target ZIP file [ "
					+ pathToZip
					+ " ].");
		}
		return target;
	}
	
	public static void main(String[] args) {
		new ArtworkUnzipper().unzipArtwork("/tmp/cb01no445j1_artwork.zip","/tmp");
	}
}
