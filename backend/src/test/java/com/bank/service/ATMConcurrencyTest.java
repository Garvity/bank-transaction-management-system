package com.bank.service;

import com.bank.entity.LoginEntity;
import com.bank.repository.LoginRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class ATMConcurrencyTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testConcurrentWithdrawals() throws InterruptedException, ExecutionException {
        // Setup temporary account credentials
        String testPin = "9988";
        String testCard = "9988998899889988";
        String testForm = " 9988";

        // Clean up any existing data
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ?", testPin);
        jdbcTemplate.update("DELETE FROM login WHERE card_number = ?", testCard);

        // Insert fresh credentials
        LoginEntity login = new LoginEntity(testForm, testCard, testPin);
        loginRepository.save(login);

        // Deposit Rs. 4,000 starting balance
        // Note: deposit calls findByPinForUpdate, so we need a transaction to do this or run direct SQL.
        // We can run direct SQL for setup to avoid locking setup.
        jdbcTemplate.update("INSERT INTO bank (pin, date, type, amount) VALUES (?, 'SetupDate', 'Deposit', '4000')", testPin);

        // Verify initial balance
        int initialBalance = bankService.getBalance(testPin);
        assertEquals(4000, initialBalance, "Initial balance should be exactly 4000");

        // Prepare 2 concurrent threads to withdraw Rs. 3,000 each
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<Boolean> task = () -> {
            try {
                return bankService.withdraw(testPin, "3000");
            } catch (Exception e) {
                // If locking throws error or blocks, count as failure
                return false;
            }
        };

        List<Future<Boolean>> futures = new ArrayList<>();
        futures.add(executor.submit(task));
        futures.add(executor.submit(task));

        // Gather results
        boolean thread1Result = futures.get(0).get();
        boolean thread2Result = futures.get(1).get();

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Assertions
        // One transaction must succeed (true) and one must fail (false)
        assertTrue(thread1Result || thread2Result, "At least one withdrawal should succeed");
        assertNotEquals(thread1Result, thread2Result, "Exactly one withdrawal must succeed and one must fail");

        // Verify final balance is exactly 1000
        int finalBalance = bankService.getBalance(testPin);
        assertEquals(1000, finalBalance, "Final balance should be exactly 1000, preventing the double-spend!");

        // Clean up test data
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ?", testPin);
        jdbcTemplate.update("DELETE FROM login WHERE card_number = ?", testCard);
    }
}
