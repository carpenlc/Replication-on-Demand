package mil.nga.rod.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Simple POJO used to hold information associated with an individual download 
 * request.  This POJO is for tracking metrics only.  Each individual ISO file
 * that is downloaded is logged. 
 * 
 * @author L. Craig Carpenter
 */
public class DownloadRequest implements Serializable {

	/* Sample command for creating the backing data table (Oracle):
	 
		create table ROD_DOWNLOAD_REQUESTS (
		    REQUEST_ID     VARCHAR2(20) not null, 
		    PROD_TYPE      VARCHAR2(10),
		    AOR_CODE       VARCHAR2(10),
		    COUNTRY_NAME   NVARCHAR2(50),
		    NRN            VARCHAR2(20),
		    NSN            VARCHAR2(15),
		    DATE_REQUESTED TIMESTAMP,
		    UNIX_PATH      VARCHAR2(2846),      
		    FILE_SIZE      NUMBER,
		    USERNAME       VARCHAR2(240),
		    SOURCE         VARCHAR2(240),
	        HOST_NAME      VARCHAR2(240)
	        CONSTRAINT REQUEST_ID_PK PRIMARY KEY (REQUEST_ID)
	    );
    
	*/
	
	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 8221225083154879713L;

	/** 
	 * Format associated with request dates.
	 */
	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd hh:mm:ss";
			
	/**
	 * DateFormat object used to convert the String based last-update date 
	 * retrieved from the target data store.
	 */
	private static final SimpleDateFormat formatter = 
				new SimpleDateFormat(DATE_FORMAT_STRING);
	
	// Private internal members.
	private final String aorCode;
	private final String countryName;
	private final Date   dateRequested;
	private final long   fileSize;
	private final String hostName;
	private final String nrn;
	private final String nsn;
	private final String path;
	private final String productType;
	private final String requestId;
	private final String source;
	private final String username;

	/**
	 * Constructor used to set all of the required internal members.
	 * 
	 * @param builder Populated builder object.
	 */
	private DownloadRequest(DownloadRequestBuilder builder) {
		this.aorCode       = builder.aorCode;
		this.countryName   = builder.countryName;
		this.dateRequested = builder.dateRequested;
		this.hostName      = builder.hostName;
		this.nrn           = builder.nrn;
		this.nsn           = builder.nsn;
		this.path          = builder.path;
		this.productType   = builder.productType;
		this.requestId     = builder.requestId;
		this.fileSize      = builder.fileSize;
		this.source        = builder.source;
		this.username      = builder.username;
	}
	
	/**
	 * Getter method for the AOR code attribute.
	 * @return The AOR code attribute.
	 */
	public String getAorCode() {
		return aorCode;
	}	
	
	/**
	 * Getter method for the country name.
	 * @return The country name.
	 */
	public String getCountryName() {
		return countryName;
	}
	
	/**
	 * Getter method for the load date.
	 * @return The load date.
	 */
	public Date getDateRequested() {
		return dateRequested;
	}
	
	/**
	 * Getter method for the load date.
	 * @return The load date.
	 */
	public String getDateRequestedString() {
		return formatter.format(dateRequested);
	}
	
	/**
	 * Getter method for the size of the ISO file requested.
	 * @return The size of the file requested.
	 */
	public long getFileSize() {
		return fileSize;
	}
	
	/**
	 * Getter method for the host name processing the request.
	 * @return The host name processing the request.
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * Getter method for the NRN number.
	 * @return The NRN number.
	 */
	public String getNRN() {
		return nrn;
	}
	
	/**
	 * Getter method for the NSN number.
	 * @return The NSN number.
	 */
	public String getNSN() {
		return nsn;
	}
	
	/**
	 * Getter method for the on-disk path of the ISO image.
	 * @return The on-disk path of the ISO image.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Getter method for the product type.
	 * @return The product type.
	 */
	public String getProductType() {
		return productType;
	}
	
	/**
	 * Getter method for the request ID.
	 * @return The request ID.
	 */
	public String getRequestId() {
		return requestId;
	}
	
	/**
	 * Getter method for the IP or host name of the source requesting the
	 * target ISO file.
	 * @return The IP or host name of the source requesting the target ISO
	 * file.
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Getter method for the user name who requested the target file.
	 * @return The user name who requested the target file.
	 */
	public String getUserName() {
		return username;
	}
	
	/**
	 * Convert to human-readable format.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ISO Download Requested: ");
		sb.append("Request ID => [ ");
		sb.append(getRequestId());
		sb.append(" ], Date requested => [ ");
		sb.append(formatter.format(getDateRequested()));
		sb.append(" ], AOR code => [ ");
		sb.append(getAorCode());
		sb.append(" ], Country => [ ");
		sb.append(getCountryName());
		sb.append(" ], File Size => [ ");
		sb.append(getFileSize());
		sb.append(" ], Host name => [ ");
		sb.append(getHostName());
		sb.append(" ], User name => [ ");
		sb.append(getUserName());
		sb.append(" ], Source IP => [ ");
		sb.append(getSource());
		sb.append(" ].");
		return sb.toString();
	}
	
	/**
	 * Internal static class implementing the Builder creation pattern for 
	 * new DownloadRequest objects.  
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class DownloadRequestBuilder {
		
		private String aorCode;
		private String countryName;
		private Date   dateRequested;
		private String hostName;
		private String nrn;
		private String nsn;
		private String productType;
		private String requestId;
		private String username;
		private String path;
		private long   fileSize;
		private String source;
		
		/**
		 * Method used to actually construct the DownloadRequest object.
		 * @return A constructed and validated DownloadRequest object.
		 */
		public DownloadRequest build() throws IllegalStateException {
			DownloadRequest object = new DownloadRequest(this);
			validateDownloadRequestObject(object);
			return object;
		}
		
