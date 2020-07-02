package org.ei.tags;

import org.ei.connectionpool.ConnectionBroker;

public class TagKeeper 
{
//	private static final String setURL = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI";
//
//	private static final String setUserName = "ap_ev_session";
//
//	private static final String setPassword = "";

	public static void main(String args[]) throws Exception 
	{
		String file ="pools.xml";
		if (args[0] != null)
		{
			file = args[0];
		}
		
		try 
		{
			openConnectionPool(file);
			TagBroker broker = new TagBroker();
			broker.houseKeeping();

		} 
		finally 
		{
			closeConnectionPool(file);
		}
	}

	public static void openConnectionPool(String file) 
								throws Exception 
	{
		
		ConnectionBroker.getInstance(file);
	}

	public static void closeConnectionPool(String file) 
								throws Exception 
    {
		ConnectionBroker broker = ConnectionBroker.getInstance(file);
		broker.closeConnections();
	}


}
