package com.pszymczyk;

import com.pszymczyk.repositiories.OrderEntity;
import com.pszymczyk.repositiories.OrderItemEntity;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pszymczyk.repositiories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = "spring.profiles.active=test")
public class ApplicationTests {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void acceptanceTest() {
        //given
        String iPhone = "iPhone";
        String iWatch = "iWatch";
        String kindle = "kindle";
        String echo = "echo";

        String orderOne = "123";
        String orderTwo = "124";

        //when add some items to orders
        addItemToOrder(orderOne, iPhone);
        addItemToOrder(orderOne, iPhone);
        addItemToOrder(orderOne, iWatch);
        addItemToOrder(orderOne, iWatch);
        addItemToOrder(orderOne, iWatch);
        addItemToOrder(orderOne, iWatch);
        addItemToOrder(orderOne, kindle);

        addItemToOrder(orderTwo, echo);
        addItemToOrder(orderTwo, echo);
        addItemToOrder(orderTwo, iPhone);
        addItemToOrder(orderTwo, iWatch);

        //order one should be saved in database
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
            () -> {
                Optional<OrderEntity> orderOneEntity = orderRepository.findByOrderId(orderOne);
                assert orderOneEntity.isPresent();
                assert orderOneEntity.get().getOrderId().equals(orderOne);
                assert orderOneEntity.get().getOrderItems().size() == 3;
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iPhone) && orderItemEntity.getCount().equals(2L));
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iWatch) && orderItemEntity.getCount().equals(4L));
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(kindle) && orderItemEntity.getCount().equals(1L));
            }
        );

        //order two should be saved in database
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
            () -> {
                Optional<OrderEntity> orderTwoEntity = orderRepository.findByOrderId(orderTwo);
                assert orderTwoEntity.isPresent();
                assert orderTwoEntity.get().getOrderId().equals(orderTwo);
                assert orderTwoEntity.get().getOrderItems().size() == 3;
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(echo) && orderItemEntity.getCount().equals(2L));
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iPhone) && orderItemEntity.getCount().equals(1L));
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iWatch) && orderItemEntity.getCount().equals(1L));
            }
        );

        //remove some items from orders
        removeItemFromOrder(orderOne, iPhone);
        removeItemFromOrder(orderOne, iWatch);
        removeItemFromOrder(orderOne, iWatch);
        removeItemFromOrder(orderTwo, iWatch);
        removeItemFromOrder(orderTwo, echo);

        //order one should be saved in database
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
            () -> {
                Optional<OrderEntity> orderOneEntity = orderRepository.findByOrderId(orderOne);
                assert orderOneEntity.isPresent();
                assert orderOneEntity.get().getOrderId().equals(orderOne);
                assert orderOneEntity.get().getOrderItems().size() == 3;
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iPhone) && orderItemEntity.getCount().equals(1L));
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iWatch) && orderItemEntity.getCount().equals(2L));
                assert orderOneEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(kindle) && orderItemEntity.getCount().equals(1L));
            }
        );

        //order two should be saved in database
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
            () -> {
                Optional<OrderEntity> orderTwoEntity = orderRepository.findByOrderId(orderTwo);
                assert orderTwoEntity.isPresent();
                assert orderTwoEntity.get().getOrderId().equals(orderTwo);
                assert orderTwoEntity.get().getOrderItems().size() == 3;
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(echo) && orderItemEntity.getCount().equals(1L));
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iPhone) && orderItemEntity.getCount().equals(1L));
                assert orderTwoEntity.get().getOrderItems().stream()
                    .anyMatch(orderItemEntity -> orderItemEntity.getName().equals(iWatch) && orderItemEntity.getCount().equals(0L));
            }
        );
    }



    void addItemToOrder(String order, String item) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // language=JSON
        String requestJsonPayload = "{\n" +
            "  \"item\":" +
            " \"" + item + "\",\n" +
            "  \"orderId\": \"" + order + "\",\n" +
            "  \"type\": \"AddItem\"\n" +
            "}";
        HttpEntity<?> postOrderCommandRequestEntity = new HttpEntity<>(requestJsonPayload, headers);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/orders/commands", HttpMethod.POST, postOrderCommandRequestEntity, String.class);
        assert exchange.getStatusCode().equals(HttpStatus.ACCEPTED);
        String[] split = exchange.getBody().split("\\.");
        assert split[0].equals("order-commands");
        assert Integer.parseInt(split[1]) == 0;
        assert Long.parseLong(split[2]) >= 0;
    }

    private void removeItemFromOrder(String order, String item) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // language=JSON
        String requestJsonPayload = "{\n" +
            "  \"item\":" +
            " \"" + item + "\",\n" +
            "  \"orderId\": \"" + order + "\",\n" +
            "  \"type\": \"RemoveItem\"\n" +
            "}";
        HttpEntity<?> postOrderCommandRequestEntity = new HttpEntity<>(requestJsonPayload, headers);
        ResponseEntity<String> exchange = testRestTemplate.exchange("/orders/commands", HttpMethod.POST, postOrderCommandRequestEntity, String.class);
        assert exchange.getStatusCode().equals(HttpStatus.ACCEPTED);
        String[] split = exchange.getBody().split("\\.");
        assert split[0].equals("order-commands");
        assert Integer.parseInt(split[1]) == 0;
        assert Long.parseLong(split[2]) >= 0;
    }

}
