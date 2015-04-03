package org.ei.data.xmlDataLoading;
import java.util.*;

public class Classifications extends BaseElement
{
	List classificationList = new ArrayList();
	String classificationsType;

	public void addClassification(String classification)
	{
		try
		{
			classificationList.add(classification);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setClassifications(List classificationList)
	{
		this.classificationList = classificationList;
	}

	public void setClassification(String classification)
	{
		addClassification(classification);
	}

	public List getClassification()
	{
		return this.classificationList;
	}

	public void setClassifications_type(String classificationsType)
	{
		this.classificationsType = classificationsType;
	}

	public String getClassification_type()
	{
		return this.classificationsType;
	}
}