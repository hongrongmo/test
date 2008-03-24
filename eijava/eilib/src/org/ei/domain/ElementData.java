package org.ei.domain;

public interface ElementData
	extends XMLSerializable
{
	public void setKey(Key akey);
	public String[] getElementData();
	public void setElementData(String[] strings);
	public void exportLabels(boolean labels);

}