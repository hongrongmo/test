package org.ei.system;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EVSaxParser extends DefaultHandler{
	private String tempVal;
	private String abstractLink;
	private String resultCount;
	private String name;


	public EVSaxParser() {
		super();
	}

	public void parseDocument(InputStream stream){

		SAXParserFactory spf = SAXParserFactory.newInstance();

			try {
				SAXParser sp = spf.newSAXParser();
				sp.parse(stream, this);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(name.equals("RESULTCOUNT")){
		this.resultCount = new String(ch,start,length);
		}

	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		name = qName;
		}

	public String getAbstractLink(){
		return abstractLink;
	}

	public String getResultCount() {
		return this.resultCount;
	}
}
