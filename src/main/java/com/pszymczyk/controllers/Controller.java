package com.pszymczyk.controllers;

import com.pszymczyk.commands.OrderCommand;
import com.pszymczyk.topics.OrderCommandsTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class Controller {

	private final KafkaTemplate<String, OrderCommand> template;
	private final OrderCommandsTopic orderCommandsTopic;

	public Controller(KafkaTemplate<String, OrderCommand> template, OrderCommandsTopic orderCommandsTopic) {
		this.template = template;
		this.orderCommandsTopic = orderCommandsTopic;
	}

	@PostMapping(path = "/orders/commands")
	public ResponseEntity<CommandId> sendFoo(@RequestBody OrderCommand orderCommand) {
		if (!OrderCommand.SUPPORTED_COMMANDS.contains(orderCommand.getType())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			SendResult<String, OrderCommand> sendResult = template
				.send(orderCommandsTopic.getName(), orderCommand.getOrderId(), orderCommand)
				.get(5, TimeUnit.SECONDS);

			return getResponseEntity(sendResult.getRecordMetadata());
		} catch (InterruptedException | ExecutionException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (TimeoutException e) {
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		}
	}

	private ResponseEntity<CommandId> getResponseEntity(RecordMetadata recordMetadata) {
		String topic = recordMetadata.topic();
		int partition = recordMetadata.partition();
		long offset = recordMetadata.offset();
		return new ResponseEntity<>(new CommandId(topic, partition, offset), HttpStatus.ACCEPTED);
	}

}
