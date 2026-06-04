package com.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BankService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        String query = "SELECT * FROM login WHERE card_number = ? AND pin = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(query, cleanCardNumber, pin);
        if (!users.isEmpty()) {
            return users.get(0);
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
        jdbcTemplate.update(query,
                data.get("formno"),
                data.get("rel"),
                data.get("cate"),
                data.get("inc"),
                data.get("edu"),
                data.get("occ"),
                data.get("pan"),
                data.get("addhar"),
                data.get("scitizen"),
                data.get("eAccount")
        );
    }

    // Signup: Step 3 (Services, Credentials generation & Insertion)
    public Map<String, String> saveSignupStep3(String formno, String accountType, String facilities) {
        Random ran = new Random();
        long first7 = (ran.nextLong() % 90000000L) + 1409963000000000L;
        String cardno = "" + Math.abs(first7);

        long first3 = (ran.nextLong() % 9000L) + 1000L;
        String pin = "" + Math.abs(first3);

        // Inserts into signupthree
        String q1 = "INSERT INTO signupthree (formno, atype, cardno, pin, fac) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(q1, formno, accountType, cardno, pin, facilities);

        // Inserts into login
        String q2 = "INSERT INTO login (formno, card_number, pin) VALUES (?, ?, ?)";
        jdbcTemplate.update(q2, formno, cardno, pin);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("cardNumber", cardno);
        credentials.put("pin", pin);
        return credentials;
    }

    // Calculate Balance
    public int getBalance(String pin) {
        String query = "SELECT type, amount FROM bank WHERE pin = ?";
        List<Map<String, Object>> transactions = jdbcTemplate.queryForList(query, pin);
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
    public void deposit(String pin, String amount) {
        String dateStr = new Date().toString();
        String query = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, 'Deposit', ?)";
        jdbcTemplate.update(query, pin, dateStr, amount);
    }

    // Withdrawal Transaction (Standard)
    public boolean withdraw(String pin, String amount) {
        int balance = getBalance(pin);
        int withdrawAmount = Integer.parseInt(amount);
        if (balance < withdrawAmount) {
            return false;
        }
        String dateStr = new Date().toString();
        // Exact type string: 'Withdrawl'
        String query = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, 'Withdrawl', ?)";
        jdbcTemplate.update(query, pin, dateStr, amount);
        return true;
    }

    // Fast Cash Transaction
    public boolean fastCash(String pin, String amount) {
        int balance = getBalance(pin);
        int withdrawAmount = Integer.parseInt(amount);
        if (balance < withdrawAmount) {
            return false;
        }
        String dateStr = new Date().toString();
        // Exact type string: 'withdrawl' (lowercase)
        String query = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, 'withdrawl', ?)";
        jdbcTemplate.update(query, pin, dateStr, amount);
        return true;
    }

    // Pin Change Transaction
    public void changePin(String oldPin, String newPin) {
        String q1 = "UPDATE bank SET pin = ? WHERE pin = ?";
        String q2 = "UPDATE login SET pin = ? WHERE pin = ?";
        String q3 = "UPDATE signupthree SET pin = ? WHERE pin = ?";

        jdbcTemplate.update(q1, newPin, oldPin);
        jdbcTemplate.update(q2, newPin, oldPin);
        jdbcTemplate.update(q3, newPin, oldPin);
    }

    // Retrieve Mini Statement info
    public Map<String, Object> getMiniStatement(String pin) {
        Map<String, Object> statementData = new HashMap<>();

        // Get Card Number
        String cardQuery = "SELECT card_number FROM login WHERE pin = ?";
        List<Map<String, Object>> cardList = jdbcTemplate.queryForList(cardQuery, pin);
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
        List<Map<String, Object>> rawTransactions = jdbcTemplate.queryForList(txQuery, pin);
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
