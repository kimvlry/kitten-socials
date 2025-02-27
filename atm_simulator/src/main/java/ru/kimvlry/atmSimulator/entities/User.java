package ru.kimvlry.atmSimulator.entities;

import ru.kimvlry.atmSimulator.operationResults.AccountNotFoundException;
import ru.kimvlry.atmSimulator.operationResults.InsufficientFundsException;
import ru.kimvlry.atmSimulator.operationResults.InvaidAmountException;
import ru.kimvlry.atmSimulator.operationResults.NoAccountSelectedException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class User {
    private Map<String, BankAccount> accounts;
    private String currentAccountID;

    /**
     *
     */
    public User() {
        this.accounts = new HashMap<>();
    }

    /**
     *
     * @return
     */
    public String createAccount() {
        String accountNumber = UUID.randomUUID().toString();
        while (accounts.containsKey(accountNumber)) {
            accountNumber = UUID.randomUUID().toString();
        }
        accounts.put(accountNumber, new BankAccount(accountNumber));
        return accountNumber;
    }

    /**
     *
     * @param accountNumber
     * @throws AccountNotFoundException
     */
    public void switchAccount(String accountNumber) throws AccountNotFoundException {
        if (!accounts.containsKey(accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        currentAccountID = accountNumber;
    }

    /**
     *
     * @return
     * @throws NoAccountSelectedException
     */
    public BigDecimal getBalance() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getBalance();
    }

    /**
     *
     * @param amount
     * @return
     * @throws NoAccountSelectedException
     */
    public BigDecimal deposit(BigDecimal amount) throws NoAccountSelectedException, InvaidAmountException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).deposit(amount);
    }

    /**
     *
     * @param amount
     * @return
     * @throws NoAccountSelectedException
     * @throws InsufficientFundsException
     */
    public BigDecimal withdraw(BigDecimal amount) throws NoAccountSelectedException, InsufficientFundsException, InvaidAmountException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).withdraw(amount);
    }

    /**
     *
     * @return
     * @throws NoAccountSelectedException
     */
    public List<String> getTransactionHistory() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getTransactionHistory();
    }
}
