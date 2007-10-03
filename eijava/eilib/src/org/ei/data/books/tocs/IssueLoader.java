package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IssueLoader {
    protected static Log log = LogFactory.getLog(IssueLoader.class);
    
    public static void main(String[] args) {
        Issue iss = IssueLoader.getIssue(new File("w:/developers/referex/EW/EVIBS02C/14701804/0007000C/issue.xml"));
        
        if(iss != null) {
            log.info("ISBN: " + iss.getIsbn().replaceAll("-", ""));
        }
        
    }
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"serial-issue/issue-info\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Issue\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:issn\" propertyname=\"issn\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:isbn\" propertyname=\"isbn\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }
    public static Issue getIssue(File issue) {
        List<Issue> issues = new ArrayList<Issue>();
        Issue anissue = new Issue();
        
        System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");
        try {
            InputSource xmlRules = new InputSource(new StringReader(rulesString.toString()));
            InputSource xmlSource = new InputSource(new FileReader(issue.toString()));

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            Digester digester = new Digester(saxParser);
            
            digester = DigesterLoader.createDigester(xmlRules, digester);
            digester.setEntityResolver(new BookDTDEntityResolver());
            
            // Push empty List onto Digester's Stack
            digester.push( issues );
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
        if(!issues.isEmpty()) {
            anissue = issues.iterator().next();
        }
        
        return anissue;
    }

}
