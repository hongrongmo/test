package org.ei.dataloading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class ReferexCombinedXMLWriter
    extends CombinedXMLWriter
{
    private String filepath = null;
    private PrintWriter outwrtr = null;
    private static final String EXTRACT = "extract";
    private static final String OUTPUT = EXTRACT + System.getProperty("file.separator") + "out";
    private static final String VALIDATED = EXTRACT + System.getProperty("file.separator") + "validated";

    public void setFilepath(String path) {
      filepath = path;
    }
    public String getFilepath() {
      return filepath;
    }


    public ReferexCombinedXMLWriter(int recsPerfile,
                             int numberID,
                             String databaseID)

    {
        super(recsPerfile, numberID, databaseID);
    }

    public void begin()
        throws Exception
    {
        if(!(new File(EXTRACT)).exists())
        {
          // Create a directory and all ancestor directories
          boolean success = ((new File(OUTPUT)).mkdirs() && (new File(VALIDATED)).mkdirs());
          if (!success) {
            throw new Exception("Cannot create output directory.");
          }
        }

        long time = System.currentTimeMillis();
        setFilepath("." + System.getProperty("file.separator") + OUTPUT + System.getProperty("file.separator") + getDatabaseID() + Long.toString(time) + ".xml");

        outwrtr = new PrintWriter(new BufferedWriter(new FileWriter(filepath)));

        setWriter(outwrtr);
        setIsOpen(true);
        setCurRecNum(0);

        outwrtr.println("<?xml version=\"1.0\"?>");
        outwrtr.println("<!DOCTYPE ROWSET PUBLIC \"EVROWSET\" \"http://usage.elsevier.com:9000/EVROWSET.dtd\">");
        outwrtr.println("<ROWSET>");
    }


    public void end()
        throws Exception
    {
        if (outwrtr != null)
        {
            outwrtr.println("</ROWSET>");
            outwrtr.close();
            setIsOpen(false);

            String part1 = Long.toString(System.currentTimeMillis());
            String part2 = "_updat_rowset_";
            String part3 = Integer.toString(getCurRecNum());
            String part4 = "_" + getDatabaseID() + "-";
            String part5 = Integer.toString(getNumberID());
            String part6 = ".xml";
            StringBuffer finalFileName = new StringBuffer();
            finalFileName.append("." + System.getProperty("file.separator") + VALIDATED + System.getProperty("file.separator"));
            finalFileName.append(part1);
            finalFileName.append(part2);
            finalFileName.append(part3);
            finalFileName.append(part4);
            finalFileName.append(part5);
            finalFileName.append(part6);
            System.out.println(" finalFileName :: " + finalFileName.toString());

            File f = new File(getFilepath());
            f.renameTo(new File(finalFileName.toString()));

            LocalValidator d = new LocalValidator();
            d.validateFile(finalFileName.toString());

        }
    }

  private class LocalValidator {

    public void validateFile(String strFilename) {

  		SAXParserFactory factory = SAXParserFactory.newInstance();
  		factory.setValidating(true);
  		SAXParser saxParser;
  		try {
  			InputStreamReader is = new InputStreamReader(new FileInputStream(strFilename));

  			System.out.println("InputStreamReader encoding is " + is.getEncoding());
  			saxParser = factory.newSAXParser();
  			XMLReader aparser = saxParser.getXMLReader();
        aparser.setEntityResolver(new LocalDTDEntityResolver());

  			aparser.setErrorHandler(new LocalErrorHandler());
  			aparser.parse(new InputSource(is));
  			System.out.println("Validated.");

  		} catch (ParserConfigurationException e) {
  			System.out.println(e.toString());
  		} catch (SAXException e) {
  			System.out.println(e.toString());
  		} catch (IOException e) {
  			System.out.println(e.toString());
  		}
	  }
	}

	private class LocalErrorHandler implements ErrorHandler {
		public void warning(SAXParseException e) throws SAXException {
			System.out.println("Warning: ");
			printInfo(e);
		}

		public void error(SAXParseException e) throws SAXException {
			System.out.println("Error: ");
			printInfo(e);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			System.out.println("Fatal error: ");
			printInfo(e);
		}

		private void printInfo(SAXParseException e) {
			System.out.println("   Public ID: " + e.getPublicId());
			System.out.println("   System ID: " + e.getSystemId());
			System.out.println("   Line number: " + e.getLineNumber());
			System.out.println("   Column number: " + e.getColumnNumber());
			System.out.println("   Message: " + e.getMessage());
		}
	}

  private class LocalDTDEntityResolver implements EntityResolver {

    public LocalDTDEntityResolver() {
        // TODO Auto-generated constructor stub
    }
    public InputSource resolveEntity(String publicId, String systemId) {
        InputStreamReader is = null;
        try {
            String dtdfile = new File(systemId).getName();
            // log.info("<!--" + dtdfile + " == " + systemId + "-->");
            is = new InputStreamReader(ReferexCombinedXMLWriter.class.getResourceAsStream(dtdfile));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new InputSource(is);
    }

}
}
