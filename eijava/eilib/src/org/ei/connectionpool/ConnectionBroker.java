package org.ei.connectionpool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ConnectionBroker
{

	private Hashtable poolsTable = new Hashtable();
	private boolean loadFailure = false;
	private String config;
	private static ConnectionBroker instance;
	private static String configFileURI="D:/devtools/ei/jakarta-tomcat-3.2.2/webapps/chemvillage/pools.xml";


	public static void main(String args[])
		throws Exception
	{

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		ConnectionBroker broker = ConnectionBroker.getInstance();
		try
		{
			con = broker.getConnection("ChemPool");
			stmt = con.createStatement();
			rs = stmt.executeQuery("Select * from tab");
			while(rs.next())
			{
				System.out.println(rs.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			broker.closeConnections();
		}
	}


	private ConnectionBroker(String configFileURI)
		throws ConnectionPoolException
	{
		try
		{

			DOMParser parser = new DOMParser();
			Document doc = parser.parse(configFileURI);

			List pools=new ArrayList();
			Element e=doc.getDocumentElement();
			NodeList childNodeList=e.getChildNodes();
			for(int i=0;i<childNodeList.getLength();i++){
				if(!(childNodeList.item(i).getNodeType()==(Node.TEXT_NODE))){
					pools.add(childNodeList.item(i));
		    	}
		    }

			for(int x=0; x<pools.size(); ++x)
			{
				Node pool = (Node)pools.get(x);
				ConnectionPool cpool = new ConnectionPool();
				NamedNodeMap map = pool.getAttributes();
				String name = map.getNamedItem("NAME").getNodeValue();
				System.out.println("NAME:"+name);
				String url = map.getNamedItem("URL").getNodeValue();
				System.out.println("URL:"+url);
				String driver = map.getNamedItem("DRIVER").getNodeValue();
				System.out.println("DRIVER:"+ driver);
				String username = map.getNamedItem("USERNAME").getNodeValue();
				System.out.println("USERNAME:"+ username);
				String password = map.getNamedItem("PASSWORD").getNodeValue();
				System.out.println("PASSWORD:"+ password);
				String logFile = map.getNamedItem("LOGFILE").getNodeValue();
				System.out.println("LOGFILE:"+ logFile);
				String mincon = map.getNamedItem("MINCONNECTIONS").getNodeValue();
				System.out.println("MINCON:"+ mincon);
				String maxcon = map.getNamedItem("MAXCONNECTIONS").getNodeValue();
				System.out.println("MAXCON:"+ maxcon);
				String refresh = map.getNamedItem("REFRESHINTERVAL").getNodeValue();
				System.out.println("REFRESH:"+ refresh);
				cpool.setUrl(url);
				cpool.setDriver(driver);
				cpool.setPassword(password);
				cpool.setUsername(username);
				cpool.setLogFile(logFile);
				cpool.setRefreshInterval(Double.parseDouble(refresh));
				cpool.setMaxConnections(Integer.parseInt(maxcon));
				cpool.setMinConnections(Integer.parseInt(mincon));
				cpool.initialize();
				poolsTable.put(name, cpool);
			}
		}
		catch(Exception e)
		{
			throw new ConnectionPoolException(e);
		}
	}


	public static synchronized ConnectionBroker getInstance(String configFileURI)
		throws ConnectionPoolException
	{
		if(instance == null)
		{
			instance = new ConnectionBroker(configFileURI);
		}

		return instance;
	}

	public static ConnectionBroker getInstance()throws ConnectionPoolException {
		if(instance == null)
		{
			instance = new ConnectionBroker(configFileURI);
		}
		return instance;
	}


	public Connection getConnection(String name)
		throws NoConnectionAvailableException
	{
		Connection c = null;
		ConnectionPool pool = (ConnectionPool)poolsTable.get(name);
		c = pool.getConnection();
		return c;
	}

	public void closeConnections()
		throws ConnectionPoolException
	{
		try
		{
			if(poolsTable.size() > 0)
			{
				// Let the connections that are out there in use get checked back in.
				Thread.sleep(2000);
				Enumeration enum = poolsTable.keys();
				while(enum.hasMoreElements())
				{
					String poolID = (String)enum.nextElement();
					ConnectionPool pool = (ConnectionPool)poolsTable.get(poolID);
					pool.closeConnections();
				}
			}
		}
		catch(Exception e)
		{
			throw new ConnectionPoolException(e);
		}
	}


	public void replaceConnection(Connection w, String name)
		throws ConnectionPoolException
	{
		ConnectionPool p = (ConnectionPool)poolsTable.get(name);
		p.replaceConnection(w);
	}

}
