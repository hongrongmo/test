package org.ei.xml;
 
import org.xml.sax.XMLReader;
 
 
 public class SAXParserFactory
 {
 
	public static XMLReader getDefaultSAXParser()
		throws Exception
	{
        	XMLReader parser = new org.apache.xerces.parsers.SAXParser();

       		parser.setFeature( "http://xml.org/sax/features/validation",
                                                false);
        	parser.setFeature( "http://xml.org/sax/features/namespaces",
                                                false);
        	parser.setFeature( "http://apache.org/xml/features/validation/schema",
                                                false);
        	parser.setFeature( "http://apache.org/xml/features/validation/schema-full-checking",
                                                false);
		return parser;		
	}


}            
