/**
 *
 * @author  Engineering Information
 * @author  John A. Moschetto
 * @version $Revision:   1.3  $, $Date:   16 Apr 2013 11:41:56  $
 * @since       EngVillage2
 */
package org.ei.domain;


import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;

public class DocumentBasket
{
    private String sessionID;
    private Hashtable basketTable;
    private int basSize = -1;
    public static int maxSize = 400;
    public static int pageSize = 50;

    /**
    *   Constructor single args
    *   @param sessionId
    *   @exception BasketException
    **/

    public DocumentBasket(String session_ID) throws BasketException
    {
        if(session_ID==null)
        {
                    throw new BasketException(new Exception("SessionID is null"));
        }
        sessionID=session_ID;
    }

    /**
    *   Constructor double args
    *   @param sessionId,basketEntry
    *   @exception BasketException
    **/

    public DocumentBasket(BasketEntry basEntry,String psessionID)
        throws BasketException
        {
        this(psessionID);

        if (basEntry==null) throw
            new BasketException(new Exception("BasketEntry is null"));

        add(basEntry);

    }


    /**Constructor double args
    *@param sessionId,basketEntryList
    *@exception BasketException
    **/

    public DocumentBasket(List basEntryList, String psessionID)
        throws BasketException
    {
        this(psessionID);

        if (basEntryList==null)
            throw new BasketException(new Exception("BasketEntry List is null"));

            addAll(basEntryList);

    }

