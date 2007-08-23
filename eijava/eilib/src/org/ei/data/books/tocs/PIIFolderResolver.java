package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class PIIFolderResolver implements URIResolver  {
    protected static Log log = LogFactory.getLog(PIIFolderResolver.class);

    private String issuepath = "";
    public PIIFolderResolver(File xml) throws IOException {
        issuepath = xml.getParent(); 
    }
    
    public Source resolve(String href, String base) throws TransformerException {
        // TODO Auto-generated method stub
        //log.info(" resolve " + href);
        Source asource = null;
        String includeitemfile = "";
        if(href.indexOf("qqDELqq") >= 0) {
            // B978-0-08-044553-3.50007-2
            String path[] = href.split("qqDELqq");
            if(path != null && path.length == 2){
                String prefix = ((String)path[0]).toUpperCase();
                String foldername = path[1].replaceAll("\\p{Punct}", "");
                includeitemfile = prefix + System.getProperty("file.separator") + foldername + System.getProperty("file.separator") + "main.xml";
            }
        }
        else if(href.startsWith("S")) {
            Pattern piiIssuePat = Pattern.compile("(\\d{4}-\\d{4})\\(([^\\)]\\d+)\\)(\\d[^-]+)-(\\w)");
            Matcher piiMatch = piiIssuePat.matcher(href);
            if(piiMatch.find()) {
                includeitemfile = piiMatch.group(2) + piiMatch.group(3) + piiMatch.group(4) + System.getProperty("file.separator") + "main.xml";
            }
        }
        else {
            includeitemfile = href; 
        }
        //
        String xmlFile  = issuepath + System.getProperty("file.separator") + includeitemfile;
        if(!(new File(xmlFile)).exists()) {
            log.error("File not found " + xmlFile);
            return new StreamSource(new StringReader("<empty/>"));
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(xmlFile));

            factory.setNamespaceAware(true);
            saxParser = factory.newSAXParser();
            XMLReader aparser = saxParser.getXMLReader();
            aparser.setEntityResolver(new BookDTDEntityResolver());
            
            asource = new SAXSource(aparser,new InputSource(is));

        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return asource;
    }

}
