package gr.codelearn.spring.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${app.kafka.topic}")
	private String topicToCreate;

	@Bean
	public KafkaAdmin createKafkaAdmin() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configuration.put(AdminClientConfig.CLIENT_ID_CONFIG, "local-admin-client-1");
		return new KafkaAdmin(configuration);
	}

	@Bean
	public NewTopic createNewTopic() {
		//@formatter:off
		return TopicBuilder.name(topicToCreate)
						   .partitions(3)
						   .replicas(2)
						   .config(TopicConfig.RETENTION_MS_CONFIG, "14400000")
						   .config(TopicConfig.RETENTION_BYTES_CONFIG, "262144")
						   .build();
		//@formatter:on
	}

	@Bean
	public NewTopics createMultipleTopics() {
		//@formatter:off
		return new NewTopics(TopicBuilder.name(topicToCreate+"-m1")
										 .partitions(5)
										 .replicas(3)
										 .build(),
							 TopicBuilder.name(topicToCreate+"-m2")
										 .partitions(2)
										 .replicas(1)
										 .build(),
							 TopicBuilder.name(topicToCreate+"-m3")
										 .partitions(4)
										 .replicas(3)
										 .build());
		//@formatter:on

	}
}
