package org.ei.service.cars.rest.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;

/**
 * Utility for generating XML documents.  Unless indicated by the escape output
 * setting, element content entity values will be escaped.
 */
public class XMLFormatter {
    /** Internal buffer for storing the generated XML. */
    private StringBuffer m_buffer;
    /** XML Header declaration Constant */
    public static final String XML_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    /** Empty String Constant */
    public static final String EMPTY_STRING = "";
    /** Open element tag Constant */
    private static final char OPEN_TAG = '<';
    /** Close element tag Constant */
    private static final char CLOSE_TAG = '>';
    /** Forward Slash Constant */
    private static final char SLASH = '/';
    /** Equal Sign Constant */
    private static final char EQUAL_SIGN = '=';
    /** Double Quote Constant */
    private static final char DOUBLE_QUOTE = '"';
    /** Space Constant */
    private static final char SPACE = ' ';
    /** Disable output escaping */
    private boolean m_disableOutputEscaping;

    /**
     * Constructor
     */
    public XMLFormatter() {
        this(false);
    }

    /**
     * Constructor
     * 
     * @param suppressHeader If true, do not include the XML header.
     */
    public XMLFormatter(boolean suppressHeader) {
        if (!suppressHeader) {
            m_buffer = new StringBuffer(XML_HEADER);
        }
    }
    /**
     * Constructor
     * 
     * @param initialCapacity Set the size of the internal StringBuffer
     */
    public XMLFormatter(int initialCapacity) {
        this(initialCapacity, false);
    }

    /**
     * Constructor
     * 
     * @param initialCapacity Set the size of the internal StringBuffer.
     * @param suppressHeader If true, do not include the XML header.
     */
    public XMLFormatter(int initialCapacity, boolean suppressHeader) {
        m_buffer = new StringBuffer(initialCapacity);
        if (!suppressHeader) {
            m_buffer.append(XML_HEADER);
        }
    }

    /**
     * Return the String reference to the internal StringBuffer that contains
     * the serialized XML.
     * 
     * @return String The reference to the internal buffer.
     */
    public String getXml() {
        return m_buffer.toString();
    }

    /**
     * Append text directly to the XML document.
     * WARNING:  The text will not be validated for accuracy.
     * 
     * @param text added to the XML document
     */
    public void append(String text) {
        m_buffer.append(text);
    }

    /**
     * Open an element with no attributes.
     * @param tag Name of the element.
     */
    public void open(String tag) {
        this.open(tag, null);
    }

    /**
     * Open an element with attributes and add the contents to the internal
     * buffer.
     * @param tag Name of the element.
     * @param attributes Attributes to include with the element.
     */
    public void open(String tag, Map<?, ?> attributes) {
        m_buffer.append(OPEN_TAG)
            .append(tag)
            .append(processAttributes(attributes))
            .append(CLOSE_TAG);
    }

    /**
     * Close an element.
     * 
     * @param tag Name of the element.
     */
    public void close(String tag) {
        m_buffer.append(OPEN_TAG).append(SLASH).append(tag).append(CLOSE_TAG);
    }

    /**
     * Create an empty element.
     * @param tag Name of the element.
     */
    public void emptyElement(String tag) {
        this.format(tag, null, null);
    }

    /**
     * Create an empty element with attributes.
     * @param tag Name of the element.
     * @param attributes Attributes to include with the element.
     */
    public void emptyElement(String tag, Map<?, ?> attributes) {
        this.format(tag, null, attributes);
    }

    /**
     * Create an element.
     * @param tag Name of the element.
     * @param data Text of the element.
     */
    public void format(String tag, String data) {
        if (GenericValidator.isBlankOrNull(data)) {
            this.emptyElement(tag);
        }
        this.format(tag, data, null);
    }

    /**
     * Create an element with attributes.
     * @param tag Name of the element.
     * @param data Text of the element.
     * @param attributes Attributes to include with the element.
     */
    public void format(String tag, String data, Map<?, ?> attributes) {
        if (GenericValidator.isBlankOrNull(tag)) {
            return;
        }
        m_buffer.append(XMLFormatter.OPEN_TAG)
            .append(tag)
            .append(processAttributes(attributes));
        if (GenericValidator.isBlankOrNull(data)) {
            m_buffer.append(XMLFormatter.SLASH)
                .append(XMLFormatter.CLOSE_TAG);
        }
        else {
            m_buffer.append(XMLFormatter.CLOSE_TAG);
            if (this.isOutputEscapingDisabled()) {
                m_buffer.append(data);
            }
            else {
                m_buffer.append(escapeString(data));
            }
            m_buffer.append(XMLFormatter.OPEN_TAG)
                .append(XMLFormatter.SLASH)
                .append(tag)
                .append(XMLFormatter.CLOSE_TAG);
        }
    }

    /**
     * Wrap element text as CDATA.
     * @param data The text to enclose in CDATA.
     */
    public void formatCDATA(String data) {
        if (GenericValidator.isBlankOrNull(data)) {
            return;
        }
        m_buffer.append(OPEN_TAG).append('!').append('[').append("CDATA")
            .append('[').append(data).append(']').append(']')
            .append(CLOSE_TAG);
    }

    /**
     * Create a name/value string from an attributes map.
     * @param attributes The name/value pairs to process.
     * @return String The formatted attributes.
     */
    private String processAttributes(Map<?, ?> attributes) {
        if (attributes == null) {
            return EMPTY_STRING;
        }
        StringBuffer buffer = new StringBuffer(50);
        Set<?> keys = attributes.keySet();
        Iterator<?> itr = keys.iterator();
        String key = null;
        String value = null;
        while (itr.hasNext()) {
            key = (String) itr.next();
            value = (String) attributes.get(key);
            
            //it's possible that value could be null
            if (value != null)
            {
                if (!this.isOutputEscapingDisabled()) {
                    value = escapeString(value);
                }
                 
                buffer.append(SPACE).append(key)
                    .append(EQUAL_SIGN).append(DOUBLE_QUOTE)
                    .append(value).append(DOUBLE_QUOTE);
            }
        }
        return buffer.toString();
    }

    /**
     * method to encode a string for xml so if you sent in "<hello>" you would
     * get back "&lt;hello&gt;"
     * @param s the string to encode
     * @return the encoded string
     */
    public static String escapeString(String s) {
        StringBuffer result = null;
        for (int i = 0, max = s.length(), delta = 0; i < max; i++) {
            char c = s.charAt(i);
            String replacement = null;
    
            if (c == '&') {
                replacement = "&amp;";
            } else if (c == '<') {
                replacement = "&lt;";
            } else if (c == '\r') {
                replacement = "&#13;";
            } else if (c == '>') {
                replacement = "&gt;";
            } else if (c == '"') {
                replacement = "&quot;";
            } else if (c == '\'') {
                replacement = "&apos;";
            }
    
            if (replacement != null) {
                if (result == null) {
                    result = new StringBuffer(s);
                }
                result.replace(i + delta, i + delta + 1, replacement);
                delta += (replacement.length() - 1);
            }
        }
        if (result == null) {
            return s;
        }
        return result.toString();
    }

    /**
     * Indicates if element text should have entity values escaped.
     * 
     * @return boolean TRUE is output escaping is disabled.
     */
    public boolean isOutputEscapingDisabled() {
        return m_disableOutputEscaping;
    }

    /**
     * Set the disable output escaping setting.
     * 
     * @param b Set to TRUE if output escaping is to be disabled.
     */
    public void setDisableOutputEscaping(boolean b) {
        m_disableOutputEscaping = b;
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
