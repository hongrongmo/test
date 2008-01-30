package org.ei.data.georef.runtime;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.domain.ElementDataMap;
import org.ei.domain.XMLWrapper;
import org.ei.domain.XMLMultiWrapper;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.apache.oro.text.perl.*;

import java.util.regex.*;

public class GRFDocBuilder implements DocumentBuilder
{
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {31});
    public static final String GROUPDELIMITER = new String(new char[] {02});
    public static String GRF_TEXT_COPYRIGHT = "GeoRef, Copyright 2008, American Geological Institute.";
    public static String GRF_HTML_COPYRIGHT = "GeoRef, Copyright &copy; 2008, American Geological Institute.";
    public static String PROVIDER_TEXT = "Ei";
    private static final Key GRF_CLASS_CODES = new Key(Keys.CLASS_CODES, "Classification codes");
    private static final Key GRF_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Index terms");

    public static final Key ILLUSTRATION = new Key("ILLUS", "Illustrations");
    public static final Key ANNOTATION = new Key("ANT", "Annotations");
    public static final Key MAP_SCALE = new Key("MPS", "Map scale");
    public static final Key MAP_TYPE = new Key("MPT", "Map type");
    public static final Key AFFILIATION_OTHER = new Key("OAF", "Other affiliation");
    public static final Key CATEGORY = new Key("CAT", "Category");

    private Database database;

    public DocumentBuilder newInstance(Database database)
    {
        return new GRFDocBuilder(database);
    }

    public GRFDocBuilder()
    {
    }

    public GRFDocBuilder(Database database)
    {
        this.database = database;
    }


    /** This method takes a list of DocID objects and dataFormat
    *  and returns a List of EIDoc Objects based on a particular
    *  dataformat
    *  @ param listOfDocIDs
    *  @ param dataFormat
    *  @ return List --list of EIDoc's
    *  @ exception DocumentBuilderException
    */
    public List buildPage(List listOfDocIDs, String dataFormat) throws DocumentBuilderException
    {
        List l = null;
        try
        {
          if(dataFormat != null)
          {
            DocumentView format = null;
            if(dataFormat.equalsIgnoreCase(Citation.CITATION_FORMAT))
            {
              format = new CitationView();
            }
            else if(dataFormat.equalsIgnoreCase(Abstract.ABSTRACT_FORMAT))
            {
              format = new AbstractView();
            }
            else if(dataFormat.equalsIgnoreCase(FullDoc.FULLDOC_FORMAT))
            {
              format = new DetailedView();
            }
            else if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT))
            {
              format = new RISView();
            }
            else if(dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT))
            {
              format = new XMLCitationView();
            }
            if(format != null)
            {
              l = loadDocument(listOfDocIDs, format);
            }
          }
        }
        catch(Exception e)
        {
          throw new DocumentBuilderException(e);
        }

        return l;
    }

    /*
    *
    */
    private List loadDocument(List listOfDocIDs, DocumentView viewformat)
          throws Exception
    {
        List list = new ArrayList();

        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        Map oidTable = getDocIDTable(listOfDocIDs);
        String INString=buildINString(listOfDocIDs);

        try
        {
          broker=ConnectionBroker.getInstance();
          con=broker.getConnection(DatabaseConfig.SEARCH_POOL);
          stmt = con.createStatement();
          rset=stmt.executeQuery(viewformat.getQuery()+INString);

          while(rset.next())
          {
            DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
            list.add(viewformat.buildDocument(rset,did));
          }
        }
        catch(Exception e)
        {
          e.printStackTrace();
          System.out.println("Exception e " + e.getMessage());
        }
        finally
        {
          if(rset != null)
          {
            try
            {
                rset.close();
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
          }
          if(stmt != null)
          {
            try
            {
                stmt.close();
            }
            catch(Exception sqle)
            {
                sqle.printStackTrace();
            }
          }
          if(con != null)
          {
            try
            {
                broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
            }
            catch(Exception cpe)
            {
                cpe.printStackTrace();
            }
          }
        }

        return list;
    }

   /* This method builds the IN String
    * from list of docId objects.
    * The select query will get the result set in a reverse way
    * So in order to get in correct order we are doing a reverse
    * example of return String--(23,22,1,12...so on);
    * @param listOfDocIDs
    * @return String
    */
    private String buildINString(List listOfDocIDs)
    {
        StringBuffer sQuery = new StringBuffer("(");
        Collections.reverse(listOfDocIDs);
        Iterator itrdocids = listOfDocIDs.iterator();
        while(itrdocids.hasNext())
        {
          DocID doc = (DocID) itrdocids.next();
          sQuery.append("'").append(doc.getDocID()).append("'");
          if(itrdocids.hasNext())
          {
            sQuery.append(",");
          }
        }
        sQuery.append(")");
        return sQuery.toString();
    }

    private Map getDocIDTable(List listOfDocIDs)
    {
        Map h = new Hashtable();

        for(int i=0; i<listOfDocIDs.size(); ++i)
        {
          DocID d = (DocID)listOfDocIDs.get(i);
          h.put(d.getDocID(), d);
        }
        return h;
    }
}