    public boolean getClearOnNewSearch()
        throws BasketException
    {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        // default setting is true  so set this to true in the case that the
        // select fails, otherwise use the value from the table
        boolean blnResult = true;

        try
        {
            conn = getConnectionFromPool();
            pstmt = conn.prepareStatement("SELECT CLEAR_ON_NEW_SEARCH FROM BASKET_STATE WHERE SESSION_ID=?");
            pstmt.setString(1,sessionID);
            rset = pstmt.executeQuery();
            if(rset.next())
            {
                blnResult = ( rset.getInt("CLEAR_ON_NEW_SEARCH") == 1);
            }
        }
        catch(Exception e)
        {
            throw new BasketException(new Exception("Couldn't Get Basket State!"));
        }
        finally
        {
            if(rset != null)
            {
                try
                {
                    rset.close();
                }
                catch(Exception se)
                {
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception se)
                {
                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return blnResult;
    }



    public boolean updateClearOnNewSearch(boolean blnClearOnSave)
            throws BasketException
    {
        Connection conn = null;
        CallableStatement pstmt = null;
        boolean blnResult = false;
        try
        {
            conn = getConnectionFromPool();
            pstmt = conn.prepareCall("{ call DB_updateClearOnNewSearch(?,?,?)}");
            pstmt.setString(1,this.sessionID);
            pstmt.setInt(2,((blnClearOnSave) ? 1 : 0) );
            pstmt.setLong(3,System.currentTimeMillis());
            pstmt.executeUpdate();
            blnResult = true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new BasketException(new Exception("Couldn't Update Basket State!"));
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception se)
                {
                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return blnResult;

    }

    /**This method gets the documents of a page.It gets the Page size
    *from the properties file.
    * @param is the page that is to be viewed
    * @return BasketPage..a page of BasketEntries Objects
    * @exception BasketException
    */

    public BasketPage pageAt(int pageIndex, String dataFormat)
        throws BasketException
    {

        if(pageIndex<=0)
              throw new BasketException(new Exception("Page Index less then 0"));

        BasketPage basketPage=new BasketPage();

        try
        {
            int reqDocsStartIndex=(pageIndex-1)*DocumentBasket.pageSize;
            List basketEntries=new ArrayList();
            int basketSize=0;
            basketSize=getBasketSize();

            if(basketSize==0)
                    throw new BasketException(new Exception("No Elements In Basket"));

            //If reqDocsStartIndex >= basket size than it returns a empty page

            if(reqDocsStartIndex<basketSize)
            {
                int endIndex=reqDocsStartIndex+DocumentBasket.pageSize;

                    if(endIndex>=basketSize)
                    {
                            endIndex=basketSize;
                    }

                    basketEntries.addAll(getEIDocumentBasketList(reqDocsStartIndex, endIndex, dataFormat));

                    for(int i=0;i<basketEntries.size();i++)
                    {
                            BasketEntry pageBasketEntry=(BasketEntry)basketEntries.get(i);
                                basketPage.add(pageBasketEntry);
                    }
                // jam 9/25/2002
                // set these parameters on the basketPage so that
                // the toXML() can correctly set the DocumentHitIndex on the
                // BasketEntry objects
                basketPage.setPageIndex(pageIndex);
                basketPage.setPageSize(DocumentBasket.pageSize);
            }
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }

        return basketPage;
    }

    /* Method to return all the Documents for this sessionid
     *  @return BasketPage..a page of BasketEntries Objects
     *  @exception BasketException
     */

    public BasketPage getBasketPage(String dataFormat)
        throws BasketException
    {

        if(dataFormat==null)
              throw new BasketException(new Exception("DataFormat cannot be null"));

        BasketPage basketPage=new BasketPage();

        try
        {

            List basketEntries=new ArrayList();
            int startIndex=0;
            int basketSize=0;
            basketSize=getBasketSize();
            if(basketSize==0)
                    throw new BasketException(new Exception("No Elements In Basket"));
            int endIndex=basketSize;

            basketEntries.addAll(getEIDocumentBasketList(startIndex, endIndex, dataFormat));
            for(int i=0;i<basketEntries.size();i++)
            {
                    BasketEntry pageBasketEntry=(BasketEntry)basketEntries.get(i);
                    basketPage.add(pageBasketEntry);
             }
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }

        return basketPage;
    }


    /**
    * Adding a Document to the basket.This takes a basket entry object, extracts the values and store them in the
    * documentBasketTable .This gets the Maximum Sno for that session and adds the next record to the table
    * with the next Sno.This is done in a synchronized block
    *
    * @author       John A. Moschetto
    *
    * @param basEntry   the BasketEntry to be added to the basket
    * @return   <code>true</code> if successful, <code>false</code> otherwise
    * @throws   BasketException
    * @see      #addAll(List )
    * @since        EngVillage 2
    */

    public boolean add(BasketEntry basEntry)
        throws BasketException
    {

        if(basEntry==null) {
            throw new BasketException(new Exception("BasketEntry is null"));
        }
        ArrayList alstBasketEntry = new ArrayList();
        alstBasketEntry.add(basEntry);

        return addAll(alstBasketEntry);
    }

    /**
    * Adding a set Documents to the basket.This takes a basket entry object, extracts the values and store them in the
    * documentBasketTable .This gets the Maximum Sno for that session and adds the next record to the table
    * with the next Sno.It is same as add method.
    *
    * @author       John A. Moschetto
    *
    * @param basketEntryList    a list of BasketEntry objects to be added to the basket.
    * @param basEntry   the Basket Entry which contains the values
    * @return   <code>true</code> if successful, <code>false</code> otherwise
    * @throws BasketException
    * @see          #add(BasketEntry )
    * @since        EngVillage 2
    */

    // jam 9/27/2002
    // added record locking to fix consurrency issue when determining
    // SNO for SINGLE records added in quick succession
    // jam 9/30/2002
    // Changed to PreparedStatement to fix ' changed to "
    // in stored DisplayQuery
    // Adding Multiple records to the Document Basket
    public boolean addAll(List basketEntryList)
            throws BasketException
    {
        if (basketEntryList == null) {
                throw new BasketException(new Exception("BasketEntry List is null!"));
        }
        if (basketEntryList.isEmpty()) {
                throw new BasketException(new Exception("BasketEntry List is Empty!"));
        }

        this.basSize = -1;

        Connection conn = null;
        CallableStatement proc = null;
        boolean blnReturnFlag = false;

        int currentSize = this.getBasketSize();
        int listsize = basketEntryList.size();

        if((currentSize + listsize) > DocumentBasket.maxSize)
        {
            throw new BasketException(new Exception("<DISPLAY>Not enough room for " + listsize + " record(s) in basket. Empty basket entries = " + (maxSize - currentSize)+"</DISPLAY>"));
        }

        try
        {
            conn = getConnectionFromPool();
            proc = (CallableStatement)conn.prepareCall("{ call DB_addAll(?,?,?,?,?,?,?,?)}");
            Iterator itor=basketEntryList.listIterator();
            while(itor.hasNext())
            {
                BasketEntry basEntry=(BasketEntry)itor.next();
                DocID docID=basEntry.getDocID();
                Query query=basEntry.getQuery();

                if(query.getDisplayQuery()==null)
                {
                    throw new Exception("Serach Query cannot be null");
                }
                if(query.getID()==null)
                {
                    throw new Exception("Search Id cannot be null");
                }
                if(query.getRecordCount()==null)
                {
                    throw new Exception("Record Count cannot be null");
                }
                if(docID.getHitIndex()==0)
                {
                    throw new Exception("DocID Handle cannot be null");
                }
                if(docID.getDatabase()==null)
                {
                    throw new Exception("DocID Database cannot be null");
                }

				String did =  docID.getDatabase().getID();
				if(did.equals("upa") ||
				   did.equals("eup"))
				{
					did = "upt";
				}

				String docid = docID.getDocID();

				if(docid.indexOf("ref_ins")<0)
				{
					proc.setString(1,sessionID);
					proc.setString(2,query.getID());
					proc.setInt(3,docID.getHitIndex());
					proc.setString(4,docid);
					proc.setString(5,"NA");
					proc.setString(6,did);
					proc.setString(7,query.getDisplayQuery());
					proc.setString(8,query.getRecordCount());
					proc.executeUpdate();
				}

            }
            blnReturnFlag = true;
        }
        catch(Exception se)
        {
            throw new BasketException(se);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                    proc = null;
                }
                catch(Exception e)
                {
                }
            }
            if(conn != null)
            {
                try
                {
                    releaseConnectionToPool(conn);
                }
                catch(Exception cpe)
                {
                }
            }
        }

        return blnReturnFlag;
    }



    /** Removing  records from the database with the indexes send in the List
        *@param ListOfBasketEntries
        *@exception BasketException
        **/

    public boolean removeSelected(List basEntryList)
            throws BasketException
    {
        Connection conn = null;
        CallableStatement proc = null;
        try
        {
            if (basEntryList==null)
            {
                throw new Exception("List is Null");
            }

            conn=getConnectionFromPool();
            Iterator itor=basEntryList.listIterator();
            BasketEntry basEntry = null;
            DocID docID = null;
            proc = (CallableStatement)conn.prepareCall("{ call DB_removeSelected(?,?,?)}");
            while(itor.hasNext())
            {
                basEntry =(BasketEntry)itor.next();
                docID = basEntry.getDocID();
                proc.setString(1,sessionID);
                proc.setString(2,docID.getDocID());
                proc.setString(3,docID.getDatabase().getID());
                proc.executeUpdate();
            }
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                    proc = null;
                }
                catch(Exception se)
                {

                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return true;
    }


    /**
    *   Removing  records from the database with the indexes send in the List
    *   @exception BasketException
    **/

    public boolean removeAll() throws
        BasketException
    {
        Connection conn = null;
        CallableStatement proc = null;

        try
        {
            conn=getConnectionFromPool();
            proc = conn.prepareCall("{ call DB_removeAll(?)}");
            proc.setString(1,sessionID);
            proc.executeUpdate();
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }
        finally
        {
            if(proc != null)
            {
                try
                {
                    proc.close();
                    proc = null;
                }
                catch(Exception se)
                {
                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return true;
    }


    public int countPages()
        throws BasketException
    {
        int dIndex = getBasketSize();
        if(dIndex == 0)
        {
            return dIndex;
        }


        int pSize = DocumentBasket.pageSize;
        int pageIndex = 0;

        if ( (dIndex%pSize) == 0 )
        {
            // if the remainder is zero the page index will find in the following way
            pageIndex  = (dIndex/pSize);
        }
        else
        {
            // otherwise find the page index in the following way
            pageIndex  = (dIndex/pSize)+1;
        }

        return pageIndex;
    }


    /**
    *   Method which gets the BasketSize
    **/

    public int getBasketSize()
        throws BasketException
    {
        if(this.basSize > -1)
        {
            return this.basSize;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            conn=getConnectionFromPool();
            stmt = conn.createStatement();
            StringBuffer selectBuffer=new StringBuffer();
            selectBuffer.append("select COUNT(SNO)  from BASKET where session_id=").append("'").append(sessionID).append("'");
            rs=stmt.executeQuery(selectBuffer.toString());

            if(rs.next()==true)
            {
                basSize =rs.getInt(1);
            }
            else
            {
                basSize=0;
            }

        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception sq)
                {}
            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception se)
                {}
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }
        return basSize;

    }

    /**An method which returns a boolean  based on the basket documents.This method returns a boolean value.
    This returns true if there are more than one search id ie documents were added to the basket in  multiple seraches
    else flase  if it contains documents from single search **/

    public boolean containsMultipleSearchIds()
            throws BasketException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean flag=false;
        int i=0;
        try
        {
            conn=getConnectionFromPool();
            stmt = conn.createStatement();
            StringBuffer selectBuffer=new StringBuffer();
            selectBuffer.append("select distinct search_id   from BASKET where session_id=").append("'").append(sessionID).append("'");

            rs=stmt.executeQuery(selectBuffer.toString());

            while(rs.next())
            {
                i=i+1;
            }

            if(i>1)
            {
                flag=true;
            }
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception sq)
                {}
            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception se)
                {}
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return flag;
    }


/**Gives the list of distinct databaes contained for this sessionId **/
    public List getListOfDatabases()
        throws BasketException
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List listOfDb=new ArrayList();
        try
        {
            conn=getConnectionFromPool();
            stmt = conn.createStatement();
            StringBuffer selectBuffer=new StringBuffer();
            selectBuffer.append("select distinct di_database from BASKET where session_id=").append("'").append(sessionID).append("'");

            rs=stmt.executeQuery(selectBuffer.toString());
            String dbName=null;
            while(rs.next())
            {
                dbName=rs.getString("DI_DATABASE");
                listOfDb.add(dbName);
            }
        }
        catch(Exception e)
        {
            throw new BasketException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception sq)
                {
                }
            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception se)
                {
                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return listOfDb;
    }


//**************************Private methods*********************************************//


    //Gets the Documents in the Document Basket
    private Set getEIDocumentBasketList(int startIndex,
                                        int endIndex,
                                        String dataFormat)
        throws Exception
    {
		TreeSet basSet = new TreeSet();
        List basketDocIDs = buildBasketDocIDs(startIndex,endIndex);
        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
    	List docs = builder.buildPage(basketDocIDs, dataFormat);
    	Iterator docItor = docs.iterator();
		while(docItor.hasNext())
		{
			Object obj = docItor.next();
			EIDoc eiDoc = (EIDoc)obj;
			DocID compareDocID = eiDoc.getDocID();
			String compDocIDHashCode = Integer.toString(compareDocID.hashCode());
			BasketEntry bEntry = (BasketEntry)basketTable.get(compDocIDHashCode);
			bEntry.setEIDoc(eiDoc);
			basSet.add(bEntry);
		}
        return basSet;
    }


    /**
    *   Efficient method for adding docIDs to folder.
    **/

    public List getAllDocIDs()
            throws BasketException
    {       List basketDocIDs = null;

            int startIndex=0;
            int basketSize=0;
            basketSize=getBasketSize();
            if(basketSize==0)
                    throw new BasketException(new Exception("No Elements In Basket"));
            int endIndex=basketSize;
            try
            {

                basketDocIDs = buildBasketDocIDs(startIndex,endIndex);
            }
            catch(Exception e)
            {
                throw new BasketException(e);
            }

            return basketDocIDs;
    }




    /** The private method which gets the connection*/

    private Connection getConnectionFromPool()
        throws Exception
    {
            ConnectionBroker db = ConnectionBroker.getInstance();
            return db.getConnection(DatabaseConfig.SESSION_POOL);
    }

    /** The private method which releases the connection*/
    private void releaseConnectionToPool(Connection conn)
    {
        try
        {
            ConnectionBroker db = ConnectionBroker.getInstance();
            db.replaceConnection(conn,DatabaseConfig.SESSION_POOL);
        }
        catch(ConnectionPoolException cpe)
        {
        }
    }



    /*
    *   private method which builds BasketEntries
    */

    private List buildBasketDocIDs(int startIndex,
                                   int endIndex)
        throws Exception
    {
        if(startIndex<0)
        {
            throw new Exception("startIndex cannot be less than zero");
        }

        if(endIndex < 0)
        {
            throw new Exception("End Index cannot be less than zero");
        }

        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        basketTable = new Hashtable();
        List basketDocIDs = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            conn=getConnectionFromPool();
            stmt = conn.createStatement();
            StringBuffer getBuffer=new StringBuffer();
            getBuffer.append("select * from BASKET where SESSION_ID='")
            .append(sessionID)
            .append("'")
            .append("ORDER BY SNO ");

            rs=stmt.executeQuery(getBuffer.toString());

            int basIndex=0;
            int Di_handle=0;
            //DocId changed to string
            String Di_docId="";
            int Q_recordCount=0;
            String Di_DocType="";
            String Di_Database="";
            String Q_searchQuery="";
            DocID docID=null;

            int pageSize=endIndex-startIndex;

            for(int i=1;i<=startIndex;i++)
            {
                rs.next();
            }

            int index =0;
            while((index++<pageSize)&& rs.next())
            {
                basIndex=rs.getInt("SNO");
                Di_handle=rs.getInt("DI_HANDLE");
                Di_docId=rs.getString("DI_DOCID");
                Di_DocType=rs.getString("DI_DOCTYPE");
                Di_Database=rs.getString("DI_DATABASE");
                Q_searchQuery=rs.getString("Q_SEARCHQUERY");
                Q_recordCount=rs.getInt("Q_RECORDCOUNT");

                docID=new DocID(Di_handle,
                                Di_docId,
                                databaseConfig.getDatabase(Di_Database));


                Query query=new Query();

                query.setDisplayQuery(Q_searchQuery);

                query.setRecordCount(Integer.toString(Q_recordCount));
                BasketEntry bEntry = new BasketEntry();
                bEntry.setQuery(query);
                bEntry.setDocumentBasketHitIndex(basIndex);
                bEntry.setDocID(docID);
                basketTable.put(Integer.toString(docID.hashCode()), bEntry);
                basketDocIDs.add(docID);
            }
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception sq)
                {
                }
            }

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception se)
                {
                }
            }

            if(conn != null)
            {
                releaseConnectionToPool(conn);
            }
        }

        return basketDocIDs;
    }
}


