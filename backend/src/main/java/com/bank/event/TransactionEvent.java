package com.bank.event;

public class TransactionEvent {
    private final String pin;
    private final String type;
    private final String amount;
    private final String timestamp;

    public TransactionEvent(String pin, String type, String amount, String timestamp) {
        this.pin = pin;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getPin() {
        return pin;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TransactionEvent{" +
                "pin='XXXX'" +
                ", type='" + type + '\'' +
                ", amount='" + amount + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
