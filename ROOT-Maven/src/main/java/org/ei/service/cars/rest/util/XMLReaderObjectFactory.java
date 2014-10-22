package org.ei.service.cars.rest.util;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.elsevier.edit.common.client.util.XMLSax;


/**
 * This XMLReader factory creates an instance of an XMLReader and configures it
 * with the desired settings optimal for XSL processing.
 */
class XMLReaderObjectFactory extends BasePoolableObjectFactory
{
  /**
   * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
   */
  public Object makeObject() throws Exception
  {
    XMLReader reader = null;
    try
    {
     
      reader = XMLReaderFactory.createXMLReader(XMLSax.DEFAULT_PARSER_NAME);
        reader.setFeature(
          "http://xml.org/sax/features/validation",
          false);
        reader.setFeature(
          "http://xml.org/sax/features/namespaces",
          true);
        reader.setFeature(
          "http://xml.org/sax/features/namespace-prefixes",
          false);
        reader.setFeature(
          "http://apache.org/xml/features/validation/schema",
          false);
        reader.setFeature(
          "http://apache.org/xml/features/validation/schema-full-checking",
          false);
        reader.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-external-dtd",
          true);
        reader.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
          true);
        reader.setFeature(
          "http://apache.org/xml/features/validation/schema/augment-psvi",
          false);
        reader.setFeature(
          "http://xml.org/sax/features/external-parameter-entities",
          false);
        reader.setFeature(
          "http://xml.org/sax/features/external-general-entities",
          false);
      
    }
    catch (SAXNotRecognizedException e)
    {
      throw new XSLTTransformationException("Error creating XMLReader",e);
    }
    catch (SAXNotSupportedException e)
    {
    	throw new XSLTTransformationException("Error creating XMLReader",e);
    }
    catch (SAXException e)
    {
    	throw new XSLTTransformationException("Error creating XMLReader",e);
    }
    return reader;
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