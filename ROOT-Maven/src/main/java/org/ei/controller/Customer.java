package org.ei.controller;

public class Customer
{
	private String ID;
	private String contactEmail;
	private String contactName;
	private Department[] departments;

	public Customer(String ID,
					String contactName,
					String contactEmail)
	{
		this.ID = ID;
		this.contactName = contactName;
		this.contactEmail = contactEmail;
	}

	public String getID()
	{
		return this.ID;
	}

	public void setID(String ID)
	{
		this.ID = ID;
	}

	public String getContactEmail()
	{
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}

	public String getContactName()
	{
		return this.contactName;
	}

	public Department[] getDepartments()
	{
		return this.departments;
	}

	public void setDepartments(Department[] departments)
	{
		this.departments = departments;
	}
}