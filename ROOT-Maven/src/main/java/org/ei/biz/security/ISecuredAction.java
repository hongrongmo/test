package org.ei.biz.security;

/**
 * This interface defines a secured action.  The interace returns a class
 * that implements IAccessControl.  This object specifies what user types 
 * can access the action.
 */
public interface ISecuredAction {
	/**
	 * Returns the access control for this action.
	 * @return IAccessControl An object that implements IAccessControl and 
     *                         defines who can access the action.
	 */
    public IAccessControl getAccessControl();
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
