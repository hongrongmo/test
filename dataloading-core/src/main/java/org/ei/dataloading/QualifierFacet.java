package org.ei.dataloading;

import org.ei.common.*;

public class QualifierFacet
{
	private boolean norole;
	private boolean reagent;
	private boolean product;

	public void setNorole(String term)
	{
		if (!this.norole && term != null && !term.equals(""))
		{
			this.norole = true;
		}
	}
	public void setReagent(String term)
	{
		if (!this.reagent && term != null && !term.equals(""))
		{
			this.reagent = true;
		}
	}
	public void setProduct(String term)
	{
		if (!this.product && term != null && !term.equals(""))
		{
			this.product = true;
		}
	}

	public String getValue()
	{
		StringBuffer value = new StringBuffer();
		if(norole)
		{
			value.append("N").append(Constants.AUDELIMITER);
		}
		if(reagent)
		{
			value.append("R").append(Constants.AUDELIMITER);
		}
		if(product)
		{
			value.append("P").append(Constants.AUDELIMITER);
		}

		return value.toString();
	}

	public String toString()
	{
		StringBuffer strvalue = new StringBuffer();

		strvalue.append("norole: ").append(norole);
		strvalue.append("reagent: ").append(reagent);
		strvalue.append("product: ").append(product);

		return strvalue.toString();
	}
}
