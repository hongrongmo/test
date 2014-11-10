package org.ei.biz.security;

/**
 * This class is an authorization failure and represents an enumeration of the
 * possible authorization failures.  It is returned by an AbstractAccessControl 
 * object to indicate to the caller why the authorization failed.  The caller 
 * then can better determine what to do when the authorization fails.
 */
public final class AuthorizationFailure {
    /** The name of the user access type */
    private String m_name;

    /** The resource requested requires individual authentication to access */
    public static final AuthorizationFailure INDIVIDUAL_AUTHENTICATION_REQUIRED =
        new AuthorizationFailure("INDIVIDUAL_AUTHENTICATION_REQUIRED");

    /** The resource requested requires authentication to access */
    public static final AuthorizationFailure AUTHENTICATION_REQUIRED =
        new AuthorizationFailure("AUTHENTICATION_REQUIRED");

    /** 
     * The resource requested requires no authentication to access.  This
     * is not really an error but is used when no authentication is required
     * and an AuthorizationFailure is requested.
     */
    public static final AuthorizationFailure NO_AUTHENTICATION_REQUIRED =
        new AuthorizationFailure("NO_AUTHENTICATION_REQUIRED");

    /** 
     * The resource requested requires authentication to access and the
     * user should be forwarded to the marketing home page. 
     */
    public static final AuthorizationFailure AUTHENTICATION_REQUIRED_HOME =
        new AuthorizationFailure("AUTHENTICATION_REQUIRED_HOME");

    /** 
     * The resource requested has been disabled and the user
     * should be forwarded to the the error page. 
     */
    public static final AuthorizationFailure FEATURE_DISABLED =
        new AuthorizationFailure("FEATURE_DISABLED");

    /**
     * Constructor.  Private since only this class constructs the possible
     * authorization failures.
     * @param name The string label for the authorization error
     */
    private AuthorizationFailure(String name) {
        m_name = name;
    }

    /**
     * Returns the string label for the authorization error.
     * @return String The string label for the authorization error.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return m_name;
    }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		// Compare the two objects using the name of the authorization failure
		if (obj != null && obj instanceof AuthorizationFailure) {
			AuthorizationFailure failure = (AuthorizationFailure) obj;
			String name = failure.getName();
			if (name != null && name.equals(this.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return m_name.hashCode();
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
