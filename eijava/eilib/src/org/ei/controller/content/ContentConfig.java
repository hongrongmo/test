package org.ei.controller.content;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
*	The ContentConfig class holds information about all the content in the
*	application.  It is a Singleton which means that right now each application
*	needs to run its own VM.
**/

public final class ContentConfig
{


	private Hashtable configTable = new Hashtable();

	private static ContentConfig instance;

	private ContentConfig(String configFile)
		throws Exception
	{
		loadConfig(configFile);
	}

	/**
	*	Since this object is a Singleton use this method to get the ContentConfig instance.
	*	If one does not already exist this method will create one.
	*
	*	@param configFile The URL to the XML config file.
	*
	*	@return The ContentConfig instance
	**/

	public static ContentConfig getInstance(String configFile)
		throws Exception
	{


		if(instance == null)
		{
			instance = new ContentConfig(configFile);
		}

		return instance;
	}


	private void loadConfig(String configFile)
		throws Exception
	{

		DOMParser parser = new DOMParser();
		Document doc = parser.parse(configFile);
		// Traverse the nodes
		NodeList topNode = doc.getChildNodes();

		// Descend into the tree
		for(int x=0; x<topNode.getLength(); ++x)
		{
			Node contentNode = topNode.item(x);

			NodeList authGroups = contentNode.getChildNodes();
			for(int y=0; y<authGroups.getLength(); ++y)
			{
				Node authGroupNode = authGroups.item(y);
				if(authGroupNode.hasChildNodes())
				{
					NamedNodeMap map = authGroupNode.getAttributes();
					Node authGroupName = map.getNamedItem("NAME");
					String aname = authGroupName.getNodeValue();
					Vector cdNodes = buildContentDescriptors(aname, authGroupNode);
					for(int z=0; z<cdNodes.size(); ++z)
					{
						ContentDescriptor cd = (ContentDescriptor)cdNodes.elementAt(z);
						//System.out.println("ASYNCURL:"+cd.getAsyncURL());
						configTable.put(aname+"_"+cd.getContentID(), cd);
						//System.out.println("Loading:"+aname+"_"+cd.getContentID());
					}
				}
			}
		}
	}


	private Vector buildContentDescriptors(String authGroupName, Node authGroupNode)
	{
		ContentDescriptor cd = null;
		Vector cdsvector = new Vector();
		NodeList cds = authGroupNode.getChildNodes();

		for(int x=0; x<cds.getLength(); ++x)
		{
			Node descriptorNode = cds.item(x);


			if(descriptorNode.hasChildNodes())
			{
				cd = new ContentDescriptor(authGroupName);
				NodeList list = descriptorNode.getChildNodes();
				NamedNodeMap map = descriptorNode.getAttributes();
				Node at = map.getNamedItem("BULKMODE");
				String a = at.getNodeValue();
				if(a.equals("YES"))
				{
					cd.setBulkmode(true);
				}
				else
				{
					cd.setBulkmode(false);
				}

				Node contentID = list.item(1);
				cd.setContentID(contentID.getFirstChild().getNodeValue());
				Node dataSourceURL = list.item(3);
				cd.setDataSourceURL(dataSourceURL.getFirstChild().getNodeValue());
				Node displayURL = list.item(5);
				cd.setDisplayURL(displayURL.getFirstChild().getNodeValue());
				Node mime = list.item(7);
				cd.setMimeType(mime.getFirstChild().getNodeValue());
				cdsvector.addElement(cd);
			}
		}

		return cdsvector;
	}


	/**
	*	This method looks for the a ContentDescriptor in the world AuthGroup
	*	only.
	*
	*	@param contentID The ID of the ContentDescriptor to get.
	*	@return The ContentDescriptor that was found or null.
	*
	*	@see ContentDescriptor
	**/

	public ContentDescriptor getContentDescriptor(String contentID)
	{
		String tid = "world_"+contentID;
		//System.out.println("Looking for:"+tid);
		return (ContentDescriptor)configTable.get(tid);
	}


	/**
	*	This method chooses the correct ContentDescriptor based on a contentID and
	*	a customerID.
	*	<p>
	*	This method will look for the ContentDescriptor int the customers specific
	*	AuthGroup first, and if does not find one it will check in the "Customer"
	*	AuthGroup.  If it still has not found one it will check in the "World"
	*	AuthGroup.  If it still has not found one it will return null.
	*
	**/

	public ContentDescriptor getContentDescriptor(String customerID, String contentID)
	{

		Object cd = null;


		if((cd = configTable.get(customerID+"_"+contentID)) != null)
		{
			return (ContentDescriptor)cd;
		}
		else if((cd = configTable.get("customer_"+contentID)) != null)
		{
			return (ContentDescriptor)cd;
		}
		else
		{
			cd = getContentDescriptor(contentID);
		}

		return (ContentDescriptor)cd;
	}

	public boolean containsKey(String contentID)
	{
		return configTable.containsKey(contentID);
	}

	public Enumeration keys()
	{
		return configTable.keys();
	}

}
