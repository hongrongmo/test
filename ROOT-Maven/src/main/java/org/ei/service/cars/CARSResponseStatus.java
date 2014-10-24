package org.ei.service.cars;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.service.cars.rest.util.XMLUtil;

/**
 * Enum class to hold the various CARS Response Status
 * 
 * @author naikn1
 * @version 1.0
 * 
 */
public class CARSResponseStatus {
    private static final Logger log4j = Logger.getLogger(CARSResponseStatus.class);
    
    public enum STATUS_CODE {
        /**
         * Successful execution
         */
        OK,
        
        /**
         * The client has sent information that doesn't allow the request to be processed.
         */
        VALIDATION_ERROR,
        
        /**
         * The authenticateUser request was processed, but an error was detected while processing the request.
         */
        AUTHENTICATION_ERROR,
        
        /**
         * The get profile request was processed, but an error was detected while processing the request.
         */
        GET_PROFILE_ERROR,
        
        /**
         * The register user or modify profile request was processed, but an error was detected while processing the
         * request.
         */
        PROFILE_ERROR,
        
        /**
         * NO_ACTIVE_PATH/NO_ACTUAL_PATH for registration
         */
        REGISTRATION_ERROR,
        
        /**
         * The get password reminder request was processed, but an error was detected while processing the request.
         */
        PASSWORD_REMINDER_ERROR,
        
        /**
         * The change password request was processed, but an error was detected while processing the request.
         */
        CHANGE_PASSWORD_ERROR,
        
        /**
         * No result set not found.
         */
        NO_RESULTS_FOUND,
        
        /**
         * Invalid auth token
         */
        INVALID_AUTH_TOKEN,
        
        /**
         * Unknown error has occurred
         */
        UNKNOWN_ERROR;
    }
    
    public enum ERROR_TYPE {
        /**
         * No active paths for user
         */
        NO_ACTIVE_PATHS,
        /**
         * Credentials not found or don't match
         */
        LOGIN_NO_MATCH,
        /**
         * Credentials don't match
         */
        CRED_NO_MATCH,
        /**
         * Invalid request
         */
        INVALID,
        /**
         * TICURL institution not found.
         */
        TICURL_INSTITUTION_NOT_FOUND,
        /**
         * SHIB institution not found.
         */
        SHIB_INSTITUTION_NOT_FOUND, 
        /**
         * Unknown error has occurred
         */
        UNKNOWN_ERROR;
        
    }
    
    private STATUS_CODE statuscode;
    private ERROR_TYPE errortype;
    private String errortext;
    private String errorfieldname;
    
    /**
     * Constructor from 2-part CARS response
     * 
     * @param mime1
     * @param mime2
     * @throws ServiceException 
     * @throws CARSResponseParseException
     */
    public CARSResponseStatus(String mime1) throws ServiceException {
        String status = XMLUtil.fetchXPathValAsString(mime1, XPathEnum.STATUS_CODE.value());
        this.statuscode = STATUS_CODE.OK;
        if (!GenericValidator.isBlankOrNull(status)) {
            try {
                statuscode = STATUS_CODE.valueOf(status);
            } catch (Throwable t) {
                log4j.error("No status code for: '" + status + "'.");
                this.statuscode = STATUS_CODE.UNKNOWN_ERROR;
            }
        }
        
        String error = XMLUtil.fetchXPathValAsString(mime1, XPathEnum.ERROR_TYPE.value());
        this.errortype = ERROR_TYPE.UNKNOWN_ERROR;
        if (!GenericValidator.isBlankOrNull(error)) {
            try {
                this.errortype = ERROR_TYPE.valueOf(error);
            } catch (Throwable t) {
                log4j.error("No error type for: '" + error + "'.");
                this.errortype = ERROR_TYPE.UNKNOWN_ERROR;
            }
        }
        this.errortext = XMLUtil.fetchXPathValAsString(mime1, XPathEnum.ERROR_TEXT.value());
        this.errorfieldname = XMLUtil.fetchXPathValAsString(mime1, XPathEnum.ERROR_FIELD_NAME.value());
        
    }
    
    public STATUS_CODE getStatuscode() {
        return this.statuscode;
    }
    
    public ERROR_TYPE getErrortype() {
        return this.errortype;
    }
    
    public String getErrortext() {
        return this.errortext;
    }
    
    public String getErrorfieldname() {
        return this.errorfieldname;
    }
    
}

/*****************************************************************************
 * ELSEVIER CONFIDENTIAL
 * 
 * This document is the property of Elsevier, and its contents are proprietary to Elsevier. Reproduction in any form by
 * anyone of the materials contained herein without the permission of Elsevier is prohibited. Finders are asked to
 * return this document to the following Elsevier location.
 * 
 * Elsevier 360 Park Avenue South, New York, NY 10010-1710
 * 
 * Copyright (c) 2013 by Elsevier, A member of the Reed Elsevier plc group.
 * 
 * All Rights Reserved.
 *****************************************************************************/
