package org.ei.domain;


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
					ArrayList l = (ArrayList)listTable.get(key);
					Database database = dConfig.getDatabase(key);
					DocumentBuilder builder = database.newBuilderInstance();
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