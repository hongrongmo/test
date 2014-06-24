package org.ei.controller.content;

/**
 * This object represents an entry from the ContentConfig.xml
 * file that stores all of EV's access points.  The file is 
 * structured as follows:
 * 
 *  <AUTHGROUP NAME="xxxx">
 *      <CONTENTDESCRIPTOR [BULKMODE=YES|NO(default)] [XMLMODE=YES(default)|NO] [REFRESH=YES|NO(default)]>
 *          <ID>xxxx</ID>
 *          <DATASOURCEURL>xxxx</DATASOURCEURL>
 *          <DISPLAYURL>xxxx</DISPLAYURL>
 *          <MIMETYPE>[text/html|text/xml|...]</MIMETYPE>
 *      </CONTENTDESCRIPTOR>
 *  </AUTHGROUP>
 * 
 * The AUTHGROUP defines the access level.  Currently there is "legacy",
 * "world", "customer" and special customer ID levels.  
 * 
 * For the CONTENTDESCRIPTOR object, the BULKMODE attribute indicates
 * a multi-request action, XMLMODE indicates whether or not XML should
 * be retrieved for the request and REFRESH indicates if the DISPLAYURL 
 * tag is a Stripes-ready URL or a legacy XSL path.
 * 
 * The ID field maps to legacy EV URL's CID parameter.  
 * 
 * TMH - We are slowly phasing out the use of the ContentConfig.xml
 * file and replacing it with Stripes Action associations.  
 *
 */
public class ContentDescriptor {

	public static String AUTHGROUP_WORLD_NAME = "world";
	public static String AUTHGROUP_LEGACY_NAME = "legacy";
	public static String AUTHGROUP_CUSTOMER_NAME = "customer";
	
	private String contentID;
	private String dataSourceURL;
	private String displayURL;
	private boolean bulkmode;
	private boolean refresh;
	private boolean xmlmode = true;
	private String authGroup;
	private String mimeType;

	public ContentDescriptor(String authGroup) {
		this.authGroup = authGroup;
	}

	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	public String getAuthGroup() {
		return this.authGroup;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	public String getContentID() {
		return this.contentID;
	}

	public String getDataSourceURL() {
		return this.dataSourceURL;
	}

	public void setDataSourceURL(String dataSourceURL) {
		this.dataSourceURL = dataSourceURL;
	}

	public String getDisplayURL() {
		return this.displayURL;
	}

	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public boolean isBulkmode() {
		return bulkmode;
	}

	public void setBulkmode(boolean bmode) {
		this.bulkmode = bmode;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	public boolean isXmlmode() {
		return xmlmode;
	}

	public void setXmlmode(boolean xmlmode) {
		this.xmlmode = xmlmode;
	}
}
