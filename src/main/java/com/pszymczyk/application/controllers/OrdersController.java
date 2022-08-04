//package com.pszymczyk.controllers;
//
//import com.pszymczyk.commands.OrderCommand;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//@RestController
//public class OrdersController {
//
//	private final KafkaTemplate<String, OrderCommand> kafkaTemplate;
//
//	public OrdersController(KafkaTemplate<String, OrderCommand> kafkaTemplate) {
//		this.kafkaTemplate = kafkaTemplate;
//	}
//
//	@PostMapping(path = "/orders/{orderId}/commands", consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<String> sendFoo(@PathVariable String orderId, @RequestBody OrderCommand orderCommand) {
//		try {
//			SendResult<String, OrderCommand> sendResult = kafkaTemplate
//				.send("order-commands", orderId, orderCommand)
//				.get(5, TimeUnit.SECONDS);
//
//			return getResponseEntity(sendResult.getRecordMetadata());
//		} catch (InterruptedException | ExecutionException e) {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		} catch (TimeoutException e) {
//			return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
//		}
//	}
//
//	private ResponseEntity<String> getResponseEntity(RecordMetadata recordMetadata) {
//		String topic = recordMetadata.topic();
//		int partition = recordMetadata.partition();
//		long offset = recordMetadata.offset();
//		return new ResponseEntity<>(String.format("%s.%d.%d", topic, partition, offset), HttpStatus.ACCEPTED);
//	}
//
//}
