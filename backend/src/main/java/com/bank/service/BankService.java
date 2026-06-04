package com.bank.service;

import com.bank.entity.LoginEntity;
import com.bank.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.event.TransactionEvent;
import com.bank.event.TransactionEventPublisher;
import com.bank.util.EncryptionUtil;
import com.bank.util.HashUtil;

import java.util.*;

@Service
public class BankService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TransactionEventPublisher eventPublisher;

    // Helper to generate a new Form Number
    public String generateFormNumber() {
        Random ran = new Random();
        long first4 = (ran.nextLong() % 9000L) + 1000L;
        return " " + Math.abs(first4);
    }

    // Helper to get from map case-insensitively
    private Object getIgnoreCase(Map<String, Object> map, String key) {
        if (map == null) return null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    // Auth: Login validation
    public Map<String, Object> validateLogin(String cardNumber, String pin) {
        String cleanCardNumber = cardNumber != null ? cardNumber.replaceAll("\\s|-", "") : "";
        String query = "SELECT * FROM login WHERE card_number = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(query, cleanCardNumber);
        if (!users.isEmpty()) {
            Map<String, Object> dbUser = users.get(0);
            String dbHash = (String) getIgnoreCase(dbUser, "pin");
            if (HashUtil.verifyBCrypt(pin, dbHash)) {
                Map<String, Object> result = new HashMap<>();
                for (Map.Entry<String, Object> entry : dbUser.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("pin")) {
                        result.put(entry.getKey(), pin);
                    } else {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }
                return result;
            }
        }
        return null;
    }

    // Signup: Step 1 (Personal Details)
    public void saveSignupStep1(Map<String, String> data) {
        String query = "INSERT INTO signup (formno, name, fname, dob, gender, email, marital, address, city, pincode, state) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(query,
                data.get("formno"),
                data.get("name"),
                data.get("fname"),
                data.get("dob"),
                data.get("gender"),
                data.get("email"),
                data.get("marital"),
                data.get("address"),
                data.get("city"),
                data.get("pincode"),
                data.get("state")
        );
    }

    // Signup: Step 2 (Additional Details)
    public void saveSignupStep2(Map<String, String> data) {
        String query = "INSERT INTO Signuptwo (formno, rel, cate, inc, edu, occ, pan, addhar, scitizen, eAccount) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String encryptedPan = EncryptionUtil.encrypt(data.get("pan"));
        String encryptedAddhar = EncryptionUtil.encrypt(data.get("addhar"));
        jdbcTemplate.update(query,
                data.get("formno"),
                data.get("rel"),
                data.get("cate"),
                data.get("inc"),
                data.get("edu"),
                data.get("occ"),
                encryptedPan,
                encryptedAddhar,
                data.get("scitizen"),
                data.get("eAccount")
        );
    }

    // Signup: Step 3 (Services, Credentials generation & Insertion)
    public Map<String, String> saveSignupStep3(String formno, String accountType, String facilities) {
        Random ran = new Random();
        long first7 = (ran.nextLong() % 90000000L) + 1409963000000000L;
        String cardno = "" + Math.abs(first7);

        int first3 = ran.nextInt(9000) + 1000;
        String pin = String.valueOf(first3);

        String hashedBcryptPin = HashUtil.hashBCrypt(pin);
        String hashedLookupPin = HashUtil.sha256(pin);

        // Inserts into signupthree (store BCrypt hash)
        String q1 = "INSERT INTO signupthree (formno, atype, cardno, pin, fac) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(q1, formno, accountType, cardno, hashedBcryptPin, facilities);

        // Inserts into login (store BCrypt hash & SHA-256 lookup hash)
        String q2 = "INSERT INTO login (formno, card_number, pin, pin_lookup_hash) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(q2, formno, cardno, hashedBcryptPin, hashedLookupPin);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("cardNumber", cardno);
        credentials.put("pin", pin);
        return credentials;
    }

    // Calculate Balance
    public int getBalance(String pin) {
        String sha256Pin = HashUtil.sha256(pin);
        String query = "SELECT type, amount FROM bank WHERE pin = ?";
        List<Map<String, Object>> transactions = jdbcTemplate.queryForList(query, sha256Pin);
        int balance = 0;
        for (Map<String, Object> tx : transactions) {
            String type = (String) getIgnoreCase(tx, "type");
            int amount = Integer.parseInt((String) getIgnoreCase(tx, "amount"));
            if ("Deposit".equals(type)) {
                balance += amount;
            } else {
                balance -= amount;
            }
        }
        return balance;
    }

    // Deposit Transaction
    @Transactional
    public void deposit(String pin, String amount) {
        String sha256Pin = HashUtil.sha256(pin);
        loginRepository.findByPinLookupHashForUpdate(sha256Pin)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PIN or account not active"));

        String dateStr = new Date().toString();
        TransactionEvent event = new TransactionEvent(sha256Pin, "Deposit", amount, dateStr);
        eventPublisher.publish(event);
    }

    // Withdrawal Transaction (Standard)
    @Transactional
    public boolean withdraw(String pin, String amount) {
        // Acquire pessimistic write lock on the customer's account row
        String sha256Pin = HashUtil.sha256(pin);
        loginRepository.findByPinLookupHashForUpdate(sha256Pin)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PIN or account not active"));

        int balance = getBalance(pin);
        int withdrawAmount = Integer.parseInt(amount);
        if (balance < withdrawAmount) {
            return false;
        }
        String dateStr = new Date().toString();
        // Exact type string: 'Withdrawl'
        TransactionEvent event = new TransactionEvent(sha256Pin, "Withdrawl", amount, dateStr);
        eventPublisher.publish(event);
        return true;
    }

    // Fast Cash Transaction
    @Transactional
    public boolean fastCash(String pin, String amount) {
        // Acquire pessimistic write lock on the customer's account row
        String sha256Pin = HashUtil.sha256(pin);
        loginRepository.findByPinLookupHashForUpdate(sha256Pin)
            .orElseThrow(() -> new IllegalArgumentException("Invalid PIN or account not active"));

        int balance = getBalance(pin);
        int withdrawAmount = Integer.parseInt(amount);
        if (balance < withdrawAmount) {
            return false;
        }
        String dateStr = new Date().toString();
        // Exact type string: 'withdrawl' (lowercase)
        TransactionEvent event = new TransactionEvent(sha256Pin, "withdrawl", amount, dateStr);
        eventPublisher.publish(event);
        return true;
    }

    // Pin Change Transaction
    public void changePin(String oldPin, String newPin) {
        String oldSha256 = HashUtil.sha256(oldPin);
        String newSha256 = HashUtil.sha256(newPin);
        String newBcrypt = HashUtil.hashBCrypt(newPin);

        String cardQuery = "SELECT card_number, formno FROM login WHERE pin_lookup_hash = ?";
        List<Map<String, Object>> cardList = jdbcTemplate.queryForList(cardQuery, oldSha256);
        if (!cardList.isEmpty()) {
            String cardno = (String) getIgnoreCase(cardList.get(0), "card_number");
            String formno = (String) getIgnoreCase(cardList.get(0), "formno");

            String q1 = "UPDATE bank SET pin = ? WHERE pin = ?";
            jdbcTemplate.update(q1, newSha256, oldSha256);

            String q2 = "UPDATE login SET pin = ?, pin_lookup_hash = ? WHERE card_number = ?";
            jdbcTemplate.update(q2, newBcrypt, newSha256, cardno);

            String q3 = "UPDATE signupthree SET pin = ? WHERE formno = ?";
            jdbcTemplate.update(q3, newBcrypt, formno);
        }
    }

    // Retrieve Mini Statement info
    public Map<String, Object> getMiniStatement(String pin) {
        Map<String, Object> statementData = new HashMap<>();
        String sha256Pin = HashUtil.sha256(pin);

        // Get Card Number
        String cardQuery = "SELECT card_number FROM login WHERE pin_lookup_hash = ?";
        List<Map<String, Object>> cardList = jdbcTemplate.queryForList(cardQuery, sha256Pin);
        String maskedCard = "Card Number: N/A";
        if (!cardList.isEmpty()) {
            String fullCard = (String) getIgnoreCase(cardList.get(0), "card_number");
            if (fullCard != null && fullCard.length() >= 16) {
                maskedCard = "Card Number: " + fullCard.substring(0, 4) + "XXXXXXXX" + fullCard.substring(12);
            }
        }
        statementData.put("cardNumber", maskedCard);

        // Get Transactions
        String txQuery = "SELECT date, type, amount FROM bank WHERE pin = ?";
        List<Map<String, Object>> rawTransactions = jdbcTemplate.queryForList(txQuery, sha256Pin);
        List<Map<String, String>> transactionsList = new ArrayList<>();
        int balance = 0;

        for (Map<String, Object> tx : rawTransactions) {
            Map<String, String> item = new HashMap<>();
            String date = (String) getIgnoreCase(tx, "date");
            String type = (String) getIgnoreCase(tx, "type");
            String amount = (String) getIgnoreCase(tx, "amount");

            item.put("date", date);
            item.put("type", type);
            item.put("amount", amount);
            transactionsList.add(item);

            int val = Integer.parseInt(amount);
            if ("Deposit".equals(type)) {
                balance += val;
            } else {
                balance -= val;
            }
        }

        statementData.put("transactions", transactionsList);
        statementData.put("balance", balance);

        return statementData;
    }
}
