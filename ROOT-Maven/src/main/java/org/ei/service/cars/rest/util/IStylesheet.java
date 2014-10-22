package org.ei.service.cars.rest.util;

/**
 * This interface is used to wrap the implementation of a stylesheet.
 */
public interface IStylesheet {
    /**
     * This method returns the lower level implementation.
     * 
     * @return Serializable
     */
    public Object getStylesheetImpl();

    /**
     * This method sets the lower level implementation.
     * 
     * @param object Stylesheet implementation.
     */
    public void setStylesheetImpl(Object object);

    /**
     * This method returns the Stylesheet Config object.
     * 
     * @return Serializable
     */
    public Object getStylesheetConfig();

    /**
     * This method sets the Stylesheet Config object.
     * 
     * @param object An instance of XSLStylesheetConfig.
     */
    public void setStylesheetConfig(Object object);
}

/*****************************************************************************
                                                                             
                                                                              
								ELSEVIER                                
                                                                              
							  CONFIDENTIAL                                    
                                                                              
	This document is the property of Elsevier, and its contents are proprietary
	to Elsevier.   Reproduction in any form by anyone of the materials 
	contained  herein  without  the  permission  of Elsevier is prohibited.  
	Finders are  asked  to  return  this  document  to the following Elsevier 
	Science location.                                  
                                                                              
		Elsevier                                                   
		360 Park Avenue South
		New York, NY 10010-1710
                                                                              
	Copyright (c) 2004 by Elsevier, A member of the Reed Elsevier plc 
	group.                                                                    
                                                                              
	All Rights Reserved.                                                      
                                                                              
 *****************************************************************************/
