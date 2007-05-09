package org.ei.data.xmlDataLoading;
import java.util.*;

public class Descriptorgroup extends BaseElement
{
	List descriptorss	= new ArrayList();
	String descriptors_controlled;
	String descriptors_type;

	public void setDescriptorss(List descriptorss)
	{
		this.descriptorss = descriptorss;
	}

	public List getDescriptorss()
	{
		return this.descriptorss;
	}

	public void addDescriptors(Descriptors descriptors)
	{
		descriptorss.add(descriptors);
	}

	public void setDescriptors_controlled(String descriptors_controlled)
	{
		this.descriptors_controlled = descriptors_controlled;
	}

	public String getDescriptors_controlled()
	{
		return this.descriptors_controlled;
	}

	public void setDescriptors_type(String descriptors_type)
	{
		this.descriptors_type = descriptors_type;
	}

	public String getDescriptors_type()
	{
		return this.descriptors_type;
	}


}
