package com.todo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private static final Logger logger = LoggerFactory.getLogger(RandomPortConfig.class);
    private static final int MIN_PORT = 11000;
    private static final int MAX_PORT = 12000;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        int port = generateRandomPort();
        factory.setPort(port);
        logger.info("应用启动在随机端口: {}", port);
    }

    private int generateRandomPort() {
        Random random = new Random();
        return MIN_PORT + random.nextInt(MAX_PORT - MIN_PORT + 1);
    }
}
