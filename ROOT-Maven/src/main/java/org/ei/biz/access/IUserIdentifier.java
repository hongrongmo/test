package org.ei.biz.access;


/**
 * This interface defines the methods that need to be implemented for user
 * identifiers.  The type associated with a given user identifier is
 * implementation dependent and will need to be defined by the implementation.
 * Some examples would be IP address, or user id.
 */
public interface IUserIdentifier {
    /**
     * Set the user identifier value.
     * @param id The identifier value at a String.
     */
    public void setUserId(String id);
    
    /**
     * Retrieve the user identifier value.
     * @return String
     */
    public String getUserId();
    
    /**
     * Set the type for this user identifier.
     * @param type value representing this user identifier.
     */
    public void setUserIdType(IUserIdentifierType type);
    
    /**
     * Retrieve the user identifier type.
     * @return IUserIdentifierType
     */
    public IUserIdentifierType getUserIdType();
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
