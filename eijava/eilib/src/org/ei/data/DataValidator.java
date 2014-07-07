package org.ei.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;

public class DataValidator
{
  private XMLReader validator;
  private String logfile = "validator.log";
  
  public void setEntityResolver(EntityResolver aresolver) {
    if(this.validator != null)
    {
      this.validator.setEntityResolver(aresolver);
    }
  }

  public void setErrorHandler(ErrorHandler ahandler) {
    if(this.validator != null)
    {
      this.validator.setErrorHandler(ahandler);
    }
  }

  public DataValidator()
  {
    System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    SAXParser saxParser;
    try {
      saxParser = factory.newSAXParser();
      validator = saxParser.getXMLReader();

      this.validator = new org.apache.xerces.parsers.SAXParser();
    } catch (ParserConfigurationException e) {
      System.out.println(e.toString());
    } catch (SAXException e) {
      System.out.println(e.toString());
    }
  }

  public void setLogfile(String filepath)
  {
	  this.logfile = filepath;
  }

  public void validateFile(String filepath)
  {
    FileOutputStream fout = null;
    FileInputStream fin = null;
    PrintWriter log = null;

    try
    {
      fin = new FileInputStream(filepath);
      log = new PrintWriter(new FileWriter(logfile, true));
      log.println((new Date()).toString()+":Validating "+ filepath);
      validator.parse(new InputSource(fin));
      log.println("Done");
    }
    catch(Exception e)
    {
      if(log!=null)
        e.printStackTrace(log);
      else
        e.printStackTrace();
    }
    finally
    {
      if(log != null)
      {
        try
        {
          log.close();
        }
        catch(Exception e1)
        {
          e1.printStackTrace();
        }
      }

      if(fin != null)
      {
        try
        {
          fin.close();
        }
        catch(Exception e1)
        {
          e1.printStackTrace();
        }
      }
    }
  }
}