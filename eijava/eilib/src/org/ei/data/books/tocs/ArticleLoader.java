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

public class ArticleLoader extends XMLLoader {
    protected static Log log = LogFactory.getLog(ArticleLoader.class);

    public static void main(String[] args) {
        Article article = ArticleLoader.getArticle(new File("W:/Developers/Referex/EW/EVFB010A/09275193/0012000C/07120234/main.xml"));
        log.info(article.toString());
    }
    
    private static final StringBuffer rulesString = new StringBuffer();
    static {
        rulesString.append("<digester-rules>");
        rulesString.append("<pattern value=\"article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Article\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"simple-article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Article\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"simple-head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("</pattern>");
        rulesString.append("<pattern value=\"converted-article\">");
        rulesString.append("<object-create-rule classname=\"org.ei.data.books.tocs.Article\"/>");
        rulesString.append("<bean-property-setter-rule pattern=\"head/ce:title\" propertyname=\"title\"/>");
        rulesString.append("</pattern>");
        rulesString.append("</digester-rules>");
    }
    public static Article getArticle(File issue) {
      Article anarticle = (Article) digest(issue, rulesString.toString());
      return anarticle;
    }
    
//    public static Object digest(File issue, String rules) {
//      
//      Object parseResult = null; 
//      System.setProperty("javax.xml.parsers.SAXParserFactory",
//          "org.apache.xerces.jaxp.SAXParserFactoryImpl");
//      
//      try {
//          InputSource xmlRules = new InputSource(new StringReader(rules));
//          InputSource xmlSource = new InputSource(new FileReader(issue.toString()));
//
//          SAXParserFactory factory = SAXParserFactory.newInstance();
//          SAXParser saxParser = factory.newSAXParser();
//          Digester digester = new Digester(saxParser);
//          
//          digester = DigesterLoader.createDigester(xmlRules, digester);
//          digester.setEntityResolver(new BookDTDEntityResolver());
//          
//          // Parse the XML document
//          parseResult = digester.parse(xmlSource);
//      } catch (SAXException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      } catch (ParserConfigurationException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
//      return parseResult;
//    }
    
}
