package org.ei.domain;

import java.io.StringReader;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class SaxHistoryParser
	extends DefaultHandler {

	Stack tagStack = null;
	Hashtable ht = null;

	public SaxHistoryParser() {
		super();
		tagStack = new Stack();
		ht = new Hashtable();
	}

	public Hashtable read(String inXML)
		throws SaxHistoryParseException {
System.out.println("inXML::"+inXML);
		InputSource sourceXML = new InputSource(new StringReader(inXML));

		try {

			XMLReader xmlReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(this);

			xmlReader.parse(sourceXML);
			
			return ht;

		} catch(Exception e) {
			throw new SaxHistoryParseException(e);
		}
	}

	// Event Handlers

	public void startElement (String uri, String name, String qName, Attributes atts) {
		if ("".equals (uri)) {
			tagStack.push(qName);
		} else {
			tagStack.push(uri + ":" + name);
		}
		if (atts.getLength() > 0) {
			StringBuffer tagBuf = new StringBuffer();
			for (int j=1; j < tagStack.size()-1; j++) {
				tagBuf.append((String)tagStack.get(j)+"_");
			}
			tagBuf.append((String)tagStack.peek());
			for (int i = 0; i < atts.getLength(); i++) {
				ht.put(tagBuf.toString()+"@"+atts.getLocalName(i),atts.getValue(i));
				// System.out.println("<"+tagBuf.toString()+"@"+atts.getLocalName(i)+">"+atts.getValue(i));
			}
		}
	}

	public void endElement (String uri, String name, String qName) {
		String endTag = (String)tagStack.pop();
	}

	public void characters (char ch[], int start, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = start; i < start + length; i++) {
			sb.append(ch[i]);
		}
		StringBuffer tagBuf = new StringBuffer();
		for (int i=1; i < tagStack.size()-1; i++) {
			tagBuf.append((String)tagStack.get(i)+"_");
		}
		tagBuf.append((String)tagStack.peek());
		ht.put(tagBuf.toString(),sb.toString());
		// System.out.println("<"+tagBuf.toString()+">"+sb.toString());
	}

}