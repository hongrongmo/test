package org.ei.biz.access;


/**
 * This interface defines the methods that need to be implemented for user
 * passwords.
 */
public interface IUserCredentials {
    /**
     * Set the password entered by this user.
     * @param password String
     */
    public void setPassword(String password);
    
    /**
     * Retrieve the password entered by this user.
     * @return String
     */
    public String getPassword();
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
