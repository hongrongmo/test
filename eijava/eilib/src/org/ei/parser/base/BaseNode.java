package org.ei.parser.base;

import java.util.Iterator;

import org.ei.parser.ParseNode;

public abstract class BaseNode extends ParseNode
{

	public BaseNode(String type, String value)
	{
		super(type, value);
	}
	
	public BaseNode(String type)
	{
		super(type);	
	}

	public abstract void accept(BaseNodeVisitor visitor);

	public abstract BaseNode shallowCopy();

	public BaseNode deepCopy()
	{
		BaseNode node = shallowCopy();
		Iterator i = iterator();
		while(i.hasNext())
		{
			BaseNode child = (BaseNode)i.next();
			node.addChild(child.deepCopy());
		}

		return node;
	}
	
}
