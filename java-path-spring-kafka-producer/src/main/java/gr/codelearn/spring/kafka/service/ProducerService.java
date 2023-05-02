package gr.codelearn.spring.kafka.service;

public interface ProducerService {
	void produceSampleMessage();

	void produceCustomer();

	void produceMultipleMessageTypes();

	void produceMultipleMessageTypesUsingRouting();
}
