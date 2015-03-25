package org.ei.dataloading.xmlDataLoading;

public class Publication_notes extends BaseElement
{
	String publication_note;
	String publication_note_type;

	public void setPublication_notes(String publication_note)
	{
		this.publication_note = publication_note;
	}

	public String getPublication_notes()
	{
		return publication_note;
	}

	public void setPublication_notes_type(String publication_note_type)
	{
		this.publication_note_type = publication_note_type;
	}

	public String getPublication_notes_type()
	{
		return publication_note_type;
	}

}
