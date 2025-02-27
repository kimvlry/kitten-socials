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
 * Represents a User in the ATM system.
 * Stores all the User's bank accounts and manages operations on the currently selected account.
 */
public class User {
    private Map<String, BankAccount> accounts;
    private String currentAccountID;

    /**
     * Creates a new User with an empty collection of bank accounts.
     */
    public User() {
        this.accounts = new HashMap<>();
    }

    /**
     * Creates a new bank account for the User.
     *
     * @return a unique identifier assigned to the newly created account
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
     * Switches the current account to another by changing {@code currentAccountID} value
     *
     * @param accountNumber the identifier of the account to switch to
     * @throws AccountNotFoundException if the specified ID is not associated with any of User's accounts
     */
    public void switchAccount(String accountNumber) throws AccountNotFoundException {
        if (!accounts.containsKey(accountNumber)) {
            throw new AccountNotFoundException(accountNumber);
        }
        currentAccountID = accountNumber;
    }

    /**
     * Returns the balance of the currently selected account by calling the corresponding {@link BankAccount} method.
     *
     * @return the balance of the active account
     * @throws NoAccountSelectedException if no account is currently selected
     */
    public BigDecimal getBalance() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getBalance();
    }

    /**
     * Deposits a specified amount into the currently selected account by calling the corresponding {@link BankAccount} method.
     *
     * @param amount the amount to deposit
     * @return the updated balance after the deposit
     * @throws NoAccountSelectedException if no account is currently selected
     * @throws InvaidAmountException if the provided amount is invalid (e.g., non-positive)
     */
    public BigDecimal deposit(BigDecimal amount) throws NoAccountSelectedException, InvaidAmountException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).deposit(amount);
    }

    /**
     * Withdraws a specified amount from the currently selected account by calling the corresponding {@link BankAccount} method.
     *
     * @param amount the amount to withdraw
     * @return the updated balance after the withdrawal
     * @throws NoAccountSelectedException if no account is currently selected
     * @throws InsufficientFundsException if the account has insufficient funds
     * @throws InvaidAmountException if the provided amount is invalid (e.g., non-positive)
     */
    public BigDecimal withdraw(BigDecimal amount) throws NoAccountSelectedException, InsufficientFundsException, InvaidAmountException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).withdraw(amount);
    }

    /**
     * Returns the transaction history of the currently selected account by calling the corresponding {@link BankAccount} method.
     *
     * @return a list of transaction records for the active account
     * @throws NoAccountSelectedException if no account is currently selected
     */
    public List<String> getTransactionHistory() throws NoAccountSelectedException {
        if (currentAccountID == null) {
            throw new NoAccountSelectedException();
        }
        return accounts.get(currentAccountID).getTransactionHistory();
    }
}
