package org.ei.biz.access;

import org.ei.biz.security.SecurityAttribute;
import org.ei.biz.security.UserAccessType;

/**
Represents a user that accesses the Application.
TODO This was blatantly copied from the STEPs EAL code base IAppUser 
interface.  Should EV ever integrate with that code base, this interface
should derive from IAppUser and some of the methods re-defined...
 */
public interface IEVUser {

	/**
	 * Retrieve the user authentication information used to create this
	 * IUser object.
	 * @return IUserAuthentication
	 */
	public IUserAuthentication getUserAuthInfo();
	
    /**
     * Retrieve the user preferences for this user.
     * @return IUserPreferences
     * @exception AccessException thrown if the user preferences data
     *            can't be loaded.
     */
    public IUserPreferences getUserPreferences() throws AccessException;
    
    /**
     * Retrieve the user info for this user.
     * @return IUserInfo
     * @exception AccessException thrown if the user info data can't be
     *            loaded.
     */
    public IUserInfo getUserInfo() throws AccessException;

    /**
     * Returns the user access type.  The returned value is of the
     * type UserAccessType which uniquely identifies a user access type.
     * @return UserAccessType The user access type.
     */
    public UserAccessType getUserAccessType();

    /**
    @return IUserFeatureConstraints Feature constraints that apply to the user.
    @roseuid 3E5E7C26020F
     */
    public IUserPreferences getFeatureConstraints();

    /**
     * Returns the feature constraint value for this
     * user associated with the specified name.
     *
     * @param name name of feature constraint
     * @return true if the user should be granted access to the feature
     * @throws AccessException if the feature constaints cannot be acquired
     *      because of a system problem.
     */
    public boolean getFeatureConstraint(String name) throws AccessException;

    /**
    @return IAccount The user's account.
     */
    public IEVAccount getAccount();

    /**
     Returns the User ID, or null if the user is not individually authenticated.
     @return The User ID, or null if the user is not individually authenticated.
     */
    public String getUserId();

    /**
     Returns the bulk password, or null if the user is not a bulk user.
     @return The bulk password, or null if the user is not a bulk user.
     */
    public String getBulkPassword();

    /**
     * Return whether the user's password must be reset before they can continue.
     * @return boolean True if the user is required to reset his/her password.
     */
    public boolean isPasswordResetRequired();

    /**
     * Is the user individually authenticated--as opposed to group
     * authenticated.  Checks for such things as username/password login
     * or IP profile.
     * @return boolean
     */
    public boolean isIndividuallyAuthenticated();

    /**
    Returns a unique id that identifies the user.  It should not change over
    time unless the user is deleted.  Even in situations where the user is not
    individually authenticated, a WebUserId will be returned (related to a
    'Guest' account).

    <P>In general, the user will not be familiar with the Web User Id - they
    are machine-generated.

    @return A unique id that identifies the user.
    */
    public String getWebUserId();

    /**
     * Returns the security attributes for the user.
     * @return An array of security attributes for the user.
     */
    public SecurityAttribute[] getSecurityAttributes();
}
/*****************************************************************************
 *                             ELSEVIER - SCIENCE
 *                                CONFIDENTIAL
 *
 * This document is the property of Elsevier Science (ES), and its contents are
 * proprietary to ES. Reproduction in any form by anyone of the materials
 * contained herein without the permission of ES is prohibited. Finders are
 * asked to return this document to the following Elsevier Science location.
 *
 * Elsevier Science
 * 360 Park Avenue South,
 * New York, NY 10010-1710
 *
 * Copyright (c) 2010 by Elsevier Science, a member of the Reed Elsevier plc
 * group. All Rights Reserved.
 *****************************************************************************/
