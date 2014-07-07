package org.ei.data.xmlDataLoading;
import java.util.*;

public class Citation_info extends BaseElement
{

	List citation_types 						= new ArrayList();
	List citation_languages 					= new ArrayList();
	List abstract_languages 					= new ArrayList();
	String author_keywords;
	String figure_information;
	String price;
	List mediums 			= new ArrayList();
	Medium mediumObject 	= null;
	String document_delivery;
	List publication_notes	= new ArrayList();
	Publication_notes publication_noteObject = null;
	String degree;


	public List getCitation_type()
	{
		return citation_types;
	}

	public void addCitation_type(Citation_type citation_type)
	{
		citation_types.add(citation_type);
	}

	public List getCitation_language()
	{
		return citation_languages;
	}

	public void addCitation_language(Citation_language citation_language)
	{
		citation_languages.add(citation_language);
	}

	public List getAbstract_language()
	{
		return this.abstract_languages;
	}

	public void addAbstract_language(Abstract_language abstract_language)
	{
		abstract_languages.add(abstract_language);
	}


	public void setAuthor_keywords(String author_keywords)
	{
		this.author_keywords = author_keywords;
	}

	public String getAuthor_keywords()
	{
		return author_keywords;
	}

	public void setFigure_information(String figure_information)
	{
		this.figure_information = figure_information;
	}

	public String getFigure_information()
	{
		return figure_information;
	}

	public void setPrice(String price)
	{
		this.price = price;
	}

	public String getPrice()
	{
		return price;
	}

	public void setMediums(List mediums)
	{
		this.mediums = mediums;
	}

	public void setMedium(String medium)
	{
		mediumObject = new Medium();
		mediumObject.setMedium(medium);
		addMedium(mediumObject);
	}

	public void setMedium_covered(String medium_covered)
	{
		mediumObject.setMedium_covered(medium_covered);
	}

	public void addMedium(Medium medium)
	{
		mediums.add(medium);
	}

	public List getMediums()
	{
		return mediums;
	}

	public void setDocument_delivery(String document_delivery)
	{
		this.document_delivery = document_delivery;
	}

	public String getDocument_delivery()
	{
		return document_delivery;
	}

	public void setPublication_notes(List publication_notes)
	{
		this.publication_notes = publication_notes;
	}

	public void addPublication_notes(Publication_notes publication_note)
	{
		publication_notes.add(publication_note);
	}

	public List getPublication_notes()
	{
		return publication_notes;
	}

	public void setPublication_notes(String publication_note)
	{
		publication_noteObject = new Publication_notes();
		publication_noteObject.setPublication_notes(publication_note);
		addPublication_notes(publication_noteObject);
	}

	public void setPublication_notes_type(String publication_note_type)
	{
		publication_noteObject.setPublication_notes_type(publication_note_type);
	}


	public void setDegree(String degree)
	{
		this.degree = degree;
	}

	public String getDegree()
	{
		return degree;
	}




}