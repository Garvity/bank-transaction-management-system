package com.bank.service;

import com.bank.util.EncryptionUtil;
import com.bank.util.HashUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class PCIDSSComplianceTest {

    @Autowired
    private BankService bankService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Object getIgnoreCase(Map<String, Object> map, String key) {
        if (map == null) return null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Test
    public void testPCIDSSComplianceFeatures() {
        String testForm = " 9977";
        String testPin = "9977";
        String testNewPin = "8866";
        String testPan = "ABCDE1234F";
        String testAadhar = "123456789012";

        // 1. Clean up potential old test records
        jdbcTemplate.update("DELETE FROM Signuptwo WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM signupthree WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM login WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ? OR pin = ?", HashUtil.sha256(testPin), HashUtil.sha256(testNewPin));

        // 2. Test Step 2 - PAN and Aadhar AES-256 Encryption at rest
        Map<String, String> step2Data = new HashMap<>();
        step2Data.put("formno", testForm);
        step2Data.put("rel", "Hindu");
        step2Data.put("cate", "General");
        step2Data.put("inc", "Above 5,00,000");
        step2Data.put("edu", "Graduate");
        step2Data.put("occ", "Professional");
        step2Data.put("pan", testPan);
        step2Data.put("addhar", testAadhar);
        step2Data.put("scitizen", "No");
        step2Data.put("eAccount", "Yes");

        bankService.saveSignupStep2(step2Data);

        // Directly query DB values to ensure they are NOT plain-text
        List<Map<String, Object>> signuptwoRows = jdbcTemplate.queryForList(
                "SELECT pan, addhar FROM Signuptwo WHERE formno = ?", testForm);
        assertEquals(1, signuptwoRows.size(), "Should have inserted step 2 data");
        String dbPan = (String) getIgnoreCase(signuptwoRows.get(0), "pan");
        String dbAadhar = (String) getIgnoreCase(signuptwoRows.get(0), "addhar");

        assertNotEquals(testPan, dbPan, "PAN should be encrypted in the database");
        assertNotEquals(testAadhar, dbAadhar, "Aadhar should be encrypted in the database");

        // Verify decryption retrieves original values
        assertEquals(testPan, EncryptionUtil.decrypt(dbPan), "Decrypted PAN should match original");
        assertEquals(testAadhar, EncryptionUtil.decrypt(dbAadhar), "Decrypted Aadhar should match original");

        // 3. Test Step 3 - PIN BCrypt & SHA-256 Hashing on Generation
        Map<String, String> credentials = bankService.saveSignupStep3(testForm, "Saving Account", "ATM CARD");
        String cardNo = credentials.get("cardNumber");
        String generatedPin = credentials.get("pin"); // Generates a random PIN, but for our test we will override it to testPin to control assertions

        // Overwrite login and signupthree with testPin values for predictable testing
        String testBcryptHash = HashUtil.hashBCrypt(testPin);
        String testSha256Hash = HashUtil.sha256(testPin);
        jdbcTemplate.update("UPDATE login SET pin = ?, pin_lookup_hash = ? WHERE card_number = ?", 
                testBcryptHash, testSha256Hash, cardNo);
        jdbcTemplate.update("UPDATE signupthree SET pin = ? WHERE formno = ?", 
                testBcryptHash, testForm);

        // Verify stored BCrypt hash in DB
        List<Map<String, Object>> loginRows = jdbcTemplate.queryForList(
                "SELECT pin, pin_lookup_hash FROM login WHERE card_number = ?", cardNo);
        assertEquals(1, loginRows.size());
        String dbBcryptPin = (String) getIgnoreCase(loginRows.get(0), "pin");
        String dbLookupHash = (String) getIgnoreCase(loginRows.get(0), "pin_lookup_hash");

        assertNotEquals(testPin, dbBcryptPin, "PIN must be hashed in the login table");
        assertTrue(dbBcryptPin.startsWith("$2a$"), "PIN hash should be a standard BCrypt hash starting with $2a$");
        assertEquals(testSha256Hash, dbLookupHash, "PIN lookup hash must match SHA-256 of plain PIN");

        // 4. Test Login authentication flow (BCrypt verification)
        Map<String, Object> loginSuccess = bankService.validateLogin(cardNo, testPin);
        assertNotNull(loginSuccess, "Login should succeed with correct PIN");
        assertEquals(testPin, getIgnoreCase(loginSuccess, "pin"), "Response should map plain PIN");

        Map<String, Object> loginFailure = bankService.validateLogin(cardNo, "0000");
        assertNull(loginFailure, "Login should fail with incorrect PIN");

        // 5. Test Transactions - Deterministic SHA-256 matching for ledger entries
        bankService.deposit(testPin, "3000");

        // Assert that events in the bank table store the SHA-256 hash
        List<Map<String, Object>> bankRows = jdbcTemplate.queryForList(
                "SELECT pin, type, amount FROM bank WHERE pin = ?", testSha256Hash);
        assertEquals(1, bankRows.size(), "Transaction should be queryable by SHA-256 hash");
        assertEquals("Deposit", getIgnoreCase(bankRows.get(0), "type"));
        assertEquals("3000", getIgnoreCase(bankRows.get(0), "amount"));

        // Verify plain query does not fetch plain-text pin logs
        List<Map<String, Object>> plainQuery = jdbcTemplate.queryForList(
                "SELECT pin FROM bank WHERE pin = ?", testPin);
        assertTrue(plainQuery.isEmpty(), "No transactions should be stored with plain-text PIN");

        // Check running balance replay succeeds
        int balance = bankService.getBalance(testPin);
        assertEquals(3000, balance, "Running balance should aggregate to 3000");

        // 6. Test PIN Change Update
        bankService.changePin(testPin, testNewPin);

        // Verify lookup hashes and BCrypt credentials updated
        String testNewSha256 = HashUtil.sha256(testNewPin);
        List<Map<String, Object>> updatedLogin = jdbcTemplate.queryForList(
                "SELECT pin, pin_lookup_hash FROM login WHERE card_number = ?", cardNo);
        String updatedBcrypt = (String) getIgnoreCase(updatedLogin.get(0), "pin");
        String updatedLookup = (String) getIgnoreCase(updatedLogin.get(0), "pin_lookup_hash");

        assertTrue(HashUtil.verifyBCrypt(testNewPin, updatedBcrypt), "Login table must verify new PIN");
        assertEquals(testNewSha256, updatedLookup, "Lookup hash must map to new PIN SHA-256");

        // Verify bank transactions are migrated to new SHA-256 hash
        List<Map<String, Object>> updatedBank = jdbcTemplate.queryForList(
                "SELECT type, amount FROM bank WHERE pin = ?", testNewSha256);
        assertEquals(1, updatedBank.size(), "Transactions must map to new hash");

        // Clean up test data
        jdbcTemplate.update("DELETE FROM Signuptwo WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM signupthree WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM login WHERE formno = ?", testForm);
        jdbcTemplate.update("DELETE FROM bank WHERE pin = ?", testNewSha256);
    }
}
