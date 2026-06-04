package com.bank.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @EventListener
    public void handleTransactionEvent(TransactionEvent event) {
        logger.info("[EVENT CONSUMER] Received Transaction Event from bus for PIN: XXXX. Type: {}, Amount: {}", 
                event.getType(), event.getAmount());
        
        String query = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(query, event.getPin(), event.getTimestamp(), event.getType(), event.getAmount());
        
        logger.info("[EVENT STORE] Successfully persisted transaction event to database.");
    }
}
