package org.ei.dataloading.xmlDataLoading;

public class Reference extends BaseElement
{
	Ref_info ref_info;
	String ref_fulltext;
	Refd_itemcitation refd_itemcitation;
	String refd_itemcitation_type;
	String reference_id;

	public void setReference_id(String reference_id)
	{
		this.reference_id = reference_id;
	}

	public String getReference_id()
	{
		return reference_id;
	}

	public void setRef_info(Ref_info ref_info)
	{
		this.ref_info = ref_info;
	}

	public Ref_info getRef_info()
	{
		return ref_info;
	}

	public void setRef_fulltext(String ref_fulltext)
	{
		this.ref_fulltext = ref_fulltext;
	}

	public String getRef_fulltext()
	{
		return ref_fulltext;
	}

	public void setRefd_itemcitation(Refd_itemcitation refd_itemcitation)
	{
		this.refd_itemcitation = refd_itemcitation;
	}

	public Refd_itemcitation getRefd_itemcitation()
	{
		return refd_itemcitation;
	}

	public void setRefd_itemcitation_type(String refd_itemcitation_type)
	{
		this.refd_itemcitation_type = refd_itemcitation_type;
	}

	public String getRefd_itemcitation_type()
	{
		return refd_itemcitation_type;
	}

}
