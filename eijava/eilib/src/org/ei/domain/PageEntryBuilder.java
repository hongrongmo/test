package org.ei.domain;

// general imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.data.inspec.runtime.InspecWrapper;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /** This class builds PageEntryList from the List of Documents to display on the page .
     */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class PageEntryBuilder
{


    private String sessionID;


    /**
    *   Constructor which takes session id as argument.
    */


    public PageEntryBuilder(String sSessionID)
    {
        sessionID=sSessionID;

    }


    /**
    * This method takes list of documents and returns a List of PageEntry's
    *
    * @param : List of EIDocuments.
    * @exception :
    * @return List of PageEntry's
    */

    public List buildPageEntryList(List docList)
            throws PageEntryBuilderException
    {
        int handle = 0;
        String docId = "";
        String database = null;
        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        List pageEntryList = new ArrayList();
        Hashtable docTable = new Hashtable();

		int docLength= docList.size();
		for(int i = 0; i < docLength ; i++ )
		{
			EIDoc eid = null;
			if(docList.get(i) instanceof InspecWrapper)
			{
				eid = ((InspecWrapper)docList.get(i)).eiDoc;
			}
			else
			{
				eid = (EIDoc)docList.get(i);
			}
			if(eid != null)
			{
				docTable.put(eid.getDocID(), eid);
			}
		}

        try
        {
            String query = buildSelectedQuery(docTable);
            //System.out.println("SQL:"+query);
            DatabaseConfig  databaseConfig = DatabaseConfig.getInstance();
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);

            stmt = con.createStatement();

            rs = stmt.executeQuery(query);
            boolean isSelected = false;
            boolean isDup = false;
            String dupIds = null;
            List selectedList= new ArrayList();
            Hashtable selectedTable = new Hashtable();
            while(rs.next())
            {
                docId  = rs.getString("DI_DOCID");
                selectedTable.put(docId, "Y");
            }

            docLength = docList.size();
            for(int i = 0; i < docLength ; i++ )
            {
				EIDoc eid1 = null;
				if(docList.get(i) instanceof InspecWrapper)
				{
					eid1 = ((InspecWrapper)docList.get(i)).eiDoc;
				}
				else
				{
					eid1 = (EIDoc)docList.get(i);
				}

                if(eid1 != null)
                {

                    DocID docID = eid1.getDocID();
                    isDup = docID.isDup();
                    dupIds = docID.getDupIds();

                    isSelected = selectedTable.containsKey(docID.getDocID());
                    if(isSelected)
                    {
//						System.out.println("IS Selected:"+ docID.getDocID());
					}
					else
					{
//						System.out.println("Not Selected:"+ docID.getDocID());
					}

                    PageEntry pageEntry = new PageEntry();
                    pageEntry.setDoc(eid1);
                    pageEntry.setSelected(isSelected);
                    pageEntry.setIsDup(isDup);
                    pageEntry.setDupIds(dupIds);
                    pageEntryList.add(pageEntry);
                }
            }
        }
        catch(Exception e)
        {
            throw new PageEntryBuilderException(e);
        }
        finally
        {

            if( rs != null )
            {
                try
                {
                    rs.close();
                }
                catch(Exception sqle)
                {

                }
            }

            if( stmt != null )
            {
                try
                {
                    stmt.close();
                }
                catch(Exception sqle)
                {


                }
            }

            if( con != null )
            {
                try
                {
                    broker.replaceConnection(con,
                                 DatabaseConfig.SESSION_POOL);
                }
                catch(Exception cpe)
                {

                }
            }
        }
        return pageEntryList;
    }

        /**
        * This method builds the query from the list of DocID objects.
        * @param : hash table contains DocID's.
        * @return : string.
        */

    private String buildSelectedQuery(Hashtable docTable)
    {

        StringBuffer buf = new StringBuffer();
        buf.append("Select DI_DOCID from BASKET ");
        buf.append(" where ");
        buf.append("DI_DOCID in ");
        buf.append("(");

        for (Enumeration e = docTable.keys() ; e.hasMoreElements() ;)
        {
            DocID docID = (DocID) e.nextElement();
            buf.append("'");
            buf.append(docID.getDocID()).append("'");
            buf.append("");
            if(e.hasMoreElements())
            {
                buf.append(",");
            }

        }

        buf.append(") and SESSION_ID ='")
        .append(sessionID)
        .append("'");
        //System.out.println("From PageEntryBuilder.java : the query is *************** "+ buf.toString());
        return buf.toString();
    }
}




