package com.pszymczyk.playground.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Utils {

    private static final Random RAND = new Random();
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final String MESSAGE = "Random number 0 = exception!";

    public static void failSometimes() {
        int randomNum = RAND.nextInt(2);
        logger.info("Random number {}.", randomNum);
        if (randomNum == 0) {
            logger.error(MESSAGE);
            throw new RuntimeException(MESSAGE);
        }
    }
}
