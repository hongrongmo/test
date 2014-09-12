package org.ei.biz.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ei.biz.access.IEVUser;
import org.ei.biz.personalization.IEVWebUser;


/**
 * An access control defines what users may access a given resource.
 * The access control is used by first checking the deny list and then
 * checking the allow list.
 *
 * NOTE: This class assumes a deny by default policy!  You must explicitly
 * allow a user type or no one will be allowed to access the given resource.
 *
 * This class should be extended and allow() and deny() should be implemented.
 * Special processing can be done by overriding isAccessAllowed().
 *
 * For example, allowing all users is best accomplished by overriding
 * isAccessAllowed().
 */
public abstract class AbstractAccessControl implements IAccessControl {
	/** Constant for the Match Any option */
    protected static final String MATCH_ANY = "MATCH_ANY";

    /** Constant for the Match All option */
    protected static final String MATCH_ALL = "MATCH_ALL";

    /**
     * Returns a list of security attributes that are allowed to access the
     * action.  This list is processed after the deny list.  This method is
     * meant to be used by derived classes only.
     * @return SecurityAttribute[] The allowed user security attributes.
     */
    protected abstract SecurityAttribute[] allow();

    /**
     * Returns a list of security attributes that are denied access to the
     * action.  This list is processed before the allow list.  This method is
     * meant to be used by derived classes only.
     * @return String[] The denied user security attributes.
     */
    protected abstract SecurityAttribute[] deny();

    /**
     * Returns the match type for the allow list.  The default
     * implementation will allow matching on ANY.
     * @return String The match type.
     */
    protected String getAllowMatchType() {
        return MATCH_ANY;
    }

    /**
     * Returns the match type for the deny list.  The default
     * implementation will allow matching on ANY.
     * @return String The match type.
     */
    protected String getDenyMatchType() {
        return MATCH_ANY;
    }

	/**
	 * Resolves whether a user is allowed to access the resource based on
     * the security attributes of the user.  The default implementation uses
     * the allow and deny lists.  This method can be overriden when special
     * handling is needed.
     * @param user The user object.
	 * @return boolean True if access is allowed, otherwise false.
     * @see com.elsevier.els.app.biz.security.authorization.IAccessControl#isAccessAllowed(IEVUser)
	 */
    public boolean isAccessAllowed(IEVWebUser user) {
        // Get the security attributes for the user.
        List<SecurityAttribute> attributes = getSecurityAttributes(user);

        // If null is returned then no user was found so return false.
        if (attributes == null || attributes.isEmpty()) {
            return false;
        }

        // Find out how many atributes are in the denied list
        int denyCount = 0;
        SecurityAttribute[] denied = deny();
        if (denied != null) {
            for (int i = 0; i < denied.length; i++) {
                if (attributes.contains(denied[i])) {
                    denyCount++;
                }
            }
        }

        // Check if the user is denied
        if (getDenyMatchType().equals(MATCH_ANY) && denyCount > 0) {
            return false;
        } else if (getDenyMatchType().equals(MATCH_ALL) &&
                   denyCount == attributes.size()) {
            // The match type requires that all match and the number of
            // matches equals the number of attributes.
            return false;
        }

        // Find out how many attributes are in the allow list
        int allowCount = 0;
        SecurityAttribute[] allowed = allow();
        if (allowed != null) {
            for (int i = 0; i < allowed.length; i++) {
                if (attributes.contains(allowed[i])) {
                    return true;
                }
            }
        }

        // Check if the user is allowed
        if (getAllowMatchType().equals(MATCH_ANY) && allowCount > 0) {
            return true;
        } else if (getAllowMatchType().equals(MATCH_ALL) &&
                   allowCount == attributes.size()) {
            // The match type requires that all match and the number of
            // matches equals the number of attributes.
            return true;
        }

        // If the user is not explicitly denied or allowed, then the user
        // is denied by default.
        return false;
    }

	/**
	 * Returns the authorization failure.  This failure describes the reason
     * why this access control would reject the user.  It is used to allow
     * the caller to determine what action to take when authorization fails.
	 * @return AuthorizationFailure
     * @see com.elsevier.els.app.biz.security.authorization.IAccessControl#getAuthorizationFailure()
	 */
    public abstract AuthorizationFailure getAuthorizationFailure();

    /**
     * A utility method that gathers the user's security attributes.  It is
     * used by isAccessAllowed() and can be used by derived classes if they
     * override isAccessAllowed().
     * @param user The user whose attributes will be retrieved.
     * @return A List of the user's security attributes.
     */
    @SuppressWarnings("unchecked")
	protected List<SecurityAttribute> getSecurityAttributes(IEVWebUser user) {
        // If no user provided then return false
        if (user == null) {
            return null;
        }

        // Set the attribute list to an empty list in case no
        // security attributes are returned.
        List<SecurityAttribute> attributes = Collections.EMPTY_LIST;

        // Get the user's security attributes.
        // Return false if none are present.
        SecurityAttribute[] attributesArray = user.getSecurityAttributes();
        if (attributesArray != null) {
            attributes = new ArrayList<SecurityAttribute>(attributesArray.length);
            for (int i = 0; i < attributesArray.length; i++) {
                attributes.add(attributesArray[i]);
            }
        }

        return attributes;
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
