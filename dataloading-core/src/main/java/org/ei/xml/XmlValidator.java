package org.ei.xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ei.dataloading.DataValidator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlValidator
{
	public static void main(String args[]) throws Exception 
	{
		String xmlfile = null;
		String xsdfile = null;
		XmlValidator v = new XmlValidator();
		
		if(args.length>0)
		{
			xmlfile = args[0];
		}
		if(args.length>1)
		{
			xsdfile = args[1];
		}
		
		if(xsdfile!=null && xsdfile.endsWith("xsd"))
		{
			v.validatedXml(xmlfile,xsdfile);
		}
		else if(xmlfile!=null)
		{
			DataValidator dv = new DataValidator();
			dv.setLogfile(xmlfile + ".validator.log");
			dv.validateFile(xmlfile);
		}
		else
		{
			System.out.println("not enough parameter");
			System.exit(1);
		}
		
	}
	
	private boolean validatedXml(String filename,String xsdFileName) throws Exception
	{
		// parse an XML document into a DOM tree
		 javax.xml.parsers.DocumentBuilder parser =  javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 Document document = parser.parse(new File(filename));

		// create a SchemaFactory capable of understanding WXS schemas
		//SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		 SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		//Source schemaFile = new StreamSource(new File("ani512.xsd"));
		Source schemaFile = new StreamSource(new File(xsdFileName));
		Schema schema = factory.newSchema(schemaFile);

		// create a Validator instance, which can be used to validate an instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
		   //validator.validate(new DOMSource(document));
			validator.validate(new StreamSource(new File(filename)));
			
		    //System.out.println(filename+" is valid!");
		    return true;
		} catch (SAXException e) {
			System.out.println(filename+" is invalid!");
			e.printStackTrace();
			return false;
		    // instance document is invalid!
		}
		
	}
}