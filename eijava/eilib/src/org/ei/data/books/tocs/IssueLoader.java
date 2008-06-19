package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IssueLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(IssueLoader.class);
    
    public static void main(String[] args) {
        Issue iss = IssueLoader.getIssue(new File("W:/Developers/Referex/EW/EVIBS02C/14701804/0007000C/issue.xml"));
        if(iss != null)
        {
          log.info(iss.toString());
        }
        else {
          log.error("parse result is null!");
        }
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"serial-issue\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Issue\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"issue-info/ce:issn\" propertyname=\"issn\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"issue-info/ce:isbn\" propertyname=\"isbn\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"*/ce:include-item\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\" />");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("<set-next-rule methodname=\"addIncludeItem\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }

    public static Issue getIssue(File issue) {
      Issue anissue = (Issue) digest(issue, rulesString.toString());
      return anissue;
  }

    public static Object digest(File issue, String rules) {
      
      Object parseResult = null; 
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