		/**
		 * Setter method for the AOR_CODE attribute.
		 * @param value The AOR_CODE attribute.
		 */
		public DownloadRequestBuilder aorCode(String value) {
			if (value != null) {
				aorCode = value.trim().toUpperCase();
			}
			return this;
		}
		
		/**
		 * Setter method for the country name attribute.
		 * @param value The country name attribute.
		 */
		public DownloadRequestBuilder countryName(String value) {
			if (value != null) {
				countryName = value.trim();
			}
			return this;
		}
		
		/**
		 * Setter method for the file size attribute.
		 * @param value The file size attribute.
		 */
		public DownloadRequestBuilder fileSize(long value) {
			fileSize = value;
			return this;
		}
		
		/**
		 * Setter method for the host name processing the request.
		 * @param value The host name handling the request.
		 */
		public DownloadRequestBuilder hostName(String value) {
			if (value != null) {
				hostName = value.trim();
			}
			return this;
		}
		
		/**
		 * Setter method for the NRN attribute.
		 * @param value The NRN attribute.
		 */
		public DownloadRequestBuilder nrn(String value) {
			if (value != null) {
				nrn = value.trim().toUpperCase();
			}
			return this;
		}
		
		/**
		 * Setter method for the NSN attribute.
		 * @param value The NSN attribute.
		 */
		public DownloadRequestBuilder nsn(String value) {
			if (value != null) {
				nsn = value.trim().toUpperCase();
			}
			return this;
		}
		
		/**
		 * Setter method for the UNIX_PATH attribute.
		 * @param value The UNIX_PATH attribute.
		 */
		public DownloadRequestBuilder path(String value) {
			if (value != null) {
				path = value.trim();
			}
			return this;
		}
		
		/**
		 * Setter method for the PROD_TYPE attribute.
		 * @param value The PROD_TYPE attribute.
		 */
		public DownloadRequestBuilder productType(String value) {
			if (value != null) {
				productType = value.trim().toUpperCase();
			}
			return this;
		}
		
		/**
		 * Setter method for the request date attribute.
		 * @param value The request date.
		 */
		public DownloadRequestBuilder requestDate(Date value) {
			if (value == null) {
				dateRequested = new Date(0);
			}
			else {
				dateRequested = value;
			}
			return this;
		}
		
		/**
		 * Setter method for the request date attribute.
		 * @param value The request date.
		 */
		public DownloadRequestBuilder requestId(String value) {
			if (value != null) {
				requestId = value.trim();
			}
			return this;
		}
		
		/**
		 * Setter method for the IP or host name of the source requester.
		 * @param value The IP or host name of the source requester.
		 */
		public DownloadRequestBuilder source(String value) {
			if (value != null) {
				source = value.trim();
			}
			return this;
		}
		
		/**
		 * Setter method for the user requesting the ISO file.
		 * @param value The user requesting the ISO file.
		 */
		public DownloadRequestBuilder username(String value) {
			if (value != null) {
				username = value.trim();
			}
			return this;
		}
		
		/**
		 * Validate internal member variables.
		 * 
		 * @param object The DownloadRequest object to validate.
		 * @throws IllegalStateException Thrown if any of the required fields 
		 * are not populated.
		 */
		private void validateDownloadRequestObject(DownloadRequest object) 
				throws IllegalStateException {
			if (object != null) {
				if ((object.getRequestId() == null) || 
						(object.getRequestId().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for "
							+ "the request ID was null.  Note: Request ID "
							+ "is the primary key.");
				}
				
				if ((object.getAorCode()== null) || 
						(object.getAorCode().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for "
							+ "AOR code was null.");
				}
				
				if ((object.getCountryName()== null) || 
						(object.getCountryName().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for "
							+ "country name was null.");
				}
				
				if ((object.getNSN() == null) || 
						(object.getNSN().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for NSN "
							+ "was null.  Complete DownloadRequest object [ "
							+ object.toString()
							+ " ].");
				}
				
				if ((object.getNRN() == null) || 
						(object.getNRN().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for NRN "
							+ "was null.   Complete DownloadRequest object [ "
							+ object.toString()
							+ " ].");
				}
				
				if ((object.getProductType() == null) || 
						(object.getProductType().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for "
							+ "productType was null.");
				}
				
				if ((object.getPath() == null) || 
						(object.getPath().isEmpty())) {
					throw new IllegalStateException("Attempted to build "
							+ "DownloadRequest object but the value for target "
							+ "ISO file was null.");
				}
			}
			else {
				throw new IllegalStateException("Construction of  "
						+ "QueryRequest object failed.  Object was null.");
			}
		}	
	}
}
