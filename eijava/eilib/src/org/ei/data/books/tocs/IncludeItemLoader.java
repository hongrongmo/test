package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
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

public class IncludeItemLoader {
    protected static Log log = LogFactory.getLog(IncludeItemLoader.class);

    public static void main(String[] args) {
        List<IncludeItem> iss = IncludeItemLoader.getIncludeItems(new File("w:/developers/referex/ew/EVF0012/9780120885817/main.xml"));
        Iterator<IncludeItem> piis = iss.iterator();
        int pageIndex = 0;
        try {
            while(piis.hasNext()) {
                IncludeItem pii = piis.next();
                List<PageRange> prs = pii.getRanges();
                Iterator<PageRange> ranges =  prs.iterator();
                log.info("<" + pii.getPii() + ">");
                while(ranges.hasNext()) {
                    PageRange pr = ranges.next();
                    int len = pr.length();
                    List pglblseq = pr.sequence();
                    log.info(pr.toString() + "[" + len + "]");
                    log.info("Page label sequence: " + pglblseq);
    
                    pageIndex += len;
                    log.info("Page index:" + pageIndex); 
                    if(pglblseq.size() != len) {
                        log.error("unmatched sequence !");
                        break;
                    }
                }
            }
        }
        catch(Exception e) {
            log.error("Bad page range!",e);
        }
        log.info(pageIndex);
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"*/ce:include-item\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.IncludeItem\"/>");
        rulesString.append("<set-next-rule methodname=\"add\" paramtype=\"java.lang.Object\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"ce:pii\" propertyname=\"pii\"/>");
        rulesString.append("<call-method-rule pattern=\"ce:pages\" methodname=\"addRange\" paramcount=\"2\" />");
        rulesString.append("<call-param-rule pattern=\"ce:pages/ce:first-page\" paramnumber=\"0\"/>");
        rulesString.append("<call-param-rule pattern=\"ce:pages/ce:last-page\" paramnumber=\"1\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }
    
    public static List<IncludeItem> getIncludeItems(File issue) {
        List<IncludeItem> piis = new ArrayList<IncludeItem>();
        digest(issue, rulesString.toString(), piis);
        return piis;
    }
    
    public static void digest(File issue, String rules, List items) {

        System.setProperty("javax.xml.parser.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

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

    public static String toXML(List<IncludeItem> includeitems) {
        int pageIndex = 0;
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("<piis>");
        try {
            Iterator<IncludeItem> items = includeitems.iterator();
            while(items.hasNext()) {
                IncludeItem pii = items.next();
                List<PageRange> prs = pii.getRanges();
                Iterator<PageRange> ranges =  prs.iterator();
                while(ranges.hasNext()) {
                    PageRange pr = ranges.next();
                    int len = pr.length();
                    
                    strbuf.append("<pii>");
                    strbuf.append("<id>").append(pii.getPii()).append("</id>");
                    strbuf.append("<page>").append(pageIndex + 1).append("</page>");
                    strbuf.append("</pii>");
                    
                    pageIndex += len;
                }
            }
        }
        catch(Exception e) {
            log.error("Bad page range!",e);
        }
        strbuf.append("</piis>");
        return (strbuf.toString());
    }
}
