package org.ei.dataloading.xmlDataLoading;

public class Author extends BaseElement
{
	String initials;
	String indexed_name;
    String degrees;
    String surname;
    String given_name;
    String suffix;
    String nametext;
    String e_address;
    String auid;
    String seq;
    String type;
    String author;

	 public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getAuthor()
	{
		return this.author;
	}

    public void setAuthor_type(String type)
	{
		this.type = type;
	}

	public String getAuthor_type()
	{
		return this.type;
	}

    public void setAuthor_seq(String seq)
	{
		this.seq = seq;
	}

	public String getAuthor_seq()
	{
		return this.seq;
	}

    public void setAuthor_auid(String auid)
	{
		this.auid = auid;
	}

	public String getAuthor_auid()
	{
		return this.auid;
	}

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

	public void setE_address(String e_address)
	{
		this.e_address = e_address;
	}

	public String getE_address()
	{
		return this.e_address;
	}


}
