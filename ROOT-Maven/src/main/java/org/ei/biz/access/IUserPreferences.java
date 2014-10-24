package org.ei.biz.access;

import java.util.Set;

/**
 * This interface defines the methods that need to be implemented by a user
 * preferences object.  The user preferences object is meant to hold
 * application-specific information about a user, such as the number of
 * results to display for a search.
 */
public interface IUserPreferences {
    /**
     * Retrieve a string value based on a key.
     * @param key
     * @return String
     * @exception AccessException thrown if the key isn't found.
     */
    public String getString(String key) throws AccessException;
    
    /**
     * Set a string value for a given key.  If the key already has a value
     * associated with it, that value will be replaced.
     * @param key
     * @param value
     */
    public void setString(String key, String value);
    
    /**
     * Retrieve a boolean value for a given key.
     * @param key
     * @return boolean
     * @exception AccessException thrown if the key isn't found.
     */
    public boolean getBoolean(String key) throws AccessException;
    
    /**
     * Set a boolean value for a given key.  If the key already has a value
     * associated with it, that value will be replaced.
     * @param key
     * @param value
     */
    public void setBoolean(String key, boolean value);
    
    /**
     * Retrieve a long value for a given key.
     * @param key
     * @return long
     * @exception AccessException thrown if the key isn't found.
     */
    public long getLong(String key) throws AccessException;
    
    /**
     * Set a long value for a given key.  If the key already has a value
     * associated with it, that value will be replaced.
     * @param key
     * @param value
     */
    public void setLong(String key, long value);

    /**
     * Retrieve the full set of keys.
     * @return Set
     */
    public Set keys();
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
