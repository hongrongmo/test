package org.ei.thesaurus;

public class ThesaurusStep
{

	private int StepNum;
	private String Context;
	private String Title;
	private String Link;


	public void setStepNum(int stepNum)
	{
		this.StepNum = stepNum;
	}
	public void setContext(String context)
	{
		this.Context = context;
	}
	public void setTitle(String title)
	{
		this.Title = title;
	}
	public void setLink(String link)
	{
		this.Link = link;
	}



	public int getStepNum()
	{
		return this.StepNum;
	}
	public String getContext()
	{
		return this.Context;
	}
	public String getTitle()
	{
		return this.Title;
	}
	public String getLink()
	{
		return this.Link;
	}


}