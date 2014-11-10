package org.ei.biz.security;

import org.ei.biz.access.IEVUser;
import org.ei.biz.personalization.IEVWebUser;

/**
 * This AccessControl object requires no authentication at all.  It always
 * returns true for the isAccessAllowed().
 */
public class NoAuthAccessControl extends AbstractAccessControl {

	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#allow()
	 */
	protected SecurityAttribute[] allow() {
		return null;
	}

	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#deny()
	 */
	protected SecurityAttribute[] deny() {
		return null;
	}

	/** (non-Javadoc)
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#isAccessAllowed(com.IEVUser.els.app.biz.access.IAppUser)
	 */
	public boolean isAccessAllowed(IEVUser user) {
		return true;
	}
    
	/**
	 * @see com.elsevier.els.app.biz.security.authorization.AbstractAccessControl#getAuthorizationFailure()
	 */
	public AuthorizationFailure getAuthorizationFailure() {
		return AuthorizationFailure.NO_AUTHENTICATION_REQUIRED;
	}

	@Override
	public boolean isAccessAllowed(IEVWebUser user) {
		return true;
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
