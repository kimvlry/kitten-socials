package ru.kimvlry.atmSimulator.operationResults;

/**
 * Business-logic checked exception thrown when the account's funds are insufficient for desired operation
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds for this operation");
    }
}
