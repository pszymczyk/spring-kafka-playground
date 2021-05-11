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
public class OrdersController {

	private final KafkaTemplate<String, OrderCommand> kafkaTemplate;
	private final OrderCommandsTopic orderCommandsTopic;

	public OrdersController(KafkaTemplate<String, OrderCommand> kafkaTemplate, OrderCommandsTopic orderCommandsTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.orderCommandsTopic = orderCommandsTopic;
	}

	@PostMapping(path = "/orders/commands")
	public ResponseEntity<String> sendFoo(@RequestBody OrderCommand orderCommand) {
		if (!OrderCommand.SUPPORTED_COMMANDS.contains(orderCommand.getType())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			SendResult<String, OrderCommand> sendResult = kafkaTemplate
				.send(orderCommandsTopic.getName(), orderCommand.getOrderId(), orderCommand)
				.get(5, TimeUnit.SECONDS);

			return getResponseEntity(sendResult.getRecordMetadata());
		} catch (InterruptedException | ExecutionException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (TimeoutException e) {
			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
		}
	}

	private ResponseEntity<String> getResponseEntity(RecordMetadata recordMetadata) {
		String topic = recordMetadata.topic();
		int partition = recordMetadata.partition();
		long offset = recordMetadata.offset();
		return new ResponseEntity<>(String.format("%s.%d.%d", topic, partition, offset), HttpStatus.ACCEPTED);
	}

}
