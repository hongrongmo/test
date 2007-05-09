package org.ei.domain;

public interface ElementData
	extends XMLSerializable
{
	public String[] getElementData();
	public void setElementData(String[] strings);
	public void exportLabels(boolean labels);

}