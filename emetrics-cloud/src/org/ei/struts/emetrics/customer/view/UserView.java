package org.ei.struts.emetrics.customer.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
/**
 * Mutable data representing a user of the system.
 */
public class UserView  implements Serializable {

	private int custId;
//	private String custName;
//	private String username;
	private String name;
	private String role;
	private String channel;
    private boolean parentonly = false;

	private Collection members = new ArrayList();

	public UserView() {
		super();
	}

	public boolean hasMember(String custid) {
		boolean ismember = false;

		Collection members = this.getConsortium();
		Iterator iter = members.iterator();
		while(iter.hasNext()) {
			Map member = (Map) iter.next();
			String memberid = (String) member.get("custid");
			if(custid.equalsIgnoreCase(memberid)) {
				ismember = true;
				break;
			}
		}
		return ismember;
	}

	public Collection getConsortium() {
		return members;
	}

	public void setConsortiumMembers(Collection members) {
		this.members = members;
	}


	public void setParentonly(boolean parentonly) {
		this.parentonly = parentonly;
	}
	public boolean getParentonly() {
		return parentonly;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//	public String getCustomerName() {
//		return custName;
//	}
//
//	public void setCustomerName(String custName) {
//		this.custName = custName;
//	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	public int getCustomerId() {
		return custId;
	}

	public void setCustomerId(int custId) {
		this.custId = custId;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append(" UserView [ customerid: ").append(this.getCustomerId());
		sb.append(", channel: ").append(this.getChannel());
		sb.append(", role: ").append(this.getRole());
		sb.append(", name: ").append(this.getName());
		sb.append(" ]");

		return sb.toString();
	}


	public String toXML() {

		StringBuffer sb = new StringBuffer();

		sb.append("<customer>");
		sb.append("<cust_name><![CDATA[").append(this.getName()).append("]]></cust_name>");
		sb.append("<cust_ref>").append(this.getCustomerId()).append("</cust_ref>");
		sb.append("<cust_ip type=\"cidr\">").append("IP RANGE(s)").append("</cust_ip>");
		sb.append("<cust_username>").append("USERNAME").append("</cust_username>");
		sb.append("<cust_criteria>").append("CUSTOMER_TYPE").append("</cust_criteria>");
		sb.append("</customer>");

		return sb.toString();
	}

}