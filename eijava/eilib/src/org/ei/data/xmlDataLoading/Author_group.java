package org.ei.data.xmlDataLoading;
import java.util.*;

public class Author_group extends BaseElement
{
	List authors = new ArrayList();
	List collaborations = new ArrayList();
	String et_al;
	Affiliation affiliation;
	String author_group = null;

	public void setAuthor_group(String author_group)
	{
		this.author_group = author_group;
	}

	public String getAuthor_group(String author_group)
	{
		return this.author_group;
	}

	public void setAuthors(List authors)
	{
		this.authors = authors;
	}

	public void addAuthor(Author author)
	{
		authors.add(author);
	}

	public List getAuthors()
	{
		return this.authors;
	}

	public void setCollaborations(List collaborations)
	{
		this.collaborations = collaborations;
	}

	public void addCollaboration(Collaboration collaboration)
	{
		collaborations.add(collaboration);
	}

	public List getCollaborations()
	{
		return this.collaborations;
	}

	public void setAffiliation(Affiliation affiliation)
	{
		this.affiliation = affiliation;
	}

	public Affiliation getAffiliation()
	{
		return this.affiliation;
	}

	public void setEt_Al(String et_al)
	{
		this.et_al = et_al;
	}

	public String getEt_Al()
	{
		return this.et_al;
	}



}

