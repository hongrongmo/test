package org.ei.biz.access;

/**
Represents an Account that can access the system.  
TODO This was blatantly copied from the STEPs EAL code base IAccount 
interface.  Should EV ever integrate with that code base, this interface
should derive from IAccount and some of the methods re-defined...
 */
public interface IEVAccount {

	/**
	Returns the id of the account.
	@return account id
	 */
	public String getAccountId();

	/**
	Returns the id of the department.
	@return department id
	 */
	public String getDepartmentId();

	/**
	Returns the name of the account.
	@return account name
	 */
	public String getAccountName();

	/**
	 * Returns the user entitlements
	 * @return String entitlements
	 */
	public String[] getEntitlements();

 	/**
	 * Returns the user Shibboleth targeted Id
	 * @return String targeted Id
	 */
	public String getShibTargetedId();

	/**
	 * Returns the user Shibboleth enntitlements
	 * @return String entitlements
	 */
	public String getShibEntitlements();

	/**
	 * Returns the user Shibboleth provider Id
	 * @return String provider Id
	 */
	public String getShibProviderId();

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
