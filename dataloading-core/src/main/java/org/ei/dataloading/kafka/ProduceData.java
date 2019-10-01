package org.ei.dataloading.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProduceData {

	public static void main(String[] args)
	{
		// create Kafka Producer
		KafkaProducer<String,String> producer = Producer.getInstance();
		
		//create a producer record
		
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("ev_topic1", "first message from KafkaProduce in java");
		
		// send data asynchronously
		
		producer.send(record);
		
		// flush data
		producer.flush();
		
		// flush the producer and close producer
		producer.close();
	}
}
