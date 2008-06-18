package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLLoader {
  public static void digest(File issue, String rules, List items) {

    System.setProperty("javax.xml.parsers.SAXParserFactory",
        "org.apache.xerces.jaxp.SAXParserFactoryImpl");

    try {
        InputSource xmlRules = new InputSource(new StringReader(rules));
        InputSource xmlSource = new InputSource(new FileReader(issue.toString()));

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        Digester digester = new Digester(saxParser);
        
        digester = DigesterLoader.createDigester(xmlRules, digester);
        digester.setEntityResolver(new BookDTDEntityResolver());
        
        // Push empty List onto Digester's Stack
        digester.push( items );
        // Parse the XML document
        digester.parse(xmlSource);
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
  }
}
