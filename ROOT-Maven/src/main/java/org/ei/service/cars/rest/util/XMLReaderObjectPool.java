package org.ei.service.cars.rest.util;


import org.apache.commons.pool.impl.GenericObjectPool;
import org.xml.sax.XMLReader;

/**
 * This pool is used to hold XMLReader objects.
 */
public final class XMLReaderObjectPool extends GenericObjectPool
{
  
  /**How many active objects can we have*/
  private static int c_maxActive = -1;
  
  /**How many idle objects can we have?*/
  private static final int MAX_IDLE = -1;
  
  /**How long to block when waiting for an object from the pool*/
  private static final long MAX_WAIT = -1;
  
  /**How long an object is not used till it is considered idle*/
  private static final long MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;
  
  /**How many objects to test during an eviction run.*/
  private static final int NUM_TESTS_PER_EVICTION_RUN = 0;
  
  /**Test the object when we borrow it?*/
  private static final boolean TEST_ON_BORROW = false;
  
  /**Test the object when we return it?*/
  private static final boolean TEST_ON_RETURN = false;
  
  /**Test the object when it is idle?*/
  private static final boolean TEST_WHILE_IDLE = false;
  
  /**Time between eviction runs if we are purging the pool (we are not).*/
  private static final long TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1;
  
  /**Action to take when MAX_ACTIVE is reached.*/
  private static final byte WHEN_EXHAUSTED_ACTION = WHEN_EXHAUSTED_GROW;   
  
  /**The singleton instance of the pool*/
  private static XMLReaderObjectPool c_instance = null;  
  
  /**The factory used to create the XMLReader*/
  private static XMLReaderObjectFactory c_factory = null;
  
  /**
   *  Initialize all the necessary static objects in a proper order.
   */
  static {
    init();
  }
  
  /** Initializes all the necessary static objects in a proper order. */
  private static void init()
  {
	  // TODO define property for this
	//c_maxActive = SystemConfig.getIntegerProperty(
	//	EnvironmentSettingConstants.XMLREADER_POOL_MAX_ACTIVE);
	c_maxActive = 10;
	c_factory = new XMLReaderObjectFactory();
	c_instance = new XMLReaderObjectPool();
  }
  /**
   *  Create an instance of the XMLReaderObjectPool with the desired configuration. 
   */
  private XMLReaderObjectPool()
  {
    super(c_factory,c_maxActive, WHEN_EXHAUSTED_ACTION, MAX_WAIT, MAX_IDLE,
          TEST_ON_BORROW, TEST_ON_RETURN, TIME_BETWEEN_EVICTION_RUNS_MILLIS,
          NUM_TESTS_PER_EVICTION_RUN, MIN_EVICTABLE_IDLE_TIME_MILLIS, TEST_WHILE_IDLE);
  }
 
  /**
   * Get an instance of this singleton pool.
   * @return the object pool
   */
  public static XMLReaderObjectPool getInstance(){return c_instance;}

  /**
   * borrow an XMLReader from the pool.
   * @return the reader we borrowed.
 * @throws Exception 
   */
  public XMLReader borrowXMLReader() throws Exception 
  {
      return (XMLReader)super.borrowObject();
    
  }

  /**
   * Return the XMLReader to the pool.
   * @param reader The reader to return.
 * @throws Exception 
   */
  public void returnXMLReader(XMLReader reader) throws Exception{
      super.returnObject(reader);
    
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

	Copyright (c) 2004 by Elsevier, A member of the Reed Elsevier plc
	group.

	All Rights Reserved.

 *****************************************************************************/
