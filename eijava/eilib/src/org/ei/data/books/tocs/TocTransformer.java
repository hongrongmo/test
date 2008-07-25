package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TocTransformer  {
  protected static Log log = LogFactory.getLog(TocTransformer.class);

  /**
   * Performs an XSLT transformation sending the results
   * to the provided result interface
   */
  public boolean runTransform(File xmlFile, File xsltFile, Result tresult) {
      System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

      boolean result = false;
      // JAXP reads data using the Source interface
      Source xmlSource = new StreamSource(xmlFile);
      Source xsltSource = new StreamSource(xsltFile);
      // replace the xmlsource with a SAX source so we can resolve the entities
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser;

      try {
          // the factory pattern supports different XSLT processors
          TransformerFactory transFact = TransformerFactory.newInstance();
          transFact.setURIResolver(new PIIFolderResolver(xmlFile));
          Transformer trans = transFact.newTransformer(xsltSource);

          InputStreamReader is = new InputStreamReader(new FileInputStream(xmlFile));
          factory.setNamespaceAware(true);
          saxParser = factory.newSAXParser();
          XMLReader aparser = saxParser.getXMLReader();
          aparser.setEntityResolver(new BookDTDEntityResolver());
          xmlSource = new SAXSource(aparser,new InputSource(is));

          trans.transform(xmlSource, tresult);
          result = true;
          trans = null;
      } catch (IOException e) {
          log.error(e.toString());
      } catch (SAXException e) {
          log.error(e.toString());
      } catch (ParserConfigurationException e) {
          log.error(e.toString());
      } catch (TransformerConfigurationException e) {
          log.error(e.toString());
      } catch (TransformerException e) {
          log.error(e.toString());
      }
      finally {

      }
      return result;

  }
}
