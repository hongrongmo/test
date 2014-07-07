package org.ei.data.xmlDataLoading;

import java.util.*;

public class Citation_title extends BaseElement
{

	List titletexts = new ArrayList();
	/*
	public void setTitletext(Titletext titletext)
	{
		this.titletext = titletext;
	}

	public Titletext getTitletext()
	{
		return titletext;
	}
	*/
	public void setTitletext(List titletexts)
	{
		this.titletexts = titletexts;
	}

	public void addTitletext(Titletext titletext)
	{
		titletexts.add(titletext);
	}

	public List getTitletext()
	{
		return this.titletexts;
	}

}