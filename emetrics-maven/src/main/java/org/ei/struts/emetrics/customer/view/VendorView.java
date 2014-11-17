package org.ei.struts.emetrics.customer.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
/**
 * Mutable data representing a user of the system.
 */
public class VendorView implements Serializable {

	private VendorView() {
		super();
	}

	public static String toXML() {

		StringBuffer sb = new StringBuffer();

		sb.append("<vendor>");
		sb.append("<vend_name>").append("Elsevier Engineering Information").append("</vend_name>");
		sb.append("<vend_site>").append("http://www.ei.org/").append("</vend_site>");
		sb.append("<vend_contact>").append("eicustomersupport@elsevier.com").append("</vend_contact>");
		sb.append("</vendor>");

		return sb.toString();
	}

}