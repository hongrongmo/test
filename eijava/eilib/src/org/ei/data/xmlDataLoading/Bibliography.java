package org.ei.data.xmlDataLoading;
import java.util.*;

public class Bibliography extends BaseElement
{
	List references = new ArrayList();
	String bibliography_refcount;
	String bibliography;

	public void setReferences(List references)
	{
		this.references = references;
	}

	public List getReferences()
	{
		return this.references;
	}

	public void addReference(Reference reference)
	{
		references.add(reference);
	}

	public void setBibliography(String bibliography)
	{
		this.bibliography = bibliography;
	}

	public String getBibliography()
	{
		return this.bibliography;
	}

	public void setBibliography_refcount(String bibliography_refcount)
	{
		this.bibliography_refcount = bibliography_refcount;
	}

	public String getBibliography_refcount()
	{
		return this.bibliography_refcount;
	}

}
