package org.ei.domain;

import java.util.*;
import java.io.*;
import org.ei.util.DiskMap;

public class ClassNode
{
	private ClassNode parent;
	private Stack children = new Stack();
	private String title;
	private String id;

	public ClassNode(String id,
					 String title,
					 Map seekMap)
	{
		this.title = title;
		this.id = id;
		seekMap.put(id, this);
	}




	public void export(DiskMap map)
		throws Exception
	{
		LinkedList ll = new LinkedList();
		ll.addLast(getTitle());
		ClassNode cn = this;

		while((cn = cn.getParent()) != null)
		{
			if(!cn.getTitle().equals("root"))
			{
				ll.addLast(cn.getTitle());
			}
		}

		StringBuffer buf = new StringBuffer();
		while(ll.size() > 0)
		{
			buf.append(ll.removeLast());
			if(ll.size() > 0)
			{
				buf.append(" (:) ");
			}
		}

		String t = buf.toString();
		map.put(getID(), t);

		Iterator it = children.iterator();
		while(it.hasNext())
		{
			ClassNode cnode = (ClassNode)it.next();
			cnode.export(map);
		}
	}



	public ClassNode(String id,
					 Map seekMap)
	{
		this.id = id;
		seekMap.put(id, this);
	}

	public String getTitle()
	{
		return this.title;
	}

	public String getID()
	{
		return this.id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public ClassNode getParent()
	{
		return this.parent;
	}

	public void setParent(ClassNode parent)
	{
		this.parent = parent;
	}

	public ClassNode getCurrentChildNode()
	{

		ClassNode currentNode = (ClassNode) children.peek();

		return currentNode;
	}

	public void addChild(ClassNode child)
	{
		child.setParent(this);
		children.push(child);
	}

	public Stack getChildren()
	{
		return children;
	}

}