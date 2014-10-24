package org.ei.domain;


/**
 * This interface defines the methods that need to be implemented by user info
 * objects. The user info object is meant to hold non-application specific
 * information about the user, such as their name and email address.
 */
public interface IUserInfo {
	/**
	 * get the value of customer id. @ return : String
	 */
	public String getCustomerId();

	/**
	 * get the value of contract id. @ return : String
	 */
	public String getContractId();

	/**
	 * get the value of email. @ return : String
	 */
	public String getEmail();

	/**
	 * get the value of first name. @ return : String
	 */
	public String getFirstName();

	/**
	 * get the value of last name. @ return : String
	 */
	public String getLastName();

	/**
	 * get the value of title name. @ return : String
	 */
	public String getTitle();

	/**
	 * get the value of announce flag. @ return : String
	 */
	public String getAnnounceFlag();

	/**
	 * get the value of password. @ return : String
	 */
	public String getPassword();
}

/*****************************************************************************
 * ELSEVIER - SCIENCE
 * 
 * CONFIDENTIAL
 * 
 * This document is the property of Elsevier Science (ES), and its contents are
 * proprietary to ES. Reproduction in any form by anyone of the materials
 * contained herein without the permission of ES is prohibited. Finders are
 * asked to return this document to the following Elsevier Science location.
 * 
 * Elsevier Science 360 Park Avenue South New York, NY 10010-1710
 * 
 * Copyright (c) 2002 by Elsevier Science, A member of the Reed Elsevier plc
 * group.
 * 
 * All Rights Reserved.
 *****************************************************************************/
