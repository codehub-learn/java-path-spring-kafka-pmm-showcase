package gr.codelearn.spring.kafka.producer;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import gr.codelearn.spring.kafka.domain.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SampleProducer extends BaseComponent {
	private final KafkaTemplate<Long, String> kafkaTemplate;
	private final KafkaTemplate<Long, Person> personKafkaTemplate;
	private final KafkaTemplate<Long, Customer> customerKafkaTemplate;
	private final RoutingKafkaTemplate routingKafkaTemplate;

	public void sendMessageWithoutKey(String topic, String message) {
		kafkaTemplate.send(topic, message);
		logger.debug("Sent a keyless message '{}' to topic '{}'.", message, topic);
	}

	public void sendMessageWithKey(String topic, Long key, String message) {
		var future = kafkaTemplate.send(topic, key, message);
		logger.debug("Sent key '{}' and message '{}' to topic '{}'.", key, message, topic);

		future.whenComplete((result, ex) -> {
			if (ex == null) {
				logger.debug("'{}':'{}' was delivered at offset {} in partition {}.", key, message,
							 result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
			} else {
				logger.warn("Unable to deliver '{}'.", message, ex);
			}
		});
	}

	public void sendPerson(String topic, Long key, Person person) {
		personKafkaTemplate.send(topic, key, person);
		logger.debug("Sent key '{}' and person '{}' to topic '{}'.", key, person, topic);
	}

	public void sendCustomer(String topic, Long key, Customer customer) {
		customerKafkaTemplate.send(topic, key, customer);
		logger.debug("Sent key '{}' and customer '{}' to topic '{}'.", key, customer, topic);
	}

	public void sendUsingRouting(String topic, Long key, Object object) {
		routingKafkaTemplate.send(topic, key, object);
		logger.debug("Sent using routing key '{}' and object '{}' to topic '{}'.", key, object, topic);
	}
}
