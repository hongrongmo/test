package org.ei.data.xmlDataLoading;
import java.util.*;

public class Confeditors extends BaseElement
{
	Editors editors;
	String editororganization = null;
	List editororganizations = new ArrayList();
	String editoraddress;

	public void setEditors(Editors editors)
	{
		this.editors = editors;
	}

	public Editors getEditors()
	{
		return this.editors;
	}

	public void setEditororganization(String editororganization)
	{
		addEditororganization(editororganization);
	}

	public void addEditororganization(String editororganization)
	{
		editororganizations.add(editororganization);
	}


	public List getEditororganizations()
	{
		return this.editororganizations;
	}

	public void setEditoraddress(String editoraddress)
	{
		this.editoraddress = editoraddress;
	}

	public String getEditoraddress()
	{
		return this.editoraddress;
	}


}


