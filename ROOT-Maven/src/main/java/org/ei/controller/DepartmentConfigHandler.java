package org.ei.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Handler to parse Department/Customer info from XML file
 */
public class DepartmentConfigHandler extends DefaultHandler {
	private Map<String, Customer> departmentMap = new HashMap<String, Customer>();
	private Customer currentCustomer;
	private ArrayList<Department> currentDepartments;

	public Map<String, Customer> getDepartmentMap() {
		return departmentMap;
	}

	@Override
	public void startElement(String nameSpace, String localName, String qName,
			Attributes att) throws SAXException {
		if (qName.equals("customer")) {
			this.currentCustomer = new Customer(att.getValue("id"),
					att.getValue("contact"), att.getValue("email"));

			this.currentDepartments = new ArrayList<Department>();
		} else if (qName.equals("department")) {
			String id = att.getValue("id");
			String name = att.getValue("name");
			currentDepartments.add(new Department(id, name));

		}
	}

	@Override
	public void endElement(String nameSpace, String localName, String qName)
			throws SAXException {
		if (qName.equals("customer")) {
			this.currentCustomer
					.setDepartments(((Department[]) currentDepartments
							.toArray(new Department[this.currentDepartments
									.size()])));
			departmentMap.put(this.currentCustomer.getID(),
					this.currentCustomer);
		}
	}
}
