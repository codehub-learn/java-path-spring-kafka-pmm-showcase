package gr.codelearn.spring.kafka.config;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import gr.codelearn.spring.kafka.domain.Person;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@EnableKafka
public class KafkaProducerConfig extends BaseComponent {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${app.kafka.topic1}")
	private String genericTopic;
	@Value("${app.kafka.topic2}")
	private String personTopic;
	@Value("${app.kafka.topic3}")
	private String customerTopic;

	@Bean
	public KafkaTemplate<Long, String> createKafkaTemplate() {
		var kafkaTemplate = new KafkaTemplate<>(createProducerFactory());

		kafkaTemplate.setProducerListener(new ProducerListener<>() {
			public void onSuccess(ProducerRecord<Long, String> producerRecord, RecordMetadata recordMetadata) {
				logger.debug("Ack received, {}:'{}' at {}@{}.", producerRecord.key(), producerRecord.value(),
							 recordMetadata.offset(), recordMetadata.partition());
			}

			public void onError(ProducerRecord<Long, String> producerRecord, RecordMetadata recordMetadata,
								Exception exception) {
				logger.error("Unable to receive {}:'{}'.", producerRecord.key(), producerRecord.value(), exception);
			}
		});

		return kafkaTemplate;
	}

	public ProducerFactory<Long, String> createProducerFactory() {
		Map<String, Object> configurationProperties = getDefaultConfigurationProperties();
		configurationProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(configurationProperties);
	}

	@Bean
	public KafkaTemplate<Long, Person> createPersonKafkaTemplate() {
		return new KafkaTemplate<>(createPersonProducerFactory());
	}

	private ProducerFactory<Long, Person> createPersonProducerFactory() {
		return new DefaultKafkaProducerFactory<>(getDefaultConfigurationProperties());
	}

	@Bean
	public KafkaTemplate<Long, Customer> createCustoerKafkaTemplate() {
		return new KafkaTemplate<>(createCustomerProducerFactory());
	}

	private ProducerFactory<Long, Customer> createCustomerProducerFactory() {
		return new DefaultKafkaProducerFactory<>(getDefaultConfigurationProperties());
	}

	@Bean
	public RoutingKafkaTemplate createRoutingKafkaTemplate(GenericApplicationContext context) {
		/*
		 * Showcasing multiple producers with different configurations where we want to select
		 * producer at runtime based on the topic name.
		 */
		DefaultKafkaProducerFactory<Object, Object> personProducerFactory = new DefaultKafkaProducerFactory<>(
				getDefaultConfigurationProperties());
		context.registerBean(DefaultKafkaProducerFactory.class, "personProducerFactory", personProducerFactory);

		DefaultKafkaProducerFactory<Object, Object> customerProducerFactory = new DefaultKafkaProducerFactory<>(
				getDefaultConfigurationProperties());

		Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
		map.put(Pattern.compile("person-.*"), personProducerFactory);
		map.put(Pattern.compile("customer-.*"), customerProducerFactory);
		return new RoutingKafkaTemplate(map);
	}

	private Map<String, Object> getDefaultConfigurationProperties() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configurationProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		configurationProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		configurationProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, "8192");
		configurationProperties.put(ProducerConfig.LINGER_MS_CONFIG, "5000");
		return configurationProperties;
	}

	@Bean
	public KafkaAdmin.NewTopics newTopics() {
		//@formatter:off
			return new KafkaAdmin.NewTopics(TopicBuilder.name(genericTopic)
													.partitions(3)
													.replicas(2)
													.build(),
										TopicBuilder.name(personTopic)
													.partitions(3)
													.replicas(2)
													.build(),
										TopicBuilder.name(customerTopic)
													.partitions(3)
													.replicas(2)
													.build()
			);
		//@formatter:on
	}
}
