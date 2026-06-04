package com.bank.controller;

import com.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private BankService bankService;

    // Deposit endpoint
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody Map<String, String> request) {
        String pin = request.get("pin");
        String amount = request.get("amount");

        if (pin == null || amount == null || amount.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN and amount are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            bankService.deposit(pin, amount);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rs. " + amount + " Deposited Successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Deposit failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Withdraw endpoint
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@RequestBody Map<String, String> request) {
        String pin = request.get("pin");
        String amount = request.get("amount");

        if (pin == null || amount == null || amount.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN and amount are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean success = bankService.withdraw(pin, amount);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Rs. " + amount + " Debited Successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Insufficient Balance");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Withdrawal failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Fast Cash endpoint
    @PostMapping("/fast-cash")
    public ResponseEntity<Map<String, Object>> fastCash(@RequestBody Map<String, String> request) {
        String pin = request.get("pin");
        String amount = request.get("amount");

        if (pin == null || amount == null || amount.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN and amount are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean success = bankService.fastCash(pin, amount);
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Rs. " + amount + " Debited Successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Insufficient Balance");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Fast cash withdrawal failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Balance Enquiry endpoint
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN parameter is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int balance = bankService.getBalance(pin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Balance check failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // PIN Change endpoint
    @PostMapping("/change-pin")
    public ResponseEntity<Map<String, Object>> changePin(@RequestBody Map<String, String> request) {
        String oldPin = request.get("pin");
        String newPin = request.get("newPin");

        if (oldPin == null || newPin == null || newPin.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Current PIN and New PIN are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            bankService.changePin(oldPin, newPin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PIN changed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN change failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Mini Statement endpoint
    @GetMapping("/mini-statement")
    public ResponseEntity<Map<String, Object>> getMiniStatement(@RequestParam String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "PIN parameter is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map<String, Object> statementData = bankService.getMiniStatement(pin);
            statementData.put("success", true);
            return ResponseEntity.ok(statementData);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve mini statement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
