package org.ei.dataloading.xmlDataLoading;

public class Sequencebank extends BaseElement
{
	Sequence_number sequence_number;
	String sequencebank_name;
	String sequencebank_complete;

	public void setSequence_number(Sequence_number sequence_number)
	{
		this.sequence_number = sequence_number;
	}

	public Sequence_number getSequence_number()
	{
		return this.sequence_number;
	}

	public void setSequencebank_name(String sequencebank_name)
	{
		this.sequencebank_name = sequencebank_name;
	}

	public String getSequencebank_name()
	{
		return this.sequencebank_name;
	}

	public void setSequencebank_complete(String sequencebank_complete)
	{
		this.sequencebank_complete = sequencebank_complete;
	}

	public String getSequencebank_complete()
	{
		return this.sequencebank_complete;
	}
}

