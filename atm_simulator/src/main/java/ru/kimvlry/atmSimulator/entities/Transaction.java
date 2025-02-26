package ru.kimvlry.atmSimulator.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public final String accountNumber;
    public final TransactionType type;
    public final BigDecimal amount;
    public final LocalDateTime timestamp;

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s %s",
                timestamp,
                type,
                amount,
                "RUB");
    }
}
