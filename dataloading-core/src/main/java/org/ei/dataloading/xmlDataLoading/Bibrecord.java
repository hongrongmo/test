package org.ei.dataloading.xmlDataLoading;

public class Bibrecord extends BaseElement
{
	Item_info item_info;
	Head head;
	Tail tail;

	public void setItem_info(Item_info item_info)
	{
		this.item_info = item_info;
	}

	public Item_info getItem_info()
	{
		return this.item_info;
	}

	public void setHead(Head head)
	{
		this.head = head;
	}

	public Head getHead()
	{
		return this.head;
	}

	public void setTail(Tail tail)
	{
		this.tail = tail;
	}

	public Tail getTail()
	{
		return this.tail;
	}

}
