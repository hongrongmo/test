package org.ei.biz.security;

import org.ei.domain.personalization.IEVWebUser;


/**
 * This AccessControl object requires that the user be individual authenticated.
 */
public class IndividualAuthRequiredAccessControl extends AbstractAccessControl {

    /** Constant that defines the allowed types (i.e. Individual). */
    private static final SecurityAttribute[] ALLOWED = 
        new SecurityAttribute[]{SecurityAttribute.INDIVIDUAL};

	/**
	 * @see org.ei.biz.security.AbstractAccessControl#allow()
	 */
	protected SecurityAttribute[] allow() {
		return ALLOWED;
	}

	/**
	 * @see org.ei.biz.security.AbstractAccessControl#deny()
	 */
	protected SecurityAttribute[] deny() {
		return null;
	}

	/**
	 * @see org.ei.biz.security.AbstractAccessControl#getAuthorizationFailure()
	 */
	public AuthorizationFailure getAuthorizationFailure() {
		return AuthorizationFailure.INDIVIDUAL_AUTHENTICATION_REQUIRED;
	}

	@Override
	public boolean isAccessAllowed(IEVWebUser user) {
		// TODO Auto-generated method stub
		if("INDIVIDUAL".equalsIgnoreCase(user.getUserAnonymity())){
			return true;
		}
		return false;
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
