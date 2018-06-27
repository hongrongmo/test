package org.ei.dataloading.xmlDataLoading;

public class Abstract extends BaseElement
{
	String publishercopyright;
	String para;
	String perspective;
	String lang = "en";
	String original;
	String source;
	String _abstract;

	public void setPublishercopyright(String publishercopyright)
	{
		this.publishercopyright = publishercopyright;
	}

	public String getPublishercopyright()
	{
		return this.publishercopyright;
	}

	public void setPara(String para)
	{
		this.para = para;
	}

	public String getPara()
	{
		return this.para;
	}

	public void setAbstract_original(String original)
	{
		this.original = original;
	}

	public String getAbstract_original()
	{
		return this.original;
	}

	public void setAbstract_source(String source)
	{
		this.source = source;
	}

	public String getAbstract_source()
	{
		return this.source;
	}

	public void setAbstract_lang(String lang)
	{
		this.lang = lang;
	}

	public String getAbstract_lang()
	{
		return this.lang;
	}

	public void setAbstract_perspective(String perspective)
	{
		this.perspective = perspective;
	}

	public String getAbstract_perspective()
	{
		return this.perspective;
	}

	public void setAbstract(String _abstract)
	{
		this._abstract = _abstract;
	}

	public String getAbstract()
	{
		return this._abstract;
	}

}
