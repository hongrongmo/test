package org.ei.data.bd.runtime;


public class CRNumStrategy 
{	
	private CRNumStrategy crn = null;
	protected String crnumraw = null;
	
	public final int ELT_BIT_MASK = 1024;
	public final int PCM_BIT_MASK = 54;
	public final int CHM_BIT_MASK = 128;
	
	public CRNumStrategy(){};
	
	public CRNumStrategy(String crnumRaw)
	{
		crnumraw = crnumRaw;
	}

	public String[] getCRN(int bitMask) 
	{		
		
		if((ELT_BIT_MASK & bitMask) == ELT_BIT_MASK)
		{
			crn = new CRNumStrategyEncompass(crnumraw);
			return crn.crnAlgorithm();
		}
		else if((PCM_BIT_MASK & bitMask) == PCM_BIT_MASK)
		{
			crn = new CRNumStrategyBData(crnumraw);
			return crn.crnAlgorithm();
		}
		else if((CHM_BIT_MASK & bitMask) == CHM_BIT_MASK)
		{
			crn = new CRNumStrategyBData(crnumraw);
			return crn.crnAlgorithm();
		}
		return null;
	}
	
	public String[] crnAlgorithm()
	{
		return null;
	}

}
