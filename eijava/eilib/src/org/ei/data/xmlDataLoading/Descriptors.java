package org.ei.data.xmlDataLoading;
import java.util.*;

public class Descriptors extends BaseElement
{
	List descriptors = new ArrayList();
	String descriptors_controlled;
	String descriptors_type;

	public void setDescriptor(List descriptors)
	{
		this.descriptors = descriptors;
	}

	public List getDescriptors()
	{
		return this.descriptors;
	}

	public void addDescriptor(Descriptor descriptor)
	{
		descriptors.add(descriptor);
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