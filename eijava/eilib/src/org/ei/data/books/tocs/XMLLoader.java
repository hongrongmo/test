package org.ei.data.books.tocs;


import org.apache.xml.resolver.tools.CatalogResolver;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLLoader {
  
  public static Object digest(File issue, String rules) {
    return digest(issue, new StringReader(rules));
  }
  public static Object digest(File issue, File rules) throws FileNotFoundException {
    return digest(issue, new FileReader(rules));
  }
  
  public static Object digest(File issue, URL rules) throws IOException {
    return digest(issue, new InputStreamReader(rules.openStream()));
  }   
  public static Object digest(File issue, Reader rules) {
    
    Object parseResult = null; 
    System.setProperty("javax.xml.parsers.SAXParserFactory",
        "org.apache.xerces.jaxp.SAXParserFactoryImpl");
    
    try {
        InputSource xmlRules = new InputSource(rules);
        //InputSource xmlSource = new InputSource(new FileReader(issue.toString()));

        InputStreamReader is = new InputStreamReader(new FileInputStream(issue),Charset.forName("UTF-8"));
        BufferedReader in = new BufferedReader(is);
        InputSource xmlSource = new InputSource(in);
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        Digester digester = new Digester(saxParser);
        
        digester = DigesterLoader.createDigester(xmlRules, digester);

        // use the xml-commons catalog resolver
        CatalogResolver resolver = new CatalogResolver();
        digester.setEntityResolver(resolver);

        // Parse the XML document
        parseResult = digester.parse(xmlSource);
    } catch (SAXException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ParserConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return parseResult;
  }
}
