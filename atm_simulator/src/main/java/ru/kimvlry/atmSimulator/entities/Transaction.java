package ru.kimvlry.atmSimulator.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction.
 */
public class Transaction {
    public final String accountNumber;
    public final TransactionType type;
    public final BigDecimal amount;
    public final LocalDateTime timestamp;

    /**
     * Constructs the transaction object with current timestamp and other parameters/
     *
     * @param accountNumber an identifier of an account that the transaction is associated with
     * @param type the type of transaction (deposit, withdrawal etc.). Types are described
     *             in {@code TransactionType} enum
     * @param amount the amount of money that is involved in the transaction
     */
    public Transaction(String accountNumber, TransactionType type, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Formats a transation to string. The format is: {@code [timestamp] type: amount RUB}
     *
     * @return a formatted string
     */
    @Override
    public String toString() {
        return String.format("[%s] %s: %s %s",
                timestamp,
                type,
                amount,
                "RUB");
    }
}
