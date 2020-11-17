package org.ei.dataloading.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * 
 * @author TELEBH
 *
 */
public class Producer 
{

	private static String bootStrapServer = "localhost:9092";
	
	private  static KafkaProducer<String, String> prorducer = null;
	
	private Producer()
	{
		
	}
	
	// static method to create instance of singleton KafkaProducer class 
	
	@SuppressWarnings("unchecked")
	public static KafkaProducer<String,String> getInstance() {
		if(prorducer == null)
		{
			Properties properties = new Properties();
			properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
			properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			prorducer = new KafkaProducer<String,String>(properties);
		}
		return prorducer;
	}
	
	
}
