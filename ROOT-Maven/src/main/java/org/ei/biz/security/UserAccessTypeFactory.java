package org.ei.biz.security;

/**
 * This factory creates and manages UserAccessType objects.  Currently, it
 * will take a string and produce the appropriate UserAccessType object.
 */
public final class UserAccessTypeFactory {
    /** The single instance of the factory that everyone uses. */
    private static UserAccessTypeFactory c_instance = 
        new UserAccessTypeFactory();
    
	/**
	 * Constructor.
     * Private since it is a singleton and object creation is controlled.
     * Only this object creates an instance.
	 */
    private UserAccessTypeFactory() {
    }
    
	/**
	 * Returns the appropriate UserAccessType object for the specified string.
	 * @param typeName The string version of the UserAccessType.
	 * @return UserAccessType The object version of the UserAccessType or null
     *          if the name specified is not found.
	 */
    public UserAccessType getUserAccessType(String typeName) {
        return (UserAccessType) UserAccessType.getAllTypes().get(typeName);
    }
    
	/**
	 * Returns the single instance of the factory.
	 * @return UserAccessTypeFactory The instance of the factory.
	 */
    public static UserAccessTypeFactory getInstance() {
        return c_instance;
    }
}

/*****************************************************************************

                               ELSEVIER
                             CONFIDENTIAL


   This document is the property of Elsevier, and its contents are
   proprietary to Elsevier.   Reproduction in any form by anyone of the
   materials contained  herein  without  the  permission  of Elsevier is
   prohibited.  Finders are  asked  to  return  this  document  to the
   following Elsevier location.

       Elsevier
       360 Park Avenue South,
       New York, NY 10010-1710

   Copyright (c) 2003 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/
