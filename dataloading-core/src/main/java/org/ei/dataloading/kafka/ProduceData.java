package org.ei.dataloading.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author TELEBH
 *
 */
public class ProduceData {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(ProduceData.class);
		// create Kafka Producer
		KafkaProducer<String, String> producer = Producer.getInstance();

		// create a producer record

		ProducerRecord<String, String> record = new ProducerRecord<String, String>("ev_topic1",
				"first message from KafkaProduce in java with 2nd time Callback");

		// send data asynchronously

		//producer.send(record);

		

		
		producer.send(record, new Callback() {

			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception == null) {
					logger.info("Received new record's metadata" + "\n" + "Topic: " + metadata.topic() + "\n"
							+ "Partition: " + metadata.partition() + "\n" + "Offset: " + metadata.offset() + "\n"
							+ "Timestamp: " + metadata.timestamp());
				} else {
					logger.error("Exception while producing a new record", exception);
				}

			}
		});

		// flush data
		producer.flush();

		// flush the producer and close producer
		producer.close();

	}

}
