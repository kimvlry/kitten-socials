package ru.kimvlry.atmSimulator.entities;

import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;
import ru.kimvlry.atmSimulator.operationResults.Result;

import java.math.BigDecimal;
import java.util.HashMap;
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

    public Result createAccount() {
        String accountNumber = UUID.randomUUID().toString();
        while (accounts.containsKey(accountNumber)) {
            accountNumber = UUID.randomUUID().toString();
        }
        accounts.put(accountNumber, new BankAccount(accountNumber));
        return Result.success(null);
    }

    public Result switchAccount(String accountNumber) {
        if (!accounts.containsKey(accountNumber)) {
            return Result.failure(new AccountNotFoundException(accountNumber));
        }
        currentAccountID = accountNumber;
        return Result.success(null);
    }

    public Result getCurrentAccount() {
        if (currentAccountID == null) {
            return Result.failure(new NoAccountSelectedException());
        }
        return Result.success(accounts.get(currentAccountID));
    }

    public Result getBalance() {
        if (currentAccountID == null) {
            return Result.failure(new NoAccountSelectedException());
        }
        return Result.success(accounts.get(currentAccountID).getBalance());
    }

    public Result deposit(BigDecimal amount) {
        if (currentAccountID == null) {
            return Result.failure(new NoAccountSelectedException());
        }
        accounts.get(currentAccountID).deposit(amount);
        return Result.success(null);
    }

    public Result withdraw(BigDecimal amount) {
        if (currentAccountID == null) {
            return Result.failure(new IllegalStateException("No account selected"));
        }
        return accounts.get(currentAccountID).withdraw(amount);
    }

    public Result getTransactionHistory() {
        if (currentAccountID == null) {
            return Result.failure(new NoAccountSelectedException());
        }
        return Result.success(accounts.get(currentAccountID).getTransactionHistory());
    }
}
