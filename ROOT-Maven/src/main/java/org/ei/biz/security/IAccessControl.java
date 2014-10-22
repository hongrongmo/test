package org.ei.biz.security;

import org.ei.biz.personalization.IEVWebUser;

/**
 * An access control specifies whether a given user can access a given resource. This interface defines all the methods common to any access control.
 */
public interface IAccessControl {
    /**
     * Resolves whether a particular user is allowed to access the resource which implements this interface. The decision is decided by the security attributes
     * for the user. The implementing object will make the determination and return a boolean.
     * 
     * @param user
     *            The user object to check.
     * @return boolean True if access is allowed, otherwise false.
     */
    public boolean isAccessAllowed(IEVWebUser user);

    /**
     * Returns the authorization failure. This failure describes the reason why this access control would reject the user. It is used to allow the caller to
     * determine what action to take when authorization fails by providing a cause or reason for the failure.
     * 
     * @return AuthorizationFailure The cause for the authorization failure.
     */
    public AuthorizationFailure getAuthorizationFailure();
}

/*****************************************************************************
 * ELSEVIER CONFIDENTIAL
 * 
 * 
 * This document is the property of Elsevier, and its contents are proprietary to Elsevier. Reproduction in any form by anyone of the materials contained herein
 * without the permission of Elsevier is prohibited. Finders are asked to return this document to the following Elsevier location.
 * 
 * Elsevier 360 Park Avenue South, New York, NY 10010-1710
 * 
 * Copyright (c) 2003 by Elsevier, A member of the Reed Elsevier plc group.
 * 
 * All Rights Reserved.
 *****************************************************************************/
