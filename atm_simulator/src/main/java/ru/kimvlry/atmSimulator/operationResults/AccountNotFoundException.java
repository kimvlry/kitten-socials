package ru.kimvlry.atmSimulator.operationResults;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accountID) {
        super("Couldn't find account by ID: " + accountID);
    }
}
