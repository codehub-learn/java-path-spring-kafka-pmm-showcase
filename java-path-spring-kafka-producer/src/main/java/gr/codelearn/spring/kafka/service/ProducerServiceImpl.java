package gr.codelearn.spring.kafka.service;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import gr.codelearn.spring.kafka.base.BaseComponent;
import gr.codelearn.spring.kafka.domain.Customer;
import gr.codelearn.spring.kafka.domain.Person;
import gr.codelearn.spring.kafka.producer.SampleProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl extends BaseComponent implements ProducerService {
	private final SampleProducer sampleProducer;

	@Value("${app.kafka.topic1}")
	private String genericTopic;
	@Value("${app.kafka.topic2}")
	private String personTopic;
	@Value("${app.kafka.topic3}")
	private String customerTopic;

	private static final Integer NUM_OF_MESSAGES = 2;
	private static final Lorem generator = LoremIpsum.getInstance();
	private final AtomicLong sequence = new AtomicLong(1);

	@Override
	@Scheduled(cron = "0/10 * * * * ?")
	public void produceSampleMessage() {
		LongStream.range(0, NUM_OF_MESSAGES).forEach(i -> {
			sampleProducer.sendMessageWithKey(genericTopic, ThreadLocalRandom.current().nextLong(0, 10),
											  generator.getWords(5));
		});
	}

	@Override
	//	@Scheduled(cron = "0/10 * * * * ?")
	public void produceCustomer() {
		sampleProducer.sendCustomer(customerTopic, ThreadLocalRandom.current().nextLong(0, 10), generateCustomer());
	}

	@Override
	//	@Scheduled(cron = "0/10 * * * * ?")
	public void produceMultipleMessageTypes() {
		if (ThreadLocalRandom.current().nextBoolean()) {
			sampleProducer.sendCustomer(genericTopic, ThreadLocalRandom.current().nextLong(0, 10), generateCustomer());
		} else {
			sampleProducer.sendPerson(genericTopic, ThreadLocalRandom.current().nextLong(0, 10), generatePerson());
		}
	}

	@Override
	@Scheduled(cron = "0/15 * * * * ?")
	public void produceMultipleMessageTypesUsingRouting() {
		if (ThreadLocalRandom.current().nextBoolean()) {
			sampleProducer.sendUsingRouting(customerTopic, ThreadLocalRandom.current().nextLong(0, 10),
											generateCustomer());
		} else {
			sampleProducer.sendUsingRouting(personTopic, ThreadLocalRandom.current().nextLong(0, 10), generatePerson());
		}
	}

	private Customer generateCustomer() {
		var firstname = generator.getFirstName();
		var lastname = generator.getLastName();
		var email = String.format("%s.%s@example.com", firstname, lastname);
		return Customer.builder().id(sequence.getAndIncrement()).firstname(firstname).lastname(lastname).email(email)
					   .age(ThreadLocalRandom.current().nextInt(18, 100)).build();
	}

	private Person generatePerson() {
		return Person.builder().id(sequence.getAndIncrement()).firstname(generator.getFirstName()).lastname(
				generator.getLastName()).address(generator.getStateAbbr()).build();
	}
}
