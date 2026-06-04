package com.bank.service;

import com.bank.entity.LoginEntity;
import com.bank.repository.LoginRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class EventSourcingLedgerTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testEventSourcedLedgerFlow() {
        String testPin = "8877";
        String testCard = "8877887788778877";
        String testForm = " 8877";

        // Clean up any existing data
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ?", testPin);
        jdbcTemplate.update("DELETE FROM login WHERE card_number = ?", testCard);

        // Save fresh login row for pin locking/lookup
        LoginEntity login = new LoginEntity(testForm, testCard, testPin);
        loginRepository.save(login);

        // Verify starting balance is 0
        int startingBalance = bankService.getBalance(testPin);
        assertEquals(0, startingBalance, "Starting balance should be 0");

        // Action: Deposit Rs. 5000
        bankService.deposit(testPin, "5000");

        // Assert: Event was published and stored in the database
        List<Map<String, Object>> events = jdbcTemplate.queryForList("SELECT type, amount FROM bank WHERE pin = ?", testPin);
        assertEquals(1, events.size(), "Should have exactly 1 transaction event persisted");
        assertEquals("Deposit", events.get(0).get("type"));
        assertEquals("5000", events.get(0).get("amount"));

        // Assert: Balance is dynamically calculated by replaying the events
        int balanceAfterDeposit = bankService.getBalance(testPin);
        assertEquals(5000, balanceAfterDeposit, "Balance should be calculated as 5000");

        // Action: Withdraw Rs. 2000
        boolean withdrawSuccess = bankService.withdraw(testPin, "2000");
        assertTrue(withdrawSuccess, "Withdrawal of Rs. 2000 should be authorized");

        // Assert: Dynamic balance recalculated from event logs
        int balanceAfterWithdraw = bankService.getBalance(testPin);
        assertEquals(3000, balanceAfterWithdraw, "Balance should drop to 3000");

        // Assert: Two events exist in store
        events = jdbcTemplate.queryForList("SELECT type, amount FROM bank WHERE pin = ? ORDER BY date ASC", testPin);
        assertEquals(2, events.size(), "Should have exactly 2 transaction events persisted");
        assertEquals("Deposit", events.get(0).get("type"));
        assertEquals("Withdrawl", events.get(1).get("type"));

        // Action: Fast Cash Rs. 1000
        boolean fastCashSuccess = bankService.fastCash(testPin, "1000");
        assertTrue(fastCashSuccess, "Fast Cash Rs. 1000 should be authorized");

        // Assert: Current balance is 2000
        int finalBalance = bankService.getBalance(testPin);
        assertEquals(2000, finalBalance, "Balance should be 2000 after Fast Cash");

        // Action: Attempt withdrawal of Rs. 3000 (exceeds balance)
        boolean overdrawSuccess = bankService.withdraw(testPin, "3000");
        assertFalse(overdrawSuccess, "Withdrawal of Rs. 3000 should fail due to insufficient funds");

        // Assert: No new event logged for failed transaction
        events = jdbcTemplate.queryForList("SELECT type, amount FROM bank WHERE pin = ?", testPin);
        assertEquals(3, events.size(), "Should still have exactly 3 events");

        // Clean up test data
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ?", testPin);
        jdbcTemplate.update("DELETE FROM login WHERE card_number = ?", testCard);
    }
}
