package mil.nga.rod;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.exceptions.InvalidQueryRequestException;
import mil.nga.rod.ejb.EJBClientUtilities;
import mil.nga.rod.messages.ProductQueryRequest;
import mil.nga.rod.messages.ProductQueryResponse;
import mil.nga.rod.model.QueryRequest;
import mil.nga.util.FileUtils;

@Path("")
public class RoD 
        extends RoDEJBClientUtilities 
        implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -676113787700992614L;

	/**
	 * Set up the Log4j system for use throughout the class
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(RoD.class);
	
	/**
	 * The name of the application
	 */
	public static final String APPLICATION_NAME = "ReplicationOnDemand";
	
    /**
     * Simple internal method used to convert the date in String format
     * to a java.sql.Date object for use in querying the target data 
     * store.
     * 
     * @param date Client-supplied date in String format.
     * @return populated <code>java.sql.Date</code> object
     */
    private java.sql.Date getDateSQL(String date) {
    	
    	java.sql.Date sqlDate = null;
    	
    	try {
	    	java.util.Date utilDate = (new SimpleDateFormat(
	    			ProductQueryRequest.INPUT_DATE_FORMAT_STRING))
	    			.parse(date);
	    	sqlDate = new java.sql.Date(utilDate.getTime());
	    }
    	catch (ParseException pe) {
    		LOGGER.error("Unexpected ParseException raised while attempting "
    				+ "to create a java.sql.Date from the input String [ "
    				+ date
    				+ " ].  This exception should never be raised as the "
    				+ "date should have already been validated.");
    	}
    	return sqlDate;
    }
    
	/** 
	 * Private method used to extract the filter if the caller supplied
	 * one.  
	 * 
	 * @param request The incoming request object.
	 * @return The filter, if supplied, or the string "unavailable".
	 */
	private String getFilter(ProductQueryRequest request) { 
		String filter = "unavailable";
		if (request != null) {
			if ((request.getAORCode() != null) && 
					(!request.getAORCode().isEmpty())) {
				filter = request.getAORCode();
			}
			else if ((request.getCountryName() != null) && 
					(!request.getCountryName().isEmpty())) {
				filter = request.getCountryName();
			}
			else if ((request.getProductType() != null) && 
					(!request.getProductType().isEmpty())) {
				filter = request.getProductType();
			}
		}
		return filter;
	}
	
	/**
	 * This method will gather the data necessary to log a query request
	 * for metrics tracking purposes. This does not raise any exceptions 
	 * and makes an asynchronous call as to not impact the query response
	 * times.
	 * 
	 * @param elapsedTime Amount of time taken servicing the request. 
	 * @param request The incoming request object.
	 * @param response The outgoing response.
	 * @param requestHeaders The incoming request headers.
	 */
	private void logQueryRequest(
			long                  elapsedTime,
			ProductQueryRequest   request,
			ProductQueryResponse  response,
			MultivaluedMap<String, String> requestHeaders) {
		
		// Collect the required data.
		String username      = getUser(requestHeaders);
		String clientIP      = getSourceIP(requestHeaders);
		String host          = FileUtils.getHostName();
		String filter        = getFilter(request);
		Date   dateRequested = getDateSQL(request.getLoadDate());

		// Build the POJO containing the query data.
		QueryRequest queryRequest = new QueryRequest.QueryRequestBuilder()
				.dateRequested(new Date(System.currentTimeMillis()))
				.elapsedTime(elapsedTime)
				.filter(filter)
				.hostName(host)
				.loadDateRequested(dateRequested)
				.numResults(response.getISOFiles().size())
				.source(clientIP)
				.username(username)
				.build();
			
		// Persist the data (asynchronous call)
		if (getMetricsService() != null) {
			getMetricsService().logQueryRequest(queryRequest);
		}
		else {
			LOGGER.warn("Unable to obtain a reference to the MetricsService "
					+ "EJB.  Download request will not be logged. ");
		}
	}
	
	/**
	 * Simple method used to determine whether or not the application 
	 * is responding to requests.
	 */
	@GET
	@Path("/isAlive")
	public Response isAlive(@Context HttpHeaders headers) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Application [ ");
		sb.append(APPLICATION_NAME);
		sb.append(" ] on host [ ");
		sb.append(FileUtils.getHostName());
		sb.append(" ] running in JVM [ ");
		sb.append(EJBClientUtilities.getInstance().getServerName());
		sb.append(" ].");
		return Response.status(Status.OK).entity(sb.toString()).build();
	}
	
	/**
	 * Method allowing clients to initiate queries of ISO holdings.  The input
	 * to this method is a JSON message containing the query parameters.  The 
	 * output is a JSON array containing the list of products that match the
	 * input query parameters.  This method is not meant to provide general 
	 * query capabilities.  It is meant to aid clients in determining what 
	 * products may be new, or changed since a client-supplied date.  The only 
	 * required parameter is the target load date, other parameters can be 
	 * supplied to narrow down the list of products returned.  
	 * 
	 * @param headers The HTTP headers associated with the incoming request.
	 * @param request The client-initiated product query.
	 * @return List of ISO files (in JSON format) that match the input query 
	 * parameters. 
	 */
	@POST
	@Path("/isoQuery")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response productQuery(
			@Context HttpHeaders headers,
			ProductQueryRequest request) {
		
		long                 start    = System.currentTimeMillis();
		ProductQueryResponse response = new ProductQueryResponse();
		
		if (request != null) {	
	    	if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Product query request received.  Details [ " 
						+ request.toString()
						+ " ] ms.");
			}
			
			if (getProductQueryService() != null) { 
				
				try {
					if (getProductQueryService()
							.validateQueryMessage(request)) {
						response = getProductQueryService()
								.getProductQueryResponse(request);
					}
				}
				catch (InvalidQueryRequestException iqre) {
					LOGGER.error("InvalidQueryRequestException raised while "
							+ "validating the incoming query request.  "
							+ "Reason [ "
							+ iqre.getMessage()
							+ " ].");
					return Response.status(Status.BAD_REQUEST)
							.entity(iqre.getMessage())
							.build();
				}
				
			}
			else {
				LOGGER.error("Unable to obtain a reference to the "
						+ "ProductQueryService EJB.");
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity("Application error encountered.")
						.build();
			}
		}		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Product query request processed in [ " 
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
		
    	logQueryRequest(
    			(System.currentTimeMillis() - start), 
    			request, 
    			response,
    			headers.getRequestHeaders());
    	
		return Response.status(Status.OK)
				.entity(response)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
}
