package org.ei.parser;

import java.util.Iterator;
import java.util.Vector;


public class ParseNode
{
	private Vector children = new Vector();
	private ParseNode parent;
	private String type;
	private int childIndex;
	protected String value;
	

	public ParseNode(String type)
	{
		this.type = type;	
	}
	
	public ParseNode(String type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public String getType()
	{
		return this.type;
	}

	
	public void setNodeValue(String value)
	{
		this.value = value;
	}

	public String getNodeValue()
	{
		return this.value;
	}
	
	public String getValue()
	{
		String v = null;
		//System.out.println(type);

		if(value == null)
		{
			StringBuffer buf = new StringBuffer(" ");
			for(int x=0; x<children.size(); ++x)
			{
				ParseNode node = (ParseNode)children.elementAt(x);		
				buf.append(node.getValue());
			}
			v = buf.toString();
		}
		else
		{
			v = " "+value+" ";
		}

		return v;
	}

	public void addChild(ParseNode node)
	{
		children.addElement(node);
		node.setChildIndex(children.size() - 1);
		node.setParent(this);
	}

	public void setChildIndex(int index)
	{
		this.childIndex = index;
	}

	public int getChildIndex()
	{
		return this.childIndex;
	}

	public void setChildAt(int index, ParseNode node)
	{
		children.setElementAt(node, index);
		node.setChildIndex(index);
		node.setParent(this);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public ParseNode getChildAt(int number)
	{
		return (ParseNode)children.elementAt(number);
	}

	public ParseNode getParent()
	{
		return this.parent;
	}

	public Iterator iterator()
	{
		return children.iterator();
	}

	public void setParent(ParseNode node)
	{
		this.parent = node;		
	}
	

	public boolean isLeaf()
	{
		return false;
	}

}
