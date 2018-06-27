package org.ei.dataloading.cbnb.loadtime;

import java.io.FileReader;
import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.*;
import java.io.*;

import java.util.zip.*;
import java.util.*;

import org.ei.util.GUID;
//import org.ei.data.*;


public class CBNBBaseTableDriver extends DefaultHandler implements ErrorHandler
{
    Hashtable record = null;
    String tag;
    StringBuffer data;
    private static CBNBBaseTableWriter baseWriter;
    private int loadNumber;
    private String infile;
    private String cdt = null;



    public static void main (String args[]) throws Exception
    {
        int loadN = Integer.parseInt(args[0]);
        String infile=args[1];
        EntityCleaner.writeFile(infile);

        XMLReader xr = XMLReaderFactory.createXMLReader();
        CBNBBaseTableDriver handler = new CBNBBaseTableDriver(loadN,infile);
        xr.setContentHandler(handler);
        xr.setErrorHandler(handler);

        FileReader r = new FileReader(infile+".cln");

        xr.parse(new InputSource(r));




    }


    public CBNBBaseTableDriver(int loadN,String infile)
    {
    super();
    this.loadNumber = loadN;
    this.infile = infile;

    }

    ////////////////////////////////////////////////////////////////////
    // Event handlers.
    ////////////////////////////////////////////////////////////////////


    public void startDocument ()
    {
        try {
            baseWriter = new CBNBBaseTableWriter(infile+"."+loadNumber+".out");
            baseWriter.begin();
            System.out.println("Start document");
        }

        catch (Exception e)
        {
            System.err.println (e);
            System.exit(1);
        }

    }


    public void endDocument ()
    {
        try {
            System.out.println("End document");
            baseWriter.end();
        }

        catch (Exception e)
        {
            System.err.println (e);
            System.exit(1);
        }

    }


    public void startElement (String uri, String name,
                  String qName, Attributes atts)
    {
        if (qName.equalsIgnoreCase("RECORD")){

            record = new Hashtable();
            data= new StringBuffer();
        }
        else{
            data= new StringBuffer();
            tag = qName;

        }

    }


    public void endElement (String uri, String name, String qName)
    {
            if (qName.equalsIgnoreCase("RECORD")) {
                try {
                    record.put("CDT", new StringBuffer(cdt));
                    record.put("GU", new StringBuffer("cbn_"+new GUID().toString()));
                    record.put("LN",new StringBuffer(Integer.toString(loadNumber)));
                    baseWriter.writeRec(record);
                }
                catch (Exception e)
                {
                    System.out.println (e);
                    System.exit(1);
                }

            }
            else if (record != null && data != null)
            {

                if(record.containsKey(tag))
                {
                    if(tag != "S")
                    {
                        StringBuffer value = (StringBuffer)record.get(tag);
                        record.put(tag, value.append(";"+data));
                    }
                    else
                    {
                        StringBuffer value = (StringBuffer)record.get(tag);
                        record.put(tag, value.append(" "+data));
                    }

                }
                else{
                    record.put(tag,data);
                }

            }
            if (qName.equalsIgnoreCase("CDT")) {

                cdt = data.toString();
            }

            data=null;
    }


    public void characters (char ch[], int start, int length)
    {
        if(data != null)
         {

            data.append(ch,start,length);
        }
    }

    /** Warning. */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning]: " + getLocationString(ex) + ": " + ex.getMessage());
    }

    /** Error. */
    public void error(SAXParseException ex) {
        System.err.println("[Error]: " + getLocationString(ex) + ": " + ex.getMessage());
    }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error]: " + getLocationString(ex) + ": " + ex.getMessage());
        throw ex;
    }

    //
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    }


}

