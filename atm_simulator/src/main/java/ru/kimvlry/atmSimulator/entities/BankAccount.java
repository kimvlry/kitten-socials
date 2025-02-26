package ru.kimvlry.atmSimulator.entities;

import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {
    private String accountID;
    private BigDecimal balance;
    private List<Transaction> transactionsHistory;

    public BankAccount(String accountID) {
        this.accountID = accountID;
        this.balance = BigDecimal.ZERO;
        this.transactionsHistory = new ArrayList<>();
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
        transactionsHistory.add(new Transaction(accountID, TransactionType.DEPOSIT, amount));
    }

    public BigDecimal withdraw(BigDecimal amount) throws InsufficientFundsException{
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        this.balance = balance.subtract(amount);
        transactionsHistory.add(new Transaction(accountID, TransactionType.WITHDRAW, amount));
        return this.balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<String> getTransactionHistory() {
        List<String> transationList = new ArrayList<>();
        for (Transaction transaction : transactionsHistory) {
            transationList.add(transaction.toString());
        }
        return transationList;
    }
}
