package org.ei.service.cars;

/**
 * Enum class to hold all cars page types
 * 
 * @author naikn1
 * @version 1.0
 *
 */
public enum PageType {
	/**
	 * The constant name for holding page type EMAIL
	 * (for example forgot password confirmation mail)
	 */
	EMAIL,
	/**
	 * The constant name for holding page type GENERIC 
	 * (all cars related pages like change password, settings, register)
	 */
	GENERIC,
	/**
	 * The constant name for holding page type POPUP
	 * (for example forgot password page to be open in a popup window)
	 */
	POPUP,
	/**
	 * The constant name for holding page type LOGIN
	 * (for example login popup page)
	 */
	LOGIN,
	/**
	 * The constant name for holding page type REDIRECT
	 * (for example shib login page)
	 */
	REDIRECT;
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

   Copyright (c) 2013 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/