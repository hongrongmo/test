/** This is a basic Type for Linda hall library .Uses
  *The Contact Info basic Type is used here for this user's contact info.
  */
package org.ei.domain;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.ei.util.StringUtil;
import org.ei.xml.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class LhlUserInfo
{
    private String accountNumber;
    private String accountName;
    private String deptName;
	private long customerID = 0;
	private long contractID = 0;
	private String shipping = StringUtil.EMPTY_STRING;
	private String m_strProductName = StringUtil.EMPTY_STRING;
	private String m_strAccessType = StringUtil.EMPTY_STRING;
	private Date createdDate;

	//Contact Info object for contact information regarding this user
	private ContactInfo contactInfo;
	
	public LhlUserInfo()
	{
		//Default Constructor.
	}


	//set and get methods for AccessType
	public void setAccessType(String strValue)
		throws InvalidArgumentException
	{
		if(strValue==null)
			throw new InvalidArgumentException("Access Type value is null");
		m_strAccessType = strValue;
	}

	public String getAccessType()
	{
		return m_strAccessType;
	}

	//set and get methods for ProductName
	public void setProductName(String strValue)
		throws InvalidArgumentException
	{
		if(strValue==null)
			throw new InvalidArgumentException("Product Name value is null");
		m_strProductName = strValue;
	}

	public String getProductName()
	{
		return m_strProductName;
	}

	//Get and set methods for contract id
	public void setContractID(long acontractID)
	{
		this.contractID = acontractID;
	}

	public long getContractID()
	{
		return this.contractID;
	}

	//Get and set methods for customerID
	public void setCustomerID(long acustomerID)
	{
		this.customerID = acustomerID;
	}

	public long getCustomerID()
	{
		return this.customerID;
	}

	//set and get methods for shipping
	public void setShipping(String ashipping)
		throws InvalidArgumentException
	{
		if(ashipping==null)
			throw new InvalidArgumentException("Shipping value is null");
		this.shipping=ashipping;
	}

	public String getShipping()
	{
		return this.shipping;
	}

	//set and get methods for date
	public void setCreatedDate(Date adate)
	{
		this.createdDate=adate;
	}

	public Date getCreatedDate()
	{
		return this.createdDate;
	}
	public String getCreatedDateString()
	{
		return ((createdDate != null) ? StringUtil.getFormattedDate(createdDate) : StringUtil.EMPTY_STRING);
	}


	//set and get methods for ContactInfo
	public void setContactInfo(ContactInfo acontactInfo)
	{
		this.contactInfo=acontactInfo;
	}

	public ContactInfo getContactInfo()
	{
		return this.contactInfo;
	}

	//to string of this object
	public String toString()
	{
		StringBuffer sBuf=new StringBuffer();
		sBuf.append("\ncustomerID  :").append(customerID);
		sBuf.append("\ncontractID  :").append(contractID);
		sBuf.append("\nshipping   :").append(shipping);
		sBuf.append("\ncreatedDate  :").append(getCreatedDateString());
		sBuf.append("\nproduct name:").append(m_strProductName);
		sBuf.append("\naccess type :").append(m_strAccessType);
		sBuf.append("\n\tCustomer Contact Info");
		sBuf.append(contactInfo.toString());
		
		return sBuf.toString();
	}

 	//FROM xml string
	public void loadFromXMLString(String strXML) 
		throws Exception
	{
	
		DOMParser parser = new DOMParser();
		Document doc = parser.parse(new ByteArrayInputStream(strXML.getBytes()));

		//NodeList aNodes = doc.selectNodes("descendant::CUSTOMER-ID");

		NodeList aNodes = doc.getElementsByTagName("CUSTOMER-ID");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setCustomerID(Long.parseLong(strVal));
			}
		}			

		aNodes = doc.getElementsByTagName("CONTRACT-ID");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setContractID(Long.parseLong(strVal));
			}
		}			
		
		aNodes = doc.getElementsByTagName("PRODUCT-NAME");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setProductName(strVal);
			}
		}			
		aNodes = doc.getElementsByTagName("ACCESS-TYPE");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setAccessType(strVal);
			}
		}			
		aNodes = doc.getElementsByTagName("SHIPPING");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				this.setShipping(strVal);
			}
		}			
		aNodes = doc.getElementsByTagName("CREATED-DATE");
		if(aNodes.getLength() > 0) {
			if (aNodes.item(0).getFirstChild() != null) {
				String strVal = aNodes.item(0).getFirstChild().getNodeValue();
				if(strVal != null) {
					// This will parse a string in the default format MM/dd/yyyy
					Date dteCreatedDate = StringUtil.getDateFromString(strVal);
					this.setCreatedDate(dteCreatedDate);
				}
			}
		}			

		ContactInfo ci = new ContactInfo();
		ci.loadFromXMLDocument(doc);
		setContactInfo(ci);

	}

	public String toXMLString()
	{
		StringBuffer xmlString=new StringBuffer();
		xmlString.append("<LHL-USER-INFO>");
		xmlString.append("<CUSTOMER-ID>").append(customerID).append("</CUSTOMER-ID>");
		xmlString.append("<CONTRACT-ID>").append(contractID).append("</CONTRACT-ID>");
		xmlString.append("<SHIPPING>").append(shipping).append("</SHIPPING>");
		xmlString.append("<PRODUCT-NAME>").append(m_strProductName).append("</PRODUCT-NAME>");
		xmlString.append("<ACCESS-TYPE>").append(m_strAccessType).append("</ACCESS-TYPE>");
		
		if(contactInfo != null) {
			xmlString.append(contactInfo.toXMLString());
		}
		else {
			System.out.println("LHL: Contact info is null for customer" + customerID);
		}
		xmlString.append("<CREATED-DATE>").append( getCreatedDateString() ).append("</CREATED-DATE>");
		xmlString.append("</LHL-USER-INFO>");
		return xmlString.toString();	
	}

	public static void main(String[] args) {
		
		//C:\javaplatform2\eijava\eilib>java -cp classes;./lib/xerces.jar org.ei.domain.LhlUserInfo
		
		String strXML = "<LHL-USER-INFO><CUSTOMER-ID>123456</CUSTOMER-ID><CONTRACT-ID>123456789</CONTRACT-ID><SHIPPING>d</SHIPPING><PRODUCT-NAME>DDS</PRODUCT-NAME><ACCESS-TYPE>IPA</ACCESS-TYPE><CONTACT-INFO><FIRST-NAME>john</FIRST-NAME><LAST-NAME>john</LAST-NAME><EMAIL>skt@test.com</EMAIL><COMPANY-NAME>csl</COMPANY-NAME><ADDRESS1>545 test center</ADDRESS1><CITY>testcenter</CITY><STATE>ap</STATE><ZIP></ZIP><PHONE>5645645</PHONE><FAX>456456456</FAX><ACCOUNT-NUMBER></ACCOUNT-NUMBER><COUNTRY>India</COUNTRY><ADDRESS2></ADDRESS2></CONTACT-INFO><CREATED-DATE>7/11/2002</CREATED-DATE></LHL-USER-INFO>";
		LhlUserInfo lhlUserInfo = null;
		try {
			/*lhlUserInfo = new LhlUserInfo();
			lhlUserInfo.loadFromXMLString(strXML);
			System.out.println(lhlUserInfo.toString());	
			
			lhlUserInfo = new LhlUserInfo();
			strXML = "<LHL-USER-INFO><CUSTOMER-ID>123456</CUSTOMER-ID><CONTRACT-ID>123456789</CONTRACT-ID><SHIPPING>d</SHIPPING><PRODUCT-NAME>DDS</PRODUCT-NAME><ACCESS-TYPE>IPA</ACCESS-TYPE><CONTACT-INFO><FIRST-NAME></FIRST-NAME><LAST-NAME></LAST-NAME><EMAIL></EMAIL><COMPANY-NAME></COMPANY-NAME><ADDRESS1></ADDRESS1><CITY></CITY><STATE></STATE><ZIP></ZIP><PHONE></PHONE><FAX></FAX><ACCOUNT-NUMBER></ACCOUNT-NUMBER><COUNTRY></COUNTRY><ADDRESS2></ADDRESS2></CONTACT-INFO><CREATED-DATE></CREATED-DATE></LHL-USER-INFO>";
			lhlUserInfo.loadFromXMLString(strXML);
			System.out.println(lhlUserInfo.toString());	

			lhlUserInfo = new LhlUserInfo();
			strXML = "<LHL-USER-INFO><CUSTOMER-ID></CUSTOMER-ID><CONTRACT-ID></CONTRACT-ID><SHIPPING></SHIPPING><PRODUCT-NAME></PRODUCT-NAME><ACCESS-TYPE></ACCESS-TYPE><CONTACT-INFO><FIRST-NAME></FIRST-NAME><LAST-NAME></LAST-NAME><EMAIL></EMAIL><COMPANY-NAME></COMPANY-NAME><ADDRESS1></ADDRESS1><CITY></CITY><STATE></STATE><ZIP></ZIP><PHONE></PHONE><FAX></FAX><ACCOUNT-NUMBER></ACCOUNT-NUMBER><COUNTRY></COUNTRY><ADDRESS2></ADDRESS2></CONTACT-INFO><CREATED-DATE></CREATED-DATE></LHL-USER-INFO>";
			lhlUserInfo.loadFromXMLString(strXML);
			System.out.println(lhlUserInfo.toString());	
			BackofficeClient boc = BackofficeClient.getInstance();
			lhlUserInfo = boc.getLhlUserInfo("923358","20923639");
			System.out.println("-----------------------------------------");
			System.out.println(lhlUserInfo.toString());
			strXML = lhlUserInfo.toXMLString();

			lhlUserInfo = new LhlUserInfo();
			lhlUserInfo.loadFromXMLString(strXML);
			
			System.out.println(boc.authenticateByPassword("sai","eilhl","20923639"));
		
			System.out.println(boc.sendOrderInfo(lhlUserInfo, "ACK", "ACK", "ACK", "ACK", "ACK", "jmoschet@netscape.net"));
			*/

		} catch(Exception e) {
			e.printStackTrace();
		}
		

	}


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public String getAccountName() {
        return this.accountName;
    }


    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }


    public String getDeptName() {
        return this.deptName;
    }


    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
