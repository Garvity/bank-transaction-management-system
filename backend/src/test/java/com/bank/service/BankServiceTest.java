package com.bank.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    @Test
    public void testGenerateFormNumber() {
        BankService service = new BankService();
        String formNo = service.generateFormNumber();
        
        assertNotNull(formNo, "Form number should not be null");
        assertTrue(formNo.startsWith(" "), "Form number should have a leading space to match Swing legacy behavior");
        
        String trimmed = formNo.trim();
        assertFalse(trimmed.isEmpty(), "Form number value should not be empty");
        
        // Form number should parse to a positive number
        long val = Long.parseLong(trimmed);
        assertTrue(val >= 0, "Form number should be non-negative");
    }
}
