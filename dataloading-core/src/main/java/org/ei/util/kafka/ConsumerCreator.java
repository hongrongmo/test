package org.ei.util.kafka;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.ei.util.kafka.IKafkaConstants;

public class ConsumerCreator {
    public static Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        String kafkaBroker=null;
        String topicName = null;
        System.out.println("getting config file");
    	try (InputStream input = new FileInputStream("./lib/config.properties")) 
    	{
    		
            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            kafkaBroker=prop.getProperty("KAFKA_BROKERS");           
	        topicName=prop.getProperty("TOPIC_NAME");
            //kafkaBroker = kafka_brokers;
            // get the property value and print it out
            System.out.println("KAFKA_BROKERS="+kafkaBroker);
          

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
    	props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, IKafkaConstants.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, IKafkaConstants.MAX_POLL_RECORDS);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, IKafkaConstants.OFFSET_RESET_EARLIER);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));
        return consumer;
    }
}
