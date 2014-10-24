package org.ei.biz.access;


/**
 * This interface defines the methods needed for user authentication.
 * An instance of this class is needed before creating the IUser object
 * for a user.
 */
public interface IUserAuthentication {
    /**
     * Retrieve the list of identifiers for this user.
     * @return IUserIdentifier[] array of user identifiers set for this user.
     */
    public IUserIdentifier[] getUserIdentifierList();
    
    /**
     * Retrieve the credentials set for this user.
     * @return IUserCredentials The credentials object for this user.
     * Can be null.
     */
    public IUserCredentials getUserCredentials();
    
    /**
     * Retrieve the name of the system that this user is accessing.
     * @return String
     */
    public String getSystemName();
    /**
     * Retrieve the name of the platform that this user is accessing.
     * @return String
     */
    public String getPlatformCode();
    /**
     * Retrieve the name of the product that this user is trying to access.
     * @return String
     */
    public String getProductName();
    
    /**
     * Attempt to validate this user.  This call determines if the user can
     * be identified in the system as a valid user based on the user
     * identifiers set and any password entered.  This method should be
     * called from the constructor after setting the IUserIdentifier and
     * IUserPassword objects for this user.
     * @exception AccessException thrown if there is a problem performing
     *            user validation, such as a database problem.
     * @exception AuthenticationException thrown if the user is not a valid
     *            user of the system.
     */
    public void validateUser() throws AccessException, AuthenticationException;
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
