package gr.codelearn.spring.kafka.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class Person {
	private Long id;
	private String firstname;
	private String lastname;
	private String address;
}
