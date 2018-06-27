package org.ei.dataloading.xmlDataLoading;

public class Enhancement extends BaseElement
{

 	Patent patent;
 	Descriptorgroup descriptorgroup;
 	Classificationgroup classificationgroup;
	Manufacturergroup manufacturergroup;
	Tradenamegroup tradenamegroup;
	Sequencebanks sequencebanks;
	Chemicalgroup chemicalgroup;

	public void setPatent(Patent patent)
	{
		this.patent = patent;
	}

	public Patent getPatent()
	{
		return this.patent;
	}

	public void setDescriptorgroup(Descriptorgroup descriptorgroup)
	{
		this.descriptorgroup = descriptorgroup;
	}

	public Descriptorgroup getDescriptorgroup()
	{
		return this.descriptorgroup;
	}

	public void setClassificationgroup(Classificationgroup classificationgroup)
	{
		this.classificationgroup = classificationgroup;
	}

	public Classificationgroup getClassificationgroup()
	{
		return this.classificationgroup;
	}

	public void setManufacturergroup(Manufacturergroup manufacturergroup)
	{
		this.manufacturergroup = manufacturergroup;
	}

	public Manufacturergroup getManufacturergroup()
	{
		return this.manufacturergroup;
	}

	public void setTradenamegroup(Tradenamegroup tradenamegroup)
	{
		this.tradenamegroup = tradenamegroup;
	}

	public Tradenamegroup getTradenamegroup()
	{
		return this.tradenamegroup;
	}

	public void setSequencebanks(Sequencebanks sequencebanks)

	{
		this.sequencebanks = sequencebanks;
	}

	public Sequencebanks getSequencebanks()
	{
		return this.sequencebanks;
	}

	public void setChemicalgroup(Chemicalgroup chemicalgroup)
	{
		this.chemicalgroup = chemicalgroup;
	}

	public Chemicalgroup getChemicalgroup()
	{
		return this.chemicalgroup;
	}

}
