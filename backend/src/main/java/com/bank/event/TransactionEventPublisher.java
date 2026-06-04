package com.bank.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventPublisher.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publish(TransactionEvent event) {
        logger.info("[EVENT BROKER] Publishing Transaction Event: {}", event);
        eventPublisher.publishEvent(event);
        logger.info("[EVENT BROKER] Transaction Event delivered successfully.");
    }
}
