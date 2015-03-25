package org.ei.dataloading.xmlDataLoading;

public class Contributor extends BaseElement
{
	String initials;
	String indexed_name;
    String degrees;
    String surname;
    String given_name;
    String suffix;
    String nametext;
    String e_address;
    String contributor_role;
	String contributor_auid;
	String contributor_seq;
	String contributor_type;

	public void setInitials(String initials)
	{
		this.initials = initials;
	}

	public String getInitials()
	{
		return this.initials;
	}

	public void setIndexed_name(String indexed_name)
	{
		this.indexed_name = indexed_name;
	}

	public String getIndexed_name()
	{
		return this.indexed_name;
	}

	public void setDegrees(String degrees)
	{
		this.degrees = degrees;
	}

	public String getDegrees()
	{
		return this.degrees;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getSurname()
	{
		return this.surname;
	}

	public void setGiven_name(String given_name)
	{
		this.given_name = given_name;
	}

	public String getGiven_name()
	{
		return this.given_name;
	}

	public void setSuffix(String suffix)
	{
		this.suffix = suffix;
	}

	public String getSuffix()
	{
		return this.suffix;
	}

	public void setNametext(String nametext)
	{
		this.nametext = nametext;
	}

	public String getNametext()
	{
		return this.nametext;
	}

	public void setE_Address(String e_address)
	{
		this.e_address = e_address;
	}

	public String getE_address()
	{
		return this.e_address;
	}

	public void setContributor_role(String contributor_role)
	{
		this.contributor_role = contributor_role;
	}

	public String getContributor_role()
	{
		return this.contributor_role;
	}

	public void setContributor_auid(String contributor_auid)
	{
		this.contributor_auid = contributor_auid;
	}

	public String getContributor_auid()
	{
		return this.contributor_auid;
	}

	public void setContributor_seq(String contributor_seq)
	{
		this.contributor_seq = contributor_seq;
	}

	public String getContributor_seq()
	{
		return this.contributor_seq;
	}

	public void setContributor_type(String contributor_type)
	{
		this.contributor_type = contributor_type;
	}

	public String getContributor_type()
	{
		return this.contributor_type;
	}



}
