package org.ei.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.internal.handlers.GzipHandler;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.CARSConstants;
import org.ei.service.cars.CARSRequestProcessor;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.service.cars.rest.request.CARSRequest;

/**
 * This class will build a WINK-based Resource object based on inputs.
 * @author harovetm
 *
 */
public class RESTResourceBuilder {
    private final static Logger log4j = Logger.getLogger(RESTResourceBuilder.class);

    /**
     * Create a REST client that can be statically re-used
     */
    private static RestClient restClient = null;
    static {
        ClientConfig config = new ClientConfig();
        int serviceTimeout = CARSConfigVariables.getConstantAsInt(CARSConfigVariables.CARS_SERVICE_TIMEOUT);
        config.connectTimeout(serviceTimeout);
        config.acceptHeaderAutoSet(true);
        config.followRedirects(true);
        config.readTimeout(serviceTimeout);
        config.handlers(new GzipHandler());
        restClient = new RestClient(config);
    }

    /**
     * Build a WINK client resource from inputs based on CARS endpoint
     *
     * @param carsrequest
     * @param request
     * @param carsRequestProcessor
     * @return
     * @throws ServiceException
     */
    public static Resource build(CARSRequest carsrequest, HttpServletRequest request, CARSRequestProcessor carsRequestProcessor) throws ServiceException {

        Resource resource = null;
        StringBuffer  resourceLog = new StringBuffer();
        try {

        	
        	
            //
            // Build the resource from CARS endpoint + URI
            //
            resource = restClient.resource(
                BaseServiceConstants.getServicesBaseURL() +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                carsrequest.getRequestURI());

            resourceLog.append("Cars Base Url : "+ BaseServiceConstants.getServicesBaseURL() +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                carsrequest.getRequestURI()+"\n");
            
            
            
            //
            // Add X_ELS_AUTHENTICATION header
            //
            resource.header(
                    CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
                    CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE)
                    );

            resourceLog.append("Header 1 : key="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION)+" , value="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE)+"\n");
            
            resource.header(CARSStringConstants.ACCEPT_ENCODING.value(), CARSConstants.GZIP);

            resourceLog.append("Header 2 : key="+CARSStringConstants.ACCEPT_ENCODING.value()+" , value="+CARSConstants.GZIP+"\n");
            
            //
            // Add session affinity cookie if present
            //
            String sessionAffinity = carsrequest.getSessionAffinity();
            if (StringUtils.isNotBlank(sessionAffinity)) {
                resource.cookie(CARSStringConstants.SESSION_AFFINITY_KEY.value() + CARSStringConstants.EQUAL.value() + sessionAffinity + CARSStringConstants.SEMICOLON.value());
                resource.header(CARSStringConstants.SESSION_AFFINITY_KEY.value(), sessionAffinity);
            }

            resourceLog.append("Header 3 : key="+CARSStringConstants.SESSION_AFFINITY_KEY.value()+" , value="+sessionAffinity+"\n");
            resourceLog.append("Cookie : "+CARSStringConstants.SESSION_AFFINITY_KEY.value() + CARSStringConstants.EQUAL.value() + sessionAffinity + CARSStringConstants.SEMICOLON.value()+"\n");
            
            //
            // Process request params if present
            //
            if (null != carsrequest.getRestRequestParams()) {
                for (Entry<RESTRequestParameters, Object> pair : carsrequest.getRestRequestParams().entrySet()) {
                    // Ensure pair and key are valid
                    if (null == pair || null == pair.getKey() || null == pair.getKey().getReqParam()) {
                        log4j.info("Empty request parameter found!");
                        continue;
                    }

                    // If special requset parameter, convert from MultiValuedMap
                    if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
                        resource.queryParams(convertToMultiValuedMap(pair));
                        resourceLog.append("requetParameters :"+convertToMultiValuedMap(pair)+"\n");
                    }
                    // Otherwise, just process the key/value pair
                    else {
                        if (null == pair.getValue()) {
                            log4j.info("Empty request parameter found!");
                            continue;
                        }
                        String value = null;
                        try {
                            value = URLEncoder.encode(pair.getValue().toString(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            log4j.error("Error occured while encoding" + pair.getKey().getReqParam());
                        }
                        resource.queryParam(pair.getKey().getReqParam(), value);
                        resourceLog.append("requetParameter :"+pair.getKey().getReqParam()+"="+ value+"\n");
                    }
                }
            }
            
            carsRequestProcessor.setErrorLog(resourceLog.toString());
            resourceLog = null;
            
        } catch (Exception e) {
        	StringBuffer errorlog = new StringBuffer();
        	errorlog.append("\n*******************************************CARS ERROR LOG START*******************************************************\n");
        	errorlog.append("Exception while building the resource object : "+ e.getClass() + ", message: " + e.getMessage()+"\n");
        	errorlog.append("Request details :"+resourceLog.toString()+"\n");
        	resourceLog = null;
        	errorlog.append("*******************************************CARS ERROR LOG END*********************************************************");
        	try{
        		Logger carsErrorLog4j = Logger.getLogger("CarsErrorLogger");
        		carsErrorLog4j.info(errorlog.toString());
        	}catch(Exception exp){
        		log4j.warn("Could not log using cars error logger ! , prinitng exception via root logger.");
        		log4j.warn(errorlog.toString());
        	}
        	errorlog = null;
        	log4j.error("Unable to build Resource!  Exception: " + e.getClass() + ", message: " + e.getMessage());
            throw new ServiceException(SystemErrorCodes.REST_RESOURCE_ERROR, e);
        }
        return resource;
    }


    /**
     * Build a PostMethod object.  This can be built from a Multipart request
     *
     * @param carsrequest
     * @param httprequest
     * @param carsRequestProcessor
     * @return
     */
    public static PostMethod buildPostMethod(CARSRequest carsrequest, HttpServletRequest httprequest, CARSRequestProcessor carsRequestProcessor) {

        PostMethod postMethod = null;

        Part[] parts = fetchParts(carsrequest, httprequest);

        StringBuffer  resourceLog = new StringBuffer();
        resourceLog.append("To build post method, isPostAsGet : "+carsrequest.isPostAsGet()+"\n");
        if (carsrequest.isPostAsGet()) {
            String querystring = null;
            try {
                querystring = buildQueryString(carsrequest);
            } catch (UnsupportedEncodingException e) {
                log4j.error("Unable to build query string!");
            }
            postMethod = new PostMethod(
                BaseServiceConstants.getServicesBaseURL() +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                carsrequest.getRequestURI() + querystring);
            resourceLog.append("Cars Url for post method : "+
                    BaseServiceConstants.getServicesBaseURL() +
                    CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                    carsrequest.getRequestURI() + querystring+"\n");
        } else {
            postMethod = new PostMethod(
                BaseServiceConstants.getServicesBaseURL() +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                carsrequest.getRequestURI());
            resourceLog.append("Cars Url for post method : "+
                    BaseServiceConstants.getServicesBaseURL() +
                    CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                    carsrequest.getRequestURI()+"\n");
            postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
        }

        postMethod.setRequestHeader(
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));

        resourceLog.append("Header 1 : key="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION)+", value="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE)+"\n");
        carsRequestProcessor.setErrorLog(resourceLog.toString());
        resourceLog = null;
        return postMethod;
    }

    /**
     * Build a PutMethod object.  This can be built from a Multipart request
     *
     * @param carsrequest
     * @param httprequest
     * @param carsRequestProcessor
     * @return
     */
    public static PutMethod buildPutMethod(CARSRequest carsrequest, HttpServletRequest httprequest, CARSRequestProcessor carsRequestProcessor) { 
    	StringBuffer  resourceLog = new StringBuffer();
        
        PutMethod putMethod = new PutMethod(
            BaseServiceConstants.getServicesBaseURL() +
            CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
            carsrequest.getRequestURI());
        resourceLog.append("Cars Url for put method :"+ BaseServiceConstants.getServicesBaseURL() +
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT) +
                carsrequest.getRequestURI()+"\n");
        putMethod.setRequestHeader(
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
                CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));

        resourceLog.append("Header 1 : key="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION)+", value="+CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE)+"\n");
        Part[] parts = fetchParts(carsrequest, httprequest);
        putMethod.setRequestEntity(new MultipartRequestEntity(parts, putMethod.getParams()));
        carsRequestProcessor.setErrorLog(resourceLog.toString());
        resourceLog = null;
        return putMethod;
    }

    /**
     * Build a query string from the current rest request params
     * @param carsrequest
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String buildQueryString(CARSRequest carsrequest) throws UnsupportedEncodingException {
        StringBuffer qs = new StringBuffer();

        if (null != carsrequest.getRestRequestParams()) {

            for (Entry<RESTRequestParameters, Object> pair : carsrequest.getRestRequestParams().entrySet()) {

                if (null != pair && null != pair.getKey()) {

                    if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
                        for (Entry<Object, Object> paramValue :((Map<Object, Object>) pair.getValue()).entrySet()) {
                            String[] arrayValue = (String[]) paramValue.getValue();
                            for (String value : arrayValue) {
                                qs.append((qs.length() == 0 ? "?" : "&") + paramValue.getKey().toString() + "=" + URLEncoder.encode(value,"UTF-8"));
                            }
                        }
                    } else if(pair.getValue()!=null){
                        qs.append((qs.length() == 0 ? "?" : "&") + pair.getKey().getReqParam() + "=" + URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
                    }
                }
            }
        }
        return qs.toString();
    }

    /**
     * Build Part array from request objects
     *
     * @param carsrequest
     * @param httprequest
     * @return
     */
    public static Part[] fetchParts(CARSRequest carsrequest, HttpServletRequest httprequest) {
        Part[] parts = null;
        if (null != carsrequest.getRestRequestParams()) {

            List<Part> partsList = new ArrayList<Part>();
            for (Entry<RESTRequestParameters, Object> pair : carsrequest.getRestRequestParams().entrySet()) {

                if (null != pair && null != pair.getKey()) {

                    if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
                        for (Entry<Object, Object> paramValue :((Map<Object, Object>) pair.getValue()).entrySet()) {
                            String[] arrayValue = (String[]) paramValue.getValue();
                            for (String value : arrayValue) {
                                partsList.add(new StringPart(paramValue.getKey().toString(), value));
                            }
                        }
                    }

                    if(pair.getValue()!=null){
                        partsList.add(new StringPart(pair.getKey().getReqParam(), pair.getValue().toString()));
                    }
                }
            }

            //add all the file upload content  from the request
            if (httprequest.getAttribute(CARSStringConstants.FILE_PARAMS.value()) != null) {
                Part part;
                Map<String, Object> fileParamsMap = (Map<String, Object>) httprequest.getAttribute(CARSStringConstants.FILE_PARAMS.value());
                for (Entry<String, Object> params : fileParamsMap.entrySet()) {
                    String key = params.getKey();
                    if(key.equals(CARSStringConstants.PROFILE_IMAGE.value())){
                        key=CARSStringConstants.PROFILE_PHOTO.value();
                    }
                    if (params.getValue() != null) {
                        part = new FilePart(key, new ByteArrayPartSource(key, (byte[]) params.getValue()));
                        partsList.add(part);
                    }
                }
            }
            parts = partsList.toArray(new Part[partsList.size()]);
        }
        return parts;
    }

    /**
     * Convert MultivaluedMap object to String
     *
     * @param pair
     * @return
     */
    private static MultivaluedMap<String, String> convertToMultiValuedMap(Entry<RESTRequestParameters, Object> pair) {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl<String, String>();

        if (null != pair.getValue()) {
            for (Entry<Object, Object> paramValue : ((Map<Object, Object>) pair.getValue()).entrySet()) {
                if (null != paramValue && null != paramValue.getValue()) {
                    String[] arrayValue = (String[]) paramValue.getValue();
                    if (null != arrayValue && arrayValue.length > 0) {
                        for (String value : arrayValue) {
                            if (null != paramValue.getKey()) {
                                try {
                                		value = URLEncoder.encode(value, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    log4j.error("Error occured while encoding"
                                            + paramValue.getKey().toString());
                                }
                                map.add(paramValue.getKey().toString(), value);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
}
