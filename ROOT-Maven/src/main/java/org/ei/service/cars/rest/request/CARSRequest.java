package org.ei.service.cars.rest.request;



import java.util.Map;

import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.HttpMethod;
import org.ei.service.cars.RESTRequestParameters;



/**
 * This interface defines all the methods needed for building the request url 
 * to make RESTFul service call
 * 
 * @author naikn1
 * @version 1.0
 *
 */
public interface CARSRequest {
    /**
     * Some CARS requests need to send a POST but put the parameters on the URL like
     * a GET (bug).  This flag indicates when this should be done.
     * @return
     */
    public boolean isPostAsGet();
    
    /**
     * This method is used to set http method value
     *
     * @param httpMethod
     */
    public HttpMethod getHTTPMethod();
    
    /**
     * 
     * This method is used to set request url
     *
     * @param reqURI
     */
    public String getRequestURI();

    /**
     * Determines if request is multi-part
     * @return
     */
    public boolean isMultiPartReq();

    /**
     * This method validates the incoming request object
     * returns true if the request object is null or empty
     * 
     * @return boolean - true/false
     */
    public boolean isEmptyRequest();    

	/**
	 * Stores the request type
	 * @return
	 */
	public CARSRequestType getRequestType();
	
	
	public void setRequestType(CARSRequestType requestType);
	
	
	
	/**
	 * This method will check for if sso authentication call has to be done or not 
	 * to avoid unnecessary authentication calls when not required
	 * 
	 * @return true/false
	 */
	public boolean isSSOAuthenticationReq();
	
	/**
	 * Method to check if the site identifier is available in request object
	 * in order to handle error in web
	 * 
	 * @return true when site identifier is available
	 */
	public boolean isSiteIdentifierPresent();
	
	/**
	 * Method to check if the platform code is available in request object
	 * in order to handle error in web
	 * 
	 * @return true when platform code is available
	 */
	public boolean isPlatformCodePresent();

	/**
	 * Method to fetch the session affinity key 
	 * 
	 * @return String - session affinity key
	 */
	public String getSessionAffinity();

    /**
     * Method to get the request parameters
     * 
     * @return Map - the rest params
     */
    public Map<RESTRequestParameters, Object> getRestRequestParams();
    
    /**
     * This method is used to add all request parameters to request object
     *
     * @param paramName - the param name to be used for REST call
     * @param paramValue - the param values to be passed
     */
    public void addRestRequestParameter(RESTRequestParameters paramName, Object paramValue);


}

