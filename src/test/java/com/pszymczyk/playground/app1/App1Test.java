package com.pszymczyk.playground.app1;

import org.junit.jupiter.api.Test;


public class App1Test extends KafkaTest {



    @Test
    public void test1() {
        assert kafkaEmbedded != null;
        assert kafkaTemplate != null;
    }
}
