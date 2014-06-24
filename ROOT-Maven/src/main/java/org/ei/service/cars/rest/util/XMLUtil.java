package org.ei.service.cars.rest.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.XMLNamespaceEnum;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a util class which is used for processing the XML data received from
 * CS
 * 
 * @author naikn1
 * @version 1.0
 * 
 */
public class XMLUtil {

	private final static Logger log4j = Logger.getLogger(XMLUtil.class);

	/**
	 * name space context declaration
	 */
	static NamespaceContext c_nameSpaceContext;

	/**
	 * static block to get the XML namespace and prefix
	 */
	static {
		c_nameSpaceContext = new NamespaceContext() {

			/**
			 * Returns the xml namespace uri
			 * 
			 * @see javax.xml.namespace.NamespaceContext#
			 *      getNamespaceURI(java.lang.String)
			 */
			public String getNamespaceURI(String prefix) {
				return XMLNamespaceEnum.getNameSpace(prefix);
			}

			/**
			 * Returns the xml prefix
			 * 
			 * @see javax.xml.namespace.NamespaceContext#
			 *      getPrefix(java.lang.String)
			 */
			public String getPrefix(String namespace) {
				return XMLNamespaceEnum.getPrefix(namespace);
			}

			/**
			 * @see javax.xml.namespace.NamespaceContext#
			 *      getPrefixes(java.lang.String)
			 */
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}
		};
	}

	/**
	 * This method retrieves the value corresponding to the Xpath expression
	 * provided from the XML response.
	 * 
	 * @param strXMLResp
	 *            - CARS MIME response as string
	 * @param xPathExp
	 *            - xpath string provided
	 * @param isListPresent
	 *            - boolean (when Xquery may return List of matches)
	 * @return Object - the xpath value from XML
	 * @throws ServiceException
	 * 
	 *             DocumentBuilder for parser would be not be any namespace
	 *             aware. Because namespace can be changed in CSAS response and
	 *             that will break the code.
	 */
	public static Object fetchXpathValueFromXML(String strXMLResp, String xPathExp, boolean isListPresent) throws ServiceException {
		String retVal = null;
		try {

			log4j.debug(CARSStringConstants.XPATH_EXR.value() + xPathExp);

			if (null != xPathExp) {
				XPathExpression xpExpression = fetchXPathExpValue(xPathExp);
				if (null != xpExpression) {
					DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
					documentBuilderFactory.setNamespaceAware(false);
					DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
					Document doc = builder.parse(new java.io.ByteArrayInputStream(strXMLResp.getBytes("UTF-8")));

					// InputSource iSource = new InputSource(strReader);

					if (isListPresent) {
						NodeList listVal = (NodeList) xpExpression.evaluate(doc, XPathConstants.NODESET);
						List<String> items = null;
						if (null != listVal && listVal.getLength() > 0) {
							int length = listVal.getLength();
							items = new ArrayList<String>(length);
							for (int nodeCounter = 0; nodeCounter < length; nodeCounter++) {
								if (listVal.item(nodeCounter).getNodeType() != Node.TEXT_NODE) {
									items.add(listVal.item(nodeCounter).getFirstChild().getNodeValue());
								} else {
									items.add(listVal.item(nodeCounter).getNodeValue());
								}
							}
						}
						return items;
					}
					retVal = xpExpression.evaluate(doc);
				}
			}
			log4j.debug(CARSStringConstants.XPATH_EXR_VAL.value() + retVal);
		} catch (XPathExpressionException xpathExp) {
			log4j.warn(CARSStringConstants.XPATH_COMPILE_ERROR.value() + xpathExp.getMessage());
			throw new ServiceException(SystemErrorCodes.XPATH_COMPILE_ERROR, CARSStringConstants.XPATH_COMPILE_ERROR.value(), xpathExp);
		} catch (Exception exp) {
			log4j.warn(CARSStringConstants.XPATH_COMMON_ERROR.value() + exp.getMessage());
			throw new ServiceException(SystemErrorCodes.XPATH_COMPILE_ERROR, CARSStringConstants.XPATH_COMPILE_ERROR.value(), exp);
		}
		return retVal;
	}

	/**
	 * This method is used for getting the xpath expression for the requested
	 * xpath string
	 * 
	 * 
	 * @param xPathExp
	 *            the xpath string given
	 * @return XPathExpression - xpath expression
	 * @throws XPathExpressionException
	 *             if any error occurs during xpath compilation
	 * 
	 *             XPathExpression would be not be any namespace aware. Because
	 *             namespace can be changed in CSAS response and that will break
	 *             the code.
	 */
	private static XPathExpression fetchXPathExpValue(String xPathExp) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		// xPath.setNamespaceContext(c_nameSpaceContext);
		XPathExpression xp = null;
		if (null != xPathExp) {
			xp = xPath.compile(xPathExp);
		}
		return xp;
	}

	/**
	 * This method uses XPath expressions to query the xml-data and fetch the
	 * corresponding node value as string
	 * 
	 * @param xml
	 *            - the xml-data from where xpath value will be retrieved
	 * @param xpath
	 *            - the node path to be retrieved
	 * @return String - the node value
	 * @throws ServiceException
	 */
	public static String fetchXPathValAsString(String xml, String xpath) throws ServiceException {
		Object value = null;
		if (null != xml && null != xpath) {
			value = fetchXpathValueFromXML(xml, xpath, false);
		}
		return value == null ? "" : value.toString();
	}

	/**
	 * This method uses XPath expressions to query the xml-data and fetch the
	 * corresponding node value as boolean
	 * 
	 * @param xml
	 *            - the xml-data from where xpath value will be retrieved
	 * @param xpath
	 *            - the node path to be retrieved
	 * @return boolean - the node value
	 * @throws ServiceException
	 */
	public static boolean fetchXPathValAsBoolean(String xml, String xpath) throws ServiceException {
		Object value = null;
		if (null != xml && null != xpath) {
			value = fetchXpathValueFromXML(xml, xpath, false);
		}
		return value == null ? false : Boolean.valueOf(value.toString()).booleanValue();
	}

	/**
	 * This method transforms the response XML provided to a HTML page using the
	 * XSL Transformation.
	 * 
	 * @param xmlResp
	 *            - the response xml as string which has to be trasformed
	 * @param xslTemplate
	 *            - CARS XSL template in cache
	 * @return String the transformed html content as string
	 * @throws TransformerConfigurationException
	 *             if any error occurs during while getting the file
	 * @throws TransformerException
	 *             if any error occurs during transformation
	 */
	public static String transformXML(String xmlResp, Templates xslTemplate) throws TransformerConfigurationException, TransformerException {
		Transformer transformer = null;
		StringWriter outWriter = new StringWriter(16);
		log4j.debug("XML that has to be transformed :" + xmlResp);
		// Use cached template to create the Transformer
		transformer = xslTemplate.newTransformer();

		// Perform the transformation from a StreamSource to a StreamResult;
		transformer.transform(new StreamSource(new StringReader(xmlResp)), new StreamResult(outWriter));

		return outWriter.getBuffer().toString();
	}
}

/*****************************************************************************
 * ELSEVIER CONFIDENTIAL
 * 
 * This document is the property of Elsevier, and its contents are proprietary
 * to Elsevier. Reproduction in any form by anyone of the materials contained
 * herein without the permission of Elsevier is prohibited. Finders are asked to
 * return this document to the following Elsevier location.
 * 
 * Elsevier 360 Park Avenue South, New York, NY 10010-1710
 * 
 * Copyright (c) 2013 by Elsevier, A member of the Reed Elsevier plc group.
 * 
 * All Rights Reserved.
 *****************************************************************************/
