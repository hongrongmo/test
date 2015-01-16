package org.ei.controller.content;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The ContentConfig class holds information about all the content in the
 * application. It is a Singleton which means that right now each application
 * needs to run its own VM.
 **/

public final class ContentConfig {
	private static Logger log4j = Logger.getLogger(ContentConfig.class);

	// Table of ContentDescriptor objects hashed by one of the following keys:
	// 1) "legacy_<CID>" - indicates Stripes should handle request instead of
	// legacy.
	// 1) "world_<CID>" - available to all requests
	// 2) "customer_<CID>" - available only to valid customers
	// 3) "<customerID>_<CID>" - available only to specific customers
	private Hashtable<String, ContentDescriptor> configTable = new Hashtable<String, ContentDescriptor>();

	// Singleton instance
	private static ContentConfig instance;

	// Private constructor - loads the config file contents
	private ContentConfig(String configFile) throws InfrastructureException {
		loadConfig(configFile);
	}

	/**
	 * Since this object is a Singleton use this method to get the ContentConfig
	 * instance. If one does not already exist this method will create one.
	 * 
	 * @param configFile
	 *            The URL to the XML config file.
	 * @return The ContentConfig instance
	 * @throws InfrastructureException
	 * @throws ContentConfigException
	 **/
	public static ContentConfig getInstance(String configFile) throws InfrastructureException {

		if (instance == null) {
			log4j.info("Initializing instance from configFile: '" + configFile + "'");
			instance = new ContentConfig(configFile);
		}

		return instance;
	}

	/**
	 * This method looks for the a ContentDescriptor by key. Callers should
	 * formulate the content ID
	 * 
	 * @param contentID
	 *            The ID of the ContentDescriptor to get.
	 * @return The ContentDescriptor that was found or null.
	 * 
	 * @see ContentDescriptor
	 **/
	public ContentDescriptor getContentDescriptor(String contentID) {
		ContentDescriptor cd = configTable.get(contentID);
		if (cd == null) {
			log4j.warn("Unable to find '" + contentID + "' content ID in configTable...");
		}
		return cd;
	}

	/**
	 * This method chooses the correct ContentDescriptor based on a contentID
	 * and a customerID.
	 * <p>
	 * This method will look for the ContentDescriptor int the customers
	 * specific AuthGroup first, and if does not find one it will check in the
	 * "Customer" AuthGroup. If it still has not found one it will check in the
	 * "World" AuthGroup. If it still has not found one it will return null.
	 * 
	 **/
	public ContentDescriptor getContentDescriptor(String customerID, String contentID) {

		ContentDescriptor cd = null;

		if (customerID != null && (cd = configTable.get(customerID + "_" + contentID)) != null) {
			return cd;
		} else if ((cd = configTable.get("customer_" + contentID)) != null) {
			return cd;
		} else {
			cd = getContentDescriptor("world_" + contentID);
		}

		if (cd == null) {
			log4j.warn("Unable to build ContentDescriptor, customer ID = '" + customerID + "',  content ID = '" + contentID + "'");
		}

		return (ContentDescriptor) cd;
	}

	/**
	 * Look into ContentDescriptor table to see if contentID is present
	 * 
	 * @param contentID
	 * @return
	 */
	public boolean containsKey(String contentID) {
		return configTable.containsKey(contentID);
	}

	/**
	 * Return the enumerated table keys
	 * 
	 * @return
	 */
	public Enumeration<String> keys() {
		return configTable.keys();
	}

	/**
	 * Load internal config table from file
	 * 
	 * @param configFile
	 * @throws ContentConfigException
	 * @throws Exception
	 */
	private void loadConfig(String configFile) throws InfrastructureException {

		DOMParser parser = new DOMParser();
		Document doc;
		try {
			doc = parser.parse(configFile);
		} catch (Exception e) {
			InfrastructureException ie = new InfrastructureException(SystemErrorCodes.CONTENT_CONFIG_PARSE_ERROR,
					"Content Confing file parsing failed:" + e.getMessage(), e);
			ie.setStackTrace(e.getStackTrace());
			throw ie;
		}

		// Traverse the nodes
		NodeList topNode = doc.getChildNodes();

		// Descend into the tree
		for (int x = 0; x < topNode.getLength(); ++x) {
			Node contentNode = topNode.item(x);

			NodeList authGroups = contentNode.getChildNodes();
			for (int y = 0; y < authGroups.getLength(); ++y) {
				Node authGroupNode = authGroups.item(y);
				if (authGroupNode.hasChildNodes()) {
					NamedNodeMap map = authGroupNode.getAttributes();
					Node authGroupName = map.getNamedItem("NAME");
					String aname = authGroupName.getNodeValue();
					Vector<ContentDescriptor> cdNodes = buildContentDescriptors(aname, authGroupNode);
					for (int z = 0; z < cdNodes.size(); ++z) {
						ContentDescriptor cd = (ContentDescriptor) cdNodes.elementAt(z);
						log4j.debug("Adding '" + aname + "_" + cd.getContentID() + "'");
						configTable.put(aname + "_" + cd.getContentID(), cd);
						// System.out.println("Loading:"+aname+"_"+cd.getContentID());
					}
				}
			}
		}
	}

	/**
	 * Initialization method for building ContentDescriptor objects from the
	 * ContentConfig file
	 * 
	 * @param authGroupName
	 * @param authGroupNode
	 * @return
	 */
	private Vector<ContentDescriptor> buildContentDescriptors(String authGroupName, Node authGroupNode) {
		ContentDescriptor cd = null;
		Vector<ContentDescriptor> cdsvector = new Vector<ContentDescriptor>();
		NodeList cds = authGroupNode.getChildNodes();

		for (int x = 0; x < cds.getLength(); ++x) {
			Node descriptorNode = cds.item(x);

			if (descriptorNode.hasChildNodes()) {
				cd = new ContentDescriptor(authGroupName);
				NodeList list = descriptorNode.getChildNodes();
				NamedNodeMap map = descriptorNode.getAttributes();

				Node at = map.getNamedItem("BULKMODE");
				String a = at.getNodeValue();
				if (a.equals("YES")) {
					cd.setBulkmode(true);
				} else {
					cd.setBulkmode(false);
				}

				//
				// Check for "XML" mode - indicates whether or
				// not the the DATASOURCEURL returns XML or not
				//
				at = map.getNamedItem("XMLMODE");
				if (at != null) {
					a = at.getNodeValue();
					if (a.equals("YES")) {
						cd.setXmlmode(true);
					} else {
						cd.setXmlmode(false);
					}
				}

				//
				// Check for "REFRESH" mode - indicates the display URL
				// is actually a Stripes action and should not be transformed
				//
				at = map.getNamedItem("REFRESH");
				if (at != null) {
					a = at.getNodeValue();
					if (a.equals("YES")) {
						cd.setRefresh(true);
					} else {
						cd.setRefresh(false);
					}
				}

				Node contentID = list.item(1);
				cd.setContentID(contentID.getFirstChild().getNodeValue());
				Node dataSourceURL = list.item(3);
				cd.setDataSourceURL(dataSourceURL.getFirstChild().getNodeValue());
				Node displayURL = list.item(5);
				if (displayURL.getFirstChild() != null) {
					cd.setDisplayURL(displayURL.getFirstChild().getNodeValue());
				}
				Node mime = list.item(7);
				cd.setMimeType(mime.getFirstChild().getNodeValue());
				cdsvector.addElement(cd);
			}
		}

		return cdsvector;
	}

}
