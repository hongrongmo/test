package org.ei.service.cars.rest.util;

/**
 * This class is a wrapper class for processing stylesheets.
 */
public final class Stylesheet implements IStylesheet
{
	/**
	 * This is the object that the Stylesheet wraps. 
	 */
	private Object  m_stylesheet = null;

    /**
     * This is the style sheet config object.
     */
    private Object m_stylesheetConfig;

	/**
	 * @see com.elsevier.els.app.biz.cache.stylesheet.stylesheet.IStylesheet#getStylesheetImpl()
	 */
	public Object getStylesheetImpl()
	{
		return m_stylesheet;
	}

	/**
	 * @see com.elsevier.els.app.biz.cache.stylesheet.stylesheet.IStylesheet#setStylesheetImpl(Object object)
	 */
	public void setStylesheetImpl(Object  object)
	{
		m_stylesheet = object;
	}
	
	/**
	 * This is the constructor
	 * @param object ?
	 */
	public Stylesheet(Object object)
	{
		m_stylesheet = object;
	}
    /**
     * Returns the stylesheet configuration object.
     * 
     * @return Object
     */
    public Object getStylesheetConfig() {
        return m_stylesheetConfig;
    }

    /**
     * Sets the stylesheet configuration object.
     * 
     * @param config ?
     */
    public void setStylesheetConfig(Object config) {
        m_stylesheetConfig = config;
    }

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
                                                                              
	Copyright (c) 2005 by Elsevier, A member of the Reed Elsevier plc 
	group.                                                                    
                                                                              
	All Rights Reserved.                                                      
                                                                              
 *****************************************************************************/

