package org.ei.biz.security;

import org.ei.biz.personalization.IEVWebUser;
import org.ei.session.UserPreferences;

/**
 * This AccessControl object requires that the user be individually authenticated
 * and have access to Tags & Groups.
 */
public class TagsGroupsAccessControl extends NormalAuthRequiredAccessControl {

	AuthorizationFailure authorizationfailure = AuthorizationFailure.FEATURE_DISABLED;

    public boolean isAccessAllowed(IEVWebUser user) {
			if (!user.getPreference(UserPreferences.FENCE_TAGSGROUPS)) {
				return false;
			}

			if (!super.isAccessAllowed(user)) {
    		authorizationfailure = AuthorizationFailure.AUTHENTICATION_REQUIRED;
			return false;
    	}
    	return true;
    }
	/**
	 * @see org.ei.biz.security.AbstractAccessControl#getAuthorizationFailure()
	 */
	public AuthorizationFailure getAuthorizationFailure() {
		return authorizationfailure;
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
