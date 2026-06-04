package com.bank.controller;

import com.bank.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private BankService bankService;

    // Generate random Form Number for Step 1
    @GetMapping("/generate-formno")
    public ResponseEntity<Map<String, String>> generateFormNumber() {
        String formNo = bankService.generateFormNumber();
        Map<String, String> response = new HashMap<>();
        response.put("formno", formNo);
        return ResponseEntity.ok(response);
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

    // Login verification
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String cardNumber = credentials.get("cardNumber");
        String pin = credentials.get("pin");

        if (cardNumber == null || pin == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Card number and PIN are required");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> user = bankService.validateLogin(cardNumber, pin);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pin", getIgnoreCase(user, "pin"));
            response.put("cardNumber", getIgnoreCase(user, "card_number"));
            response.put("formno", getIgnoreCase(user, "formno"));
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Incorrect Card Number or PIN");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Signup Step 1
    @PostMapping("/signup/step1")
    public ResponseEntity<Map<String, Object>> signupStep1(@RequestBody Map<String, String> data) {
        String formno = data.get("formno");
        String name = data.get("name");

        if (formno == null || name == null || name.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Form number and Name are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            bankService.saveSignupStep1(data);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("formno", formno);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration step 1 failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Signup Step 2
    @PostMapping("/signup/step2")
    public ResponseEntity<Map<String, Object>> signupStep2(@RequestBody Map<String, String> data) {
        String formno = data.get("formno");
        String pan = data.get("pan");
        String addhar = data.get("addhar");

        if (formno == null || pan == null || pan.trim().isEmpty() || addhar == null || addhar.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Form number, PAN, and Aadhar numbers are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            bankService.saveSignupStep2(data);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration step 2 failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Signup Step 3
    @PostMapping("/signup/step3")
    public ResponseEntity<Map<String, Object>> signupStep3(@RequestBody Map<String, String> data) {
        String formno = data.get("formno");
        String atype = data.get("atype");
        String fac = data.get("fac");

        if (formno == null || atype == null || atype.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Form number and Account Type are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Map<String, String> credentials = bankService.saveSignupStep3(formno, atype, fac);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cardNumber", credentials.get("cardNumber"));
            response.put("pin", credentials.get("pin"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration step 3 failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
