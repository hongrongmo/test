package org.ei.dataloading;
import org.ei.util.kafka.*;


public abstract class CombinedWriter
{

	public abstract void begin()
		throws Exception;

	public abstract void writeRec(EVCombinedRec rec)
		throws Exception;

	public abstract void writeRec(EVCombinedRec[] rec)
	throws Exception;
	
	public abstract void writeRec(EVCombinedRec[] rec, KafkaService kafka)
			throws Exception;
	
	public abstract void writeRec(EVCombinedRec rec, KafkaService kafka)
			throws Exception;

	public abstract void end()
		throws Exception;

	public abstract void flush()
	throws Exception;

	public abstract void setOperation(String op)
	throws Exception;
	
	public abstract void setEndpoint(String op)
			throws Exception;

}
