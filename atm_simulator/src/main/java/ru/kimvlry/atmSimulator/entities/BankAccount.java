package ru.kimvlry.atmSimulator.entities;

import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.InvalidAmountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account with a set of basic operations (deposit, withdrawal, viewing
 * balance and history of transactions)
 */
public class BankAccount {
    private String accountID;
    private BigDecimal balance;
    private List<Transaction> transactionsHistory;

    /**
     * Creates a new account and initializes it's balance to zero.
     *
     * @param accountID a unique bank account identifier
     */
    public BankAccount(String accountID) {
        this.accountID = accountID;
        this.balance = BigDecimal.ZERO;
        this.transactionsHistory = new ArrayList<>();
    }

    /**
     * Deposits a specified amount of money into the account.
     *
     * @param amount the amount to deposit.
     * @return account balance after successful transaction
     * @throws InvalidAmountException in case the {@code amount} is < 0 RUB
     */
    public BigDecimal deposit(BigDecimal amount) throws InvalidAmountException {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException();
        }
        this.balance = balance.add(amount);
        transactionsHistory.add(new Transaction(accountID, TransactionType.DEPOSIT, amount));
        return this.balance;
    }

    /**
     * Withdraws a specified amount of money from the account.
     *
     * @param amount the amount to withdraw
     * @return account balance after successful transaction
     * @throws InsufficientFundsException if the account balance is insufficient
     * @throws InvalidAmountException in case the {@code amount} is < 0 RUB
     */
    public BigDecimal withdraw(BigDecimal amount) throws InsufficientFundsException, InvalidAmountException {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException();
        }
        this.balance = balance.subtract(amount);
        transactionsHistory.add(new Transaction(accountID, TransactionType.WITHDRAW, amount));
        return this.balance;
    }

    /**
     * Shows current bank account balance
     *
     * @return current balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Shows the history of all account transactions in chronological order
     *
     * @return list of transactions as formatted strings
     */
    public List<String> getTransactionHistory() {
        List<String> transationList = new ArrayList<>();
        if (transactionsHistory.isEmpty()) {
            transationList.add("No transactions yet");
        }
        else {
            for (Transaction transaction : transactionsHistory) {
                transationList.add(transaction.toString());
            }
        }
        return transationList;
    }
}
