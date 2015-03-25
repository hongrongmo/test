package org.ei.dataloading.xmlDataLoading;

public class Titletext extends BaseElement
{
	String titletext_lang;
	String titletext_original;
	String titletext;

	public void setTitletext(String titletext)
	{

		this.titletext = titletext;
	}

	public String getTitletext()
	{
		return titletext;
	}

	public void setTitletext_lang(String titletext_lang)
	{

		this.titletext_lang = titletext_lang;
	}

	public String getTitletext_lang()
	{
		return titletext_lang;
	}

	public void setTitletext_original(String titletext_original)
	{

		this.titletext_original = titletext_original;
	}

	public String getTitletext_original()
	{
		return titletext_original;
	}

}
