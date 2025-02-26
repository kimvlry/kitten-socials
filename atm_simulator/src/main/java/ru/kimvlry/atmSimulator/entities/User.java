package ru.kimvlry.atmSimulator.entities;

import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private String name;
    private Map<String, BankAccount> accounts;
    private String currentAccountID;

    public User(String name) {
        this.name = name;
        this.accounts = new HashMap<>();
    }

    public String createAccount() {
        String accountNumber = UUID.randomUUID().toString();
        while (accounts.containsKey(accountNumber)) {
            accountNumber = UUID.randomUUID().toString();
        }
        accounts.put(accountNumber, new BankAccount(accountNumber));
        return accountNumber;
    }

    public void switchAccount(String accountNumber) throws AccountNotFoundException {
        if (!accounts.containsKey(accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        currentAccountID = accountNumber;
    }

    public String getCurrentAccount() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return currentAccountID;
    }

    public BigDecimal getBalance() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getBalance();
    }

    public BigDecimal deposit(BigDecimal amount) throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        accounts.get(currentAccountID).deposit(amount);
        return accounts.get(currentAccountID).getBalance();
    }

    public BigDecimal withdraw(BigDecimal amount) throws NoAccountSelectedException, InsufficientFundsException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        try {
            accounts.get(currentAccountID).withdraw(amount);
        }
        catch (InsufficientFundsException e) {
            throw e;
        }
        return accounts.get(currentAccountID).getBalance();
    }

    public List<String> getTransactionHistory() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getTransactionHistory();
    }
}
