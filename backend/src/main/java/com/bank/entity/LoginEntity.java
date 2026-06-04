package com.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login")
public class LoginEntity {

    @Id
    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    private String formno;
    private String pin;

    @Column(name = "pin_lookup_hash")
    private String pinLookupHash;

    // Default Constructor
    public LoginEntity() {}

    public LoginEntity(String formno, String cardNumber, String pin) {
        this.formno = formno;
        this.cardNumber = cardNumber;
        this.pin = pin;
    }

    public LoginEntity(String formno, String cardNumber, String pin, String pinLookupHash) {
        this.formno = formno;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.pinLookupHash = pinLookupHash;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFormno() {
        return formno;
    }

    public void setFormno(String formno) {
        this.formno = formno;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPinLookupHash() {
        return pinLookupHash;
    }

    public void setPinLookupHash(String pinLookupHash) {
        this.pinLookupHash = pinLookupHash;
    }
}
