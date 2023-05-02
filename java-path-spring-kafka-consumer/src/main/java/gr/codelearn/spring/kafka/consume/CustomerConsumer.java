package gr.codelearn.spring.kafka.consume;

import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class CustomerConsumer extends BaseComponent {
	/* List for Customer objects and forward the string representation to the first topic. */
	@KafkaListener(topics = "${app.kafka.topic3}", containerFactory = "customerKafkaListenerContainerFactory")
	@SendTo("${app.kafka.topic1}")
	public String listenForCustomerAndForward(@Header(KafkaHeaders.RECEIVED_KEY) Long key, Customer customer,
											  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
											  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
		logger.trace("Received customer {}:'{}' from '{}'@{}.", key, customer, topic, partition);
		return String.format("Customer with email '%s'.", customer.email());
	}
}
