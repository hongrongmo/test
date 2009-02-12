package org.ei.domain;
import org.ei.data.bd.runtime.*;
import org.ei.data.bd.*;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public class MultiDatabaseDocBuilder
	implements DocumentBuilder
{

	public DocumentBuilder newInstance(Database database)
	{
		return new MultiDatabaseDocBuilder();
	}

	public MultiDatabaseDocBuilder()
	{
	}

	public List buildPage(List listOfDocIDs,
						  String dataFormat)
		throws DocumentBuilderException
	{
			List finishedList = new ArrayList(listOfDocIDs.size());			
			try
			{
			    BdDatabase bdDatabase = new BdDatabase();
			    boolean isBdDatabase = false;
			    
				Hashtable listTable = new Hashtable();
				for(int i=0; i<listOfDocIDs.size();++i)
				{
					DocID id = (DocID)listOfDocIDs.get(i);
					Database database = id.getDatabase();
					String databaseID = database.getID();
					if(databaseID.length()> 3)
					{
						databaseID = databaseID.substring(0,3);
					}
					
					if(bdDatabase.isBdDatabase(databaseID))
					{
					    isBdDatabase = true;					   
					    databaseID = bdDatabase.getID();
					    System.out.println("isBdDatabase ::"+databaseID);
					}

					if(listTable.containsKey(databaseID))
					{
						ArrayList al = (ArrayList)listTable.get(databaseID);
						al.add(id);
					}
					else
					{
						ArrayList al = new ArrayList();
						al.add(id);
						listTable.put(databaseID, al);
					}
				}

				DatabaseConfig dConfig = DatabaseConfig.getInstance();

				Enumeration en = listTable.keys();
				Hashtable builtDocsTable = new Hashtable();

				while(en.hasMoreElements())
				{
					String key = (String)en.nextElement();
					System.out.println("en.nextElement::"+key);
					ArrayList l = (ArrayList)listTable.get(key);
					Database database = null;
					DocumentBuilder builder = null;
										
					//if(isBdDatabase)
					if(key.equalsIgnoreCase("bd"))
					{					   
					    builder = new BDDocBuilder();
					}
					else
					{ 
					    database = dConfig.getDatabase(key);
					    builder = database.newBuilderInstance(); // ??
					}
					
					for(int m=0; m<l.size(); ++m)
					{
					    DocID doc = (DocID)l.get(m);
						System.out.print(" -- " +  (doc.getDocID()));
					}
					
					List bList = builder.buildPage(l, dataFormat);

					for(int k=0; k<bList.size(); ++k)
					{
						EIDoc doc = (EIDoc)bList.get(k);
						builtDocsTable.put((doc.getDocID()).getDocID(),
											doc);
					}
				}

				for(int z=0; z<listOfDocIDs.size(); ++z)
				{
					DocID dID = (DocID)listOfDocIDs.get(z);

					finishedList.add(builtDocsTable.get(dID.getDocID()));
				}
			}
			catch(Exception e)
			{
				throw new DocumentBuilderException(e);
			}

			return finishedList;
	}



}