package org.ei.controller;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;


public class DepartmentConfigHandler
	extends DefaultHandler
{
	private Map departmentMap = new HashMap();
	private Customer currentCustomer;
	private ArrayList currentDepartments;

	public Map getDepartmentMap()
	{
		return departmentMap;
	}

	public void startElement(String nameSpace,
							 String localName,
							 String qName,
							 Attributes att)
		throws SAXException
	{
		if(qName.equals("customer"))
		{
			this.currentCustomer = new Customer(att.getValue("id"),
												att.getValue("contact"),
												att.getValue("email"));

			this.currentDepartments = new ArrayList();
		}
		else if(qName.equals("department"))
		{
			String id = att.getValue("id");
			String name = att.getValue("name");
			currentDepartments.add(new Department(id, name));

		}
	}

	public void endElement(String nameSpace,
						   String localName,
						   String qName)
		throws SAXException
	{
		if(qName.equals("customer"))
		{
			this.currentCustomer.setDepartments(((Department[])currentDepartments.toArray(new Department[this.currentDepartments.size()])));
			departmentMap.put(this.currentCustomer.getID(),
							  this.currentCustomer);
		}
	}
}
