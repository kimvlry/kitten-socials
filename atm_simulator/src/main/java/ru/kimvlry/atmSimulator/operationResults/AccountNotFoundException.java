package ru.kimvlry.atmSimulator.operationResults;

/**
 * Business-logic checked exception thrown when the operation is attempted on the non-existing account.
 */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accountID) {
        super("Couldn't find account by ID: " + accountID);
    }
}
