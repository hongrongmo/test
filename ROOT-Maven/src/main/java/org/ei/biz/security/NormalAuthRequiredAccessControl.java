package org.ei.biz.security;


/**
 * An AccessControl object that allows any normal authenticated user to access
 * the resource in question.  The user must be authenticated and must be a
 * normal, unrestricted user type.  Guest, bulk, and cctivation pending user 
 * types are not allowed by this access control.
 */
public class NormalAuthRequiredAccessControl extends AbstractAccessControl {
    /** Constant that defines the allowed types for normal authentication. */
    private static final SecurityAttribute[] ALLOWED = 
        {SecurityAttribute.INDIVIDUAL, SecurityAttribute.ANONYMOUS, SecurityAttribute.CANREGISTER};

    /** Constant that defines the denied types for normal authentication. */
    private static final SecurityAttribute[] DENIED = 
        {SecurityAttribute.GUEST};

	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#allow()
	 */
	protected SecurityAttribute[] allow() {
		return ALLOWED;
	}

	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#deny()
	 */
	protected SecurityAttribute[] deny() {
		return DENIED;
	}

	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#getAuthorizationFailure()
	 */
	public AuthorizationFailure getAuthorizationFailure() {
		return AuthorizationFailure.AUTHENTICATION_REQUIRED;
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
