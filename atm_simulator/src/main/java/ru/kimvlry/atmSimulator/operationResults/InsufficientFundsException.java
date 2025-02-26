package ru.kimvlry.atmSimulator.operationResults;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super("Insufficient funds for this operation");
    }
}
