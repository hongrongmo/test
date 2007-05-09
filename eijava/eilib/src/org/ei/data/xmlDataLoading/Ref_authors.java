package org.ei.data.xmlDataLoading;
import java.util.*;

public class Ref_authors extends BaseElement
{

	List authors = new ArrayList();
	List collaborations = new ArrayList();
	String et_al;

	public void setAuthors(List authors)
	{
		this.authors = authors;
	}

	public void addAuthors(Author author)
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

	public void setEt_alr(String et_al)
	{
		this.et_al = et_al;
	}

	public String getEt_al()
	{
		return this.et_al;
	}
}

