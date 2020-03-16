package org.ei.util.kafka;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;


public class ProducerCreator {
        public static KafkaProducer<String, String> createProducer(String kafka_brokers) {
            // props = new Properties();
        	String kafkaBroker=null;
        	try (InputStream input = new FileInputStream("/data/loading/shared/config.properties")) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);
                //kafkaBroker=prop.getProperty("KAFKA_BROKERS");
                kafkaBroker = kafka_brokers;
                // get the property value and print it out
                System.out.println("KAFKA_BROKERS="+kafkaBroker);
              

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Properties props = new Properties();
            //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker); //getkafka broker from config.properties file
            props.put(ProducerConfig.CLIENT_ID_CONFIG, IKafkaConstants.CLIENT_ID);
            //.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            //high throughput producer setting
            props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
            props.put(ProducerConfig.LINGER_MS_CONFIG, "20");
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));
            //props.put("max.block.ms", 30000);
            //props.put("request.timeout.ms", 30000);
            //return new KafkaProducer<Long,String>(props);
            return new KafkaProducer<String,String>(props);
        }

}
