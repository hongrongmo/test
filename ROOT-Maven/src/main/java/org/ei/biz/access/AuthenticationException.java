package org.ei.biz.access;


/**
 * Thrown when an authentication problem occurs such as when a client is not 
 * authorized to use a particular service
 * 
 * @since 1.0 
 */
public class AuthenticationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5586589203391412551L;

	/**
     * Constructor for AuthenticationException
     * 
     * @param ee The error event id to use when reporting this exception.
     * @param message The error message
     * @param throwable An exception to nest.
     */
    public AuthenticationException(
        String message,
        Throwable throwable) {
        super(message, throwable);
    }

}
/*****************************************************************************
                                                                             
                                                                              
                           ELSEVIER - SCIENCE                                 
                                                                              
                              CONFIDENTIAL                                    
                                                                              
    This document is the property of Elsevier Science (ES),                   
    and its contents are proprietary to ES.   Reproduction in any form by     
    anyone of the materials contained  herein  without  the  permission  of   
    ES is prohibited.  Finders are  asked  to  return  this  document  to     
    the following Elsevier Science location.                                  
                                                                              
        Elsevier Science                                                      
        360 Park Avenue South
        New York, NY 10010-1710
                                                                              
    Copyright (c) 2002 by Elsevier Science, A member of the Reed Elsevier plc 
    group.                                                                    
                                                                              
    All Rights Reserved.                                                      
                                                                              
 *****************************************************************************/