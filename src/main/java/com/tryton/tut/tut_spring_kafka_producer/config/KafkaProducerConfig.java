package com.tryton.tut.tut_spring_kafka_producer.config;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.tryton.tut.tut_spring_kafka_producer.service.WikimediaChangeHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

	@Bean
	public Producer<String, String> kafkaProducer() {
		String bootstrapServers = "127.0.0.1:9092";

		// create Producer Properties
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// set safe producer configs (Kafka <= 2.8)
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // same as setting -1
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); // same as setting -1

		// set high throughput producer configs
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

		// create the Producer
		return new KafkaProducer<>(properties);
	}

	@Bean
	public EventHandler eventHandler() {
		String topic = "wikimedia.recentchange";
		return new WikimediaChangeHandler(topic, kafkaProducer());
	}

	@Bean
	public EventSource eventSource() {
		String url = "https://stream.wikimedia.org/v2/stream/recentchange";
		EventSource.Builder builder = new EventSource.Builder(eventHandler(), URI.create(url));
		return builder.build();
	}
}
