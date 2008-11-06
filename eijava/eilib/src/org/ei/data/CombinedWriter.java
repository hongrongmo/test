package org.ei.data;


public abstract class CombinedWriter
{

	public abstract void begin()
		throws Exception;

	public abstract void writeRec(EVCombinedRec rec)
		throws Exception;
	
	public abstract void writeRec(EVCombinedRec[] rec)
	throws Exception;

	public abstract void end()
		throws Exception;
	
	public abstract void flush()
	throws Exception;
	
	public abstract void setOperation(String op)
	throws Exception;
	
	public abstract void setEnvironment(String env)
	throws Exception;
			
}