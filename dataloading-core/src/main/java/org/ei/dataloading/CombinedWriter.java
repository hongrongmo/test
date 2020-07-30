package org.ei.dataloading;
import java.util.concurrent.ExecutorService;
import java.util.HashMap;
import org.ei.util.kafka.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
	
	public abstract void writeRec(EVCombinedRec[] rec, KafkaService kafka, Map<String,String> batchData) throws Exception;
	
	public abstract void writeRec(EVCombinedRec rec, KafkaService kafka, Map<String,String> batchData) throws Exception;
	
	public abstract void writeRec(EVCombinedRec[] rec, KafkaService kafka, Map<String,String> batchData, Map<String,String> missedData)
			throws Exception;
	
	public abstract void writeRec(EVCombinedRec rec, KafkaService kafka, Map<String,String> batchData, Map<String,String> missedData)
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
