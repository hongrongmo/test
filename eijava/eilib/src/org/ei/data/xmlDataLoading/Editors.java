package org.ei.data.xmlDataLoading;
import java.util.*;

public class Editors extends BaseElement
{
	String editors_complete;
	Editor editor;
	List editors = new ArrayList();

	public void setEditors_complete(String editors_complete)
	{
		this.editors_complete = editors_complete;
	}

	public String getEditors_complete()
	{
		return this.editors_complete;
	}

	public void addEditor(Editor editor)
	{
		editors.add(editor);
	}

	public List getEditor()
	{
		return this.editors;
	}

}